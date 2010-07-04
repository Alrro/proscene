public class AuxiliarViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);    
    scene.setHelpIsDrawn(false);
    scene.setRadius(200);
    scene.showAll();
    scene.addDrawHandler(parentPApplet, "auxiliarViewerDrawing");
  }
}
