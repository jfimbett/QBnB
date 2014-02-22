/**
 * 
 */
package model;
import algorithms.*;
import java.util.ArrayList;

/**
 * @author jimbett
 *
 */
public class QBnBEnv {

	private Model m;
	/**
	 * 
	 */
	public QBnBEnv(Model m) {
		this.m=m;
	}
	
	public void maximize(){
		//Identifies the type of the problem
		if(isMIP()){
			System.out.println("Solving MP Problem");
			BnB bnb=new BnB(m);
			bnb.maximizes();
			
		}
		else{
			m.solveLP(1);
		}
		
	}
	
	public void minimize(){
		//Identifies the type of the problem
		if(isMIP()){
			
		}
		else{
			m.solveLP(0);
		}
		
	}
	
	public boolean isMIP(){
		boolean ismip=false;
		ArrayList<QBnBVariable> vars= m.getVariables();
		for (int i = 0; i < vars.size() && !ismip; i++) {
			if(vars.get(i).getType()==1)ismip=true;
		}
		return ismip;
	}

}
