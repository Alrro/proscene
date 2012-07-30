package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.proscene.AxisPlaneConstraint.Type;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  //Scene instantiation
	  scene = new Scene(this);  
	  
	  /**
	  WorldConstraint constraint2d = new WorldConstraint();
	  PVector direction = new PVector(0,0,1);
	  constraint2d.setRotationConstraint(Type.AXIS, direction);
	  //constraint2d.setTranslationConstraint(Type.PLANE, direction);
	  scene.camera().frame().setConstraint(constraint2d);
	  // */	
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.BasicUse" });
	}
}
