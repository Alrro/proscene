package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import controlP5.*;

public class CRD extends PApplet {
	String renderer = P3D;
	//String renderer = OPENGL;
	int w = 640;
	int h = 480;
	
	//define your navigator position using screen coordinates 
	int screenX = w*3/4;
	int screenY = h/4;
	
	PGraphics canvas;
	Scene scene;
	InteractiveFrame iFrame, planeIFrame;	
	ControlP5 controlP5;

	int sliderValue = 0;

	public void setup() {
		size(w, h, renderer);
		canvas = createGraphics(width, height, renderer);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.showAll();
		
		iFrame = new InteractiveFrame(scene);
		iFrame.setReferenceFrame(scene.camera().frame());
		
		LocalConstraint constraint = new LocalConstraint();
		constraint.setTranslationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
		iFrame.setConstraint(constraint);
		
		scene.setInteractiveFrame(iFrame);
		planeIFrame = new InteractiveFrame(scene);
		controlP5 = new ControlP5(this);
		controlP5.addSlider("sliderValue", -100, 100, sliderValue, 10, 50, 100,	10);
		
		// throws a null when running as an applet from within eclipse 
		//this.frame.setResizable(true);
	}

	public void draw() {		
		canvas.beginDraw();
		scene.beginDraw();

		canvas.background(255);
		canvas.fill(204, 102, 0);			

		// first level: draw respect to the camera frame
		canvas.pushMatrix();
		scene.camera().frame().applyTransformation();
		// second level: draw respect to the iFrame
		canvas.pushMatrix();
		iFrame.applyTransformation();
		scene.drawAxis(20);

		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			canvas.fill(255, 0, 0);
			canvas.box(20, 20, 20);
		} else if (scene.interactiveFrameIsDrawn()) {
			canvas.fill(0, 255, 255);
			canvas.box(20, 20, 20);
		} else {
			canvas.fill(0, 0, 255);
			canvas.box(20, 20, 20);
		}
		canvas.popMatrix();
		canvas.popMatrix();

		// rotate respect to world coordinate system origin
		//PVector slide = PVector.mult(iFrame.yAxis(), -sliderValue);
		
		// /**
		// rotate respect to plane center
		PVector slide = new PVector(0, -sliderValue, 0);
		// Transform to world coordinate system
		slide = scene.camera().frame().orientation().rotate(PVector.mult(slide, scene.camera().frame().translationSensitivity()));
		// And then down to frame
		// /*
		if (planeIFrame.referenceFrame() != null)
			slide = planeIFrame.referenceFrame().transformOf(slide);
		// */
		
		planeIFrame.setTranslation(slide);
		planeIFrame.setRotation(iFrame.rotation());

		canvas.pushMatrix();
		planeIFrame.applyTransformation();
		scene.drawAxis(40);
		canvas.rectMode(CENTER);
		canvas.rect(0, 0, 200, 200);
		canvas.popMatrix();

		scene.endDraw();
		canvas.endDraw();

		image(canvas, 0, 0, width, height);
		controlP5.draw();
		
		//this line defines the parameterization
		iFrame.setTranslation(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f), scene.camera().frame()));
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.CRD" });
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			// /**
			PVector screenPos = new PVector(screenX, screenY, 0.5f);
			PVector wVec = scene.camera().unprojectedCoordinatesOf(screenPos);
			PVector cVec = scene.camera().cameraCoordinatesOf(wVec);
			// */
			println("wVec: " + wVec);
			println("cVec: " + cVec);
			iFrame.setTranslation(cVec);
		}
		if(key == 'v' || key == 'V') {
			iFrame.setTranslation(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f),
					                                                      scene.camera().frame()));
		}
	}

	public void mousePressed() {
		if (mouseX < 100) {
			scene.disableMouseHandling();
		} else {
			scene.enableMouseHandling();
		}
	}
}