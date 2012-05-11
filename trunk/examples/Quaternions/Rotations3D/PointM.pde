class PointM {
  InteractiveFrame repere, util;
  PVector mpos, phimpos, psiphimpos;//le point M et des symetriques
  PointM() {
    repere=new InteractiveFrame(scene);
    util =new InteractiveFrame(scene);
    repere.setPosition(new PVector(200, -200, 150));
    mpos=new PVector(140, -220, 20);
    phimpos=new PVector();
    psiphimpos=new PVector();
  }

  void draw() {
    pushMatrix();
    repere.applyTransformation();
    fill(255, 255, 0);
    box(10);
    popMatrix();
    mpos=repere.position();

    phimpos=qphi.multiply(mpos);//rotate
    psiphimpos=qpsi.multiply(phimpos);


    cercle(mpos, pt1.repere.position(), phi, color(255, 0, 0), avancer1, phi0);
    cercle(phimpos, pt2.repere.position(), psi, color(0, 0, 255), avancer2, psi0);
    cercle(mpos, qcomp.axis(), qcomp.angle(), color(0, 255, 0), avancer3, phipsi0);
  }

  void cercle(PVector pt, PVector u, float aaa, int coul, boolean bool, float rho) {
    PVector uu=u.get();
    uu.normalize();
    PVector pp=PVector.mult( uu, uu.dot(pt));
    util.setZAxis(uu);
    util.setPosition(pp);
    util.setXAxis(comb(1, pt, -1, pp));//util_X est au point de départ
    float d=(comb(2, pt, -2, pp)).mag();

    pushMatrix();
    util.applyTransformation();
    PVector rep0=util.coordinatesOf(o);
    ellipseMode(CENTER);
    fill(255, 0, 0, 50);
    stroke(0);
    ellipse(0, 0, d, d);
    arcad(d/2.0, aaa, rep0.z, coul, bool, rho);
    popMatrix();
  }


  void arcad(float ray, float an, float zz, int co, boolean boo, float rhoo) {
    fill(155, 0, 0, 254);
    noStroke();
    float alph=an/50.0;
    beginShape(TRIANGLE_FAN);

    vertex(0, 0, 0);
    for (int i=0; i<51;i++) {
      fill(155, 250-i, 190-2*i, 200);  
      vertex(ray*cos(alph*i), ray*sin(alph*i), 0);
    }
    endShape();
    stroke(co);
    strokeWeight(5);
    for (int i=0; i<50;i++) {
      line(ray*cos(alph*i), ray*sin(alph*i), 0, ray*cos(alph*(i+1)), ray*sin(alph*(i+1)), 0);
    }
    strokeWeight(1);
    if (boo) {
      pushMatrix();
      translate(ray*cos(rhoo), ray*sin(rhoo), 70);
      fill(140);
      noStroke();
      rotateY(PI);
      scene.drawArrow(70);



      popMatrix();
    }
    /* beginShape(TRIANGLE_FAN);
     //noFill();  stroke(0,100,50,250);
     fill(55, 170, 170);
     vertex(0, 0, zz);
     for (int i=0; i<51;i++) {
     fill(55+3*i, 170+i, 170+i);
     vertex(ray*cos(alph*i), ray*sin(alph*i), 0);
     }
     endShape();*/
  }
}

