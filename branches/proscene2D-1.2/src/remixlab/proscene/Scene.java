/**
 *                     ProScene (version 1.1.90)      
 *    Copyright (c) 2010-2012 by National University of Colombia
 *                 @author Jean Pierre Charalambos      
 *           http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
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
import processing.opengl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A 3D interactive Processing scene.
 * <p>
 * A Scene has a full reach Camera, it can be used for on-screen or off-screen
 * rendering purposes (see the different constructors), and it has two means to
 * manipulate objects: an {@link #interactiveFrame()} single instance (which by
 * default is null) and a {@link #mouseGrabber()} pool.
 * <h3>Usage</h3>
 * To use a Scene you have three choices:
 * <ol>
 * <li><b>Direct instantiation</b>. In this case you should instantiate your own
 * Scene object at the {@code PApplet.setup()} function.
 * See the example <i>BasicUse</i>.
 * <li><b>Inheritance</b>. In this case, once you declare a Scene derived class,
 * you should implement {@link #proscenium()} which defines the objects in your
 * scene. Just make sure to define the {@code PApplet.draw()} method, even if
 * it's empty. See the example <i>AlternativeUse</i>.
 * <li><b>External draw handler registration</b>. You can even declare an
 * external drawing method and then register it at the Scene with
 * {@link #addDrawHandler(Object, String)}. That method should return {@code
 * void} and have one single {@code Scene} parameter. This strategy may be useful
 * when there are multiple viewers sharing the same drawing code. See the
 * example <i>StandardCamera</i>.
 * </ol>
 * <h3>Interactivity mechanisms</h3>
 * Proscene provides two interactivity mechanisms to manage your scene: global
 * keyboard shortcuts and camera profiles.
 * <ol>
 * <li><b>Global keyboard shortcuts</b> provide global configuration options
 * such as {@link #drawGrid()} or {@link #drawAxis()} that are common among
 * the different registered camera profiles. To define a global keyboard shortcut use
 * {@link #setShortcut(Character, KeyboardAction)} or one of its different forms.
 * Check {@link #setDefaultShortcuts()} to see the default global keyboard shortcuts.
 * <li><b>Camera profiles</b> represent a set of camera keyboard shortcuts, and camera and
 * frame mouse bindings which together represent a "camera mode". The scene provide
 * high-level methods to manage camera profiles such as
 * {@link #registerCameraProfile(CameraProfile)},
 * {@link #unregisterCameraProfile(CameraProfile)} or {@link #currentCameraProfile()}
 * among others. To perform the configuration of a camera profile see the CameraProfile
 * class documentation.
 * </ol>
 * <h3>Animation mechanisms</h3>
 * Proscene provides three animation mechanisms to define how your scene evolves
 * over time:
 * <ol>
 * <li><b>Overriding the {@link #animate()} method.</b>  In this case, once you
 * declare a Scene derived class, you should implement {@link #animate()} which
 * defines how your scene objects evolve over time. See the example <i>Animation</i>.
 * <li><b>External animation handler registration.</b> You can also declare an
 * external animation method and then register it at the Scene with
 * {@link #addAnimationHandler(Object, String)}. That method should return {@code
 * void} and have one single {@code Scene} parameter. See the example
 * <i>AnimationHandler</i>.
 * <li><b>By querying the state of the {@link #animatedFrameWasTriggered} variable.</b>
 * During the drawing loop, the variable {@link #animatedFrameWasTriggered} is set
 * to {@code true} each time an animated frame is triggered (and to {@code false}
 * otherwise), which is useful to notify the outside world when an animation event
 * occurs. See the example <i>Flock</i>.
 */
public class Scene extends AbstractScene {	
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
		/** Toggles camera type (orthographic or perspective. */
		CAMERA_TYPE("Toggles camera type (orthographic or perspective)"),
		/** Toggles camera kind (proscene or standard). */
		CAMERA_KIND("Toggles camera kind (proscene or standard)"),
		/** Toggles animation. */
		ANIMATION("Toggles animation"),
		/** Set the arcball reference point from the pixel under the mouse. */
		ARP_FROM_PIXEL("Set the arcball reference point from the pixel under the mouse"),
		/** Reset the arcball reference point to the 3d frame world origin. */
		RESET_ARP("Reset the arcball reference point to the 3d frame world origin"),
		/** Displays the global help. */
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
		/** Set the arcball reference point from the pixel under the mouse */
		ARP_FROM_PIXEL("Set the arcball reference point from the pixel under the mouse"),
		/** Reset the arcball reference point to the 3d frame world origin */
		RESET_ARP("Reset the arcball reference point to the 3d frame world origin"),
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
		/** Move forward frame (camera or interactive frame). */
		MOVE_FORWARD("Move forward frame (camera or interactive frame)"),
		/** move backward frame (camera or interactive frame). */
		MOVE_BACKWARD("move backward frame (camera or interactive frame)"),
		/** Look around with frame (camera or interactive drivable frame). */
		LOOK_AROUND("Look around with frame (camera or interactive drivable frame)"),
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

	// mouse actions
	protected boolean arpFlag;
	protected boolean pupFlag;
	protected PVector pupVec;

	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S	
	protected Frame tmpFrame;

	// O B J E C T S		

	// S C R E E N C O O R D I N A T E S	
	protected float zC;		

