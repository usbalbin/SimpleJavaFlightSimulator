package se.liu.ida.albhe417.tddd78.math;

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

    public static Matrix4x4 createPosFromVector(Vector3 position){
		Matrix4x4 res = new Matrix4x4();
		res.setValueAt(3, 0, position.getX());
		res.setValueAt(3, 1, position.getY());
		res.setValueAt(3, 2, position.getZ());
		return res;
    }

	public static Matrix4x4 createProjectionMatrix(float fieldOfView, float apsectRatio, float nearLimit, float farLimit){
		Matrix4x4 result = new Matrix4x4(0);

		final float yScale = 1.0f / (float)Math.tan(fieldOfView / 2.0f);
		final float xScale = yScale / apsectRatio;
		final float zFactor1 = -(farLimit + nearLimit) / (farLimit - nearLimit);
		final float zFactor2 = -((2.0f * nearLimit * farLimit) / (farLimit - nearLimit));
		final float wFactor = -1;

		//TODO: Fix: stuff gets larger the further away they get
		result.values[0][0] = xScale;
		result.values[1][1] = yScale;
		result.values[2][2] = zFactor1;
		result.values[2][3] = wFactor;
		result.values[3][2] = zFactor2;

		return result;
	}

	public Matrix4x4 multiply(Matrix4x4 other){
		Matrix4x4 result = new Matrix4x4(0);

		for(int row = 0; row < 4; row++){
			for (int column = 0; column < 4; column++){
				for(int i = 0; i < 4; i++)
					result.values[row][column] += this.values[row][i] * other.values[i][column];
			}
		}

		return result;
	}

	public Matrix4x4 getRotatedAboutX(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[1] = new float[]{0, cos, -sin, 0};
		result.values[2] = new float[]{0, sin,  cos, 0};

		return result;
	}

	public Matrix4x4 getRotatedAboutY(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[0] = new float[]{cos, 0, -sin, 0};
		result.values[2] = new float[]{sin, 0,  cos, 0};

		return result;
	}

	public Matrix4x4 getRotatedAboutZ(float angle){
		Matrix4x4 result = new Matrix4x4();

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		result.values[0] = new float[]{cos, -sin, 0, 0};
		result.values[1] = new float[]{sin,  cos,  0, 0};

		return result;
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
}
