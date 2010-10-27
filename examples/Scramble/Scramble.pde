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
  board = new Board(3, null);
  scene.camera().setPosition(new PVector(-20, 100, 230));
  scene.camera().lookAt(new PVector(0, 0, 0));
}

public void draw() {
  lights();
  directionalLight(50, 50, 50, scene.camera().orientation().x - scene.camera().position().x, scene.camera().orientation().y - scene.camera().position().y, scene.camera().orientation().z - scene.camera().position().z);
  spotLight(150, 150, 150, scene.camera().position().x, scene.camera().position().y, scene.camera().position().z, 0, 0, -1, 1, 20);
  spotLight(100, 100, 100, scene.camera().position().x, scene.camera().position().y, scene.camera().position().z, 0, 0, 1, 1, 20);
  board.draw();
  drawText();
}

public void drawText() {
  textMode(SCREEN);
  fill(#BBBBBB);
  textFont(createFont("FFScala", 14));
  text("" + board.getMoves() + " moves.", 5, height - 20);
  text("Press 'i' to scramble, 'o' to order, 'p' to change mode, 'q' to increase size, 'w' to decrease size.", 5, height - 5);
  textFont(createFont("FFScala", 30));
  fill(#EEEEEE);
  text(board.isOrdered() && board.getMoves() > 0 ? "COMPLETED!" : "", 5, 28);
  textMode(MODEL);
}

public void keyTyped() {
  if(key == 'i' || key == 'I') {
    board.scramble();
  } else if(key == 'o' || key == 'O') {
    board.order();
  } else if(key == 'p' || key == 'P') {
    if(board.getImage() == null) {
      board.setImage(loadImage("image.png"));
    } else {
      board.setImage(null);
    }
    board.order();
  } else if(key == 'q') {
    if(board.getSize() < 5) {
      board.setSize(board.getSize() + 1);
    }
  } else if(key == 'w') {
    if(board.getSize() > 3) {
      board.setSize(board.getSize() - 1);
    }
  }
}