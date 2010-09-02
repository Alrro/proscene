package remixlab.proscene;

public abstract class CameraProfile extends KeyboardProfile {	
	public CameraProfile(Scene scn) {
		super(scn);
	}
	
	abstract public void setDefaultShortcuts();
}
