package basic_geom;
import geom.Box;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
//import javax.media.opengl.glu.GLU;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

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
	  
	  GLCamera glCam = new GLCamera(scene);
	  scene.setCamera(glCam);
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
	    boxes[i].draw();
	}
	
	public void keyPressed() {		
		if ((key == 'y') || (key == 'Y')) {
			scene.camera().optimizeUnprojectCache(!scene.camera().unprojectCacheOptimized);
			if(scene.camera().unprojectCacheOptimized)
				println("unprojectCacheOptimized is on");
			else
				println("unprojectCacheOptimized is off");
		}
		if( key == 'x' )
			scene.switchTimers();
	}

	class GLCamera extends Camera {
		protected PGraphicsOpenGL pgl;
		protected GL gl;
		//protected GLU glu;

		public GLCamera(Scene scn) {
			super(scn);
			pgl = (PGraphicsOpenGL) scn.pg3d;
			gl = pgl.gl;
			//glu = pgl.glu;
		}
		
		@Override
		public WorldPoint pointUnderPixel(Point pixel) {
			float[] depth = new float[1];
			pgl.beginGL();
			gl.glReadPixels((int) pixel.x, (screenHeight() - (int) pixel.y), 1,
					1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, FloatBuffer
							.wrap(depth));
			pgl.endGL();
			Vector3D point = new Vector3D((int) pixel.x, (int) pixel.y, depth[0]);
			point = unprojectedCoordinatesOf(point);
			return new WorldPoint(point, (depth[0] < 1.0f));
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic_geom.PointUnderPixel" });
	}
}
