/**
 * Moebius Tornado
 * by Jacques Maire (http://www.alcys.com/)
 * Coded on Monday 21/05/12
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
Bande bande;

void setup() {
  size(640, 640, P3D);
  scene=new Scene(this); 
  scene.setRadius(800);
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  scene.camera().setPosition(new PVector(0, 490, 140));
  scene.camera().setOrientation(new Quaternion(new PVector(1, 0, 0), -PI*0.34));
  bande=new Bande();
  frameRate(30);
}

void draw() {
  background(0);
  // lights();
  bande.remplir();
}

class Bande {
  float rayon, plus;
  int nbnoeud=73;
  int nbdiv=18;
  float angle=TWO_PI/72;
  InteractiveFrame frame0, frame1;
  PVector[][] huits;

  Bande() {
    rayon=300;
    plus=280;
    frame0=new InteractiveFrame(scene);
    frame1=new InteractiveFrame(scene);
    frame1.setReferenceFrame(frame0);
    huits=new PVector[nbdiv][nbnoeud];
  }

  void remplir() { 
    for (int ic=0;ic<nbdiv;ic++) {
      float  inc=PI/8.0*ic;
      float inc1=PI/8.0+inc;
      for (int i=0;i<nbnoeud;i++) {
        frame0.setOrientation(new Quaternion(new PVector(0, 0, 1), angle*i+inc+millis()*0.0015));
        frame1.setTranslation(new PVector(rayon, 0, 0));
        frame1.setRotation(new Quaternion(new PVector(0, 1, 0), inc/18+angle*i));
        huits[ic][i]=frame1.inverseCoordinatesOf(new PVector(plus, 0, 0));
      }
    }
    noStroke();
    for (int ic=0;ic<nbdiv-1;ic++) {
      for (int i=0;i<nbnoeud-1;i++) {
        fill(255*(ic%2));
        beginShape(TRIANGLES);
        vertex( huits[ic][i].x, huits[ic][i].y, huits[ic][i].z);
        vertex( huits[ic][i+1].x, huits[ic][i+1].y, huits[ic][i+1].z);
        vertex( huits[ic+1][i+1].x, huits[ic+1][i+1].y, huits[ic+1][i+1].z);

        vertex( huits[ic][i].x, huits[ic][i].y, huits[ic][i].z);
        vertex( huits[ic+1][i+1].x, huits[ic+1][i+1].y, huits[ic+1][i+1].z);
        vertex( huits[ic+1][i].x, huits[ic+1][i].y, huits[ic+1][i].z);
        endShape();
      }
    }
  }
}

