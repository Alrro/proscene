import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class CajasOrientadas extends PApplet {
	Scene scene;
	Caja [] cajas;
	Esfera esfera;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);		
		scene.setGridIsDrawn(true);		
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(2);		
		scene.showAll();
		
		esfera = new Esfera(this);
		esfera.setPosition(new PVector(0.0f, 1.0f, 0.0f));
		
		cajas = new Caja[5];
		for (int i = 0; i < 5; i++) {
			cajas[i] = new Caja(this);
			cajas[i].setPosition(new PVector((-1.0f + (i*0.5f )), 0.0f, 0.0f));
			//not really necessary here: it is set in draw!
			//cajas[i].setOrientation( esfera.getPosition() );
		}	
	}

	public void draw() {
		//set camera stuff, always necessary:
		background(0);
				
		scene.beginDraw();
		// Here we are in the world coordinate system.
		// Draw your scene here.				
	    esfera.draw();
		for (int i = 0; i < 5; i++) {
			cajas[i].draw(esfera.getPosition());
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
