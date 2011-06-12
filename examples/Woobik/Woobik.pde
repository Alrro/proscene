/*
 * Woobik  - A Rubik Puzzle Game by Wookie Labs
 * 
 * Universidad Nacional de Colombia - Sede Bogotá
 * Developed By:
 * Andres Esteban Paez Torres  aepaezt@unal.edu.co
 * Hugo Camilo Salomon Torres  hcsalomont@unal.edu.co
 * 
 * Mods by:
 * <If you made any modification to source, put your name here>
 */

import remixlab.proscene.*;

Scene scene;
RubikCube c;
GraphicInterface gi;
boolean completado,pickerito=false;
int sequencer=0;

void setup() {
  size(600, 400, P3D);
  scene=new Scene(this);
  scene.setGridIsDrawn(false);
  scene.setAxisIsDrawn(false);
  smooth();
  c=new RubikCube(3);
  gi=new GraphicInterface();
}

void draw() {  
  if (sequencer==0) {
    gi.draw();
  }
  if (sequencer==1) {
    background(0);
    c.draw();
  }
  completado=c.isComplete();
  if(completado){sequencer=0;}
}

void mousePressed() {  
  if(pickerito){
    pickerito=false;
  }else{
    pickerito=true;
  }
  c.eventTouched();
  println(pickerito);
}

void mouseDragged() {
  pickerito=false;
}

