package events;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.devices.*;
import remixlab.remixcam.events.*;

public class LowLevelEventRegistration extends PApplet {
	Scene scene;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		scene.interactiveFrame().translate(new Vector3D(30, 30, 0));
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.disableKeyboardHandling();
		scene.disableMouseHandling();
	}

	public void draw() {
		background(0);

		fill(204, 102, 0);
		box(20, 30, 40);
		pushMatrix();
		scene.interactiveFrame().applyTransformation();
		scene.drawAxis(20);

		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(12, 17, 22);
		} else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(12, 17, 22);
		} else {
			fill(0, 0, 255);
			box(10, 15, 20);
		}

		popMatrix();
	}
	
	public void keyTyped() {
		new DLKeyEvent(scene, DLKeyEvent.TYPE, 0, key, keyCode);
	}
	
	public void keyReleased() {
		new DLKeyEvent(scene, DLKeyEvent.RELEASE, 0, key, keyCode);
	}
	
    public void mousePressed() {
    	new DLMouseEvent(scene, DLMouseEvent.PRESS, 0, mouseX, mouseY, mouseButton, 0);
    }

    public void mouseReleased() {
    	new DLMouseEvent(scene, DLMouseEvent.RELEASE, 0, mouseX, mouseY, mouseButton, 0);
    }
    
    public void mouseMoved() {
    	new DLMouseEvent(scene, DLMouseEvent.MOVE, 0, mouseX, mouseY, mouseButton, 0);
    }
    
    public void mouseDragged() {
    	new DLMouseEvent(scene, DLMouseEvent.DRAG, 0, mouseX, mouseY, mouseButton, 0);
    }
}
