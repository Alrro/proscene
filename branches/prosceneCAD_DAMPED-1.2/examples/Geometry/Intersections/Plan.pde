class Plan {
  int nb=5;
  InteractiveFrame[] reperes;
  AxisPlaneConstraint contrainteDuPlan, contrainteDroite, contrainte4;
  PVector[][] intersections;

  Plan() {
    intersections=new PVector[nb][nb];
    creerTable();
    reperes=new InteractiveFrame[nb];
    for (int i=0;i<nb;i++) {
      reperes[i]=new InteractiveFrame(scene);
      if (i>0) reperes[i].setReferenceFrame( reperes[0]);
    }

    // Initialize frames
    reperes[1].setTranslation(80, -80, 0);
    reperes[2].setTranslation(150, -20, 0);
    reperes[3].setTranslation(100, -180, 0);
    reperes[4].setTranslation(100, 0, 0);
    reperes[1].setRotation(new Quaternion(new PVector(0, 0, 1), 0.6));
    reperes[2].setRotation(new Quaternion(new PVector(0, 0, 1), 1.1));
    reperes[3].setRotation(new Quaternion(new PVector(0, 0, 1), -1.3));
    reperes[4].setRotation(new Quaternion(new PVector(0, 0, 1), 0.0));
    contrainteDuPlan=new LocalConstraint();
    contrainteDuPlan.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
    contrainteDuPlan.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.1f, 0.0f, 0.0f));

    contrainteDroite=new LocalConstraint();
    contrainteDroite.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0.0f, 0.0f, 1.0f));
    contrainteDroite.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f, 0.0f, 1.0f));

    contrainte4=new LocalConstraint();
    contrainte4.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));
    contrainte4.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new PVector(0.0f, 0.0f, 0.0f));


    reperes[0].setConstraint(contrainteDuPlan);
    reperes[1].setConstraint(contrainteDroite);
    reperes[2].setConstraint(contrainteDroite);
    reperes[3].setConstraint(contrainteDroite);
    reperes[4].setConstraint(contrainte4);
  }

  void draw() {
    rotateY(-PI/4);
    rectangle(color(0, 0, 255, 98));
    rotateX(-PI/4);
    pushMatrix();// on quite le monde pour reperes[0]

    reperes[0].applyTransformation();// reperes[0] devient le repere de ref
    scene.drawAxis(80); 

    noStroke();
    fill(0, 255, 0);
    sphere(6);
    fill(255, 0, 0, 98);
    beginShape();
    vertex(0, 0, 0);
    vertex(0, -200, 0);
    vertex(200, -200, 0);
    vertex(200, 0, 0);
    endShape(CLOSE);

    pushMatrix();
    reperes[1].applyTransformation();
    drawLine(1, color(255, 0, 0));
    popMatrix();
    pushMatrix();
    reperes[2].applyTransformation();
    drawLine(2, color(255, 0, 0));
    popMatrix();
    pushMatrix();
    reperes[3].applyTransformation();
    drawLine(3, color(0, 0, 255));
    popMatrix();
    pushMatrix();
    reperes[4].applyTransformation();
    drawLine(4, color(255));
    popMatrix();
    //  **********************
    calculerIntersections();


    popMatrix();// retour au monde
  }

  void   drawLine(int n, color c) {
    if (n!=4) {
      noStroke();
      fill(c);
      sphere(3);
    };
    fill(255, 0, 0, 98);
    stroke(0);
    line(-200, 0, 0, 200, 0, 0);
  }

  float det(float a, float b, float ap, float bp) {
    return a*bp-ap*b;
  }

  PVector cramer(float a, float b, float c, float ap, float bp, float cp) {
    float d=det(a, ap, b, bp);
    float dx=det(c, cp, b, bp);
    float dy=det(a, ap, c, cp);
    return new PVector(dx/d, dy/d, 0);
  }

  PVector intersection(InteractiveFrame f1, InteractiveFrame f2) {
    Quaternion q1=f1.rotation();
    float c1=q1.w*q1.w-q1.z*q1.z;
    float s1=2.0*q1.w*q1.z;
    Quaternion q2=f2.rotation();
    float c2=q2.w*q2.w-q2.z*q2.z;
    float s2=2.0*q2.w*q2.z;
    PVector res=cramer(-s1, c1, -s1*f1.translation().x+c1*f1.translation().y, 
    -s2, c2, -s2*f2.translation().x+c2*f2.translation().y);
    return res;
  }

  void creerTable() {
    for (int i=0;i<nb;i++) {
      for (int j=0;j<nb;j++) {
        intersections[i][j]=new PVector();
      }
    }
  }

  void  calculerIntersections() {
    for (int i=1;i<nb;i++) {
      for (int j=i+1;j<nb;j++) {
        intersections[i][j]=intersection(reperes[i], reperes[j]);
        pushMatrix();    
        translate(intersections[i][j].x, intersections[i][j].y, intersections[i][j].z);
        fill(255, 255, 0);
        noStroke();
        sphere(2);
        popMatrix();
      }
    }
  }

  void rectangle(color c) {
    fill(c);
    beginShape();
    vertex(0, 0, 0);
    vertex(0, -200, 0);
    vertex(200, -200, 0);
    vertex(200, 0, 0);
    endShape(CLOSE);
  }
}