/**
 * Screen Drawing.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to combine 2D and 3D drawing.
 *
 * All screen drawing should be enclosed between a Scene.beginScreenDrawing() / Scene.endScreenDrawing() pair.
 * Then you can just begin drawing your screen shapes (defined between a beginShape() / endShape() pair).
 * Just note that the (x,y) vertex screen coordinates should be specified as:
 * vertex(Scene.xCoord(x), Scene.yCoord(y), Scene.zCoord()). 
 *
 * Press 'h' to toggle the mouse and keyboard navigation help. Note that the point
 * picking mechanism is disable when the help is display (otherwise the help text
 * can interfere).
 */


import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;
Box [] boxes;

void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  scene.setGridIsDrawn(true);
  scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
  scene.setRadius(2);
  scene.showAll();
  
  boxes = new Box[6];
  for (int i = 0; i < 6; i++) {
    boxes[i] = new Box(this);
    boxes[i].setPosition(new PVector((-1.0f + (i*0.4f )), 0.0f, 0.0f));
  }
}

void draw() {
  //set camera stuff, always necessary:
  background(0);  
  scene.beginDraw();
  // Here we are in the world coordinate system.
  // Draw your scene here.
  for (int i = 0; i < 6; i++) {
    //2D drawing of arrows
    //PVector head = scene.camera().projectedCoordinatesOf(boxes[i].getPosition());
    //same as the previous line:
    PVector pos = boxes[i].getPosition();
    PVector head = new PVector (screenX(pos.x, pos.y, pos.z), screenY(pos.x, pos.y, pos.z), screenZ(pos.x, pos.y, pos.z)); 
    scene.beginScreenDrawing();
    stroke(255, 255, 255);
    strokeWeight(2);
    noFill();
    beginShape(LINES);
    vertex(Scene.xCoord(head.x-10), Scene.yCoord(head.y-10), Scene.zCoord());
    vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
    vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
    vertex(Scene.xCoord(head.x-10), Scene.yCoord(head.y+10), Scene.zCoord());
    vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
    vertex(Scene.xCoord(head.x-30), Scene.yCoord(head.y), Scene.zCoord());
    endShape();
    strokeWeight(1);
    scene.endScreenDrawing();
    //3D drawing
    boxes[i].draw();
  }
  scene.endDraw();
}

void keyPressed() {
  scene.defaultKeyBindings();
}
