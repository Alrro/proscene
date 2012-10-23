package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Damping extends PApplet {	
	Scene scene;
	InteractiveCameraDampingFrame iCamFrame;
	
	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  /**
	  iCamFrame = new InteractiveCameraDampingFrame(scene.camera());
	  scene.camera().setFrame(iCamFrame);
	  scene.showAll();
	  */
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);	  
	}
	
	public class InteractiveCameraDampingFrame extends InteractiveCameraFrame {
		public InteractiveCameraDampingFrame(Camera cam) {
			super(cam);
		}
		
		/**
		@Override
		public void mouseDragged(Point eventPoint, Camera camera) {
			if (action != Scene.MouseAction.ROTATE)
				super.mouseDragged(eventPoint, camera);
			else {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());
				Quaternion rot = deformedBallQuaternion((int) eventPoint.x,
						(int) eventPoint.y, trans.x, trans.y, camera);				
				
				
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				spin();
				
				prevPos = eventPoint;
			}			
		}
		*/
		
		@Override
		public void mouseReleased(Point event, Camera camera) {
			keepsGrabbingMouse = false;

			if (prevConstraint != null)
				setConstraint(prevConstraint);		

			action = Scene.MouseAction.NO_MOUSE_ACTION;
		}
		
		/**
		 if (camera().anyInterpolationIsStarted())
					camera().stopAllInterpolations();
				Camera cm = camera().clone();
				cm.setPosition(avatar().cameraPosition());
				cm.setUpVector(avatar().upVector());
				cm.lookAt(avatar().target());
				camera().interpolateTo(cm.frame());
				currentCameraProfile = camProfile;
		 */
		
		@Override
		public void spin() {
			//InteractiveCameraFrame fr = this.clone();
			
			//fr.rotateAroundPoint(spinningQuaternion(), arcballReferencePoint());
			
			/**
			float dampAngle = spinningQuaternion().angle() * rotationSensitivity() * 1.2f;
			Quaternion dampQuaternion = new Quaternion(spinningQuaternion().axis(), dampAngle);			
			fr.setOrientation(dampQuaternion);			
			this.camera.interpolateTo(fr);
			
			this.setPosition(fr.position());
			this.setOrientation(fr.orientation());
			// */			
			
			float dampAngle = spinningQuaternion().angle() * rotationSensitivity() * 2f;
			Quaternion dampQuaternion = new Quaternion(spinningQuaternion().axis(), dampAngle);
			rotateAroundPoint(dampQuaternion, arcballReferencePoint());						
		}

	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Damping" });
	}
}
