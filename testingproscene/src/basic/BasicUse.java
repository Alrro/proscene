package basic;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.AbstractScene.CameraKeyboardAction;
import remixlab.remixcam.core.AbstractScene.KeyboardAction;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
	
	public class Vec1 {
		public float [] values;
		
		public Vec1() {
			values = new float[3];
			values[0] = 0;
			values[1] = 0;
			values[2] = 0;
		}
		
		public Vec1(Object any) {		
			this(any, true);
		}
		
		public Vec1(Object any, boolean GV) {
			if(GV) {				
				try {			
					values = (float []) any.getClass().getMethod("getValues").invoke(any);
					} catch ( Exception e ) {
						throw(new RuntimeException("vec cannot handle class in constructor: "+any.getClass(),e));
					}
			}
			else {
				try {
					values = new float[3];
					float [] result = new float [3];
					any.getClass().getMethod("get", new Class[] { float [].class }).invoke(any, result);
					values[0] = result[0];
					values[1] = result[1];
					values[2] = result[2];      
			      
			  		} catch ( Exception e ) {
			  			throw(new RuntimeException("vec cannot handle class in constructor: "+any.getClass(),e));
			  		}
			}
		}
		
		public void set(Object any) {
			try {
				values = (float []) any.getClass().getDeclaredField("values").get(any);
		  		} catch ( Exception e ) {
		  			throw(new RuntimeException("vec cannot handle class in constructor: "+any.getClass(),e));
		  		}
		}		
		
		public float x() {
			return values[0];
		}
		
		public float y() {
			return values[1];
		}
		
		public float z() {
			return values[2];
		}
		
		public float[] get(float[] target) {
			if (target == null) {
				return new float[] { x(), y(), z() };
			}
			if (target.length >= 2) {
				target[0] = x();
				target[1] = y();
			}
			if (target.length >= 3) {
				target[2] = z();
			}
			return target;
		}
		
		public float [] getValues() {
			return values;
		}
	}
	
	public class Vec2 {
		public float [] values;
		
		public Vec2() {
			values = new float[3];
			values[0] = 1;
			values[1] = 2;
			values[2] = 3;
		}
		
		public float x() {
			return values[0];
		}
		
		public float y() {
			return values[1];
		}
		
		public float z() {
			return values[2];
		}
		
		public float[] get(float[] target) {
			if (target == null) {
				return new float[] { x(), y(), z() };
			}
			if (target.length >= 2) {
				target[0] = x();
				target[1] = y();
			}
			if (target.length >= 3) {
				target[2] = z();
			}
			return target;
		}
		
		public float [] getValues() {
			return values;
		}
	}
	
	public class Vec3 {
		public float x, y, z;
		
		public Vec3() {
			x = 4;
			y = 5;
			y = 6;
		}
		
		public Vec3(Object any) {
			try {				
				//values = (float []) any.getClass().getMethod("getValues").invoke(any);
				x = (Float) any.getClass().getDeclaredField("x").get(any);
				y = (Float) any.getClass().getDeclaredField("y").get(any);
				z = (Float) any.getClass().getDeclaredField("z").get(any);
				
				//any.getClass().getDeclaredField("z").get(z);
				} catch ( Exception e ) {
					throw(new RuntimeException("vec cannot handle class in constructor: "+any.getClass(),e));
				}				
		}
		
		public float[] get(float[] target) {
			if (target == null) {
				return new float[] { x, y, z };
			}
			if (target.length >= 2) {
				target[0] = x;
				target[1] = y;
			}
			if (target.length >= 3) {
				target[2] = z;
			}
			return target;
		}
		
		public float [] getValues() {
			float [] res = new float [3];
			res[0] = x;
			res[1] = y;
			res[2] = z;
			return res;
		}
	}
	
	public class Vec4 {
		public float x, y, z;
		
		public Vec4() {
			x = 7;
			y = 8;
			z = 9;
		}
		
		public float[] get(float[] target) {
			if (target == null) {
				return new float[] { x, y, z };
			}
			if (target.length >= 2) {
				target[0] = x;
				target[1] = y;
			}
			if (target.length >= 3) {
				target[2] = z;
			}
			return target;
		}
		
		public float [] getValues() {
			float [] res = new float [3];
			res[0] = x;
			res[1] = y;
			res[2] = z;
			return res;
		}
	}
	
	public class Vec5 {
		public Object vectorWrap;
		
		public Vec5() {
			vectorWrap = new Object();
		}
		
		public Vec5(Object any) {
			vectorWrap = any;
		}				
		
		public float x() {
			try {
				return (Float) vectorWrap.getClass().getDeclaredField("x").get(vectorWrap);
				} catch ( Exception e ) {
					throw(new RuntimeException("vec cannot handle class in constructor: "+vectorWrap.getClass(),e));
				}			
		}
		
		public float y() {
			try {
				return (Float) vectorWrap.getClass().getDeclaredField("y").get(vectorWrap);
				} catch ( Exception e ) {
					throw(new RuntimeException("vec cannot handle class in constructor: "+vectorWrap.getClass(),e));
				}			
		}
		
		public float z() {
			try {
				return (Float) vectorWrap.getClass().getDeclaredField("z").get(vectorWrap);
				} catch ( Exception e ) {
					throw(new RuntimeException("vec cannot handle class in constructor: "+vectorWrap.getClass(),e));
				}			
		}		
		
		public float[] get(float[] target) {
			if (target == null) {
				return new float[] { x(), y(), z() };
			}
			if (target.length >= 2) {
				target[0] = x();
				target[1] = y();
			}
			if (target.length >= 3) {
				target[2] = z();
			}
			return target;
		}		
	}
	
	PImage b;
	Scene scene;
	MyCamProfile profile;
	CameraProfile cp;
	float radians = 0;
	
	public void setup()	{
		size(640, 360, P3D);
		
		println("major version: " + Scene.majorVersionNumber() );
		println("minor version: " + Scene.minorVersionNumber() );
		
		scene = new Scene(this);		
		
		//scene.camera().setSpinningSensitivity(100);
		
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		//scene.disableBackgroundHanddling();
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();		
		
		//frameRate(200);
		
		/**
		cp = new CameraProfile(scene, "ARCBALL", CameraProfile.Mode.ARCBALL);
	    scene.registerCameraProfile(cp);
	    scene.unregisterCameraProfile("FIRST_PERSON");
	    scene.unregisterCameraProfile("WHEELED_ARCBALL");
	    // */
		
		//scene.addDrawHandler(this, "m1");		
		
		//b = loadImage("pierre.png");
		//scene.background(b);
		//frame.setResizable(true);
	}

	// /**
	public void draw() {		
		//PVector eulerAngles = scene.camera().orientation().eulerAngles();
		//println(eulerAngles);
		//scene.background(b);		
		background(0);
		//scene.displayGlobalHelp(false);
		//scene.displayCurrentCameraProfileHelp(false);
		if(frame != null) {
			frame.setResizable(true);
			PApplet.println("set size");
		}
		noStroke();
		if( scene.camera().sphereIsVisible(new Vector3D(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);		
	}
	// */
	
	public void keyPressed() {
		if (key == 'x') {
			if( scene.isCameraProfileRegistered(profile) ) {
				scene.unregisterCameraProfile(profile);				
			}
			else {
				scene.registerCameraProfile(profile);
			}
		}
		
		if (key == 'c') {
			radians += PApplet.QUARTER_PI/4;
			scene.camera().setOrientation(new Quaternion(scene.camera().frame().zAxis(), radians));
		}
		
		if (key == 'w') {
			radians += PApplet.QUARTER_PI/4;
			scene.camera().setOrientation(new Quaternion(new Vector3D(0,0,1), radians));
		}
		
		/**
		if(key == 'y') {
			println(frameRate);
			PVector pvec = new PVector(1,2,3);			
			Vec3D vec = new Vec3D(new PVector(1,2,3));
			println(vec.x);
			println(vec.y);
			println(vec.z);
			//scene.camera().testing3(vec);s
			Vec3D otherVec = new Vec3D(0.0f,1.0f,2.0f);
		}
		*/
		
		if(key == 'z') {
			/**
			Vec2 vec2 = new Vec2();
			Vec1 vec1 = new Vec1(vec2, false);
			*/
			
			/**
			Vec1 vec1 = new Vec1();
			vec1.set(vec2);
			*/
				
			/**
			vec2.values[2] = -3;
			vec1.values[0] = -2;			
			
			println(vec1.x());
			println(vec1.y());
			println(vec1.z());
			println(vec2.x());
			println(vec2.y());
			println(vec2.z());
			*/
			
			/**
			float [] test = new float [3];
			vec2.get(test);
			
			println(test[0]);
			println(test[1]);
			println(test[2]);
			*/
			
			/**
			test[0] = 5;
			test[1] = 6;
			test[2] = 7;
			println(vec1.x());
			println(vec1.y());
			println(vec1.z());
			println(vec2.x());
			println(vec2.y());
			println(vec2.z());
			println(test[0]);
			println(test[1]);
			println(test[2]);
			*/
			
			/**
			Vec4 vec4 = new Vec4();
			Vec3 vec3 = new Vec3(vec4);
			
			vec4.z = -3;
			vec3.x = -2;			
			
			println(vec3.x);
			println(vec3.y);
			println(vec3.z);
			println(vec4.x);
			println(vec4.y);
			println(vec4.z);
			// */
			
			PVector pvec = new PVector(1,2,3);
			Vec5 vec5 = new Vec5(pvec);
			println(vec5.x());
			println(vec5.y());
			println(vec5.z());
			pvec.x = -6;
			println(vec5.x());
			println(vec5.y());
			println(vec5.z());
		}
	}
	
	
	public void m1(PApplet p) {
		p.fill(255, 0, 0);
		p.sphere(40);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicUse" });
	}
	
	// /**
	//public class MyCamProfile extends ThirdPersonCameraProfile {
	public class MyCamProfile extends CameraProfile {
		public MyCamProfile(Scene arg0, String arg1) {
			super(arg0, arg1);
			setShortcut('a', CameraKeyboardAction.MOVE_CAMERA_DOWN);
		}
		
	}
	// */
}
