package SpaceNavigator;

import processing.core.*;
import procontroll.*;
import net.java.games.input.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.constraints.*;
import remixlab.proscene.*;

public class SpaceNavigator extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present",
				"SpaceNavigator.SpaceNavigator" });
	}
	
	Scene scene;
	InteractiveAvatarFrame iFrame;

	ControllIO controll;
	ControllDevice device; // my SpaceNavigator
	ControllSlider sliderXpos; // Positions
	ControllSlider sliderYpos;
	ControllSlider sliderZpos;
	ControllSlider sliderXrot; // Rotations
	ControllSlider sliderYrot;
	ControllSlider sliderZrot;
	ControllButton button1; // Buttons
	ControllButton button2;
	
	float totalX = 0; // inits
	float totalY = 0;
	float totalZ = 0;
	float totalXrot = 0;
	float totalYrot = 0;
	float totalZrot = 0;

	public void setup() {
		size(640, 360, P3D);

		controll = ControllIO.getInstance(this);
		// device = controll.getDevice("SpaceNavigator");//magic name, for
		// windows
		device = controll.getDevice("3Dconnexion SpaceNavigator");// magic name for linux
		device.setTolerance(5.00f);

		sliderXpos = device.getSlider(2);
		sliderYpos = device.getSlider(1);
		sliderZpos = device.getSlider(0);
		sliderXrot = device.getSlider(5);
		sliderYrot = device.getSlider(4);
		sliderZrot = device.getSlider(3);
		button1 = device.getButton(0);
		button2 = device.getButton(1);
		sliderXpos.setMultiplier(0.01f); // sensitivities
		sliderYpos.setMultiplier(0.01f);
		sliderZpos.setMultiplier(0.01f);
		sliderXrot.setMultiplier(0.0001f);
		sliderYrot.setMultiplier(0.0001f);
		sliderZrot.setMultiplier(0.0001f);

		scene = new Scene(this);
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame = new InteractiveAvatarFrame(scene);
		//scene.setInteractiveFrame(iFrame);
		scene.interactiveFrame().translate(new Vector3D(30, 30, 0));
		
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	}

	public void draw() {
		background(33, 170, 170);
		
		updateScene();
		
		// draw scene:		
		fill(204, 102, 0);
		box(20, 30, 40);  
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}		
		popMatrix();
	}
	
	public void updateScene() {
		totalX = totalX + sliderXpos.getValue();
		//debug in linux
		//println("slider x pos value: " + sliderXpos.getValue());
		totalY = totalY + sliderYpos.getValue();
		totalZ = totalZ + sliderZpos.getValue();
		totalXrot = totalXrot + sliderXrot.getValue();
		totalYrot = totalYrot + sliderYrot.getValue();
		totalZrot = totalZrot + sliderZrot.getValue();
		
		
		scene.interactiveFrame().translate(null);
	}
}
