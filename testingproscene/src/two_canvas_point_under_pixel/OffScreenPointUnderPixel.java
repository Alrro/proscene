package two_canvas_point_under_pixel;

import geom.Box;
import processing.core.*;
import processing.event.Event;
import processing.opengl.*;
import remixlab.proscene.*;

public class OffScreenPointUnderPixel extends PApplet {
	Scene scene, auxScene;
	Box[] boxes;

	PGraphics canvas, auxCanvas;
	int mainWinHeight = 400; // should be less than the papplet height

	public void setup() {
		size(640, 720, P3D);
		canvas = createGraphics(width, mainWinHeight, P3D);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.camera().attachToP5Camera();
		scene.addDrawHandler(this, "drawing");
		//scene.camera().detachFromP5Camera();
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setShortcut('z', Scene.KeyboardAction.ARP_FROM_PIXEL);
		// add the click actions to all camera profiles
		CameraProfile[] camProfiles = scene.getCameraProfiles();
		for (int i=0; i<camProfiles.length; i++) {
		    // left click will zoom on pixel:
		    camProfiles[i].setClickBinding( LEFT, Scene.ClickAction.ZOOM_ON_PIXEL );
		    // middle click will show all the scene:
		    camProfiles[i].setClickBinding( CENTER, Scene.ClickAction.SHOW_ALL);
		    // right click will will set the arcball reference point:
		    camProfiles[i].setClickBinding( RIGHT, Scene.ClickAction.ARP_FROM_PIXEL );
		    // double click with the middle button while pressing SHIFT will reset the arcball reference point:
		    camProfiles[i].setClickBinding( Event.SHIFT, CENTER, 2, Scene.ClickAction.RESET_ARP );
		  }

		boxes = new Box[50];
		// create an array of boxes with random positions, sizes and colors
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(scene);

		// /**
		auxCanvas = createGraphics(width, (height - canvas.height), P3D);
		auxScene = new Scene(this, (PGraphics3D) auxCanvas);
		//auxScene.camera().detachFromP5Camera();
		
		/**
		auxScene.setInteractiveFrame(new InteractiveFrame(auxScene));
		auxScene.interactiveFrame().translate(new PVector(30, 30, 0));
		auxScene.setRadius(50);
		auxScene.setGridIsDrawn(false);
		*/

		auxScene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		auxScene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		// same drawing function which is defined below
		//auxScene.addDrawHandler(this, "drawing");
		// */
	}

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		// the actual scene drawing (defined by the "drawing" function below)
		// is magically called by the draw handler
		scene.beginDraw();
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		/**
		 * auxCanvas.beginDraw(); // same here with the auxScene
		 * auxScene.beginDraw(); auxScene.endDraw(); auxCanvas.endDraw();
		 * image(auxCanvas, 0, canvas.height); //
		 */
	}

	public void drawing(Scene scn) {
		PGraphicsOpenGL pg3d = scn.renderer();
		pg3d.background(0);
		for (int i = 0; i < boxes.length; i++)
			boxes[i].draw();
	}

	public void handleMouse() {
		if (mouseY < canvas.height) {
			scene.enableMouseHandling();
			scene.enableKeyboardHandling();
			auxScene.disableMouseHandling();
			auxScene.disableKeyboardHandling();
		} else {
			scene.disableMouseHandling();
			scene.disableKeyboardHandling();
			auxScene.enableMouseHandling();
			auxScene.enableKeyboardHandling();
		}
	}
}
