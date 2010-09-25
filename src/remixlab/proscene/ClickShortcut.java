package remixlab.proscene;

public class ClickShortcut {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((button == null) ? 0 : button.hashCode());
		result = prime * result + ((mask == null) ? 0 : mask.hashCode());
		result = prime * result
				+ ((numberOfClicks == null) ? 0 : numberOfClicks.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClickShortcut other = (ClickShortcut) obj;
		if (button == null) {
			if (other.button != null)
				return false;
		} else if (!button.equals(other.button))
			return false;
		if (mask == null) {
			if (other.mask != null)
				return false;
		} else if (!mask.equals(other.mask))
			return false;
		if (numberOfClicks == null) {
			if (other.numberOfClicks != null)
				return false;
		} else if (!numberOfClicks.equals(other.numberOfClicks))
			return false;
		return true;
	}
	public ClickShortcut(Scene.Button b) {
		this(0, b, 1);
	}
	public ClickShortcut(Integer m, Scene.Button b) {
		this(m, b, 1);
	}
	public ClickShortcut(Scene.Button b, Integer c) {
		this(0, b, c);
	}
	public ClickShortcut(Integer m, Scene.Button b, Integer c) {
		// TODO 0 < numberOfClicks < 3(?)
		this.mask = m;
		this.button = b;		
		this.numberOfClicks = c;
	}		
	public final Integer mask;
	public final Integer numberOfClicks;
	public final Scene.Button button;
}

/**
public class ClickShortcut {		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((mask == null) ? 0 : mask.hashCode());
		result = prime * result
				+ ((numberOfClicks == null) ? 0 : numberOfClicks.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClickShortcut other = (ClickShortcut) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (mask == null) {
			if (other.mask != null)
				return false;
		} else if (!mask.equals(other.mask))
			return false;
		if (numberOfClicks == null) {
			if (other.numberOfClicks != null)
				return false;
		} else if (!numberOfClicks.equals(other.numberOfClicks))
			return false;
		return true;
	}
	public ClickShortcut(Integer m) {
		this(m, 1);
	}
	public ClickShortcut(Integer m, Integer c) {
		// TODO 0 < numberOfClicks < 3(?)
		this.mask = m;
		this.numberOfClicks = c;
	}		
	public final Integer mask;
	public final Integer numberOfClicks;
	private CameraProfile getOuterType() {
		return CameraProfile.this;
	}
}
// */