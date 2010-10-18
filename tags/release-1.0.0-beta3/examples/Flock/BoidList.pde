class BoidList {
  ArrayList boids; // will hold the boids in this BoidList
  float h; // for color

  BoidList(int n, float ih) {
    boids = new ArrayList();
    h = ih;
    for (int i = 0; i < n; i++)
      boids.add(new Boid(new PVector(flockWidth/2, flockHeight/2, flockDepth/2 )));
  }

  void add() {
    boids.add(new Boid(new PVector(flockWidth/2, flockHeight/2)));
  }

  void addBoid(Boid b) {
    boids.add(b);
  }

  void run(boolean aW) {
    // iterate through the list of boids
    for (int i = 0; i < boids.size(); i++) {
      // create a temporary boid to process and make it the current boid in the list
      Boid tempBoid = (Boid) boids.get(i); 
      tempBoid.hue = h;
      tempBoid.avoidWalls = aW;
      tempBoid.run(boids); // tell the temporary boid to execute its run method
    }
  }
}