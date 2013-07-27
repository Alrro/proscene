package grabbers.complex;

import processing.core.*;
import remixlab.proscene.Scene;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.DOF2Event;
import remixlab.tersehandling.event.TerseEvent;

public abstract class Button2D extends AbstractGrabber {
	Scene scene;	
	protected Grabbers parent;
	String myText;
	PFont myFont;
	public int myWidth;
	public int myHeight;
	PVector position;
	protected boolean grbs;

	public Button2D(Scene scn, PVector p) {
		this(scn, p, "");
	}

	public Button2D(Scene scn, PVector p, String t) {
		scene = scn;
		scene.prosceneMouse.addInPool(this);
		parent = (Grabbers)scene.parent;
		position = p;
		myText = t;
		myFont = parent.createFont("FFScala", 24);
		parent.textFont(myFont);
		parent.textAlign(PApplet.CENTER);
		setText(t);
	}

	void setText(String text) {
		myText = text;
		myWidth = (int) parent.textWidth(myText);
		myHeight = (int) (parent.textAscent() + parent.textDescent());
	}

	void display() {
		parent.pushStyle();
		
		parent.fill(255);
		if( grabsAgent(scene.prosceneMouse) )
			parent.fill(255);
		else
			parent.fill(100);

		scene.beginScreenDrawing();
		parent.text(myText, position.x, position.y, myWidth, myHeight);
		scene.endScreenDrawing();
		parent.popStyle();
	}

	@Override
	public boolean checkIfGrabsInput(TerseEvent event) {
		if(event instanceof DOF2Event) {
			float x = ((DOF2Event)event).getX();
			float y = ((DOF2Event)event).getY();
			return ((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
		}
		else
			return false;
	}
}
