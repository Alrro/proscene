import processing.core.*;
import processing.opengl.*;
import proscene.*;

Scene scene;	
	
void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		//size(300, 200, OPENGL);
		//size(300, 200, P3D);
		scene = new Scene(this);			  
}

void draw() {
		background(0);
		scene.draw();		
}	
	
void keyPressed() {
		scene.defaultKeyBindings();
}
	
class Scene extends PScene {
		public Scene(PApplet p) {
			super(p);
		}

		public void init() {			
			setGridIsDrawn(true);
			setAxisIsDrawn(true);
		}
		
		public void scene() {
			fill(204, 102, 0);
			box(0.2f, 0.3f, 0.5f);
		}
}
	