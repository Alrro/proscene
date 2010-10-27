public class Board {
  private int size;
  private int moves;
  private PImage img;
  private Patch[][] patches;
  
  public Board(int size, PImage img) {
    this.size = size;
    this.img = img;
    order();
  }
  
  public void order() {
    scene.clearMouseGrabberPool();
    patches = new Patch[size][size];
    int number = 1;
    
    for(int y = 0; y < size; y++) {
      for(int x = 0; x < size; x++) {
        if(number < size * size) {
          PImage pimg = null;
          if(img != null) {
            pimg = createImage(img.width / size, img.height / size, RGB);
            pimg.blend(img, x * pimg.width, y * pimg.height, pimg.width, pimg.height, 0, 0, pimg.width, pimg.height, ADD);
          }
          patches[y][x] = new Patch(number, 150f / size, pimg, scene, this);
          number++;
        }
      }
    }
    
    moves = 0;
  }
  
  public void draw() {
    for(int y = 0; y < size; y++) {
      for(int x = 0; x < size; x++) {
        if(patches[y][x] != null) {
          patches[y][x].draw(patches[y][x].getSize() * ((float) x - (float) size / 2) + patches[y][x].getSize() / 2, patches[y][x].getSize() * ((float) y - (float) size / 2) + patches[y][x].getSize() / 2);
        }
      }
    }
  }
  
  public void movePatch(Patch patch) {
    int xp = -1, yp = -1, xh = -1, yh = -1;
    for(int y = 0; y < size; y++) {
      for(int x = 0; x < size; x++) {
        if(patches[y][x] == patch) {
          xp = x;
          yp = y;
        } else if(patches[y][x] == null) {
          xh = x;
          yh = y;
        }
      }
    }
    if(yp == yh) {
      movePatchHorizontally(xp, xh, yh);
    } else if(xp == xh) {
      movePatchVertically(yp, yh, xh);
    }
  }
  
  private void movePatchHorizontally(int xp, int xh, int y) {
    int i = xp < xh ? 1 : -1;
    
    if(patches[y][xp + i] == null) {
      swap(xp, y, xp + i, y);
    } else {
      movePatchHorizontally(xp + i, xh, y);
      movePatchHorizontally(xp, xh, y);
    }
  }
  
  private void movePatchVertically(int yp, int yh, int x) {
    int i = yp < yh ? 1 : -1;
    
    if(patches[yp + i][x] == null) {
      swap(x, yp, x, yp + i);
    } else {
      movePatchVertically(yp + i, yh, x);
      movePatchVertically(yp, yh, x);
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
    for(int i = 0; i < size * size * 1000; i++) {
      movePatch(patches[(int) random(0, size)][(int) random(0, size)]);
    }
    moves = 0;
  }
  
  public boolean isOrdered() {
    int max = 0;
    for(int y = 0; y < size; y++) {
      for(int x = 0; x < size; x++) {
        if(patches[y][x] != null) {
          if(patches[y][x].getNumber() > max) {
            max = patches[y][x].getNumber();
          } else {
            return false;
          }
        }
      }
    }
    
    return patches[size - 1][size - 1] == null;
  }
  
  public int getMoves() {
    return moves;
  }
  
  public PImage getImage() {
    return img;
  }
  
  public void setImage(PImage img) {
    this.img = img;
  }
  
  public int getSize() {
    return size;
  }
  
  public void setSize(int size) {
    this.size = size;
    order();
  } 
}