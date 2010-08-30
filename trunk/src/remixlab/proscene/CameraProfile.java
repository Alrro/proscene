package remixlab.proscene;

public class CameraProfile extends InteractionProfile {
	public enum ClickAction { NO_CLICK_ACTION, ZOOM_ON_PIXEL, ZOOM_TO_FIT, SELECT, RAP_FROM_PIXEL, RAP_IS_CENTER,
		CENTER_FRAME, CENTER_SCENE, SHOW_ENTIRE_SCENE, ALIGN_FRAME, ALIGN_CAMERA };
	
	public enum MouseAction { NO_MOUSE_ACTION,
		ROTATE, ZOOM, TRANSLATE,
		MOVE_FORWARD, LOOK_AROUND, MOVE_BACKWARD,
		SCREEN_ROTATE, ROLL, DRIVE,
		SCREEN_TRANSLATE, ZOOM_ON_REGION }
	
	//keyboard actions: MOVE_CAMERA_LEFT, MOVE_CAMERA_RIGHT, MOVE_CAMERA_UP, MOVE_CAMERA_DOWN
	
	public CameraProfile(Scene scn) {
		super(scn);
	}
}
