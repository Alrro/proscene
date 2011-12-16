package remixlab.proscene;

import remixlab.remixcam.geom.Matrix3D;

public class Mat3D extends Matrix3D {
	public Mat3D() {
		super();
	}

	public Mat3D(float _m00, float _m01, float _m02, float _m10, float _m11, float _m12) {
		super(_m00, _m01, _m02, 0, _m10, _m11, _m12, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public Mat3D(float _m00, float _m01, float _m02, float _m03,
			         float _m10, float _m11, float _m12, float _m13,
			         float _m20, float _m21, float _m22, float _m23,
			         float _m30, float _m31, float _m32, float _m33) {
		super(_m00, _m01, _m02, _m03, _m10, _m11, _m12, _m13, _m20, _m21, _m22, _m23, _m30, _m31, _m32, _m33);
	}

	public Mat3D(Matrix3D matrix) {
		super(matrix);
	}

	public Mat3D(Object any) {
		try {
			float[] result = new float[16];
			any.getClass().getMethod("get", new Class[] { float[].class }).invoke(any, result);

			mat[0] = result[0];
			mat[4] = result[1];
			mat[8] = result[2];
			mat[12] = result[3];

			mat[1] = result[4];
			mat[5] = result[5];
			mat[9] = result[6];
			mat[13] = result[7];

			mat[2] = result[8];
			mat[6] = result[9];
			mat[10] = result[10];
			mat[14] = result[11];

			mat[3] = result[12];
			mat[7] = result[13];
			mat[11] = result[14];
			mat[15] = result[15];

		} catch (Exception e) {
			throw (new RuntimeException("vec cannot handle class in constructor: "
					+ any.getClass(), e));
		}
	}
}
