package TUIO2dofDandelion;

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

public class TUIO2DOFAgent extends HIDAgent {
	Scene scene;
	GenericDOF2Event<DOF2Action> event, prevEvent;

	public TUIO2DOFAgent(Scene scn, String n) {
		super(scn, n);
		this.enableTracking();
		scene = scn;
		//estas acciones son 2dof, pero aun asi se pueden emplear
		cameraProfile().setBinding(DOF6Action.ROTATE);
		//cameraProfile().setBinding(DOF6Action.TRANSLATE);
		//frameProfile().setBinding(DOF6Action.ROTATE);
		frameProfile().setBinding(DOF6Action.TRANSLATE);		
		//esta accion requiere al menos 3 dofs:
		//cameraProfile().setBinding(DOF6Action.ROTATE3);
		//y esta 6:
		//cameraProfile().setBinding(DOF6Action.TRANSLATE_ROTATE);
		//pero hace falta reducir los eventos, mas abajo,
		//como se venia haciendo en el TUIOAgent6DOF
		//TODO 
	}

	public void addTuioCursor(TuioCursor tcur) {
		event = new GenericDOF2Event<DOF2Action>(prevEvent,
												 tcur.getScreenX(scene.width()),
												 tcur.getScreenY(scene.height()),
												 TH_NOMODIFIER_MASK,
												 TH_NOBUTTON);
		updateGrabber(event);
		prevEvent = event.get();
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		event = new GenericDOF2Event<DOF2Action>(prevEvent,
				 								 tcur.getScreenX(scene.width()),
				 								 tcur.getScreenY(scene.height()),
				 								 TH_NOMODIFIER_MASK,
				 								 TH_NOBUTTON);
		handle(event);
		prevEvent = event.get();
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		event = new GenericDOF2Event<DOF2Action>(prevEvent,
												 tcur.getScreenX(scene.width()),
												 tcur.getScreenY(scene.height()),
												 TH_NOMODIFIER_MASK,
												 TH_NOBUTTON);
		updateGrabber(event);
		prevEvent = event.get();
	}
}