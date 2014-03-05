package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import model.Model;
import model.QBnBEnv;
import model.QBnBLinExp;
import model.QBnBVariable;
import model.QBnBconstr;

public class Test {

	private QBnBVariable[][] I;
	
	private QBnBVariable[][] Q;
	
	private QBnBVariable[][] Y;
	
	private QBnBconstr[][] inventory;
	
	private QBnBconstr[] invcapacity;
	
	private QBnBconstr[] production;
	
	private QBnBconstr[][] productionRel;
	
	private double ub;
	
	private double[][] D;
	
	private double[] inInv;
	
	private double K;
	
	private double[] N;
	
	private double[] M;
	
	private double[] R;
	
	private int psize;
	
	private int tsize;
	
	private double[] V;
	
	public Test() {
	/*	try {
			loadFromFile("./data/input3.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Model m = new Model();
		createVariables(m);
		createConstraints(m);
		createFo(m);
		QBnBEnv env = new QBnBEnv(m);
		env.print(true);
		env.maximize();
		System.out.println("Q: ");
		printSolution(Q);
		System.out.println("Y: ");
		printSolution(Y);
		System.out.println("I: ");
		printSolution(I);*/
	}

	public void printSolution(QBnBVariable[][] sol){
		for (int i = 0; i < sol.length; i++) {
			for (int j = 0; j < sol[0].length; j++) {
				System.out.print(sol[i][j].getValue()+" ");
			}
			System.out.println();
		}
	}
		
		public void loadFromFile(String file) throws IOException{
			double in=System.currentTimeMillis();
			File f= new File(file);
			FileReader fr = new FileReader(f);
			BufferedReader bf= new BufferedReader(fr);
			String line= bf.readLine();
		
			int k=0;

			
	         tsize=Integer.parseInt(line);
			line=bf.readLine();
			psize=Integer.parseInt(line);
			line=bf.readLine();
			
			K=Integer.parseInt(line);
			line=bf.readLine();
			D= new double[psize][tsize];
			V= new double[psize];
			R= new double[psize];
	
			M=new double[psize];
			N=new double[psize];
			inInv=new double[psize];
			int[][] A=new int[psize][tsize];
			while(line!=null){
				//Reads demand
				if(k<psize){
					String[] data= line.split("\t");
					for (int t = 0; t < data.length; t++) {
						D[k][t]=Integer.parseInt(data[t]);
					}
				}
				 //Loads initial inventory
				if(k>= psize && k<2*psize){
					inInv[k-psize]=Integer.parseInt(line);
				}
				//Loads opportunity costs
				if(k>= 2*psize && k<3*psize){
					R[k-2*psize]=Double.parseDouble(line);
				}
				if(k>= 3*psize && k<4*psize){
					V[k-3*psize]=Double.parseDouble(line);
				}
				if(k>= 4*psize && k<5*psize){
					M[k-4*psize]=Integer.parseInt(line);
				}
				if(k>= 5*psize && k<6*psize){
					N[k-5*psize]=Integer.parseInt(line);
				}
				
				if(k>=6*psize){
					String[] data= line.split("\t");
					
					for (int t = 0; t < data.length; t++) {
						A[k-6*psize][t]=Integer.parseInt(data[t]);
					}
				}
				k++;
				line=bf.readLine();
			}
			
			System.out.println("Time loading data: "+(System.currentTimeMillis()-in)/1000+" seconds.");
		
		}	
	
	
	public void createVariables(Model m){
		ub=99999999;
		//Creates I
		I= new QBnBVariable[psize][tsize];
		Q= new QBnBVariable[psize][tsize];
		Y= new QBnBVariable[psize][tsize];
		for (int i = 0; i < I.length; i++) {
			for (int j = 0; j < I[0].length; j++) {
				//Creates each I
				I[i][j]= new QBnBVariable(0, ub, 1, "I"+i+","+j);
				m.addVar(I[i][j]);
				Q[i][j]= new QBnBVariable(0, ub, 1, "Q"+i+","+j);
				m.addVar(Q[i][j]);
				Y[i][j]= new QBnBVariable(0, 1, 1, "Y"+i+","+j);
				m.addVar(Y[i][j]);
			}
		}
		
	}
	
	public void createFo(Model m){
		//Creates fo
		QBnBLinExp zle= new QBnBLinExp();
		for (int p = 0; p < psize; p++) {
			for (int t = 0; t < tsize; t++) {
				zle.addTerm(-R[p], I[p][t]);
			}
		}
		m.addObj(zle);
	}
	
