package gnutopic;

import geom.Box;
import geom.Sphere;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.constraint.*;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class GalaxyVisualization extends PApplet {
	Scene scene;
	Box [] cajas;
	SpecializedSphere esfera;
	
	public void setup()	{
		//size(640, 360, P3D);
		size(640, 360, OPENGL);
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(160);
		//scene.camera().setPosition(new PVector(10,0,0));
		//scene.camera().lookAt( scene.center() );
		scene.showAll();
		
		esfera = new SpecializedSphere(scene);
		esfera.setPosition(new Vector3D(0.0f, 1.4f, 0.0f));
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
	}
	
	public void keyPressed() {
		if ((key == 'x') || (key == 'x')) {
			scene.toggleDeviceTracking();
			println(scene.isTrackingDevice());
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "gnutopic.GalaxyVisualization" });
	}
}