	/**
	 * Constructor that defines an on-screen Scene (the one that most likely
	 * would just fulfill all of your needs). All viewer parameters (display flags,
	 * scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation. This is actually just a convenience
	 * function that simply calls {@code this(p, (PGraphicsOpenGL) p.g)}. Call any
	 * other constructor by yourself to possibly define an off-screen Scene.
	 * 
	 * @see #Scene(PApplet, PGraphicsOpenGL)
	 * @see #Scene(PApplet, PGraphicsOpenGL, int, int)
	 */	
	public Scene(PApplet p) {
		this(p, (PGraphicsOpenGL) p.g);
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
	 * @see #Scene(PApplet, PGraphicsOpenGL, int, int)
	 */
	public Scene(PApplet p, PGraphicsOpenGL renderer) {
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
	 * @see #Scene(PApplet, PGraphicsOpenGL)
	 */
	public Scene(PApplet p, PGraphicsOpenGL renderer, int x, int y) {
		parent = p;
		pg = renderer;
		width = pg.width;
		height = pg.height;
		
		space = Space.THREE_D;
		
		tmpFrame = new Frame();
		
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
		
		arpFlag = false;
		pupFlag = false;

		withConstraint = true;

		setAxisIsDrawn(true);
		setGridIsDrawn(true);
		setFrameSelectionHintIsDrawn(false);
		setCameraPathsAreDrawn(false);
		
		disableFrustumEquationsUpdate();

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

	// 2. Associated objects	
	
	/**
	 * Sets the avatar object to be tracked by the Camera when
	 * {@link #currentCameraProfile()} is an instance of ThirdPersonCameraProfile.
	 * 
	 * @see #unsetAvatar()
	 */
	@Override
	public void setAvatar(Trackable t) {
		trck = t;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveDrivableFrame = false;
		if (avatar() instanceof InteractiveAvatarFrame) {
			avatarIsInteractiveAvatarFrame = true;
			avatarIsInteractiveDrivableFrame = true;
			if (interactiveFrame() != null)
				((InteractiveDrivableFrame) interactiveFrame()).setFlySpeed(0.01f * radius());
		} else if (avatar() instanceof InteractiveDrivableFrame) {
			avatarIsInteractiveAvatarFrame = false;
			avatarIsInteractiveDrivableFrame = true;
			if (interactiveFrame() != null)
				((InteractiveDrivableFrame) interactiveFrame()).setFlySpeed(0.01f * radius());
		}
	}

	// 4. State of the viewer	

	/**
	 * Toggles the {@link #camera()} type between PERSPECTIVE and ORTHOGRAPHIC.
	 */
	public void toggleCameraType() {
		if (camera().type() == Camera.Type.PERSPECTIVE)
			setCameraType(Camera.Type.ORTHOGRAPHIC);
		else
			setCameraType(Camera.Type.PERSPECTIVE);
	}

	/**
	 * Toggles the {@link #camera()} kind between PROSCENE and STANDARD.
	 */
	public void toggleCameraKind() {
		if (camera().kind() == Camera.Kind.PROSCENE)
			setCameraKind(Camera.Kind.STANDARD);
		else
			setCameraKind(Camera.Kind.PROSCENE);
	}	
	
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
		if (type != camera().type())
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
		if (kind != camera().kind()) {
			camera().setKind(kind);
			if (kind == Camera.Kind.PROSCENE)
				PApplet.println("Changing camera kind to Proscene");
			else
				PApplet.println("Changing camera kind to Standard");
		}
	}	
	
	/**
	 * Toggles the {@link #interactiveFrame()} interactivity on and off.
	 */
	public void toggleDrawInteractiveFrame() {
		if (interactiveFrameIsDrawn())
			setDrawInteractiveFrame(false);
		else
			setDrawInteractiveFrame(true);
	}
	
	/**
	 * Convenience function that simply calls {@code setDrawInteractiveFrame(true)}.
	 * 
	 * @see #setDrawInteractiveFrame(boolean)
	 */
	public void setDrawInteractiveFrame() {
		setDrawInteractiveFrame(true);
	}

	/**
	 * Sets the interactivity to the Scene {@link #interactiveFrame()} instance
	 * according to {@code draw}
	 */
	public void setDrawInteractiveFrame(boolean draw) {
		if (draw && (glIFrame == null))
			return;
		if (!draw && (currentCameraProfile().mode() == CameraProfile.Mode.THIRD_PERSON)
				&& interactiveFrame().equals(avatar()))// more natural than to bypass it
			return;
		iFrameIsDrwn = draw;
	}
	
	// 5. Drawing methods

	/**
	 * Internal use. Display various on-screen visual hints to be called from {@link #pre()}
	 * or {@link #draw()}.
	 */
	@Override
	protected void displayVisualHints() {		
		if (frameSelectionHintIsDrawn())
			drawSelectionHints();
		if (cameraPathsAreDrawn()) {
			camera().drawAllPaths();
			drawCameraPathSelectionHints();
		} else {
			camera().hideAllPaths();
		}
		if (dE.camMouseAction == MouseAction.ZOOM_ON_REGION)
			drawZoomWindowHint();
		if (dE.camMouseAction == MouseAction.SCREEN_ROTATE)
			drawScreenRotateLineHint();
		if (arpFlag)
			drawArcballReferencePointHint();
		if (pupFlag) {
			PVector v = camera().projectedCoordinatesOf(pupVec);
			drawCross(v.x, v.y);
		}
	}
	
	/**
	 * Bind processing matrices to proscene matrices.
	 */
	@Override
	protected void bindMatrices() {
		// We set the processing camera matrices from our remixlab.proscene.Camera
		setPProjectionMatrix();
		setPModelViewMatrix();
		// same as the two previous lines:
		// WARNING: this can produce visual artifacts when using OPENGL and
		// GLGRAPHICS renderers because
		// processing will anyway set the matrices at the end of the rendering
		// loop.
		// camera().computeProjectionMatrix();
		// camera().computeModelViewMatrix();
		camera().cacheMatrices();
	}	
	
	/**
	 * Returns the renderer context linked to this scene. 
	 * 
	 * @return PGraphicsOpenGL renderer.
	 */
	public PGraphicsOpenGL renderer() {
		return (PGraphicsOpenGL) pg;
	}
	
	// 4. Scene dimensions

	/**
	 * Sets the {@link #center()} and {@link #radius()} of the Scene from the
	 * {@code min} and {@code max} PVectors.
	 * <p>
	 * Convenience wrapper function that simply calls {@code
	 * camera().setSceneBoundingBox(min,max)}
	 * 
	 * @see #setRadius(float)
	 * @see #setCenter(PVector)
	 */
	public void setBoundingBox(PVector min, PVector max) {
		camera().setSceneBoundingBox(min, max);
	}
	
	//TODO how to handle in 2D?
	
	/**
	 * Returns the arcball reference point.
	 * <p>
	 * Convenience wrapper function that simply returns {@code
	 * camera().arcballReferencePoint()}
	 * 
	 * @see #setCenter(PVector) {@link #radius()}
	 */
	public PVector arcballReferencePoint() {
		return camera().arcballReferencePoint();
	}
	
	//TODO how to handle in 2D?
	
	/**
	 * Convenience wrapper function that simply returns {@code
	 * camera().setArcballReferencePointFromPixel(pixel)}.
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

	// 6. Display of visual hints and Display methods
	
	/**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the {@link #renderer()} 
	 * positive {@code z} axis.
	 * <p>
	 * Code adapted from http://www.processingblogs.org/category/processing-java/
	 * 
	 * @see #hollowCylinder(int, float, float, PVector, PVector)
	 */
	public void cylinder(float w, float h) {
		float px, py;

		pg.beginShape(QUAD_STRIP);
		for (float i = 0; i < 13; i++) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			pg.vertex(px, py, 0);
			pg.vertex(px, py, h);
		}
		pg.endShape();

		pg.beginShape(TRIANGLE_FAN);
		pg.vertex(0, 0, 0);
		for (float i = 12; i > -1; i--) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			pg.vertex(px, py, 0);
		}
		pg.endShape();

		pg.beginShape(TRIANGLE_FAN);
		pg.vertex(0, 0, h);
		for (float i = 0; i < 13; i++) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			pg.vertex(px, py, h);
		}
		pg.endShape();
	}
	
	/**
	 * Convenience function that simply calls
	 * {@code hollowCylinder(20, w, h, new PVector(0,0,-1), new PVector(0,0,1))}.
	 * 
	 * @see #hollowCylinder(int, float, float, PVector, PVector)
	 * @see #cylinder(float, float)
	 */
	public void hollowCylinder(float w, float h) {
		this.hollowCylinder(20, w, h, new PVector(0,0,-1), new PVector(0,0,1));
	}
	
	/**
	 * Convenience function that simply calls
	 * {@code hollowCylinder(detail, w, h, new PVector(0,0,-1), new PVector(0,0,1))}.
	 * 
	 * @see #hollowCylinder(int, float, float, PVector, PVector)
	 * @see #cylinder(float, float)
	 */
	public void hollowCylinder(int detail, float w, float h) {
		this.hollowCylinder(detail, w, h, new PVector(0,0,-1), new PVector(0,0,1));
	}
 
	/**
	 * Draws a cylinder whose bases are formed by two cutting planes ({@code m}
	 * and {@code n}), along the {@link #renderer()} positive {@code z} axis.
	 * 
	 * @param detail
	 * @param w radius of the cylinder and h is its height
	 * @param h height of the cylinder
	 * @param m normal of the plane that intersects the cylinder at z=0
	 * @param n normal of the plane that intersects the cylinder at z=h
	 * 
	 * @see #cylinder(float, float)
	 */
	public void hollowCylinder(int detail, float w, float h, PVector m, PVector n) {
		//eqs taken from: http://en.wikipedia.org/wiki/Line-plane_intersection
		PVector pm0 = new PVector(0,0,0);
		PVector pn0 = new PVector(0,0,h);
		PVector l0 = new PVector();		
		PVector l = new PVector(0,0,1);
		PVector p = new PVector();
		float x,y,d;		
		
		pg.noStroke();
		pg.beginShape(QUAD_STRIP);
		
		for (float t = 0; t <= detail; t++) {
			x = w * PApplet.cos(t * TWO_PI/detail);
			y = w * PApplet.sin(t * TWO_PI/detail);
			l0.set(x,y,0);
			
			d = ( m.dot(PVector.sub(pm0, l0)) )/( l.dot(m) );
			p =  PVector.add( PVector.mult(l, d), l0 );
			pg.vertex(p.x, p.y, p.z);
			
			l0.z = h;
			d = ( n.dot(PVector.sub(pn0, l0)) )/( l.dot(n) );
			p =  PVector.add( PVector.mult(l, d), l0 );
			pg.vertex(p.x, p.y, p.z);
		}
		pg.endShape();
	}
	
	/**
	 * Same as {@code cone(det, 0, 0, r, h);}
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public void cone(int det, float r, float h) {
		cone(det, 0, 0, r, h);
	}		
	
	/**
	 * Same as {@code cone(12, 0, 0, r, h);}
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public void cone(float r, float h) {
		cone(12, 0, 0, r, h);
	}			
	
	/**
	 * Draws a cone along the {@link #renderer()} positive {@code z} axis, with its
	 * base centered at {@code (x,y)}, height {@code h}, and radius {@code r}.
	 * <p>
	 * The code of this function was adapted from
	 * http://processinghacks.com/hacks:cone Thanks to Tom Carden.
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public void cone(int detail, float x, float y, float r, float h) {
		float unitConeX[] = new float[detail + 1];
		float unitConeY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = TWO_PI * i / detail;
			unitConeX[i] = r * (float) Math.cos(a1);
			unitConeY[i] = r * (float) Math.sin(a1);
		}

		pg.pushMatrix();
		pg.translate(x, y);
		pg.beginShape(TRIANGLE_FAN);
		pg.vertex(0, 0, h);
		for (int i = 0; i <= detail; i++) {
			pg.vertex(unitConeX[i], unitConeY[i], 0.0f);
		}
		pg.endShape();
		pg.popMatrix();
	}
	
	/**
	 * Same as {@code cone(det, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public void cone(int det, float r1, float r2, float h) {
		cone(det, 0, 0, r1, r2, h);
	}	
	
	/**
	 * Same as {@code cone(18, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public void cone(float r1, float r2, float h) {
		cone(18, 0, 0, r1, r2, h);
	}

	/**
	 * Draws a truncated cone along the {@link #renderer()} positive {@code z} axis,
	 * with its base centered at {@code (x,y)}, height {@code h}, and radii
	 * {@code r1} and {@code r2} (basis and height respectively).
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public void cone(int detail, float x, float y,	float r1, float r2, float h) {
		float firstCircleX[] = new float[detail + 1];
		float firstCircleY[] = new float[detail + 1];
		float secondCircleX[] = new float[detail + 1];
		float secondCircleY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = TWO_PI * i / detail;
			firstCircleX[i] = r1 * (float) Math.cos(a1);
			firstCircleY[i] = r1 * (float) Math.sin(a1);
			secondCircleX[i] = r2 * (float) Math.cos(a1);
			secondCircleY[i] = r2 * (float) Math.sin(a1);
		}

		pg.pushMatrix();
		pg.translate(x, y);
		pg.beginShape(QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			pg.vertex(firstCircleX[i], firstCircleY[i], 0);
			pg.vertex(secondCircleX[i], secondCircleY[i], h);
		}
		pg.endShape();
		pg.popMatrix();
	}	
	
	/**
	 * Convenience function that simply calls {@code drawAxis(100)}.
	 */
	public void drawAxis() {
		drawAxis(100);
	}		
	
	/**
	 * Draws an axis of length {@code length} which origin correspond to the
	 * {@link #renderer()}'s world coordinate system origin.
	 * 
	 * @see #drawGrid(float, int)
	 */
	@Override
	public void drawAxis(float length) {
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;

		// pg3d.noLights();

		pg.pushStyle();
		
		pg.beginShape(LINES);		
		pg.strokeWeight(2);
		// The X
		pg.stroke(255, 178, 178);
		pg.vertex(charShift, charWidth, -charHeight);
		pg.vertex(charShift, -charWidth, charHeight);
		pg.vertex(charShift, -charWidth, -charHeight);
		pg.vertex(charShift, charWidth, charHeight);
		// The Y
		pg.stroke(178, 255, 178);
		pg.vertex(charWidth, charShift, charHeight);
		pg.vertex(0.0f, charShift, 0.0f);
		pg.vertex(-charWidth, charShift, charHeight);
		pg.vertex(0.0f, charShift, 0.0f);
		pg.vertex(0.0f, charShift, 0.0f);
		pg.vertex(0.0f, charShift, -charHeight);
		// The Z
		pg.stroke(178, 178, 255);
		
		//left_handed
		pg.vertex(-charWidth, -charHeight, charShift);
		pg.vertex(charWidth, -charHeight, charShift);
		pg.vertex(charWidth, -charHeight, charShift);
		pg.vertex(-charWidth, charHeight, charShift);
		pg.vertex(-charWidth, charHeight, charShift);
		pg.vertex(charWidth, charHeight, charShift);
	  //right_handed coordinate system should go like this:
		//pg3d.vertex(-charWidth, charHeight, charShift);
		//pg3d.vertex(charWidth, charHeight, charShift);
		//pg3d.vertex(charWidth, charHeight, charShift);
		//pg3d.vertex(-charWidth, -charHeight, charShift);
		//pg3d.vertex(-charWidth, -charHeight, charShift);
		//pg3d.vertex(charWidth, -charHeight, charShift);
		
		pg.endShape();

		// Z axis
		pg.noStroke();
		pg.fill(178, 178, 255);
		drawArrow(length, 0.01f * length);

		// X Axis
		pg.fill(255, 178, 178);
		pg.pushMatrix();
		pg.rotateY(HALF_PI);
		drawArrow(length, 0.01f * length);
		pg.popMatrix();

		// Y Axis
		pg.fill(178, 255, 178);
		pg.pushMatrix();
		pg.rotateX(-HALF_PI);
		drawArrow(length, 0.01f * length);
		pg.popMatrix();

		pg.popStyle();
	}			
	
	/**
	 * Simply calls {@code drawArrow(length, 0.05f * length)}
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(float length) {
		drawArrow(length, 0.05f * length);
	}		
	
	/**
	 * Draws a 3D arrow along the {@link #renderer()} positive Z axis.
	 * <p>
	 * {@code length} and {@code radius} define its geometry.
	 * <p>
	 * Use {@link #drawArrow(PVector, PVector, float)} to place the arrow
	 * in 3D.
	 */
	public void drawArrow(float length, float radius) {
		float head = 2.5f * (radius / length) + 0.1f;
		float coneRadiusCoef = 4.0f - 5.0f * head;

		cylinder(radius, length * (1.0f - head / coneRadiusCoef));
		pg.translate(0.0f, 0.0f, length * (1.0f - head));
		cone(coneRadiusCoef * radius, head * length);
		pg.translate(0.0f, 0.0f, -length * (1.0f - head));
	}		
	
	/**
	 * Draws a 3D arrow between the 3D point {@code from} and the 3D point {@code
	 * to}, both defined in the current {@link #renderer()} ModelView coordinates
	 * system.
	 * 
	 * @see #drawArrow(float, float)
	 */
	public void drawArrow(PVector from, PVector to,	float radius) {
		pg.pushMatrix();
		pg.translate(from.x, from.y, from.z);
		pg.applyMatrix(new Quaternion(new PVector(0, 0, 1), PVector.sub(to,
				from)).matrix());
		drawArrow(PVector.sub(to, from).mag(), radius);
		pg.popMatrix();
	}			
	
	/**
	 * Convenience function that simply calls {@code drawGrid(100, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid() {
		drawGrid(100, 10);
	}	
	
	/**
	 * Convenience function that simply calls {@code drawGrid(size, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	@Override
	public void drawGrid(float size) {
		drawGrid(size, 10);
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(100,
	 * nbSubdivisions)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public void drawGrid(int nbSubdivisions) {
		drawGrid(100, nbSubdivisions);
	}

	/**
	 * Draws a grid in the XY plane, centered on (0,0,0) (defined in the current
	 * coordinate system).
	 * <p>
	 * {@code size} (processing scene units) and {@code nbSubdivisions} define its
	 * geometry.
	 * 
	 * @see #drawAxis(float)
	 */	
	public void drawGrid(float size, int nbSubdivisions) {
		pg.pushStyle();
		pg.stroke(170, 170, 170);
		pg.strokeWeight(1);
		pg.beginShape(LINES);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
			pg.vertex(pos, -size);
			pg.vertex(pos, +size);
			pg.vertex(-size, pos);
			pg.vertex(size, pos);
		}
		pg.endShape();
		pg.popStyle();
	}
	
	// 2. CAMERA

	/**
	 * Convenience function that simply calls {@code drawCamera(camera,
	 * 170, true, 1.0f)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera) {
		drawCamera(camera, 170, true, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(camera,
	 * 170, true, scale)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera, float scale) {
		drawCamera(camera, 170, true, scale);
	}
	
	/**
	 * Convenience function that simply calls {@code drawCamera(camera,
	 * color, true, 1.0f)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera, int color) {
		drawCamera(camera, color, true, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(camera,
	 * 170, drawFarPlane, 1.0f)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera,	boolean drawFarPlane) {
		drawCamera(camera, 170, drawFarPlane, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(camera, 170, drawFarPlane, scale)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera,	boolean drawFarPlane, float scale) {
		drawCamera(camera, 170, drawFarPlane, scale);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(camera, color, true, scale)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera, int color,	float scale) {
		drawCamera(camera, color, true, scale);
	}
	
	/**
	 * Convenience function that simply calls {@code drawCamera(camera,
	 * color, drawFarPlane, 1.0f)}
	 * 
	 * @see #drawCamera(Camera, int, boolean, float)
	 */
	public void drawCamera(Camera camera, int color,	boolean drawFarPlane) {
		drawCamera(camera, color, drawFarPlane, 1.0f);
	}

	/**
	 * Draws a representation of the {@code camera} in the {@link #renderer()} 3D
	 * virtual world using {@code color}.
	 * <p>
	 * The near and far planes are drawn as quads, the frustum is drawn using
	 * lines and the camera up vector is represented by an arrow to disambiguate
	 * the drawing.
	 * <p>
	 * When {@code drawFarPlane} is {@code false}, only the near plane is drawn.
	 * {@code scale} can be used to scale the drawing: a value of 1.0 (default)
	 * will draw the Camera's frustum at its actual size.
	 * <p>
	 * <b>Note:</b> The drawing of a Scene's own Scene.camera() should not be
	 * visible, but may create artifacts due to numerical imprecisions.
	 */
	public void drawCamera(Camera camera, int color, boolean drawFarPlane, float scale) {
		pg.pushMatrix();

		// pg3d.applyMatrix(camera.frame().worldMatrix());
		// same as the previous line, but maybe more efficient
		tmpFrame.fromMatrix(camera.frame().worldMatrix());
		tmpFrame.applyTransformation((PGraphicsOpenGL)pg);

		// 0 is the upper left coordinates of the near corner, 1 for the far one
		PVector[] points = new PVector[2];
		points[0] = new PVector();
		points[1] = new PVector();

		points[0].z = scale * camera.zNear();
		points[1].z = scale * camera.zFar();

		switch (camera.type()) {
		case PERSPECTIVE: {
			points[0].y = points[0].z * PApplet.tan(camera.fieldOfView() / 2.0f);
			points[0].x = points[0].y * camera.aspectRatio();
			float ratio = points[1].z / points[0].z;
			points[1].y = ratio * points[0].y;
			points[1].x = ratio * points[0].x;
			break;
		}
		case ORTHOGRAPHIC: {
			float[] wh = camera.getOrthoWidthHeight();
			points[0].x = points[1].x = scale * wh[0];
			points[0].y = points[1].y = scale * wh[1];
			break;
		}
		}

		int farIndex = drawFarPlane ? 1 : 0;
		
   	// Frustum lines
		pg.stroke(color);
		pg.strokeWeight(2);
		switch (camera.type()) {
		case PERSPECTIVE:
			pg.beginShape(PApplet.LINES);
			pg.vertex(0.0f, 0.0f, 0.0f);
			pg.vertex(points[farIndex].x, points[farIndex].y, -points[farIndex].z);
			pg.vertex(0.0f, 0.0f, 0.0f);
			pg.vertex(-points[farIndex].x, points[farIndex].y, -points[farIndex].z);
			pg.vertex(0.0f, 0.0f, 0.0f);
			pg.vertex(-points[farIndex].x, -points[farIndex].y,	-points[farIndex].z);
			pg.vertex(0.0f, 0.0f, 0.0f);
			pg.vertex(points[farIndex].x, -points[farIndex].y, -points[farIndex].z);
			pg.endShape();
			break;
		case ORTHOGRAPHIC:
			if (drawFarPlane) {
				pg.beginShape(PApplet.LINES);
				pg.vertex(points[0].x, points[0].y, -points[0].z);
				pg.vertex(points[1].x, points[1].y, -points[1].z);
				pg.vertex(-points[0].x, points[0].y, -points[0].z);
				pg.vertex(-points[1].x, points[1].y, -points[1].z);
				pg.vertex(-points[0].x, -points[0].y, -points[0].z);
				pg.vertex(-points[1].x, -points[1].y, -points[1].z);
				pg.vertex(points[0].x, -points[0].y, -points[0].z);
				pg.vertex(points[1].x, -points[1].y, -points[1].z);
				pg.endShape();
				}
			}
		
		// Near and (optionally) far plane(s)
		pg.pushStyle();
		pg.noStroke();
		pg.fill(color);
		pg.beginShape(PApplet.QUADS);
		for (int i = farIndex; i >= 0; --i) {
			pg.normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
			pg.vertex(points[i].x, points[i].y, -points[i].z);
			pg.vertex(-points[i].x, points[i].y, -points[i].z);
			pg.vertex(-points[i].x, -points[i].y, -points[i].z);
			pg.vertex(points[i].x, -points[i].y, -points[i].z);
		}
		pg.endShape();

		// Up arrow
		float arrowHeight = 1.5f * points[0].y;
		float baseHeight = 1.2f * points[0].y;
		float arrowHalfWidth = 0.5f * points[0].x;
		float baseHalfWidth = 0.3f * points[0].x;

		// pg3d.noStroke();
		pg.fill(color);
		// Base
		pg.beginShape(PApplet.QUADS);
		
		pg.vertex(-baseHalfWidth, -points[0].y, -points[0].z);
		pg.vertex(baseHalfWidth, -points[0].y, -points[0].z);
		pg.vertex(baseHalfWidth, -baseHeight, -points[0].z);
		pg.vertex(-baseHalfWidth, -baseHeight, -points[0].z);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(-baseHalfWidth, points[0].y, -points[0].z);
		//pg3d.vertex(baseHalfWidth, points[0].y, -points[0].z);
		//pg3d.vertex(baseHalfWidth, baseHeight, -points[0].z);
		//pg3d.vertex(-baseHalfWidth, baseHeight, -points[0].z);
		
		pg.endShape();

		// Arrow
		pg.fill(color);
		pg.beginShape(PApplet.TRIANGLES);
		
		pg.vertex(0.0f, -arrowHeight, -points[0].z);
		pg.vertex(-arrowHalfWidth, -baseHeight, -points[0].z);
		pg.vertex(arrowHalfWidth, -baseHeight, -points[0].z);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(0.0f, arrowHeight, -points[0].z);
		//pg3d.vertex(-arrowHalfWidth, baseHeight, -points[0].z);
		//pg3d.vertex(arrowHalfWidth, baseHeight, -points[0].z);
		
		pg.endShape();		

		pg.popStyle();

		pg.popMatrix();
	}

	// 3. KEYFRAMEINTERPOLATOR CAMERA
	
	@Override
	public void drawPath(List<Frame> path, int mask, int nbFrames, int nbSteps, float scale) {
		if (mask != 0) {
			renderer().pushStyle();
			renderer().strokeWeight(2);

			if ((mask & 1) != 0) {
				renderer().noFill();
				renderer().stroke(170);
				renderer().beginShape();
				for (Frame myFr : path)
					renderer().vertex(myFr.position().x, myFr.position().y, myFr.position().z);
				renderer().endShape();
			}
			if ((mask & 6) != 0) {
				int count = 0;
				if (nbFrames > nbSteps)
					nbFrames = nbSteps;
				float goal = 0.0f;

				for (Frame myFr : path)
					if ((count++) >= goal) {
						goal += nbSteps / (float) nbFrames;
						renderer().pushMatrix();
						
					  //applyTransformation(myFr);
						myFr.applyTransformation((PGraphicsOpenGL)pg);						

						if ((mask & 2) != 0)
							drawKFICamera(scale);
						if ((mask & 4) != 0)
							drawAxis(scale / 10.0f);

						renderer().popMatrix();
					}
			}
			renderer().popStyle();
		}
	}

	public void drawKFICamera(float scale) {
		drawKFICamera(170, scale);
	}

	public void drawKFICamera(int color, float scale) {
		float halfHeight = scale * 0.07f;
		float halfWidth = halfHeight * 1.3f;
		float dist = halfHeight / PApplet.tan(PApplet.PI / 8.0f);

		float arrowHeight = 1.5f * halfHeight;
		float baseHeight = 1.2f * halfHeight;
		float arrowHalfWidth = 0.5f * halfWidth;
		float baseHalfWidth = 0.3f * halfWidth;

		// Frustum outline
		pg.pushStyle();

		pg.noFill();
		pg.stroke(color);
		pg.beginShape();
		pg.vertex(-halfWidth, halfHeight, -dist);
		pg.vertex(-halfWidth, -halfHeight, -dist);
		pg.vertex(0.0f, 0.0f, 0.0f);
		pg.vertex(halfWidth, -halfHeight, -dist);
		pg.vertex(-halfWidth, -halfHeight, -dist);
		pg.endShape();
		pg.noFill();
		pg.beginShape();
		pg.vertex(halfWidth, -halfHeight, -dist);
		pg.vertex(halfWidth, halfHeight, -dist);
		pg.vertex(0.0f, 0.0f, 0.0f);
		pg.vertex(-halfWidth, halfHeight, -dist);
		pg.vertex(halfWidth, halfHeight, -dist);
		pg.endShape();

		// Up arrow
		pg.noStroke();
		pg.fill(color);
		// Base
		pg.beginShape(PApplet.QUADS);
		
		pg.vertex(baseHalfWidth, -halfHeight, -dist);
		pg.vertex(-baseHalfWidth, -halfHeight, -dist);
		pg.vertex(-baseHalfWidth, -baseHeight, -dist);
		pg.vertex(baseHalfWidth, -baseHeight, -dist);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(-baseHalfWidth, halfHeight, -dist);
		//pg3d.vertex(baseHalfWidth, halfHeight, -dist);
		//pg3d.vertex(baseHalfWidth, baseHeight, -dist);
		//pg3d.vertex(-baseHalfWidth, baseHeight, -dist);
		
		pg.endShape();
		// Arrow
		pg.beginShape(PApplet.TRIANGLES);
		
		pg.vertex(0.0f, -arrowHeight, -dist);
		pg.vertex(arrowHalfWidth, -baseHeight, -dist);
		pg.vertex(-arrowHalfWidth, -baseHeight, -dist);
	  //right_handed coordinate system should go like this:
		//pg3d.vertex(0.0f, arrowHeight, -dist);
		//pg3d.vertex(-arrowHalfWidth, baseHeight, -dist);
		//pg3d.vertex(arrowHalfWidth, baseHeight, -dist);
		
		pg.endShape();

		pg.popStyle();
	}

	/**
	 * Draws a rectangle on the screen showing the region where a zoom operation
	 * is taking place.
	 */
	protected void drawZoomWindowHint() {
		float p1x = (float) dE.fCorner.getX();
		float p1y = (float) dE.fCorner.getY();
		float p2x = (float) dE.lCorner.getX();
		float p2y = (float) dE.lCorner.getY();
		beginScreenDrawing();
		PVector p1 = coords(new Point(p1x, p1y));
		PVector p2 = coords(new Point(p2x, p2y));
		PVector p3 = coords(new Point(p2x, p1y));
		PVector p4 = coords(new Point(p1x, p2y));
		pg.pushStyle();
		pg.stroke(255, 255, 255);
		pg.strokeWeight(2);
		pg.noFill();
		pg.beginShape();
		pg.vertex(p1.x, p1.y, p1.z);
		pg.vertex(p3.x, p3.y, p3.z);//p3
		pg.vertex(p2.x, p2.y, p2.z);
		pg.vertex(p4.x, p4.y, p4.z);//p4
		pg.endShape(CLOSE);
		pg.popStyle();
		endScreenDrawing();
	}

	/**
	 * Draws visual hint (a line on the screen) when a screen rotation is taking
	 * place.
	 */
	protected void drawScreenRotateLineHint() {
		float p1x = (float) dE.fCorner.getX();
		float p1y = (float) dE.fCorner.getY();
		PVector p2 = camera().projectedCoordinatesOf(arcballReferencePoint());
		beginScreenDrawing();
		PVector p1s = coords(new Point(p1x, p1y));
		PVector p2s = coords(new Point(p2.x, p2.y));
		pg.pushStyle();
		pg.stroke(255, 255, 255);
		pg.strokeWeight(2);
		pg.noFill();
		pg.beginShape(LINE);
		pg.vertex(p1s.x, p1s.y, p1s.z);
		pg.vertex(p2s.x, p2s.y, p2s.z);
		pg.endShape();
		pg.popStyle();
		endScreenDrawing();
	}

	/**
	 * Draws visual hint (a cross on the screen) when the
	 * {@link #arcballReferencePoint()} is being set.
	 * <p>
	 * Simply calls {@link #drawCross(float, float)} on {@code
	 * camera().projectedCoordinatesOf(arcballReferencePoint())} {@code x} and
	 * {@code y} coordinates.
	 * 
	 * @see #drawCross(float, float)
	 */
	protected void drawArcballReferencePointHint() {
		PVector p = camera().projectedCoordinatesOf(arcballReferencePoint());
		drawCross(p.x, p.y);
	}

	/**
	 * Draws all InteractiveFrames' selection regions: a shooter target
	 * visual hint of {@link remixlab.proscene.InteractiveFrame#grabsMouseThreshold()} pixels size.
	 * 
	 * <b>Attention:</b> If the InteractiveFrame is part of a Camera path draws
	 * nothing.
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
	protected void drawSelectionHints() {
		for (MouseGrabbable mg : MouseGrabberPool) {
			if(mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				if (!iF.isInCameraPath()) {
					PVector center = camera().projectedCoordinatesOf(iF.position());
					if (mg.grabsMouse())
						drawShooterTarget(pg.color(0, 255, 0), center, (iF.grabsMouseThreshold() + 1), 2);
					else
						drawShooterTarget(pg.color(240, 240, 240), center, iF.grabsMouseThreshold(), 1);
				}
			}
		}
	}

	/**
	 * Draws the selection regions (a shooter target visual hint of
	 * {@link remixlab.proscene.InteractiveFrame#grabsMouseThreshold()} pixels size) of all
	 * InteractiveFrames forming part of the Camera paths.
	 * 
	 * @see #drawSelectionHints()
	 */
	protected void drawCameraPathSelectionHints() {
		for (MouseGrabbable mg : MouseGrabberPool) {
			if(mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				if (iF.isInCameraPath()) {
					PVector center = camera().projectedCoordinatesOf(iF.position());
					if (mg.grabsMouse())
						drawShooterTarget(pg.color(0, 255, 255), center, (iF.grabsMouseThreshold() + 1), 2);
					else
						drawShooterTarget(pg.color(255, 255, 0), center, iF.grabsMouseThreshold(), 1);
				}
			}
		}
	}

	/**
	 * Convenience function that simply calls {@code
	 * drawPointUnderPixelHint(pg3d.color(255,255,255),px,py,15,3)}.
	 */
	public void drawCross(float px, float py) {
		drawCross(pg.color(255, 255, 255), px, py, 15, 3);
	}

	/**
	 * Draws a cross on the screen centered under pixel {@code (px, py)}, and edge
	 * of size {@code size}. {@code strokeWeight} defined the weight of the
	 * stroke.
	 * 
	 * @see #drawArcballReferencePointHint()
	 */
	public void drawCross(int color, float px, float py, float size, int strokeWeight) {
		beginScreenDrawing();
		PVector p1 = coords(new Point(px - size, py));
		PVector p2 = coords(new Point(px + size, py));
		PVector p3 = coords(new Point(px, py - size));
		PVector p4 = coords(new Point(px, py + size));
		pg.pushStyle();
		pg.stroke(color);
		pg.strokeWeight(strokeWeight);
		pg.noFill();
		pg.beginShape(LINES);
		pg.vertex(p1.x, p1.y, p1.z);
		pg.vertex(p2.x, p2.y, p2.z);
		pg.vertex(p3.x, p3.y, p3.z);
		pg.vertex(p4.x, p4.y, p4.z);
		pg.endShape();
		pg.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Convenience function that simply calls
	 * {@code drawFilledCircle(40, color, center, radius)}.
	 * 
	 * @see #drawFilledCircle(int, int, PVector, float)
	 */
	public void drawFilledCircle(int color, PVector center, float radius) {
		drawFilledCircle(40, color, center, radius);
	}

	/**
	 * Draws a filled circle using screen coordinates.
	 * 
	 * @param subdivisions
	 *          Number of triangles aproximating the circle. 
	 * @param color
	 *          Color used to fill the circle.
	 * @param center
	 *          Circle screen center.
	 * @param radius
	 *          Circle screen radius.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 */	
	public void drawFilledCircle(int subdivisions, int color, PVector center, float radius) {
		float precision = TWO_PI/subdivisions;
		float x = center.x;
		float y = center.y;
		float angle, x2, y2;
		beginScreenDrawing();
		pg.pushStyle();
		pg.noStroke();
		pg.fill(color);
		pg.beginShape(TRIANGLE_FAN);
		PVector c = coords(new Point(x, y));
		pg.vertex(c.x, c.y, c.z);
		PVector aux = new PVector();
		for (angle = 0.0f; angle <= TWO_PI + 1.1*precision; angle += precision) {			
			x2 = x + PApplet.sin(angle) * radius;
			y2 = y + PApplet.cos(angle) * radius;
			aux.set(coords(new Point(x2, y2)));
			pg.vertex(aux.x, aux.y, aux.z);
		}
		pg.endShape();
		pg.popStyle();
		endScreenDrawing();
	}

	/**
	 * Draws a filled square using screen coordinates.
	 * 
	 * @param color
	 *          Color used to fill the square.
	 * @param center
	 *          Square screen center.
	 * @param edge
	 *          Square edge length.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 */
	public void drawFilledSquare(int color, PVector center, float edge) {
		float x = center.x;
		float y = center.y;
		beginScreenDrawing();
		PVector p1 = coords(new Point(x - edge, y + edge));
		PVector p2 = coords(new Point(x + edge, y + edge));
		PVector p3 = coords(new Point(x + edge, y - edge));
		PVector p4 = coords(new Point(x - edge, y - edge));
		pg.pushStyle();
		pg.noStroke();
		pg.fill(color);
		pg.beginShape(QUADS);
		pg.vertex(p1.x, p1.y, p1.z);
		pg.vertex(p2.x, p2.y, p2.z);
		pg.vertex(p3.x, p3.y, p3.z);
		pg.vertex(p4.x, p4.y, p4.z);
		pg.endShape();
		pg.popStyle();
		endScreenDrawing();
	}

	/**
	 * Draws the classical shooter target on the screen.
	 * 
	 * @param color
	 *          Color of the target
	 * @param center
	 *          Center of the target on the screen
	 * @param length
	 *          Length of the target in pixels
	 * @param strokeWeight
	 *          Stroke weight
	 */
	public void drawShooterTarget(int color, PVector center, float length, int strokeWeight) {
		float x = center.x;
		float y = center.y;
		beginScreenDrawing();
		PVector p1 = coords(new Point((x - length), (y - length) + (0.6f * length)));
		PVector p2 = coords(new Point((x - length), (y - length)));
		PVector p3 = coords(new Point((x - length) + (0.6f * length), (y - length)));
		PVector p4 = coords(new Point(((x + length) - (0.6f * length)), (y - length)));
		PVector p5 = coords(new Point((x + length), (y - length)));
		PVector p6 = coords(new Point((x + length), ((y - length) + (0.6f * length))));
		PVector p7 = coords(new Point((x + length), ((y + length) - (0.6f * length))));
		PVector p8 = coords(new Point((x + length), (y + length)));
		PVector p9 = coords(new Point(((x + length) - (0.6f * length)), (y + length)));
		PVector p10 = coords(new Point(((x - length) + (0.6f * length)), (y + length)));
		PVector p11 = coords(new Point((x - length), (y + length)));
		PVector p12 = coords(new Point((x - length), ((y + length) - (0.6f * length))));
		
		pg.pushStyle();

		pg.stroke(color);
		pg.strokeWeight(strokeWeight);
		pg.noFill();

		pg.beginShape();
		pg.vertex(p1.x, p1.y, p1.z);
		pg.vertex(p2.x, p2.y, p2.z);
		pg.vertex(p3.x, p3.y, p3.z);
		pg.endShape();

		pg.beginShape();
		pg.vertex(p4.x, p4.y, p4.z);
		pg.vertex(p5.x, p5.y, p5.z);
		pg.vertex(p6.x, p6.y, p6.z);
		pg.endShape();

		pg.beginShape();
		pg.vertex(p7.x, p7.y, p7.z);
		pg.vertex(p8.x, p8.y, p8.z);
		pg.vertex(p9.x, p9.y, p9.z);
		pg.endShape();

		pg.beginShape();
		pg.vertex(p10.x, p10.y, p10.z);
		pg.vertex(p11.x, p11.y, p11.z);
		pg.vertex(p12.x, p12.y, p12.z);
		pg.endShape();

		pg.popStyle();
		endScreenDrawing();

		drawCross(color, center.x, center.y, 0.6f * length, strokeWeight);
	}	
	
	/**
	void QGLViewer::startScreenCoordinatesSystem(bool upward) const
	{
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		if (tileRegion_ != NULL)
		  if (upward)
		    glOrtho(tileRegion_->xMin, tileRegion_->xMax, tileRegion_->yMin, tileRegion_->yMax, 0.0, -1.0);
		  else
		    glOrtho(tileRegion_->xMin, tileRegion_->xMax, tileRegion_->yMax, tileRegion_->yMin, 0.0, -1.0);
		else
		  if (upward)
		    glOrtho(0, width(), 0, height(), 0.0, -1.0);
		  else
		    glOrtho(0, width(), height(), 0, 0.0, -1.0);

		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
	}
	
	void QGLViewer::stopScreenCoordinatesSystem() const
	{
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();

		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	*/

	/**
	 * Computes the world coordinates of an screen object so that drawing can be
	 * done directly with 2D screen coordinates.
	 * <p>
	 * All screen drawing should be enclosed between {@link #beginScreenDrawing()}
	 * and {@link #endScreenDrawing()}. Then you can just begin drawing your
	 * screen shapes (defined between {@code PApplet.beginShape()} and {@code
	 * PApplet.endShape()}).
	 * <p>
	 * <b>Note:</b> To specify a {@code (x,y)} vertex screen coordinate you should 
	 * first call {@code PVector p = coords(new Point(x, y))} then do your
	 * drawing as {@code vertex(p.x, p.y, p.z)}.
	 * <p>
	 * <b>Attention:</b> If you want your screen drawing to appear on top of your
	 * 3d scene then draw first all your 3d before doing any call to a 
	 * {@link #beginScreenDrawing()} and {@link #endScreenDrawing()} pair.  
	 * 
	 * @see #endScreenDrawing()
	 * @see #coords(Point)
	 */
	public void beginScreenDrawing() {
		//TODO fix me!
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
							                 + "endScreenDrawing() and they cannot be nested. Check your implementation!");
		
		startCoordCalls++;
		
		/**
		if ( pg3d.getClass() == processing.core.PGraphicsOpenGL.class ) {
		//if ( pg3d instanceof processing.core.PGraphicsOpenGL ) {
			pg3d.hint(DISABLE_DEPTH_TEST);
			pg3d.matrixMode(PROJECTION);
			pg3d.pushMatrix();
			pg3d.ortho(-width/2, width/2, -height/2, height/2, -10, 10);
			pg3d.matrixMode(MODELVIEW);
			pg3d.pushMatrix();
		  // Camera needs to be reset!
			pg3d.camera();
			zC = 0.0f;
		}
		else {
			zC = 0.1f;
		}
		*/
		pg.hint(DISABLE_DEPTH_TEST);
		((PGraphicsOpenGL)pg).pushProjection();
		
		//pg3d.ortho(0, width, 0, height, camera().zNear(), camera().zFar());
		
		/**
		float[] wh = camera().getOrthoWidthHeight();
		pg3d.ortho(0, 2*wh[0], 0, 2*wh[1], camera().zNear(), camera().zFar());
		// */
		
		//pg3d.ortho(-width/2, width/2, -height/2, height/2, -10, 10);
		
		//pg3d.ortho(0f, width, 0f, height, 0.0f, -1.0f);
		//pg3d.ortho(0f, width, 0f, height, -1.0f, 0.0f);
		//pg3d.ortho(0f, width, height, 0f, 0.0f, -1.0f);
		
		
		// /**
		float cameraZ = (height/2.0f) / PApplet.tan( camera().fieldOfView() /2.0f);
		float cameraMaxFar = cameraZ * 2.0f;
		float cameraNear = cameraZ / 2.0f;
		float cameraFar = cameraZ * 2.0f;
	  pg.ortho(-width/2, width/2, -height/2, height/2, cameraNear, cameraFar);		
		//pg3d.ortho(0, width, 0, height, cameraNear, cameraFar);
		// */
		
		/**
		float[] wh = camera().getOrthoWidthHeight();//return halfWidth halfHeight
		pg3d.ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());
		// */
		
		pg.pushMatrix();
	  // Camera needs to be reset!
		pg.camera();		
		zC = 0.0f;		
	}

	/**
	 * Ends screen drawing. See {@link #beginScreenDrawing()} for details.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #coords(Point)
	 */
	public void endScreenDrawing() {
		startCoordCalls--;
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
							                 + "endScreenDrawing() and they cannot be nested. Check your implementation!");

		/**
		if ( pg3d.getClass() == processing.core.PGraphicsOpenGL.class ) {
			pg3d.matrixMode(PROJECTION);
			pg3d.popMatrix();
			pg3d.matrixMode(MODELVIEW);  
			pg3d.popMatrix();		  
			pg3d.hint(ENABLE_DEPTH_TEST);
		}
		*/		
		((PGraphicsOpenGL)pg).popProjection();  
		pg.popMatrix();		  
		pg.hint(ENABLE_DEPTH_TEST);
	}
	
	/**
	 * Computes the world coordinates of the {@code p} screen Point.
	 * <p>
	 * This method is only useful when drawing directly on screen. It should be
	 * used in conjunction with {@link #beginScreenDrawing()} and
	 * {@link #endScreenDrawing()} (which may be consulted for details).
	 * <P>
	 * The method {@link #beginScreenDrawing()} should be called before, otherwise
	 * a runtime exception is thrown.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 * @see #coords(Point)
	 */
	public PVector coords(Point p) {
		if (startCoordCalls != 1)
			throw new RuntimeException("beginScreenDrawing() should be called before this method!");
		/**
		if ( pg3d.getClass() == processing.core.PGraphicsOpenGL.class )
			return new PVector(p.x, p.y, zC);
		else
			return camera().unprojectedCoordinatesOf(new PVector(p.x,p.y,zC));
			*/
		return new PVector(p.x, p.y, zC);
	}	

	/**
	 * Called from the timer to stop displaying the point under pixel and arcball
	 * reference point visual hints.
	 */
	protected void unSetTimerFlag() {
		arpFlag = false;
		pupFlag = false;
	}

	// 7. Camera profiles

	/**
	 * Internal method that defines the default camera profiles: WHEELED_ARCBALL
	 * and FIRST_PERSON.
	 */
	@Override
	protected void initDefaultCameraProfiles() {
		cameraProfileMap = new HashMap<String, CameraProfile>();
		cameraProfileNames = new ArrayList<String>();
		currentCameraProfile = null;
		// register here the default profiles
		//registerCameraProfile(new CameraProfile(this, "ARCBALL", CameraProfile.Mode.ARCBALL));
		registerCameraProfile( new CameraProfile(this, "WHEELED_ARCBALL", CameraProfile.Mode.WHEELED_ARCBALL) );
		registerCameraProfile( new CameraProfile(this, "FIRST_PERSON", CameraProfile.Mode.FIRST_PERSON) );
		//setCurrentCameraProfile("ARCBALL");
		setCurrentCameraProfile("WHEELED_ARCBALL");
	}	

	/**
	 * Unregisters the given camera profile by its name. Returns true if succeeded.
	 * Registration will fail in two cases: no camera profile is registered under
	 * the provided name, or the camera profile is the only registered camera profile which
	 * mode is different than THIRD_PERSON.
	 * <p>
	 * The last condition above guarantees that there should always be registered at least
	 * one camera profile which mode is different than THIRD_PERSON. 
	 * 
	 * @param cp camera profile
	 * @return true if succeeded
	 */
	@Override
	public boolean unregisterCameraProfile(String cp) {
		if (!isCameraProfileRegistered(cp))
			return false;

		CameraProfile cProfile = cameraProfile(cp);
		int instancesDifferentThanThirdPerson = 0;

		for (CameraProfile camProfile : cameraProfileMap.values())
			if (camProfile.mode() != CameraProfile.Mode.THIRD_PERSON)
				instancesDifferentThanThirdPerson++;

		if ((cProfile.mode() != CameraProfile.Mode.THIRD_PERSON) && (instancesDifferentThanThirdPerson == 1))
			return false;

		if (isCurrentCameraProfile(cp))
			nextCameraProfile();

		if (cameraProfileNames.remove(cp)) {
			cameraProfileMap.remove(cp);
			return true;
		}

		return false;
	}	
	
	/**
	 * Set current the camera profile associated to the given name.
	 * Returns true if succeeded.
	 * <p>
	 * This method triggers smooth transition animations
	 * when switching between camera profile modes.
	 */
	@Override
	public boolean setCurrentCameraProfile(String cp) {
		CameraProfile camProfile = cameraProfileMap.get(cp);
		if (camProfile == null)
			return false;
		if ((camProfile.mode() == CameraProfile.Mode.THIRD_PERSON) && (avatar() == null))
			return false;
		else {			
			// first-person
			if (camProfile.mode() == CameraProfile.Mode.FIRST_PERSON && cursorIsHiddenOnFirstPerson())
				parent.noCursor();
			else {
				if (currentCameraProfile != null)
					if ((currentCameraProfile.mode() == CameraProfile.Mode.FIRST_PERSON ) && (camProfile.mode() != CameraProfile.Mode.FIRST_PERSON))			
						parent.cursor();
			}			
			//third person
			if (camProfile.mode() == CameraProfile.Mode.THIRD_PERSON) {
				setDrawInteractiveFrame();
				setCameraType(Camera.Type.PERSPECTIVE);
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar()).removeFromMouseGrabberPool();
				camera().frame().updateFlyUpVector();// ?
				camera().frame().stopSpinning();
				if (avatarIsInteractiveDrivableFrame) {
					((InteractiveDrivableFrame) (avatar())).updateFlyUpVector();
					((InteractiveDrivableFrame) (avatar())).stopSpinning();
				}
				// perform small animation ;)
				if (camera().anyInterpolationIsStarted())
					camera().stopAllInterpolations();
				Camera cm = camera().clone();
				cm.setPosition(avatar().cameraPosition());
				cm.setUpVector(avatar().upVector());
				cm.lookAt(avatar().target());
				camera().interpolateTo(cm.frame());
				currentCameraProfile = camProfile;
			} else {
				camera().frame().updateFlyUpVector();
				camera().frame().stopSpinning();
				
				if(currentCameraProfile != null)
					if (currentCameraProfile.mode() == CameraProfile.Mode.THIRD_PERSON)
						camera().interpolateToFitScene();												
        
				currentCameraProfile = camProfile;        
				
				setDrawInteractiveFrame(false);
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar()).addInMouseGrabberPool();
			}			
			return true;
		}
	}	

	// 8. Keyboard customization	

	/**
	 * Sets global default keyboard shortcuts and the default key-frame shortcut keys.
	 * <p>
	 * Default global keyboard shortcuts are:
	 * <p>
	 * <ul>
	 * <li><b>'a'</b>: {@link remixlab.proscene.Scene.KeyboardAction#DRAW_AXIS}.
	 * <li><b>'e'</b>: {@link remixlab.proscene.Scene.KeyboardAction#CAMERA_TYPE}.
	 * <li><b>'g'</b>: {@link remixlab.proscene.Scene.KeyboardAction#DRAW_GRID}.
	 * <li><b>'h'</b>: {@link remixlab.proscene.Scene.KeyboardAction#GLOBAL_HELP}
	 * <li><b>'H'</b>: {@link remixlab.proscene.Scene.KeyboardAction#CURRENT_CAMERA_PROFILE_HELP}
	 * <li><b>'r'</b>: {@link remixlab.proscene.Scene.KeyboardAction#EDIT_CAMERA_PATH}.
	 * <li><b>space bar</b>: {@link remixlab.proscene.Scene.KeyboardAction#CAMERA_PROFILE}.
	 * </ul> 
	 * <p>
	 * Default key-frame shortcuts keys are:
	 * <ul>
	 * <li><b>'[1..5]'</b>: Play path [1..5]. 
	 * <li><b>'CTRL'+'[1..5]'</b>: Add key-frame to path [1..5].   
	 * <li><b>'ALT'+'[1..5]'</b>: Remove path [1..5].
	 * </ul> 
	 */
	@Override
	public void setDefaultShortcuts() {
		// D e f a u l t s h o r t c u t s		
		setShortcut('a', KeyboardAction.DRAW_AXIS);
		setShortcut('g', KeyboardAction.DRAW_GRID);
		setShortcut(' ', KeyboardAction.CAMERA_PROFILE);
		setShortcut('e', KeyboardAction.CAMERA_TYPE);		
		setShortcut('h', KeyboardAction.GLOBAL_HELP);
		setShortcut('H', KeyboardAction.CURRENT_CAMERA_PROFILE_HELP);
		setShortcut('r', KeyboardAction.EDIT_CAMERA_PATH);

		// K e y f r a m e s s h o r t c u t k e y s
		setAddKeyFrameKeyboardModifier(Modifier.CTRL);
		setDeleteKeyFrameKeyboardModifier(Modifier.ALT);
		setPathKey('1', 1);
		setPathKey('2', 2);
		setPathKey('3', 3);
		setPathKey('4', 4);
		setPathKey('5', 5);
	}  

	/**
	 * Internal method. Handles the different global keyboard actions.
	 */
	@Override
	protected void handleKeyboardAction(KeyboardAction id) {
		if( !keyboardIsHandled() )
			return;
		switch (id) {
		case DRAW_AXIS:
			toggleAxisIsDrawn();
			break;
		case DRAW_GRID:
			toggleGridIsDrawn();
			break;
		case CAMERA_PROFILE:
			nextCameraProfile();
			break;
		case CAMERA_TYPE:
			toggleCameraType();
			break;
		case CAMERA_KIND:
			toggleCameraKind();
			break;
		case ANIMATION:
			toggleAnimation();
			break;
		case ARP_FROM_PIXEL:
			/**
			if (Camera.class == camera().getClass())
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. "
								+ "See the Point Under Pixel example!");
			else
			*/
			if (setArcballReferencePointFromPixel(new Point(parent.mouseX, parent.mouseY))) {
				arpFlag = true;
				Timer timer=new Timer();
				TimerTask timerTask = new TimerTask() {
					public void run() {
						unSetTimerFlag();
					}
				};
				timer.schedule(timerTask, 1000);
			}
			break;
		case RESET_ARP:
			camera().setArcballReferencePoint(new PVector(0, 0, 0));
			arpFlag = true;
			Timer timer=new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run() {
					unSetTimerFlag();
				}
			};
			timer.schedule(timerTask, 1000);
			break;
		case GLOBAL_HELP:
			displayGlobalHelp();
			break;
		case CURRENT_CAMERA_PROFILE_HELP:
			displayCurrentCameraProfileHelp();
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

	/**
	 * Internal method. Handles the different camera keyboard actions.
	 */
	@Override
	protected void handleCameraKeyboardAction(CameraKeyboardAction id) {
		if( !keyboardIsHandled() )
			return;
		switch (id) {
		case INTERPOLATE_TO_ZOOM_ON_PIXEL:
			/**
			if (Camera.class == camera().getClass())
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. "
								+ "See the Point Under Pixel example!");
			else {
			*/
				Camera.WorldPoint wP = interpolateToZoomOnPixel(new Point(parent.mouseX, parent.mouseY));
				if (wP.found) {
					pupVec = wP.point;
					pupFlag = true;
					Timer timer=new Timer();
					TimerTask timerTask = new TimerTask() {
						public void run() {
							unSetTimerFlag();
						}
					};
					timer.schedule(timerTask, 1000);
				}
			//}
			break;
		case INTERPOLATE_TO_FIT_SCENE:
			camera().interpolateToFitScene();
			break;
		case SHOW_ALL:
			showAll();
			break;
		case MOVE_CAMERA_LEFT:
			camera().frame().translate(
					camera().frame().inverseTransformOf(
							new PVector(-10.0f * camera().flySpeed(), 0.0f, 0.0f)));
			break;
		case MOVE_CAMERA_RIGHT:
			camera().frame().translate(
					camera().frame().inverseTransformOf(
							new PVector(10.0f * camera().flySpeed(), 0.0f, 0.0f)));
			break;
		case MOVE_CAMERA_UP:
			if( this.isRightHanded() )
				camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f, 10.0f * camera().flySpeed(), 0.0f)));
			else
				camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f, -10.0f * camera().flySpeed(), 0.0f)));
			break;
		case MOVE_CAMERA_DOWN:
			if( this.isRightHanded() )
				camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f, -10.0f * camera().flySpeed(), 0.0f)));
			else
				camera().frame().translate(camera().frame().inverseTransformOf(new PVector(0.0f, 10.0f * camera().flySpeed(), 0.0f)));
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
			if (avatar() != null)
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar())
							.setFlySpeed(((InteractiveDrivableFrame) avatar()).flySpeed() * 1.2f);
			break;
		case DECREASE_AVATAR_FLY_SPEED:
			if (avatar() != null)
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar())
							.setFlySpeed(((InteractiveDrivableFrame) avatar()).flySpeed() / 1.2f);
			break;
		case INCREASE_AZYMUTH:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setAzimuth(((InteractiveAvatarFrame) avatar()).azimuth()
									+ PApplet.PI / 64);
			break;
		case DECREASE_AZYMUTH:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setAzimuth(((InteractiveAvatarFrame) avatar()).azimuth()
									- PApplet.PI / 64);
			break;
		case INCREASE_INCLINATION:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setInclination(((InteractiveAvatarFrame) avatar()).inclination()
									+ PApplet.PI / 64);
			break;
		case DECREASE_INCLINATION:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setInclination(((InteractiveAvatarFrame) avatar()).inclination()
									- PApplet.PI / 64);
			break;
		case INCREASE_TRACKING_DISTANCE:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setTrackingDistance(((InteractiveAvatarFrame) avatar())
									.trackingDistance()
									+ radius() / 50);
			break;
		case DECREASE_TRACKING_DISTANCE:
			if (avatar() != null)
				if (avatarIsInteractiveAvatarFrame)
					((InteractiveAvatarFrame) avatar())
							.setTrackingDistance(((InteractiveAvatarFrame) avatar())
									.trackingDistance()
									- radius() / 50);
			break;
		}
	}

	/**
	 * Convenience funstion that simply calls {@code displayGlobalHelp(true)}.
	 * 
	 * @see #displayGlobalHelp(boolean)
	 */
	public void displayGlobalHelp() {
		displayGlobalHelp(true);
	}
	
	/**
	 * Displays global keyboard bindings.
	 * 
	 * @param onConsole if this flag is true displays the help on console.
	 * Otherwise displays it on the applet
	 * 
	 * @see #displayGlobalHelp()
	 */
	public void displayGlobalHelp(boolean onConsole) {
		if (onConsole)
			PApplet.println(globalHelp());
		else { //on applet
			pg.textFont(parent.createFont("Arial", 12));
			pg.textMode(SCREEN);
			pg.fill(0,255,0);
			pg.textLeading(20);
			pg.text(globalHelp(), 10, 10, (pg.width-20), (pg.height-20));
		}
	}
	
	/**
	 * Returns a String with the global keyboard bindings.
	 * 
	 * @see #displayGlobalHelp()
	 */
	public String globalHelp() {
		String description = new String();
		description += "GLOBAL keyboard shortcuts\n";
		for (Entry<KeyboardShortcut, Scene.KeyboardAction> entry : gProfile.map.entrySet()) {			
			Character space = ' ';
			if (!entry.getKey().description().equals(space.toString())) 
				description += entry.getKey().description() + " -> " + entry.getValue().description() + "\n";
			else
				description += "space_bar" + " -> " + entry.getValue().description() + "\n";
		}
		
		for (Entry<Integer, Integer> entry : pathKeys.map.entrySet())
			description += DesktopEvents.getKeyText(entry.getKey()) + " -> plays camera path " + entry.getValue().toString() + "\n";
		description += DesktopEvents.getModifiersExText(addKeyFrameKeyboardModifier.ID) + " + one of the above keys -> adds keyframe to the camera path \n";
		description += DesktopEvents.getModifiersExText(deleteKeyFrameKeyboardModifier.ID) + " + one of the above keys -> deletes the camera path \n";
		
		return description;		
	}
	
	/**
	 * Convenience function that simply calls {@code displayCurrentCameraProfileHelp(true)}.
	 * 
	 * @see #displayCurrentCameraProfileHelp(boolean)
	 */
	public void displayCurrentCameraProfileHelp() {
		displayCurrentCameraProfileHelp(true);
	}
	
	/**
	 * Displays the {@link #currentCameraProfile()} bindings.
	 * 
	 * @param onConsole if this flag is true displays the help on console.
	 * Otherwise displays it on the applet
	 * 
	 * @see #displayCurrentCameraProfileHelp()
	 */
	public void displayCurrentCameraProfileHelp(boolean onConsole) {
		if (onConsole)
			PApplet.println(currentCameraProfileHelp());
		else { //on applet
			pg.textFont(parent.createFont("Arial", 12));
			pg.textMode(SCREEN);
			pg.fill(0,255,0);
			pg.textLeading(20);
			pg.text(currentCameraProfileHelp(), 10, 10, (pg.width-20), (pg.height-20));			
		}
	}
	
	/**
	 * Returns a String with the {@link #currentCameraProfile()} keyboard and mouse bindings.
	 * 
	 * @see remixlab.proscene.CameraProfile#cameraMouseBindingsDescription()
	 * @see remixlab.proscene.CameraProfile#frameMouseBindingsDescription()
	 * @see remixlab.proscene.CameraProfile#mouseClickBindingsDescription()
	 * @see remixlab.proscene.CameraProfile#keyboardShortcutsDescription()
	 * @see remixlab.proscene.CameraProfile#cameraWheelBindingsDescription()
	 * @see remixlab.proscene.CameraProfile#frameWheelBindingsDescription()
	 */
	public String currentCameraProfileHelp() {
		String description = new String();
		description += currentCameraProfile().name() + " camera profile keyboard shortcuts and mouse bindings\n";
		int index = 1;
		if( currentCameraProfile().keyboardShortcutsDescription().length() != 0 ) {
			description += index + ". " + "Keyboard shortcuts\n";
			description += currentCameraProfile().keyboardShortcutsDescription();
			index++;
		}
		if( currentCameraProfile().cameraMouseBindingsDescription().length() != 0 ) {
			description += index + ". " + "Camera mouse bindings\n";
			description += currentCameraProfile().cameraMouseBindingsDescription();
			index++;
		}
		if( currentCameraProfile().mouseClickBindingsDescription().length() != 0 ) {
			description += index + ". " + "Mouse click bindings\n";
			description += currentCameraProfile().mouseClickBindingsDescription();
			index++;
		}
		if( currentCameraProfile().frameMouseBindingsDescription().length() != 0 ) {
			description += index + ". " + "Interactive frame mouse bindings\n";
			description += currentCameraProfile().frameMouseBindingsDescription();
			index++;
		}
		if( currentCameraProfile().cameraWheelBindingsDescription().length() != 0 ) {
			description += index + ". " + "Camera mouse wheel bindings\n";
			description += currentCameraProfile().cameraWheelBindingsDescription();
			index++;
		}
		if( currentCameraProfile().frameWheelBindingsDescription().length() != 0 ) {
			description += index + ". " + "Interactive frame mouse wheel bindings\n";
			description += currentCameraProfile().frameWheelBindingsDescription();
			index++;
		}
		return description;
	}

	// 9. Mouse customization	

	/**
	 * Internal method. Handles the different mouse click actions.
	 */
	@Override
	protected void handleClickAction(ClickAction action) {
		// public enum ClickAction { NO_CLICK_ACTION, ZOOM_ON_PIXEL, ZOOM_TO_FIT,
		// SELECT, ARP_FROM_PIXEL, RESET_ARP,
		// CENTER_FRAME, CENTER_SCENE, SHOW_ALL, ALIGN_FRAME, ALIGN_CAMERA }
		if( !mouseIsHandled() )
			return;
		switch (action) {
		case NO_CLICK_ACTION:
			break;
		case ZOOM_ON_PIXEL:
			/**
			if (Camera.class == camera().getClass())
				PApplet
						.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. "
								+ "See the Point Under Pixel example!");
			else {
			*/
				Camera.WorldPoint wP = interpolateToZoomOnPixel(new Point(parent.mouseX, parent.mouseY));
				if (wP.found) {
					pupVec = wP.point;
					pupFlag = true;
					Timer timer=new Timer();
					TimerTask timerTask = new TimerTask() {
						public void run() {
							unSetTimerFlag();
						}
					};
					timer.schedule(timerTask, 1000);
				}
			//}
			break;
		case ZOOM_TO_FIT:
			camera().interpolateToFitScene();
			break;
		case ARP_FROM_PIXEL:
			/**
			if (Camera.class == camera().getClass())
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. "
								+ "See the Point Under Pixel example!");
			else */
			if (setArcballReferencePointFromPixel(new Point(parent.mouseX, parent.mouseY))) {
				arpFlag = true;
				Timer timer=new Timer();
				TimerTask timerTask = new TimerTask() {
					public void run() {
						unSetTimerFlag();
					}
				};
				timer.schedule(timerTask, 1000);
			}
			break;
		case RESET_ARP:
			camera().setArcballReferencePoint(new PVector(0, 0, 0));
			arpFlag = true;
			Timer timer=new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run() {
					unSetTimerFlag();
				}
			};
			timer.schedule(timerTask, 1000);
			break;
		case CENTER_FRAME:
			if (interactiveFrame() != null)
				interactiveFrame().projectOnLine(camera().position(),
						camera().viewDirection());
			break;
		case CENTER_SCENE:
			camera().centerScene();
			break;
		case SHOW_ALL:
			camera().showEntireScene();
			break;
		case ALIGN_FRAME:
			if (interactiveFrame() != null)
				interactiveFrame().alignWithFrame(camera().frame());
			break;
		case ALIGN_CAMERA:
			camera().frame().alignWithFrame(null, true);
			break;
		}
	}

	// 12. Processing objects

	/**
	 * Sets the processing camera projection matrix from {@link #camera()}. Calls
	 * {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link remixlab.proscene.Camera#type()}.
	 */
	protected void setPProjectionMatrix() {
		// option 1 (only one of the following two lines)
		// pg3d.projection.set(camera().getProjectionMatrix());
		// camera().computeProjectionMatrix();		
		// /**
		// option 2
		// compute the processing camera projection matrix from our camera()
		// parameters
		switch (camera().type()) {
		case PERSPECTIVE:
			pg.perspective(camera().fieldOfView(), camera().aspectRatio(), camera().zNear(), camera().zFar());
			break;
		case ORTHOGRAPHIC:
			float[] wh = camera().getOrthoWidthHeight();//return halfWidth halfHeight
			// 1. P5 1.5 version:
			pg.ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());			
			// 2. As it is done in P5-a5 perspective vs ortho example, but using proscene w and h
		  // ortho: screen drawing broken; frame translation fixed
		  // persp: screen drawing broken; frame translation fixed
			//pg3d.ortho(0, 2*wh[0], 0, 2*wh[1], camera().zNear(), camera().zFar());			
		  // 3. As it is done in P5-a5 perspective vs ortho example
			// ortho: screen drawing fixed; frame translation broken
		  // persp: screen drawing broken; frame translation fixed
			// pg3d.ortho(0, width, 0, height, camera().zNear(), camera().zFar());
			break;
		}
		// if our camera() matrices are detached from the processing Camera matrices,
		// we cache the processing camera projection matrix into our camera()
		// camera().setProjectionMatrix(pg3d.projection);//TODO no needed: camera matrices are references to P5 anyway	
		// */
	}

	/**
	 * Sets the processing camera matrix from {@link #camera()}. Simply calls
	 * {@code PApplet.camera()}.
	 */
	protected void setPModelViewMatrix() {
	  // option 1 (only one of the following two lines)
		//pg3d.modelview.set(camera().getModelViewMatrix());
	  //camera().computeModelViewMatrix();
		// /**
		// option 2
		// compute the processing camera modelview matrix from our camera()
		// parameters
		pg.camera(camera().position().x, camera().position().y, camera().position().z,
				        camera().at().x, camera().at().y, camera().at().z,
				        camera().upVector().x, camera().upVector().y, camera().upVector().z);
		// if our camera() matrices are detached from the processing Camera matrices,
		// we cache the processing camera modelview matrix into our camera()
		// camera().setModelViewMatrix(pg3d.modelview);//TODO no needed: camera matrices are references to P5 anyway		
		// */
	}
}