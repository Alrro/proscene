package proscene;

import processing.core.*;

/** 
 * An abstract class for PSFrame PSConstraints defined by an axis or a plane.
 * <p> 
 * PSAxisPlaneConstraint is an interface for (translation and/or rotation)
 * PSConstraint that are defined by a direction. {@link #translationConstraintType()}
 * and {@link #rotationConstraintType()} define how this direction should be
 * interpreted: as an axis or as a plane normal. 
 * <p> 
 * The three implementations of this class: PSLocalConstraint, PSWorldConstraint and
 * PSCameraConstraint differ by the coordinate system in which this direction is expressed.
 */

public class PSAxisPlaneConstraint extends PSConstraint {

	/**
	 * Type lists the different types of translation and rotation constraints that are available. 
	 * <p> 
	 * It specifies the meaning of the constraint direction (see
	 * {@link #translationConstraintDirection()} and {@link #rotationConstraintDirection()}):
	 * as an axis direction or a plane normal. {@link Type#FREE} means no constraint while
	 * {@link Type#FORBIDDEN} completely forbids the translation and/or the rotation. 
	 * <b>Attention: </b> The {@link Type#PLANE} Type is not valid for rotational
	 * constraint. 
	 * <p> 
	 * New derived classes can use their own extended {@code enum} for specific constraints.
    */
	public enum Type {
		FREE, AXIS, PLANE, FORBIDDEN
	};

	private Type transConstraintType;
	private Type rotConstraintType;
	private PVector transConstraintDir;
	private PVector rotConstraintDir;

	/**
	 * 
	 * Default constructor. 
	 * <p> 
	 * {@link #translationConstraintType()} and {@link #rotationConstraintType()}
	 * are set to {@link Type#FREE}. {@link #translationConstraintDirection()} and
	 * {@link #rotationConstraintDirection()} are set to (0,0,0).
	 */
	public PSAxisPlaneConstraint() {
		// Do not use set since setRotationConstraintType needs a read.
		this.transConstraintType = PSAxisPlaneConstraint.Type.FREE;
		this.rotConstraintType = PSAxisPlaneConstraint.Type.FREE;
		transConstraintDir = new PVector(0.0f, 0.0f, 0.0f);
		rotConstraintDir = new PVector(0.0f, 0.0f, 0.0f);
	}

	/**
	 * Returns the translation constraint Type. 
	 * <p> 
	 * Depending on this value, the PSFrame will freely translate ({@link Type#FREE}),
	 * will only be able to translate along an axis direction ({@link Type#AXIS}), will be
	 * forced to stay into a plane ({@link Type#PLANE}) or will not able to translate at
	 * all ({@link Type#FORBIDDEN}). 
	 * <p> 
	 * Use {@link proscene.PSFrame#setPosition(PVector)} to define the
	 * position of the constrained PSFrame before it gets constrained.
	 */
	public Type translationConstraintType() {
		return transConstraintType;
	}

	/**
	 * 
	 * Returns the direction used by the translation constraint. 
	 * <p> 
	 * It represents the axis direction ({@link Type#AXIS}) or the plane normal
	 * ({@link Type#PLANE}) depending on the {@link #translationConstraintType()}.
	 * It is undefined for ({@link Type#FREE}) or ({@link Type#FORBIDDEN}). 
	 * <p> 
	 * The PSAxisPlaneConstraint derived classes express this direction in different
	 * coordinate system (camera for PSCameraConstraint, local for PSLocalConstraint,
	 * and world for PSWorldConstraint). This value can be modified with
	 * {@link #setRotationConstraintDirection(PVector)}.
	 */
	public PVector translationConstraintDirection() {
		return transConstraintDir;
	}
	
	/**
	 * Returns the rotation constraint Type.
	 */
	public Type rotationConstraintType() {
		return rotConstraintType;
	}

