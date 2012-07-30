package two_d;

import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {	
	Scene2D scene;
	//Scene scene;

	public void setup() {
	  size(640, 360, OPENGL);
	  //Scene instantiation
	  scene = new Scene2D(this);
	  //scene = new Scene(this);
	}
	
	public void keyPressed() {
		if ( key == 'x' || key =='X')
			if ( scene.camera().isAttachedToP5Camera() )
				println("is attached to P5 camera");
			else
				println("is detached from P5 camera");
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.BasicUse" });
	}
}
