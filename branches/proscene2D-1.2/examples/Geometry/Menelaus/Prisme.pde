class Prisme {


  int nb=4;
  InteractiveFrame[] reperes ;
  AxisPlaneConstraint contrainteX, contrainteZ, contrainte4;
  PVector inter12, inter23, inter13;
  float tanang0, haut12, haut13, haut23;

  //
  //
  //
  Prisme() {  
    reperes=new InteractiveFrame[nb];
    for (int i=0;i<nb;i++) {
      reperes[i]=new InteractiveFrame(scene);
    }


    reperes[0].setTranslation(-100, 100, 0);
    reperes[1].setTranslation(80, 100, 0);
    reperes[2].setTranslation(-90, 100, 0);
    reperes[3].setTranslation(-30, 100, 0);

    reperes[0].setRotation(new Quaternion(new PVector(-1, 0, 0), 2.4));

    reperes[1].setRotation(new Quaternion(new PVector(0, 0, 1), -0.6));
    reperes[2].setRotation(new Quaternion(new PVector(0, 0, 1), 1.1));
    reperes[3].setRotation(new Quaternion(new PVector(0, 0, 1), 0.3));
    contrainteX=new WorldConstraint();
    contrainteX.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
    contrainteX.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.1f, 0.0f, 0.0f));
    contrainteZ=new WorldConstraint();
    contrainteZ.setTranslationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.1f, 0.0f, 0.0f));
    contrainteZ.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f, 0.0f, 1.0f));
    reperes[0].setConstraint(contrainteX);
    reperes[1].setConstraint(contrainteZ);
    reperes[2].setConstraint(contrainteZ);
    reperes[3].setConstraint(contrainteZ);
  }         

  void draw() {
    rectangle(color(255, 210, 0, 70), -100, -100, 100, 100);
    //le plan 0
    pushMatrix();
    pushMatrix();
    reperes[0].applyTransformation();
    fill(255, 0, 0);
    noStroke();
    sphere(3);
    Quaternion q= reperes[0].orientation();
    float c0=sq(q.w)-sq(q.x);
    float s0=2.0*q.w*q.x;
    tanang0=s0/c0;
    rectangle(color(255, 255, 55, 70), 0, 0, 200, 200);
    popMatrix();

    //le plan 1
    pushMatrix();
    reperes[1].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix();

    //le plan 2
    pushMatrix();
    reperes[2].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix();

    //le plan 3
    pushMatrix();
    reperes[3].applyTransformation();
    fill(0, 255, 0);
    noStroke();
    sphere(3);
    popMatrix();

    inter12=intersection(1, 2);
    inter23=intersection(2, 3);
    inter13=intersection(1, 3);

    haut12=(-100+ inter12.y)*tanang0; 
    haut23=(-100+ inter23.y)*tanang0;   
    haut13=(-100+ inter13.y)*tanang0;

    dessinerPrisme();
    popMatrix();
  }


  void    rectangle(color c, float dx, float dy, float ax, float ay) {
    stroke(150);
    fill(c);
    beginShape();
    vertex(dx, dy, 0);
    vertex(ax, dy, 0);
    fill(255, 50, 250, 70);
    vertex(ax, ay, 0);
    vertex(dx, ay, 0);
    endShape(CLOSE);
  }
  void    rectanglev(color c, float dy, float dz, float ay, float az) {
    stroke(150);
    fill(c);
    beginShape();
    vertex( 0, dy, dz);
    vertex(0, ay, dz);
    fill(155, 50, 250, 30);
    vertex(0, ay, az);
    vertex(0, dy, az);
    endShape(CLOSE);
  }
  //
  //
  //
  PVector intersection(int i, int j) {
    float lambda ; 
    Quaternion q0= reperes[i].orientation();
    Quaternion q1= reperes[j].orientation();
    float s0=sq(q0.w)-sq(q0.z);
    float c0=-2.0*q0.w*q0.z;
    float s1=sq(q1.w)-sq(q1.z);
    float c1=-2.0*q1.w*q1.z;
    float d=reperes[j].position().x-reperes[i].position().x;
    lambda=d*s1/(c0*s1-c1*s0);
    PVector res= PVector.add(reperes[i].position(), new PVector(lambda*c0, lambda*s0, 0));     
    balle(res);
    return res;
  }

  //
  //
  void balle(PVector res) {
    pushStyle(); 
    pushMatrix();
    translate(res.x, res.y, res.z);
    fill(0, 0, 255);
    noStroke();
    sphere(2);
    stroke(255, 255, 0);
    strokeWeight(4);
    line(0, 0, 0, 0, 0, (-100+res.y)*tanang0);
    translate(0, 0, (-100+res.y)*tanang0);
    fill(0, 0, 255);
    noStroke();
    sphere(2);
    popMatrix(); 
    popStyle();
  }
  void dessinerPrisme() {
    stroke(150);
    triangle(inter12, 1, haut12);
    triangle(inter12, 2, haut12);
    triangle(inter13, 1, haut13);
    triangle(inter13, 3, haut13);         
    triangle(inter23, 2, haut23);
    triangle(inter23, 3, haut23);
  }
  //
  //
  //
  void triangle(PVector inter, int n, float haut) {

    beginShape();
    fill(255, 200, 0, 60);
    vertex( inter.x, inter.y, inter.z);
    fill(155, 255, 0, 60);       
    vertex(inter.x, inter.y, inter.z+haut);
    fill(155, 50, 250, 60);
    vertex(reperes[n].position().x, reperes[n].position().y, reperes[n].position().z);
    endShape();
  }
}//fin de la classe

