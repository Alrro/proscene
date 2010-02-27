/**
 * Mouse Grabber. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the picking mechanism built-in proscene,
 * which represents one of the three interactive mechanisms found in
 * proscene (camera and interactive frame, being the other two).
 * Once you select a box it will be highlighted and you can manipulate
 * it with the mouse. Try the different mouse buttons to see what happens.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help. 
 */

import processing.core.*;
import processing.opengl.*;
import proscene.*;

PScene scene;
Box [] boxes;
	
void setup() {
	size(640, 360, OPENGL);
	scene = new PScene(this);		
	scene.setGridIsDrawn(true);		
	scene.setCameraType(PSCamera.Type.ORTHOGRAPHIC);
	scene.setSceneRadius(2);		
	scene.showEntireScene();
		
	boxes = new Box[6];
	for (int i = 0; i < 6; i++) {
		boxes[i] = new Box(this);
		boxes[i].setPosition(new PVector((-1.0f + (i*0.4f )), 0.0f, 0.0f));
	}
}

// Your actual scene drawing should be enclosed between the
// PScene.beginDraw() and PScene.endDraw() pair.
void draw() {
	// Should always be defined before PScene.beginDraw()
	background(0);
			
	scene.beginDraw();
	// Here we are in the world coordinate system.
	// Draw your scene here.
	for (int i = 0; i < 6; i++) {
		boxes[i].draw();
	}		    
	scene.endDraw();
}
	
// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
void keyPressed() {
	scene.defaultKeyBindings();
}
	