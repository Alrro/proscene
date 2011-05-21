import java.util.ArrayList;
import java.util.HashMap;

import processing.core.*;
import processing.opengl.*;
import codeanticode.glgraphics.*;
import remixlab.proscene.*;

public class PDBview extends PApplet {
	String PDB_FILE = "abh0.pdb"; // PDB file to read.

	// Some parameters to control the visual appearance:
	float scaleFactor = 5;        // Size scale factor.
	int renderMode = 1;           // 0 = lines, 1 = flat ribbons
	int ribbonDetail = 1;         // Ribbon detail: from 1 (highest) to 10 (lowest).
	float helixWidth = 10;        // Controls the helix diameter.

	ArrayList models;
	int renderedModels = 0;

	BSpline splineSide1;
	BSpline splineCenter;
	BSpline splineSide2;
	PVector flipTestV;
	int[] ribbonWidth;

	float avex, avey, avez;
	int natoms;

	Scene scene;

	int HELIX = 0;
	int STRAND = 1;
	int COIL = 2;
	int LHANDED = -1;
	int RHANDED = 1;

	public void setup() {
	  size(800, 600, GLConstants.GLGRAPHICS);

	  scene = new Scene(this);     
	  //scene.setGridIsDrawn(true);
	  scene.setAxisIsDrawn(false);	  

	  splineSide1 = new BSpline(false);
	  splineCenter = new BSpline(false);
	  splineSide2 = new BSpline(false);

	  ribbonWidth = new int[3];
	  ribbonWidth[HELIX] = 10;
	  ribbonWidth[STRAND] = 7;  
	  ribbonWidth[COIL] = 2;
	  flipTestV = new PVector();

	  loadPDB(PDB_FILE);
	}

	public void draw() {
	  GLGraphics renderer = (GLGraphics)g;
	  renderer.beginGL(); 

	  pointLight(250, 250, 250, 0, 0, 400);

	  renderedModels = 0;
	  GLModel model;
	  for (int i = 0; i < models.size(); i++) {
	    model = (GLModel)models.get(i);	    
	    model.setTint(50, 50, 200, 230);
	    renderer.model(model);
	    renderedModels ++;
	  }

	  renderer.endGL();      
	}
	
	public void keyPressed() {
		if(key == 'n') {
			println("rendered models " + models.size());
		}
	}

	void loadPDB(String filename) {
	  String strLines[];

	  String xstr, ystr, zstr;
	  float x, y, z;
	  int res, res0;
	  int nmdl;
	  String atstr, resstr;

	  HashMap residue;
	  ArrayList residues;
	  ArrayList atoms;
	  GLModel model;
	  PVector v;
	  String s;
	  strLines = loadStrings(filename);

	  models = new ArrayList();

	  avex = avey = avez = 0;
	  natoms = 0;

	  boolean readingModel = false;
	  atoms = null;
	  residues = null;
	  res0 = -1;    
	  nmdl = -1;
	  residue = null;
	  model = null;
	  for (int i = 0; i < strLines.length; i++) {
	    s = strLines[i];

	    if (s.startsWith("MODEL") || (s.startsWith("ATOM") && nmdl == -1)) {
	      nmdl++;

	      residue = null;
	      res0 = -1;

	      atoms = new ArrayList();
	      residues = new ArrayList();
	    }

	    if (s.startsWith("ATOM")) {
	      atstr = s.substring(12, 15);
	      atstr = atstr.trim();
	      resstr = s.substring(22, 26);
	      resstr = resstr.trim();
	      res = parseInt(resstr);

	      xstr = s.substring(30, 37);
	      xstr = xstr.trim();
	      ystr = s.substring(38, 45);
	      ystr = ystr.trim();            
	      zstr = s.substring(46, 53);
	      zstr = zstr.trim();

	      x = scaleFactor * parseFloat(xstr);
	      y = scaleFactor * parseFloat(ystr);
	      z = scaleFactor * parseFloat(zstr);            
	      v = new PVector(x, y, z);

	      avex += x;
	      avey += y;
	      avez += z;
	      natoms++;

	      atoms.add(v);

	      if (res0 != res) {
	        if (residue != null) residues.add(residue);            
	        residue = new HashMap();
	      }
	      residue.put(atstr, v);

	      res0 = res;
	    }

	    if (s.startsWith("ENDMDL")) {
	      if (residue != null) residues.add(residue);

	      createRibbonModel(residues, model, models);
	      float rgyr = calculateGyrRadius(atoms);

	      residue = null;  
	      atoms = null;
	      residues = null;
	    }
	  }

	  if (residue != null) {
	    if (residue != null) residues.add(residue);

	    createRibbonModel(residues, model, models);
	    float rgyr = calculateGyrRadius(atoms);

	    atoms = null;
	    residues = null;
	  }

	  // Centering model.  
	  avex /= natoms;
	  avey /= natoms;
	  avez /= natoms;
	  for (int i = 0; i < models.size(); i++) {
	    model = (GLModel)models.get(i);
	    model.beginUpdateVertices();
	    for (int k = 0; k < model.getSize(); k++) {
	      model.displaceVertex(k, -avex, -avey, -avez);
	    }
	    model.endUpdateVertices();
	  }

	  println("Loaded PDB file with " + models.size() + " models.");
	}

