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
	// P R O C E S S I N G   A P P L E T   A N D   O B J E C T S	
	protected Frame tmpFrame;			

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
		super(p, renderer, x, y);
		space = Space.THREE_D;
		p5renderer = P5Renderer.P3D;
		
		cam = new Camera(this);
		setCamera(camera());//calls showAll();
		
		initDefaultCameraProfiles();
		
		tmpFrame = new Frame();		
		disableFrustumEquationsUpdate();
		
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
	
	// 5. Drawing methods	
	
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

			if ( ((mask & 1) != 0) && path.size() > 1 ) {			
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
	
	@Override
	public void beginScreenDrawing() {
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
	  	+ "endScreenDrawing() and they cannot be nested. Check your implementation!");
		startCoordCalls++;
		renderer().hint(DISABLE_DEPTH_TEST);
		renderer().pushProjection();
		float cameraZ = (height/2.0f) / PApplet.tan(camera().fieldOfView() /2.0f);
		float cameraNear = cameraZ / 2.0f;
		float cameraFar = cameraZ * 2.0f;
		renderer().ortho(-width/2, width/2, -height/2, height/2, cameraNear, cameraFar);
		renderer().pushMatrix();
		renderer().camera();
	}
	
	@Override
	public void endScreenDrawing() {
		startCoordCalls--;
		if (startCoordCalls != 0)
			throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
		  + "endScreenDrawing() and they cannot be nested. Check your implementation!");
		renderer().popProjection();
		renderer().popMatrix();
		renderer().hint(ENABLE_DEPTH_TEST);
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

	// 9. Mouse customization	

	// 12. Processing objects

	/**
	 * Sets the processing camera projection matrix from {@link #camera()}. Calls
	 * {@code PApplet.perspective()} or {@code PApplet.orhto()} depending on the
	 * {@link remixlab.proscene.Camera#type()}.
	 */
	protected void setPProjectionMatrix() {
		// option 1 (only one of the following two lines)
		// pg3d.projection.set(camera().getProjectionMatrix());
		//camera().computeProjectionMatrix();		
		///**
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
		///**
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