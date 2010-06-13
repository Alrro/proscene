import processing.core.*;
import remixlab.proscene.*;

Viewer viewer;
Viewer auxViewer;

OctreeNode Root;

void setup() {
  PVector v = new PVector(100, 70, 130);
  Root = new OctreeNode(v, PVector.mult(v, -1.0f));
  Root.buildBoxHierarchy(4);
  
  //viewer = new Viewer(this, 640, 360, true);
  viewer =  new Viewer(this, 640, 360, 200, true);
  auxViewer = new Viewer(this, viewer, 640, 360);
  viewer.init();
  auxViewer.init();
  size(640, 720, P3D);
}
	
void proscenium(PApplet p) {
  if(viewer.getScene() != null)
    if(viewer.getScene().camera() != null)
      Root.drawIfAllChildrenAreVisible(p, viewer.getScene().camera());
}