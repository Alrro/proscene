package screendrawing;

import processing.core.*;

import processing.opengl.*;
public class OrthoVSPerspective extends PApplet {
	private static final long serialVersionUID = 1L;
	
	PGraphicsOpenGL pgl;
	float zC = 0;

	public void setup() {
	  size(640, 360, OPENGL);
	  pgl = (PGraphicsOpenGL) g;
	  noStroke();
	  fill(204);
	}

	public void draw() {
	  background(250);  
	  lights();
	 
	  if(mousePressed) {
	    float fov = PI/3.0f; 
	    float cameraZ = (height/2.0f) / tan(fov/2.0f); 
	    perspective(fov, (float)width/ (float)height, cameraZ/2.0f, cameraZ*2.0f); 
	  } else {
	    ortho(0, width, 0, height, -200, +200);
	  }
	  
	  translate(width/2, height/2, 0);
	  rotateX(-PI/6); 
	  rotateY(PI/3); 
	  box(160);  
	  
	  stroke(200, 100, 10);
	  beginScreenDrawing();
	  line(10, 10, zC, width-10, height-10, zC);
	  endScreenDrawing();
	  
	  beginScreenDrawing();
	  line(10, height-10, zC, width-10, 10, zC);
	  endScreenDrawing();
	}

	public void beginScreenDrawing() {
	  pgl.hint(DISABLE_DEPTH_TEST);
	  pgl.pushProjection();
	  pgl.ortho(-width/2, width/2, -height/2, height/2, -10, 10); 
	  pgl.pushMatrix();
	  pgl.camera();
	  zC = 0.0f;
	}

	public void endScreenDrawing() {
	  pgl.popProjection();  
	  pgl.popMatrix();   
	  hint(ENABLE_DEPTH_TEST);
	}

	/**
	public void beginScreenDrawing() {
	  hint(DISABLE_DEPTH_TEST);
	  pgl.pushProjection();
	  ortho(-width/2, width/2, -height/2, height/2, -10, 10); 
	  pushMatrix();
	  camera();
	  zC = 0.0f;
	}

	public void endScreenDrawing() {
	  pgl.popProjection();  
	  popMatrix();   
	  hint(ENABLE_DEPTH_TEST);
	}
	*/
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "screendrawing.OrthoVSPerspective" });
	}
}
