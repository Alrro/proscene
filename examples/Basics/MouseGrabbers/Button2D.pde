/**
 * Button 2D.
 * by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement
 * a MouseGrabber which can enable complex mouse interactions.
 */

public class Button2D extends MouseGrabber {
  String myText;
  PFont myFont;
  int myWidth;
  int myHeight;  
  PVector position;
  
  Button2D(Scene scn, PVector p) {
    this(scn, p, "");
  }

  Button2D(Scene scn, PVector p, String t) {
    super(scn);
    position = p;
    myText = t;    
    myFont = loadFont("FreeSans-36.vlw"); 
    textFont(myFont);
    textAlign(CENTER);
    setText(t);    
  }
  
  void setText(String text) {
    myText = text;
    myWidth = (int) textWidth(myText);
    myHeight = (int) (textAscent() + textDescent());
  }

  void display() {
    pushStyle();
    if(grabsMouse())
      fill(255);
    else
      fill(100);
    scene.beginScreenDrawing();
    text(myText, position.x, position.y, myWidth, myHeight);
    scene.endScreenDrawing();
    popStyle();
  }

  void checkIfGrabsMouse(int x, int y, Camera camera) {
    // Rectangular activation area
    setGrabsMouse( (position.x <= x ) && ( x <= position.x + myWidth ) && (position.y <= y ) && ( y <= position.y + myHeight ) );
  }
}
