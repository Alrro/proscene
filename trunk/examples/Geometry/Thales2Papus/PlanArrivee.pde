class PlanArrivee {

  InteractiveFrame repere;

  float posix, angle, largeur, longueur;
  PVector normale, direc;
  color col;
  float lambda2, lambda3;

  PlanArrivee(float posx, float an) {
    posix=posx;
    angle=an;
    longueur=1000;
    largeur=1600;
    normale=new PVector(0, 0, 1);
    col=color(130, 100, 120, 254);
    repere=new InteractiveFrame(scene);
    repere.setTranslation(posx, 0, 0);
    repere.setRotation(new Quaternion(new PVector(1, 0, 0), angle));
    repere.setConstraint(contrainteX);
  }

  void draw() {
    pushMatrix();
    repere.applyTransformation(); 
    rectangle(col, 0, 0, longueur, largeur );
    balle(1);
    text("into PAPPUS", 700, 700);  
    popMatrix();
  }

  void getNormaleDansWorld() {
    normale=  repere.inverseTransformOf(new PVector(0, 0, 1));
  }
}

