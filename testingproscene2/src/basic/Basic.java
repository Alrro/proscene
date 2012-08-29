package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class Basic extends PApplet {
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  //Scene instantiation
	  scene = new Scene(this);
	  scene.setAxisIsDrawn(false);
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public void keyPressed() {
		if(key == 'x' || key == 'X')
			if( scene.renderer() instanceof Renderer3D )
				println("scene.renderer() instanceof Renderer3D");
			else
				println("scene.renderer() IS NOT instanceof Renderer3D");
		if(key == 'y' || key == 'Y')
			if( ((Renderer3D)scene.renderer()).pg() instanceof PGraphics3D )
				println("pg instanceof PGraphics3D");
			else
				println("pg IS NOT instanceof PGraphics3D");
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
