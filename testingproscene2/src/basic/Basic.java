package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.geom.*;


public class Basic extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  scene.setShortcut('v', Scene.KeyboardAction.CAMERA_KIND);	  
	  scene.showAll();
	  //hint(DISABLE_STROKE_PERSPECTIVE);
	}	

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	/**
	public void keyPressed() {
		if(key == 'x' || key == 'X') {
			scene.camera().setPosition(new Vector3D(0,0,-100));
			scene.camera().lookAt(scene.camera().sceneCenter());
			scene.camera().setUpVector(new Vector3D(0,-1,0));
		}
		if(key == 'u' || key == 'U') {			
			println("axis: " + ( (Quaternion) scene.pinhole().frame().orientation()).axis()					
		          + " angle: " + scene.pinhole().frame().orientation().angle() );
		}
		if(key == 'z' || key == 'Z') {
			Vector3D v = scene.pinhole().projectedCoordinatesOf(new Vector3D(0,0,0));
			println(v);
		}		
		if(key == 'y' || key == 'Y') {if(scene.is3D())
				println("scene is 3d");
			else
				println("scene is 2d");
			VFrame tmpFrame = new VFrame(scene.is3D());			
			println("scene.pinhole().frame().worldMatrix().print():");
			scene.pinhole().frame().worldMatrix().print();
			tmpFrame.fromMatrix(scene.pinhole().frame().worldMatrix());
			println("calling tmpFrame.fromMatrix(scene.pinhole().frame().worldMatrix())...");
			println("tmpFrame.worldMatrix().print():");
			tmpFrame.worldMatrix().print();			
			println("camera angle: " + scene.pinhole().frame().orientation().angle() );
			println("tmp angle: " + tmpFrame.orientation().angle() );
		}
	}
	*/
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
