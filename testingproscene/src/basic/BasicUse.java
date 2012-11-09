package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {	
	Scene scene;
	String warning;
	PFont myFont;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
	  // Ortho projection is buggy. Uncomment the following line to see it.
	  // (you can also press 'e' to switch the projection at run time). 
	  //scene.setCameraType(Camera.Type.ORTHOGRAPHIC);	  
	  warning = "press 'e' to switch the projection and see that ortho is buggy";
	  myFont = createFont("FFScala", 14);
	  textFont(myFont);
	  scene.registerCameraProfile( new CameraProfile(scene, "CAD_ARCBALL", CameraProfile.Mode.CAD) );
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	  scene.beginScreenDrawing();
	  fill(255,0,255);
	  text(warning, 20, 20);
	  if(scene.cameraType() == Camera.Type.PERSPECTIVE)
		  text("PERSP projection currently", 20, 35);
	  else
		  text("ORTHO projection currently", 20, 35);
	  scene.endScreenDrawing();	  
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.BasicUse" });
	}
}
