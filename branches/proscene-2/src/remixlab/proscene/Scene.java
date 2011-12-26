/**
 *                     ProScene (version 1.2.0)      
 *    Copyright (c) 2010-2011 by National University of Colombia
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

/**
import remixlab.remixcam.core.*;
import remixlab.remixcam.devices.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.util.*;
// */

// /*
import remixlab.remixcam.core.AbstractScene;
import remixlab.remixcam.core.Camera;
import remixlab.remixcam.core.InteractiveDrivableFrame;
import remixlab.remixcam.core.InteractiveFrame;
import remixlab.remixcam.core.SimpleFrame;
import remixlab.remixcam.core.KeyFrameInterpolator;
import remixlab.remixcam.devices.DeviceGrabbable;
import remixlab.remixcam.devices.Bindings;
import remixlab.remixcam.util.AbstractTimerJob;
import remixlab.remixcam.util.SingleThreadedTaskableTimer;
import remixlab.remixcam.util.SingleThreadedTimer;
import remixlab.remixcam.geom.Matrix3D;
import remixlab.remixcam.geom.Vector3D;
import remixlab.remixcam.geom.Point;
// */

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
public class Scene extends AbstractScene implements PConstants {
	// proscene version
	public static final String version = "1.2.0";
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

	// K E Y F R A M E S
	protected Bindings<Integer, Integer> pathKeys;
	protected Modifier addKeyFrameKeyboardModifier;
	protected Modifier deleteKeyFrameKeyboardModifier;

	// S h o r t c u t k e y s
	protected Bindings<KeyboardShortcut, KeyboardAction> gProfile;

	// c a m e r a p r o f i l e s
	private HashMap<String, CameraProfile> cameraProfileMap;
	private ArrayList<String> cameraProfileNames;
	private CameraProfile currentCameraProfile;		

	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S
	public PApplet parent;
	public PGraphics3D pg3d;	
	protected boolean offscreen;
	public Point upperLeftCorner;
	protected SimpleFrame tmpFrame;

	// O B J E C T S
	protected DesktopEvents dE;

	// S C R E E N C O O R D I N A T E S	
	protected float zC;

	// E X C E P T I O N H A N D L I N G
	protected int startCoordCalls;
  protected int beginOffScreenDrawingCalls;	
	
	/**
	// M O U S E   G R A B B E R   H I N T   C O L O R S
	private int onSelectionHintColor;
	private int offSelectionHintColor;
	private int cameraPathOnSelectionHintColor;
	private int cameraPathOffSelectionHintColor;
	*/

	// R E G I S T E R   D R A W   A N D   A N I M A T I O N   M E T H O D S
	// Draw
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

	/**
	 * Constructor that defines an on-screen Scene (the one that most likely
	 * would just fulfill all of your needs). All viewer parameters (display flags,
	 * scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation. This is actually just a convenience
	 * function that simply calls {@code this(p, (PGraphics3D) p.g)}. Call any
	 * other constructor by yourself to possibly define an off-screen Scene.
	 * 
	 * @see #Scene(PApplet, PGraphics3D)
	 * @see #Scene(PApplet, PGraphics3D, int, int)
	 */	
	public Scene(PApplet p) {
		this(p, (PGraphics3D) p.g);
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
	 * @see #Scene(PApplet, PGraphics3D, int, int)
	 */
	public Scene(PApplet p, PGraphics3D renderer) {
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
	 * @see #Scene(PApplet, PGraphics3D)
	 */
	public Scene(PApplet p, PGraphics3D renderer, int x, int y) {
		parent = p;
		pg3d = renderer;
		width = pg3d.width;
		height = pg3d.height;
		
		//TODO testing
		prosceneTimers = true;
		setLeftHanded();
		
		/**
		// TODO decide if this should go
		//mouse grabber selection hint colors		
		setMouseGrabberOnSelectionHintColor(pg3d.color(0, 0, 255));
		setMouseGrabberOffSelectionHintColor(pg3d.color(255, 0, 0));
		setMouseGrabberCameraPathOnSelectionHintColor(pg3d.color(255, 255, 0));
		setMouseGrabberCameraPathOffSelectionHintColor(pg3d.color(0, 255, 255));
		*/
		
		tmpFrame = new SimpleFrame();
		
		//event handler
		dE = new DesktopEvents(this);
		
		// 1 ->   	

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
		animationTimer = new SingleThreadedTimer(this);
		setAnimationPeriod(40, false); // 25Hz
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

		// called only once
		init();
	}
	
	// 0. Matrix stack wrappers: solving resolution naming possible  conflicts
	
	@Override
	public void pushMatrix() {
		pg3d.pushMatrix();
	}
	
	@Override
	public void popMatrix() {
		pg3d.popMatrix();
	}	 
	
	@Override
  public void translate(float tx, float ty) {    
  	pg3d.translate(tx, ty);
  }

	@Override
  public void translate(float tx, float ty, float tz) {    
  	pg3d.translate(tx, ty, tz);
  }

	@Override
  public void rotate(float angle) {    
  	pg3d.rotate(angle);
  }

	@Override
  public void rotateX(float angle) {    
  	pg3d.rotateX(angle);
  }

	@Override
  public void rotateY(float angle) {
  	pg3d.rotateY(angle);
  }

	@Override
  public void rotateZ(float angle) {
  	pg3d.rotateZ(angle);
  }

	@Override
  public void rotate(float angle, float vx, float vy, float vz) {
  	pg3d.rotate(angle, vx, vy, vz);
  }

	@Override
  public void scale(float s) {
  	pg3d.scale(s);
  }

	@Override
  public void scale(float sx, float sy) {
  	pg3d.scale(sx, sy);
  }

	@Override
  public void scale(float x, float y, float z) {
  	pg3d.scale(x, y, z);
  }

	@Override
  public void shearX(float angle) {
  	pg3d.shearX(angle);
  }

	@Override
  public void shearY(float angle) {
  	pg3d.shearY(angle);
  }
  
	@Override
  public void loadIdentity() {
  	pg3d.resetMatrix();
  }

	@Override
  public void resetMatrix() {
  	pg3d.resetMatrix();
  }
  
	@Override
  public void loadMatrix(Matrix3D source) {
  	PMatrix3D pM = new PMatrix3D();  	
  	pM.set( source.getTransposed(new float[16]) );
  	pg3d.setMatrix(pM);
  	
  }
  
	@Override
  public void multiplyMatrix(Matrix3D source) {
  	this.applyMatrix(source);
  }
  
	@Override
  public void applyMatrix(Matrix3D source) {
  	PMatrix3D pM = new PMatrix3D();  	
  	pM.set( source.getTransposed(new float[16]) );
  	pg3d.applyMatrix(pM);
  }

	@Override
  public void applyMatrix(float n00, float n01, float n02, float n03,
                          float n10, float n11, float n12, float n13,
                          float n20, float n21, float n22, float n23,
                          float n30, float n31, float n32, float n33) {    
  	pg3d.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
  }
  
	@Override
  public void frustum(float left, float right, float bottom, float top, float znear, float zfar) {
  	pg3d.frustum(left, right, bottom, top, znear, zfar);
  }

	@Override
  public Matrix3D getMatrix() {		
		PMatrix3D pM = (PMatrix3D) pg3d.getMatrix();
		return new Matrix3D(pM.get(new float[16]), true);// set it transposed
  }

	@Override
  public Matrix3D getMatrix(Matrix3D target) {		
		PMatrix3D pM = (PMatrix3D) pg3d.getMatrix();
		target.setTransposed(pM.get(new float[16]));
		return target;
  }

	@Override
  public void setMatrix(Matrix3D source) {
		resetMatrix();
    applyMatrix(source);
  }

	@Override
  public void printMatrix() {
  	pg3d.printMatrix();
  }
  
	@Override
  public void matrixMode( int mode  ) {
  	pg3d.matrixMode(mode);
  }
	
	// end matrix stack wrapper	
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#applyTransformation(SimpleFrame)}.
	 */
	@Override
	public void applyTransformation(SimpleFrame frame) {
		pg3d.translate( frame.translation().vec[0], frame.translation().vec[1], frame.translation().vec[2] );
		pg3d.rotate( frame.rotation().angle(), frame.rotation().axis().vec[0], frame.rotation().axis().vec[1], frame.rotation().axis().vec[2]);
	}

	// 2. Associated objects
	
	// TODO only AWTTimers, of course
	@Override
	public void registerInTimerPool(AbstractTimerJob job) {
		if (prosceneTimers) {			
			job.setTimer(new SingleThreadedTaskableTimer(this, job));
			timerPool.add(job);
		}
		else {
			job.setTimer(new TimerWrap(this, job));
		}
	}	
	
	/**
	 * Replaces the current {@link #camera()} with {@code camera}
	 */
	@Override
	public void setCamera(Camera camera) {
		if (camera == null)
			return;

		camera.setSceneRadius(radius());		
		camera.setSceneCenter(center());

		camera.setScreenWidthAndHeight(pg3d.width, pg3d.height);

		cam = camera;		

		showAll();
	}	

	// 4. State of the viewer

	/**
	 * Returns {@code true} if this Scene is associated to an offscreen 
	 * renderer and {@code false} otherwise.
	 * 
	 * @see #Scene(PApplet, PGraphics3D)
	 */
	
	public boolean isOffscreen() {
		return offscreen;
	}	
	
	/**
	 * Sets the interactivity to the Scene {@link #interactiveFrame()} instance
	 * according to {@code draw}
	 */
	@Override
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
			Vector3D v = camera().projectedCoordinatesOf(pupVec);
			pg3d.pushStyle();			
			pg3d.stroke(255);
			pg3d.strokeWeight(3);
			drawCross(v.vec[0], v.vec[1]);
			pg3d.popStyle();
		}
	}	

