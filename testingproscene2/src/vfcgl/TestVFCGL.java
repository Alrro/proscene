package vfcgl;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class TestVFCGL extends PApplet {
	AABox[] cajas;
	int boxCount = 1000;
	static int volSize = 500;
	static int renderedCubes;
	boolean drawBoundingVolumes = false;
	Scene scene;

	public void setup() {
		//size(640, 480, GLConstants.GLGRAPHICS);
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setRadius(volSize);
		scene.showAll();
		// enable computation of the frustum planes equations (disabled by
		// default)
		scene.enableFrustumEquationsUpdate();
		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);

		cajas = new AABox[boxCount];
		for (int i = 0; i < cajas.length; i++)
			cajas[i] = new AABox();
	}

	public void draw() {
		background(0);
		renderedCubes = 0;
		for (int i = 0; i < cajas.length; i++) {
			cajas[i].draw(this, scene.camera(), drawBoundingVolumes);
		}
	}

	public void keyPressed() {
		if (key == 'n' || key == 'N') {
			println("number of rendered cubes = " + renderedCubes);
			return;
		}		
		if (key == 'b' || key == 'B') {
			drawBoundingVolumes = !drawBoundingVolumes;
		}
		if (drawBoundingVolumes) {
			println("Enabling drawing of bounding volumes");
		} else {
			println("Disabling drawing of bounding volumes");
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "TestVFCGL" });
	}
}