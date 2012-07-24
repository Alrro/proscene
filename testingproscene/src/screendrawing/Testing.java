package screendrawing;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Testing extends PApplet {
	/**
	 * A. Goals
	 * 
	 * 1. To handle the matrices consistently (overriden methods: setPProjectionMatrix
	 * and setPModelViewMatrix).
	 * 2. To fix the screenDrawing in proscene (overriden methods: beginScreenDrawing and
	 * endScreenDrawing). The main method to be set here is beginScreenDrawing.
	 * 
	 * A. Differences between running the program in eclipse and running it in the PDE.
	 *  
	 * 1. Drawing artifacts appear in the axis when running the sketch in eclipse. The grid
	 * isn't drawn in eclipse. None of these things happen when running the program in the PDE.
	 * 
	 * B. Instructions
	 * 
	 * The sketch implements the projection and modelview matrix computation, and the screen
	 * drawing (pretty much in the same way as it is done in the proscene trunk). It's worth noticing
	 * that: 1. proscene matrices are references to P5 matrices; and, 2. setPProjectionMatrix
	 * and setPModelViewMatrix are called in the pre(), i.e., just before draw(). Here, two
	 * lines along the sketch diagonal should be drawn using screen coordinates, anchor inches
	 * away the corner. The code is just broken and doesn't work using ortho nor persp (although
	 * there are noticeable changes between these two).
	 * 
	 * 1. 'e' switches camera type: perspective and orthographic. 
	 * 2. 'v' switches camera kind: proscene and standard.
	 * 3. 'x' switches the way the projection matrix is computed.
	 * 4. 'y' switches the way the modelview matrix is computed.
	 * 5. 'z' switches the way beginScreenDrawing is implemented.
	 * 
	 * C. Observations.
	 * 
	 * 1. When switching the way the projection matrix is computed, I noticed a flip in the
	 * the way the scene is controlled with the mouse. It didn't happen in P5-1.5.
	 * 2. The two implementations of beginScreenDrawing doesn't affect that much. One uses
	 * your previous suggestion and the other is inspired by "How do I draw 2D controls over
	 * my 3D rendering?" (http://www.opengl.org/archives/resources/faq/technical/transformations.htm).
	 */
	ProSceneMatrices scene;
	int anchor = 10;
	boolean changed;

	public void setup() {
		size(640, 360, P3D);
		//size(360, 360, P3D);
		scene = new ProSceneMatrices(this);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		scene.setRadius(200);
		scene.showAll();
	}

	public void draw() {
		background(80);
		box(160);
		stroke(200, 100, 10);

		scene.beginScreenDrawing();
		PVector p1 = scene.coords(new Point(anchor, anchor));
		PVector p2 = scene.coords(new Point(width - anchor, height - anchor));
		PVector p3 = scene.coords(new Point(anchor, height - anchor));
		PVector p4 = scene.coords(new Point(width - anchor, anchor));
		scene.renderer().line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
		scene.renderer().line(p3.x, p3.y, p3.z, p4.x, p4.y, p4.z);
		scene.endScreenDrawing();
	}
	
	public void keyPressed() {
		if (key == 'x' || key == 'X') {
			scene.projMatrixOpt1 = !scene.projMatrixOpt1;
			if (scene.projMatrixOpt1)
				println("camera().computeProjectionMatrix() is being called");
			else
				println("renderer().perspective() or renderer().ortho() is being called");
		}
		if (key == 'y' || key == 'Y') {
			scene.modelviewMatrixOpt1 = !scene.modelviewMatrixOpt1;
			if (scene.modelviewMatrixOpt1)
				println("camera().computeModelViewMatrix() is being called");
			else
				println("renderer().camera() is being called");
		}
		if (key == 'z' || key == 'Z') {
			scene.screenDrawingOpt1 = !scene.screenDrawingOpt1;
			if(scene.screenDrawingOpt1)
				println("screen drawing by Andres suggestion (using width and height)");
			else
				println("QGLViewer based screen drawing (using camera().getOrthoWidthHeight())");
		}
		if (key == 'w' || key == 'W') {
			float[] wh = scene.camera().getOrthoWidthHeight();
			println(wh[0] + " " + wh[1]);
		}
	}

	class ProSceneMatrices extends Scene {
		boolean projMatrixOpt1 = false;
		boolean modelviewMatrixOpt1 = false;
		
		boolean screenDrawingOpt1 = false;

		public ProSceneMatrices(PApplet p) {
			super(p);
		}

		@Override
		public void init() {
			setGridIsDrawn(true);
			setAxisIsDrawn(true);
		}

		@Override
		public void beginScreenDrawing() {
			if (startCoordCalls != 0)
				throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
								         + "endScreenDrawing() and they cannot be nested. Check your implementation!");

			startCoordCalls++;

			renderer().hint(DISABLE_DEPTH_TEST);
			renderer().pushProjection();

			if(screenDrawingOpt1) {
				float cameraZ = (height/2.0f) / PApplet.tan(camera().fieldOfView() /2.0f);
				float cameraMaxFar = cameraZ *	2.0f;
				float cameraNear = cameraZ / 2.0f;
				float cameraFar = cameraZ * 2.0f;
				renderer().ortho(-width/2, width/2, -height/2, height/2, cameraNear, cameraFar);
			}
			
			else {
				changed = false;
				if (camera().type() == Camera.Type.PERSPECTIVE) {
					changed = true;
					setCameraType(Camera.Type.ORTHOGRAPHIC);	
				}
				
				float[] wh = camera().getOrthoWidthHeight();// return halfWidth halfHeight
				renderer().ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());
			}

			renderer().pushMatrix();
			// Camera needs to be reset!
			renderer().camera();
			zC = 0.0f;
		}

		@Override
		public void endScreenDrawing() {
			startCoordCalls--;
			if (startCoordCalls != 0)
				throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
								         + "endScreenDrawing() and they cannot be nested. Check your implementation!");
			renderer().popProjection();
			renderer().popMatrix();
			renderer().hint(ENABLE_DEPTH_TEST);
			
			if(changed)
				setCameraType(Camera.Type.PERSPECTIVE);
		}

		@Override
		protected void setPProjectionMatrix() {
			if (projMatrixOpt1)
				camera().computeProjectionMatrix();
			else {
				switch (camera().type()) {
				case PERSPECTIVE:
					renderer().perspective(camera().fieldOfView(), camera().aspectRatio(), camera().zNear(), camera().zFar());
					break;
				case ORTHOGRAPHIC:
					float[] wh = camera().getOrthoWidthHeight();// return halfWidth halfHeight					
					renderer().ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(),camera().zFar());
					break;
				}
			}
		}

		@Override
		protected void setPModelViewMatrix() {
			if (modelviewMatrixOpt1)
				camera().computeModelViewMatrix();
			else {
				renderer().camera(camera().position().x, camera().position().y,
						   camera().position().z, camera().at().x,
						   camera().at().y, camera().at().z,
						   camera().upVector().x, camera().upVector().y, camera().upVector().z);
			}
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "screendrawing.Testing" });
	}
}
