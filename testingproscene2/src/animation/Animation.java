package animation;
import geom.Particle;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

public class Animation extends PApplet {

	MyScene scene;

	public void setup() {
		//size(640, 360, P3D);
		size(640, 360, OPENGL);
		// We instantiate our MyScene class defined below
		scene = new MyScene(this);
		//scene.setAWTTimers();
	}

	// Make sure to define the draw() method, even if it's empty.
	public void draw() {
		background(0);
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

	class MyScene extends Scene {
		int nbPart;
		Particle[] particle;

		// We need to call super(p) to instantiate the base class
		public MyScene(PApplet p) {
			super(p);
		}

		// Initialization stuff could have also been performed at
		// setup(), once after the Scene object have been instantiated
		public void init() {
			setShortcut('m', Scene.KeyboardAction.ANIMATION);
			smooth();			
			nbPart = 2000;
			particle = new Particle[nbPart];
			for (int i = 0; i < particle.length; i++)
			    particle[i] = new Particle(parent);
			setAxisIsDrawn(false);
			startAnimation();
		}

		// Define here what is actually going to be drawn.
		public void proscenium() {
			parent.pushStyle();
			strokeWeight(3); // Default
			beginShape(POINTS);
			for (int i = 0; i < nbPart; i++) {
				particle[i].draw();
			}
			endShape();
			parent.popStyle();
		}

		// Define here your animation.
		public void animate() {
			for (int i = 0; i < nbPart; i++)
				if(particle[i] != null)
					particle[i].animate();
		}
	}
}