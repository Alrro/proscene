/**
 * Basic Use. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates a direct approach to using proscene
 * by proscene.PScene proper instantiation.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import proscene.*;
import processing.opengl.*;

PScene scene;
	
void setup() {
	size(640, 360, OPENGL);
	// PScene instantiation
	scene = new PScene(this);
	scene.setGridIsDrawn(true);
	scene.setAxisIsDrawn(true);
}

// Your actual scene drawing should be enclosed between the
// PScene.beginDraw() and PScene.endDraw() pair.
void draw() {
	// Should always be defined before PScene.beginDraw()
	background(0);
	scene.beginDraw();
	// Actual scene
	fill(204, 102, 0);
	box(0.2f, 0.3f, 0.5f);
	scene.endDraw();
}

// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
void keyPressed() {
	scene.defaultKeyBindings();
}
