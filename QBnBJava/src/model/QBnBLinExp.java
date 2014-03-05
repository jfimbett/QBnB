package model;

import java.util.ArrayList;

public class QBnBLinExp {
	
	private ArrayList<Double> coeffs;
	private ArrayList<String> varNames;
	
	public QBnBLinExp() {
		coeffs=new ArrayList<Double>();
		varNames= new ArrayList<String>();
	}
	
	public void addTerm(double coeff, QBnBVariable var){
		coeffs.add(coeff);
		varNames.add(var.getName());
	}

	public ArrayList<Double> getCoeffs() {
		return coeffs;
	}

	public void setCoeffs(ArrayList<Double> coeffs) {
		this.coeffs = coeffs;
	}

	public ArrayList<String> getVarNames() {
		return varNames;
	}

	public void setVarNames(ArrayList<String> varNames) {
		this.varNames = varNames;
	}
	
	

}
