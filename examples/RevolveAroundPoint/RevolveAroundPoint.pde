/**
 * Revolve Around Point. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates 3D world point picking.
 *
 * Press 'o' to select the revolve around point of the camera (which is defined in 3D
 * world coordinates). It will be set from the mouse position (which is a point
 * defined in 2D screen coordinates) at the time the key press takes place.
 *
 * Press 'p' to reset the revolve around point position (it will be set to to its
 * default value: (0,0,0)).
 *
 * This example requires OpenGL to read the pixel depth. 
 *
 * Press 'h' to toggle the mouse and keyboard navigation help. Note that the point
 * picking mechanism is disable when the help is display (otherwise the help text
 * can interfere).
 */

import java.awt.Point;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;
void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  GLCamera glCam = new GLCamera(this);
  scene.setCamera(glCam);
  scene.setGridIsDrawn(true);
  scene.setAxisIsDrawn(true);
  scene.setHelpIsDrawn(false);
}

void draw() {
  background(0);
  scene.beginDraw();
  fill(204, 102, 0);
  box(0.2f, 0.3f, 0.5f);
  scene.endDraw();
}
  
void keyPressed() {
  scene.defaultKeyBindings();
}
  
class GLCamera extends Camera {
  protected PGraphicsOpenGL pgl;
  protected PApplet parent;
  protected GL gl;
  protected GLU glu;
    
  public GLCamera(PApplet p) {
    super();
    parent = p;
    pgl = (PGraphicsOpenGL)parent.g;
    gl = pgl.gl;
    glu = pgl.glu;
  }
    
  protected WorldPoint pointUnderPixel(Point pixel) {
    float []depth = new float[1];
    pgl.beginGL();
    gl.glReadPixels(pixel.x, (screenHeight() - pixel.y), 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, FloatBuffer.wrap(depth));
    pgl.endGL();
    PVector point = new PVector(pixel.x, pixel.y, depth[0]);
    point = unprojectedCoordinatesOf(point);
    return new WorldPoint(point, (depth[0] < 1.0f));
  }
}
