/**
 * Button 2D.
 * by Jean Pierre Charalambos.
 * 
 * Base class of "2d buttons" that shows how simple is to implement
 * a MouseGrabber which can enable complex mouse interactions.
 */

public class Button2D extends MouseGrabber {
  String myText;
  int myWidth;
  int myHeight;
  PVector position;
  Scene scene;

  Button2D(Scene scn, PVector p) {
    this(scn, p, "");
    scene = scn;
  }

  Button2D(Scene scn, PVector p, String t) {
    super(scn);
    scene = scn;
    position = p;
    myText = t;
    setText(t);
  }

  public void setText(String text) {
    myText = text;
    myWidth = (int) textWidth(myText);
    myHeight = (int) textAscent() + (int) textDescent();
  }

  public void display() {
    pushStyle();	
    colorMode(PApplet.HSB);
    if (grabsMouse())
      fill(50, 255, 255);
    else
      fill(50, 255, 100);
    text(myText, position.x, position.y, myWidth, myHeight);
    popStyle();
  }

  public void checkIfGrabsMouse(int x, int y, Camera camera) {
    // Rectangular activation area
    setGrabsMouse((position.x <= x) && (x <= position.x + myWidth) && (position.y <= y) && (y <= position.y + myHeight));
  }
}