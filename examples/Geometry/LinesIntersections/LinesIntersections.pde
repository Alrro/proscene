/**
 * Lines Intersections
 * by Jacques Maire (http://www.alcys.com/)
 * 
 * Part of proscene classroom: http://www.openprocessing.org/classroom/1158
 * Check also the collection: http://www.openprocessing.org/collection/1438
 *
 * Four coplanar lines with interactive computing their intersections.
 *
 * The yellow spheres are the intersections calculated
 * The red and blue green spheres are interactive by click and drag (left and right)
 * The red spheres turn blue and the translatent straight.
 * The green sphere allows the rotation of the plane of four straight
 *
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
Plan plan;
WorldConstraint  waxial; 
LocalConstraint lplanaire, fixe;

void setup() {
  size (640, 640, P3D);  
  scene = new Scene(this);
  scene.setRadius(200);
  scene.camera().setPosition(new PVector(0, 350, 390));
  scene.camera().setViewDirection(new PVector(0, -0.8, -0.56));
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(false);
  waxial=new WorldConstraint();
  waxial.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
  waxial.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(1f, 0.0f, 0.0f));

  lplanaire=new LocalConstraint();
  lplanaire.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0.0f, 0.0f, 1.0f));
  lplanaire.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f, 0.0f, 1.0f));

  fixe=new LocalConstraint();
  fixe.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
  fixe.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));

  plan=new Plan();
}

void draw() {
  background(250, 220, 55);
  directionalLight(255, 255, 255, 0, -1, -1);

  scene.drawAxis(80);  
  plan.draw();
}

