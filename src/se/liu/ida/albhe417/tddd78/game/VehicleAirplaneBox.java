package se.liu.ida.albhe417.tddd78.game;

import se.liu.ida.albhe417.tddd78.math.Vector3;
import se.liu.ida.albhe417.tddd78.math.Vector4;

import java.util.ArrayList;

public class VehicleAirplaneBox extends VehicleAirplane
{
    public VehicleAirplaneBox(Vector3 position, final int shaderProgram){
		super(position);
		setup(shaderProgram);
    }

    private void setup(final int shaderProgram){
		ArrayList<AbstractDrawablePart> parts = new ArrayList<>();
		parts.add(setupBody(shaderProgram));


		setupParts(parts);
    }

    private DrawablePartPosColor setupBody(final int shaderProgram){
		final Vector3 red = 	new Vector3(1, 0, 0);
		final Vector3 green =	new Vector3(0, 1, 0);
		final Vector3 blue = 	new Vector3(0, 0, 1);
		final Vector3 white = 	new Vector3(1, 1, 1);

		final float SIZE = 0.1f;

		//"LTR" = left top rear
		Vector3 posLTR = new Vector3(-SIZE, SIZE, SIZE);
		Vector3 posRTR = new Vector3( SIZE, SIZE, SIZE);
		Vector3 posRBR = new Vector3( SIZE,-SIZE, SIZE);
		Vector3 posLBR = new Vector3(-SIZE,-SIZE, SIZE);

		Vector3 posLTF = new Vector3(-SIZE, SIZE, -SIZE);
		Vector3 posRTF = new Vector3( SIZE, SIZE, -SIZE);
		Vector3 posRBF = new Vector3( SIZE,-SIZE, -SIZE);
		Vector3 posLBF = new Vector3(-SIZE,-SIZE, -SIZE);

		VertexPositionColor LTR = new VertexPositionColor(posLTR, white);
		VertexPositionColor RTR = new VertexPositionColor(posRTR, white);
		VertexPositionColor RBR = new VertexPositionColor(posRBR, white);
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

    public void update(){

    }
}
