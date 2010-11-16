/**
 * Alternative Use. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates an alternative approach to using
 * proscene through inheritance.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import processing.opengl.*;
import proscene.*;

Scene scene;	
	
void setup() {
  size(640, 360, OPENGL);
  // We instantiate our Scene class defined below
  scene = new Scene(this);			  
}

void draw() {
  background(0);
  scene.draw();		
}	
  	
// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
void keyPressed() {
  scene.defaultKeyBindings();
}
	
class Scene extends PScene {
  // We need to call super(p) to instantiate the base class
  public Scene(PApplet p) {
    super(p);
  }

  // Initialization stuff could have also been performed at
  // setup(), once after the Scene object have been instantiated 
  public void init() {			
    setGridIsDrawn(true);
    setAxisIsDrawn(true);
  }
		
  public void scene() {
    fill(204, 102, 0);
    box(0.2f, 0.3f, 0.5f);
  }
}