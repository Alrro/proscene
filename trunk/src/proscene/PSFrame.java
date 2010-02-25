/**
 * This package provides classes to ease the creation of interactive 3D
 * scenes implemented with the PSSL Processing renderer.
 */

package proscene;

import processing.core.*;

/**
 * A PSFrame is a 3D coordinate system, represented by a {@link #position()} and an
 * {@link #orientation()}. The order of these transformations is important:
 * the PSFrame is first translated and then rotated around the new translated origin.
 */

public class PSFrame implements Cloneable {
	protected PVector trans;
	protected PSQuaternion rot;
	protected PSFrame refFrame;
	protected PSConstraint constr;

	/**
	 * Creates a default PSFrame. 
	 * <p> 
	 * Its {@link #position()} is (0,0,0) and it has an identity
	 * {@link #orientation()} PSQuaternion. The {@link #referenceFrame()} and 
	 * the {@link #constraint()} are {@code null}.
	 */
	public PSFrame() {
		trans = new PVector(0, 0, 0);
		rot = new PSQuaternion();
		refFrame = null;
		constr = null;
	}

	/**
	 * Creates a PSFrame with a {@link #position()} and an {@link #orientation()}. 
	 * <p> 
	 * See the PVector and PSQuaternion documentations for convenient constructors and methods. 
	 * <p> 
	 * The PSFrame is defined in the world coordinate system (its {@link #referenceFrame()}
	 * is {@code null}). It has a {@code null} associated {@link #constraint()}.
	 */ 
	public PSFrame(PVector p, PSQuaternion r) {
		trans = new PVector (p.x, p.y, p.z);
		rot = new PSQuaternion(r);
		refFrame = null;
		constr = null;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 *            the PSFrame containing the data where to copy from
	 */
	protected PSFrame(PSFrame other) {
		trans = new PVector (other.translation().x, other.translation().y, other.translation().z);
		rot = new PSQuaternion(other.rotation());
		refFrame = other.referenceFrame();
		constr = other.constraint();
	}
	
	/**
	 * Calls {@link #PSFrame(PSFrame)} (which is private) and returns a copy
	 * of {@code this} object.
	 * 
	 * @see #PSFrame(PSFrame)
	 */
	public PSFrame copy() {
        return new PSFrame(this);
    }
	
	/**
	 * Implementation of the clone method. 
	 * <p> 
	 * The method performs a deep copy of the {@link #translation()} and 
	 * {@link #rotation()} objects of the PSFrame, and a shallow copy of
	 * its {@link #referenceFrame()} and {@link #constraint()} objects.
	 * 
	 * @see #copy()
	 */
	//public PSFrame clone() throws CloneNotSupportedException {
	public PSFrame clone() {
        try {
            PSFrame clonedPSFrame = (PSFrame) super.clone();
            clonedPSFrame.trans = new PVector (translation().x, translation().y, translation().z);
            clonedPSFrame.rot = new PSQuaternion(rotation());
            return clonedPSFrame;
        } catch (CloneNotSupportedException e) {
            throw new Error("Is too");
        }
    }

    /**
     * Returns the PSFrame translation, defined with respect to
     * the {@link #referenceFrame()}. 
     * <p>
     * Use {@link #position()} to get the result in the world coordinates.
     * These two values are identical when the {@link #referenceFrame()}
     * is {@code null} (default).
     * 
     * @see #setTranslation(PVector)
     * @see #setTranslationWithConstraint(PVector)
     */
	public final PVector translation() {
		return trans;
	}

    /**
     * Returns the PSFrame rotation, defined with respect
     * to the {@link #referenceFrame()} (i.e, the current
     * PSQuaternion orientation). 
     * <p> 
     * Use {@link #orientation()} to get the result in the world coordinates.
     * These two values are identical when the {@link #referenceFrame()}
     * is {@code null} (default).
     * 
     * @see #setRotation(PSQuaternion)
     * @see #setRotationWithConstraint(PSQuaternion)
     */
	public final PSQuaternion rotation() {
		return rot;
	}

    /**
     * Returns the reference PSFrame, in which coordinates system the PSFrame is defined. 
     * <p> 
     * The {@link #translation()} {@link #rotation()} of the PSFrame are defined with
     * respect to the reference PSFrame coordinate system. A {@code null}
     * reference PSFrame (default value) means that the PSFrame is defined in
     * the world coordinate system.  
     * <p> 
     * Use {@link #position()} and {@link #orientation()} to recursively convert values
     * along the reference PSFrame chain and to get values expressed in the world
     * coordinate system. The values match when the reference PSFrame {@code null}.  
     * <p>  
     * Use {@link #setReferenceFrame(PSFrame)} to set this value and create a Frame hierarchy.
     * Convenient functions allow you to convert 3D coordinates from one PSFrame
     * to another: see {@link #coordinatesOf(PVector)}, {@link #localCoordinatesOf(PVector)} ,
     * {@link #coordinatesOfIn(PVector, PSFrame)} and their inverse functions.  
     * <p>  
     * Vectors can also be converted using {@link #transformOf(PVector)},
     * {@link #transformOfIn(PVector, PSFrame)}, {@link #localTransformOf(PVector)} and
     * their inverse functions.
     */
	public final PSFrame referenceFrame() {
		return refFrame;
	}
	
	/**
	 * Returns the current constraint applied to the PSFrame. 
	 * <p> 
	 * A {@code null} value (default) means that no PSConstraint is used to
	 * filter the PSFrame translation and rotation. 
	 * <p> 
	 * See the PSConstraint class documentation for details.
	 */
	public PSConstraint constraint() {
		return constr;
	}

    /**
     * Sets the {@link #translation()} of the frame, locally defined
     * with respect to the {@link #referenceFrame()}. 
     * <p> 
     * Use {@link #setPosition(PVector)} to define the world coordinates {@link #position()}.
     * Use {@link #setTranslationWithConstraint(PVector)} to take into account the potential
     * {@link #constraint()} of the PSFrame.
     */
	public final void setTranslation(PVector t) {
		this.trans = t;
	}

	/**
	 * Same as {@link #setTranslation(PVector)}, but with {@code float} parameters.
	 */
	public final void setTranslation(float x, float y, float z) {
		setTranslation(new PVector(x, y, z));
	}
	
	/**
	 * Same as {@link #setTranslation(PVector)}, but if there's a
	 * {@link #constraint()} it is satisfied (without modifying
	 * {@code translation}).
	 * 
	 * @see #setRotationWithConstraint(PSQuaternion)
	 * @see #setPositionWithConstraint(PVector)
	 */
	public final void setTranslationWithConstraint(PVector translation) {
		PVector deltaT = PVector.sub(translation, this.translation());
		if (constraint() != null)
			deltaT = constraint().constrainTranslation(deltaT, this);
		
		setTranslation( PVector.add(this.translation(), deltaT) );
		
		/**
		translation.x = this.translation().x;
		translation.y = this.translation().y;
		translation.z = this.translation().z;
		*/
	}

    /**
     * Set the current rotation PSQuaternion. See the different
     * PSQuaternion constructors. 
     * <p> 
     * Sets the {@link #rotation()} of the Frame, locally defined
     * with respect to the {@link #referenceFrame()}. 
     * <p> 
     * Use {@link #setOrientation(PSQuaternion)} to define the world
     * coordinates {@link #orientation()}. The potential 
     * {@link #constraint()} of the PSFrame is not taken into account,
     * use {@link #setRotationWithConstraint(PSQuaternion)} instead.
     *      
     * @see #setRotationWithConstraint(PSQuaternion)
     * @see #rotation()
     * @see #setTranslation(PVector)
     */
	public final void setRotation(PSQuaternion r) {
		this.rot = r;
	}

	/**
	 * Same as {@link #setRotation(PSQuaternion)} but with {@code float}
	 * PSQuaternion parameters.
	 */
	public final void setRotation(float x, float y, float z, float w) {
		setRotation(new PSQuaternion(x, y, z, w));
	}

	/**
	 * Same as {@link #setRotation(PSQuaternion)}, if there's a {@link #constraint()}
	 * it's satisfied (without modifying {@code rotation}).
	 * 
	 * @see #setTranslationWithConstraint(PVector)
	 * @see #setOrientationWithConstraint(PSQuaternion)
	 */
	public final void setRotationWithConstraint(PSQuaternion rotation) {
		PSQuaternion deltaQ = PSQuaternion.multiply(this.rotation().inverse(), rotation);
		if (constraint() != null)
			deltaQ = constraint().constrainRotation(deltaQ, this);

	    deltaQ.normalize(); // Prevent numerical drift

	    setRotation(PSQuaternion.multiply(this.rotation(), deltaQ));
	    rot.normalize();
	    //rotation.x = this.rotation().x;
	    //rotation.y = this.rotation().y;
	    //rotation.z = this.rotation().z;
	    //rotation.w = this.rotation().w;
	}
	
	/**
	 * Sets the {@link #referenceFrame()} of the PSFrame.
	 * <p> 
	 * The PSFrame {@link #translation()} and {@link #rotation()} are then defined
	 * in the {@link #referenceFrame()} coordinate system. 
	 * <p> 
	 * Use {@link #position()} and {@link #orientation()} to express these in
	 * the world coordinate system. 
	 * <p> 
	 * Using this method, you can create a hierarchy of Frames. This hierarchy needs
	 * to be a tree, which root is the world coordinate system (i.e.,
	 * {@code null} {@link #referenceFrame()}). No action is performed if setting
	 * {@code refFrame} as the {@link #referenceFrame()} would create a loop in the
	 * PSFrame hierarchy.
	 * 
	 * @see #settingAsReferenceFrameWillCreateALoop(PSFrame)
	 */ 
	public final void setReferenceFrame(PSFrame rFrame) {
		if (!settingAsReferenceFrameWillCreateALoop(rFrame))
			this.refFrame = rFrame;
	}
	
	public void setConstraint(PSConstraint c) {
		this.constr = c;
	}

	/**
	 * Returns {@code true} if setting {@code frame} as the PSFrame's {@link #referenceFrame()}
	 * would create a loop in the PSFrame hierarchy.
	 */
	public final boolean settingAsReferenceFrameWillCreateALoop(PSFrame frame) {
		PSFrame f = frame;
		while (f != null) {
			if (f == this)
				return true;
			f = f.referenceFrame();
		}
		return false;
	}

	/** 
	 * Returns the orientation of the PSFrame, defined in the world coordinate system.
	 * 
	 * @see #position()
	 * @see #setOrientation(PSQuaternion)
	 * @see #rotation()
	 */
	public final PSQuaternion orientation() {
		PSQuaternion res = rotation();
		PSFrame fr = referenceFrame();
		while (fr != null) {
			res = PSQuaternion.multiply(fr.rotation(), res);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Sets the {@link #position()} of the PSFrame, defined in the world coordinate system. 
	 * <p> 
	 * Use  {@link #setTranslation(PVector)} to define the local PSframe translation
	 * (with respect to the {@link #referenceFrame()}). The potential {@link #constraint()}
	 * of the PSFrame is not taken into account, use
	 * {@link #setPositionWithConstraint(PVector)} instead.
	 */
	public final void setPosition(PVector p) {
		if (referenceFrame() != null)
			setTranslation(referenceFrame().coordinatesOf(p));
		else
			setTranslation(p);
	}

	/**
	 * Same as {@link #setPosition(float, float, float)}, but with {@code float} parameters.
	 */
	public final void setPosition(float x, float y, float z) {
		setPosition(new PVector(x, y, z));
	}
	
	/**
	 * Same as {@link #setPosition(PVector)}, but if there's a
	 * {@link #constraint()} it is satisfied (without modifying
	 * {@code position}).
	 * 
	 * @see #setOrientationWithConstraint(PSQuaternion)
	 * @see #setTranslationWithConstraint(PVector)
	 */
	public final void setPositionWithConstraint(PVector position) {
		  if (referenceFrame() != null)
			  position = referenceFrame().coordinatesOf(position);
		  
		  setTranslationWithConstraint(position);
	}

	/**
	 * Sets the {@link #orientation()} of the PSFrame, defined in the world coordinate system. 
	 * <p> 
	 * Use {@link #setRotation(PSQuaternion)}to define the local frame rotation
	 * (with respect to the {@link #referenceFrame()}).  The potential
	 * {@link #constraint()} of the PSFrame is not taken into account, use
	 * {@link #setOrientationWithConstraint(PSQuaternion)} instead.
	 */	 
	public final void setOrientation(PSQuaternion q) {
		if (referenceFrame() != null)
			setRotation(PSQuaternion.multiply(referenceFrame().orientation().inverse(), q));
		else
			setRotation(q);
	}

	/**
	 * Same as {@link #setOrientation(PSQuaternion)}, but with {@code float} parameters.
	 */
	public final void setOrientation(float x, float y, float z, float w) {
		setOrientation(new PSQuaternion(x, y, z, w));
	}
	
	/**
	 * Same as {@link #setOrientation(PSQuaternion)}, but if there's a
	 * {@link #constraint()} it is satisfied (without modifying
	 * {@code orientation}).
	 * 
	 * @see #setPositionWithConstraint(PVector)
	 * @see #setRotationWithConstraint(PSQuaternion)
	 */
	public final void setOrientationWithConstraint(PSQuaternion orientation) {		
		if (referenceFrame() != null)
		    orientation = PSQuaternion.multiply(referenceFrame().orientation().inverse(), orientation);

		  setRotationWithConstraint(orientation);
	}

	/**
	 * Returns the position of the PSFrame, defined in the world coordinate system.
	 * 
	 * @see #orientation()
	 * @see #setPosition(PVector)
	 * @see #translation()
	 */
	public final PVector position() {
		return inverseCoordinatesOf(new PVector(0, 0, 0));
	}

	/**
	 * Translates the PSFrame of {@code t} (defined in the PSFrame coordinate system).
	 * <p> 
	 * If there's a {@link #constraint()} it is satisfied without modifying {@code t}:
	 * The translation actually applied to the PSFrame may differ from {@code t} 
	 * since it can be filtered by the {@link #constraint()}. Use
	 * {@link #translateModifyingArgument(PVector)} to retrieve the filtered
	 * translation value. Use {@link #setTranslation(PVector)} to directly translate
	 * the PSFrame without taking the {@link #constraint()} into account.
	 * 
	 * @see #rotate(PSQuaternion)
	 */
	public final void translate(PVector t) {		
		if (constraint() != null)
		    trans.add(constraint().constrainTranslation(t, this));
		else
			trans.add(t);
	}
	
	/**
	//it can also be:
	public final void translate(PVector t) {
		PVector oV = new PVector(t.x, t.y, t.z);
		translateModifyArgument(oV);
	}
	*/
	
	/**
	 * Same as {@link #translate(PVector)} but if there's a {@link #constraint()} 
	 * {@code t} is modified to satisfy it.
	 */
	public final void translateModifyingArgument(PVector t) {
		//TODO this may be overkill: check if this function can be discarded
		if (constraint() != null) {
			PVector o = constraint().constrainTranslation(t, this);		    
		    t.x = o.x;
		    t.y = o.y;
		    t.z = o.z;		    
		}
		trans.add(t);
	}
	
	/**
	 * Same as {@link #translate(PVector)} but with {@code float} parameters.
	 */
	public final void translate(float x, float y, float z) {		
		translate(new PVector(x, y, z));
	}

	/**
	 * Rotates the PSFrame by {@code q} (defined in the PSFrame coordinate system):
	 * {@code R = R*q}.
	 * <p> 
	 * If there's a {@link #constraint()} it is satisfied without modifying {@code q}:
	 * The rotation actually applied to the PSFrame may differ from {@code q} since it
	 * can be filtered by the {@link #constraint()}. Use
	 * {@link #rotateModifyingArgument(PSQuaternion)} to retrieve the filtered rotation
	 * value. Use {@link #setRotation(PSQuaternion)} to directly rotate the PSFrame
	 * without taking the {@link #constraint()} into account.
	 * 
	 * @see #translate(PVector)
	 */
	public final void rotate(PSQuaternion q) {
		if (constraint()!= null)
			rot.multiply(constraint().constrainRotation(q, this));
		else
			rot.multiply(q);
		
		rot.normalize(); // Prevents numerical drift
	}
	
	/**
	 * Same as {@link #rotate(PSQuaternion)} but if there's a {@link #constraint()} 
	 * {@code q} is modified to satisfy it.
	 */
	public final void rotateModifyingArgument(PSQuaternion q) {
		//TODO this may be overkill: check if this function can be discarded
		if (constraint() != null) {
			PSQuaternion o = constraint().constrainRotation(q, this);		    
		    q.x = o.x;
		    q.y = o.y;
		    q.z = o.z;
		    q.w = o.w;
		}
		rot.multiply(q);
		
		rot.normalize(); // Prevents numerical drift
	}

	/**
	 * Same as {@link #rotate(PSQuaternion)} but with {@code float} PSQuaternion parameters.
	 */
	public final void rotate(float x, float y, float z, float w) {
		rotate(new PSQuaternion(x, y, z, w));
	}

	/**
	 * Makes the PSFrame {@link #rotate(PSQuaternion)} by {@code rotation} around {@code point}. 
	 * <p> 
	 * {@code point} is defined in the world coordinate system, while the {@code rotation}
	 * axis is defined in the PSFrame coordinate system. 
	 * <p> 
	 * If the PSFrame has a {@link #constraint()}, {@code rotation} is first constrained using
	 * {@link proscene.PSConstraint#constrainRotation(PSQuaternion, PSFrame)}.
	 * The translation which results from the filtered rotation around {@code point} is then
	 * computed and filtered using
	 * {@link proscene.PSConstraint#constrainTranslation(PVector, PSFrame)}.
	 */
	public final void rotateAroundPoint(PSQuaternion rotation, PVector point) {
		if (constraint() != null)
			rotation = constraint().constrainRotation(rotation, this);
		
		this.rot.multiply(rotation);		
		this.rot.normalize(); // Prevents numerical drift
		
		PSQuaternion q = new PSQuaternion(inverseTransformOf(rotation.axis()), rotation.angle());		
		PVector t = PVector.add(point, q.rotate(PVector.sub(position(), point)));
		t.sub(trans);
		
		if (constraint() != null)
			trans.add(constraint().constrainTranslation(t, this));
		else
			trans.add(t);
	}

	/**
	 * Same as {@link #rotateAroundPoint(PSQuaternion, PVector)} but if there's a
	 * {@link #constraint()} {@code rotation} is modified to satisfy it.
	*/
	public final void rotateAroundPointModifyingArgument(PSQuaternion rotation, PVector point) {
		//TODO this may be overkill: check if this function can be discarded
		if (constraint() != null) {
			PSQuaternion q = constraint().constrainRotation(rotation, this);
			rotation.x = q.x;
			rotation.y = q.y;
			rotation.z = q.z;
			rotation.w = q.w;
		}
		
		this.rot.multiply(rotation);		
		this.rot.normalize(); // Prevents numerical drift
		
		PSQuaternion q = new PSQuaternion(inverseTransformOf(rotation.axis()), rotation.angle());		
		PVector t = PVector.add(point, q.rotate(PVector.sub(position(), point)));
		t.sub(trans);
		
		if (constraint() != null)
			trans.add(constraint().constrainTranslation(t, this));
		else
			trans.add(t);
	}
	
	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, false, 0.85f)}
	 */
	public final void alignWithFrame(PSFrame frame) {
		alignWithFrame(frame, false, 0.85f);
	}

	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, move, 0.85f)}
	 */
	public final void alignWithFrame(PSFrame frame, boolean move) {
		alignWithFrame(frame, move, 0.85f);
	}

	/**
	 * Convenience function that simply calls {@code alignWithFrame(frame, false, threshold)}
	 */
	public final void alignWithFrame(PSFrame frame, float threshold) {
		alignWithFrame(frame, false, threshold);
	}

	/**
	 * Aligns the PSFrame with {@code frame}, so that two of their axis are parallel. 
	 * <p> 
	 * If one of the X, Y and Z axis of the PSFrame is almost parallel to any of the
	 * X, Y, or Z axis of {@code frame}, the PSFrame is rotated so that these two
	 * axis actually become parallel. 
	 * <p> 
	 * If, after this first rotation, two other axis are also almost parallel, a
	 * second alignment is performed. The two frames then have identical orientations,
	 * up to 90 degrees rotations. 
	 * <p> 
	 * {@code threshold} measures how close two axis must be to be considered parallel.
	 * It is compared with the absolute values of the dot product of the normalized axis. 
	 * <p> 
	 * When {@code move} is set to {@code true}, the PSFrame {@link #position()} is
	 * also affected by the alignment. The new PSFrame {@link #position()} is such that
	 * the {@code frame} frame position (computed with {@link #coordinatesOf(PVector)},
	 * in the PSFrame coordinates system) does not change. 
	 * <p> 
	 * {@code frame} may be {@code null} and then represents the world coordinate system
	 * (same convention than for the {@link #referenceFrame()}).
	 */
	public final void alignWithFrame(PSFrame frame, boolean move, float threshold) {
		PVector[][] directions = new PVector[2][3];
		for (int d = 0; d < 3; ++d) {
			PVector dir = new PVector((d == 0) ? 1.0f : 0.0f, (d == 1) ? 1.0f
					: 0.0f, (d == 2) ? 1.0f : 0.0f);
			if (frame != null)
				directions[0][d] = frame.inverseTransformOf(dir);
			else
				directions[0][d] = dir;
			directions[1][d] = inverseTransformOf(dir);
		}

		float maxProj = 0.0f;
		float proj;
		short[] index = new short[2];
		index[0] = index[1] = 0;

		PVector vec = new PVector(0.0f, 0.0f, 0.0f);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				vec.set(directions[0][i]);
				proj = PApplet.abs(vec.dot(directions[1][j]));
				if ((proj) >= maxProj) {
					index[0] = (short) i;
					index[1] = (short) j;
					maxProj = proj;
				}
			}
		}

		//PSFrame old = new PSFrame(this);
		PSFrame old = this.clone();

		vec.set(directions[0][index[0]]);
		float coef = vec.dot(directions[1][index[1]]);

		if (PApplet.abs(coef) >= threshold) {
			vec.set(directions[0][index[0]]);
			PVector axis = vec.cross(directions[1][index[1]]);
			float angle = PApplet.asin(axis.mag());
			if (coef >= 0.0)
				angle = -angle;
			// setOrientation(Quaternion(axis, angle) * orientation());
			PSQuaternion q = new PSQuaternion(axis, angle);
			q = PSQuaternion.multiply(rotation().inverse(), q);
			q = PSQuaternion.multiply(q, orientation());
			rotate(q);

			// Try to align an other axis direction
			short d = (short) ((index[1] + 1) % 3);
			PVector dir = new PVector((d == 0) ? 1.0f : 0.0f, (d == 1) ? 1.0f
					: 0.0f, (d == 2) ? 1.0f : 0.0f);
			dir = inverseTransformOf(dir);

			float max = 0.0f;
			for (int i = 0; i < 3; ++i) {
				vec.set(directions[0][i]);
				proj = PApplet.abs(vec.dot(dir));
				if (proj > max) {
					index[0] = (short) i;
					max = proj;
				}
			}

			if (max >= threshold) {
				vec.set(directions[0][index[0]]);
				axis = vec.cross(dir);
				angle = PApplet.asin(axis.mag());
				vec.set(directions[0][index[0]]);
				if (vec.dot(dir) >= 0.0)
					angle = -angle;
				// setOrientation(Quaternion(axis, angle) * orientation());
				q.fromAxisAngle(axis, angle);
				q = PSQuaternion.multiply(rotation().inverse(), q);
				q = PSQuaternion.multiply(q, orientation());
				rotate(q);
			}
		}
		
		if (move) {			
			PVector center = new PVector(0.0f, 0.0f, 0.0f);
			if (frame != null)
				center = frame.position();

			vec = PVector.sub(center, orientation().rotate(old.coordinatesOf(center)));
			vec.sub(translation());
			translate(vec);
		}
	}

	/**
	 * Translates the PSFrame so that its {@link #position()} lies on the line
	 * defined by {@code origin} and {@code direction} (defined in the world
	 * coordinate system). 
	 * <p> 
	 * Simply uses an orthogonal projection. {@code direction} does not need
	 * to be normalized.
	 */
	public final void projectOnLine(PVector origin, PVector direction) {
		PVector shift = PVector.sub(origin, position());
		PVector proj = shift;				
		//float directionSquaredNorm = (direction.x * direction.x) + (direction.y * direction.y) + (direction.z * direction.z);
		//float modulation = proj.dot(direction) / directionSquaredNorm; 
		//proj = PVector.mult(direction, modulation);
		proj = PSUtility.projectVectorOnAxis(proj, direction);
		translate(PVector.sub(shift, proj));	
	}

	/**
	 * Returns the PSFrame coordinates of a point {@code src} defined in the world
	 * coordinate system (converts from world to Frame).
	 * <p>  
	 * {@link #inverseCoordinatesOf(PVector)} performs the inverse conversion.
	 * {@link #transformOf(PVector)} converts 3D vectors instead of 3D coordinates.
	 */
	public final PVector coordinatesOf(PVector src) {
		if (referenceFrame() != null)
			return localCoordinatesOf(referenceFrame().coordinatesOf(src));
		else
			return localCoordinatesOf(src);
	}

	/**
	 * Returns the world coordinates of the point whose position in the PSFrame
	 * coordinate system is {@code src} (converts from Frame to world). 
	 * <p> 
	 * {@link #coordinatesOf(PVector)} performs the inverse conversion. Use
	 * {@link #inverseTransformOf(PVector)} to transform 3D vectors instead
	 * of 3D coordinates.
	 */
	public final PVector inverseCoordinatesOf(PVector src) {
		PSFrame fr = this;
		PVector res = src;
		while (fr != null) {
			res = fr.localInverseCoordinatesOf(res);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Returns the PSFrame coordinates of a point {@code src} defined in the
	 * {@link #referenceFrame()} coordinate system (converts from {@link #referenceFrame()}
	 * to Frame). 
	 * <p> 
	 * {@link #localInverseCoordinatesOf(PVector)} performs the inverse conversion.
	 * 
	 * @see #localTransformOf(PVector)
	 */
	public final PVector localCoordinatesOf(PVector src) {
		return rotation().inverseRotate(PVector.sub(src, translation()));
	}

	/**
	 * Returns the {@link #referenceFrame()} coordinates of a point {@code src} defined
	 * in the PSFrame coordinate system (converts from Frame to {@link #referenceFrame()}). 
	 * <p> 
	 * {@link #localCoordinatesOf(PVector)} performs the inverse conversion.
	 * 
	 * @see #localInverseTransformOf(PVector)
	 */
	public final PVector localInverseCoordinatesOf(PVector src) {
		return PVector.add(rotation().rotate(src), translation());
	}

	/**
	 * Returns the PSFrame coordinates of the point whose position in the {@code from}
	 * coordinate system is {@code src} (converts from {@code from} to PSFrame). 
	 * <p> 
	 * {@link #coordinatesOfIn(PVector, PSFrame)} performs the inverse transformation.
	 */
	public final PVector coordinatesOfFrom(PVector src, PSFrame from) {
		if (this == from)
			return src;
		else if (referenceFrame() != null)
			return localCoordinatesOf(referenceFrame().coordinatesOfFrom(src,
					from));
		else
			return localCoordinatesOf(from.inverseCoordinatesOf(src));
	}

	/**
	 * Returns the {@code in} coordinates of the point whose position in the PSFrame
	 * coordinate system is {@code src} (converts from PSFrame to {@code in}). 
	 * <p> 
	 * {@link #coordinatesOfFrom(PVector, PSFrame)} performs the inverse transformation.
	 */
	public final PVector coordinatesOfIn(PVector src, PSFrame in) {
		PSFrame fr = this;
		PVector res = src;
		while ((fr != null) && (fr != in)) {
			res = fr.localInverseCoordinatesOf(res);
			fr = fr.referenceFrame();
		}

		if (fr != in)
			// in was not found in the branch of this, res is now expressed in
			// the world
			// coordinate system. Simply convert to in coordinate system.
			res = in.coordinatesOf(res);

		return res;
	}
	
	/**
	 * Returns the PSFrame transform of a vector {@code src} defined in the world
	 * coordinate system (converts vectors from world to Frame). 
	 * <p> 
	 * {@link #inverseTransformOf(PVector)} performs the inverse transformation.
	 * {@link #coordinatesOf(PVector)} converts 3D coordinates instead of 3D vectors
	 * (here only the rotational part of the transformation is taken into account).
	 */
	public final PVector transformOf(PVector src) {
		if (referenceFrame() != null)
			return localTransformOf(referenceFrame().transformOf(src));
		else
			return localTransformOf(src);

	}

	/**
	 * Returns the world transform of the vector whose coordinates in the PSFrame
	 * coordinate system is {@code src} (converts vectors from Frame to world). 
	 * <p> 
	 * {@link #transformOf(PVector)} performs the inverse transformation. Use
	 * {@link #inverseCoordinatesOf(PVector)} to transform 3D coordinates instead
	 * of 3D vectors.
	 */
	public final PVector inverseTransformOf(PVector src) {
		PSFrame fr = this;
		PVector res = src;
		while (fr != null) {
			res = fr.localInverseTransformOf(res);
			fr = fr.referenceFrame();
		}
		return res;
	}

	/**
	 * Returns the PSFrame transform of a vector {@code src} defined in the
	 * {@link #referenceFrame()} coordinate system (converts vectors from
	 * {@link #referenceFrame()} to PSFrame). 
	 * <p> 
	 * {@link #localInverseTransformOf(PVector)} performs the inverse transformation.
	 * 
	 * @see #localCoordinatesOf(PVector)
	 */
	public final PVector localTransformOf(PVector src) {
		return rotation().inverseRotate(src);
	}

	/**
	 * Returns the {@link #referenceFrame()} transform of a vector {@code src}
	 * defined in the PSFrame coordinate system (converts vectors from PSFrame
	 * to {@link #referenceFrame()}). 
	 * <p> 
	 * {@link #localTransformOf(PVector)} performs the inverse transformation.
	 * 
	 * @see #localInverseCoordinatesOf(PVector)
	 */
	public final PVector localInverseTransformOf(PVector src) {
		return rotation().rotate(src);
	}

	/**
	 * Returns the PSFrame transform of the vector whose coordinates in the {@code from}
	 * coordinate system is {@code src} (converts vectors from {@code from} to PSFrame). 
	 * <p> 
	 * {@link #transformOfIn(PVector, PSFrame)} performs the inverse transformation.
	 */
	public final PVector transformOfFrom(PVector src, PSFrame from) {
		if (this == from)
			return src;
		else if (referenceFrame() != null)
			return localTransformOf(referenceFrame().transformOfFrom(src, from));
		else
			return localTransformOf(from.inverseTransformOf(src));
	}

	/**
	 * Returns the {@code in} transform of the vector whose coordinates in the PSFrame
	 * coordinate system is {@code src} (converts vectors from PSFrame to {@code in}). 
	 * <p> 
	 * {@link #transformOfFrom(PVector, PSFrame)} performs the inverse transformation.
	 */
	public final PVector transformOfIn(PVector src, PSFrame in) {
		PSFrame fr = this;
		PVector res = src;
		while ((fr != null) && (fr != in)) {
			res = fr.localInverseTransformOf(res);
			fr = fr.referenceFrame();
		}

		if (fr != in)
			// in was not found in the branch of this, res is now expressed in
			// the world
			// coordinate system. Simply convert to in coordinate system.
			res = in.transformOf(res);

		return res;
	}

	/**
	 * Returns the 4x4 OpenGL transformation matrix represented by the PSFrame. 
	 * <p>  
	 * <b>Attention:</b> The OpenGL format of the result is the transpose of the
	 * actual mathematical European representation (translation is on
	 * the last <i>line</i> instead of the last <i>column</i>). 
	 * <p>
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 * 
	 * @see #pMatrix()
	 */
	public final float[][] openGLMatrix() {
		float[][] m = new float[4][4];

		m = rot.openGLMatrix();

		m[3][0] = trans.x;
		m[3][1] = trans.y;
		m[3][2] = trans.z;

		return m;
	}
	
	/**
	 * Returns the PMatrix3D associated with this PSFrame. 
	 * <p> 
	 * This method should be used in conjunction with {@code multMatrix()}
	 * to modify the processing modelview matrix from a PSFrame hierarchy. For example
	 * with this PSFrame hierarchy: 
	 * <p> 
	 * {@code PSFrame body = new PSFrame();} <br>
	 * {@code PSFrame leftArm = new PSFrame();} <br>
	 * {@code PSFrame rightArm = new PSFrame();} <br>
	 * {@code leftArm.setReferenceFrame(body);} <br>
	 * {@code rightArm.setReferenceFrame(body);} <br> 
	 * <p> 
	 * The associated processing drawing code should look like: 
	 * <p> 
	 * {@code pushMatrix();} <br>
	 * {@code applyMatrix(body.pMatrix());} <br>
	 * {@code drawBody();} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyMatrix(leftArm.pMatrix());} <br>
	 * {@code drawArm();} <br>
	 * {@code popMatrix();} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyMatrix(rightArm.pMatrix());} <br>
	 * {@code drawArm();} <br>
	 * {@code popMatrix();} <br>
	 * {@code popMatrix();} <br> 
	 * <p> 
	 * Note the use of nested {@code pushMatrix()} and {@code popMatrix()}
	 * blocks to represent the frame hierarchy: {@code leftArm} and {@code rightArm}
	 * are both correctly drawn with respect to the {@code body} coordinate system. 
	 * <p> 
	 * This matrix only represents the local PSFrame transformation (i.e., with respect
	 * to the {@link #referenceFrame()}). Use {@link #worldOpenGLMatrix()} to get the full 
	 * PSFrame transformation matrix (i.e., from the world to the Frame coordinate system).
	 * These two match when the {@link #referenceFrame()} is {@code null}. 
	 * <p>
	 * The result is only valid until the next call to {@code pMatrix()}, {@link #openGLArray()},
	 * {@link #worldOpenGLMatrix()} or {@link #worldOpenGLArray()}. Use it immediately (as above). 
	 * <p> 
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 * 
	 * @see #openGLMatrix()
	 */
	public final PMatrix3D pMatrix() {
		return PSUtility.fromOpenGLToPMatrix3D(openGLMatrix());
	}
	
	/**
	 * Apply the transformation defined by this PSFrame to {@code p}. The PSFrame is
	 * first translated and then rotated around the new translated origin.
	 * <p>
	 * Same as:
	 * <p>
	 * {@code p.translate(translation().x, translation().y, translation().z);} <br>
	 * {@code p.rotate(rotation().angle(), rotation().axis().x, 
	 * rotation().axis().y, 
	 * rotation().axis().z);} <br>
	 * <p>
	 * This method should be used whenever possible.
	 * <p>
	 * <b>Attention:</b> In order to apply the PSFrame transformation one can also call
	 * {@code p.applyMatrix(this.pMatrix())}. However, {@code p.applyMatrix} is very
	 * slow because it will try to calculate the inverse of the transform, so it should
	 * be avoided whenever possible. 
	 */
	public void applyTransformation(PApplet p) {
		p.translate(translation().x, translation().y, translation().z);
		p.rotate(rotation().angle(), rotation().axis().x,
				                     rotation().axis().y,
				                     rotation().axis().z);
	}

	/**
	 * float[] version of {@link #openGLMatrix()}.
	 */
	public final float[] openGLArray() {
		float[] m = new float[16];

		m = rot.openGLArray();

		m[12] = trans.x;
		m[13] = trans.y;
		m[14] = trans.z;

		return m;
	}

	/**
	 * Returns the 4x4 OpenGL transformation matrix represented by the PSFrame. 
	 * <p> 
	 * This method should be used in conjunction with {@code multMatrix()}
	 * to modify the processing modelview matrix from a PSFrame: 
	 * <p> 
	 * {@code // The modelview here corresponds to the world coordinate system.}
	 * {@code PSFrame fr = new PSFrame(pos, PSQuaternion(from, to));} <br>
	 * {@code pushMatrix();} <br>
	 * {@code applyMatrix(PSUtility.fromOpenGL4x4Matrix(worldMatrix()));} <br>
	 * {@code // draw object in the fr coordinate system.} <br>
	 * {@code popMatrix();} <br> 
	 * <p>
	 * This matrix represents the global PSFrame transformation: the entire
	 * {@link #referenceFrame()} hierarchy is taken into account to define the
	 * PSFrame transformation from the world coordinate system.
	 * Use {@link #pMatrix()} (or {@link #openGLMatrix()}) to get the local PSFrame transformation matrix
	 * (i.e. defined with respect to the referenceFrame()).
	 * These two match when the {@link #referenceFrame()} is {@code null}. 
	 * <p> 
	 * The OpenGL format of the result is the transpose of the actual
	 * mathematical European representation (translation is on the last
	 * <i>line</i> instead of the last <i>column</i>. 
	 * <p> 
	 * <b>Attention:</b> The result is only valid until the next call to
	 * {@link #openGLMatrix()}, {@link #openGLArray()} {@code worldMatrix()} or {@link #worldOpenGLArray()}.
	 * Use it immediately (as above). 
	 * <p> 
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 */
	public final float[][] worldOpenGLMatrix() {
		//TODO: should implement worldPMatrix instead!
		if (referenceFrame() != null) {
			final PSFrame fr = new PSFrame();
			fr.setTranslation(position());
			fr.setRotation(orientation());
			return fr.openGLMatrix();
		} else
			return openGLMatrix();
	}

	/**
	 * float[] version of {@link #worldOpenGLMatrix()}.
	 */
	public final float[] worldOpenGLArray() {		
		if (referenceFrame() != null) {
			final PSFrame fr = new PSFrame();
			fr.setTranslation(position());
			fr.setRotation(orientation());
			return fr.openGLArray();
		} else
			return openGLArray();
	}

	/**
	 * This is an overloaded method provided for convenience. Same as {@link #fromOpenGLArray(float[])}
	 * 
	 * @see #fromPMatrix(PMatrix3D)
	 */
	public final void fromOpenGLMatrix(float[][] m) {
		// m should be of size [4][4]
		if (PApplet.abs(m[3][3]) < 1E-8) {
			// pending: catch the exception
			return;
		}
		trans.x = m[3][0] / m[3][3];
		trans.y = m[3][1] / m[3][3];
		trans.z = m[3][2] / m[3][3];
		float[][] r = new float[3][3];
		for (int i = 0; i < 3; ++i) {			
			for (int j = 0; j < 3; ++j)
				// Beware of the transposition (OpenGL to European math)
				r[i][j] = m[j][i] / m[3][3];
		}
		rot.fromRotationMatrix(r);
	}
	
	/**
	 * This is an overloaded method provided for convenience.
	 * 
	 * @see #fromOpenGLMatrix(float[][])
	 */
	public final void fromPMatrix(PMatrix3D pM) {
		fromOpenGLMatrix(PSUtility.fromPMatrix3DToOpenGL(pM));
	}

	/**
	 * Sets the PSFrame from an OpenGL matrix representation (rotation
	 * in the upper left 3x3 matrix and translation on the last line). 
	 * <p>
	 * Hence, if a code fragment looks like: 
	 * <p> 
	 * {@code float [] m = new float [16]; m[0]=...;} <br>
	 * {@code gl.glMultMatrixf(m);} <br> 
	 * <p> 
	 * It is equivalent to write: 
	 * <p> 
	 * {@code PSFrame fr = new PSFrame();} <br>
	 * {@code fr.setFromMatrix(m);} <br>
	 * {@code applyMatrix(fr.pMatrix());} <br>
	 * <p> 
	 * Using this conversion, you can benefit from the powerful PSFrame
	 * transformation methods to translate points and vectors to and from
	 * the PSFrame coordinate system to any other PSFrame coordinate system
	 * (including the world coordinate system). See {@link #coordinatesOf(PVector)}
	 * and {@link #transformOf(PVector)}. 
	 * <p> 
	 * <b>Attention:</b> A PSFrame does not contain a scale factor. The possible scaling
	 * in {@code m} will not be	converted into the PSFrame by this method.
	 */
	public final void fromOpenGLArray(float[] m) {
		// m should be of size [16]
		float[][] mat = new float[4][4];
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j)
				mat[i][j] = m[i * 4 + j];
		fromOpenGLMatrix(mat);
	}

	/**
	 * Returns a PSFrame representing the inverse of the PSFrame space transformation. 
	 * <p> 
	 * The {@link #rotation()} the new PSFrame is the
	 * {@link proscene.PSQuaternion#inverse()} of the original rotation.
	 * Its {@link #translation()} is the negated inverse rotated image of the original
	 * translation. 
	 * <p> 
	 * If a PSFrame is considered as a space rigid transformation (translation and
	 * rotation), the inverse() PSFrame performs the inverse transformation. 
	 * <p> 
	 * Only the local PSFrame transformation (i.e., defined with respect to the
	 * {@link #referenceFrame()}) is inverted. Use {@link #worldInverse()} for a
	 * global inverse. 
	 * <p> 
	 * The resulting PSFrame has the same  {@link #referenceFrame()} as the PSFrame
	 * and a {@code null} {@link #constraint()}. 
	 * <p> 
	 * <b>Note:</b> The scaling factor of the 4x4 matrix is 1.0.
	 */
	public final PSFrame inverse() {
		PSFrame fr = new PSFrame(PVector.mult(rot.inverseRotate(trans), -1),
				rot.inverse());
		fr.setReferenceFrame(referenceFrame());
		return fr;
	}

    /**
     * 
     * Returns the {@link #inverse()} of the PSFrame world transformation. 
     * <p> 
     * The {@link #orientation()} of the new PSFrame is the
     * {@link proscene.PSQuaternion#inverse()} of the original orientation.
     * Its {@link #position()} is the negated and inverse rotated image of the
     * original position. 
     * <p> 
     * The result PSFrame has a {@code null} {@link #referenceFrame()} and a
     * {@code null} {@link #constraint()}. 
     * <p> 
     * Use {@link #inverse()} for a local (i.e., with respect to {@link #referenceFrame()})
     * transformation inverse.
     */
	public final PSFrame worldInverse() {
		return (new PSFrame( PVector.mult(orientation().inverseRotate(position()), -1), orientation().inverse()) );		
	}
}
