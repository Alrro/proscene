package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class TwoViews extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	String renderer = P2D;
	//String renderer = JAVA2D;
	
	public void setup() {		
		size(640, 720, renderer);
		canvas = createGraphics(640, 360, renderer);
		scene = new Scene(this, canvas);		
		
		//scene.viewWindow().flip();
		 
		auxCanvas = createGraphics(640, 360, renderer);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);		
		auxScene.setRadius(200);
		auxScene.showAll();
		
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
		auxScene.drawViewWindow(scene.viewWindow());
		auxScene.pg().popStyle();		
		auxScene.endDraw();
		auxCanvas.endDraw();
		
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
		
		//println("camera angle: " + scene.pinhole().frame().orientation().angle());
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
			scene.viewWindow().projection().print();
			println("world matrix:");
			scene.viewWindow().frame().worldMatrix().print();			
			println("view matrix:");
			scene.viewWindow().view().print();
			println("camera angle: " + scene.viewWindow().frame().orientation().angle());
		}
	}
}
