package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.*;


public class BasicCad extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  //Register a CAD Camera profile and name it "CAD_CAM"
	  scene.registerCameraProfile(new CameraProfile(scene, "CAD_CAM", CameraProfile.Mode.CAD));
	  //Set the CAD_CAM as the current camera profile
	  scene.setCurrentCameraProfile("CAD_CAM");
	  //Unregister the  first-person camera profile (i.e., leave WHEELED_ARCBALL and CAD_CAM)
	  scene.unregisterCameraProfile("FIRST_PERSON");	  
	  //scene.camera().frame().setCADAxis(new Vector3D(0, 1, 0));
	  //scene.camera().frame().setCADAxis(new Vector3D(0, 0, -1));
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public void keyPressed() {
		if(key == 't') {
			scene.camera().frame().scale(2,2,2);			
		}
		if(key == 'T') {
			scene.camera().frame().scale(0.5f,0.5f,0.5f);
		}
		if(key == 'u' || key == 'U') {
			scene.camera().frame().scale(1,1.2f,1);			
		}
		
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}
		
		if(key == 'x' || key == 'X') {
			scene.camera().frame().scale(-1,1,1);			
		}
		if(key == 'y' || key == 'Y') {
			scene.camera().frame().scale(1,-1,1);			
		}
		if(key == 'z' || key == 'Z') {
			scene.camera().frame().scale(1,1,-1);
		}		
				
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		println( "scene.camera().frame().scaling(): " + scene.camera().frame().scaling() );
		println( "scene.camera().frame().magnitude(): " + scene.camera().frame().magnitude() );
		println("cam scene radius: " + scene.camera().sceneRadius());
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
