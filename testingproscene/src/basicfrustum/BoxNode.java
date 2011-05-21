package basicfrustum;
import processing.core.*;
import remixlab.proscene.*;

public class BoxNode {
	PVector p1, p2;
	BoxNode child[];
	int level;
	
	BoxNode(PVector P1, PVector P2) {
		p1 = P1;
		p2 = P2;
		child = new BoxNode[8];		
	}
	
	public void draw(PApplet parent) {		
		//parent.colorMode(PApplet.RGB, 1);
		parent.stroke(parent.color(0.3f*level*255, 0.2f*255, (1.0f-0.3f*level)*255));
		parent.strokeWeight(level+1);
		
		parent.beginShape();		
		parent.vertex(p1.x, p1.y, p1.z);
		parent.vertex(p1.x, p2.y, p1.z);
		parent.vertex(p2.x, p2.y, p1.z);
		parent.vertex(p2.x, p1.y, p1.z);
		parent.vertex(p1.x, p1.y, p1.z);
		parent.vertex(p1.x, p1.y, p2.z);
		parent.vertex(p1.x, p2.y, p2.z);
		parent.vertex(p2.x, p2.y, p2.z);
		parent.vertex(p2.x, p1.y, p2.z);
		parent.vertex(p1.x, p1.y, p2.z);
		parent.endShape();
		//parent.endShape(PApplet.CLOSE);
		
		parent.beginShape(PApplet.LINES);
		parent.vertex(p1.x, p2.y, p1.z);
		parent.vertex(p1.x, p2.y, p2.z);
		parent.vertex(p2.x, p2.y, p1.z);
		parent.vertex(p2.x, p2.y, p2.z);
		parent.vertex(p2.x, p1.y, p1.z);
		parent.vertex(p2.x, p1.y, p2.z);
		parent.endShape();
		//parent.colorMode(PApplet.RGB, 255);
	}
	
	public void drawIfAllChildrenAreVisible(PApplet parent, Camera camera) {
		Camera.Visibility vis = camera.aaBoxIsVisible(p1, p2);
		if ( vis == Camera.Visibility.VISIBLE )			
			draw(parent);
		else if ( vis == Camera.Visibility.SEMIVISIBLE )
			if (child[0]!=null)
				for (int i=0; i<8; ++i)
					child[i].drawIfAllChildrenAreVisible(parent, camera);
			else
				draw(parent);
	}
	
	public void buildBoxHierarchy(int l) {
		level = l;		
		PVector middle = PVector.mult(PVector.add(p1, p2), 1/2.0f);
		for (int i=0; i<8; ++i) {
			// point in one of the 8 box corners
		    PVector point = new PVector(((i&4)!=0)?p1.x:p2.x, ((i&2)!=0)?p1.y:p2.y, ((i&1)!=0)?p1.z:p2.z);
		    if (level > 0) {
		    	child[i] = new BoxNode(point, middle);
		    	child[i].buildBoxHierarchy(level-1);
		    }
		    else
		    	child[i] = null;
		    }
	}	
}
