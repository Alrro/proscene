package remixlab.proscene;

import java.util.HashMap;

public class GlobalProfile extends InteractionProfile {
	// S h o r t c u t k e y s
	protected HashMap<Scene.KeyboardAction, String> keyboardActionDescription;
	
	//Iterator<Character, KeyboardAction> it;

	public GlobalProfile(Scene scn) {
		super(scn);
		keyboardActionDescription = new HashMap<Scene.KeyboardAction, String>();		
		this.setDefaultShortcuts();
	}

	// Key bindings. 0 means not defined
	public void setDefaultShortcuts() {
		// D e f a u l t s h o r t c u t s
		setShortcut('a', Scene.KeyboardAction.DRAW_AXIS);
		keyboardActionDescription.put(Scene.KeyboardAction.DRAW_AXIS,
				"Toggles the display of the world axis");
		setShortcut('g', Scene.KeyboardAction.DRAW_GRID);
		keyboardActionDescription.put(Scene.KeyboardAction.DRAW_GRID,
				"Toggles the display of the XY grid");
		setShortcut(' ', Scene.KeyboardAction.CAMERA_MODE);
		keyboardActionDescription.put(Scene.KeyboardAction.CAMERA_MODE,
				"Cycles to the registered camera mode profiles");
		setShortcut('e', Scene.KeyboardAction.CAMERA_TYPE);
		keyboardActionDescription.put(Scene.KeyboardAction.CAMERA_TYPE,
				"Toggles camera type: orthographic or perspective");
		setShortcut('k', Scene.KeyboardAction.CAMERA_KIND);
		keyboardActionDescription.put(Scene.KeyboardAction.CAMERA_KIND,
				"Toggles camera kind: proscene or standard");
		// setShortcut(KeyboardAction.STEREO, ?);
		// setShortcut(KeyboardAction.ANIMATION, ?);
		setShortcut('h', Scene.KeyboardAction.HELP);
		keyboardActionDescription.put(Scene.KeyboardAction.HELP,
				"Toggles the display of the help");
		setShortcut('r', Scene.KeyboardAction.EDIT_CAMERA_PATH);
		keyboardActionDescription.put(Scene.KeyboardAction.EDIT_CAMERA_PATH,
				"Toggles the key frame camera paths (if any) for edition");
		setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVEFRAME);
		keyboardActionDescription
				.put(Scene.KeyboardAction.FOCUS_INTERACTIVEFRAME,
						"Toggle interactivity between camera and interactive frame (if any)");
		setShortcut('w', Scene.KeyboardAction.CONSTRAIN_FRAME);
		keyboardActionDescription.put(Scene.KeyboardAction.CONSTRAIN_FRAME,
				"Toggles on and off frame constraints (if any)");

		// K e y f r a m e s s h o r t c u t k e y s
		// setPathKey(Qt::Key_F1, 1);
		// setPathKey(Qt::Key_F2, 2);
		// setPathKey(Qt::Key_F3, 3);
		// setPathKey(Qt::Key_F4, 4);
		// setPathKey(Qt::Key_F5, 5);
		// setPathKey(Qt::Key_F6, 6);
		// setPathKey(Qt::Key_F7, 7);
		// setPathKey(Qt::Key_F8, 8);
		// setPathKey(Qt::Key_F9, 9);
		// setPathKey(Qt::Key_F10, 10);
		// setPathKey(Qt::Key_F11, 11);
		// setPathKey(Qt::Key_F12, 12);

		// setAddKeyFrameKeyboardModifiers(Qt::AltModifier);
		// setPlayPathKeyboardModifiers(Qt::NoModifier);
	}	

	
}
