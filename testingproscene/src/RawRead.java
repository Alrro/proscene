import processing.core.*;
import processing.opengl.*;
import remixlab.glproscene.*;
import remixlab.proscene.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.nio.*;

@SuppressWarnings("serial")
public class RawRead extends PApplet {
	GLScene scene;

	GL gl;
	GLU glu;

	public void setup() {
		size(640, 360, OPENGL);
		scene = new GLScene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		
	  gl=((PGraphicsOpenGL)g).gl;
	  glu=((PGraphicsOpenGL)g).glu;
	}
	  

	public void draw()	{
		background(0);
		scene.beginDraw();
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.5f);		
		scene.endDraw();
	  // DRAW ALL 3D stuff	  
	}
	
	public void keyPressed() {
		scene.defaultKeyBindings();
		if (key == 'x' || key == 'X') {
			float[] mousePos=getMouse3D();
			println("mouse 3D: " + mousePos[0] + " " + mousePos[1] + " " + mousePos[2]);
			//returns x,y,z values hopefully.			
		}
		
	}

	public float[] getMouse3D() {
	  ((PGraphicsOpenGL)g).beginGL();
	  int viewport[] = new int[4];
	  double[] proj=new double[16];
	  double[] model=new double[16];
	  gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
	  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX,proj,0);
	  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX,model,0);
	  FloatBuffer fb=ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	  gl.glReadPixels(mouseX, height-mouseY, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, fb);
	  fb.rewind();
	  println("mouse 2D: " + mouseX + " " + mouseY + " " + fb.get(0));
	  double[] mousePosArr=new double[4];
	  glu.gluUnProject((double)mouseX,height-(double)mouseY,(double)fb.get(0),model,0,proj,0,viewport,0,mousePosArr,0);
	  ((PGraphicsOpenGL)g).endGL();
	  return new float[]{(float)mousePosArr[0],(float)mousePosArr[1],(float)mousePosArr[2]};
	} 
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "RawRead" });
	}
}
