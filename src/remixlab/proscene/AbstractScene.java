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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public abstract class AbstractScene  implements PConstants {
  //proscene version
	public static final String version = "1.1.90";
	/**
	 * Returns the major release version number of proscene as an integer.
	 * <p>
	 * {@code Scene.version} will return the complete version (major+minor)
	 * number as a string. 
	 */
	public static int majorVersionNumber() {
		return Integer.parseInt(majorVersion());
	}
	
	/**
	 * Returns the major release version number of proscene as a string.
	 * <p>
	 * {@code Scene.version} will return the complete version (major+minor)
	 * number as a string.
	 */
	public static String majorVersion() {
		return version.substring(0, version.indexOf("."));
	}
	
	/**
	 * Returns the minor release version number of proscene as a float.
	 * <p>
	 * {@code Scene.version} will return the complete version (major+minor)
	 * number as a string.
	 */
	public static float minorVersionNumber() {
		return Float.parseFloat(minorVersion());
	}
	
	/**
	 * Returns the minor release version number of proscene as a string.
	 * <p>
	 * {@code Scene.version} will return the complete version (major+minor)
	 * number as a string.
	 */
	public static String minorVersion() {
		return version.substring(version.indexOf(".") + 1);
	}
	
	public enum Space {
		TWO_D, THREE_D;
	}
	
	protected Space space;
	
	public Space space() {
		return space;
	}
	
	/**
	 * Constants associated to the different mouse buttons which follow java conventions.
	 */
	public enum Button {
		// values correspond to: BUTTON1_DOWN_MASK, BUTTON2_DOWN_MASK and BUTTON3_DOWN_MASK
		// see: http://download-llnw.oracle.com/javase/6/docs/api/constant-values.html
		LEFT(1024), MIDDLE(2048), RIGHT(4096);
		public final int ID;
    Button(int code) {
    	this.ID = code;
    }    
    //The following code works but is considered overkill :)
    /**
    public int id() { return ID; }
    private static final Map<Integer,Button> lookup = new HashMap<Integer,Button>();
    static {
    	for(Button s : EnumSet.allOf(Button.class))
         lookup.put(s.id(), s);
    }
    public static Button get(int code) { 
      return lookup.get(code);
    }
    // */
	}
	
	/**
	 * Constants associated to the different arrow keys. Taken from Processing constants 
	 * (which follows java conventions). 
	 */	
	public enum Arrow {
		UP(PApplet.UP), DOWN(PApplet.DOWN), LEFT(PApplet.LEFT), RIGHT(PApplet.RIGHT);
		public final int ID;
    Arrow(int code) {
    	this.ID = code;
    }
    //The following code works but is considered overkill :)
    /**
    public int id() { return ID; }
    private static final Map<Integer,Arrow> lookup = new HashMap<Integer,Arrow>();
    static {
    	for(Arrow s : EnumSet.allOf(Arrow.class))
         lookup.put(s.id(), s);
    }
    public static Arrow get(int code) { 
      return lookup.get(code);
    }
    // */
	}

	/**
	 * Constants associated to the different modifier keys which follow java conventions.
	 */
	public enum Modifier {
		// values correspond to: ALT_DOWN_MASK, SHIFT_DOWN_MASK, CTRL_DOWN_MASK, META_DOWN_MASK, ALT_GRAPH_DOWN_MASK
		// see: http://download-llnw.oracle.com/javase/6/docs/api/constant-values.html
		ALT(512), SHIFT(64), CTRL(128), META(256), ALT_GRAPH(8192);
		public final int ID;
		Modifier(int code) {
      this.ID = code;
    }
    //The following code works but is considered overkill :)
    /**
    public int id() { return ID; }
    private static final Map<Integer,Modifier> lookup = new HashMap<Integer,Modifier>();
    static {
    	for(Modifier s : EnumSet.allOf(Modifier.class))
         lookup.put(s.id(), s);
    }
    public static Modifier get(int code) {
      return lookup.get(code);
    }
    // */
	}
	
  //K E Y F R A M E S
	protected Bindings<Integer, Integer> pathKeys;
	protected Modifier addKeyFrameKeyboardModifier;
	protected Modifier deleteKeyFrameKeyboardModifier;
	
  //c a m e r a p r o f i l e s
	protected HashMap<String, CameraProfile> cameraProfileMap;
	protected ArrayList<String> cameraProfileNames;
	protected CameraProfile currentCameraProfile;
	
  //P R O C E S S I N G   A P P L E T   A N D   O B J E C T S
	public PApplet parent;
	protected PGraphics pg;
	protected int width, height;// size
	protected boolean offscreen;
	
  //O B J E C T S
  //TODO how to deal with the camera?
	protected Camera cam;
	protected DesktopEvents dE;
	protected InteractiveFrame glIFrame;
	protected boolean iFrameIsDrwn;
	protected Trackable trck;
	protected boolean avatarIsInteractiveDrivableFrame;
	protected boolean avatarIsInteractiveAvatarFrame;
	
  //D I S P L A Y F L A G S
	protected boolean axisIsDrwn; // world axis
	protected boolean gridIsDrwn; // world XY grid
	protected boolean frameSelectionHintIsDrwn;
	protected boolean cameraPathsAreDrwn;
	
  //C O N S T R A I N T S
	protected boolean withConstraint;
	
  //LEFT vs RIGHT_HAND
	protected boolean rightHanded;

	// K E Y B O A R D A N D M O U S E
	protected boolean mouseHandling;
	protected boolean keyboardHandling;
	
  //M o u s e G r a b b e r
	protected List<MouseGrabbable> MouseGrabberPool;
	protected MouseGrabbable mouseGrbbr;
	protected boolean mouseGrabberIsAnIFrame;	
	protected boolean mouseTrckn;
	
	// A N I M A T I O N
	protected float targetFrameRate;
	protected float animationFrameRate;
	protected long initialDrawingFrameWhenAnimationStarted;
	protected long currentAnimationFrame;
	protected float animationToFrameRateRatio;
	//private int framesInBetween;
	protected boolean animationStarted;
	public boolean animatedFrameWasTriggered;
	protected float animationPeriod;

	// R E G I S T E R   D R A W   A N D   A N I M A T I O N   M E T H O D S
	/** The object to handle the draw event */
	protected Object drawHandlerObject;
	/** The method in drawHandlerObject to execute */
	protected Method drawHandlerMethod;
	/** the name of the method to handle the event */
	protected String drawHandlerMethodName;
	// Animation
	/** The object to handle the animation */
	protected Object animateHandlerObject;
	/** The method in animateHandlerObject to execute */
	protected Method animateHandlerMethod;
	/** the name of the method to handle the animation */
	protected String animateHandlerMethodName;
	
  //D E V I C E S	
	protected ArrayList<HIDevice> devices;
	
  //F I R S T   P E R S O N   C A M E R A
	protected boolean hideCursorOn1stPerson = false;
	
	// 0. testing right and left handed	
	public boolean isLeftHanded() {
		return !this.rightHanded;
	}
	
	public boolean isRightHanded() {
		return this.rightHanded;
	}
	
	public void setRightHanded() {
		rightHanded = true;
	}
	
	public void setLeftHanded() {
		rightHanded = false;
	}
	
	
	// 1. Scene overloaded
	
	/**
	 * This method is called before the first drawing happen and should be overloaded to
	 * initialize stuff not initialized in {@code PApplet.setup()}. The default
	 * implementation is empty.
	 * <p>
	 * Typical usage include {@link #camera()} initialization ({@link #showAll()})
	 * and Scene state setup ({@link #setAxisIsDrawn(boolean)} and
	 * {@link #setGridIsDrawn(boolean)}.
	 */
	public void init() {}
	
	/**
	 * The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from Scene, this is the method you
	 * should overload, but no if you instantiate your own Scene object (in this
	 * case you should just overload {@code PApplet.draw()} to define your scene).
	 * <p>
	 * The processing camera set in {@link #pre()} converts from the world to the
	 * camera coordinate systems. Vertices given in {@link #draw()} can then be
	 * considered as being given in the world coordinate system. The camera is
	 * moved in this world using the mouse. This representation is much more
	 * intuitive than a camera-centric system (which for instance is the standard
	 * in OpenGL).
	 */
	public void proscenium() {}
	
  //2. Associated objects
	/**
	 * Returns a list containing references to all the active MouseGrabbers.
	 * <p>
	 * Used to parse all the MouseGrabbers and to check if any of them
	 * {@link remixlab.proscene.MouseGrabbable#grabsMouse()} using
	 * {@link remixlab.proscene.MouseGrabbable#checkIfGrabsMouse(int, int, Camera)}.
	 * <p>
	 * You should not have to directly use this list. Use
	 * {@link #removeFromMouseGrabberPool(MouseGrabbable)} and
	 * {@link #addInMouseGrabberPool(MouseGrabbable)} to modify this list.
	 */
	public List<MouseGrabbable> mouseGrabberPool() {
		return MouseGrabberPool;
	}
	
	/**
	 * Returns the InteractiveFrame associated to this Scene. It could be null if
	 * there's no InteractiveFrame associated to this Scene.
	 * 
	 * @see #setInteractiveFrame(InteractiveFrame)
	 */
	public InteractiveFrame interactiveFrame() {
		return glIFrame;
	}

	/**
	 * Returns the avatar object to be tracked by the Camera when
	 * {@link #currentCameraProfile()} is an instance of ThirdPersonCameraProfile.
	 * Simply returns {@code null} if no avatar has been set.
	 */
	public Trackable avatar() {
		return trck;
	}	

	/**
	 * If there's a avatar unset it.
	 * 
	 * @see #setAvatar(Trackable)
	 */
	public void unsetAvatar() {
		trck = null;
		avatarIsInteractiveAvatarFrame = false;
		avatarIsInteractiveDrivableFrame = false;
	}	

	/**
	 * Returns the current MouseGrabber, or {@code null} if none currently grabs
	 * mouse events.
	 * <p>
	 * When {@link remixlab.proscene.MouseGrabbable#grabsMouse()}, the different
	 * mouse events are sent to it instead of their usual targets (
	 * {@link #camera()} or {@link #interactiveFrame()}).
	 */
	public MouseGrabbable mouseGrabber() {
		return mouseGrbbr;
	}

	/**
	 * Directly defines the {@link #mouseGrabber()}.
	 * <p>
	 * You should not call this method directly as it bypasses the
	 * {@link remixlab.proscene.MouseGrabbable#checkIfGrabsMouse(int, int, Camera)}
	 * test performed by {@link #mouseMoved(MouseEvent)}.
	 */
	protected void setMouseGrabber(MouseGrabbable mouseGrabber) {
		mouseGrbbr = mouseGrabber;

		mouseGrabberIsAnIFrame = mouseGrabber instanceof InteractiveFrame;
	}
	
  //3. Mouse grabber handling
	
	/**
	 * Returns true if the mouseGrabber is currently in the {@link #mouseGrabberPool()} list.
	 * <p>
	 * When set to false using {@link #removeFromMouseGrabberPool(MouseGrabbable)}, the Scene no longer
	 * {@link remixlab.proscene.MouseGrabbable#checkIfGrabsMouse(int, int, Camera)} on this mouseGrabber.
	 * Use {@link #addInMouseGrabberPool(MouseGrabbable)} to insert it back.
	 */
	public boolean isInMouseGrabberPool(MouseGrabbable mouseGrabber) {
		return mouseGrabberPool().contains(mouseGrabber);
	}
	
	/**
	 * Adds the mouseGrabber in the {@link #mouseGrabberPool()}.
	 * <p>
	 * All created InteractiveFrames (which are MouseGrabbers) are automatically added in the
	 * {@link #mouseGrabberPool()} by their constructors. Trying to add a
	 * mouseGrabber that already {@link #isInMouseGrabberPool(MouseGrabbable)} has no effect.
	 * <p>
	 * Use {@link #removeFromMouseGrabberPool(MouseGrabbable)} to remove the mouseGrabber from
	 * the list, so that it is no longer tested with
	 * {@link remixlab.proscene.MouseGrabbable#checkIfGrabsMouse(int, int, Camera)}
	 * by the Scene, and hence can no longer grab mouse focus. Use
	 * {@link #isInMouseGrabberPool(MouseGrabbable)} to know the current state of the MouseGrabber.
	 */
	public void addInMouseGrabberPool(MouseGrabbable mouseGrabber) {
		if (!isInMouseGrabberPool(mouseGrabber))
			mouseGrabberPool().add(mouseGrabber);
	}

	/**
	 * Removes the mouseGrabber from the {@link #mouseGrabberPool()}.
	 * <p>
	 * See {@link #addInMouseGrabberPool(MouseGrabbable)} for details. Removing a mouseGrabber
	 * that is not in {@link #mouseGrabberPool()} has no effect.
	 */
	public void removeFromMouseGrabberPool(MouseGrabbable mouseGrabber) {
		mouseGrabberPool().remove(mouseGrabber);
	}

	/**
	 * Clears the {@link #mouseGrabberPool()}.
	 * <p>
	 * Use this method only if it is faster to clear the
	 * {@link #mouseGrabberPool()} and then to add back a few MouseGrabbers
	 * than to remove each one independently.
	 */
	public void clearMouseGrabberPool() {
		mouseGrabberPool().clear();
	}
	
	/**
	 * Returns {@code true}
	 * if {@link remixlab.proscene.DesktopEvents#mouseMoved(java.awt.event.MouseEvent)}
	 * is called even when no mouse button is pressed.
	 * <p>
	 * You need to setMouseTracking() to \c true in order to use MouseGrabber (see mouseGrabber()).
	 */
	public boolean hasMouseTracking() {
		return mouseTrckn;
	}
	
	/**
	 * Sets the {@link #hasMouseTracking()} value.
	 */
	public void setMouseTracking(boolean enable) {		
		if(!enable) {
			if( mouseGrabber() != null )
				mouseGrabber().setGrabsMouse(false);
			setMouseGrabber(null);			
		}
		mouseTrckn = enable;
	}
	
	/**
	 * Calls {@link #setMouseTracking(boolean)} to toggle the {@link #hasMouseTracking()} value.
	 */
	public void toggleMouseTracking() {
		setMouseTracking(!hasMouseTracking());			
	}
	
  //4. State of the viewer
	
	/**
	 * Returns {@code true} if this Scene is associated to an offscreen 
	 * renderer and {@code false} otherwise.
	 * 
	 * @see #Scene(PApplet, PGraphicsOpenGL)
	 */
	
	public boolean isOffscreen() {
		return offscreen;
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
	 * Toggles the draw with constraint on and off.
	 */
	public void toggleDrawWithConstraint() {
		if (drawIsConstrained())
			setDrawWithConstraint(false);
		else
			setDrawWithConstraint(true);
	}
	
	/**
	 * Returns {@code true} if drawn is currently being constrained and {@code
	 * false} otherwise.
	 */
	public boolean drawIsConstrained() {
		return withConstraint;
	}

	/**
	 * Constrain frame displacements according to {@code wConstraint}
	 */
	public void setDrawWithConstraint(boolean wConstraint) {
		withConstraint = wConstraint;
	}
	
	/**
	 * Returns {@code true} if axis is currently being drawn and {@code false}
	 * otherwise.
	 */
	public boolean axisIsDrawn() {
		return axisIsDrwn;
	}

	/**
	 * Returns {@code true} if grid is currently being drawn and {@code false}
	 * otherwise.
	 */
	public boolean gridIsDrawn() {
		return gridIsDrwn;
	}

	/**
	 * Returns {@code true} if the frames selection visual hints are currently
	 * being drawn and {@code false} otherwise.
	 */
	public boolean frameSelectionHintIsDrawn() {
		return frameSelectionHintIsDrwn;
	}
	
	/**
	 * Returns {@code true} if axis is currently being drawn and {@code false}
	 * otherwise.
	 */
	public boolean interactiveFrameIsDrawn() {
		return iFrameIsDrwn;
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
	 * Sets the display of the interactive frames' selection hints according to
	 * {@code draw}
	 */
	public void setFrameSelectionHintIsDrawn(boolean draw) {
		frameSelectionHintIsDrwn = draw;
	}
	
	/**
   * Returns {@code true} if the cursor is hidden in the first person
   * camera profile (meaning that the {@link remixlab.proscene.Scene.MouseAction#LOOK_AROUND}
   * mouse action whill be performed by just moving the mouse) and {@code false} otherwise.
   * 
   * @see #hideCursorOnFirstPerson(boolean)
   * @see #toggleCursorHiddenOnFirstPerson()
   */
	public boolean cursorIsHiddenOnFirstPerson() {
		return hideCursorOn1stPerson;
	}
	
  /**
   * Toggles {@link #cursorIsHiddenOnFirstPerson()}.
   * 
   * @see #hideCursorOnFirstPerson(boolean)
   */
	public void toggleCursorHiddenOnFirstPerson() {
		hideCursorOnFirstPerson(!cursorIsHiddenOnFirstPerson());
	}
	
	/**
	 * Hides the cursor on the first person camera profile if {@code hide}
	 * is {@code true}, otherwise shows it. Default behaviour is to show the
	 * cursor on the first person camera profle.
	 * <p>
	 * If the cursor is hidden in the first person
   * camera profile the {@link remixlab.proscene.Scene.MouseAction#LOOK_AROUND}
   * mouse action will be performed by just moving the mouse. The
   * {@link remixlab.proscene.Scene.MouseAction#LOOK_AROUND} mouse action
   * binding is never changed by a call to this method.
   * <p>
   * <b>Attention:</b> If the {@link #cursorIsHiddenOnFirstPerson()} maybe you'd
   * want to disable mouse tracking (i.e., by a call to {@link #setMouseTracking(boolean)})
   * which doesn't seem to make too much sense when the mouse cursor is not shown. 
	 */
	public void hideCursorOnFirstPerson(boolean hide) {
		hideCursorOn1stPerson = hide;
		if (currentCameraProfile.mode() == CameraProfile.Mode.FIRST_PERSON)
			if (hideCursorOn1stPerson)
				parent.noCursor();
			else									
				parent.cursor();
	}
	/**
	 * Returns {@code true} if the keyboard is currently being handled by proscene
	 * and {@code false} otherwise. Set keyboard handling with
	 * {@link #enableMouseHandling(boolean)}.
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
		if (enable)
			enableKeyboardHandling();
		else
			disableKeyboardHandling();
	}

	/**
	 * Enables Proscene keyboard handling.
	 * 
	 * @see #keyboardIsHandled()
	 * @see #enableMouseHandling()
	 * @see #disableKeyboardHandling()
	 */
	public void enableKeyboardHandling() {
		if( keyboardIsHandled() )
			return;
		keyboardHandling = true;
		parent.registerKeyEvent(dE);
	}

	/**
	 * Disables Proscene keyboard handling.
	 * 
	 * @see #keyboardIsHandled()
	 */
	public void disableKeyboardHandling() {
		if( !keyboardIsHandled() )
			return;
		keyboardHandling = false;
		parent.unregisterKeyEvent(dE);
	}
	
	/**
	 * Returns {@code true} if the mouse is currently being handled by proscene and
	 * {@code false} otherwise. Set mouse handling with
	 * {@link #enableMouseHandling(boolean)}.
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
		if (enable)
			enableMouseHandling();
		else
			disableMouseHandling();
	}

	/**
	 * Enables Proscene mouse handling.
	 * 
	 * @see #mouseIsHandled()
	 * @see #disableMouseHandling()
	 * @see #enableKeyboardHandling()
	 */
	public void enableMouseHandling() {
		if( mouseIsHandled() )
			return;
		mouseHandling = true;
		parent.registerMouseEvent(dE);
	}

	/**
	 * Disables Proscene mouse handling.
	 * 
	 * @see #mouseIsHandled()
	 */
	public void disableMouseHandling() {
		if( !mouseIsHandled() )
			return;
		mouseHandling = false;
		parent.unregisterMouseEvent(dE);
	}
	
	// 2. Associated objects	

	/**
	 * Returns the associated Camera, never {@code null}.
	 */
	public Camera camera() {
		return cam;
	}

	/**
	 * Replaces the current {@link #camera()} with {@code camera}
	 */
	public void setCamera(Camera camera) {
		if (camera == null)
			return;

		camera.setSceneRadius(radius());
		camera.setSceneCenter(center());

		camera.setScreenWidthAndHeight(pg.width, pg.height);

		cam = camera;

		showAll();
	}	
	
  //4. Scene dimensions

	/**
	 * Returns the scene radius.
	 * <p>
	 * Convenience wrapper function that simply calls {@code
	 * camera().sceneRadius()}
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
	 * Convenience wrapper function that simply returns {@code
	 * camera().sceneCenter()}
	 * 
	 * @see #setCenter(PVector) {@link #radius()}
	 */
	public PVector center() {
		return camera().sceneCenter();
	}	

	/**
	 * Sets the {@link #radius()} of the Scene.
	 * <p>
	 * Convenience wrapper function that simply returns {@code
	 * camera().setSceneRadius(radius)}
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
	 * Convenience wrapper function that simply calls {@code
	 * camera().showEntireScene()}
	 * 
	 * @see remixlab.proscene.Camera#showEntireScene()
	 */
	public void showAll() {
		camera().showEntireScene();
	}
	
	/**
	 * Convenience wrapper function that simply returns {@code
	 * camera().interpolateToZoomOnPixel(pixel)}.
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
	 * Convenience wrapper function that simply returns {@code
	 * camera().setSceneCenterFromPixel(pixel)}
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

	/**
	 * Returns the {@link PApplet#width} to {@link PApplet#height} aspect ratio of
	 * the processing display window.
	 */
	public float aspectRatio() {
		return (float) pg.width / (float) pg.height;
	}
	
//Device registration
	
	/**
	 * Adds an HIDevice to the scene.
	 * 
	 * @see #removeDevice(HIDevice)
	 * @see #removeAllDevices()
	 */
	public void addDevice(HIDevice device) {
		devices.add(device);
	}
	
	/**
	 * Removes the device from the scene.
	 * 
	 * @see #addDevice(HIDevice)
	 * @see #removeAllDevices()
	 */
	public void removeDevice(HIDevice device) {
		devices.remove(device);
	}
	
	/**
	 * Removes all registered devices from the scene.
	 * 
	 * @see #addDevice(HIDevice)
	 * @see #removeDevice(HIDevice)
	 */
	public void removeAllDevices() {
		devices.clear();
	}
	
	// 10. Draw method registration

	/**
	 * Attempt to add a 'draw' handler method to the Scene. The default event
	 * handler is a method that returns void and has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #removeDrawHandler()
	 */
	public void addDrawHandler(Object obj, String methodName) {
		try {
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { Scene.class });
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			  PApplet.println("Something went wrong when registering your " + methodName + " method");
			  e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'draw' handler method (if any has previously been added to
	 * the Scene).
	 * 
	 * @see #addDrawHandler(Object, String)
	 */
	public void removeDrawHandler() {
		drawHandlerMethod = null;
		drawHandlerObject = null;
		drawHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered a 'draw' handler method to
	 * the Scene and {@code false} otherwise.
	 */
	public boolean hasRegisteredDrawHandler() {
		if (drawHandlerMethodName == null)
			return false;
		return true;
	}
	
	// 11. Animation
	
	/**
	 * Returns the target frame rate.
	 * 
	 * @see #setFrameRate(float, boolean)
	 */
	public float frameRate() {
		return targetFrameRate;
	}
	
	/**
	 * Convenience function that simply calls {@code setFrameRate(fRate, true)}.
	 * 
	 * @see #setFrameRate(float, boolean)
	 */
	public void setFrameRate(float fRate) {
		setFrameRate(fRate, true);
	}
	
	/**
	 * Specifies the number of frames to be displayed every second. If the processor
	 * is not fast enough to maintain the specified rate, it will not be achieved.
	 * <p>
	 * For example, the function call setFrameRate(30) will attempt to refresh 30 times
	 * a second. It is recommended to set the frame rate within setup(). If restart is {@code true}
	 * and {@link #animationIsStarted()} then {@link #restartAnimation()} is called.
	 * <p>
	 * The default rate is 60 frames per second.
	 */
	public void setFrameRate(float fRate, boolean restart) {
		targetFrameRate = fRate;
		parent.frameRate(targetFrameRate);
		if(animationIsStarted() && restart)
			restartAnimation();
	}
	
	/**
	 * Return {@code true} when the animation loop is started.
	 * <p>
	 * Proscene animation loop relies on processing drawing loop. The {@link #draw()} function will
	 * check when {@link #animationIsStarted()} and then called the animation handler method
	 * (set with {@link #addAnimationHandler(Object, String)}) or {@link #animate()} (if no handler
	 * has been added to the scene) every {@link #animationPeriod()} milliseconds. In addition,
	 * During the drawing loop, the variable {@link #animatedFrameWasTriggered} is set
   * to {@code true} each time an animated frame is triggered (and {@code false} otherwise),
   * which is useful to notify to the outside world when an animation event occurs. 
	 * <p>
	 * Be sure to call {@code loop()} before an animation is started.
	 * <p>
	 * <b>Note:</b> The drawing frame rate may be modified when {@link #startAnimation()} is called,
	 * depending on the {@link #animationPeriod()}.   
	 * <p>
	 * Use {@link #startAnimation()}, {@link #stopAnimation()} or {@link #toggleAnimation()}
	 * to change this value.
	 * 
	 * @see #startAnimation()
	 * @see #addAnimationHandler(Object, String)
	 * @see #animate()
	 */
	public boolean animationIsStarted() {
		return animationStarted;
	}
	
	/**
	 * The animation loop period, in milliseconds. When {@link #animationIsStarted()}, this is
	 * the delay that takes place between two consecutive iterations of the animation loop.
	 * <p>
	 * This delay defines a target frame rate that will only be achieved if your
	 * {@link #animate()} and {@link #draw()} methods are fast enough. If you want to know
	 * the maximum possible frame rate of your machine on a given scene,
	 * {@link #setAnimationPeriod(float)} to {@code 1}, and {@link #startAnimation()}. The display
	 * will then be updated as often as possible, and the frame rate will be meaningful.  
	 * <p>
	 * Default value is 16.6666 milliseconds (60 Hz) which matches <b>processing</b> default
	 * frame rate.
	 * <p>
	 * <b>Note:</b> This value is taken into account only the next time you call
	 * {@link #startAnimation()}. If {@link #animationIsStarted()}, you should
	 * {@link #stopAnimation()} first. See {@link #restartAnimation()} and
	 * {@link #setAnimationPeriod(float, boolean)}.
	 * 
	 * @see #setAnimationPeriod(float, boolean)
	 */
	public float animationPeriod() {
		return animationPeriod;
	}
	
	/**
	 * Convenience function that simply calls {@code setAnimationPeriod(period, true)}.
	 * 
	 * @see #setAnimationPeriod(float, boolean)
	 */
	public void setAnimationPeriod(float period) {
		setAnimationPeriod(period, true);
	}
	
	/**
	 * Sets the {@link #animationPeriod()}, in milliseconds. If restart is {@code true}
	 * and {@link #animationIsStarted()} then {@link #restartAnimation()} is called.
	 * <p>
	 * <b>Note:</b> The drawing frame rate could be modified when {@link #startAnimation()} is called
	 * depending on the {@link #animationPeriod()}.
	 * 
	 * @see #startAnimation()
	 */
	public void setAnimationPeriod(float period, boolean restart) {
		if(period>0) {
			animationPeriod = period;
			animationFrameRate = 1000f/animationPeriod;
			if(animationIsStarted() && restart)				
				restartAnimation();
		}
	}
	
	/**
	 * Stops animation.
	 * <p>
	 * <b>Warning:</b> Restores the {@code PApplet} frame rate to its default value,
	 * i.e., calls {@code parent.frameRate(60)}. 
	 * 
	 * @see #animationIsStarted()
	 */
	public void stopAnimation()	{
		animationStarted = false;
		animatedFrameWasTriggered = false;
		setFrameRate(targetFrameRate, false);
	}
	
	/**
	 * Starts the animation loop.
	 * <p>
	 * Syncs the drawing frame rate according to {@link #animationPeriod()}: If the animation
	 * frame rate (which value depends on the {@link #animationPeriod()})
	 * is higher than the current {@link #frameRate()}, the frame rate is modified to match it,
	 * i.e., each drawing frame will trigger exactly one animation event. If the animation
	 * frame rate is lower than the {@link #frameRate()}, the frame rate is left unmodified,
	 * and the animation frames will be interleaved among the drawing frames in intervals
	 * needed to achieve the target {@link #animationPeriod()} (provided that your
	 * {@link #animate()} and {@link #draw()} methods are fast enough).
	 * 
	 * @see #animationIsStarted()
	 */
	public void startAnimation() {
		animationStarted = true;		
		//sync with processing drawing method:		
		currentAnimationFrame = -1;
		animatedFrameWasTriggered = false;
		if( (animationFrameRate > targetFrameRate) )
			parent.frameRate( animationFrameRate ); //bypass setFrameRate()
		else {
			parent.frameRate( targetFrameRate ); //same as setFrameRate(targetFrameRate, false)
			initialDrawingFrameWhenAnimationStarted = parent.frameCount;
			currentAnimationFrame = 0;
			animationToFrameRateRatio = animationFrameRate/targetFrameRate;
		}
	}
	
	/**
	 * Restart the animation.
	 * <p>
	 * Simply calls {@link #stopAnimation()} and then {@link #startAnimation()}.
	 */
  public void restartAnimation() {
  	stopAnimation();
  	startAnimation();
	}
  
  /**
	 * Internal use.
	 * <p>
	 * Calls the animation handler. Calls {@link #animate()} if there's no such a handler. Sets
	 * the value of {@link #animatedFrameWasTriggered} to {@code true} or {@code false}
	 * depending on whether or not an animation event was triggered during this drawing frame
	 * (useful to notify the outside world when an animation event occurs). 
	 * 
	 * @see #animationPeriod()
	 * @see #startAnimation()
	 */
	protected void performAnimation() {		
		if( currentAnimationFrame >= 0 ) {
			long previousAnimationFrame = currentAnimationFrame;
			currentAnimationFrame = PApplet.round( (parent.frameCount - initialDrawingFrameWhenAnimationStarted) * animationToFrameRateRatio );
			if(currentAnimationFrame == previousAnimationFrame) {
				animatedFrameWasTriggered = false;
				return;
			}				
		}		
		animatedFrameWasTriggered = true;		
		if (animateHandlerObject != null) {
			try {
				animateHandlerMethod.invoke(animateHandlerObject, new Object[] { this });
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your "	+ animateHandlerMethodName + " method");
				e.printStackTrace();
			}
		}
		else
			animate();
	}
	
	/**
	 * Scene animation method.
	 * <p>
	 * When {@link #animationIsStarted()}, this method defines how your scene evolves over time.
	 * <p>
	 * Overload it as needed. Default implementation is empty. You may
	 * {@link #addAnimationHandler(Object, String)} instead.
	 * <p>
	 * <b>Note</b> that remixlab.proscene.KeyFrameInterpolator (which regularly updates a Frame)
	 * do not use this method.
	 * 
	 * @see #addAnimationHandler(Object, String).
	 */
	public void animate() {
	}
	
	/**
	 * Calls {@link #startAnimation()} or {@link #stopAnimation()}, depending on
	 * {@link #animationIsStarted()}.
	 */
	public void toggleAnimation() {
		if (animationIsStarted()) stopAnimation(); else startAnimation();
	}
	
	/**
	 * Attempt to add an 'animation' handler method to the Scene. The default event
	 * handler is a method that returns void and has one single Scene parameter.
	 * 
	 * @param obj
	 *          the object to handle the event
	 * @param methodName
	 *          the method to execute in the object handler class
	 * 
	 * @see #animate()
	 */
	public void addAnimationHandler(Object obj, String methodName) {
		try {
			animateHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { Scene.class });
			animateHandlerObject = obj;
			animateHandlerMethodName = methodName;
		} catch (Exception e) {
			  PApplet.println("Something went wrong when registering your " + methodName + " method");
			  e.printStackTrace();
		}
	}

	/**
	 * Unregisters the 'animation' handler method (if any has previously been added to
	 * the Scene).
	 * 
	 * @see #addAnimationHandler(Object, String)
	 */
	public void removeAnimationHandler() {
		animateHandlerMethod = null;
		animateHandlerObject = null;
		animateHandlerMethodName = null;
	}

	/**
	 * Returns {@code true} if the user has registered an 'animation' handler method to
	 * the Scene and {@code false} otherwise.
	 */
	public boolean hasRegisteredAnimationHandler() {
		if (animateHandlerMethodName == null)
			return false;
		return true;
	}
	
	public abstract void drawPath(List<Frame> path, int mask, int nbFrames, int nbSteps, float scale);
}
