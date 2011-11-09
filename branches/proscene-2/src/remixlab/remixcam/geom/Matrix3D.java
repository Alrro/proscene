package remixlab.remixcam.geom;

/**
 * 4x4 matrix implementation.
 */
public final class Matrix3D implements Matrix /*, PConstants*/ {

  public float m00, m01, m02, m03;
  public float m10, m11, m12, m13;
  public float m20, m21, m22, m23;
  public float m30, m31, m32, m33;


  // locally allocated version to avoid creating new memory
  protected Matrix3D inverseCopy;


  public Matrix3D() {
    reset();
  }


  public Matrix3D(float m00, float m01, float m02,
                   float m10, float m11, float m12) {
    set(m00, m01, m02, 0,
        m10, m11, m12, 0,
        0,   0,   1,   0,
        0,   0,   0,   1);
  }


  public Matrix3D(float m00, float m01, float m02, float m03,
                   float m10, float m11, float m12, float m13,
                   float m20, float m21, float m22, float m23,
                   float m30, float m31, float m32, float m33) {
    set(m00, m01, m02, m03,
        m10, m11, m12, m13,
        m20, m21, m22, m23,
        m30, m31, m32, m33);
  }


  public Matrix3D(Matrix matrix) {
    set(matrix);
  }
  
  public Matrix3D(Object any) {
  	try {
  		float [] result = new float [16];  		
      any.getClass().getMethod("get", new Class[] { float [].class }).invoke(any, result);
      
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
      
  		} catch ( Exception e ) {
  			throw(new RuntimeException("vec cannot handle class in constructor: "+any.getClass(),e));
  		}
  }
  
  public final float[][] get3x3UpperLeftMatrixFromMatrix3D() {
  	return get3x3UpperLeftMatrixFromMatrix3D(this);
  }
  
  /**
	 * Utility function that returns the 3x3 upper left sub-matrix of the given
	 * PMatrix3D.
	 */
	public static final float[][] get3x3UpperLeftMatrixFromMatrix3D(Matrix3D pM) {
		float[][] m = new float[3][3];
		m[0][0] = pM.m00;
		m[0][1] = pM.m01;
		m[0][2] = pM.m02;
		m[1][0] = pM.m10;
		m[1][1] = pM.m11;
		m[1][2] = pM.m12;
		m[2][0] = pM.m20;
		m[2][1] = pM.m21;
		m[2][2] = pM.m22;
		return m;
	}

  public void reset() {
    set(1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1);
  }


  /**
   * Returns a copy of this Matrix.
   */
  public Matrix3D get() {
    Matrix3D outgoing = new Matrix3D();
    outgoing.set(this);
    return outgoing;
  }


  /**
   * Copies the matrix contents into a 16 entry float array.
   * If target is null (or not the correct size), a new array will be created.
   */
  public float[] get(float[] target) {
    if ((target == null) || (target.length != 16)) {
      target = new float[16];
    }
    target[0] = m00;
    target[1] = m01;
    target[2] = m02;
    target[3] = m03;

    target[4] = m10;
    target[5] = m11;
    target[6] = m12;
    target[7] = m13;

    target[8] = m20;
    target[9] = m21;
    target[10] = m22;
    target[11] = m23;

    target[12] = m30;
    target[13] = m31;
    target[14] = m32;
    target[15] = m33;

    return target;
  }


  public void set(Matrix matrix) {
    if (matrix instanceof Matrix3D) {
      Matrix3D src = (Matrix3D) matrix;
      set(src.m00, src.m01, src.m02, src.m03,
          src.m10, src.m11, src.m12, src.m13,
          src.m20, src.m21, src.m22, src.m23,
          src.m30, src.m31, src.m32, src.m33);
    } else {
      Matrix2D src = (Matrix2D) matrix;
      set(src.m00, src.m01, 0, src.m02,
          src.m10, src.m11, 0, src.m12,
          0, 0, 1, 0,
          0, 0, 0, 1);
    }
  }


  public void set(float[] source) {
    if (source.length == 6) {
      set(source[0], source[1], source[2],
          source[3], source[4], source[5]);

    } else if (source.length == 16) {
      m00 = source[0];
      m01 = source[1];
      m02 = source[2];
      m03 = source[3];

      m10 = source[4];
      m11 = source[5];
      m12 = source[6];
      m13 = source[7];

      m20 = source[8];
      m21 = source[9];
      m22 = source[10];
      m23 = source[11];

      m30 = source[12];
      m31 = source[13];
      m32 = source[14];
      m33 = source[15];
    }
  }


