import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class TestingAPI extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		//size(300, 200, OPENGL);
		//size(300, 200, P3D);
		scene = new Scene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		 
		Quaternion q1 = Quaternion.randomQuaternion();
		Quaternion q2 = Quaternion.randomQuaternion();
		
		//q1.pMatrix().print();
		//q1.matrix().print();
		
		//q2.pMatrix().print();
		//q2.matrix().print();
		
		PVector v1 = new PVector(random(-9.9f, 9.9f),random(-9.9f, 9.9f), random(-9.9f, 9.9f));
		PVector v2 = new PVector(random(-9.9f, 9.9f),random(-9.9f, 9.9f), random(-9.9f, 9.9f));
		Frame f1 = new Frame(v1, q1);
		
		//f1.pMatrix().print();
		f1.matrix().print();
		
		//PSFrame f2 = new PSFrame();
		//f2.fromPMatrix(f1.matrix());
		
		Frame f3 = new Frame();
		f3.fromMatrix(f1.matrix());
		
		//f2.matrix().print();
		f3.matrix().print();
		
		//println(f1.translation().x + " " + f1.translation().y + " " + f1.translation().z);
		//println(f3.translation().x + " " + f3.translation().y + " " + f3.translation().z);
		
		println(f1.rotation().x + " " + f1.rotation().y + " " + f1.rotation().z + " " +  f1.rotation().w);
		//println(f2.rotation().x + " " + f2.rotation().y + " " + f2.rotation().z + " " +  f2.rotation().w);
		println(f3.rotation().x + " " + f3.rotation().y + " " + f3.rotation().z + " " +  f3.rotation().w);
		
		PVector v3 = f1.rotation().rotate(v2);
		PVector v4 = f3.rotation().rotate(v2);
		
		println(v3.x + " " + v3.y + " " + v3.z);
		println(v4.x + " " + v4.y + " " + v4.z);
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
		PApplet.main(new String[] { "--present", "test.TestingAPI" });
	}
}
