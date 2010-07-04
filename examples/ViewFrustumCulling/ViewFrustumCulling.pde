import remixlab.proscene.*;
import napplet.*;

NAppletManager nappletManager;
NApplet mainNApplet;
OctreeNode Root;

void setup() {
  size(640, 720, P3D);
  PVector p = new PVector(100, 70, 130);
  Root = new OctreeNode(p, PVector.mult(p, -1.0f));
  Root.buildBoxHierarchy(4);
  
  nappletManager = new NAppletManager(this);
  mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);  
  nappletManager.createNApplet("AuxiliarViewer", 0, 360);
}

void auxiliarViewerDrawing(PApplet p) {
  Root.drawIfAllChildrenAreVisible(p, ((MainViewer)mainNApplet).getScene().camera());
  DrawingUtils.drawCamera(p, ((MainViewer)mainNApplet).getScene().camera());
}

void draw() {  
  background(50);
}

