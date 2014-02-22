package model;

public class QBnBconstr {

	private String name;
	
	private QBnBLinExp le;
	
	private int type;
	
	private double rs;
	
	public QBnBconstr(QBnBLinExp le, int type, double rs) {
		this.le=le;
		this.type=type;
		this.rs=rs;
	}
	
    public QBnBconstr(QBnBLinExp le, int type, double rs, String name) {
    	this.le=le;
		this.type=type;
		this.rs=rs;
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public QBnBLinExp getLe() {
		return le;
	}

	public void setLe(QBnBLinExp le) {
		this.le = le;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getRs() {
		return rs;
	}

	public void setRs(double rs) {
		this.rs = rs;
	}
    
    

}
