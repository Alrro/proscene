package test;

import processing.core.PApplet;

//An utility class to calculate framerate more accurately.
class Chronometer {
  PApplet parent;
  int fcount;
  int lastmillis;
  int interval;
  float fps;
  float time;
  boolean updated;

  Chronometer(PApplet parent) {
    this.parent = parent;
    lastmillis = 0;
    fcount = 0;
    interval = 5;
    updated = false;
  }

  Chronometer(PApplet parent, int t) {
    this.parent = parent;    
    lastmillis = 0;
    fcount = 0;
    interval = t;
    updated = false;
  }

  void update() {
    fcount++;
    int t = parent.millis();
    if (t - lastmillis > interval * 1000) {
      fps = (float) (fcount) / interval;
      time = (float) (t) / 1000;
      fcount = 0;
      lastmillis = t;
      updated = true;
    } else
      updated = false;
  }

  void printFps() {
    if (updated)
      PApplet.println("FPS: " + fps);
  }
}

