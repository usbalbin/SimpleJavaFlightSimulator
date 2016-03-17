package se.liu.ida.albhe417.tddd78.game.Vehicles;
import static org.lwjgl.glfw.GLFW.*;

import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public class VehicleAirplaneBox extends VehicleAirplane
{
    public VehicleAirplaneBox(final Vector3 position, float yaw, final Terrain_old terrain, final int shaderProgram){
		super(position, yaw, 100.0f, 1.0f, terrain);
		setup(shaderProgram);
    }

    private void setup(final int shaderProgram){
		ArrayList<AbstractDrawablePart> parts = new ArrayList<>();
		parts.add(setupBody(shaderProgram));


		setupParts(parts);
    }

	public void handleInput(float deltaTime){


		//TODO: byta ut allt mot global modelMatrix
		float relativeX = 0;
		float relativeY = 0;
		float relativeZ = 0;

		float relativeYaw = 0;
		float relativePitch = 0;
		float relativeRoll = 0;

		InputHandler input = InputHandler.getInstance();
		if(input.isPressed(GLFW_KEY_W))
			relativeZ -= 0.1f;
		if(input.isPressed(GLFW_KEY_S))
			relativeZ += 0.1f;
		if(input.isPressed(GLFW_KEY_RIGHT))
			relativeRoll += 0.001;
		if(input.isPressed(GLFW_KEY_LEFT))
			relativeRoll -= 0.001;
		if(input.isPressed(GLFW_KEY_UP))
			relativePitch += 0.001;
		if(input.isPressed(GLFW_KEY_DOWN))
			relativePitch -= 0.001;


		if(input.isPressed(GLFW_KEY_D))
			relativeYaw -= 0.001;
		if(input.isPressed(GLFW_KEY_A))
			relativeYaw += 0.001;


		Matrix4x4 move = Matrix4x4.createTranslation(new Vector3(relativeX, relativeY, relativeZ));
		move = move.getRotated(relativeYaw, relativePitch, relativeRoll);
		modelMatrix = modelMatrix.multiply(move);
	}

    private DrawablePartPosColor setupBody(final int shaderProgram){
		final Vector3 red = 	new Vector3(1, 0, 0);
		final Vector3 green =	new Vector3(0, 1, 0);
		final Vector3 blue = 	new Vector3(0, 0, 1);
		final Vector3 white = 	new Vector3(1, 1, 1);

		final float SIZE = 1f;

		//"LTR" = left top rear
		Vector3 posLTR = new Vector3(-SIZE, SIZE, SIZE);
		Vector3 posRTR = new Vector3( SIZE, SIZE, SIZE);
		Vector3 posRBR = new Vector3( SIZE,-SIZE, SIZE);
		Vector3 posLBR = new Vector3(-SIZE,-SIZE, SIZE);

		Vector3 posLTF = new Vector3(-SIZE, SIZE, -SIZE);
		Vector3 posRTF = new Vector3( SIZE, SIZE, -SIZE);
		Vector3 posRBF = new Vector3( SIZE,-SIZE, -SIZE);
		Vector3 posLBF = new Vector3(-SIZE,-SIZE, -SIZE);

		VertexPositionColor LTR = new VertexPositionColor(posLTR, red);
		VertexPositionColor RTR = new VertexPositionColor(posRTR, green);
		VertexPositionColor RBR = new VertexPositionColor(posRBR, blue);
		VertexPositionColor LBR = new VertexPositionColor(posLBR, white);

		VertexPositionColor LTF = new VertexPositionColor(posLTF, red);
		VertexPositionColor RTF = new VertexPositionColor(posRTF, green);
		VertexPositionColor RBF = new VertexPositionColor(posRBF, blue);
		VertexPositionColor LBF = new VertexPositionColor(posLBF, white);

		VertexPositionColor[] vertices = {
			LTR, RTR, RBR, LBR,
			LTF, RTF, RBF, LBF
		};

		int[] indices = {
			0, 2, 3, 	0, 1, 2,//Rear
			4, 3, 7, 	4, 0, 3,//Left
			5, 7, 6, 	5, 4, 7,//Front
			1, 6, 2, 	1, 5, 6,//Right
			4, 1, 0, 	4, 5, 1,//Top
			3, 6, 7, 	3, 2, 6	//Bottom
		};

		return new DrawablePartPosColor(vertices, indices, shaderProgram);
    }

    public void update(float deltaTime){
		float AirPlaneHeight = 1;
		Vector3 position = modelMatrix.getPosition();

		float terrainHeight = terrain.getHeight(position.getX(), position.getZ());
		if( position.getY() < terrainHeight + AirPlaneHeight){
			position.setY(terrainHeight + AirPlaneHeight);
			modelMatrix.setPosition(position);
		}
    }
}
