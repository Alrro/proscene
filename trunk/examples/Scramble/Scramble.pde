/**
 * Scramble.
 * by Alejandro Duarte (alejandro.d.a@gmail.com)
 * 
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */

import remixlab.proscene.*;

Scene scene;
Board board;

public void setup() {
  size(800, 500, P3D);
  scene = new Scene(this);
  scene.setAxisIsDrawn(false);
  scene.setGridIsDrawn(false);
  board = new Board(scene, null);
  board.scramble();
}

public void draw() {
  board.draw();
  textMode(SCREEN);
  fill(#BBBBBB);
  textFont(createFont("FFScala", 14));
  text("Moves: " + board.getMoves(), 5, height - 20);
  text("Press 'S' to scramble, 'O' to order, 'M' to change mode", 5, height - 5);
  textFont(createFont("FFScala", 20));
  text(board.isOrdered() && board.getMoves() > 0 ? "COMPLETED!" : "", 5, 20);
  textMode(MODEL);
}

public void keyTyped() {
  if(key == 's' || key == 'S') {
    board.scramble();
  } else if(key == 'o' || key == 'O') {
    board.order();
  } else if(key == 'm' || key == 'M') {
    if(board.getImage() == null) {
      board.setImage(loadImage("image.png"));
    } else {
      board.setImage(null);
    }
    board.order();
  }
}
