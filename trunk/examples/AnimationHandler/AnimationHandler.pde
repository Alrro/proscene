/**
 * Animation.
 * by Jean Pierre Charalambos.
 * 
 * The animate() function illustrated by a water particle simulation.
 *
 * When animation is activated, the animate() and then the parent.redraw()
 * (which in turn calls {@code PApplet.draw()}) functions are called in an
 * infinite loop.
 * 
 * You can tune the frequency of your animation (default is 25Hz) using
 * setAnimationPeriod(). The frame rate will then be fixed, provided that
 * your animation loop function is fast enough.
 *
 * Press 'm' to toggle (start/stop) animation.
 * Press 'x' to decrease the animation period (animation speeds up).
 * Press 'y' to decrease the animation period (animation speeds down).
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

int nbPart;
Particle[] particle;
Scene scene;

void animateScene(Scene s) {  
  for (int i = 0; i < nbPart; i++)
    if(particle[i] != null)
      particle[i].animate();
}

void setup() {
  size(640, 360, P3D);
  scene = new Scene(this); 
  scene.setAxisIsDrawn(false);
  nbPart = 2000;
  particle = new Particle[nbPart];     
  for (int i = 0; i < particle.length; i++)
    particle[i] = new Particle();
  scene.addAnimationHandler(this, "animateScene");
  // press 'm' to start/stop animation
  scene.setShortcut('m', Scene.KeyboardAction.ANIMATION);
  scene.setAnimationPeriod(40); // 25Hz
  scene.startAnimation();
  smooth();
}

// Make sure to define the draw() method, even if it's empty.
void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  pushStyle();
  strokeWeight(3); // Default
  beginShape(POINTS);
  for (int i = 0; i < nbPart; i++) {
    particle[i].draw();
  }
  endShape();
  popStyle();
}

void keyPressed() {
  if((key == 'x') || (key == 'X'))
    scene.setAnimationPeriod(scene.animationPeriod()-2);
  if((key == 'y') || (key == 'Y'))
    scene.setAnimationPeriod(scene.animationPeriod()+2);
}