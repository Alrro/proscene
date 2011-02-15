/*
 * Code based in the BoundingBox, BoundingSphere, BufferUtils from the jMonkeyEngine.
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 */

package test;

import java.nio.*;

import processing.core.PApplet;

abstract class BoundingVolume {
	Vector3f center;

	BoundingVolume() {
		center = new Vector3f();
	}

	abstract void computeFromPoints(FloatBuffer points);

	/**
	 * Updates the values of the given vector from the specified buffer at the
	 * index provided.
	 * 
	 * @param vector
	 *            the vector to set data on
	 * @param buf
	 *            the buffer to read from
	 * @param index
	 *            the position (in terms of vectors, not floats) to read from
	 *            the buf
	 */
	void populateFromBuffer(Vector3f vector, FloatBuffer buf, int index) {
		vector.x = buf.get(index * 3);
		vector.y = buf.get(index * 3 + 1);
		vector.z = buf.get(index * 3 + 2);
	}

	/**
	 * Sets the data contained in the given Vector3F into the FloatBuffer at the
	 * specified index.
	 * 
	 * @param vector
	 *            the data to insert
	 * @param buf
	 *            the buffer to insert into
	 * @param index
	 *            the postion to place the data; in terms of vectors not floats
	 */
	void setInBuffer(Vector3f vector, FloatBuffer buf, int index) {
		if (buf == null) {
			return;
		}
		if (vector == null) {
			buf.put(index * 3, 0);
			buf.put((index * 3) + 1, 0);
			buf.put((index * 3) + 2, 0);
		} else {
			buf.put(index * 3, vector.x);
			buf.put((index * 3) + 1, vector.y);
			buf.put((index * 3) + 2, vector.z);
		}
	}
}

// From jMonkey code
// http://code.google.com/p/jmonkeyengine/source/browse/trunk/src/com/jme/bounding/BoundingBox.java
class BoundingBox extends BoundingVolume {
	public float xExtent, yExtent, zExtent;
	Vector3f _compVect1, _compVect2, _compVect3;

	BoundingBox() {
		super();
		xExtent = yExtent = zExtent = 0;

		_compVect1 = new Vector3f();
		_compVect2 = new Vector3f();
		_compVect3 = new Vector3f();
	}

	/**
	 * <code>computeFromPoints</code> creates a new Bounding Box from a given
	 * set of points. It uses the <code>containAABB</code> method as default.
	 * 
	 * @param points
	 *            the points to contain.
	 */
	public void computeFromPoints(FloatBuffer points) {
		containAABB(points);
	}

	/**
	 * <code>containAABB</code> creates a minimum-volume axis-aligned bounding
	 * box of the points, then selects the smallest enclosing sphere of the box
	 * with the sphere centered at the boxes center.
	 * 
	 * @param points
	 *            the list of points.
	 */
	public void containAABB(FloatBuffer points) {
		if (points == null)
			return;

		points.rewind();
		if (points.remaining() <= 2) // we need at least a 3 float vector
			return;

		populateFromBuffer(_compVect1, points, 0);
		float minX = _compVect1.x, minY = _compVect1.y, minZ = _compVect1.z;
		float maxX = _compVect1.x, maxY = _compVect1.y, maxZ = _compVect1.z;

		for (int i = 1, len = points.remaining() / 3; i < len; i++) {
			populateFromBuffer(_compVect1, points, i);

			if (_compVect1.x < minX)
				minX = _compVect1.x;
			else if (_compVect1.x > maxX)
				maxX = _compVect1.x;

			if (_compVect1.y < minY)
				minY = _compVect1.y;
			else if (_compVect1.y > maxY)
				maxY = _compVect1.y;

			if (_compVect1.z < minZ)
				minZ = _compVect1.z;
			else if (_compVect1.z > maxZ)
				maxZ = _compVect1.z;
		}

		center.set(minX + maxX, minY + maxY, minZ + maxZ);
		center.multLocal(0.5f);

		xExtent = maxX - center.x;
		yExtent = maxY - center.y;
		zExtent = maxZ - center.z;
	}
}

// From jMonkey code:
// http://code.google.com/p/jmonkeyengine/source/browse/trunk/src/com/jme/bounding/BoundingSphere.java
// http://code.google.com/p/jmonkeyengine/source/browse/trunk/src/com/jme/util/geom/BufferUtils.java
class BoundingSphere extends BoundingVolume {
	float radius;
	Vector3f tempA, tempB, tempC, tempD;
	float radiusEpsilon = 1f + 0.00001f;

	BoundingSphere() {
		super();
		radius = 0;

		tempA = new Vector3f();
		tempB = new Vector3f();
		tempC = new Vector3f();
		tempD = new Vector3f();
	}

	/**
	 * <code>computeFromPoints</code> creates a new Bounding Sphere from a given
	 * set of points. It uses the <code>calcWelzl</code> method as default.
	 * 
	 * @param points
	 *            the points to contain.
	 */
	public void computeFromPoints(FloatBuffer points) {
		calcWelzl(points);
	}