	/**
	 * Paint method which is called just before your {@code PApplet.draw()}
	 * method. This method is registered at the PApplet and hence you don't need
	 * to call it.
	 * <p>
	 * Sets the processing camera parameters from {@link #camera()} and updates
	 * the frustum planes equations if {@link #enableFrustumEquationsUpdate(boolean)}
	 * has been set to {@code true}.
	 */
	// TODO: try to rewrite and test how resizable works now
	public void pre() {
		if (isOffscreen()) return;	
		
		// handle possible resize events
		// weird: we need to bypass the handling of a resize event when running the
		// applet from eclipse		
		if ((parent.frame != null) && (parent.frame.isResizable())) {
			if ((width != pg3d.width) || (height != pg3d.height)) {
				width = pg3d.width;
				height = pg3d.height;				
				camera().setScreenWidthAndHeight(width, height);				
			} else {
				if ((currentCameraProfile().mode() == CameraProfile.Mode.THIRD_PERSON)
						&& (!camera().anyInterpolationIsStarted())) {
					camera().setPosition(avatar().cameraPosition());
					camera().setUpVector(avatar().upVector());
					camera().lookAt(avatar().target());
				}
			}
		} else {
			if ((currentCameraProfile().mode() == CameraProfile.Mode.THIRD_PERSON)
					&& (!camera().anyInterpolationIsStarted())) {
				camera().setPosition(avatar().cameraPosition());
				camera().setUpVector(avatar().upVector());
				camera().lookAt(avatar().target());
			}
		}

		preDraw();
	}

	/**
	 * Paint method which is called just after your {@code PApplet.draw()} method.
	 * This method is registered at the PApplet and hence you don't need to call
	 * it. Calls {@link #drawCommon()}.
	 * 
	 * @see #drawCommon()
	 */
	public void draw() {
		if (isOffscreen()) return;
		postDraw();
	}	
	
	@Override
	protected void invokeRegisteredMethod() {
     	// 3. Draw external registered method
			if (drawHandlerObject != null) {
				try {
					drawHandlerMethod.invoke(drawHandlerObject, new Object[] { this });
				} catch (Exception e) {
					PApplet.println("Something went wrong when invoking your "	+ drawHandlerMethodName + " method");
					e.printStackTrace();
				}
			}	
	}
	
	/**
	 * Returns the renderer context linked to this scene. 
	 * 
	 * @return PGraphics3D renderer.
	 */
	public PGraphics3D renderer() {
		return pg3d;
	}

	/**
	 * This method should be called when using offscreen rendering 
	 * right after renderer.beginDraw().
   */	
	public void beginDraw() {
		if (isOffscreen()) {
			if (beginOffScreenDrawingCalls != 0)
				throw new RuntimeException(
						"There should be exactly one beginDraw() call followed by a "
								+ "endDraw() and they cannot be nested. Check your implementation!");			
			beginOffScreenDrawingCalls++;
						
			if ((currentCameraProfile().mode() == CameraProfile.Mode.THIRD_PERSON)
					&& (!camera().anyInterpolationIsStarted())) {
				camera().setPosition(avatar().cameraPosition());
				camera().setUpVector(avatar().upVector());
				camera().lookAt(avatar().target());
			}
			
			preDraw();	
		}
	}

	/**
	 * This method should be called when using offscreen rendering 
	 * right before renderer.endDraw(). Calls {@link #drawCommon()}.
	 * 
	 * @see #drawCommon() 
   */		
	public void endDraw() {
		beginOffScreenDrawingCalls--;
		
		if (beginOffScreenDrawingCalls != 0)
			throw new RuntimeException(
					"There should be exactly one beginDraw() call followed by a "
							+ "endDraw() and they cannot be nested. Check your implementation!");
		
		postDraw();
	}
	
  // 4. Scene dimensions
	
	/**
	@Override
	public float frameRate() {
		return parent.frameRate;
	}
	*/

	/**
	@Override
	public long frameCount() {
		return parent.frameCount;
	}
	*/

	/**
	 * Returns the {@link PApplet#width} to {@link PApplet#height} aspect ratio of
	 * the processing display window.
	 */
	public float aspectRatio() {
		return (float) pg3d.width / (float) pg3d.height;
	}

	// 6. Display of visual hints and Display methods
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#cylinder(float, float)}.
	 * <p>
	 * Code adapted from http://www.processingblogs.org/category/processing-java/ 
	 */
	@Override
	public void cylinder(float w, float h) {		
		float px, py;

		pg3d.beginShape(QUAD_STRIP);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			pg3d.vertex(px, py, 0);
			pg3d.vertex(px, py, h);
		}
		pg3d.endShape();

