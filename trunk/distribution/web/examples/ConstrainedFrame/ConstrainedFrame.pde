import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import proscene.*;

PScene scene;
PFont myFont;
private int transDir;
private int rotDir;
PSInteractiveFrame frame;
PSAxisPlaneConstraint constraints[] = new PSAxisPlaneConstraint[3];
int activeConstraint;
	
void setup()	{
		size(640, 360, P3D);				
		myFont = createFont("Arial", 12);
		textFont(myFont);
		textMode(SCREEN);
		
		scene = new PScene(this);		
		
		constraints[0] = new PSLocalConstraint();
		// Note that a CameraConstraint(camera) would produce the same results:
		// A CameraConstraint is a LocalConstraint when applied to the camera frame !
		constraints[1] = new PSWorldConstraint();
		constraints[2] = new PSCameraConstraint(scene.camera());
		transDir = 0;
		rotDir   = 0;
		activeConstraint = 0;
		
		frame = new PSInteractiveFrame();
		scene.setInteractiveFrame(frame);
		frame.setConstraint(constraints[activeConstraint]);
		
		scene.setAxisIsDrawn(true);
}
	
static PSAxisPlaneConstraint.Type nextTranslationConstraintType(PSAxisPlaneConstraint.Type type) {
		PSAxisPlaneConstraint.Type rType;
		switch (type) {
		case FREE  : rType = PSAxisPlaneConstraint.Type.PLANE; break;
	    case PLANE : rType = PSAxisPlaneConstraint.Type.AXIS;  break;
	    case AXIS  : rType =  PSAxisPlaneConstraint.Type.FORBIDDEN;  break;
	    case FORBIDDEN   : rType = PSAxisPlaneConstraint.Type.FREE; break;
	    default : rType = PSAxisPlaneConstraint.Type.FREE;
	    }
		return rType;
}
	
static PSAxisPlaneConstraint.Type nextRotationConstraintType(PSAxisPlaneConstraint.Type type) {
		PSAxisPlaneConstraint.Type rType;
		switch (type) {
		case FREE  : rType = PSAxisPlaneConstraint.Type.AXIS; break;	    
	    case AXIS  : rType =  PSAxisPlaneConstraint.Type.FORBIDDEN;  break;
	    case PLANE : 
	    case FORBIDDEN   : rType = PSAxisPlaneConstraint.Type.FREE; break;
	    default : rType = PSAxisPlaneConstraint.Type.FREE;
	    }
		return rType;
}
	
void changeConstraint() {
	  int previous = activeConstraint;
	  activeConstraint = (activeConstraint+1)%3;

	  constraints[activeConstraint].setTranslationConstraintType(constraints[previous].translationConstraintType());
	  constraints[activeConstraint].setTranslationConstraintDirection(constraints[previous].translationConstraintDirection());
	  constraints[activeConstraint].setRotationConstraintType(constraints[previous].rotationConstraintType());
	  constraints[activeConstraint].setRotationConstraintDirection(constraints[previous].rotationConstraintDirection());

	  frame.setConstraint(constraints[activeConstraint]);
}

void draw() {
		background(0);
		scene.beginDraw();
		//applyMatrix( frame.pMatrix() );
		//Same as the previous commented line, but a lot more efficient:
		frame.applyTransformation(this);
		PScene.drawAxis(0.4f);		
		fill(204, 102, 0);
		box(0.2f, 0.3f, 0.2f);
		fill(0, 0, 255);
		displayText();
		scene.endDraw();
}	
	
void displayType(PSAxisPlaneConstraint.Type type, int x, int y, char c)	{
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
	
void displayDir(int dir, int x, int y, char c) {
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
	
void displayText() {
		text("TRANSLATION :", 10, height-30);
		displayDir(transDir, 190, height-30, 'F');
		displayType(constraints[activeConstraint].translationConstraintType(), 10, height-60, 'T');
		
		text("ROTATION :", width-220,height-30);
		displayDir(rotDir, width-100, height-30, 'D');
		displayType(constraints[activeConstraint].rotationConstraintType(), width-220, height-60, 'R');
		
		switch (activeConstraint) {
		case 0 : text("Constraint direction defined w/r to LOCAL (L)", 320,20); break;
		case 1 : text("Constraint direction defined w/r to WORLD (L)", 320,20); break;
	    case 2 : text("Constraint direction defined w/r to CAMERA (L)", 320,20); break;
	    }
}
	
void keyPressed() {
		scene.defaultKeyBindings();
		
		if (key == 'd' || key == 'D') {
			rotDir   = (rotDir+1)%3;
		}
		if (key == 'f' || key == 'F') {
			transDir = (transDir+1)%3;
		}
		if (key == 'l' || key == 'L') {
			changeConstraint();
		}
		if (key == 't' || key == 'T') {
			constraints[activeConstraint].setTranslationConstraintType(nextTranslationConstraintType(constraints[activeConstraint].translationConstraintType()));
		}
		if (key == 'r' || key == 'R') {
			constraints[activeConstraint].setRotationConstraintType(nextRotationConstraintType(constraints[activeConstraint].rotationConstraintType()));
		}
		
		PVector dir = new PVector(0.0f, 0.0f, 0.0f);
		switch (transDir) {
		case 0 : dir.x = 1.0f; break;
		case 1 : dir.y = 1.0f; break;
		case 2 : dir.z = 1.0f; break;
		}
		
		constraints[activeConstraint].setTranslationConstraintDirection(dir);

		dir.set(0.0f, 0.0f, 0.0f);
		switch (rotDir) {
		case 0 : dir.x = 1.0f; break;
		case 1 : dir.y = 1.0f; break;
		case 2 : dir.z = 1.0f; break;
		}
		constraints[activeConstraint].setRotationConstraintDirection(dir);
}
	