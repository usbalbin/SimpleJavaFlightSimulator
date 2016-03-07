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
		Vector3 color = new Vector3(1, 1, 1);
		final int SIZE = 1;

		//"LTR" = left top rear
		Vector3 posLTR = new Vector3(-SIZE, SIZE, 0);
		Vector3 posRTR = new Vector3( SIZE, SIZE, 0);
		Vector3 posRBR = new Vector3( SIZE,-SIZE, 0);
		Vector3 posLBR = new Vector3(-SIZE,-SIZE, 0);

		VertexPositionColor LTR = new VertexPositionColor(posLTR, color);
		VertexPositionColor RTR = new VertexPositionColor(posRTR, color);
		VertexPositionColor RBR = new VertexPositionColor(posRBR, color);
		VertexPositionColor LBR = new VertexPositionColor(posLBR, color);


		VertexPositionColor[] vertices = {
				LTR, RTR, RBR, LBR
		};

		int[] indices = {
			4, 6, 7, 	4, 5, 6,//Front
		};

		return new DrawablePartPosColor(vertices, indices, shaderProgram);

	}

    private DrawablePartPosColor setupBody_box(final int shaderProgram){
		Vector3 color = new Vector3(1, 1, 1);
		final int SIZE = 1;

		//"LTR" = left top rear
		Vector3 posLTR = new Vector3(-SIZE, SIZE, SIZE);
		Vector3 posRTR = new Vector3( SIZE, SIZE, SIZE);
		Vector3 posRBR = new Vector3( SIZE,-SIZE, SIZE);
		Vector3 posLBR = new Vector3(-SIZE,-SIZE, SIZE);

		Vector3 posLTF = new Vector3(-SIZE, SIZE, -SIZE);
		Vector3 posRTF = new Vector3( SIZE, SIZE, -SIZE);
		Vector3 posRBF = new Vector3( SIZE,-SIZE, -SIZE);
		Vector3 posLBF = new Vector3(-SIZE,-SIZE, -SIZE);

		VertexPositionColor LTR = new VertexPositionColor(posLTR, color);
		VertexPositionColor RTR = new VertexPositionColor(posRTR, color);
		VertexPositionColor RBR = new VertexPositionColor(posRBR, color);
		VertexPositionColor LBR = new VertexPositionColor(posLBR, color);

		VertexPositionColor LTF = new VertexPositionColor(posLTF, color);
		VertexPositionColor RTF = new VertexPositionColor(posRTF, color);
		VertexPositionColor RBF = new VertexPositionColor(posRBF, color);
		VertexPositionColor LBF = new VertexPositionColor(posLBF, color);

		VertexPositionColor[] vertices = {
			LTR, RTR, RBR, LBR,
			LTF, RTF, RBF, LBF
		};

		int[] indices = {
			0, 2, 3, 	0, 1, 2,//Rear
			5, 3, 6, 	5, 0, 3,//Left
			4, 6, 7, 	4, 5, 6,//Front
			1, 7, 2, 	1, 4, 7,//Right
			5, 1, 0, 	5, 4, 1,//Top
			3, 7, 6, 	3, 2, 7	//Bottom
		};

		return new DrawablePartPosColor(vertices, indices, shaderProgram);
    }

    public void update(){

    }
}
