package remixlab.proscene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.core.*;

public class Scene2D extends AbstractScene {
	/**
	 * Defines the different actions that can be associated with a specific
	 * keyboard key.
	 */
	public enum KeyboardAction {
		/** Toggles the display of the world axis. */
		DRAW_AXIS("Toggles the display of the world axis"),
		/** Toggles the display of the XY grid. */
		DRAW_GRID("Toggles the display of the XY grid"),
		/** Cycles to the registered camera profiles. */
		CAMERA_PROFILE("Cycles to the registered camera profiles"),		
		/** Toggles animation. */
		ANIMATION("Toggles animation"),		
		GLOBAL_HELP("Displays the global help"),
		/** Displays the current camera profile help. */
		CURRENT_CAMERA_PROFILE_HELP("Displays the current camera profile help"),
		/** Toggles the key frame camera paths (if any) for edition. */
		EDIT_CAMERA_PATH("Toggles the key frame camera paths (if any) for edition"),
		/** Toggle interactivity between camera and interactive frame (if any). */
		FOCUS_INTERACTIVE_FRAME("Toggle interactivity between camera and interactive frame (if any)"),
		/** Toggle interactive frame selection region drawing. */
		DRAW_FRAME_SELECTION_HINT("Toggle interactive frame selection region drawing"),
		/** Toggles on and off frame constraints (if any). */
		CONSTRAIN_FRAME("Toggles on and off frame constraints (if any)");
		
		private String description;
		
		KeyboardAction(String description) {
       this.description = description;
    }
    
    public String description() {
      return description;
    }
	}

	/**
	 * Defines the different camera actions that can be associated with a specific
	 * keyboard key. Actions are defined here, but bindings are defined at the CameraProfile level,
	 * i.e., the scene acts like a bridge between the CameraProfile and proscene low-level classes.
	 */
	public enum CameraKeyboardAction {
		/** Interpolate the camera to zoom on pixel. */
		INTERPOLATE_TO_ZOOM_ON_PIXEL("Interpolate the camera to zoom on pixel"),
		/** Interpolate the camera to fit the whole scene. */
		INTERPOLATE_TO_FIT_SCENE("Interpolate the camera to fit the whole scene"),
		/** Show the whole scene. */
		SHOW_ALL("Show the whole scene"),
		/** Move camera to the left. */
		MOVE_CAMERA_LEFT("Move camera to the left"),
		/** Move camera to the right. */
		MOVE_CAMERA_RIGHT("Move camera to the right"),
		/** Move camera up. */
		MOVE_CAMERA_UP("Move camera up"),
		/** Move camera down. */
		MOVE_CAMERA_DOWN("Move camera down"),
		/** Increase camera rotation sensitivity (only meaningful in arcball mode). */
		INCREASE_ROTATION_SENSITIVITY("Increase camera rotation sensitivity (only meaningful in arcball mode)"),
		/** Decrease camera rotation sensitivity (only meaningful in arcball mode). */
		DECREASE_ROTATION_SENSITIVITY("Decrease camera rotation sensitivity (only meaningful in arcball mode)"),
		/** Increase camera fly speed (only meaningful in first-person mode). */
		INCREASE_CAMERA_FLY_SPEED("Increase camera fly speed (only meaningful in first-person mode)"),
		/** Decrease camera fly speed (only meaningful in first-person mode). */
		DECREASE_CAMERA_FLY_SPEED("Decrease camera fly speed (only meaningful in first-person mode)"),
		/** Increase avatar fly speed (only meaningful in third-person mode). */
		INCREASE_AVATAR_FLY_SPEED("Increase avatar fly speed (only meaningful in third-person mode)"),
		/** Decrease avatar fly speed (only meaningful in third-person mode). */
		DECREASE_AVATAR_FLY_SPEED("Decrease avatar fly speed (only meaningful in third-person mode)"),
		/** Increase camera azymuth respect to the avatar (only meaningful in third-person mode). */
		INCREASE_AZYMUTH("Increase camera azymuth respect to the avatar (only meaningful in third-person mode)"),
		/** Decrease camera azymuth respect to the avatar (only meaningful in third-person mode). */
		DECREASE_AZYMUTH("Decrease camera azymuth respect to the avatar (only meaningful in third-person mode)"),
		/** Increase camera inclination respect to the avatar (only meaningful in third-person mode). */
		INCREASE_INCLINATION("Increase camera inclination respect to the avatar (only meaningful in third-person mode)"),
		/** Decrease camera inclination respect to the avatar (only meaningful in third-person mode). */
		DECREASE_INCLINATION("Decrease camera inclination respect to the avatar (only meaningful in third-person mode)"),
		/** Increase camera tracking distance respect to the avatar (only meaningful in third-person mode). */
		INCREASE_TRACKING_DISTANCE("Increase camera tracking distance respect to the avatar (only meaningful in third-person mode)"),
		/** Decrease camera tracking distance respect to the avatar (only meaningful in third-person mode). */
		DECREASE_TRACKING_DISTANCE("Decrease camera tracking distance respect to the avatar (only meaningful in third-person mode)");
		
