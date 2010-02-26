/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.7.0
 * 
 * Copyright (c) 2010 Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. 
 */

package proscene;

import processing.core.*;

import java.awt.Point;
import java.awt.event.*;
import javax.swing.event.*; 

/**
 * A processing 3D interactive scene. A PScene provides a default interactivity for your scene through
 * the mouse and keyboard in the hope that it should fit most user needs. For those users whose needs
 * are not completely fulfill by default, proscene main interactivity mechanisms can easily be extended
 * to fit them. 
 * <p>
 * A PScene has a full reach PSCamera, an two means to manipulate objects: an {@link #interactiveFrame()}
 * single instance (which by default is null) and a {@link #mouseGrabber()} pool.
 * <p>
 * To use a PScene, you can instantiate a PScene object directly or you can implement your own derived PScene
 * class.
 * <p>
 * If you instantiate your own PScene object you should implement your {@link PApplet#draw()} as usual,
 * but enclosing your drawing calls between {@link #beginDraw()} and {@link #endDraw()}. Thus, for instance,
 * if the following code define the body of your {@link PApplet#draw()}:
 * <p>
 * {@code scene.beginDraw();}<br>
 * {@code processing drawing routines...}<br>
 * {@code scene.endDraw();}<br>
 * <p>
 * you would obtain full interactivity to manipulate your scene "for free".
 * <p>
 * If you derive from PScene, you should implement {@link #scene()} which defines the objects
 * in your scene. Then all you have to do is to call {@link #scene()} from {@link PApplet#draw()}, e.g.,
 * {@code public void draw() {scene.draw();}}
 * <p>
 * See the examples BasicUse and AlternativeUse for an illustration of both techniques.
 */
public class PScene implements MouseWheelListener, MouseInputListener { 
    /**
     * This enum defines keyboard actions to be binded to the keyboard. 
     */
	/**
    public enum KeyboardAction { DRAW_AXIS, DRAW_GRID, DISPLAY_FPS, ENABLE_TEXT, EXIT_VIEWER,
		SAVE_SCREENSHOT, CAMERA_MODE, FULL_SCREEN, STEREO, ANIMATION, HELP, EDIT_CAMERA,
		MOVE_CAMERA_LEFT, MOVE_CAMERA_RIGHT, MOVE_CAMERA_UP, MOVE_CAMERA_DOWN,
		INCREASE_FLYSPEED, DECREASE_FLYSPEED, SNAPSHOT_TO_CLIPBOARD
	};
	*/
	
	/**
	public enum MouseHandler { CAMERA, FRAME };
	*/
	
	/**
     * This enum defines mouse click actions to be binded to the mouse. 
     */
	/**
	public enum ClickAction { NO_CLICK_ACTION, ZOOM_ON_PIXEL, ZOOM_TO_FIT, SELECT, RAP_FROM_PIXEL, RAP_IS_CENTER,
		CENTER_FRAME, CENTER_SCENE, SHOW_ENTIRE_SCENE, ALIGN_FRAME, ALIGN_CAMERA };
	*/
	
	/**
	 * This enum defines mouse actions to be binded to the mouse.
	 */
	public enum MouseAction { NO_MOUSE_ACTION,
			ROTATE, ZOOM, TRANSLATE,
			MOVE_FORWARD, LOOK_AROUND, MOVE_BACKWARD,
			SCREEN_ROTATE, ROLL, DRIVE,
			SCREEN_TRANSLATE, ZOOM_ON_REGION };
	
	//mouse actions for testing purposes
	protected MouseAction cameraLeftButton, cameraMidButton, cameraRightButton;
	protected MouseAction frameLeftButton, frameMidButton, frameRightButton;
	boolean zoomOnRegion;
	boolean rotateScreen;
	boolean translateScreen;
	
	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S
	protected static PApplet parent;
	protected PGraphics3D pg3d;
	PMatrix3D pProjectionMatrix;
	PMatrix3D pModelViewMatrix;
	
	// O B J E C T S
	protected PSCamera cam;
	protected PSInteractiveFrame glIFrame;
	boolean interactiveFrameIsACam;
	boolean iFrameIsDrwn;
	
	// Z O O M _ O N _ R E G I O N
	Point fCorner;//also used for SCREEN_ROTATE
	Point lCorner;
	
	// M o u s e   G r a b b e r
	PSMouseGrabber mouseGrbbr;
	boolean mouseGrabberIsAnIFrame;
	boolean mouseGrabberIsAnICamFrame;
	
	// D I S P L A Y   W O R L D   C O O R D I N A T E S   F L A G S
	boolean axisIsDrwn;	// world axis
	boolean gridIsDrwn;	// world XY grid
	
	// C O N S T R A I N T S
	boolean withConstraint;
	//TODO: find a better way than this hack!
	boolean readyToGo;
	
	//O N L I N E   H E L P
	boolean helpIsDrwn;
	PFont font;
	
