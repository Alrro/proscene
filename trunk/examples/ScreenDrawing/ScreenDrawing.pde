/**
 * Screen Drawing.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to combine 2D and 3D drawing.
 *
 * All screen drawing should be enclosed between Scene.beginScreenDrawing() and
 * Scene.endScreenDrawing(). Then you can just begin drawing your screen shapes
 * (defined between beginShape() and endShape()). Just note that the (x,y) vertex
 * screen coordinates should be specified as:
 * vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord()).
 *
 * Press 'y' while pressing + moving the mouse to draw directly on the screen.
 *
 * Press 'x' to clean your screen drawing.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help. Note that the point
 * picking mechanism is disable when the help is display (otherwise the help text
 * can interfere).
 */

import remixlab.proscene.*;

import java.awt.Point;

Scene scene;
Box [] boxes;
ArrayList points;
	
void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setRadius(150);
  scene.showAll();
  
  boxes = new Box[50];
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box(this);
  
  points = new ArrayList();  // Create an empty ArrayList
}

// Your actual scene drawing should be enclosed between the
// Scene.beginDraw() and Scene.endDraw() pair.
void draw() {
  // Should always be defined before Scene.beginDraw()
  background(0);
  
  scene.beginDraw();
    
  // A. 3D drawing
  for (int i = 0; i < boxes.length; i++)
    boxes[i].draw();
    
  // B. 2D drawing
  // All screen drawing should be enclosed between Scene.beginScreenDrawing() and
  // Scene.endScreenDrawing(). Then you can just begin drawing your screen shapes
  // (defined between beginShape() and endShape()). Just note that the (x,y) vertex
  // screen coordinates should be specified as:
  // vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord()).
  scene.beginScreenDrawing();
  strokeWeight(8);
  stroke(183,67,158,127);
  noFill();
  beginShape();
  for (int i = 0; i < points.size(); i++)
    vertex(scene.xCoord( ((Point) points.get(i)).x ),
           scene.yCoord( ((Point) points.get(i)).y ), scene.zCoord());
  endShape();
  strokeWeight(1);
  scene.endScreenDrawing();
  
  scene.endDraw();
}

// To take full advantage of proscene 3d navigation power keyPressed() should always
// call Scene.defaultKeyBindings()
void keyPressed() {
  if(key == 'y' && mousePressed) {
    // to draw on screen first disable proscene mouse handling
    scene.disableMouseHandling();
    points.add(new Point(mouseX, mouseY));
  }
  else {
    // re-enable proscene mouse handling
    scene.enableMouseHandling();
    if (key == 'x')
      points.clear();
    else
      scene.defaultKeyBindings();
  }
}
