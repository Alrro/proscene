package remixlab.proscene;

import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PVector;

public class HIDevice {
	public enum CameraMode {
		FIRST_PERSON("FIRST_PERSON camera mode set"),
		GOOGLE_EARTH("GOOGLE_EARTH camera mode set"),
		WORLD("WORLD camera mode set"),
		CUSTOM("CUSTOM camera mode set");
    private String description;		
    CameraMode(String description) {
       this.description = description;
    }		
    public String description() {
        return description;
    }
	}
	
	public enum IFrameMode {
		FRAME("FRAME interactive frame control mode set"),
		CAMERA("CAMERA interactive frame control mode set"),
		WORLD("WORLD interactive frame control mode set"),
		CUSTOM("CUSTOM interactive frame control mode set");
		private String description;		
		IFrameMode(String description) {
       this.description = description;
    }		
    public String description() {
        return description;
    }
	}
	
	protected CameraMode camMode;
	protected IFrameMode iFrameMode;
	
	protected Object handlerObject;
	protected Method handlerMethod;	
	protected String handlerMethodName;
	
	protected Scene scene;
	protected Camera camera;
	protected InteractiveCameraFrame cameraFrame;
	protected InteractiveFrame iFrame;

	protected PVector rotation, rotSens;
	protected PVector translation, transSens;
	
	protected PVector t;
	protected Quaternion q;
	protected float tx;
  protected float ty;
  protected float tz;
	protected float roll;
  protected float pitch;
  protected float yaw;

	protected Quaternion quaternion;

	public HIDevice(Scene scn) {
		scene = scn;
		camera = scene.camera();
		cameraFrame = camera.frame();
		iFrame = scene.interactiveFrame();
		translation = new PVector();
		transSens = new PVector(1, 1, 1);
		rotation = new PVector();
		rotSens = new PVector(1, 1, 1);		
		quaternion = new Quaternion();
		t = new PVector();
    q = new Quaternion();
    tx = translation.x * transSens.x;
    ty = translation.y * transSens.y;
    tz = translation.z * transSens.z;
  	roll = rotation.x * rotSens.x;
    pitch = rotation.y * rotSens.y;
    yaw = rotation.z * rotSens.z;
    camMode = CameraMode.FIRST_PERSON;
    iFrameMode = IFrameMode.CAMERA;
	}
	
	public void feed(float tx, float ty, float tz, float rx, float ry, float rz) {
		feedTranslation(tx,ty,tz);
		feedRotation(rx,ry,rz);
	}

	public void feedTranslation(float x, float y, float z) {
		translation.set(x, y, z);
	}
	
	public void feedRotation(float x, float y, float z) {
		rotation.set(x, y, z);
	}
	
	public void feedXTranslation(float t) {
		translation.x = t;
	}

	public void feedYTranslation(float t) {
		translation.y = t;
	}

	public void feedZTranslation(float t) {
		translation.z = t;
	}	

	public void feedXRotation(float t) {
		rotation.x = t;
	}

	public void feedYRotation(float t) {
		rotation.y = t;
	}

	public void feedZRotation(float t) {
		rotation.z = t;
	}
	
	public float feedXTranslation() {
		return 0;
	}

	public float feedYTranslation() {
		return 0;
	}

	public float feedZTranslation() {
		return 0;
	}

	public float feedXRotation() {
		return 0;
	}

	public float feedYRotation() {
		return 0;
	}

	public float feedZRotation() {
		return 0;
	}
		
	public void setTranslationSensitivity(float sx, float sy, float sz) {
		transSens.set(sx, sy, sz);
	}
	
	public void setRotationSensitivity(float sx, float sy, float sz) {
		rotSens.set(sx, sy, sz);
	}
	
	public void setXTranslationSensitivity(float sensitivity) {
		transSens.x = sensitivity;
	}

	public void setYTranslationSensitivity(float sensitivity) {
		transSens.y = sensitivity;
	}

	public void setZTranslationSensitivity(float sensitivity) {
		transSens.z = sensitivity;
	}	

	public void setXRotationSensitivity(float sensitivity) {
		rotSens.x = sensitivity;
	}

	public void setYRotationSensitivity(float sensitivity) {
		rotSens.y = sensitivity;
	}

