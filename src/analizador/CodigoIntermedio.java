package analizador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;

public class CodigoIntermedio {
	
	private String code;
	private ArrayList<Cuadruplo> cuadruplos;
	private int auxCount = 1; 
	private ArrayList<ValoresTabla> tablaSimbolos;

	public CodigoIntermedio(String code,ArrayList<ValoresTabla> tablaSimbolos) {
		this.code = code;
		cuadruplos = new ArrayList<Cuadruplo>();
		this.tablaSimbolos = tablaSimbolos;
		ComputeCuadruplos();
	}

	public ArrayList<Cuadruplo> getCuadruplos() {
		return this.cuadruplos;
	}
	
	private void ComputeCuadruplos() {
		//List<String> expresions =  getAllExpressions(this.code, "[a-z]{1,20}[0-1]{1,20}[\\s]{0,3}=[\\s]{0,3}([\\s]{0,3}\\({0,1}[a-z]{1,20}[0-9]{1,20}[\\s]{0,3}(\\+|\\-|\\*|\\/){0,1}\\){0,1}[\\s]{0,3}){1,7};");
		List<String> expresions =  getAllExpressions(this.code, "[a-z]{1,20}[0-9]{1,20}[\\s]{0,3}=[\\s]{0,3}.*;");
		for(String expresion : expresions) {
			expresion = expresion.replace(" ","");
 			String[] expresionArr = expresion.split("(?<=[-+*/=\\(\\)])|(?=[-+*/=\\(\\)])");
			
			if(expresionArr.length > 3) {
				this.asignaValor(expresion, expresionArr[0]);
			}
			
			ArrayList<String> expressionList =  new ArrayList<String>(Arrays.asList(expresionArr));
		    this.solveOperaciones(expressionList, expresion);
		}

	}
	
	
	private void asignaValor(String exp, String id) {
		for(ValoresTabla vt : tablaSimbolos) {
			if(vt.nombre.equals(id)) {
				vt.valor = exp.replace(id,"").replace("=", "");
			}
		}
	}
	
	
	private void solveOperaciones(ArrayList<String> expressionList,String expresion) {
		//int auxCount = 1;
		
		if(expressionList.indexOf(")") != -1){
		    //System.out.println("hay parentesis");
		    int originalIndex;
    		int indexIniciParentesis = expressionList.indexOf("(") +1;
    		int indexFinParentesis = expressionList.indexOf(")") ;
    		ArrayList<String> expressionParentesis = new ArrayList<>(expressionList.subList(indexIniciParentesis, indexFinParentesis));
    		originalIndex = indexIniciParentesis;
    		
    		for(int i = 0; i < expressionParentesis.size(); i++){
    			String current = expressionParentesis.get(i);
    		    String op,oper1,oper2,res;
    		    if(current.equals("*") || current.equals("/")){
    		        op = current;
    		        oper1 = expressionParentesis.get(i-1);
    		        oper2 = expressionParentesis.get(i+1);
    		        res = "Operacion"+auxCount;
    		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
    		        this.cuadruplos.add(new Cuadruplo(op,oper1,oper2,res,expresion));
    		        auxCount ++;
    		        expressionParentesis.set(i, res);
    		        expressionList.set(originalIndex, res);
    		        expressionParentesis.remove(i-1);
    		        expressionList.remove(originalIndex - 1);
    		        expressionParentesis.remove(i);
    		        expressionList.remove(originalIndex);
    			    i--;
    			    originalIndex--;
    		    }
    		    originalIndex++;
	   }
		
		originalIndex = indexIniciParentesis;
		for(int i = 0; i < expressionParentesis.size(); i++){
			String current = expressionParentesis.get(i);
		    String op,oper1,oper2,res;
		    //System.out.println(expressionParentesis.toString());
		    if(current.equals("+") || current.equals("-")){
		        op = current;
		        oper1 = expressionParentesis.get(i-1);
		        oper2 = expressionParentesis.get(i+1);
		        res = "Operacion"+auxCount;
		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
		        this.cuadruplos.add(new Cuadruplo(op,oper1,oper2,res,expresion));
		        auxCount ++;
		        expressionParentesis.set(i, res);
		        expressionList.set(originalIndex, res);
		        expressionParentesis.remove(i-1);
		        expressionList.remove(originalIndex - 1);
		        expressionParentesis.remove(i);
		        expressionList.remove(originalIndex);
			    i--;
			    originalIndex--;
		    }
		    originalIndex++;
	   }
		}
		
		int indexIniciParentesis = expressionList.indexOf("(");
		if(indexIniciParentesis != -1) {
			expressionList.remove(indexIniciParentesis);
			int indexFinParentesis = expressionList.indexOf(")") ;
			expressionList.remove(indexFinParentesis);
		}
		
		
		for(int i = 0; i < expressionList.size(); i++){
		    String current = expressionList.get(i);
		    String op,oper1,oper2,res;
		    if(current.equals("*") || current.equals("/")){
		        op = current;
		        oper1 = expressionList.get(i-1);
		        oper2 = expressionList.get(i+1);
		        res = "Operacion"+auxCount;
		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
		        this.cuadruplos.add(new Cuadruplo(op,oper1,oper2,res,expresion));
		        auxCount ++;
		        expressionList.set(i, res);
		        expressionList.remove(i-1);
			    expressionList.remove(i);
			    i--;
			    //expressionList.remove(i+1);
		    }
		}
		
		for(int i = 0; i < expressionList.size(); i++){
		    String current = expressionList.get(i);
		    String op,oper1,oper2,res;
		    if(current.equals("+") || current.equals("-")){
		        op = current;
		        oper1 = expressionList.get(i-1);
		        oper2 = expressionList.get(i+1);
		        res = "Operacion"+auxCount;
		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
		        this.cuadruplos.add(new Cuadruplo(op,oper1,oper2,res,expresion));
		        auxCount ++;
		        expressionList.set(i, res);
		        expressionList.remove(i-1);
			    expressionList.remove(i);
			    i--;
			    //expressionList.remove(i+1);
		    }
		}
		
		System.out.println(expressionList.get(1)+" "+expressionList.get(2)+" "+""+" "+expressionList.get(0));
        this.cuadruplos.add(new Cuadruplo(expressionList.get(1),expressionList.get(2),"",expressionList.get(0),expresion));
		
	}
	
