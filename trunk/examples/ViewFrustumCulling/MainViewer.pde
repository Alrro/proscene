/**
 * Main Viewer. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the View Frustum Culling example.
 * The octree is culled against this PApplet scene camera.
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

public class MainViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    // enable computation of the frustum planes equations (disabled by default)
    scene.enableFrustumEquationsUpdate();
    scene.setHelpIsDrawn(false);
    scene.setGridIsDrawn(false);
    scene.setAxisIsDrawn(false);
  }
  
  // We need to pass the scene to the auxiliar viwer
  Scene getScene() {
    return scene;
  }
  
  void draw() {
    Root.drawIfAllChildrenAreVisible(this, scene.camera());
  }
}
