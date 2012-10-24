package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import controlP5.*;
import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class CRDGLGraphics extends PApplet {
	/**
	 * Driver
	 * 
	 * This release of proscene 1.1.1.b (the one that comes with this sketch)
	 * works only in P5-1.5.1. It implements the new CAD_ARCBALL CameraProfile
	 * (default) based on the new ROTATE_CAD Scene.MouseAction. The others
	 * are: WHEELED_ARCBALL, and FIRST_PERSON. Please refer to the
	 * CameraProfile example to further customise your profiles (unlikely).
	 * 
	 * a. Press 'e' to change camera type: ortho or persp.
	 * b. Press 'f' to draw the frame selection hints.
	 * c. Press 'u' to change the plane relative movement: world or plane.
	 * d. Press the space bar to cycle among camera profiles.
	 * 
	 * The navigator position is parameterized using screen coordinates:
	 * screenX and screenY.
	 * 
	 * Observation: the navigator should always be visible (please see the
	 * parameteriseNavigator documentation below)
	 */
		
	String renderer = GLConstants.GLGRAPHICS;
	
	boolean once = false;
	
	//Define scene dimension here
	int w = 640;
	int h = 480;
	
	//define your navigator position using screen coordinates
	int screenX = w*3/4;
	int screenY = h/4;
	
	//Define navigator size in pixels here:
	float boxLenght = 50;
	float boxLenghtRatio;
	
	boolean rotateRespectToWorld = false;
		
	GLGraphicsOffScreen canvas;
	
	Scene scene;
	InteractiveFrame iFrame, planeIFrame;	
	ControlP5 controlP5;

	int sliderValue = 0;

	public void setup() {
		size(w, h, renderer);	
		canvas = new GLGraphicsOffScreen(this, width, height);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		//scene.setCameraKind(Camera.Kind.STANDARD);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
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
		scene.drawAxis(boxLenghtRatio * 1.3f);
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			canvas.fill(255, 0, 0);
			canvas.box(boxLenghtRatio);
		} else if (scene.interactiveFrameIsDrawn()) {
			canvas.fill(0, 255, 255);
			canvas.box(boxLenghtRatio);
		} else {
			canvas.fill(0, 0, 255);
			canvas.box(boxLenghtRatio);
		}
		canvas.popMatrix();
		canvas.popMatrix();

		PVector slide;
		if( rotateRespectToWorld )
			slide = PVector.mult(iFrame.yAxis(), -sliderValue);
		else {
			slide = new PVector(0, -sliderValue, 0);
			// Transform to world coordinate system
			slide = scene.camera().frame().orientation().rotate(PVector.mult(slide, scene.camera().frame().translationSensitivity()));
			// And then down to frame
			if (planeIFrame.referenceFrame() != null)
				slide = planeIFrame.referenceFrame().transformOf(slide);
		}		
		
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
		
		GLTexture tex = canvas.getTexture();
		image(tex, 0, 0, width, height);
		
		controlP5.draw();
		
		parameteriseNavigator();
	}
	
	/**
	 * This procedure defines the parameterization.
	 * Note that the navigator should *always* be visible since the third parameter passed to
	 * unprojectedCoordinatesOf is 0.5, i.e., just half way between zNear and zFar
	 * Please refer to camera.unprojectedCoordinatesOf 
	 */
	public void parameteriseNavigator() {
		if( scene.cameraType() == Camera.Type.ORTHOGRAPHIC )
			//use just one out of the two
			//iFrame.setTranslation(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f), scene.camera().frame()));
			iFrame.setPosition(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f)));
		else {
			//PERSP may be optimised
			if(!once) {								
				//use just one out of the two
				//iFrame.setTranslation(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f), scene.camera().frame()));
				iFrame.setPosition(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, 0.5f)));
				once = true;
			}
		}
		this.boxLenghtRatio = boxLenght * scene.camera().pixelP5Ratio(iFrame.position());
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.CRDGLGraphics" });
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			rotateRespectToWorld = !rotateRespectToWorld;
			if(rotateRespectToWorld)
				println("Rotate plane respect to the world coordinate system");
			else
				println("Rotate plane relative to its position in the world coordinate system");
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