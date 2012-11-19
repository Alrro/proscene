/**
 * Geometrie Projective
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
Figure figure;
PVector or;
PFont font;
InteractiveFrame repere, dragueur, rotateur;
WorldConstraint pivot, drag;
LocalConstraint planaire;
float zplan;

void setup() {
  size(640, 640, P3D);
  scene = new Scene(this);
  scene.setRadius(1600);

  scene.setCameraType(Camera.Type.PERSPECTIVE);
  font = createFont("FFScala", 48);

  float a=7.0;
  or=new PVector(0, 0, 0);
  zplan=30;
  pivot=new WorldConstraint();
  pivot.setRotationConstraint(AxisPlaneConstraint.Type.FREE, new PVector(0, 0, 0));
  pivot.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0, 0, 0));
  planaire=new LocalConstraint();
  planaire.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0, 0, 1));
  planaire.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0, 0, 0));
  drag=new WorldConstraint();
  drag.setTranslationConstraint(AxisPlaneConstraint.Type.FREE, new PVector(0, 0, 0));
  drag.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0, 0, 0));

  dragueur = new InteractiveFrame(scene);
  rotateur=new InteractiveFrame(scene);
  repere = new InteractiveFrame(scene);
  rotateur.setReferenceFrame(dragueur);
  repere.setConstraint(pivot);  
  dragueur.setConstraint(drag);    
  rotateur.setConstraint(planaire);

  dragueur.setPosition(new PVector(0, 0, 150));
  rotateur.setTranslation(new PVector(12, 0, 0));

  figure=new Figure(new PVector(a, a, 2*a), new PVector(a, -a, 2*a), new PVector(-a, -a, 2*a), new PVector(-a, a, 2*a));
  scene.camera().setPosition(new PVector(0, 0, 520));
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  rectMode(CENTER);
}

void draw() {
  background(255);
  textFont(font, 24);
  scene.drawGrid(100, 4);
  rotateur.setTranslation(normaliser(rotateur.translation(), 30));
  pushMatrix();
  dragueur.applyTransformation();
  pushMatrix();
  rotateur.applyTransformation();
  fill(255, 0, 0); 
  noStroke();  
  sphere(3);
  popMatrix();    
  noStroke();
  fill(255, 255, 0);
  sphere(3);
  popMatrix();
  ligne(or, dragueur.position());
  repere.setZAxis(dragueur.position());
  PVector vv=comb(1, dragueur.position(), -1, rotateur.position());
  repere.setXAxis(vv);
  repere.setZAxis(dragueur.position());
  pushMatrix();
  repere.applyTransformation();
  scene.drawAxis(100);
  figure.draw();
  popMatrix();
  pushMatrix();
  translate(0, 0, zplan-2);
  fill(250, 50, 150, 40);
  rect(0, 0, 300, 300);
  popMatrix();
  intersection(figure.plan12);
  intersection(figure.plan23);
  intersection(figure.plan34);
  intersection(figure.plan41);
  ligne(dragueur.position(), rotateur.position());
  ligne(or, dragueur.position());
  ligneDeFuite();
  unText1(" modifier l'homographie  ", rotateur.position());
}

void intersection(Plan plan) {
  PVector d1, d2;
  float skl=200;
  PVector n=repere.inverseTransformOf(plan.normale);
  if (n.y!=0) {
    d1=new PVector(skl, (-n.x*skl-n.z*zplan)/n.y, zplan);
    d2=new PVector(-skl, (n.x*skl-n.z*zplan)/n.y, zplan);
  }
  else {
    d1=new PVector(-n.z*zplan/n.x, skl, zplan);
    d2=new PVector(-n.z*zplan/n.x, -skl, zplan);
  }
  strokeWeight(4);
  ligne(d1, d2);
  strokeWeight(1);
}

void ligneDeFuite() {
  PVector n=repere.inverseTransformOf(new PVector(0, 0, 1));
  PVector d1, d2;
  float skl=320;

  if (n.y!=0) {
    d1=new PVector(skl, (-n.x*skl-n.z*zplan)/n.y, zplan);
    d2=new PVector(-skl, (n.x*skl-n.z*zplan)/n.y, zplan);
  }
  else {
    d1=new PVector(-n.z*zplan/n.x, skl, zplan);
    d2=new PVector(-n.z*zplan/n.x, -skl, zplan);
  }
  stroke(255, 0, 0);
  strokeWeight(5);
  ligne(d1, d2);
  strokeWeight(1);
  fill(255, 0, 0);

  d1=comb(0.5, d1, 0.5, d2);
  unText1("droite a l'infini", d1);
}