	void createRibbonModel(ArrayList residues, GLModel model, ArrayList trj) {
	  ArrayList vertices;
	  ArrayList normals;
	  vertices = new ArrayList();
	  normals = new ArrayList();

	  int[] ss = new int[residues.size()];
	  int[] handness = new int[residues.size()];

	  calculateSecStr(residues, ss, handness);   

	  for (int i = 0; i < residues.size(); i++) {
	    constructControlPoints(residues, i, ss[i], handness[i]);

	    if (renderMode == 0) {
	      generateSpline(0, vertices);
	      generateSpline(1, vertices);        
	      generateSpline(2, vertices);        
	    } 
	    else generateFlatRibbon(vertices, normals);
	  }  

	  if (renderMode == 0) { 
	    model = new GLModel(this, vertices.size(), LINES, GLModel.STATIC);
	    model.updateVertices(vertices);
	    model.initColors();
	    model.setColors(255, 100);      
	  } else {
	    model = new GLModel(this, vertices.size(), QUADS, GLModel.STATIC);
	    model.updateVertices(vertices);
	    model.initNormals();
	    model.updateNormals(normals);
	  }

	  trj.add(model);

	  println("Adding new model with " + vertices.size() + " vertices.");
	}

	float calculateGyrRadius(ArrayList atoms)
	{
	  PVector ati, atj;
	  float dx, dy, dz;    
	  float r = 0;
	  for (int i = 0; i < atoms.size(); i++) {
	    ati = (PVector)atoms.get(i);
	    for (int j = i + 1; j < atoms.size(); j++) {  
	      atj = (PVector)atoms.get(j);

	      dx = ati.x - atj.x;
	      dy = ati.y - atj.y;
	      dz = ati.z - atj.z;
	      r +=  dx * dx + dy * dy + dz * dz;
	    }
	  }
	  return sqrt(r) / (atoms.size() + 1);
	}

