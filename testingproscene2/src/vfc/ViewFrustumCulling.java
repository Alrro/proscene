package vfc;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class ViewFrustumCulling extends PApplet {
	OctreeNode Root;
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	
	ProsceneMouse mouse, auxMouse;
	ProsceneKeyboard keyboard, auxKeyboard;

	public void setup() {
		size(640, 720, P3D);
		// declare and build the octree hierarchy
		Vec p = new Vec(100, 70, 130);
		Root = new OctreeNode(p, Vec.mult(p, -1.0f));
		Root.buildBoxHierarchy(4);

		canvas = createGraphics(640, 360, P3D);
		scene = new Scene(this, canvas);
		scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);

		auxCanvas = createGraphics(640, 360, P3D);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		//auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		
		mouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		keyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");
		auxMouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		auxKeyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");

		handleMouse();
	}

	public void draw() {		
		handleMouse();
		canvas.beginDraw();
		scene.beginDraw();
		canvas.background(0);
		Root.drawIfAllChildrenAreVisible(scene.pggl(), scene.camera());
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxCanvas.background(0);
		Root.drawIfAllChildrenAreVisible(auxScene.pggl(), scene.camera());
		auxScene.pg3d().pushStyle();
		auxScene.pg3d().stroke(255,255,0);
		auxScene.pg3d().fill(255,255,0,160);
		auxScene.drawCamera(scene.camera());
		auxScene.pg3d().popStyle();
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
}