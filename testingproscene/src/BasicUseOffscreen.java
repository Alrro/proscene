import processing.core.*;
import remixlab.proscene.*;
import processing.opengl.*;
import codeanticode.glgraphics.*;

public class BasicUseOffscreen extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Scene scene;
	GLGraphicsOffScreen canvas;

	public void setup() {
	  size(640, 360, GLConstants.GLGRAPHICS);
	  
	  canvas = new GLGraphicsOffScreen(this, width, height);
	  // You can create an antialiased surface if the hardware
	  // supports it:
	  //canvas = new GLGraphicsOffScreen(this, width, height, true, 4);
	  
	  // Scene instantiation. Note that we also pass the 
	  // offscreen surface (canvas) to the constructor of
	  // the scene.
	  scene = new Scene(this, canvas);  
	}

	public void draw() {
	  background(255);
	  
	  // Offscreen rendering of the scene. The
	  // beginDraw/endDraw calls are required. Note
	  // the order: beginDraw of canvas first, then
	  // beginDraw of scene.
	  canvas.beginDraw();
	  scene.beginDraw(); 
	  canvas.fill(204, 102, 0);
	  canvas.box(20, 30, 50);
	  scene.endDraw();
	  canvas.endDraw();
	  
	  // Now you can use the offscreen image
	  // to do wherever you want.
	  GLTexture tex = canvas.getTexture();
	  image(tex, 0, 0, 320, 180);
	  image(tex, 320, 0, 320, 180);
	  image(tex, 0, 180, 320, 180);
	  image(tex, 320, 180, 320, 180);  
	}
	
	public void keyPressed() {
		if ((key == 'x') || (key == 'x')) {
			scene.toggleMouseTracking();
			println(scene.hasMouseTracking());
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicUseOffscreen" });
	}
}
