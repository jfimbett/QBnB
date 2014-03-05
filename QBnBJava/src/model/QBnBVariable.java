/**
 * 
 */
package model;

/**
 * @author jimbett
 *
 */
public class QBnBVariable {
	
	private String name;
	
	private double lb;
	
	private double ub;
	
	private int type;
	
	private double value;
	
	private boolean slack;

	/**
	 * 
	 */
	public QBnBVariable(double lb, double ub, int type, String name) {
		this.setSlack(false);
		this.name=name;
		this.lb=lb;
		this.ub=ub;
		this.type=type;
	}
	
	public QBnBVariable(double lb, double ub, int type) {
		this.setSlack(false);
		this.name=name;
		this.lb=lb;
		this.ub=ub;
		this.type=type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLb() {
		return lb;
	}

	public void setLb(double lb) {
		this.lb = lb;
	}

	public double getUb() {
		return ub;
	}

	public void setUb(double ub) {
		this.ub = ub;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isSlack() {
		return slack;
	}

	public void setSlack(boolean slack) {
		this.slack = slack;
	}
	
	

}
