class Pierre extends Interpolation {
  float longueur;

  Pierre(PVector b, Quaternion qb, float lng) {
    super(b, qb);
    this.longueur=lng;
  }
  
  void actualiser() {
    super.dessin(longueur);
  }
}

