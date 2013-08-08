package basic;

import processing.core.*;
import processing.event.KeyEvent;
import processing.opengl.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.KeyboardAgent;
import remixlab.dandelion.agent.MouseAgent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.proscene.*;
import remixlab.tersehandling.generic.event.GenericDOF2Event;
import remixlab.tersehandling.generic.event.GenericKeyboardEvent;

public class MouseMoveCameraRotate extends PApplet {
	Scene scene;
	MouseMoveAgent agent;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();
		agent = new MouseMoveAgent(scene, "MyMouseAgent");
		scene.terseHandler().unregisterAgent(agent);
	}
	
	public void draw() {	
		background(0);	
		noStroke();
		if( scene.camera().sphereIsVisible(new Vec(0,0,0), 40) == Camera.Visibility.SEMIVISIBLE )
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);		
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.MouseMoveCameraRotate" });
	}
	
	public void keyPressed() {
		if( key != ' ') return;
		if( !scene.terseHandler().isAgentRegistered(agent) ) {
			scene.terseHandler().registerAgent(agent);
			scene.parent.registerMethod("mouseEvent", agent);
			scene.disableDefaultMouseAgent();
		}
		else {
			scene.terseHandler().unregisterAgent(agent);
			scene.parent.unregisterMethod("mouseEvent", agent);
			scene.enableDefaultMouseAgent();
		}
	}
	
	public class MouseMoveAgent extends MouseAgent {
		GenericDOF2Event<Constants.DOF2Action> event, prevEvent;
		public MouseMoveAgent(AbstractScene scn, String n) {
			super(scn, n);
			cameraProfile().setBinding(DOF2Action.ROTATE);
		}
		
		public void mouseEvent(processing.event.MouseEvent e) {
			if( e.getAction() == processing.event.MouseEvent.MOVE ) {
				event = new GenericDOF2Event<Constants.DOF2Action>(prevEvent, e.getX() - scene.upperLeftCorner.getX(), e.getY() - scene.upperLeftCorner.getY());
				handle(event);
				prevEvent = event.get();
			}
		}		
	}
}
