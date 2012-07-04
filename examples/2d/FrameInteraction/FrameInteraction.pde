import remixlab.proscene.*;

Scene2D scene;

void setup() {  
  size(640, 360, P2D);
  scene = new Scene2D(this);
  // A Scene has a single InteractiveFrame (null by default). We set it here.
  scene.setInteractiveFrame(new InteractiveFrame(scene));
  scene.interactiveFrame().translate(new PVector(30, 30));
  // press 'i' to switch the interaction between the camera frame and the interactive frame
  scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
  // press 'f' to display frame selection hints
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
}

void draw() {
  background(0);
  fill(204, 102, 0);
  rect(0, 0, 55, 55);
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is handy but inefficient 
  scene.interactiveFrame().applyTransformation(); //optimum
  // Draw an axis using the Scene static function
  scene.drawAxis(20);
  // Draw a second box attached to the interactive frame
  if (scene.interactiveFrame().grabsMouse()) {
    fill(255, 0, 0);
    rect(0, 0, 35, 35);
  }
  else if (scene.interactiveFrameIsDrawn()) {
    fill(0, 255, 255);
    rect(0, 0, 35, 35);
  }
  else {
    fill(0, 0, 255);
    rect(0, 0, 30, 30);
  }    
  popMatrix();
}