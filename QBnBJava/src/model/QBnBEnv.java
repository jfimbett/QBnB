/**
 * 
 */
package model;
import algorithms.*;

import java.util.ArrayList;

/*************************************************************************
 *  
 *  Class: QBnBEnv.java
 *
 *  Represents the main optimization environment which will connect directly to the optimization algorithms
 *
 *  Author: Juan Imbett
 *  Email: jfimbett@gmail.com
 *
 *************************************************************************/
public class QBnBEnv {

	/**
	 * Optimization Model
	 */
	private Model m;
	
	private double bestSolution;
	
	/**
	 * Parameter that indicates if the solver will print results
	 */
	private boolean print;
	
	private boolean debugMode;
	
	
	/**
	 * Creates a new environment
	 * @param m: Main model
	 */
	public QBnBEnv(Model m) {
		this.m=m;
	}
	
	public void preSolve(){
		double init=System.currentTimeMillis();
		m.preSolve();
		m.update();
		System.out.println("Presolve time: "+(System.currentTimeMillis()-init)/1000+" seconds.");
	}
	/**
	 * Maximizes the main problem
	 */
	public void maximize(){
		
		//Identifies the type of the problem
		if(isMIP()){
			if(print){
				printInitial();
				int columns= m.getConstraints().size();
				int rows=m.getVariables().size();
				System.out.println("Solving problem with "+columns+" columns, and "+rows+" rows.");
				
			}
			preSolve();
			BnB bnb=new BnB(m, this);
			bnb.print(print);
			bnb.setDebugmode(debugMode);
			bnb.maximizes();
			
			
		}
		else{
			m.solveLP(1);
		}
		
	}
	
	/**
	 * Minimizes the main prices
	 */
	public void minimize(){
		//Identifies the type of the problem
		
		if(isMIP()){
			if(print){
				printInitial();
				int columns= m.getConstraints().size();
				int rows=m.getVariables().size();
				System.out.println("Solving problem with "+columns+" columns, and "+rows+" rows.");
				}
			   preSolve();
				BnB bnb=new BnB(m, this);
				bnb.print(print);
				bnb.minimizes();	
		}
		else{
			m.solveLP(0);
		}
		
	}
	
	public void printInitial(){
		System.out.println("************************************************************************************************");
		System.out.println("************************************* QBnB solver ***********************************************");
		System.out.println("************************************************************************************************");
	}
	
	/**
	 * Indicates if a problem is either a MIP or a LP problem
	 * @return
	 */
	public boolean isMIP(){
		boolean ismip=false;
		ArrayList<QBnBVariable> vars= m.getVariables();
		for (int i = 0; i < vars.size() && !ismip; i++) {
			if(vars.get(i).getType()==1 || vars.get(i).getType()==2 )ismip=true;
		}
		return ismip;
	}
	
	public void print(boolean sol){
		print=sol;
	}

	public Model getM() {
		return m;
	}

	public void setM(Model m) {
		this.m = m;
	}

	public double getBestSolution() {
		return bestSolution;
	}

	public void setBestSolution(double bestSolution) {
		this.bestSolution = bestSolution;
	}

	public boolean isPrint() {
		return print;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	

}
