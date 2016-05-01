package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.dynamics.DynamicsWorld;
import se.liu.ida.albhe417.tddd78.game.InputHandler;
import se.liu.ida.albhe417.tddd78.game.game_object.weapons.Gun;
import se.liu.ida.albhe417.tddd78.game.game_object.weapons.Weapon;
import se.liu.ida.albhe417.tddd78.game.game_object_part.Wing;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import static org.lwjgl.glfw.GLFW.*;

/**
 * AbstractVehicleBox might be useful to inherit from for most simple box-shaped vehicles
 *
 * Contains methods for setting up the graphics-box as well as the physics box, drawing and handling input is also implemented
 * here. However acting on the input will have to be implemented by extending class.
 */
abstract public class AbstractVehicleBox extends AbstractVehicle {
	private final Weapon weaponLeft;
	private final Weapon weaponRight;

	AbstractVehicleBox(final Vector3 position, final DynamicsWorld physics, final float maxHealth, final String playerName, int shaderProgram)
	{
		super(position, physics, maxHealth, playerName);
		this.weaponLeft = new Gun(new Vector3(-2, 0, -2), this, physics, shaderProgram);
		this.weaponRight = new Gun(new Vector3(+2, 0, -2), this, physics, shaderProgram);
	}

	void setupBoxBody(float mass, int shaderProgram, DynamicsWorld physics){
		final Vector3 size = new Vector3(2, 0.5f, 2);
		partBody = new Wing(Vector3.ZERO, size, mass, modelMatrix, shaderProgram, physics, this);
		parts.add(partBody);
	}

	public void handleInput(float deltaTime){

		float deltaThrottle = 0.0f;
		float yawValue = 0;
		float pitchValue = 0;
		float rollValue = 0;

		InputHandler input = InputHandler.getInstance();
		if(input.isPressed(GLFW_KEY_W))
			deltaThrottle += 1;
		if(input.isPressed(GLFW_KEY_S))
			deltaThrottle -= 1;

		if(input.isPressed(GLFW_KEY_A))
			yawValue += 1;
		if(input.isPressed(GLFW_KEY_D))
			yawValue -= 1;

		if(input.isPressed(GLFW_KEY_UP))
			pitchValue += 1;
		if(input.isPressed(GLFW_KEY_DOWN))
			pitchValue -= 1;

		if(input.isPressed(GLFW_KEY_RIGHT))
			rollValue += 1;
		if(input.isPressed(GLFW_KEY_LEFT))
			rollValue -= 1;

		if(input.isPressed(GLFW_KEY_SPACE)) {
			weaponLeft.fire(deltaTime);
			weaponRight.fire(deltaTime);
		}

		calcHandling(deltaThrottle, yawValue, pitchValue, rollValue, deltaTime);
	}


	abstract protected void calcHandling(float deltaThrottle, float yawValue, float pitchValue, float rollValue, float deltaTime);


	@Override public void draw(final Matrix4x4 cameraMatrix, final int modelViewProjectionMatrixId, final int modelMatrixId) {
		super.draw(cameraMatrix, modelViewProjectionMatrixId, modelMatrixId);
		weaponLeft.draw(cameraMatrix, modelViewProjectionMatrixId, modelMatrixId);
		weaponRight.draw(cameraMatrix, modelViewProjectionMatrixId, modelMatrixId);
	}

	@Override
	public void destroy() {
		super.destroy();
		weaponLeft.destroy();
		weaponRight.destroy();
	}
}
