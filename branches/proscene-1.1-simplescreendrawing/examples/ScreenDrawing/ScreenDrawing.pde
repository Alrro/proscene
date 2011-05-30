/**
 * Screen Drawing.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to combine 2D and 3D drawing.
 * 
 * All screen drawing should be enclosed between Scene.beginScreenDrawing() and
 * Scene.endScreenDrawing(). Then you can just begin drawing your screen shapes
 * (defined between beginShape() and endShape()) using screen coordinates with
 * a z coordinate value of 0.
 * 
 * Press 'x' to toggle the screen drawing.
 * Press 'y' to clean your screen drawing.
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
Box [] boxes;
ArrayList points;
	
void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  // press 'f' to display frame selection hints
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  scene.setRadius(150);
  scene.showAll();
  
  boxes = new Box[50];
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box();
  
  points = new ArrayList();  // Create an empty ArrayList
}

void draw() {
  background(0);
  // A. 3D drawing
  for (int i = 0; i < boxes.length; i++)
    boxes[i].draw();
    
  // B. 2D drawing
  // All screen drawing should be enclosed between Scene.beginScreenDrawing() and
  // Scene.endScreenDrawing().  Then you can just begin drawing your screen shapes
  // (defined between beginShape() and endShape()) using screen coordinates with
  // a z coordinate value of 0.
  scene.beginScreenDrawing();
  pushStyle();
  strokeWeight(8);
  stroke(183,67,158,127);
  noFill();
  beginShape();
  for (int i = 0; i < points.size(); i++)
    // x and y are screen coordinates; the z coordinate should be 0.
    vertex(((Point) points.get(i)).x, ((Point) points.get(i)).y, 0);
  endShape();  
  popStyle();
  scene.endScreenDrawing();
}

void keyPressed() {
  if ((key == 'x') || (key == 'x'))
    scene.toggleMouseHandling();
  if ((key == 'y') || (key == 'Y'))
    points.clear();
}

void mouseDragged() {
  if(!scene.mouseIsHandled())
    points.add(new Point(mouseX, mouseY));
}