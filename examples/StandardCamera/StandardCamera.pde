import remixlab.proscene.*;
import napplet.*;

NAppletManager nappletManager;
NApplet mainNApplet;

void setup() {
  size(640, 720, P3D);
  // instantiate the viewers and embed them into a napplet manager
  nappletManager = new NAppletManager(this);
  mainNApplet = nappletManager.createNApplet("MainViewer", 0, 0);  
  nappletManager.createNApplet("AuxiliarViewer", 0, 360);
}

void draw() {  
  background(50);
}