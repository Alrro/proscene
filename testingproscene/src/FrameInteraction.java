import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	InteractiveAvatarFrame iFrame;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		//scene.camera().setKind(Camera.Kind.STANDARD);
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setGridIsDrawn(true);
		scene.setAxisIsDrawn(true);		
		scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame = new InteractiveAvatarFrame(scene);
		//scene.setInteractiveFrame(iFrame);
		scene.interactiveFrame().translate(new PVector(30, 30, 0));
	}

	public void draw() {	
		fill(204, 102, 0);
		box(20, 30, 40);  
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		scene.interactiveFrame().applyTransformation(this);//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		// Draw a second box
		if (scene.interactiveFrame().grabsMouse()) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}		
		popMatrix();
	}
	
	public void keyPressed() {
		if((key == 'u')) {
			scene.setAvatar(iFrame);		
			for (String s: scene.keys())
				print(s);
		}		
		if (key == 'U') {
			scene.unsetAvatar();
			for (String s: scene.keys())
				print(s);
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "FrameInteraction" });
	}
}
