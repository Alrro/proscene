/**
 * Point Under Pixel.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates 3D world point picking.
 * 
 * The click bindings below define some per-pixel user interactions,
 * such as "zoom on pixel" or "set the arcball reference point". 
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
Box [] boxes;

void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  scene.setShortcut('z', Scene.KeyboardAction.ARP_FROM_PIXEL);
  //add the click actions to all camera profiles
  CameraProfile [] camProfiles = scene.getCameraProfiles();
  for (int i=0; i<camProfiles.length; i++) {
    // left click will zoom on pixel:
    camProfiles[i].setClickBinding( LEFT, Scene.ClickAction.ZOOM_ON_PIXEL );
    // middle click will show all the scene:
    camProfiles[i].setClickBinding( CENTER, Scene.ClickAction.SHOW_ALL);
    // right click will will set the arcball reference point:
    camProfiles[i].setClickBinding( RIGHT, Scene.ClickAction.ARP_FROM_PIXEL );
    // double click with the middle button while pressing SHIFT will reset the arcball reference point:
    camProfiles[i].setClickBinding( Event.SHIFT, CENTER, 2, Scene.ClickAction.RESET_ARP );
  }	  

  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  scene.setRadius(150);
  scene.showAll();
  boxes = new Box[50];
  // create an array of boxes with random positions, sizes and colors
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box();
}

void draw() {
  background(0);
  for (int i = 0; i < boxes.length; i++)    
    boxes[i].draw();
}
