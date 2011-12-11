package remixlab.proscene;

import remixlab.remixcam.geom.Vector3D;

public class Vec3D extends Vector3D {
	/**
   * Constructor for an empty vector: x, y, and z are set to 0.
   */
  public Vec3D() {
  	super();
  }
  
  public Vec3D(float x, float y, float z) {
  	super(x,y,z);
  }
  
  public Vec3D(float x, float y) {
  	super(x,y);
  }
	
	// TODO New testing
	/**
	 * public Vec3D(Object any) { try { x =
	 * any.getClass().getDeclaredField("x").getFloat(any); y =
	 * any.getClass().getDeclaredField("y").getFloat(any); z =
	 * any.getClass().getDeclaredField("z").getFloat(any); } catch ( Exception e )
	 * { throw(new
	 * RuntimeException("vec cannot handle class in constructor: "+any.
	 * getClass(),e)); } }
	 */

	public Vec3D(Object any) {
		try {
			float[] result = new float[3];
			any.getClass().getMethod("get", new Class[] { float[].class })
					.invoke(any, result);
			x(result[0]);
			y(result[1]);
			z(result[2]);

		} catch (Exception e) {
			throw (new RuntimeException("vec cannot handle class in constructor: "
					+ any.getClass(), e));
		}
	}
}