		private String description;
		
		CameraKeyboardAction(String description) {
       this.description = description;
    }
		
    public String description() {
        return description;
    }
	}

	/**
	 * This enum defines mouse click actions to be binded to the mouse.
	 * Actions are defined here, but bindings are defined at the CameraProfile level,
	 * i.e., the scene acts like a bridge between the CameraProfile and proscene low-level classes.
	 */
	public enum ClickAction {
		/** No click action. */
		NO_CLICK_ACTION("No click action"),
		/** Zoom on pixel */
		ZOOM_ON_PIXEL("Zoom on pixel"),
		/** Zoom to fit the scene */
		ZOOM_TO_FIT("Zoom to fit the scene"),		
		/** Center frame */
		CENTER_FRAME("Center frame"),
		/** Center scene */
		CENTER_SCENE("Center scene"),
		/** Show the whole scene */
		SHOW_ALL("Show the whole scene"),
		/** Align interactive frame (if any) with world */
		ALIGN_FRAME("Align interactive frame (if any) with world"),
		/** Align camera with world */
		ALIGN_CAMERA("Align camera with world");

		private String description;
		
		ClickAction(String description) {
       this.description = description;
    }
		
    public String description() {
        return description;
    }
	}

	/**
	 * This enum defines mouse actions (click + drag) to be binded to the mouse.
	 * Actions are defined here, but bindings are defined at the CameraProfile level,
	 * i.e., the scene acts like a bridge between the CameraProfile and proscene low-level classes.
	 */
	public enum MouseAction {
		/** No mouse action. */
		NO_MOUSE_ACTION("No mouse action"),
		/** Rotate frame (camera or interactive frame. */
		ROTATE("Rotate frame (camera or interactive frame)"),
		/** Zoom. */
		ZOOM("Zoom"),
		/** Translate frame (camera or interactive frame). */
		TRANSLATE("Translate frame (camera or interactive frame)"),		
		/** Screen rotate (camera or interactive frame). */
		SCREEN_ROTATE("Screen rotate (camera or interactive frame)"),
		/** Roll frame (camera or interactive drivable frame). */
		ROLL("Roll frame (camera or interactive drivable frame)"),
		/** Drive (camera or interactive drivable frame). */
		DRIVE("Drive (camera or interactive drivable frame)"),
		/** Screen translate frame (camera or interactive frame). */
		SCREEN_TRANSLATE("Screen translate frame (camera or interactive frame)"),
		/** Zoom on region (camera or interactive drivable frame). */
		ZOOM_ON_REGION("Zoom on region (camera or interactive drivable frame)");		

		private String description;
		
		MouseAction(String description) {
       this.description = description;
    }
		
    public String description() {
        return description;
    }
	}	

	// S h o r t c u t k e y s
	protected Bindings<KeyboardShortcut, KeyboardAction> gProfile;
	
	/**
	 * Constructor that defines an on-screen Scene (the one that most likely
	 * would just fulfill all of your needs). All viewer parameters (display flags,
	 * scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation. This is actually just a convenience
	 * function that simply calls {@code this(p, (PGraphics) p.g)}. Call any
	 * other constructor by yourself to possibly define an off-screen Scene.
	 * 
	 * @see #Scene(PApplet, PGraphics)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */	
	public Scene2D(PApplet p) {
		this(p, p.g);
	}
	
