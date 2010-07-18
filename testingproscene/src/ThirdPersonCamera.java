import processing.core.*;
import remixlab.proscene.*;
import napplet.*;

public class ThirdPersonCamera extends PApplet {
	NAppletManager nappletManager;
	NApplet mainNApplet;

	public void setup() {
		size(640, 720, P3D);
		// instantiate the viewers and embed them into a napplet manager
		nappletManager = new NAppletManager(this);
		mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);
		nappletManager.createNApplet("AuxiliarViewer", 0, 360);
	}

	public void draw() {
		background(50);
	}

	public class MainViewer extends NApplet {
		Scene scene;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			// enable computation of the frustum planes equations (disabled by
			// default)
			scene.enableFrustumEquationsUpdate();
			scene.setHelpIsDrawn(false);
			scene.setGridIsDrawn(false);
		}

		// We need to pass the scene to the auxiliar viewer
		Scene getScene() {
			return scene;
		}

		public void draw() {
			noStroke();
			if (scene.camera().sphereIsVisible(new PVector(0, 0, 0), 40) == Camera.Visibility.SEMIVISIBLE)
				fill(255, 0, 0);
			else
				fill(0, 255, 0);
			sphere(40);
		}

		public void keyPressed() {
			if (key == 'v')
				scene.toggleCameraKind();
			if ((key == 'u') && (scene.camera().kind() == Camera.Kind.STANDARD))
				scene.camera().changeStandardOrthoFrustumSize(true);
			if ((key == 'U') && (scene.camera().kind() == Camera.Kind.STANDARD))
				scene.camera().changeStandardOrthoFrustumSize(false);
		}
	}

	public class AuxiliarViewer extends NApplet {
		Scene scene;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
			scene.setAxisIsDrawn(false);
			scene.setGridIsDrawn(false);
			scene.setHelpIsDrawn(false);
			scene.setRadius(200);
			scene.showAll();
		}

		public void draw() {
			noStroke();
			if (((MainViewer) mainNApplet).getScene().camera().sphereIsVisible(
					new PVector(0, 0, 0), 40) == Camera.Visibility.SEMIVISIBLE)
				fill(255, 0, 0);
			else
				fill(0, 255, 0);
			sphere(40);
			DrawingUtils.drawCamera(this, ((MainViewer) mainNApplet).getScene()
					.camera());
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ThirdPersoncamera" });
	}
}
