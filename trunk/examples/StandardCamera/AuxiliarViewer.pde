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
  }
  
  void draw() {
    noStroke();
    if( ((MainViewer)mainNApplet).getScene().camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
      fill(255, 0, 0);
    else
      fill(0, 255, 0);
    sphere(40);    
    DrawingUtils.drawCamera(this, ((MainViewer)mainNApplet).getScene().camera());
  }
}