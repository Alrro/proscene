package proscene;

import processing.core.*;

/**
 * An interface class for Frame constraints. 
 * <p> 
 * This class defines the interface for the PSConstraints that can be applied
 * to a PSFrame to limit its motion. Use
 * {@link proscene.PSFrame#setConstraint(PSConstraint)} to
 * associate a PSConstraint to a PSFrame (default is a {@code null}
 * {@link proscene.PSFrame#constraint()}.
 */
public class PSConstraint {
	/**
	 * Filters the translation applied to the PSframe. This default implementation
	 * is empty (no filtering). 
	 * <p> 
	 * Overload this method in your own PSConstraint class to define a new 
	 * translation constraint. {@code frame} is the PSFrame to which is applied the
	 * translation. Use its {@link proscene.PSFrame#position()} and
	 * update the translation accordingly instead. 
	 * <p> 
	 * {@code translation} is expressed in the local PSFrame coordinate system.
	 * Use {@link proscene.PSFrame#inverseTransformOf(PVector)} to 
	 * express it in the world coordinate system if needed. 
	 */
    public PVector constrainTranslation(PVector translation, PSFrame frame) {
    	return new PVector(translation.x, translation.y, translation.z);
    }
	
    /**
     * Filters the rotation applied to the {@code frame}. This default implementation
     * is empty (no filtering). 
     * <p> 
     * Overload this method in your own PSConstraint class to define a new rotation
     * constraint. See {@link #constrainTranslation(PVector, PSFrame)} for details. 
     * <p> 
     * Use {@link proscene.PSFrame#inverseTransformOf(PVector)} on the
     * {@code rotation} {@link proscene.PSQuaternion#axis()} to express
     * {@code rotation} in the world coordinate system if needed.
     */
	public PSQuaternion constrainRotation(PSQuaternion rotation, PSFrame frame) {
		return new PSQuaternion(rotation);
	}
}
