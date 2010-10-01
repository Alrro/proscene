/**
 *                     ProScene (version 1.0.0-beta1)      
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
import remixlab.proscene.InteractiveFrame.CoordinateSystemConvention;

/**
 * Utility class that implements some drawing methods used among proscene
 * classes.
 */
public class DrawingUtils implements PConstants {
	// needed for drawCamera
	static protected Frame tmpFrame = new Frame();

	// 1. SCENE

	/**
	 * Draws a cylinder of width {@code w} and height {@code h}, along the {@code
	 * parent} positive {@code z} axis.
	 * <p>
	 * Code adapted from http://www.processingblogs.org/category/processing-java/
	 */
	public static void cylinder(PApplet parent, float w, float h) {
		float px, py;

		parent.beginShape(QUAD_STRIP);
		for (float i = 0; i < 13; i++) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			parent.vertex(px, py, 0);
			parent.vertex(px, py, h);
		}
		parent.endShape();

		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(0, 0, 0);
		for (float i = 12; i > -1; i--) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			parent.vertex(px, py, 0);
		}
		parent.endShape();

		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(0, 0, h);
		for (float i = 0; i < 13; i++) {
			px = PApplet.cos(PApplet.radians(i * 30)) * w;
			py = PApplet.sin(PApplet.radians(i * 30)) * w;
			parent.vertex(px, py, h);
		}
		parent.endShape();
	}

	/**
	 * Same as {@code cone(parent, det, 0, 0, r, h);}
	 * 
	 * @see #cone(PApplet, int, float, float, float, float)
	 */
	public static void cone(PApplet parent, int det, float r, float h) {
		cone(parent, det, 0, 0, r, h);
	}

	/**
	 * Same as {@code cone(parent, 12, 0, 0, r, h);}
	 * 
	 * @see #cone(PApplet, int, float, float, float, float)
	 */
	public static void cone(PApplet parent, float r, float h) {
		cone(parent, 12, 0, 0, r, h);
	}

	/**
	 * Draws a cone along the {@code parent} positive {@code z} axis, with its
	 * base centered at {@code (x,y)}, height {@code h}, and radius {@code r}.
	 * <p>
	 * The code of this function was adapted from
	 * http://processinghacks.com/hacks:cone Thanks to Tom Carden.
	 * 
	 * @see #cone(PApplet, int, float, float, float, float, float)
	 */
	public static void cone(PApplet parent, int detail, float x, float y,
			float r, float h) {
		float unitConeX[] = new float[detail + 1];
		float unitConeY[] = new float[detail + 1];

		for (int i = 0; i <= detail; i++) {
			float a1 = TWO_PI * i / detail;
			unitConeX[i] = r * (float) Math.cos(a1);
			unitConeY[i] = r * (float) Math.sin(a1);
		}

		parent.pushMatrix();
		parent.translate(x, y);
		parent.beginShape(TRIANGLE_FAN);
		parent.vertex(0, 0, h);
		for (int i = 0; i <= detail; i++) {
			parent.vertex(unitConeX[i], unitConeY[i], 0.0f);
		}
		parent.endShape();
		parent.popMatrix();
	}

	/**
	 * Same as {@code cone(parent, det, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(PApplet, int, float, float, float, float, float)
	 */
	public static void cone(PApplet parent, int det, float r1, float r2, float h) {
		cone(parent, det, 0, 0, r1, r2, h);
	}

	/**
	 * Same as {@code cone(parent, 18, 0, 0, r1, r2, h);}
	 * 
	 * @see #cone(PApplet, int, float, float, float, float, float)
	 */
	public static void cone(PApplet parent, float r1, float r2, float h) {
		cone(parent, 18, 0, 0, r1, r2, h);
	}

	/**
	 * Draws a truncated cone along the {@code parent} positive {@code z} axis,
	 * with its base centered at {@code (x,y)}, height {@code h}, and radii
	 * {@code r1} and {@code r2} (basis and height respectively).
	 * 
	 * @see #cone(PApplet, int, float, float, float, float)
	 */
	public static void cone(PApplet parent, int detail, float x, float y,
			float r1, float r2, float h) {
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

		parent.pushMatrix();
		parent.translate(x, y);
		parent.beginShape(QUAD_STRIP);
		for (int i = 0; i <= detail; i++) {
			parent.vertex(firstCircleX[i], firstCircleY[i], 0);
			parent.vertex(secondCircleX[i], secondCircleY[i], h);
		}
		parent.endShape();
		parent.popMatrix();
	}

	/**
	 * Convenience function that simply calls {@code drawAxis(parent, 100)}
	 */
	public static void drawAxis(PApplet parent) {
		drawAxis(parent, 100);
	}

	/**
	 * Draws an axis of length {@code length} which origin correspond to the
	 * {@code parent}'s world coordinate system origin.
	 * 
	 * @see #drawGrid(PApplet, float, int)
	 */
	public static void drawAxis(PApplet parent, float length) {
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;

		// parent.noLights();

		parent.beginShape(LINES);
		parent.pushStyle();
		parent.strokeWeight(2);
		// The X
		parent.stroke(255, 178, 178);
		parent.vertex(charShift, charWidth, -charHeight);
		parent.vertex(charShift, -charWidth, charHeight);
		parent.vertex(charShift, -charWidth, -charHeight);
		parent.vertex(charShift, charWidth, charHeight);
		// The Y
		parent.stroke(178, 255, 178);
		parent.vertex(charWidth, charShift, charHeight);
		parent.vertex(0.0f, charShift, 0.0f);
		parent.vertex(-charWidth, charShift, charHeight);
		parent.vertex(0.0f, charShift, 0.0f);
		parent.vertex(0.0f, charShift, 0.0f);
		parent.vertex(0.0f, charShift, -charHeight);
		// The Z
		parent.stroke(178, 178, 255);
		if (InteractiveFrame.coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED) {
			parent.vertex(-charWidth, -charHeight, charShift);
			parent.vertex(charWidth, -charHeight, charShift);
			parent.vertex(charWidth, -charHeight, charShift);
			parent.vertex(-charWidth, charHeight, charShift);
			parent.vertex(-charWidth, charHeight, charShift);
			parent.vertex(charWidth, charHeight, charShift);
		} else {
			parent.vertex(-charWidth, charHeight, charShift);
			parent.vertex(charWidth, charHeight, charShift);
			parent.vertex(charWidth, charHeight, charShift);
			parent.vertex(-charWidth, -charHeight, charShift);
			parent.vertex(-charWidth, -charHeight, charShift);
			parent.vertex(charWidth, -charHeight, charShift);
		}
		parent.endShape();

		// Z axis
		parent.noStroke();
		parent.fill(178, 178, 255);
		drawArrow(parent, length, 0.01f * length);

		// X Axis
		parent.fill(255, 178, 178);
		parent.pushMatrix();
		parent.rotateY(HALF_PI);
		drawArrow(parent, length, 0.01f * length);
		parent.popMatrix();

		// Y Axis
		parent.fill(178, 255, 178);
		parent.pushMatrix();
		parent.rotateX(-HALF_PI);
		drawArrow(parent, length, 0.01f * length);
		parent.popMatrix();

		parent.popStyle();
	}

	/**
	 * Simply calls {@code drawArrow(parent, length, 0.05f * length)}
	 * 
	 * @see #drawArrow(PApplet, float, float)
	 */
	public static void drawArrow(PApplet parent, float length) {
		float radius = 0.05f * length;
		drawArrow(parent, length, radius);
	}

	/**
	 * Draws a 3D arrow along the {@code parent} positive Z axis.
	 * <p>
	 * {@code length} and {@code radius} define its geometry.
	 * <p>
	 * Use {@link #drawArrow(PApplet, PVector, PVector, float)} to place the arrow
	 * in 3D.
	 */
	public static void drawArrow(PApplet parent, float length, float radius) {
		float head = 2.5f * (radius / length) + 0.1f;
		float coneRadiusCoef = 4.0f - 5.0f * head;

		DrawingUtils.cylinder(parent, radius, length
				* (1.0f - head / coneRadiusCoef));
		parent.translate(0.0f, 0.0f, length * (1.0f - head));
		DrawingUtils.cone(parent, coneRadiusCoef * radius, head * length);
		parent.translate(0.0f, 0.0f, -length * (1.0f - head));
	}

	/**
	 * Draws a 3D arrow between the 3D point {@code from} and the 3D point {@code
	 * to}, both defined in the current {@code parent} ModelView coordinates
	 * system.
	 * 
	 * @see #drawArrow(PApplet, float, float)
	 */
	public static void drawArrow(PApplet parent, PVector from, PVector to,
			float radius) {
		parent.pushMatrix();
		parent.translate(from.x, from.y, from.z);
		parent.applyMatrix(new Quaternion(new PVector(0, 0, 1), PVector.sub(to,
				from)).matrix());
		drawArrow(parent, PVector.sub(to, from).mag(), radius);
		parent.popMatrix();
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(parent, 100, 10)}
	 * 
	 * @see #drawGrid(PApplet, float, int)
	 */
	public static void drawGrid(PApplet parent) {
		drawGrid(parent, 100, 10);
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(parent, size, 10)}
	 * 
	 * @see #drawGrid(PApplet, float, int)
	 */
	public static void drawGrid(PApplet parent, float size) {
		drawGrid(parent, size, 10);
	}

	/**
	 * Convenience function that simply calls {@code drawGrid(parent, 100,
	 * nbSubdivisions)}
	 * 
	 * @see #drawGrid(PApplet, float, int)
	 */
	public static void drawGrid(PApplet parent, int nbSubdivisions) {
		drawGrid(parent, 100, nbSubdivisions);
	}

	/**
	 * Draws a grid in the XY plane, centered on (0,0,0) (defined in the current
	 * coordinate system).
	 * <p>
	 * {@code size} (processing scene units) and {@code nbSubdivisions} define its
	 * geometry.
	 * 
	 * @see #drawAxis(PApplet, float)
	 */
	public static void drawGrid(PApplet parent, float size, int nbSubdivisions) {
		parent.pushStyle();
		parent.stroke(170, 170, 170);
		parent.strokeWeight(1);
		parent.beginShape(LINES);
		for (int i = 0; i <= nbSubdivisions; ++i) {
			final float pos = size * (2.0f * i / nbSubdivisions - 1.0f);
			parent.vertex(pos, -size);
			parent.vertex(pos, +size);
			parent.vertex(-size, pos);
			parent.vertex(size, pos);
		}
		parent.endShape();
		parent.popStyle();
	}

	// 2. CAMERA

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * 170, true, 1.0f)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera) {
		drawCamera(parent, camera, 170, true, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * 170, true, scale)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera, float scale) {
		drawCamera(parent, camera, 170, true, scale);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * color, true, 1.0f)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera, int color) {
		drawCamera(parent, camera, color, true, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * 170, drawFarPlane, 1.0f)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera,
			boolean drawFarPlane) {
		drawCamera(parent, camera, 170, drawFarPlane, 1.0f);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * 170, drawFarPlane, scale)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera,
			boolean drawFarPlane, float scale) {
		drawCamera(parent, camera, 170, drawFarPlane, scale);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * color, true, scale)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera, int color,
			float scale) {
		drawCamera(parent, camera, color, true, scale);
	}

	/**
	 * Convenience function that simply calls {@code drawCamera(parent, camera,
	 * color, drawFarPlane, 1.0f)}
	 * 
	 * @see #drawCamera(PApplet, Camera, int, boolean, float)
	 */
	public static void drawCamera(PApplet parent, Camera camera, int color,
			boolean drawFarPlane) {
		drawCamera(parent, camera, color, drawFarPlane, 1.0f);
	}

	/**
	 * Draws a representation of the {@code camera} in the {@code parent} 3D
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
	public static void drawCamera(PApplet parent, Camera camera, int color,
			boolean drawFarPlane, float scale) {
		parent.pushMatrix();

		// parent.applyMatrix(camera.frame().worldMatrix());
		// same as the previous line, but maybe more efficient
		tmpFrame.fromMatrix(camera.frame().worldMatrix());
		tmpFrame.applyTransformation(parent);

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

		// Near and (optionally) far plane(s)
		parent.pushStyle();
		parent.noStroke();
		parent.fill(color);
		parent.beginShape(PApplet.QUADS);
		for (int i = farIndex; i >= 0; --i) {
			parent.normal(0.0f, 0.0f, (i == 0) ? 1.0f : -1.0f);
			parent.vertex(points[i].x, points[i].y, -points[i].z);
			parent.vertex(-points[i].x, points[i].y, -points[i].z);
			parent.vertex(-points[i].x, -points[i].y, -points[i].z);
			parent.vertex(points[i].x, -points[i].y, -points[i].z);
		}
		parent.endShape();

		// Up arrow
		float arrowHeight = 1.5f * points[0].y;
		float baseHeight = 1.2f * points[0].y;
		float arrowHalfWidth = 0.5f * points[0].x;
		float baseHalfWidth = 0.3f * points[0].x;

		// parent.noStroke();
		parent.fill(color);
		// Base
		parent.beginShape(PApplet.QUADS);
		if (InteractiveFrame.coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED) {
			parent.vertex(-baseHalfWidth, -points[0].y, -points[0].z);
			parent.vertex(baseHalfWidth, -points[0].y, -points[0].z);
			parent.vertex(baseHalfWidth, -baseHeight, -points[0].z);
			parent.vertex(-baseHalfWidth, -baseHeight, -points[0].z);
		} else {
			parent.vertex(-baseHalfWidth, points[0].y, -points[0].z);
			parent.vertex(baseHalfWidth, points[0].y, -points[0].z);
			parent.vertex(baseHalfWidth, baseHeight, -points[0].z);
			parent.vertex(-baseHalfWidth, baseHeight, -points[0].z);
		}
		parent.endShape();

		// Arrow
		parent.fill(color);
		parent.beginShape(PApplet.TRIANGLES);
		if (InteractiveFrame.coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED) {
			parent.vertex(0.0f, -arrowHeight, -points[0].z);
			parent.vertex(-arrowHalfWidth, -baseHeight, -points[0].z);
			parent.vertex(arrowHalfWidth, -baseHeight, -points[0].z);
		} else {
			parent.vertex(0.0f, arrowHeight, -points[0].z);
			parent.vertex(-arrowHalfWidth, baseHeight, -points[0].z);
			parent.vertex(arrowHalfWidth, baseHeight, -points[0].z);
		}
		parent.endShape();

		// Frustum lines
		parent.stroke(color);
		parent.strokeWeight(2);
		switch (camera.type()) {
		case PERSPECTIVE:
			parent.beginShape(PApplet.LINES);
			parent.vertex(0.0f, 0.0f, 0.0f);
			parent
					.vertex(points[farIndex].x, points[farIndex].y, -points[farIndex].z);
			parent.vertex(0.0f, 0.0f, 0.0f);
			parent.vertex(-points[farIndex].x, points[farIndex].y,
					-points[farIndex].z);
			parent.vertex(0.0f, 0.0f, 0.0f);
			parent.vertex(-points[farIndex].x, -points[farIndex].y,
					-points[farIndex].z);
			parent.vertex(0.0f, 0.0f, 0.0f);
			parent.vertex(points[farIndex].x, -points[farIndex].y,
					-points[farIndex].z);
			parent.endShape();
			break;
		case ORTHOGRAPHIC:
			if (drawFarPlane) {
				parent.beginShape(PApplet.LINES);
				parent.vertex(points[0].x, points[0].y, -points[0].z);
				parent.vertex(points[1].x, points[1].y, -points[1].z);
				parent.vertex(-points[0].x, points[0].y, -points[0].z);
				parent.vertex(-points[1].x, points[1].y, -points[1].z);
				parent.vertex(-points[0].x, -points[0].y, -points[0].z);
				parent.vertex(-points[1].x, -points[1].y, -points[1].z);
				parent.vertex(points[0].x, -points[0].y, -points[0].z);
				parent.vertex(points[1].x, -points[1].y, -points[1].z);
				parent.endShape();
			}
		}

		parent.popStyle();

		parent.popMatrix();
	}

	// 3. CAMERA

	public static void drawKFICamera(PApplet parent, float scale) {
		drawKFICamera(parent, 170, scale);
	}

	public static void drawKFICamera(PApplet parent, int color, float scale) {
		float halfHeight = scale * 0.07f;
		float halfWidth = halfHeight * 1.3f;
		float dist = halfHeight / PApplet.tan(PApplet.PI / 8.0f);

		float arrowHeight = 1.5f * halfHeight;
		float baseHeight = 1.2f * halfHeight;
		float arrowHalfWidth = 0.5f * halfWidth;
		float baseHalfWidth = 0.3f * halfWidth;

		// Frustum outline
		parent.pushStyle();

		parent.noFill();
		parent.stroke(color);
		parent.beginShape();
		parent.vertex(-halfWidth, halfHeight, -dist);
		parent.vertex(-halfWidth, -halfHeight, -dist);
		parent.vertex(0.0f, 0.0f, 0.0f);
		parent.vertex(halfWidth, -halfHeight, -dist);
		parent.vertex(-halfWidth, -halfHeight, -dist);
		parent.endShape();
		parent.noFill();
		parent.beginShape();
		parent.vertex(halfWidth, -halfHeight, -dist);
		parent.vertex(halfWidth, halfHeight, -dist);
		parent.vertex(0.0f, 0.0f, 0.0f);
		parent.vertex(-halfWidth, halfHeight, -dist);
		parent.vertex(halfWidth, halfHeight, -dist);
		parent.endShape();

		// Up arrow
		parent.noStroke();
		parent.fill(color);
		// Base
		parent.beginShape(PApplet.QUADS);
		if (InteractiveFrame.coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED) {
			parent.vertex(baseHalfWidth, -halfHeight, -dist);
			parent.vertex(-baseHalfWidth, -halfHeight, -dist);
			parent.vertex(-baseHalfWidth, -baseHeight, -dist);
			parent.vertex(baseHalfWidth, -baseHeight, -dist);
		} else {
			parent.vertex(-baseHalfWidth, halfHeight, -dist);
			parent.vertex(baseHalfWidth, halfHeight, -dist);
			parent.vertex(baseHalfWidth, baseHeight, -dist);
			parent.vertex(-baseHalfWidth, baseHeight, -dist);
		}
		parent.endShape();
		// Arrow
		parent.beginShape(PApplet.TRIANGLES);
		if (InteractiveFrame.coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED) {
			parent.vertex(0.0f, -arrowHeight, -dist);
			parent.vertex(arrowHalfWidth, -baseHeight, -dist);
			parent.vertex(-arrowHalfWidth, -baseHeight, -dist);
		} else {
			parent.vertex(0.0f, arrowHeight, -dist);
			parent.vertex(-arrowHalfWidth, baseHeight, -dist);
			parent.vertex(arrowHalfWidth, baseHeight, -dist);
		}
		parent.endShape();

		parent.popStyle();
	}
}
