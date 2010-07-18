/*Main runner area
 * Matt Wetmore
 * Changelog
 * ---------
 * 12/14/09: Started work
 * 12/18/09: Reimplemented with BoidList class
 */

import processing.core.*;
import remixlab.proscene.*;
import napplet.*;

public class flock3d extends PApplet {
	Scene scene;
	int initBoidNum = 300; // amount of boids to start the program with
	int flockWidth = 1200;
	int flockHeight = 600;
	int flockDepth = 600;
	BoidList flock1;// ,flock2,flock3;
	boolean smoothEdges = false;
	boolean avoidWalls = true;

	public void setup() {
		size(640, 360, P3D);		
		scene = new Scene(this);
		scene.setAxisIsDrawn(false);
		scene.setGridIsDrawn(false);
		scene.setHelpIsDrawn(false);
		scene.setBoundingBox(new PVector(0,0,0), new PVector(flockWidth,flockHeight,flockDepth));
		//scene.setRadius(600);
		//scene.setCenter(new PVector(flockWidth/2, flockHeight/2, flockDepth/2));
		scene.showAll();
		// create and fill the list of boids
		flock1 = new BoidList(scene, initBoidNum, 255);
		// flock2 = new BoidList(100,255);
		// flock3 = new BoidList(100,128);
	}

	public void draw() {	
		scene.background(190,220,220);
		ambientLight(128,128,128);
		directionalLight(255, 255, 255, 0, 1, -100);
		noFill();
		stroke(0);
		
		line(0, 0, 0, 0, flockHeight, 0);
		line(0, 0, flockDepth, 0, flockHeight, flockDepth);
		line(0, 0, 0, flockWidth, 0, 0);
		line(0, 0, flockDepth, flockWidth, 0, flockDepth);

		line(flockWidth, 0, 0, flockWidth, flockHeight, 0);
		line(flockWidth, 0, flockDepth, flockWidth, flockHeight, flockDepth);
		line(0, flockHeight, 0, flockWidth, flockHeight, 0);
		line(0, flockHeight, flockDepth, flockWidth, flockHeight, flockDepth);

		line(0, 0, 0, 0, 0, flockDepth);
		line(0, flockHeight, 0, 0, flockHeight, flockDepth);
		line(flockWidth, 0, 0, flockWidth, 0, flockDepth);
		line(flockWidth, flockHeight, 0, flockWidth, flockHeight, flockDepth);				

		flock1.run(avoidWalls);
		// flock2.run();
		// flock3.run();
		if (smoothEdges)
			smooth();
		else
			noSmooth();
	}

	public void keyPressed() {
		switch (key) {
		case 'u':
			smoothEdges = !smoothEdges;
			break;
		case 'v':
			avoidWalls = !avoidWalls;
			break;
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "flock3d" });
	}

}