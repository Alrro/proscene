package bfc;
import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.Camera.Cone;

@SuppressWarnings("serial")
public class RGBCube extends PApplet {
	float size = 50;
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	
	ProsceneMouse mouse, auxMouse;
	ProsceneKeyboard keyboard, auxKeyboard;

	PGraphics3D g3;
	Vec normalXPos = new Vec(1,0,0);
	Vec normalYPos = new Vec(0,1,0);
	Vec normalZPos = new Vec(0,0,1);
	Vec normalXNeg = new Vec(-1,0,0);
	Vec normalYNeg = new Vec(0,-1,0);
	Vec normalZNeg = new Vec(0,0,-1);
	ArrayList<Camera.Cone> normalCones; 
	ArrayList<Vec> normals;
	Vec [] normalArray = new Vec [2];	

	public void setup() {
		size(640, 720, P3D);
		normals = new ArrayList<Vec>();
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
		
		mouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		keyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");
		auxMouse = (ProsceneMouse)scene.terseHandler().getAgent("proscene_mouse");
		auxKeyboard = (ProsceneKeyboard)scene.terseHandler().getAgent("proscene_keyboard");

		handleMouse();

		// g3 = (PGraphics3D)g;
		//noStroke();
		colorMode(RGB, 1);
		
		Cone cone = scene.camera().new Cone(normals);
		println( "cone angle: " + cone.angle() + " cone axis: " + cone.axis() );
		
		ArrayList<Vec> nT = new ArrayList<Vec>();
		nT.add(new Vec(1,1,1));
		nT.add(new Vec(1,1,-1));
		nT.add(new Vec(1,-1,1));
		nT.add(new Vec(1,-1,-1));
		
		nT.add(new Vec(-1,1,1));
		nT.add(new Vec(-1,1,-1));
		nT.add(new Vec(-1,-1,1));
		nT.add(new Vec(-1,-1,-1));
		
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
		
        Vec nVD = scene.camera().viewDirection();
        
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
