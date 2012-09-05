package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.Vector3D;

public class Basic extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
	  ///**
	  scene.camera().setPosition(new Vector3D(0,0,-100));
	  scene.camera().lookAt(scene.camera().sceneCenter());
	  scene.camera().setUpVector(new Vector3D(0,-1,0));
	  // */
	  scene.showAll();
	  hint(DISABLE_STROKE_PERSPECTIVE);
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
		if(key == 'u' || key == 'U') {			
			println("axis: " + scene.pinhole().frame().orientation().axis()					
		          + " angle: " + scene.pinhole().frame().orientation().angle() );
		}
	}	
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