		pg3d.beginShape(TRIANGLE_FAN);
		pg3d.vertex(0, 0, 0);
		for (float i = 12; i > -1; i--) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			pg3d.vertex(px, py, 0);
		}
		pg3d.endShape();

		pg3d.beginShape(TRIANGLE_FAN);
		pg3d.vertex(0, 0, h);
		for (float i = 0; i < 13; i++) {
			px = (float) Math.cos(PApplet.radians(i * 30)) * w;
			py = (float) Math.sin(PApplet.radians(i * 30)) * w;
			pg3d.vertex(px, py, h);
		}
		pg3d.endShape();
	}					
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#cone(int, float, float, float, float)}.
	 * <p>
	 * The code of this function was adapted from
	 * http://processinghacks.com/hacks:cone Thanks to Tom Carden.
	 * 
	 * @see #cone(int, float, float, float, float, float)
	 */
	@Override
	public void cone(int detail, float x, float y, float r, float h) {
		float unitConeX[] = new float[detail + 1];
		float unitConeY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			unitConeX[i] = r * (float) Math.cos(a1);
			unitConeY[i] = r * (float) Math.sin(a1);
		}

		pg3d.pushMatrix();
		pg3d.translate(x, y);
		pg3d.beginShape(TRIANGLE_FAN);
		pg3d.vertex(0, 0, h);
		for (int i = 0; i <= detail; i++) {
			pg3d.vertex(unitConeX[i], unitConeY[i], 0.0f);
		}
		pg3d.endShape();
		pg3d.popMatrix();
	}	

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#cone(int, float, float, float, float, float)}.
	 */
	@Override
	public void cone(int detail, float x, float y,	float r1, float r2, float h) {
		float firstCircleX[] = new float[detail + 1];
		float firstCircleY[] = new float[detail + 1];
		float secondCircleX[] = new float[detail + 1];
		float secondCircleY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = PApplet.TWO_PI * i / detail;
			firstCircleX[i] = r1 * (float) Math.cos(a1);
			firstCircleY[i] = r1 * (float) Math.sin(a1);
			secondCircleX[i] = r2 * (float) Math.cos(a1);
			secondCircleY[i] = r2 * (float) Math.sin(a1);
		}

		pg3d.pushMatrix();
		pg3d.translate(x, y);
		pg3d.beginShape(QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			pg3d.vertex(firstCircleX[i], firstCircleY[i], 0);
			pg3d.vertex(secondCircleX[i], secondCircleY[i], h);
		}
		pg3d.endShape();
		pg3d.popMatrix();
	}		
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawAxis(float)}.
	 */
	@Override
	public void drawAxis(float length) {		
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;

		// pg3d.noLights();

		pg3d.pushStyle();
		
		pg3d.beginShape(LINES);		
		pg3d.strokeWeight(2);
		// The X
		pg3d.stroke(200, 0, 0);
		pg3d.vertex(charShift, charWidth, -charHeight);
		pg3d.vertex(charShift, -charWidth, charHeight);
		pg3d.vertex(charShift, -charWidth, -charHeight);
		pg3d.vertex(charShift, charWidth, charHeight);
		// The Y
		pg3d.stroke(0, 200, 0);
		pg3d.vertex(charWidth, charShift, charHeight);
		pg3d.vertex(0.0f, charShift, 0.0f);
		pg3d.vertex(-charWidth, charShift, charHeight);
		pg3d.vertex(0.0f, charShift, 0.0f);
		pg3d.vertex(0.0f, charShift, 0.0f);
		pg3d.vertex(0.0f, charShift, -charHeight);
		// The Z
		pg3d.stroke(0, 100, 200);
		
		//left_handed
		pg3d.vertex(-charWidth, -charHeight, charShift);
		pg3d.vertex(charWidth, -charHeight, charShift);
		pg3d.vertex(charWidth, -charHeight, charShift);
		pg3d.vertex(-charWidth, charHeight, charShift);
		pg3d.vertex(-charWidth, charHeight, charShift);
		pg3d.vertex(charWidth, charHeight, charShift);
	  //right_handed coordinate system should go like this:
		//pg3d.vertex(-charWidth, charHeight, charShift);
		//pg3d.vertex(charWidth, charHeight, charShift);
		//pg3d.vertex(charWidth, charHeight, charShift);
		//pg3d.vertex(-charWidth, -charHeight, charShift);
		//pg3d.vertex(-charWidth, -charHeight, charShift);
		//pg3d.vertex(charWidth, -charHeight, charShift);
		
		pg3d.endShape();
		
	  /**
		// Z axis
		pg3d.noStroke();
		pg3d.fill(0, 100, 200);
		drawArrow(length, 0.01f * length);

		// X Axis
		pg3d.fill(200, 0, 0);
		pg3d.pushMatrix();
		pg3d.rotateY(HALF_PI);
		drawArrow(length, 0.01f * length);
		pg3d.popMatrix();

		// Y Axis
		pg3d.fill(0, 200, 0);
		pg3d.pushMatrix();
		pg3d.rotateX(-HALF_PI);
		drawArrow(length, 0.01f * length);
		pg3d.popMatrix();
		// */
		
	  // X Axis
		pg3d.stroke(200, 0, 0);
		pg3d.line(0, 0, 0, length, 0, 0);
	  // Y Axis
		pg3d.stroke(0, 200, 0);		
		pg3d.line(0, 0, 0, 0, length, 0);
		// Z Axis
		pg3d.stroke(0, 100, 200);
		pg3d.line(0, 0, 0, 0, 0, length);		

		pg3d.popStyle();	  
	}	

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawGrid(float, int)}.
	 */
	@Override
	public void drawGrid(float size, int nbSubdivisions) {
		/**
		pg3d.pushStyle();
		pg3d.stroke(170, 170, 170);
		pg3d.strokeWeight(1);
		pg3d.beginShape(LINES);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
			pg3d.vertex(pos, -size);
			pg3d.vertex(pos, +size);
			pg3d.vertex(-size, pos);
			pg3d.vertex(size, pos);
		}
		pg3d.endShape();
		pg3d.popStyle();
		// */		
		
		float posi, posj;
		pg3d.pushStyle();
		pg3d.stroke(170);
		pg3d.strokeWeight(2);
		pg3d.beginShape(POINTS);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			posi = size * (2.0f * i / nbSubdivisions - 1.0f);
			for(int j = 0; j <= nbSubdivisions; ++j) {
				posj = size * (2.0f * j / nbSubdivisions - 1.0f);
				pg3d.vertex(posi, posj);
			}			
		}
		pg3d.endShape();
		//pg3d.popStyle();
		
		int internalSub = 5;
		int subSubdivisions = nbSubdivisions * internalSub;
		//pg3d.pushStyle();
		pg3d.stroke(100);
		pg3d.strokeWeight(1);
		pg3d.beginShape(POINTS);
		for (int i = 0; i <= subSubdivisions; ++i) {
			posi = size * (2.0f * i / subSubdivisions - 1.0f);
			for(int j = 0; j <= subSubdivisions; ++j) {
				posj = size * (2.0f * j / subSubdivisions - 1.0f);
				if(( (i%internalSub) != 0 ) || ( (j%internalSub) != 0 ) )
					pg3d.vertex(posi, posj);
			}			
		}
		pg3d.endShape();
		pg3d.popStyle();
	}
	
	// 2. CAMERA

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawCamera(Camera, boolean, float)}
	 */	
	@Override
	public void drawCamera(Camera camera, boolean drawFarPlane, float scale) {		
		pg3d.pushMatrix();

		// pg3d.applyMatrix(camera.frame().worldMatrix());
		// same as the previous line, but maybe more efficient
		tmpFrame.fromMatrix(camera.frame().worldMatrix());
		applyTransformation(tmpFrame);

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
		pg3d.strokeWeight(2);
		switch (camera.type()) {
			case PERSPECTIVE:
				pg3d.beginShape(PApplet.LINES);
				pg3d.vertex(0.0f, 0.0f, 0.0f);
				pg3d.vertex(points[farIndex].x, points[farIndex].y, -points[farIndex].z);
				pg3d.vertex(0.0f, 0.0f, 0.0f);
				pg3d.vertex(-points[farIndex].x, points[farIndex].y, -points[farIndex].z);
				pg3d.vertex(0.0f, 0.0f, 0.0f);
				pg3d.vertex(-points[farIndex].x, -points[farIndex].y,	-points[farIndex].z);
				pg3d.vertex(0.0f, 0.0f, 0.0f);
				pg3d.vertex(points[farIndex].x, -points[farIndex].y, -points[farIndex].z);
				pg3d.endShape();
				break;
			case ORTHOGRAPHIC:
				if (drawFarPlane) {
					pg3d.beginShape(PApplet.LINES);
					pg3d.vertex(points[0].x, points[0].y, -points[0].z);
					pg3d.vertex(points[1].x, points[1].y, -points[1].z);
					pg3d.vertex(-points[0].x, points[0].y, -points[0].z);
					pg3d.vertex(-points[1].x, points[1].y, -points[1].z);
					pg3d.vertex(-points[0].x, -points[0].y, -points[0].z);
					pg3d.vertex(-points[1].x, -points[1].y, -points[1].z);
					pg3d.vertex(points[0].x, -points[0].y, -points[0].z);
					pg3d.vertex(points[1].x, -points[1].y, -points[1].z);
					pg3d.endShape();
				}
		}

		// Near and (optionally) far plane(s)
		pg3d.pushStyle();
		pg3d.noStroke();
		pg3d.beginShape(PApplet.QUADS);
		for (int i = farIndex; i >= 0; --i) {
			pg3d.normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
			pg3d.vertex(points[i].x, points[i].y, -points[i].z);
			pg3d.vertex(-points[i].x, points[i].y, -points[i].z);
			pg3d.vertex(-points[i].x, -points[i].y, -points[i].z);
			pg3d.vertex(points[i].x, -points[i].y, -points[i].z);
		}
		pg3d.endShape();

		// Up arrow
		float arrowHeight = 1.5f * points[0].y;
		float baseHeight = 1.2f * points[0].y;
		float arrowHalfWidth = 0.5f * points[0].x;
		float baseHalfWidth = 0.3f * points[0].x;

		// pg3d.noStroke();
		// Base
		pg3d.beginShape(PApplet.QUADS);
		
		pg3d.vertex(-baseHalfWidth, -points[0].y, -points[0].z);
		pg3d.vertex(baseHalfWidth, -points[0].y, -points[0].z);
		pg3d.vertex(baseHalfWidth, -baseHeight, -points[0].z);
		pg3d.vertex(-baseHalfWidth, -baseHeight, -points[0].z);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(-baseHalfWidth, points[0].y, -points[0].z);
		//pg3d.vertex(baseHalfWidth, points[0].y, -points[0].z);
		//pg3d.vertex(baseHalfWidth, baseHeight, -points[0].z);
		//pg3d.vertex(-baseHalfWidth, baseHeight, -points[0].z);
		
		pg3d.endShape();

		// Arrow
		pg3d.beginShape(PApplet.TRIANGLES);
		
		pg3d.vertex(0.0f, -arrowHeight, -points[0].z);
		pg3d.vertex(-arrowHalfWidth, -baseHeight, -points[0].z);
		pg3d.vertex(arrowHalfWidth, -baseHeight, -points[0].z);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(0.0f, arrowHeight, -points[0].z);
		//pg3d.vertex(-arrowHalfWidth, baseHeight, -points[0].z);
		//pg3d.vertex(arrowHalfWidth, baseHeight, -points[0].z);
		
		pg3d.endShape();	
		pg3d.popStyle();
		pg3d.popMatrix();
	}

	// 3. KEYFRAMEINTERPOLATOR CAMERA

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawKFICamera(int, float)}.
	 */
	@Override
	public void drawKFICamera(float scale) {
		float halfHeight = scale * 0.07f;
		float halfWidth = halfHeight * 1.3f;
		float dist = halfHeight / (float) Math.tan(PApplet.PI / 8.0f);

		float arrowHeight = 1.5f * halfHeight;
		float baseHeight = 1.2f * halfHeight;
		float arrowHalfWidth = 0.5f * halfWidth;
		float baseHalfWidth = 0.3f * halfWidth;

		// Frustum outline
		pg3d.pushStyle();

		pg3d.noFill();		
		pg3d.beginShape();
		pg3d.vertex(-halfWidth, halfHeight, -dist);
		pg3d.vertex(-halfWidth, -halfHeight, -dist);
		pg3d.vertex(0.0f, 0.0f, 0.0f);
		pg3d.vertex(halfWidth, -halfHeight, -dist);
		pg3d.vertex(-halfWidth, -halfHeight, -dist);
		pg3d.endShape();
		pg3d.noFill();
		pg3d.beginShape();
		pg3d.vertex(halfWidth, -halfHeight, -dist);
		pg3d.vertex(halfWidth, halfHeight, -dist);
		pg3d.vertex(0.0f, 0.0f, 0.0f);
		pg3d.vertex(-halfWidth, halfHeight, -dist);
		pg3d.vertex(halfWidth, halfHeight, -dist);
		pg3d.endShape();

		// Up arrow
		pg3d.noStroke();
		// Base
		pg3d.beginShape(PApplet.QUADS);
		
		pg3d.vertex(baseHalfWidth, -halfHeight, -dist);
		pg3d.vertex(-baseHalfWidth, -halfHeight, -dist);
		pg3d.vertex(-baseHalfWidth, -baseHeight, -dist);
		pg3d.vertex(baseHalfWidth, -baseHeight, -dist);
  	//right_handed coordinate system should go like this:
		//pg3d.vertex(-baseHalfWidth, halfHeight, -dist);
		//pg3d.vertex(baseHalfWidth, halfHeight, -dist);
		//pg3d.vertex(baseHalfWidth, baseHeight, -dist);
		//pg3d.vertex(-baseHalfWidth, baseHeight, -dist);
		
		pg3d.endShape();
		// Arrow
		pg3d.beginShape(PApplet.TRIANGLES);
		
		pg3d.vertex(0.0f, -arrowHeight, -dist);
		pg3d.vertex(arrowHalfWidth, -baseHeight, -dist);
		pg3d.vertex(-arrowHalfWidth, -baseHeight, -dist);
	  //right_handed coordinate system should go like this:
		//pg3d.vertex(0.0f, arrowHeight, -dist);
		//pg3d.vertex(-arrowHalfWidth, baseHeight, -dist);
		//pg3d.vertex(arrowHalfWidth, baseHeight, -dist);
		
		pg3d.endShape();

		pg3d.popStyle();
	}
	
	@Override
	protected void drawZoomWindowHint() {
		float p1x = (float) dE.fCorner.getX();
		float p1y = (float) dE.fCorner.getY();
		float p2x = (float) dE.lCorner.getX();
		float p2y = (float) dE.lCorner.getY();
		beginScreenDrawing();
		Vector3D p1 = coords(new Point(p1x, p1y));
		Vector3D p2 = coords(new Point(p2x, p2y));
		Vector3D p3 = coords(new Point(p2x, p1y));
		Vector3D p4 = coords(new Point(p1x, p2y));
		pg3d.pushStyle();
		pg3d.stroke(255, 255, 255);
		pg3d.strokeWeight(2);
		pg3d.noFill();
		pg3d.beginShape();
		pg3d.vertex(p1.vec[0], p1.vec[1], p1.vec[2]);
		pg3d.vertex(p3.vec[0], p3.vec[1], p3.vec[2]);//p3
		pg3d.vertex(p2.vec[0], p2.vec[1], p2.vec[2]);
		pg3d.vertex(p4.vec[0], p4.vec[1], p4.vec[2]);//p4
		pg3d.endShape(CLOSE);
		pg3d.popStyle();
		endScreenDrawing();
	}

	@Override
	protected void drawScreenRotateLineHint() {
		float p1x = (float) dE.fCorner.getX();
		float p1y = (float) dE.fCorner.getY();
		Vector3D p2 = camera().projectedCoordinatesOf(arcballReferencePoint());
		beginScreenDrawing();
		Vector3D p1s = coords(new Point(p1x, p1y));
		Vector3D p2s = coords(new Point(p2.vec[0], p2.vec[1]));
		pg3d.pushStyle();
		pg3d.stroke(255, 255, 255);
		pg3d.strokeWeight(2);
		pg3d.noFill();
		pg3d.beginShape(LINE);
		pg3d.vertex(p1s.vec[0], p1s.vec[1], p1s.vec[2]);
		pg3d.vertex(p2s.vec[0], p2s.vec[1], p2s.vec[2]);
		pg3d.endShape();
		pg3d.popStyle();
		endScreenDrawing();
	}
	
	/**
	 * Sets the mouse grabber on selection hint {@code color}
	 * (drawn as a shooter target).
	 * 
	 * @see #drawSelectionHints()
	 */
  //public void setMouseGrabberOnSelectionHintColor(int color) { 	onSelectionHintColor = color; }
	
  /**
	 * Sets the mouse grabber off selection hint {@code color}
	 * (drawn as a shooter target).
	 * 
	 * @see #drawSelectionHints()
	 */  
	//public void setMouseGrabberOffSelectionHintColor(int color) { offSelectionHintColor = color;	}
	
	/**
	 * Returns the mouse grabber on selection hint {@code color}.
	 * 
	 * @see #drawSelectionHints()
	 */
	//public int mouseGrabberOnSelectionHintColor() {	return onSelectionHintColor;}
	
	/**
	 * Returns the mouse grabber off selection hint {@code color}.
	 * 
	 * @see #drawSelectionHints()
	 */
  //public int mouseGrabberOffSelectionHintColor() {return offSelectionHintColor;}
  
  /**
	 * Sets the mouse grabber on selection hint {@code color} for camera paths
	 * (drawn as a shooter target).
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
  // public void setMouseGrabberCameraPathOnSelectionHintColor(int color) {	cameraPathOnSelectionHintColor = color; }
	
  /**
	 * Sets the mouse grabber off selection hint {@code color} for camera paths
	 * (drawn as a shooter target).
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
	//public void setMouseGrabberCameraPathOffSelectionHintColor(int color) {	cameraPathOffSelectionHintColor = color;	}
	
	/**
	 * Returns the mouse grabber on selection hint {@code color} for camera paths.
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
	//public int mouseGrabberCameraPathOnSelectionHintColor() {	return cameraPathOnSelectionHintColor;	}
	
	/**
	 * Returns the mouse grabber off selection hint {@code color} for camera paths.
	 * 
	 * @see #drawCameraPathSelectionHints()
	 */
  //public int mouseGrabberCameraPathOffSelectionHintColor() {	return cameraPathOffSelectionHintColor;	}
	
	@Override
	protected void drawSelectionHints() {
		for (DeviceGrabbable mg : msGrabberPool) {
			if(mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				if (!iF.isInCameraPath()) {
					Vector3D center = camera().projectedCoordinatesOf(iF.position());
					if (mg.grabsMouse()) {						
						pg3d.pushStyle();
					  //pg3d.stroke(mouseGrabberOnSelectionHintColor());
						pg3d.stroke(pg3d.color(0, 255, 0));
						pg3d.strokeWeight(2);
						drawShooterTarget(center, (iF.grabsMouseThreshold() + 1));
						pg3d.popStyle();					
					}
					else {						
						pg3d.pushStyle();
					  //pg3d.stroke(mouseGrabberOffSelectionHintColor());
						pg3d.stroke(pg3d.color(240, 240, 240));
						pg3d.strokeWeight(1);
						drawShooterTarget(center, iF.grabsMouseThreshold());
						pg3d.popStyle();
					}
				}
			}
		}
	}

	@Override
	protected void drawCameraPathSelectionHints() {
		for (DeviceGrabbable mg : msGrabberPool) {
			if(mg instanceof InteractiveFrame) {
				InteractiveFrame iF = (InteractiveFrame) mg;// downcast needed
				if (iF.isInCameraPath()) {
					Vector3D center = camera().projectedCoordinatesOf(iF.position());
					if (mg.grabsMouse()) {
						pg3d.pushStyle();						
					  //pg3d.stroke(mouseGrabberCameraPathOnSelectionHintColor());
						pg3d.stroke(pg3d.color(0, 255, 255));
						pg3d.strokeWeight(2);
						drawShooterTarget(center, (iF.grabsMouseThreshold() + 1));
						pg3d.popStyle();
					}
					else {
						pg3d.pushStyle();
					  //pg3d.stroke(mouseGrabberCameraPathOffSelectionHintColor());
						pg3d.stroke(pg3d.color(255, 255, 0));
						pg3d.strokeWeight(1);
						drawShooterTarget(center, iF.grabsMouseThreshold());
						pg3d.popStyle();
					}
				}
			}
		}
	}
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawArcballReferencePointHint()}.
	 */
	@Override
	public void drawArcballReferencePointHint() {
		Vector3D p = camera().projectedCoordinatesOf(arcballReferencePoint());
		pg3d.pushStyle();
		pg3d.stroke(255);
		pg3d.strokeWeight(3);
		drawCross(p.vec[0], p.vec[1]);
		pg3d.popStyle();
	}	

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawCross(int, float, float, float, int)}.
	 */
	@Override
	public void drawCross(float px, float py, float size) {
		beginScreenDrawing();
		Vector3D p1 = coords(new Point(px - size, py));
		Vector3D p2 = coords(new Point(px + size, py));
		Vector3D p3 = coords(new Point(px, py - size));
		Vector3D p4 = coords(new Point(px, py + size));
		pg3d.pushStyle();
		pg3d.noFill();
		pg3d.beginShape(LINES);
		pg3d.vertex(p1.vec[0], p1.vec[1], p1.vec[2]);
		pg3d.vertex(p2.vec[0], p2.vec[1], p2.vec[2]);
		pg3d.vertex(p3.vec[0], p3.vec[1], p3.vec[2]);
		pg3d.vertex(p4.vec[0], p4.vec[1], p4.vec[2]);
		pg3d.endShape();
		pg3d.popStyle();
		endScreenDrawing();
	}	

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawFilledCircle(int, int, Vector3D, float)}.
	 */	
	@Override
	public void drawFilledCircle(int subdivisions, Vector3D center, float radius) {
		float precision = PApplet.TWO_PI/subdivisions;
		float x = center.vec[0];
		float y = center.vec[1];
		float angle, x2, y2;
		beginScreenDrawing();
		pg3d.pushStyle();
		pg3d.noStroke();
		//pg3d.fill(color);
		pg3d.beginShape(TRIANGLE_FAN);
		Vector3D c = coords(new Point(x, y));
		pg3d.vertex(c.vec[0], c.vec[1], c.vec[2]);
		Vector3D aux = new Vector3D();
		for (angle = 0.0f; angle <= PApplet.TWO_PI + 1.1*precision; angle += precision) {			
			x2 = x + (float) Math.sin(angle) * radius;
			y2 = y + (float) Math.cos(angle) * radius;
			aux.set(coords(new Point(x2, y2)));
			pg3d.vertex(aux.vec[0], aux.vec[1], aux.vec[2]);
		}
		pg3d.endShape();
		pg3d.popStyle();
		endScreenDrawing();
	}

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawFilledSquare(Vector3D, float)}.	 
	 */
	@Override
	public void drawFilledSquare(Vector3D center, float edge) {
		float x = center.vec[0];
		float y = center.vec[1];
		beginScreenDrawing();
		Vector3D p1 = coords(new Point(x - edge, y + edge));
		Vector3D p2 = coords(new Point(x + edge, y + edge));
		Vector3D p3 = coords(new Point(x + edge, y - edge));
		Vector3D p4 = coords(new Point(x - edge, y - edge));
		pg3d.pushStyle();
		pg3d.noStroke();
		//pg3d.fill(color);
		pg3d.beginShape(QUADS);
		pg3d.vertex(p1.vec[0], p1.vec[1], p1.vec[2]);
		pg3d.vertex(p2.vec[0], p2.vec[1], p2.vec[2]);
		pg3d.vertex(p3.vec[0], p3.vec[1], p3.vec[2]);
		pg3d.vertex(p4.vec[0], p4.vec[1], p4.vec[2]);
		pg3d.endShape();
		pg3d.popStyle();
		endScreenDrawing();
	}

	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawShooterTarget(Vector3D, float, int)}.
	 */
	@Override
	public void drawShooterTarget(Vector3D center, float length) {
		float x = center.vec[0];
		float y = center.vec[1];
		beginScreenDrawing();
		Vector3D p1 = coords(new Point((x - length), (y - length) + (0.6f * length)));
		Vector3D p2 = coords(new Point((x - length), (y - length)));
		Vector3D p3 = coords(new Point((x - length) + (0.6f * length), (y - length)));
		Vector3D p4 = coords(new Point(((x + length) - (0.6f * length)), (y - length)));
		Vector3D p5 = coords(new Point((x + length), (y - length)));
		Vector3D p6 = coords(new Point((x + length), ((y - length) + (0.6f * length))));
		Vector3D p7 = coords(new Point((x + length), ((y + length) - (0.6f * length))));
		Vector3D p8 = coords(new Point((x + length), (y + length)));
		Vector3D p9 = coords(new Point(((x + length) - (0.6f * length)), (y + length)));
		Vector3D p10 = coords(new Point(((x - length) + (0.6f * length)), (y + length)));
		Vector3D p11 = coords(new Point((x - length), (y + length)));
		Vector3D p12 = coords(new Point((x - length), ((y + length) - (0.6f * length))));
		
		pg3d.pushStyle();
		
		pg3d.noFill();

		pg3d.beginShape();
		pg3d.vertex(p1.vec[0], p1.vec[1], p1.vec[2]);
		pg3d.vertex(p2.vec[0], p2.vec[1], p2.vec[2]);
		pg3d.vertex(p3.vec[0], p3.vec[1], p3.vec[2]);
		pg3d.endShape();

		pg3d.beginShape();
		pg3d.vertex(p4.vec[0], p4.vec[1], p4.vec[2]);
		pg3d.vertex(p5.vec[0], p5.vec[1], p5.vec[2]);
		pg3d.vertex(p6.vec[0], p6.vec[1], p6.vec[2]);
		pg3d.endShape();

		pg3d.beginShape();
		pg3d.vertex(p7.vec[0], p7.vec[1], p7.vec[2]);
		pg3d.vertex(p8.vec[0], p8.vec[1], p8.vec[2]);
		pg3d.vertex(p9.vec[0], p9.vec[1], p9.vec[2]);
		pg3d.endShape();

		pg3d.beginShape();
		pg3d.vertex(p10.vec[0], p10.vec[1], p10.vec[2]);
		pg3d.vertex(p11.vec[0], p11.vec[1], p11.vec[2]);
		pg3d.vertex(p12.vec[0], p12.vec[1], p12.vec[2]);
		pg3d.endShape();

		pg3d.popStyle();
		endScreenDrawing();

		drawCross(center.vec[0], center.vec[1], 0.6f * length);
	}
	
	/**
	 * Overriding of {@link remixlab.remixcam.core.AbstractScene#drawPath(KeyFrameInterpolator, int, int, float)}.
	 */
	@Override
	public void drawPath(List<SimpleFrame> path, int mask, int nbFrames, int nbSteps, float scale) {
		if (mask != 0) {
			renderer().pushStyle();
			renderer().strokeWeight(2);

			if ((mask & 1) != 0) {
				renderer().noFill();
				renderer().stroke(170);
				renderer().beginShape();
				for (SimpleFrame myFr : path)
					renderer().vertex(myFr.position().vec[0], myFr.position().vec[1], myFr.position().vec[2]);
				renderer().endShape();
			}
			if ((mask & 6) != 0) {
				int count = 0;
				if (nbFrames > nbSteps)
					nbFrames = nbSteps;
				float goal = 0.0f;

				for (SimpleFrame myFr : path)
					if ((count++) >= goal) {
						goal += nbSteps / (float) nbFrames;
						renderer().pushMatrix();
						
						applyTransformation(myFr);

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
	 * first call {@code Vector3D p = coords(new Point(x, y))} then do your
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
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
							                 + "endScreenDrawing() and they cannot be nested. Check your implementation!");
		
		startCoordCalls++;
		
		if ( pg3d.getClass() == processing.core.PGraphics3D.class ) {
		//if ( pg3d instanceof processing.core.PGraphics3D ) {
			pg3d.hint(DISABLE_DEPTH_TEST);
			pg3d.matrixMode(PApplet.PROJECTION);
			pg3d.pushMatrix();
			pg3d.ortho(-width/2, width/2, -height/2, height/2, -10, 10);
			pg3d.matrixMode(PApplet.MODELVIEW);
			pg3d.pushMatrix();
		  // Camera needs to be reset!
			pg3d.camera();
			zC = 0.0f;
		}
		else {
			zC = 0.1f;
		}
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

		if ( pg3d.getClass() == processing.core.PGraphics3D.class ) {
			pg3d.matrixMode(PApplet.PROJECTION);
			pg3d.popMatrix();
			pg3d.matrixMode(PApplet.MODELVIEW);  
			pg3d.popMatrix();		  
			pg3d.hint(ENABLE_DEPTH_TEST);
		}
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
	public Vector3D coords(Point p) {
		if (startCoordCalls != 1)
			throw new RuntimeException("beginScreenDrawing() should be called before this method!");
		if ( pg3d.getClass() == processing.core.PGraphics3D.class )
			return new Vector3D(p.x, p.y, zC);
		else
			return camera().unprojectedCoordinatesOf(new Vector3D(p.x,p.y,zC));
	}	

	// 7. Camera profiles

	/**
	 * Internal method that defines the default camera profiles: WHEELED_ARCBALL
	 * and FIRST_PERSON.
	 */
	private void initDefaultCameraProfiles() {
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
	 * Registers a camera profile. Returns true if succeeded. If there's a
	 * registered camera profile with the same name, registration will fail. 
	 * <p>
	 * <b>Attention:</b> This method doesn't make current {@code cp}. For that call
	 * {@link #setCurrentCameraProfile(CameraProfile)}.
	 * 
	 * @param cp camera profile
	 * 
	 * @see #setCurrentCameraProfile(CameraProfile)
	 * @see #unregisterCameraProfile(CameraProfile) 
	 */
	public boolean registerCameraProfile(CameraProfile cp) {
		// if(!isCameraProfileRegistered(cp)) {
		if (cp == null)
			return false;
		if (!isCameraProfileRegistered(cp)) {
			cameraProfileNames.add(cp.name());
			cameraProfileMap.put(cp.name(), cp);
			return true;
		}
		return false;
	}

	/**
	 * Convenience function that simply returns {@code unregisterCameraProfile(cp.name())}.
	 */
	public boolean unregisterCameraProfile(CameraProfile cp) {
		return unregisterCameraProfile(cp.name());
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
	public boolean unregisterCameraProfile(String cp) {
		if (!isCameraProfileRegistered(cp))
			return false;

		CameraProfile cProfile = cameraProfile(cp);
		int instancesDifferentThanThirdPerson = 0;

		for (CameraProfile camProfile : cameraProfileMap.values())
			if (camProfile.mode() != CameraProfile.Mode.THIRD_PERSON)
				instancesDifferentThanThirdPerson++;

		if ((cProfile.mode() != CameraProfile.Mode.THIRD_PERSON)
				&& (instancesDifferentThanThirdPerson == 1))
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
	 * Returns the camera profile which name matches the one provided.
	 * Returns null if there's no camera profile registered by this name.
	 * 
	 * @param name camera profile name
	 * @return camera profile object
	 */
	public CameraProfile cameraProfile(String name) {
		return cameraProfileMap.get(name);
	}
	
	/**
	 * Returns an array of the camera profile objects that are currently
	 * registered at the Scene.
	 */
	public CameraProfile [] getCameraProfiles() {		
		return cameraProfileMap.values().toArray(new CameraProfile[0]);
	}

	/**
	 * Returns true the given camera profile is currently registered.
	 */
	public boolean isCameraProfileRegistered(CameraProfile cp) {
		return cameraProfileMap.containsValue(cp);
	}

	/**
	 * Returns true if currently there's a camera profile registered by
	 * the given name.
	 */
	public boolean isCameraProfileRegistered(String name) {
		return cameraProfileMap.containsKey(name);
	}

	/**
	 * Returns the current camera profile object. Never null.
	 */
	public CameraProfile currentCameraProfile() {
		return currentCameraProfile;
	}

	/**
	 * Returns true if the {@link #currentCameraProfile()} matches 
	 * the one by the given name.
	 */
	boolean isCurrentCameraProfile(String cp) {
		return isCurrentCameraProfile(cameraProfileMap.get(cp));
	}

	/**
	 * Returns true if the {@link #currentCameraProfile()} matches 
	 * the one given.
	 */
	boolean isCurrentCameraProfile(CameraProfile cp) {
		return currentCameraProfile() == cp;
	}

	/**
	 * Set current the given camera profile. Returns true if succeeded.
	 * <p>
	 * Registers first the given camera profile if it is not registered.
	 */
	public boolean setCurrentCameraProfile(CameraProfile cp) {
		if (cp == null) {
			return false;
		}
		if (!isCameraProfileRegistered(cp))
			if (!registerCameraProfile(cp))
				return false;

		return setCurrentCameraProfile(cp.name());
	}
	
	/**
	 * Set current the camera profile associated to the given name.
	 * Returns true if succeeded.
	 * <p>
	 * This method triggers smooth transition animations
	 * when switching between camera profile modes.
	 */
	public boolean setCurrentCameraProfile(String cp) {
		CameraProfile camProfile = cameraProfileMap.get(cp);
		if (camProfile == null)
			return false;
		if ((camProfile.mode() == CameraProfile.Mode.THIRD_PERSON) && (avatar() == null))
			return false;
		else {
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
				Camera cm = camera().get();
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

	/**
	 * Sets the next registered camera profile as current.
	 * <p>
	 * Camera profiles are ordered by their registration order.
	 */
	@Override
	public void nextCameraProfile() {
		int currentCameraProfileIndex = cameraProfileNames.indexOf(currentCameraProfile().name());
		nextCameraProfile(++currentCameraProfileIndex);
	}

	/**
	 * Internal use. Used by {@link #nextCameraProfile()}.
	 */
	private void nextCameraProfile(int index) {
		if (!cameraProfileNames.isEmpty()) {
			if (index == cameraProfileNames.size())
				index = 0;

			if (!setCurrentCameraProfile(cameraProfileNames.get(index)))
				nextCameraProfile(++index);
			// debug:
			else
				PApplet.println("Camera profile changed to: "
						+ cameraProfileNames.get(index));
		}
	}

	// 8. Keyboard customization

	/**
	 * Parses the sketch to find if any KeyXxxx method has been implemented. If
	 * this is the case, print a warning message telling the user what to do to
	 * avoid possible conflicts with proscene.
	 * <p>
	 * The methods sought are: {@code keyPressed}, {@code keyReleased}, and
	 * {@code keyTyped}.
	 */
	protected void parseKeyXxxxMethods() {
		boolean foundKP = true;
		boolean foundKR = true;
		boolean foundKT = true;

		try {
			parent.getClass().getDeclaredMethod("keyPressed");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKP = false;
		} catch (NoSuchMethodException e) {
			foundKP = false;
		}

		try {
			parent.getClass().getDeclaredMethod("keyReleased");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKR = false;
		} catch (NoSuchMethodException e) {
			foundKR = false;
		}

		try {
			parent.getClass().getDeclaredMethod("keyTyped");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundKT = false;
		} catch (NoSuchMethodException e) {
			foundKT = false;
		}

		if ( (foundKP || foundKR || foundKT) && keyboardIsHandled() ) {
			// if( (foundKP || foundKR || foundKT) &&
			// (!parent.getClass().getName().equals("remixlab.proscene.Viewer")) ) {
			PApplet.println("Warning: it seems that you have implemented some KeyXxxxMethod in your sketch. You may temporarily disable proscene " +
					"keyboard handling with Scene.disableKeyboardHandling() (you can re-enable it later with Scene.enableKeyboardHandling()).");
		}
	}	

	/**
	 * Enables Proscene keyboard handling.
	 * 
	 * @see #keyboardIsHandled()
	 * @see #enableMouseHandling()
	 * @see #disableKeyboardHandling()
	 */
	@Override
	public void enableKeyboardHandling() {
		super.enableKeyboardHandling();
		parent.registerKeyEvent(dE);
	}

	/**
	 * Disables Proscene keyboard handling.
	 * 
	 * @see #keyboardIsHandled()
	 */
	@Override
	public void disableKeyboardHandling() {
		super.disableKeyboardHandling();
		parent.unregisterKeyEvent(dE);
	}

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
	 * Associates key-frame interpolator path to key. High-level version
	 * of {@link #setPathKey(Integer, Integer)}.
	 *  
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * @param path key-frame interpolator path
	 * 
	 * @see #setPathKey(Integer, Integer)
	 */
	public void setPathKey(Character key, Integer path) {
		setPathKey(ClickBinding.getVKey(key), path);
	}
	
	/**
	 * Associates key-frame interpolator path to the given virtual key. Low-level version
	 * of {@link #setPathKey(Character, Integer)}.
	 * 
	 * @param vKey shortcut
	 * @param path key-frame interpolator path
	 * 
	 * @see #setPathKey(Character, Integer)
	 */
	public void setPathKey(Integer vKey, Integer path) {
		if ( isPathKeyInUse(vKey) ) {
			Integer p = path(vKey);
			PApplet.println("Warning: overwritting path key which was previously binded to path " + p);
		}
		pathKeys.setBinding(vKey, path);
	}

	/**
	 * Returns the key-frame interpolator path associated with key. High-level version
	 * of {@link #path(Integer)}.
	 * 
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * 
	 * @see #path(Integer)
	 */
	public Integer path(Character key) {
		return path(ClickBinding.getVKey(key));
	}
	
	/**
	 * Returns the key-frame interpolator path associated with key. Low-level version
	 * of {@link #path(Character)}.
	 * 
	 * @param vKey shortcut
	 * 
	 * @see #path(Character)
	 */
	public Integer path(Integer vKey) {
		return pathKeys.binding(vKey);
	}

	/**
	 * Removes the key-frame interpolator shortcut. High-level version
	 * of {@link #removePathKey(Integer)}.
	 * 
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * 
	 * @see #removePathKey(Integer)
	 */
	public void removePathKey(Character key) {
		removePathKey(ClickBinding.getVKey(key));
	}
	
	/**
	 * Removes the key-frame interpolator shortcut. Low-level version
	 * of {@link #removePathKey(Character)}.
	 * 
	 * @param vKey shortcut
	 * 
	 * @see #removePathKey(Character)
	 */
	public void removePathKey(Integer vKey) {
		pathKeys.removeBinding(vKey);
	}
	
	/**
	 * Returns true if the given key binds a key-frame interpolator path. High-level version
	 * of {@link #isPathKeyInUse(Integer)}.
	 * 
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * 
	 * @see #isPathKeyInUse(Integer)
	 */
	public boolean isPathKeyInUse(Character key) {
		return isPathKeyInUse(ClickBinding.getVKey(key));
	}
	
	/**
	 * Returns true if the given virtual key binds a key-frame interpolator path. Low-level version
	 * of {@link #isPathKeyInUse(Character)}.
	 * 
	 * @param vKey shortcut
	 * 
	 * @see #isPathKeyInUse(Character)
	 */
	public boolean isPathKeyInUse(Integer vKey) {
		return pathKeys.isShortcutInUse(vKey);
	}

	/**
	 * Sets the modifier key needed to play the key-frame interpolator paths.
	 * 
	 * @param modifier
	 */
	public void setAddKeyFrameKeyboardModifier(Modifier modifier) {
		addKeyFrameKeyboardModifier = modifier;
	}

	/**
	 * Sets the modifier key needed to delete the key-frame interpolator paths.
	 * 
	 * @param modifier
	 */
	public void setDeleteKeyFrameKeyboardModifier(Modifier modifier) {
		deleteKeyFrameKeyboardModifier = modifier;
	}

  /**
   * Defines a global keyboard shortcut to bind the given action.
   * 
   * @param key shortcut
   * @param action keyboard action
   */
	public void setShortcut(Character key, KeyboardAction action) {
		if ( isKeyInUse(key) ) {
			KeyboardAction a = shortcut(key);
			PApplet.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		gProfile.setBinding(new KeyboardShortcut(key), action);
	}
	
  /**
   * Defines a global keyboard shortcut to bind the given action. High-level version
   * of {@link #setShortcut(Integer, Integer, KeyboardAction)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param key character (internally converted to a coded key) defining the shortcut
   * @param action keyboard action
   * 
   * @see #setShortcut(Integer, Integer, KeyboardAction)
   */
	public void setShortcut(Integer mask, Character key, KeyboardAction action) {
		setShortcut(mask, ClickBinding.getVKey(key), action);
	}
	
  /**
   * Defines a global keyboard shortcut to bind the given action. High-level version
   * of {@link #setShortcut(Integer, Character, KeyboardAction)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param vKey coded key defining the shortcut
   * @param action keyboard action
   * 
   * @see #setShortcut(Integer, Character, KeyboardAction)
   */
	public void setShortcut(Integer mask, Integer vKey, KeyboardAction action) {
		if ( isKeyInUse(mask, vKey) ) {
			KeyboardAction a = shortcut(mask, vKey);
			PApplet.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		gProfile.setBinding(new KeyboardShortcut(mask, vKey), action);
	}

	/**
	 * Defines a global keyboard shortcut to bind the given action.
	 * 
	 * @param vKey coded key defining the shortcut
	 * @param action keyboard action
	 */
	public void setShortcut(Integer vKey, KeyboardAction action) {
		if ( isKeyInUse(vKey) ) {
			KeyboardAction a = shortcut(vKey);
			PApplet.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		gProfile.setBinding(new KeyboardShortcut(vKey), action);
	}

	/**
	 * Removes all global keyboard shortcuts.
	 */
	public void removeAllShortcuts() {
		gProfile.removeAllBindings();
	}
	
	/**
	 * Removes the global keyboard shortcut.
	 * 
	 * @param key shortcut
	 */
	public void removeShortcut(Character key) {
		gProfile.removeBinding(new KeyboardShortcut(key));
	}
	
  /**
   * Removes the global keyboard shortcut. High-level version
   * of {@link #removeShortcut(Integer, Integer)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param key character (internally converted to a coded key) defining the shortcut
   * 
   * @see #removeShortcut(Integer, Integer)
   */
	public void removeShortcut(Integer mask, Character key) {
		removeShortcut(mask, ClickBinding.getVKey(key));
	}

	/**
   * Removes the global keyboard shortcut. Low-level version
   * of {@link #removeShortcut(Integer, Character)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param vKey virtual coded-key defining the shortcut
   * 
   * @see #removeShortcut(Integer, Character)
   */
	public void removeShortcut(Integer mask, Integer vKey) {
		gProfile.removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Removes the global keyboard shortcut.
	 * 
	 * @param vKey virtual coded-key defining the shortcut
	 */
	public void removeShortcut(Integer vKey) {
		gProfile.removeBinding(new KeyboardShortcut(vKey));
	}
	
	/**
	 * Returns the action that is binded to the given global keyboard shortcut.
	 * 
	 * @param key shortcut
	 */
	public KeyboardAction shortcut(Character key) {
		return gProfile.binding(new KeyboardShortcut(key));
	}
	
  /**
   * Returns the action that is binded to the given global keyboard shortcut.
   * High-level version of {@link #shortcut(Integer, Integer)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param key character (internally converted to a coded key) defining the shortcut
   * 
   * @see #shortcut(Integer, Integer)
   */
	public KeyboardAction shortcut(Integer mask, Character key) {
		return shortcut(mask, ClickBinding.getVKey(key));
	}

	/**
   * Returns the action that is binded to the given global keyboard shortcut.
   * Low-level version of {@link #shortcut(Integer, Character)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param vKey virtual coded-key defining the shortcut
   * 
   * @see #shortcut(Integer, Character)
   */
	public KeyboardAction shortcut(Integer mask, Integer vKey) {
		return gProfile.binding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the action that is binded to the given global keyboard shortcut.
	 * 
	 * @param vKey virtual coded-key defining the shortcut
	 */
	public KeyboardAction shortcut(Integer vKey) {
		return gProfile.binding(new KeyboardShortcut(vKey));
	}

	/**
	 * Returns true if the given global keyboard shortcut binds an action.
	 * 
	 * @param key shortcut
	 */
	public boolean isKeyInUse(Character key) {
		return gProfile.isShortcutInUse(new KeyboardShortcut(key));
	}
	
  /**
   * Returns true if the given global keyboard shortcut binds an action.
   * High-level version of {@link #isKeyInUse(Integer, Integer)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param key character (internally converted to a coded key) defining the shortcut
   * 
   * @see #isKeyInUse(Integer, Integer)
   */
	public boolean isKeyInUse(Integer mask, Character key) {
		return isKeyInUse(mask, ClickBinding.getVKey(key));
	}
	
	/**
   * Returns true if the given global keyboard shortcut binds an action.
   * Low-level version of {@link #isKeyInUse(Integer, Character)}.
   * 
   * @param mask modifier mask defining the shortcut
   * @param vKey virtual coded-key defining the shortcut
   * 
   * @see #isKeyInUse(Integer, Character)
   */
	public boolean isKeyInUse(Integer mask, Integer vKey) {
		return gProfile.isShortcutInUse(new KeyboardShortcut(mask, vKey));
	}
	
	/**
	 * Returns true if the given global keyboard shortcut binds an action.
	 * 
	 * @param vKey virtual coded-key defining the shortcut
	 */
	public boolean isKeyInUse(Integer vKey) {
		return gProfile.isShortcutInUse(new KeyboardShortcut(vKey));
	}

	/**
	 * Returns true if there is a global keyboard shortcut for the given action.
	 */
	public boolean isActionBinded(KeyboardAction action) {
		return gProfile.isActionMapped(action);
	}
		
	/**
	 * Displays global keyboard bindings.
	 * 
	 * @param onConsole if this flag is true displays the help on console.
	 * Otherwise displays it on the applet
	 * 
	 * @see #displayGlobalHelp()
	 */
	@Override
	public void displayGlobalHelp(boolean onConsole) {
		if (onConsole)
			PApplet.println(globalHelp());
		else { //on applet
			pg3d.textFont(parent.createFont("Arial", 12));
			pg3d.textMode(SCREEN);
			pg3d.fill(0,255,0);
			pg3d.textLeading(20);
			pg3d.text(globalHelp(), 10, 10, (pg3d.width-20), (pg3d.height-20));
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
		for (Entry<KeyboardShortcut, Scene.KeyboardAction> entry : gProfile.map().entrySet()) {			
			Character space = ' ';
			if (!entry.getKey().description().equals(space.toString())) 
				description += entry.getKey().description() + " -> " + entry.getValue().description() + "\n";
			else
				description += "space_bar" + " -> " + entry.getValue().description() + "\n";
		}
		
		for (Entry<Integer, Integer> entry : pathKeys.map().entrySet())
			description += ClickBinding.getKeyText(entry.getKey()) + " -> plays camera path " + entry.getValue().toString() + "\n";
		description += ClickBinding.getModifiersExText(addKeyFrameKeyboardModifier.ID) + " + one of the above keys -> adds keyframe to the camera path \n";
		description += ClickBinding.getModifiersExText(deleteKeyFrameKeyboardModifier.ID) + " + one of the above keys -> deletes the camera path \n";
		
		return description;		
	}	
	
	/**
	 * Displays the {@link #currentCameraProfile()} bindings.
	 * 
	 * @param onConsole if this flag is true displays the help on console.
	 * Otherwise displays it on the applet
	 * 
	 * @see #displayCurrentCameraProfileHelp()
	 */
	@Override
	public void displayCurrentCameraProfileHelp(boolean onConsole) {
		if (onConsole)
			PApplet.println(currentCameraProfileHelp());
		else { //on applet
			pg3d.textFont(parent.createFont("Arial", 12));
			pg3d.textMode(SCREEN);
			pg3d.fill(0,255,0);
			pg3d.textLeading(20);
			pg3d.text(currentCameraProfileHelp(), 10, 10, (pg3d.width-20), (pg3d.height-20));			
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
	 * Parses the sketch to find if any mouseXxxx method has been implemented. If
	 * this is the case, print a warning message telling the user what to do to
	 * avoid possible conflicts with proscene.
	 * <p>
	 * The methods sought are: {@code mouseDragged}, {@code mouseMoved}, {@code
	 * mouseReleased}, {@code mousePressed}, and {@code mouseClicked}.
	 */
	protected void parseMouseXxxxMethods() {
		boolean foundMD = true;
		boolean foundMM = true;
		boolean foundMR = true;
		boolean foundMP = true;
		boolean foundMC = true;

		try {
			parent.getClass().getDeclaredMethod("mouseDragged");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMD = false;
		} catch (NoSuchMethodException e) {
			foundMD = false;
		}

		try {
			parent.getClass().getDeclaredMethod("mouseMoved");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMM = false;
		} catch (NoSuchMethodException e) {
			foundMM = false;
		}

		try {
			parent.getClass().getDeclaredMethod("mouseReleased");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMR = false;
		} catch (NoSuchMethodException e) {
			foundMR = false;
		}

		try {
			parent.getClass().getDeclaredMethod("mousePressed");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMP = false;
		} catch (NoSuchMethodException e) {
			foundMP = false;
		}

		try {
			parent.getClass().getDeclaredMethod("mouseClicked");
		} catch (SecurityException e) {
			e.printStackTrace();
			foundMC = false;
		} catch (NoSuchMethodException e) {
			foundMC = false;
		}

		if ( (foundMD || foundMM || foundMR || foundMP || foundMC) && mouseIsHandled() ) {			
			PApplet.println("Warning: it seems that you have implemented some mouseXxxxMethod in your sketch. You may temporarily disable proscene " +
			"mouse handling with Scene.disableMouseHandling() (you can re-enable it later with Scene.enableMouseHandling()).");
		}
	}	

	/**
	 * Enables Proscene mouse handling.
	 * 
	 * @see #mouseIsHandled()
	 * @see #disableMouseHandling()
	 * @see #enableKeyboardHandling()
	 */
	@Override
	public void enableMouseHandling() {
		super.enableMouseHandling();
		parent.registerMouseEvent(dE);
	}

	/**
	 * Disables Proscene mouse handling.
	 * 
	 * @see #mouseIsHandled()
	 */
	@Override
	public void disableMouseHandling() {
		super.disableMouseHandling();
		parent.unregisterMouseEvent(dE);
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
	@Override
	protected void performAnimation() {
		if( !animationTimer.isTrigggered() ) {
			animatedFrameWasTriggered = false;
			return;
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

	// 12. Processing objects

	/**
	 * Sets the processing camera projection matrix from {@link #camera()}. Calls
	 * {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link remixlab.remixcam.core.Camera#type()}.
	 */
	@Override
	protected void setProjectionMatrix() {
	  // All options work seemlessly
		 /**		
		// Option 1
		Matrix3D mat = new Matrix3D();		
		camera().getProjectionMatrix(mat);
		mat.transpose();
		float[] target = new float[16];
		pg3d.projection.set(mat.get(target));
		// */
		
		// Option 2
		//pg3d.projection.set(camera().getProjectionMatrix(new float[16], true));// set it transposed
		
		// /**
		// Option 3
		// compute the processing camera projection matrix from our camera() parameters
		switch (camera().type()) {
		case PERSPECTIVE:
			pg3d.perspective(camera().fieldOfView(), camera().aspectRatio(), camera().zNear(), camera().zFar());
			break;
		case ORTHOGRAPHIC:
			float[] wh = camera().getOrthoWidthHeight();
			pg3d.ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());
			break;
		}
		// if our camera() matrices are detached from the processing Camera
		// matrices, we cache the processing camera projection matrix into our camera()
		camera().setProjectionMatrix( pg3d.projection.get(new float[16]), true ); // set it transposed				 
		// */
	}

	/**
	 * Sets the processing camera matrix from {@link #camera()}. Simply calls
	 * {@code PApplet.camera()}.
	 */
	@Override
	protected void setModelViewMatrix() {
	  // All options work seemlessly
		 /**		
		// Option 1
		Matrix3D mat = new Matrix3D();		
		camera().getModelViewMatrix(mat);
		mat.transpose();// experimental
		float[] target = new float[16];
		pg3d.modelview.set(mat.get(target));
		// */
		
	  // Option 2
		//pg3d.modelview.set(camera().getModelViewMatrix(new float[16], true));// set it transposed
		
		// /**
	  // Option 3
		// compute the processing camera modelview matrix from our camera() parameters
		pg3d.camera(camera().position().x(), camera().position().y(), camera().position().z(),
				        camera().at().x(), camera().at().y(), camera().at().z(),
				        camera().upVector().x(), camera().upVector().y(), camera().upVector().z());
		// if our camera() matrices are detached from the processing Camera
		// matrices, we cache the processing camera modelview matrix into our camera()
		camera().setModelViewMatrix( pg3d.modelview.get(new float[16]), true );// set it transposed
		// */
	}
}