package mouseSensitivities;

import deviceGrabbers.buttons.Button2D;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import mouseSensitivities.MouseSensitivities.Sensitivity;

public class ClickButton extends Button2D {
	boolean increase;
	Sensitivity sensitivity;

	public ClickButton(Scene scn, Vector3D p, String t, int fontSize, Sensitivity sens, boolean inc) {
		super(scn, p, t, fontSize);
		increase = inc;
		sensitivity = sens;
	}

	@Override
	public void buttonClicked(Integer button, int numberOfClicks) {
		if (numberOfClicks == 1) {
			if (increase)
				((MouseSensitivities)((Scene)scene).parent).increaseSensitivity(sensitivity);
			else
				((MouseSensitivities)((Scene)scene).parent).decreaseSensitivity(sensitivity);
		}
	}
}
