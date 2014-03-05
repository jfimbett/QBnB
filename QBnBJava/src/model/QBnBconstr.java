package model;
/*************************************************************************
 *  
 *  Class: QBnBconstr.java
 *
 *  Represents a linear Constraint 
 *
 *  Author: Juan Imbett
 *  Email: jfimbett@gmail.com
 *
 *************************************************************************/
public class QBnBconstr {

	/**
	 * Name of the consdtraint (Helps identify the slack variable)
	 */
	private String name;
	
	/**
	 * Linear expresion contained in the constraint
	 */
	
	private QBnBLinExp le;
	
	/**
	 * Type of the constraint
	 * 0= Equality constraint
	 * 1  \sum a_i x_i<=b Constraint
	 * 2  \sum a_i x_i>=b Constraint
	 */
	
	private int type;
	
	/**
	 * Constant part of the constraint
	 */
	
	private double rs;
	
	/**
	 * Value of the slack in the optimal solution
	 */
	
	private double slack;
	
	/**
	 * Model that contains this constraint
	 */
	
	private Model model;
	
	

	/**
	 * Creates a new Linear Constraint
	 * @param le: Linear Expression
	 * @param type: Type of the constraint
	 * @param rs: Right Hand Side of the constraint
	 * @param name: Name of the constraint
	 * @param model: Model that contains the ocnstraint
	 */
    public QBnBconstr(QBnBLinExp le, int type, double rs, String name, Model model) {
    	//If constraint is not an equality constraint transforms it 
    	if(type!=0){
    		this.model=model;
    		this.type=0;
    		this.rs=rs;
    		this.name=name;
    		//Adds the slack Variable
    		QBnBVariable slack= new QBnBVariable(0, Double.MAX_VALUE, 0, "S_"+this.name);
    		slack.setSlack(true);
    		model.addVar(slack);
    		if(type==1){
    			le.addTerm(1, slack);
    		}
    		else{
    			le.addTerm(-1, slack);
    		}
    		
    		this.le=le;
    		
    	}
    	else{
    		this.le=le;
    		this.type=type;
    		this.rs=rs;
    		this.name=name;
    	}
    
	}

    /**************************************************************************************************
     **************************************************************************************************
     **************************************************************************************************
     ***************************************GETTERS AND SETTERS****************************************
     **************************************************************************************************
     **************************************************************************************************
     **************************************************************************************************/
    
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
