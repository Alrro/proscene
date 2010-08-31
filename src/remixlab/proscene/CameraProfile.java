package remixlab.proscene;

public abstract class CameraProfile extends InteractionProfile {	
	public CameraProfile(Scene scn) {
		super(scn);
	}
	
	abstract public void setDefaultShortcuts();
}
