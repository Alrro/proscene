import java.awt.Point;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class PointUnderPixel extends PApplet {
	Scene scene;
	Box [] boxes;
	
	public void setup()	{
		size(640, 360, OPENGL);
		scene = new Scene(this);
		GLCamera glCam = new GLCamera(this);
		scene.setCamera(glCam);
		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);
		scene.setHelpIsDrawn(false);
		scene.setRadius(scene.radius() * 1.5f);
		scene.showAll();
		
		boxes = new Box[50];
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(this);
	}

	public void draw() {
		background(0);
		scene.beginDraw();
		for (int i = 0; i < boxes.length; i++) {
			//3D drawing
			boxes[i].draw();
		}
		scene.endDraw();
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
	}
	
	class GLCamera extends Camera {
		protected PGraphicsOpenGL pgl;
		protected PApplet parent;    
	    protected GL gl;
	    protected GLU glu;
	    
		public GLCamera(PApplet p) {
			super();
			parent = p;
			pgl = (PGraphicsOpenGL)parent.g;
	        gl = pgl.gl;
	        glu = pgl.glu;
		}
		
		protected WorldPoint pointUnderPixel(Point pixel) {
			float []depth = new float[1];
			pgl.beginGL();		
			gl.glReadPixels(pixel.x, (screenHeight() - pixel.y), 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, FloatBuffer.wrap(depth));
			pgl.endGL();
			PVector point = new PVector(pixel.x, pixel.y, depth[0]);		
			point = unprojectedCoordinatesOf(point);		
			return new WorldPoint(point, (depth[0] < 1.0f));
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "PointUnderPixel" });
	}
}
