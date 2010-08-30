package remixlab.proscene;

import java.util.HashMap;

public class GlobalProfile extends InteractionProfile {
	/** Defines the different actions that can be associated with a keyboard shortcut using
	 *  {@link remixlab.proscene.Scene#setShortcut(KeyboardAction, Character)}.
	 */
	enum KeyboardAction { DRAW_AXIS, DRAW_GRID, CAMERA_MODE, CAMERA_TYPE, CAMERA_KIND,
		STEREO, ANIMATION, HELP, EDIT_CAMERA_PATH, FOCUS_INTERACTIVEFRAME, CONSTRAIN_FRAME }
	
	// S h o r t c u t   k e y s
	protected HashMap<KeyboardAction, String> keyboardActionDescription;
	protected HashMap<KeyboardAction, Character> keyboardBinding;
	
	public GlobalProfile(Scene scn) {
		super(scn);
		keyboardActionDescription = new HashMap<KeyboardAction, String>();
		keyboardBinding = new HashMap<KeyboardAction, Character>();
	}
	
	// Key bindings. 0 means not defined
	public void setDefaultShortcuts() {
		// D e f a u l t   s h o r t c u t s
		setShortcut(KeyboardAction.DRAW_AXIS, 'a');
		keyboardActionDescription.put(KeyboardAction.DRAW_AXIS, "Toggles the display of the world axis");
		setShortcut(KeyboardAction.DRAW_GRID, 'g');
		setShortcut(KeyboardAction.CAMERA_MODE, ' ');
		//setShortcut(KeyboardAction.STEREO, ?);
		//setShortcut(KeyboardAction.ANIMATION, ?);
		setShortcut(KeyboardAction.HELP, 'h');
		setShortcut(KeyboardAction.EDIT_CAMERA_PATH, 'r');
		
		
		
		

		keyboardActionDescription.put(KeyboardAction.DISPLAY_FPS] = tr("Toggles the display of the FPS", "DISPLAY_FPS action description");
		keyboardActionDescription[SAVE_SCREENSHOT] = tr("Saves a screenshot", "SAVE_SCREENSHOT action description");
		keyboardActionDescription[FULL_SCREEN] = tr("Toggles full screen display", "FULL_SCREEN action description");
		
		keyboardActionDescription[DRAW_GRID] = tr("Toggles the display of the XY grid", "DRAW_GRID action description");
		keyboardActionDescription[CAMERA_MODE] = tr("Changes camera mode (revolve or fly)", "CAMERA_MODE action description");
		keyboardActionDescription[STEREO] = tr("Toggles stereo display", "STEREO action description");
		keyboardActionDescription[HELP] = tr("Opens this help window", "HELP action description");
		keyboardActionDescription[ANIMATION] = tr("Starts/stops the animation", "ANIMATION action description");
		keyboardActionDescription[EDIT_CAMERA] = tr("Toggles camera paths display", "EDIT_CAMERA action description"); // TODO change
		keyboardActionDescription[ENABLE_TEXT] = tr("Toggles the display of the text", "ENABLE_TEXT action description");
		keyboardActionDescription[EXIT_VIEWER] = tr("Exits program", "EXIT_VIEWER action description");
		keyboardActionDescription[MOVE_CAMERA_LEFT] = tr("Moves camera left", "MOVE_CAMERA_LEFT action description");
		keyboardActionDescription[MOVE_CAMERA_RIGHT] = tr("Moves camera right", "MOVE_CAMERA_RIGHT action description");
		keyboardActionDescription[MOVE_CAMERA_UP] = tr("Moves camera up", "MOVE_CAMERA_UP action description");
		keyboardActionDescription[MOVE_CAMERA_DOWN] = tr("Moves camera down", "MOVE_CAMERA_DOWN action description");
		keyboardActionDescription[INCREASE_FLYSPEED] = tr("Increases fly speed", "INCREASE_FLYSPEED action description");
		keyboardActionDescription[DECREASE_FLYSPEED] = tr("Decreases fly speed", "DECREASE_FLYSPEED action description");
		keyboardActionDescription[SNAPSHOT_TO_CLIPBOARD] = tr("Copies a snapshot to clipboard", "SNAPSHOT_TO_CLIPBOARD action description");

		// K e y f r a m e s   s h o r t c u t   k e y s
		//setPathKey(Qt::Key_F1,   1);
		//setPathKey(Qt::Key_F2,   2);
		//setPathKey(Qt::Key_F3,   3);
		//setPathKey(Qt::Key_F4,   4);
		//setPathKey(Qt::Key_F5,   5);
		//setPathKey(Qt::Key_F6,   6);
		//setPathKey(Qt::Key_F7,   7);
		//setPathKey(Qt::Key_F8,   8);
		//setPathKey(Qt::Key_F9,   9);
		//setPathKey(Qt::Key_F10, 10);
		//setPathKey(Qt::Key_F11, 11);
		//setPathKey(Qt::Key_F12, 12);
	   
		//setAddKeyFrameKeyboardModifiers(Qt::AltModifier);
		//setPlayPathKeyboardModifiers(Qt::NoModifier);	
	}
	
	//removes handling
	public void setShortcut(KeyboardAction action) {
		setShortcut(action, null);
	}
	
	/** Defines the shortcut() that triggers a given KeyboardAction.

	Here are some examples:
	\code
	// Press 'Q' to exit application
	setShortcut(EXIT_VIEWER, Qt::Key_Q);

	// Alt+M toggles camera mode
	setShortcut(CAMERA_MODE, Qt::ALT+Qt::Key_M);

	// The DISPLAY_FPS action is disabled
	setShortcut(DISPLAY_FPS, 0);
	\endcode

	Only one shortcut can be assigned to a given QGLViewer::KeyboardAction (new bindings replace
	previous ones). If several KeyboardAction are binded to the same shortcut, only one of them is
	active. */
	public void setShortcut(KeyboardAction action, Character pChar) {
		if (pChar != null)
			keyboardBinding.put(action, pChar);
		else
			keyboardBinding.remove(action);
	}
	
	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no Character is defined for keyboard {@code action}).
	 */
	public Character shortcut(KeyboardAction action) {
		return keyboardBinding.get(action);
	}
	
	protected void handleKeyboardAction(KeyboardAction id) {
		/**
		enum KeyboardAction { DRAW_AXIS, DRAW_GRID, CAMERA_MODE, CAMERA_TYPE, CAMERA_KIND, STEREO, ANIMATION, HELP,
		EDIT_CAMERA_PATH, FOCUS_INTERACTIVEFRAME, CONSTRAIN_FRAME 
	    }
		*/
		
		switch (id)	{
		case DRAW_AXIS :		scene.toggleAxisIsDrawn(); break;
		case DRAW_GRID :		scene.toggleGridIsDrawn(); break;		
		case CAMERA_MODE :		scene.nextCameraMode();	break;
		case CAMERA_TYPE : scene.toggleCameraType(); break;
		case CAMERA_KIND : scene.toggleCameraKind(); break;
		case STEREO :			/**toggleStereoDisplay();*/ break;
		case ANIMATION :		/**toggleAnimation();*/ break;
		case HELP :			    scene.toggleHelpIsDrawn(); break;
		case EDIT_CAMERA_PATH :	scene.toggleCameraPathsAreDrawn(); break;
		case FOCUS_INTERACTIVEFRAME : scene.toggleDrawInteractiveFrame(); break;
		case CONSTRAIN_FRAME : scene.toggleDrawInteractiveFrame(); break;
	}
}
}
