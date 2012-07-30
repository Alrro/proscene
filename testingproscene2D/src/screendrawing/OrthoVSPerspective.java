package screendrawing;

import processing.core.*;
import processing.opengl.*;

@SuppressWarnings("serial")
public class OrthoVSPerspective extends PApplet {	
	float cameraFOV;
	float cameraZ;
	float cameraMaxFar;
	float cameraNear;
	float cameraFar;

	float angleX = -PI/6;
	float angleY = PI/3;
	
	PGraphicsOpenGL pgl;
	float zC = 0;

	public void setup() {
	  size(640, 360, OPENGL);
	  pgl = (PGraphicsOpenGL) g;
	  noStroke();
	  fill(204);
	  
	  cameraFOV = PI/3.0f; 
	  cameraZ = (height/2.0f) / tan(cameraFOV/2.0f);
	  cameraMaxFar = cameraZ * 2.0f;

	  cameraNear = cameraZ / 2.0f;
	  cameraFar = cameraZ * 2.0f;
	}

	public void draw() {
	  background(250);  
	  lights();
	 
	  if(mousePressed) {   
	    perspective(cameraFOV, (float)width / (float)height, cameraNear, cameraFar);
	  } else {	    
		  ortho(0, width, 0, height, cameraNear, cameraFar);
	  }
	  
	  translate(width/2, height/2, 0);
	  rotateX(-PI/6); 
	  rotateY(PI/3); 
	  box(160);  
	  
	  stroke(200, 100, 10);
	  beginP5ScreenDrawing();
	  line(10, 10, zC, width-10, height-10, zC);
	  endP5ScreenDrawing();
	  
	  beginP5ScreenDrawing();
	  line(10, height-10, zC, width-10, 10, zC);
	  endP5ScreenDrawing();
	}

	public void beginP5ScreenDrawing() {
	  pgl.hint(DISABLE_DEPTH_TEST);
	  pgl.pushProjection();	  
	  pgl.ortho(0, width, 0, height, cameraNear, cameraFar);
	  pgl.pushMatrix();
	  pgl.camera();
	  zC = 0.0f;
	}

	public void endP5ScreenDrawing() {
	  pgl.popProjection();  
	  pgl.popMatrix();   
	  hint(ENABLE_DEPTH_TEST);
	}	
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "screendrawing.OrthoVSPerspective" });
	}
}
