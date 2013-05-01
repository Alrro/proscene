package interpolation;

import processing.core.*;
import processing.opengl.*;

import remixlab.remixcam.constraint.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.profile.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.util.*;

import remixlab.proscene.*;

@SuppressWarnings("serial")
public class FrameInterpolation extends PApplet {
	Scene scene;
	InteractiveFrame keyFrame[];
	KeyFrameInterpolator kfi;
	int nbKeyFrames;
	boolean singleThreaded = true;

	public void setup() {
	  //size(640, 360, P3D);
	  size(640, 360, OPENGL);
	  nbKeyFrames = 4;
	  scene = new Scene(this);
	  scene.setAxisIsDrawn(false);
	  scene.setGridIsDrawn(false);
	  scene.setRadius(70);
	  scene.showAll();
	  scene.setFrameSelectionHintIsDrawn(true);
	  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);	 
	  
	  //scene.setAWTTimers();
	  
	  kfi = new KeyFrameInterpolator(scene);
	  kfi.setLoopInterpolation();
	  
	  // An array of interactive (key) frames.
	  keyFrame = new InteractiveFrame[nbKeyFrames];
	  // Create an initial path
	  for (int i=0; i<nbKeyFrames; i++) {
	    keyFrame[i] = new InteractiveFrame(scene);
	    keyFrame[i].setPosition(-100 + 200*i/(nbKeyFrames-1), 0, 0);
	    
	    if(i == nbKeyFrames-2)
	    	//keyFrame[i].setScaling(1, -1, 1);
	    	//keyFrame[i].setScaling(-1, 1, 1);
	    	keyFrame[i].setScaling(-1, -1, 1);
	    	//keyFrame[i].setScaling(-1, -1, -1);
	    
	    kfi.addKeyFrame(keyFrame[i]);
	  }
	  
	  kfi.startInterpolation();
	  frameRate(200);
	  //frameRate(2);
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
	    
	    // /**
	    if ( keyFrame[i].grabsDevice() )
	      scene.drawAxis(40);
	    else
	      scene.drawAxis(20);
	    // */
	      
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
	  if( key == 'x' )
		  scene.switchTimers();
	  /**
	  if (key == 'y' ) {
		  for (int i=0; i<nbKeyFrames; i++) {
			  if( keyFrame[i].isLeftHanded() )
				  println( "KeyFrame " + i + " is LeftHanded" );
			  else
				  println( "KeyFrame " + i + " is RightHanded" );
		  }
	  }	
	  */	  
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "interpolation.FrameInterpolation" });
	}
}
