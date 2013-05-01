package deviceGrabbers.mouseGrabbers;

import deviceGrabbers.buttons.*;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class ClickButton extends Button2D {
	boolean addBox;

	public ClickButton(Scene scn, Vector3D p, String t, int fontSize, boolean addB) {
		super(scn, p, t, fontSize);
		addBox = addB;
	}

	@Override
	public void buttonClicked(Integer button, int numberOfClicks) {
		if (numberOfClicks == 1) {
			if (addBox)
				((MouseGrabbers)((Scene)scene).parent).addBox();
			else
				((MouseGrabbers)((Scene)scene).parent).removeBox();
		}
	}
}
