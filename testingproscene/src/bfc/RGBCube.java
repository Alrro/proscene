package bfc;
import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Camera.Cone;

@SuppressWarnings("serial")
public class RGBCube extends PApplet {
	float size = 50;
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	PGraphics3D g3;
	PVector normalXPos = new PVector(1,0,0);
	PVector normalYPos = new PVector(0,1,0);
	PVector normalZPos = new PVector(0,0,1);
	PVector normalXNeg = new PVector(-1,0,0);
	PVector normalYNeg = new PVector(0,-1,0);
	PVector normalZNeg = new PVector(0,0,-1);
	ArrayList<Camera.Cone> normalCones; 
	ArrayList<PVector> normals;
	PVector [] normalArray = new PVector [2];	

	public void setup() {
		size(640, 720, P3D);
		normals = new ArrayList<PVector>();
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
		// scene.enableFrustumEquationsUpdate();
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
		
		ArrayList<PVector> nT = new ArrayList<PVector>();
		nT.add(new PVector(1,1,1));
		nT.add(new PVector(1,1,-1));
		nT.add(new PVector(1,-1,1));
		nT.add(new PVector(1,-1,-1));
		
		nT.add(new PVector(-1,1,1));
		nT.add(new PVector(-1,1,-1));
		nT.add(new PVector(-1,-1,1));
		nT.add(new PVector(-1,-1,-1));
		
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
		PGraphicsOpenGL p = s.renderer();
		p.background(0);
		drawScene(p);
	}

	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);
		s.drawCamera(scene.camera());
	}

	void drawScene(PGraphics p) {
		//p.background(0.5f);
		p.noStroke();
		p.beginShape(QUADS);		
		
        PVector nVD = scene.camera().viewDirection();
        
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
