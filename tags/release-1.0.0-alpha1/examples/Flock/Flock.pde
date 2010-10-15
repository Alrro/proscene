/**
 * Flock
 * by Matt Wetmore. Adapted to proscene by Jean Pierre Charalambos. 
 * 
 * A more complex example which interactively enables the selection of a frame
 * "avatar" for the camera to follow.
 * 
 * This example displays the famous artificial life program "Boids", developed by
 * Craig Reynolds in 1986.
 * 
 * Boids under the mouse will be colored blue. If you click on a boid it will be
 * selected as the avatar, useful for the THIRD_PERSON proscene camera mode.
 * 
 * Click the space bar to switch between the different camera modes: ARCBALL,
 * WALKTHROUGH, and THIRD_PERSON.
 * 
 * Press 'f' to toggle the drawing of the frames' visual hints.
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
//flock bounding box
int flockWidth = 1280;
int flockHeight = 720;
int flockDepth = 600;
int initBoidNum = 300; // amount of boids to start the program with
BoidList flock1;// ,flock2,flock3;
boolean smoothEdges = false;
boolean avoidWalls = true;

void setup() {
  size(640, 360, P3D);  
  scene = new Scene(this);
  scene.registerCameraProfile( new ThirdPersonCameraProfile(scene, "THIRD_PERSON") );
  scene.background(180,250,250);
  scene.setAxisIsDrawn(false);
  scene.setGridIsDrawn(false);
  scene.setBoundingBox(new PVector(0,0,0), new PVector(flockWidth,flockHeight,flockDepth));
  scene.showAll();
  // create and fill the list of boids
  flock1 = new BoidList(initBoidNum, 255);
  // flock2 = new BoidList(100,255);
  // flock3 = new BoidList(100,128);
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead
  //(which can be called in the setup or here at the beginning of your drawing).  
  ambientLight(128,128,128);
  directionalLight(255, 255, 255, 0, 1, -100);
  noFill();
  stroke(0);

  line(0, 0, 0, 0, flockHeight, 0);
  line(0, 0, flockDepth, 0, flockHeight, flockDepth);
  line(0, 0, 0, flockWidth, 0, 0);
  line(0, 0, flockDepth, flockWidth, 0, flockDepth);

  line(flockWidth, 0, 0, flockWidth, flockHeight, 0);
  line(flockWidth, 0, flockDepth, flockWidth, flockHeight, flockDepth);
  line(0, flockHeight, 0, flockWidth, flockHeight, 0);
  line(0, flockHeight, flockDepth, flockWidth, flockHeight, flockDepth);

  line(0, 0, 0, 0, 0, flockDepth);
  line(0, flockHeight, 0, 0, flockHeight, flockDepth);
  line(flockWidth, 0, 0, flockWidth, 0, flockDepth);
  line(flockWidth, flockHeight, 0, flockWidth, flockHeight, flockDepth);				

  flock1.run(avoidWalls);
  // flock2.run();
  // flock3.run();
  if (smoothEdges)
    smooth();
  else
    noSmooth();
}

void keyPressed() {
  switch (key) {
  case 'u':
    smoothEdges = !smoothEdges;
    break;
  case 'v':
    avoidWalls = !avoidWalls;
    break;
  }
}