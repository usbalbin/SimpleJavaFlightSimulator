package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;

public class Game
{
    private GLFWErrorCallback errorCallback;
    private long window;//Reference to window
    private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 768;
    private static final int WINDOW_POS_X = 50;
	private static final int WINDOW_POS_Y = 50;

	private static final float FOV = 90 * (float)Math.PI / 180.0f;
	private static final float DRAW_DISTANCE = 256;
	private static final float DRAW_DISTANCE_NEAR_LIMIT = 0.2f;

    private static int AA_LEVEL = 16;
	private static float OPENGL_VERSION = 3.2f;
    private static String title = "Simple Java Flight Simulator";

	private Matrix4x4 projectionMatrix;
	private Matrix4x4 viewMatrix;
	private int cameraMatrixId;
	private VehicleAirplane plane;
	private int shaderProgram;

	//TODO: ta bort
	protected float yaw = 0;

    public Game(){

		//TODO: Move to render-class
		setupGraphics();
		setupShaders();
		setupGameObjects();
		setupProjectionMatrix();
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

		//TODO: fix matrix-support, layout-issue and Use normals
		String vertexShaderCodeOld =
			"#version 150 core\n" +
			"/*layout(location=0) */in vec3 position;\n" +
			"/*layout(location=1) */in vec3 color;\n" +
			"out vec3 vertexColor;\n" +
			"\n" +
			"void main(){\n" +
			"	vertexColor = color;\n" +
			"	gl_Position = vec4(position, 1.0);\n" +
			"}";

		String vertexShaderCode =
			"#version 150 core\n" +
			"/*layout(location=0) */in vec3 position;\n" +
			"/*layout(location=1) */in vec3 color;\n" +
			"out vec3 vertexColor;\n" +
			"uniform mat4 cameraMatrix;\n" +
			"\n" +
			"void main(){\n" +
			"	vertexColor = color;\n" +
			"	gl_Position = cameraMatrix * vec4(position, 1.0);\n" +
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
	}

	private void setupGameObjects(){
		plane = new VehicleAirplaneBox(new Vector3(0, 0, 0), shaderProgram);
	}

	private void setupProjectionMatrix(){
		projectionMatrix = Matrix4x4.createProjectionMatrix(FOV, (float)WINDOW_WIDTH / WINDOW_HEIGHT, DRAW_DISTANCE_NEAR_LIMIT, DRAW_DISTANCE);
	}

    private void update(){
		updateViewMatrix(new Vector3(0, 0, -1), yaw, 0, 0);
		yaw += 0.0001f;
    }

	private void updateViewMatrix(Vector3 position, float yaw, float pitch, float roll){
		final int matrixRows = 4;
		viewMatrix = new Matrix4x4();
		viewMatrix = viewMatrix.multiply(Matrix4x4.createPosFromVector(position));
		viewMatrix = viewMatrix.multiply(viewMatrix.getRotatedAboutY(yaw));
		viewMatrix = viewMatrix.multiply(viewMatrix.getRotatedAboutX(pitch));
		viewMatrix = viewMatrix.multiply(viewMatrix.getRotatedAboutZ(roll));
		Matrix4x4 cameraMatrix = projectionMatrix.multiply(viewMatrix);
		cameraMatrixId = glGetUniformLocation(shaderProgram, "cameraMatrix");



		FloatBuffer buffer = BufferUtils.createFloatBuffer(matrixRows * matrixRows);
		for(int row = 0; row < matrixRows; row++)
			for(int column = 0; column < matrixRows; column++)
				buffer.put(cameraMatrix.getValueAt(column, row));
		buffer.flip();
		//TODO: kolla upp booleanen vid ev fel, annars ta bort denna kommentar

		glUseProgram(shaderProgram);
		glUniformMatrix4fv(cameraMatrixId, false, buffer);
		glUseProgram(0);
	}

    private void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		plane.draw(new Matrix4x4());

		glfwSwapBuffers(window);
    }

    public void run(){


		while(glfwWindowShouldClose(window) != GL_TRUE){
			glfwPollEvents();

			update();
			draw();
		}
    }
}
