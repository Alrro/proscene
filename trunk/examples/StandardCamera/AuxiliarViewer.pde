public class AuxiliarViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
    scene.setAxisIsDrawn(false);
    scene.setGridIsDrawn(false);
    scene.setRadius(200);
    scene.showAll();
    // register the drawing method which was defined externally
    scene.addDrawHandler(parentPApplet, "auxiliarDrawing");
  }
}