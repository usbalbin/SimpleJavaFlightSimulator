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
import se.liu.ida.albhe417.tddd78.game.game_object.AbstractGameObject;
import se.liu.ida.albhe417.tddd78.game.game_object.misc.Target;
import se.liu.ida.albhe417.tddd78.game.game_object.vehicles.AbstractVehicle;
import se.liu.ida.albhe417.tddd78.game.game_object.vehicles.VehicleAirplane;
import se.liu.ida.albhe417.tddd78.game.game_object.vehicles.VehicleAirplaneBox;
import se.liu.ida.albhe417.tddd78.game.game_object.vehicles.VehicleHelicopterBox;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public class Game implements Runnable
{
	private final Settings settings;

	private long window;//Reference to window

	//WindowsSizeCallback and errorCallback must be fields,
	// otherwise the garbage collector will remove them and they will not work (I have tested)
	private GLFWWindowSizeCallback windowSizeCallback;
	private GLFWErrorCallback errorCallback;

	private static final String TITLE = "Simple Java Flight Simulator";

	private Matrix4x4 cameraMatrix;
	private int MVPMatrixId;
	private int modelMatrixId;

	private int lightDirectionId;
	private Vector3 cameraPosition = new Vector3(0);
	private Thread terrainThread;


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
		try {
			setupGraphics();
			setupShaders();
		}catch (GraphicsInitException e){
			if(window != NULL)
				glfwDestroyWindow(window);
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(1);
		}
		setupPhysics();
		setupGameObjects();
		setupLight();
	}

	private void setupGraphics() throws GraphicsInitException{
		errorCallback = GLFWErrorCallback.createPrint(System.err);
		glfwSetErrorCallback(errorCallback);


		long res = glfwInit();
		if(res != GL_TRUE){
			throw new GraphicsInitException("Failed to initialize!");
		}

		glfwWindowHint(GLFW_SAMPLES, Settings.AA_LEVEL);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, (int) Settings.OPENGL_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, (int)(Settings.OPENGL_VERSION * 10) % 10);


		window = glfwCreateWindow(settings.getWindowWidth(), settings.getWindowHeight(), TITLE, NULL, NULL);

		if(window == NULL){
			throw new GraphicsInitException("Failed to create window");
		}

		windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				settings.setWindowWidth(width);
				settings.setWindowHeight(height);
				glViewport(0, 0, width, height);
			}
		};

		glfwSetWindowSizeCallback(window, windowSizeCallback);

		glfwSetKeyCallback(window, InputHandler.getInstance());

		final int windowPosX = 50, windowPosY = 50;
		glfwSetWindowPos(window, windowPosX, windowPosY);
		glfwMakeContextCurrent(window);


		GL.createCapabilities();


		final Vector4 clearColor = Vector4.createColor(0x00, 0x00, 0xCC, 0xFF);
		glClearColor(clearColor.getX(), clearColor.getY(), clearColor.getZ(), clearColor.getW());

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

	}

	private void setupShaders() throws GraphicsInitException{
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
			throw new GraphicsInitException("Failed to compile vertex shader");
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

			throw new GraphicsInitException("Failed to compile fragment shader");
		}

		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShaderRef);
		glAttachShader(shaderProgram, fragmentShaderRef);

		glLinkProgram(shaderProgram);

		result = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if(result != GL_TRUE){
			throw new GraphicsInitException("Failed to link shader program");
		}
		MVPMatrixId = glGetUniformLocation(shaderProgram, "modelViewProjectionMatrix");
		modelMatrixId = glGetUniformLocation(shaderProgram, "modelMatrix");
		lightDirectionId = glGetUniformLocation(shaderProgram, "lightDirection");

		VertexPositionColorNormal.init(shaderProgram);
	}

	private void setupPhysics(){
		BroadphaseInterface broadPhase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		physics = new DiscreteDynamicsWorld(dispatcher, broadPhase, solver, collisionConfiguration);
		physics.setGravity(GRAVITY.toVector3f());
		BulletGlobals.setContactProcessedCallback(new TargetHitCallback());
	}

	private void setupGameObjects(){
		gameObjects = new ArrayList<>();

		terrain = new TerrainLOD(new Vector3(0, 0, 0), HEIGHT_SCALE, settings, shaderProgram, physics, this);

		final Vector3 targetSpacing = new Vector3(250, 0, 250);
		final int targetHeight = 140;
		final int numTargets = 64;
		final int halfNumTargetsPerSide = (int)Math.sqrt(numTargets) / 2;

		for(int y = -halfNumTargetsPerSide; y < halfNumTargetsPerSide; y++) {
			for(int x = -halfNumTargetsPerSide; x < halfNumTargetsPerSide; x++) {
				gameObjects.add(new Target(new Vector3(x * targetSpacing.getX(), targetHeight, y * targetSpacing.getZ()), shaderProgram, physics, this, "Target at " + x + "; " + y));
			}
		}

		respawn();
		lastTime = System.nanoTime();

	}

	private void setupLight(){
		Vector3 lightDirection = new Vector3(-1, -1, -1);
		int numVectorElements = 3;


		FloatBuffer buffer = BufferUtils.createFloatBuffer(numVectorElements);
		buffer.put(lightDirection.values);
		buffer.flip();

		glUseProgram(shaderProgram);
		glUniform3fv(lightDirectionId, buffer);
		glUseProgram(0);
	}

	private void respawn(){
		final Vector3 spawnPos = new Vector3(0, 130, 0);//new Vector3(0, -307, 0);

		switch (settings.getVehicleType()) {
			case HelicopterBox:
				currentVehicle = new VehicleHelicopterBox(spawnPos, shaderProgram, physics, this, settings.getPlayerName());
				break;
			case AirplaneBox:
				currentVehicle = new VehicleAirplaneBox(spawnPos, shaderProgram, physics, this, settings.getPlayerName());
				break;
			case Airplane:
				currentVehicle = new VehicleAirplane(spawnPos, physics, this, settings.getPlayerName(), shaderProgram);
				break;
		}
		gameObjects.add(currentVehicle);
	}

	private void updateCameraPosition(){
		if(currentVehicle != null)
			cameraPosition = currentVehicle.getCameraPosition();
	}

	private void updateCameraMatrix() {

		Matrix4x4 viewMatrix = currentVehicle.getViewMatrix();

		Matrix4x4 projectionMatrix = Matrix4x4.createProjectionMatrix(settings.getFov(), settings.getAspectRatio(), settings.getDrawDistanceNearLimit(), settings.getDrawDistance());
		cameraMatrix = projectionMatrix.multiply(viewMatrix);

	}

	private void updateGameObjects(){
		Iterator<AbstractGameObject> iterator = gameObjects.iterator();

		while(iterator.hasNext()) {
			AbstractGameObject gameObject = iterator.next();
			if(gameObject.shouldDie()) {
				gameObject.destroy();

				if(gameObject == currentVehicle) {
					glfwHideWindow(window);
					JOptionPane.showMessageDialog(null,
												  "Score: " + currentVehicle.getScore() + "\n" +
												  "Killed by " + currentVehicle.killedBy.playerName
					);
					currentVehicle = null;
					glfwShowWindow(window);
				}

				iterator.remove();
			}
			else{
				gameObject.update();
			}
		}
	}

	private void update(){
		final float nanoToSec = 1e9f;


		long nowTime = System.nanoTime();
		float deltaTime = (nowTime - lastTime) / nanoToSec;
		lastTime = nowTime;

		if(currentVehicle == null) {
			respawn();
		}

		currentVehicle.handleInput(deltaTime);



		updateCameraPosition();
		updateCameraMatrix();

		boolean isThreaded = settings.isThreaded();
		if(isThreaded){
			terrainThread = new Thread(() -> {
				terrain.update(cameraPosition, cameraMatrix);
			});
			terrainThread.start();
		}else {
			terrain.update(cameraPosition, cameraMatrix);
		}

		physics.stepSimulation(deltaTime, settings.getTicksPerFrame(), settings.getPreferredTimeStep());

		if(isThreaded){
			try {
				terrainThread.join();
			}catch (InterruptedException e){
				glfwHideWindow(window);
				JOptionPane.showMessageDialog(null,
											  "Something went really wrong with the terrain thread!: \n" +
											  e.getMessage() + "\n" +
											  "If this shows again, try to un tick the \"Multi threading\" checkbox."
				);
				glfwShowWindow(window);
			}
		}

		terrain.updateGraphics();

		updateGameObjects();


	}

	private void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//Wireframe
		if(settings.isWireFrame())
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		terrain.draw(cameraMatrix, MVPMatrixId, modelMatrixId);

		for (AbstractGameObject drawable : gameObjects) {
			drawable.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
		}

		glfwSwapBuffers(window);
		//glFinish();

	}



	public void run(){
		setup();
		while(glfwWindowShouldClose(window) != GL_TRUE && !InputHandler.getInstance().isPressed(GLFW_KEY_ESCAPE)){
			glfwPollEvents();

			update();
			draw();
		}
		while (!gameObjects.isEmpty()) {
			gameObjects.get(0).destroy();
			gameObjects.remove(0);
		}
		glfwDestroyWindow(window);
		JOptionPane.showMessageDialog(null, "Score: " + currentVehicle.getScore());
	}
}
