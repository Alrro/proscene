package gnutopic;

import processing.core.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.proscene.*;

public class CircledInteractiveFrame extends InteractiveFrame {
	float radius;
	
	public CircledInteractiveFrame(Scene scn, float r) {
		super(scn);
		radius = r;
	}
	
	public void checkIfGrabsDevice(int x, int y, Camera camera) {
		Vector3D pos = position();
		Vector3D res = Vector3D.add(pos, Vector3D.mult(camera.upVector(), radius));
		Vector3D posProj = camera.projectedCoordinatesOf(pos);
		Vector3D resProj = camera.projectedCoordinatesOf(res);
		float d1 = PApplet.sqrt( PApplet.sq(posProj.x() - x ) + PApplet.sq(posProj.y() - y ) );
		float d2 = PApplet.sqrt( PApplet.sq(posProj.x() - resProj.x() ) + PApplet.sq(posProj.y() - resProj.y() ) );
		setGrabsDevice(keepsGrabbingDevice || ( d1 <= d2 ) );
	}
}
