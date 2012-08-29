/**
 * @author Ivan Dario Chinome
 * @author David Montañez Guerrero
 */

package cameracrane;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

//TODO fix obj reading
@SuppressWarnings("serial")
public class CameraCrane extends PApplet {
	boolean enabledLights = true;
	boolean drawRobotCamFrustum = false;
	ArmCam armCam;
	HeliCam heliCam;
	PGraphics canvas, armCanvas, heliCanvas;
	Scene mainScene, armScene, heliScene;
	int mainWinHeight = 400; // should be less than the PApplet height
	PShape teapot;

	public void setup() {
	  size(1024, 720, P3D);
	  teapot = loadShape("cameracrane/Models/teapot.obj");
	  
	  canvas = createGraphics(width, mainWinHeight, P3D);
	  mainScene = new Scene(this, (PGraphics3D) canvas);
	  mainScene.setGridIsDrawn(false);
	  mainScene.setAxisIsDrawn(false);
	  mainScene.setRadius(110);
	  mainScene.showAll();
	  // press 'f' to display frame selection hints
	  mainScene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);

	  armCanvas = createGraphics(width / 2, (height - canvas.height), P3D);
	  // Note that we pass the upper left corner coordinates where the scene
	  // is to be drawn (see drawing code below) to its constructor.  
	  armScene = new Scene(this, (PGraphics3D) armCanvas, 0, canvas.height);  
	  armScene.setRadius(50);
	  armScene.setGridIsDrawn(false);
	  armScene.setAxisIsDrawn(false);
	  heliCanvas = createGraphics(width / 2, (height - canvas.height), P3D);
	  // Note that we pass the upper left corner coordinates where the scene
	  // is to be drawn (see drawing code below) to its constructor.
	  heliScene = new Scene(this, (PGraphics3D) heliCanvas, canvas.width / 2, canvas.height);
	  heliScene.setRadius(50);
	  heliScene.setGridIsDrawn(false);
	  heliScene.setAxisIsDrawn(false); 

	  // Frame linking
	  armCam = new ArmCam(60, -60, 2);
	  armScene.camera().frame().linkTo(armCam.frame(5));

	  heliCam = new HeliCam();
	  heliScene.camera().frame().linkTo(heliCam.frame(3));
	}

	// off-screen rendering
	public void draw() {
	  handleMouse();
	  canvas.beginDraw();
	  mainScene.beginDraw();
	  drawing(mainScene);
	  mainScene.endDraw();
	  canvas.endDraw();
	  image(canvas, 0, 0);

	  armCanvas.beginDraw();
	  drawing(armScene);
	  armScene.beginDraw();
	  armScene.endDraw();
	  armCanvas.endDraw();
	  // We retrieve the scene upper left coordinates defined above.
	  image(armCanvas, armScene.upperLeftCorner.x, armScene.upperLeftCorner.y);

	  heliCanvas.beginDraw();
	  drawing(heliScene);
	  heliScene.beginDraw();
	  heliScene.endDraw();
	  heliCanvas.endDraw();
	  // We retrieve the scene upper left coordinates defined above.
	  image(heliCanvas, heliScene.upperLeftCorner.x, heliScene.upperLeftCorner.y);
	}

	public void handleMouse() {
	  if (mouseY < canvas.height) {
	    mainScene.enableMouseHandling();
	    mainScene.enableKeyboardHandling();
	    armScene.disableMouseHandling();
	    armScene.disableKeyboardHandling();
	    heliScene.disableMouseHandling();
	    heliScene.disableKeyboardHandling();
	  } 
	  else {
	    if (mouseX < canvas.width / 2) {
	      mainScene.disableMouseHandling();
	      mainScene.disableKeyboardHandling();
	      armScene.enableMouseHandling();
	      armScene.enableKeyboardHandling();
	      heliScene.disableMouseHandling();
	      heliScene.disableKeyboardHandling();
	    } 
	    else {
	      mainScene.disableMouseHandling();
	      mainScene.disableKeyboardHandling();
	      armScene.disableMouseHandling();
	      armScene.disableKeyboardHandling();
	      heliScene.enableMouseHandling();
	      heliScene.enableKeyboardHandling();
	    }
	  }
	}

	// the actual drawing function, shared by the two scenes
	public void drawing(Scene scn) {  
	  PGraphicsOpenGL pg3d = scn.renderer();
	  pg3d.background(0);
	  if (enabledLights) {
	    pg3d.lights();
	  }
	  // 1. draw the robot cams

	  armCam.draw(scn);
	  heliCam.draw(scn);

	  // 2. draw the scene

	  // Rendering of the OBJ model
	  pg3d.noStroke();
	  pg3d.fill(24, 184, 199);
	  pg3d.pushMatrix();
	  pg3d.translate(0,0,20);
	  pg3d.scale(2.5);
	  pg3d.rotateX(HALF_PI); 
	  
	  pg3d.shape(teapot);
	  pg3d.popMatrix();

	  // 2a. draw a ground
	  pg3d.noStroke();
	  pg3d.fill(120, 120, 120);
	  float nbPatches = 100;
	  pg3d.normal(0.0f, 0.0f, 1.0f);
	  for (int j = 0; j < nbPatches; ++j) {
	    pg3d.beginShape(QUAD_STRIP);
	    for (int i = 0; i <= nbPatches; ++i) {
	      pg3d.vertex((200 * (float) i / nbPatches - 100), (200 * j / nbPatches - 100));
	      pg3d.vertex((200 * (float) i / nbPatches - 100), (200 * (float) (j + 1) / nbPatches - 100));
	    }
	    pg3d.endShape();
	  }
	}

	public void keyPressed() {
	  if (key == 'l') {
	    enabledLights = !enabledLights;
	    if (enabledLights) {
	      println("camera spot lights enabled");
	    } 
	    else {
	      println("camera spot lights disabled");
	    }
	  }
	  if (key == 'x') {
	    drawRobotCamFrustum = !drawRobotCamFrustum;
	    if (drawRobotCamFrustum) {
	      println("draw robot camera frustums");
	    } 
	    else {
	      println("don't draw robot camera frustums");
	    }
	  }
	}
	public static void main(String[] args) {
		PApplet.main(new String[] { "crane.CameraCrane" });
	}	
}
