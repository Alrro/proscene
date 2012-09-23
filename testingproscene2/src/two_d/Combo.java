package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class Combo extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	InteractiveFrame auxFrame;
	String renderer = P2D;
	// String renderer = JAVA2D;	

	public void setup() {
		size(640, 720, renderer);
		canvas = createGraphics(640, 360, renderer);
		scene = new Scene(this, canvas);
		scene.addDrawHandler(this, "mainDrawing");
		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		scene.interactiveFrame().translate(new Vector3D(30, 30));
		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		auxCanvas = createGraphics(640, 360, renderer);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.addDrawHandler(this, "auxDrawing");
		auxScene.setRadius(200);
		auxScene.showAll();
		
		auxFrame = new InteractiveFrame(auxScene);
		auxFrame.linkTo(scene.interactiveFrame());

		handleMouse();
	}

	public void draw() {
		handleMouse();

		canvas.beginDraw();
		scene.beginDraw();
		canvas.background(0);
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxCanvas.background(0);		
		auxScene.endDraw();
		auxCanvas.endDraw();

		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
	}

	public void mainDrawing(Scene s) {
		commonDrawing(s);
		
		s.pg().pushStyle();
		s.pushMatrix();
		s.interactiveFrame().applyTransformation();
		s.drawAxis(40);
		if (s.interactiveFrame().grabsMouse()) {
			s.pg().fill(255, 0, 0);
			s.pg().rect(0, 0, 35, 35);
		} else if (s.interactiveFrameIsDrawn()) {
			s.pg().fill(0, 255, 255);
			s.pg().rect(0, 0, 35, 35);
		} else {
			s.pg().fill(0, 0, 255);
			s.pg().rect(0, 0, 30, 30);
		}
		s.popMatrix();
		s.pg().popStyle();
	}
		
	public void auxDrawing(Scene s) {
		commonDrawing(s);
		
		s.pg().pushStyle();
		s.pg().stroke(255, 255, 0);
		s.pg().fill(255, 255, 0, 160);
		s.drawViewWindow(scene.viewWindow());
		s.pg().popStyle();
		
		s.pushMatrix();
		auxFrame.applyTransformation();
		s.drawAxis(40);
		s.pg().pushStyle();
		if (scene.interactiveFrame().grabsMouse()) {
			s.pg().fill(255, 0, 0);
			s.pg().rect(0, 0, 35, 35);
		} else if (s.interactiveFrameIsDrawn()) {
			s.pg().fill(0, 255, 255);
			s.pg().rect(0, 0, 35, 35);
		} else {
			s.pg().fill(0, 0, 255);
			s.pg().rect(0, 0, 30, 30);
		}
		s.pg().popStyle();
		s.popMatrix();
	}
	
	public void commonDrawing(Scene s) {
		s.pg().ellipse(0, 0, 40, 40);
		s.pg().rect(50, 50, 30, 30);
	}

	public void handleMouse() {
		if (mouseY < 360) {
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

	public void keyPressed() {
		if (key == 'u' || key == 'U') {
			println("projection matrix:");
			scene.viewWindow().projection().print();
			println("world matrix:");
			scene.viewWindow().frame().worldMatrix().print();
			println("view matrix:");
			scene.viewWindow().view().print();
			println("camera angle: "
					+ scene.viewWindow().frame().orientation().angle());
		}
	}
}
