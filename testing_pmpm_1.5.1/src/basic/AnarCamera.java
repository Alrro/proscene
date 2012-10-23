package basic;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class AnarCamera extends PApplet {
	Scene scene;
	String warning;
	PFont myFont;
	ANARinteractiveFrame aFrame;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		aFrame = new ANARinteractiveFrame(scene.camera());
		scene.camera().setFrame(aFrame);
		scene.showAll();
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 50);
	}

	class ANARinteractiveFrame extends InteractiveCameraFrame {

		public ANARinteractiveFrame(remixlab.proscene.Camera cam) {
			super(cam);
		}

		public remixlab.proscene.Camera camera() {
			return camera;
		}

		@Override
		protected Quaternion deformedBallQuaternion(int x, int y, float cx,
				float cy, remixlab.proscene.Camera camera) {
			// Points on the deformed ball
			float px = rotationSensitivity() * ((int) prevPos.x - cx)
					/ camera.screenWidth();
			float py = rotationSensitivity() * (cy - (int) prevPos.y)
					/ camera.screenHeight();
			float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
			float dy = rotationSensitivity() * (cy - y) / camera.screenHeight();

			PVector axisX = camera.frame()
					.transformOf(
							camera().frame().inverseTransformOf(
									(new PVector(1, 0, 0))));
			PVector axisZ = camera.frame().transformOf(new PVector(0, 0, 1));

			float angleZ = rotationSensitivity() * (dx - px);
			float angleX = rotationSensitivity() * (dy - py);

			// left-handed coordinate system correction
			//if (scene.isLeftHanded())
				angleX = -angleX;			

			Quaternion quatZ = new Quaternion(axisZ, angleZ);
			Quaternion quatX = new Quaternion(axisX, angleX);

			return Quaternion.multiply(quatZ, quatX);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.AnarCamera" });
	}
}
