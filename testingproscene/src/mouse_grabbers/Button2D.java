package mouse_grabbers;

import processing.core.*;
import remixlab.proscene.*;

public class Button2D extends MouseGrabber {
	String myText;
	PFont myFont;
	int myWidth;
	int myHeight;
	PVector position;
	Scene scene;

	Button2D(Scene scn, PVector p, int fontSize) {
		this(scn, p, "", fontSize);
		scene = scn;
	}

	Button2D(Scene scn, PVector p, String t, int fontSize) {
		super(scn);
		scene = scn;
		position = p;
		myText = t;
		myFont = scene.parent.createFont("FFScala", fontSize);
		scene.parent.textFont(myFont);
		scene.parent.textMode(PApplet.SCREEN);
		scene.parent.textAlign(PApplet.CENTER);
		setText(t);
	}

	public void setText(String text) {
		myText = text;
		myWidth = (int) scene.parent.textWidth(myText);
		myHeight = (int) (scene.parent.textAscent() + scene.parent.textDescent());
	}

	public void display() {
		scene.parent.pushStyle();
		if (grabsMouse())
			scene.parent.fill(255);
		else
			scene.parent.fill(100);
		scene.beginScreenDrawing();
		scene.parent.text(myText, position.x, position.y, myWidth, myHeight);
		scene.endScreenDrawing();
		scene.parent.popStyle();
	}

	public void checkIfGrabsMouse(int x, int y, Camera camera) {
		// Rectangular activation area
		setGrabsMouse((position.x <= x) && (x <= position.x + myWidth)
				&& (position.y <= y) && (y <= position.y + myHeight));
	}
}