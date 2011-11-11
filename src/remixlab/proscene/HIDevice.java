package remixlab.proscene;

import java.lang.reflect.Method;

import remixlab.remixcam.core.AbstractScene;
import remixlab.remixcam.devices.*;

public class HIDevice extends AbstractHIDevice {
	protected Method handlerMethod;
	
	public HIDevice(AbstractScene scn, Mode m) {
		super(scn, m);
	}	
	
	/**
	 * Overriding of
	 * {@link remixlab.remixcam.devices.AbstractHIDevice#addHandler(Object, String)}.
	 */
	@Override
	public void addHandler(Object obj, String methodName) {
		try {
			handlerMethod = obj.getClass().getMethod(methodName, new Class[] { HIDevice.class });
			handlerObject = obj;
			handlerMethodName = methodName;
		} catch (Exception e) {
			  System.out.println("Something went wrong when registering your " + methodName + " method");
			  e.printStackTrace();
		}
	}
	
	/**
	 * Overriding of
	 * {@link remixlab.remixcam.devices.AbstractHIDevice#removeHandler()}.
	 */
	@Override
	public void removeHandler() {
		handlerMethod = null;
		handlerObject = null;
		handlerMethodName = null;
	}
	
	/**
	 * Overriding of
	 * {@link remixlab.remixcam.devices.AbstractHIDevice#invoke()}.
	 */
	@Override
	public boolean invoke() {
		boolean result = false;
		if (handlerObject != null) {
			try {
				handlerMethod.invoke(handlerObject, new Object[] { this });
				result = true;
			} catch (Exception e) {
				System.out.println("Something went wrong when invoking your "	+ handlerMethodName + " method");
				e.printStackTrace();
			}
		}
		return result;
	}	
}
