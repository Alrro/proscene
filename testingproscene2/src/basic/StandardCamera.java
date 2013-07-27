package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.core.Constants.DOF0Action;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
//import remixlab.dandelion.constraints.*;

//import remixlab.proscene.AxisPlaneConstraint.Type;

@SuppressWarnings("serial")
public class StandardCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	
	ProsceneMouse mouse, auxMouse;
	ProsceneKeyboard keyboard, auxKeyboard;

	public void setup() {
		size(640, 720, P3D);
		//size(640, 720, OPENGL);

		canvas = createGraphics(640, 360, P3D);
		//canvas = createGraphics(640, 360, OPENGL);
		scene = new Scene(this, canvas);
		
		scene.prosceneKeyboard.profile().setShortcut('v', DOF0Action.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by
		// default)
		scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);
		scene.addDrawHandler(this, "mainDrawing");
		
		auxCanvas = createGraphics(640, 360, P3D);
		//auxCanvas = createGraphics(640, 360, OPENGL);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");
		
		mouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		keyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");
		auxMouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		auxKeyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");

		handleMouse();
	}

	public void mainDrawing(Scene s) {
		PGraphicsOpenGL p = s.pggl();
		p.background(0);
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its
		// frustum
		switch (scene.camera().sphereIsVisible(new Vec(0, 0, 0), 40)) {
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
		//s.drawCamera(scene.camera());		
		
		s.pg3d().pushStyle();
		s.pg3d().stroke(255,255,0);
		s.pg3d().fill(255,255,0,160);
		s.drawCamera(scene.camera());
		s.pg3d().popStyle();
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
			scene.terseHandler().registerAgent(mouse);
			scene.terseHandler().registerAgent(keyboard);			
			auxScene.terseHandler().unregisterAgent(auxMouse);
			auxScene.terseHandler().unregisterAgent(auxKeyboard);
		} else {
			scene.terseHandler().unregisterAgent(mouse);
			scene.terseHandler().unregisterAgent(keyboard);			
			auxScene.terseHandler().registerAgent(auxMouse);
			auxScene.terseHandler().registerAgent(auxKeyboard);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StandardCamera" });
	}
}