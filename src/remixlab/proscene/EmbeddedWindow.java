package remixlab.proscene;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.lang.reflect.Method;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class EmbeddedWindow extends Panel implements WindowOwner {
	
	boolean regDraw = false;
	
	/** The object to handle the draw event */
	Object drawHandlerObject = null;
	/** The method in drawHandlerObject to execute */
	Method drawHandlerMethod = null;
	/** the name of the method to handle the event */ 
	String drawHandlerMethodName = null;
	
	boolean regSceneConfig = false;
	
	/** The object to handle the draw event */
	Object sceneConfigHandlerObject = null;
	/** The method in drawHandlerObject to execute */
	Method sceneConfigHandlerMethod = null;
	/** the name of the method to handle the event */ 
	String sceneConfigHandlerMethodName = null;
	
	
	protected PApplet parent;
	
	/**
	 * Gives direct access to the PApplet object inside the frame
	 * 
	 */
	public Viewer viewer;
	
	protected String winName;
	
	public EmbeddedWindow(PApplet theApplet, String name, int w, int h, String mode) {
		super();
		parent = theApplet;
		winName = name;

		if(mode == null || mode.equals(""))
			mode = PApplet.P3D;
		
		viewer = new Viewer(mode);
		viewer.owner = this;		
		//viewer.frame = this;
		//viewer.frame.setResizable(true);

		viewer.appWidth = w;
		viewer.appHeight = h;

		windowCtorCore();
		
		//super.setResizable(true);
	}
	
	/**
	 * Core stuff for GWindows ctor
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param noFrame
	 * @param mode
	 */
	private void windowCtorCore() {
		//papplet.bkColor = papplet.color(0);
		
		viewer.resize(viewer.appWidth, viewer.appHeight);
		viewer.setPreferredSize(new Dimension(viewer.appWidth, viewer.appHeight));
		viewer.setMinimumSize(new Dimension(viewer.appWidth, viewer.appHeight));

		// add the PApplet to the Frame
		setLayout(new BorderLayout());
		add(viewer, BorderLayout.CENTER);
		//add(viewer, BorderLayout.NORTH);

		// ensures that the animation thread is started and
		// that other internal variables are properly set.
		viewer.init();

		// Pack the window, position it and make visible
		//setUndecorated(noFrame);
		//pack();
		//setLocation(x,y);
		setVisible(true);
		
		// At least get a blank screen
		viewer.registerDraw(viewer);
		regDraw = true;	
	}	
	
    //interface implementation!
	
	public void addDrawHandler(Object obj, String methodName) {
		try{
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { Viewer.class } );			
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
		} catch (Exception e) {
			
		}
	}
	
	public void addSceneConfigHandler(Object obj, String methodName) {
		try{
			sceneConfigHandlerMethod = obj.getClass().getMethod(methodName, new Class[] { Scene.class } );			
			sceneConfigHandlerObject = obj;
			sceneConfigHandlerMethodName = methodName;
			regSceneConfig = true;
		} catch (Exception e) {
			
		}
	}
	
    public boolean hasRegisteredDraw() {
    	return regDraw;
    }
	
    public Object getDrawHandlerObject() {
    	return drawHandlerObject;
    }
	
    public  Method getDrawHandlerMethod() {
    	return drawHandlerMethod;
    }
	
    public String getDrawHandlerMethodName() {
    	return drawHandlerMethodName;
    }
	
    public boolean hasRegisteredSceneConfig() {
    	return regSceneConfig;  
    }
	
    public Object getSceneConfigHandlerObject() {
    	return sceneConfigHandlerObject;
    }
	
    public Method getSceneConfigHandlerMethod() {
    	return sceneConfigHandlerMethod;
    }
	
    public String getSceneConfigHandlerMethodName() {
    	return sceneConfigHandlerMethodName;
    }
    
    public Scene getScene() {
    	return viewer.getScene();
    }
}
