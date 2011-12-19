class Boid {
  InteractiveAvatarFrame frame;
  Quaternion q;
  int grabsMouseColor;//color
  int avatarColor;

  // fields
  Vector3D pos, vel, acc, ali, coh, sep; // pos, velocity, and acceleration in
  // a vector datatype
  float neighborhoodRadius; // radius in which it looks for fellow boids
  float maxSpeed = 4; // maximum magnitude for the velocity vector
  float maxSteerForce = .1f; // maximum magnitude of the steering vector
  float sc = 3; // scale factor for the render of the boid
  float flap = 0;
  float t = 0;

  // constructors
  Boid(Vector3D inPos) {
    grabsMouseColor = color(0,0,255);
    avatarColor = color(255,0,0);		
    pos = new Vector3D();
    pos.set(inPos);
    frame = new InteractiveAvatarFrame(scene);	
    frame.setPosition(pos);
    frame.setAzimuth(-HALF_PI);
    frame.setInclination(PI*(4/5));
    frame.setTrackingDistance(scene.radius()/10);
    vel = new Vector3D(random(-1, 1), random(-1, 1), random(1, -1));
    acc = new Vector3D(0, 0, 0);
    neighborhoodRadius = 100;
  }

  Boid(Vector3D inPos, Vector3D inVel, float r) {
    grabsMouseColor = color(0,0,255);
    avatarColor = color(255,0,0);
    pos = new Vector3D();
    pos.set(inPos);
    frame = new InteractiveAvatarFrame(scene);
    frame.setPosition(pos);
    frame.setAzimuth(-HALF_PI);
    frame.setTrackingDistance(scene.radius()/10);
    vel = new Vector3D();
    vel.set(inVel);
    acc = new Vector3D(0, 0);
    neighborhoodRadius = r;
  }

  void run(ArrayList bl) {
    t += .1;
    flap = 10 * sin(t);
    // acc.add(steer(new Vector3D(mouseX,mouseY,300),true));
    // acc.add(new Vector3D(0,.05,0));
    if (avoidWalls) {
      acc.add(Vector3D.mult(avoid(new Vector3D(pos.x(), flockHeight, pos.z()), true), 5));
      acc.add(Vector3D.mult(avoid(new Vector3D(pos.x(), 0, pos.z()), true), 5));
      acc.add(Vector3D.mult(avoid(new Vector3D(flockWidth, pos.y(), pos.z()),	true), 5));
      acc.add(Vector3D.mult(avoid(new Vector3D(0, pos.y(), pos.z()), true), 5));
      acc.add(Vector3D.mult(avoid(new Vector3D(pos.x(), pos.y(), 0), true), 5));
      acc.add(Vector3D.mult(avoid(new Vector3D(pos.x(), pos.y(), flockDepth), true), 5));
    }
    flock(bl);
    move();
    checkBounds();
    //render();
  }

  // ///-----------behaviors---------------
  void flock(ArrayList bl) {
    ali = alignment(bl);
    coh = cohesion(bl);
    sep = seperation(bl);
    acc.add(Vector3D.mult(ali, 1));
    acc.add(Vector3D.mult(coh, 3));
    acc.add(Vector3D.mult(sep, 1));
  }

  void scatter() {

  }

  // //------------------------------------

  void move() {
    vel.add(acc); // add acceleration to velocity
    vel.limit(maxSpeed); // make sure the velocity vector magnitude does not
    // exceed maxSpeed
    pos.add(vel); // add velocity to position
    frame.setPosition(pos);
    acc.mult(0); // reset acceleration
  }

  void checkBounds() {
    if (pos.x() > flockWidth)
      pos.x(0);
    if (pos.x() < 0)
      pos.x(flockWidth);
    if (pos.y() > flockHeight)
      pos.y(0);
    if (pos.y() < 0)
      pos.y(flockHeight);
    if (pos.z() > flockDepth)
      pos.z(0);
    if (pos.z() < 0)
      pos.z(flockDepth);
  }
  
  // check if this boid's frame is the avatar
  boolean isAvatar() {
    if ( scene.avatar() == null )
      return false;
    if ( scene.avatar().equals(frame) )
      return true;
    return false;
  }

  void render() {
    pushStyle();
    stroke(hue);
    noFill();
    noStroke();
    fill(hue);		

    q = Quaternion.multiply(new Quaternion( new Vector3D(0,1,0),  atan2(-vel.z(), vel.x())), 
                            new Quaternion( new Vector3D(0,0,1),  asin(vel.y() / vel.mag())) );		
    frame.setRotation(q);

    pushMatrix();
    // Multiply matrix to get in the frame coordinate system.
    frame.applyTransformation();

    // highlight boids under the mouse
    if (frame.grabsMouse()) {
      fill( grabsMouseColor);
      // additionally, set the boid's frame as the avatar if the mouse is pressed
      if (mousePressed == true) 
        scene.setAvatar(frame);			
    }
    
    // highlight the boid if its frame is the avatar
    if ( isAvatar() ) {
      fill( avatarColor );
    }

    //draw boid
    beginShape(TRIANGLES);
    vertex(3 * sc, 0, 0);
    vertex(-3 * sc, 2 * sc, 0);
    vertex(-3 * sc, -2 * sc, 0);

    vertex(3 * sc, 0, 0);
    vertex(-3 * sc, 2 * sc, 0);
    vertex(-3 * sc, 0, 2 * sc);

    vertex(3 * sc, 0, 0);
    vertex(-3 * sc, 0, 2 * sc);
    vertex(-3 * sc, -2 * sc, 0);

    vertex(-3 * sc, 0, 2 * sc);
    vertex(-3 * sc, 2 * sc, 0);
    vertex(-3 * sc, -2 * sc, 0);
    endShape();		

    popMatrix();

    popStyle();		
  }

  // steering. If arrival==true, the boid slows to meet the target. Credit to
  // Craig Reynolds
  Vector3D steer(Vector3D target, boolean arrival) {
    Vector3D steer = new Vector3D(); // creates vector for steering
    if (!arrival) {
      steer.set(Vector3D.sub(target, pos)); // steering vector points
      // towards target (switch
      // target and pos for
      // avoiding)
      steer.limit(maxSteerForce); // limits the steering force to
      // maxSteerForce
    } 
    else {
      Vector3D targetOffset = Vector3D.sub(target, pos);
      float distance = targetOffset.mag();
      float rampedSpeed = maxSpeed * (distance / 100);
      float clippedSpeed = min(rampedSpeed, maxSpeed);
      Vector3D desiredVelocity = Vector3D.mult(targetOffset,
      (clippedSpeed / distance));
      steer.set(Vector3D.sub(desiredVelocity, vel));
    }
    return steer;
  }

  // avoid. If weight == true avoidance vector is larger the closer the boid
  // is to the target
  Vector3D avoid(Vector3D target, boolean weight) {
    Vector3D steer = new Vector3D(); // creates vector for steering
    steer.set(Vector3D.sub(pos, target)); // steering vector points away from
    // target
    if (weight)
      steer.mult(1 / sq(Vector3D.dist(pos, target)));
    // steer.limit(maxSteerForce); //limits the steering force to
    // maxSteerForce
    return steer;
  }

  Vector3D seperation(ArrayList boids) {
    Vector3D posSum = new Vector3D(0, 0, 0);
    Vector3D repulse;
    for (int i = 0; i < boids.size(); i++) {
      Boid b = (Boid) boids.get(i);
      float d = Vector3D.dist(pos, b.pos);
      if (d > 0 && d <= neighborhoodRadius) {
        repulse = Vector3D.sub(pos, b.pos);
        repulse.normalize();
        repulse.div(d);
        posSum.add(repulse);
      }
    }
    return posSum;
  }

  Vector3D alignment(ArrayList boids) {
    Vector3D velSum = new Vector3D(0, 0, 0);
    int count = 0;
    for (int i = 0; i < boids.size(); i++) {
      Boid b = (Boid) boids.get(i);
      float d = Vector3D.dist(pos, b.pos);
      if (d > 0 && d <= neighborhoodRadius) {
        velSum.add(b.vel);
        count++;
      }
    }
    if (count > 0) {
      velSum.div((float) count);
      velSum.limit(maxSteerForce);
    }
    return velSum;
  }

  Vector3D cohesion(ArrayList boids) {
    Vector3D posSum = new Vector3D(0, 0, 0);
    Vector3D steer = new Vector3D(0, 0, 0);
    int count = 0;
    for (int i = 0; i < boids.size(); i++) {
      Boid b = (Boid) boids.get(i);
      float d = dist(pos.x(), pos.y(), b.pos.x(), b.pos.y());
      if (d > 0 && d <= neighborhoodRadius) {
        posSum.add(b.pos);
        count++;
      }
    }
    if (count > 0) {
      posSum.div((float) count);
    }
    steer = Vector3D.sub(posSum, pos);
    steer.limit(maxSteerForce);
    return steer;
  }
}