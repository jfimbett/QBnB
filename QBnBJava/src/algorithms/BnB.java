package algorithms;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ListIterator;

import model.Model;
import model.QBnBEnv;
import model.QBnBVariable;
import model.QBnBconstr;

public class BnB {

	/**
	 * 1 for maximization
	 * 0 for minimization
	 */
    private int type;
	private Model best;
	private Queue priorityQueue;
	private double timeLoadingData;
	private boolean print;
	private double epsilon;
	private boolean cuts;
	private QBnBEnv e;
	private int nodesExplored;
	private boolean printSimplex;
	private boolean debugmode;
	
	public BnB(Model initial, QBnBEnv e) {
		this.e=e;
		best=initial;
		this.cuts=false;
		
	}

	public void printInfoFromModel(Model m){
		System.out.println("Solving Model: "+m.getId());
		m.getSimplex().printProblem();
	}
	public void minimizes(){
		nodesExplored=0;
		double initSol=System.currentTimeMillis();
		double bestSolution=Double.MAX_VALUE;
		priorityQueue= new Queue<Model>();
		priorityQueue.enqueue(best);
		best.setId("");
		int depth=0;
		while(!priorityQueue.isEmpty()){
			Model m= (Model)priorityQueue.dequeue();
			//m.printProblemToConsole();
			//System.out.println("Solving model "+m.getId());
			m.solveLP(0);
			double lowerbound=m.getSimplex().primalfo();
			nodesExplored++;
             if(m.getSimplex().getStatus()==3){
				//System.out.println("Prunning not feasible branch");
			}
           else if(m.isInteger() && lowerbound<bestSolution){
				best=m;
				bestSolution=lowerbound;
				if(print){
					System.out.println("Best Integer Solution "+bestSolution);
//					double dual=m.getSimplex().dualfo();
//					double gap=(upperbound-dual)/upperbound;
//					System.out.println("Gap "+gap*100+"%");
					System.out.println("Integer Solution found, prunning rest of the branch");
				}
				
			}
			
			else if(lowerbound<bestSolution){
			
				Model[] aa= integerBranch(m);
				Model a=aa[0];
				
				a.setId(m.getId()+"l");
				priorityQueue.enqueue(a);	
				
				Model b=aa[1];
				b.setId(m.getId()+"r");
				priorityQueue.enqueue(b);
				
				
				
			}
			else{
				//System.out.println("Prunning for bound");
			}
		}
		best.restoreSolutions();
		if(print){
		System.out.println("CPU time: "+(System.currentTimeMillis()-initSol)/1000+" seconds.");
		System.out.println(nodesExplored+" nodes explored.");
		e.setBestSolution(bestSolution);
//		System.out.println("Best Solution found ");
//		String[] names=best.getSimplex().getIb();
//		double[] values=best.getSimplex().getXb();
//		for (int i = 0; i < values.length; i++) {
//			if(names[i].charAt(0)!='S'){
//				System.out.println(names[i]+": "+values[i]);
//			}
//			
//		}
//		double dual=best.getSimplex().dualfo();
//		double u= best.getSimplex().primalfo();
//		double gap=(u-dual)/u;
		//System.out.println("Gap "+gap*100+"%");
		}
	}
	


	public void maximizes(){
		nodesExplored=0;
		double initSol=System.currentTimeMillis();
		double bestSolution=-Double.MAX_VALUE;
		priorityQueue= new Queue<Model>();
		priorityQueue.enqueue(best);
		best.setId("");
		int depth=0;
		while(!priorityQueue.isEmpty()){
			Model m= (Model)priorityQueue.dequeue();
			System.out.println("Node: "+nodesExplored);
			m.solveLP(1);
			double upperbound=-m.getSimplex().primalfo();
			if(debugmode)printInfoFromModel(m);		
			nodesExplored++;
             if(m.getSimplex().getStatus()==3){
				System.out.println("Prunning not feasible branch");
			}
           else if(m.isInteger() && upperbound>bestSolution){
				best=m;
				bestSolution=upperbound;
				if(print){
					System.out.println("Best Integer Solution "+bestSolution);
					System.out.println("Integer Solution found, prunning rest of the branch");
				}	
			}	
			else if(upperbound>bestSolution){		
				Model[] aa= integerBranch(m);
				Model a=aa[0];				
				a.setId(m.getId()+"l");
				priorityQueue.enqueue(a);			
				Model b=aa[1];
				b.setId(m.getId()+"r");
				priorityQueue.enqueue(b);	
			}
			else{
			
			}
		}
		best.restoreSolutions();
		if(print){
		System.out.println("CPU time: "+(System.currentTimeMillis()-initSol)/1000+" seconds.");
		System.out.println(nodesExplored+" nodes explored.");
		e.setBestSolution(bestSolution);
//		System.out.println("Best Solution found ");
//		String[] names=best.getSimplex().getIb();
//		double[] values=best.getSimplex().getXb();
//		for (int i = 0; i < values.length; i++) {
//			if(names[i].charAt(0)!='S'){
//				System.out.println(names[i]+": "+values[i]);
//			}
//			
//		}
//		double dual=best.getSimplex().dualfo();
//		double u= best.getSimplex().primalfo();
//		double gap=(u-dual)/u;
		//System.out.println("Gap "+gap*100+"%");
		}
	}
	