	void calculateSecStr(ArrayList residues, int[] ss, int[] handness) {
	  PVector c0, n1, ca1, c1, n2;
	  HashMap res0, res1, res2;
	  int n = residues.size();

	  float[] phi = new float[n];
	  float[] psi = new float[n];

	  for (int i = 0; i < n; i++) {
	    if (i == 0 || i == n - 1) {
	      phi[i] = 90;
	      psi[i] = 90;              
	    } else {
	      res0 = (HashMap)residues.get(i - 1);
	      res1 = (HashMap)residues.get(i);
	      res2 = (HashMap)residues.get(i + 1);

	      c0 = (PVector)res0.get("C");
	      n1 = (PVector)res1.get("N");
	      ca1 = (PVector)res1.get("CA"); 
	      c1 = (PVector)res1.get("C");
	      n2 = (PVector)res2.get("N");

	      phi[i] = calculateTorsionalAngle(c0, n1, ca1, c1);
	      psi[i] = calculateTorsionalAngle(n1, ca1, c1, n2);
	    }
	  }  

	  int firstHelix = 0;
	  int nconsRHelix = 0;
	  int nconsLHelix = 0;
	  int firstStrand = 0;
	  int nconsStrand = 0;
	  for (int i = 0; i < n; i++) {
	    // Right-handed helix      
	    if ((dist(phi[i], psi[i], -60, -45) < 30) && (i < n - 1)) {
	      if (nconsRHelix == 0) firstHelix = i;
	      nconsRHelix++;
	    } 
	    else {
	      if (3 <= nconsRHelix) {
	        for (int k = firstHelix; k < i; k++) {
	          ss[k] = HELIX;
	          handness[k] = RHANDED;                  
	        }
	      }
	      nconsRHelix = 0;
	    }

	    // Left-handed helix
	    if ((dist(phi[i], psi[i], +60, +45) < 30) && (i < n - 1)) {
	      if (nconsLHelix == 0) firstHelix = i;
	      nconsLHelix++;

	    } else {
	      if (3 <= nconsLHelix) {
	        for (int k = firstHelix; k < i; k++) {
	          ss[k] = HELIX;
	          handness[k] = LHANDED;
	        }
	      }
	      nconsLHelix = 0;
	    }

	    // Strand
	    if ((dist(phi[i], psi[i], -110, +130) < 30) && (i < n - 1)) {
	      if (nconsStrand == 0) firstStrand = i;
	      nconsStrand++;
	    } else {
	      if (2 <= nconsStrand) {
	        for (int k = firstStrand; k < i; k++) {
	          ss[k] = STRAND;
	          handness[k] = RHANDED;

	        }
	      }
	      nconsStrand = 0;
	    }        

	    ss[i] = COIL;
	    handness[i] = RHANDED;
	  }
	}

	float calculateTorsionalAngle(PVector at0, PVector at1, PVector at2, PVector at3) {
	  PVector r01 = PVector.sub(at0, at1);
	  PVector r32 = PVector.sub(at3, at2);
	  PVector r12 = PVector.sub(at1, at2);

	  PVector p = r12.cross(r01);
	  PVector q = r12.cross(r32);
	  PVector r = r12.cross(q);

	  float u = q.dot(q);
	  float v = r.dot(r);    

	  float a;
	  if (u <= 0.0 || v <= 0.0) {
	    a = 360.0f;
	  } else {
	    float u1 = p.dot(q); // u1 = p * q
	    float v1 = p.dot(r); // v1 = p * r

	      u = u1 / sqrt(u);
	    v = v1 / sqrt(v);

	    if (abs(u) > 0.01 || abs(v) > 0.01) a = degrees(atan2(v, u));
	    else a = 360.0f;
	  }    
	  return a;
	}


	void generateSpline(int n, ArrayList vertices) {
	  int ui;
	  float u;
	  PVector v0, v1;

	  v0 = new PVector();
	  v1 = new PVector();

	  if (n == 0) splineSide1.feval(0, v1); 
	  else if (n == 1) splineCenter.feval(0, v1);
	  else splineSide2.feval(0, v1);

	  for (ui = 1; ui <= 10; ui ++) {
	    if (ui % ribbonDetail == 0) {
	      u = 0.1f * ui; 
	      v0.set(v1);

	      if (n == 0) splineSide1.feval(u, v1); 
	      else if (n == 1) splineCenter.feval(u, v1); 
	      else splineSide2.feval(u, v1);

	      vertices.add(new PVector(v0.x, v0.y, v0.z));
	      vertices.add(new PVector(v1.x, v1.y, v1.z));            
	    }
	  }
	}

