/**
 * Constrained Camera. 
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates how to add constrains (see PSConstraint
 * related classes) to your frames to limit their motion. All possible
 * constraints are tested here. They can be defined respect to the
 * world or camera frames. Try all the possibilities following the on
 * screen helping text.   
 */

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import proscene.*;

PScene scene;
PFont myFont;
private int transDir;
private int rotDir;
PSAxisPlaneConstraint constraints[] = new PSAxisPlaneConstraint[2];
int activeConstraint;
	
void setup()	{
	size(640, 360, P3D);				
	myFont = createFont("Arial", 12);
	textFont(myFont);
	textMode(SCREEN);
		
	scene = new PScene(this);		
		
	constraints[0] = new PSWorldConstraint();
	// Note that a PSCameraConstraint(camera) would produce the same results:
	// A PSCameraConstraint is a PSLocalConstraint when applied to the camera frame !
	constraints[1] = new PSLocalConstraint();		
	transDir = 0;
	rotDir   = 0;
	activeConstraint = 0;
	scene.camera().frame().setConstraint(constraints[activeConstraint]);
		
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
	    case PLANE : rType = PSAxisPlaneConstraint.Type.FREE;  break;
	    case AXIS  : rType =  PSAxisPlaneConstraint.Type.FORBIDDEN;  break;
	    case FORBIDDEN   : rType = PSAxisPlaneConstraint.Type.FREE; break;
	    default : rType = PSAxisPlaneConstraint.Type.FREE;
	}
	return rType;
}
	
void changeConstraint() {
	int previous = activeConstraint;
	activeConstraint = (activeConstraint+1)%2;

	constraints[activeConstraint].setTranslationConstraintType(constraints[previous].translationConstraintType());
	constraints[activeConstraint].setTranslationConstraintDirection(constraints[previous].translationConstraintDirection());
	constraints[activeConstraint].setRotationConstraintType(constraints[previous].rotationConstraintType());
	constraints[activeConstraint].setRotationConstraintDirection(constraints[previous].rotationConstraintDirection());

	scene.camera().frame().setConstraint(constraints[activeConstraint]);
}

// Your actual scene drawing should be enclosed between the
// PScene.beginDraw() and PScene.endDraw() pair.
void draw() {
	// Should always be defined before PScene.beginDraw()
	background(0);
	scene.beginDraw();
	fill(204, 102, 0);
	box(0.2f, 0.3f, 0.5f);
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
	text("TRANSLATION :", 350, height-30);
	displayDir(transDir, (350+90), height-30, 'F');
	displayType(constraints[activeConstraint].translationConstraintType(), 350, height-60, 'T');
		
	text("ROTATION :", width-120,height-30);		
	displayDir(rotDir, width-50, height-30, 'D');		
	displayType(constraints[activeConstraint].rotationConstraintType(), width-120, height-60, 'R');
		
	switch (activeConstraint) {	
	    case 0 : text("Constraint direction defined w/r to WORLD (L)", 370,20); break;
	    case 1 : text("Constraint direction defined w/r to CAMERA (L)", 370,20); break;
    }
}

// To take full advantage of proscene 3d navigation power this
// method should always call PScene.defaultKeyBindings()
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
	