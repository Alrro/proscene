package two_canvas_iframe;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

public class FrameInteraction extends PApplet {
	Scene scene, auxScene;

	PGraphics canvas, auxCanvas;
	int mainWinHeight = 400; // should be less than the papplet height

	public void setup() {
		size(640, 720, P3D);
		canvas = createGraphics(width, mainWinHeight, P3D);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.addDrawHandler(this, "drawing");
		scene.camera().detachFromP5Camera();

		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		scene.interactiveFrame().translate(new PVector(30, 30, 0));
		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		auxCanvas = createGraphics(width, (height - canvas.height), P3D);
		auxScene = new Scene(this, (PGraphics3D) auxCanvas);
		auxScene.camera().detachFromP5Camera();
		auxScene.setInteractiveFrame(new InteractiveFrame(auxScene));
		auxScene.interactiveFrame().translate(new PVector(30, 30, 0));
		auxScene.setRadius(50);
		auxScene.setGridIsDrawn(false);

		auxScene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		auxScene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		// same drawing function which is defined below
		auxScene.addDrawHandler(this, "drawing");
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

		// /**
		auxCanvas.beginDraw();
		// same here with the auxScene
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		image(auxCanvas, 0, canvas.height);
		// */
	}

	public void drawing(Scene scn) {
		PGraphicsOpenGL pg3d = scn.renderer();
		pg3d.background(0);
		pg3d.fill(204, 102, 0);
		pg3d.box(20, 20, 40);
		// Save the current model view matrix
		pg3d.pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is handy but
		// inefficient
		scn.interactiveFrame().applyTransformation(); // optimum
		// Draw an axis using the Scene static function
		scn.drawAxis(20);
		// Draw a second box attached to the interactive frame
		if (scn.interactiveFrame().grabsMouse()) {
			pg3d.fill(255, 0, 0);
			pg3d.box(12, 17, 22);
		} else if (scn.interactiveFrameIsDrawn()) {
			pg3d.fill(0, 255, 255);
			pg3d.box(12, 17, 22);
		} else {
			pg3d.fill(0, 0, 255);
			pg3d.box(10, 15, 20);
		}
		pg3d.popMatrix();
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