	void generateFlatRibbon(ArrayList vertices, ArrayList normals) {
	  PVector CentPoint0, CentPoint1;
	  PVector Sid1Point0, Sid1Point1;
	  PVector Sid2Point0, Sid2Point1;
	  PVector Transversal, Tangent;
	  PVector Normal0, Normal1;
	  int ui;
	  float u;

	  CentPoint0 = new PVector();
	  CentPoint1 = new PVector();
	  Sid1Point0 = new PVector();
	  Sid1Point1 = new PVector();
	  Sid2Point0 = new PVector();
	  Sid2Point1 = new PVector();
	  Transversal = new PVector();
	  Tangent = new PVector();
	  Normal0 = new PVector();
	  Normal1 = new PVector();

	  // The initial geometry is generated.
	  splineSide1.feval(0, Sid1Point1);
	  splineCenter.feval(0, CentPoint1);
	  splineSide2.feval(0, Sid2Point1);

	  // The tangents at the three previous points are the same.
	  splineSide2.deval(0, Tangent);

	  // Vector transversal to the ribbon.    
	  Transversal = PVector.sub(Sid1Point1, Sid2Point1);

	  //println("Transversal: " + Transversal);    
	  //println("Tangent: " + Tangent);

	  // The normal is calculated.
	  Normal1 = Transversal.cross(Tangent);
	  Normal1.normalize();
	  //println("Normal1 0:" + Normal1);

	  for (ui = 1; ui <= 10; ui ++) {
	    if (ui % ribbonDetail == 0) {
	      u = 0.1f * ui;

	      // The geometry of the previous iteration is saved.
	      Sid1Point0.set(Sid1Point1);
	      CentPoint0.set(CentPoint1);
	      Sid2Point0.set(Sid2Point1);
	      Normal0.set(Normal1);

	      // The new geometry is generated.
	      splineSide1.feval(u, Sid1Point1);
	      splineCenter.feval(u, CentPoint1);
	      splineSide2.feval(u, Sid2Point1);

	      // The tangents at the three previous points are the same.
	      splineSide2.deval(u, Tangent);
	      // Vector transversal to the ribbon.
	      Transversal = PVector.sub(Sid1Point1, Sid2Point1);
	      // The normal is calculated.
	      Normal1 = Transversal.cross(Tangent);
	      Normal1.normalize();

	      // The (Sid1Point0, Sid1Point1, MiddPoint0, MiddPoint1) face is drawn.
	      vertices.add(new PVector(Sid1Point0.x, Sid1Point0.y, Sid1Point0.z));
	      normals.add(new PVector(Normal0.x, Normal0.y, Normal0.z));

	      vertices.add(new PVector(Sid1Point1.x, Sid1Point1.y, Sid1Point1.z));
	      normals.add(new PVector(Normal1.x, Normal1.y, Normal1.z));

	      vertices.add(new PVector(CentPoint1.x, CentPoint1.y, CentPoint1.z));
	      normals.add(new PVector(Normal1.x, Normal1.y, Normal1.z));

	      vertices.add(new PVector(CentPoint0.x, CentPoint0.y, CentPoint0.z));
	      normals.add(new PVector(Normal0.x, Normal0.y, Normal0.z));            

	      // (MiddPoint0, MiddPoint1, Sid2Point0, Sid2Point1) plane is drawn.
	      vertices.add(new PVector(Sid2Point0.x, Sid2Point0.y, Sid2Point0.z));
	      normals.add(new PVector(Normal0.x, Normal0.y, Normal0.z));

	      vertices.add(new PVector(Sid2Point1.x, Sid2Point1.y, Sid2Point1.z));
	      normals.add(new PVector(Normal1.x, Normal1.y, Normal1.z));                        

	      vertices.add(new PVector(CentPoint1.x, CentPoint1.y, CentPoint1.z));
	      normals.add(new PVector(Normal1.x, Normal1.y, Normal1.z));            

	      vertices.add(new PVector(CentPoint0.x, CentPoint0.y, CentPoint0.z));
	      normals.add(new PVector(Normal0.x, Normal0.y, Normal0.z));            

	      //println("Normal0: " + ui + " " + Normal0);
	      //println("Normal1: " + ui + " " + Normal1);            
	    }
	  }
	}


