package grabbers.complex;

import processing.event.*;
import remixlab.dandelion.core.AbstractScene;
import remixlab.proscene.Scene;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.generic.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.event.*;

//import remixlab.remixcam.interactivity.*;

public class CustomisedAgent extends Agent {
	GenericDOF2Event event, prevEvent;
	GenericKeyboardEvent keyEvent;
	Grabbers parent;

	public CustomisedAgent(AbstractScene scn, String n) {
		super(scn.terseHandler(), n);
		parent = (Grabbers) ((Scene) scn).parent;
		parent.registerMethod("mouseEvent", this);
		//parent.registerMethod("keyEvent", this);
	}

	public void mouseEvent(MouseEvent e) {
		if (e.getAction() == MouseEvent.MOVE) {
			event = new GenericDOF2Event(prevEvent, e.getX(), e.getY());
			updateGrabber(event);
			prevEvent = event.get();
		}
		/**
		if (e.getAction() == MouseEvent.DRAG) {
			event = new GenericDOF2Event(prevEvent, e.getX(), e.getY(),	e.getModifiers(), e.getButton());
			handle(event);
			prevEvent = event.get();
		}
		if (e.getAction() == MouseEvent.RELEASE) {
			event = new GenericDOF2Event(prevEvent, e.getX(), e.getY());
			updateGrabber(event);
			prevEvent = event.get();
		}
		*/
		if( e.getAction() == MouseEvent.CLICK ) {
			handle(new GenericClickEvent(e.getModifiers(), e.getButton(), e.getCount()));
		}	
	}
	
	public void keyEvent(KeyEvent e) {
		keyEvent = new GenericKeyboardEvent( e.getModifiers(), e.getKey(), e.getKeyCode() );
		if(e.getAction() == KeyEvent.RELEASE)
			handle(keyEvent);
	}
}
