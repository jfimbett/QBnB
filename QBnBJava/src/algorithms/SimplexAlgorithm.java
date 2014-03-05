

/*************************************************************************
 *  
 *  Class: SimplexAlgorithm.java
 *
 *  Solves a generic Bounded LP of the form
 *  
 *  min cx
 *  
 *  s.t.
 *  
 *  Ax=b
 *  l<=x<=u
 *  
 *  If variable is unbounded then, u=Infinity
 *
 *  Author: Juan Imbett
 *  Email: jfimbett@gmail.com
 *
 *************************************************************************/
package algorithms;

import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
public class SimplexAlgorithm {
	
	 /**
	  * Basic Matrix
	  */
	 private double[][] B;
	 
	 /**
	  * NonBasic Matrix
	  */
	 
	 private double[][] N;
	 
	 /**
	  * Basic Solutions
	  */
	 
	 private double[] xb;
	 
	 /**
	  * Non Basic Solutions
	  */
	 
	 private double[] xn;
	 
	 /**
	  * Basic costs
	  */
	 
	 private double[] cb;
	 
	 /**
	  * Non-Basic Costs
	  */
	 
	 private double[] cn;
	 
	 /**
	  * RHS
	  */
	 
	 private double[] b;
	 
	 /**
	  * List of NonBasic variables
	  */
	 
	 private String[] In;
	 
	 /**
	  * List of Basic variables
	  */
	 
	 private String[] Ib;
	 
	 /**
	  * Cost vector
	  */
	 
	 private double[] c;
	 
	 /**
	  * Status of the problem
	  * 0 Optimal
	  * 1 Unbounded
	  * 3 Unfeasible
	  */
	 
	 private int status;
	 
	 /**
	  * Whole coefficient Matrix
	  */
	 
	 private double[][] A;
	 
	 /**
	  * Dual solutions
	  */
	 
	 private double[] dual;
	 
	 /**
	  * Reduced costs solution
	  */
	 
	 private double[] rq;
	 
	 /**
	  * Upper bounds for basic solutions
	  */
	 
	 private double[] Ub;
	 
	 /**
	  * Lower bounds for basic solutions
	  */
	 
	 private double[] Lb;
	 
	 /**
	  * Upper bounds for non basic solutions
	  */
	 
	 
	 private double[] Un;
	 
	 /**
	  * Lower bounds for non basic solutions
	  */
	 
	 private double[] Ln;
	 
	 /**
	  * Complete vector of upper bounds
	  */
	 
	 private double[] U;
	 
	 /**
	  * Complete vector of lower bounds
	  */
	 
	 private double[] L;
	 
	 /**
	  * Number of constraints
	  */
	 
	 public int m;
	 
	 /**
	  * Number of variables
	  */
	 
	 public int n;
	 
	 /**
	  * Initial time of the algorithm
	  */
	 
	 public double initialTime;
	 
	 /**
	  * Number of artificial variables added
	  */
	 
	 public int nart;
	 
	 public boolean printSolution;
	 
	 public String[] I;
	 
	 
	 /**
	  * Format Ax=b
	  * @param A: The Matrix of coefficients
	  * @param b: The RHS
	  * @param c: The cost vector
	  * 
	  */
	 public SimplexAlgorithm(double[][] A, double[] b, double[] c, String[] I, double[] U, double[] L, boolean print) {
	  initialTime=System.currentTimeMillis();
	  this.printSolution=print;
	  this.m=A.length;
	  this.n=A[0].length;
	  this.U=U;
	  this.L=L;
	  this.A=A;
	  this.b=b;
	  Ib=new String[0];
	  In= new String[0];
	  Ub= new double[0];
	  Un= new double[0];
	  Lb= new double[0];
	  Ln= new double[0];
	  cb= new double[0];
	  cn= new double[0];
	  this.I=I;
	  nart=0;
	  this.c=c;
	  createBasis(A, c, I);
	  phase2();
	  dual=getDual();
	 }
	 
	 public SimplexAlgorithm() {
	 
	 }
	 
