package knapSavings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;



public class KnapSavings {

	private double[] W;
	
	private double[] P;
	
	private double[][] delta;
	
	private int[] x;
	
	private double K;
	
	ArrayList<Integer> dominant= new ArrayList<Integer>();
	
	ArrayList<Integer> nondominant= new ArrayList<Integer>();
	
	public KnapSavings() {
	    try {
			loadFromFile("./data/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double init= System.currentTimeMillis();
		breakSets();
		ArrayList<String> or=delta();
		ArrayList<String> delta= sort(or);
		Collections.reverse(delta);
		x= new int[P.length];
	    x=mainAlgorithm(delta);
	    System.out.println("CPU time: "+(System.currentTimeMillis()-init)/1000+" sec.");
	    System.out.println("Solution: "+z());
	    for (int i = 0; i < P.length; i++) {
			System.out.println("x"+(i+1)+" "+x[i]);
		}
	    
	}
	
	public double z(){
		double z=0;
		for (int i = 0; i < P.length; i++) {
			z+=x[i]*P[i];
		}
		return z;
	}

	public ArrayList<String> sort(ArrayList<String> a){
	    for (int i = 1; i < a.size(); i++) {
			int j=i;
			
			while(j > 0){
				double aa=Double.parseDouble(a.get(j-1).split(",")[0]);
				double ba=Double.parseDouble(a.get(j).split(",")[0]);
				double ca=Double.parseDouble(a.get(j-1).split(",")[1]);
				double da=Double.parseDouble(a.get(j).split(",")[1]);
				if( larger(aa,ba,ca,da)){
				String temp=a.get(j);
				a.set(j, a.get(j-1));
				a.set(j-1, temp);
				j--;
				}
				else{
					break;
				}
			}
		}
	    return a;
	}
	
	public boolean larger(double a, double b, double c, double d){
		if(a>b){
			return true;
		}
		else{
			if(a==b){
				return c>=d;
			}
			else{
				return false;
			}
			
		}
	}
	
	public void printVector(int[] v){
		for (int i = 0; i < v.length; i++) {
			System.out.print(v[i]+" ");
		}
		System.out.println();
	}
	
	public void breakSets(){
		
		
		
		for (int i = 0; i < P.length; i++) {
			int n= 0;
			boolean dom=true;
			for (int j = 0; j < P.length && dom ; j++) {
				if(P[j]>P[i]){
					if(W[j]<=W[i]){
						nondominant.add(i);
						dom=false;
					}
				}
			}
			if(dom){
				dominant.add(i);
			}
		}
		
	}
	
	public ArrayList<String> sortLex(ArrayList<String> original){
		ArrayList data= new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			data.add(original.get(i).split(",")[0]+original.get(i).split(",")[1]);
		}
		 
		 return sort(data);
	}
	
	public int[] mainAlgorithm(ArrayList<String> delta){
		double r=K;
		for (int i = 0; i < (int) delta.size()/2; i++) {
			String[] data=delta.get(i).split(",");
			int j=Integer.parseInt(data[data.length-1]);
			if(r>0){
				x[j]=(int) Math.floor(r/W[j]);
				r-=W[j]*x[j];
			}
		}
		return x;
	}
	
	public void loadFromFile(String file) throws IOException{
		File f= new File(file);
		FileReader fr= new FileReader(f);
		BufferedReader bf= new BufferedReader(fr);
		String line= bf.readLine();
		 K=0;
		int vars=Integer.parseInt(line);
		line= bf.readLine();
		 W= new double[vars];
		 P=new double[vars];
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
	}
	public ArrayList<String> delta(){
		ArrayList<String> delta= new ArrayList<String>();
		int a= dominant.size();
		
		for (int i = 0; i < a; i++) {
			for (int j = 0; j < a; j++) {
				if(i!=j){
				delta.add((P[i]-P[j])+","+(W[i]-W[j])+","+dominant.get(i)+","+dominant.get(j));
				}
			}
		}
		return delta;
	}
	public static void main(String[] args) {
		
		
	
		KnapSavings k = new KnapSavings();
	
	}
}