	private void solveParentesis(ArrayList<String> expressionList, int index){
		int auxCount = 1;
		int originalIndex;
		int indexIniciParentesis = expressionList.indexOf("(") -1;
		int indexFinParentesis = expressionList.indexOf(")") -1;
		ArrayList<String> expressionParentesis = new ArrayList<>(expressionList.subList(indexIniciParentesis, indexFinParentesis));
		originalIndex = indexIniciParentesis;
		
		for(int i = 0; i < expressionParentesis.size(); i++){
			originalIndex++;
			String current = expressionParentesis.get(i);
		    String op,oper1,oper2,res;
		    if(current.equals("*") || current.equals("/")){
		        op = current;
		        oper1 = expressionParentesis.get(i-1);
		        oper2 = expressionParentesis.get(i+1);
		        res = "Operacion"+auxCount;
		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
		        auxCount ++;
		        expressionParentesis.set(i, res);
		        expressionList.set(originalIndex, res);
		        expressionParentesis.remove(i-1);
		        expressionList.remove(originalIndex - 1);
		        expressionParentesis.remove(i);
		        expressionParentesis.remove(originalIndex);
			    i--;
			    originalIndex--;
		    }
	   }
		
		for(int i = 0; i < expressionParentesis.size(); i++){
			originalIndex++;
			String current = expressionParentesis.get(i);
		    String op,oper1,oper2,res;
		    if(current.equals("+") || current.equals("-")){
		        op = current;
		        oper1 = expressionParentesis.get(i-1);
		        oper2 = expressionParentesis.get(i+1);
		        res = "Operacion"+auxCount;
		        System.out.println(op+" "+oper1+" "+oper2+" "+res);
		        auxCount ++;
		        expressionParentesis.set(i, res);
		        expressionList.set(originalIndex, res);
		        expressionParentesis.remove(i-1);
		        expressionList.remove(originalIndex - 1);
		        expressionParentesis.remove(i);
		        expressionParentesis.remove(originalIndex);
			    i--;
			    originalIndex--;
		    }
	   }
	}
	
	public List<String> getAllExpressions(String input,String regex) {
	    final Matcher m = Pattern.compile(regex).matcher(input);

	    final List<String> matches = new ArrayList<>();
	    while (m.find()) {
	        matches.add(m.group(0));
	    }

	    return matches;
	}
	
	
	

}
