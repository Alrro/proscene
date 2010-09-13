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

public class ArcballCameraProfile extends CameraProfile {
	public ArcballCameraProfile(Scene scn, String n) {
		super(scn, n);
	}
	
	@Override
	protected void setDefaultShortcuts() {		
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
		
		setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.CONTROL, Scene.MouseAction.ZOOM_ON_REGION );
		setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT, Scene.MouseAction.SCREEN_ROTATE );
		
		setShortcut('+', Scene.CameraKeyboardAction.INCREASE_ROTATION_SENSITIVITY);
		setShortcut('-', Scene.CameraKeyboardAction.DECREASE_ROTATION_SENSITIVITY);
		
		setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
		setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
	}
 }
