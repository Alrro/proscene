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
		scene = new Scene(this);	
		scene.setGridIsDrawn(true);		
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(160);		
		scene.showAll();
		
		esfera = new Sphere(scene);
		esfera.setPosition(new PVector(0.0f, 1.4f, 0.0f));
		esfera.setColor(color(0,0,255));
		
		cajas = new Box[30];
		for (int i = 0; i < cajas.length; i++)
			cajas[i] = new Box(scene);
	}

	public void draw() {				
	    esfera.draw();
		for (int i = 0; i < cajas.length; i++) {
			cajas[i].setOrientation(esfera.getPosition());
			cajas[i].draw(true);
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CajasOrientadas" });
	}
}
