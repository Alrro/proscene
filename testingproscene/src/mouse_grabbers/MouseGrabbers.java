package mouse_grabbers;

import java.util.ArrayList;

import processing.core.*;
import remixlab.proscene.*;

public class MouseGrabbers extends PApplet {
	private static final long serialVersionUID = 1L;

	Scene scene;
	ArrayList boxes;
	Button2D button1, button2;
	int myColor;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		button1 = new ClickButton(scene, new PVector(10, 10), "+", 32, true);
		button2 = new ClickButton(scene, new PVector(
				(10 + button1.myWidth + 5), 10), "-", 32, false);
		scene.setGridIsDrawn(true);
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(150);
		scene.showAll();

		myColor = 125;
		boxes = new ArrayList();
		addBox();
	}

	public void draw() {
		background(0);
		button1.display();
		button2.display();

		for (int i = 0; i < boxes.size(); i++) {
			Box box = (Box) boxes.get(i);
			box.draw(true);
		}
	}

	public void addBox() {
		Box box = new Box(scene);
		box.setSize(20, 20, 20);
		box.setColor(color(0, 0, 255));
		boxes.add(box);
	}

	public void removeBox() {
		if (boxes.size() > 0) {
			scene.removeFromMouseGrabberPool(((Box) boxes.get(0)).iFrame);
			boxes.remove(0);
		}
	}
}
