/**
 * @author Ivan Dario Chinome
 * @author David Montañez Guerrero
 */

package cameracrane;

import processing.core.*;
import remixlab.proscene.*;
import saito.objloader.*;

@SuppressWarnings("serial")
public class CameraCrane extends PApplet {
	boolean enabledLights = true;
	boolean drawRobotCamFrustum = false;
	RobotArm robot;
	HeliCam heli;
	Scene scene, auxScene, auxScene1, auxScene2;
	PGraphics canvas, auxCanvas, auxCanvas1, auxCanvas2;
	int mainWinHeight = 400; // should be less than the papplet height
	OBJModel ahstray = new OBJModel(this, "cameracrane/Models/ahstray.obj",	"absolute", TRIANGLES);

	public void setup() {
		size(1024, 720, P3D); // size(640, 720, P3D)
		canvas = createGraphics(width, mainWinHeight, P3D);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.setGridIsDrawn(false);
		// the drawing function is shared among the two scenes
		scene.addDrawHandler(this, "drawing");
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

		auxCanvas = createGraphics(width / 2, (height - canvas.height), P3D);// (width,
																				// (height
																				// -
																				// canvas.height),
																				// P3D)
		auxScene = new Scene(this, (PGraphics3D) auxCanvas);
		auxScene.setRadius(50);
		auxScene.setGridIsDrawn(false);
		// same drawing function which is defined below
		auxScene.addDrawHandler(this, "drawing");

		auxCanvas1 = createGraphics(width / 2, (height - canvas.height), P3D);// (width,
																				// (height
																				// -
																				// canvas.height),
																				// P3D)
		auxScene1 = new Scene(this, (PGraphics3D) auxCanvas1);
		auxScene1.setRadius(50);
		auxScene1.setGridIsDrawn(false);
		// same drawing function which is defined below
		auxScene1.addDrawHandler(this, "drawing");
		ahstray.scale(3);

		// robot stuff
		robot = new RobotArm(scene, 60, -60, 2);
		auxScene.setCamera(robot.cam);

		heli = new HeliCam(scene);
		auxScene1.setCamera(heli.cam);
	}

	// off-screen rendering
	// don't edit this unless you know what you're doing!

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		// the actual scene drawing (defined by the "drawing" function below)
		// is magically called by the draw handler
		scene.beginDraw();
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		// same here with the auxScene
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		image(auxCanvas, 0, canvas.height);

		auxCanvas1.beginDraw();
		// same here with the auxScene1
		auxScene1.beginDraw();
		auxScene1.endDraw();
		auxCanvas1.endDraw();
		image(auxCanvas1, canvas.width / 2, canvas.height);
		auxCanvas1.beginDraw();
	}

	public void handleMouse() {
		if (mouseY < canvas.height) {
			scene.enableMouseHandling();
			scene.enableKeyboardHandling();
			auxScene.disableMouseHandling();
			auxScene.disableKeyboardHandling();
			auxScene1.disableMouseHandling();
			auxScene1.disableKeyboardHandling();
		} else {
			if (mouseX < canvas.width / 2) {
				scene.disableMouseHandling();
				scene.disableKeyboardHandling();
				auxScene.enableMouseHandling();
				auxScene.enableKeyboardHandling();
				auxScene1.disableMouseHandling();
				auxScene1.disableKeyboardHandling();
			} else {
				scene.disableMouseHandling();
				scene.disableKeyboardHandling();
				auxScene.disableMouseHandling();
				auxScene.disableKeyboardHandling();
				auxScene1.enableMouseHandling();
				auxScene1.enableKeyboardHandling();
			}
		}
	}

	// the actual drawing function, shared by the two scenes
	public void drawing(Scene scn) {
		PGraphics3D pg3d = scn.renderer();
		pg3d.background(0);
		if (enabledLights) {
			pg3d.lights();
		}
		// 1. draw the robot cams

		robot.draw(scn);
		heli.draw(scn);

		// 2. draw the scene

		// The OBJ is drawn earning points that form part of each side of the
		// model.
		// Then, sides are drawn using beginShape and endShape.

		pg3d.noStroke();
		pg3d.fill(24, 184, 199);
		pg3d.pushMatrix();
		pg3d.rotateX(-HALF_PI);
		for (int k = 0; k < ahstray.getFaceCount(); k++) {
			PVector[] faceVertices = ahstray.getFaceVertices(k);
			pg3d.beginShape(TRIANGLE_FAN);
			for (int i = 0; i < faceVertices.length; i++) {
				pg3d.vertex(faceVertices[i].x, faceVertices[i].y,
						faceVertices[i].z);
			}
			pg3d.endShape();
		}
		pg3d.popMatrix();

		// 2a. draw a ground
		pg3d.noStroke();
		pg3d.fill(120, 120, 120);
		float nbPatches = 100;
		pg3d.normal(0.0f, 0.0f, 1.0f);
		for (int j = 0; j < nbPatches; ++j) {
			pg3d.beginShape(QUAD_STRIP);
			for (int i = 0; i <= nbPatches; ++i) {
				pg3d.vertex((200 * (float) i / nbPatches - 100), (200 * j
						/ nbPatches - 100));
				pg3d.vertex((200 * (float) i / nbPatches - 100), (200
						* (float) (j + 1) / nbPatches - 100));
			}
			pg3d.endShape();
		}

	}

	public void keyPressed() {
		if (key == 'l') {
			enabledLights = !enabledLights;
			if (enabledLights) {
				println("camera spot light enabled");
			} else {
				println("camera spot light disabled");
			}
		}
		if (key == 'x') {
			drawRobotCamFrustum = !drawRobotCamFrustum;
			if (drawRobotCamFrustum) {
				println("draw robot camera frustum");
			} else {
				println("don't draw robot camera frustum");
			}
		}
	}

	public void drawModel(Scene scn) {
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "crane.CameraCrane" });
	}
}
