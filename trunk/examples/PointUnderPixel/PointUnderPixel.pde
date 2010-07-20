/**
 * Point Under Pixel.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates 3D world point picking.
 * 
 * Press 'o' to select the camera arcball reference point, or press 'p' to select
 * a point for the camera to zoom in. Note that in both cases the point is defined
 * in 3D world coordinates, but it will be set from the mouse position (which is a
 * point defined in 2D screen coordinates) at the time the key press takes place.
 * Nothing happens if there's no scene point under the mouse.
 * 
 * Press 'O' to reset the arcball reference point position (it will be set to its
 * default value: (0,0,0)).
 * 
 * This example requires OpenGL to read the pixel depth. 
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help.
 * 
 * Note that the point picking mechanism is disable when the help is display
 * (otherwise the help text can interfere).
 */

import java.awt.Point;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.opengl.*;
import remixlab.proscene.*;

Scene scene;
Box [] boxes;

void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  GLCamera glCam = new GLCamera(scene);
  scene.setCamera(glCam);
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  scene.setHelpIsDrawn(false);
  scene.setRadius(150);
  scene.showAll();
  boxes = new Box[50];
  // create an array of boxes with random positions, sizes and colors
  for (int i = 0; i < boxes.length; i++)
    boxes[i] = new Box();
}

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  for (int i = 0; i < boxes.length; i++)    
    boxes[i].draw();
}

class GLCamera extends Camera {
  protected PGraphicsOpenGL pgl;
  protected GL gl;
  protected GLU glu;
    
  public GLCamera(Scene scn) {
    super(scn);
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