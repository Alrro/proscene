import proscene.*;
import processing.opengl.*;

PScene scene;
	
void setup() {
	size(640, 360, OPENGL);
	scene = new PScene(this);
	scene.setGridIsDrawn(true);
	scene.setAxisIsDrawn(true);
}

void draw() {
	background(0);
	scene.beginDraw();
	fill(204, 102, 0);
	box(0.2f, 0.3f, 0.5f);
	scene.endDraw();
}
	
void keyPressed() {
	scene.defaultKeyBindings();
}
