/**
 * Key Frames.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates the KeyFrameInterpolator functionality.
 *
 * KeyFrameInterpolator smoothly interpolate their attached Frame over time on
 * a path defined by Frames. The interpolation can be started/stopped/reset,
 * played in loop, played at a different speed, etc...
 *
 * In this example, the path is defined by four InteractivedFrames which can be
 * moved with the mouse. The interpolating path is updated accordingly. The path
 * and the interpolating axis are drawn using KeyFrameInterpolator.drawPath().
 *
 * The Camera holds 5 KeyFrameInterpolators, binded to [j..n]/[J..N]/[1..5] keys:
 * set/reset/play the key frame interpolators, respectively. Press 'r' to display
 * all the key frame camera paths (if any).
 *
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import processing.core.*;
import remixlab.proscene.*;


Scene scene;
InteractiveFrame keyFrame[];
KeyFrameInterpolator kfi;
int nbKeyFrames;

void setup() {
  size(640, 360, P3D);
  nbKeyFrames = 4;
  scene = new Scene(this);
  scene.setRadius(scene.radius()*0.7f);
  scene.showAll();
  kfi = new KeyFrameInterpolator();
  kfi.setLoopInterpolation();
  
  // An array of interactive (key) frames.
  keyFrame = new InteractiveFrame[nbKeyFrames];
  // Create an initial path
  for (int i=0; i<nbKeyFrames; i++) {
    keyFrame[i] = new InteractiveFrame();
    keyFrame[i].setPosition(-1.0f + 2.0f*i/(nbKeyFrames-1), 0.0f, 0.0f);
    kfi.addKeyFrame(keyFrame[i]);
  }
  
  kfi.startInterpolation();
}

void draw() {
  background(0);
  scene.beginDraw();
  pushMatrix();
  kfi.frame().applyTransformation(this);
  Scene.drawAxis(0.3f);
  popMatrix();
  
  kfi.drawPath(5, 10);
  
  for (int i=0; i<nbKeyFrames; ++i) {
    // Hack: it is necessary to reset the KeyFrameInterpolator cache each time
    // one of its frames changes. We do here a soft test: we check if the frame
    // grabs the mouse or if it is spinning.
    if ( keyFrame[i].grabsMouse() || keyFrame[i].isSpinning() )
      kfi.invalidateValues();// resets KeyFrameInterpolator cache
      
    pushMatrix();
    kfi.keyFrame(i).applyTransformation(this);
    
    if ( keyFrame[i].grabsMouse() )
      Scene.drawAxis(0.4f);
    else
      Scene.drawAxis(0.2f);
      
    popMatrix();
  }
  scene.endDraw();
}
	
void keyPressed() {
  scene.defaultKeyBindings();
  if ((key == ENTER) || (key == RETURN))
  kfi.toggleInterpolation();
  if (key == CODED) {
    if (keyCode == UP)
      kfi.setInterpolationSpeed(kfi.interpolationSpeed()+0.25f);
    if (keyCode == DOWN)
      kfi.setInterpolationSpeed(kfi.interpolationSpeed()-0.25f);
  }
}
