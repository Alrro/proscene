class Tableau {
  Boid[] bird;
  int nb;
  float voisinage;
  float maxSteerForce = .1;   

  Tableau() {
    nb = 60;
    bird = new Boid[nb];
    voisinage=100;
    for (int i=0; i<nb; i++) {
      bird[i] = new Boid();
    }
  }

  void run()
  {
    for (int i=0;i<nb;i++)
    {//calcul de l'acceleration
      bird[i].acc= alignement(i);
      bird[i].acc.add(PVector.mult(cohesion(i), 1));
      bird[i].acc.add(PVector.mult(separation(i), 3));
      //calcul de la position

      bird[i].draw();
    }
  } 

  //Acceleration de la direction 

  PVector alignement(int i) {
    PVector velSum = new PVector(0, 0, 0);
    int compteur = 0;
    for (int c=0;c<nb;c++)
    {
      float d = PVector.dist(bird[i].pos, bird[c].pos);
      if (d>0 && d<=voisinage)
      {
        velSum.add(bird[c].vit);
        compteur++;
      }
    }
    if (compteur>0)
    {
      velSum.div((float)compteur);
      velSum.limit(maxSteerForce);
    }
    return velSum;
  }

  ////acceleration pour la cohesion 

  PVector cohesion(int i) {
    PVector sommeDesPositions = new PVector(), force = new PVector();

    int compteur = 0;
    for (int c=0;c<nb;c++)
    {
      float d = bird[i].pos.dist(bird[c].pos);
      if (d>0&&d<=voisinage)
      {
        sommeDesPositions.add(bird[c].pos);
        compteur++;
      }
    }
    if (compteur>0)
    {
      sommeDesPositions.div((float)compteur);
    }
    force = PVector.sub(sommeDesPositions, bird[i].pos);
    force.limit(maxSteerForce);
    return force;
  }

  //acceleration pour la repulsion

  PVector separation(int i) {
    PVector sommeDesForces = new PVector(0, 0, 0);
    PVector force;
    for (int c=0;c<nb;c++)
    {

      float d = PVector.dist(bird[i].pos, bird[c].pos);
      if (d>0&&d<=voisinage)
      {
        force = PVector.sub( bird[i].pos, bird[c].pos);
        force.normalize();
        force.div(d);
        sommeDesForces.add(force);
      }
    }
    return sommeDesForces;
  }
}
