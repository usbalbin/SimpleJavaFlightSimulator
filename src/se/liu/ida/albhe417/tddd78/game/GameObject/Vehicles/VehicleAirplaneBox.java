package se.liu.ida.albhe417.tddd78.game.GameObject.Vehicles;
import static org.lwjgl.glfw.GLFW.*;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.*;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import java.util.ArrayList;

public class VehicleAirplaneBox extends VehicleAirplane
{

    public VehicleAirplaneBox(final Vector3 position, final Terrain terrain, final int shaderProgram, DynamicsWorld physics, Game game){
		super(position, 100.0f, 1.0f, physics, game);
		setup(shaderProgram);
    }

    private void setup(final int shaderProgram){
		this.parts = new ArrayList<>(1);
		partBody = setupBody(shaderProgram);
		parts.add(partBody);
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

    private GameObjectPart setupBody(final int shaderProgram){
		final Vector3 red = 	new Vector3(1, 0, 0);
		final Vector3 green =	new Vector3(0, 1, 0);
		final Vector3 blue = 	new Vector3(0, 0, 1);
		final Vector3 white = 	new Vector3(1, 1, 1);

		final float SIZE = 1.0f;

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

		//Physics

		Transform transform = new Transform(modelMatrix.toMatrix4f());
		MotionState motionState = new DefaultMotionState(transform);

		CollisionShape shape = new BoxShape(new Vector3(SIZE).toVector3f());
		RigidBody physicsObject = new RigidBody(MASS, motionState, shape);

		return new GameObjectPart(vertices, indices, shaderProgram, physicsObject);
    }

    public void update(float deltaTime){

    }
}
