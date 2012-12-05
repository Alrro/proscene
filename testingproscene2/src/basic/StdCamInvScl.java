package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
//import remixlab.remixcam.constraints.*;

//import remixlab.proscene.AxisPlaneConstraint.Type;

@SuppressWarnings("serial")
public class StdCamInvScl extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;

	public void setup() {
		size(640, 720, P3D);
		//size(640, 720, OPENGL);

		canvas = createGraphics(640, 360, P3D);
		//canvas = createGraphics(640, 360, OPENGL);
		scene = new Scene(this, canvas);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by default)
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
		auxScene.setRadius(800);
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
	
	public void keyPressed() {
		if(key == 't') {
			scene.camera().frame().scale(2,2,2);			
		}
		if(key == 'T') {
			scene.camera().frame().scale(0.5f,0.5f,0.5f);
		}
		if(key == 'u' || key == 'U') {
			scene.camera().frame().scale(1,1.2f,1);			
		}
		
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}
		
		if(key == 'x' || key == 'X') {
			scene.camera().frame().scale(-1,1,1);			
		}
		if(key == 'y' || key == 'Y') {
			scene.camera().frame().scale(1,-1,1);			
		}
		if(key == 'z' || key == 'Z') {
			scene.camera().frame().scale(1,1,-1);
		}		
				
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		println( "scene.camera().frame().scaling(): " + scene.camera().frame().scaling() );
		println( "scene.camera().frame().magnitude(): " + scene.camera().frame().magnitude() );
		println("cam scene radius: " + scene.camera().sceneRadius());
		println("distance to scene center: " + scene.camera().distanceToSceneCenter());
		println("distance to arp: " + scene.camera().distanceToARP());
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StandardCamera" });
	}
}