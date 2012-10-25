package pmpm;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Anar2Camera extends PApplet {
	Scene scene;
	AxisPlaneConstraint constraint;

	public void setup() {
	  size(640, 360, P3D);
	  //Scene instantiation
	  scene = new Scene(this);
	  scene.camera().setPosition(new PVector(0,200,200));
	  scene.camera().lookAt(scene.center());
	  constraint = new WorldConstraint();
	  constraint.setRotationConstraintType(AxisPlaneConstraint.Type.AXIS);
	  constraint.setRotationConstraintDirection(new PVector(0,0,1));
	  scene.camera().frame().setConstraint(constraint);
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
}
