//import java.util.ArrayList;
//import processing.core.*;

/*BoidList object class
 * Matt Wetmore
 * Changelog
 * ---------
 * 12/18/09: Started work
 */

class BoidList {
  Scene scene;
  PApplet parent;

  ArrayList boids; // will hold the boids in this BoidList
  float h; // for color

  BoidList(Scene s, int n, float ih) {
    scene = s;
    parent = scene.parent;
    boids = new ArrayList();
    h = ih;
    for (int i = 0; i < n; i++)
      boids.add(new Boid(scene, new PVector(((Flock)parent).flockWidth / 2, ((Flock)parent).flockHeight / 2, ((Flock)parent).flockDepth / 2 )));
  }

  void add() {
    boids.add(new Boid(scene, new PVector(((Flock)parent).flockWidth / 2,((Flock)parent).flockHeight / 2)));
  }

  void addBoid(Boid b) {
    boids.add(b);
  }

  void run(boolean aW) {
    for (int i = 0; i < boids.size(); i++) // iterate through the list of
      // boids
    {
      Boid tempBoid = (Boid) boids.get(i); // create a temporary boid to
      // process and make it the
      // current boid in the list
      tempBoid.hue = h;
      tempBoid.avoidWalls = aW;
      tempBoid.run(boids); // tell the temporary boid to execute its run
      // method
    }
  }
}