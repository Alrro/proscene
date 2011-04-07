package remixlab.proscene;

import processing.core.PVector;

public abstract class SixDOFDevice {
	protected Scene scene;
	protected Camera camera;
	protected InteractiveCameraFrame cameraFrame;
	protected InteractiveFrame iFrame;

	protected PVector rotation, rotSens;
	protected PVector translation, transSens;

	Quaternion quaternion;

	public SixDOFDevice(Scene scn) {
		scene = scn;
		camera = scene.camera();
		cameraFrame = camera.frame();
		iFrame = scene.interactiveFrame();
		rotation = new PVector();
		rotSens = new PVector(1, 1, 1);
		translation = new PVector();
		transSens = new PVector(1, 1, 1);
		quaternion = new Quaternion();
	}

	protected void handleDevice() {
		PVector trans = new PVector();
	  Quaternion quat = new Quaternion();
	  
	  /**
		feedXTranslation(feedXTranslation());
		feedYTranslation(feedYTranslation());
		feedZTranslation(feedZTranslation());
		feedXRotation(feedXRotation());
		feedYRotation(feedYRotation());
		feedZRotation(feedZRotation());
		*/

		if (scene.interactiveFrameIsDrawn()) {			
			// InteractiveFrame iFrame = scene.interactiveFrame();

			// 1.1. Translate the iFrame
			//trans.set(sliderXpos.getValue(), sliderYpos.getValue(), -sliderZpos.getValue());
			trans.set(feedXTranslation(), feedYTranslation(), -feedZTranslation());
			// Transform to world coordinate system
			// trans = scene.camera().frame().orientation().rotate(PVector.mult(trans,
			// iFrame.translationSensitivity()));
			trans = cameraFrame.orientation().rotate(trans);
			// And then down to frame
			if (iFrame.referenceFrame() != null)
				trans = iFrame.referenceFrame().transformOf(trans);
			iFrame.translate(trans);

			// 1.2. Rotate the iFrame
			trans = scene.camera().projectedCoordinatesOf(iFrame.position());
			//quat.fromEulerAngles(sliderXrot.getValue(), sliderYrot.getValue(), -sliderZrot.getValue());
			quat.fromEulerAngles(feedXRotation(), feedYRotation(), -feedZRotation());
			trans.set(-quat.x, -quat.y, -quat.z);
			trans = cameraFrame.orientation().rotate(trans);
			trans = iFrame.transformOf(trans);
			quat.x = trans.x;
			quat.y = trans.y;
			quat.z = trans.z;
			iFrame.rotate(quat);
		}

		// 2. Otherwise translate/rotate the camera:
		else {
			// Translate
			//trans.set(sliderXpos.getValue(), sliderYpos.getValue(), -sliderZpos.getValue());
			trans.set(feedXTranslation(), feedYTranslation(), -feedZTranslation());
			cameraFrame.translate(cameraFrame.localInverseTransformOf(trans));
			// Rotate
			//quat.fromEulerAngles(-sliderXrot.getValue(), -sliderYrot.getValue(), sliderZrot.getValue());
			quat.fromEulerAngles(-feedXRotation(), -feedYRotation(), feedZRotation());
			cameraFrame.rotate(quat);

			// Google earth navigation emulation

			/**
			 * trans = PVector.mult(cameraFrame.position(), -sliderZpos.getValue());
			 * cameraFrame.translate(trans);
			 * 
			 * quat.fromEulerAngles(-sliderYpos.getValue(), sliderXpos.getValue(), 0);
			 * cameraFrame.rotateAroundPoint(q,
			 * scene.camera().arcballReferencePoint());
			 * 
			 * quat.fromEulerAngles(0, 0, sliderZrot.getValue());
			 * cameraFrame.rotateAroundPoint(q,
			 * scene.camera().arcballReferencePoint());
			 * 
			 * quat.fromEulerAngles(-sliderXrot.getValue(), 0, 0);
			 * cameraFrame.rotate(q);
			 */
		}
	}

	public abstract float feedXTranslation();

	public abstract float feedYTranslation();

	public abstract float feedZTranslation();

	public abstract float feedXRotation();

	public abstract float feedYRotation();

	public abstract float feedZRotation();

	public void feed(PVector t, PVector r) {
		translation.set(t);
		rotation.set(r);
	}

	public void feedTranslation(PVector t) {
		translation.set(t);
	}

	public void feedXTranslation(float t) {
		translation.x = t;
	}

	public void feedYTranslation(float t) {
		translation.y = t;
	}

	public void feedZTranslation(float t) {
		translation.z = t;
	}

	public void feedRotation(PVector r) {
		rotation.set(r);
	}

	public void feedXRotation(float t) {
		rotation.x = t;
	}

	public void feedYRotation(float t) {
		rotation.y = t;
	}

	public void feedZRotation(float t) {
		rotation.z = t;
	}

	public void setTranslationSensitivity(PVector t) {
		transSens.set(t);
	}

	public void setXTranslationSensitivity(float sensitivity) {
		transSens.x = sensitivity;
	}

	public void setYTranslationSensitivity(float sensitivity) {
		transSens.y = sensitivity;
	}

	public void setZTranslationSensitivity(float sensitivity) {
		transSens.z = sensitivity;
	}

	public void setRotationSensitivity(PVector r) {
		rotSens.set(r);
	}

	public void setXRotationSensitivity(float sensitivity) {
		rotSens.x = sensitivity;
	}

	public void setYRotationSensitivity(float sensitivity) {
		rotSens.y = sensitivity;
	}

	public void setZRotationSensitivity(float sensitivity) {
		rotSens.z = sensitivity;
	}
}