	/******************************************************************************
	 * The code in the following three functions was based in the theory presented in
	 * the following article:
	 * "Algorithm for ribbon models of proteins."
	 * Authors: Mike Carson and Charles E. Bugg
	 * University of Alabama at Birmingham, Comprehensive Cancer Center
	 * 252 BHS, THT 79, University Station, Birmingham, AL 35294, USA
	 * Published in: J.Mol.Graphics 4, pp. 121-122 (1986)
	 ******************************************************************************/

	// Shifts the control points one place to the left.
	void shiftControlPoints() {
	  splineSide1.shiftBSplineCPoints();
	  splineCenter.shiftBSplineCPoints();
	  splineSide2.shiftBSplineCPoints();
	}

	// Adds a new control point to the arrays CPCenter, CPRight and CPLeft
	void addControlPoints(PVector ca0, PVector ox0, PVector ca1, int ss, int handness) {
	  PVector A, B, C, D, p0, cpt0, cpt1, cpt2;

	  A = PVector.sub(ca1, ca0);
	  B = PVector.sub(ox0, ca0);

	  // Vector normal to the peptide plane (pointing outside in the case of the
	  // alpha helix).
	  C = A.cross(B);

	  // Vector contained in the peptide plane (perpendicular to its direction).
	  D = C.cross(A);

	  // Normalizing vectors.
	  C.normalize();
	  D.normalize();

	  // Flipping test (to avoid self crossing in the strands).
	  if ((ss != HELIX) && (90.0 < degrees(PVector.angleBetween(flipTestV, D)))) {
	    // Flip detected. The plane vector is inverted.
	    D.mult(-1.0f);
	  }

	  // The central control point is constructed.
	  cpt0 = linearComb(0.5f, ca0, 0.5f, ca1);
	  splineCenter.setCPoint(3, cpt0);

	  if (ss == HELIX) {
	    // When residue i is contained in a helix, the control point is moved away
	    // from the helix axis, along the C direction. 
	    p0 = new PVector();
	    splineCenter.getCPoint(3, p0);
	    cpt0 = linearComb(1.0f, p0, handness * helixWidth, C);
	    splineCenter.setCPoint(3, cpt0);
	  }

	  // The control points for the side ribbons are constructed.
	  cpt1 = linearComb(1.0f, cpt0, +ribbonWidth[ss], D);
	  splineSide1.setCPoint(3, cpt1);

	  cpt2 = linearComb(1.0f, cpt0, -ribbonWidth[ss], D);
	  splineSide2.setCPoint(3, cpt2);

	  // Saving the plane vector (for the flipping test in the next call).
	  flipTestV.set(D);
	}

