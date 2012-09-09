package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class TwoViews extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	
	public void setup() {
		size(640, 720, P2D);
		canvas = createGraphics(640, 360, P2D);
		scene = new Scene(this, canvas);		
		 
		auxCanvas = createGraphics(640, 360, P2D);
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
				 
		scene.pg2d().ellipse(0, 0, 40, 40);
		scene.pg2d().rect(50, 50, 30, 30);		
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxCanvas.background(0);		
		auxScene.pg2d().ellipse(0, 0, 40, 40);
		auxScene.pg2d().rect(50, 50, 30, 30);
		auxScene.pg2d().pushStyle();		
		auxScene.pg2d().stroke(255,255,0);
		auxScene.pg2d().fill(255,255,0,160);
		auxScene.drawViewWindow(scene.viewWindow());
		auxScene.pg2d().popStyle();		
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
		if(key == 'u' || key== 'U' ) {
			scene.viewWindow().frame().worldMatrix().print();
		}
	}
}
