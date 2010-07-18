import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class CameraInterpolation extends PApplet {
	Scene scene;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this); 
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);
		//create the camera path:
		scene.camera().setPosition(new PVector(80,0,0));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		scene.camera().setPosition(new PVector(30,30,-80));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		scene.camera().setPosition(new PVector(-30,-30,-80));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		scene.camera().setPosition(new PVector(-80,0,0));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		//scene.showAll();
		scene.camera().addKeyFrameToPath(1);
		
		//
		scene.camera().setPosition(new PVector(0,0,1));
		scene.camera().lookAt( scene.camera().sceneCenter() );
		scene.showAll();
		
		scene.setCameraPathsAreDrawn(true);
	}

	public void draw() {		
		fill(204, 102, 0);
		box(20, 30, 50);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "CameraInterpolation" });
	}
}