    /**
     * Returns the axis direction used by the rotation constraint. 
     * <p> 
     * This direction is defined only when {@link #rotationConstraintType()}
     * is {@link Type#AXIS}. 
     * <p> 
     * The PSAxisPlaneConstraint derived classes express this direction in different
     * coordinate system (camera for PSCameraConstraint, local for PSLocalConstraint,
     * and world for PSWorldConstraint). This value can be modified
     * with {@link #setRotationConstraintDirection(PVector)}.
     */
	public PVector rotationConstraintDirection() {
		return rotConstraintDir;
	}

	/**
	 * Simply calls {@link #setTranslationConstraintType(Type)}
	 * and {@link #setTranslationConstraintDirection(PVector)}.
	 */
	public void setTranslationConstraint(Type type, PVector direction) {
		setTranslationConstraintType(type);
		setTranslationConstraintDirection(direction);
	}

	/**
	 * Defines the {@link #translationConstraintDirection()}. The coordinate system
	 * where {@code direction} is expressed depends on your class implementation.
	 */
	public void setTranslationConstraintDirection(PVector direction) {
		if ((translationConstraintType() != PSAxisPlaneConstraint.Type.FREE)
				&& (translationConstraintType() != PSAxisPlaneConstraint.Type.FORBIDDEN)) {
			float norm = direction.mag();
			if (norm < 1E-8) {
				//TODO Warning("PSAxisPlaneConstraint.setTranslationConstraintDir: null vector for translation constraint");
				transConstraintType = PSAxisPlaneConstraint.Type.FREE;
			} else
				transConstraintDir = PVector.mult(direction, (1.0f / norm));
		}
	}

	/**
	 * Simply calls {@link #setRotationConstraintType(Type)} 
	 * and {@link #setRotationConstraintDirection(PVector)}.
	 */
	public void setRotationConstraint(Type type, PVector direction) {
		setRotationConstraintType(type);
		setRotationConstraintDirection(direction);
	}

	/**
	 * Defines the {@link #rotationConstraintDirection()}. The coordinate system where
	 * {@code direction} is expressed depends on your class implementation.
	 */
	public void setRotationConstraintDirection(PVector direction) {
		if ((rotationConstraintType() != PSAxisPlaneConstraint.Type.FREE)
				&& (rotationConstraintType() != PSAxisPlaneConstraint.Type.FORBIDDEN)) {
			float norm = direction.mag();
			if (norm < 1E-8) {
				//TODO Warning("PSAxisPlaneConstraint.setRotationConstraintDir: null vector for rotation constraint");
				rotConstraintType = PSAxisPlaneConstraint.Type.FREE;
			} else
				rotConstraintDir = PVector.mult(direction, (1.0f / norm));
		}
	}

	/**
	 * Sets the Type() of the {@link #translationConstraintType()}. Default is
	 * {@link Type#FREE}
	 */
	public void setTranslationConstraintType(Type type) {
		transConstraintType = type;
	}

	/**
	 * Set the Type of the {@link #rotationConstraintType()}. Default is {@link Type#FREE}. 
	 * <p> 
	 * Depending on this value, the PSFrame will freely rotate ({@link Type#FREE}), will
	 * only be able to rotate around an axis ({@link Type#AXIS}), or will not able to rotate
	 * at all {@link Type#FORBIDDEN}. 
	 * <p> 
	 * Use {@link proscene.PSFrame#setOrientation(PSQuaternion)} to define the
	 * orientation of the constrained PSFrame before it gets constrained. 
	 * <p> 
	 * <b>Attention:</b> An {@link Type#PLANE} Type is not meaningful for rotational
	 * constraints and will be ignored.
	 */
	public void setRotationConstraintType(Type type) {
		// TODO Buggy? if (type == PSAxisPlaneConstraint.Type.PLANE)
		if (rotationConstraintType() == PSAxisPlaneConstraint.Type.PLANE) {
			//TODO Warning("PSAxisPlaneConstraint.setRotationConstraintType: the PLANE type cannot be used for a rotation constraints");
			return;
		}

		rotConstraintType = type;
	}
}
