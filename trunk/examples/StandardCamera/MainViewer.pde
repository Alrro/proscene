public class MainViewer extends NApplet {
  Scene scene;
  
  void setup() {
    size(640, 360, P3D);
    scene = new Scene(this);
    // enable computation of the frustum planes equations (disabled by default)
    scene.enableFrustumEquationsUpdate();
    scene.setHelpIsDrawn(false);
    scene.setGridIsDrawn(false);
  }
  
  // We need to pass the scene to the auxiliar viewer
  Scene getScene() {
    return scene;
  }
  
  void draw() {
    noStroke();
    if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
      fill(255, 0, 0);
    else
      fill(0, 255, 0);
    sphere(40);
  }
  
  void keyPressed() {
    if ( key == 'v')
      scene.toggleCameraKind();
    if ( (key == 'u') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(true);
    if ( (key == 'U') && (scene.camera().kind() == Camera.Kind.STANDARD) )
      scene.camera().changeStandardOrthoFrustumSize(false);
  }
}