package deviceGrabbers.cameraInterpolation;

import deviceGrabbers.buttons.Button2D;
import deviceGrabbers.mouseGrabbers.MouseGrabbers;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class ClickButton extends Button2D {
	  int path;

	  public ClickButton(Scene scn, Vector3D p, int fontSize, int index) {
	    this(scn, p, "", fontSize, index);
	  }

	  public ClickButton(Scene scn, Vector3D p, String t, int fontSize, int index) {
	    super(scn, p, t, fontSize);
	    path = index;
	  }

	  @Override
	  public void buttonClicked(Integer button, int numberOfClicks) {
	    if(numberOfClicks == 1)
	      if(path==0)
	        scene.toggleCameraPathsAreDrawn();
	      else
	        scene.camera().playPath(path);
	    	//camera.playPath(path);
	  }

	  public void display() {
	    String text = new String();
	    if(path == 0)
	      if(scene.cameraPathsAreDrawn())
	        text = "don't edit camera paths";
	      else
	        text = "edit camera paths";
	    else {
	      if(grabsDevice()) {
	        if (scene.camera().keyFrameInterpolator(path).numberOfKeyFrames() > 1) 
	          if (scene.camera().keyFrameInterpolator(path).interpolationIsStarted())
	            text = "stop path ";
	          else
	            text = "play path ";        
	        else
	          text = "restore position ";
	      }
	      else {
	        if (scene.camera().keyFrameInterpolator(path).numberOfKeyFrames() > 1)
	          text = "path ";
	        else
	          text = "position ";
	      }
	      text += ((Integer)path).toString();
	    }
	    setText(text);
	    super.display();
	  }
	}