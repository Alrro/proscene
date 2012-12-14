package basic_geom;
import geom.Box;

import processing.core.*;
import processing.event.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class PointUnderPixel extends PApplet {
	Scene scene;
	Box [] boxes;

	public void setup() {
	  size(640, 360, OPENGL);
	  scene = new Scene(this);
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	  scene.setShortcut('z', Scene.KeyboardAction.ARP_FROM_PIXEL);
	  //add the click actions to all camera profiles
	  CameraProfile [] camProfiles = scene.getCameraProfiles();
	  /**
	  for (int i=0; i<camProfiles.length; i++) {
		    // left click will zoom on pixel:
		    camProfiles[i].setClickBinding( Scene.Button.LEFT, Scene.ClickAction.ZOOM_ON_PIXEL );
		    // middle click will show all the scene:
		    camProfiles[i].setClickBinding( Scene.Button.MIDDLE, Scene.ClickAction.SHOW_ALL);
		    // right click will will set the arcball reference point:
		    camProfiles[i].setClickBinding( Scene.Button.RIGHT, Scene.ClickAction.ARP_FROM_PIXEL );
		    // double click with the middle button while pressing SHIFT will reset the arcball reference point:
		    camProfiles[i].setClickBinding( Scene.Modifier.SHIFT.ID, Scene.Button.MIDDLE, 2, Scene.ClickAction.RESET_ARP );
		  }
		  //*/
	  // /**
	  for (int i=0; i<camProfiles.length; i++) {
	    // left click will zoom on pixel:
	    camProfiles[i].setClickBinding( 0, LEFT, 1, Scene.ClickAction.ZOOM_ON_PIXEL );
	    // middle click will show all the scene:
	    camProfiles[i].setClickBinding( 0, CENTER, 1, Scene.ClickAction.SHOW_ALL);
	    // right click will will set the arcball reference point:
	    camProfiles[i].setClickBinding( 0, RIGHT, 1, Scene.ClickAction.ARP_FROM_PIXEL );
	    // double click with the middle button while pressing SHIFT will reset the arcball reference point:
	    camProfiles[i].setClickBinding( Event.SHIFT, CENTER, 2, Scene.ClickAction.RESET_ARP );
	    //camProfiles[i].setClickBinding( CENTER, 2, Scene.ClickAction.RESET_ARP );
	  }
	  //*/
	  
	  /**
	  GLCamera glCam = new GLCamera(scene);	  
	  // */
	  
	  //scene.setCamera(glCam);
	  
	  scene.setGridIsDrawn(false);
	  scene.setAxisIsDrawn(false);
	  scene.setRadius(150);
	  scene.showAll();
	  boxes = new Box[50];
	  // create an array of boxes with random positions, sizes and colors
	  for (int i = 0; i < boxes.length; i++)
	    boxes[i] = new Box(scene);
	  
	  println( "PApplet.ALT: " + PApplet.ALT + " Event.ALT: " + Event.ALT );
	  println( "PApplet.SHIFT: " + PApplet.SHIFT + " Event.SHIFT: " + Event.SHIFT );
	  println( "PApplet.CONTROL: " + PApplet.CONTROL + " Event.CTRL: " + Event.CTRL );
	  //println( "PApplet.SHIFT: " + PApplet.m + " Event.SHIFT: " + Event.SHIFT );
	  
	  println(SHIFT + ": " + getMT(Event.SHIFT));
	}
	
	public String getMT(int mask) {
		String r = new String();
		if((Event.ALT & mask) == Event.ALT) r += "ALT";						
		if((Event.SHIFT & mask) == Event.SHIFT) r += (r.length() > 0) ? "+SHIFT" : "SHIFT";
		if((Event.CTRL & mask) == Event.CTRL) r += (r.length() > 0) ? "+CTRL" : "CTRL";
		if((Event.META & mask) == Event.META) r += (r.length() > 0) ? "+META" : "META";
		return r;
	}	

	public void draw() {
	  background(0);
	  for (int i = 0; i < boxes.length; i++)    
	    boxes[i].draw();
	}
	
	public void keyPressed() {
		if(key == 't' || key == 'T') {
			if( scene.camera().isAttachedToP5Camera() ) {
				scene.camera().detachFromP5Camera();
				println("cam matrices detached");
			}
			else {
				scene.camera().attachToP5Camera();
				println("cam matrices attached");
			}
		}
		if(key == 'u' || key == 'U') {
			if( scene.isRightHanded() ) {
				scene.setLeftHanded();
				println("left handed set");
			}
			else {
				scene.setRightHanded();
				println("right handed set");
			}
		}
		// /**
		if (key == 'x' || key == 'X')
			scene.camera().frame().setCADAxis(new PVector(1, 0, 0));
		if (key == 'y' || key == 'Y')
		    scene.camera().frame().setCADAxis(new PVector(0, 1, 0));
		if (key == 'z' || key == 'Z')
		    scene.camera().frame().setCADAxis(new PVector(0, 0, 1));
		// */
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "PointUnderPixel" });
	}
}
