/**
 * Standard Camera.
 * by Jean Pierre Charalambos.
 * 
 * A 'standard' Camera with fixed near and far planes.
 * 
 * Proscene supports a STANDARD camera kind which may be need by some applications.
 * Its near and far planes distances are set to fixed values, instead of being fit
 * to scene dimensions as is done in the PROSCENE camera kind (default).
 * 
 * Note, however, that the precision of the z-Buffer highly depends on how the zNear()
 * and zFar() values are fitted to your scene (as it is done with the PROSCENE camera
 * kind). Loose boundaries will result in imprecision along the viewing direction.
 * 
 * Press 'v' in the main viewer (the upper one) to toggle the camera kind.  
 * Press 'u'/'U' in the main viewer to change the frustum size (when the
 * camera kind is "STANDARD"). 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
// GLGraphicsOffScreen canvas;
PGraphics canvas;

Scene auxScene;
// GLGraphicsOffScreen canvas;
PGraphics auxCanvas;

void setup() {
  size(640, 720, P3D);

  canvas = createGraphics(640, 360, P3D);
  scene = new Scene(this, (PGraphics3D) canvas);
  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
  // enable computation of the frustum planes equations (disabled by
  // default)
  scene.enableFrustumEquationsUpdate();
  scene.setGridIsDrawn(false);
  scene.addDrawHandler(this, "mainDrawing");

  auxCanvas = createGraphics(640, 360, P3D);
  auxScene = new Scene(this, (PGraphics3D) auxCanvas);
  auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
  auxScene.setAxisIsDrawn(false);
  auxScene.setGridIsDrawn(false);
  auxScene.setRadius(200);
  auxScene.showAll();
  auxScene.addDrawHandler(this, "auxiliarDrawing");

  handleMouse();
}

void mainDrawing(Scene s) {		
  PGraphics3D p = s.renderer();
  p.noStroke();
  // the main viewer camera is used to cull the sphere object against its
  // frustum
  switch (scene.camera().sphereIsVisible(new PVector(0, 0, 0), 40)) {
  case VISIBLE:
    p.fill(0, 255, 0);
    p.sphere(40);
    break;
  case SEMIVISIBLE:
    p.fill(255, 0, 0);
    p.sphere(40);
    break;
  case INVISIBLE:
    break;
  }
}

void auxiliarDrawing(Scene s) {
  mainDrawing(s);
  DrawingUtils.drawCamera(s.renderer(), scene.camera());
}

void draw() {
  handleMouse();
  canvas.beginDraw();
  scene.beginDraw();
  //canvas.fill(204, 102, 0);
  //canvas.box(20, 30, 50);
  scene.endDraw();
  canvas.endDraw();
  image(canvas, 0, 0);

  auxCanvas.beginDraw();
  auxScene.beginDraw();
  //canvas.fill(204, 102, 0);
  //canvas.box(20, 30, 50);
  auxScene.endDraw();
  auxCanvas.endDraw();
  image(auxCanvas, 0, 360);
}

public void handleMouse() {
  if (mouseY < 360) {
    scene.enableMouseHandling();
    scene.enableKeyboardHandling();
    auxScene.disableMouseHandling();
    auxScene.disableKeyboardHandling();
  } 
  else {
    scene.disableMouseHandling();
    scene.disableKeyboardHandling();
    auxScene.enableMouseHandling();
    auxScene.enableKeyboardHandling();
  }
}		