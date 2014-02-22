package algorithms;

import java.util.ArrayList;

import javax.swing.JTable.PrintMode;

public class SimplexAlgorithm {
	
	private double[][] B;
	
	private double[][] N;
	
	private double[] xb;
	
	private double[] xn;
	
	private double[] cb;
	
	private double[] cn;
	
	private double[] b;
	
	private String[] In;
	
	private String[] Ib;

	
	public SimplexAlgorithm(double[][] A, double[] b, double[] c, int format) {
		In=new String[A.length];
		Ib=new String[c.length-In.length];
	}
	
	public SimplexAlgorithm() {
		double[][] AA={{2,1,4,1},{3,0,5,1}};
		double[] cc={1,2,0,0};
		createBasis(AA, cc);
		//phase2();
	}
	
	public void phase1(){
		
	}
	
	public double[] substractVectors(double[] a, double[] b){
		double[] c = new double[a.length];
		for (int i = 0; i < c.length; i++) {
			c[i]=a[i]-b[i];
		}
		return c;
	}
	
	public double[][] removeColumnFromMatrix(int pos, double[][] X){
		double[][] fin=new double[X.length][X[0].length-1];
		for (int i = 0; i < fin.length; i++) {
			int j=0;
			while(j<fin[0].length){
				if(j!=pos){
					fin[i][j]=X[i][j];
				}
			}
		}
		return fin;
	}
	public double[][] addColumnToMatrix(double[] n, double[][] X){
		double[][] fin= new double[X.length][X[0].length+1];
		for (int i = 0; i < fin.length; i++) {
			for (int j = 0; j < fin[0].length; j++) {
				if(j==fin[0].length-1){
					fin[i][j]=n[i];
				}
				else{
					fin[i][j]=X[i][j];
				}
				
			}
		}
		return fin;
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
	
	public String[] addToVector(String val, String[] vector){
		String[] newVector=new String[vector.length+1];
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
	 * Creates a feasible solution if x0 is not feasible
	 */
	public void createBasis(double[][] A, double[] c){
		//Identifies how many artificial variables are needed
		//Finds an identity matrix in A
		//Indicates in which columns are the necessary columns to form a base
		int numArt=0;
		int numb=0;
		for (int i = 0; i < A.length; i++) {
			double[] Ii= new double[A.length];
			Ii[i]=1;
			int pos=isColumn(Ii, A);
			if(pos==-1){
				//Adds artificial variable
				String idAr="a"+numArt;
				numArt++;
				Ib=addToVector(idAr, Ib);
				//Depends on the initialization methodology
				if(true){
				cb=addToVector(99999999, cb);
				}
				double[] column=Ii;
				if(B.length!=0){
					B=addColumnToMatrix(column, B);
				}
				else{
					B=new double[A.length][1];
					B=addColumnToMatrix(column, B);
				}		
			}
			else{
				System.out.println("Look column "+pos);
				String idAr=""+numb;
				Ib=addToVector(idAr, Ib);
				B=addColumnToMatrix(Ii, B);
				addToVector(c[pos], cb);
				removeFromVector(pos, c);
				A=removeColumnFromMatrix(pos, A);
				numb++;
			}
		}
		
		
	}
	
	public double[] removeFromVector(int pos, double[] vector){
		double[] nvector=new double[0];
		int i=0;
		while(i<nvector.length){
			if(i!=pos){
				nvector=addToVector(vector[i], nvector);
			}
		}
		return nvector;
		
	}
	
	public int isColumn(double[] column, double[][] M){
		int is=-1;
		for (int j = 0; j < M[0].length; j++) {
			int numberOfCoincidences=0;
			for (int i = 0; i < M.length; i++) {
				if(M[i][j]==column[i])numberOfCoincidences++;
			}
			if(numberOfCoincidences==column.length)is=j;
		}	
		return is;
	}
	public void phase2(){
		double start=System.currentTimeMillis();
		boolean optimal=false;
		B=new double[2][2];
		double[][] BB={{1,0},{0,1}};
		B=BB;
		N=new double[2][2];
		double[][] NN={{1,0},{0,1}};
		N=NN;
		double[] cnn={-1,-1};
		cn=cnn;
		double[] cbn={0,0};
		String[] Ibn={"3","4"};
		Ib=Ibn;
		String[] Inn={"1","2"};
		In=Inn;
		cb=cbn;
		double[] bb={1,1};
		b=bb;
		xb=Multiply(inverse(B), b);
		
		
		double[] rq=new double[cn.length];
		boolean unbounded=true;
		int k=0;
		while(true){
			//Finds reduced costs			
			double[][] invB=inverse(B);
			double[][] binvN=Multiply(invB, N);
			double[] rsc=Multiply(cb, binvN);
			rq=substractVectors(cn, rsc);
			
			if(rqoptimal(rq)){
				optimal=true;
				xb=Multiply(inverse(B), b);
			}
		if(optimal){
			break;
		}
		else{
			//In what direction should I move?, find just the largest negative reduced cost
			int q=lowestIndex(rq);
			double[] d=new double[cn.length+cb.length];
			//Creates d
			double[] Aq=returnColumn(q, N);
			double[] dup=Multiply(scalartimesmatrix(-1, invB), Aq);
			for (int i = 0; i < d.length; i++) {
				if(i<cn.length){
					d[i]=dup[i];
				}
				else if(i==cn.length+q){
					d[i]=1;
				}
				else{
					d[i]=0;
				}
			}
			
			//How much to move?
			double min=Double.MAX_VALUE;
			int arg=0;
			
			for (int i = 0; i < cb.length; i++) {
				if(d[i]<0){
					double actual=-xb[i]/d[i];
					if(actual<min){
						min=actual;
						arg=i;
						unbounded=false;
					}
				}
			}
			
			if(unbounded){
				System.out.println("Unbounded problem");
				break;
			}
			else{
			//Refresh B, N, xn and xb
			
			//Finds variable for pos
		    String arg1=Ib[arg];
		    String q1=In[q];
			replaceBN(q1, arg1, q, arg);
			xb=Multiply(inverse(B), b);
			}
			k++;
		}
		}
		System.out.println("Problem solved in: "+(System.currentTimeMillis()-start)/1000+" seconds.");
		System.out.println(k+" Simplex iterations");
		if(!unbounded){
		System.out.println("Best sol: ");
		for (int i = 0; i < xb.length; i++) {
			System.out.println("x_"+Ib[i]+" "+xb[i]);
		}
		}
	}
	
	public void replaceI(String q, String b){
		for (int i = 0; i < In.length; i++) {
			if(In[i].equals(q)){
				In[i]=""+b;
			}
		}
		for (int i = 0; i < Ib.length; i++) {
			if(Ib[i].equals(b)){
				Ib[i]=""+q;
			}
		}
	}
	
	public void replaceBN(String q, String b, int qpos, int bpos){
		double[] toreplaceb=returnColumn(bpos, B);
		double[] toreplaceq=returnColumn(qpos, N);
		double torcb=cb[bpos];
		double torcn=cn[qpos];
		N=replaceColumnInMatrix(toreplaceb, qpos, N);
		B=replaceColumnInMatrix(toreplaceq, bpos, B);
		replaceI(q, b);
		cb[bpos]=torcn;
		cn[qpos]=torcb;
	}
	
	public int findpos(int q, int[] a){
		int pos=0;
		boolean found=false;
		for (int i = 0; i < a.length && !found; i++) {
			if(q==a[i]){pos=i; found=true;} 
		}
		return pos;
	}
	
	public double[][] replaceColumnInMatrix(double[] c, int pos, double[][] D){
		for (int i = 0; i < D.length; i++) {
			for (int j = 0; j < D[0].length; j++) {
				if(j==pos){
					D[i][j]=c[i];
				}
			}
		}
		return D;
	}
	public double[] Multiply(double[][] a, double[] b){
		double[] c=new double[a.length];
		for (int i = 0; i < c.length; i++) {
			c[i]=dotProduct(a[i], b);
		}
		return c;
	}

	public int lowestIndex(double[] rq){
		double a= 0;
		int j=0;
		for (int i = 0; i < rq.length; i++) {
			if(rq[i]<a){
				a=rq[i];
				j=i;
			}
		}
		return j;
	}
	public boolean rqoptimal(double[] rq){
		boolean opt=true;
		for (int i = 0; i < rq.length && opt; i++) {
			if(rq[i]<0) opt=false;
		}
		return opt;
	}
	
	public double[] join(double[] a, double[] b){
		double[] c = new double[a.length+b.length];
		for (int i = 0; i < c.length; i++) {
			if(i<a.length){
				c[i]=a[i];
			}
			else{
				c[i]=b[i-a.length];
			}
		}
		return c;
	}
	public double[][] Multiply(double[][] A, double[][] B){
		double[][] C= new double[A.length][B[0].length];
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				C[i][j]=dotProduct(A[i], returnColumn(j, B));
			}
		}
		return C;
	}
	
