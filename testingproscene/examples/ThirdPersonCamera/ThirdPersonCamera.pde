import remixlab.proscene.*;

Scene scene;
InteractiveAvatarFrame avatar;

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this);
  scene.setRadius(400);
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  
  avatar = new InteractiveAvatarFrame(scene);
  avatar.setTrackingDistance(300);
  avatar.setAzimuth(PI/12);
  avatar.setInclination(avatar.inclination() + PI/6);
  
  WorldConstraint baseConstraint = new WorldConstraint();
  baseConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new PVector(0.0f,1.0f,0.0f));
  baseConstraint.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new PVector(0.0f,1.0f,0.0f));
  avatar.setConstraint(baseConstraint);
  
  scene.setInteractiveFrame(avatar);
  scene.setCameraMode( Scene.CameraMode.THIRD_PERSON );
}

void draw() {
  // Save the current model view matrix
  pushMatrix();
  // Multiply matrix to get in the frame coordinate system.
  // applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient
  scene.interactiveFrame().applyTransformation(this);//very efficient
  // Draw an axis using the Scene static function
  scene.drawAxis(20);
  if (scene.interactiveFrameIsDrawn())
    fill(255, 0, 0);
  else
    fill(0,0,255);
  box(15, 20, 30);
  popMatrix();
  
  //draw the ground
  noStroke();
  fill(120, 120, 120);
  beginShape();
  vertex(-400, 10, -400);
  vertex(400, 10, -400);
  vertex(400, 10, 400);
  vertex(-400, 10, 400);
  endShape(CLOSE);
}