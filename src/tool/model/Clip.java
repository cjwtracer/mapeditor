package tool.model;

import java.io.Serializable;

public class Clip implements Serializable{
	private static final long serialVersionUID = -6874239080839623489L;
	
	boolean locked;
	/**
	 * relative to the anmation
	 */
	int x, y;
	String resource;

	public String getResource() {
		return resource;
	}

	public boolean locked() {
		return locked;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setResource(String res) {
		resource = res;
	}

}
