public class Patch extends InteractiveFrame {
  private int number;
  private float size;
  private float padding = 0f;
  private Board board;
  private PImage img;
    
  public Patch(int number, float size, PImage img, Scene scene, Board board) {
    super(scene);
    this.number = number;
    this.size = size;
    this.img = img;
    this.board = board;
    setGrabsMouseThreshold((int) getSize());
  }
  
  public void draw(float x, float y) {
    pushMatrix();
    pushStyle();
    setPosition(x, y, 0);
    setRotation(0, 0, 0, 0);
    applyTransformation();
    
    if(grabsMouse()) {
      fill(250, 250, 60);
      stroke(200, 200, 100);
      strokeWeight(3);
    } else {
      if(number % 2 == 0) {
        fill(250, 250, 255);
      } else {
        fill(100, 100, 255);
      }
      stroke(150, 150, 255);
      strokeWeight(2);
    }
    
    float thickness = 6;
    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    endShape(CLOSE);
    
    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, 0);
    vertex(-getSize() / 2, getSize() / 2, 0);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);
    
    beginShape();
    vertex(getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);
    
    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, -getSize() / 2, 0);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    endShape(CLOSE);
    
    beginShape();
    vertex(-getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, 0);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    endShape(CLOSE);
    
    textureMode(NORMALIZED);
    beginShape();
    if(img != null) {
      if(grabsMouse()) {
        fill(250, 250, 0);
      } else {
        fill(10);
      }
      texture(img);
      vertex(-getSize() / 2, -getSize() / 2, 0, 0, 0);
      vertex(-getSize() / 2, getSize() / 2, 0, 0, 1);
      vertex(getSize() / 2, getSize() / 2, 0, 1, 1);
      vertex(getSize() / 2, -getSize() / 2, 0, 1, 0);
    } else {
      vertex(-getSize() / 2, -getSize() / 2, 0);
      vertex(-getSize() / 2, getSize() / 2, 0);
      vertex(getSize() / 2, getSize() / 2, 0);
      vertex(getSize() / 2, -getSize() / 2, 0);
    }
    endShape(CLOSE);
    
    if(img == null) {
      textFont(createFont("FFScala", size / 1.5));
      strokeWeight(1);
      fill(0, 0, 20);
      text("" + number, -10, 10, 0.1);
    }

    popStyle();
    popMatrix();
  }
  
  public void checkIfGrabsMouse(int x, int y, Camera camera) {
    setGrabsMouse(pointInsideQuad(camera));
  }
  
  private boolean pointInsideQuad(Camera camera) {
    PVector v1 = camera.projectedCoordinatesOf(new PVector((-getSize() / 2) + position().x, (-getSize() / 2) + position().y));
    PVector v2 = camera.projectedCoordinatesOf(new PVector((-getSize() / 2) + position().x, (getSize() / 2) + position().y));
    PVector v3 = camera.projectedCoordinatesOf(new PVector((getSize() / 2) + position().x, (getSize() / 2) + position().y));
    PVector v4 = camera.projectedCoordinatesOf(new PVector((getSize() / 2) + position().x, (-getSize() / 2) + position().y));
    
    return
      computePointPosition(mouseX, mouseY, v1.x, v1.y, v2.x, v2.y) < 0 &&
      computePointPosition(mouseX, mouseY, v2.x, v2.y, v3.x, v3.y) < 0 &&
      computePointPosition(mouseX, mouseY, v3.x, v3.y, v4.x, v4.y) < 0 &&
      computePointPosition(mouseX, mouseY, v4.x, v4.y, v1.x, v1.y) < 0;
  }
  
  private float computePointPosition(float x, float y, float x0, float y0, float x1, float y1) {
    return (y - y0) * (x1 - x0) - (x - x0) * (y1 - y0);
  }
  
  public float getSize() {
    return size;
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
  
  public void mousePressed(Point eventPoint, Camera camera)  {
    super.mousePressed(eventPoint, camera);
    board.movePatch(this);
  }  
}