	/**
	 * Calculates a minimum bounding sphere for the set of points. The algorithm
	 * was originally found at
	 * http://www.flipcode.com/cgi-bin/msg.cgi?showThread
	 * =COTD-SmallestEnclosingSpheres&forum=cotd&id=-1 in C++ and translated to
	 * java by Cep21
	 * 
	 * @param points
	 *            The points to calculate the minimum bounds from.
	 */
	public void calcWelzl(FloatBuffer points) {
		FloatBuffer buf = ByteBuffer.allocateDirect(4 * points.limit()).order(
				ByteOrder.nativeOrder()).asFloatBuffer();
		buf.clear();
		points.rewind();
		buf.put(points);
		buf.flip();
		recurseMini(buf, buf.limit() / 3, 0, 0);
	}

	/**
	 * Used from calcWelzl. This function recurses to calculate a minimum
	 * bounding sphere a few points at a time.
	 * 
	 * @param points
	 *            The array of points to look through.
	 * @param p
	 *            The size of the list to be used.
	 * @param b
	 *            The number of points currently considering to include with the
	 *            sphere.
	 * @param ap
	 *            A variable simulating pointer arithmatic from C++, and offset
	 *            in <code>points</code>.
	 */
	private void recurseMini(FloatBuffer points, int p, int b, int ap) {
		switch (b) {
		case 0:
			this.radius = 0;
			this.center.set(0, 0, 0);
			break;
		case 1:
			this.radius = 1f - radiusEpsilon;
			populateFromBuffer(center, points, ap - 1);
			break;
		case 2:
			populateFromBuffer(tempA, points, ap - 1);
			populateFromBuffer(tempB, points, ap - 2);
			setSphere(tempA, tempB);
			break;
		case 3:
			populateFromBuffer(tempA, points, ap - 1);
			populateFromBuffer(tempB, points, ap - 2);
			populateFromBuffer(tempC, points, ap - 3);
			setSphere(tempA, tempB, tempC);
			break;
		case 4:
			populateFromBuffer(tempA, points, ap - 1);
			populateFromBuffer(tempB, points, ap - 2);
			populateFromBuffer(tempC, points, ap - 3);
			populateFromBuffer(tempD, points, ap - 4);
			setSphere(tempA, tempB, tempC, tempD);
			return;
		}
		for (int i = 0; i < p; i++) {
			populateFromBuffer(tempA, points, i + ap);
			if (tempA.distanceSquared(center) - (radius * radius) > radiusEpsilon - 1f) {
				for (int j = i; j > 0; j--) {
					populateFromBuffer(tempB, points, j + ap);
					populateFromBuffer(tempC, points, j - 1 + ap);
					setInBuffer(tempC, points, j + ap);
					setInBuffer(tempB, points, j - 1 + ap);
				}
				recurseMini(points, i, b + 1, ap + 1);
			}
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 4 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @param B
	 *            The 3rd point inside the sphere.
	 * @param C
	 *            The 4th point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(Vector3f O, Vector3f A, Vector3f B, Vector3f C) {
		Vector3f a = A.subtract(O);
		Vector3f b = B.subtract(O);
		Vector3f c = C.subtract(O);

		float Denominator = 2.0f * (a.x * (b.y * c.z - c.y * b.z) - b.x
				* (a.y * c.z - c.y * a.z) + c.x * (a.y * b.z - b.y * a.z));
		if (Denominator == 0) {
			center.set(0, 0, 0);
			radius = 0;
		} else {
			Vector3f o = a.cross(b).multLocal(c.lengthSquared()).addLocal(
					c.cross(a).multLocal(b.lengthSquared())).addLocal(
					b.cross(c).multLocal(a.lengthSquared())).divideLocal(
					Denominator);

			radius = o.length() * radiusEpsilon;
			O.add(o, center);
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 3 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @param B
	 *            The 3rd point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(Vector3f O, Vector3f A, Vector3f B) {
		Vector3f a = A.subtract(O);
		Vector3f b = B.subtract(O);
		Vector3f acrossB = a.cross(b);

		float Denominator = 2.0f * acrossB.dot(acrossB);

		if (Denominator == 0) {
			center.set(0, 0, 0);
			radius = 0;
		} else {

			Vector3f o = acrossB.cross(a).multLocal(b.lengthSquared())
					.addLocal(b.cross(acrossB).multLocal(a.lengthSquared()))
					.divideLocal(Denominator);
			radius = o.length() * radiusEpsilon;
			O.add(o, center);
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 2 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(Vector3f O, Vector3f A) {
		radius = PApplet.sqrt(((A.x - O.x) * (A.x - O.x) + (A.y - O.y)
				* (A.y - O.y) + (A.z - O.z) * (A.z - O.z)) / 4f)
				+ radiusEpsilon - 1f;
		center.interpolate(O, A, .5f);
	}
}