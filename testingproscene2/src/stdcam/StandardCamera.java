package stdcam;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

// TODO fix me, add draw handler
public class StandardCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;

	public void setup() {
	  size(640, 720, P3D);

	  canvas = createGraphics(640, 360, P3D);
	  scene = new Scene(this, (PGraphicsOpenGL) canvas);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
	  // enable computation of the frustum planes equations (disabled by
	  // default)
	  scene.enableFrustumEquationsUpdate();
	  scene.setGridIsDrawn(false);
	  scene.addDrawHandler(this, "mainDrawing");

	  auxCanvas = createGraphics(640, 360, P3D);
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
	  PGraphicsOpenGL p = s.pggl();
	  p.background(0);
	  p.noStroke();
	  // the main viewer camera is used to cull the sphere object against its
	  // frustum
	  switch (scene.camera().sphereIsVisible(new Vector3D(0, 0, 0), 40)) {
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
		background(0);
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
	  } 
	  else {
	    scene.disableMouseHandling();
	    scene.disableKeyboardHandling();
	    auxScene.enableMouseHandling();
	    auxScene.enableKeyboardHandling();
	  }
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "stdcam.StandardCamera" });
	}
}
