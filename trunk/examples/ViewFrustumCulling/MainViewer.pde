public class MainViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    scene.enableFrustumEquationsUpdate();
    scene.setHelpIsDrawn(false);
    scene.setGridIsDrawn(false);
    scene.setAxisIsDrawn(false);
  }
  
  Scene getScene() {
    return scene;
  }
  
  void draw() {
    Root.drawIfAllChildrenAreVisible(this, scene.camera());
  }
}