	public void printStatus(Model m){
		int status=m.getSimplex().getStatus();
		if(status==3){
			System.out.println("Problem unfeasible");
		}
		else if(status==1){
			System.out.println("Problem solve to optimality");
		}
		else{
			System.out.println("Problem unbounded");
		}
	}
	/**
	 * Given a precision parameter indicates if a double is close enough to an integer
	 * @param i: number to analyze
	 * @return true: If number can be assumed to be integer
	 * false: otherwise
	 */
	public boolean doubleIsInteger(double i){	
		epsilon=0.00001;
		if(i+epsilon>=Math.ceil(i) || i-epsilon <= Math.floor(i)) return true;
		else return false;
	}
	
	public Model[] integerBranch(Model m){
		Model[] branch= new Model[2];
		double[] x= m.getSimplex().getXb();
		String[] Ib=m.getSimplex().getIb();
		String b="";
		int k=0;
		for (int i = 0; i < x.length; i++) {
			//If solution is not integer
			if(!(doubleIsInteger(x[i]) )&&m.isInt(Ib[i])){ 
				b=Ib[i];
				k=i;
				break;
			}
		}
		//System.out.println("Branching "+b+" : "+x[k]);
		double a=Math.floor(x[k]);
		double c= Math.ceil(x[k]);
		//System.out.println(Ib[k]+ "<="+a+","+Ib[k]+">="+c);
		double[] cost=m.getC();
		String[] II=m.getI();
		double[] rhs= m.getB();
		Model a1=new Model();
		Model a2=new Model();
		ArrayList<QBnBconstr> c1=m.getConstraints();
		ArrayList<QBnBconstr> c2=(ArrayList<QBnBconstr>) c1.clone();
		a1.setConstraints(c1);
		
		ArrayList<QBnBVariable> v1=m.getVariables();
		a1.setVariables(v1);
		
		double[] cc1=m.getC();
		double[] cc2=cc1.clone();
		a1.setC(cc1);
		a2.setC(cc2);
		a2.setConstraints(c2);
		
		//Adds new bounds
		double[] Ub1=m.getU();		
		double[] Ub2=Ub1.clone();	
		double[] Lb1=m.getL();
		double[] Lb2=Lb1.clone();
		changeBound(Ib[k], a, Ub1, a1.varNames());
		changeBound(Ib[k], c, Lb2, a1.varNames());
		a1.setU(Ub1);
		a1.setL(Lb1);
		a2.setU(Ub2);
		a2.setL(Lb2);
		double[][] A=m.getA();
		double[][] A2=A.clone();
		a1.setA(A);
		a2.setA(A2);
		double[] bb= m.getB();
		double[] bb1=bb.clone();
		a1.setB(bb);
		a2.setB(bb1);
		//Adds cuts for variable x_j \notin Integers
		if(cuts){
			//Finds the j row in matrix B^-1N
			SimplexAlgorithm current= m.getSimplex();
			double[][] B=current.getBasis();
			double[][] N=current.getN();
			double[][] Gamma=current.Multiply(current.inverse(B), N);
			double[] r=floor(Gamma[k]);
			double rhs1=a;
			a1.generateCut(r, current.getIn(), rhs1);
			a2.generateCut(r, current.getIn(), rhs1);
		}
		
		branch[0]=a1;
		branch[1]=a2;
		a1.setPrint(printSimplex);
		a2.setPrint(printSimplex);
		return branch;
	}
	
	public void changeBound(String varName, double value, double[] bounds, String[] varNames){
		for (int i = 0; i < varNames.length; i++) {
			if(varNames[i].equals(varName)){
				bounds[i]=value;
				break;
			}
		}
	}
	public double[] floor(double[] original){
		double[] copy= new double[original.length];
		for (int i = 0; i < original.length; i++) {
			copy[i]=Math.floor(original[i]);
		}
		return copy;
		
	}
	public void printMatrixToConsole(double[][] C){
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				System.out.print(C[i][j]+" ");
			}
			System.out.println();
		}
	}
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
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Model getBest() {
		return best;
	}

	public void setBest(Model best) {
		this.best = best;
	}

	public Queue getPriorityQueue() {
		return priorityQueue;
	}

	public void setPriorityQueue(Queue priorityQueue) {
		this.priorityQueue = priorityQueue;
	}

	public double getTimeLoadingData() {
		return timeLoadingData;
	}

	public void setTimeLoadingData(double timeLoadingData) {
		this.timeLoadingData = timeLoadingData;
	}
	public boolean isPrinting() {
		return print;
	}

	public void print(boolean print) {
		this.print = print;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean isPrint() {
		return print;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public boolean isCuts() {
		return cuts;
	}

	public void setCuts(boolean cuts) {
		this.cuts = cuts;
	}

	public QBnBEnv getE() {
		return e;
	}

	public void setE(QBnBEnv e) {
		this.e = e;
	}

	public int getNodesExplored() {
		return nodesExplored;
	}

	public void setNodesExplored(int nodesExplored) {
		this.nodesExplored = nodesExplored;
	}

	public boolean isPrintSimplex() {
		return printSimplex;
	}

	public void setPrintSimplex(boolean printSimplex) {
		this.printSimplex = printSimplex;
	}

	public boolean isDebugmode() {
		return debugmode;
	}

	public void setDebugmode(boolean debugmode) {
		this.debugmode = debugmode;
	}


	
	

}
