package basic;

import processing.core.*;
import remixlab.proscene.*;

public class Basic extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public void keyPressed() {
		if(key == 'x' || key == 'X')
			this.noCursor();
		if(key == 'y' || key == 'Y')
			this.cursor();		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
