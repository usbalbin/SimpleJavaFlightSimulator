package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.*;

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
import org.lwjgl.opengl.GL;
import se.liu.ida.albhe417.tddd78.game.Vehicles.AbstractVehicle;
import se.liu.ida.albhe417.tddd78.game.Vehicles.VehicleHelicopterBox;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Game
{
	//TODO: Needed?
    private GLFWErrorCallback errorCallback;
    private long window;//Reference to window
    private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 768;
    private static final int WINDOW_POS_X = 50;
	private static final int WINDOW_POS_Y = 50;

	private static final float FOV = 90 * (float)Math.PI / 180.0f;
	private static final float DRAW_DISTANCE = 2048;
	private static final float DRAW_DISTANCE_NEAR_LIMIT = 1f;

	private static boolean WIRE_FRAME = false;
    private static int AA_LEVEL = 16;
	private static float OPENGL_VERSION = 3.2f;
    private static String title = "Simple Java Flight Simulator";

	private Matrix4x4 projectionMatrix;
	private Matrix4x4 viewMatrix;
	private Matrix4x4 cameraMatrix;
	private int modelViewProjectionMatrixId;

	private Vector3 lightDirection;
	private int lightDirectionId;


	private static Vector3 GRAVITY = new Vector3(0, -9.82f, 0);
	private DynamicsWorld physics;

	ArrayList<AbstractGameObject> gameObjects;
	AbstractVehicle currentVehicle;
	Terrain terrain;
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

	private void setupShaders_old(){
		int result;

		//TODO: fix layout-issue and Use normals
		String vertexShaderCode =
			"#version 150 core\n" +
			"/*layout(location=0) */in vec3 position;\n" +
			"/*layout(location=1) */in vec3 color;\n" +
			"out vec3 vertexColor;\n" +
			"uniform mat4 modelViewProjectionMatrix;\n" +
			"uniform vec3 lightDirection;\n" +
			"\n" +
			"void main(){\n" +
			"	vertexColor = color;\n" +
			"	gl_Position = modelViewProjectionMatrix * vec4(position, 1.0);\n" +
			"}";

		int vertexShaderRef = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderRef, vertexShaderCode);
		glCompileShader(vertexShaderRef);
		result = glGetShaderi(vertexShaderRef, GL_COMPILE_STATUS);
		if(result != GL_TRUE){
			throw new RuntimeException("Failed to compile vertex shader");
		}

		//TODO: Make use of normals
		String fragmentShaderCode =
			"#version 150 core\n" +
			"in vec3 vertexColor;\n" +
			"out vec4 pixelColor;\n" +
			"\n" +
			"void main(){\n" +
			"	pixelColor = vec4(vertexColor.xyz, 1.0);\n" +
			"}";

		int fragmentShaderRef = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderRef, fragmentShaderCode);
		glCompileShader(fragmentShaderRef);
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
		modelViewProjectionMatrixId = glGetUniformLocation(shaderProgram, "modelViewProjectionMatrix");
		lightDirectionId = glGetUniformLocation(shaderProgram, "lightDirection");
	}

	private void setupShaders(){
		int result;

		//TODO: fix layout-issue and Use normals
		String vertexShaderCode =
				"#version 150 core\n" +
				"/*layout(location=0) */in vec3 position;\n" +
				"/*layout(location=1) */in vec3 normal;\n" +
				"/*layout(location=2) */in vec3 color;\n" +

				"out vec3 vertexColor;\n" +
				"out vec3 vertexNormal;" +

				"uniform mat4 modelViewProjectionMatrix;\n" +
				"\n" +
				"\n" +
				"void main(){\n" +
				"	vertexColor = color;\n" +
				"	gl_Position = modelViewProjectionMatrix * vec4(position, 1.0);\n" +
				//"	vertexNormal = normal;\n" +
				"}";

		int vertexShaderRef = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderRef, vertexShaderCode);
		glCompileShader(vertexShaderRef);
		result = glGetShaderi(vertexShaderRef, GL_COMPILE_STATUS);
		if(result != GL_TRUE){
			throw new RuntimeException("Failed to compile vertex shader");
		}

		//TODO: Make use of normals
		String fragmentShaderCode =
				"#version 150 core\n" +
				"in vec3 vertexColor;\n" +
				//"in vec3 vertexNormal;\n" +
				//"uniform vec3 lightDirection;" +
				"out vec4 pixelColor;\n" +

				"\n" +
				"\n" +
				"void main(){\n" +
				"	pixelColor = vec4(vertexColor.xyz, 1.0);// * dot(vertexNormal, -lightDirection);\n" +
				"}";

		int fragmentShaderRef = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderRef, fragmentShaderCode);
		glCompileShader(fragmentShaderRef);
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
		modelViewProjectionMatrixId = glGetUniformLocation(shaderProgram, "modelViewProjectionMatrix");
		lightDirectionId = glGetUniformLocation(shaderProgram, "lightDirection");
	}

	private void setupPhysics(){
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		physics = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		physics.setGravity(GRAVITY.toVector3f());
	}

	private void setupGameObjects(){
		gameObjects = new ArrayList<>(3);

		terrain = new TerrainLOD(new Vector3(0, 0, 0), shaderProgram, physics);
		//currentVehicle = new VehicleHelicopterBox(new Vector3(11, 6, 148.0f), -(float)Math.PI / 2.0f, terrain, shaderProgram, physics);
		currentVehicle = new VehicleHelicopterBox(new Vector3(1024, 15, 1024.0f), -(float)Math.PI / 2.0f, terrain, shaderProgram, physics);

		gameObjects.add(currentVehicle);
		gameObjects.add(new VehicleHelicopterBox(new Vector3(1024, 16, 1026.0f), -(float)Math.PI / 2.0f, terrain, shaderProgram, physics));
		//gameObjects.add(terrain);


		lastTime = System.currentTimeMillis();
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
		glUniform3fv(lightDirectionId, buffer);
	}

    private void update(){
		long nowTime = System.currentTimeMillis();
		float deltaTime = (nowTime - lastTime) / 1000.0f;
		lastTime = nowTime;

		currentVehicle.handleInput(deltaTime);

		for (AbstractGameObject drawable: gameObjects) {
			drawable.update(deltaTime);
		}


		updateCameraMatrix();
		Vector3 cameraPosition = currentVehicle.getCameraPosition();
		//TODO remove cast
		((TerrainLOD)terrain).update(cameraPosition);
		physics.stepSimulation(deltaTime);

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

		((TerrainLOD)terrain).draw(cameraMatrix, modelViewProjectionMatrixId);

		for (AbstractGameObject drawable: gameObjects) {
			drawable.draw(cameraMatrix, modelViewProjectionMatrixId);
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
