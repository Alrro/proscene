package basic;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

@SuppressWarnings("serial")
public class CameraInterpolation extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		//create the camera path:
		//scene.camera().setPosition(new PVector(80,0,0));
		scene.camera().setPosition(new Vector3D(80,0,0));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		//scene.camera().setPosition(new PVector(30,30,-80));
		scene.camera().setPosition(new Vector3D(30,30,-80));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		//scene.camera().setPosition(new PVector(-30,-30,-80));
		scene.camera().setPosition(new Vector3D(-30,-30,-80));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		//scene.camera().setPosition(new PVector(-80,0,0));
		scene.camera().setPosition(new Vector3D(-80,0,0));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		//
		//scene.camera().setPosition(new PVector(0,0,1));
		scene.camera().setPosition(new Vector3D(0,0,1));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		scene.showAll();
		
		scene.setCameraPathsAreDrawn(true);
	}
	
	public void keyPressed() {
		if( key == 'x' ) {
			scene.camera().keyFrameInterpolator(1).removeKeyFrame(1);
		}
		
		KeyFrameInterpolator kfi = scene.camera().keyFrameInterpolator(scene.path('1'));
		if (kfi == null)
			return;
		
		if( key == 'u' || key == 'v' ) {
			if ( key == 'u')
				kfi.setInterpolationSpeed(kfi.interpolationSpeed()-0.25f);
			if ( key == 'v')
				kfi.setInterpolationSpeed(kfi.interpolationSpeed()+0.25f);
			println("interpolation speed = " + kfi.interpolationSpeed());
		}
		
		if( key == 'l' ) { //toggle  loop
			if( kfi.loopInterpolation() ) {
				kfi.setLoopInterpolation(false);
				println("interpolation is played only once (no loop)");
			}
			else {
				kfi.setLoopInterpolation(true);
				println("interpolation is played within a loop");
			}
		}
	}

	public void draw() {		
		background(0);
		fill(204, 102, 0);
		box(20, 30, 50);
		
		//scene.camera().playPath(arg0);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CameraInterpolation" });
	}
}
