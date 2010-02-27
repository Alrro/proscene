/**
 * Interactive Frame. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to add and manipulate and interactive
 * frame to your PScene, which represents one of the three interactive
 * mechanisms built-in proscene (camera and mouse grabber, being the
 * other two).
 *
 * Press 'i' to switch the interaction between the camera frame and
 * the interactive frame.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import proscene.*;

PScene scene;
	
void setup()	{
	size(640, 360, OPENGL);
	scene = new PScene(this); 
	scene.setCameraType(PSCamera.Type.ORTHOGRAPHIC);
	scene.setSceneRadius(scene.sceneRadius()*1.3f);
	scene.showEntireScene();
	scene.setGridIsDrawn(true);
	scene.setAxisIsDrawn(true);
	// A PScene have a single PSInteractiveFrame (null by default).
	// We set it here. 
	scene.setInteractiveFrame(new PSInteractiveFrame());
	scene.interactiveFrame().translate(new PVector(0.2f, 0.2f, 0));
	scene.setDrawInteractiveFrame(true);
}

// Your actual scene drawing should be enclosed between the
// PScene.beginDraw() and PScene.endDraw() pair.
void draw() {
	// Should always be defined before PScene.beginDraw()
	background(0);
	scene.beginDraw();
	// Here we are in the world coordinate system.
	// Draw your scene here.
	//background(0);
	fill(204, 102, 0);
	box(0.2f, 0.3f, 0.5f);
	// Save the current model view matrix
	pushMatrix();
	// Multiply matrix to get in the frame coordinate system.
	//applyMatrix( scene.interactiveFrame().pMatrix() );
	//Same as the previous commented line, but a lot more efficient:
	scene.interactiveFrame().applyTransformation(this);
	// Draw an axis using the GLScene static function
	PScene.drawAxis();
	// Draw a second box. This is box is the one attached to the
	// interactive frame
	fill(255, 0, 0);
	box(0.1f, 0.15f, 0.25f);
	popMatrix();
	scene.endDraw();
}	

// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
void keyPressed() {
	scene.defaultKeyBindings();
}
	