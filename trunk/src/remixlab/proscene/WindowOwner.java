package remixlab.proscene;

import java.lang.reflect.Method;

public interface WindowOwner {	
	/**
	 * Attempt to add the 'draw' handler method. 
	 * The default event handler is a method that returns void and one
	 * PApplet parameter
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	void addDrawHandler(Object obj, String methodName);
	
	void addSceneConfigHandler(Object obj, String methodName);
	
	boolean hasRegisteredDraw();
	
	Object getDrawHandlerObject();
	
	Method getDrawHandlerMethod();
	
	String getDrawHandlerMethodName();
	
	boolean hasRegisteredSceneConfig();
	
	Object getSceneConfigHandlerObject();
	
	Method getSceneConfigHandlerMethod();
	
	String getSceneConfigHandlerMethodName();	
	
	Scene getScene();
}
