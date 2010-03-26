import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class AlternativeUse extends PApplet {
	MyScene scene;	
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new MyScene(this);			  
	}

	public void draw() {
		background(0);
		scene.draw();		
	}	
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	class MyScene extends Scene {
		public MyScene(PApplet p) {
			super(p);
		}

		public void init() {			
			setGridIsDrawn(true);
			setAxisIsDrawn(true);
		}
		
		public void proscenium() {
			fill(204, 102, 0);
			box(0.2f, 0.3f, 0.5f);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "AlternativeUse" });
	}
}
