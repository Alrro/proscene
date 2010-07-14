/**
 * Flock by Matt Wetmore. Adapted to proscene by Jean Pierre Charalambos. 
 * 
 * A more complex example which enables enables interactively the selection
 * of a frame "avatar" for the camera to follow.
 * 
 * This example displays the famous artificial life program "Boids", developed by
 * Craig Reynolds in 1986.
 *
 * Boids under the mouse will be colored blue. If you click on a boid it will be
 * selected as the the avatar, useful for the THIRD_PERSON proscene camera mode.
 *
 * Click the space_bar to change between the camera modes (ARCBALL, WALKTHROUGH,
 * and THIRD_PERSON). 
 *
 * Press 'f' to toggle the drawing of the frames' visual hints.
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

/*Main runner area
 * Matt Wetmore
 * Changelog
 * ---------
 * 12/14/09: Started work
 * 12/18/09: Reimplemented with BoidList class
 */

import remixlab.proscene.*;

Scene scene;
int flockWidth, flockHeight, flockDepth;
int initBoidNum = 300; // amount of boids to start the program with
BoidList flock1;// ,flock2,flock3;
boolean smoothEdges = false;
boolean avoidWalls = true;

void setup() {
  size(640, 360, P3D);
  flockWidth = 1200;
  flockHeight = 600;
  flockDepth = 600;
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setGridIsDrawn(false);
  scene.setHelpIsDrawn(false);		
  scene.setRadius(600);
  scene.setCenter(new PVector(flockWidth/2, flockHeight/2, flockDepth/2));
  scene.showAll();
  // create and fill the list of boids
  flock1 = new BoidList(scene, initBoidNum, 255);
  // flock2 = new BoidList(100,255);
  // flock3 = new BoidList(100,128);
}

void draw() {	
  scene.background(190,220,220);
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