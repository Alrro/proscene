/**
 * Auxiliar Viewer. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the View Frustum Culling example.
 * This class displays an external view of the main viewer scene
 * that exhibits the clipping.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

public class AuxiliarViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
    scene.setAxisIsDrawn(false);
    scene.setGridIsDrawn(false);
    scene.setHelpIsDrawn(false);    
    scene.setRadius(200);
    scene.showAll();
    // we add the external drawing method declared in the View Furstum Class
    scene.addDrawHandler(parentPApplet, "auxiliarViewerDrawing");
  }
  
  // dummy declaration (the drawing has been defined externally)
  void draw() {}
}
