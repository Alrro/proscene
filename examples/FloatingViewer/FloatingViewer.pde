import remixlab.proscene.*;

Scene scene;
FloatingWindow fWin;
OctreeNode Root;

void setup()	{
  size(640, 360, P3D);
  PVector p = new PVector(100, 70, 130);
  Root = new OctreeNode(p, PVector.mult(p, -1.0f));
  Root.buildBoxHierarchy(4);
  scene = new Scene(this);
  scene.enableFrustumEquationsUpdate();
  scene.setHelpIsDrawn(false);
  
  fWin = new FloatingWindow(this, "Extra Viewer", 130, 100, 640, 360, false, P3D);
  //pb1: occasionally we get null pointer exceptions
  fWin.getScene().setHelpIsDrawn(false);
  fWin.getScene().setRadius(200);
  fWin.getScene().showAll();
  fWin.addDrawHandler(this, "windowDraw");
}

public void draw() {
  Root.drawIfAllChildrenAreVisible(this, scene.camera());
}

void windowDraw(Viewer v) {
  Root.drawIfAllChildrenAreVisible(v, scene.camera());
  DrawingUtils.drawCamera( v,  scene.camera() );
}