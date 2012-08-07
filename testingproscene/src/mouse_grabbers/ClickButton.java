package mouse_grabbers;

import processing.core.*;
import remixlab.proscene.*;

public class ClickButton extends Button2D {
	boolean addBox;

	public ClickButton(Scene scn, PVector p, String t, int fontSize, boolean addB) {
		super(scn, p, t, fontSize);
		addBox = addB;
	}

	public void mouseClicked(Scene.Button button, int numberOfClicks, Camera camera) {
		if (numberOfClicks == 1) {
			if (addBox)
				((MouseGrabbers)scene.parent).addBox();
			else
				((MouseGrabbers)scene.parent).removeBox();
		}
	}
}
