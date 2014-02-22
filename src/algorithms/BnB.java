package algorithms;

import java.util.ListIterator;

import model.Model;

public class BnB {

	/**
	 * 1 for maximization
	 * 0 for minimization
	 */
    private int type;
	private Model best;
	private Queue priorityQueue;
	
	public BnB(Model initial) {
		best=initial;
	}

	public void minimizes(){
		
		double bestSolution=Double.MAX_VALUE;
		priorityQueue= new Queue<Model>();
		priorityQueue.enqueue(best);
		while(!priorityQueue.isEmpty()){
			Model m= (Model)priorityQueue.dequeue();
			m.solveLP(0);
			double lowerbound=m.getFo();
			if(m.isInteger() && lowerbound<bestSolution){
				best=m;
				bestSolution=lowerbound;
			}
			else if(lowerbound<bestSolution){
				Model a= integerBranch(m)[0];
				priorityQueue.enqueue(a);
				Model b= integerBranch(m)[1];
				priorityQueue.enqueue(b);
			}
		}
	}
	

	public void maximizes(){
		double bestSolution=-Double.MAX_VALUE;
		priorityQueue= new Queue<Model>();
		priorityQueue.enqueue(best);
		best.setId("00");
		int depth=0;
		while(!priorityQueue.isEmpty()){
			Model m= (Model)priorityQueue.dequeue();
			System.out.println("Solving model "+m.getId());
			m.solveLP(1);
			double upperbound=m.getFo();
			if(m.isInteger() && upperbound>bestSolution){
				best=m;
				bestSolution=upperbound;
				System.out.println("Best Integer Solution "+bestSolution);
			}
			else if(upperbound>bestSolution){
				depth++;
				Model[] aa= integerBranch(m);
				Model a=aa[0];
				
				a.setId("d "+depth);
				System.out.println("Adding "+a.getId());
				priorityQueue.enqueue(a);	
				depth++;
				Model b=aa[1];
				b.setId("d: "+depth);
				priorityQueue.enqueue(b);
				System.out.println("Adding "+b.getId());
				
				
			}
		}
	}
	
	public Model[] integerBranch(Model m){
		Model[] branch= new Model[2];
		double[] x= m.getSimplex().primal();
		int b=0;
		for (int i = 0; i < x.length; i++) {
			//If solution is not integer branch
			if(!(Math.floor(x[i])==x[i])){ 
				b=i;
				break;
			}
		}
		
		double a=Math.floor(x[b]);
		System.out.println("Branching x_"+b+"="+a);
		double c= Math.ceil(x[b]);
		System.out.println("Branching x_"+b+"="+c);
		Model a1=new Model();
		Model a2=new Model();
		a1.setC(m.getC());
		a2.setC(m.getC());
		double[] b1=m.addEqualityConstraintb(b, a);
		double[] b2=m.addEqualityConstraintb(b, c);
		double[][] A1=m.addEqualityConstraintA(b, a);
		double[][] A2=m.addEqualityConstraintA(b, c);
		a1.setA(A1);
		a2.setA(A2);
		a1.setB(b1);
		a2.setB(b2);
		branch[0]=a1;
		branch[1]=a2;
		return branch;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
