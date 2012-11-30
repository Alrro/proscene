package basic_geom;

import geom.Box;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class PointUnderPixel extends PApplet {
	Scene scene;
	Box[] boxes;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setShortcut('z', Scene.KeyboardAction.ARP_FROM_PIXEL);
		// add the click actions to all camera profiles
		CameraProfile[] camProfiles = scene.getCameraProfiles();
		for (int i = 0; i < camProfiles.length; i++) {
			// left click will zoom on pixel:
			camProfiles[i].setClickBinding(Scene.Button.LEFT, Scene.ClickAction.ZOOM_ON_PIXEL);
			// middle click will show all the scene:
			camProfiles[i].setClickBinding(Scene.Button.MIDDLE, Scene.ClickAction.SHOW_ALL);
			// right click will will set the arcball reference point:
			camProfiles[i].setClickBinding(Scene.Button.RIGHT, Scene.ClickAction.ARP_FROM_PIXEL);
			// double click with the middle button while pressing SHIFT will reset the arcball reference point:
			camProfiles[i].setClickBinding(Scene.Modifier.SHIFT.ID, Scene.Button.MIDDLE, 2, Scene.ClickAction.RESET_ARP);
		}

		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);
		scene.setRadius(150);
		scene.showAll();
		boxes = new Box[50];
		// create an array of boxes with random positions, sizes and colors
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(scene);
	}

	public void draw() {
		background(0);
		for (int i = 0; i < boxes.length; i++)
			boxes[i].draw(true);
	}

	public void keyPressed() {
		if (key == 'u' || key == 'U')
			if (scene.isRightHanded())
				scene.setLeftHanded();
			else
				scene.setRightHanded();
		if (key == 'v' || key == 'V')
			if (scene.camera().unprojectCacheIsOptimized())
				scene.camera().optimizeUnprojectCache(false);
			else
				scene.camera().optimizeUnprojectCache(true);
		if (scene.isRightHanded())
			println("scene is RH");
		else
			println("scene is LH");
		if (scene.camera().unprojectCacheIsOptimized())
			println("camera cache activated");
		else
			println("camera cache NOT activated");
		float[] wh = scene.pinhole().getOrthoWidthHeight();
		println("half width: " + wh[0] + " half height: " + wh[1]);
		println("zNear: " + scene.camera().zNear() + " zNear: " + scene.camera().zFar());
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "PointUnderPixel" });
	}
}
