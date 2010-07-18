import processing.core.*;
import remixlab.proscene.*;
import napplet.*;

public class DriveAndRoll extends PApplet {
	NAppletManager nappletManager;
	NApplet mainNApplet;

	public void setup() {
		size(640, 720, P3D);
		// instantiate the viewers and embed them into a napplet manager
		nappletManager = new NAppletManager(this);
		mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);		
		nappletManager.createNApplet("AuxiliarViewer", 0, 360);
	}

	public void draw() {
		background(50);
	}
	
	/**
	public void mainDrawing(Scene s) {
		//Scene s = ((MainViewer)(mainNApplet)).getScene();
		s.parent.fill(204, 102, 0);
		s.parent.box(20, 30, 40);  
		// Save the current model view matrix
		s.parent.pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		s.interactiveFrame().applyTransformation(s.parent);//very efficient
		// Draw an axis using the Scene static function
		s.drawAxis(20);
		// Draw a second box
		if (s.interactiveFrame().grabsMouse()) {
			s.parent.fill(255, 0, 0);
			s.parent.box(12, 17, 22);
		}
		else if (s.interactiveFrameIsDrawn()) {
			s.parent.fill(0, 255, 255);
			s.parent.box(12, 17, 22);
		}
		else {
			s.parent.fill(0,0,255);
			s.parent.box(10, 15, 20);
		}		
		s.parent.popMatrix();
	}
	*/
	
	// /**
	public void mainDrawing(PApplet p) {
		Scene s = ((MainViewer)(mainNApplet)).getScene();
		p.fill(204, 102, 0);
		p.box(20, 30, 40);  
		// Save the current model view matrix
		p.pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		s.interactiveFrame().applyTransformation(p);//very efficient
		// Draw an axis using the Scene static function
		s.drawAxis(20);
		// Draw a second box
		if (s.interactiveFrame().grabsMouse()) {
			p.fill(255, 0, 0);
			p.box(12, 17, 22);
		}
		else if (s.interactiveFrameIsDrawn()) {
			p.fill(0, 255, 255);
			p.box(12, 17, 22);
		}
		else {
			p.fill(0,0,255);
			p.box(10, 15, 20);
		}		
		p.popMatrix();
	}
	// */
	
	public void auxiliarDrawing(PApplet p) {
		mainDrawing(p);
		DrawingUtils.drawCamera(p, ((MainViewer)(mainNApplet)).getScene().camera());
	}

	public class MainViewer extends NApplet {
		Scene scene;
		InteractiveAvatarFrame iFrame;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			// enable computation of the frustum planes equations (disabled by
			// default)
			scene.enableFrustumEquationsUpdate();
			scene.setHelpIsDrawn(false);
			scene.setGridIsDrawn(false);
			iFrame = new InteractiveAvatarFrame(scene);
			//scene.setAvatar(iFrame);
			scene.setInteractiveFrame(iFrame);
			scene.interactiveFrame().translate(new PVector(30, 30, 0));
			
			// we add the external drawing method declared in the View DriveAndRoll Class
		    scene.addDrawHandler(parentPApplet, "mainDrawing");
		}

		// We need to pass the scene to the auxiliar viewer
		Scene getScene() {
			return scene;
		}
		
		public void draw() {}
		
		 /**
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
		//*/
	}

	public class AuxiliarViewer extends NApplet {
		Scene scene;

		public void setup() {
			size(640, 360, P3D);
			scene = new Scene(this);
			scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
			scene.setAxisIsDrawn(false);
			scene.setGridIsDrawn(false);
			scene.setHelpIsDrawn(false);
			scene.setRadius(200);
			scene.showAll();
			scene.addDrawHandler(parentPApplet, "auxiliarDrawing");
		}
		
		public void draw() {}

		/**
		public void draw() {			
			fill(204, 102, 0);
			box(20, 30, 40);  
			// Save the current model view matrix
			pushMatrix();
			// Multiply matrix to get in the frame coordinate system.
			// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
			//scene.interactiveFrame().applyTransformation(this);//very efficient
			((MainViewer) mainNApplet).getScene().interactiveFrame().applyTransformation(this);//very efficient
			// Draw an axis using the Scene static function
			//scene.drawAxis(20);
			((MainViewer) mainNApplet).getScene().drawAxis(20);
			// Draw a second box
			//if (scene.interactiveFrame().grabsMouse()) {
			if (((MainViewer) mainNApplet).getScene().interactiveFrame().grabsMouse()) {
				fill(255, 0, 0);
				box(12, 17, 22);
			}
			//else if (scene.interactiveFrameIsDrawn()) {
			else if (((MainViewer) mainNApplet).getScene().interactiveFrameIsDrawn()) {
				fill(0, 255, 255);
				box(12, 17, 22);
			}
			else {
				fill(0,0,255);
				box(10, 15, 20);
			}		
			popMatrix();
			
			DrawingUtils.drawCamera(this, ((MainViewer) mainNApplet).getScene().camera());
		}
		*/
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "DriveAndRoll" });
	}
}