	void constructControlPoints(ArrayList residues, int res, int ss, int handness) {
	  PVector ca0, ox0, ca1;
	  PVector p0, p1, p2, p3;

	  p1 = new PVector();        
	  p2 = new PVector();
	  p3 = new PVector();

	  HashMap res0, res1;

	  res0 = res1 = null;
	  if (res == 0) {
	    // The control points 2 and 3 are created.
	    flipTestV.set(0, 0, 0);

	    res0 = (HashMap)residues.get(res);
	    res1 = (HashMap)residues.get(res + 1);
	    ca0 = (PVector)res0.get("CA");
	    ox0 = (PVector)res0.get("O");
	    ca1 = (PVector)res1.get("CA");        
	    addControlPoints(ca0, ox0, ca1, ss, handness);
	    splineSide1.copyCPoints(3, 2);
	    splineCenter.copyCPoints(3, 2);
	    splineSide2.copyCPoints(3, 2);        

	    res0 = (HashMap)residues.get(res + 1);
	    res1 = (HashMap)residues.get(res + 2);
	    ca0 = (PVector)res0.get("CA");
	    ox0 = (PVector)res0.get("O");
	    ca1 = (PVector)res1.get("CA"); 
	    addControlPoints(ca0, ox0, ca1, ss, handness);

	    // We still need the two first control points.
	    // Moving backwards along the cp_center[2] - cp_center[3] direction.
	    splineCenter.getCPoint(2, p2);
	    splineCenter.getCPoint(3, p3);

	    p1 = linearComb(2.0f, p2, -1, p3);
	    splineCenter.setCPoint(1, p1);        
	    splineSide1.setCPoint(1, linearComb(1.0f, p1, +ribbonWidth[ss], flipTestV)); 
	    splineSide2.setCPoint(1, linearComb(1.0f, p1, -ribbonWidth[ss], flipTestV));        

	    p0 = linearComb(2.0f, p1, -1, p2);
	    splineCenter.setCPoint(0, p0);
	    splineSide1.setCPoint(0, linearComb(1.0f, p0, +ribbonWidth[ss], flipTestV));
	    splineSide2.setCPoint(0, linearComb(1.0f, p0, -ribbonWidth[ss], flipTestV));
	  } else {
	    shiftControlPoints();
	    if ((residues.size() - 1 == res) || (residues.size() - 2 == res)) { 
	      // Moving forward along the cp_center[1] - cp_center[2] direction.
	      splineCenter.getCPoint(1, p1);             
	      splineCenter.getCPoint(2, p2);

	      p3 = linearComb(2.0f, p2, -1, p1);
	      splineCenter.setCPoint(3, p3);
	      splineSide1.setCPoint(3, linearComb(1.0f, p3, +ribbonWidth[ss], flipTestV));
	      splineSide2.setCPoint(3, linearComb(1.0f, p3, -ribbonWidth[ss], flipTestV));
	    } else {
	      res0 = (HashMap)residues.get(res + 1);
	      res1 = (HashMap)residues.get(res + 2);
	      ca0 = (PVector)res0.get("CA");
	      ox0 = (PVector)res0.get("O");
	      ca1 = (PVector)res1.get("CA");        
	      addControlPoints(ca0, ox0, ca1, ss, handness);
	    }
	  }
	  splineSide1.updateMatrix3();
	  splineCenter.updateMatrix3();
	  splineSide2.updateMatrix3();
	}

	PVector linearComb(float scalar0, PVector vector0, float scalar1, PVector vector1) {
	  return PVector.add(PVector.mult(vector0, scalar0), PVector.mult(vector1, scalar1));
	}
	
	
	
	// the other class
	

	final int MAX_BEZIER_ORDER = 10; // Maximum curve order.

	final float[][] BSplineMatrix = {
			{ -1.0f / 6.0f, 1.0f / 2.0f, -1.0f / 2.0f, 1.0f / 6.0f },
			{ 1.0f / 2.0f, -1.0f, 1.0f / 2.0f, 0.0f },
			{ -1.0f / 2.0f, 0.0f, 1.0f / 2.0f, 0.0f },
			{ 1.0f / 6.0f, 2.0f / 3.0f, 1.0f / 6.0f, 0.0f } };

	// The element(i, n) of this array contains the binomial coefficient
	// C(i, n) = n!/(i!(n-i)!)
	final int[][] BinomialCoefTable = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
			{ 0, 1, 3, 6, 10, 15, 21, 28, 36, 45 },
			{ 0, 0, 1, 4, 10, 20, 35, 56, 84, 120 },
			{ 0, 0, 0, 1, 5, 15, 35, 70, 126, 210 },
			{ 0, 0, 0, 0, 1, 6, 21, 56, 126, 252 },
			{ 0, 0, 0, 0, 0, 1, 7, 28, 84, 210 },
			{ 0, 0, 0, 0, 0, 0, 1, 8, 36, 120 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 9, 45 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 10 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };

	// The element of this(i, j) of this table contains(i/10)^(3-j).
	final float[][] TVectorTable = {
	// t^3, t^2, t^1, t^0
			{ 0, 0, 0, 1 }, // t = 0.0
			{ 0.001f, 0.01f, 0.1f, 1 }, // t = 0.1
			{ 0.008f, 0.04f, 0.2f, 1 }, // t = 0.2
			{ 0.027f, 0.09f, 0.3f, 1 }, // t = 0.3
			{ 0.064f, 0.16f, 0.4f, 1 }, // t = 0.4
			{ 0.125f, 0.25f, 0.5f, 1 }, // t = 0.5
			{ 0.216f, 0.36f, 0.6f, 1 }, // t = 0.6
			{ 0.343f, 0.49f, 0.7f, 1 }, // t = 0.7
			{ 0.512f, 0.64f, 0.8f, 1 }, // u = 0.8
			{ 0.729f, 0.81f, 0.9f, 1 }, // t = 0.9
			{ 1, 1, 1, 1 } // t = 1.0
	};

