package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import model.Model;
import model.QBnBEnv;
import model.QBnBLinExp;
import model.QBnBVariable;
import model.QBnBconstr;

public class KnapsackInstances {

	public KnapsackInstances() {
		// TODO Auto-generated constructor stub
	}

	public void loadFromFile(String file) throws IOException{
		File f= new File(file);
		FileReader fr= new FileReader(f);
		BufferedReader bf= new BufferedReader(fr);
		String line= bf.readLine();
		double K=0;
		int vars=Integer.parseInt(line);
		line= bf.readLine();
		double[] W= new double[vars];
		double[] P=new double[vars];
		int k=0;
		while(line!=null){
			if(k==0){
				K=Double.parseDouble(line);
			}
			else if(k==1){
			String[] data=line.split("\t");
			W= new double[data.length];
			for (int i = 0; i < data.length; i++) {
				W[i]=Double.parseDouble(data[i]);
			}
			}
			else{
				String[] data=line.split("\t");
				P= new double[data.length];
				for (int j = 0; j < data.length; j++) {
					P[j]=Double.parseDouble(data[j]);
				}
				
			}
			k++;
			line=bf.readLine();
		}
		
		//Creates the model
		Model m = new Model();
		QBnBVariable[] x= new QBnBVariable[W.length];
		for (int i = 0; i < x.length; i++) {
			x[i]= new QBnBVariable(0, 1, 1, "x"+(i+1));
			m.addVar(x[i]);
		}
		
		//Main Restricion
		QBnBLinExp le = new QBnBLinExp();
		for (int i = 0; i < x.length; i++) {
			le.addTerm(W[i], x[i]);
		}
		QBnBconstr c = new QBnBconstr(le, 1, K, "K", m);
		m.addConst(c);
		
		//Objective function
		QBnBLinExp z = new QBnBLinExp();
		for (int i = 0; i < x.length; i++) {
			z.addTerm(P[i], x[i]);
		}
		m.addObj(z);
		
		m.update();
		QBnBEnv env= new QBnBEnv(m);
		env.print(true);
		env.maximize();
		
		//Prints 
		System.out.println("Value: "+env.getBestSolution());
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i].getName()+": "+Math.round(x[i].getValue()));
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KnapsackInstances k = new KnapsackInstances();
		try {
			k.loadFromFile("./data/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
