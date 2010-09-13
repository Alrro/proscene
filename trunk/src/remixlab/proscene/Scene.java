/**
 *                     ProScene (version 1.0.0-alpha1)      
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

package remixlab.proscene;

import processing.core.*;

import java.awt.Point;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Timer;

/**
 * A processing 3D interactive scene. A Scene provides a default interactivity for your scene through
 * the mouse and keyboard in the hope that it should fit most user needs. For those users whose needs
 * are not completely fulfill by default, proscene main interactivity mechanisms can easily be extended
 * to fit them.
 * <p>
 * A Scene has a full reach Camera, an two means to manipulate objects: an {@link #interactiveFrame()}
 * single instance (which by default is null) and a {@link #mouseGrabber()} pool.
 * <p>
 * To use a Scene, you have three choices:
 * <ol>
 * <li> <b>Direct instantiation</b>. In this case you should instantiate your own Scene object at the
 * {@code PApplet.setup()} function. 
 * <li> <b>Inheritance</b>. In this case, once you declare a Scene derived class, you should implement
 * {@link #proscenium()} which defines the objects in your scene. Just make sure to define the
 * {@code PApplet.draw()} method, even if it's empty.
 * <li> <b>External draw handler registration</b>. You can even declare an external drawing method and then
 * register it at the Scene with {@link #addDrawHandler(Object, String)}. That method should return
 * {@code void} and have one single {@code Scene} or {@code PApplet} parameter. This strategy may be useful
 * when there are multiple viewers sharing the same drawing code.
 * </ol>
 * <p>
 * See the examples <i>BasicUse</i>, <i>AlternativeUse</i> and <i>StandardCamera</i> for an illustration
 * of these techniques.
 * <p>
 * <b>Attention:</b> To set the PApplet's background you should call one of the {@code Scene.background()}
 * versions instead of any of the {@code PApplet.background()} ones. The background is set to black by default.
 */
public class Scene implements PConstants {
	/**
	 * Defines the different actions that can be associated with a specific keyboard key.
	 */	
	public enum GlobalKeyboardAction {
		DRAW_AXIS, DRAW_GRID, CAMERA_MODE, CAMERA_TYPE, CAMERA_KIND,
		/**	STEREO, */		
		ARP_FROM_PIXEL, RESET_ARP,		
		HELP, EDIT_CAMERA_PATH, FOCUS_INTERACTIVE_FRAME, DRAW_FRAME_SELECTION_HINT, CONSTRAIN_FRAME
	}
	/**
	 * Defines the different actions that can be associated with a specific keyboard key.
	 */	
	public enum CameraKeyboardAction {	
		INTERPOLATE_TO_ZOOM_ON_PIXEL, INTERPOLATE_TO_FIT_SCENE, SHOW_ALL,
		/**	ANIMATION, */
		/**	AZIMUTH, INCLINATION, TRACKING_DISTANCE, */		
		MOVE_CAMERA_LEFT, MOVE_CAMERA_RIGHT, MOVE_CAMERA_UP, MOVE_CAMERA_DOWN,
		INCREASE_ROTATION_SENSITIVITY, DECREASE_ROTATION_SENSITIVITY,
		INCREASE_CAMERA_FLY_SPEED, DECREASE_CAMERA_FLY_SPEED,
		INCREASE_AVATAR_FLY_SPEED, DECREASE_AVATAR_FLY_SPEED,
		INCREASE_AZYMUTH, DECREASE_AZYMUTH,
		INCREASE_INCLINATION, DECREASE_INCLINATION,
		INCREASE_TRACKING_DISTANCE, DECREASE_TRACKING_DISTANCE
	}
	
	public enum ClickAction { NO_CLICK_ACTION, ZOOM_ON_PIXEL, ZOOM_TO_FIT, /**SELECT,*/ ARP_FROM_PIXEL, RESET_ARP,
		CENTER_FRAME, CENTER_SCENE, SHOW_ALL, ALIGN_FRAME, ALIGN_CAMERA }
	
	/**
	 * This enum defines mouse actions to be binded to the mouse.
	 */
	public enum MouseAction { NO_MOUSE_ACTION,
		ROTATE, ZOOM, TRANSLATE,
		MOVE_FORWARD, LOOK_AROUND, MOVE_BACKWARD,
		SCREEN_ROTATE, ROLL, DRIVE,
		SCREEN_TRANSLATE, ZOOM_ON_REGION }
	//TODO: add descriptions to all atomic actions
	
	protected MouseAction camMouseAction;
	
	//protected MouseAction iFrameMouseAction;
	
	public enum Button { LEFT, MIDDLE, RIGHT }
	
	public enum Arrow { UP, DOWN, LEFT, RIGHT }
	
	public enum Modifier { ALT, SHIFT, CONTROL, ALT_GRAPH }	
	
	/**
	 * This enum defines the papplet background mode which should be set by proscene.
	 */
	public enum BackgroundMode {
		RGB, RGB_ALPHA, GRAY, GRAY_ALPHA, XYZ, XYZ_ALPHA, PIMAGE
	}
	
	// K E Y F R A M E S
	protected ShortcutMappings<KeyboardShortcut, Integer> pathKeys;
	protected Modifier addKeyFrameKeyboardModifier;
	protected Modifier deleteKeyFrameKeyboardModifier;
	
	// S h o r t c u t k e y s
	protected ShortcutMappings<KeyboardShortcut, GlobalKeyboardAction> gProfile;	
	protected HashMap<GlobalKeyboardAction, String> keyboardActionDescription;
	
	// C L I C K   A C T I O N S
	protected ShortcutMappings<ClickShortcut, ClickAction> clickActions;
	
	// c a m e r a   p r o f i l e s
	private HashMap<String, CameraProfile> cameraProfileMap;
	private ArrayList<String> cameraProfileNames;
	private CameraProfile currentCameraProfile;
	
	//mouse actions
	boolean arpFlag;
	boolean pupFlag;
	PVector pupVec;
	
	//background
	private BackgroundMode backgroundMode;
	private boolean enableBackground;
	int rgb;
	float gray, alpha, x, y, z;	
	PImage image;
	
	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S
	public PApplet parent;
	public PGraphics3D pg3d;
	int width, height;//size
	
	// O B J E C T S
	protected Camera cam;
	protected InteractiveFrame glIFrame;
	boolean interactiveFrameIsACam;
	// boolean interactiveFrameIsAnAvatar;
	boolean iFrameIsDrwn;
	protected Trackable trck;
	boolean avatarIsInteractiveDrivableFrame;
	boolean avatarIsInteractiveAvatarFrame;
	
	// S C R E E N   C O O R D I N A T E S
	float halfWidthSpace;
	float halfHeightSpace;
	float zC;
	
	// Z O O M _ O N _ R E G I O N
	Point fCorner;//also used for SCREEN_ROTATE
	Point lCorner;
	
	// R E V O L V E   A R O U N D   P O I N T
	private Timer utilityTimer;
    private ActionListener taskTimerPerformer;
    
    // E X C E P T I O N   H A N D L I N G
    protected int startCoordCalls;
	
	// M o u s e   G r a b b e r
    protected List<MouseGrabber> MouseGrabberPool;
	MouseGrabber mouseGrbbr;
	boolean mouseGrabberIsAnIFrame;
	boolean mouseGrabberIsAnICamFrame;
	
	// F r u s t u m   p l a n e   c o e f f i c i e n t s
	protected boolean fpCoefficientsUpdate;	
	
	// D I S P L A Y   F L A G S
	boolean axisIsDrwn;	// world axis
	boolean gridIsDrwn;	// world XY grid
	boolean frameSelectionHintIsDrwn;
	boolean cameraPathsAreDrwn;
	
	// C O N S T R A I N T S
	boolean withConstraint;
	
	// K E Y B O A R D   A N D   M O U S E
	protected boolean mouseHandling;
	protected boolean keyboardHandling;	
	
	// O N L I N E   H E L P
	
	
	// T H I R D   P E R S O N	
	//private PVector target;
	
	// R E G I S T E R   D R A W   M E T H O D
	/** Draw handler is called on this or on the papplet*/
	protected Boolean drawHandlerParamIsThis;
	/** The object to handle the draw event */
	protected Object drawHandlerObject;
	/** The method in drawHandlerObject to execute */
	protected Method drawHandlerMethod;
	/** the name of the method to handle the event */ 
	protected String drawHandlerMethodName;
	
