import processing.core.*;
import remixlab.proscene.*;

Viewer viewer;
Viewer auxViewer;

OctreeNode Root;

//Main viewer drawing method (we register it at the constructor which is found below in the setup)
void viewerDrawing(PApplet p) {
  //Uncomment the following line if you want to get rid of some exceptions
  //if(viewer.getScene() != null) if(viewer.getScene().camera() != null)
  Root.drawIfAllChildrenAreVisible(p, viewer.getScene().camera());
}

//Main viewer configuration method (we register it at the constructor which is found below in the setup)
void configureViewerScene(Scene s) {
  //Computation of the frustum planes coefficients is expensive, so it's disabled by default 
  s.enableFrustumEquationsUpdate();
  s.setHelpIsDrawn(false);
}

//Auxiliary viewer drawing method (we register it at the constructor which is found below in the setup)
void auxViewerDrawing(PApplet p) {
  //Same as main viewer, but we also draw the main viewer camera
  viewerDrawing(p);
  DrawingUtils.drawCamera(p, viewer.getScene().camera() );
}

//Auxiliary viewer configuration method (we register it at the constructor which is found below in the setup)
void configureAuxViewerScene(Scene s) {
  s.setHelpIsDrawn(false);
  s.setRadius(200);
  s.showAll();
}

void setup() {
  PVector v = new PVector(100, 70, 130);
  Root = new OctreeNode(v, PVector.mult(v, -1.0f));
  Root.buildBoxHierarchy(4);
  viewer =  new Viewer(this, "viewerDrawing", 640, 360, "configureViewerScene");
  auxViewer = new Viewer(this, "auxViewerDrawing", 640, 360, "configureAuxViewerScene");
  viewer.init();
  auxViewer.init();
  size(640, 740);
}