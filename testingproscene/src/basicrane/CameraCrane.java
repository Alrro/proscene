package basicrane;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class CameraCrane extends PApplet {
	boolean enabledLights = true;
	boolean drawRobotCamFrustum = false;
	RobotArm robot;
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	int mainWinHeight = 400; // should be less than the papplet height

	public void setup() {
		size(640, 720, P3D);
		canvas = createGraphics(width, mainWinHeight, P3D);
		scene = new Scene(this, (PGraphics3D) canvas);
		scene.setGridIsDrawn(false);
		// the drawing function is shared among the two scenes
		scene.addDrawHandler(this, "drawing");
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		auxCanvas = createGraphics(width, (height - canvas.height), P3D);
		auxScene = new Scene(this, (PGraphics3D) auxCanvas);
		auxScene.setRadius(50);
		auxScene.setGridIsDrawn(false);
		// same drawing function which is defined below
		auxScene.addDrawHandler(this, "drawing");
		
		//robot stuff
		robot = new RobotArm(scene);
		scene.setInteractiveFrame(robot.frame(0));
		auxScene.setCamera(robot.cam);
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
	}

	public void handleMouse() {
		if (mouseY < canvas.height) {
			scene.enableMouseHandling();
			scene.enableKeyboardHandling();
			auxScene.disableMouseHandling();
			auxScene.disableKeyboardHandling();
		} else {
			scene.disableMouseHandling();
			scene.disableKeyboardHandling();
			auxScene.enableMouseHandling();
			auxScene.enableKeyboardHandling();
		}
	}

	// the actual drawing function, shared by the two scenes
	public void drawing(Scene scn) {
		PGraphics3D pg3d = scn.renderer();
		pg3d.background(0);
		if(enabledLights)
			pg3d.lights();
		// 1. draw the robot
		robot.draw(scn);
		// 2. draw the scene
		// 2a. draw a ground
		pg3d.noStroke();
		pg3d.fill(120, 120, 120);
		float nbPatches = 100;
		pg3d.normal(0.0f, 0.0f, 1.0f);
		for (int j = 0; j < nbPatches; ++j) {
			pg3d.beginShape(QUAD_STRIP);
			for (int i = 0; i <= nbPatches; ++i) {
				pg3d.vertex((200 * (float) i / nbPatches - 100), (200 * j / nbPatches - 100));
				pg3d.vertex((200 * (float) i / nbPatches - 100), (200 * (float) (j + 1) / nbPatches - 100));
			}
			pg3d.endShape();
		}
		// 2b. draw a box
		pg3d.pushStyle();
		pg3d.noStroke();
		pg3d.fill(255, 0, 255);
		pg3d.pushMatrix();
		pg3d.translate(30,30,10);
		pg3d.box(20);
		pg3d.popMatrix();
		pg3d.popStyle();
	}
	
	public void keyPressed() {
		if(key == 'x')
			robot.nextIFrame();
		if(key == 'l') {
			enabledLights = !enabledLights;
			if(enabledLights)
				println("camera spot light enabled");
			else
				println("camera spot light disabled");
		}
		if(key == 'f') {
			drawRobotCamFrustum = !drawRobotCamFrustum;
			if(drawRobotCamFrustum)
				println("draw robot camera frustum");
			else
				println("don't draw robot camera frustum");
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CameraCrane" });
	}
}
