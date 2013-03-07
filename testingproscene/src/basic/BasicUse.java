package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
	  // Ortho projection is buggy. Uncomment the following line to see it.
	  // (you can also press 'e' to switch the projection at run time). 
	  //scene.setCameraType(Camera.Type.ORTHOGRAPHIC);	  
	  //scene.registerCameraProfile( new CameraProfile(scene, "CAD_ARCBALL", CameraProfile.Mode.CAD) );	 
	  //frame.setResizable(true);
	  
	  int s;
	  s = 1 << 0;
	  println("s: " + s);
	  s = 1 << 1;
	  println("s: " + s);
	  s = 1 << 2;
	  println("s: " + s);
	  s = 1 << 3;
	  println("s: " + s);
	  s = 1 << 4;
	  println("s: " + s);
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
