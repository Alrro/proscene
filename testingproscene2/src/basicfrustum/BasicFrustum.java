package basicfrustum;
import processing.core.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class BasicFrustum extends PApplet {
	Scene scene;
	
	BoxNode Root;
	
	public void setup()	{		
		size(640, 360, P3D);
		Vector3D p = new Vector3D(100, 70, 130);		
		Root = new BoxNode(p, Vector3D.mult(p, -1.0f));
		Root.buildBoxHierarchy(4);
        scene = new Scene(this);
        scene.enableFrustumEquationsUpdate();        
        scene.setGridIsDrawn(true);
        scene.setAxisIsDrawn(true);
	}

	public void draw() {
		background(0);
		Root.drawIfAllChildrenAreVisible(this, scene.camera());
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "BasicFrustum" });
	}
}
