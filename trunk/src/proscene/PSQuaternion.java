/**
 * This package provides classes to ease the creation of interactive 3D
 * scenes implemented with the PSSL Processing renderer.
 */

package proscene;

import processing.core.*;

/**
 * A 4 element unit quaternion represented by single precision floating point
 * x,y,z,w coordinates.
 * 
 */

public class PSQuaternion {

	final static float EPS10 = 1E-10f;
	final static float EPS8 = 1E-8f;
	final static float EPS6 = 1E-6f;
	final static float EPS5 = 1E-5f;
	final static float PI = 3.14159265359f;
	final static float PIO2 = 1.57079632679f;

	/**
	 * The x coordinate, i.e., the x coordinate of the vector part of the
	 * PSQuaternion.
	 */
	public float x;

	/**
	 * The y coordinate, i.e., the y coordinate of the vector part of the
	 * PSQuaternion.
	 */
	public float y;

	/**
	 * The z coordinate, i.e., the z coordinate of the vector part of the
	 * PSQuaternion.
	 */
	public float z;

	/**
	 * The w coordinate which corresponds to the scalar part of the PSQuaternion.
	 */
	public float w;

	/**
	 * Constructs and initializes a PSQuaternion to (0.0,0.0,0.0,1.0), i.e., an
	 * identity rotation.
	 */
	public PSQuaternion() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 1;
	}
	
	/**
	 * Default constructor for PSQuaternion(float x, float y, float z, float w,
	 * boolean normalize), with {@code normalize=true}.  
	 * 
	 */
	public PSQuaternion(float x, float y, float z, float w) {
		this(x, y, z, w, true);
	}

	/**
	 * Constructs and initializes a PSQuaternion from the specified xyzw
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param z
	 *            the z coordinate
	 * @param w
	 *            the w scalar component
	 * @param normalize
	 *            tells whether or not the constructed PSQuaternion should be normalized.
	 */
	public PSQuaternion(float x, float y, float z, float w, boolean normalize) {
		if (normalize) {
			float mag = PApplet.sqrt(x * x + y * y + z * z + w * w);
			if (mag > 0.0f) {
				this.x = x / mag;
				this.y = y / mag;
				this.z = z / mag;
				this.w = w / mag;
			} else {
				this.x = 0;
				this.y = 0;
				this.z = 0;
				this.w = 1;
			}
		} else {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
	}

	/**
	 * Default constructor for PSQuaternion(float[] q, boolean normalize)
	 * with {@code normalize=true}.  
	 * 
	 */
	public PSQuaternion(float[] q) {
		this(q, true);
	}
		
	/**
	 * Constructs and initializes a PSQuaternion from the array of length 4.
	 * 
	 * @param q
	 *            the array of length 4 containing xyzw in order
	 */
	public PSQuaternion(float[] q, boolean normalize) {
		if (normalize) {
			float mag = PApplet.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]
					+ q[3] * q[3]);
			if (mag > 0.0f) {
				this.x = q[0] / mag;
				this.y = q[1] / mag;
				this.z = q[2] / mag;
				this.w = q[3] / mag;
			} else {
				this.x = 0;
				this.y = 0;
				this.z = 0;
				this.w = 1;
			}
		} else {
			this.x = q[0];
			this.y = q[1];
			this.z = q[2];
			this.w = q[3];
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param q1
	 *            the PSQuaternion containing the initialization x y z w data
	 */
	public PSQuaternion(PSQuaternion q1) {
		this.x = q1.x;
		this.y = q1.y;
		this.z = q1.z;
		this.w = q1.w;
		this.normalize();
	}

	/**
	 * Constructs and initializes a PSQuaternion from the specified
	 * rotation {@link #axis() axis} (non null) and {@link #angle() angle} (in radians).
	 * 
	 * @param axis
	 *            the PVector representing the axis
	 * @param angle
	 *            the angle in radians
	 *            
	 * @see #fromAxisAngle(PVector, float)
	 */
	public PSQuaternion(PVector axis, float angle) {
		fromAxisAngle(axis, angle);
	}

	/**
	 * Constructs a PSQuaternion that will rotate from the
	 * {@code from} direction to the {@code to} direction.
	 * 
	 * @param from
	 *            the first PVector
	 * @param to
	 *            the second PVector
	 *
	 * @see #fromTo(PVector, PVector)
	 */
	public PSQuaternion(PVector from, PVector to) {
		fromTo(from, to);
	}

	/**
	 * Sets the value of this PSQuaternion to the conjugate of itself.
	 */
	public final void conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	/**
	 * Sets the value of this PSQuaternion to the conjugate of PSQuaternion q1.
	 * 
	 * @param q1
	 *            the source vector
	 */
	public final void conjugate(PSQuaternion q1) {
		this.x = -q1.x;
		this.y = -q1.y;
		this.z = -q1.z;
		this.w = q1.w;
	}

	/**
	 * Negates all the coefficients of the PSQuaternion.
	 */
	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	/**
	 * Returns the "dot" product of this PSQuaternion and {@code b}:
	 * <p> 
	 * {@code this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w}
	 * 
	 * @param b
	 *            the PSQuaternion    
	 */
	public final float dotProduct(PSQuaternion b) {
		return this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w;
	}

	/**
	 * Returns the "dot" product of {@code a} and {@code b}: 
	 * <p> 
	 * {@code a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w}
	 * 
	 * @param a
	 *            the first PSQuaternion
	 * @param b
	 *            the second PSQuaternion
	 */
	public final static float dotProduct(PSQuaternion a, PSQuaternion b) {
		return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
	}

	/**
	 * Sets the value of this PSQuaternion to the PSQuaternion product of itself and
	 * {@code q1}, (i.e., {@code this = this * q1}).
	 * 
	 * @param q1
	 *            the other PSQuaternion
	 */
	public final void multiply(PSQuaternion q1) {
		float x, y, w;

		w = this.w * q1.w - this.x * q1.x - this.y * q1.y - this.z * q1.z;
		x = this.w * q1.x + q1.w * this.x + this.y * q1.z - this.z * q1.y;
		y = this.w * q1.y + q1.w * this.y - this.x * q1.z + this.z * q1.x;
		this.z = this.w * q1.z + q1.w * this.z + this.x * q1.y - this.y * q1.x;
		this.w = w;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the PSQuaternion which is product of quaternions {@code q1} and {@code q2}.
	 * 
	 * @param q1
	 *            the first PSQuaternion
	 * @param q2
	 *            the second PSQuaternion
	 */
	public final static PSQuaternion multiply(PSQuaternion q1, PSQuaternion q2) {
		float x, y, z, w;
		w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
		x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
		y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
		z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
		return new PSQuaternion(x, y, z, w);
	}

	/**
	 * Returns the image of {@code v} by the rotation of this vector.
	 * Same as {@code this.rotate(v).}
	 * 
	 * @param v
	 *            the PVector
	 *            
	 * @see #rotate(PVector)
	 * @see #inverseRotate(PVector)
	 */
	public final PVector multiply(PVector v) {
		return this.rotate(v);
	}

	/**
	 * Returns the image of {@code v} by the rotation {@code q1}. Same as {@code q1.rotate(v).}
	 * 
	 * @param q1
	 *            the PSQuaternion
	 * 
	 * @param v
	 *            the PVector
	 *            
	 * @see #rotate(PVector)
	 * @see #inverseRotate(PVector)
	 */
	public static final PVector multiply(PSQuaternion q1, PVector v) {
		return q1.rotate(v);
	}

	/**
	 * Multiplies this PSQuaternion by the inverse of PSQuaternion {@code q1} and places the
	 * value into this PSQuaternion (i.e., {@code this = this * q^-1}). The value of the
	 * argument PSQuaternion is preserved.
	 * 
	 * @param q1
	 *            the other PSQuaternion
	 */
	public final void multiplyInverse(PSQuaternion q1) {
		PSQuaternion tempQuat = new PSQuaternion(q1);
		tempQuat.invert();
		this.multiply(tempQuat);
	}

	/**
	 * Returns the product of PSQuaternion {@code q1} by the inverse of PSQuaternion {@code q2}
	 * (i.e., {@code q1 * q2^-1}). The value of both argument quaternions is preserved.
	 * 
	 * @param q1
	 *            the first PSQuaternion
	 * @param q2
	 *            the second PSQuaternion
	 */
	public static final PSQuaternion multiplyInverse(PSQuaternion q1,
			PSQuaternion q2) {
		PSQuaternion tempQuat = new PSQuaternion(q2);
		tempQuat.invert();
		return PSQuaternion.multiply(q1, tempQuat);
	}

	/**
	 * Returns the inverse PSQuaternion (inverse rotation). 
	 * <p>  
	 * The result has a negated {@link #axis()} direction and the
	 * same {@link #angle()}. 
	 * <p> 
	 * A composition of a PSQuaternion and its {@link #inverse()} results in
	 * an identity function. Use {@link #invert()} to actually
	 * modify the PSQuaternion.
	 * 
	 * @see #invert()
	 */
	public final PSQuaternion inverse() {
		PSQuaternion tempQuat = new PSQuaternion(this);
		tempQuat.invert();
		return tempQuat;
	}

	/**
	 * Sets the value of this PSQuaternion to the inverse of itself.
	 * 
	 * @see #inverse()
	 */
	public final void invert() {
		float sqNorm = squaredNorm(this);
		this.w /= sqNorm;
		this.x /= -sqNorm;
		this.y /= -sqNorm;
		this.z /= -sqNorm;
	}

	/**
	 * Sets the value of this PSQuaternion to the PSQuaternion inverse of {@code q1}.
	 * 
	 * @param q1
	 *            the PSQuaternion to be inverted
	 */
	public final void invert(PSQuaternion q1) {
		float sqNorm = squaredNorm(q1);
		this.w = q1.w / sqNorm;
		this.x = -q1.x / sqNorm;
		this.y = -q1.y / sqNorm;
		this.z = -q1.z / sqNorm;
	}

	/**
	 * Normalizes the value of this PSQuaternion in place and return its {@code norm}.
	 */
	public final float normalize() {
		float norm = PApplet.sqrt(this.x * this.x + this.y * this.y + this.z
				* this.z + this.w * this.w);
		if (norm > 0.0f) {
			this.x /= norm;
			this.y /= norm;
			this.z /= norm;
			this.w /= norm;
		} else {
			this.x = (float) 0.0;
			this.y = (float) 0.0;
			this.z = (float) 0.0;
			this.w = (float) 1.0;
		}
		return norm;
	}

	/**
	 * Returns the image of {@code v} by the PSQuaternion rotation.
	 * 
	 * @param v
	 *            the PVector
	 */
	public final PVector rotate(PVector v) {
		float q00 = 2.0f * x * x;
		float q11 = 2.0f * y * y;
		float q22 = 2.0f * z * z;

		float q01 = 2.0f * x * y;
		float q02 = 2.0f * x * z;
		float q03 = 2.0f * x * w;

		float q12 = 2.0f * y * z;
		float q13 = 2.0f * y * w;

		float q23 = 2.0f * z * w;

		return new PVector((1.0f - q11 - q22) * v.x + (q01 - q23) * v.y
				+ (q02 + q13) * v.z, (q01 + q23) * v.x + (1.0f - q22 - q00)
				* v.y + (q12 - q03) * v.z, (q02 - q13) * v.x + (q12 + q03)
				* v.y + (1.0f - q11 - q00) * v.z);
	}

	/**
	 * Returns the image of {@code v} by the PSQuaternion {@link #inverse()} rotation. 
	 * <p> 
	 * {@link #rotate(PVector)} performs an inverse transformation.
	 * 
	 * @param v
	 *            the PVector
	 */
	public final PVector inverseRotate(PVector v) {
		PSQuaternion tempQuat = new PSQuaternion(x, y, z, w);
		tempQuat.invert();
		return tempQuat.rotate(v);
	}

	/**
	 * Sets the PSQuaternion as a rotation of {@link #axis() axis} and {@link #angle() angle}
	 * (in radians).
	 * <p> 
	 * The {@code axis} does not need to be normalized. A null {@code axis}
	 * will result in an identity PSQuaternion.
	 * 
	 * @param axis
	 *            the PVector representing the axis
	 * @param angle
	 *            the angle in radians
	 */
	public void fromAxisAngle(PVector axis, float angle) {
		float norm = axis.mag();
		if (norm < EPS8) {
			// Null rotation
			this.x = 0.0f;
			this.y = 0.0f;
			this.z = 0.0f;
			this.w = 1.0f;
		} else {
			float sin_half_angle = PApplet.sin(angle / 2.0f);
			this.x = sin_half_angle * axis.x / norm;
			this.y = sin_half_angle * axis.y / norm;
			this.z = sin_half_angle * axis.z / norm;
			this.w = PApplet.cos(angle / 2.0f);
		}
	}
	
	/**
	 * Sets the PSQuaternion as a rotation from the {@code from} direction to
	 * the {@code to} direction.
	 * <p>
	 * <b>Attention:</b> this rotation is not uniquely defined. The selected axis
	 * is usually orthogonal to {@code from} and {@code to}, minimizing the rotation angle.
	 * This method is robust and can handle small or almost identical vectors.
	 * 
	 * @see #fromAxisAngle(PVector, float)
	 */
	public void fromTo(PVector from, PVector to) {
		float fromSqNorm = PSUtility.squaredNorm(from);
		float toSqNorm = PSUtility.squaredNorm(to);
		// Identity PSQuaternion when one vector is null
		if ((fromSqNorm < EPS10) || (toSqNorm < EPS10)) {
			this.x = this.y = this.z = 0.0f;
			this.w = 1.0f;
		} else {

			PVector axis = from.cross(to);

			float axisSqNorm = PSUtility.squaredNorm(axis);

			// Aligned vectors, pick any axis, not aligned with from or to
		    if (axisSqNorm < EPS10)
		    	axis = PSUtility.orthogonalVector(from);

			float angle = PApplet.asin(PApplet.sqrt(axisSqNorm
					/ (fromSqNorm * toSqNorm)));

			if (from.dot(to) < 0.0)
				angle = PI - angle;

			fromAxisAngle(axis, angle);
		}
	}

	/**
	 * Set the PSQuaternion from a (supposedly correct) 3x3 rotation matrix. 
	 * <p> 
	 * The matrix is expressed in European format: its three columns are the
	 * images by the rotation of the three vectors of an orthogonal basis.
	 * <p>
	 * {@link #fromRotatedBasis(PVector, PVector, PVector)} sets a PSQuaternion
	 * from the three axis of a rotated frame. It actually fills the three
	 * columns of a matrix with these rotated basis vectors and calls this method.
	 * 
	 * @param m
	 *            the 3*3 matrix of float values
	 */
	public final void fromRotationMatrix(float m[][]) {
		// Compute one plus the trace of the matrix
		float onePlusTrace = 1.0f + m[0][0] + m[1][1] + m[2][2];

		if (onePlusTrace > EPS5) {
			// Direct computation
			float s = PApplet.sqrt(onePlusTrace) * 2.0f;
			this.x = (m[2][1] - m[1][2]) / s;
			this.y = (m[0][2] - m[2][0]) / s;
			this.z = (m[1][0] - m[0][1]) / s;
			this.w = 0.25f * s;
		} else {
			// Computation depends on major diagonal term
			if ((m[0][0] > m[1][1]) & (m[0][0] > m[2][2])) {
				float s = PApplet.sqrt(1.0f + m[0][0] - m[1][1] - m[2][2]) * 2.0f;
				this.x = 0.25f * s;
				this.y = (m[0][1] + m[1][0]) / s;
				this.z = (m[0][2] + m[2][0]) / s;
				this.w = (m[1][2] - m[2][1]) / s;
			} else if (m[1][1] > m[2][2]) {
				float s = PApplet.sqrt(1.0f + m[1][1] - m[0][0] - m[2][2]) * 2.0f;
				this.x = (m[0][1] + m[1][0]) / s;
				this.y = 0.25f * s;
				this.z = (m[1][2] + m[2][1]) / s;
				this.w = (m[0][2] - m[2][0]) / s;
			} else {
				float s = PApplet.sqrt(1.0f + m[2][2] - m[0][0] - m[1][1]) * 2.0f;
				this.x = (m[0][2] + m[2][0]) / s;
				this.y = (m[1][2] + m[2][1]) / s;
				this.z = 0.25f * s;
				this.w = (m[0][1] - m[1][0]) / s;
			}
		}
		normalize();
	}
	
	/**
	 * Set the PSQuaternion from a (supposedly correct) 3x3 rotation matrix given in the
	 * upper left 3x3 sub-matrix of the PMatrix3D.
	 * 
	 * @see #fromRotationMatrix(float[][])
	 */
	public final void fromMatrix(PMatrix3D pM) {
		fromRotationMatrix(PSUtility.get3x3UpperLeftMatrixFromPMatrix3D(pM));
	}

	/**
	 * Sets the PSQuaternion from the three rotated vectors of an orthogonal basis. 
	 * <p> 
	 * The three vectors do not have to be normalized but must be orthogonal and
	 * direct (i,e., {@code X^Y=k*Z, with k>0}).
	 * 
	 * @param X
	 *            the first PVector
	 * @param Y
	 *            the second PVector
	 * @param Z
	 *            the third PVector
	 *            
	 * @see #fromRotationMatrix(float[][])
	 * @see #PSQuaternion(PVector, PVector)
	 * 
	 */
	public final void fromRotatedBasis(PVector X, PVector Y, PVector Z) {
		float m[][] = new float[3][3];
		float normX = X.mag();
		float normY = Y.mag();
		float normZ = Z.mag();

		for (int i = 0; i < 3; ++i) {
			m[i][0] = (X.array())[i] / normX;
			m[i][1] = (Y.array())[i] / normY;
			m[i][2] = (Z.array())[i] / normZ;
		}

		fromRotationMatrix(m);
	}

	/**
	 * Returns the normalized axis direction of the rotation represented by the
	 * PSQuaternion. 
	 * <p> 
	 * The result is null for an identity PSQuaternion.
	 * 
	 * @see #angle()
	 */
	public final PVector axis() {
		PVector res = new PVector(this.x, this.y, this.z);
		float sinus = res.mag();
		if (sinus > EPS8)
			res.div(sinus);
		if (PApplet.acos(this.w) <= PIO2)
			return res;
		else {
			res.x = -res.x;
			res.y = -res.y;
			res.z = -res.z;
			return res;
		}
	}

	/**
	 * Returns the {@code angle} (in radians) of the rotation represented by the
	 * PSQuaternion. 
	 * <p> 
	 * This value is always in the range {@code [0-pi]}. Larger rotational angles are
	 * obtained by inverting the {@link #axis()} direction.
	 * 
	 * @see #axis()
	 */
	public final float angle() {
		float angle = 2.0f * PApplet.acos(this.w);
		return (angle <= PI) ? angle : 2.0f * PI - angle;
	}

	/**
	 * Returns the 3x3 rotation matrix associated with the PSQuaternion. 
	 * <p> 
	 * <b>Attention:</b> The method returns the European mathematical representation of
	 * the rotation matrix.
	 * 
	 * @see #inverseRotationMatrix()
	 * 
	 */
	public final float[][] rotationMatrix() {
		return PSUtility.get3x3UpperLeftMatrixFromPMatrix3D(matrix());
	}
	
	/**
	 * Returns the PMatrix3D (processing matrix) which represents the rotation
	 * matrix associated with the PSQuaternion. 
	 *  
	 * @see #rotationMatrix()
	 */
	public final PMatrix3D matrix() {

		float q00 = 2.0f * this.x * this.x;
		float q11 = 2.0f * this.y * this.y;
		float q22 = 2.0f * this.z * this.z;

		float q01 = 2.0f * this.x * this.y;
		float q02 = 2.0f * this.x * this.z;
		float q03 = 2.0f * this.x * this.w;

		float q12 = 2.0f * this.y * this.z;
		float q13 = 2.0f * this.y * this.w;

		float q23 = 2.0f * this.z * this.w;

		float m00 = 1.0f - q11 - q22;
		float m01 = q01 - q23;
		float m02 = q02 + q13;

		float m10 = q01 + q23;
		float m11 = 1.0f - q22 - q00;
		float m12 = q12 - q03;

		float m20 = q02 - q13;
		float m21 = q12 + q03;
		float m22 = 1.0f - q11 - q00;

		float m30 = 0.0f;
		float m31 = 0.0f;
		float m32 = 0.0f;

		float m03 = 0.0f;
		float m13 = 0.0f;
		float m23 = 0.0f;
		float m33 = 1.0f;

		return new PMatrix3D(m00,m01,m02,m03,m10,m11,m12,m13,m20,m21,m22,m23,m30,m31,m32,m33);
	}
	
	/**
	 * Returns the associated inverse rotation processing PMatrix3D. This is simply
	 * {@link #matrix()} of the {@link #inverse()}. 
	 * <p> 
	 * <b>Attention:</b> The result is only valid until the next call to
	 * {@link #inverseMatrix()}. Use it immediately
	 * (as in {@code applyMatrix(q.inverseMatrix())}).
	 */
	public final PMatrix3D inverseMatrix() {
		PSQuaternion tempQuat = new PSQuaternion(x, y, z, w);
		tempQuat.invert();
		return tempQuat.matrix();
	}

	/**
	 * Returns the 3x3 inverse rotation matrix associated with the PSQuaternion. 
	 * <p> 
	 * <b>Attention:</b> This is the classical mathematical rotation matrix.
	 */
	public final float[][] inverseRotationMatrix() {
		return PSUtility.get3x3UpperLeftMatrixFromPMatrix3D(inverseMatrix());
	}
	
	/**
	 * Returns the logarithm of the PSQuaternion.
	 * 
	 * @see #exp()
	 */
	public final PSQuaternion log() {
		//Warning: this method should not normalize the PSQuaternion
		float len = PApplet.sqrt(this.x * this.x + this.y * this.y + this.z
				* this.z);

		if (len < EPS6)
			return new PSQuaternion(this.x, this.y, this.z, 0.0f, false);
		else {
			float coef = PApplet.acos(this.w) / len;
			return new PSQuaternion(this.x * coef, this.y * coef, this.z * coef, 0.0f, false);
		}
	}

	/**
	 * Returns the exponential of the PSQuaternion.
	 * 
	 * @see #log()
	 */
	public final PSQuaternion exp() {
		float theta = PApplet.sqrt(this.x * this.x + this.y * this.y + this.z
				* this.z);

		if (theta < EPS6)
			return new PSQuaternion(this.x, this.y, this.z, PApplet.cos(theta));
		else {
			float coef = PApplet.sin(theta) / theta;
			return new PSQuaternion(this.x * coef, this.y * coef,
					this.z * coef, PApplet.cos(theta));
		}
	}

	/**
	 * Returns a random unit PSQuaternion. 
	 * <p> 
	 * You can create a randomly directed unit vector using: 
	 * <p> 
	 * {@code PVector randomDir = new PVector(1.0f, 0.0f, 0.0f);} <br> 
	 * {@code randomDir = PSQuaternion.multiply(PSQuaternion.randomQuaternion(), randomDir);} 
	 */
	public final static PSQuaternion randomQuaternion() {
		float seed = (float) Math.random();
		float r1 = PApplet.sqrt(1.0f - seed);
		float r2 = PApplet.sqrt(seed);
		float t1 = 2.0f * PI * (float) Math.random();
		float t2 = 2.0f * PI * (float) Math.random();

		return new PSQuaternion(PApplet.sin(t1) * r1, PApplet.cos(t1) * r1,
				PApplet.sin(t2) * r2, PApplet.cos(t2) * r2);
	}

	/**
	 * Wrapper function that simply calls {@code slerp(a, b, t, true)}. 
	 * <p> 
	 * See {@link #slerp(PSQuaternion, PSQuaternion, float, boolean)} for details.
	 */
	public static final PSQuaternion slerp(PSQuaternion a, PSQuaternion b,
			float t) {
		return PSQuaternion.slerp(a, b, t, true);
	}

	/**
	 * Returns the slerp interpolation of quaternions {@code a} and {@code b},
	 * at time {@code t}. 
	 * <p> 
	 * {@code t} should range in {@code [0,1]}. Result is a when {@code t=0 } and 
	 * {@code b} when {@code t=1}. 
	 * <p> 
	 * When {@code allowFlip} is true (default) the slerp interpolation will always use
	 * the "shortest path" between the quaternions' orientations, by "flipping"
	 * the source PSQuaternion if needed (see {@link #negate()}).
	 * 
	 * @param a
	 *            the first PSQuaternion
	 * @param b
	 *            the second PSQuaternion
	 * @param t
	 *            the t interpolation parameter
	 * @param allowFlip
	 *            tells whether or not the interpolation allows axis flip
	 */
	public static final PSQuaternion slerp(PSQuaternion a, PSQuaternion b,
			float t, boolean allowFlip) {
		//Warning: this method should not normalize the PSQuaternion
		float cosAngle = PSQuaternion.dotProduct(a, b);

		float c1, c2;
		// Linear interpolation for close orientations
		if ((1.0 - PApplet.abs(cosAngle)) < 0.01) {
			c1 = 1.0f - t;
			c2 = t;
		} else {
			// Spherical interpolation
			float angle = PApplet.acos(PApplet.abs(cosAngle));
			float sinAngle = PApplet.sin(angle);
			c1 = PApplet.sin(angle * (1.0f - t)) / sinAngle;
			c2 = PApplet.sin(angle * t) / sinAngle;
		}

		// Use the shortest path
		if (allowFlip && (cosAngle < 0.0))
			c1 = -c1;

		return new PSQuaternion(c1 * a.x + c2 * b.x, c1 * a.y + c2 * b.y, c1
				* a.z + c2 * b.z, c1 * a.w + c2 * b.w, false);
	}

	/**
	 * Returns the slerp interpolation of the two quaternions {@code a} and {@code b}, 
	 * at time {@code t}, using tangents {@code tgA} and {@code tgB}. 
	 * <p> 
	 * The resulting PSQuaternion is "between" {@code a} and {@code b} (result is {@code a}
	 * when {@code t=0} and {@code b} for {@code t=1}). 
	 * <p> 
	 * Use {@link #squadTangent(PSQuaternion, PSQuaternion, PSQuaternion)} to
	 * define the PSQuaternion tangents {@code tgA} and {@code tgB}.
	 * 
	 * @param a
	 *            the first PSQuaternion
	 * @param tgA
	 *            the first tangent PSQuaternion
	 * @param tgB
	 *            the second tangent PSQuaternion
	 * @param b
	 *            the second PSQuaternion
	 * @param t
	 *            the t interpolation parameter
	 */
	public static final PSQuaternion squad(PSQuaternion a, PSQuaternion tgA,
			PSQuaternion tgB, PSQuaternion b, float t) {
		PSQuaternion ab = PSQuaternion.slerp(a, b, t);
		PSQuaternion tg = PSQuaternion.slerp(tgA, tgB, t, false);
		return PSQuaternion.slerp(ab, tg, 2.0f * t * (1.0f - t), false);
	}
	

	/**
	 * Simply returns {@code log(a. inverse() * b)}. 
	 * <p> 
	 * Useful for {@link #squadTangent(PSQuaternion, PSQuaternion, PSQuaternion)}.
	 * 
	 * @param a
	 *            the first PSQuaternion
	 * @param b
	 *            the second PSQuaternion
	 */
	public static final PSQuaternion lnDif(PSQuaternion a, PSQuaternion b) {
	  PSQuaternion dif = a.inverse();
	  dif.multiply(b);
	  
	  dif.normalize();
	  return dif.log();
	}

	/**
	 * Returns a tangent PSQuaternion for {@code center}, defined by {@code before} 
	 * and {@code after} quaternions.
	 * 
	 * @param before
	 *            the first PSQuaternion
	 * @param center
	 *            the second PSQuaternion
	 * @param after
	 *            the third PSQuaternion
	 */
	public static final PSQuaternion squadTangent(PSQuaternion before, PSQuaternion center, PSQuaternion after) {
	  PSQuaternion l1 = PSQuaternion.lnDif(center,before);
	  PSQuaternion l2 = PSQuaternion.lnDif(center,after);
	  PSQuaternion e = new PSQuaternion();
	  
	  e.x = -0.25f * (l1.x + l2.x);
	  e.y = -0.25f * (l1.y + l2.y);
	  e.z = -0.25f * (l1.z + l2.z);
	  e.w = -0.25f * (l1.w + l2.w);
	  
	  return PSQuaternion.multiply(center, e.exp());
	}
	
	/**
	 * Utility function that returns the squared norm of the PSQuaternion.
	 */
	public static float squaredNorm(PSQuaternion q) {
		return (q.x * q.x) + (q.y * q.y) + (q.z * q.z) + (q.w * q.w);
	}
}
