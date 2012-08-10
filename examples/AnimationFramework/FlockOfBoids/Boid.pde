class Boid {

  PVector pos, vit, acc;
  PVector oz=new PVector(0, 0, 1), oy;
  float  period = random(0.14, 0.16); 

  Boid() {
    pos=new PVector(random(-large, large), random(-large, large), random(-haut, haut));
    vit=new PVector( random(-2, 2), random(-2, 2), random(-2, 2));
    acc=new PVector();
    vit.limit(5);
  }

  void frontieres() {
    if (pos.x<-large) pos.x =large;
    if (pos.x>large)  pos.x =-large;
    if (pos.y<-large) pos.y =large;
    if (pos.y>large)  pos.y =-large;
    if (pos.z<-haut)  pos.z =haut;
    if (pos.z>haut)   pos.z=-haut;
  } 

  void draw() {
    vit.add(acc);
    vit.limit(maxSpeed);
    pos.add(vit);
    frontieres();

    oy=vit.cross(oz );
    repere.setZAxis(vit);
    repere.setYAxis(oy);
    repere.setZAxis(vit);
    repere.setPosition(pos);
    noStroke();
    fill(100, 0, 100);
    pushMatrix();
    repere.applyTransformation();
    scale(1.4);
    scene.cone(6, 4, 0, 14);
    fill(255, 255, 105);
    scene.cone(6, 4, 1, -12);
    translate(0, 0, 8);
    fill(255, 0, 0);
    sphere(3);
    translate(0, 0, -8);
    float h=12*cos(millis()*period);
    beginShape(TRIANGLES);
    vertex(0, 2, -10);
    vertex(-h/2, 7, -23);
    vertex(0, -3, -13);
    vertex(0, 0, -10);
    vertex(-h/2, -7, -23);
    vertex(0, -3, -13);
    fill(255);
    vertex(0, 2, 7);
    vertex(h, 15, -3);
    vertex(0, 0, 0);
    vertex(0, -2, 7);
    vertex(h, -15, -3);
    vertex(0, 0, 0);

    endShape();
    popMatrix();
  }
}
