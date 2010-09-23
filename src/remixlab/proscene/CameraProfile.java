/**
 *                     ProScene (version 1.0.0-alpha1)      
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

package remixlab.proscene;

import java.awt.event.*;

import remixlab.proscene.Scene.Button;
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.Modifier;
import remixlab.proscene.Scene.MouseAction;

public class CameraProfile {
	public enum Mode {ARCBALL, WHEELED_ARCBALL, FIRST_PERSON, THIRD_PERSON, CUSTOM}
	protected String name;
	protected Scene scene;
	protected Mode mode;
	protected ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction> keyboard;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> cameraActions;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> iFrameActions;
	// C L I C K A C T I O N S
	protected ShortcutMappings<ClickShortcut, ClickAction> clickActions;
	private Integer numberOfClicks;
	//private java.util.Timer clickTimer;
	//private Timer clickTimer;	
	//private boolean hasMouseDoubleClicked;
	//private Button clickButton;
	//private Modifier clickModifier;

	// W H E E L
	public final Integer WHEEL = 1;
	protected ShortcutMappings<Shortcut<Integer>, Scene.MouseAction> cameraWheelActions;
	protected ShortcutMappings<Shortcut<Integer>, Scene.MouseAction> frameWheelActions;
	
	public CameraProfile(Scene scn, String n) {
		this(scn, n, Mode.CUSTOM);
	}

	public CameraProfile(Scene scn, String n, Mode m) {
		scene = scn;		
		name = n;
		mode = m;
		keyboard = new ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction>(scene);
		cameraActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(scene);
		iFrameActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(scene);		
		clickActions = new ShortcutMappings<ClickShortcut, Scene.ClickAction>(scene);
		
		/**
		scene.parent.addMouseWheelListener( scene.dE );
		cameraWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		frameWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		*/
		
		switch (mode) {
		case ARCBALL:
			arcballDefaultShortcuts();
			break;
		case WHEELED_ARCBALL:
			cameraWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
			frameWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
			
			arcballDefaultShortcuts();
			
			setCameraWheelShortcut( MouseAction.ZOOM );
			setCameraWheelShortcut( Scene.Modifier.CONTROL, MouseAction.MOVE_FORWARD );
			setCameraWheelShortcut( Scene.Modifier.ALT, MouseAction.MOVE_BACKWARD );
			setFrameWheelShortcut( MouseAction.ZOOM );
			setFrameWheelShortcut( Scene.Modifier.CONTROL, MouseAction.MOVE_FORWARD );
			setFrameWheelShortcut( Scene.Modifier.ALT, MouseAction.MOVE_BACKWARD );
			
			scene.parent.addMouseWheelListener( scene.dE );
			break;
		case FIRST_PERSON:
			// setShortcut('a', CameraKeyboardAction.SHOW_ALL);
			setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD);
			setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND);
			setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD);
			setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
					Scene.MouseAction.ROLL);
			setCameraShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT,
					Scene.MouseAction.DRIVE);
			setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
			setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_CAMERA_FLY_SPEED);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_CAMERA_FLY_SPEED);

			setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
			setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
			break;
		case THIRD_PERSON:
			setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD);
			setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD);
			setIFrameShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
					Scene.MouseAction.ROLL);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT,
					Scene.MouseAction.DRIVE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_AVATAR_FLY_SPEED);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_AVATAR_FLY_SPEED);
			setShortcut('a', Scene.CameraKeyboardAction.INCREASE_AZYMUTH);
			setShortcut('A', Scene.CameraKeyboardAction.DECREASE_AZYMUTH);
			setShortcut('i', Scene.CameraKeyboardAction.INCREASE_INCLINATION);
			setShortcut('I', Scene.CameraKeyboardAction.DECREASE_INCLINATION);
			setShortcut('t', Scene.CameraKeyboardAction.INCREASE_TRACKING_DISTANCE);
			setShortcut('T', Scene.CameraKeyboardAction.DECREASE_TRACKING_DISTANCE);
			break;
		case CUSTOM:
			break;
		}
	}
	
	private void arcballDefaultShortcuts() {
		setShortcut(Scene.Arrow.RIGHT, Scene.CameraKeyboardAction.MOVE_CAMERA_RIGHT);
		setShortcut(Scene.Arrow.LEFT, Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
		setShortcut(Scene.Arrow.UP, Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
		setShortcut(Scene.Arrow.DOWN, Scene.CameraKeyboardAction.MOVE_CAMERA_DOWN);

		setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
		setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
		setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);
		setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
		setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
		setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);

		setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.CONTROL,
				Scene.MouseAction.ZOOM_ON_REGION);
		setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
				Scene.MouseAction.SCREEN_ROTATE);

		setShortcut('+', Scene.CameraKeyboardAction.INCREASE_ROTATION_SENSITIVITY);
		setShortcut('-', Scene.CameraKeyboardAction.DECREASE_ROTATION_SENSITIVITY);

		setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
		setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
		
		setClickShortcut(Button.LEFT, 2, ClickAction.ALIGN_CAMERA);
		setClickShortcut(Button.MIDDLE, 2, ClickAction.ZOOM_TO_FIT);
	}
	
	public Mode mode() {
		return mode;
	}
	
	protected MouseAction iFrameMouseAction(MouseEvent e) {
		MouseAction iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		Button button = scene.dE.getButton(e);

		if (button == null) {
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return iFrameMouseAction;
		}

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.ALT);
			if (e.isAltGraphDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.ALT_GRAPH);
			if (e.isControlDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.CONTROL);
			if (e.isShiftDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.SHIFT);
			if (iFrameMouseAction != null)
				return iFrameMouseAction;
		}

		iFrameMouseAction = iFrameShortcut(button);

		if (iFrameMouseAction == null)
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;

		return iFrameMouseAction;
	}
	
	protected MouseAction cameraMouseAction(MouseEvent e) {
		MouseAction camMouseAction = MouseAction.NO_MOUSE_ACTION;
		Button button = scene.dE.getButton(e);

		if (button == null) {
			camMouseAction = MouseAction.NO_MOUSE_ACTION;
			return camMouseAction;
		}

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				camMouseAction = cameraShortcut(button, Modifier.ALT);
			if (e.isAltGraphDown())
				camMouseAction = cameraShortcut(button, Modifier.ALT_GRAPH);
			if (e.isControlDown())
				camMouseAction = cameraShortcut(button, Modifier.CONTROL);
			if (e.isShiftDown())
				camMouseAction = cameraShortcut(button, Modifier.SHIFT);
			if (camMouseAction != null)
				return camMouseAction;
		}

		camMouseAction = cameraShortcut(button);

		if (camMouseAction == null)
			camMouseAction = MouseAction.NO_MOUSE_ACTION;

		return camMouseAction;
	}

	public String name() {
		return name;
	}

	public boolean isRegistered() {
		return scene.isCameraProfileRegistered(this);
	}

	public boolean register() {
		return scene.registerCameraProfile(this);
	}

	public void unregister() {
		scene.unregisterCameraProfile(this);
	}

	public ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction> keyBindings() {
		return keyboard;
	}

	// keyboard wrappers:
	public void setShortcut(char key, Scene.Modifier modifier, CameraKeyboardAction action) {
		int vKey = MathUtils.getVKey(key);
		if (vKey >= 0 )
			setShortcut(vKey, modifier, action);
	}
	
	public void setShortcut(Integer vKey, Scene.Modifier modifier, CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(vKey, modifier), action);
	}

	public void setShortcut(Scene.Arrow arrow, CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(arrow), action);
	}

	public void setShortcut(Character key, CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(key), action);
	}

	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllMappings();
	}
	
	public void removeShortcut(char key, Scene.Modifier modifier) {
		removeShortcut(MathUtils.getVKey(key), modifier);
	}

	public void removeShortcut(Integer vKey, Scene.Modifier modifier) {
		keyboard.removeMapping(new KeyboardShortcut(vKey, modifier));
	}

	public void removeShortcut(Scene.Arrow arrow) {
		keyboard.removeMapping(new KeyboardShortcut(arrow));
	}

	public void removeShortcut(Character key) {
		keyboard.removeMapping(new KeyboardShortcut(key));
	}

	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.mapping(new KeyboardShortcut(key));
	}
	
	public CameraKeyboardAction shortcut(char key, Scene.Modifier modifier) {
		return shortcut(MathUtils.getVKey(key), modifier);
	}

	public CameraKeyboardAction shortcut(Integer vKey, Scene.Modifier modifier) {
		return keyboard.mapping(new KeyboardShortcut(vKey, modifier));
	}

	public CameraKeyboardAction shortcut(Scene.Arrow arrow) {
		return keyboard.mapping(new KeyboardShortcut(arrow));
	}

	public boolean isKeyInUse(KeyboardShortcut key) {
		return keyboard.isShortcutInUse(key);
	}

	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionMapped(action);
	}

	// camera wrappers:
	public void removeAllCameraShortcuts() {
		cameraActions.removeAllMappings();
	}

	public boolean isCameraKeyInUse(Shortcut<Scene.Button> key) {
		return cameraActions.isShortcutInUse(key);
	}

	public boolean isActionBindedToCamera(Scene.MouseAction action) {
		return cameraActions.isActionMapped(action);
	}

	public void setCameraShortcut(Scene.Button button, Scene.Modifier modifier,
			Scene.MouseAction action) {
		cameraActions.setMapping(new Shortcut<Scene.Button>(button, modifier),
				action);
	}

	public void setCameraShortcut(Scene.Button button, Scene.MouseAction action) {
		cameraActions.setMapping(new Shortcut<Scene.Button>(button), action);
	}

	public void removeCameraShortcut(Scene.Button button, Scene.Modifier modifier) {
		cameraActions.removeMapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public void removeCameraShortcut(Scene.Button button) {
		cameraActions.removeMapping(new Shortcut<Scene.Button>(button));
	}

	public Scene.MouseAction cameraShortcut(Scene.Button button,
			Scene.Modifier modifier) {
		return cameraActions.mapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public Scene.MouseAction cameraShortcut(Scene.Button button) {
		return cameraActions.mapping(new Shortcut<Scene.Button>(button));
	}

	// iFrame wrappers:
	public void removeAllIFrameShortcuts() {
		iFrameActions.removeAllMappings();
	}

	public boolean isIFrameKeyInUse(Shortcut<Scene.Button> key) {
		return iFrameActions.isShortcutInUse(key);
	}

	public boolean isActionBindedToIFrame(Scene.MouseAction action) {
		return iFrameActions.isActionMapped(action);
	}

	public void setIFrameShortcut(Scene.Button button, Scene.Modifier modifier,
			Scene.MouseAction action) {
		iFrameActions.setMapping(new Shortcut<Scene.Button>(button, modifier),
				action);
	}

	public void setIFrameShortcut(Scene.Button button, Scene.MouseAction action) {
		iFrameActions.setMapping(new Shortcut<Scene.Button>(button), action);
	}

	public void removeIFrameShortcut(Scene.Button button, Scene.Modifier modifier) {
		iFrameActions.removeMapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public void removeIFrameShortcut(Scene.Button button) {
		iFrameActions.removeMapping(new Shortcut<Scene.Button>(button));
	}

	public Scene.MouseAction iFrameShortcut(Scene.Button button,
			Scene.Modifier modifier) {
		return iFrameActions.mapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public Scene.MouseAction iFrameShortcut(Scene.Button button) {
		return iFrameActions.mapping(new Shortcut<Scene.Button>(button));
	}
	
	// click wrappers:
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllMappings();
	}

	public boolean isClickKeyInUse(ClickShortcut key) {
		return clickActions.isShortcutInUse(key);
	}

	public boolean isClickActionBinded(Scene.ClickAction action) {
		return clickActions.isActionMapped(action);
	}

	public void setClickShortcut(Scene.Button button, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button), action);
	}

	public void setClickShortcut(Scene.Button button, Scene.Modifier modifier,
			Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, modifier), action);
	}

	public void setClickShortcut(Scene.Button button, Integer nc,
			Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, nc), action);
	}

	public void setClickShortcut(Scene.Button button, Scene.Modifier modifier,
			Integer nc, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(button, modifier, nc), action);
	}

	public void removeClickShortcut(Scene.Button button) {
		clickActions.removeMapping(new ClickShortcut(button));
	}

	public void removeClickShortcut(Scene.Button button, Scene.Modifier modifier) {
		clickActions.removeMapping(new ClickShortcut(button, modifier));
	}

	public void removeClickShortcut(Scene.Button button, Integer nc) {
		clickActions.removeMapping(new ClickShortcut(button, nc));
	}

	public void removeClickShortcut(Scene.Button button, Scene.Modifier modifier,
			Integer nc) {
		clickActions.removeMapping(new ClickShortcut(button, modifier, nc));
	}

	public Scene.ClickAction clickShortcut(Scene.Button button) {
		return clickActions.mapping(new ClickShortcut(button));
	}

	public Scene.ClickAction clickShortcut(Scene.Button button,
			Scene.Modifier modifier) {
		return clickActions.mapping(new ClickShortcut(button, modifier));
	}

	public Scene.ClickAction clickShortcut(Scene.Button button, Integer nc) {
		return clickActions.mapping(new ClickShortcut(button, nc));
	}

	public Scene.ClickAction clickShortcut(Scene.Button button,
			Scene.Modifier modifier, Integer nc) {
		return clickActions.mapping(new ClickShortcut(button, modifier, nc));
	}
	
	// click event
	protected void mouseClicked(MouseEvent e) {
		// 1. get button
		Button button = scene.dE.getButton(e);
		// 2. get modifier
		/**
		clickModifier = null;
		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown()) {
			if (e.isAltDown())
				clickModifier = Modifier.ALT;
			if (e.isAltGraphDown())
				clickModifier = Modifier.ALT_GRAPH;
			if (e.isControlDown())
				clickModifier = Modifier.CONTROL;
			if (e.isShiftDown())
				clickModifier = Modifier.SHIFT;
		}
		*/

		// 2. get number of clicks
		numberOfClicks = e.getClickCount();
		
		/**
		final int clickDelay = 200;		
		hasMouseDoubleClicked = false;
		if (e.getClickCount() == 2) {
			numberOfClicks = 2;
			PApplet.println( "  and it's a double click!");
			hasMouseDoubleClicked = true;
		} else {
			clickTimer = new Timer(clickDelay, new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (hasMouseDoubleClicked) {
						hasMouseDoubleClicked = false; // reset flag
					}
					else {
						numberOfClicks = 1;
						PApplet.println( "  and it's a simple click!");
					}
				}
			});
			clickTimer.setRepeats(false);
			clickTimer.start();
		}
		// */
		
		/**
		if (e.getClickCount() == 1 && !e.isConsumed()) {
			e.consume();
			numberOfClicks = 1;
			//handle double click.
		}
		*/
		/**
		if (e.getClickCount() == 2 && !e.isConsumed()) {
			e.consume();
			numberOfClicks = 2;
			//handle double click.
		}
		if (e.getClickCount() == 1 && !e.isConsumed() ) {
			e.consume();
			numberOfClicks = 1;
			//handle double click.
		}
		*/
		
		 /**		
		numberOfClicks = 0;
		hasMouseDoubleClicked = true;
		if (e.getClickCount() == 1)  {
			clickTimer = new java.util.Timer();
			clickTimer.schedule(new TimerTask() {
				public void run() {
					if (!hasMouseDoubleClicked) {
						numberOfClicks = 1;
						// Handle single-click
					}
					hasMouseDoubleClicked = false;
					clickTimer.cancel();
					}
				}, 175);
    }
    else if (e.getClickCount() == 2) {
    	numberOfClicks = 2;
    	hasMouseDoubleClicked = true;
    }
		// */	

		/**
		final int clickDelay=200; //delay in msec before processing events
	  if (e.getClickCount() == 1) {
	    clickTimer = new Timer(clickDelay, new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {	    		
	    		//do something for the single click
	    		PApplet.println("single click");
	    		numberOfClicks = 1;
	    		test(numberOfClicks);
	    		}
	    	});
	    clickTimer.setRepeats(false); //after expiring once, stop the timer
	    clickTimer.start();
	  }
	  else if (e.getClickCount() == 2) {
	    clickTimer.stop(); //the single click will not be processed
	    PApplet.println("double click");
	    //do something for the double click
	    numberOfClicks = 2;
	    test(numberOfClicks);
	  }
	  // */
	  // you can repeat this pattern for more clicks		
		
		// debug:
		//PApplet.println("number of clicks: " + numberOfClicks);
	  // /**
		ClickAction ca = null;
		
		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isShiftDown()) {
			if (e.isAltDown())
				ca = clickShortcut(button, Modifier.ALT, numberOfClicks);
			if (e.isAltGraphDown())
				ca = clickShortcut(button, Modifier.ALT_GRAPH, numberOfClicks);
			if (e.isControlDown())
				ca = clickShortcut(button, Modifier.CONTROL, numberOfClicks);
			if (e.isShiftDown())
				ca = clickShortcut(button, Modifier.SHIFT, numberOfClicks);
		}	
		
		if (ca == null)
			ca = clickShortcut(button, numberOfClicks);			

		if (ca == null)
			return;
		else {
			scene.handleClickAction(ca);
		}
		// */
	}
	
	/**
	private void test(int n) {
		ClickAction ca = null;		
		if(clickButton == null)
			return;
		
		if ( clickModifier != null ) 
				ca = clickShortcut(clickButton, clickModifier, n);
		
		if (ca == null)
			ca = clickShortcut(clickButton, n);			

		if (ca == null)
			return;
		else {
			handleClickAction(ca);
		}		
	}
	// */
	
	// wheel
	//Camera Wheel
	public void removeAllCameraWheelShortcuts() {
		cameraWheelActions.removeAllMappings();
	}

	public boolean isCameraWheelKeyInUse(Shortcut<Integer> key) {
		return cameraWheelActions.isShortcutInUse(key);
	}

	public boolean isWheelActionBindedToCamera(Scene.MouseAction action) {
		return cameraWheelActions.isActionMapped(action);
	}

	public void setCameraWheelShortcut(Scene.Modifier modifier,	Scene.MouseAction action) {
		cameraWheelActions.setMapping(new Shortcut<Integer>(WHEEL, modifier),
				action);
	}

	public void setCameraWheelShortcut(Scene.MouseAction action) {
		cameraWheelActions.setMapping(new Shortcut<Integer>(WHEEL), action);
	}

	public void removeCameraWheelShortcut(Scene.Modifier modifier) {
		cameraWheelActions.removeMapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public void removeCameraWheelShortcut() {
		cameraWheelActions.removeMapping(new Shortcut<Integer>(WHEEL));
	}

	public Scene.MouseAction cameraWheelShortcut(Scene.Modifier modifier) {
		return cameraWheelActions.mapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public Scene.MouseAction cameraWheelShortcut() {
		return cameraWheelActions.mapping(new Shortcut<Integer>(WHEEL));
	}
	
	protected MouseAction cameraWheelMouseAction(MouseWheelEvent e) {
		MouseAction wMouseAction = MouseAction.NO_MOUSE_ACTION;

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.ALT);
			if (e.isAltGraphDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.ALT_GRAPH);
			if (e.isControlDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.CONTROL);
			if (e.isShiftDown())
				wMouseAction = cameraWheelShortcut(Scene.Modifier.SHIFT);
			if (wMouseAction != null)
				return wMouseAction;
		}

		wMouseAction = cameraWheelShortcut();

		if (wMouseAction == null)
			wMouseAction = MouseAction.NO_MOUSE_ACTION;

		return wMouseAction;
	}
	
  //Frame Wheel
	public void removeAllFrameWheelShortcuts() {
		frameWheelActions.removeAllMappings();
	}

	public boolean isFrameWheelKeyInUse(Shortcut<Integer> key) {
		return frameWheelActions.isShortcutInUse(key);
	}

	public boolean isWheelActionBindedToFrame(Scene.MouseAction action) {
		return frameWheelActions.isActionMapped(action);
	}

	public void setFrameWheelShortcut(Scene.Modifier modifier,	Scene.MouseAction action) {
		frameWheelActions.setMapping(new Shortcut<Integer>(WHEEL, modifier),
				action);
	}

	public void setFrameWheelShortcut(Scene.MouseAction action) {
		frameWheelActions.setMapping(new Shortcut<Integer>(WHEEL), action);
	}

	public void removeFrameWheelShortcut(Scene.Modifier modifier) {
		frameWheelActions.removeMapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public void removeFrameWheelShortcut() {
		frameWheelActions.removeMapping(new Shortcut<Integer>(WHEEL));
	}

	public Scene.MouseAction frameWheelShortcut(Scene.Modifier modifier) {
		return frameWheelActions.mapping(new Shortcut<Integer>(WHEEL, modifier));
	}

	public Scene.MouseAction frameWheelShortcut() {
		return frameWheelActions.mapping(new Shortcut<Integer>(WHEEL));
	}
	
	protected MouseAction frameWheelMouseAction(MouseWheelEvent e) {
		MouseAction fMouseAction = MouseAction.NO_MOUSE_ACTION;

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.ALT);
			if (e.isAltGraphDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.ALT_GRAPH);
			if (e.isControlDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.CONTROL);
			if (e.isShiftDown())
				fMouseAction = frameWheelShortcut(Scene.Modifier.SHIFT);
			if (fMouseAction != null)
				return fMouseAction;
		}

		fMouseAction = frameWheelShortcut();

		if (fMouseAction == null)
			fMouseAction = MouseAction.NO_MOUSE_ACTION;

		return fMouseAction;
	}
}