  public void set(float m00, float m01, float m02,
                  float m10, float m11, float m12) {
    set(m00, m01, 0, m02,
        m10, m11, 0, m12,
        0, 0, 1, 0,
        0, 0, 0, 1);
  }


  public void set(float m00, float m01, float m02, float m03,
                  float m10, float m11, float m12, float m13,
                  float m20, float m21, float m22, float m23,
                  float m30, float m31, float m32, float m33) {
    this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
    this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
    this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
    this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
  }


  public void translate(float tx, float ty) {
    translate(tx, ty, 0);
  }

//  public void invTranslate(float tx, float ty) {
//    invTranslate(tx, ty, 0);
//  }


  public void translate(float tx, float ty, float tz) {
    m03 += tx*m00 + ty*m01 + tz*m02;
    m13 += tx*m10 + ty*m11 + tz*m12;
    m23 += tx*m20 + ty*m21 + tz*m22;
    m33 += tx*m30 + ty*m31 + tz*m32;
  }


  public void rotate(float angle) {
    rotateZ(angle);
  }


  public void rotateX(float angle) {
    float c = cos(angle);
    float s = sin(angle);
    apply(1, 0, 0, 0,  0, c, -s, 0,  0, s, c, 0,  0, 0, 0, 1);
  }


  public void rotateY(float angle) {
    float c = cos(angle);
    float s = sin(angle);
    apply(c, 0, s, 0,  0, 1, 0, 0,  -s, 0, c, 0,  0, 0, 0, 1);
  }


