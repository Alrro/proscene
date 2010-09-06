package remixlab.proscene;

import remixlab.proscene.Scene.CameraKeyboardAction;

public class FirstPersonCameraProfile extends CameraProfile {
	
	//enum KeyboardAction { INCREASE_FLYSPEED, DECREASE_FLYSPEED }

	public FirstPersonCameraProfile(Scene scn, String n) {
		super(scn, n);
		setDefaultShortcuts();
	}
	
	public void setDefaultShortcuts() {
		setShortcut('a', CameraKeyboardAction.SHOW_ALL);
	}
}
