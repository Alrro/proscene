package basic;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	InteractiveAvatarFrame iFrame;
	
	public void setup()	{
		size(640, 360, P3D);		
		scene = new Scene(this);
		scene.setShortcut('q', Scene.KeyboardAction.CAMERA_KIND);
		//scene.setInteractiveFrame(new InteractiveFrameTesting(scene));
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		//scene.camera().setKind(Camera.Kind.STANDARD);
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.interactiveFrame().translate(new Vector3D(30, 30, 0));		
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);		
		//Register a CAD Camera profile and name it "CAD_CAM"
		scene.registerCameraProfile(new CameraProfile(scene, "CAD_CAM", CameraProfile.Mode.CAD));
		//Set the CAD_CAM as the current camera profile
		//scene.setCurrentCameraProfile("CAD_CAM");
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);		
		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}	
		
		popMatrix();
	}
	
	public void keyPressed() {
		if(key == 'x') scene.interactiveFrame().scale(-1, 1, 1);
		//if(key == 'X') scene.camera().frame().scale(-1, 1, 1);
		if(key == 'y') scene.interactiveFrame().scale(1, -1, 1);
		//if(key == 'Y') scene.camera().frame().scale(1, -1, 1);
		if(key == 'z') scene.interactiveFrame().scale(1, 1, -1);
		//if(key == 'Z') scene.camera().frame().scale(1, 1, -1);
		
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}			
		
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");		
		if(scene.interactiveFrame().isInverted())
			println("scene.interactiveFrame() is inverted");
		else
			println("scene.interactiveFrame() is NOT inverted");
		
		/**
		if(scene.camera().frame().isInverted())
			println("scene.camera().frame() is inverted");
		else
			println("scene.camera().frame() is NOT inverted");
		*/
		
		if( key == 'u' || key == 'U') {
			print("cam pos: ");
			scene.camera().position().print();
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FrameInteraction" });
	}
	
	public class InteractiveFrameTesting extends InteractiveFrame {
		public InteractiveFrameTesting(AbstractScene scn) {
			super(scn);
		}
		
		@Override
		protected Quaternion deformedBallQuaternion(int x, int y, float cx, float cy,	Camera camera) {
			// Points on the deformed ball
			float px, py, dx, dy;
			/**
			px = rotationSensitivity() * ((int)prevPos.x - cx) / camera.screenWidth();
			if( scene.isLeftHanded() )
				py = rotationSensitivity() * ((int)prevPos.y - cy) / camera.screenHeight();
			else
				py = rotationSensitivity() * (cy - (int)prevPos.y) / camera.screenHeight();
			dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
			if( scene.isLeftHanded() )
				dy = rotationSensitivity() * (y - cy) / camera.screenHeight();
			else
				dy = rotationSensitivity() * (cy - y) / camera.screenHeight();
			*/
			px = rotationSensitivity() * ((int)prevPos.x - cx) / camera.screenWidth();
			py = rotationSensitivity() * ((int)prevPos.y - cy) / camera.screenHeight();
			dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
			dy = rotationSensitivity() * (y - cy) / camera.screenHeight();
			
			/**
			if ( (scene.isRightHanded() && this.magnitude().y() > 0) ) {
				py = -py;
				dy = -dy;
			}
			
			if ( ( this.magnitude().y() < 0) ) {				
				py = -py;
				dy = -dy;
				
				if(scene.isLeftHanded()) {
					px = -px;
					dx = -dx;
				}
			}*/
			
			if ( scene.isRightHanded() ) {
				py = -py;
				dy = -dy;
			}
			
			/**
			if ( this.magnitude().x() < 0 ) {
				px = -px;
				dx = -dx;
			} */					

			Vector3D p1 = new Vector3D(px, py, projectOnBall(px, py));
			Vector3D p2 = new Vector3D(dx, dy, projectOnBall(dx, dy));
			// Approximation of rotation angle
			// Should be divided by the projectOnBall size, but it is 1.0
			Vector3D axis = p2.cross(p1);
			float angle = 2.0f * (float) Math.asin((float) Math.sqrt(axis.squaredNorm() / p1.squaredNorm() / p2.squaredNorm()));			
	 		
			/**
		  //left-handed coordinate system correction
			if( scene.isLeftHanded() ) {
				axis.vec[1] = -axis.vec[1];
				angle = -angle;
			}
			*/

			return new Quaternion(axis, angle);
		}
		
	}
}
