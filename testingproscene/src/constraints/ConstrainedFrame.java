package constraints;
import processing.core.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;
import remixlab.remixcam.constraints.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class ConstrainedFrame extends PApplet {
	Scene scene;
	PFont myFont;
	private int transDir;
	private int rotDir;
	InteractiveFrame frame;
	AxisPlaneConstraint constraints[] = new AxisPlaneConstraint[3];
	int activeConstraint;
	
	public void setup()	{
		size(640, 360, P3D);
		//size(640, 360, OPENGL);
		myFont = createFont("Arial", 12);
		textFont(myFont);
		textMode(SCREEN);
		
		scene = new Scene(this);
		// press 'i' to switch the interaction between the camera frame and the interactive frame
		scene.setShortcut('i', Scene.KeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setCameraType(Camera.Type.ORTHOGRAPHIC);
		scene.setAxisIsDrawn(true);
		
		constraints[0] = new LocalConstraint();
		// Note that a CameraConstraint(camera) would produce the same results:
		// A CameraConstraint is a LocalConstraint when applied to the camera frame !
		constraints[1] = new WorldConstraint();
		constraints[2] = new CameraConstraint(scene.camera());
		transDir = 0;
		rotDir   = 0;
		activeConstraint = 0;
		
		frame = new InteractiveFrame(scene);
		frame.translate(new Vector3D(0.2f, 0.2f, 0));
		scene.setInteractiveFrame(frame);			
		frame.setConstraint(constraints[activeConstraint]);
		scene.setDrawInteractiveFrame(true);	
	}
	
	public static AxisPlaneConstraint.Type nextTranslationConstraintType(AxisPlaneConstraint.Type type) {
		AxisPlaneConstraint.Type rType;
		switch (type) {
		case FREE  : rType = AxisPlaneConstraint.Type.PLANE; break;
	    case PLANE : rType = AxisPlaneConstraint.Type.AXIS;  break;
	    case AXIS  : rType = AxisPlaneConstraint.Type.FORBIDDEN;  break;
	    case FORBIDDEN   : rType = AxisPlaneConstraint.Type.FREE; break;
	    default : rType = AxisPlaneConstraint.Type.FREE;
	    }
		return rType;
	}
	
	public static AxisPlaneConstraint.Type nextRotationConstraintType(AxisPlaneConstraint.Type type) {
		AxisPlaneConstraint.Type rType;
		switch (type) {
		case FREE  : rType = AxisPlaneConstraint.Type.AXIS; break;	    
	    case AXIS  : rType = AxisPlaneConstraint.Type.FORBIDDEN;  break;
	    case PLANE : 
	    case FORBIDDEN   : rType = AxisPlaneConstraint.Type.FREE; break;
	    default : rType = AxisPlaneConstraint.Type.FREE;
	    }
		return rType;
	}
	
	private void changeConstraint() {
	  int previous = activeConstraint;
	  activeConstraint = (activeConstraint+1)%3;

	  constraints[activeConstraint].setTranslationConstraintType(constraints[previous].translationConstraintType());
	  constraints[activeConstraint].setTranslationConstraintDirection(constraints[previous].translationConstraintDirection());
	  constraints[activeConstraint].setRotationConstraintType(constraints[previous].rotationConstraintType());
	  constraints[activeConstraint].setRotationConstraintDirection(constraints[previous].rotationConstraintDirection());

	  frame.setConstraint(constraints[activeConstraint]);
	}

	public void draw() {		
		background(0);
		pushMatrix();
		// /**
		float [] m = new float[16];
		PMatrix3D pM = new PMatrix3D();
		pM.set(frame.matrix().getTransposed(m) );
		applyMatrix( pM );
		// */
		//Same as the previous commented lines, but a lot more efficient:
		//frame.applyTransformation();		
		scene.drawAxis(40);		
		fill(204, 102, 0);
		box(30, 30, 30);				
		popMatrix();
		
		fill(0, 0, 255);
		displayText();
	}	

	protected void displayType(AxisPlaneConstraint.Type type, int x, int y, char c)	{
		String textToDisplay = new String();
		switch (type) {
	    case FREE:  textToDisplay = "FREE (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    case PLANE: textToDisplay = "PLANE (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    case AXIS:  textToDisplay = "AXIS (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    case FORBIDDEN: textToDisplay = "FORBIDDEN (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    }
		text(textToDisplay, x, y);
	}
	
	protected void displayDir(int dir, int x, int y, char c) {
		String textToDisplay = new String();
		switch (dir) {
	    case 0: textToDisplay = "X (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    case 1: textToDisplay = "Y (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    case 2: textToDisplay = "Z (";
	    textToDisplay += c;
	    textToDisplay += ")";
	    break;
	    }
		text(textToDisplay, x, y);
	}
	
	public void displayText() {		
		text("TRANSLATION :", 350, height-30);
		displayDir(transDir, (350+90), height-30, 'D');
		displayType(constraints[activeConstraint].translationConstraintType(), 350, height-60, 'T');
		
		text("ROTATION :", width-120,height-30);		
		displayDir(rotDir, width-50, height-30, 'B');		
		displayType(constraints[activeConstraint].rotationConstraintType(), width-120, height-60, 'R');
		
		switch (activeConstraint) {
		case 0 : text("Constraint direction defined w/r to LOCAL (U)", 350,20); break;
		case 1 : text("Constraint direction defined w/r to WORLD (U)", 350,20); break;
	    case 2 : text("Constraint direction defined w/r to CAMERA (U)", 350,20); break;
	    }
	}
	
	public void keyPressed() {
		//scene.defaultKeyBindings();
	
		if (key == 'b' || key == 'B') {
			rotDir   = (rotDir+1)%3;
		}
		if (key == 'd' || key == 'D') {
			transDir = (transDir+1)%3;
		}
		if (key == 'u' || key == 'U') {
			changeConstraint();
		}
		if (key == 't' || key == 'T') {
			constraints[activeConstraint].setTranslationConstraintType(nextTranslationConstraintType(constraints[activeConstraint].translationConstraintType()));
		}
		if (key == 'r' || key == 'R') {
			constraints[activeConstraint].setRotationConstraintType(nextRotationConstraintType(constraints[activeConstraint].rotationConstraintType()));
		}
		
		Vector3D dir = new Vector3D(0.0f, 0.0f, 0.0f);
		switch (transDir) {
		case 0 : dir.x(1.0f); break;
		case 1 : dir.y(1.0f); break;
		case 2 : dir.z(1.0f); break;
		}
		
		constraints[activeConstraint].setTranslationConstraintDirection(dir);

		dir.set(0.0f, 0.0f, 0.0f);
		switch (rotDir) {
		case 0 : dir.x(1.0f); break;
		case 1 : dir.y(1.0f); break;
		case 2 : dir.z(1.0f); break;
		}
		
		constraints[activeConstraint].setRotationConstraintDirection(dir);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ConstrainedFrame" });
	}
}