	// The element of this(i, j) of this table contains(3-j)*(i/10)^(2-j) if
	// j < 3, 0 otherwise.
	final float[][] DTVectorTable = {
	// 3t^2, 2t^1, t^0
			{ 0, 0, 1, 0 }, // t = 0.0
			{ 0.03f, 0.2f, 1, 0 }, // t = 0.1
			{ 0.12f, 0.4f, 1, 0 }, // t = 0.2
			{ 0.27f, 0.6f, 1, 0 }, // t = 0.3
			{ 0.48f, 0.8f, 1, 0 }, // t = 0.4
			{ 0.75f, 1.0f, 1, 0 }, // t = 0.5
			{ 1.08f, 1.2f, 1, 0 }, // t = 0.6
			{ 1.47f, 1.4f, 1, 0 }, // t = 0.7
			{ 1.92f, 1.6f, 1, 0 }, // t = 0.8
			{ 2.43f, 1.8f, 1, 0 }, // t = 0.9
			{ 3, 2, 1, 0 } // t = 1.0
	};

	abstract class Curve3D {
		abstract void feval(float t, PVector p);

		abstract void deval(float t, PVector d);

		abstract float fevalX(float t);

		abstract float fevalY(float t);

		abstract float fevalZ(float t);

		abstract float devalX(float t);

		abstract float devalY(float t);

		abstract float devalZ(float t);
	}

	abstract class Spline extends Curve3D {
		// The factorial of n.
		int factorial(int n) {
			return n <= 0 ? 1 : n * factorial(n - 1);
		}

		// Gives n!/(i!(n-i)!).
		int binomialCoef(int i, int n) {
			if ((i <= MAX_BEZIER_ORDER) && (n <= MAX_BEZIER_ORDER))
				return BinomialCoefTable[i][n - 1];
			else
				return (int) (factorial(n) / (factorial(i) * factorial(n - i)));
		}

		// Evaluates the Berstein polinomial(i, n) at u.
		float bersteinPol(int i, int n, float u) {
			return binomialCoef(i, n) * pow(u, i) * pow(1 - u, n - i);
		}

		// The derivative of the Berstein polinomial.
		float dbersteinPol(int i, int n, float u) {
			float s1, s2;
			if (i == 0)
				s1 = 0;
			else
				s1 = i * pow(u, i - 1) * pow(1 - u, n - i);
			if (n == i)
				s2 = 0;
			else
				s2 = -(n - i) * pow(u, i) * pow(1 - u, n - i - 1);
			return binomialCoef(i, n) * (s1 + s2);
		}
	}

	class BSpline extends Spline {
		BSpline() {
			initParameters(true);
		}

		BSpline(boolean t) {
			initParameters(t);
		}

		// Sets lookup table use.
		void initParameters(boolean t) {
			bsplineCPoints = new float[4][3];
			TVector = new float[4];
			DTVector = new float[4];
			M3 = new float[4][3];
			pt = new float[3];
			tg = new float[3];
			lookup = t;
		}

		// Sets n-th control point.
		void setCPoint(int n, PVector P) {
			bsplineCPoints[n][0] = P.x;
			bsplineCPoints[n][1] = P.y;
			bsplineCPoints[n][2] = P.z;
			updateMatrix3();
		}

		// Gets n-th control point.
		void getCPoint(int n, PVector P) {
			P.set(bsplineCPoints[n]);
		}

		// Replaces the current B-spline control points(0, 1, 2) with(1, 2, 3).
		// This
		// is used when a new spline is to be joined to the recently drawn.
		void shiftBSplineCPoints() {
			for (int i = 0; i < 3; i++) {
				bsplineCPoints[0][i] = bsplineCPoints[1][i];
				bsplineCPoints[1][i] = bsplineCPoints[2][i];
				bsplineCPoints[2][i] = bsplineCPoints[3][i];
			}
			updateMatrix3();
		}

