package TUIO6dofDandelion;

import processing.core.*;
import TUIO.TuioCursor;
import TUIO.TuioPoint;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.proscene.Scene;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

public class TUIO6DOFAgent extends HIDAgent {
	Scene scene;
	GenericDOF6Event<DOF6Action> event, prevEvent;

	public TUIO6DOFAgent(Scene scn, String n) {
		super(scn, n);
		this.enableTracking();
		scene = scn;
		cameraProfile().setBinding(DOF6Action.ROTATE);
		//cameraProfile().setBinding(DOF6Action.TRANSLATE);
		frameProfile().setBinding(DOF6Action.ROTATE);
		//frameProfile().setBinding(DOF6Action.TRANSLATE);
	}

	public void addTuioCursor(TuioCursor tcur) {
		event = new GenericDOF6Event<DOF6Action>(prevEvent,
												 tcur.getScreenX(scene.width()),
												 tcur.getScreenY(scene.height()),
												 0,
												 0,
												 0,
												 0,
												 TH_NOMODIFIER_MASK,
												 TH_NOBUTTON);
		updateGrabber(event);
		prevEvent = event.get();
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		event = new GenericDOF6Event<DOF6Action>(prevEvent,
				 								 tcur.getScreenX(scene.width()),
				 								 tcur.getScreenY(scene.height()),
				 								 0,
				 								 0,
				 								 0,
				 								 0,
				 								 TH_NOMODIFIER_MASK,
				 								 TH_NOBUTTON);
		handle(event);
		prevEvent = event.get();
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		event = new GenericDOF6Event<DOF6Action>(prevEvent,
												 tcur.getScreenX(scene.width()),
												 tcur.getScreenY(scene.height()),
												 0,
												 0,
												 0,
												 0,
												 TH_NOMODIFIER_MASK,
												 TH_NOBUTTON);
		updateGrabber(event);
		prevEvent = event.get();
	}
}