package remixlab.util;

public interface Timable { 
	public void run(long period);
	public void runOnce(long period);
	public void cancel();
	public void create();
}
