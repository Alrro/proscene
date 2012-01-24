/**
 * Button 2D.
 * by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement
 * a MouseGrabber which can enable complex mouse interactions.
 */

public class Button2D extends DeviceGrabber {
  String myText;
  PFont myFont;
  int myWidth;
  int myHeight;  
  Vector3D position;
  
  Button2D(Scene scn, Vector3D p, int fontSize) {
    this(scn, p, "", fontSize);
  }

  Button2D(Scene scn, Vector3D p, String t, int fontSize) {
    super(scn);
    position = p;
    myText = t;    
    myFont = createFont("FFScala", fontSize);
    textFont(myFont);
    textMode(SCREEN);
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
    text(myText, position.x(), position.y(), myWidth, myHeight);
    popStyle();
  }

  void checkIfGrabsMouse(int x, int y, Camera camera) {
    // Rectangular activation area
    setGrabsMouse( (position.x() <= x ) && ( x <= position.x() + myWidth ) && (position.y() <= y ) && ( y <= position.y() + myHeight ) );
  }
}