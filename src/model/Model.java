package model;

import java.util.ArrayList;
import algorithms.*;

import algorithms.Simplex;

public class Model {
	
	private String id;
	
	private double[][] A;
	
	private double[] c;
	
	private double[] b;
	
	private double[] x;
	
	private boolean feasible;
	
	private double fo;
	
	private String type;
	
	private boolean integer;
	
	private ArrayList<QBnBconstr> constraints;
	
	private ArrayList<String> constraintsAdded;
	
	private ArrayList<QBnBVariable> variables;
	
	private QBnBLinExp ofle;

	private Simplex simplex;
	public Model() {
		constraints= new ArrayList<QBnBconstr>();
		variables= new ArrayList<QBnBVariable>();
		constraintsAdded= new ArrayList<String>();
	}

	public void solveLP(int type){
		if(type==1){
			simplex= new Simplex(A, b, c);
			simplex.print();
			this.setFo(simplex.value());
		}
		else{
			simplex= new Simplex(A, b, changeSign(c));
			simplex.print();
		}
		
	}

	
	
	public double[] changeSign(double[] cc){
		double[] a=new double[cc.length];
		for (int i = 0; i < a.length; i++) {
			a[i]=-cc[i];
		}
		return a;
		
	}

	public void update(){
		updateConstraints();
	}
	
	public void updateVariables(){
		
	}
	
	public ArrayList<String> variableNames(){
		ArrayList<String> s= new ArrayList<String>();
		for (int i = 0; i < constraints.size(); i++) {
			QBnBconstr con= constraints.get(i);
			QBnBLinExp le= con.getLe();
			ArrayList<String> names=le.getVarNames();
			for (int j = 0; j < names.size(); j++) {
				String n=names.get(j);
				if(!alreadyAdded(n, s)) s.add(n);
			}
		}
		return s;
	}
	
	public void addConst(QBnBconstr c){
	constraints.add(c);	
	}
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
	public void addObj(QBnBLinExp le){
		ofle=le;
		ArrayList<String> names=variableNames();
		System.out.println(variables.size());
		c=new double[variables.size()];
		for (int i = 0; i < c.length; i++) {
			c[i]=findCoeffFromNameOF(names.get(i));
		}
		
	}
	
	public void addVar(QBnBVariable v){
		variables.add(v);
	}
	public void printMatrixToConsole(double[][] C){
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				System.out.print(C[i][j]+" ");
			}
			System.out.println();
		}
	}
	public void updateConstraints(){
		ArrayList<String> names=variableNames();
		A=new double[numberOfLessThanEqualConstraints()][(int)variables.size()];
		b=new double[numberOfLessThanEqualConstraints()];
		int k=0;
		int i=0;
		while(k<constraints.size()){		
			QBnBconstr c= constraints.get(k);
			QBnBLinExp coeff=c.getLe();
			ArrayList<Double> coff=coeff.getCoeffs();
			ArrayList<String> n=coeff.getVarNames();
			
			if(c.getType()==0){
			//Adds two restrictions				
				for (int j = 0; j < names.size(); j++) {
					
					A[i][j]=findCoeffFromName(n.get(j), coeff);
					A[i+1][j]=-findCoeffFromName(names.get(j), coeff);;
					
				}
				b[i]=c.getRs();
				b[i+1]=-c.getRs();
				i+=2;
			} //<=
			else if(c.getType()==1){
				for (int j = 0; j < names.size(); j++) {
				
					A[i][j]=findCoeffFromName(names.get(j), coeff);
					
				}
				b[i]=c.getRs();
				i++;
			}
			else{
				for (int j = 0; j < names.size(); j++) {
					
					A[i][j]=-findCoeffFromName(names.get(j), coeff);
					
				}
				b[i]=-c.getRs();
				i++;
			}
			k++;
			
		}
		printMatrixToConsole(A);
	}
	
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

	
	public double[][] addEqualityConstraintA(int pos, double value){

		//Adds x<= value && -x<=-value
		double[][] nA= new double[A.length+2][A[0].length];
		for (int i = 0; i < nA.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				if(i<A.length){
				nA[i][j]=A[i][j];
				}
			}
		}
		nA[nA.length-2][pos]=1;
		nA[nA.length-1][pos]=-1;
		
		
		//printMatrixToConsole(A);
		return nA;
	}
	
	public double[] addEqualityConstraintb(int pos, double value){

		double[] nb= new double[b.length+2];
		for (int i = 0; i < nb.length; i++) {
			if(i<b.length){
				nb[i]=b[i];
			}
		}
		nb[nb.length-2]=value;
		nb[nb.length-1]=-value;
		
		
		//printMatrixToConsole(A);
		return nb;
	}
	
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

	public boolean isInteger() {
		boolean is=false;
		int integers=0;
		double[] sol=simplex.primal();
		for (int i = 0; i < sol.length; i++) {
			if(Math.floor(sol[i])==sol[i]){
				integers++;
			}
		}
		if(integers==sol.length){
			is=true;
		}
		return is;
		
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

	public ArrayList<String> getConstraintsAdded() {
		return constraintsAdded;
	}

	public void setConstraintsAdded(ArrayList<String> constraintsAdded) {
		this.constraintsAdded = constraintsAdded;
	}

	public ArrayList<QBnBVariable> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<QBnBVariable> variables) {
		this.variables = variables;
	}

	public Simplex getSimplex() {
		return simplex;
	}

	public void setSimplex(Simplex simplex) {
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
