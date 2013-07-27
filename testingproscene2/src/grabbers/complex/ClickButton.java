package grabbers.complex;

import processing.core.PVector;
import remixlab.proscene.Scene;
import remixlab.tersehandling.event.*;

public class ClickButton extends Button2D {
	boolean addBox;

	public ClickButton(Scene scn, PVector p, String t, boolean addB) {
		super(scn, p, t);
		addBox = addB;
	}

	@Override
	public void performInteraction(TerseEvent event) {
		if (event instanceof ClickEvent)
			if (((ClickEvent) event).getClickCount() == 1) {
				if (addBox)
					parent.addBox();
				else
					parent.removeBox();
			}
	}
}
