package remixlab.proscene;

import remixlab.proscene.Scene.CameraKeyboardAction;

public abstract class CameraProfile {
	protected String name;
	protected Scene scene;
	protected Bindings<KeyboardShortcut, Scene.CameraKeyboardAction> keyboard;
	protected Bindings<Shortcut<Scene.Button>, Scene.ClickAction> clickActions;
	protected Bindings<Shortcut<Scene.Button>, Scene.MouseAction> mouseActions;
	
	public CameraProfile(Scene scn, String n) {
		scene = scn;
		name = n;
		keyboard = new Bindings<KeyboardShortcut, Scene.CameraKeyboardAction>(scene);
		clickActions = new Bindings<Shortcut<Scene.Button>, Scene.ClickAction>(scene);
		mouseActions = new Bindings<Shortcut<Scene.Button>, Scene.MouseAction>(scene);
	}
	
	public String name() {
		return name;
	}
	
	public boolean isRegistered() {
		return scene.isCameraProfileRegistered(this);
	}
	
	public boolean register() {
		return scene.registerCameraProfile(this, true);
	}
	
	public void unregister() {
		scene.unregisterCameraProfile(this);
	}
	
	public Bindings<KeyboardShortcut, Scene.CameraKeyboardAction> keyBindings() {
		return keyboard;
	}
	
	//keyboard wrappers:
	public void setShortcut(Integer vKey, Scene.Modifier modifier, CameraKeyboardAction action) {		
		keyboard.setBinding(new KeyboardShortcut(vKey, modifier), action);
	}
	
	public void setShortcut(Scene.Arrow arrow, CameraKeyboardAction action) {
		keyboard.setBinding(new KeyboardShortcut(arrow), action);
	}
	
	public void setShortcut(Character key, CameraKeyboardAction action) {
		keyboard.setBinding(new KeyboardShortcut(key), action);
	}
	
	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllBindings();
	}
	
	public void removeShortcut(Integer vKey, Scene.Modifier modifier) {
		keyboard.removeBinding(new KeyboardShortcut(vKey, modifier));
	}	
	
	public void removeShortcut(Scene.Arrow arrow) {
		keyboard.removeBinding(new KeyboardShortcut(arrow));
	}
	
	public void removeShortcut(Character key) {
		keyboard.removeBinding(new KeyboardShortcut(key));
	}
	
	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.binding(new KeyboardShortcut(key));
	}
	
	public CameraKeyboardAction shortcut(Integer vKey, Scene.Modifier modifier) {
		return keyboard.binding(new KeyboardShortcut(vKey, modifier));
	}
	
	public CameraKeyboardAction shortcut(Scene.Arrow arrow) {
		return keyboard.binding(new KeyboardShortcut(arrow));
	}
	
	public boolean isKeyInUse(KeyboardShortcut key) {
		return keyboard.isKeyInUse(key);
	}
	
	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionBinded(action);
	}
	
	//click wrappers:
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllBindings();
	}
	
	public boolean isClickKeyInUse(Shortcut<Scene.Button> key) {
		return clickActions.isKeyInUse(key);
	}
	
	public boolean isActionBinded(Scene.ClickAction action) {
		return clickActions.isActionBinded(action);
	}
	
	public void setShortcut(Scene.Button button, Scene.Modifier modifier, Scene.ClickAction action) {
		clickActions.setBinding(new Shortcut<Scene.Button>(button, modifier), action);
	}
	
	public void setShortcut(Scene.Button button, Scene.ClickAction action) {
		clickActions.setBinding(new Shortcut<Scene.Button>(button), action);
	}
	
	public void removeClickShortcut(Scene.Button button, Scene.Modifier modifier) {
		clickActions.removeBinding(new Shortcut<Scene.Button>(button, modifier));
	}
	
	public void removeClickShortcut(Scene.Button button) {
		clickActions.removeBinding(new Shortcut<Scene.Button>(button));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button, Scene.Modifier modifier) {
		return clickActions.binding(new Shortcut<Scene.Button>(button, modifier));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button) {
		return clickActions.binding(new Shortcut<Scene.Button>(button));
	}
	
	//mouse wrappers:
	// /**
	public void removeAllMouseActionShortcuts() {
		mouseActions.removeAllBindings();
	}
	
	public boolean isMouseKeyInUse(Shortcut<Scene.Button> key) {
		return mouseActions.isKeyInUse(key);
	}
	
	public boolean isActionBinded(Scene.MouseAction action) {
		return mouseActions.isActionBinded(action);
	}
	
	public void setShortcut(Scene.Button button, Scene.Modifier modifier, Scene.MouseAction action) {
		mouseActions.setBinding(new Shortcut<Scene.Button>(button, modifier), action);
	}
	
	public void setShortcut(Scene.Button button, Scene.MouseAction action) {
		mouseActions.setBinding(new Shortcut<Scene.Button>(button), action);
	}
	
	public void removeMouseShortcut(Scene.Button button, Scene.Modifier modifier) {
		mouseActions.removeBinding(new Shortcut<Scene.Button>(button, modifier));
	}
	
	public void removeMouseShortcut(Scene.Button button) {
		mouseActions.removeBinding(new Shortcut<Scene.Button>(button));
	}
	
	public Scene.MouseAction mouseShortcut(Scene.Button button, Scene.Modifier modifier) {
		return mouseActions.binding(new Shortcut<Scene.Button>(button, modifier));
	}
	
	public Scene.MouseAction mouseShortcut(Scene.Button button) {
		return mouseActions.binding(new Shortcut<Scene.Button>(button));
	}
}
