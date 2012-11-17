package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.core.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene scene;
	PFont font;
	float angle;	

	public void setup() {
		//size(640, 360, JAVA2D);
		size(640, 360, P2D);
		//size(360, 640, P2D);
		// /**
		font = createFont("Arial", 16);
		textFont(font, 16);
		// */
		scene = new Scene(this);
		
		//scene.viewWindow().flip();
		//scene.camera().centerScene();
		//scene.showAll();	
		
		Quaternion q = new Quaternion();
		println("axis: " + q.axis()					
		          + " angle: " + q.angle() );		
		
		Vector3D from = new Vector3D(-1,1);
		Vector3D to = new Vector3D(-1,-1);
	}	

	public void draw() {
		background(150);
		ellipse(0, 0, 40, 40);		
		
		// /**
		scene.beginScreenDrawing();
		text("Hello world", 5, 17);
		scene.endScreenDrawing();
		// */
		
		rect(50, 50, 30, 30);
		
		/**
		scene.beginScreenDrawing();
		pushStyle();
		stroke(255, 255, 255);
		strokeWeight(2);
		noFill();
		beginShape();
		vertex(30, 20);
		vertex(85, 20);
		vertex(85, 75);
		vertex(30, 75);
		endShape(CLOSE);
		popStyle();
		scene.endScreenDrawing();
		// */		
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			//scene.viewWindow().interpolateToZoomOnPixel(new Point(mouseX, mouseY));			
			println("scale factor: " );
			scene.viewWindow().frame().scaling().print();
			float[] wh = scene.viewWindow().getOrthoWidthHeight();
			println("halfWidth: " + wh[0]);
			println("halfHeight: " + wh[1]);
			println("screenWidth: " + scene.viewWindow().screenWidth() );
			println("screenHeight: " + scene.viewWindow().screenHeight() );			
		}
		if(key == 'x' || key == 'X') {
			Vector3D v = scene.pinhole().projectedCoordinatesOf(new Vector3D(0,0,0));
			println(v);
		}
		if(key == 'v' || key == 'V') {
			Vector3D v = scene.pinhole().unprojectedCoordinatesOf(new Vector3D(width/2,height/2,0.5f));
			println(v);
		}
		if(key == 'z' || key == 'Z') {
			//scene.viewWindow().fitCircle(new Vector3D(0,0), 20);
			scene.viewWindow().fitCircle(new Vector3D(65,65), 15);
		}
		if(key == 'y' || key == 'Y') {
			scene.viewWindow().flip();			
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
