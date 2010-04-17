import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class CajasOrientadas extends PApplet {
	Scene scene;
	Box [] cajas;
	Esfera esfera;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);		
		scene.setGridIsDrawn(true);		
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(1.6f);		
		scene.showAll();
		
		esfera = new Esfera(this);
		esfera.setPosition(new PVector(0.0f, 1.4f, 0.0f));
		esfera.setColor(color(0,0,255));
		
		cajas = new Box[30];
		for (int i = 0; i < cajas.length; i++)
			cajas[i] = new Box(this);	
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.				
	    esfera.draw();
		for (int i = 0; i < cajas.length; i++) {
			cajas[i].setOrientation(esfera.getPosition());
			cajas[i].draw(true);
		}		    
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CajasOrientadas" });
	}
}
