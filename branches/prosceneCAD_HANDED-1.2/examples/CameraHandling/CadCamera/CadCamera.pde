/**
 * Cad Camera.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to add a CAD Camera type to your your scene.
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 * Press the space bar to switch between camera profiles: CAD and CAD_CAM.
 * Press x, y or z to set the main rotation axis (defined in the world
 * coordinate system) used by the CAD Camera.
 */

import remixlab.proscene.*;

Scene scene;

void setup() {
  size(640, 360, P3D);
  //Scene instantiation
  scene = new Scene(this);
  //Register a CAD Camera profile and name it "CAD_CAM"
  scene.registerCameraProfile(new CameraProfile(scene, "CAD_CAM", CameraProfile.Mode.CAD));
  //Set the CAD_CAM as the current camera profile
  scene.setCurrentCameraProfile("CAD_CAM");
  //Unregister the  first-person camera profile (i.e., leave WHEELED_ARCBALL and CAD_CAM)
  scene.unregisterCameraProfile("FIRST_PERSON");
}

void draw() {
  background(0);
  fill(204, 102, 0);
  box(20, 30, 50);
}

void keyPressed() {
  if (key == 'x' || key == 'X')
    scene.camera().frame().setCADAxis(new PVector(1, 0, 0));
  else if (key == 'y' || key == 'Y')
    scene.camera().frame().setCADAxis(new PVector(0, 1, 0));
  else if (key == 'z' || key == 'Z')
    scene.camera().frame().setCADAxis(new PVector(0, 0, 1));
}