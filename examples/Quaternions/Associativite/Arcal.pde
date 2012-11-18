class Arcal {
  float angle, raycercle, zcercle;
  PVector axe, ocentre, depart, arrivee;
  Quaternion quat;
  InteractiveFrame repere;
  String texte;

  Arcal (Quaternion q, PVector dep, PVector arr, String tex) {
    depart=dep;
    arrivee=arr;
    quat=q;
    axe=quat.axis();
    angle=quat.angle();
    texte=tex;
    ocentre=projectionSurDroite(depart, axe);
    zcercle=sqrt(sq(raySphere)-ocentre.dot(ocentre));
    raycercle=sqrt(sq(raySphere)-sq(zcercle));
    repere=new InteractiveFrame(scene);
    repere.setZAxis(axe);
    repere.setXAxis(comb(1, depart, -1, ocentre));
    repere.setPosition(ocentre);
  }

  void draw() {
    pushMatrix();
    repere.applyTransformation();
    if (montretout) {
      fill(0, 255, 0, 50);
      stroke(255, 0, 0);
      ellipse(0, 0, 2*raycercle, 2*raycercle);
    }
    popMatrix();
    dessinerArc();
  }

  void dessinerArc() {
    stroke(255, 0, 0);
    strokeWeight(6);
    ligne(repere.position(), or);
    strokeWeight(1);  
    pushMatrix();
    repere.applyTransformation();
    fill(215, 210, 255, 30);
    float r=raycercle;
    noStroke();
    fill(255, 0, 0);

    beginShape(QUAD_STRIP); 

    for (int a=0;a<=100;a++) {
      float aa=angle/100*a;
      vertex(r*cos(aa), r*sin(aa), 3);
      vertex(r*cos(aa), r*sin(aa), 23);
    }
    endShape();

    fill(255, 250, 100);
    beginShape(TRIANGLE_FAN); 
    vertex(0, 0, 0);
    fill(255, 155, 150);
    for (int a=0;a<50;a++) {
      float aa=angle*a/50.0;
      vertex(r*cos(aa), r*sin(aa), 0);
    }
    vertex(r*cos(angle), r*sin(angle), 0);
    endShape();
    translate(r*cos(angle/2.0), r*sin(angle/2.0), 0);
    text(texte, 30, 30);
    popMatrix();
  }

  void init(Quaternion q, PVector dep, PVector arr) { 
    quat=q;
    axe=quat.axis();
    //angle=angleQuaternion(quat);
    angle=quat.angle();
    depart=dep;
    arrivee=arr;
    ocentre=projectionSurDroite(depart, axe);
    zcercle=ocentre.mag();
    raycercle=sqrt(sq(raySphere)-sq(zcercle));
    repere.setPosition(ocentre);
    repere.setZAxis(axe);
    repere.setXAxis(comb(1, depart, -1, ocentre));
  }
}
