package sensitivities;

import processing.core.*;
import remixlab.proscene.*;
import sensitivities.MouseSensitivities.Sensitivity;

public class ClickButton extends Button2D {
	boolean increase;
	Sensitivity sensitivity;

	public ClickButton(Scene scn, PVector p, String t, Sensitivity sens, boolean inc) {
		super(scn, p, t);
		increase = inc;
		sensitivity = sens;
	}

	public void mouseClicked(Integer button, int numberOfClicks, Camera camera) {
		if (numberOfClicks == 1) {
			if (increase)
				((MouseSensitivities)scene.parent).increaseSensitivity(sensitivity);
			else
				((MouseSensitivities)scene.parent).decreaseSensitivity(sensitivity);
		}
	}
}
