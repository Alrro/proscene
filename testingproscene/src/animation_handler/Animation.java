package animation_handler;
import geom.Particle;
import processing.core.*;
import remixlab.proscene.*;

public class Animation extends PApplet {

	Scene scene;
	int nbPart;
	Particle[] particle;

	public void setup() {
		size(640, 360, P3D);
		// We instantiate our MyScene class defined below
		scene = new Scene(this);
		scene.addAnimationHandler(this, "performAnimation");
		scene.setShortcut('m', Scene.KeyboardAction.ANIMATION);
		smooth();			
		nbPart = 2000;
		particle = new Particle[nbPart];
		for (int i = 0; i < particle.length; i++)
		    particle[i] = new Particle(this);
		scene.setAxisIsDrawn(false);
		scene.startAnimation();
	}
	
	// Define here your animation.
	public void performAnimation(Scene s) {
		for (int i = 0; i < nbPart; i++)
			if(particle[i] != null)
				particle[i].animate();
	}

	// Make sure to define the draw() method, even if it's empty.
	public void draw() {
		background(0);
		pushStyle();
		strokeWeight(3); // Default
		beginShape(POINTS);
		for (int i = 0; i < nbPart; i++) {
			particle[i].draw();
		}
		endShape();
		popStyle();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Animation" });
	}
	
	public void keyPressed() {
		if((key == 'x') || (key == 'X'))
			scene.setAnimationPeriod(scene.animationPeriod()-2);
		if((key == 'y') || (key == 'Y'))
			scene.setAnimationPeriod(scene.animationPeriod()+2);
	}
}