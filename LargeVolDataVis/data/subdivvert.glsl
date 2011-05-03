attribute vec3 objCenter;

varying vec3 center;

void main() {
  gl_Position = gl_Vertex;
  center = objCenter;
}