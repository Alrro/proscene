package deviceGrabbers.buttons;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class Button2D extends DeviceGrabber {
	String myText;
	PFont myFont;
	public int myWidth;
	public int myHeight;
	Vector3D position;

	public Button2D(Scene scn, Vector3D p, int fontSize) {
		this(scn, p, "", fontSize);
	}

	public Button2D(Scene scn, Vector3D p, String t, int fontSize) {
		super(scn);
		position = p;
		myText = t;
		
		//myFont = loadFont("FreeSans-16.vlw");		
		myFont = ((Scene)scene).parent.createFont("FFScala", fontSize);
		
		((Scene)scene).parent.textFont(myFont);
		//((Scene)scene).parent.textMode(PApplet.SCREEN);
		((Scene)scene).parent.textAlign(PApplet.CENTER);
		setText(t);
	}

	public void setText(String text) {
		myText = text;
		myWidth = (int) ((Scene)scene).parent.textWidth(myText);
		myHeight = (int) (((Scene)scene).parent.textAscent() + ((Scene)scene).parent.textDescent());
	}

	public void display() {
		((Scene)scene).parent.pushStyle();
		if (grabsDevice())
			((Scene)scene).parent.fill(255);
		else
			((Scene)scene).parent.fill(100);
		scene.beginScreenDrawing();
		((Scene)scene).parent.text(myText, position.x(), position.y(), myWidth, myHeight);
		scene.endScreenDrawing();
		((Scene)scene).parent.popStyle();
	}

	@Override
	public void checkIfGrabsDevice(int x, int y) {
		// Rectangular activation area
		setGrabsDevice((position.x() <= x) && (x <= position.x() + myWidth) && (position.y() <= y) && (y <= position.y() + myHeight));
	}
}