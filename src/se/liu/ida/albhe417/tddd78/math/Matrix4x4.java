package se.liu.ida.albhe417.tddd78.math;

import javax.vecmath.Matrix4f;

/**
 * Project TDDD78
 *
 * File created by Albin.
 */
public class Matrix4x4 {
	public final float[][] values;

    public Matrix4x4(float[][] matrixArray){
		assert matrixArray.length == 4 : "Wrong sized array";
		for(float[] row : matrixArray)
			assert row.length == 4 : "Wrong sized array";

		this.values = matrixArray;
    }

	public Matrix4x4(Matrix4f matrix){
		this();
		float[][] floats = this.values;
		for(int row = 0; row < 4; row++)
			matrix.getColumn(row, floats[row]);//Res matrix has columns switched with rows
	}

    public Matrix4x4(){
    	this(1.0f);
    }

	public Matrix4x4(float scale){
		this(new float[4][4]);

		this.values[0] = new float[]{scale, 0, 	  0,	 0	  };
		this.values[1] = new float[]{0, 	   scale, 0, 	 0	  };
		this.values[2] = new float[]{0, 	   0, 	  scale, 0	  };
		this.values[3] = new float[]{0, 	   0, 	  0, 	 scale};
	}

	public static Matrix4x4 createTranslation(Vector3 position){
		Matrix4x4 res = new Matrix4x4();
		res.values[3][0] = position.getX();
		res.values[3][1] = position.getY();
		res.values[3][2] = position.getZ();
		return res;
    }

	public static Matrix4x4 createProjectionMatrix(float fieldOfView, float aspectRatio, float nearLimit, float farLimit){
		Matrix4x4 result = new Matrix4x4(0);

		final float yScale = 1.0f / (float)Math.tan(fieldOfView / 2.0f);
		final float xScale = yScale / aspectRatio;
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
				for(int i = 0; i < 4; i++)
					result.values[row][column] += this.values[i][column] * other.values[row][i];
			}
		}

		return result;
	}

	//TODO find out why row and columns are flipped compared to multiply for Vector3
	public Vector4 multiply(Vector4 other){
		Vector4 result = new Vector4();

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				result.values[row] += this.values[row][column] * other.values[column];
			}
		}
		return result;
	}

	public Vector3 multiply(Vector3 other, boolean isPosition){
		float w = isPosition ? 1 : 0;
		Vector4 vector = new Vector4(other, w);
		Vector4 result = new Vector4();

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				result.values[row] += this.values[column][row] * vector.values[column];
			}
		}
		return result.toVector3();
	}

	public Matrix4x4 getTranslated(Vector3 move){
		return this.multiply(createTranslation(move));
	}

	public Vector3 getPosition(){
		return new Vector3(values[3][0], values[3][1], values[3][2]);//Vector3(values[0][3], values[1][3], values[2][3]);
	}

	public Matrix4f toMatrix4f() {
	    float[] floats = new float[16];
	    int j = 0;
	    for (int row = 0; row < 4; row++)
		for (int col = 0; col < 4; col++){
		    floats[j] = this.values[col][row];//getValueAt(row, col);//Res matrix has columns switched with rows
		    j++;
		}

	    return new Matrix4f(floats);
	}

}
