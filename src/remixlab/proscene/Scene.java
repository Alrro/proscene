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

package remixlab.proscene;

import processing.core.*;

import java.awt.Point;
import java.awt.event.*;

import javax.swing.event.*;
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
 * To use a Scene, you can instantiate a Scene object directly or you can implement your own derived Scene
 * class. You also need to call {@link #defaultKeyBindings()} from your PApplet.keyPressed() function.
 * That's all there is to it.
 * <p>
 * If you instantiate your own Scene object you should implement your {@link PApplet#draw()} as usual,
 * but enclosing your drawing calls between {@link #beginDraw()} and {@link #endDraw()}. Thus, for instance,
 * if the following code define the body of your {@link PApplet#draw()}:
 * <p>
 * {@code scene.beginDraw();}<br>
 * {@code processing drawing routines...}<br>
 * {@code scene.endDraw();}<br>
 * <p>
 * you would obtain full interactivity to manipulate your scene "for free".
 * <p>
 * If you derive from Scene instead, you should implement {@link #proscenium()} which
 * defines the objects in your scene. Then all you have to do is to call {@link #draw()}
 * from {@link PApplet#draw()}, e.g., {@code public void draw() {scene.draw();}}
 * (supposing {@code scene} is an instance of the Scene derived class).
 * <p>
 * See the examples BasicUse and AlternativeUse for an illustration of both techniques.
 */
public class Scene implements MouseWheelListener, MouseInputListener, PConstants {	
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
	boolean rap;
	
	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S
	public static PApplet parent;
	protected PGraphics3D pg3d;
	PMatrix3D projectionMatrix;
	PMatrix3D modelviewMatrix;
	
	// O B J E C T S
	protected Camera cam;
	protected InteractiveFrame glIFrame;
	boolean interactiveFrameIsACam;
	boolean iFrameIsDrwn;	
	
	// S C R E E N   C O O R D I N A T E S
	static float halfWidthSpace;
	static float halfHeightSpace;
	static float zC;
	
	// Z O O M _ O N _ R E G I O N
	Point fCorner;//also used for SCREEN_ROTATE
	Point lCorner;
	
	// R E V O L V E   A R O U N D   P O I N T
	private Timer rapTimer;
    private ActionListener taskRapPerformer;
    
    // E X C E P T I O N   H A N D L I N G
    protected static int beginDrawCalls;
    protected static int startCoordCalls;
	
	// M o u s e   G r a b b e r
	MouseGrabber mouseGrbbr;
	boolean mouseGrabberIsAnIFrame;
	boolean mouseGrabberIsAnICamFrame;
	
	// D I S P L A Y   F L A G S
	boolean axisIsDrwn;	// world axis
	boolean gridIsDrwn;	// world XY grid
	boolean frameSelectionHintIsDrwn;
	boolean cameraPathsAreDrwn;
	
	// C O N S T R A I N T S
	boolean withConstraint;
	//TODO: find a better way than this hack!
	protected boolean readyToGo;
	
	//O N L I N E   H E L P
	boolean helpIsDrwn;
	PFont font;
	
	/**
	 * All viewer parameters (display flags, scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation.
	 */
	public Scene(PApplet p) {
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
		rap = false;
		
		cam = new Camera();
		setCamera(camera());
		
		projectionMatrix = null;
		modelviewMatrix = null;
		
		//setDefaultShortcuts();
		//setDefaultMouseBindings();
		
		glIFrame = null;
		interactiveFrameIsACam = false;
		iFrameIsDrwn = false;
		
		setMouseGrabber(null);
		mouseGrabberIsAnIFrame = false;
		mouseGrabberIsAnICamFrame = false;
		
		withConstraint = true;
		
		//showAll();It is set in setCamera()
		
		setAxisIsDrawn(false);
		setGridIsDrawn(false);
		setFrameSelectionHintIsDrawn(false);
		setCameraPathsAreDrawn(false);
		setHelpIsDrawn(true);
		
		readyToGo = false;
		
		font = parent.createFont("Arial", 12);
		
		taskRapPerformer = new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		unSetRapFlag();
        	}
        };
        rapTimer = new Timer(1000, taskRapPerformer);
        rapTimer.setRepeats(false);
        
        // E X C E P T I O N   H A N D L I N G
        beginDrawCalls = 0;
        startCoordCalls = 0;
		
		//called only once
		init();
	}
	
	// 1. Associated objects
		
	/**
	 * Returns the associated Camera, never {@code null}.
	 */
	public Camera camera() {
		return cam;
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
	 * Sets {@code frame} as the InteractiveFrame associated to this Scene.
	 * 
	 * @see #interactiveFrame()
	 */
	public void setInteractiveFrame (InteractiveFrame frame) {
		glIFrame = frame;		
		interactiveFrameIsACam = ((interactiveFrame() != camera().frame()) &&
				                  (interactiveFrame() instanceof InteractiveCameraFrame));
		if (glIFrame == null)
			iFrameIsDrwn = false;
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
		if ( camera().type() == Camera.Type.PERSPECTIVE )
			setCameraType(Camera.Type.ORTHOGRAPHIC);
		else
			setCameraType(Camera.Type.PERSPECTIVE);		
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
	 * Associates the different interactions to default keys.
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
		if (parent.key == 'f' || parent.key == 'F') {
			toggleFrameSelectionHintIsDrawn();
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
		if (parent.key == 'r' || parent.key == 'R') {
			toggleCameraPathsAreDrawn();
		}
		if (parent.key == 's') {			
			camera().interpolateToFitScene();
		}
		if (parent.key == 'S') {
			showAll();
		}
		if (parent.key == 'w' || parent.key == 'W') {
			toggleDrawWithConstraint();
		}
		if (parent.key == 'o' || parent.key == 'O') {			
			if ( Camera.class == camera().getClass() )
				PApplet.println("Override Camera.pointUnderPixel calling gl.glReadPixels() in your own OpenGL Camera derived class. " +
						        "See the Revolve Around Point example!");
			else if (!helpIsDrawn())
				if (setRevolveAroundPointFromPixel(new Point(parent.mouseX, parent.mouseY))) {
					rap = true;
					rapTimer.start();
				}
		}
		if (parent.key == 'p' || parent.key == 'P') {
			camera().setRevolveAroundPoint(new PVector(0,0,0));
			rap = true;
			rapTimer.start();
		}
		if( parent.key == '1' || parent.key == 'j' || parent.key == 'J' ) {
			if( parent.key == '1') camera().playPath(1);			
			if( parent.key == 'j') {
				camera().addKeyFrameToPath(1);
				//debug
				/**
				PApplet.println("Frame " + camera().keyFrameInterpolator(1).numberOfKeyFrames() + " Position: "
						+ camera().position().x + ", " +  camera().position().y + ", "+ camera().position().z + " Orientation: " 
						+ camera().orientation().x + ", " + camera().orientation().y + ", " + camera().orientation().z + ", " + camera().orientation().w);
			    // */
			}
			if( parent.key == 'J') camera().deletePath(1);			
		}
		if( parent.key == '2' || parent.key == 'k' || parent.key == 'K' ) {
			if( parent.key == '2') camera().playPath(2);			
			if( parent.key == 'k') camera().addKeyFrameToPath(2);
			if( parent.key == 'K') camera().deletePath(2);
		}
		if( parent.key == '3' || parent.key == 'l' || parent.key == 'L' ) {
			if( parent.key == '3') camera().playPath(3);			
			if( parent.key == 'l') camera().addKeyFrameToPath(3);
			if( parent.key == 'L') camera().deletePath(3);
		}
		if( parent.key == '4' || parent.key == 'm' || parent.key == 'M' ) {
			if( parent.key == '4') camera().playPath(4);			
			if( parent.key == 'm') camera().addKeyFrameToPath(4);
			if( parent.key == 'M') camera().deletePath(4);
		}
		if( parent.key == '5' || parent.key == 'n' || parent.key == 'N' ) {
			if( parent.key == '5') camera().playPath(5);			
			if( parent.key == 'n') camera().addKeyFrameToPath(5);
			if( parent.key == 'N') camera().deletePath(5);
		}
	}
	
	/**
	 * Displays the help text describing how interactivity actions are binded to the keyboard and mouse.
	 */
	public void help() {
		parent.textFont(font);
		parent.textMode(SCREEN);
		
		String textToDisplay = new String();
		textToDisplay += "KEYBOARD\n";
		textToDisplay += "+/-: Increase/Decrease fly speed (only for fly camera mode)\n";
		textToDisplay += "a/g: Toggle axis/grid/ drawn\n";
		textToDisplay += "c/e: Toggle camera mode (arcball or fly mode)/Toggle camera type (orthographic or perspective)\n";
		textToDisplay += "h: Toggle the display of this help\n";
		textToDisplay += "i: Toggle interactivity between camera and interactive frame (if any)\n";
		textToDisplay += "o/p: (un)set revolve around point (implement pointUnderPixel in your OpenGL Camera)\n";
		textToDisplay += "f/r: Toggle visual hints: interactive frame selection region/key frame camera paths (if any)\n";
		textToDisplay += "(s)/S: (interpolate to) / show entire scene\n";
		textToDisplay += "w: Toggle draw with constraint (if any)\n";		
		textToDisplay += "[j..n]/[J..N]/[1..5]: set/reset/play key frame interpolators\n";
		textToDisplay += "MOUSE (left, middle and right buttons resp.)\n";
		textToDisplay += "Arcball mode: rotate, zoom and translate\n";
		textToDisplay += "Fly mode: move forward, look around, and move backward\n";
		textToDisplay += "Double click: align scene, show entire scene, and center scene\n";
		textToDisplay += "MOUSE MODIFIERS (applied to left button)\n";
		textToDisplay += "shift/ctrl/altgraph: zoom on region/rotate screen/translate screen\n";
		//parent.textAlign(CENTER, CENTER);
		//parent.textAlign(RIGHT);		
		parent.fill(0, 255, 0);		
		parent.textLeading(20);
		parent.text(textToDisplay, 10, 10, (parent.width-20), (parent.height-20));		 
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
	
	/** Main paint method.
	 * <p>
	 * Calls the following methods, in that order:<br>
	 * {@link #beginDraw()}: Sets processing camera parameters from the Camera and displays
	 * axis and grid accordingly to user flags. <br>
	 * {@link #proscenium()}: Main drawing method that could be overloaded.  <br>
	 * {@link #endDraw()}: Displays some visual hints, such the {@link #help()} text.
	 */
    public void draw() {
		beginDraw();
		proscenium();
		endDraw();
	}
    
    /**
     * Sets the processing camera parameters from {@link #camera()} and displays
     * axis, grid and interactive frames' selection hints accordingly to user flags.
     */
	public void beginDraw() {
		pg3d = (PGraphics3D) parent.g;  // g may change
		//TODO would be nice to check if the background was set and set if not (no not set it if yes).
		if ( beginDrawCalls != 0 )
			throw new RuntimeException("There should be exactly one beginDraw / endDraw calling pair. Check your draw function!");
		beginDrawCalls ++;
		
		//We set the processing camera matrices from our remixlab.proscene.Camera
		setPProjectionMatrix();
		setPModelViewMatrix();
		
		if (gridIsDrawn()) drawGrid(camera().sceneRadius());
		if (axisIsDrawn()) drawAxis(camera().sceneRadius());
		if (frameSelectionHintIsDrawn()) drawInteractiveFrameSelectionHint();
		if (cameraPathsAreDrawn()) camera().drawAllPaths();
	}
	
	/** The method that actually defines the scene.
	 * <p>
	 * If you build a class that inherits from Scene, this is the method you should overload,
	 * but no if you instantiate your own Scene object (in this case you should just overload
	 * {@code PApplet.draw()} to define your scene).
	 * <p> 
	 * The processing camera set in {@link #beginDraw()} converts from the world to the camera
	 * coordinate systems. Vertices given in {@code scene()} can then be considered as being
	 * given in the world coordinate system. The camera is moved in this world using the mouse.
	 * This representation is much more	intuitive than a camera-centric system (which for 
	 * instance is the standard in OpenGL).
	 */
	public void proscenium() {}
	
	/**
	 * Displays some visual hints, such the {@link #help()} text.
	 */
	public void endDraw() {
		if(readyToGo) {
			if( helpIsDrawn() ) help();
			if( zoomOnRegion ) drawZoomWindowHint();
			if( rotateScreen ) drawScreenRotateLineHint();
			if( rap ) drawRevolveAroundPointHint();
		}		
		else
			readyToGo = true;
		beginDrawCalls --;
		if ( beginDrawCalls != 0 )
			throw new RuntimeException("There should be exactly one beginDraw / endDraw calling pair. Check your draw function!");
	}
	
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
	 * Returns the revolve around point.
	 * <p>
	 * Convenience wrapper function that simply returns {@code camera().revolveAroundPoint()}
	 * 
	 * @see #setCenter(PVector)
	 * {@link #radius()}
	 */
	public PVector revolveAroundPoint() {
		return camera().revolveAroundPoint();
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
	 * {@code camera().setRevolveAroundPointFromPixel(pixel)}
	 * <p>
	 * Current implementation set no
	 * {@link remixlab.proscene.Camera#revolveAroundPoint()}. Override
	 * {@link remixlab.proscene.Camera#pointUnderPixel(Point)} in your openGL
	 * based camera for this to work.
	 * 
	 * @see remixlab.proscene.Camera#setRevolveAroundPointFromPixel(Point)
	 * @see remixlab.proscene.Camera#pointUnderPixel(Point)
	 */
	public boolean setRevolveAroundPointFromPixel(Point pixel) {
		return camera().setRevolveAroundPointFromPixel(pixel);
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
	
	/**
	 * Convenience function that simply calls {@code setHelpIsDrawn(true)}
	 */
	public void setHelpIsDrawn() {
		setHelpIsDrawn(true);
	}
	
	/**
	 * Sets the display of the {@link #help()} according to {@code draw}
	 */
	public void setHelpIsDrawn(boolean draw) {
		helpIsDrwn = draw;
	}
	
	public void setDrawInteractiveFrame() {
		setDrawInteractiveFrame(true);
	}
	
	/**
	 * Sets the interactivity to the Scene {@link #interactiveFrame()} instance according to {@code draw}
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
		
		parent.beginShape(LINES);
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
		parent.rotateY(HALF_PI);
		drawArrow(length, 0.01f*length);
		parent.popMatrix();
		
		// Y Axis
		parent.fill(178, 255, 178);
		parent.pushMatrix();
		parent.rotateX(-HALF_PI);
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
		parent.applyMatrix(new Quaternion(new PVector(0,0,1), PVector.sub(to, from)).matrix());		
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
		parent.beginShape(LINES);
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
	protected void drawZoomWindowHint() {
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();			
		float p2x = (float) lCorner.getX();
		float p2y = (float) lCorner.getY();
		beginScreenDrawing();
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();		
		parent.beginShape();		
		parent.vertex(xCoord(p1x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2x), yCoord(p2y), zCoord());
		parent.vertex(xCoord(p1x), yCoord(p2y), zCoord());
		parent.endShape(CLOSE);		
		parent.strokeWeight(1);
		parent.noStroke();
		endScreenDrawing();
	}
	
	/**
	 * Draws visual hint (a line on the screen) when a screen
	 * rotation is taking place. 
	 */
	protected void drawScreenRotateLineHint() {
		float p1x = (float) fCorner.getX();
		float p1y = (float) fCorner.getY();
		PVector p2 = camera().projectedCoordinatesOf(revolveAroundPoint());
		beginScreenDrawing();
		parent.stroke(255, 255, 255);
		parent.strokeWeight(2);
		parent.noFill();
		parent.beginShape(LINE);
		parent.vertex(xCoord(p1x), yCoord(p1y), zCoord());
		parent.vertex(xCoord(p2.x), yCoord(p2.y), zCoord());		
		parent.endShape();		
		parent.strokeWeight(1);
		parent.noStroke();
		endScreenDrawing();
	}
	
	/**
	 * Draws visual hint (a cross on the screen) when the {@link #revolveAroundPoint()}
	 * is being set.
	 */	
	protected void drawRevolveAroundPointHint() {
		PVector p = camera().projectedCoordinatesOf(revolveAroundPoint());
		beginScreenDrawing();
		parent.stroke(255, 255, 255);
		parent.strokeWeight(3);
		parent.noFill();
		float size = 15;
		parent.beginShape(LINES);
		parent.vertex(xCoord(p.x - size), yCoord(p.y), zCoord());
		parent.vertex(xCoord(p.x + size), yCoord(p.y), zCoord());
		parent.vertex(xCoord(p.x), yCoord(p.y - size), zCoord());
		parent.vertex(xCoord(p.x), yCoord(p.y + size), zCoord());
		parent.endShape();		
		parent.strokeWeight(1);
		parent.noStroke();
		endScreenDrawing();
	}
	
	/**
	 * Draws all InteractiveFrames' selection regions (a 10x10 pixel square) as a visual hint.
	 */
	protected void drawInteractiveFrameSelectionHint() {
		for (MouseGrabber mg: MouseGrabber.MouseGrabberPool) {
			InteractiveFrame iF = (InteractiveFrame) mg;//downcast needed
			PVector center = camera().projectedCoordinatesOf( iF.position() );
			if ( mg.grabsMouse() )				
				filledSquare(parent.color(0,255,0),center,10);
			else
				filledSquare(parent.color(190,190,190),center,10);
		}
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
	public void filledCircle(int color, PVector center, float radius) {
		float x = center.x;
		float y = center.y;
		float angle, x2, y2;
		parent.noStroke();
		parent.fill(color);
		beginScreenDrawing();
		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord());
		for (angle=0.0f;angle<=TWO_PI; angle+=0.157f) {
		    x2 = x+PApplet.sin(angle)*radius;
		    y2 = y+PApplet.cos(angle)*radius;
		    parent.vertex(Scene.xCoord(x2), Scene.yCoord(y2), Scene.zCoord());
		}		
		parent.endShape();
		endScreenDrawing();
	}
	
	/**
	 * Draws a filled square using screen coordinates.
	 * @param color Color used to fill the circle.
	 * @param center Circle screen center.
	 * @param edge Circle screen radius.
	 * 
	 * @see #beginScreenDrawing()
	 * @see #endScreenDrawing()
	 */
	public void filledSquare(int color, PVector center, float edge) {
		float x = center.x;
		float y = center.y;
		parent.noStroke();
		parent.fill(color);
		beginScreenDrawing();
		parent.beginShape(QUADS);
		parent.vertex(Scene.xCoord(x-10), Scene.yCoord(y+10), Scene.zCoord());
		parent.vertex(Scene.xCoord(x+10), Scene.yCoord(y+10), Scene.zCoord());
		parent.vertex(Scene.xCoord(x+10), Scene.yCoord(y-10), Scene.zCoord());
		parent.vertex(Scene.xCoord(x-10), Scene.yCoord(y-10), Scene.zCoord());
		parent.endShape();
		endScreenDrawing();
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
	 * {@code vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord())}.
	 * 
	 * @see #endScreenDrawing()
	 * @see #xCoord(float)
	 * @see #yCoord(float)
	 * @see #zCoord()
	 */
	public void beginScreenDrawing() {		
		if ( startCoordCalls != 0 )
			throw new RuntimeException("There should be exactly one startScreenCoordinatesSystem() call followed by a " +
					"stopScreenCoordinatesSystem() and they cannot be nested. Check your implmentation!");
		startCoordCalls ++;
		float threshold = 0.01f;
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
		camera().frame().applyTransformation(parent);
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
		parent.popMatrix();
		startCoordCalls --;
		if ( startCoordCalls != 0 )
			throw new RuntimeException("There should be exactly one startScreenCoordinatesSystem() call followed by a " +
					"stopScreenCoordinatesSystem() and they cannot be nested. Check your implmentation!");
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
	public static float xCoord(float px) {
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
	public static float yCoord(float py) {
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
	public static float zCoord() {
		return -zC;
	}
	
	/**
	 * Called from the timer to stop displaying the revolve around point visual hint. 
	 */
	protected void unSetRapFlag() {
		rap = false;
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
	 * Sets the Camera from processing camera parameters.
	 * <p>
	 * {@link #setMouseGrabber(MouseGrabber)} to the MouseGrabber that grabs
	 * the mouse (or to {@code null} if none of them grab it).
	 */
	public void mouseMoved(MouseEvent event) {
		//TODO: hack, sometimes setMouseGrabber is called by mouseMove before proper setup
		if ( readyToGo ) {
			//need in order to make mousewheel work properly
			setMouseGrabber(null);
			for (MouseGrabber mg: MouseGrabber.MouseGrabberPool) {
				mg.checkIfGrabsMouse(event.getX(), event.getY(), camera());
				if(mg.grabsMouse())
					setMouseGrabber(mg);
			}
		}
	}
	
	/** Implementation of the MouseMotionListener interface method.
	 * <p>
	 * When the user clicks on the mouse: If a {@link #mouseGrabber()} is defined,
	 * {@link remixlab.proscene.MouseGrabber#mousePressEvent(MouseEvent, Camera)} is called.
	 * Otherwise, the {@link #camera()} or the {@link #interactiveFrame()} interprets
	 * the mouse displacements,	depending on mouse bindings.
	 * 
	 * @see #mouseDragged(MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		if ( readyToGo ) {
	    if ( ( event.isShiftDown() || event.isControlDown() || event.isAltGraphDown() ) ) {
	    if ( event.isShiftDown() ) {
	    	fCorner = event.getPoint();
	    	lCorner = event.getPoint();
			zoomOnRegion = true;
			camera().frame().startAction(Scene.MouseAction.ZOOM_ON_REGION, withConstraint);			
		}
	    if ( event.isControlDown() ) {
	    	fCorner = event.getPoint();
	    	rotateScreen = true;
	    	camera().frame().startAction(Scene.MouseAction.SCREEN_ROTATE, withConstraint);
	    }
	    if ( event.isAltGraphDown() ) {
	    	translateScreen = true;
	    	camera().frame().startAction(Scene.MouseAction.SCREEN_TRANSLATE, withConstraint);
	    }
	    camera().frame().mousePressEvent(event, camera()); //totally necessary
	    }
		else
		if ( mouseGrabber() != null ) {
			if ( mouseGrabberIsAnIFrame ) {				
				InteractiveFrame iFrame = (InteractiveFrame)(mouseGrabber());
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
					((InteractiveFrame)mouseGrabber()).mouseMoveEvent(event, camera());
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
    			((InteractiveFrame)mouseGrabber()).mouseReleaseEvent(event, camera());
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
	 * Implementation of the MouseListener interface method.
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
				InteractiveFrame iFrame = (InteractiveFrame)mouseGrabber();
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
	 * Sets the processing camera projection matrix from {@link #camera()} (Camera).
	 * Calls {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link remixlab.proscene.Camera#type()}. 
	 */
	protected void setPProjectionMatrix() {
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
		projectionMatrix = pg3d.projection;
		//Processing computes the camera matrices for us (it actually computes the
		//projection matrix from our Camera parameters). We just set them back
		//(instead of calling camera().computeProjectionMatrix() which is also possible
		//but expensive)
		camera().setProjectionMatrix(projectionMatrix);
	}
	
	/**
	 * Sets the processing camera matrix from {@link #camera()} (Camera).
	 * Simply calls {@code PApplet.camera()}.
	 */
	protected void setPModelViewMatrix() {
		parent.camera(camera().position().x, camera().position().y, camera().position().z,
				      camera().at().x, camera().at().y, camera().at().z,
				      camera().upVector().x, camera().upVector().y, camera().upVector().z);
		modelviewMatrix = pg3d.modelview;
		//Processing computes the camera matrices for us (it actually computes the
		//modelview matrix from our Camera parameters). We just set them back
		//(instead of calling camera().computeModelViewMatrix() which is also possible
		//but expensive)
		camera().setModelViewMatrix(modelviewMatrix);
	}
	
	// 11. Utility
	
	/**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the
	 * positive {@code z} axis.
	 * <p>
	 * Code adapted from http://www.processingblogs.org/category/processing-java/
	 */	
	public static void cylinder(float w,float h) {
		  float px,py;

		  parent.beginShape(QUAD_STRIP);
		  for(float i=0; i<13; i++) {
			  px=PApplet.cos(PApplet.radians(i*30))*w;
			  py=PApplet.sin(PApplet.radians(i*30))*w;
			  parent.vertex(px,py,0);
			  parent.vertex(px,py,h);
		  }
		  parent.endShape();

		  parent.beginShape(TRIANGLE_FAN);
		  parent.vertex(0,0,0);
		  for(float i=12; i>-1; i--) {
			  px=PApplet.cos(PApplet.radians(i*30))*w;
			  py=PApplet.sin(PApplet.radians(i*30))*w;
			  parent.vertex(px,py,0);
		  }
		  parent.endShape();

		  parent.beginShape(TRIANGLE_FAN);
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
	 * Draws a cone along the positive {@code z} axis, with its base centered at
	 * {@code (x,y)}, height {@code h}, and radius {@code r}.
	 * <p>
	 * The code of this function was adapted from http://processinghacks.com/hacks:cone
	 * Thanks to Tom Carden.
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	public static void cone(int detail, float x, float y, float r, float h) {
		float unitConeX[] = new float[detail+1];
		float unitConeY[] = new float[detail+1];
		
		for (int i = 0; i <= detail; i++) {
			float a1 = TWO_PI * i / detail;
		    unitConeX[i] = r * (float)Math.cos(a1);
		    unitConeY[i] = r * (float)Math.sin(a1);
		}
		
		parent.pushMatrix();
		parent.translate(x,y);		
		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(0,0,h);
		for (int i = 0; i <= detail; i++) {
			parent.vertex(unitConeX[i],unitConeY[i],0.0f);
		}
		parent.endShape();
		parent.popMatrix();
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
	public static void cone(float r1, float r2,float h) {
		cone(18, 0, 0, r1, r2, h);
	}
	
	/**
	 * Draws a truncated cone along the positive {@code z} axis, with its base centered at
	 * {@code (x,y)}, height {@code h}, and radii {@code r1} and {@code r2} (basis and
	 * height respectively).
	 * 
	 * @see #cone(int, float, float, float, float)
	 */
	public static void cone(int detail, float x, float y, float r1, float r2, float h) {
		float firstCircleX[] = new float[detail+1];
		float firstCircleY[] = new float[detail+1];		
		float secondCircleX[] = new float[detail+1];
		float secondCircleY[] = new float[detail+1];
		
		for (int i = 0; i <= detail; i++) {
			float a1 = TWO_PI * i / detail;
		    firstCircleX[i] = r1 * (float)Math.cos(a1);
		    firstCircleY[i] = r1 * (float)Math.sin(a1);
		    secondCircleX[i] = r2 * (float)Math.cos(a1);
		    secondCircleY[i] = r2 * (float)Math.sin(a1);
		}
		
		parent.pushMatrix();
		parent.translate(x,y);
		parent.beginShape(QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			parent.vertex(firstCircleX[i], firstCircleY[i], 0);
			parent.vertex(secondCircleX[i], secondCircleY[i], h);			
		}
		parent.endShape();
		parent.popMatrix();
	}
}
