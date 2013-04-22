package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class Combo extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;	
	InteractiveFrame frame1, auxFrame1, frame2, auxFrame2, frame3, auxFrame3;
	String renderer = P2D;
	//String renderer = JAVA2D;	
	boolean drawHints = false;

	public void setup() {
		size(640, 720, renderer);
		canvas = createGraphics(640, 360, renderer);
		canvas.smooth();
		scene = new Scene(this, canvas);
		scene.addDrawHandler(this, "mainDrawing");
		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		//scene.setInteractiveFrame(new InteractiveFrame(scene));
		frame1 = new InteractiveFrame(scene);
		frame1.translate(new Vector3D(30, 30));
		frame2 = new InteractiveFrame(scene);
		frame2.setReferenceFrame(frame1);
		frame2.translate(new Vector3D(40,0,0));
		frame3 = new InteractiveFrame(scene);
		frame3.setReferenceFrame(frame2);
		frame3.translate(new Vector3D(40,0,0));
		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		auxCanvas = createGraphics(640, 360, renderer);
		auxCanvas.smooth();
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.addDrawHandler(this, "auxDrawing");
		//auxScene.setRadius(200);
		auxScene.showAll();
		
		auxFrame1 = new InteractiveFrame(auxScene);
		auxFrame1.linkTo(frame1);
		
		auxFrame2 = new InteractiveFrame(auxScene);
		auxFrame2.linkTo(frame2);
		
		auxFrame3 = new InteractiveFrame(auxScene);
		auxFrame3.linkTo(frame3);

		handleMouse();
		smooth();
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
		s.pg().pushStyle();
		s.pushMatrix();
		if(s == scene)
			frame1.applyTransformation();
		else
			auxFrame1.applyTransformation();		
		if(drawHints)
			s.drawAxis(40);
		if (drawHints && frame1.grabsMouse()) {
			s.pg().fill(255, 0, 0);
			s.pg().rect(0, 0, 40, 10, 5);
		}
		else {
			s.pg().fill(0, 0, 255);
			s.pg().rect(0, 0, 40, 10, 5);
		}
		
		s.pushMatrix();
		if(s == scene)
			frame2.applyTransformation();
		else
			auxFrame2.applyTransformation();
		if(drawHints)
			s.drawAxis(40);
		if (drawHints && frame2.grabsMouse()) {
			s.pg().fill(255, 0, 0);
			s.pg().rect(0, 0, 40, 10, 5);
		}
		else {
			s.pg().fill(255, 0, 255);
			s.pg().rect(0, 0, 40, 10, 5);
		}		
		
		s.pushMatrix();
		if(s == scene)
			frame3.applyTransformation();
		else
			auxFrame3.applyTransformation();
		if(drawHints)
			s.drawAxis(40);
		if (drawHints && frame3.grabsMouse()) {
			s.pg().fill(255, 0, 0);
			s.pg().rect(0, 0, 40, 10, 5);
		}
		else {
			s.pg().fill(0, 255, 255);
			s.pg().rect(0, 0, 40, 10, 5);
		}		
		s.popMatrix();
		
		s.popMatrix();
		
		s.popMatrix();
		s.pg().popStyle();		
	}
		
	public void auxDrawing(Scene s) {
		mainDrawing(s);		
		
		s.pg().pushStyle();
		s.pg().stroke(255, 255, 0);
		s.pg().fill(255, 255, 0, 160);
		s.drawViewWindow(scene.viewWindow());
		s.pg().popStyle();	
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
	
	public void printFrame(GeomFrame frame) {
		println("Translation: " + frame.translation());
		println("Angle: " + frame.rotation().angle());
		println("Scaling: " + frame.scaling());
	}

	public void keyPressed() {
		if (key == 'u' || key == 'U') {
			drawHints = !drawHints;
		}
		if(key == 'v' || key == 'V') {
			scene.viewWindow().flip();			
		}
		/**
		if (key == 'v' || key == 'V') {
			VFrame tmpFrame = new VFrame(scene.is3D());
			tmpFrame.fromMatrix(scene.viewWindow().frame().worldMatrix(), scene.viewWindow().frame().magnitude());
			println("tmpFrame");
			printFrame(tmpFrame);
			println();
			println("scene.viewWindow().frame()");
			printFrame(scene.viewWindow().frame());			
		}
		*/
	}
}
