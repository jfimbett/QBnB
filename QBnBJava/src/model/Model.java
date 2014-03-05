package model;

import java.util.ArrayList;

import javax.swing.tree.VariableHeightLayoutCache;

import algorithms.*;
/*************************************************************************
 *  
 *  Class: Model.java
 *
 *  Represents an Optimization Model, either Linear (LP) or Mixed Integer (MIP)
 *
 *  Author: Juan Imbett
 *  Email: jfimbett@gmail.com
 *
 *************************************************************************/



public class Model {
	
	/**
	 * Vector of upper bounds
	 */
	private double[] U;
	
	/**
	 * Vector of Lower bounds
	 */
	
	private double[] L;
	
	/**
	 * Id of the model
	 */
	private String id;
	
	/**
	 * Matrix of the LP in standard form. 
	 * 
	 * min (max) c*x
	 * s.t.
	 * Ax=b
	 */
	
	
	private double[][] A;
	
	/**
	 * Cost vector
	 */
	private double[] c;
	
	/**
	 * RHS
	 */
	
	private double[] b;
	
	private boolean debugMode;
	
	/**
	 * Solution
	 */
	
	private double[] x;
	
	/**
	 * Problem is feasible: true
	 * Problem is not feasible: false
	 */
	
	private boolean feasible;
	
	/**
	 * Value of c*x at the optimum
	 */
	
	private double fo;
	
	/**
	 * Type of problem
	 * 0 -> Minimization
	 * 1 -> Maximization
	 */
	
	private String type;
	
	/**
	 * Vector of variable names (useful to separate basic and non basic variables during simplex
	 */
	
	private String[] I;
	
	/**
	 * True: Problem is MIP
	 * False: Otherwise
	 */
	
	private boolean integer;
	
	/**
	 * Array of linear constraints
	 */
	
	private ArrayList<QBnBconstr> constraints;
	
	/**
	 * Array of variables
	 */
	
	private ArrayList<QBnBVariable> variables;
	
	/**
	 * Linear expression that holds the information of the Objective Function
	 */
	private QBnBLinExp ofle;
	
	/**
	 * Precision parameter
	 */
	
	private double epsilon;
	

	/**
	 * Simplex Algorithm for solving LP and Relaxed MIP Problems
	 */
	private SimplexAlgorithm simplex;
	
	private boolean print;
	/**
	 * Initializes an empty model
	 */
	public Model() {
		constraints= new ArrayList<QBnBconstr>();
		variables= new ArrayList<QBnBVariable>();
		epsilon=0.000001;
		
	}

	
	/**
	 * Initializes a new model from the following format:
	 * min (max) c*x
	 * s.t.
	 * Ax <= (>=) b
	 * @param AA Matrix of coefficients
	 * @side 1 if <, 2 if >
	 */
	public void generateStandardFormat(double[][] AA, int side){
		
	}
	
	/**
	 * Prints message to console with out breakline
	 * @param m: message to be printed
	 */
	public void p(String m){
		System.out.print(m);
	}
	
	/**
	 * Prints message to console with a break line
	 * @param m
	 */
	public void pl(String m){
		System.out.println(m);
	}
	
	/**
	 * Prints a break line
	 */
	public void pl(){
		System.out.println();
	}
	
