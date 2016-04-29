package se.liu.ida.albhe417.tddd78.game.game_object.vehicles;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import se.liu.ida.albhe417.tddd78.game.Game;
import se.liu.ida.albhe417.tddd78.game.InputHandler;
import se.liu.ida.albhe417.tddd78.game.VertexPositionColorNormal;
import se.liu.ida.albhe417.tddd78.game.game_object.misc.Gun;
import se.liu.ida.albhe417.tddd78.game.game_object.misc.Weapon;
import se.liu.ida.albhe417.tddd78.game.game_object_Part.GameObjectPart;
import se.liu.ida.albhe417.tddd78.math.Matrix4x4;
import se.liu.ida.albhe417.tddd78.math.Vector3;

import javax.vecmath.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

abstract public class AbstractVehicleBox extends AbstractVehicle {
    private final Weapon weaponLeft;
    private final Weapon weaponRight;

    public AbstractVehicleBox(final Vector3 position, final float mass, final float thrustFactor, final DynamicsWorld physics,
			      final Game game, final float maxHealth, final String playerName, int shaderProgram)
    {
	super(position, mass, thrustFactor, physics, game, maxHealth, playerName);
	this.weaponLeft = new Gun(new Vector3(-2, 0, -2), this, physics, shaderProgram, game, playerName + "'s left gun");
	this.weaponRight = new Gun(new Vector3(+2, 0, -2), this, physics, shaderProgram, game, playerName + "'s right gun");
    }

    protected void setupBody(final int shaderProgram, DynamicsWorld physics){
            final Vector3 red = 	new Vector3(1, 0, 0);
            final Vector3 green =	new Vector3(0, 1, 0);
            final Vector3 blue = 	new Vector3(0, 0, 1);
            final Vector3 white = 	new Vector3(1, 1, 1);

            final Vector3 size = new Vector3(2, 0.5f, 2);

            //"leftTopRight" = left top rear
            Vector3 posLTR = new Vector3(-size.getX(), size.getY(), size.getZ());
            Vector3 posRTR = new Vector3( size.getX(), size.getY(), size.getZ());
            Vector3 posRBR = new Vector3( size.getX(),-size.getY(), size.getZ());
            Vector3 posLBR = new Vector3(-size.getX(),-size.getY(), size.getZ());

            Vector3 posLTF = new Vector3(-size.getX(), size.getY(), -size.getZ());
            Vector3 posRTF = new Vector3( size.getX(), size.getY(), -size.getZ());
            Vector3 posRBF = new Vector3( size.getX(),-size.getY(), -size.getZ());
            Vector3 posLBF = new Vector3(-size.getX(),-size.getY(), -size.getZ());


            VertexPositionColorNormal leftTopRear = new VertexPositionColorNormal(posLTR, red, posLTR);
            VertexPositionColorNormal rightTopRear = new VertexPositionColorNormal(posRTR, green, posRTR);
            VertexPositionColorNormal rightBottomRear = new VertexPositionColorNormal(posRBR, blue, posRBR);
            VertexPositionColorNormal leftBottomRear = new VertexPositionColorNormal(posLBR, white, posLBR);

            VertexPositionColorNormal leftTopFront = new VertexPositionColorNormal(posLTF, red, posLTF);
            VertexPositionColorNormal rightTopFront = new VertexPositionColorNormal(posRTF, green, posRTF);
            VertexPositionColorNormal rightBottomFront = new VertexPositionColorNormal(posRBF, blue, posRBF);
            VertexPositionColorNormal leftBottomFront = new VertexPositionColorNormal(posLBF, white, posLBF);

            VertexPositionColorNormal[] vertices = {
                leftTopRear, rightTopRear, rightBottomRear, leftBottomRear,
                leftTopFront, rightTopFront, rightBottomFront, leftBottomFront
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

            CollisionShape shape = new BoxShape(size.toVector3f());
            Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(mass, inertia);
            RigidBody physicsObject = new RigidBody(mass, motionState, shape, inertia);
            physicsObject.setUserPointer(this);

            physics.addRigidBody(physicsObject);

            partBody = new GameObjectPart(vertices, indices, shaderProgram, physicsObject);
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

	calcAerodynamics(deltaThrottle, yawValue, pitchValue, rollValue, deltaTime);
    }


    abstract protected void calcAerodynamics(float deltaThrottle, float yawValue, float pitchValue, float rollValue, float deltaTime);


    @Override public void draw(final Matrix4x4 cameraMatrix, final int MVPMatrixId, final int modelMatrixId) {
    	super.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
    	weaponLeft.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
    	weaponRight.draw(cameraMatrix, MVPMatrixId, modelMatrixId);
    }

    @Override
    public void destroy() {
	super.destroy();
	weaponLeft.destroy();
	weaponRight.destroy();
    }
}
