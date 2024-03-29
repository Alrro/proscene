/**
 * Thales to Pappus
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */
 
import remixlab.proscene.*;

Scene scene;
WorldConstraint contrainteX, libreTR;
LocalConstraint  libreT, axial, fixe;
Plan plan1;
PlanArrivee planProjection;

InteractiveFrame soleil;
PFont font;

void setup() {
  size(640, 640, P3D);
  font = loadFont("FreeSans-24.vlw"); 
  textFont(font, 8); 
  scene=new Scene(this);
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  scene.setRadius(180);
  soleil=new InteractiveFrame(scene);
  soleil.setPosition(new PVector(30, -70, 70));
  contrainteX=new WorldConstraint();
  contrainteX.setTranslationConstraint(AxisPlaneConstraint.Type.FREE, new PVector(0.0f, 0.0f, 0.0f));
  contrainteX.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.1f, 0.0f, 0.0f));
  libreT=new LocalConstraint();
  libreT.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0.0f, 0.0f, 0.1f));
  libreT.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
  axial=new LocalConstraint();
  axial.setTranslationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f, 0.0f, 0.1f));
  axial.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
  fixe=new LocalConstraint();
  fixe.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
  fixe.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));   
  libreTR=new WorldConstraint();
  libreTR.setTranslationConstraint(AxisPlaneConstraint.Type.FREE, new PVector(0.0f, 0.0f, 0.0f));
  libreTR.setRotationConstraint(AxisPlaneConstraint.Type.FREE, new PVector(0.0f, 0.0f, 0.0f));

  plan1=new Plan(5.0, 2.16);

  planProjection=new PlanArrivee(-40, 0.0);
  planProjection.repere.setPosition(-30, -30, -30);

  plan1.setLongueur(80);
  scene.camera().setOrientation(new Quaternion( sin(-PI/10), 0, 0, cos(-PI/10)));
  scene.camera().setPosition(new PVector(30, 200, 250));
}

void draw() {
  background( 255, 200, 0);
  // lights();
  // directionalLight(55, 55, 255, -0.2, -0.3, 0.7);
  placeSoleil();
  planProjection.draw();
  plan1.draw();
}

void placeSoleil() {
  pushMatrix();
  soleil.applyTransformation();
  noStroke();
  fill(255, 0, 0);
  sphere(1);
  fill(255, 255, 0, 200);
  sphere(2);
  popMatrix();
}

