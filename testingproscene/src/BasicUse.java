import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);
		//scene.camera().setUpVector(new PVector(0,-1,0));
		//scene.camera().setUpVector(new PVector(0,0,1));
		scene.enableFrustumUpdate();
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		scene.showAll();
		//scene.camera().setUpVector(new PVector(0,-1,0));
		//println( scene.camera().upVector() );
		//scene.setRadius(scene.radius()*2);
	}

	public void draw() {
		background(0);
		scene.beginDraw();
		if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);
		//box(20, 30, 50);		
		scene.endDraw();
	}
	
	// /**
	public void keyPressed() {
		if (key == 'x')
			println("hello");
	}
	// */
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicUse" });
	}
}