		void copyCPoints(int n_source, int n_dest) {
			for (int i = 0; i < 3; i++) {
				bsplineCPoints[n_dest][i] = bsplineCPoints[n_source][i];
			}
		}

		// Updates the temporal matrix used in order 3 calculations.
		void updateMatrix3() {
			float s;
			int i, j, k;
			for (i = 0; i < 4; i++) {
				for (j = 0; j < 3; j++) {
					s = 0;
					for (k = 0; k < 4; k++)
						s += BSplineMatrix[i][k] * bsplineCPoints[k][j];
					M3[i][j] = s;
				}
			}
		}

		void feval(float t, PVector p) {
			evalPoint(t);
			p.set(pt);
		}

		void deval(float t, PVector d) {
			evalTangent(t);
			d.set(tg);
		}

		float fevalX(float t) {
			evalPoint(t);
			return pt[0];
		}

		float fevalY(float t) {
			evalPoint(t);
			return pt[1];
		}

		float fevalZ(float t) {
			evalPoint(t);
			return pt[2];
		}

		float devalX(float t) {
			evalTangent(t);
			return tg[0];
		}

		float devalY(float t) {
			evalTangent(t);
			return tg[1];
		}

		float devalZ(float t) {
			evalTangent(t);
			return tg[2];
		}

		// Point evaluation.
		void evalPoint(float t) {
			if (lookup) {
				bsplinePointI((int) (10 * t));
			} else {
				bsplinePoint(t);
			}
		}

		// Tangent evaluation.
		void evalTangent(float t) {
			if (lookup) {
				bsplineTangentI((int) (10 * t));
			} else {
				bsplineTangent(t);
			}
		}

		// Calculates the point on the cubic spline corresponding to the
		// parameter value t in [0, 1].
		void bsplinePoint(float t) {
			// Q(u) = UVector * BSplineMatrix * BSplineCPoints

			float s;
			int i, j, k;

			for (i = 0; i < 4; i++) {
				TVector[i] = pow(t, 3 - i);
			}

			for (j = 0; j < 3; j++) {
				s = 0;
				for (k = 0; k < 4; k++) {
					s += TVector[k] * M3[k][j];
				}
				pt[j] = s;
			}
		}

		// Calculates the tangent vector of the spline at t.
		void bsplineTangent(float t) {
			// Q(u) = DTVector * BSplineMatrix * BSplineCPoints

			float s;
			int i, j, k;

			for (i = 0; i < 4; i++) {
				if (i < 3) {
					DTVector[i] = (3 - i) * pow(t, 2 - i);
				} else {
					DTVector[i] = 0;
				}
			}

			for (j = 0; j < 3; j++) {
				s = 0;
				for (k = 0; k < 4; k++) {
					s += DTVector[k] * M3[k][j];
				}
				tg[j] = s;
			}
		}

		// Gives the point on the cubic spline corresponding to t/10(using the
		// lookup table).
		void bsplinePointI(int t) {
			// Q(u) = TVectorTable[u] * BSplineMatrix * BSplineCPoints

			float s;
			int j, k;

			for (j = 0; j < 3; j++) {
				s = 0;
				for (k = 0; k < 4; k++) {
					s += TVectorTable[t][k] * M3[k][j];
				}
				pt[j] = s;
			}
		}

		// Calulates the tangent vector of the spline at t/10.
		void bsplineTangentI(int t) {
			// Q(u) = DTVectorTable[u] * BSplineMatrix * BSplineCPoints

			float s;
			int j, k;

			for (j = 0; j < 3; j++) {
				s = 0;
				for (k = 0; k < 4; k++) {
					s += DTVectorTable[t][k] * M3[k][j];
				}
				tg[j] = s;
			}
		}

		// Control points.
		float[][] bsplineCPoints;

		// Parameters.
		boolean lookup;

		// Auxiliary arrays used in the calculations.
		float[][] M3;
		float[] TVector, DTVector;

		// Point and tangent vectors.
		float[] pt, tg;
	}
}
