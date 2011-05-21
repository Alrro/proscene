import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.KeyboardAction;

@SuppressWarnings("serial")
public class BasicUse extends PApplet {
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
		
		scene.camera().setSpinningSensitivity(100);
		
		//scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		//scene.disableBackgroundHanddling();
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();
		
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
		//background(0);
		//scene.displayGlobalHelp(false);
		//scene.displayCurrentCameraProfileHelp(false);
		if(frame != null) {
			frame.setResizable(true);
			PApplet.println("set size");
		}
		noStroke();
		if( scene.camera().sphereIsVisible(new PVector(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
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
			scene.camera().setOrientation(new Quaternion(new PVector(0,0,1), radians));
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