	/**
	 * All viewer parameters (display flags, scene parameters, associated objects...) are set to their default values.
	 * The PApplet background is set to black. See the associated documentation. 
	 */
	public Scene(PApplet p) {
		parent = p;
		pg3d = (PGraphics3D) parent.g;
		width = parent.width;
		height = parent.height;
		
		gProfile = new ShortcutMappings<KeyboardShortcut, GlobalKeyboardAction>(this);
		pathKeys = new ShortcutMappings<KeyboardShortcut, Integer>(this);
		clickActions = new ShortcutMappings<ClickShortcut, ClickAction>(this);
		
		setActionDescriptions();
		setDefaultShortcuts();
		setDefaultClickActions();
		
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		//iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		initDefaultCameraProfiles();
		
		MouseGrabberPool = new ArrayList<MouseGrabber>();
		
		arpFlag = false;
		pupFlag = false;
		
		avatarIsInteractiveDrivableFrame = false;//also init in setAvatar, but we need it here to properly init the camera 
		avatarIsInteractiveAvatarFrame = false;//also init in setAvatar, but we need it here to properly init the camera
		
		cam = new Camera(this);
		setCamera(camera());
		
		setInteractiveFrame(null);
		setAvatar(null);	
		
		setMouseGrabber(null);
		mouseGrabberIsAnIFrame = false;
		mouseGrabberIsAnICamFrame = false;
		
		withConstraint = true;
		
		//showAll();It is set in setCamera()
		
		setAxisIsDrawn(true);
		setGridIsDrawn(true);	    
		setFrameSelectionHintIsDrawn(false);
		setCameraPathsAreDrawn(false);
		
		taskTimerPerformer = new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		unSetTimerFlag();
        	}
        };
        utilityTimer = new Timer(1000, taskTimerPerformer);
        utilityTimer.setRepeats(false);
        
        disableFrustumEquationsUpdate();
        
        // E X C E P T I O N   H A N D L I N G
        startCoordCalls = 0;       
        
        enableBackgroundHanddling();
        image = null;
        background(0);
        parent.registerPre(this);
        parent.registerDraw(this);
        //parent.registerPost(this);
        enableKeyboardHandling();
        enableMouseHandling();
		parseKeyXxxxMethods();
		parseMouseXxxxMethods();
		
		//register draw method
		drawHandlerParamIsThis = null;
		drawHandlerObject = null;
		drawHandlerMethod = null; 
		drawHandlerMethodName = null;
		
		//called only once
		init();
	}	
	
	// 1. Associated objects
	
	public List<MouseGrabber> mouseGrabberPool() {
		return MouseGrabberPool;
	}
		
	/**
	 * Returns the associated Camera, never {@code null}.
	 */
	public Camera camera() {
		return cam;
	}
	
	public ShortcutMappings<KeyboardShortcut, GlobalKeyboardAction> keyBindings() {
		return gProfile;
	}
	
	/**
	 * Replaces the current {@link #camera()} with {@code camera}
	 */
	public void setCamera (Camera camera) {
		if (camera == null)
			return;
		
		camera.setSceneRadius(radius());
		camera.setSceneCenter(center());		
		
		camera.setScreenWidthAndHeight(parent.width, parent.height);
		
		cam = camera;		
		
		showAll();
	}
	
	/**
	 * Returns the InteractiveFrame associated to this Scene. It could be
	 * null if there's no InteractiveFrame associated to this Scene. 
	 * 
	 * @see #setInteractiveFrame(InteractiveFrame)
	 */
	public InteractiveFrame interactiveFrame() {
		return glIFrame;
	}
	
	/**
	 * Returns the avatar object to be tracked by the Camera when {@link #cameraMode()} is
	 * THIRD_PERSON. Simply returns {@code null} if no avatar has been set.
	 */
	public Trackable avatar() {
		return trck;
	}
	
	/**
	 * Sets the avatar object to be tracked by the Camera when {@link #cameraMode()} is
	 * THIRD_PERSON. 
	 */
	public void setAvatar(Trackable t) {
		trck = t;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveDrivableFrame = false;
		if (avatar() instanceof InteractiveAvatarFrame) {
			avatarIsInteractiveAvatarFrame = true;
			avatarIsInteractiveDrivableFrame = true;
			/**
			//TODO !!! really necessary? I think no :)
			 * <b>Attention: </b> If {@code t} is an instance of the InteractiveAvatarFrame class,
			 * the {@link remixlab.proscene.InteractiveAvatarFrame#trackingDistance()} is set to
			 * {@link #radius()}/3.
			if ( ((InteractiveAvatarFrame)avatar()).trackingDistance() == 0 )
				((InteractiveAvatarFrame)avatar()).setTrackingDistance(radius()/3);
		    */
			if ( interactiveFrame() != null )
				((InteractiveDrivableFrame)interactiveFrame()).setFlySpeed(0.01f * radius());				
		}
		else
			if (avatar() instanceof InteractiveDrivableFrame) {
				avatarIsInteractiveAvatarFrame = false;
				avatarIsInteractiveDrivableFrame = true;
				if ( interactiveFrame() != null )
					((InteractiveDrivableFrame)interactiveFrame()).setFlySpeed(0.01f * radius());
		}
	}
	
	public void unsetAvatar() {
		trck = null;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveDrivableFrame = false;
	}
	
	/**
	 * Sets {@code frame} as the InteractiveFrame associated to this Scene. If {@code frame}
	 * is instance of Trackable it is also automatically set as the Scene {@link #avatar()}
	 * (by automatically calling {@code setAvatar((Trackable) frame)}).
	 * 
	 * @see #interactiveFrame()
	 * @see #setAvatar(Trackable)
	 */
	public void setInteractiveFrame(InteractiveFrame frame) {
		glIFrame = frame;               
        interactiveFrameIsACam = ((interactiveFrame() != camera().frame()) && (interactiveFrame() instanceof InteractiveCameraFrame));
        if (glIFrame == null)
        	iFrameIsDrwn = false;
        else
        	if ( glIFrame instanceof Trackable )
        		setAvatar((Trackable)glIFrame);
	}
	
	/**
	 * Returns the current MouseGrabber, or {@code null} if none currently grabs mouse events.
	 * <p> 
	 * When {@link remixlab.proscene.MouseGrabber#grabsMouse()}, the different mouse events are sent to it
	 * instead of their usual targets ({@link #camera()} or {@link #interactiveFrame()}).
	 */
	public MouseGrabber mouseGrabber() {
		return mouseGrbbr;
	}
	
	/**
	 * Directly defines the {@link #mouseGrabber()}.
	 * <p> 
	 * You should not call this method directly as it bypasses the 
	 * {@link remixlab.proscene.MouseGrabber#checkIfGrabsMouse(int, int, Camera)}
	 * test performed by {@link #mouseMoved(MouseEvent)}.
	 */
    protected void setMouseGrabber(MouseGrabber mouseGrabber) {
		mouseGrbbr = mouseGrabber;
		
		mouseGrabberIsAnIFrame = mouseGrabber instanceof InteractiveFrame;
		mouseGrabberIsAnICamFrame = (( mouseGrabber != camera().frame()) &&
			                         ( mouseGrabber instanceof InteractiveCameraFrame ));		
	}
	
	// 2. State of the viewer
    
    /**
	 * Enables background handling in the Scene (see the different {@code background} functions),
	 * otherwise the background should be set with the corresponding PApplet functions.
	 * 
	 * @see #toggleBackgroundHanddling()
	 * @see #backgroundIsHandled()
	 */
    public void enableBackgroundHanddling() {
    	enableBackground = true;
    }
    
    /**
	 * Disables background handling by the Scene. Hence the background should be set with the
	 * corresponding PApplet functions.
	 * 
	 * @see #toggleBackgroundHanddling()
	 * @see #backgroundIsHandled()
	 */
    public void disableBackgroundHanddling() {
    	enableBackground = false;
    }
    
    /**
     * Returns {@code true} if the background is handled by the Scene and {@code false} otherwise.
     * 
     * @see #enableBackgroundHanddling()
	 * @see #disableBackgroundHanddling()
     */ 
    public boolean backgroundIsHandled() {
    	return enableBackground;
    }
    
	/**
	 * Toggles the state of the {@link #backgroundIsHandled()}.
	 * 
	 * @see #enableBackgroundHanddling()
	 * @see #disableBackgroundHanddling()
	 */
    public void toggleBackgroundHanddling() {
    	if(backgroundIsHandled())
    		disableBackgroundHanddling();
    	else
    		enableBackgroundHanddling();
    }
    
    /**
     * Internal use only. Call the proper PApplet background function at the beginning of
     * {@link #pre()}.
     * 
     * @see #pre()
     * @see #background(float)
     * @see #background(int)
     * @see #background(PImage)
     * @see #background(float, float)
     * @see #background(int, float)
     * @see #background(float, float, float)
     * @see #background(float, float, float, float)
     */
    protected void setBackground() {
    	switch (backgroundMode) {
		case RGB :
			parent.background(rgb);
			break;
	    case RGB_ALPHA :
	    	parent.background(rgb,alpha);
	    	break;
	    case GRAY :
	    	parent.background(gray);
	    	break;
	    case GRAY_ALPHA :
	    	parent.background(gray,alpha);
	    	break;
	    case XYZ :
	    	parent.background(x,y,z);
	    	break;
	    case XYZ_ALPHA :
	    	parent.background(x,y,z,alpha);
	    	break;
	    case PIMAGE :
	    	parent.background(image);
	    	break;
	    }
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(int my_rgb) {
    	rgb = my_rgb;
    	backgroundMode = BackgroundMode.RGB;
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(int my_rgb, float my_alpha) {
    	rgb = my_rgb;
    	alpha = my_alpha;
    	backgroundMode = BackgroundMode.RGB_ALPHA;
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(float my_gray) {
    	gray = my_gray;
    	backgroundMode = BackgroundMode.GRAY;
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(float my_gray, float my_alpha) {
    	gray = my_gray;
    	alpha = my_alpha;
    	backgroundMode = BackgroundMode.GRAY_ALPHA;
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(float my_x, float my_y, float my_z) {
    	x = my_x;
    	y = my_y;
    	z = my_z;
    	backgroundMode = BackgroundMode.XYZ;
    }
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the color used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window. 
     */
    public void background(float my_x, float my_y, float my_z, float my_a) {
    	x = my_x;
    	y = my_y;
    	z = my_z;
    	alpha = my_a;
    	backgroundMode = BackgroundMode.XYZ_ALPHA;
    }    
    
    /**
     * Wrapper function for the {@code PApplet.background()} function with the same signature.
     * Sets the PImage used for the background of the Processing window. The default background
     * is black. See the processing documentation for details.
     * <p>
     * The {@code PApplet.background()} is automatically called at the beginning of the
     * {@link #pre()} method (Hence you can call this function from where ever you want) and
     * is used to clear the display window.
     * <p>
     * <b>Attention:</b> If the sizes of the {@code img} and the PApplet differ, the {@code img}
     * will be resized to accommodate the size of the PApplet. 
     */
    public void background(PImage img) {    	
    	image = img;
    	if ( ( image.width != parent.width ) || ( image.height != parent.height ) )
    		image.resize(parent.width, parent.height); 	
    	backgroundMode = BackgroundMode.PIMAGE;
    }
    
    /**
     * Returns the background image if any.
     * @return image
     */
    public PImage backgroundImage() {
    	return image;
    }
    
    /**
     * Returns {@code true} if automatic update of the camera frustum plane equations is enabled and
     * {@code false} otherwise. Computation of the equations is expensive and hence is disabled by default.
     * 
     * @see #toggleFrustumEquationsUpdate()
	 * @see #disableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate(boolean)
	 * @see remixlab.proscene.Camera#updateFrustumEquations()
     */
    public boolean frustumEquationsUpdateIsEnable() {
    	return fpCoefficientsUpdate; 
    }
    
    /**
	 * Toggles automatic update of the camera frustum plane equations every frame. Computation of the equations
	 * is expensive and hence is disabled by default.
	 * 
	 * @see #frustumEquationsUpdateIsEnable()
	 * @see #disableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate(boolean)
	 * @see remixlab.proscene.Camera#updateFrustumEquations()
     */
    public void toggleFrustumEquationsUpdate() {
    	fpCoefficientsUpdate = !fpCoefficientsUpdate;
    }
    
    /**
	 * Disables automatic update of the camera frustum plane equations every frame. Computation of the equations
	 * is expensive and hence is disabled by default.
	 * 
	 * @see #frustumEquationsUpdateIsEnable()
	 * @see #toggleFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate(boolean)
	 * @see remixlab.proscene.Camera#updateFrustumEquations()
     */
    public void disableFrustumEquationsUpdate() {
    	enableFrustumEquationsUpdate(false);
    }  
    
    /**
	 * Enables automatic update of the camera frustum plane equations every frame. Computation of the equations
	 * is expensive and hence is disabled by default.
	 * 
	 * @see #frustumEquationsUpdateIsEnable()
	 * @see #toggleFrustumEquationsUpdate()
	 * @see #disableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate(boolean)
	 * @see remixlab.proscene.Camera#updateFrustumEquations()
     */
    public void enableFrustumEquationsUpdate() {
    	enableFrustumEquationsUpdate(true);
    }
    
    /**
	 * Enables or disables automatic update of the camera frustum plane equations every frame according to {@code flag}.
	 * Computation of the equations is expensive and hence is disabled by default.
	 * 
	 * @see #frustumEquationsUpdateIsEnable()
	 * @see #toggleFrustumEquationsUpdate()
	 * @see #disableFrustumEquationsUpdate()
	 * @see #enableFrustumEquationsUpdate()
	 * @see remixlab.proscene.Camera#updateFrustumEquations()
     */
    public void enableFrustumEquationsUpdate(boolean flag) {
    	fpCoefficientsUpdate = flag;
    }
    	
	/**
	 * Toggles the state of {@link #axisIsDrawn()}.
	 * 
	 * @see #axisIsDrawn()
	 * @see #setAxisIsDrawn(boolean)
	 */
	public void toggleAxisIsDrawn() {
		setAxisIsDrawn(!axisIsDrawn());
	}
	
	/**
	 * Toggles the state of {@link #gridIsDrawn()}.
	 * 
	 * @see #setGridIsDrawn(boolean)
	 */
	public void toggleGridIsDrawn() {
		setGridIsDrawn(!gridIsDrawn());
	}
	
	/**
	 * Toggles the state of {@link #frameSelectionHintIsDrawn()}.
	 * 
	 * @see #setFrameSelectionHintIsDrawn(boolean)
	 */
	public void toggleFrameSelectionHintIsDrawn() {
		setFrameSelectionHintIsDrawn(!frameSelectionHintIsDrawn());
	}
	
	/**
	 * Toggles the state of {@link #cameraPathsAreDrawn()}.
	 * 
	 * @see #setCameraPathsAreDrawn(boolean)
	 */
	public void toggleCameraPathsAreDrawn() {
		setCameraPathsAreDrawn(!cameraPathsAreDrawn());
	}
	
	/**
	 * Toggles the {@link #camera()} type between PERSPECTIVE and ORTHOGRAPHIC.
	 */
	public void toggleCameraType() {
		if ( camera().type() == Camera.Type.PERSPECTIVE )
			setCameraType(Camera.Type.ORTHOGRAPHIC);
		else
			setCameraType(Camera.Type.PERSPECTIVE);		
	}
	
	/**
	 * Toggles the {@link #camera()} kind between PROSCENE and STANDARD.
	 */
	public void toggleCameraKind() {
		if ( camera().kind() == Camera.Kind.PROSCENE )
			setCameraKind(Camera.Kind.STANDARD);
		else
			setCameraKind(Camera.Kind.PROSCENE);		
	}
	
	/**
	 * Toggles the {@link #interactiveFrame()} interactivity on and off.
	 */
	public void toggleDrawInteractiveFrame() {
		if ( interactiveFrameIsDrawn() )
			setDrawInteractiveFrame(false);
		else
			setDrawInteractiveFrame(true);
	}
	
	/**
	 * Toggles the draw with constraint on and off.	
	 */
	public void toggleDrawWithConstraint() {
		if ( drawIsConstrained() )
			setDrawWithConstraint(false);
		else
			setDrawWithConstraint(true);
	}	
	
	// 3. Drawing methods
	
	/**
	 * This method is called before the first drawing and should be overloaded to initialize stuff
	 * not initialized in {@code PApplet.setup()}. The default implementation is empty.
	 * <p>
	 * Typical usage include {@link #camera()} initialization ({@link #showAll()}) and
	 * Scene state setup ({@link #setAxisIsDrawn(boolean)}, {@link #setGridIsDrawn(boolean)}
	 * {@link #setHelpIsDrawn(boolean)}).
	 */
	public void init() {}
	
	/**
	 * Internal use. Display various visual hints to be called from {@link #pre()} or {@link #draw()}
	 * depending on the {@link #backgroundIsHandled()} state.
	 */
	private void displayVisualHints() {
		if (gridIsDrawn()) drawGrid(camera().sceneRadius());
		if (axisIsDrawn()) drawAxis(camera().sceneRadius());
		if (frameSelectionHintIsDrawn()) drawSelectionHints();
		if (cameraPathsAreDrawn()) {
			camera().drawAllPaths();
			drawCameraPathSelectionHints();
		} else {
			camera().hideAllPaths();
		}		
		
		if( camMouseAction == MouseAction.ZOOM_ON_REGION ) drawZoomWindowHint();
		if( camMouseAction == MouseAction.SCREEN_ROTATE ) drawScreenRotateLineHint();
		if( arpFlag ) drawArcballReferencePointHint();
		if( pupFlag ) {
			PVector v = camera().projectedCoordinatesOf( pupVec );
			drawCross( v.x, v.y );
		}
	}
	
	/**
	 * Paint method which is called just before your {@code PApplet.draw()} method. This method
	 * is registered at the PApplet and hence you don't need to call it.
	 * <p>
     * First sets the background (see {@link #setBackground()}) and then sets the processing
     * camera parameters from {@link #camera()} and displays axis, grid, interactive frames'
     * selection hints and camera paths, accordingly to user flags.
     */
	public void pre() {
    	//handle possible resize events
    	//weird: we need to bypass the handling of a resize event when running the applet from eclipse
    	if ( (parent.frame != null) && (parent.frame.isResizable()) ) {
    		if ( backgroundIsHandled() && (backgroundMode == BackgroundMode.PIMAGE) )
    			this.background( this.image );    		
    		if( (width != parent.width) || (height != parent.height) ) {
    			width = parent.width;
    			height = parent.height;
    			//weirdly enough we need to bypass what processing does
    			//to the matrices when a resize event takes place
    			camera().detachFromPCamera();    			
    			camera().setScreenWidthAndHeight(width, height);
    			camera().attachToPCamera();
    		}
    		else {    			
    			if( ( currentCameraProfile() instanceof ThirdPersonCameraProfile ) && ( !camera().anyInterpolationIsStarted() ) ) {
        			camera().setPosition( avatar().cameraPosition() );
        			camera().setUpVector( avatar().upVector() );
        			camera().lookAt( avatar().target() );
        		}
        		//We set the processing camera matrices from our remixlab.proscene.Camera
        		setPProjectionMatrix();        	
        		setPModelViewMatrix();
    			//camera().computeProjectionMatrix();
        		//camera().computeModelViewMatrix();
    		}
    	}
    	else {    		
    		if( ( currentCameraProfile() instanceof ThirdPersonCameraProfile ) && ( !camera().anyInterpolationIsStarted() ) ) {
    			camera().setPosition( avatar().cameraPosition() );
    			camera().setUpVector( avatar().upVector() );
    			camera().lookAt( avatar().target() );
    		} 			
    		//We set the processing camera matrices from our remixlab.proscene.Camera
    		setPProjectionMatrix();    		
    		setPModelViewMatrix();
    		//same as the two previous lines:
    		//WARNING: this can produce visual artifacts when using OPENGL and GLGRAPHICS renderers because
    		//processing will anyway set the matrices at the end of the rendering loop.
    		//camera().computeProjectionMatrix();
    		//camera().computeModelViewMatrix();
		}
		
		if( frustumEquationsUpdateIsEnable() )
			camera().updateFrustumEquations();
		
		if( backgroundIsHandled() ) {
			setBackground();
			displayVisualHints();
		}
	}
	
	/**
	 * Paint method which is called just after your {@code PApplet.draw()} method. This method
	 * is registered at the PApplet and hence you don't need to call it.
	 * <p>
	 * First calls {@link #proscenium()} which is the main drawing method that could be overloaded.
	 * then, if there's an additional drawing method registered at the Scene, calls it
	 * (see {@link #addDrawHandler(Object, String)}). 
	 * Finally, displays the {@link #help()} and some visual hints (such {@link #drawZoomWindowHint()},
	 * {@link #drawScreenRotateLineHint()} and {@link #drawArcballReferencePointHint()}) according
	 * to user interaction and flags.
	 * 
	 * @see #proscenium()
	 * @see #addDrawHandler(Object, String)
	 */
    public void draw() {
    	// 1. Alternative use only
		proscenium();
		
		// 2. Draw external registered method		
		if(drawHandlerObject != null) {
			try {
				if (drawHandlerParamIsThis)
					drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this } );
				else
					drawHandlerMethod.invoke(drawHandlerObject, new Object[] { parent } );
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your " + drawHandlerMethodName + " method");
				e.printStackTrace();
			}
		}
		
		// 3. Try to draw what should have been draw in the pre()
		if( !backgroundIsHandled() ) 
			displayVisualHints();				
	}
	
	/** The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from Scene, this is the method you should overload,
	 * but no if you instantiate your own Scene object (in this case you should just overload
	 * {@code PApplet.draw()} to define your scene).
	 * <p> 
	 * The processing camera set in {@link #pre()} converts from the world to the camera
	 * coordinate systems. Vertices given in {@link #draw()} can then be considered as being
	 * given in the world coordinate system. The camera is moved in this world using the mouse.
	 * This representation is much more	intuitive than a camera-centric system (which for 
	 * instance is the standard in OpenGL).
	 */
	public void proscenium() {}
	
	// 4. Scene dimensions
	
	/**
	 * Returns the scene radius.
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().sceneRadius()}
	 * 
	 * @see #setRadius(float)
	 * @see #center()
	 */
	public float radius() {
		return camera().sceneRadius();
	} 
	
	/**
	 * Returns the scene center.
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().sceneCenter()}
	 * 
	 * @see #setCenter(PVector)
	 * {@link #radius()}
	 */
	public PVector center() {
		return camera().sceneCenter();
	}
	
	/**
	 * Returns the arcball reference point.
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().arcballReferencePoint()}
	 * 
	 * @see #setCenter(PVector)
	 * {@link #radius()}
	 */
	public PVector arcballReferencePoint() {
		return camera().arcballReferencePoint();
	}
	
	/**
	 * Sets the {@link #radius()} of the Scene. 
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().setSceneRadius(radius)}
	 * 
	 * @see #setCenter(PVector)
	 */
	public void setRadius(float radius) {
		camera().setSceneRadius(radius);		
	}
	
	/**
	 * Sets the {@link #center()} of the Scene. 
	 * <p>
	 * Convenience wrapper function that simply calls {@code }
	 * 
	 * @see #setRadius(float)
	 */
	public void setCenter(PVector center) {
		camera().setSceneCenter(center);
	}
	
	/**
	 * Sets the {@link #center()} and {@link #radius()} of the Scene from the
	 * {@code min} and {@code max} PVectors. 
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().setSceneBoundingBox(min,max)}
	 * 
	 * @see #setRadius(float)
	 * @see #setCenter(PVector)
	 */
	public void setBoundingBox(PVector min, PVector max) {
		camera().setSceneBoundingBox(min,max);
	}
	
	/**
	 * Convenience wrapper function that simply calls {@code camera().showEntireScene()}
	 * 
	 * @see remixlab.proscene.Camera#showEntireScene()
	 */
	public void showAll() {
		camera().showEntireScene();
	}
	
	/**
	 * Convenience wrapper function that simply returns
	 * {@code camera().setArcballReferencePointFromPixel(pixel)}.
	 * <p>
	 * Current implementation set no
	 * {@link remixlab.proscene.Camera#arcballReferencePoint()}. Override
	 * {@link remixlab.proscene.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.proscene.Camera#setArcballReferencePointFromPixel(Point)
	 * @see remixlab.proscene.Camera#pointUnderPixel(Point)
	 */
	public boolean setArcballReferencePointFromPixel(Point pixel) {
		return camera().setArcballReferencePointFromPixel(pixel);
	}
	
	/**
	 * Convenience wrapper function that simply returns
	 * {@code camera().interpolateToZoomOnPixel(pixel)}.
	 * <p>
	 * Current implementation does nothing. Override
	 * {@link remixlab.proscene.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.proscene.Camera#interpolateToZoomOnPixel(Point)
	 * @see remixlab.proscene.Camera#pointUnderPixel(Point)
	 */
	public Camera.WorldPoint interpolateToZoomOnPixel(Point pixel) {
		return camera().interpolateToZoomOnPixel(pixel);
	}
	
	
	/**
	 * Convenience wrapper function that simply returns
	 * {@code camera().setSceneCenterFromPixel(pixel)}
	 * <p>
	 * Current implementation set no
	 * {@link remixlab.proscene.Camera#sceneCenter()}. Override
	 * {@link remixlab.proscene.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.proscene.Camera#setSceneCenterFromPixel(Point)
	 * @see remixlab.proscene.Camera#pointUnderPixel(Point)
	 */
	public boolean setCenterFromPixel(Point pixel) {
		return camera().setSceneCenterFromPixel(pixel);
	}
	
	// 5. State of the viewer
	
	/**
	 * Returns the current {@link #camera()} type.
	 */
	public final Camera.Type cameraType() {
		return camera().type();
	}
	
	/**
	 * Sets the {@link #camera()} type.
	 */
	public void setCameraType(Camera.Type type) {
		if ( type != camera().type() )
			camera().setType(type);
	}
	
	/**
	 * Returns the current {@link #camera()} kind.
	 */
	public final Camera.Kind cameraKind() {
		return camera().kind();
	}
	
	/**
	 * Sets the {@link #camera()} kind.
	 */
	public void setCameraKind(Camera.Kind kind) {
		if ( kind != camera().kind() ) { 
			camera().setKind(kind);
			if (kind == Camera.Kind.PROSCENE)
				PApplet.println("Changing camera kind to Proscene");
			else
				PApplet.println("Changing camera kind to Standard");
		}
	}
	
	
	/**
	 * Returns the {@link PApplet#width} to {@link PApplet#height} aspect ratio of the
	 * processing display window.
	 */
	public float aspectRatio() { 
		return (float)parent.width / (float)parent.height;
	}	
	
	// 6. Display of visual hints and Display methods
	
	/**
	 * Returns {@code true} if axis is currently being drawn and {@code false} otherwise. 
	 */
	public boolean axisIsDrawn () {
		return axisIsDrwn;
	} 
	
	/**
	 * Returns {@code true} if grid is currently being drawn and {@code false} otherwise. 
	 */
	public boolean gridIsDrawn() {
		return gridIsDrwn;
	}
	
	/**
	 * Returns {@code true} if the frames selection visual hints are currently being drawn and
	 * {@code false} otherwise. 
	 */
	public boolean frameSelectionHintIsDrawn() {
		return frameSelectionHintIsDrwn;
	}
	
	/**
	 * Returns {@code true} if the camera key frame paths are currently being drawn and
	 * {@code false} otherwise. 
	 */
	public boolean cameraPathsAreDrawn() {
		return cameraPathsAreDrwn;
	}
	
	
	
	/**
	 * Returns {@code true} if axis is currently being drawn and {@code false} otherwise. 
	 */
	public boolean interactiveFrameIsDrawn() {
		return iFrameIsDrwn;
	}
	
	/**
	 * Returns {@code true} if drawn is currently being constrained and {@code false} otherwise. 
	 */
	public boolean drawIsConstrained() {
		return withConstraint;
	}
	
	/**
	 * Convenience function that simply calls {@code setAxisIsDrawn(true)}
	 */
	public void setAxisIsDrawn() {
		setAxisIsDrawn(true);
	}
	
	/**
	 * Sets the display of the axis according to {@code draw}
	 */
	public void setAxisIsDrawn(boolean draw) {
		axisIsDrwn = draw;
	}
	
	/**
	 * Convenience function that simply calls {@code setGridIsDrawn(true)}
	 */
	public void setGridIsDrawn() {
		setGridIsDrawn(true);
	}
	
	/**
	 * Sets the display of the grid according to {@code draw}
	 */
	public void setGridIsDrawn(boolean draw) {
		gridIsDrwn = draw;
	}
	
	/**
	 * Sets the display of the interactive frames' selection hints according to {@code draw}
	 */
	public void setFrameSelectionHintIsDrawn(boolean draw) {
		frameSelectionHintIsDrwn = draw;
	}
	
	/**
	 * Sets the display of the camera key frame paths according to {@code draw}
	 */
	public void setCameraPathsAreDrawn(boolean draw) {
		cameraPathsAreDrwn = draw;
	}
	
	public void setDrawInteractiveFrame() {
		setDrawInteractiveFrame(true);
	}
	
	/**
	 * Sets the interactivity to the Scene {@link #interactiveFrame()} instance according to {@code draw}
	 */
    public void setDrawInteractiveFrame(boolean draw) {
    	if ( draw && (glIFrame == null) )
    		return;
    	if  ( !draw && ( currentCameraProfile() instanceof ThirdPersonCameraProfile ) && interactiveFrame().equals(avatar()) )//more natural than to bypass it
    		return;
    	iFrameIsDrwn = draw;
	}
    
    /**
	 * Constrain frame displacements according to {@code wConstraint}
	 */
    public void setDrawWithConstraint(boolean wConstraint) {
    	withConstraint = wConstraint;
    }
	
    /**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawAxis(parent)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawAxis(PApplet)
     */
	public void drawAxis() {
		DrawingUtils.drawAxis(parent);
	}
	
	/**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawAxis(parent, length)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawAxis(PApplet, float)
     */
	public void drawAxis(float length) {
		DrawingUtils.drawAxis(parent, length);
	}
	
	/**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawGrid(parent, 100, 10)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawGrid(PApplet)
     */
	public void drawGrid() {
		DrawingUtils.drawGrid(parent, 100, 10);
	}
	
	/**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawGrid(parent, size, 10)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawGrid(PApplet, float)
     */
	public void drawGrid(float size) {
		DrawingUtils.drawGrid(parent, size, 10);
	}
	
	/**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawGrid(parent, 100, nbSubdivisions)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawGrid(PApplet, float, int)
     */
	public void drawGrid(int nbSubdivisions) {
		DrawingUtils.drawGrid(parent, 100, nbSubdivisions);
	}
	
	/**
     * Convenience wrapper function that simply calls {@code DrawingUtils.drawGrid(parent, size, nbSubdivisions)}
     * 
     * @see remixlab.proscene.DrawingUtils#drawGrid(PApplet, float, int)
     */
	public void drawGrid(float size, int nbSubdivisions) {
		DrawingUtils.drawGrid(parent, size, nbSubdivisions);
	}
	
	/**
	 * Draws a rectangle on the screen showing the region where a zoom
	 * operation is taking place. 
	 */
	protected void drawZoomWindowHint() {
		if( (fCorner == null) || (lCorner == null) )
			return;
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();			
		float p2x = (float) lCorner.getX();
		float p2y = (float) lCorner.getY();		
		beginScreenDrawing();
		parent.pushStyle();
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();		
		parent.beginShape();		
		parent.vertex(xCoord(p1x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2x), yCoord(p2y), zCoord());
		parent.vertex(xCoord(p1x), yCoord(p2y), zCoord());
		parent.endShape(CLOSE);
		parent.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Draws visual hint (a line on the screen) when a screen
	 * rotation is taking place. 
	 */
	protected void drawScreenRotateLineHint() {
		if(fCorner == null)
			return;
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();
		PVector p2 = camera().projectedCoordinatesOf(arcballReferencePoint());
		beginScreenDrawing();
		parent.pushStyle();
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();
		parent.beginShape(LINE);
		parent.vertex(xCoord(p1x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2.x), yCoord(p2.y), zCoord());		
		parent.endShape();
		parent.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Draws visual hint (a cross on the screen) when the {@link #arcballReferencePoint()}
	 * is being set.
	 * <p>
	 * Simply calls {@link #drawCross(float, float)} on
	 * {@code camera().projectedCoordinatesOf(arcballReferencePoint())} {@code x} and 
	 * {@code y} coordinates.
	 * 
	 * @see #drawCross(float, float)
	 */	
	protected void drawArcballReferencePointHint() {
		PVector p = camera().projectedCoordinatesOf(arcballReferencePoint());
		drawCross(p.x, p.y);
	}
	
	/**
	 * Draws all InteractiveFrames' selection regions (a 10x10 shooter target visual hint).
	 * 
	 * <b>Attention:</b> If the InteractiveFrame is part of a Camera path draws nothing.
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
	protected void drawSelectionHints() {
		for (MouseGrabber mg: MouseGrabberPool) {
			InteractiveFrame iF = (InteractiveFrame) mg;//downcast needed
			if ( !iF.isInCameraPath() ) {
				PVector center = camera().projectedCoordinatesOf( iF.position() );
				if ( mg.grabsMouse() )
					drawShooterTarget(parent.color(0,255,0),center,12,2);
				else
					drawShooterTarget(parent.color(240,240,240),center,10,1);
			}
		}
	}
	
	/**
	 * Draws the selection regions (a 10x10 shooter target visual hint) of all InteractiveFrames
	 * forming part of the Camera paths.
	 * 
	 * @see #drawSelectionHints()
	 */
	protected void drawCameraPathSelectionHints() {
		for (MouseGrabber mg: MouseGrabberPool) {			
			InteractiveFrame iF = (InteractiveFrame) mg;//downcast needed
			if ( iF.isInCameraPath() ) {
				PVector center = camera().projectedCoordinatesOf( iF.position() );
				if ( mg.grabsMouse() )
					drawShooterTarget(parent.color(0,255,255),center,12,2);
				else
					drawShooterTarget(parent.color(255,255,0),center,10,1);
			}
		}
	}
	
	/**
	 * Convenience function that simply calls
	 * {@code drawPointUnderPixelHint(parent.color(255,255,255),px,py,15,3)}.
	 */
	public void drawCross(float px, float py) {
		drawCross(parent.color(255,255,255),px,py,15,3);
	}
	
	/**
	 * Draws a cross on the screen centered under pixel {@code (px, py)}, and edge
	 * of size {@code size}. {@code strokeWeight} defined the weight of the stroke.
	 * 
	 * @see #drawArcballReferencePointHint()
	 */
	public void drawCross(int color, float px, float py, float size, int strokeWeight) {		
		beginScreenDrawing();
		parent.pushStyle();
		parent.stroke(color);
		parent.strokeWeight(strokeWeight);
		parent.noFill();
		parent.beginShape(LINES);
		parent.vertex(xCoord(px - size), yCoord(py), zCoord());
		parent.vertex(xCoord(px + size), yCoord(py), zCoord());
		parent.vertex(xCoord(px), yCoord(py - size), zCoord());
		parent.vertex(xCoord(px), yCoord(py + size), zCoord());
		parent.endShape();
		parent.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Draws a filled circle using screen coordinates.
	 * @param color Color used to fill the circle.
	 * @param center Circle screen center.
	 * @param radius Circle screen radius.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 */
	public void drawFilledCircle(int color, PVector center, float radius) {
		float x = center.x;
		float y = center.y;
		float angle, x2, y2;		
		beginScreenDrawing();
		parent.pushStyle();
		parent.noStroke();
		parent.fill(color);
		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(xCoord(x), yCoord(y), zCoord());
		for (angle=0.0f;angle<=TWO_PI; angle+=0.157f) {
		    x2 = x+PApplet.sin(angle)*radius;
		    y2 = y+PApplet.cos(angle)*radius;
		    parent.vertex(xCoord(x2), yCoord(y2), zCoord());
		}		
		parent.endShape();
		parent.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Draws a filled square using screen coordinates.
	 * @param color Color used to fill the square.
	 * @param center Square screen center.
	 * @param edge Square edge length.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 */
	public void drawFilledSquare(int color, PVector center, float edge) {
		float x = center.x;
		float y = center.y;
		
		beginScreenDrawing();
		parent.pushStyle();
		parent.noStroke();
		parent.fill(color);		
		parent.beginShape(QUADS);
		parent.vertex(xCoord(x-edge), yCoord(y+edge), zCoord());
		parent.vertex(xCoord(x+edge), yCoord(y+edge), zCoord());
		parent.vertex(xCoord(x+edge), yCoord(y-edge), zCoord());
		parent.vertex(xCoord(x-edge), yCoord(y-edge), zCoord());
		parent.endShape();
		parent.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Draws the classical shooter target on the screen.
	 * 
	 * @param color Color of the target
	 * @param center Center of the target on the screen
	 * @param length Length of the target in pixels
	 * @param strokeWeight Stroke weight
	 */
	public void drawShooterTarget(int color, PVector center, float length, int strokeWeight) {		
		float x = center.x;
		float y = center.y;
		
		beginScreenDrawing();
		parent.pushStyle();
		
		parent.stroke(color);
		parent.strokeWeight(strokeWeight);
		parent.noFill();
		
		parent.beginShape();		
		parent.vertex(xCoord((x-length)), yCoord((y-length)+(0.6f*length)), zCoord());		
		parent.vertex(xCoord(x-length), yCoord(y-length), zCoord());
		parent.vertex(xCoord((x-length)+(0.6f*length)), yCoord((y-length)), zCoord());
		parent.endShape();
		
		parent.beginShape();
		parent.vertex(xCoord((x+length)-(0.6f*length)), yCoord(y-length), zCoord());
		parent.vertex(xCoord(x+length), yCoord(y-length), zCoord());		
		parent.vertex(xCoord(x+length), yCoord((y-length)+(0.6f*length)), zCoord());
		parent.endShape();
		
		parent.beginShape();
		parent.vertex(xCoord(x+length), yCoord((y+length)-(0.6f*length)), zCoord());
		parent.vertex(xCoord(x+length), yCoord(y+length), zCoord());
		parent.vertex(xCoord((x+length)-(0.6f*length)), yCoord(y+length), zCoord());
		parent.endShape();
		
		parent.beginShape();
		parent.vertex(xCoord((x-length)+(0.6f*length)), yCoord(y+length), zCoord());
		parent.vertex(xCoord(x-length), yCoord(y+length), zCoord());
		parent.vertex(xCoord(x-length), yCoord((y+length)-(0.6f*length)), zCoord());
		parent.endShape();
		
		parent.popStyle();
		endScreenDrawing();
		
		drawCross(color, center.x, center.y, 0.6f*length, strokeWeight);
	}
	
	/**
	 * Computes the world coordinates of an screen object so that drawing can be done directly
	 * with 2D screen coordinates.
	 * <p>
	 * All screen drawing should be enclosed between {@link #beginScreenDrawing()} and {@link #endScreenDrawing()}.
	 * Then you can just begin drawing your screen shapes (defined between {@code PApplet.beginShape()} and
	 * {@code PApplet.endShape()}).
	 * <p>
	 * <b>Note:</b> The (x,y) vertex screen coordinates should be specified as:
	 * {@code vertex(xCoord(x), yCoord(y), Scene.zCoord())}.
	 * 
	 * @see #endScreenDrawing()
	 * @see #xCoord(float)
	 * @see #yCoord(float)
	 * @see #zCoord()
	 */
	public void beginScreenDrawing() {
		if ( startCoordCalls != 0 )
			throw new RuntimeException("There should be exactly one startScreenCoordinatesSystem() call followed by a " +
					"stopScreenCoordinatesSystem() and they cannot be nested. Check your implementation!");
		startCoordCalls ++;

		float threshold = 0.03f;
		zC = camera().zNear() + threshold * ( camera().zFar() - camera().zNear() );		
		if( camera().type() == Camera.Type.PERSPECTIVE ) {							
			halfWidthSpace = PApplet.tan(camera().horizontalFieldOfView()/2) * zC;
			halfHeightSpace = PApplet.tan(camera().fieldOfView()/2) * zC;
		}
		else {
			float wh[] = camera().getOrthoWidthHeight();
			halfWidthSpace = wh[0];
			halfHeightSpace = wh[1];
		}
		
		parent.pushMatrix();
		if (camera().frame()!=null) camera().frame().applyTransformation(parent);
	}
	
	/**
	 * Ends screen drawing. See {@link #beginScreenDrawing()} for details.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #xCoord(float)
	 * @see #yCoord(float)
	 * @see #zCoord()
	 */
	public void endScreenDrawing() {
		startCoordCalls --;
		if ( startCoordCalls != 0 )
			throw new RuntimeException("There should be exactly one startScreenCoordinatesSystem() call followed by a " +
					"stopScreenCoordinatesSystem() and they cannot be nested. Check your implementation!");
		
		parent.popMatrix();
	}
	
	/**
	 * Computes the {@code x} coordinate of the {@code px} screen coordinate.
	 * <p>
	 * This method is only useful when drawing directly on screen.
	 * It should be used in conjunction with {@link #beginScreenDrawing()} and
	 * {@link #endScreenDrawing()} (which may be consulted for details).
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 * @see #yCoord(float)
	 * @see #zCoord()
	 */
	public float xCoord(float px) {
		//translate screen origin to center
		px = px - (parent.width/2);		
		//normalize
		px = px / (parent.width/2);
		return halfWidthSpace * px;
	}
	
	/**
	 * Computes the {@code y} coordinate of the {@code py} screen coordinate.
	 * <p>
	 * This method is only useful when drawing directly on screen.
	 * It should be used in conjunction with {@link #beginScreenDrawing()} and
	 * {@link #endScreenDrawing()} (which may be consulted for details).
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 * @see #xCoord(float)
	 * @see #zCoord()
	 */
	public float yCoord(float py) {
		//translate screen origin to center
		py = py - (parent.height/2);		
		//normalize
		py = py / (parent.height/2);		
		return halfHeightSpace * py;
	}
	
	/**
	 * Returns the {@code z} coordinate needed when drawing objects directly on screen.
	 * <p>
	 * This should be used in conjunction with {@link #beginScreenDrawing()} and
	 * {@link #endScreenDrawing()} (which may be consulted for details).
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 * @see #yCoord(float)
	 * @see #zCoord()
	 */
	public float zCoord() {
		return -zC;
	}
	
	/**
	 * Called from the timer to stop displaying the point under pixel and
	 * arcball reference point visual hints. 
	 */
	protected void unSetTimerFlag() {
		arpFlag = false;
		pupFlag = false;			
	}
	
	// 7. Camera profiles
	
	private void initDefaultCameraProfiles() {
		cameraProfileMap = new HashMap<String, CameraProfile>();
		cameraProfileNames = new ArrayList<String>();
		currentCameraProfile = null;
		// register here the default profiles
		registerCameraProfile( new ArcballCameraProfile(this, "ARCBALL") );
		//registerCameraProfile( new WheeledArcballCameraProfile(this, "ARCBALL") );//TODO test
		registerCameraProfile( new FirstPersonCameraProfile(this, "FIRST_PERSON") );
		setCurrentCameraProfile("ARCBALL");
	}
	
	/**
	public HashMap<String, CameraProfile> cameraProfileHandler() {
		return cameraProfileHandler;
	}
	*/
	
	/**
	 * Make it current if size==1 or makeCurrent
	 * Doesn't allow to register nulls.
	 * Doesn't allow to register duplicates.
	 */
	public boolean registerCameraProfile( CameraProfile cp ) {
		//if(!isCameraProfileRegistered(cp)) {
		if(cp == null)
			return false;
		if ( !isCameraProfileRegistered(cp) ) {
			cameraProfileNames.add( cp.name() );
			cameraProfileMap.put(cp.name(), cp);
			return true;	
		}
		return false;
	}
	
	public boolean unregisterCameraProfile( CameraProfile cp ) {
		return unregisterCameraProfile( cp.name() );
	}
	
	public boolean unregisterCameraProfile( String cp ) {
		if( !isCameraProfileRegistered(cp) )
			return false;		
		
		CameraProfile cProfile = cameraProfile(cp);
		int abInstances = 0;
		int fpInstances = 0;
		
		for (CameraProfile camProfile : cameraProfileMap.values() ) {
			if( camProfile instanceof ArcballCameraProfile )
				abInstances++;
			if( camProfile instanceof FirstPersonCameraProfile )
				fpInstances++;
		}		
		
		if ( (cProfile instanceof ArcballCameraProfile) && ( abInstances == 1 ) && ( fpInstances == 0 ) )
			return false;
		
		if ( (cProfile instanceof FirstPersonCameraProfile) && ( fpInstances == 1 ) && ( abInstances == 0 ) )
			return false;
		
		if( isCurrentCameraProfile(cp) )
			nextCameraProfile();
		
		if ( cameraProfileNames.remove( cp ) ) {
			cameraProfileMap.remove( cp );
			return true;
		}
		
		return false;
	}
	
	public CameraProfile cameraProfile(String name) {		
		return cameraProfileMap.get(name);
	}
	
	public boolean isCameraProfileRegistered(CameraProfile cp) {
		return cameraProfileMap.containsValue(cp);
	}
	
	public boolean isCameraProfileRegistered(String name) {
		return cameraProfileMap.containsKey(name);
	}
	
	/**
	 * could be null
	 */
	public CameraProfile currentCameraProfile() {
		return currentCameraProfile;
	}
	
	boolean isCurrentCameraProfile(String cp) {
		return isCurrentCameraProfile( cameraProfileMap.get(cp) );
	}
	
	boolean isCurrentCameraProfile(CameraProfile cp) {
		return currentCameraProfile() == cp;
	}
 
	/**
	 * only true if cp is non-null and succeeded making it current.
	 * cp is first register if necessary.
	 */
	public boolean setCurrentCameraProfile( CameraProfile cp ) {
		if(cp == null) {
			return false;
		}	
		if (!isCameraProfileRegistered(cp))
			if (!registerCameraProfile(cp))
				return false;
		
		return setCurrentCameraProfile(cp.name());
	}
	
	/**
	 * only true if succeeded making cp current.
	 */
	public boolean setCurrentCameraProfile( String cp ) {		
		CameraProfile camProfile = cameraProfileMap.get(cp);
		if(camProfile == null)
			return false;		
		if( ( camProfile instanceof ThirdPersonCameraProfile) && (avatar() == null) )
			return false;
		else {			
			if( camProfile instanceof ThirdPersonCameraProfile) {
				setDrawInteractiveFrame();
				setCameraType(Camera.Type.PERSPECTIVE);//TODO can use camera.kind.standard and ortho?
				if( avatarIsInteractiveDrivableFrame )
					((InteractiveDrivableFrame)avatar()).removeFromMouseGrabberPool();				
				camera().frame().updateFlyUpVector();//?
				camera().frame().stopSpinning();
				if ( avatarIsInteractiveDrivableFrame ) {
					((InteractiveDrivableFrame)(avatar())).updateFlyUpVector();
					((InteractiveDrivableFrame)(avatar())).stopSpinning();
				}
                //perform small animation ;)	
				if ( camera().anyInterpolationIsStarted() ) 
					camera().stopAllInterpolations();
				Camera cm = camera().clone();
				cm.setPosition( avatar().cameraPosition() );
    			cm.setUpVector( avatar().upVector() );
    			cm.lookAt( avatar().target() );
				camera().interpolateTo(cm.frame());
				currentCameraProfile = camProfile;
			} else {			
				if( camProfile instanceof FirstPersonCameraProfile) {
					camera().frame().updateFlyUpVector();
					camera().frame().stopSpinning();
				}
				if( currentCameraProfile instanceof ThirdPersonCameraProfile )
                    camera().interpolateToFitScene();
				
				currentCameraProfile = camProfile;
				
				setDrawInteractiveFrame(false);
				if( avatarIsInteractiveDrivableFrame )
                    ((InteractiveDrivableFrame)avatar()).addInMouseGrabberPool();
			}			
			return true;
		}
	}
	
	/**
	 * Sets the next camera profile.
	 */
	public void nextCameraProfile() {
		int currentCameraProfileIndex = cameraProfileNames.indexOf( currentCameraProfile().name() );	    	    
		nextCameraProfile(++currentCameraProfileIndex);		
	}
	
	private void nextCameraProfile(int index) {		
		if (!cameraProfileNames.isEmpty()) {
			if( index == cameraProfileNames.size() )
				index = 0;
			
			if ( !setCurrentCameraProfile( cameraProfileNames.get(index) ) )
				nextCameraProfile(++index);			
			//debug:
			else
				PApplet.println("Camera profile changed to: " + cameraProfileNames.get(index));				
		}
	}
	
	// 8. Keyboard customization
	
	/**
	 * Parses the sketch to find if any KeyXxxx method has been implemented. If this is the case,
	 * print a warning message telling the user what to do to avoid possible conflicts with proscene.
	 * <p>
	 * The methods sought are: {@code keyPressed}, {@code keyReleased}, and {@code keyTyped}. 
	 */
	protected void parseKeyXxxxMethods() {		
		boolean foundKP = true;
		boolean foundKR = true;
		boolean foundKT = true;		
		
		try {
			parent.getClass().getDeclaredMethod( "keyPressed" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKP = false;
		} catch (NoSuchMethodException e) {
			foundKP = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "keyReleased" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKR = false;
		} catch (NoSuchMethodException e) {
			foundKR = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "keyTyped" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKT = false;
		} catch (NoSuchMethodException e) {
			foundKT = false;
		}
		
		if(foundKP || foundKR || foundKT) {
		//if( (foundKP || foundKR || foundKT) && (!parent.getClass().getName().equals("remixlab.proscene.Viewer")) ) {
			PApplet.println("It seems that you have implemented some KeyXxxxMethod in your sketch! Please bear in mind that proscene reserves some keys for its own use." +
					" To avoid possible conflicts with proscene you may disable proscene keyboard handling while doing your keyboard manipulation by calling" +
					" Scene.disableKeyboardHandling() (you can re-enable it later by calling Scene.enableKeyboardHandling()).");
		}
	}
		
	/**
	 * Returns {@code true} if the keyboard is currently being handled by proscene and {@code false} otherwise.
	 * Set keyboard handling with {@link #enableMouseHandling(boolean)}.
	 * <p>
	 * Keyboard handling is enable by default.
	 */
	public boolean keyboardIsHandled() {
		return keyboardHandling; 
	}
	
	/**
     * Toggles the state of {@link #keyboardIsHandled()}
     */
	public void toggleKeyboardHandling() {
		enableKeyboardHandling(!keyboardHandling);
	}
	
	/**
	 * Enables or disables proscene keyboard handling according to {@code enable}
	 * 
	 * @see #keyboardIsHandled()
	 */
	public void enableKeyboardHandling(boolean enable) {
		if(enable)
			enableKeyboardHandling();
		else
			disableKeyboardHandling();
	}
	
	/**
	 * Enables proscene keyboard handling.
	 * 
	 * @see  #keyboardIsHandled()
	 */
	public void enableKeyboardHandling() {		
		keyboardHandling = true;
		parent.registerKeyEvent(this);
	}
	
	/**
	 * Disables proscene keyboard handling.
	 * 
	 * @see  #keyboardIsHandled()
	 */
	public void disableKeyboardHandling() {		
		keyboardHandling = false;
		parent.unregisterKeyEvent(this);
	}
	
	/**
	 * Method interface between proscene and processing to handle the keyboard.
	 * 
	 * @see #keyboardIsHandled()
	 * @see #enableKeyboardHandling(boolean)
	 */
	public void keyEvent(KeyEvent e) {
		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			break;
		case KeyEvent.KEY_RELEASED:
			keyReleased(e);
			break;
		case KeyEvent.KEY_TYPED:
			break;
		}
	}
	
	private void setActionDescriptions() {
		//1. keyboard
		keyboardActionDescription = new HashMap<GlobalKeyboardAction, String>();
		keyboardActionDescription.put(GlobalKeyboardAction.DRAW_AXIS, "Toggles the display of the world axis");
		keyboardActionDescription.put(GlobalKeyboardAction.DRAW_GRID, "Toggles the display of the XY grid");
		keyboardActionDescription.put(GlobalKeyboardAction.CAMERA_MODE, "Cycles to the registered camera mode profiles");
		keyboardActionDescription.put(GlobalKeyboardAction.CAMERA_TYPE, "Toggles camera type: orthographic or perspective");
		keyboardActionDescription.put(GlobalKeyboardAction.CAMERA_KIND, "Toggles camera kind: proscene or standard");
		keyboardActionDescription.put(GlobalKeyboardAction.HELP, "Toggles the display of the help");
		keyboardActionDescription.put(GlobalKeyboardAction.EDIT_CAMERA_PATH, "Toggles the key frame camera paths (if any) for edition");
		keyboardActionDescription.put(GlobalKeyboardAction.FOCUS_INTERACTIVE_FRAME, "Toggle interactivity between camera and interactive frame (if any)");
		keyboardActionDescription.put(GlobalKeyboardAction.DRAW_FRAME_SELECTION_HINT, "Toggle interactive frame selection region drawing");
		keyboardActionDescription.put(GlobalKeyboardAction.CONSTRAIN_FRAME, "Toggles on and off frame constraints (if any)");
		//2. mouse click
		
		//3. mouse actions
	}
	
	// Key bindings. 0 means not defined
	public void setDefaultShortcuts() {
		/**		 		
		ARP_FROM_PIXEL, RESET_ARP,		
		 */		
		// D e f a u l t s h o r t c u t s
		//gProfile.setShortcut(KeyEvent.VK_A, KeyBindings.Modifier.CONTROL, Scene.KeyboardAction.DRAW_AXIS);
		//gProfile.setShortcut('a', PApplet.CONTROL, Scene.KeyboardAction.DRAW_AXIS);
		setShortcut('a', GlobalKeyboardAction.DRAW_AXIS);
		//test CASE
		//setShortcut('A', GlobalKeyboardAction.DRAW_AXIS);
		//setShortcut('G', GlobalKeyboardAction.DRAW_GRID);	
		setShortcut('g', GlobalKeyboardAction.DRAW_GRID);
		setShortcut(KeyEvent.VK_G, Modifier.ALT_GRAPH, GlobalKeyboardAction.DRAW_GRID);
		setShortcut('B', GlobalKeyboardAction.DRAW_GRID);
		setShortcut(' ', GlobalKeyboardAction.CAMERA_MODE);
		setShortcut('e', GlobalKeyboardAction.CAMERA_TYPE);
		setShortcut('k', GlobalKeyboardAction.CAMERA_KIND);
		setShortcut('h', GlobalKeyboardAction.HELP);
		setShortcut('r', GlobalKeyboardAction.EDIT_CAMERA_PATH);
		setShortcut('i', GlobalKeyboardAction.FOCUS_INTERACTIVE_FRAME);
		setShortcut('f', GlobalKeyboardAction.DRAW_FRAME_SELECTION_HINT);		
		setShortcut('w', GlobalKeyboardAction.CONSTRAIN_FRAME);

		// K e y f r a m e s s h o r t c u t k e y s
		setAddKeyFrameKeyboardModifier(Modifier.CONTROL);		
		setDeleteKeyFrameKeyboardModifier(Modifier.ALT);
		setPathKey('1', 1);
		setPathKey('2', 2);
		setPathKey('3', 3);
		setPathKey('4', 4);
		setPathKey('5', 5);
		//setPathKey('q', 6);//TODO need it to be more general and handle any kind of key?
	}
	
	public void setPathKey(Character key, Integer path) {
		pathKeys.setMapping(new KeyboardShortcut(key), path);	
	}
	
	public Integer path(Character key) {
		return pathKeys.mapping(new KeyboardShortcut(key));
	}
	
	public void removePathKey(Character key) {
		pathKeys.removeMapping(new KeyboardShortcut(key));
	}
	
	public void setAddKeyFrameKeyboardModifier(Modifier modifier) {
		addKeyFrameKeyboardModifier = modifier;
	}
	
	public void setDeleteKeyFrameKeyboardModifier(Modifier modifier) {
		deleteKeyFrameKeyboardModifier = modifier;
	}
	
	//wrappers:
	//KeyBindings<KeyboardShortcut, GlobalKeyboardAction>
	public void setShortcut(Integer vKey, Modifier modifier, GlobalKeyboardAction action) {
		gProfile.setMapping(new KeyboardShortcut(vKey, modifier), action);
	}
	
	public void setShortcut(Arrow arrow, GlobalKeyboardAction action) {
		gProfile.setMapping(new KeyboardShortcut(arrow), action);
	}
	
	public void setShortcut(Character key, GlobalKeyboardAction action) {
		gProfile.setMapping(new KeyboardShortcut(key), action);
	}
	
	public void removeAllShortcuts() {
		gProfile.removeAllMappings();
	}
	
	public void removeShortcut(Integer vKey, Modifier modifier) {
		gProfile.removeMapping(new KeyboardShortcut(vKey, modifier));
	}	
	//2.
	
	public void removeShortcut(Arrow arrow) {
		gProfile.removeMapping(new KeyboardShortcut(arrow));
	}
	
	//3.
	public void removeShortcut(Character key) {
		gProfile.removeMapping(new KeyboardShortcut(key));
	}
	
	public GlobalKeyboardAction shortcut(Integer vKey, Modifier modifier) {
		return gProfile.mapping(new KeyboardShortcut(vKey, modifier));
	}
	
	public GlobalKeyboardAction shortcut(Arrow arrow) {
		return gProfile.mapping(new KeyboardShortcut(arrow));
	}
	
	public GlobalKeyboardAction shortcut(Character key) {
		return gProfile.mapping(new KeyboardShortcut(key));
	}
	
	public boolean isKeyInUse(KeyboardShortcut key) {
		return gProfile.isShortcutInUse(key);
	}
	
	public boolean isActionBinded(GlobalKeyboardAction action) {
		return gProfile.isActionMapped(action);
	}
	
	protected boolean handleKeyboardAction(KeyEvent e) {
		//keyframes:
		Integer path = path(parent.key);
		PApplet.println(parent.key + " pressed");
		if(	path != null ) {
			if( !e.isAltDown() && !e.isAltGraphDown() && !e.isControlDown() && !e.isShiftDown() ) {
				PApplet.println("playing");
				camera().playPath(path);
				return true;
			}			
			if ( ((addKeyFrameKeyboardModifier == Modifier.ALT) && (e.isAltDown())) ||
				 ((addKeyFrameKeyboardModifier == Modifier.ALT_GRAPH) && (e.isAltGraphDown())) ||
				 ((addKeyFrameKeyboardModifier == Modifier.CONTROL) && (e.isControlDown())) ||
				 ((addKeyFrameKeyboardModifier == Modifier.SHIFT) && (e.isShiftDown()))	) {
				PApplet.println("adding");
				camera().addKeyFrameToPath(path);
				return true;
			}
			if ( ((deleteKeyFrameKeyboardModifier == Modifier.ALT) && (e.isAltDown())) ||
				 ((deleteKeyFrameKeyboardModifier == Modifier.ALT_GRAPH) && (e.isAltGraphDown())) ||
				 ((deleteKeyFrameKeyboardModifier == Modifier.CONTROL) && (e.isControlDown())) ||
				 ((deleteKeyFrameKeyboardModifier == Modifier.SHIFT) && (e.isShiftDown()))	) {
				camera().deletePath(path);
				PApplet.println("removing");
				return true;
			}
		}	
		
		GlobalKeyboardAction kba = null;	
		kba = shortcut( parent.key );
		if(kba == null) {
			if( e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown() ) {
				if (e.isAltDown())
					kba = shortcut( e.getKeyCode(), Modifier.ALT );
				if (e.isAltGraphDown())
					kba = shortcut( e.getKeyCode(), Modifier.ALT_GRAPH );
				if (e.isControlDown())
					kba = shortcut( e.getKeyCode(), Modifier.CONTROL );
				if (e.isShiftDown())
					kba = shortcut( e.getKeyCode(), Modifier.SHIFT );
			}
			else if (parent.key == CODED) {
				if ( (parent.keyCode == UP) || (parent.keyCode == DOWN) || (parent.keyCode == RIGHT) || (parent.keyCode == LEFT) ) {
					if (parent.keyCode == UP)
						kba = shortcut( Arrow.UP );
					if (parent.keyCode == DOWN)
						kba = shortcut( Arrow.DOWN );
					if (parent.keyCode == RIGHT)
						kba = shortcut( Arrow.RIGHT );
					if (parent.keyCode == LEFT)
						kba = shortcut( Arrow.LEFT );
					}
			}
		}
		
		if (kba == null)
			return false;
		else {
			handleKeyboardAction(kba);
			return true;
		}
	}	
	
	protected void handleKeyboardAction(GlobalKeyboardAction id) {
		switch (id) {
		case DRAW_AXIS:
			toggleAxisIsDrawn();			
			break;
		case DRAW_GRID:
			toggleGridIsDrawn();
			break;
		case CAMERA_MODE:
			nextCameraProfile();
			break;
		case CAMERA_TYPE:
			toggleCameraType();
			break;
		case CAMERA_KIND:
			toggleCameraKind();
			break;
        case ARP_FROM_PIXEL:
        	if ( Camera.class == camera().getClass() )
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. " +
						        "See the Point Under Pixel example!");
			else if (setArcballReferencePointFromPixel(new Point(parent.mouseX, parent.mouseY))) {
					arpFlag = true;
					utilityTimer.start();
				}
			break;
		case RESET_ARP:
			camera().setArcballReferencePoint(new PVector(0,0,0));
			arpFlag = true;
			utilityTimer.start();
			break;
		case HELP:
			help();
			break;
		case EDIT_CAMERA_PATH:
			toggleCameraPathsAreDrawn();
			break;
		case FOCUS_INTERACTIVE_FRAME:
			toggleDrawInteractiveFrame();
			break;
		case DRAW_FRAME_SELECTION_HINT:
			toggleFrameSelectionHintIsDrawn();
			break;			
		case CONSTRAIN_FRAME:
			toggleDrawInteractiveFrame();
			break;
		}
	}
	
	protected boolean handleCameraKeyboardAction(KeyEvent e) {
		PApplet.println(parent.key + " pressed");
		CameraKeyboardAction kba = null;
		kba = currentCameraProfile().shortcut( parent.key );
		if(kba == null) {
			if(e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown() ) {
				if (e.isAltDown())
					kba = currentCameraProfile().shortcut( e.getKeyCode(), Modifier.ALT );
				if (e.isAltGraphDown())
					kba = currentCameraProfile().shortcut( e.getKeyCode(), Modifier.ALT_GRAPH );
				if (e.isControlDown())
					kba = currentCameraProfile().shortcut( e.getKeyCode(), Modifier.CONTROL );
				if (e.isShiftDown())
					kba = currentCameraProfile().shortcut( e.getKeyCode(), Modifier.SHIFT );
			}
			else if (parent.key == CODED) {
				if ( (parent.keyCode == UP) || (parent.keyCode == DOWN) || (parent.keyCode == RIGHT) || (parent.keyCode == LEFT) ) {
					if (parent.keyCode == UP)
						kba = currentCameraProfile().shortcut( Arrow.UP );
					if (parent.keyCode == DOWN)
						kba = currentCameraProfile().shortcut( Arrow.DOWN );
					if (parent.keyCode == RIGHT)
						kba = currentCameraProfile().shortcut( Arrow.RIGHT );
					if (parent.keyCode == LEFT)
						kba = currentCameraProfile().shortcut( Arrow.LEFT );
				}
			}
		}
		
		if (kba == null)
			return false;
		else {
			handleCameraKeyboardAction(kba);
			return true;
		}
	}
	
	protected void handleCameraKeyboardAction(CameraKeyboardAction id) {
		switch (id) {
		case INTERPOLATE_TO_ZOOM_ON_PIXEL:
			if ( Camera.class == camera().getClass() )
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. " +
						        "See the Point Under Pixel example!");
			else {
				Camera.WorldPoint wP = interpolateToZoomOnPixel(new Point(parent.mouseX, parent.mouseY));
				if (wP.found) {
					pupVec = wP.point; 
					pupFlag = true;
					utilityTimer.start();
				}
			}
			break;
		case INTERPOLATE_TO_FIT_SCENE:
			camera().interpolateToFitScene();
			break;
		case SHOW_ALL:
			showAll();
			break;
		case MOVE_CAMERA_LEFT:
			camera().frame().translate(camera().frame().inverseTransformOf(new PVector(-10.0f*camera().flySpeed(), 0.0f, 0.0f)));			
		break;
		case MOVE_CAMERA_RIGHT:
			camera().frame().translate(camera().frame().inverseTransformOf(new PVector( 10.0f*camera().flySpeed(), 0.0f, 0.0f)));
			break;
		case MOVE_CAMERA_UP:
			camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f,  -10.0f*camera().flySpeed(), 0.0f)));
			break;
		case MOVE_CAMERA_DOWN:			
			camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f,   10.0f*camera().flySpeed(), 0.0f)));
			break;
		case INCREASE_ROTATION_SENSITIVITY:
			camera().setRotationSensitivity(camera().rotationSensitivity() * 1.2f);
			break;
		case DECREASE_ROTATION_SENSITIVITY:
			camera().setRotationSensitivity(camera().rotationSensitivity() / 1.2f);
			break;
		case INCREASE_CAMERA_FLY_SPEED:
			camera().setFlySpeed(camera().flySpeed() * 1.2f);
			break;
		case DECREASE_CAMERA_FLY_SPEED:
			camera().setFlySpeed(camera().flySpeed() / 1.2f);
			break;
		case INCREASE_AVATAR_FLY_SPEED:
			if(avatar()!=null)
				if ( avatarIsInteractiveDrivableFrame )
					((InteractiveDrivableFrame)avatar()).setFlySpeed(((InteractiveDrivableFrame)avatar()).flySpeed() * 1.2f);
			break;
		case DECREASE_AVATAR_FLY_SPEED:
			if(avatar()!=null)
				if ( avatarIsInteractiveDrivableFrame )
					((InteractiveDrivableFrame)avatar()).setFlySpeed(((InteractiveDrivableFrame)avatar()).flySpeed() / 1.2f);
			break;
		case INCREASE_AZYMUTH:
			if(avatar()!=null) 
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setAzimuth(((InteractiveAvatarFrame)avatar()).azimuth() + PApplet.PI/64 );
			break;
		case DECREASE_AZYMUTH:
			if(avatar()!=null)
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setAzimuth(((InteractiveAvatarFrame)avatar()).azimuth() - PApplet.PI/64 );
			break;
		case INCREASE_INCLINATION:
			if(avatar()!=null)
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setInclination(((InteractiveAvatarFrame)avatar()).inclination() + PApplet.PI/64 );
			break;
		case DECREASE_INCLINATION:
			if(avatar()!=null)
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setInclination(((InteractiveAvatarFrame)avatar()).inclination() - PApplet.PI/64 );
			break;
		case INCREASE_TRACKING_DISTANCE:
			if(avatar()!=null)
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setTrackingDistance(((InteractiveAvatarFrame)avatar()).trackingDistance() + radius()/50 );
			break;
		case DECREASE_TRACKING_DISTANCE:
			if(avatar()!=null)
				if(avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame)avatar()).setTrackingDistance(((InteractiveAvatarFrame)avatar()).trackingDistance() - radius()/50 );
			break;			
		}
	}
	
	/**
	 * Associates the different interactions to the keys.
	 */
	protected void keyReleased(KeyEvent e) {		
		boolean handled = false;
		if(currentCameraProfile()!=null)
			handled = handleCameraKeyboardAction(e);
		if(!handled)
			handleKeyboardAction(e);
	}
	
	/**
	 * Displays the help text describing how interactivity actions are binded to the keyboard and mouse.
	 */
	public void help() {
		//TODO implement me!
	}
	
	// 9. Mouse customization
	
	/**
	 * Parses the sketch to find if any mouseXxxx method has been implemented. If this is the case,
	 * print a warning message telling the user what to do to avoid possible conflicts with proscene.
	 * <p>
	 * The methods sought are: {@code mouseDragged}, {@code mouseMoved}, {@code mouseReleased},
	 * {@code mousePressed}, and {@code mouseClicked}.
	 */
	protected void parseMouseXxxxMethods() {
		boolean foundMD = true;
		boolean foundMM = true;
		boolean foundMR = true;
		boolean foundMP = true;
		boolean foundMC = true;		
		
		try {
			parent.getClass().getDeclaredMethod( "mouseDragged" );
		} catch (SecurityException e) {			
			e.printStackTrace();
			foundMD = false;
		} catch (NoSuchMethodException e) {			
			foundMD = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "mouseMoved" );
		} catch (SecurityException e) {			
			e.printStackTrace();
			foundMM = false;
		} catch (NoSuchMethodException e) {
			foundMM = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "mouseReleased" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMR = false;
		} catch (NoSuchMethodException e) {
			foundMR = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "mousePressed" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMP = false;
		} catch (NoSuchMethodException e) {
			foundMP = false;
		}
		
		try {
			parent.getClass().getDeclaredMethod( "mouseClicked" );
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMC = false;
		} catch (NoSuchMethodException e) {
			foundMC = false;
		}			
		
		if(foundMD || foundMM || foundMR || foundMP || foundMC) {			
			PApplet.println("It seems that you have implemented some mouseXxxxMethod in your sketch! Please bear in mind that proscene overrides processing" +
					" mouse event methods to handle the camera for you. To avoid posibble conflicts you can disable proscene mouse handling while doing your" +
					" mouse manipulation by calling Scene.disableMouseHandling() (you can re-enable it later by calling Scene.enableMouseHandling()).");
		}		
	}
	
	/**
	 * Returns {@code true} if mouse is currently being handled by proscene and {@code false} otherwise.
	 * Set mouse handling with {@link #enableMouseHandling(boolean)}.
	 * <p>
	 * Mouse handling is enable by default.
	 */
	public boolean mouseIsHandled() {
		return mouseHandling; 
	}
	
	/**
     * Toggles the state of {@link #mouseIsHandled()}
     */
	public void toggleMouseHandling() {
		enableMouseHandling(!mouseHandling);
	}
	
	/**
	 * Enables or disables proscene mouse handling according to {@code enable}
	 * 
	 * @see #mouseIsHandled()
	 */
	public void enableMouseHandling(boolean enable) {
		if(enable)
			enableMouseHandling();
		else
			disableMouseHandling();
	}
	
	/**
	 * Enables proscene mouse handling.
	 * 
	 * @see #mouseIsHandled()
	 */
	public void enableMouseHandling() {
		mouseHandling = true;
		parent.registerMouseEvent(this);
	}
	
	/**
	 * Disables proscene mouse handling.
	 * 
	 * 
	 * @see #mouseIsHandled()
	 */
	public void disableMouseHandling() {
		mouseHandling = false;
		parent.unregisterMouseEvent(this);
	}
	
    protected void handleClickAction(ClickAction action) {
    	//public enum ClickAction { NO_CLICK_ACTION, ZOOM_ON_PIXEL, ZOOM_TO_FIT, SELECT, ARP_FROM_PIXEL, RESET_ARP,
			//CENTER_FRAME, CENTER_SCENE, SHOW_ALL, ALIGN_FRAME, ALIGN_CAMERA }    	
    	
    	switch (action) {
    	case NO_CLICK_ACTION :
    		break;
    	case ZOOM_ON_PIXEL :
    		if ( Camera.class == camera().getClass() )
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. " +
						        "See the Point Under Pixel example!");
			else {
				Camera.WorldPoint wP = interpolateToZoomOnPixel(new Point(parent.mouseX, parent.mouseY));
				if (wP.found) {
					pupVec = wP.point; 
					pupFlag = true;
					utilityTimer.start();
				}
			}
    		break;
    	case ZOOM_TO_FIT :
    		camera().interpolateToFitScene();
    		break;
    	case ARP_FROM_PIXEL :
    		if ( Camera.class == camera().getClass() )
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. " +
						        "See the Point Under Pixel example!");
			else if (setArcballReferencePointFromPixel(new Point(parent.mouseX, parent.mouseY))) {
				arpFlag = true;
				utilityTimer.start();
			}
    		break;
    	case RESET_ARP :
    		camera().setArcballReferencePoint(new PVector(0,0,0));
			arpFlag = true;
			utilityTimer.start();
    		break;
    	case CENTER_FRAME :
    		if (interactiveFrame() != null)
    			interactiveFrame().projectOnLine(camera().position(), camera().viewDirection());
    		break;
    	case CENTER_SCENE :
    		camera().centerScene();
    		break;
    	case SHOW_ALL :
    		camera().showEntireScene();
    		break;
    	case ALIGN_FRAME :
    		if (interactiveFrame() != null)
    			interactiveFrame().alignWithFrame(camera().frame());
    		break;
    	case ALIGN_CAMERA :
    		camera().frame().alignWithFrame(null, true);
    		break;
    	}    	
	}
	
    //TODO implement me!
	protected void setDefaultClickActions() {
		setClickShortcut( Button.LEFT, 2, ClickAction.ALIGN_CAMERA );
		setClickShortcut( Button.MIDDLE, 2, ClickAction.ZOOM_TO_FIT );
		setClickShortcut( Button.RIGHT, 2, ClickAction.SHOW_ALL );
	}
	
	//click wrappers:
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllMappings();
	}
	
	public boolean isClickKeyInUse(ClickShortcut key) {
		return clickActions.isShortcutInUse(key);
	}
	
	public boolean isClickActionBinded(Scene.ClickAction action) {
		return clickActions.isActionMapped(action);
	}
	
	public void setClickShortcut(Scene.Button button, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button), action);		
	}
	
	public void setClickShortcut(Scene.Button button, Scene.Modifier modifier, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, modifier), action);		
	}
	
	public void setClickShortcut(Scene.Button button, Integer nc, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, nc), action);		
	}
	
	public void setClickShortcut(Scene.Button button, Scene.Modifier modifier, Integer nc, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, modifier, nc), action);
	}	
	
	public void removeClickShortcut(Scene.Button button) {
		clickActions.removeMapping(new ClickShortcut(button));
	}
	
	public void removeClickShortcut(Scene.Button button, Scene.Modifier modifier) {
		clickActions.removeMapping(new ClickShortcut(button, modifier));
	}
	
	public void removeClickShortcut(Scene.Button button, Integer nc) {
		clickActions.removeMapping(new ClickShortcut(button, nc));
	}
	
	public void removeClickShortcut(Scene.Button button, Scene.Modifier modifier, Integer nc) {
		clickActions.removeMapping(new ClickShortcut(button, modifier, nc));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button) {
		return clickActions.mapping(new ClickShortcut(button));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button, Scene.Modifier modifier) {
		return clickActions.mapping(new ClickShortcut(button, modifier));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button, Integer nc) {
		return clickActions.mapping(new ClickShortcut(button, nc));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button, Scene.Modifier modifier, Integer nc) {
		return clickActions.mapping(new ClickShortcut(button, modifier, nc));
	}
	
	/**
	 * Method interface between proscene and processing to handle the mouse.
	 * 
	 * @see #mouseIsHandled()
	 * @see #enableMouseHandling(boolean)
	 */
	public void mouseEvent(MouseEvent e) {
		if ( currentCameraProfile() == null )
			return;
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked(e);
		break;
		case MouseEvent.MOUSE_DRAGGED:
			this.mouseDragged(e);
		break;
		case MouseEvent.MOUSE_MOVED:
			this.mouseMoved(e);
		break;
		case MouseEvent.MOUSE_PRESSED:
			this.mousePressed(e);
		break;
		case MouseEvent.MOUSE_RELEASED:
			this.mouseReleased(e);
		break;
	    }			
	}
	
	/**
	 * Sets the Camera from processing camera parameters.
	 * <p>
	 * {@link #setMouseGrabber(MouseGrabber)} to the MouseGrabber that grabs
	 * the mouse (or to {@code null} if none of them grab it).
	 */
	public void mouseMoved(MouseEvent event) {
		setMouseGrabber(null);
		for (MouseGrabber mg: MouseGrabberPool) {
			mg.checkIfGrabsMouse(event.getX(), event.getY(), camera());
			if(mg.grabsMouse())
				setMouseGrabber(mg);
		}
	}
	
	protected MouseAction iFrameMouseAction(MouseEvent e) {
		MouseAction iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		Button button = null;
		switch (e.getButton()) {
		case MouseEvent.NOBUTTON:
			button = null;
			break;
		case MouseEvent.BUTTON1: //left button
			button = Button.LEFT;
			break;
		case MouseEvent.BUTTON2: //middle button
			button = Button.MIDDLE;			
			break;
		case MouseEvent.BUTTON3: //right button
			button = Button.RIGHT;
			break;			
		}
		
		if (button == null) {
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return iFrameMouseAction;
		}	
				
		if(e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown() ) {
			if (e.isAltDown())
				iFrameMouseAction = currentCameraProfile().iFrameShortcut( button, Modifier.ALT );			
			if (e.isAltGraphDown())
				iFrameMouseAction = currentCameraProfile().iFrameShortcut( button, Modifier.ALT_GRAPH );
			if (e.isControlDown())
				iFrameMouseAction = currentCameraProfile().iFrameShortcut( button, Modifier.CONTROL );
			if (e.isShiftDown())
				iFrameMouseAction = currentCameraProfile().iFrameShortcut( button, Modifier.SHIFT );
			if( iFrameMouseAction != null )
				return iFrameMouseAction;
		}
		
		iFrameMouseAction = currentCameraProfile().iFrameShortcut( button );
		
		if ( iFrameMouseAction == null )
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;	
		
		return iFrameMouseAction;
	}
	
	private Button getButton(MouseEvent e) {
		Button button = null;
		switch (e.getButton()) {
		case MouseEvent.NOBUTTON:
			break;
		case MouseEvent.BUTTON1: //left button
			button = Button.LEFT;
			break;
		case MouseEvent.BUTTON2: //middle button
			button = Button.MIDDLE;			
			break;
		case MouseEvent.BUTTON3: //right button
			button = Button.RIGHT;
			break;
		}
		return button;
	}
	
	protected MouseAction cameraMouseAction(MouseEvent e) {
		/**
		Button button = null;
		switch (e.getButton()) {
		case MouseEvent.NOBUTTON:
			button = null;
			break;
		case MouseEvent.BUTTON1: //left button
			button = Button.LEFT;
			break;
		case MouseEvent.BUTTON2: //middle button
			button = Button.MIDDLE;			
			break;
		case MouseEvent.BUTTON3: //right button
			button = Button.RIGHT;
			break;			
		}
		*/
		
		Button button = getButton(e);
		
		if (button == null) {
			camMouseAction = MouseAction.NO_MOUSE_ACTION;
			return camMouseAction;
		}	
				
		if(e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown() ) {
			if (e.isAltDown())
				camMouseAction = currentCameraProfile().cameraShortcut( button, Modifier.ALT );			
			if (e.isAltGraphDown())
				camMouseAction = currentCameraProfile().cameraShortcut( button, Modifier.ALT_GRAPH );
			if (e.isControlDown())
				camMouseAction = currentCameraProfile().cameraShortcut( button, Modifier.CONTROL );
			if (e.isShiftDown())
				camMouseAction = currentCameraProfile().cameraShortcut( button, Modifier.SHIFT );
			if( camMouseAction != null )
				return camMouseAction;
		}
		
		camMouseAction = currentCameraProfile().cameraShortcut( button );
		
		if ( camMouseAction == null )
			camMouseAction = MouseAction.NO_MOUSE_ACTION;	
		
		return camMouseAction;
	}
	
	/**
	 * When the user clicks on the mouse: If a {@link #mouseGrabber()} is defined,
	 * {@link remixlab.proscene.MouseGrabber#mousePressed(Point, Camera)} is called.
	 * Otherwise, the {@link #camera()} or the {@link #interactiveFrame()} interprets
	 * the mouse displacements,	depending on mouse bindings.
	 * 
	 * @see #mouseDragged(MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {		
		if ( mouseGrabber() != null ) {
			if ( mouseGrabberIsAnIFrame ) {
				InteractiveFrame iFrame = (InteractiveFrame)mouseGrabber();
				//TODO: need to call the iFrame version of the method, but don't know how to do it!
				//need to hardcopy the methods!
				//if ( mouseGrabberIsAnICamFrame ) {					
				//	iFrame.startAction(action);					
				//	iFrame.mousePressed(event.getPoint(), camera());
				//}
				//else
				{
					iFrame.startAction(iFrameMouseAction(event), withConstraint);
					iFrame.mousePressed(event.getPoint(), camera());					
				}
			}
			else
				mouseGrabber().mousePressed(event.getPoint(), camera());
			return;
		}		
		if ( interactiveFrameIsDrawn() ) {
			interactiveFrame().startAction(iFrameMouseAction(event), withConstraint);
			interactiveFrame().mousePressed(event.getPoint(), camera());
			return;
		}		
		cameraMouseAction(event);//updates camMouseAction
		if ( camMouseAction == MouseAction.ZOOM_ON_REGION ) {
			fCorner = event.getPoint();
	    	lCorner = event.getPoint();
		}			
		if ( camMouseAction == MouseAction.SCREEN_ROTATE )
			fCorner = event.getPoint();			
		camera().frame().startAction(camMouseAction, withConstraint);
		camera().frame().mousePressed(event.getPoint(), camera());		
	}
	
	/**
	 * Mouse drag event is sent to the {@link #mouseGrabber()} (if any) or to the
	 * {@link #camera()} or the {@link #interactiveFrame()}, depending on mouse bindings.
	 * 
	 * @see #mouseMoved(MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {		
		if ( mouseGrabber() != null ) {
			mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), camera());
			if (mouseGrabber().grabsMouse())
				//if ( mouseGrabberIsAnICamFrame )
					//TODO: implement me
					//mouseGrabber().mouseMoveEvent(event, camera());
					//((InteractiveFrame)mouseGrabber()).mouseDragged(event.getPoint(), camera());
				//else
					mouseGrabber().mouseDragged(event.getPoint(), camera());
			else
				setMouseGrabber(null);
			return;
		}		
		if ( interactiveFrameIsDrawn() ) {
			interactiveFrame().mouseDragged(event.getPoint(), camera());
			return;
		}	
		if ( camMouseAction == MouseAction.ZOOM_ON_REGION )
			lCorner = event.getPoint();
		else {
			if ( camMouseAction == MouseAction.SCREEN_ROTATE )
				fCorner = event.getPoint();
			camera().frame().mouseDragged(event.getPoint(), camera());
		}
	}
	
	/**
	 * Calls the {@link #mouseGrabber()}, {@link #camera()} or
	 * {@link #interactiveFrame()} mouseReleaseEvent method.
	 */
	public void mouseReleased(MouseEvent event) {		
		if ( mouseGrabber() != null ) {
			//if ( mouseGrabberIsAnICamFrame )
    			//mouseGrabber().mouseReleaseEvent(event, camera());
    			//((InteractiveFrame)mouseGrabber()).mouseReleased(event.getPoint(), camera());
    		//else
    			mouseGrabber().mouseReleased(event.getPoint(), camera());
    		mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), camera());
    		if (!(mouseGrabber().grabsMouse()))
    			setMouseGrabber(null);
    		//iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
    		return;
		}		
		if ( interactiveFrameIsDrawn() ) {
    		interactiveFrame().mouseReleased(event.getPoint(), camera());
    		//iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
    		return;
		}
		
		if ( ( camMouseAction == MouseAction.ZOOM_ON_REGION ) || ( camMouseAction == MouseAction.SCREEN_ROTATE ) || ( camMouseAction == MouseAction.SCREEN_TRANSLATE ) )
			lCorner = event.getPoint();
		camera().frame().mouseReleased(event.getPoint(), camera());		
		camMouseAction = MouseAction.NO_MOUSE_ACTION;
		//iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
    }    
	
	/**
	 * Implements mouse click events: left button aligns scene,
	 * middle button shows entire scene, and right button centers scene.
	 */
	public void mouseClicked(MouseEvent e) {
		Button button = getButton(e);
		Integer numberOfClicks = e.getClickCount();
		PApplet.println("number of clicks: " + numberOfClicks);
		ClickAction ca = null;
		ca = clickShortcut(button, numberOfClicks);
		if(ca == null) {
			if(e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown() ) {
				if (e.isAltDown())
					ca = clickShortcut(button, Modifier.ALT, numberOfClicks);
				if (e.isAltGraphDown())
					ca = clickShortcut(button, Modifier.ALT_GRAPH, numberOfClicks);
				if (e.isControlDown())
					ca = clickShortcut(button, Modifier.CONTROL, numberOfClicks);
				if (e.isShiftDown())
					ca = clickShortcut(button, Modifier.SHIFT, numberOfClicks);
			}
		}
		
		if (ca == null)
			return;
		else {
			handleClickAction(ca);
		}
	}	
	
	// 10. Register draw method	
	
	/**
	 * Attempt to add a 'draw' handler method to the Scene. 
	 * The default event handler is a method that returns void and has one single
	 * Scene or PApplet parameter.
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addDrawHandler(Object obj, String methodName) {
		drawHandlerParamIsThis = true;
		try {
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { Scene.class } );
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (NoSuchMethodException mE) {
			drawHandlerParamIsThis = false;
			try {
				drawHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { PApplet.class } );
				drawHandlerObject = obj;
				drawHandlerMethodName = methodName;
			} catch (Exception e) {
				drawHandlerParamIsThis = null;
				PApplet.println("Something went wrong when registering your " + methodName + " method");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to the Scene).
	 * 
	 * @see #addDrawHandler(Object, String)
	 */
	public void removeDrawHandler() {
		drawHandlerParamIsThis = null;
		drawHandlerMethod = null;			
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}
	
	/**
	 * Returns {@code true} if the user has registered a 'draw' handler
	 * method to the Scene and {@code false} otherwise.
	 */
    public boolean hasRegisteredDraw() {
    	if (drawHandlerMethodName == null)
    		return false;
    	return true;
    }
    	
	// 11. Processing objects
    
	/**
	 * Sets the processing camera projection matrix from {@link #camera()}.
	 * Calls {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link remixlab.proscene.Camera#type()}. 
	 */
	protected void setPProjectionMatrix() {
		//compute the processing camera projection matrix from our camera() parameters
		switch (camera().type()) {
		case PERSPECTIVE:
			parent.perspective(camera().fieldOfView(), camera().aspectRatio(),
					           camera().zNear(), camera().zFar());
			break;
		case ORTHOGRAPHIC:
			float [] wh = camera().getOrthoWidthHeight();
			parent.ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());
			break;
		}
		//if our camera() matrices are detached from the processing Camera matrices,
		//we cache the processing camera projection matrix into our camera()
		camera().setProjectionMatrix(pg3d.projection);
		//camera().setProjectionMatrix(((PGraphics3D) parent.g).projection);
	}
	
	/**
	 * Sets the processing camera matrix from {@link #camera()}.
	 * Simply calls {@code PApplet.camera()}.
	 */
	protected void setPModelViewMatrix() {
		//compute the processing camera modelview matrix from our camera() parameters
		parent.camera(camera().position().x, camera().position().y, camera().position().z,
				      camera().at().x, camera().at().y, camera().at().z,
				      camera().upVector().x, camera().upVector().y, camera().upVector().z);
		//if our camera() matrices are detached from the processing Camera matrices,
		//we cache the processing camera modelview matrix into our camera()
		camera().setModelViewMatrix(pg3d.modelview);
		//camera().setProjectionMatrix(((PGraphics3D) parent.g).modelview);
	}
}
