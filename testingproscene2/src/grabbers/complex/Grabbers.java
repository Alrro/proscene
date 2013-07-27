package grabbers.complex;

import java.util.ArrayList;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;

public class Grabbers extends PApplet {
	CustomisedAgent agent;
	Scene scene;
	ArrayList boxes;
	Button2D button1, button2;
	int myColor;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		//scene.terseHandler().unregisterAgent("proscene_mouse");		
				
		agent = new CustomisedAgent(scene, "MYAGENT");		
		//registerMethod("mouseEvent", agent);
		
		// scene.setShortcut('f',
		// Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		button1 = new ClickButton(scene, new PVector(10, 10), "+", true);
		button2 = new ClickButton(scene, new PVector(16, (2 + button1.myHeight)), "-", false);
		
		agent.addInPool(button1);
		agent.addInPool(button2);
		
		scene.prosceneMouse.addInPool(button1);
		scene.prosceneMouse.addInPool(button2);
		
		//scene.terseHandler().addInAllAgentPools(button1);
		//scene.terseHandler().addInAllAgentPools(button2);
		
		scene.setGridIsDrawn(true);
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(150);
		scene.showAll();

		myColor = 125;
		boxes = new ArrayList();
		addBox();
	}

	public void draw() {
		background(0);
		for (int i = 0; i < boxes.size(); i++) {
			Box box = (Box) boxes.get(i);
			box.draw(true);
		}
		button1.display();
		button2.display();
	}

	public void addBox() {
		Box box = new Box(scene);
		box.setSize(20, 20, 20);
		box.setColor(color(0, 0, 255));
		boxes.add(box);
	}

	public void removeBox() {
		if (boxes.size() > 0) {
			scene.terseHandler().removeFromAllAgentPools(((Box) boxes.get(0)).iFrame);
			boxes.remove(0);
		}
	}
	
	public void keyPressed() {
		if(  button1.grabsAgent(scene.prosceneMouse) )
			println("prosceneMouse grabs button1");
		else
			println("prosceneMouse doesn't grab button1");
		if(  button1.grabsAgent(agent) )
			println("agent grabs button1");
		else
			println("agent doesn't grab button1");
	}
}