	 /**
	  * Returns the dual solutions cbTBinv
	  * @return the dual solution
	  */
	 public double[] getDual(){
	  return Multiply(cb, inverse(B));
	 }
	 
	
	 /**
	  * Returns the dual value of the current solution
	  * @return, WTb
	  */
	 public double dualfo(){
	  return dotProduct(dual, b);
	 }
	 
	 /**
	  * Substracts two vectors
	  * @param a: First vector
	  * @param b: Second vector
	  * @return: a-b
	  */
	 public double[] substractVectors(double[] a, double[] b){
	  double[] c = new double[a.length];
	  for (int i = 0; i < c.length; i++) {
	   c[i]=a[i]-b[i];
	  }
	  return c;
	 }
	 
	 /**
	  * Removes column in position pos from X
	  * @param pos: The number of the column to remove
	  * @param X: The Matrix
	  * @return X-{X_q}
	  */
	 public double[][] removeColumnFromMatrix(int pos, double[][] X){
	  double[][] fin=new double[X.length][X[0].length-1];
	  for (int i = 0; i < fin.length; i++) {
	   int j=0;
	   int k=0;
	   while(k<X[0].length){
	    if(k!=pos){
	     fin[i][j]=X[i][k];
	     j++;
	    }
	    k++;
	   }
	  }
	  return fin;
	 }
	 
	 /**
	  * Adds a new column to a matrix
	  * @param n: new column
	  * @param X: The Matrix
	  * @return X+{n}
	  */
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
	 
	 /**
	  * Adds element to the end of a vector
	  * @param val: The element to be added
	  * @param vector: The vector
	  * @return The original vector plus val at the end
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
	  * Adds element to the end of a vector
	  * @param val: The element to be added
	  * @param vector: The vector
	  * @return The original vector plus val at the end
	  */
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
	  * Returns the primal objective function
	  * @return
	  */
	 public double primalfo(){
	  return dotProduct(cb, xb)+dotProduct(cn, xn);
	 }
	 
