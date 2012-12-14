package basic;
import processing.core.*;
import processing.event.Event;
import remixlab.proscene.*;
//import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.ClickAction;

@SuppressWarnings("serial")
public class CameraProfiles extends PApplet {
	PImage b;
	Scene scene;
	MyCameraProfile profile;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		scene.interactiveFrame().translate(new PVector(30, 30, 0));
		
		// 1. Perform some keyboard configuration:
		// Note that there are some defaults set (soon to be  documented ;)
		// change interaction between camera an interactive frame:
		// 'f' toggles interactive drawing of the interactive frame:
		scene.setShortcut('f', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// 'z' toggles camera type:
		scene.setShortcut('z', Scene.KeyboardAction.CAMERA_TYPE);

		// 2. Customized camera profile:
		profile = new MyCameraProfile(scene, "MY_PROFILE");
		scene.registerCameraProfile(profile);
		// Unregister FIRST_PERSON camera profile (i.e., leave only WHEELED_ARCBALL and MY_PROFILE):
		scene.unregisterCameraProfile("FIRST_PERSON");
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 20, 40);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is handy but
		// inefficient
		scene.interactiveFrame().applyTransformation(); // optimum
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		// Draw a second box attached to the interactive frame
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

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CameraProfiles" });
	}

	public class MyCameraProfile extends CameraProfile {
		public MyCameraProfile(Scene scn, String name) {
			super(scn, name);
			// 1. Perform some keyboard configuration (warning: camera profiles override those of the scene):
		    // 'u' = move camera up
		    setShortcut('u', Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
		    // CTRL + SHIFT + 'l' = move camera to the left
		    setShortcut((Event.ALT | Event.SHIFT), 'l', Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
		    // 'S' (note the caps) = move the camera to show all the scene
		    setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
		    // 2. Describe how to control the camera:
		    // mouse left button = translate camera
		    setCameraMouseBinding(LEFT, Scene.MouseAction.TRANSLATE);
		    // SHIFT + mouse left button = rotate camera
		    setCameraMouseBinding(Event.SHIFT, LEFT, Scene.MouseAction.ROTATE);   
		    // Right button = zoom on region
		    setCameraMouseBinding(RIGHT, Scene.MouseAction.ZOOM_ON_REGION);
		    // 3. Describe how to control the interactive frame:
		    // Left button = rotate interactive frame
		    setFrameMouseBinding(LEFT, Scene.MouseAction.ROTATE);
		    // Right button = translate interactive frame
		    setFrameMouseBinding(RIGHT, Scene.MouseAction.TRANSLATE);
		    // Right button + SHIFT = screen translate interactive frame
		    setFrameMouseBinding(Event.SHIFT, RIGHT, Scene.MouseAction.SCREEN_TRANSLATE);
		    // 4. Configure some click actions:
		    // double click + button left = align frame with world
		    setClickBinding(LEFT, 2, Scene.ClickAction.ALIGN_FRAME);
		    // single click + middle button + SHIFT + ALT = interpolate to show all the scene
		    setClickBinding((Event.SHIFT | Event.ALT), CENTER, 1, Scene.ClickAction.ZOOM_TO_FIT);
		    // double click + middle button = align camera with world
		    setClickBinding(CENTER, 2, Scene.ClickAction.ALIGN_CAMERA);
		}
	}
}