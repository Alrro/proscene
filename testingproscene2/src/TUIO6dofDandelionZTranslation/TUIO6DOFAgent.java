package TUIO6dofDandelionZTranslation;

import processing.core.*;
import TUIO.TuioCursor;
import TUIO.TuioPoint;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.core.InteractiveCameraFrame;
import remixlab.dandelion.core.InteractiveFrame;
import remixlab.proscene.Scene;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

public class TUIO6DOFAgent extends HIDAgent {
	Scene scene;
	GenericDOF6Event<DOF6Action> event, prevEvent;
	boolean translateZ;

	public TUIO6DOFAgent(Scene scn, String n) {
		super(scn, n);
		this.enableTracking();
		scene = scn;
		//estas acciones son 2dof, pero aun asi se pueden emplear
		cameraProfile().setBinding(DOF6Action.ROTATE);
		//cameraProfile().setBinding(DOF6Action.TRANSLATE);
		//frameProfile().setBinding(DOF6Action.ROTATE);
		//frameProfile().setBinding(DOF6Action.TRANSLATE);
		frameProfile().setBinding(DOF6Action.TRANSLATE3);
		//esta accion requiere al menos 3 dofs:
		//cameraProfile().setBinding(DOF6Action.ROTATE3);
		//y esta 6:
		//cameraProfile().setBinding(DOF6Action.TRANSLATE_ROTATE);
		//pero hace falta reducir los eventos, mas abajo,
		//como se venia haciendo en el TUIOAgent6DOF
		//TODO 
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
		if( (grabber().getClass() == InteractiveFrame.class ) && translateZ )
			prevEvent = new GenericDOF6Event<DOF6Action>(0,0,tcur.getScreenY(scene.height()),0,0,0);
		else
			prevEvent = event.get();
			
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		PVector trans;
		if( (grabber().getClass() == InteractiveFrame.class ) && translateZ )
			trans = new PVector(prevEvent.getX(),prevEvent.getY(),tcur.getScreenY(scene.height()));
		else
			trans = new PVector(tcur.getScreenX(scene.width()),tcur.getScreenY(scene.height()),0);
		event = new GenericDOF6Event<DOF6Action>(prevEvent,
												 trans.x,
				 								 trans.y,
				 								 trans.z,
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
		if( (grabber().getClass() == InteractiveFrame.class ) && translateZ )
			prevEvent = new GenericDOF6Event<DOF6Action>(0,0,tcur.getScreenY(scene.height()),0,0,0);
		else
			prevEvent = event.get();
	}
}