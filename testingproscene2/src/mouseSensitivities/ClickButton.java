package mouseSensitivities;

import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import mouseSensitivities.MouseSensitivities.Sensitivity;

public class ClickButton extends Button2D {
	boolean increase;
	Sensitivity sensitivity;

	public ClickButton(Scene scn, Vector3D p, String t, Sensitivity sens, boolean inc) {
		super(scn, p, t);
		increase = inc;
		sensitivity = sens;
	}

	@Override
	public void mouseClicked(Integer button, int numberOfClicks, Pinhole camera) {
		if (numberOfClicks == 1) {
			if (increase)
				((MouseSensitivities)scene.parent).increaseSensitivity(sensitivity);
			else
				((MouseSensitivities)scene.parent).decreaseSensitivity(sensitivity);
		}
	}
}
