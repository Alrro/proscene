package grabbers.camerainterpolation;

import grabbers.button.Button2D;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.tersehandling.event.ClickEvent;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class ClickButton extends Button2D {
	int path;

	public ClickButton(Scene scn, PVector p, int index) {
		this(scn, p, "", index);
	}

	public ClickButton(Scene scn, PVector p, String t, int index) {
		super(scn, p, t);
		path = index;
	}

	@Override
	public void performInteraction(TerseEvent event) {
		if (event instanceof ClickEvent)
			if (((ClickEvent) event).getClickCount() == 1)
				if (path == 0)
					scene.toggleCameraPathsAreDrawn();
				else
					scene.camera().playPath(path);
	}

	public void display() {
		String text = new String();
		if (path == 0)
			if (scene.cameraPathsAreDrawn())
				text = "don't edit camera paths";
			else
				text = "edit camera paths";
		else {
			if (grabsAgent(scene.defaultMouseAgent())) {
				if (scene.camera().keyFrameInterpolator(path)
						.numberOfKeyFrames() > 1)
					if (scene.camera().keyFrameInterpolator(path)
							.interpolationIsStarted())
						text = "stop path ";
					else
						text = "play path ";
				else
					text = "restore position ";
			} else {
				if (scene.camera().keyFrameInterpolator(path)
						.numberOfKeyFrames() > 1)
					text = "path ";
				else
					text = "position ";
			}
			text += ((Integer) path).toString();
		}
		setText(text);
		super.display();
	}
}