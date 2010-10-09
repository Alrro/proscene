import remixlab.proscene.*;
import remixlab.proscene.Scene.Button;

public class Button2D extends MouseGrabber {
  PFont myFont;
  int myWidth;
  int myHeight;
  String myText;
  PVector position;
  boolean addBox;
  int myColor;

  Button2D(Scene scn, String t, PVector p, boolean addB) {
    super(scn);
    myText = t;
    position = p;
    addBox = addB;
    myFont = createFont("FFScala", 28);
    textFont(myFont);
    textMode(SCREEN);
    textAlign(CENTER);
    myWidth = (int) textWidth(myText);
    myHeight = (int) (textAscent() + textDescent());
  }

  void display() {    
    if(grabsMouse())
      myColor = color(255);
    else
      myColor = color(100);
    pushStyle();
    fill(myColor);
    text(myText, position.x, position.y, myWidth, myHeight);
    popStyle();
  }

  void checkIfGrabsMouse(int x, int y, Camera camera) {
    // Rectangular activation area
    setGrabsMouse( (position.x <= x ) && ( x <= position.x + myWidth ) && (position.y <= y ) && ( y <= position.y + myHeight ) );
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