	public void setZRotationSensitivity(float sensitivity) {
		rotSens.z = sensitivity;
	}	
	
	public void addHandler(Object obj, String methodName) {
		try {
			handlerMethod = obj.getClass().getMethod(methodName, new Class[] { HIDevice.class });
			handlerObject = obj;
			handlerMethodName = methodName;
		} catch (Exception e) {
			  PApplet.println("Something went wrong when registering your " + methodName + " method");
			  e.printStackTrace();
		}
	}
	
	public void removeHandler() {
		handlerMethod = null;
		handlerObject = null;
		handlerMethodName = null;
	}

	protected void handle() {		
		if (handlerObject != null) {
			try {
				handlerMethod.invoke(handlerObject, new Object[] { this });
			} catch (Exception e) {
				PApplet.println("Something went wrong when invoking your "	+ handlerMethodName + " method");
				e.printStackTrace();
			}
		}
		else {
			feedXTranslation(feedXTranslation());
			feedYTranslation(feedYTranslation());
			feedZTranslation(feedZTranslation());
			feedXRotation(feedXRotation());
			feedYRotation(feedYRotation());
			feedZRotation(feedZRotation());
		}
		
		tx = translation.x * transSens.x;
    ty = translation.y * transSens.y;
    tz = translation.z * transSens.z;
  	roll = rotation.x * rotSens.x;
    pitch = rotation.y * rotSens.y;
    yaw = rotation.z * rotSens.z;
		
		if (scene.interactiveFrameIsDrawn() || (scene.mouseGrabber() != null && scene.mouseGrabber() instanceof InteractiveFrame) )
			handleIFrame();
		else
			handleCamera();
  }
	
  protected void handleIFrame() {  	
  	switch (iFrameMode) {
		case FRAME:
			// A. Translate the iFrame      
      iFrame.translate(iFrame.inverseTransformOf(new PVector(tx,ty,-tz))); 
      // B. Rotate the iFrame 
      q.fromEulerAngles(-roll, -pitch, yaw);
      iFrame.rotate(q);
			break;
		case CAMERA:
		  // A. Translate the iFrame      
      // Transform to world coordinate system                     
      t = cameraFrame.inverseTransformOf(new PVector(tx,ty,-tz)); //same as: t = cameraFrame.orientation().rotate(new PVector(tx,ty,-tz));
      // And then down to frame
      if (iFrame.referenceFrame() != null)
        t = iFrame.referenceFrame().transformOf(t);
      iFrame.translate(t);
      // B. Rotate the iFrame
      t = camera.projectedCoordinatesOf(iFrame.position());    
      q.fromEulerAngles(roll, pitch, -yaw);
      t.set(-q.x, -q.y, -q.z);
      t = cameraFrame.orientation().rotate(t);
      t = iFrame.transformOf(t);
      q.x = t.x;
      q.y = t.y;
      q.z = t.z;
      iFrame.rotate(q);
			break;
    case WORLD:    	
      // Transform to frame
    	t.set(tx,ty,-tz);
      if (iFrame.referenceFrame() != null)
        t = iFrame.referenceFrame().transformOf(t);
      iFrame.translate(t);        
      // B. Rotate the iFrame
      Quaternion qx = new Quaternion(iFrame.transformOf(new PVector(1, 0, 0)), -roll);
      Quaternion qy = new Quaternion(iFrame.transformOf(new PVector(0, 1, 0)), -pitch);     
      Quaternion qz = new Quaternion(iFrame.transformOf(new PVector(0, 0, 1)), yaw);      
      q.set(qy);
      q.multiply(qz);
      q.multiply(qx);
      iFrame.rotate(q);
			break;
    case CUSTOM:
			customIFrameHandle();
			break;
		}  	
	}

