public class Board {
  private Patch[][] patches;
  private int moves;
  private PImage img;
  private Scene scene;
  
  public Board(Scene scene, PImage img) {
    this.scene = scene;
    this.img = img;
    order();
  }
  
  public void order() {
    patches = new Patch[3][3];
    int number = 1;
    
    for(int y = 0; y < 3; y++) {
      for(int x = 0; x < 3; x++) {
        if(number < 9) {
          PImage pimg = null;
          if(img != null) {
            pimg = createImage(img.width / 3, img.height / 3, RGB);
            pimg.blend(img, x * pimg.width, y * pimg.height, pimg.width, pimg.height, 0, 0, pimg.width, pimg.height, ADD);
          }
          patches[y][x] = new Patch(number, pimg, scene, this);
          number++;
        }
      }
    }
    
    moves = 0;
  }
  
  public PImage getImage() {
    return img;
  }
  
  public void setImage(PImage img) {
    this.img = img;
  }
  
  public void draw() {
    for(int y = 0; y < 3; y++) {
      for(int x = 0; x < 3; x++) {
        if(patches[y][x] != null) {
          patches[y][x].draw(patches[y][x].getSize() * (x - 1), patches[y][x].getSize() * (y - 1));
        }
      }
    }
  }
  
  public void movePatch(Patch patch) {
    int xp = -1, yp = -1;
    for(int y = 0; y < 3; y++) {
      for(int x = 0; x < 3; x++) {
        if(patches[y][x] == patch) {
          xp = x;
          yp = y;
          break;
        }
      }
    }
    
    if(xp >= 0 && xp < 3 && yp >= 0 && yp < 3) {
      if(xp < 2) {
        if(patches[yp][xp + 1] == null) {
          swap(xp, yp, xp + 1, yp);
        }
      }
      if(xp > 0) {
        if(patches[yp][xp - 1] == null) {
          swap(xp, yp, xp - 1, yp);
        }
      }
      if(yp < 2) {
        if(patches[yp + 1][xp] == null) {
          swap(xp, yp, xp, yp + 1);
        }
      }
      if(yp > 0) {
        if(patches[yp - 1][xp] == null) {
          swap(xp, yp, xp, yp - 1);
        }
      }
    }
  }
  
  private void swap(int x1, int y1, int x2, int y2) {
    Patch t = patches[y1][x1];
    patches[y1][x1] = patches[y2][x2];
    patches[y2][x2] = t;
    moves++;
  }
  
  public void scramble() {
    order();
    for(int i = 0; i < 1000; i++) {
      movePatch(patches[(int) random(0, 3)][(int) random(0, 3)]);
    }
    moves = 0;
  }
  
  public int getMoves() {
    return moves;
  }
  
  public boolean isOrdered() {
    int max = 0;
    for(int y = 0; y < 3; y++) {
      for(int x = 0; x < 3; x++) {
        if(patches[y][x] != null) {
          if(patches[y][x].getNumber() > max) {
            max = patches[y][x].getNumber();
          } else {
            return false;
          }
        }
      }
    }
    
    return patches[2][2] == null;
  }
  
}
