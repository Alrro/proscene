/**
 * <b>Third Person Camera</b> by Jean Pierre Charalambos.
 * <p>
 * This example illustrates the THIRD_PERSON proscene camera mode.
 * <p>
 * The THIRD_PERSON camera mode is enabled once a scene.avatar() is set by calling<br>
 * scene.setAvatar(). Any object implementing the Trackable interface may be defined<br>
 * as the avatar.
 * <p>
 * Since the InteractiveAvatarFrame class is an InteractiveFrame that implements the<br>
 * Trackable interface we may set an instance of it as the avatar by calling<br>
 * scene.setInteractiveFrame() (which automatically calls scene.setAvatar()).<br>
 * When the camera mode is set to THIRD_PERSON you can then manipulate your<br>
 * interactive frame with the mouse and the camera will follow it.
 * <p>
 * Click the <b>space bar</b> to change between the camera modes: ARCBALL, WALKTHROUGH,<br>
 * and THIRD_PERSON.
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

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
  
  // This also sets the scene.avatar() by automatically calling scene.setAvatar()
  // (provided that the interactive frame is an instance of the InteractiveAvatarFrame class).
  scene.setInteractiveFrame(avatar);
  scene.setCameraMode( Scene.CameraMode.THIRD_PERSON );
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
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