	/**
	 * This constructor is typically used to define an off-screen Scene. This is
	 * accomplished simply by specifying a custom {@code renderer}, different
	 * from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. This
	 * is actually just a convenience function that simply calls
	 * {@code this(p, renderer, 0, 0)}. If you plan to define an on-screen Scene,
	 * call {@link #Scene(PApplet)} instead.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene2D(PApplet p, PGraphics renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * This constructor is typically used to define an off-screen Scene. This is
	 * accomplished simply by specifying a custom {@code renderer}, different
	 * from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. The
	 * {@code x} and {@code y} parameters define the position of the upper-left
	 * corner where the off-screen Scene is expected to be displayed, e.g., for
	 * instance with a call to the Processing built-in {@code image(img, x, y)}
	 * function. If {@link #isOffscreen()} returns {@code false} (i.e.,
	 * {@link #renderer()} equals the PApplet's renderer), the values of x and y
	 * are meaningless (both are set to 0 to be taken as dummy values). If you
	 * plan to define an on-screen Scene, call {@link #Scene(PApplet)} instead. 
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics)
	 */
	public Scene2D(PApplet p, PGraphics renderer, int x, int y) {
		parent = p;
		pg = renderer;
		width = pg.width;
		height = pg.height;
		
		space = Space.TWO_D;
				
		//event handler
		dE = new DesktopEvents(this);
		
		//mouse grabber pool
		MouseGrabberPool = new ArrayList<MouseGrabbable>();
		
		//devices
		devices = new ArrayList<HIDevice>();

		gProfile = new Bindings<KeyboardShortcut, KeyboardAction>(this);
		pathKeys = new Bindings<Integer, Integer>(this);		
		setDefaultShortcuts();

		avatarIsInteractiveDrivableFrame = false;// also init in setAvatar, but we
		// need it here to properly init the camera
		avatarIsInteractiveAvatarFrame = false;// also init in setAvatar, but we
		// need it here to properly init the camera
		cam = new Camera(this);
		setCamera(camera());//calls showAll();
		setInteractiveFrame(null);
		setAvatar(null);
		
  	// This scene is offscreen if the provided renderer is
		// different from the main PApplet renderer.
		offscreen = renderer != p.g;
		if(offscreen)
			upperLeftCorner = new Point(x, y);
		else
			upperLeftCorner = new Point(0, 0);
		beginOffScreenDrawingCalls = 0;		
		setMouseTracking(true);
		setMouseGrabber(null);
		
		mouseGrabberIsAnIFrame = false;

		initDefaultCameraProfiles();

		//animation		
		animationStarted = false;
		setFrameRate(60, false);
		setAnimationPeriod(1000/60, false); // 60Hz
		stopAnimation();				

		withConstraint = true;

		setAxisIsDrawn(true);
		setGridIsDrawn(true);
		setFrameSelectionHintIsDrawn(false);
		setCameraPathsAreDrawn(false);
		
		//disableFrustumEquationsUpdate();

		// E X C E P T I O N H A N D L I N G
		startCoordCalls = 0;

		parent.registerPre(this);
		parent.registerDraw(this);
		// parent.registerPost(this);
		enableKeyboardHandling();
		enableMouseHandling();		
		parseKeyXxxxMethods();
		parseMouseXxxxMethods();

		// register draw method
		removeDrawHandler();
	  // register animation method
		removeAnimationHandler();
		
		//setRightHanded();
		setLeftHanded();

		// called only once
		init();
	}
	
	public void pre() {
		
	}	

	@Override
	public void drawPath(List<Frame> path, int mask, int nbFrames, int nbSteps,	float scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAvatar(Trackable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultShortcuts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDefaultCameraProfiles() {
		cameraProfileMap = new HashMap<String, CameraProfile>();
		cameraProfileNames = new ArrayList<String>();
		currentCameraProfile = null;
		// register here the default profiles
		registerCameraProfile( new CameraProfile(this, "WHEELED_ARCBALL", CameraProfile.Mode.WHEELED_ARCBALL) );
		setCurrentCameraProfile("WHEELED_ARCBALL");		
	}

	@Override
	public boolean unregisterCameraProfile(String cp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setCurrentCameraProfile(String cp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void handleKeyboardAction(remixlab.proscene.Scene.KeyboardAction id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleCameraKeyboardAction(
			remixlab.proscene.Scene.CameraKeyboardAction id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleClickAction(remixlab.proscene.Scene.ClickAction action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginDraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGrid(float radius) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawAxis(float radius) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void displayVisualHints() {
		// TODO Auto-generated method stub
		
	}
}
