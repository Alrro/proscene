package basic;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.core.AbstractScene.KeyboardAction;
import remixlab.remixcam.core.AbstractScene.ClickAction;

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
		scene.interactiveFrame().translate(new Vector3D(30, 30, 0));
		
		// 1. Perform some keyboard configuration:
		// Note that there are some defaults set (soon to be  documented ;)
		// change interaction between camera an interactive frame:
		// 'f' toggles interactive drawing of the interactive frame:
		scene.setShortcut('f', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// 'z' toggles camera type:
		scene.setShortcut('z', Scene.KeyboardAction.CAMERA_TYPE);
		// 
		scene.setShortcut('i', KeyboardAction.DRAW_FRAME_SELECTION_HINT);

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
			setShortcut('u', Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
			// CTRL + SHIFT + 'l' = move camera to the left
			setShortcut((Scene.Modifier.CTRL.ID | Scene.Modifier.SHIFT.ID), 'l', Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
			// 'S' (note the caps) = move the camera to show all the scene
			setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
			// 2. Describe how to control the camera:
			// SHIFT + mouse left button = zoom on region
			setCameraMouseBinding((Scene.Modifier.SHIFT.ID | Scene.Button.LEFT.ID), Scene.MouseAction.ZOOM_ON_REGION);
			// CTRL + mouse left button = rotate camera
			setCameraMouseBinding((Scene.Button.LEFT.ID | Scene.Modifier.CTRL.ID), Scene.MouseAction.ROTATE);
			// Right button = translate camera
			setCameraMouseBinding(Scene.Button.RIGHT.ID, Scene.MouseAction.TRANSLATE);
			// Middle button = zoom
			setCameraMouseBinding(Scene.Button.MIDDLE.ID, Scene.MouseAction.ZOOM);
			// 3. Describe how to control the interactive frame:
			// Left button = rotate interactive frame
			setFrameMouseBinding(Scene.Button.LEFT.ID, Scene.MouseAction.ROTATE);
			// Left button = translate interactive frame
			setFrameMouseBinding(Scene.Button.RIGHT.ID, Scene.MouseAction.TRANSLATE);
			// Right button + SHIFT = screen translate interactive frame
			setFrameMouseBinding((Scene.Button.RIGHT.ID | Scene.Modifier.SHIFT.ID), Scene.MouseAction.SCREEN_TRANSLATE);
			// 4. Configure some click actions:
			// double click + button left = align frame with world
			setClickBinding(Scene.Button.LEFT, 2, ClickAction.ALIGN_FRAME);
			// single click + middle button + SHIFT + ALT = interpolate to show all the scene.
			setClickBinding((Scene.Modifier.SHIFT.ID | Scene.Modifier.ALT.ID), Scene.Button.MIDDLE, ClickAction.ZOOM_TO_FIT);
			// double click + button right = align camera with world
			setClickBinding(Scene.Button.RIGHT, 2, ClickAction.ALIGN_CAMERA);
		}
	}
}