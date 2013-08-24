package TUIO6dof;
import java.util.HashMap;
import java.util.Map;

import processing.core.*;
import TUIO.TuioCursor;
import TUIO.TuioPoint;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;

public class TUIOAgent6DOF
		extends
		GenericMotionAgent<GenericMotionProfile<MotionAction>, GenericClickProfile<ClickAction>>
		implements EventConstants {

	PGraphics canvas;

	GenericDOF6Event<MotionAction> event, prevEvent;

	Map<Integer, Grabbable> grabMap = new HashMap<Integer, Grabbable>();

	Integer cursorIdRotation = null;

	public TUIOAgent6DOF(TerseHandler scn, String n, PGraphics canvas) {

		super(new GenericMotionProfile<MotionAction>(),	new GenericClickProfile<ClickAction>(), scn, n);

		this.canvas = canvas;
		// default bindings
		clickProfile().setClickBinding(TH_LEFT, 1, ClickAction.CHANGE_COLOR);
		clickProfile().setClickBinding(TH_META, TH_RIGHT, 1, ClickAction.CHANGE_ROTATION);
		// clickProfile().setClickBinding(TH_META, TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
		// clickProfile().setClickBinding((TH_META | TH_SHIFT), TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
		profile().setBinding(MotionAction.CHANGE_POSITION);
		profile().setBinding(TH_META, TH_RIGHT, MotionAction.CHANGE_ROTATION);
		// profile().setBinding(TH_SHIFT, TH_LEFT, MotionAction.CHANGE_SHAPE);
		// profile().setBinding(TH_META, TH_RIGHT, MotionAction.CHANGE_SHAPE);
	}

	public void mouseEvent(processing.event.MouseEvent e) {
		if (e.getAction() == processing.event.MouseEvent.MOVE) {
			event = new GenericDOF6Event<MotionAction>(prevEvent, e.getX(),
					e.getY(), 0, 0, 0, 0, e.getModifiers(), e.getButton());
			updateGrabber(event);
			prevEvent = event.get();
			System.out
					.println("move " + e.getModifiers() + " " + e.getButton());
		}
		if (e.getAction() == processing.event.MouseEvent.DRAG) {
			event = new GenericDOF6Event<MotionAction>(prevEvent, e.getX(),
					e.getY(), 0, 0, 0, 0, e.getModifiers(), e.getButton());
			handle(event);
			prevEvent = event.get();
			System.out.println("drag");
		}
		if (e.getAction() == processing.event.MouseEvent.CLICK) {
			handle(new GenericClickEvent<ClickAction>(e.getModifiers(),
					e.getButton(), e.getCount()));
			System.out.println("click");
		}
	}

	public void addTuioCursor(TuioCursor tcur) {

		// event = new GenericDOF2Event<MotionAction>(prevEvent,
		// tcur.getScreenX(canvas.width), tcur.getScreenY(canvas.height),
		// 0, 0);

		event = new GenericDOF6Event<MotionAction>(prevEvent,
				tcur.getScreenX(canvas.width), tcur.getScreenY(canvas.height),
				0, 0, 0, 0, 0, 0);

		Grabbable grabbable = updateGrabber(event);
		if (grabbable != null) {
			grabMap.put(tcur.getCursorID(), grabbable);
		} else if (grabMap.size() == 1) {
			cursorIdRotation = tcur.getCursorID();
		}
		// Grabbable grb2 = trackedGrabber;

	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {

		Grabbable trackedGrabber = grabMap.get(tcur.getCursorID());

		if (trackedGrabber != null) {
			if (cursorIdRotation != null && grabMap.size() == 1) {
//				event = new GenericDOF6Event<MotionAction>(prevEvent,
//						tcur.getScreenX(canvas.width), 0,
//						tcur.getScreenY(canvas.height)-canvas.height/2 , 0, 0, 0, 0, TH_LEFT);
				//PRIMER INTENTO DE MOVER EN Z ... 
				// updateGrabber(event);
			} else {
				event = new GenericDOF6Event<MotionAction>(prevEvent, tcur.getScreenX(canvas.width),
						                                              tcur.getScreenY(canvas.height),
						                                              0, 0, 0, 0, 0, TH_NOBUTTON);
				// updateGrabber(event);
			}
			disableTracking();
		    setDefaultGrabber(trackedGrabber);
			handle(event);
			enableTracking();
		} else if (cursorIdRotation != null
				&& cursorIdRotation == tcur.getCursorID()
				&& grabMap.size() == 1) {
			// vamos a rotar el seleccionado
			TuioPoint last = tcur.getPath().get(1);
			int lastX = last.getScreenX(canvas.width);
			int lastY = last.getScreenY(canvas.height);

			int x = tcur.getScreenX(canvas.width);
			int y = tcur.getScreenY(canvas.height);

			int difX = lastX - x;
			int difY = lastY + y;
			float ratio = 100;
			event = new GenericDOF6Event<MotionAction>(prevEvent, 0, 0, 0, -difY / ratio, -difX / ratio, 0, 0, TH_RIGHT);
			// updateGrabber(event);
			Grabbable grabbable4Rotate = grabMap.values().iterator().next();

			PApplet.println("" + difX + " " + difY + " object"	+ grabbable4Rotate);
			disableTracking();
		    setDefaultGrabber(grabbable4Rotate);
			handle(event);
			enableTracking();

		}
		prevEvent = event.get();
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		event = new GenericDOF6Event<MotionAction>(prevEvent, -1000, -1000, 0,
				0, 0, 0, 0, 0);

		updateGrabber(event);

		Grabbable grabbable = grabMap.remove(tcur.getCursorID());

		// queremos quitar tb el otro parametro, para no intentar rotar algo que ya hemos soltado
		if (grabbable != null) {
			cursorIdRotation = null;
		}

		if (cursorIdRotation != null && tcur.getCursorID() == cursorIdRotation) {
			cursorIdRotation = null;
		}
	}
}