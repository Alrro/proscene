import processing.core.*;
import processing.opengl.*;
import proscene.*;

PScene scene;
Box [] boxes;
	
void setup()	{
		size(640, 360, OPENGL);
		scene = new PScene(this);		
		scene.setGridIsDrawn(true);		
		scene.setCameraType(PSCamera.Type.ORTHOGRAPHIC);
		scene.setSceneRadius(2);		
		scene.showEntireScene();
		
		boxes = new Box[6];
		for (int i = 0; i < 6; i++) {
			boxes[i] = new Box(this);
			boxes[i].setPosition(new PVector((-1.0f + (i*0.4f )), 0.0f, 0.0f));
		}
}

void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		//background(0);		
		for (int i = 0; i < 6; i++) {
			boxes[i].draw();
		}		    
		scene.endDraw();
}
	
void keyPressed() {
		scene.defaultKeyBindings();
}
	