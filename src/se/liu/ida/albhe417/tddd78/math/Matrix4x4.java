package se.liu.ida.albhe417.tddd78.math;

import javax.vecmath.Matrix4f;

public class Matrix4x4 extends AbstractMatrix
{

    public Matrix4x4(float[][] matrixArray){
		super(matrixArray);
		if(matrixArray.length != 4)
			throw new IllegalArgumentException("Wrong sized array");
		for(float[] row : matrixArray){
			if(row.length != 4)
			throw new IllegalArgumentException("Wrong sized array");
		}
    }

	public Matrix4x4(Matrix4f matrix){
		this();
		float[][] floats = this.values;
		int j = 0;
		for(int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				matrix.getColumn(row, floats[row]);//Res matrix has columns switched with rows
	}

    public Matrix4x4(){
    	this(1.0f);
    }

	public Matrix4x4(float scale){
		super(new float[4][4]);
		values[0] = new float[]{scale, 0, 	  0,	 0	  };
		values[1] = new float[]{0, 	   scale, 0, 	 0	  };
		values[2] = new float[]{0, 	   0, 	  scale, 0	  };
		values[3] = new float[]{0, 	   0, 	  0, 	 scale};
	}

    public Matrix4x4(Vector4 zero, Vector4 one, Vector4 two, Vector4 three){
		super(new float[4][4]);
		values[0] = zero.getNewFloats();
		values[1] = one.getNewFloats();
		values[2] = two.getNewFloats();
		values[3] = three.getNewFloats();
    }

    public static Matrix4x4 createTranslation(Vector3 position){
		Matrix4x4 res = new Matrix4x4();
		res.values[3][0] = position.getX();
		res.values[3][1] = position.getY();
		res.values[3][2] = position.getZ();
		return res;
    }

	public static Matrix4x4 createProjectionMatrix(float fieldOfView, float apsectRatio, float nearLimit, float farLimit){
		Matrix4x4 result = new Matrix4x4(0);

		final float yScale = 1.0f / (float)Math.tan(fieldOfView / 2.0f);
		final float xScale = yScale / apsectRatio;
		final float zFactor1 = -(farLimit + nearLimit) / (farLimit - nearLimit);
		final float zFactor2 = -(2.0f * nearLimit * farLimit) / (farLimit - nearLimit);
		final float wFactor = -1;

		//TODO: Fix: stuff gets larger the further away they get
		result.values[0][0] = xScale;
		result.values[1][1] = yScale;
		result.values[2][2] = zFactor1;
		result.values[2][3] = wFactor;
		result.values[3][2] = zFactor2;

		return result;
	}


	public static Matrix4x4 createViewMatrix(Vector3 cameraPos, Vector3 target, Vector3 upVector){
		Vector3 forward = target.sub(cameraPos).getNormalized();
		Vector3 side = forward.cross(upVector).getNormalized();
		Vector3 up = side.cross(forward);

		float[][] values = {
			{ side.getX(),		 	up.getX(), 			-forward.getX(), 		0.0f},
			{ side.getY(),			up.getY(),			-forward.getY(), 		0.0f},
			{ side.getZ(),			up.getZ(),			-forward.getZ(),		0.0f},
			{-side.dot(cameraPos), -up.dot(cameraPos), 	forward.dot(cameraPos),1.0f}
		};

		return new Matrix4x4(values);
	}

	public Matrix4x4 multiply(Matrix4x4 other){
		Matrix4x4 result = new Matrix4x4(0);

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				for(int i = 0; i < 4; i++)//TODO kolla om row och column ska byta plats
					result.values[row][column] += this.values[i][column] * other.values[row][i];
			}
		}

		return result;
	}

	public Vector4 multiply(Vector4 other){
		Vector4 result = new Vector4();

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				result.values[row] += this.values[row][column] * other.values[column];
			}
		}
		return result;
	}

	//TODO: Remove "vector"?
	public Vector3 multiply(Vector3 other, boolean isPosition){
		float w = isPosition ? 1 : 0;
		Vector4 vector = new Vector4(other.getX(), other.getY(), other.getZ(), w);
		Vector4 result = new Vector4();

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				result.values[row] += this.values[row][column] * vector.values[column];
			}
		}
		return result.toVector3();

	}


	public Matrix4x4 getRotatedAboutX(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[1] = new float[]{0, cos, -sin, 0};
		result.values[2] = new float[]{0, sin,  cos, 0};

		return this.multiply(result);
	}

	public Matrix4x4 getRotatedAboutY(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[0] = new float[]{cos, 0, -sin, 0};
		result.values[2] = new float[]{sin, 0,  cos, 0};

		return this.multiply(result);
	}

	public Matrix4x4 getRotatedAboutZ(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[0] = new float[]{cos, -sin, 0, 0};
		result.values[1] = new float[]{sin,  cos,  0, 0};

		return this.multiply(result);
	}

	public Matrix4x4 getRotated(float yaw, float pitch, float roll){
		return this.getRotatedAboutY(yaw).getRotatedAboutX(pitch).getRotatedAboutZ(roll);
	}

	public Matrix4x4 getTranslated(Vector3 move){
		return this.multiply(createTranslation(move));
	}

	//TODO: ta bort kanske
	public Matrix4x4 copy(){
		float[][] values = new float[4][4];
		for(int row = 0; row < 4; row++){
			for(int column = 0; column < 4; column++)
				values[row][column] = this.values[row][column];
		}

		return new Matrix4x4(values);
	}

	public Vector3 getPosition(){
		return new Vector3(values[3][0], values[3][1], values[3][2]);
	}

	public void setPosition(Vector3 position){
		values[3][0] = position.getX();
		values[3][1] = position.getY();
		values[3][2] = position.getZ();
	}

	//TODO make sure this really works
	public Matrix4x4 getInverse(){
		float[][] values = new float[4][4];

		//Flip frustumMatrix
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 4; column++) {
				values[row][column] = this.values[column][row];
			}
		}
		values[0][3] = 0;
		values[1][3] = 0;
		values[2][3] = 0;
		values[3][3] = 1;

		Matrix4x4 result = new Matrix4x4(values);
		Vector4 lastRowIn = new Vector4(-this.values[3][0], -this.values[3][1], -this.values[3][2], this.values[3][3]);
		Vector4 lastRowRes = result.multiply(lastRowIn);
		result.values[3] = lastRowRes.values;
		return result;
	}

	public Matrix4f toMatrix4f(){
		float[] floats = new float[16];
		int j = 0;
		for(int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				floats[j++] = this.getValueAt(row, col);//Res matrix has columns switched with rows

		return new Matrix4f(floats);
	}

	public static void main(String[] args) {
		Matrix4x4 m = new Matrix4x4(
				new float[][]{
						{1, 2, 3, 4},
						{5, 6, 7, 8},
						{9, 10, 11, 12},
						{13, 14, 15, 16}
				}
		);

		Matrix4x4 n = m.getInverse().getInverse();
	}
}
