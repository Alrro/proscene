package remixlab.proscene;

import remixlab.remixcam.geom.Matrix;
import remixlab.remixcam.geom.Matrix3D;

public class Mat3D extends Matrix3D {
	public Mat3D() {
		super();
	}

	public Mat3D(float m00, float m01, float m02, float m10, float m11, float m12) {
		super(m00, m01, m02, 0, m10, m11, m12, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public Mat3D(float m00, float m01, float m02, float m03, float m10,
			float m11, float m12, float m13, float m20, float m21, float m22,
			float m23, float m30, float m31, float m32, float m33) {
		super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31,
				m32, m33);
	}

	public Mat3D(Matrix matrix) {
		super(matrix);
	}

	public Mat3D(Object any) {
		try {
			float[] result = new float[16];
			any.getClass().getMethod("get", new Class[] { float[].class }).invoke(any, result);

			m00 = result[0];
			m01 = result[1];
			m02 = result[2];
			m03 = result[3];

			m10 = result[4];
			m11 = result[5];
			m12 = result[6];
			m13 = result[7];

			m20 = result[8];
			m21 = result[9];
			m22 = result[10];
			m23 = result[11];

			m30 = result[12];
			m31 = result[13];
			m32 = result[14];
			m33 = result[15];

		} catch (Exception e) {
			throw (new RuntimeException("vec cannot handle class in constructor: "
					+ any.getClass(), e));
		}
	}
}