	/**
	 * All viewer parameters (display flags, scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation.
	 */
	public PScene(PApplet p) {
		parent = p;	
		parent.addMouseListener(this);
		parent.addMouseMotionListener(this);
		parent.addMouseWheelListener(this);
		
		//test: setting camera to revolveAround mode
		cameraLeftButton = MouseAction.ROTATE;		
		cameraMidButton = MouseAction.ZOOM;
		cameraRightButton = MouseAction.TRANSLATE;
		frameLeftButton = MouseAction.ROTATE;		
		frameMidButton = MouseAction.ZOOM;
		frameRightButton = MouseAction.TRANSLATE;
		
		zoomOnRegion = false;
		rotateScreen = false;
		translateScreen = false;
		
		cam = new PSCamera();
		setCamera(camera());
		
		pProjectionMatrix = null;
		pModelViewMatrix = null;
		
		//setDefaultShortcuts();
		//setDefaultMouseBindings();
		
		glIFrame = null;
		interactiveFrameIsACam = false;
		iFrameIsDrwn = false;
		
		setMouseGrabber(null);
		mouseGrabberIsAnIFrame = false;
		mouseGrabberIsAnICamFrame = false;
		
		withConstraint = true;
		
		showEntireScene();
		
		setAxisIsDrawn(false);
		setGridIsDrawn(false);
		setHelpIsDrawn(true);
		
		readyToGo = false;
		
		font = parent.createFont("Arial", 12);
		
		//called only once
		init();
	}
	
	// 1. Associated objects
		
	/**
	 * Returns the associated PSCamera, never {@code null}.
	 */
	public PSCamera camera() {		
		return cam;
	}
	
	/**
	 * Replaces the current {@link #camera()} with {@code camera}
	 */
	public void setCamera (PSCamera camera) {
		if (camera == null)
			return;
		
		camera.setSceneRadius(sceneRadius());
		camera.setSceneCenter(sceneCenter());		
		
		camera.setScreenWidthAndHeight(parent.width, parent.height);
		cam = camera;
	}
	
	/**
	 * Returns the PSInteractiveFrame associated to this PScene. It could be
	 * null if there's no PSInteractiveFrame associated to this PScene. 
	 * 
	 * @see #setInteractiveFrame(PSInteractiveFrame)
	 */
	public PSInteractiveFrame interactiveFrame() {
		return glIFrame;
	} 
	
	/**
	 * Sets {@code frame} as the PSInteractiveFrame associated to this PScene.
	 * 
	 * @see #interactiveFrame()
	 */
	public void setInteractiveFrame (PSInteractiveFrame frame) {
		glIFrame = frame;		
		interactiveFrameIsACam = ((interactiveFrame() != camera().frame()) &&
				                  (interactiveFrame() instanceof PSInteractiveCameraFrame));
		if (glIFrame == null)
			iFrameIsDrwn = false;
	}
	
	/**
	 * Returns the current PSMouseGrabber, or {@code null} if none currently grabs mouse events.
	 * <p> 
	 * When {@link proscene.PSMouseGrabber#grabsMouse()}, the different mouse events are sent to it
	 * instead of their usual targets ({@link #camera()} or {@link #interactiveFrame()}).
	 */
	public PSMouseGrabber mouseGrabber() {
		return mouseGrbbr;
	}
	
	/**
	 * Directly defines the {@link #mouseGrabber()}.
	 * <p> 
	 * You should not call this method directly as it bypasses the 
	 * {@link proscene.PSMouseGrabber#checkIfGrabsMouse(int, int, PSCamera)}
	 * test performed by {@link #mouseMoved(MouseEvent)}.
	 */
    protected void setMouseGrabber(PSMouseGrabber mouseGrabber) {
		mouseGrbbr = mouseGrabber;
		
		mouseGrabberIsAnIFrame = mouseGrabber instanceof PSInteractiveFrame;
		mouseGrabberIsAnICamFrame = (( mouseGrabber != camera().frame()) &&
			                         ( mouseGrabber instanceof PSInteractiveCameraFrame ));		
	}
	
