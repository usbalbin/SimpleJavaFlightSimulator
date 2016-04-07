package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.Callbacks.*;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import org.lwjgl.BufferUtils;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import se.liu.ida.albhe417.tddd78.game.GameObject.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.GameObject.Misc.Target;
import se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles.AbstractVehicle;
import se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles.VehicleHelicopterBox;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Game
{
	//TODO: Needed?
    private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
    private long window;//Reference to window
    private static int WINDOW_WIDTH = 1400;
	private static int WINDOW_HEIGHT = 800;
    private static final int WINDOW_POS_X = 50;
	private static final int WINDOW_POS_Y = 50;

	private static final float FOV = 90 * (float)Math.PI / 180.0f;
	private static final float DRAW_DISTANCE = 3072;
	private static final float DRAW_DISTANCE_NEAR_LIMIT = 1f;

	private static boolean WIRE_FRAME = false;
    private static int AA_LEVEL = 16;
	private static float OPENGL_VERSION = 3.0f;
    private static String title = "Simple Java Flight Simulator";

	private Matrix4x4 projectionMatrix;
	private Matrix4x4 viewMatrix;
	private Matrix4x4 cameraMatrix;
	private int MVPmatrixId;
	private int modelMatrixId;

	private Vector3 lightDirection;
	private int lightDirectionId;


	private static final float HEIGHT_SCALE = 0.01f;
	private static final Vector3 GRAVITY = new Vector3(0, -9.82f, 0);
	private DynamicsWorld physics;

	ArrayList<AbstractGameObject> gameObjects;
	AbstractVehicle currentVehicle;
	TerrainLOD terrain;
	private int shaderProgram;

	private long lastTime;

    public Game(){
		//TODO: Move to render-class?
		setupGraphics();
		setupShaders();
		setupPhysics();
		setupGameObjects();
		setupProjectionMatrix();
		setupLight();
    }



    private void setupGraphics(){
		errorCallback = GLFWErrorCallback.createPrint(System.err);//Bind error output to console
		glfwSetErrorCallback(errorCallback);


		long res = glfwInit();
		if(res != GL_TRUE){
			throw new RuntimeException("Failed to initialize!");
		}

		glfwWindowHint(GLFW_SAMPLES, AA_LEVEL);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, (int)OPENGL_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, (int)(OPENGL_VERSION * 10) % 10);


		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title, NULL, NULL);

		if(window == NULL){
			throw new RuntimeException("Failed to create window");
		}

		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WINDOW_WIDTH = width;
				WINDOW_HEIGHT = height;
				glViewport(0, 0, width, height);
				projectionMatrix = Matrix4x4.createProjectionMatrix(FOV, (float)WINDOW_WIDTH / WINDOW_HEIGHT, DRAW_DISTANCE_NEAR_LIMIT, DRAW_DISTANCE);
			}
		});

		glfwSetKeyCallback(window, InputHandler.getInstance());

		glfwSetWindowPos(window, WINDOW_POS_X, WINDOW_POS_Y);
		glfwMakeContextCurrent(window);


		GL.createCapabilities();

		glfwShowWindow(window);
		glClearColor(1.0f, 0.6f, 0.75f, 1.0f);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

		//TODO: Add culling stuff
    }

	private void setupShaders(){
		int result;

		//TODO: fix layout-issue and transform normal by cameraMatrix(not including modelMatrix)
		String vertexShaderCode =
				"#version 130\n" +
				"/*layout(location=0) */in vec3 position;\n" +
				"/*layout(location=1) */in vec3 normal;\n" +
				"/*layout(location=2) */in vec3 color;\n" +

				"out vec3 vertexColor;\n" +
				"out vec3 vertexNormal;" +

				"uniform mat4 modelViewProjectionMatrix;\n" +
				"uniform mat4 modelMatrix;\n" +
				"\n" +
				"\n" +
				"void main(){\n" +
				"	vertexColor = color;\n" +
				"	gl_Position = modelViewProjectionMatrix * vec4(position, 1.0);\n" +
				"	vertexNormal = (modelMatrix * vec4(normal, 0.0)).xyz;\n" +
				"}";

		int vertexShaderRef = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderRef, vertexShaderCode);
		glCompileShader(vertexShaderRef);
		result = glGetShaderi(vertexShaderRef, GL_COMPILE_STATUS);
		if(result != GL_TRUE){
		    glGetShaderInfoLog(vertexShaderRef);
		    throw new RuntimeException("Failed to compile vertex shader");
		}

		//TODO: Make use of normals
		String fragmentShaderCode =
				"#version 130\n" +
				"float ambient = 0.65; \n" +
				"in vec3 vertexColor;\n" +
				"in vec3 vertexNormal;\n" +
				"uniform vec3 lightDirection;" +
				"out vec4 pixelColor;\n" +

				"\n" +
				"\n" +
				"void main(){\n" +
				"	if(length(vertexNormal) == 0 || length(lightDirection) == 0)\n" +
				"		pixelColor = vec4(vertexColor.xyz, 1.0);\n" +
				"	else\n" +
				"		pixelColor = vec4(vertexColor.xyz, 1.0) * (ambient + dot(normalize(vertexNormal) * (1- ambient), -lightDirection));\n" +
				"}";

		int fragmentShaderRef = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderRef, fragmentShaderCode);
		glCompileShader(fragmentShaderRef);
		glGetShaderInfoLog(fragmentShaderRef);
		result = glGetShaderi(fragmentShaderRef, GL_COMPILE_STATUS);
		if(result != GL_TRUE){

		    throw new RuntimeException("Failed to compile fragment shader");
		}

		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShaderRef);
		glAttachShader(shaderProgram, fragmentShaderRef);

		glLinkProgram(shaderProgram);

		result = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if(result != GL_TRUE){
		    throw new RuntimeException("Failed to link shader program");
		}
		MVPmatrixId = glGetUniformLocation(shaderProgram, "modelViewProjectionMatrix");
		modelMatrixId = glGetUniformLocation(shaderProgram, "modelMatrix");
		lightDirectionId = glGetUniformLocation(shaderProgram, "lightDirection");


		int positionIndex = glGetAttribLocation(shaderProgram, "position");
		int colorIndex = glGetAttribLocation(shaderProgram, "color");
		int normalIndex = glGetAttribLocation(shaderProgram, "normal");

		VertexPositionColor.POSITION_INDEX 			= positionIndex;
		VertexPositionColor.COLOR_INDEX				= colorIndex;

		VertexPositionColorNormal.POSITION_INDEX 	= positionIndex;
		VertexPositionColorNormal.COLOR_INDEX		= colorIndex;
		VertexPositionColorNormal.NORMAL_INDEX		= normalIndex;
	}

	private void setupPhysics(){
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		physics = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		physics.setGravity(GRAVITY.toVector3f());
		BulletGlobals.setContactProcessedCallback(new TargetHitCallback(this, physics));
	}

	private void setupGameObjects(){
		gameObjects = new ArrayList<>(3);

		terrain = new TerrainLOD(new Vector3(0, 0, 0), HEIGHT_SCALE, shaderProgram, physics, this);
		//currentVehicle = new VehicleHelicopterBox(new Vector3(11, 6, 148.0f), -(float)Math.PI / 2.0f, terrain, shaderProgram, physics);

		for(int y = 0; y < 4; y++) {
			for(int x = -2; x < 2; x++) {
				//gameObjects.add(new ProjectileMesh(new Vector3(x * 10, 2 + 5 * y, y * 4), new Vector3(), shaderProgram, physics, this));
				gameObjects.add(new Target(new Vector3(x * 10, 1 + 5 * y, y * 4), shaderProgram, physics, this));
				//gameObjects.add(new VehicleHelicopterBox(new Vector3(x * 10 + 2, 1 + 5 * y, y * 4), -(float) Math.PI / 2.0f, shaderProgram, physics, this));
			}
		}
		//gameObjects.add(terrain);


		lastTime = System.nanoTime();

	}

	private void setupProjectionMatrix(){
		projectionMatrix = Matrix4x4.createProjectionMatrix(FOV, (float)WINDOW_WIDTH / WINDOW_HEIGHT, DRAW_DISTANCE_NEAR_LIMIT, DRAW_DISTANCE);
	}

	private void setupLight(){
		lightDirection = new Vector3(-1, -1, -1);
		int numVectorElements = 3;


		FloatBuffer buffer = BufferUtils.createFloatBuffer(numVectorElements);
		buffer.put(lightDirection.values);
		buffer.flip();

		glUseProgram(shaderProgram);
		glUniform3fv(lightDirectionId, buffer);
		glUseProgram(0);
	}

	public void remove(AbstractGameObject gameObject){
		gameObjects.remove(gameObject);
		if(gameObject == currentVehicle)
			currentVehicle = null;
	}

	public void respawn(){
		currentVehicle = new VehicleHelicopterBox(new Vector3(-225, -316.1f, 20), -(float)Math.PI / 2.0f, shaderProgram, physics, this);
		gameObjects.add(currentVehicle);
	}

    private void update(){
		long nowTime = System.nanoTime();
		float deltaTime = (nowTime - lastTime) / 1000000.0f;
		lastTime = nowTime;

		if(currentVehicle == null)
			respawn();

		currentVehicle.handleInput(deltaTime);

		for (AbstractGameObject drawable: gameObjects) {
			drawable.update(deltaTime);
		}


		physics.stepSimulation(deltaTime, 10, 1f/60f/10f);

		if(currentVehicle == null)
			return;

		updateCameraMatrix();
		Vector3 cameraPosition = currentVehicle.getCameraPosition();
		//TODO remove cast
		((TerrainLOD)terrain).update(cameraPosition, cameraMatrix);
    }

	private void updateCameraMatrix() {

		viewMatrix = currentVehicle.getViewMatrix();

		cameraMatrix = projectionMatrix.multiply(viewMatrix);

	}

    private void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//Wireframe
		if(WIRE_FRAME)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		//TODO: remove cast
		((TerrainLOD)terrain).draw(cameraMatrix, MVPmatrixId, modelMatrixId);

		for (AbstractGameObject drawable: gameObjects) {
			drawable.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
		}

		glfwSwapBuffers(window);
		//glFinish();

    }

    public void run(){

		while(glfwWindowShouldClose(window) != GL_TRUE){
			glfwPollEvents();

			update();
			draw();
		}
    }
}
