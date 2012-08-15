package basic_geom;
import geom.Box;
import geom.Sphere;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class CajasOrientadas extends PApplet {
	Scene scene;
	Box [] cajas;
	Sphere esfera;
	
	public void setup()	{
		size(640, 360, P3D);
		//size(640, 360, OPENGL);
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(160);
		//scene.camera().setPosition(new PVector(10,0,0));
		//scene.camera().lookAt( scene.center() );
		scene.showAll();		
		//scene.disableBackgroundHanddling();
		
		esfera = new Sphere(scene);
		esfera.setPosition(new PVector(0.0f, 1.4f, 0.0f));
		esfera.setColor(color(0,0,255));
		
		cajas = new Box[30];
		for (int i = 0; i < cajas.length; i++)
			cajas[i] = new Box(scene);
	}

	public void draw() {		
		background(0);
	    esfera.draw();
		for (int i = 0; i < cajas.length; i++) {
			cajas[i].setOrientation(esfera.getPosition());
			cajas[i].draw(true);
		}
		
		int c = color(255,0,0);
		scene.drawFilledCircle(c, new PVector(20,20,0), 10);
		
		pushStyle();
		
		/**
		Camera camera = scene.camera();
		PVector c1 = camera.unprojectedCoordinatesOf(new PVector(8, 8, 0.1f));
		PVector c2 = camera.unprojectedCoordinatesOf(new PVector(width-8, height-8, 0.1f));
		stroke(255);
		line(c1.x, c1.y, c1.z, c2.x, c2.y, c2.z);
		// */
		
		/**
		Camera camera = scene.camera();
		PVector v1 = camera.unprojectedCoordinatesOf(new PVector(8,8,0.1f));
		PVector v2 = camera.unprojectedCoordinatesOf(new PVector(8,8,0.1f));
		PVector v3 = camera.unprojectedCoordinatesOf(new PVector(width-8,height-8,0.1f));
		PVector v4 = camera.unprojectedCoordinatesOf(new PVector(width-8,height-8,0.1f));		
		stroke(255);
		float c1x = v1.x;
		float c1y = v2.y;
		float c1z = v1.z;
		float c2x = v3.x;
		float c2y = v4.y;
		float c2z = v4.z;
		line(c1x, c1y, c1z, c2x, c2y, c2z);
		// */
		
		/**
		Camera camera = scene.camera();
		PVector v1 = camera.unprojectedCoordinatesOf(new PVector(8,0,0.1f));
		PVector v2 = camera.unprojectedCoordinatesOf(new PVector(0,8,0.1f));
		PVector v3 = camera.unprojectedCoordinatesOf(new PVector(0,0,0.1f));
		PVector v4 = camera.unprojectedCoordinatesOf(new PVector(width-8,0,0.1f));
		PVector v5 = camera.unprojectedCoordinatesOf(new PVector(0,height-8,0.1f));
		PVector v6 = camera.unprojectedCoordinatesOf(new PVector(0,0,0.1f));
		stroke(255);
		float c1x = v1.x;
		float c1y = v2.y;
		float c1z = v3.z;
		float c2x = v4.x;
		float c2y = v5.y;
		float c2z = v6.z;
		line(c1x, c1y, c1z, c2x, c2y, c2z);
		// */
		
		/**
		Camera camera = scene.camera();
		float c1x = camera.unprojectedCoordinatesOf(new PVector(8,0,0.1f)).x;
		float c1y = camera.unprojectedCoordinatesOf(new PVector(0,8,0.1f)).y;
		float c2x = camera.unprojectedCoordinatesOf(new PVector(width-8,0,0.1f)).x;
		float c2y = camera.unprojectedCoordinatesOf(new PVector(0,height-8,0.1f)).y;		
		stroke(255);
		line(c1x, c1y, 0.1f, c2x, c2y, 0.1f);
		// */		
		
		popStyle();
	}
	
	public void keyPressed() {
		if ((key == 'x') || (key == 'x')) {
			scene.toggleMouseTracking();
			println(scene.hasMouseTracking());
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CajasOrientadas" });
	}
}
