import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class ScreenDrawing extends PApplet {
	Scene scene;
	Box [] boxes;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);		
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(2);		
		scene.showAll();
		
		boxes = new Box[6];
		for (int i = 0; i < 6; i++) {
			boxes[i] = new Box(this, 0.3f, color(0,0,255));
			boxes[i].setPosition(new PVector((-1.0f + (i*0.4f )), 0.0f, 0.0f));
		}
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.
		//background(0);		
		for (int i = 0; i < 6; i++) {			
			//2D drawing of arrows
			//PVector head = scene.camera().projectedCoordinatesOf(boxes[i].getPosition());
			//same as the previous line:
			PVector pos = boxes[i].getPosition();
			PVector head = new PVector (screenX(pos.x, pos.y, pos.z), screenY(pos.x, pos.y, pos.z), screenZ(pos.x, pos.y, pos.z));
			scene.beginScreenDrawing();
			stroke(255, 255, 255);
			strokeWeight(2);
			noFill();
			beginShape(LINES);
			vertex(Scene.xCoord(head.x-10), Scene.yCoord(head.y-10), Scene.zCoord());
			vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
			vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
			vertex(Scene.xCoord(head.x-10), Scene.yCoord(head.y+10), Scene.zCoord());
			vertex(Scene.xCoord(head.x), Scene.yCoord(head.y), Scene.zCoord());
			vertex(Scene.xCoord(head.x-30), Scene.yCoord(head.y), Scene.zCoord());
			endShape();
			strokeWeight(1);
			scene.endScreenDrawing();
			//3D drawing
			boxes[i].draw();
		}		    
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ScreenDrawing" });
	}
}