	public double[] Multiply(double[] AA, double[][] B){
		double[][] A=new double[1][AA.length];
		A[0]=AA;
		double[] C= new double[B[0].length];
		for (int i = 0; i < C.length; i++) {
			C[i]=dotProduct(AA, returnColumn(i, B));
		}
		return C;
	}
	
	public double[][] sumMatrix(double[][] a, double[][] b){
		double[][] c= new double[a.length][a[0].length];
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++) {
				c[i][j]=a[i][j]+b[i][j];
			}
		}
		return c;
	}
	
	public double[][] inverse(double[][] X){
		
	if(X.length==2 && X[0].length==2){
		double[][] Xi=new double[X.length][X[0].length];
		double det=det(X);
		Xi[0][0]=X[1][1];
		Xi[1][1]=X[0][0];
		Xi[1][0]=-X[1][0];
		Xi[0][1]=-X[0][1];
		return scalartimesmatrix(1/det, Xi);
		
	}
	else{
	
	double overdet=1/det(X);
	double[][] adj=adjoint(X);
	return scalartimesmatrix(overdet, transpose(adj));
	}
	}
	
	public double[][] transpose(double[][] a){
		double[][] c= new double[a[0].length][a.length];
		
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c.length; j++) {
				c[i][j]=a[j][i];
			}
		}
		return c;
	}
	
	public double[][] scalartimesmatrix(double scalar, double[][] m){
		double[][] mm= new double[m.length][m[0].length];
		for (int i = 0; i < mm.length; i++) {
			for (int j = 0; j < mm[0].length; j++) {
				mm[i][j]=scalar*m[i][j];
			}
		}
		return mm;
	}
	
	public double det(double[][] X){
		
	double det=0;
	if(X.length==2 && X[0].length==2){
		return X[0][0]*X[1][1]-X[0][1]*X[1][0];
	}
	else{
		for (int i = 0; i < X[0].length; i++) {
			int a=i;
			int b=0;
			double[][] A= reduceColumn(i, X);
			A=reduceRow(0, A);
			det+= X[0][i]*Math.pow(-1, a+b)*det(A);
		}
		return det;
	}
		
	}
	
    public double[][] reduceColumn(int pos, double[][] X){
    	double[][] E= new double[X.length][X[0].length-1];
    	for (int i = 0; i < X.length; i++) {
    		int k=0;
			for (int j = 0; j < X[0].length; j++) {
				if(j!=pos){
					E[i][k]=X[i][j];
					k++;
				}	
			}
		}
    	return E;
    }
    
    public double[][] reduceRow(int pos, double[][] X){
    	double[][] E= new double[X.length-1][X[0].length];
    	int k=0;
    	for (int i = 0; i < X.length; i++) {		
    		if(i!=pos){
    			for (int j = 0; j < X[0].length; j++) {
    				E[k][j]=X[i][j];
    			}	
				k++;
			}
		} 	
    	return E;
    }
	
	public double[][] substractMatrix(double[][] a, double[][] b){
		double[][] c= new double[a.length][a[0].length];
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++) {
				c[i][j]=a[i][j]-b[i][j];
			}
		}
		return c;
	}
	
	public double[] returnColumn(int col, double[][] C){
		double[] sol= new double[C.length];
		for (int i = 0; i < sol.length; i++) {
			sol[i]=C[i][col];
		}
		return sol;
	}
	
	public double dotProduct(double[] a, double[] b){
		double dp=0;
		for (int i = 0; i < b.length; i++) {
			dp+=a[i]*b[i];
		}	
		return dp;	
	}
	public void printMatrixToConsole(double[][] C){
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				System.out.print(C[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public double[][] adjoint(double[][] x){
		double[][] a= new double[x.length][x[0].length];
		
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				double[][] X1=reduceColumn(j, x);
				X1=reduceRow(i, X1);
				a[i][j]=Math.pow(-1, i+j)*det(X1);
			}
		}
		return a;
	}
	
	public void printVectorToConsole(double[] C){
		for (int i = 0; i < C.length; i++) {
				System.out.print(C[i]+" ");
		}
		System.out.println();
	}
	public static void main(String[] args) {
// TODO Auto-generated method stub
  SimplexAlgorithm sa = new SimplexAlgorithm();
//  double[] a={1,2};
//  double[][] b={{1,1},{2,2},{3,3}};
//  double[] c= {4,4,4};
//  b=sa.replaceColumnInMatrix(c, 0, b);
//  sa.printMatrixToConsole(b);
 
	}
}
