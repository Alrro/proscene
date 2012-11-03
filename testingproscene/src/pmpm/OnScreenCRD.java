package pmpm;

import processing.core.*;
import remixlab.proscene.*;
import controlP5.*;

@SuppressWarnings("serial")
public class OnScreenCRD extends PApplet {
	/**
	 * Driver
	 * 
	 * This release of proscene 1.1.93c (the one that comes with this sketch)
	 * works only in P5-2b5. It implements the new CAD_ARCBALL CameraProfile
	 * (default) based on the new ROTATE_CAD Scene.MouseAction. The others are:
	 * WHEELED_ARCBALL, and FIRST_PERSON. Please refer to the CameraProfile
	 * example to further customise your profiles (unlikely).
	 * 
	 * a. Press 'e' to change camera type: ortho or persp. b. Press 'f' to draw
	 * the frame selection hints. c. Press 'u' to change the plane relative
	 * movement: world or plane. d. Press the space bar to cycle among camera
	 * profiles.
	 * 
	 * The navigator position is parameterized using screen coordinates: screenX
	 * and screenY.
	 * 
	 * Observation: the navigator should always be visible (please see the
	 * parameteriseNavigator documentation below)
	 */
	
	// Define scene dimension here
	int w = 640;
	int h = 480;

	// define your navigator position using screen coordinates
	int screenX = w * 3 / 4;
	int screenY = h / 4;	
	//this is a depth value ranging in [0..1] (near and far plane respectively).
	//play with it according to your needs
	float screenZ = 0.15f;

	// Define navigator size in pixels here:
	float boxLenght = 50;
	float boxLenghtRatio;

	boolean rotateRespectToWorld = false;
	boolean drawFrameSelectionHints = false;
	boolean applied = false;

	Scene scene;
	InteractiveFrame iFrame;
	InteractiveFrame planeIFrame;
	PVector slide = new PVector();
	PVector tranSlide = new PVector();
	ControlP5 controlP5;

	int sliderValue = 0;
	int oldSliderValue = 0;

	public void setup() {
		size(w, h, P3D);
		scene = new Scene(this);
		//scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		scene.showAll();

		iFrame = new InteractiveFrame(scene);
		iFrame.setReferenceFrame(scene.camera().frame());

		LocalConstraint constraint = new LocalConstraint();
		constraint.setTranslationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
		iFrame.setConstraint(constraint);

		scene.setInteractiveFrame(iFrame);		
		planeIFrame = new InteractiveFrame(scene);
		
		controlP5 = new ControlP5(this);
		controlP5.addSlider("sliderValue", -100, 100, sliderValue, 10, 50, 100, 10);
		controlP5.setAutoDraw(false);

		scene.registerCameraProfile(new CameraProfile(scene, "CAD_ARCBALL",	CameraProfile.Mode.CAD_ARCBALL));
		scene.setCurrentCameraProfile("CAD_ARCBALL");
		
		// Needs testing: disabling it gives better results in my setup. See:
		// 1. http://code.google.com/p/proscene/issues/detail?id=7
		// 2. http://processing.org/reference/hint_.html
		hint(DISABLE_STROKE_PERSPECTIVE);

		// throws a null when running as an applet from within eclipse
		// this.frame.setResizable(true);
	}

	public void draw() {
		background(255);
		fill(204, 102, 0);

		// first level: draw respect to the camera frame
		pushMatrix();
		scene.camera().frame().applyTransformation();
		// second level: draw respect to the iFrame
		pushMatrix();
		iFrame.applyTransformation();
		scene.drawAxis(boxLenghtRatio * 1.3f);
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(boxLenghtRatio);
		} else
		if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(boxLenghtRatio);
		} else {
			fill(0, 0, 255);
			box(boxLenghtRatio);
		}
		popMatrix();
		popMatrix();

		if (rotateRespectToWorld) {
			slide = PVector.mult(iFrame.yAxis(), -sliderValue);
			planeIFrame.setTranslation(slide);
			planeIFrame.setRotation(iFrame.rotation());
		}		
		else {
			// far from simple
			PVector oldSlide = slide.get();
			slide = new PVector(0, 0, (sliderValue-oldSliderValue));
			boolean changed = ((oldSlide.x != slide.x) || (oldSlide.y != slide.y) || (oldSlide.z != slide.z));
			tranSlide = planeIFrame.inverseTransformOf(slide);			
			if (planeIFrame.referenceFrame() != null)
				tranSlide = planeIFrame.referenceFrame().transformOf(tranSlide);			
			if(changed) {
				oldSliderValue = sliderValue;
				planeIFrame.translate(tranSlide);
			}
			planeIFrame.setRotation(new Quaternion(iFrame.rotation()));			
		}		

		pushMatrix();
		planeIFrame.applyTransformation(scene);
		scene.drawAxis(40);
		rectMode(CENTER);
		rect(0, 0, 200, 200);
		popMatrix();			

		//the important bit here (!) is the order of the operations:
		//1. Draw the frame selection hints		
		if(drawFrameSelectionHints)
			scene.drawSelectionHints();		
		//2. Disable the z-buffer (it's done in beginScreenDrawing)
		scene.beginScreenDrawing();
		//3. Then draw the gui: it should be drawn on top of the 3D scene
		//Think if the painter's algorithm
		controlP5.draw();
		scene.endScreenDrawing();			

		parameteriseNavigator();
	}

	/**
	 * This procedure defines the parameterization. Note that the navigator
	 * should *always* be visible since the third parameter passed to
	 * unprojectedCoordinatesOf is 0.5, i.e., just half way between zNear and
	 * zFar Please refer to camera.unprojectedCoordinatesOf.
	 */
	public void parameteriseNavigator() {
		iFrame.setPosition(scene.camera().unprojectedCoordinatesOf(new PVector(screenX, screenY, screenZ)));
		this.boxLenghtRatio = boxLenght	* scene.camera().pixelP5Ratio(iFrame.position());
	}	

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "pmpm.OnScreenCRD" });
	}

	public void keyPressed() {
		if(key == 'f' || key == 'F') {
			drawFrameSelectionHints = !drawFrameSelectionHints;
		}
		if(key == 'u' || key == 'U') {
			rotateRespectToWorld = !rotateRespectToWorld;
			if (rotateRespectToWorld)
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