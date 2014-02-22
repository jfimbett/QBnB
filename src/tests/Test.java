package tests;

import model.Model;
import model.QBnBEnv;
import model.QBnBLinExp;
import model.QBnBVariable;
import model.QBnBconstr;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		//Solves a simple simplex problem
		Model m= new Model();
		QBnBVariable x1= new QBnBVariable(0, 100, 1, "x1");
		QBnBVariable x2= new QBnBVariable(0, 100, 1, "x2");
		m.addVar(x1);
		m.addVar(x2);
		QBnBLinExp l1= new QBnBLinExp();
		l1.addTerm(1, x1);
		QBnBconstr c1= new QBnBconstr(l1, 1, 4.5);
		QBnBLinExp l2= new QBnBLinExp();
		l2.addTerm(1, x2);
		QBnBconstr c2= new QBnBconstr(l2, 1, 4.5);
		m.addConst(c1);
		m.addConst(c2);
		QBnBLinExp z= new QBnBLinExp();
		z.addTerm(1, x1);
		z.addTerm(1, x2);
		
		m.addObj(z);
		m.update();
		QBnBEnv env= new QBnBEnv(m);
		env.maximize();
        
	}

}
