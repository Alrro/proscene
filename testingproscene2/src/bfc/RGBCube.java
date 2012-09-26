package bfc;
import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.core.Camera.Cone;


@SuppressWarnings("serial")
public class RGBCube extends PApplet {
	float size = 50;
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	PGraphics3D g3;
	Vector3D normalXPos = new Vector3D(1,0,0);
	Vector3D normalYPos = new Vector3D(0,1,0);
	Vector3D normalZPos = new Vector3D(0,0,1);
	Vector3D normalXNeg = new Vector3D(-1,0,0);
	Vector3D normalYNeg = new Vector3D(0,-1,0);
	Vector3D normalZNeg = new Vector3D(0,0,-1);
	ArrayList<Camera.Cone> normalCones; 
	ArrayList<Vector3D> normals;
	Vector3D [] normalArray = new Vector3D [2];	

	public void setup() {
		size(640, 720, P3D);
		normals = new ArrayList<Vector3D>();
		normals.add(normalZPos);
		normals.add(normalXPos);
		normals.add(normalYPos);
		//normals.add(normalZNeg);
		
		/**
		normalArray[0] = normalZPos;
		normalArray[1] = normalXPos;
		*/
		
		
		canvas = createGraphics(640, 360, P3D);
		scene = new Scene(this, (PGraphicsOpenGL) canvas);
		scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by
		// default)
		//scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);
		scene.addDrawHandler(this, "mainDrawing");

		auxCanvas = createGraphics(640, 360, P3D);
		auxScene = new Scene(this, (PGraphicsOpenGL) auxCanvas);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");

		handleMouse();

		// g3 = (PGraphics3D)g;
		//noStroke();
		colorMode(RGB, 1);
		
		Cone cone = scene.camera().new Cone(normals);
		println( "cone angle: " + cone.angle() + " cone axis: " + cone.axis() );
		
		ArrayList<Vector3D> nT = new ArrayList<Vector3D>();
		nT.add(new Vector3D(1,1,1));
		nT.add(new Vector3D(1,1,-1));
		nT.add(new Vector3D(1,-1,1));
		nT.add(new Vector3D(1,-1,-1));
		
		nT.add(new Vector3D(-1,1,1));
		nT.add(new Vector3D(-1,1,-1));
		nT.add(new Vector3D(-1,-1,1));
		nT.add(new Vector3D(-1,-1,-1));
		
		/**
		 [ 1.0, 1.0, 1.0 ]
         [ 1.0, 1.0, -1.0 ]
         [ -1.0, 1.0, -1.0 ]
         [ -1.0, 1.0, 1.0 ]
		 */
		
		Cone cT  = scene.camera().new Cone(nT);
		println( "cone angle: " + ( cT.angle()*360 / (2*PApplet.PI)) + " cone axis: " + cT.axis() );
	}

	// public void draw() {
	// scene.background(0.5f);
	// drawScene(g3);
	// }

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
		image(auxCanvas, 0, 360);
	}

	public void mainDrawing(Scene s) {
		//s.background(0.0f);
		PGraphicsOpenGL p = s.pggl();
		p.background(0);
		drawScene(p);
	}

	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);
		s.pggl().pushStyle();
		s.pggl().fill(0,255,255);
		s.pggl().stroke(0,255,255);
		s.drawCamera(scene.camera());
		s.pggl().popStyle();
	}

	void drawScene(PGraphics p) {
		//p.background(0.5f);
		p.noStroke();
		p.beginShape(QUADS);		
		
        Vector3D nVD = scene.camera().viewDirection();
        
        if(!scene.camera().coneIsBackFacing(nVD, normals)) {        
        
		// z-axis
		//if(!scene.camera().faceIsBackFacing(nVD, normalZPos)) {
		p.fill(0, size, size);
		p.vertex(-size, size, size);
		p.fill(size, size, size);
		p.vertex(size, size, size);
		p.fill(size, 0, size);
		p.vertex(size, -size, size);
		p.fill(0, 0, size);
		p.vertex(-size, -size, size);
		//}
		
		// x-axis
		//if(!scene.camera().faceIsBackFacing(nVD, normalXPos)) {
		p.fill(size, size, size);
		p.vertex(size, size, size);
		p.fill(size, size, 0);
		p.vertex(size, size, -size);
		p.fill(size, 0, 0);
		p.vertex(size, -size, -size);
		p.fill(size, 0, size);
		p.vertex(size, -size, size);
		// } 
		
		// /**
		// y-axis
		//if(!scene.camera().faceIsBackFacing(nVD, normalYPos)) {
		p.fill(0, size, 0);
		p.vertex(-size, size, -size);
		p.fill(size, size, 0);
		p.vertex(size, size, -size);
		p.fill(size, size, size);
		p.vertex(size, size, size);
		p.fill(0, size, size);
		p.vertex(-size, size, size);        
		// */
        

		/**
		// -z-axis
		p.fill(size, size, 0);
		p.vertex(size, size, -size);
		p.fill(0, size, 0);
		p.vertex(-size, size, -size);
		p.fill(0, 0, 0);
		p.vertex(-size, -size, -size);
		p.fill(size, 0, 0);
		p.vertex(size, -size, -size);
		// */
		
        } // cone condition

		/**
		// -x-axis
		p.fill(0, size, 0);
		p.vertex(-size, size, -size);
		p.fill(0, size, size);
		p.vertex(-size, size, size);
		p.fill(0, 0, size);
		p.vertex(-size, -size, size);
		p.fill(0, 0, 0);
		p.vertex(-size, -size, -size);
		// */		

		/**
		// -y-axis
		p.fill(0, 0, 0);
		p.vertex(-size, -size, -size);
		p.fill(size, 0, 0);
		p.vertex(size, -size, -size);
		p.fill(size, 0, size);
		p.vertex(size, -size, size);
		p.fill(0, 0, size);
		p.vertex(-size, -size, size);
		// */

		p.endShape();
	}
	
	public void keyPressed() {
		if(key == 'd') {
			println(scene.camera().viewDirection());
		}
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
}
