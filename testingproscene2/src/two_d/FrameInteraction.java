package two_d;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class FrameInteraction extends PApplet {
	Scene scene;
	InteractiveFrame iFrame;

	public void setup() {
		//size(640, 360, P2D);
		size(640, 360, JAVA2D);
		//scene = new Java2DScene(this);
		scene = new Scene(this);
		iFrame = new InteractiveFrame(scene);
		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		//scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame.translate(new Vec(30, 30));
		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		//scene.setShortcut('i', DLKeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		//scene.setShortcut('f', DLKeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setFrameSelectionHintIsDrawn(true);
	}

	public void draw() {		
		//if (scene.renderer() instanceof RendererJava2D  && scene instanceof Java2DScene) bindM();

		background(0);
		fill(204, 102, 0);
		rect(0, 0, 55, 55);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(iFrame.matrix()) is handy but
		// inefficient
		iFrame.applyTransformation();// optimum
		// Draw an axis using the Scene static function
		scene.drawAxis(40);
		// Draw a second box attached to the interactive frame
		if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
			fill(255, 0, 0);
			rect(0, 0, 35, 35);			
		} /** else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			rect(0, 0, 35, 35);
		} */ else {
			fill(0, 0, 255);
			rect(0, 0, 30, 30);
		}
		popMatrix();

		scene.beginScreenDrawing(); text("Hello world", 5, 17);
		scene.endScreenDrawing(); //		
	}

	/**
	public void bindM() {		
		scene.window().computeProjectionMatrix();
		scene.window().computeViewMatrix();
		scene.window().computeProjectionViewMatrix();
		
		Vec pos = scene.window().position();
		Orientable quat = scene.window().frame().orientation();

		translate(scene.width() / 2, scene.height() / 2);
		
		if(scene.isRightHanded()) scale(1,-1);
		
		scale(scene.window().frame().inverseMagnitude().x(), 
			  scene.window().frame().inverseMagnitude().y());
		
		rotate(-quat.angle());
		
		translate(-pos.x(), -pos.y());
		
		//if(scene.isRightHanded()) scale(1,-1);
	}
	*/

	/**
	public void keyPressed() {
		if (key == 'x')
			iFrame.scale(-1, 1);
		if (key == 'X')
			scene.window().frame().scale(-1, 1);
		if (key == 'y')
			iFrame.scale(1, -1);
		if (key == 'Y')
			scene.window().frame().scale(1, -1);
		if (key == 'v' || key == 'V') {
			scene.window().flip();
		}

		println("iFrame scaling: " + iFrame.scaling());
		println("iFrame magnitude: " + iFrame.magnitude());

		if (scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		if (iFrame.isInverted())
			println("iFrame is inverted");
		else
			println("iFrame is NOT inverted");
		if (scene.window().frame().isInverted())
			println("scene.window().frame() is inverted");
		else
			println("scene.window().frame() is NOT inverted");
	}
	*/
	
	public class Java2DScene extends Scene {
		public Java2DScene(PApplet p) {
			super(p);
		}
		
		@Override	
		public void applyTransformation(RefFrame frame) {
			/**
			if( renderer() instanceof RendererJava2D && isRightHanded() && frame.referenceFrame() == null )
				scale(1,-1);
			// */
			translate(frame.translation().x(), frame.translation().y());
			rotate(frame.rotation().angle());
			scale(frame.scaling().x(), frame.scaling().y());
		}		
	}
}
