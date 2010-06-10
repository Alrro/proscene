import processing.core.*;
import remixlab.proscene.*;

public class CullingCamera extends Camera {
	float planeCoefficients[][];
	CullingCamera(Scene s) {
		super(s);
		planeCoefficients = new float [6][4];
	}
	
	/**
	public void computeFrustumPlanesEquations() {
		//planeCoefficients = getFrustumPlanesCoefficients();
		getFrustumPlanesCoefficients(planeCoefficients);
	}
	
	public float distanceToFrustumPlane(int index, PVector pos) {
		PVector myVec = new PVector(planeCoefficients[index][0],planeCoefficients[index][1], planeCoefficients[index][2]);
		return PVector.dot(pos, myVec) - planeCoefficients[index][3];
	}
	
	public boolean sphereIsVisible(PVector center, float radius) {
		for (int i=0; i<6; ++i)
		    if (distanceToFrustumPlane(i, center) > radius)
		      return false;
		  return true;
	}
	*/
	
	//public boolean aaBoxIsVisible(PVector p1, PVector p2, boolean entirely) {
	public boolean AABoxIsVisible(PVector p1, PVector p2) {
		boolean allInForAllPlanes = true;
		for (int i=0; i<6; ++i) {
			boolean allOut = true;
			for (int c=0; c<8; ++c) {
				PVector pos = new PVector(((c&4)!=0)?p1.x:p2.x, ((c&2)!=0)?p1.y:p2.y, ((c&1)!=0)?p1.z:p2.z);
				if (distanceToFrustumPlane(i, pos) > 0.0)
					allInForAllPlanes = false;
				else
					allOut = false;
			}			
			// The eight points are on the outside side of this plane
			if (allOut)
				return false;
		}
		
		if (BasicFrustum.entirely)
		//if (ViewFrustumCulling.entirely)
		    // Entirely visible : the eight points are on the inside side of the 6 planes
		    BasicFrustum.entirely = allInForAllPlanes;
			//ViewFrustumCulling.entirely = allInForAllPlanes;

		  // Too conservative, but tangent cases are too expensive to detect
		  return true;
	}
}
