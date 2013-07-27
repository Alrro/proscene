package basic;
//import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	Scene scene;
	float radians = 0;
	
	public void setup()	{
		//size(640, 360, P3D);
		size(640, 360, OPENGL);
		
		println("major version: " + Scene.majorVersionNumber() );
		println("minor version: " + Scene.minorVersionNumber() );
		
		scene = new Scene(this);		
	
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();		
		
		//frameRate(200);
	}

	public void draw() {	
		background(0);	
				
		if(frame != null) {
			frame.setResizable(true);
			PApplet.println("set size");
		}
		noStroke();
		if( scene.camera().sphereIsVisible(new Vec(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicUse" });
	}
}
