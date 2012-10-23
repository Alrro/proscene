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
