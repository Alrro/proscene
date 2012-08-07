package remixlab.proscene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public class Scene2D extends AbstractScene {	
	/**
	 * Constructor that defines an on-screen Scene (the one that most likely
	 * would just fulfill all of your needs). All viewer parameters (display flags,
	 * scene parameters, associated objects...) are set to their default values.
	 * See the associated documentation. This is actually just a convenience
	 * function that simply calls {@code this(p, (PGraphics) p.g)}. Call any
	 * other constructor by yourself to possibly define an off-screen Scene.
	 * 
	 * @see #Scene(PApplet, PGraphics)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */	
	public Scene2D(PApplet p) {
		//this(p, (PGraphicsOpenGL) p.g);
		this(p, p.g);
	}
	
	/**
	 * This constructor is typically used to define an off-screen Scene. This is
	 * accomplished simply by specifying a custom {@code renderer}, different
	 * from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. This
	 * is actually just a convenience function that simply calls
	 * {@code this(p, renderer, 0, 0)}. If you plan to define an on-screen Scene,
	 * call {@link #Scene(PApplet)} instead.
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics, int, int)
	 */
	public Scene2D(PApplet p, PGraphics renderer) {
	//public Scene2D(PApplet p, PGraphicsOpenGL renderer) {
		this(p, renderer, 0, 0);
	}

	/**
	 * This constructor is typically used to define an off-screen Scene. This is
	 * accomplished simply by specifying a custom {@code renderer}, different
	 * from the PApplet's renderer. All viewer parameters (display flags, scene
	 * parameters, associated objects...) are set to their default values. The
	 * {@code x} and {@code y} parameters define the position of the upper-left
	 * corner where the off-screen Scene is expected to be displayed, e.g., for
	 * instance with a call to the Processing built-in {@code image(img, x, y)}
	 * function. If {@link #isOffscreen()} returns {@code false} (i.e.,
	 * {@link #renderer()} equals the PApplet's renderer), the values of x and y
	 * are meaningless (both are set to 0 to be taken as dummy values). If you
	 * plan to define an on-screen Scene, call {@link #Scene(PApplet)} instead. 
	 * 
	 * @see #Scene(PApplet)
	 * @see #Scene(PApplet, PGraphics)
	 */
	public Scene2D(PApplet p, PGraphics renderer, int x, int y) {
		super(p, renderer, x, y);
		space = Space.TWO_D;
		if( renderer instanceof PGraphicsJava2D )
			p5renderer = P5Renderer.JAVA2D;
		else
			p5renderer = P5Renderer.P2D;
		
		cam = new Camera(this);
		setCamera(camera());//calls showAll();
		
		camera().setUpVector(new PVector(0,-1,0));
		
		initDefaultCameraProfiles();
		
		// called only once
		init();
	}	
	
	/**
	 * Returns the renderer context linked to this scene. 
	 * 
	 * @return PGraphics renderer.
	 */
	public PGraphics renderer() {
		return pg;
	}
	
	@Override
	protected void bindMatrices() {
		camera().computeProjectionMatrix();
		camera().computeModelViewMatrix();
		// /**
		if ( this.p5Renderer() == P5Renderer.JAVA2D ) {			
			//renderer().translate( camera().frame().translation().x, camera().frame().translation().y);
			//renderer().rotate( camera().frame().rotation().angle());			
			
			//renderer().translate(-camera().position().x, -camera().position().y);
			//renderer().translate(pg.width/2, pg.height/2);
			
			//renderer().translate(pg.width/2 - camera().position().x, pg.height/2 - camera().position().y);
			
			renderer().rotate(-camera().frame().rotation().angle());
			renderer().translate(-camera().position().x, -camera().position().y);
			
			float rad = -camera().frame().rotation().angle();
			float deg =  PApplet.degrees(rad);
			//PApplet.println(rad + " radians is " + deg + " degrees");
			//renderer().rotate( rad );
			//renderer().rotate( -camera().frame().rotation().angle() );			
									
			float[] wh = camera().getOrthoWidthHeight();// return halfWidth halfHeight
			//renderer().scale( 1/(2*wh[0]), 1/(2*wh[1]));
			//renderer().scale( 1/(wh[0]), 1/(wh[1]));
			//renderer().scale( wh[0], wh[1] );
		  //renderer().scale( (2*wh[0])/pg.width, (2*wh[1])/pg.height);
			//renderer().scale( pg.width/(2*wh[0]), -pg.height/(2*wh[1]));
		}
		// */
	}

	@Override
	protected void initDefaultCameraProfiles() {
		cameraProfileMap = new HashMap<String, CameraProfile>();
		cameraProfileNames = new ArrayList<String>();
		currentCameraProfile = null;
		// register here the default profiles
		registerCameraProfile( new CameraProfile(this, "TWO_D", CameraProfile.Mode.TWO_D) );
		setCurrentCameraProfile("TWO_D");		
	}	

	@Override
	public boolean setCurrentCameraProfile(String cp) {
		CameraProfile camProfile = cameraProfileMap.get(cp);
		if (camProfile == null)
			return false;
		if ((camProfile.mode() == CameraProfile.Mode.THIRD_PERSON) && (avatar() == null))
			return false;
		else {
			//third person
			if (camProfile.mode() == CameraProfile.Mode.THIRD_PERSON) {
				setDrawInteractiveFrame();
				//setCameraType(Camera.Type.PERSPECTIVE);
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar()).removeFromMouseGrabberPool();
				camera().frame().updateFlyUpVector();// ?
				camera().frame().stopSpinning();
				if (avatarIsInteractiveDrivableFrame) {
					((InteractiveDrivableFrame) (avatar())).updateFlyUpVector();
					((InteractiveDrivableFrame) (avatar())).stopSpinning();
				}
				// perform small animation ;)
				if (camera().anyInterpolationIsStarted())
					camera().stopAllInterpolations();
				Camera cm = camera().clone();
				cm.setPosition(avatar().cameraPosition());
				cm.setUpVector(avatar().upVector());
				cm.lookAt(avatar().target());
				camera().interpolateTo(cm.frame());
				currentCameraProfile = camProfile;
			} else {
				camera().frame().updateFlyUpVector();
				camera().frame().stopSpinning();
				
				if(currentCameraProfile != null)
					if (currentCameraProfile.mode() == CameraProfile.Mode.THIRD_PERSON)
						camera().interpolateToFitScene();												
        
				currentCameraProfile = camProfile;        
				
				setDrawInteractiveFrame(false);
				if (avatarIsInteractiveDrivableFrame)
					((InteractiveDrivableFrame) avatar()).addInMouseGrabberPool();
			}			
			return true;
		}
	}
	
	@Override
	public void setAvatar(Trackable t) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawAxis(float length) {
		/**
		final float charWidth = length / 40.0f;
		final float charHeight = length / 30.0f;
		final float charShift = 1.04f * length;
		*/

		pg.pushStyle();				
		pg.strokeWeight(2);			  
		
	  // X Axis
		pg.stroke(200, 0, 0);
		pg.line(0, 0, length, 0);
	  // Y Axis
		pg.stroke(0, 200, 0);		
		pg.line(0, 0, 0, length);		

		pg.popStyle();	  		
	}
	
	@Override
	public void drawPath(List<Frame> path, int mask, int nbFrames, int nbSteps,	float scale) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void drawGrid(float radius) {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public void drawCross(int color, float px, float py, float size, int strokeWeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawFilledCircle(int subdivisions, int color, PVector center, float radius) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawFilledSquare(int color, PVector center, float edge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawShooterTarget(int color, PVector center, float length, int strokeWeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawZoomWindowHint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawScreenRotateLineHint() {
		// TODO Auto-generated method stub
		
	}
}