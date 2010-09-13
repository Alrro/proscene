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

public class FirstPersonCameraProfile extends CameraProfile {
	public FirstPersonCameraProfile(Scene scn, String n) {
		super(scn, n);
	}
	
	@Override
	protected void setDefaultShortcuts() {
		//setShortcut('a', CameraKeyboardAction.SHOW_ALL);
		setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD );
		setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND );
		setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD );
		setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT, Scene.MouseAction.ROLL );		
		setCameraShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT, Scene.MouseAction.DRIVE );
		setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
		setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
		setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);
		
		setShortcut('+', Scene.CameraKeyboardAction.INCREASE_CAMERA_FLY_SPEED);
		setShortcut('-', Scene.CameraKeyboardAction.DECREASE_CAMERA_FLY_SPEED);
		
		setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
		setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
	}
}
