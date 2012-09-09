package vfc;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

public class OctreeNode {
	Vector3D p1, p2;
	OctreeNode child[];
	int level;

	OctreeNode(Vector3D P1, Vector3D P2) {
		p1 = P1;
		p2 = P2;
		child = new OctreeNode[8];
	}

	public void draw(PGraphicsOpenGL pg3d) {
		// pg3d.stroke(color(0.3f*level*255, 0.2f*255, (1.0f-0.3f*level)*255));
		pg3d.stroke(pg3d.color(0.3f*level*255, 0.2f*255, (1.0f-0.3f*level)*255));
		pg3d.strokeWeight(level + 1);

		pg3d.beginShape();
		pg3d.vertex(p1.x(), p1.y(), p1.z());
		pg3d.vertex(p1.x(), p2.y(), p1.z());
		pg3d.vertex(p2.x(), p2.y(), p1.z());
		pg3d.vertex(p2.x(), p1.y(), p1.z());
		pg3d.vertex(p1.x(), p1.y(), p1.z());
		pg3d.vertex(p1.x(), p1.y(), p2.z());
		pg3d.vertex(p1.x(), p2.y(), p2.z());
		pg3d.vertex(p2.x(), p2.y(), p2.z());
		pg3d.vertex(p2.x(), p1.y(), p2.z());
		pg3d.vertex(p1.x(), p1.y(), p2.z());
		pg3d.endShape();
		// pg3d.endShape(CLOSE);

		pg3d.beginShape(PApplet.LINES);
		pg3d.vertex(p1.x(), p2.y(), p1.z());
		pg3d.vertex(p1.x(), p2.y(), p2.z());
		pg3d.vertex(p2.x(), p2.y(), p1.z());
		pg3d.vertex(p2.x(), p2.y(), p2.z());
		pg3d.vertex(p2.x(), p1.y(), p1.z());
		pg3d.vertex(p2.x(), p1.y(), p2.z());
		pg3d.endShape();
	}

	public void drawIfAllChildrenAreVisible(PGraphicsOpenGL pg3d, Camera camera) {
		Camera.Visibility vis = camera.aaBoxIsVisible(p1, p2);
		if (vis == Camera.Visibility.VISIBLE)
			draw(pg3d);
		else if (vis == Camera.Visibility.SEMIVISIBLE)
			if (child[0] != null)
				for (int i = 0; i < 8; ++i)
					child[i].drawIfAllChildrenAreVisible(pg3d, camera);
			else
				draw(pg3d);
	}

	public void buildBoxHierarchy(int l) {
		level = l;
		Vector3D middle = Vector3D.mult(Vector3D.add(p1, p2), 1 / 2.0f);
		for (int i = 0; i < 8; ++i) {
			// point in one of the 8 box corners
			Vector3D point = new Vector3D(((i & 4) != 0) ? p1.x() : p2.x(),
					((i & 2) != 0) ? p1.y() : p2.y(), ((i & 1) != 0) ? p1.z()
							: p2.z());
			if (level > 0) {
				child[i] = new OctreeNode(point, middle);
				child[i].buildBoxHierarchy(level - 1);
			} else
				child[i] = null;
		}
	}
}