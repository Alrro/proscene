package misc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
//import codeanticode.glgraphics.*;

@SuppressWarnings("serial")
public class TestingAPI extends PApplet {
	Scene scene;
	InteractiveFrame []keyFrame_;
	KeyFrameInterpolator kfi_;
	int nbKeyFrames;
	int currentKF_;
	
	public void setup()	{		
		size(640, 360, P3D);
		//size(300, 200, OPENGL);
		//size(300, 200, P3D);
		scene = new Scene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		
		ArrayList<String> list = new ArrayList<String>();
		String name = "jean";
		String on = "jean"; 
		list.add(name);
		if ( list.contains(on) )
			PApplet.println("contains jean!");
		else
			PApplet.println("does not contain jean!");
		
		//Quaternion q1 = Quaternion.randomQuaternion();
		//Quaternion q2 = new Quaternion(q1);
		
		Quaternion q1 = new Quaternion();
		Quaternion q2 = new Quaternion();
		
		/**
		float roll = this.QUARTER_PI;
		float pitch = this.THIRD_PI/2;
		float yaw = this.HALF_PI/2;
		*/
		
		float low = -PApplet.PI;
		float high = PApplet.PI;
		
		float roll = random(low, high);		
		float pitch = random(low, high);		
		float yaw = random(low, high);		
		PApplet.println("roll = " + roll);
		PApplet.println("pitch = " + pitch);
		PApplet.println("yaw = " + yaw);		
		
		q1.fromEulerAngles(roll, pitch, yaw);
		
		PApplet.println("q1 = (" + q1.x + ", " + q1.y + ", " + q1.z + ", " + q1.w + ")");
		
		PVector v = q1.eulerAngles();
		
		roll = v.x;
		pitch = v.y;
		yaw = v.z;		
		PApplet.println("roll = " + roll);
		PApplet.println("pitch = " + pitch);
		PApplet.println("yaw = " + yaw);
		
		q2.fromEulerAngles(roll, pitch, yaw);
		
		PApplet.println("q2 = (" + q2.x + ", " + q2.y + ", " + q2.z + ", " + q2.w + ")");
		
		float x = random(200);
		float y = random(200);
		float z = random(200);
		PVector axis3 = new PVector(x, y, z);
		PVector axis4 = new PVector(-x, -y, -z);
		float angle = random(low, high);;
		
		Quaternion q3 = new Quaternion(axis3, angle);
		Quaternion q4 = new Quaternion(axis4, -angle);
		
		PApplet.println("q3 = (" + q3.x + ", " + q3.y + ", " + q3.z + ", " + q3.w + ")");
		PApplet.println("q4 = (" + q4.x + ", " + q4.y + ", " + q4.z + ", " + q4.w + ")");
		
		PVector vec = new PVector(0, -8, 5);
		
		float mag = 45;
		
		vec.normalize();
		
		vec.mult(mag);
		
		PApplet.println(vec.mag()); 
		
		
		
		//q1.normalize();
		//q2.normalize();
		
		//PApplet.println("q1 = (" + q1.x + " ," + q1.y + " ," + q1.z + " ," + q1.w + ")");
		//PApplet.println("q2 = (" + q2.x + " ," + q2.y + " ," + q2.z + " ," + q2.w + ")");
		
		/**
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setCameraType(Camera.Type.PERSPECTIVE);
		scene.camera().setPosition(new PVector(-30,-30,-80));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		scene.showAll();
		*/
		
		/**
		float [][] coef = new float [6][4];		
		coef = scene.camera().getFrustumEquations();
		
		for (int i=0; i<6; i++)
			for (int j=0; j<4; j++)				
				println("coef[" + i + "]" + "[" + j + "] = " + coef[i][j]);
		*/
		
		
		/**
		ArrayList<String> alist = new ArrayList<String>();
		// . . . Add Strings to alist
		alist.add("one");
		alist.add("two");
		alist.add("three");
		alist.add("four");
		alist.add("five");
		
		ListIterator<String> it1 = alist.listIterator();
		ListIterator<String> it2 = alist.listIterator();
		
		String s = it1.next();
		//it2 = it1;
		it2 = alist.listIterator( it1.nextIndex() );		
		//it2.next();

		println("it1:");
		while ( it1.hasNext() ) {
			s = it1.next();  // No downcasting required.
			println(s);
		}
		
		println("it2:");
		while ( it2.hasNext() ) {
			s = it2.next();  // No downcasting required.
			println(s);
		}
		*/

		 /**
		nbKeyFrames = 4;
		
		// myFrame is the Frame that will be interpolated.
		Frame myFrame = new Frame();
		
		// Set myFrame as the KeyFrameInterpolator interpolated Frame.
		kfi_ = new KeyFrameInterpolator(myFrame, this);		
		
		//kfi_.setLoopInterpolation();

		// An array of manipulated (key) frames.
		keyFrame_ = new InteractiveFrame[nbKeyFrames];

		// Create an initial path
		for (int i=0; i<nbKeyFrames; i++) {
			keyFrame_[i] = new InteractiveFrame();
			keyFrame_[i].setPosition(-1.0f + 2.0f*i/(nbKeyFrames-1), 0.0f, 0.0f);
			Quaternion q = new Quaternion(new PVector(0,1,0), i*(3.1416f/6));
			keyFrame_[i].setOrientation(q.x, q.y, q.z, q.w);
			kfi_.addKeyFrame(keyFrame_[i], true);
		}
		
		currentKF_ = 0;
		scene.setInteractiveFrame(keyFrame_[currentKF_]);
		kfi_.startInterpolation();
		// */
		 
		//Quaternion q1 = Quaternion.randomQuaternion();
		//Quaternion q2 = Quaternion.randomQuaternion();
		
		//q1.pMatrix().print();
		//q1.matrix().print();
		
		//q2.pMatrix().print();
		//q2.matrix().print();
		
		//PVector v1 = new PVector(random(-9.9f, 9.9f),random(-9.9f, 9.9f), random(-9.9f, 9.9f));
		//PVector v2 = new PVector(random(-9.9f, 9.9f),random(-9.9f, 9.9f), random(-9.9f, 9.9f));
		//Frame f1 = new Frame(v1, q1);
		
		//f1.pMatrix().print();
		//f1.matrix().print();
		
		//PSFrame f2 = new PSFrame();
		//f2.fromPMatrix(f1.matrix());
		
		//Frame f3 = new Frame();
		//f3.fromMatrix(f1.matrix());
		
		//f2.matrix().print();
		//f3.matrix().print();
		
		//println(f1.translation().x + " " + f1.translation().y + " " + f1.translation().z);
		//println(f3.translation().x + " " + f3.translation().y + " " + f3.translation().z);
		
		//println(f1.rotation().x + " " + f1.rotation().y + " " + f1.rotation().z + " " +  f1.rotation().w);
		//println(f2.rotation().x + " " + f2.rotation().y + " " + f2.rotation().z + " " +  f2.rotation().w);
		//println(f3.rotation().x + " " + f3.rotation().y + " " + f3.rotation().z + " " +  f3.rotation().w);
		
		//PVector v3 = f1.rotation().rotate(v2);
		//PVector v4 = f3.rotation().rotate(v2);
		
		//println(v3.x + " " + v3.y + " " + v3.z);
		//println(v4.x + " " + v4.y + " " + v4.z);
	}

	public void draw() {
		fill(204, 102, 0);
		box(20f, 30f, 50f);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "test.TestingAPI" });
	}
}
