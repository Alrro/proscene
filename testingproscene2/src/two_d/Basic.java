package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene scene;
	PFont font;

	public void setup() {
		//size(640, 360, JAVA2D);
		size(640, 360, P2D);
		/**
		font = createFont("Arial", 16);
		textFont(font, 16);
		// */
		scene = new Scene(this);
		//scene.camera().centerScene();
		//scene.showAll();	
		hint(DISABLE_STROKE_PERSPECTIVE);
		
		Quaternion q = new Quaternion();
		println("axis: " + q.axis()					
		          + " angle: " + q.angle() );
	}	

	public void draw() {
		background(150);
		rect(0, 0, 55, 55);
		
		/**
		scene.beginScreenDrawing();
		text("Hello Pierre", 5, 17);
		scene.endScreenDrawing();
		// */
		
		/**
		scene.beginScreenDrawing();
		pushStyle();
		stroke(255, 255, 255);
		strokeWeight(2);
		noFill();
		beginShape();
		vertex(30, 20);
		vertex(85, 20);
		vertex(85, 75);
		vertex(30, 75);
		endShape(CLOSE);
		popStyle();
		scene.endScreenDrawing();
		// */
		
		//println(scene.camera().frame().orientation().angle());
		
		/**
		println("proscene projection matrix:");
		scene.camera().getProjectionMatrix().print();
		println("p5 projection matrix:");
		((PGraphicsOpenGL)g).projection.print();
		
		println("proscene modelview matrix:");
		scene.camera().getModelViewMatrix().print();
		println("p5 modelview matrix:");
		((PGraphicsOpenGL)g).modelview.print();
		*/
		
		/**
		if( scene.camera().getProjectionMatrix() == ((PGraphicsOpenGL)g).projection )
			println("projections matrices are the same");
		else
			println("projections matrices differ");
		*/
		
		//println(scene.camera().position().x + " " + scene.camera().position().y + " " + scene.camera().position().z + " " );
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {			
			println("axis: " + scene.pinhole().frame().orientation().axis()					
		          + " angle: " + scene.pinhole().frame().orientation().angle() 
		          + " position: " + scene.pinhole().position());
		}			
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
