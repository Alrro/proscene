import processing.core.*;
import remixlab.proscene.*;

EmbeddedWindow w1, w2;
OctreeNode Root;

void setup() {
  PVector p = new PVector(100, 70, 130);
  Root = new OctreeNode(p, PVector.mult(p, -1.0f));
  Root.buildBoxHierarchy(4);
  
  w1 = new EmbeddedWindow(this, "Win1", 640, 360, P3D);
  w2 = new EmbeddedWindow(this, "Win2", 640, 360, P3D);  
  w1.addDrawHandler(this, "draw1");
  w1.addSceneConfigHandler(this, "configureScene1");
  w2.addDrawHandler(this, "draw2");
  w2.addSceneConfigHandler(this, "configureScene2");
  
  add(w1);
  add(w2);
  
  //pb1: If trying to configure the scene we get
  //null pointer exceptions
  //the hack is to register the config as above with: addSceneConfigHandler 
  //w2.getScene().setRadius(200);
  //w2.getScene().showAll();
  
  size(640, 740);
  //pb2: If we use P3D then the octree is not properly updated
  //when changing the camera position!
  //size(640, 740, P3D);  
  
  noLoop();
}

void configureScene1(Scene s) {
  s.enableFrustumEquationsUpdate();
}

void configureScene2(Scene s) {
  s.setHelpIsDrawn(false);
  s.setRadius(200);
  s.showAll();
}

void draw1(Viewer v) {
  Root.drawIfAllChildrenAreVisible(v, w1.getScene().camera());
}

void draw2(Viewer v) {
  //same as draw1(v);
  Root.drawIfAllChildrenAreVisible(v, w1.getScene().camera());
  DrawingUtils.drawCamera( v, w1.getScene().camera());
}