package two_d;

import processing.core.PApplet;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene2D scene;

	public void setup() {
		//size(640, 360, JAVA2D);
		size(640, 360, P2D);
		scene = new Scene2D(this);
		//scene.camera().centerScene();
		scene.showAll();
		if ( scene.camera().isAttachedToP5Camera() )
			println("is attached to P5 camera");
		else
			println("is detached from P5 camera");		
		if( scene.p5Renderer() == AbstractScene.P5Renderer.JAVA2D )
			println("JAVA2D");
		else
			println("P2D");		
	}	

	public void draw() {
		background(150);
		rect(0, 0, 55, 55);
		
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
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
