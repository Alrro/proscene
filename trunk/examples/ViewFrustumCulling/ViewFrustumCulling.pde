/**
 * <b>View Frustum Culling</b> by Jean Pierre Charalambos.
 * <p>
 * This example illustrates how to solve analytically the camera frustum plane equations.
 * <p>
 * A hierarchical octree structure is clipped against a camera's frustum clipping planes.<br>
 * A second viewer displays an external view of the scene that exhibits the clipping<br>
 * (using DrawingUtils.drawCamera() to display the frustum). 
 * <p>
 * This example requires the <a href="http://github.com/acsmith/napplet">napplet</a> library.
 * (download it <a href="http://github.com/acsmith/napplet/downloads">here</a>).
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;
import napplet.*;

NAppletManager nappletManager;
NApplet mainNApplet;
OctreeNode Root;

void setup() {
  size(640, 720, P3D);
  // declare and build the octree hierarchy
  PVector p = new PVector(100, 70, 130);  
  Root = new OctreeNode(p, PVector.mult(p, -1.0f));
  Root.buildBoxHierarchy(4);
  // instantiate the viewers and embed them into a napplet manager
  nappletManager = new NAppletManager(this);
  mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);  
  nappletManager.createNApplet("AuxiliarViewer", 0, 360);
}

// Since we need to pass the main viewer scene to the auxiliar viewer, we declare its
// drawing here. We then add the drawing at the AuxiliarViewer class.
void auxiliarViewerDrawing(PApplet p) {
  Root.drawIfAllChildrenAreVisible(p, ((MainViewer)mainNApplet).getScene().camera());
  DrawingUtils.drawCamera(p, ((MainViewer)mainNApplet).getScene().camera());
}

void draw() {  
  background(50);
}