	 /**
	  * Creates a feasible solution if the Identity Matrix is not found on A
	  * @param A: The matrix of coefficients
	  * @param c: The cost vector
	  * @param I: The variable names
	  */
	 public void createBasis(double[][] A, double[] c, String[] I){
	  //Identifies how many artificial variables are needed
	  //Finds an identity matrix in A
	  //Indicates in which columns are the necessary columns to form a base
	  B= new double[0][0];
	  int numArt=0;
	  for (int i = 0; i < A.length; i++) {
	   double[] Ii= new double[A.length];
	   Ii[i]=1;
	   int pos=isColumn(Ii, A);
	   if(pos==-1){
	    //Adds artificial variable
	    String idAr="a"+numArt;
	    nart++;
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
	     B=new double[Ii.length][1];
	     for (int j = 0; j < Ii.length; j++) {
	      B[j][0]=Ii[j];
	     }
	    } 
	    if(Ub.length!=0){
	     Ub=addToVector(Double.MAX_VALUE, Ub);
	    }
	    else{
	     Ub= new double[1];
	     Ub[0]=Double.MAX_VALUE;
	    }
	    if(Lb.length!=0){
	     Lb=addToVector(0, Lb);
	    }
	    else{
	     Lb= new double[1];
	     Lb[0]=0;
	    }
	   }
	   else{
	    String idAr=I[pos];
	    Ib=addToVector(idAr, Ib);
	    if(B.length==0){
	     B=new double[Ii.length][1];
	     for (int j = 0; j < Ii.length; j++) {
	      B[j][0]=Ii[j];
	     }
	    }
	    else{
	     B=addColumnToMatrix(Ii, B); 
	    }
	    Ub=addToVector(U[pos], Ub);
	    Lb=addToVector(L[pos], Lb);
	    cb=addToVector(c[pos], cb);
	    c=removeFromVector(pos, c);
	    A=removeColumnFromMatrix(pos, A);
	   I= removeFromVector(pos, I);
	    U=removeFromVector(pos, U);
	    L=removeFromVector(pos, L); 
	   }
	  }
	  cn=c;
	  N=A;
	  In=I;
	  Un=U;
	  Ln=L;
	  setNBtoBounds();
	 }
	 
	 /**
	  * Set all the non basic variables to their lower bounds
	  */
	 public void setNBtoBounds(){
	  xn=new double[n-m+nart];
	  for (int i = 0; i < In.length; i++) {
	   xn[i]=Ln[i];
	  }
	 }
	 
	 /**
	  * Removes element from position pos from vector
	  * @param pos: Position of the element to be deleted
	  * @param vector: The vector
	  * @return: The new vector
	  */
	 public double[] removeFromVector(int pos,double[] vector){
	  double[] nvector=new double[vector.length-1];
	  int i=0;
	  int k=0;
	  while(k<vector.length){
	   if(k!=pos){
	    nvector[i]=vector[k];
	    i++;
	   }
	   k++;
	   
	  }
	  return nvector;
	  
	 }
	 
	 /**
	  * Removes element from position pos from vector
	  * @param pos: Position of the element to be deleted
	  * @param vector: The vector
	  * @return: The new vector
	  */
	 public String[] removeFromVector(int pos,String[] vector){
	  String[] nvector=new String[vector.length-1];
	  int i=0;
	  int k=0;
	  while(k<vector.length){
	   if(k!=pos){
	    nvector[i]=vector[k];
	    i++;
	   }
	   k++;
	   
	  }
	  return nvector;
	  
	 }
	 
	 public boolean areEqual(double[] a, double[] b){
		 boolean equal=true;
		 for (int i = 0; i < b.length; i++) {
			if(a[i]!=b[i]){
				equal=false;
				break;
			}
		}
		 return equal;
	 }
	 /**
	  * Returns true if column is a column of M
	  * False otherwise
	  * @param column: The column to find in M
	  * @param M: The Matrix where the column may be or may be not be
	  * @return The position of the column
	  * -1 if column is not found
	  */
	 public int isColumn(double[] column, double[][] M){
		 Matrix m= new Matrix(M);
		 int p=-1;
		 for (int i = 0; i < M[0].length; i++) {
			double[][] a= m.getMatrix(0, M.length-1, i, i).transpose().getArrayCopy();
			if(Arrays.equals(a[0],column)){
				p=i;
			}
		}
		 return p;
//	  int is=-1;
//	  for (int j = 0; j < M[0].length; j++) {
//	   int numberOfCoincidences=0;
//	   for (int i = 0; i < M.length; i++) {
//	    if(M[i][j]==column[i])numberOfCoincidences++;
//	   }
//	   if(numberOfCoincidences==column.length)is=j;
//	  } 
//	  return is;
	 }
	 
	 /**
	  * Main Algorithm
	  */
	 public void phase2(){
	  boolean optimal=false;	 
	  double[][] Binv=inverse(B);
	  xb=substractVectors(Multiply(Binv, b),Multiply(Multiply(Binv, N), xn));    
	  rq=new double[cn.length];
	  boolean unbounded=true;
	  int k=0;
	  while(true){	   
		  System.out.println("It "+k);
	   //Finds reduced costs   
	   double[][] invB=inverse(B);
	   double[][] binvN=Multiply(invB, N);
	   double[] rsc=Multiply(cb, binvN);
	   rq=substractVectors(cn, rsc);   
	   xb=substractVectors(Multiply(invB, b),Multiply(Multiply(invB, N), xn));
	   if(rqoptimal(rq)){
	    optimal=true;
	    
	   }
	  if(optimal){
	   break;
	  }
	  else{
	   //In what direction should I move?, find just the largest negative reduced cost
	   int q=lowestIndex(rq);
	   boolean degenerancy=true;
	   int arg=0;
	   int argmax=0;
	   double[] d=new double[cn.length+cb.length];
	   //Creates d
	   double[] Aq=returnColumn(q, N);
	   double[] dup=Multiply(scalartimesmatrix(-1, invB), Aq);
	   for (int i = 0; i < d.length; i++) {
	    if(i<cb.length){
	     d[i]=dup[i];
	    }
	    else if(i==cb.length+q){
	     d[i]=1;
	    }
	    else{
	     d[i]=0;
	    }
	   }
	   //How much to move?  
	   double min=Double.MAX_VALUE;
	   for (int i = 0; i < cb.length; i++) {
	    if(d[i]<0){
	     double actual=-(xb[i]-Lb[i])/d[i];
	     if(actual<min){
	      min=actual;
	      arg=i;
	      unbounded=false;
	     }
	    }
	   }
	   double min2=Double.MAX_VALUE;
	   for (int i = 0; i < cb.length; i++) {
	    if(d[i]>0){
	     double actual=-(xb[i]-Ub[i])/d[i];
	     if(actual<min){
	      min2=actual;
	      argmax=i;
	      unbounded=false;
	     }
	    }
	   }  
	   double minx=Math.min(Math.min(min, Un[q]-Ln[q]),min2); //Minimum of the three possible steps
	   if(unbounded){
	    System.out.println("Unbounded problem");
	    status=1;
	    break;
	   }
	   else{ 
	   //Finds variable for pos
	    if(minx==Un[q]-Ln[q]){
	     xn[q]=Un[q];
	    }
	    else{
	     if(minx==min){
	      String arg1=Ib[arg];
	       String q1=In[q];
	       double l=Lb[arg];
	       replaceBN(q1, arg1, q, arg);
	       xn[q]=l;
	     }
	     else{   
	      String arg1=Ib[argmax];
	       String q1=In[q];
	       double u=Ub[argmax];
	       replaceBN(q1, arg1, q, argmax); 
	       xn[q]=u;
	     }
	    }
	   }
	   k++;
	  }
	  
	  }
	  if(printSolution){
	  System.out.println("Problem solved in: "+(System.currentTimeMillis()-initialTime)/1000+" seconds.");
	  System.out.println(k+" Simplex iterations");
	  }
	  if(!feasible()){
		  if(printSolution){
	   System.out.println("Problem not feasible");
		  }
	   status=3;
	  }
	  else{
	   if(!unbounded){
	    status=0;
	    if(printSolution){
	    System.out.println("Best sol: ");
	    for (int i = 0; i < xb.length; i++) {
	    if(Ib[i].charAt(0)!='S' && Ib[i].charAt(0)!='a'){
	      System.out.println(Ib[i]+" "+xb[i]);
	      
	    }
	    }
	    for (int i = 0; i < xn.length; i++) {
	     if(In[i].charAt(0)!='S' && In[i].charAt(0)!='a'){
	       System.out.println(In[i]+" "+xn[i]);       
	     }
	     }
	    }
	   }
	  }	  
	 }
	 
	
	
	 /**
	  * Indicates if a problem is feasible by checking artificial variables in the basic solution
	  * @return True if feasible, false if not feasible
	  */
	 public boolean feasible(){
	  boolean feasible=true;
	  for (int i = 0; i < Ib.length; i++) {
	   char a=Ib[i].charAt(0);
	   if(a=='a' && xb[i]!=0){
	    feasible=false;
	    break;
	   }
	  }
	  return feasible;
	 }
	 
	 /**
	  * Replaces the variable names in the indicator sets
	  * @param q Non basic name
	  * @param b Basic Name
	  */
	 public void replaceI(String q, String b){
	  for (int i = 0; i < In.length; i++) {
	   if(In[i].equals(q)){
	    In[i]=""+b;
	    break;
	   }
	  }
	  for (int i = 0; i < Ib.length; i++) {
	   if(Ib[i].equals(b)){
	    Ib[i]=""+q;
	    break;
	   }
	  }
	 }
	 
	 /**
	  * Refresh B and N
	  * @param q: Non basic variable to enter the basis
	  * @param b: Basic variable to leave the basis
	  * @param qpos: Position of the non basic variable to enter the basis
	  * @param bpos: Position of the Basic variable to leave the absis
	  */
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
	  double uub=Ub[bpos];
	  double uun=Un[qpos];
	  double llb=Lb[bpos];
	  double lln=Ln[qpos];
	  Ub[bpos]=uun;
	  Un[qpos]=uub;
	  Lb[bpos]=lln;
	  Ln[qpos]=llb;
	 }
	 
	/**
	 * Finds the position of q in a
	 * @param q: The value to find
	 * @param a: The vector to find in
	 * @return The position, l1 if not found. 
	 */
	 public int findpos(int q, int[] a){
	  int pos=-1;
	  boolean found=false;
	  for (int i = 0; i < a.length && !found; i++) {
	   if(q==a[i]){pos=i; found=true;} 
	  }
	  return pos;
	 }
	 
	 /**
	  * Replaces column pos from D with c
	  * @param c: The new column
	  * @param pos: The position of the column to be replaced
	  * @param D: The matrix
	  * @return: The new matirx
	  */
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
	 
	 /**
	  * Multiplies matrix a with column vector b using JAMA
	  * @param a: Matrix
	  * @param b: Column vector
	  * @return A column vector with a*b
	  */
	 public double[] Multiply(double[][] a, double[] b){
	 Matrix AA= new Matrix(a);
	 Matrix bb= new Matrix(b,1);
	 return AA.times(bb.transpose()).getRowPackedCopy();
	 }
	 
	 /**
	  * Returns the lowest reduced cost in which a non'basic variable can enter the basis
	  * @param rq
	  * @return
	  */
	 public int lowestIndex(double[] rq){
	  double a= 0;
	  int j=0;
	  for (int i = 0; i < rq.length; i++) {
	   if(rq[i]<a && xn[i]<Un[i]){
	    a=rq[i];
	    j=i;
	   }
	  }
	  return j;
	 }
	 
	 /**
	  * Indicates if the LP problem is optimal by checking the reduced costs
	  * @param rq: Vector of reduced costs
	  * @return: True if feasible, false if not feasible. 
	  */
	 public boolean rqoptimal(double[] rq){
	  boolean opt=true;
	  for (int i = 0; i < rq.length && opt; i++) {
	   if(rq[i]<0 && xn[i]<Un[i]){
	    opt=false;
	   }
	  }
	  return opt;
	 }
	 
	 /**
	  * Joins vector a and vector b
	  * @param a: First vector
	  * @param b: Second vector
	  * @return a new vector that is the joint of a and b
	  */
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
	 
	 /**
	  * Multiplies A and B using JAMA
	  * @param A: The first Matrix
	  * @param B: The second Matrix
	  * @return C=AB
	  */
	 public double[][] Multiply(double[][] A, double[][] B){
	 Matrix a= new Matrix(A);
	 Matrix b= new Matrix(B);
	 return a.times(b).getArrayCopy();
	 }
	 
	 
	 /**
	  * Multiplies row vector AA with matrix B using JAMA
	  * @param AA the row vector
	  * @param B The Matrix
	  * @return AA*B
	  */
	 public double[] Multiply(double[] AA, double[][] B){
	 Matrix a= new Matrix(AA,1);
	 Matrix b= new Matrix(B);
	 return a.times(b).getRowPackedCopy();
	 }
	 
	
	 
	 public double[][] inverse(double[][] X){
	  
	  Matrix X1= new Matrix(X);
	  return X1.inverse().getArrayCopy();
	 }
	 
	 /**
	  * Multiplies a real number with a Matrix
	  * @param scalar: Real number
	  * @param m: Matrix
	  * @return scalar*m
	  */
	 public double[][] scalartimesmatrix(double scalar, double[][] m){
	  double[][] mm= new double[m.length][m[0].length];
	  for (int i = 0; i < mm.length; i++) {
	   for (int j = 0; j < mm[0].length; j++) {
	    mm[i][j]=scalar*m[i][j];
	   }
	  }
	  return mm;
	 }
	 
	
	 
	 /**
	  * Returns column in position col from C
	  * @param col: Position of column
	  * @param C: Matrix
	  * @return C[i][col] \forall i
	  */
	 public double[] returnColumn(int col, double[][] C){
	  double[] sol= new double[C.length];
	  for (int i = 0; i < sol.length; i++) {
	   sol[i]=C[i][col];
	  }
	  return sol;
	 }
	 
	 /**
	  * Returns the dot product of two vectors \sum a_i b_i
	  * @param a: The first vector
	  * @param b: The second vector
	  * @return: The dot product of a and b
	  */
	 public double dotProduct(double[] a, double[] b){
	  double dp=0;
	  for (int i = 0; i < b.length; i++) {
	   dp+=a[i]*b[i];
	  } 
	  return dp; 
	 }
	 
	 /**
	  * Prints a matrix to console, useful for debugging purposes
	  * @param C: Matrix to print
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
	  * Prints a vector to console, useful for debugging purposes
	  * @param C: Vector to print
	  */
	 public void printVectorToConsole(double[] C){
	  for (int i = 0; i < C.length; i++) {
	    System.out.print(C[i]+" ");
	  }
	  System.out.println();
	 }
	 
	 /**
	  * Prints the current LP problem
	  */
	 public void printProblem(){
		  System.out.print("min ");
		  printvectorTranspose(c);
		  
		  System.out.println("s.t.");
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				String sign="+";
				if(A[i][j]<0){
					 sign="";
				}
				System.out.print(sign+" "+A[i][j]+"*"+I[j]);
			}
			System.out.print("="+b[i]);
			System.out.println();
		}
		for (int i = 0; i < Ib.length; i++) {
			System.out.println(Lb[i]+"<="+Ib[i]+"<="+Ub[i]+" -> "+xb[i]);
		}
		for (int i = 0; i < In.length; i++) {
			System.out.println(Ln[i]+"<="+In[i]+"<="+Un[i]+" -> "+xn[i]);
		}
		
		System.out.println("Sol; ");
		
		 }
		
	 /**
	  * Prints a vector horizontally 
	  * @param vector: Vector to print
	  */
	public void printvectorTranspose(double[] vector){
		  System.out.print("[");
		  for (int i = 0; i < vector.length; i++) {
		   System.out.print(vector[i]+",");
		  }
		  System.out.print("] ");
		 }
		 
     /******************************************************************************************
     ******************************************************************************************* 
     **********************************GETTERS AND SETTERS************************************** 
     ********************************************************************************************
     ********************************************************************************************/


	 public void setB(double[][] b) {
	  B = b;
	 }
	 
	 public double[][] getBasis(){
	  return this.B;
	 }
	 public double[][] getN() {
	  return N;
	 }
	 public void setN(double[][] n) {
	  N = n;
	 }
	 public double[] getXb() {
	  return xb;
	 }
	 public void setXb(double[] xb) {
	  this.xb = xb;
	 }
	 public double[] getXn() {
	  return xn;
	 }
	 public void setXn(double[] xn) {
	  this.xn = xn;
	 }
	 public double[] getCb() {
	  return cb;
	 }
	 public void setCb(double[] cb) {
	  this.cb = cb;
	 }
	 public double[] getCn() {
	  return cn;
	 }
	 public void setCn(double[] cn) {
	  this.cn = cn;
	 }
	 public double[] getB() {
	  return b;
	 }
	 public void setB(double[] b) {
	  this.b = b;
	 }
	 public String[] getIn() {
	  return In;
	 }
	 public void setIn(String[] in) {
	  In = in;
	 }
	 public String[] getIb() {
	  return Ib;
	 }
	 public void setIb(String[] ib) {
	  Ib = ib;
	 }
	 public int getStatus() {
	  return status;
	 }
	 public void setStatus(int status) {
	  this.status = status;
	 }
	 
	 public double[] getC() {
	  return c;
	 }
	 public void setC(double[] c) {
	  this.c = c;
	 }
	 public double[][] getA() {
	  return A;
	 }
	 public void setA(double[][] a) {
	  A = a;
	 }
	 public void setDual(double[] dual) {
	  this.dual = dual;
	 }
	 public double[] getRq() {
	  return rq;
	 }
	 public void setRq(double[] rq) {
	  this.rq = rq;
	 }
	 
	 
	 public static void main(String[] args) {
	// TODO Auto-generated method stub
	  double[][] A={{1,1,1,0},{1,2,0,-1}};
	  double[] c={-3,-4,0,0};
	  String[] names={"x1","x2","S1","S2"};
	  double[] b={100,20};
	  double[] U={20,10,Double.MAX_VALUE, Double.MAX_VALUE};
	  double[] L={5,3,0,0};
	  SimplexAlgorithm sa = new SimplexAlgorithm(A,b,c,names, U, L, true);
	  sa.printProblem();
		 
/*		 SimplexAlgorithm sa= new SimplexAlgorithm();
		 double[][] A={{1,2},{1,1}};
		 double[] c={2,1};
		 System.out.println(sa.isColumn(c, A));*/
	  
	
}
}
