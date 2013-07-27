package basic;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class FrameInteractionCamInvScale extends PApplet {
	Scene scene;
	InteractiveFrame iFrame1, iFrame2;
	
	public void setup()	{
		size(640, 360, P3D);		
		scene = new Scene(this);		
		iFrame1 = new InteractiveFrame(scene);
		iFrame2 = new InteractiveFrame(scene);
		iFrame1.translate(new Vec(0, 0, 100));
		iFrame2.translate(new Vec(0, 0, -100));
		scene.setAxisIsDrawn(false);
		scene.camera().setPosition(new Vec(0,0,0));
	}

	public void draw() {
		background(0);
		pushMatrix();
		iFrame1.applyTransformation();
		scene.drawAxis(20);
		fill(255, 0, 0);
		box(12, 17, 22);		
		popMatrix();
		
		pushMatrix();
		iFrame2.applyTransformation();
		scene.drawAxis(20);
		fill(0, 255, 0);
		box(12, 17, 22);		
		popMatrix();
	}
	
	public void keyPressed() {
		if(key == 't') {
			scene.camera().frame().scale(2,2,2);			
		}
		if(key == 'T') {
			scene.camera().frame().scale(0.5f,0.5f,0.5f);
		}
		if(key == 'u' || key == 'U') {
			scene.camera().frame().scale(1,1.6f,1);			
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
		PApplet.main(new String[] { "--present", "basic.FrameInteractionCamInvScale" });
	}
}
