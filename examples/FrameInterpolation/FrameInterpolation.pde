/**
 * <b>Frame Interpolation</b> by Jean Pierre Charalambos.
 * <p>
 * This example (together with Camera Interpolation) illustrates the KeyFrameInterpolator<br>
 * functionality.
 * <p>
 * KeyFrameInterpolator smoothly interpolate its attached Frame over time on a path<br>
 * defined by Frames. The interpolation can be started/stopped/reset, played in loop,<br>
 * played at a different speed, etc...
 * <p>
 * In this example, the path is defined by four InteractivedFrames which can be moved<br>
 * with the mouse. The interpolating path is updated accordingly. The path and the<br>
 * interpolating axis are drawn using KeyFrameInterpolator.drawPath().
 * <p>
 * The Camera holds 5 KeyFrameInterpolators, binded to <b>[j..n]/[J..N]/[1..5]</b> keys:<br>
 * set/reset/play the key frame interpolators, respectively. Press <b>'r'</b> to display all<br>
 * the key frame camera paths (if any).
 * <p>
 * Press <b>'h'</b> to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
InteractiveFrame keyFrame[];
KeyFrameInterpolator kfi;
int nbKeyFrames;

void setup() {
  size(640, 360, P3D);
  nbKeyFrames = 4;
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setGridIsDrawn(false);
  scene.setRadius(70);
  scene.showAll();
  scene.setFrameSelectionHintIsDrawn(true);
  kfi = new KeyFrameInterpolator(this);
  kfi.setLoopInterpolation();
  
  // An array of interactive (key) frames.
  keyFrame = new InteractiveFrame[nbKeyFrames];
  // Create an initial path
  for (int i=0; i<nbKeyFrames; i++) {
    keyFrame[i] = new InteractiveFrame();
    keyFrame[i].setPosition(-100 + 200*i/(nbKeyFrames-1), 0, 0);
    kfi.addKeyFrame(keyFrame[i]);
  }
  
  kfi.startInterpolation();
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  pushMatrix();
  kfi.frame().applyTransformation(this);
  scene.drawAxis(30);
  popMatrix();
  
  kfi.drawPath(5, 10);
  
  for (int i=0; i<nbKeyFrames; ++i) {      
    pushMatrix();
    kfi.keyFrame(i).applyTransformation(this);
    
    if ( keyFrame[i].grabsMouse() )
      scene.drawAxis(40);
    else
      scene.drawAxis(20);
      
    popMatrix();
  }
}

void keyPressed() {
  if ((key == ENTER) || (key == RETURN))
  kfi.toggleInterpolation();
  if (key == CODED) {
    if (keyCode == UP)
      kfi.setInterpolationSpeed(kfi.interpolationSpeed()+0.25f);
    if (keyCode == DOWN)
      kfi.setInterpolationSpeed(kfi.interpolationSpeed()-0.25f);
  }
}