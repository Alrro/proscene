package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.AxisPlaneConstraint.Type;

@SuppressWarnings("serial")
public class StandardCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;

	public void setup() {
		size(640, 720, P3D);
		//size(640, 720, OPENGL);

		canvas = createGraphics(640, 360, P3D);
		//canvas = createGraphics(640, 360, OPENGL);
		scene = new Scene(this, (PGraphicsOpenGL) canvas);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by
		// default)
		scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);
		scene.addDrawHandler(this, "mainDrawing");
		
		/**
		WorldConstraint constraint2d = new WorldConstraint();
		PVector direction = new PVector(0,0,1);
		constraint2d.setRotationConstraint(Type.AXIS, direction);
		//constraint2d.setTranslationConstraint(Type.PLANE, direction);
		scene.camera().frame().setConstraint(constraint2d);
		*/		

		auxCanvas = createGraphics(640, 360, P3D);
		//auxCanvas = createGraphics(640, 360, OPENGL);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, (PGraphicsOpenGL) auxCanvas, 0, 360);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");

		handleMouse();
	}

	public void mainDrawing(Scene s) {
		PGraphicsOpenGL p = s.renderer();
		p.background(0);
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its
		// frustum
		switch (scene.camera().sphereIsVisible(new PVector(0, 0, 0), 40)) {
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

	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);
		s.drawCamera(scene.camera());
	}

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		scene.beginDraw();
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
	}

	public void handleMouse() {
		if (mouseY < 360) {
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
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StandardCamera" });
	}
}