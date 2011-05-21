package basic;
import processing.core.*;
import remixlab.proscene.*;
import napplet.*;

public class StandardCamera extends PApplet {
	private static final long serialVersionUID = 1L;
	NAppletManager nappletManager;
	NApplet mainNApplet;
	float radians = 0;

	public void setup() {
		size(640, 720, P3D);
		// instantiate the viewers and embed them into a napplet manager
		nappletManager = new NAppletManager(this);
		mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);
		nappletManager.createNApplet("AuxiliarViewer", 0, 360);
	}

	public void mainDrawing(Scene s) {
		PApplet p = s.parent;
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its
		// frustum
		Scene scn = ((MainViewer) (mainNApplet)).scene;
		switch (scn.camera().sphereIsVisible(new PVector(0, 0, 0), 40)) {
		case VISIBLE:
			p.fill(0, 255, 0);
			p.sphere(40);
			break;
		case SEMIVISIBLE:
			p.fill(255, 0, 0);
			p.sphere(40);
			break;
		case INVISIBLE:
			break;
		}
	}

	// same as the main drawing, but we also draw a representation of the main
	// camera
	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);
		s.drawCamera(((MainViewer) (mainNApplet)).scene.camera());
		
		//draw axis:
		PApplet parent = s.parent;
		Frame iFrame = ((MainViewer) (mainNApplet)).scene.camera().frame();
		
		parent.pushMatrix();		
		iFrame.applyTransformation(parent);		
		s.drawAxis(50);
		
		parent.popMatrix();
	}

	public void draw() {
		background(50);
	}

	public class MainViewer extends NApplet {
		private static final long serialVersionUID = 1L;
		Scene scene;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			// 'v' toggles camera kind:
			scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
			// enable computation of the frustum planes equations (disabled by
			// default)
			scene.enableFrustumEquationsUpdate();
			scene.setGridIsDrawn(false);
			// register the drawing method which was defined externally
			scene.addDrawHandler(parentPApplet, "mainDrawing");
			// hack to make middle and right buttons work under napplet
			// see issue report:
			// http://forum.processing.org/#Topic/25080000000041027
			CameraProfile[] camProfiles = scene.getCameraProfiles();
			for (int i = 0; i < camProfiles.length; i++) {
				camProfiles[i].setCameraMouseBinding(
						(Scene.Button.MIDDLE.ID | Scene.Modifier.ALT.ID),
						Scene.MouseAction.ZOOM);
				camProfiles[i].setCameraMouseBinding(
						(Scene.Button.RIGHT.ID | Scene.Modifier.META.ID),
						Scene.MouseAction.TRANSLATE);
				camProfiles[i].setFrameMouseBinding(
						(Scene.Button.MIDDLE.ID | Scene.Modifier.ALT.ID),
						Scene.MouseAction.ZOOM);
				camProfiles[i].setFrameMouseBinding(
						(Scene.Button.RIGHT.ID | Scene.Modifier.META.ID),
						Scene.MouseAction.TRANSLATE);
			}
		}

		public void keyPressed() {
			if (key == 'c') {
				scene.camera().frame().rotate(new Quaternion(new PVector(0,0,1), PApplet.QUARTER_PI));
			}

			if (key == 'w') {
				scene.camera().frame().rotate(new Quaternion(scene.camera().frame().transformOf(new PVector(0,0,1)), PApplet.QUARTER_PI / 16));
			}
			
			if (key == 't') {
				radians += PApplet.QUARTER_PI / 16;
				scene.camera().setOrientation(new Quaternion(new PVector(1,1,1),
					    /**
						0 // */
						/** 
						PApplet.QUARTER_PI // */
						// /**
						radians // */
						));
				//PApplet.println("Rotating around camera z axis in world which is: " + scene.camera().frame().zAxis() + " radians: " + radians);
			}

			if ((key == 'u') && (scene.camera().kind() == Camera.Kind.STANDARD))
				scene.camera().changeStandardOrthoFrustumSize(true);
			if ((key == 'U') && (scene.camera().kind() == Camera.Kind.STANDARD))
				scene.camera().changeStandardOrthoFrustumSize(false);
		}
	}

	public class AuxiliarViewer extends NApplet {
		private static final long serialVersionUID = 1L;
		Scene scene;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
			//scene.setAxisIsDrawn(false);
			//scene.setGridIsDrawn(false);
			scene.setRadius(200);
			scene.showAll();
			// register the drawing method which was defined externally
			scene.addDrawHandler(parentPApplet, "auxiliarDrawing");
			// hack to make middle and right buttons work under napplet
			// see issue report:
			// http://forum.processing.org/#Topic/25080000000041027
			CameraProfile[] camProfiles = scene.getCameraProfiles();
			for (int i = 0; i < camProfiles.length; i++) {
				camProfiles[i].setCameraMouseBinding(
						(Scene.Button.MIDDLE.ID | Scene.Modifier.ALT.ID),
						Scene.MouseAction.ZOOM);
				camProfiles[i].setCameraMouseBinding(
						(Scene.Button.RIGHT.ID | Scene.Modifier.META.ID),
						Scene.MouseAction.TRANSLATE);
				camProfiles[i].setFrameMouseBinding(
						(Scene.Button.MIDDLE.ID | Scene.Modifier.ALT.ID),
						Scene.MouseAction.ZOOM);
				camProfiles[i].setFrameMouseBinding(
						(Scene.Button.RIGHT.ID | Scene.Modifier.META.ID),
						Scene.MouseAction.TRANSLATE);
			}
		}
	}
}
