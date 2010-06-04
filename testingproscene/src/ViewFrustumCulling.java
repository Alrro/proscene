import java.awt.Panel;

import processing.core.*;
import remixlab.proscene.*;

public class ViewFrustumCulling extends PApplet {
	Viewer viewer;
	AuxViewer auxViewer;
	Panel p1;
	Panel p2;
	
	static boolean entirely;
	static BoxNode Root;

	public void cmmnds(PApplet p) {
		if(viewer.cullingCamera != null)
			Root.drawIfAllChildrenAreVisible(p, viewer.cullingCamera);
	}

	public void setup() {
		PVector v = new PVector(100, 70, 130);		
		Root = new BoxNode(v, PVector.mult(v, -1.0f));
		Root.buildBoxHierarchy(4);
		
		viewer = new Viewer();		
		auxViewer = new AuxViewer();
		viewer.init();
		auxViewer.init();		

		p1 = new Panel();
		p1.setVisible(false);
		p2 = new Panel();
		p2.setVisible(false);
		p1.add(viewer);
		p2.add(auxViewer);

		this.setEnabled(false);
		add(p1);
		add(p2);
		this.setEnabled(true);
		p1.setVisible(true);
		p2.setVisible(true);
		size(640, 720);
		this.setEnabled(true);
	}

	class Viewer extends PApplet {
		Scene s;
		CullingCamera cullingCamera; 
		public void setup() {
			size(640, 360, P3D);
	        //size(640, 360, OPENGL);
	        s = new Scene(this);
	        cullingCamera = new CullingCamera(this);
	        s.setCamera(cullingCamera);
	        s.setHelpIsDrawn(false);
	        s.setGridIsDrawn(true);
	        s.setAxisIsDrawn(true);
		}		

		public Scene scene() {
			return s;
		}
		
		public void draw() {
			background(0);
			s.beginDraw();
			cmmnds(this);
			//Root.drawIfAllChildrenAreVisible(this, cullingCamera);
			cullingCamera.computeFrustumPlanesEquations();
			s.endDraw();
		}		
	}
	
	class AuxViewer extends PApplet {
		Scene s;
		Camera mainCam = null;
		boolean flag = false;
		
		public void setup() {
			size(640, 360, P3D);		
			s = new Scene(this);
			if (viewer.scene()!=null)
			if (viewer.scene().camera()!=null)
				setMainCameraReference(viewer.scene().camera());
			s.setRadius(200);
	        s.showAll();
			s.setGridIsDrawn(true);
			s.setAxisIsDrawn(false);
			s.setHelpIsDrawn(false);
		}
		
		public Scene scene() {
			return s;
		}
		
		public void setMainCameraReference(Camera mC) {
			mainCam = mC;
		}
		
		public void draw() {
			background(0);
			s.beginDraw();
			//Root.drawIfAllChildrenAreVisible(this, cullingCamera);
			cmmnds(this);			
			if (mainCam != null)
	        	DrawingUtils.drawCamera(this, mainCam);	        
			s.endDraw();
		}
		
		public void keyPressed() {
			if (key == 'x') {
				if(flag) 
					setMainCameraReference(viewer.scene().camera());
				else
					setMainCameraReference(null);
				flag = !flag;
	        }		
		}			
	}	
}
