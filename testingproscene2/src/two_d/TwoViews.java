package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;

@SuppressWarnings("serial")
public class TwoViews extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	//String renderer = P2D;
	String renderer = JAVA2D;
	
	ProsceneMouse mouse, auxMouse;
	ProsceneKeyboard keyboard, auxKeyboard;
	
	public void setup() {		
		size(640, 720, renderer);
		canvas = createGraphics(640, 360, renderer);
		scene = new Scene(this, canvas);		
		
		//scene.window().flip();
		 
		auxCanvas = createGraphics(640, 360, renderer);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);		
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
		
		// /**
		scene.pg().ellipse(0, 0, 40, 40);
		scene.pg().rect(50, 50, 30, 30);		
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxCanvas.background(0);		
		auxScene.pg().ellipse(0, 0, 40, 40);
		auxScene.pg().rect(50, 50, 30, 30);
		auxScene.pg().pushStyle();		
		auxScene.pg().stroke(255,255,0);
		auxScene.pg().fill(255,255,0,160);
		auxScene.drawWindow(scene.window());
		auxScene.pg().popStyle();		
		auxScene.endDraw();
		auxCanvas.endDraw();
		
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
		
		//println("camera angle: " + scene.pinhole().frame().orientation().angle());
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
	
	public void keyPressed() {
		if(key == 'w') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();
			if(scene.isRightHanded())
				println("Scene is RIGHT handed");
			else
				println("Scene is LEFT handed");
		}
		if(key == 'W') {
			if(auxScene.isRightHanded())
				auxScene.setLeftHanded();			
			else
				auxScene.setRightHanded();
			if(auxScene.isRightHanded())
				println("auxScene is RIGHT handed");
			else
				println("auxScene is LEFT handed");
		}
		if(key == 'u' || key== 'U' ) {
			println("projection matrix:");
			scene.window().projection().print();
			println("world matrix:");
			scene.window().frame().worldMatrix().print();			
			println("view matrix:");
			scene.window().view().print();
			println("camera angle: " + scene.window().frame().orientation().angle());
		}
	}
}
