class Interpolation {
  InteractiveFrame keyFrame[];
  KeyFrameInterpolator kfi;
  int nbKeyFrames;  
  boolean enmarche;

  Interpolation( PVector but, Quaternion qbut) {
    kfi = new KeyFrameInterpolator(scene);
    nbKeyFrames = 3;
    keyFrame=new  InteractiveFrame[nbKeyFrames];
    enmarche=false;

    for (int i=0; i<nbKeyFrames; i++) {
      keyFrame[i] = new InteractiveFrame(scene);
      kfi.addKeyFrame(keyFrame[i]);
    }
    keyFrame[0].setPosition(new PVector(random(-600, -400), random(-500, -200), -500));
    keyFrame[0].setOrientation(new Quaternion(new PVector(random(-3, 3), random(-3, 3), random(-3, 3)), random(-2, 3.2))); 
    keyFrame[1].setPosition(new PVector(random(-300, -150), 30+random(100, 150), random(180, 240))); 
    keyFrame[1].setOrientation(new Quaternion(new PVector(random(-3, 3), random(-3, 3), random(-3, 3)), random(2, 6.2))); 
    keyFrame[2].setPosition(but);
    keyFrame[2].setOrientation(qbut);
    kfi.setLoopInterpolation(false);
    kfi.setInterpolationSpeed(0.4);
  }
  
  void dessin(float lng) {
    if (!kfi.interpolationIsStarted()&& !enmarche) { 
      kfi.startInterpolation();
      enmarche=true;
    }
    pushMatrix();
    kfi.frame().applyTransformation(scene);
    noStroke();
    scene.cone(4,5,5,lng);
    popMatrix();
  }
}

