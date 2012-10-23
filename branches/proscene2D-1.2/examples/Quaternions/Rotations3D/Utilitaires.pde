PVector comb(float t1, PVector v1, float t2, PVector v2) {
  PVector res=PVector.add(PVector.mult(v1, t1), PVector.mult(v2, t2));
  return res;
}

PVector comb(float t1, PVector v1, float t2, PVector v2, float t3, PVector v3) {
  PVector res=PVector.add(PVector.mult(v1, t1), PVector.mult(v2, t2));
  res=PVector.add(res, PVector.mult(v3, t3));
  return res;
}

PVector centreGravite(PVector u, PVector v, PVector r) {
  PVector gr= comb(0.5f, u, 0.5f, v);
  gr= comb(1.0f/3.0f, r, 2.0f/3.0f, gr);
  return gr;
}

PVector barycentre(float lamb, PVector u, PVector v) {
  return comb(lamb, u, 1-lamb, v);
}

float  barycentre(float lamb, float u, float v) {
  return lamb*u+(1-lamb)*v;
}

void ligne(PVector a, PVector b) {
  line(a.x, a.y, a.z, b.x, b.y, b.z);
}

void afficher(PVector u) {
  println("vecteur = "+u.x+"    "+u.y+"   "+u.z);
}

void afficher(Quaternion q) {
  println("quaternion = x  "+q.x+"  y  "+q.y+" z  "+q.z+"... w  "+q.z);
}

void rectangle(color c, float dx, float dy, float ax, float ay) {
  stroke(150);
  fill(c);
  beginShape();
  vertex(dx, dy, 0);
  vertex(ax, dy, 0);
  fill(color(red(c)*2, green(c)*2, blue(c)*2));
  vertex(ax, ay, 0);
  vertex(dx, ay, 0);
  endShape(CLOSE);
}

//
void triangle3d(PVector a, PVector b, PVector c) {

  beginShape();
  fill(255, 200, 0, 200);
  vertex( a.x, a.y, a.z);
  fill(255, 255, 0, 200);       
  vertex( b.x, b.y, b.z);
  fill(155, 50, 250, 200);
  vertex( c.x, c.y, c.z);
  endShape();
}    
void triangle3d(PVector a, PVector b, PVector c, float k, float l, float m) {
  stroke(0, 100, 255);
  beginShape();
  fill(k, l, m, 250);
  vertex( a.x, a.y, a.z);
  fill(l, k, m, 250);
  vertex( b.x, b.y, b.z);
  fill(m, k, l, 250);
  vertex( c.x, c.y, c.z);
  endShape();
}    
void triangles3D(PVector a, PVector b, PVector c) {

  triangle3d(a, b, o);
  triangle3d(b, c, o);
  triangle3d(a, c, o);
}

PVector symetriePlan(PVector m, PVector u, PVector v) {
  PVector normale=u.cross(v);
  normale.normalize();
  PVector pm=PVector.mult(normale, m.dot(normale));

  return comb(1, m, -2.0, pm);
}
PVector projectionSurDroite(PVector v, PVector droite) {
  PVector u=droite.get();
  u.normalize();
  return PVector.mult(u, u.dot(v));
}
