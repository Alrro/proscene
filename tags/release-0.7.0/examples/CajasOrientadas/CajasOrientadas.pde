/**
 * Cajas Orientadas. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates some basic PSFrame properties, particularly how
 * to orient them. Select and move the sphere (holding the right mouse button
 * pressed) to see how the boxes will immediately be oriented towards it.
 * You can also pick and move the boxes and still they will be oriented
 * towards the sphere.
 *
 * This example illustrates some functionality which was needed by the
 * http://cajas.sourceforge.net/ project.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import proscene.*;

PScene scene;
Caja [] cajas;
Esfera esfera;
	
void setup() {
  size(640, 360, OPENGL);
  scene = new PScene(this);		
  scene.setGridIsDrawn(true);		
  scene.setCameraType(PSCamera.Type.ORTHOGRAPHIC);
  scene.setSceneRadius(2);		
  scene.showEntireScene();
		
  esfera = new Esfera(this);
  esfera.setPosition(new PVector(0.0f, 1.0f, 0.0f));
		
  cajas = new Caja[5];
  for (int i = 0; i < 5; i++) {
    cajas[i] = new Caja(this);
    cajas[i].setPosition(new PVector((-1.0f + (i*0.5f )), 0.0f, 0.0f));
    //not really necessary here: it is set in draw!
    //cajas[i].setOrientation( esfera.getPosition() );
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
  esfera.draw();
  for (int i = 0; i < 5; i++) {
    cajas[i].draw(esfera.getPosition());
  }		    
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
