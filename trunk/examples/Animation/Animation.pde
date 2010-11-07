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

MyScene scene;

void setup() {
  size(640, 360, P3D);
  // We instantiate our MyScene class defined below
  scene = new MyScene(this);
}

// Make sure to define the draw() method, even if it's empty.
void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
}

void keyPressed() {
  if((key == 'x') || (key == 'X'))
    scene.setAnimationPeriod(scene.animationPeriod()-2);
  if((key == 'y') || (key == 'Y'))
    scene.setAnimationPeriod(scene.animationPeriod()+2);
}

class MyScene extends Scene {
  int nbPart;
  Particle[] particle;

  // We need to call super(p) to instantiate the base class
  public MyScene(PApplet p) {
    super(p);
  }

  // Initialization stuff could have also been performed at
  // setup(), once after the Scene object have been instantiated
  public void init() {
    nbPart = 2000;
    particle = new Particle[nbPart];
    setShortcut('m', Scene.KeyboardAction.ANIMATION);    
    for (int i = 0; i < particle.length; i++)
      particle[i] = new Particle();
    setAxisIsDrawn(false);
    setAnimationPeriod(40); // 25Hz
    startAnimation();
    smooth();
  }

  // Define here what is actually going to be drawn.
  public void proscenium() {
    parent.pushStyle();
    strokeWeight(3); // Default
    beginShape(POINTS);
    for (int i = 0; i < nbPart; i++) {
      particle[i].draw();
    }
    endShape();
    parent.popStyle();
  }

  // Define here your animation.
  public void animate() {
    for (int i = 0; i < nbPart; i++)
      if(particle[i] != null)
        particle[i].animate();
  }
}