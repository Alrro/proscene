package crane;

import processing.core.*;
import remixlab.proscene.*;

public class RobotArm {
	Scene scene;
	//camera located at the robot's head
	Camera cam;
	InteractiveFrame[] frameArray;

	RobotArm(Scene mainScn) {
		scene = mainScn;
		// the instantiated cam is detached from the scene meaning
		// that its matrices are independent from those of processing
		cam = new Camera(scene, false);
		frameArray = new InteractiveFrame[4];
		for (int i = 0; i < 4; ++i) {
			// last frame should be a camera frame:
			if(i == 3) {				
				frameArray[i] = new InteractiveCameraFrame(scene);
				// ... so we set it as the cam frame
				cam.setFrame((InteractiveCameraFrame)frameArray[i]);
			}
			else
				frameArray[i] = new InteractiveFrame(scene);
			// Creates a hierarchy of frames
			if (i > 0)
				frame(i).setReferenceFrame(frame(i - 1));
		}	

		// Initialize frames
		frame(1).setTranslation(0, 0, 8); // Base height
		frame(2).setTranslation(0, 0, 50); // Arm length
		frame(3).setTranslation(0, 0, 50); // Arm length
		frame(1).setRotation(new Quaternion(new PVector(1.0f, 0.0f, 0.0f), 0.6f));
		frame(2).setRotation(new Quaternion(new PVector(1.0f, 0.0f, 0.0f), -2.0f));		
		frame(3).setRotation(new Quaternion(new PVector(1.0f, -0.3f, 0.0f), 1.7f));

		// Set frame constraints
		WorldConstraint baseConstraint = new WorldConstraint();
		baseConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE,	new PVector(0.0f, 0.0f, 1.0f));
		baseConstraint.setRotationConstraint(AxisPlaneConstraint.Type.AXIS,	new PVector(0.0f, 0.0f, 1.0f));
		frame(0).setConstraint(baseConstraint);

		LocalConstraint XAxis = new LocalConstraint();
		XAxis.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
		XAxis.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(1.0f, 0.0f, 0.0f));
		frame(1).setConstraint(XAxis);
		frame(2).setConstraint(XAxis);

		LocalConstraint headConstraint = new LocalConstraint();
		headConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f,	0.0f));
		frame(3).setConstraint(headConstraint);
	}

	public void draw(Scene scn) {
		// Robot arm's local frame
		PGraphics3D pg3d = scn.renderer();	
		
		pg3d.pushMatrix();
		frame(0).applyTransformation(pg3d);
		setColor(scn, frame(0).equals(scene.interactiveFrame()) );
		drawBase(scn);

		pg3d.pushMatrix();
		frame(1).applyTransformation(pg3d);
		setColor(scn, frame(1).equals(scene.interactiveFrame()) );
		drawCylinder(scn);
		drawArm(scn);

		pg3d.pushMatrix();
		frame(2).applyTransformation(pg3d);
		setColor( scn, frame(2).equals(scene.interactiveFrame()) );
		drawCylinder(scn);
		drawArm(scn);

		pg3d.pushMatrix();
		frame(3).applyTransformation(pg3d);
		setColor( scn, frame(3).equals(scene.interactiveFrame()) );
		drawHead(scn);

		// Add light if the flag enables it
		if( ( (CameraCrane) scene.parent).enabledLights )
			pg3d.spotLight(155, 255, 255, 0, 0, 0, 0, 0, -1, PApplet.THIRD_PI, 1);

		pg3d.popMatrix();// frame(3)

		pg3d.popMatrix();// frame(2)

		pg3d.popMatrix();// frame(1)

		// totally necessary
		pg3d.popMatrix();// frame(0)
		
		// Scene.drawCamera takes into account the whole scene hierarchy above
		// the camera iFrame. Thus, we call it after restoring the gl state.
		// Calling it before the first push matrix above, should do it too.
		if( ( (CameraCrane)scene.parent).drawRobotCamFrustum && scn.equals( scene) )
			scn.drawCamera(cam);
	}

	public void drawBase(Scene scn) {
		drawCone(scn, 0, 3, 15, 15, 30);
		drawCone(scn, 3, 5, 15, 13, 30);
		drawCone(scn, 5, 7, 13, 1, 30);
		drawCone(scn, 7, 9, 1, 1, 10);
	}

	public void drawArm(Scene scn) {
		PGraphics3D pg3d = scn.renderer();
		pg3d.translate(2, 0, 0);
		drawCone(scn, 0, 50, 1, 1, 10);
		pg3d.translate(-4, 0, 0);
		drawCone(scn, 0, 50, 1, 1, 10);
		pg3d.translate(2, 0, 0);
	}

	public void drawHead(Scene scn) {
		if( ( (CameraCrane)scene.parent).drawRobotCamFrustum && scn.equals( scene) )
			scn.drawAxis( cam.sceneRadius() * 1.2f );	
		drawCone(scn, 2, -6, 4, 4, 30);
		drawCone(scn, -6, -15, 4, 17, 30);
		drawCone(scn, -15, -17, 17, 17, 30);
	}

	public void drawCylinder(Scene scn) {
		PGraphics3D pg3d = scn.renderer();
		pg3d.pushMatrix();
		pg3d.rotate(PApplet.HALF_PI, 0, 1, 0);
		drawCone(scn, -5, 5, 2, 2, 20);
		pg3d.popMatrix();
	}

	public void drawCone(Scene scn, float zMin, float zMax, float r1, float r2, int nbSub) {
		PGraphics3D pg3d = scn.renderer();
		pg3d.translate(0.0f, 0.0f, zMin);
		scn.cone(nbSub, 0, 0, r1, r2, zMax - zMin);
		pg3d.translate(0.0f, 0.0f, -zMin);
	}

	public void setColor(Scene scn, boolean selected) {
		PGraphics3D pg3d = scn.renderer();
		if (selected)
			pg3d.fill(200, 200, 0);
		else
			pg3d.fill(200, 200, 200);
	}
	
	// set the scene.interactiveFrame() to the next frame in the robot scene hierarchy
	public void nextIFrame() {
		if( scene.interactiveFrame() == null )
			scene.setInteractiveFrame(frame(0));
		else  {
			for (int i = 0; i < 4; ++i) {
				if( frame(i).equals(scene.interactiveFrame()) ) {
					if(i==3)
						scene.setInteractiveFrame(frame(0));
					else
						scene.setInteractiveFrame(frame(i+1));
					break;
				}
			}
		}
	}

	public InteractiveFrame frame(int i) {
		return frameArray[i];
	}
}