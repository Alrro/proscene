package basic;

import processing.core.*;
import remixlab.dandelion.core.Constants.KeyboardAction;
import remixlab.proscene.*;

public class Basic extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.defaultKeyboardAgent().profile().setShortcut('v', KeyboardAction.CAMERA_KIND);	  
	  //scene.setSingleThreadedTimers();
	  scene.showAll();
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
		
	/**
	public void keyPressed() {
		if( key == 'x' || key == 'X' ) {
			scene.camera().setUpVector(new Vec(1,0,0));
			println("x -> dir");
		}
		if( key == 'y' || key == 'Y' ) {
			scene.camera().setUpVector(new Vec(0,1,0));
			println("y -> dir");
		}
		if( key == 'z' || key == 'Z' ) {
			scene.camera().setUpVector(new Vec(0,0,1));
			println("z -> dir");
		}
		if(key == 'u' || key == 'U') {
			if(scene.isRightHanded()) {
				scene.setLeftHanded();
				println("Left handed set");
			}
			else {
				scene.setRightHanded();
				println("Right handed set");
			}
		}
	}
	*/
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
