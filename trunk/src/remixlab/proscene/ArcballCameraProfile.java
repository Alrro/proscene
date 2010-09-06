package remixlab.proscene;

import remixlab.proscene.Scene.CameraKeyboardAction;

public class ArcballCameraProfile extends CameraProfile {
	/**
	enum KeyboardAction { INCREASE_ROTATION_SENSITIVITY, DECREASE_ROTATION_SENSITIVITY 
	}
	*/

	public ArcballCameraProfile(Scene scn, String n) {
		super(scn, n);
		setDefaultShortcuts();
	}
	
    //
	
	public void setDefaultShortcuts() {
		setShortcut('a', CameraKeyboardAction.MOVE_CAMERA_LEFT);
	}
}
