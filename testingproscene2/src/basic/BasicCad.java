package basic;

import processing.core.*;
import remixlab.proscene.*;

public class BasicCad extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.camera().setCadRotate(true);
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
