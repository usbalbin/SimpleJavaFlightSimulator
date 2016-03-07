package se.liu.ida.albhe417.tddd78.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Game
{
    private GLFWErrorCallback errorCallback;
    private long window;//Reference to window
    private static int WINDOW_WIDTH = 1024, WINDOW_HEIGHT = 768;
    private static int WINDOW_POS_X = 0, WINDOW_POS_Y = 0;

    private static int AA_LEVEL = 4;
    private static float OPENGL_VERSION = 3.2f;
    private static String title = "<Projekttitel>";

	private VehicleAirplane plane;
	private int shaderProgramRef;
	private int vertexBuffer;
	private int vertexArray;

    public Game(){

		setupGraphics();
		//setupShaders();
		setupGameObjects();


		//TODO: flytta mig
		vertexArray = glGenVertexArrays();
		glBindVertexArray(vertexArray);

		/*FloatBuffer vertexBufferData = BufferUtils.createFloatBuffer(3 * 3);
		vertexBufferData.put(new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,  1.0f, 0.0f,});
		vertexBufferData.flip();

		vertexBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, vertexBufferData, GL_STATIC_DRAW);
		*/



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

		String vertexShaderCode =
			"#version 150 core\n" +
			"in vec3 position;\n" +
			"in vec3 color;\n" +
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

		shaderProgramRef = glCreateProgram();
		glAttachShader(shaderProgramRef, vertexShaderRef);
		glAttachShader(shaderProgramRef, fragmentShaderRef);
		//TODO: kolla om behövs
		glBindFragDataLocation(shaderProgramRef, 0, "fragColor");
		glLinkProgram(shaderProgramRef);

		result = glGetProgrami(shaderProgramRef, GL_LINK_STATUS);
		if(result != GL_TRUE){
			throw new RuntimeException("Failed to link shader program");
		}
	}

	private void setupGameObjects(){
		plane = new VehicleAirplaneBox(new Vector3(0, 0, 0), shaderProgramRef);
	}

    private void update(){

    }

    private void draw(){
		glClear(GL_COLOR_BUFFER_BIT);
		//glUseProgram(shaderProgramRef);

		plane.draw();

		/*glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glDrawArrays(GL_TRIANGLES, 0, 3);
		glDisableVertexAttribArray(0);
		*/
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
