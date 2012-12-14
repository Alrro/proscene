package screendrawing;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Testing extends PApplet {

   // 1. 'e' switches camera type: perspective and orthographic. 
   // 2. 'v' switches camera kind: proscene and standard.
   // 3. 'x' switches the way the projection matrix is computed.
   // 4. 'y' switches the way the modelview matrix is computed.
   // 5. 'z' switches the way beginScreenDrawing is implemented.
  ProSceneMatrices scene;
  int anchor = 30;

  public void setup() {
    size(640, 360, P3D);
    
    scene = new ProSceneMatrices(this);
    scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);
    scene.setRadius(200);
    scene.showAll();
  }

  public void draw() {
    background(80);
    box(160);
    stroke(200, 100, 10);

    scene.beginScreenDrawing();
    PVector p1 = scene.coords(new Point(anchor, anchor));
    PVector p2 = scene.coords(new Point(width - anchor, height - anchor));
    PVector p3 = scene.coords(new Point(anchor, height - anchor));
    PVector p4 = scene.coords(new Point(width - anchor, anchor));
    scene.renderer().line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
    scene.renderer().line(p3.x, p3.y, p3.z, p4.x, p4.y, p4.z);
    scene.renderer().rect(0, 0, anchor, anchor);
    scene.renderer().rect(width - anchor, 0, anchor, anchor);
    scene.renderer().rect(0, height - anchor, anchor, anchor);
    scene.renderer().rect(width - anchor, height - anchor, anchor, anchor);
    scene.endScreenDrawing();
  }
  
  public void keyPressed() {
    if (key == 'x' || key == 'X') {
      scene.projMatrixOpt1 = !scene.projMatrixOpt1;
    }
    if (key == 'y' || key == 'Y') {
      scene.modelviewMatrixOpt1 = !scene.modelviewMatrixOpt1;
    }
    if (key == 'z' || key == 'Z') {
      scene.screenDrawingOpt1 = !scene.screenDrawingOpt1;
    }
  }

  class ProSceneMatrices extends Scene {
    boolean projMatrixOpt1 = false;
    boolean modelviewMatrixOpt1 = false;
    
    boolean screenDrawingOpt1 = false;

    public ProSceneMatrices(PApplet p) {
      super(p);
    }

    @Override
    public void init() {
      setGridIsDrawn(true);
      setAxisIsDrawn(true);
    }

    @Override
    public void beginScreenDrawing() {
      if (startCoordCalls != 0)
        throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
                         + "endScreenDrawing() and they cannot be nested. Check your implementation!");

      startCoordCalls++;

      renderer().hint(DISABLE_DEPTH_TEST);
      renderer().pushProjection();

      if(screenDrawingOpt1) {
        float cameraZ = (height/2.0f) / PApplet.tan(camera().fieldOfView() /2.0f);
        float cameraNear = cameraZ / 2.0f;
        float cameraFar = cameraZ * 2.0f;
        renderer().ortho(-width/2, width/2, -height/2, height/2, cameraNear, cameraFar);
        println("screenDrawingOpt1: " + cameraNear + " " + cameraFar);
      } else {
        float[] wh = camera().getOrthoWidthHeight();// return halfWidth halfHeight
        renderer().ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(), camera().zFar());
        println("screenDrawingOpt2: " + camera().zNear() + "  " + camera().zFar());
      }

      renderer().pushMatrix();
      renderer().camera();      
            
      zC = 0.0f;
    }

    @Override
    public void endScreenDrawing() {
      startCoordCalls--;
      if (startCoordCalls != 0)
        throw new RuntimeException("There should be exactly one beginScreenDrawing() call followed by a "
                         + "endScreenDrawing() and they cannot be nested. Check your implementation!");
      renderer().popProjection();
      renderer().popMatrix();
      renderer().hint(ENABLE_DEPTH_TEST);
    }

    @Override
    protected void setP5ProjectionMatrix() {
      if (projMatrixOpt1)
        camera().computeProjectionMatrix();
      else {
        switch (camera().type()) {
        case PERSPECTIVE:
        	renderer().perspective(camera().fieldOfView(), camera().aspectRatio(), camera().zNear(), camera().zFar());
          break;
        case ORTHOGRAPHIC:
          float[] wh = camera().getOrthoWidthHeight();// return halfWidth halfHeight          
          renderer().ortho(-wh[0], wh[0], -wh[1], wh[1], camera().zNear(),camera().zFar());
          break;
        }
      }
    }

    @Override
    protected void setP5ModelViewMatrix() {
      if (modelviewMatrixOpt1)
        camera().computeModelViewMatrix();
      else {
    	  renderer().camera(camera().position().x, camera().position().y,
              camera().position().z, camera().at().x,
              camera().at().y, camera().at().z,
              camera().upVector().x, camera().upVector().y, camera().upVector().z);
      }
    }
  }
  
  public static void main(String args[]) {
    PApplet.main(new String[] { "--present", "screendrawing.Testing" });
  }
}
