package TUIO6dof;

import processing.core.*;

import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioPoint;
import TUIO.TuioProcessing;
import TUIO.TuioTime;

public class Tuio6DOF extends PApplet {

	int w = 800;
	int h = 500;
	TUIOAgent6DOF agent;
	TerseHandler terseHandler;
	GrabbableCircle6DOF[] circles;

	TuioProcessing tuioClient;

	public void setup() {
		size(w, h, P3D);
		terseHandler = new TerseHandler();
		agent = new TUIOAgent6DOF(terseHandler, "my_mouse", g);
		registerMethod("mouseEvent", agent);
		circles = new GrabbableCircle6DOF[50];
		for (int i = 0; i < circles.length; i++)
			circles[i] = new GrabbableCircle6DOF(this, g, agent);

		tuioClient = new TuioProcessing(this, 3333);
	}

	public void draw() {
		background(0);
		for (int i = 0; i < circles.length; i++) {
			if (circles[i].grabsAgent(agent))
				circles[i].draw(color(255, 0, 0), g);
			else
				circles[i].draw(g);
		}

		drawTuio();

		terseHandler.handle();
	}

	private void drawTuio() {

		float obj_size = 10;
		float cur_size = 5;

		Vector tuioObjectList = tuioClient.getTuioObjects();
		for (int i = 0; i < tuioObjectList.size(); i++) {
			TuioObject tobj = (TuioObject) tuioObjectList.elementAt(i);
			stroke(0);
			fill(0);
			pushMatrix();
			translate(tobj.getScreenX(width), tobj.getScreenY(height));
			rotate(tobj.getAngle());
			rect(-obj_size / 2, -obj_size / 2, obj_size, obj_size);
			popMatrix();
			fill(255);
			text("" + tobj.getSymbolID(), tobj.getScreenX(width),
					tobj.getScreenY(height));
		}

		Vector tuioCursorList = tuioClient.getTuioCursors();
		for (int i = 0; i < tuioCursorList.size(); i++) {
			TuioCursor tcur = (TuioCursor) tuioCursorList.elementAt(i);
			Vector pointList = tcur.getPath();

			if (pointList.size() > 0) {
				stroke(0, 0, 255);
				TuioPoint start_point = (TuioPoint) pointList.firstElement();
				;
				for (int j = 0; j < pointList.size(); j++) {
					TuioPoint end_point = (TuioPoint) pointList.elementAt(j);
					stroke(255, 0, 0, j * 20);
					line(start_point.getScreenX(width),
							start_point.getScreenY(height),
							end_point.getScreenX(width),
							end_point.getScreenY(height));
					start_point = end_point;
				}

				stroke(192, 192, 192);
				fill(192, 192, 192);
				ellipse(tcur.getScreenX(width), tcur.getScreenY(height),
						cur_size, cur_size);
				fill(0);
				text("" + tcur.getCursorID(), tcur.getScreenX(width) - 5,
						tcur.getScreenY(height) + 5);
			}
		}
	}

	// these callback methods are called whenever a TUIO event occurs

	// called when an object is added to the scene
	public void addTuioObject(TuioObject tobj) {
		// println("add object " + tobj.getSymbolID() + " (" +
		// tobj.getSessionID()
		// + ") " + tobj.getX() + " " + tobj.getY() + " "
		// + tobj.getAngle());
	}

	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) {
		// println("remove object " + tobj.getSymbolID() + " ("
		// + tobj.getSessionID() + ")");

	}

	// called when an object is moved
	public void updateTuioObject(TuioObject tobj) {
		println("update object " + tobj.getSymbolID() + " ("
				+ tobj.getSessionID() + ") " + tobj.getX() + " " + tobj.getY()
				+ " " + tobj.getAngle() + " " + tobj.getMotionSpeed() + " "
				+ tobj.getRotationSpeed() + " " + tobj.getMotionAccel() + " "
				+ tobj.getRotationAccel());
	}

	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) {
		// println("add cursor " + tcur.getCursorID() + " (" +
		// tcur.getSessionID()
		// + ") " + tcur.getX() + " " + tcur.getY());

		// AbstractElement abstractElement = elementManager.contains(new
		// DLVector(
		// tcur.getScreenX(width), tcur.getScreenY(height)));
		//
		// if (abstractElement != null) {
		// selected = abstractElement;
		// }

		// dev.clickProfile().handle()
		agent.addTuioCursor(tcur);
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		// println("update cursor " + tcur.getCursorID() + " ("
		// + tcur.getSessionID() + ") " + tcur.getX() + " " + tcur.getY()
		// + " " + tcur.getMotionSpeed() + " " + tcur.getMotionAccel());

		// float x = tcur.getPath().get(1).getScreenX(width) -
		// tcur.getPath().get(0).getScreenX(width);
		// float y = tcur.getPath().get(1).getScreenY(height)-
		// tcur.getPath().get(0).getScreenY(height);
		agent.updateTuioCursor(tcur);

	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		// println("remove cursor " + tcur.getCursorID() + " ("
		// + tcur.getSessionID() + ")");
		// if (selected != null) {
		// selected = null;
		// }
		agent.removeTuioCursor(tcur);
	}

	// called after each message bundle
	// representing the end of an image frame
	public void refresh(TuioTime bundleTime) {
		redraw();
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "TUIO6dof.Tuio6DOF" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