	protected void handleCamera() {		
		switch (camMode) {
		case FIRST_PERSON:
   		// Translate      
      cameraFrame.translate(cameraFrame.localInverseTransformOf(new PVector(tx,ty,-tz)));
      // Rotate
      q.fromEulerAngles(-roll, -pitch, yaw);
      cameraFrame.rotate(q);
			break;
		case GOOGLE_EARTH:
			t = PVector.mult(cameraFrame.position(), -tz * ( rotSens.z/transSens.z ) );
      cameraFrame.translate(t);

      q.fromEulerAngles(-ty * ( rotSens.y/transSens.y ), tx * ( rotSens.x/transSens.x ), 0);
      cameraFrame.rotateAroundPoint(q, scene.camera().arcballReferencePoint());

      q.fromEulerAngles(0, 0, yaw);
      cameraFrame.rotateAroundPoint(q, scene.camera().arcballReferencePoint());

      q.fromEulerAngles(-roll, 0, 0);
      cameraFrame.rotate(q);
			break;
		case WORLD:
		  // Translate          
      cameraFrame.translate(new PVector(tx,ty,tz));
      // /**
      // Rotate (same as q.fromEulerAngles, but axes are expressed int the world coordinate system)            
      Quaternion qx = new Quaternion(cameraFrame.transformOf(new PVector(1, 0, 0)), -roll);
      Quaternion qy = new Quaternion(cameraFrame.transformOf(new PVector(0, 1, 0)), -pitch);     
      Quaternion qz = new Quaternion(cameraFrame.transformOf(new PVector(0, 0, 1)), yaw);      
      q.set(qy);
      q.multiply(qz);
      q.multiply(qx);
      cameraFrame.rotate(q);
      // */
			break;
		case CUSTOM:
			customCameraHandle();
			break;
		}
	}
	
	public void nextCameraMode() {
		switch (camMode) {
		case FIRST_PERSON:
			setCameraMode(CameraMode.GOOGLE_EARTH);
			break;
		case GOOGLE_EARTH:
			setCameraMode(CameraMode.WORLD);
			break;
		case WORLD:
			if (HIDevice.class == this.getClass())
				setCameraMode(CameraMode.FIRST_PERSON);
			else
				setCameraMode(CameraMode.CUSTOM);
			break;
		case CUSTOM:
			setCameraMode(CameraMode.FIRST_PERSON);
			break;
		}
	}
	
  public void previousCameraMode() {  	
  	switch (camMode) {
		case FIRST_PERSON:
			if (HIDevice.class == this.getClass())
				setCameraMode(CameraMode.WORLD);
			else
				setCameraMode(CameraMode.CUSTOM);
			break;
		case GOOGLE_EARTH:
			setCameraMode(CameraMode.FIRST_PERSON);
			break;
		case WORLD:
			setCameraMode(CameraMode.GOOGLE_EARTH);
			break;
		case CUSTOM:
			setCameraMode(CameraMode.WORLD);
			break;
		}
	}
  
  public CameraMode cameraMode() {
  	return camMode;
  }
  
  public void setCameraMode(CameraMode cMode) {
  	camMode = cMode;
  	if( camMode == CameraMode.GOOGLE_EARTH)
  		camera.interpolateToFitScene(); 
  	PApplet.println( camMode.description() );
  }
  
  public void nextIFrameMode() {  	
  	switch (iFrameMode) {
		case FRAME:
			setIFrameMode(IFrameMode.CAMERA);
			break;
		case CAMERA:
			setIFrameMode(IFrameMode.WORLD);
			break;
		case WORLD:
			if (HIDevice.class == this.getClass())
				setIFrameMode(IFrameMode.FRAME);
			else
				setIFrameMode(IFrameMode.CUSTOM);
			break;
		case CUSTOM:
			setIFrameMode(IFrameMode.FRAME);
			break;
		}
  }
  
  public void prevIFrameMode() {  	
  	switch (iFrameMode) {
		case FRAME:
			if (HIDevice.class == this.getClass())
				setIFrameMode(IFrameMode.WORLD);
			else
				setIFrameMode(IFrameMode.CUSTOM);
			break;
		case CAMERA:
			setIFrameMode(IFrameMode.FRAME);
			break;
		case WORLD:
			setIFrameMode(IFrameMode.CAMERA);
			break;
		case CUSTOM:
			setIFrameMode(IFrameMode.WORLD);
			break;
		}
  }
  
  public IFrameMode iFrameModeMode() {
  	return iFrameMode;
  }
  
  public void setIFrameMode(IFrameMode iMode) {
  	iFrameMode = iMode; 
  	PApplet.println( iFrameMode.description() );
  }
	
	protected void customCameraHandle() {}
	
  protected void customIFrameHandle() {}
}