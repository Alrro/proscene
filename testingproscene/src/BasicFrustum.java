import processing.core.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicFrustum extends PApplet {
	Scene scene;
	
	BoxNode Root;
	
	public void setup()	{		
		size(640, 360, P3D);
		PVector p = new PVector(100, 70, 130);		
		Root = new BoxNode(p, PVector.mult(p, -1.0f));
		Root.buildBoxHierarchy(4);
        scene = new Scene(this);
        scene.enableFrustumEquationsUpdate();
        scene.setHelpIsDrawn(false);
        scene.setGridIsDrawn(true);
        scene.setAxisIsDrawn(true);
	}

	public void draw() {
		Root.drawIfAllChildrenAreVisible(this, scene.camera());
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicFrustum" });
	}
}
