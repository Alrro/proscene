/**
 *                     ProScene (version 0.9.96)      
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

//Thanks to Diego Chapeton who contributed code for this class

package remixlab.proscene;

/**
 * Point class that provides a quick replacement for the java.awt.Point.
 */
public class Point {
	
	/**
	 * The X coordinate of this Point.
	 */
	public int x;
	
	/**
	 * The Y coordinate of this Point.
	 */
	public int y;

	/**
	 * Constructs and initializes a point at the specified (x,y) location in the
	 * coordinate space.
	 */
	public Point() {
		this(0, 0);
	}

	/**
	 * Copy constructor
	 * 
	 * @param p
	 *          the point to be copied
	 */
	public Point(Point p) {
		this(p.getX(), p.getY());
	}

	/**
	 * Constructs and initializes a point at the specified (x,y) location in the
	 * coordinate space.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs and initializes a point at the specified (x,y) location in the
	 * coordinate space. The location (x,y) is given in single float precision.
	 */
	public Point(float x, float y) {
		this.x = (int) x;
		this.y = (int) y;
	}

	/**
	 * Returns the x coordinate of the point.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of the point.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Convenience wrapper function that simply returns {@code Point.distance(new
	 * Point(x1, y1), new Point(x2, y2))}.
	 * 
	 * @see #distance(Point, Point)
	 */
	public static float distance(int x1, int y1, int x2, int y2) {
		return Point.distance((float)x1, (float)y1, (float)x2, (float)y2);
	}

	/**
	 * Convenience wrapper function that simply returns {@code Point.distance(new
	 * Point(x1, y1), new Point(x2, y2))}.
	 * 
	 * @see #distance(Point, Point)
	 */
	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((float) Math.pow((x2 - x1), 2.0)	+ (float) Math.pow((y2 - y1), 2.0));
	}

	/**
	 * Returns the Euclidean distance between points p1 and p2.
	 */
	public static float distance(Point p1, Point p2) {
		return Point.distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}