	public void createConstraints(Model m){
		//Inventory balance
		inventory= new QBnBconstr[psize][tsize];
		for (int p = 0; p < psize; p++) {
			for (int t = 0; t < tsize; t++) {
				if(t==0){
					QBnBLinExp le= new QBnBLinExp();
					le.addTerm(1, Q[p][t]);
					le.addTerm(-1, I[p][t]);
					inventory[p][t]= new QBnBconstr(le, 0, D[p][t]-inInv[p], "inv"+p+","+t,m);
					m.addConst(inventory[p][t]);
				}
				else{
					QBnBLinExp le= new QBnBLinExp();
					le.addTerm(1, Q[p][t]);
					le.addTerm(-1, I[p][t]);
					le.addTerm(1, I[p][t-1]);
					inventory[p][t]= new QBnBconstr(le, 0, D[p][t], "D"+p+","+t, m);
					m.addConst(inventory[p][t]);
				}
			}
		}
		
		//Capacity of inventory
		invcapacity= new QBnBconstr[tsize];
		for (int t = 0; t < tsize; t++) {
			QBnBLinExp le= new QBnBLinExp();
			for (int p = 0; p < psize; p++) {
				le.addTerm(V[p], I[p][t]);
			}
			invcapacity[t]= new QBnBconstr(le, 1, K, "K"+t, m);
			m.addConst(invcapacity[t]);
		}
		
		//production
		production= new QBnBconstr[psize];
		for (int p = 0; p < psize; p++) {
			QBnBLinExp le= new QBnBLinExp();
			
			for (int t = 0; t < tsize; t++) {
				le.addTerm(1, Y[p][t]);
			}
			production[p]= new QBnBconstr(le, 1, N[p], "N"+p, m);
			m.addConst(production[p]);
		}
		
		productionRel= new QBnBconstr[psize][tsize];
		
		for (int p = 0; p < psize; p++) {
			for (int t = 0; t < tsize; t++) {
				QBnBLinExp le= new QBnBLinExp();
				le.addTerm(1, Q[p][t]);
				le.addTerm(-M[p], Y[p][t]);
				productionRel[p][t]= new QBnBconstr(le, 1, 0, "pR"+p+","+t, m);
				m.addConst(productionRel[p][t]);
			}
		}
		
	}
	public static void main(String[] args) {
		
		//Model
		Model m= new Model();
		QBnBVariable x1= new QBnBVariable(1, 100.9, 1, "x1");
		QBnBVariable x2= new QBnBVariable(75.1, 200.4, 1, "x2");
		QBnBVariable x3= new QBnBVariable(0, Double.MAX_VALUE, 1, "x3");
		
		m.addVar(x1);
		m.addVar(x2);
		m.addVar(x3);
		
		//Constraints
		
		QBnBLinExp l1= new QBnBLinExp();
		QBnBLinExp l2= new QBnBLinExp();
		QBnBLinExp l3= new QBnBLinExp();
		
		l1.addTerm(6, x1);
		l1.addTerm(8, x2);
		l1.addTerm(-4, x3);
		QBnBconstr c1 = new QBnBconstr(l1, 2, 7, "R1", m);
		
		l2.addTerm(1, x1);
		l2.addTerm(1, x2);
		l2.addTerm(1, x3);
		QBnBconstr c2 = new QBnBconstr(l2, 1, 500.3, "R2", m);
	
		l3.addTerm(1, x1);
		l3.addTerm(2, x2);
		l3.addTerm(4, x3);
		QBnBconstr c3 = new QBnBconstr(l3, 1,500.4, "R3", m);
		m.addConst(c1);
		m.addConst(c2);
		m.addConst(c3);
		
		m.update();
		
		QBnBLinExp z= new QBnBLinExp();
		z.addTerm(7, x1);
		z.addTerm(8, x2);
		z.addTerm(4, x3);
		m.addObj(z);
		QBnBEnv e= new QBnBEnv(m);
		e.print(true);
		e.setDebugMode(false);
		e.maximize();	
		System.out.println("Solution: ");
		System.out.println("x1: "+x1.getValue());
		System.out.println("x2: "+x2.getValue());
		System.out.println("x3: "+x3.getValue());
      //Test t= new Test();
		
	}
}