	// 2. State of the viewer
	
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
	 * Toggles the state of the {@link #helpIsDrawn()}
	 * 
	 * @see #setHelpIsDrawn()
	 */
	public void toggleHelpIsDrawn() {
		setHelpIsDrawn(!helpIsDrawn());
	}
	
	/**
	 * Tests if the {@link #camera()} is in revolve mode (also known as arc-ball).
	 * The other {@link #camera()} mode is fly.
	 * 
	 * {@link #toggleCameraMode()}
	 */
	public boolean cameraIsInRevolveMode() {
		return ( (cameraLeftButton == MouseAction.ROTATE)
			  || (cameraMidButton == MouseAction.ROTATE) 
			  || (cameraRightButton == MouseAction.ROTATE));
	}
	
	/**
	 * Toggles the {@link #camera()} mode between arc-ball and fly.
	 */
	public void toggleCameraMode() {		 
		if ( cameraIsInRevolveMode() ) {
			camera().frame().updateFlyUpVector();
			camera().frame().stopSpinning();
			cameraLeftButton = MouseAction.MOVE_FORWARD;
			cameraMidButton = MouseAction.LOOK_AROUND;
			cameraRightButton = MouseAction.MOVE_BACKWARD;
		}
		else {
			cameraLeftButton = MouseAction.ROTATE;
			cameraMidButton = MouseAction.ZOOM;
			cameraRightButton = MouseAction.TRANSLATE;
		}		
	}
	
	/**
	 * Toggles the {@link #camera()} type between PERSPECTIVE and ORTHOGRAPHIC.
	 */
	public void toggleCameraType() {
		if ( camera().type() == PSCamera.Type.PERSPECTIVE )
			setCameraType(PSCamera.Type.ORTHOGRAPHIC);
		else
			setCameraType(PSCamera.Type.PERSPECTIVE);		
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
	
	/**
	 * Associates interactivity actions to default keys.
	 */
	public void defaultKeyBindings() {
		if (parent.key == '+') {
			camera().setFlySpeed(camera().flySpeed() * 1.5f);
		}
		if (parent.key == '-') {
			camera().setFlySpeed(camera().flySpeed() / 1.5f);
		}
		if (parent.key == 'a' || parent.key == 'A') {
			toggleAxisIsDrawn();
		}
		if (parent.key == 'c' || parent.key == 'C') {
			toggleCameraMode();
		}
		if (parent.key == 'e' || parent.key == 'E') {
			toggleCameraType();
		}
		if (parent.key == 'g' || parent.key == 'G') {
			toggleGridIsDrawn();
		}
		if (parent.key == 'h' || parent.key == 'H') {
			toggleHelpIsDrawn();
		}
		if (parent.key == 'i' || parent.key == 'I') {
			toggleDrawInteractiveFrame();
		}
		if (parent.key == 's' || parent.key == 'S') {
			showEntireScene();
		}
		if (parent.key == 'w' || parent.key == 'W') {
			toggleDrawWithConstraint();
		}
	}
	
	/**
	 * Displays the help text describing how interactivity actions are binded to the keyboard and mouse.
	 */
	public void help() {
		parent.textFont(font);
		parent.textMode(PApplet.SCREEN);
		
		String textToDisplay = new String();
		textToDisplay += "KEYBOARD\n";
		textToDisplay += "+/-: Increase/Decrease fly speed (only for fly camera mode)\n";
		textToDisplay += "a/g: Toggle axis/grid drawn\n";
		textToDisplay += "c: Toggle camera mode (arcball or fly mode)\n";
		textToDisplay += "e: Toggle camera type (ortographic or perspective)\n";
		textToDisplay += "h: Toggle the display of this help\n";
		textToDisplay += "i: Toggle interactivity between camera and interactiv frame (if any)\n";
		textToDisplay += "s: Show entire scene\n";
		textToDisplay += "w: Toggle draw with constraint (if any)\n";
		textToDisplay += "MOUSE (left, middle and right buttons resp.)\n";
		textToDisplay += "Arcball mode: motate, zoom and translate\n";
		textToDisplay += "Fly mode: move forward, look around, and move backward\n";
		textToDisplay += "Double click: align scene, show entire scene, and center scene\n";
		textToDisplay += "MOUSE MODIFIERS\n";
		textToDisplay += "shift+left button: zoom on region\n";
		textToDisplay += "ctrl+left button: rotate screen\n";
		textToDisplay += "altgraph+left button: translate screen\n";
		//parent.textAlign(PApplet.CENTER, PApplet.CENTER);
		//parent.textAlign(PApplet.RIGHT);		
		parent.fill(0, 255, 0);		
		parent.textLeading(20);
		parent.text(textToDisplay, 10, 10, (parent.width-20), (parent.height-20));
	}
	
	// 3. Drawing methods
	
	/**
	 * This method is called before the first drawing and should be overloaded to initialize stuff
	 * not initialized in {@code PApplet.setup()}. The default implementation is empty.
	 * <p>
	 * Typical usage include {@link #camera()} initialization ({@link #showEntireScene()}) and
	 * PScene state setup ({@link #setAxisIsDrawn(boolean)}, {@link #setGridIsDrawn(boolean)}
	 * {@link #setHelpIsDrawn(boolean)}).
	 */
	public void init() {}
	
	/** Main paint method.
	 * <p>
	 * Calls the following methods, in that order:<br>
	 * {@link #beginDraw()}: Sets processing camera parameters from the PSCamera and displays
	 * axis and grid accordingly to user flags. <br>
	 * {@link #scene()}: Main drawing method that could be overloaded.  <br>
	 * {@link #endDraw()}: Displays some visual hints, such the {@link #help()} text.
	 */
    public void draw() {
		beginDraw();
		scene();
		endDraw();
	}
    
    /**
     * Sets the processing camera parameters from {@link #camera()} and displays
     * axis and grid accordingly to user flags
     */
	protected void beginDraw() {
		pg3d = (PGraphics3D) parent.g;  // g may change
		//TODO would be nice to check if the background was set and set if not (no not set it if yes).
		setPCameraProjection();
		setPCameraMatrix();
		
		if (gridIsDrawn()) drawGrid(camera().sceneRadius());
		if (axisIsDrawn()) drawAxis(camera().sceneRadius());		
	}
	
	/** The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from PScene, this is the method you should overload,
	 * but no if you instantiate your own PScene object (in this case you should just overload
	 * {@code PApplet.draw()} to define your scene).
	 * <p> 
	 * The processing camera set in {@link #beginDraw()} converts from the world to the camera
	 * coordinate systems. Vertices given in {@code scene()} can then be considered as being
	 * given in the world coordinate system. The camera is moved in this world using the mouse.
	 * This representation is much more	intuitive than a camera-centric system (which for 
	 * instance is the standard in OpenGL).
	 */
	public void scene() {}
	
	/**
	 * Displays some visual hints, such the {@link #help()} text.
	 */
	protected void endDraw() {
		if(readyToGo) {
			if( helpIsDrawn() ) help();
			if( zoomOnRegion ) drawZoomWindow();
			if( rotateScreen ) drawScreenRotateLine();
		//parent.pushMatrix();
		//setPCameraMatrix();
		//if (gridIsDrawn()) drawGrid(camera().sceneRadius());
		//if (axisIsDrawn()) drawAxis(camera().sceneRadius());
		//parent.popMatrix();
		}
		else
			readyToGo = true;
	}
	
	// 4. Scene dimensions
	
	/**
	 * Returns the scene radius.
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().sceneRadius()}
	 * 
	 * @see #setSceneRadius(float)
	 * @see #sceneCenter()
	 */
	public float sceneRadius () {
		return camera().sceneRadius();
	} 
	
	/**
	 * Returns the scene center.
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().sceneCenter()}
	 * 
	 * @see #setSceneCenter(PVector)
	 * {@link #sceneRadius()}
	 */
	public PVector sceneCenter () {
		return camera().sceneCenter();
	} 
	
	/**
	 * Sets the {@link #sceneRadius()} of the PScene. 
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().setSceneRadius(radius)}
	 * 
	 * @see #setSceneCenter(PVector)
	 */
	public void setSceneRadius (float radius) {
		camera().setSceneRadius(radius);
	}
	
	/**
	 * Sets the {@link #sceneCenter()} of the PScene. 
	 * <p>
	 * Convenience wrapper function that simply calls {@code }
	 * 
	 * @see #setSceneRadius(float)
	 */
	public void setSceneCenter (PVector center) {
		camera().setSceneCenter(center);
	}
	
	/**
	 * Sets the {@link #sceneCenter()} and {@link #sceneRadius()} of the PScene from the
	 * {@code min} and {@code max} PVectors. 
	 * <p>
	 * Convenience wrapper function that simply calls {@code camera().setSceneBoundingBox(min,max)}
	 * 
	 * @see #setSceneRadius(float)
	 * @see #setSceneCenter(PVector)
	 */
	public void setSceneBoundingBox (PVector min, PVector max) {
		camera().setSceneBoundingBox(min,max);
	}
	
	/**
	 * Convenience wrapper function that simply calls {@code camera().showEntireScene()}
	 * 
	 * @see proscene.PSCamera#showEntireScene()
	 */
	public void showEntireScene () {
		camera().showEntireScene();
	}
	
	// 5. State of the viewer
	
	/**
	 * Returns the current {@link #camera()} type.
	 */
	public final PSCamera.Type cameraType() {
		return camera().type();
	}
	
	/**
	 * Sets the {@link #camera()} type.
	 */
	public void setCameraType(PSCamera.Type type) {
		if ( type != camera().type() ) {
			camera().setType(type);		
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
	 * Returns {@code true} if the {@link #help()} is currently being drawn and {@code false} otherwise. 
	 */
	public boolean helpIsDrawn() {
		return helpIsDrwn;
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
	 * 
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
	 * 
	 */
	public void setGridIsDrawn(boolean draw) {
		gridIsDrwn = draw;
	}
	
	/**
	 * Convenience function that simply calls {@code setHelpIsDrawn(true)}
	 */
	public void setHelpIsDrawn() {
		setHelpIsDrawn(true);
	}
	
	/**
	 * Sets the display of the {@link #help()} according to {@code draw}
	 * 
	 */
	public void setHelpIsDrawn(boolean draw) {
		helpIsDrwn = draw;
	}
	
	public void setDrawInteractiveFrame() {
		setDrawInteractiveFrame(true);
	}
	
	/**
	 * Sets the interactivity to the PScene {@link #interactiveFrame()} instance according to {@code draw}
	 */
    public void setDrawInteractiveFrame(boolean draw) {
    	if (draw && (glIFrame == null))
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
     * Convenience function that simply calls {@code drawAxis(1.0f)} 
     */
	public static void drawAxis() {
		drawAxis(1.0f);
	}
	
	/**
	 * Draws an axis of length {@code length} which origin correspond to that
	 * of the world coordinate system.
	 * 
	 * @see #drawGrid(float, int)
	 */
	public static void drawAxis(float length) {
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;
		
		//parent.noLights();
		
		parent.beginShape(PApplet.LINES);
		// The X
		parent.stroke(255, 178, 178);
		parent.vertex(charShift,  charWidth, -charHeight);
		parent.vertex(charShift, -charWidth,  charHeight);
		parent.vertex(charShift, -charWidth, -charHeight);
		parent.vertex(charShift,  charWidth,  charHeight);
		// The Y
		parent.stroke(178, 255, 178);
		parent.vertex( charWidth, charShift, charHeight);
		parent.vertex(0.0f,       charShift, 0.0f);
		parent.vertex(-charWidth, charShift, charHeight);
		parent.vertex(0.0f,       charShift, 0.0f);
		parent.vertex(0.0f,       charShift, 0.0f);
		parent.vertex(0.0f,       charShift, -charHeight);
		// The Z
		parent.stroke(178, 178, 255);
		parent.vertex(-charWidth,  charHeight, charShift);
		parent.vertex( charWidth,  charHeight, charShift);
		parent.vertex( charWidth,  charHeight, charShift);
		parent.vertex(-charWidth, -charHeight, charShift);
		parent.vertex(-charWidth, -charHeight, charShift);
		parent.vertex( charWidth, -charHeight, charShift);		
		parent.endShape();
	    
		parent.noStroke();
		// Z axis
		parent.fill(178, 178, 255);
		drawArrow(length, 0.01f*length);		
		
		// X Axis
		parent.fill(255, 178, 178);
		parent.pushMatrix();		
		parent.rotateY(PApplet.HALF_PI);
		drawArrow(length, 0.01f*length);
		parent.popMatrix();
		
		// Y Axis
		parent.fill(178, 255, 178);
		parent.pushMatrix();
		parent.rotateX(-PApplet.HALF_PI);
		drawArrow(length, 0.01f*length);
		parent.popMatrix();
		
		//parent.lights();		
	}
	
	/**
	 * Simply calls {@code drawArrow(length, 0.05f * length)}
	 * 
	 * @see #drawArrow(float, float)
	 */	
	public static void drawArrow(float length){
		float radius = 0.05f * length;
		drawArrow(length, radius);
	}
	
	/**
	 * Draws a 3D arrow along the positive Z axis. 
	 * <p> 
	 * {@code length} and {@code radius} define its geometry. 
	 * <p> 
	 * Use {@link #drawArrow(PVector, PVector, float)} to place the arrow in 3D. 
	 */
	public static void drawArrow(float length, float radius) {
		float head = 2.5f*(radius / length) + 0.1f;
		float coneRadiusCoef = 4.0f - 5.0f * head;
		
		cylinder(radius, length * (1.0f - head/coneRadiusCoef));
		parent.translate(0.0f, 0.0f, length * (1.0f - head));
		cone(coneRadiusCoef * radius, head * length);
		parent.translate(0.0f, 0.0f, -length * (1.0f - head));
	}
	
	/**
	 * Draws a 3D arrow between the 3D point {@code from} and the 3D point {@code to},
	 * both defined in the current ModelView coordinates system.
	 * 
	 * @see #drawArrow(float, float)
	 */
	public static void drawArrow(PVector from, PVector to, float radius) {
		parent.pushMatrix();
		parent.translate(from.x,from.y,from.z);		
		parent.applyMatrix(new PSQuaternion(new PVector(0,0,1), PVector.sub(to, from)).matrix());		
		drawArrow(PVector.sub(to, from).mag(), radius);
		parent.popMatrix();
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(1.0f, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public static void drawGrid() {
		drawGrid(1.0f, 10);
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(size, 10)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public static void drawGrid(float size) {
		drawGrid(size, 10);
	}
	
	/**
	 * Convenience function that simply calls {@code drawGrid(1.0f, nbSubdivisions)}
	 * 
	 * @see #drawGrid(float, int)
	 */
	public static void drawGrid(int nbSubdivisions) {
		drawGrid(1.0f, nbSubdivisions);
	}
	
	/** Draws a grid in the XY plane, centered on (0,0,0)
	 * (defined in the current coordinate system).
	 * <p>
	 * {@code size} (processing scene units) and {@code nbSubdivisions} define its geometry.
	 * 
	 * @see #drawAxis(float) 
	 */
	public static void drawGrid(float size, int nbSubdivisions) {
		//parent.noLights();		
		parent.stroke(170, 170, 170);		
		parent.beginShape(PApplet.LINES);
		for (int i=0; i<=nbSubdivisions; ++i) {
			final float pos = size*(2.0f*i/nbSubdivisions-1.0f);
			parent.vertex(pos, -size);
			parent.vertex(pos, +size);
			parent.vertex(-size, pos);
			parent.vertex( size, pos);
		}
		parent.endShape();		
		parent.noStroke();		
		//parent.lights();
	}
	
	/**
	 * Draws a rectangle on the screen showing the region where a zoom
	 * operation is taking place. 
	 */
	protected void drawZoomWindow() {
		float threshold = 0.01f;
		float z = camera().zNear() + threshold * ( camera().zFar() - camera().zNear() ); 
		PVector v1 = new PVector();
		PVector v2 = new PVector();		
		float halfWidthSpace;
		float halfHeightSpace;		
		if( camera().type() == PSCamera.Type.PERSPECTIVE ) {							
			halfWidthSpace = PApplet.tan(camera().horizontalFieldOfView()/2) * z;
			halfHeightSpace = PApplet.tan(camera().fieldOfView()/2) * z;
		}
		else {
			float wh[] = camera().getOrthoWidthHeight();
			halfWidthSpace = wh[0];
			halfHeightSpace = wh[1];
			//any z value should do it, since camera projection is ortho
			//v1 = camera().unprojectedCoordinatesOf( new PVector(fCorner.x, fCorner.y, (z - camera().zNear()) / (camera().zFar() - camera().zNear())), camera().frame() );
			//v2 = camera().unprojectedCoordinatesOf( new PVector(lCorner.x, lCorner.y, (z - camera().zNear()) / (camera().zFar() - camera().zNear())), camera().frame() );
			//v1 = camera().unprojectedCoordinatesOf( new PVector(fCorner.x, fCorner.y, 0.5f), camera().frame() );
			//v2 = camera().unprojectedCoordinatesOf( new PVector(lCorner.x, lCorner.y, 0.5f), camera().frame() );
		}		
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();			
		float p2x = (float) lCorner.getX();
		float p2y = (float) lCorner.getY();
		
		//translate screen origin to center
		p1x = p1x - (parent.width/2);
		p1y = p1y - (parent.height/2);
		p2x = p2x - (parent.width/2);
		p2y = p2y - (parent.height/2);			
		
		//normalize
		p1x = p1x / (parent.width/2);
		p1y = p1y / (parent.height/2);
		p2x = p2x / (parent.width/2);
		p2y = p2y / (parent.height/2);								
		
		v1.x = halfWidthSpace * p1x;
		v1.y = halfHeightSpace * p1y;			
		v2.x = halfWidthSpace * p2x;
		v2.y = halfHeightSpace * p2y;
					
		//v1.z = -z;
		//v2.z = -z;		
		
        parent.pushMatrix();
		camera().frame().applyTransformation(parent);
		
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();		
		
		parent.beginShape();
		parent.vertex(v1.x, v1.y, -z);
		parent.vertex(v2.x, v1.y, -z);
		parent.vertex(v2.x, v2.y, -z);
		parent.vertex(v1.x, v2.y, -z);		
		parent.endShape(PApplet.CLOSE);
		
		parent.strokeWeight(1);
		parent.noStroke();
		
		parent.popMatrix();
	}
	
	/**
	 * Draws visual hint (a line on the screen) when a screen
	 * rotation is taking place. 
	 */
	protected void drawScreenRotateLine() {
		float threshold = 0.01f;
		float z = camera().zNear() + threshold * ( camera().zFar() - camera().zNear() ); 
		PVector v1 = new PVector();
		float halfWidthSpace;
		float halfHeightSpace;
		if( camera().type() == PSCamera.Type.PERSPECTIVE ) {
			halfWidthSpace = PApplet.tan(camera().horizontalFieldOfView()/2) * z;
			halfHeightSpace = PApplet.tan(camera().fieldOfView()/2) * z;
		} else {
			float wh[] = camera().getOrthoWidthHeight();
			halfWidthSpace = wh[0];
			halfHeightSpace = wh[1];
		}		
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();
		
		//translate screen origin to center
		p1x = p1x - (parent.width/2);
		p1y = p1y - (parent.height/2);			
		
		//normalize
		p1x = p1x / (parent.width/2);
		p1y = p1y / (parent.height/2);		
		
		v1.x = halfWidthSpace * p1x;
		v1.y = halfHeightSpace * p1y;		
		
		//v1.z = -z;
		//v2.z = -z;
		
		parent.pushMatrix();
		camera().frame().applyTransformation(parent);
		
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();		
		
		PVector pnt = camera().frame().coordinatesOf(camera().revolveAroundPoint());
		parent.beginShape(PApplet.LINE);
		parent.vertex(v1.x, v1.y, -z);
		parent.vertex(pnt.x, pnt.y, pnt.z);				
		parent.endShape();
		
		parent.strokeWeight(1);
		parent.noStroke();
		
		parent.popMatrix();
	}
	
	// 7. Mouse customization
	
	// 8. Keyboard customization
	
	// 9. Mouse, keyboard and event handlers
	
	//mouseClicked, mouseEntered, mouseExited, mousePressed, mouseReleased
	
	// /**
	//only in interface MouseMotionListener
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Sets the PSCamera from processing camera parameters.
	 * <p>
	 * {@link #setMouseGrabber(PSMouseGrabber)} to the PSMouseGrabber that grabs
	 * the mouse (or to {@code null} if none of them grab it).
	 */
	public void mouseMoved(MouseEvent event) {
		//TODO: hack, sometimes setMouseGrabber is called by mouseMove before proper setup
		if ( readyToGo ) {
		//camera().computeProjectionMatrix();
		//camera().computeModelViewMatrix();
		if((pProjectionMatrix != null) && (pModelViewMatrix != null)) {
			camera().setProjectionfromPCamera(pProjectionMatrix);
			camera().setModelViewfromPCamera(pModelViewMatrix);
		}		
		//need in order to make mousewheel work properly 
		setMouseGrabber(null);
		
		for (PSMouseGrabber mg: PSMouseGrabber.MouseGrabberPool) {
			mg.checkIfGrabsMouse(event.getX(), event.getY(), camera());
			if(mg.grabsMouse())
				setMouseGrabber(mg);
		}
		}
	}
	
	/** Implementation of the MouseMotionListener interface method.
	 * <p>
	 * When the user clicks on the mouse: If a {@link #mouseGrabber()} is defined,
	 * {@link proscene.PSMouseGrabber#mousePressEvent(MouseEvent, PSCamera)} is called.
	 * Otherwise, the {@link #camera()} or the {@link #interactiveFrame()} interprets
	 * the mouse displacements,	depending on mouse bindings.
	 * 
	 * @see #mouseDragged(MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		if ( readyToGo ) {
		//ZOOM_ON_REGION:
	    if ( event.isShiftDown() || event.isControlDown() || event.isAltGraphDown() ) {
	    if (event.isShiftDown()) {
	    	fCorner = event.getPoint();
	    	lCorner = event.getPoint();
			zoomOnRegion = true;
			camera().frame().startAction(PScene.MouseAction.ZOOM_ON_REGION, withConstraint);			
		}
	    if (event.isControlDown()) {
	    	fCorner = event.getPoint();
	    	rotateScreen = true;
	    	camera().frame().startAction(PScene.MouseAction.SCREEN_ROTATE, withConstraint);
	    }
	    if (event.isAltGraphDown()) {
	    	translateScreen = true;
	    	camera().frame().startAction(PScene.MouseAction.SCREEN_TRANSLATE, withConstraint);
	    }
	    camera().frame().mousePressEvent(event, camera());
	    }
		else
		if ( mouseGrabber() != null ) {
			if ( mouseGrabberIsAnIFrame ) {				
				PSInteractiveFrame iFrame = (PSInteractiveFrame)(mouseGrabber());
				if ( mouseGrabberIsAnICamFrame ) {
					//TODO: implement me
					//iFrame.startAction(action);
					//iFrame.mousePressEvent(event, camera());
				}
				else {
					switch (event.getButton()) {
					case MouseEvent.NOBUTTON:
					case MouseEvent.BUTTON1: { //left button
						iFrame.startAction(frameLeftButton, withConstraint);
						break;
						}
					case MouseEvent.BUTTON2: { //middle button
						iFrame.startAction(frameMidButton, withConstraint);
						break;
						}
					case MouseEvent.BUTTON3: { //right button
						iFrame.startAction(frameRightButton, withConstraint);
						break;
						}
					}
					iFrame.mousePressEvent(event, camera());
				}
			}
			else
				mouseGrabber().mousePressEvent(event, camera());
		}
		else if ( interactiveFrameIsDrawn() ) {
			switch (event.getButton()) {
			case MouseEvent.NOBUTTON:
			case MouseEvent.BUTTON1: { //left button
				interactiveFrame().startAction(frameLeftButton, withConstraint);
				break;
				}
			case MouseEvent.BUTTON2: { //middle button
				interactiveFrame().startAction(frameMidButton, withConstraint);
				break;
				}
			case MouseEvent.BUTTON3: { //right button
				interactiveFrame().startAction(frameRightButton, withConstraint);
				break;
				}
			}	
			interactiveFrame().mousePressEvent(event, camera());
		}
		else {
			switch (event.getButton()) {
			case MouseEvent.NOBUTTON:
			case MouseEvent.BUTTON1: { //left button
				camera().frame().startAction(cameraLeftButton, withConstraint);
				break;
				}
			case MouseEvent.BUTTON2: { //middle button
				camera().frame().startAction(cameraMidButton, withConstraint);
				break;
				}
			case MouseEvent.BUTTON3: { //right button
				camera().frame().startAction(cameraRightButton, withConstraint);
				break;
				}
			}	
			camera().frame().mousePressEvent(event, camera());
		}
		}
	}
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Mouse drag event is sent to the {@link #mouseGrabber()} (if any) or to the
	 * {@link #camera()} or the {@link #interactiveFrame()}, depending on mouse bindings.
	 * 
	 * @see #mouseMoved(MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {
		if ( readyToGo ) {
		// /**
		//ZOOM_ON_REGION:		
		if ( zoomOnRegion || rotateScreen || translateScreen) {
	    	if (zoomOnRegion) lCorner = event.getPoint();
	    	else if (rotateScreen) {
	    		fCorner = event.getPoint();
				camera().frame().mouseMoveEvent(event, camera());
	    	}
	    	else //translateScreen
	    		camera().frame().mouseMoveEvent(event, camera());
		} else if ( mouseGrabber()!= null ) {
			mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY(), camera());
			if (mouseGrabber().grabsMouse())
				if ( mouseGrabberIsAnICamFrame )
					//TODO: implement me
					//mouseGrabber().mouseMoveEvent(event, camera());
					((PSInteractiveFrame)mouseGrabber()).mouseMoveEvent(event, camera());
				else
					mouseGrabber().mouseMoveEvent(event, camera());
			else
				setMouseGrabber(null);
		}
		else if ( interactiveFrameIsDrawn() ) {
			interactiveFrame().mouseMoveEvent(event, camera());
		}
		else {
			camera().frame().mouseMoveEvent(event, camera());
		}
		}
	}
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Calls the {@link #mouseGrabber()}, {@link #camera()} or
	 * {@link #interactiveFrame()} mouseReleaseEvent method.
	 */
	public void mouseReleased(MouseEvent event) {
		if ( readyToGo ) {
		if( zoomOnRegion || rotateScreen || translateScreen ) {
	    	lCorner = event.getPoint();
			camera().frame().mouseReleaseEvent(event, camera());
			zoomOnRegion = false;
			rotateScreen = false;
			translateScreen = false;
		}
		else 
		if (mouseGrabber() != null ) {
    		if ( mouseGrabberIsAnICamFrame )
    			//mouseGrabber().mouseReleaseEvent(event, camera());
    			((PSInteractiveFrame)mouseGrabber()).mouseReleaseEvent(event, camera());
    		else
    			mouseGrabber().mouseReleaseEvent(event, camera());
    		mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY() , camera());
    		if (!(mouseGrabber().grabsMouse()))
    			setMouseGrabber(null);
    	}
    	else if ( interactiveFrameIsDrawn() ) {
    		interactiveFrame().mouseReleaseEvent(event, camera());
		}
		else {
			camera().frame().mouseReleaseEvent(event, camera());
		}
		}
    }
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Implements mouse double click events: left button aligns scene,
	 * middle button shows entire scene, and right button centers scene.
	 */
	public void mouseClicked(MouseEvent event) {
		if ( readyToGo && ( event.getClickCount() == 2 ) ) {			
		if ( mouseGrabber() != null ) {
			mouseGrabber().mouseDoubleClickEvent(event, camera());
		}
		else if ( interactiveFrameIsDrawn() ) {
			switch (event.getButton()) {
			case MouseEvent.NOBUTTON:
			case MouseEvent.BUTTON1: { //left button
				//align frame
				interactiveFrame().alignWithFrame(camera().frame());
				break;
				}
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3: { //right button
				//center frame:
				interactiveFrame().projectOnLine(camera().position(), camera().viewDirection());
				break;
				}
			}
		}		
		else {
			switch (event.getButton()) {
			case MouseEvent.NOBUTTON:
			case MouseEvent.BUTTON1: { //left button
				camera().frame().alignWithFrame(null, true);
				break;
				}
			case MouseEvent.BUTTON2: { //middle button
				camera().showEntireScene();
				break;
				}
			case MouseEvent.BUTTON3: { //right button
				camera().centerScene();
				break;
				}
			}
		}
		}
	}
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Current implementation is empty.
	 */ 
	public void mouseEntered(MouseEvent event) {}
	
	/**
	 * Implementation of the MouseMotionListener interface method.
	 * <p>
	 * Current implementation is empty.
	 */
	public void mouseExited(MouseEvent event) {}	
	
	/**
	 * Implementation of the MouseWheelListener interface method.
	 * <p>
	 * Calls the {@link #mouseGrabber()}, {@link #camera()} or
	 * {@link #interactiveFrame()} mouseWheelEvent method.
	 */
	public void mouseWheelMoved(MouseWheelEvent event) {
		if ( readyToGo ) {
		if ( mouseGrabber() != null )	{
			if ( mouseGrabberIsAnIFrame ) {
				PSInteractiveFrame iFrame = (PSInteractiveFrame)mouseGrabber();
				if ( mouseGrabberIsAnICamFrame ) {
					//TODO: implement me
				}
				else {
					iFrame.startAction(MouseAction.ZOOM, withConstraint);
					iFrame.mouseWheelEvent(event, camera());
				}
			}
			else
				mouseGrabber().mouseWheelEvent(event, camera());
			//test
			/**
			mouseGrabber().checkIfGrabsMouse(event.getX(), event.getY() , camera());
    		if (!(mouseGrabber().grabsMouse()))
    			setMouseGrabber(null);
    		*/
    		//end test
		}
		else if ( interactiveFrameIsDrawn() ) {
			interactiveFrame().startAction(MouseAction.ZOOM, withConstraint);
			interactiveFrame().mouseWheelEvent(event, camera());
		}
		else {
			camera().frame().startAction(MouseAction.ZOOM, withConstraint);
			camera().frame().mouseWheelEvent(event, camera());
		}
		}
	}
	
	// 10. Processing objects
	
	/**
	 * Sets the processing camera projection matrix from {@link #camera()} (PSCamera).
	 * Calls {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link proscene.PSCamera#type()}. 
	 */
	protected void setPCameraProjection() {
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
		pProjectionMatrix = pg3d.projection;		
	}
	
	/**
	 * Sets the processing camera matrix from {@link #camera()} (PSCamera).
	 * Simply calls {@code PApplet.camera()}.
	 */
	protected void setPCameraMatrix() {
		parent.camera(camera().position().x, camera().position().y, camera().position().z,
				      camera().at().x, camera().at().y, camera().at().z,
				      camera().upVector().x, camera().upVector().y, camera().upVector().z);
		pModelViewMatrix = pg3d.modelview;
	}
	
	// 11. Utility
	
	/**
	 * Draws a cylinder of width {@code w} and height {@code h}.
	 * <p>
	 * Code adapted from http://www.processingblogs.org/category/processing-java/
	 */	
	public static void cylinder(float w,float h) {
		  float px,py;

		  parent.beginShape(PApplet.QUAD_STRIP);
		  for(float i=0; i<13; i++) {
			  px=PApplet.cos(PApplet.radians(i*30))*w;
			  py=PApplet.sin(PApplet.radians(i*30))*w;
			  parent.vertex(px,py,0);
			  parent.vertex(px,py,h);
		  }
		  parent.endShape();

		  parent.beginShape(PApplet.TRIANGLE_FAN);
		  parent.vertex(0,0,0);
		  for(float i=12; i>-1; i--) {
			  px=PApplet.cos(PApplet.radians(i*30))*w;
			  py=PApplet.sin(PApplet.radians(i*30))*w;
			  parent.vertex(px,py,0);
		  }
		  parent.endShape();

		  parent.beginShape(PApplet.TRIANGLE_FAN);
		  parent.vertex(0,0,h);
		  for(float i=0; i<13; i++) {
			  px=PApplet.cos(PApplet.radians(i*30))*w;
			  py=PApplet.sin(PApplet.radians(i*30))*w;
			  parent.vertex(px,py,h);
		  }
		  parent.endShape();	  
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
	public static void cone(float r, float h) {
		cone(12, 0, 0, r, h);
	}
	
	/**
	 * Places a cone with it's base centered at {@code (x,y)}, height {@code h}
	 * in positive {@code z}, radius {@code r}.
	 * <p>
	 * The code of this function was taken from http://processinghacks.com/hacks:cone
	 * Thanks to Tom Carden
	 */
	public static void cone(int detail, float x, float y, float r, float h) {		
		float unitConeX[] = new float[detail+1];
		float unitConeY[] = new float[detail+1];
		
		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
		    unitConeX[i] = (float)Math.cos(a1);
		    unitConeY[i] = (float)Math.sin(a1);
		}
		
		parent.pushMatrix();
		parent.translate(x,y);
		parent.scale(r,r);
		parent.beginShape(PApplet.TRIANGLES);
		for (int i = 0; i < detail; i++) {
			parent.vertex(unitConeX[i],unitConeY[i],0.0f);
			parent.vertex(unitConeX[i+1],unitConeY[i+1],0.0f);
			parent.vertex(0,0,h);
		}
		parent.endShape();
		parent.popMatrix();
	}	
}
