import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	PImage b;
	Scene scene;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		//scene.disableBackgroundHanddling();
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();
		//scene.addDrawHandler(this, "m1");		
		
		//b = loadImage("pierre.png");
		//scene.background(b);
		//frame.setResizable(true);
	}

	// /**
	public void draw() {		
		//scene.background(b);		
		//background(0);
		if(frame != null) {
			frame.setResizable(true);
			PApplet.println("set size");
		}
		noStroke();
		if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);		
	}
	// */
	
	//public void draw() {}
	
	
	public void m1(PApplet p) {
		p.fill(255, 0, 0);
		p.sphere(40);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicUse" });
	}
}
