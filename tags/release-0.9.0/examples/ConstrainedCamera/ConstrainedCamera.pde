/**
 * Constrained Camera by Jean Pierre Charalambos.
 * 
 * This example illustrates how to add constrains (see Constraint related classes)
 * to your frames to limit their motion. All possible constraints are tested here.
 * They can be defined respect to the world or camera frames. Try all the possibilities
 * following the on screen helping text.
 * 
 * Press 'h' to toggle the mouse and keyboard navigation help.
 */

import remixlab.proscene.*;

Scene scene;
PFont myFont;
private int transDir;
private int rotDir;
AxisPlaneConstraint constraints[] = new AxisPlaneConstraint[2];
int activeConstraint;

void setup() {
  size(640, 360, P3D);
  myFont = createFont("Arial", 12);
  textFont(myFont);
  textMode(SCREEN);

  scene = new Scene(this);

  constraints[0] = new WorldConstraint();
  // Note that a CameraConstraint(camera) would produce the same results:
  // A CameraConstraint is a LocalConstraint when applied to the camera frame!
  constraints[1] = new LocalConstraint();
  transDir = 0;
  rotDir   = 0;
  activeConstraint = 0;
  scene.camera().frame().setConstraint(constraints[activeConstraint]);

  scene.setGridIsDrawn(false);
}

static AxisPlaneConstraint.Type nextTranslationConstraintType(AxisPlaneConstraint.Type type) {
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

static AxisPlaneConstraint.Type nextRotationConstraintType(AxisPlaneConstraint.Type type) {
  AxisPlaneConstraint.Type rType;
  switch (type) {
    case FREE  : rType = AxisPlaneConstraint.Type.AXIS; break;
    case PLANE : rType = AxisPlaneConstraint.Type.FREE;  break;
    case AXIS  : rType = AxisPlaneConstraint.Type.FORBIDDEN;  break;
    case FORBIDDEN   : rType = AxisPlaneConstraint.Type.FREE; break;
    default : rType = AxisPlaneConstraint.Type.FREE;
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

void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  fill(204, 102, 0);
  box(20, 30, 50);
  fill(0, 0, 255);
  displayText();
}

void displayType(AxisPlaneConstraint.Type type, int x, int y, char c) {
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
  displayDir(transDir, (350+90), height-30, 'D');
  displayType(constraints[activeConstraint].translationConstraintType(), 350, height-60, 'T');

  text("ROTATION :", width-120,height-30);
  displayDir(rotDir, width-50, height-30, 'B');
  displayType(constraints[activeConstraint].rotationConstraintType(), width-120, height-60, 'R');

  switch (activeConstraint) {
    case 0 : text("Constraint direction defined w/r to WORLD (U)", 370,20); break;
    case 1 : text("Constraint direction defined w/r to CAMERA (U)", 370,20); break;
  }
}

void keyPressed() {
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