  public void rotateZ(float angle) {
    float c = cos(angle);
    float s = sin(angle);
    apply(c, -s, 0, 0,  s, c, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
  }


  public void rotate(float angle, float v0, float v1, float v2) {
    // TODO should make sure this vector is normalized

    float c = cos(angle);
    float s = sin(angle);
    float t = 1.0f - c;

    apply((t*v0*v0) + c, (t*v0*v1) - (s*v2), (t*v0*v2) + (s*v1), 0,
          (t*v0*v1) + (s*v2), (t*v1*v1) + c, (t*v1*v2) - (s*v0), 0,
          (t*v0*v2) - (s*v1), (t*v1*v2) + (s*v0), (t*v2*v2) + c, 0,
          0, 0, 0, 1);
  }


  public void scale(float s) {
    //apply(s, 0, 0, 0,  0, s, 0, 0,  0, 0, s, 0,  0, 0, 0, 1);
    scale(s, s, s);
  }


  public void scale(float sx, float sy) {
    //apply(sx, 0, 0, 0,  0, sy, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
    scale(sx, sy, 1);
  }


  public void scale(float x, float y, float z) {
    //apply(x, 0, 0, 0,  0, y, 0, 0,  0, 0, z, 0,  0, 0, 0, 1);
    m00 *= x;  m01 *= y;  m02 *= z;
    m10 *= x;  m11 *= y;  m12 *= z;
    m20 *= x;  m21 *= y;  m22 *= z;
    m30 *= x;  m31 *= y;  m32 *= z;
  }


  public void shearX(float angle) {
    float t = (float) Math.tan(angle);
    apply(1, t, 0, 0,
          0, 1, 0, 0,
          0, 0, 1, 0,
          0, 0, 0, 1);
  }


  public void shearY(float angle) {
    float t = (float) Math.tan(angle);
    apply(1, 0, 0, 0,
          t, 1, 0, 0,
          0, 0, 1, 0,
          0, 0, 0, 1);
  }


  public void apply(Matrix source) {
    if (source instanceof Matrix2D) {
      apply((Matrix2D) source);
    } else if (source instanceof Matrix3D) {
      apply((Matrix3D) source);
    }
  }


  public void apply(Matrix2D source) {
    apply(source.m00, source.m01, 0, source.m02,
          source.m10, source.m11, 0, source.m12,
          0, 0, 1, 0,
          0, 0, 0, 1);
  }


  public void apply(Matrix3D source) {
    apply(source.m00, source.m01, source.m02, source.m03,
          source.m10, source.m11, source.m12, source.m13,
          source.m20, source.m21, source.m22, source.m23,
          source.m30, source.m31, source.m32, source.m33);
  }


  public void apply(float n00, float n01, float n02,
                    float n10, float n11, float n12) {
    apply(n00, n01, 0, n02,
          n10, n11, 0, n12,
          0, 0, 1, 0,
          0, 0, 0, 1);
  }
  
  public static void mult(Matrix3D a, Matrix3D b, Matrix3D c) { 
  	c.m00 = a.m00*b.m00 + a.m01*b.m10 + a.m02*b.m20 + a.m03*b.m30;
    c.m01 = a.m00*b.m01 + a.m01*b.m11 + a.m02*b.m21 + a.m03*b.m31;
    c.m02 = a.m00*b.m02 + a.m01*b.m12 + a.m02*b.m22 + a.m03*b.m32;
    c.m03 = a.m00*b.m03 + a.m01*b.m13 + a.m02*b.m23 + a.m03*b.m33;

    c.m10 = a.m10*b.m00 + a.m11*b.m10 + a.m12*b.m20 + a.m13*b.m30;
    c.m11 = a.m10*b.m01 + a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31;
    c.m12 = a.m10*b.m02 + a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32;
    c.m13 = a.m10*b.m03 + a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33;

    c.m20 = a.m20*b.m00 + a.m21*b.m10 + a.m22*b.m20 + a.m23*b.m30;
    c.m21 = a.m20*b.m01 + a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31;
    c.m22 = a.m20*b.m02 + a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32;
    c.m23 = a.m20*b.m03 + a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33;

    c.m30 = a.m30*b.m00 + a.m31*b.m10 + a.m32*b.m20 + a.m33*b.m30;
    c.m31 = a.m30*b.m01 + a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31;
    c.m32 = a.m30*b.m02 + a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32;
    c.m33 = a.m30*b.m03 + a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33;
  }


  public void apply(float n00, float n01, float n02, float n03,
                    float n10, float n11, float n12, float n13,
                    float n20, float n21, float n22, float n23,
                    float n30, float n31, float n32, float n33) {

    float r00 = m00*n00 + m01*n10 + m02*n20 + m03*n30;
    float r01 = m00*n01 + m01*n11 + m02*n21 + m03*n31;
    float r02 = m00*n02 + m01*n12 + m02*n22 + m03*n32;
    float r03 = m00*n03 + m01*n13 + m02*n23 + m03*n33;

    float r10 = m10*n00 + m11*n10 + m12*n20 + m13*n30;
    float r11 = m10*n01 + m11*n11 + m12*n21 + m13*n31;
    float r12 = m10*n02 + m11*n12 + m12*n22 + m13*n32;
    float r13 = m10*n03 + m11*n13 + m12*n23 + m13*n33;

    float r20 = m20*n00 + m21*n10 + m22*n20 + m23*n30;
    float r21 = m20*n01 + m21*n11 + m22*n21 + m23*n31;
    float r22 = m20*n02 + m21*n12 + m22*n22 + m23*n32;
    float r23 = m20*n03 + m21*n13 + m22*n23 + m23*n33;

    float r30 = m30*n00 + m31*n10 + m32*n20 + m33*n30;
    float r31 = m30*n01 + m31*n11 + m32*n21 + m33*n31;
    float r32 = m30*n02 + m31*n12 + m32*n22 + m33*n32;
    float r33 = m30*n03 + m31*n13 + m32*n23 + m33*n33;

    m00 = r00; m01 = r01; m02 = r02; m03 = r03;
    m10 = r10; m11 = r11; m12 = r12; m13 = r13;
    m20 = r20; m21 = r21; m22 = r22; m23 = r23;
    m30 = r30; m31 = r31; m32 = r32; m33 = r33;
  }


  public void preApply(Matrix2D left) {
    preApply(left.m00, left.m01, 0, left.m02,
             left.m10, left.m11, 0, left.m12,
             0, 0, 1, 0,
             0, 0, 0, 1);
  }


  /**
   * Apply another matrix to the left of this one.
   */
  public void preApply(Matrix3D left) {
    preApply(left.m00, left.m01, left.m02, left.m03,
             left.m10, left.m11, left.m12, left.m13,
             left.m20, left.m21, left.m22, left.m23,
             left.m30, left.m31, left.m32, left.m33);
  }


  public void preApply(float n00, float n01, float n02,
                       float n10, float n11, float n12) {
    preApply(n00, n01, 0, n02,
             n10, n11, 0, n12,
             0, 0, 1, 0,
             0, 0, 0, 1);
  }


  public void preApply(float n00, float n01, float n02, float n03,
                       float n10, float n11, float n12, float n13,
                       float n20, float n21, float n22, float n23,
                       float n30, float n31, float n32, float n33) {

    float r00 = n00*m00 + n01*m10 + n02*m20 + n03*m30;
    float r01 = n00*m01 + n01*m11 + n02*m21 + n03*m31;
    float r02 = n00*m02 + n01*m12 + n02*m22 + n03*m32;
    float r03 = n00*m03 + n01*m13 + n02*m23 + n03*m33;

    float r10 = n10*m00 + n11*m10 + n12*m20 + n13*m30;
    float r11 = n10*m01 + n11*m11 + n12*m21 + n13*m31;
    float r12 = n10*m02 + n11*m12 + n12*m22 + n13*m32;
    float r13 = n10*m03 + n11*m13 + n12*m23 + n13*m33;

    float r20 = n20*m00 + n21*m10 + n22*m20 + n23*m30;
    float r21 = n20*m01 + n21*m11 + n22*m21 + n23*m31;
    float r22 = n20*m02 + n21*m12 + n22*m22 + n23*m32;
    float r23 = n20*m03 + n21*m13 + n22*m23 + n23*m33;

    float r30 = n30*m00 + n31*m10 + n32*m20 + n33*m30;
    float r31 = n30*m01 + n31*m11 + n32*m21 + n33*m31;
    float r32 = n30*m02 + n31*m12 + n32*m22 + n33*m32;
    float r33 = n30*m03 + n31*m13 + n32*m23 + n33*m33;

    m00 = r00; m01 = r01; m02 = r02; m03 = r03;
    m10 = r10; m11 = r11; m12 = r12; m13 = r13;
    m20 = r20; m21 = r21; m22 = r22; m23 = r23;
    m30 = r30; m31 = r31; m32 = r32; m33 = r33;
  }


  //////////////////////////////////////////////////////////////


  public Vector3D mult(Vector3D source, Vector3D target) {
    if (target == null) {
      target = new Vector3D();
    }
    target.x = m00*source.x + m01*source.y + m02*source.z + m03;
    target.y = m10*source.x + m11*source.y + m12*source.z + m13;
    target.z = m20*source.x + m21*source.y + m22*source.z + m23;
//    float tw = m30*source.x + m31*source.y + m32*source.z + m33;
//    if (tw != 0 && tw != 1) {
//      target.div(tw);
//    }
    return target;
  }


  /*
  public Vector3D cmult(Vector3D source, Vector3D target) {
    if (target == null) {
      target = new Vector3D();
    }
    target.x = m00*source.x + m10*source.y + m20*source.z + m30;
    target.y = m01*source.x + m11*source.y + m21*source.z + m31;
    target.z = m02*source.x + m12*source.y + m22*source.z + m32;
    float tw = m03*source.x + m13*source.y + m23*source.z + m33;
    if (tw != 0 && tw != 1) {
      target.div(tw);
    }
    return target;
  }
  */


  /**
   * Multiply a three or four element vector against this matrix. If out is
   * null or not length 3 or 4, a new float array (length 3) will be returned.
   */
  public float[] mult(float[] source, float[] target) {
    if (target == null || target.length < 3) {
      target = new float[3];
    }
    if (source == target) {
      throw new RuntimeException("The source and target vectors used in " +
                                 "Matrix3D.mult() cannot be identical.");
    }
    if (target.length == 3) {
      target[0] = m00*source[0] + m01*source[1] + m02*source[2] + m03;
      target[1] = m10*source[0] + m11*source[1] + m12*source[2] + m13;
      target[2] = m20*source[0] + m21*source[1] + m22*source[2] + m23;
      //float w = m30*source[0] + m31*source[1] + m32*source[2] + m33;
      //if (w != 0 && w != 1) {
      //  target[0] /= w; target[1] /= w; target[2] /= w;
      //}
    } else if (target.length > 3) {
      target[0] = m00*source[0] + m01*source[1] + m02*source[2] + m03*source[3];
      target[1] = m10*source[0] + m11*source[1] + m12*source[2] + m13*source[3];
      target[2] = m20*source[0] + m21*source[1] + m22*source[2] + m23*source[3];
      target[3] = m30*source[0] + m31*source[1] + m32*source[2] + m33*source[3];
    }
    return target;
  }


  public float multX(float x, float y) {
    return m00*x + m01*y + m03;
  }


  public float multY(float x, float y) {
    return m10*x + m11*y + m13;
  }


  public float multX(float x, float y, float z) {
    return m00*x + m01*y + m02*z + m03;
  }


  public float multY(float x, float y, float z) {
    return m10*x + m11*y + m12*z + m13;
  }


  public float multZ(float x, float y, float z) {
    return m20*x + m21*y + m22*z + m23;
  }


  public float multW(float x, float y, float z) {
    return m30*x + m31*y + m32*z + m33;
  }


  public float multX(float x, float y, float z, float w) {
    return m00*x + m01*y + m02*z + m03*w;
  }


  public float multY(float x, float y, float z, float w) {
    return m10*x + m11*y + m12*z + m13*w;
  }


  public float multZ(float x, float y, float z, float w) {
    return m20*x + m21*y + m22*z + m23*w;
  }


  public float multW(float x, float y, float z, float w) {
    return m30*x + m31*y + m32*z + m33*w;
  }


  /**
   * Transpose this matrix.
   */
  public void transpose() {
    float temp;
    temp = m01; m01 = m10; m10 = temp;
    temp = m02; m02 = m20; m20 = temp;
    temp = m03; m03 = m30; m30 = temp;
    temp = m12; m12 = m21; m21 = temp;
    temp = m13; m13 = m31; m31 = temp;
    temp = m23; m23 = m32; m32 = temp;
  }
  
  
  /**
   * Invert this matrix into {@code m}, i.e., doesn't modify this matrix.
   * <p>
   * {@code m} should be non-null.
   */
  public boolean invert(Matrix3D m) {
  	float determinant = determinant();
    if (determinant == 0) {
      return false;
    }    
    

    // first row
    float t00 =  determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33);
    float t01 = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33);
    float t02 =  determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33);
    float t03 = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32);

    // second row
    float t10 = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33);
    float t11 =  determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33);
    float t12 = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33);
    float t13 =  determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32);

    // third row
    float t20 =  determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33);
    float t21 = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33);
    float t22 =  determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33);
    float t23 = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32);

    // fourth row
    float t30 = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23);
    float t31 =  determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23);
    float t32 = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23);
    float t33 =  determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22);

     // transpose and divide by the determinant
     m.m00 = t00 / determinant;
     m.m01 = t10 / determinant;
     m.m02 = t20 / determinant;
     m.m03 = t30 / determinant;

     m.m10 = t01 / determinant;
     m.m11 = t11 / determinant;
     m.m12 = t21 / determinant;
     m.m13 = t31 / determinant;

     m.m20 = t02 / determinant;
     m.m21 = t12 / determinant;
     m.m22 = t22 / determinant;
     m.m23 = t32 / determinant;

     m.m30 = t03 / determinant;
     m.m31 = t13 / determinant;
     m.m32 = t23 / determinant;
     m.m33 = t33 / determinant;
     
     return true;
  }

  /**
   * Invert this matrix.
   * @return true if successful
   */
  public boolean invert() {
    float determinant = determinant();
    if (determinant == 0) {
      return false;
    }

    // first row
    float t00 =  determinant3x3(m11, m12, m13, m21, m22, m23, m31, m32, m33);
    float t01 = -determinant3x3(m10, m12, m13, m20, m22, m23, m30, m32, m33);
    float t02 =  determinant3x3(m10, m11, m13, m20, m21, m23, m30, m31, m33);
    float t03 = -determinant3x3(m10, m11, m12, m20, m21, m22, m30, m31, m32);

    // second row
    float t10 = -determinant3x3(m01, m02, m03, m21, m22, m23, m31, m32, m33);
    float t11 =  determinant3x3(m00, m02, m03, m20, m22, m23, m30, m32, m33);
    float t12 = -determinant3x3(m00, m01, m03, m20, m21, m23, m30, m31, m33);
    float t13 =  determinant3x3(m00, m01, m02, m20, m21, m22, m30, m31, m32);

    // third row
    float t20 =  determinant3x3(m01, m02, m03, m11, m12, m13, m31, m32, m33);
    float t21 = -determinant3x3(m00, m02, m03, m10, m12, m13, m30, m32, m33);
    float t22 =  determinant3x3(m00, m01, m03, m10, m11, m13, m30, m31, m33);
    float t23 = -determinant3x3(m00, m01, m02, m10, m11, m12, m30, m31, m32);

    // fourth row
    float t30 = -determinant3x3(m01, m02, m03, m11, m12, m13, m21, m22, m23);
    float t31 =  determinant3x3(m00, m02, m03, m10, m12, m13, m20, m22, m23);
    float t32 = -determinant3x3(m00, m01, m03, m10, m11, m13, m20, m21, m23);
    float t33 =  determinant3x3(m00, m01, m02, m10, m11, m12, m20, m21, m22);

    // transpose and divide by the determinant
    m00 = t00 / determinant;
    m01 = t10 / determinant;
    m02 = t20 / determinant;
    m03 = t30 / determinant;

    m10 = t01 / determinant;
    m11 = t11 / determinant;
    m12 = t21 / determinant;
    m13 = t31 / determinant;

    m20 = t02 / determinant;
    m21 = t12 / determinant;
    m22 = t22 / determinant;
    m23 = t32 / determinant;

    m30 = t03 / determinant;
    m31 = t13 / determinant;
    m32 = t23 / determinant;
    m33 = t33 / determinant;

    return true;
  }

  /**
   * Calculate the determinant of a 3x3 matrix.
   * @return result
   */
  private float determinant3x3(float t00, float t01, float t02,
                               float t10, float t11, float t12,
                               float t20, float t21, float t22) {
    return (t00 * (t11 * t22 - t12 * t21) +
            t01 * (t12 * t20 - t10 * t22) +
            t02 * (t10 * t21 - t11 * t20));
  }


  /**
   * @return the determinant of the matrix
   */
  public float determinant() {
    float f =
      m00
      * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32)
         - m13 * m22 * m31
         - m11 * m23 * m32
         - m12 * m21 * m33);
    f -= m01
      * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32)
         - m13 * m22 * m30
         - m10 * m23 * m32
         - m12 * m20 * m33);
    f += m02
      * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31)
         - m13 * m21 * m30
         - m10 * m23 * m31
         - m11 * m20 * m33);
    f -= m03
      * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31)
         - m12 * m21 * m30
         - m10 * m22 * m31
         - m11 * m20 * m32);
    return f;
  }


  //////////////////////////////////////////////////////////////

  // REVERSE VERSIONS OF MATRIX OPERATIONS

  // These functions should not be used, as they will be removed in the future.


  protected void invTranslate(float tx, float ty, float tz) {
    preApply(1, 0, 0, -tx,
             0, 1, 0, -ty,
             0, 0, 1, -tz,
             0, 0, 0, 1);
  }


  protected void invRotateX(float angle) {
    float c = cos(-angle);
    float s = sin(-angle);
    preApply(1, 0, 0, 0,  0, c, -s, 0,  0, s, c, 0,  0, 0, 0, 1);
  }


  protected void invRotateY(float angle) {
    float c = cos(-angle);
    float s = sin(-angle);
    preApply(c, 0, s, 0,  0, 1, 0, 0,  -s, 0, c, 0,  0, 0, 0, 1);
  }


  protected void invRotateZ(float angle) {
    float c = cos(-angle);
    float s = sin(-angle);
    preApply(c, -s, 0, 0,  s, c, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
  }


  protected void invRotate(float angle, float v0, float v1, float v2) {
    //TODO should make sure this vector is normalized

    float c = cos(-angle);
    float s = sin(-angle);
    float t = 1.0f - c;

    preApply((t*v0*v0) + c, (t*v0*v1) - (s*v2), (t*v0*v2) + (s*v1), 0,
             (t*v0*v1) + (s*v2), (t*v1*v1) + c, (t*v1*v2) - (s*v0), 0,
             (t*v0*v2) - (s*v1), (t*v1*v2) + (s*v0), (t*v2*v2) + c, 0,
             0, 0, 0, 1);
  }


  protected void invScale(float x, float y, float z) {
    preApply(1/x, 0, 0, 0,  0, 1/y, 0, 0,  0, 0, 1/z, 0,  0, 0, 0, 1);
  }


  protected boolean invApply(float n00, float n01, float n02, float n03,
                             float n10, float n11, float n12, float n13,
                             float n20, float n21, float n22, float n23,
                             float n30, float n31, float n32, float n33) {
    if (inverseCopy == null) {
      inverseCopy = new Matrix3D();
    }
    inverseCopy.set(n00, n01, n02, n03,
                    n10, n11, n12, n13,
                    n20, n21, n22, n23,
                    n30, n31, n32, n33);
    if (!inverseCopy.invert()) {
      return false;
    }
    preApply(inverseCopy);
    return true;
  }

  //////////////////////////////////////////////////////////////

  private final float sin(float angle) {
    return (float) Math.sin(angle);
  }

  private final float cos(float angle) {
    return (float) Math.cos(angle);
  }
}