	/**
	 * Prints the current LP, or MIP Model to console
	 * 
	 */
	public void printProblemToConsole(){
		
	ArrayList<String> namesof=new ArrayList<String>();
	ArrayList<Double> coeffof=new ArrayList<Double>();

		for (int i = 0; i < constraints.size(); i++) {
			QBnBconstr c=constraints.get(i);
			QBnBLinExp le =c.getLe();
			namesof= le.getVarNames();
		    coeffof=le.getCoeffs();
		    for (int j = 0; j < namesof.size(); j++) {
		    	double d=coeffof.get(j);
		    	if(d<0){
					p(d+""+namesof.get(j));
				}
				else{
					p("+"+d+""+namesof.get(j));
				}
		    	
			}
		    if(c.getType()==0){
		    	p("="+c.getRs());
		    }
		    else if(c.getType()==1){
		    	p("<="+c.getRs());
		    }
		    else{
		    	p(">="+c.getRs());
		    }
		    pl();
		}
		
	}
	 public boolean isSparse(){
		 boolean sparse=false;
		 int count=0;
		 for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				if(A[i][j]!=0){
					count++;
				}
			}
		}
		 if(count/(A.length*A[0].length)<0.3){
			 sparse=true;
		 }
		 return sparse;
	 }
	/**
	 * Solve LP Problem
	 * @param type: 1 If Maximization, 0 if Minimization
	 */
	public void solveLP(int type){
		
		if(type==1){
			
			if(isSparse()){
				System.out.println("Matrix is sparse");
			}
			ArrayList<String> v= variableNames();
			I= new String[v.size()];
			v.toArray(I);
			double[] d=changeSign(c);
			simplex=new SimplexAlgorithm(A, b, d , I, U, L, print);
			this.setFo(simplex.primalfo());
			int status=simplex.getStatus();
			if(status==3){
				this.feasible=false;
			}
			
		}
		else{
			I=varNames();
			simplex=new SimplexAlgorithm(A, b, c, I, U, L, print);
			this.setFo(simplex.primalfo());
			int status=simplex.getStatus();
			if(status==3){
				this.feasible=false;
			}
		}
		
	}

	/**
	 * Returns the names of the variables
	 * @return A vector with the different name of variables
	 */
	public String[] varNames(){
		String[] names= new String[variableNames().size()];
		for (int i = 0; i < names.length; i++) {
			names[i]=variableNames().get(i);
			}
		return names;
	}
	
	/**
	 * Changes the sign of each element in a vector
	 * @param cc: The original vector
	 * @return -cc
	 */
	public double[] changeSign(double[] cc){
		double[] a=new double[cc.length];
		for (int i = 0; i < a.length; i++) {
			a[i]=-cc[i];
		}
		return a;
		
	}

 /**
  * Update variables after adding the constraints. 
  */
	public void update(){
		
		updateConstraintsE();
		updateBounds();
		
	}
	
	public void changeLBoundToVariable(String varName, double nlb){
		QBnBVariable v= findVarByName(varName);
		v.setLb(nlb);
	}
	

	public void changeUBoundToVariable(String varName, double nub){
		QBnBVariable v= findVarByName(varName);
		v.setUb(nub);
	}
	public void updateBounds(){
		int n=variables.size();
		U= new double[n];
		L= new double[n];
		
		for (int i = 0; i < n; i++) {
			QBnBVariable v= variables.get(i);
			U[i]=v.getUb();
			L[i]=v.getLb();
		}
		
	}
	
	/**
	 * Restore the solutions to every variable in the model. 
	 */
	public void restoreSolutions(){
		//Values for variables and restrictions
		double[] xb=this.getSimplex().getXb();
		String[] Ib= this.getSimplex().getIb();
		for (int i = 0; i < xb.length; i++) {
			String name=Ib[i];
				int pos=posVarByName(name);
				if(pos!=-1){
					variables.get(pos).setValue(xb[i]);
				}
				
		}	
		
		double[] xn=this.getSimplex().getXn();
		String[] In= this.getSimplex().getIn();
		for (int i = 0; i < xn.length; i++) {
			String name=In[i];
				int pos=posVarByName(name);
				if(pos!=-1){
					variables.get(pos).setValue(xn[i]);
				}
				
		}	
		
		fo=this.simplex.primalfo();
	}
	

	/**
	 * Returns the position of a variable in the variables using its name
	 * @param name, name of the variable to search
	 * @return k, the position in the array where the variable is located
	 */
	public int posVarByName(String name){
		int k=-1;
		for (int i = 0; i < variables.size(); i++) {
			QBnBVariable v= variables.get(i);
			if(v.getName().equals(name)){
				k=i;
				break;
			}
		}
		return k;
	}

	
	/**
	 * Returns an Array with names of the variables in the model
	 * @return s, the Array with the variables
	 */
	public ArrayList<String> variableNames(){
		ArrayList<String> s= new ArrayList<String>();
		for (int i = 0; i < constraints.size(); i++) {
			QBnBconstr con= constraints.get(i);
			QBnBLinExp le= con.getLe();
			ArrayList<String> names=le.getVarNames();
			for (int j = 0; j < names.size(); j++) {
				String n=names.get(j);
				if(!s.contains(n)) s.add(n);
			}
		}
		return s;
	}
	
	/**
	 * Generates a cut from the restriction generated by variables with name vars, coefficients coeff and right hand side rhs. 
	 * @param coeff: Vector with the coefficients of the new restriction
	 * @param vars: Name of the variables that belong to the cut
	 * @param rhs: Right side of the cut
	 */
	public void generateCut(double[] coeff, String[] vars, double rhs){
		QBnBLinExp le= new QBnBLinExp();
		for (int i = 0; i < vars.length; i++) {
			le.addTerm(coeff[i], findVarByName(vars[i]));
		}
		QBnBconstr c= new QBnBconstr(le, 2, rhs, "Cut", this);
		this.addConst(c);
		this.update();
	}
	
	/**
	 * Adds a new constraint to the problem
	 * @param c: Constraint to add
	 */
	public void addConst(QBnBconstr c){
	constraints.add(c);	
	}
	
	/**
	 * Finds the coefficient of a variable in a LinearExpresion
	 * @param name: The name of the variable which coefficient we want
	 * @param le: Linear Expression to search in
	 * @return coeff: The coefficient of the variable in the Linear Expresion
	 */
	public double findCoeffFromName(String name, QBnBLinExp le){
		double coeff=0;
		ArrayList<Double> values=le.getCoeffs();
		ArrayList<String> names= le.getVarNames();
		for (int i = 0; i < values.size(); i++) {
			if(names.get(i).equalsIgnoreCase(name)){
				coeff=values.get(i);
				break;
			}
		}
		return coeff;
	}
	
	/**
	 * Indicates if a string is already added to an ArrayList of Strings
	 * @param ss: The String to find
	 * @param s: The ArrayList ot find in
	 * @return true if ss \in S, false otherwise
	 */
	public boolean alreadyAdded(String ss, ArrayList<String> s){
		boolean answer=false;
		for (int i = 0; i < s.size(); i++) {
			if(ss.equalsIgnoreCase(s.get(i))){
				answer=true;
				break;
			}
		}
		return answer;
	}
	
	/**
	 * Finds the coefficient of a variable in the Linear Expression of the Objective Function
	 * @param name: The name of the variable which coefficient we want
	 * @param le: Linear Expression to search in
	 * @return coeff: The coefficient of the variable in the Objective Function
	 */
	public double findCoeffFromNameOF(String name){
		double val=0;
		ArrayList<String> names=ofle.getVarNames();
		ArrayList<Double> vals=ofle.getCoeffs();
		for (int i = 0; i < names.size(); i++) {
			if(names.get(i).equals(name)){
				val=vals.get(i);
				break;
			}
		}
		return val;
	}
	
	/**
	 * Adds a new Objective Function
	 * @param le: Linear Expression that Represents the Objective Function
	 */
	public void addObj(QBnBLinExp le){
		ofle=le;
		ArrayList<String> names=variableNames();
		
		c=new double[variables.size()];
		for (int i = 0; i < c.length; i++) {
			c[i]=findCoeffFromNameOF(names.get(i));
		}
		
	}
	
	/**
	 * Adds new variable to the problem
	 * @param v: Variable to be added
	 */
	
	public void addVar(QBnBVariable v){
		variables.add(v);
		
	}
	
	/**
	 * Prints matrix To console
	 * @param C: Matrix to be printed
	 */
	public void printMatrixToConsole(double[][] C){
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				System.out.print(C[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Updates constraints making each one an equality constraint.
	 */
	public void updateConstraintsE(){
		ArrayList<String> names=variableNames();
		A=new double[constraints.size()][variables.size()];
		b=new double[constraints.size()];
		int k=0;
		while(k<constraints.size()){		
			QBnBconstr c= constraints.get(k);
			QBnBLinExp coeff=c.getLe();
			ArrayList<Double> coff=coeff.getCoeffs();
			ArrayList<String> n=coeff.getVarNames();
			if(c.getType()==0){	
				for (int j = 0; j < names.size(); j++) {
						A[k][j]=findCoeffFromName(names.get(j), coeff);				
				}
				b[k]=c.getRs();
			} 
			k++;
			
		}
		this.A=A;
		//printMatrixToConsole(A);
	}
	
	
	/**
	 * Returns the number of <= constraints in the problem
	 * @return num: the number of <= constraints in the problem
	 */
	public int numberOfLessThanEqualConstraints(){
		int num=0;
		for (int i = 0; i < constraints.size(); i++) {
			//Equality constraint
			if(constraints.get(i).getType()==0){
				num+=2;
			}
			else{
				num++;
			}
		}
		return num;
	}
	
	/**
	 * Returns the number of slack variables in the problem
	 * @return num: The number of slack variables in the problem
	 */
	public int numberOfSlackVariables(){
		int num=0;
		for (int i = 0; i < constraints.size(); i++) {
			//Equality constraint
			if(constraints.get(i).getType()==0){
				
			}
			else{
				num++;
			}
		}
		return num;
	}


	/**
	 * Creates a bound by adding the constraint x<=bound or x>= bound
	 * This method is used in Branch and Bounding for adding constraints to the initial problem
	 * @param varname: Name of the variable to bound
	 * @param value: Name of the bound
	 * @param sign: Side of the restriction <= -> 1, >= -> 2
	 * @return
	 */
	public double[][] addEqualityConstraintA(String varname, double value, boolean sign){
		//<=
		A=null;
		if(sign){
			QBnBVariable var= findVarByName(varname);
			c=addToVector(0, c);
		   
			QBnBLinExp le= new QBnBLinExp();
			le.addTerm(1, var);
			QBnBconstr c= new QBnBconstr(le, 1, 1, "B"+varname+"<="+value, this);
			this.addConst(c);
			
			update();
		}
		else{
			QBnBVariable var= findVarByName(varname);
			c=addToVector(0, c);
		   
			QBnBLinExp le= new QBnBLinExp();
			le.addTerm(1, var);
			QBnBconstr c= new QBnBconstr(le, 2, 1, "B"+varname+"<="+value, this);
			this.addConst(c);
			
			update();
		}
	
	return this.A;
		
	}
	
	/**
	 * Adds value after the last position of a vector
	 * @param val: Value to be added
	 * @param vector: Original vector
	 * @return newVector: Original vector plus the value added. 
	 */
	public double[] addToVector(double val, double[] vector){
		double[] newVector=new double[vector.length+1];
		for (int i = 0; i < newVector.length; i++) {
			if(i<vector.length){
				newVector[i]=vector[i];
			}
			else{
				newVector[i]=val;
			}
		}
		return newVector;
	}
	
	/**
	 * Returns the variable with name "name"
	 * @param name, the name of the variable to find
	 * @return the variable with name "name"
	 */
	public QBnBVariable findVarByName(String name){
		
		for (int i = 0; i < variables.size(); i++) {
			QBnBVariable var= variables.get(i);
			if(variables.get(i).getName().equals(name)) return var;
		}
		return null;
	}
	
	/**
	 * Returns the position of a variable using its name
	 * @param name: The name of the variable
	 * @return pos= Position of the variable
	 */
	public int findByName(String name){
		int pos=0;
		for (int i = 0; i < I.length; i++) {
			String n=I[i];
			if(name.equalsIgnoreCase(n)){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	/**
	 * Indicates if the problem is a MIP
	 * @return true if problem is MIP
	 * false otherwise
	 */
	public boolean isInteger() {
		boolean is=true;
		double[] sol=simplex.getXb();
		String[] names=simplex.getIb();
		for (int i = 0; i < sol.length; i++) {
			if(isInt(names[i])){
				if(!doubleIsInteger(sol[i])){
					is=false;
					break;
				}	
			}
		}
		
		 sol=simplex.getXn();
		 names=simplex.getIn();
		for (int i = 0; i < sol.length; i++) {
			if(isInt(names[i])){
				if(!doubleIsInteger(sol[i])){
					is=false;
					break;
				}	
			}
		}
		
		return is;
		
	}
	
	/**
	 * Given a precision parameter indicates if a double is close enough to an integer
	 * @param i: number to analyze
	 * @return true: If number can be assumed to be integer
	 * false: otherwise
	 */
	public boolean doubleIsInteger(double i){	
		if(i+epsilon>=Math.ceil(i) || i-epsilon <= Math.floor(i)) return true;
		else return false;
	}
	
	/**
	 * Indicates if a variable with name "name" is integer or binary
	 * @param name
	 * @return
	 */
	
	public boolean isInt(String name){
		boolean f=false;
		for (int i = 0; i < variables.size(); i++) {
			QBnBVariable v=variables.get(i);
			if(v.getName().equalsIgnoreCase(name)){
				if(v.getType()==1 ||v.getType()==2 ){
					f=true;
				}
				break;
			}
		}
		return f;
	}
	
	/**
	 * Count the number of integer or binary variables in a model
	 * @return
	 */
	public int countIntegers(){
		int integer=0;
		for (int i = 0; i < variables.size(); i++) {
			if(variables.get(i).getType()==1){
				integer++;
			}
		}
		return integer;
	}
	
	
	public void preSolve(){
		
		preSolveBoundsOnIntegers();
		preSolveEmptyRow();
		
	}
	
	/**
	 * Eliminates Rows that are 0, if bi is different from 0 then the problem is unfeasible
	 */
	public boolean preSolveEmptyRow(){
		int rowsRemoved=0;
		boolean feasible=true;
		for (int i = 0; i < constraints.size(); i++) {
			QBnBconstr c= constraints.get(i);
			QBnBLinExp l= c.getLe();
			ArrayList<Double> coeff=l.getCoeffs();
			boolean empty=true;
			for (int j = 0; j < coeff.size(); j++) {
				if(coeff.get(j)!=0){
					empty=false;
					break;
				}
			}
			
			if(empty){
				//Checks if bi==0
				if(b[i]==0){
					//Removes row
					if(constraints.remove(c)){
						rowsRemoved++;
					}
				}
				else{
					feasible=false;
				}
			}
		}
		return feasible;
	}
	
	public boolean preSolveBoundsOnIntegers(){
		
		for (int i = 0; i < variables.size(); i++) {
			if(variables.get(i).getType()==1){
				//If Lb \notin N them Lb=ceil(Lb)
				variables.get(i).setLb(Math.ceil(variables.get(i).getLb()));
				//If Ub \notin N them Ub=floor(Ub)
				variables.get(i).setUb(Math.floor(variables.get(i).getUb()));
			}
			else if(variables.get(i).getType()==2){
				//If Lb \notin N them Lb=ceil(Lb)
				if(variables.get(i).getLb()<0){
					variables.get(i).setLb(0);
				}
				
				if(variables.get(i).getUb()>1){
					variables.get(i).setUb(1);
				}
				
				
			}
		}
		return true;
	}
	
	/************************************************************************************************************************************/
	/************************************************************************************************************************************/
	/****************************************************** GETTERS AND SETTERS *********************************************************/
	/************************************************************************************************************************************/
	/************************************************************************************************************************************/
	
	
	public double[][] getA() {
		return A;
	}

	public void setA(double[][] a) {
		A = a;
	}

	public double[] getC() {
		return c;
	}

	public void setC(double[] c) {
		this.c = c;
	}

	public double[] getB() {
		return b;
	}

	public void setB(double[] b) {
		this.b = b;
	}

	public boolean isFeasible() {
		return feasible;
	}

	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}

	public double getFo() {
		return fo;
	}

	public void setFo(double fo) {
		this.fo = fo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

	public void setInteger(boolean integer) {
		this.integer = integer;
	}
	

	public double[] getX() {
		return x;
	}

	public void setX(double[] x) {
		this.x = x;
	}
	
	

	public ArrayList<QBnBconstr> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<QBnBconstr> constraints) {
		this.constraints = constraints;
	}

	public ArrayList<QBnBVariable> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<QBnBVariable> variables) {
		this.variables = variables;
	}

	public SimplexAlgorithm getSimplex() {
		return simplex;
	}

	public void setSimplex(SimplexAlgorithm simplex) {
		this.simplex = simplex;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public QBnBLinExp getOfle() {
		return ofle;
	}

	public void setOfle(QBnBLinExp ofle) {
		this.ofle = ofle;
	}
	
	

	public String[] getI() {
		return I;
	}

	public void setI(String[] i) {
		I = i;
	}

	public static void main(String[] args) {
		

	}


	public double[] getU() {
		return U;
	}


	public void setU(double[] u) {
		U = u;
	}


	public double[] getL() {
		return L;
	}


	public void setL(double[] l) {
		L = l;
	}


	public double getEpsilon() {
		return epsilon;
	}


	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
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
