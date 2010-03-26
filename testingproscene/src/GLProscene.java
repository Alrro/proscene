import java.awt.Point;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.core.*;
import processing.opengl.*;
import remixlab.glproscene.*;
//import remixlab.proscene.*;

@SuppressWarnings("serial")
public class GLProscene extends PApplet {
	GLScene scene;
	GLCamera camera;
	
	GL gl;
	GLU glu;
	
	Esfera esfera;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new GLScene(this);
		camera = new GLCamera(this);
		scene.setCamera(camera);
		scene.showAll();
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		
		gl=scene.gl;
		glu=scene.glu;
	}

	public void draw() {
		background(0);
		scene.beginDraw();
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.5f);		
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();		
	}	
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "GLProscene" });
	}
}