package basic_geom;
import geom.Box;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class PointUnderPixel extends PApplet {
	Scene scene;
	Box [] boxes;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	  scene.setShortcut('z', Scene.KeyboardAction.ARP_FROM_PIXEL);
	  //add the click actions to all camera profiles
	  CameraProfile [] camProfiles = scene.getCameraProfiles();
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
	  
	  scene.setGridIsDrawn(false);
	  scene.setAxisIsDrawn(false);
	  scene.setRadius(150);
	  scene.showAll();
	  boxes = new Box[50];
	  // create an array of boxes with random positions, sizes and colors
	  for (int i = 0; i < boxes.length; i++)
	    boxes[i] = new Box(scene);  
	}

	public void draw() {
		background(0);
		for (int i = 0; i < boxes.length; i++)
			boxes[i].draw(true);
	}
	
	public void keyPressed() {
		if(key == 'q' || key == 'Q') {
			scene.camera().optimizeUnprojectCache( !scene.camera().unprojectCacheIsOptimized() );
			if( scene.camera().unprojectCacheIsOptimized() )
				println("unproject cache is optimized");
			else
				println("unproject cache is NOT optimized");
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
		/**
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
