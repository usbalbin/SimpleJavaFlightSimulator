package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.*;

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
import se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles.VehicleAirplaneBox;
import se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles.VehicleHelicopterBox;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable
{
	private Settings settings;

	//TODO: Needed?
    private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
    private long window;//Reference to window

    private static final String TITLE = "Simple Java Flight Simulator";

	private Matrix4x4 projectionMatrix;
	private Matrix4x4 viewMatrix;
	private Matrix4x4 cameraMatrix;
	private int MVPmatrixId;
	private int modelMatrixId;

	private Vector3 lightDirection;
	private int lightDirectionId;
	private Vector3 cameraPosition = new Vector3();


	private static final float HEIGHT_SCALE = 0.01f;
	private static final Vector3 GRAVITY = new Vector3(0, -9.82f, 0);
	private DynamicsWorld physics;

	private List<AbstractGameObject> gameObjects;
	private AbstractVehicle currentVehicle;
	private TerrainLOD terrain;
	private int shaderProgram;

	private long lastTime;

    public Game(Settings settings){
		this.settings = settings;
    }


	private void setup(){
		setupGraphics();
		setupShaders();
		setupPhysics();
		setupGameObjects();
		setupLight();
	}

    private void setupGraphics(){
		errorCallback = GLFWErrorCallback.createPrint(System.err);//Bind error output to console
		glfwSetErrorCallback(errorCallback);


		long res = glfwInit();
		if(res != GL_TRUE){
			throw new RuntimeException("Failed to initialize!");
		}

		glfwWindowHint(GLFW_SAMPLES, settings.AA_LEVEL);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, (int)settings.OPENGL_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, (int)(settings.OPENGL_VERSION * 10) % 10);


		window = glfwCreateWindow(settings.getWindowWidth(), settings.getWindowHeight(), TITLE, NULL, NULL);
		//glfwHideWindow(window);

		if(window == NULL){
			throw new RuntimeException("Failed to create window");
		}

		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				settings.setWindowWidth(width);
				settings.setWindowHeight(height);
				glViewport(0, 0, width, height);
				projectionMatrix = Matrix4x4.createProjectionMatrix(settings.getFov(), settings.getAspectRatio(), settings.getDrawDistanceNearLimit(), settings.getDrawDistance());
			}
		});

		glfwSetKeyCallback(window, InputHandler.getInstance());

		glfwSetWindowPos(window, settings.getWindowPosX(), settings.getWindowPosY());
		glfwMakeContextCurrent(window);


		GL.createCapabilities();


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

		terrain = new TerrainLOD(new Vector3(0, 0, 0), HEIGHT_SCALE, settings, shaderProgram, physics, this);
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

	public int getScore(){
		return currentVehicle.getScore();
	}

	public void remove(AbstractGameObject gameObject){
		gameObjects.remove(gameObject);
		if(gameObject == currentVehicle)
			currentVehicle = null;
	}

	public void respawn(){
		final Vector3 spawnPos = new Vector3(0, -307, 0);//new Vector3(-225, /*-316.1f*/0, 20);
		currentVehicle = new VehicleHelicopterBox(spawnPos, -(float)Math.PI / 2, shaderProgram, physics, this);
		gameObjects.add(currentVehicle);
	}

	public void updateCameraPosition(){
		if(currentVehicle != null)
			cameraPosition = currentVehicle.getCameraPosition();
	}

	private void updateCameraMatrix() {

		viewMatrix = currentVehicle.getViewMatrix();

		Matrix4x4 projectionMatrix = Matrix4x4.createProjectionMatrix(settings.getFov(), settings.getAspectRatio(), settings.getDrawDistanceNearLimit(), settings.getDrawDistance());
		cameraMatrix = projectionMatrix.multiply(viewMatrix);

	}

    private void update(){
		final float nanoToSec = 1000000000.0f;


		long nowTime = System.nanoTime();
		float deltaTime = (nowTime - lastTime) / nanoToSec;
		lastTime = nowTime;
		//System.out.println(deltaTime);

		if(currentVehicle == null) {
			respawn();
		}

		currentVehicle.handleInput(deltaTime);

		for (AbstractGameObject drawable: gameObjects) {
			drawable.update(deltaTime);
		}

		updateCameraPosition();
		updateCameraMatrix();

		if(settings.isThreaded()){
			Thread threadP = new Thread(new Runnable() {
				@Override
				public void run() {
					physics.stepSimulation(deltaTime, settings.getTicksPerFrame(), settings.getPreferredTimeStep());
				}
			});
			threadP.start();

			Thread threadT = new Thread(new Runnable() {
				@Override
				public void run() {
					terrain.update(cameraPosition, cameraMatrix);
				}
			});
			threadT.start();

			try {
				threadP.join();
				threadT.join();
			}catch (InterruptedException e){

			}
		}else {
			physics.stepSimulation(deltaTime, 10, settings.getPreferredTimeStep());
			terrain.update(cameraPosition, cameraMatrix);
		}
		//terrain.update(cameraPosition, cameraMatrix);
		terrain.updateGraphics();

    }

    private void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//Wireframe
		if(settings.isWireFrame())
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		//TODO: remove cast
		((TerrainLOD)terrain).draw(cameraMatrix, MVPmatrixId, modelMatrixId);

		for (AbstractGameObject drawable : gameObjects) {
			drawable.draw(cameraMatrix, MVPmatrixId, modelMatrixId);
		}

		glfwSwapBuffers(window);
		//glFinish();

    }



    public void run(){
		setup();
		//glfwShowWindow(window);
		while(glfwWindowShouldClose(window) != GL_TRUE){
			glfwPollEvents();

			update();
			draw();
		}
		while (gameObjects.size() > 0) {
			gameObjects.get(0).destroy();
		}

		glfwDestroyWindow(window);
	}
}
