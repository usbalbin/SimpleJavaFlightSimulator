package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import se.liu.ida.albhe417.tddd78.math.Vector3;

public class Game
{
    private GLFWErrorCallback errorCallback;
    private long window;//Reference to window
    private static int WINDOW_WIDTH = 1024;
	private static int WINDOW_HEIGHT = 768;
    private static int WINDOW_POS_X = 50;
	private static int WINDOW_POS_Y = 50;

    private static int AA_LEVEL = 4;
    private static float OPENGL_VERSION = 3.2f;
    private static String title = "Simple Java Flight Simulator";

	private VehicleAirplane plane;
	private int shaderProgram;

    public Game(){

		setupGraphics();
		setupShaders();
		setupGameObjects();

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
		glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

	private void setupShaders(){
		int result;

		//TODO: fix matrix-support, layout-issue and Use normals
		String vertexShaderCode =
			"#version 150 core\n" +
			"/*layout(location=0) */in vec3 position;\n" +
			"/*layout(location=1) */in vec3 color;\n" +
			"out vec3 vertexColor;\n" +
			"\n" +
			"void main(){\n" +
			"	vertexColor = color;\n" +
			"	gl_Position = vec4(position, 1.0);\n" +
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
			"	pixelColor = vec4(vertexColor.xy, 0.0, 1.0);\n" +
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

    private void update(){

    }

    private void draw(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		plane.draw();

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
