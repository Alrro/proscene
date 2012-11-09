package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene scene;
	float d = 150;
	CameraConstraint constraint;

	public void setup() {
	  size(640, 360, P3D);	  
	  scene = new Scene(this);	  
	  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
	  scene.camera().setUpVector(new PVector(0,0,-1));
	  scene.camera().setPosition(new PVector(d,d,d));
	  scene.camera().lookAt(scene.center());	  
	  scene.showAll();
	  constraint = new CameraConstraint(scene.camera());
	  constraint.setTranslationConstraintType(AxisPlaneConstraint.Type.PLANE);
	  constraint.setTranslationConstraintDirection(new PVector(0,1,0));
	  constraint.setRotationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
	  scene.camera().frame().setConstraint(constraint);
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);	  
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
