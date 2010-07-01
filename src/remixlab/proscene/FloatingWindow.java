package remixlab.proscene;

import processing.core.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class FloatingWindow extends Frame implements WindowOwner {
	enum WindowBehaviour { SHUTDOWN_ON_EXIT, CLOSE_ON_EXIT }
	
	protected WindowBehaviour winBehaviour = WindowBehaviour.CLOSE_ON_EXIT;
	
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
	
	/**
	 * Remember what we have registered for.
	 */	
	
	public FloatingWindow(PApplet theApplet, String name, int x, int y, int w, int h, boolean noFrame, String mode) {
		super(name);
		parent = theApplet;
		winName = name;

		if(mode == null || mode.equals(""))
			mode = PApplet.P3D;
		
		viewer = new Viewer(mode);
		viewer.owner = this;
		viewer.frame = this;
		viewer.frame.setResizable(true);

		viewer.appWidth = w;
		viewer.appHeight = h;

		windowCtorCore(x, y, noFrame);
		
		super.setResizable(true);
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
	private void windowCtorCore(int x, int y, boolean noFrame) {
		//papplet.bkColor = papplet.color(0);
		
		viewer.resize(viewer.appWidth, viewer.appHeight);
		viewer.setPreferredSize(new Dimension(viewer.appWidth, viewer.appHeight));
		viewer.setMinimumSize(new Dimension(viewer.appWidth, viewer.appHeight));

		// add the PApplet to the Frame
		setLayout(new BorderLayout());
		add(viewer, BorderLayout.CENTER);

		// ensures that the animation thread is started and
		// that other internal variables are properly set.
		viewer.init();
		
		// add an exit on close listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				switch(winBehaviour) {
				case CLOSE_ON_EXIT:
					// close this frame
					setVisible(false);
					//dispose();
					break;
				case SHUTDOWN_ON_EXIT:
					System.exit(0);
					break;
					}
			}
		});

		// Pack the window, position it and make visible
		setUndecorated(noFrame);
		pack();
		setLocation(x,y);
		setVisible(true);
		
		// At least get a blank screen
		viewer.registerDraw(viewer);
		regDraw = true;
		
		// Make the window always on top
		setOnTop(true);	
	}
	
	/**
	 * Always make this window appear on top of other windows (or not). <br>
	 * This will not work when run from a remote server (ie Applet over the web)
	 * for security reasons. In this situation a call to this method is ignored
	 * and a warning is generated. 
	 * 
	 * @param onTop
	 */
	public void setOnTop(boolean onTop){
		try{
			setAlwaysOnTop(onTop);
		} catch (Exception e){
			PApplet.println("Warning: setOnTop() method will not work when the sketch is run from a remote location.");
		}
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
