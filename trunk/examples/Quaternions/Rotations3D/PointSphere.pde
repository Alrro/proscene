class PointSphere {
  InteractiveFrame repere, dragueur, alidade0, alidade1;
  Quaternion quat2;
  LocalConstraint planaire;
  float angleRot;

  PointSphere(PVector pos, float fi) {
    quat2=new Quaternion();
    repere=new InteractiveFrame(scene);
    dragueur=new InteractiveFrame(scene);
    alidade0=new InteractiveFrame(scene);
    alidade1=new InteractiveFrame(scene);
    alidade0.setReferenceFrame(repere);
    alidade1.setReferenceFrame(repere);

    repere.setPosition(new PVector(200, 0, 0));
    alidade0.setTranslation(new PVector(150, 0, 0));
    alidade1.setTranslation(new PVector(150*cos(fi), 150*sin(fi), 0));
    dragueur.setPosition(pos);

    planaire=new LocalConstraint();
    planaire.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0, 0, 1));
    planaire.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0, 0, 0));
    alidade0.setConstraint(planaire);
    alidade1.setConstraint(planaire);
  }

  void draw(PVector posit) {
    scene.drawAxis();
    pushMatrix();
    dragueur.applyTransformation();
    noStroke();
    fill(150, 0, 0);
    sphere(14);
    popMatrix();
    PVector p=dragueur.position().get();
    p.normalize();
    repere.setZAxis(p);
    repere.setPosition(projectionSurDroite(posit, p));

    pushMatrix();
    repere.applyTransformation();
    pushMatrix();
    alidade0.applyTransformation();
    noStroke();
    fill(0, 225, 0);
    sphere(8);
    popMatrix();
    pushMatrix();
    alidade1.applyTransformation();
    noStroke();
    fill(0, 0, 255);
    sphere(8);
    popMatrix();
    popMatrix();
    stroke(0);

    PVector pa0=projectionSurDroite(alidade0.position(), repere.position());
    PVector pa1=projectionSurDroite(alidade1.position(), repere.position());
    PVector reppa0=repere.transformOf(comb(1, alidade0.position(), -1, pa0));
    PVector reppa1=repere.transformOf(comb(1, alidade1.position(), -1, pa1));
    reppa0.normalize();
    reppa1.normalize();
    ligne(alidade0.position(), pa0);
    ligne(dragueur.position(), o);
    ligne(alidade1.position(), pa1);

    float cosi=reppa0.dot(reppa1);
    PVector orth=reppa0.cross(reppa1);
    float sinu=orth.z;

    // println((sq(orth.z)+sq(cosi)));//verification
    angleRot=acos(cosi);
    if (sinu<0) angleRot=TWO_PI-angleRot;
    quat2=new Quaternion(p, angleRot);
  }
}
