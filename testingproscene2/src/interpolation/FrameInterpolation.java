package interpolation;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;

public class FrameInterpolation extends PApplet {
	Scene scene;
	InteractiveFrame keyFrame[];
	KeyFrameInterpolator kfi;
	int nbKeyFrames;

	public void setup() {
	  size(640, 360, P3D);
	  nbKeyFrames = 4;
	  scene = new Scene(this);  
	  scene.setAxisIsDrawn(false);
	  scene.setGridIsDrawn(false);
	  scene.setRadius(70);
	  scene.showAll();
	  scene.setFrameSelectionHintIsDrawn(true);
	  //scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
	  kfi = new KeyFrameInterpolator(scene);
	  kfi.setLoopInterpolation();
	  
	  // An array of interactive (key) frames.
	  keyFrame = new InteractiveFrame[nbKeyFrames];
	  // Create an initial path
	  for (int i=0; i<nbKeyFrames; i++) {
	    keyFrame[i] = new InteractiveFrame(scene);
	    keyFrame[i].setPosition(-100 + 200*i/(nbKeyFrames-1), 0, 0);
	    kfi.addKeyFrame(keyFrame[i]);
	  }
	  
	  kfi.startInterpolation();
	}

	public void draw() {
	  background(0);
	  pushMatrix();
	  kfi.frame().applyTransformation(scene);
	  scene.drawAxis(30);
	  popMatrix();
	  
	  kfi.drawPath(5, 10);
	  
	  for (int i=0; i<nbKeyFrames; ++i) {
	    pushMatrix();
	    kfi.keyFrame(i).applyTransformation(scene);
	    
	    if ( keyFrame[i].grabsAgent(scene.defaultMouseAgent()) )
	      scene.drawAxis(40);
	    else
	      scene.drawAxis(20);
	      
	    popMatrix();
	  }
	}

	public void keyPressed() {
		if ((key == ENTER) || (key == RETURN))
	  kfi.toggleInterpolation();
	  if ( key == 'u')
	    kfi.setInterpolationSpeed(kfi.interpolationSpeed()-0.25f);
	  if ( key == 'v')
	    kfi.setInterpolationSpeed(kfi.interpolationSpeed()+0.25f);  	
	}

}
