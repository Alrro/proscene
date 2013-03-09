package mouseSensitivities;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.devices.*;
import remixlab.remixcam.geom.*;

public class Button2D extends DeviceGrabber {
	String myText;
	int myWidth;
	int myHeight;
	Vector3D position;
	Scene scene;
	
	Button2D(Scene scn, Vector3D p) {
		this(scn, p, "");
		scene = scn;
	}

	Button2D(Scene scn, Vector3D p, String t) {
		super(scn);
		scene = scn;
		position = p;
		myText = t;
		setText(t);
	}

	public void setText(String text) {
		myText = text;
		myWidth = (int) scene.parent.textWidth(myText);
		myHeight = (int) (scene.parent.textAscent() + scene.parent.textDescent());
	}

	public void display() {
		scene.parent.pushStyle();		
		scene.parent.colorMode(PApplet.HSB);
		if (grabsMouse())
			scene.parent.fill(50,255,255);
		else
			scene.parent.fill(50,255,100);
		scene.parent.text(myText, position.x(), position.y(), myWidth, myHeight);
		scene.parent.popStyle();		
	}

	@Override
	public void checkIfGrabsMouse(int x, int y, Pinhole camera) {
		// Rectangular activation area
		setGrabsMouse((position.x() <= x) && (x <= position.x() + myWidth) && (position.y() <= y) && (y <= position.y() + myHeight));
	}
}