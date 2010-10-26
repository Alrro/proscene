public class Patch extends InteractiveFrame {
  private int number;
  private float padding = 0f;
  private Board board;
  private PImage img;
    
  public Patch(int number, PImage img, Scene scene, Board board) {
    super(scene);
    this.number = number;
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
      fill(100, 100, 255);
      stroke(200, 200, 100);
      strokeWeight(3);
    } else {
      fill(250, 250, 255);
      stroke(150, 150, 255);
      strokeWeight(2);
    }
    
    float thickness = 6;
    beginShape();
    vertex(-getSize() / 2, -getSize() / 2, -thickness);
    vertex(-getSize() / 2, getSize() / 2, -thickness);
    vertex(getSize() / 2, getSize() / 2, -thickness);
    vertex(getSize() / 2, -getSize() / 2, -thickness);
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
      textFont(createFont("FFScala", 30));
      strokeWeight(1);
      fill(0, 0, 20);
      text("" + number, -10, 10, 0.1);
    }

    popStyle();
    popMatrix();
  }
  
  public float getSize() {
    return 50f;
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
