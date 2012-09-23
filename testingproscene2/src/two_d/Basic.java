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
		/**
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
		fromTo(from, to);
		println("angle between (-1,1) and (-1,-1): " + angle);
		fromTo(to, from);
		println("angle between (-1,-1) and (-1,1): " + angle);
	}	
	
	public void fromTo(Vector3D from, Vector3D to) {
		float fromNorm = from.mag();
		float toNorm = to.mag();				
		if ((fromNorm < 1E-10f) || (toNorm < 1E-10f)) {
			angle = 0;
		} else {
			//angle =(float) Math.acos( (double)Vector3D.dot(from, to) / ( fromNorm * toNorm ));
			angle = (float )Math.atan2( from.x()*to.y() - from.y()*to.x(), from.x()*to.x() + from.y()*to.y() );
		}
	}

	public void draw() {
		background(150);
		ellipse(0, 0, 40, 40);
		rect(50, 50, 30, 30);
		
		/**
		scene.beginScreenDrawing();
		text("Hello Pierre", 5, 17);
		scene.endScreenDrawing();
		// */
		
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
		
		//println(scene.camera().frame().orientation().angle());
		
		/**
		println("proscene projection matrix:");
		scene.camera().getProjectionMatrix().print();
		println("p5 projection matrix:");
		((PGraphicsOpenGL)g).projection.print();
		
		println("proscene modelview matrix:");
		scene.camera().getModelViewMatrix().print();
		println("p5 modelview matrix:");
		((PGraphicsOpenGL)g).modelview.print();
		*/
		
		/**
		if( scene.camera().getProjectionMatrix() == ((PGraphicsOpenGL)g).projection )
			println("projections matrices are the same");
		else
			println("projections matrices differ");
		*/
		
		//println(scene.camera().position().x + " " + scene.camera().position().y + " " + scene.camera().position().z + " " );
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			scene.viewWindow().interpolateToZoomOnPixel(new Point(mouseX, mouseY));
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
			//scene.viewWindow().flip();
			
			if(scene.is3D())
				println("scene is 3d");
			else
				println("scene is 2d");
			VFrame tmpFrame = new VFrame(scene.is3D());			
			println("scene.pinhole().frame().worldMatrix().print():");
			scene.pinhole().frame().worldMatrix().print();
			tmpFrame.fromMatrix(scene.pinhole().frame().worldMatrix());
			println("calling tmpFrame.fromMatrix(scene.pinhole().frame().worldMatrix())...");
			println("tmpFrame.worldMatrix().print():");
			tmpFrame.worldMatrix().print();
			println("camera angle: " + scene.pinhole().frame().orientation().angle() );
			println("tmp angle: " + tmpFrame.orientation().angle() );
		}
		if(key == 'q' || key == 'Q') {
			println("View Matrix:");
			scene.pinhole().getViewMatrix().print();
			println("Projection Matrix:");
			scene.pinhole().getProjectionMatrix().print();
			println("camera angle: " + scene.viewWindow().frame().orientation().angle());
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
