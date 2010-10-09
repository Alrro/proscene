import remixlab.proscene.*;
import remixlab.proscene.Scene.Button;

public class Button2D extends MouseGrabber {
  int width;
  int height;
  String myText;
  PVector position;
  boolean addBox;
  int myColor;
  
  Button2D(Scene scn, String t, PVector p, int w, int h, boolean addB) {
    super(scn);
    myText = t;
    position = p;
    width = w;
    height = h;
    addBox = addB;
    myColor = 125;
  }
  
  void setColor(int aColor) {
    myColor = aColor;
  }
  
  void display() {
    scene.beginScreenDrawing();
    pushStyle();
    strokeWeight(2);
    stroke(0,255,255);
    fill(myColor);
    beginShape();
    vertex(scene.xCoord( position.x ), scene.yCoord( position.y ), scene.zCoord());
    vertex(scene.xCoord( (position.x + this.width) ), scene.yCoord( position.y ), scene.zCoord());
    vertex(scene.xCoord( (position.x + this.width) ), scene.yCoord( (position.y + this.height) ), scene.zCoord());
    vertex(scene.xCoord( position.x ), scene.yCoord( (position.y + this.height) ), scene.zCoord());
    endShape(PApplet.CLOSE);
    popStyle();
    scene.endScreenDrawing();
    text(myText, position.x, (position.y+4), this.width, this.height);
  }

  void checkIfGrabsMouse(int x, int y, Camera camera) {
    // Rectangular activation area
    setGrabsMouse( (position.x <= x ) && ( x <= position.x + this.width ) && (position.y <= y ) && ( y <= position.y + this.height ) );
  }

  void mouseClicked(Button button, int numberOfClicks, Camera camera) {
    if(numberOfClicks == 1) {
      if(addBox)
        ((MouseGrabbers)scene.parent).addBox();
      else
        ((MouseGrabbers)scene.parent).removeBox();
    }
  }
}