package analizador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Semantico {
	private ArrayList<ValoresTabla> tablaSimbolos;
	private ArrayList<String> errors;
	private String code;
	public boolean status;
	
	private String[] operators = {"==","+","-",">","<",">=","<=","!="};
	private List<String> operatorsForNumbers = Arrays.asList("+","-","==",">","<",">=","<=","!=");
	private List<String> operatorsForBooleans = Arrays.asList();
	
	public Semantico(ArrayList<ValoresTabla> tablaSimbolos, String code) {
		this.tablaSimbolos = tablaSimbolos;
		this.code = code;
		errors = new ArrayList<String>();
	}

	public ArrayList<ValoresTabla> getTablaSimbolos() {
		return tablaSimbolos;
	}

	public void setTablaSimbolos(ArrayList<ValoresTabla> tablaSimbolos) {
		this.tablaSimbolos = tablaSimbolos;
	}
	
	public ArrayList<String> checkSemantic(){
		CheckAssignments();
		checkUndeclared();
		checkDuplicity();
		checkOperators();
		if(this.errors.size() == 0) {
			status = true;
			errors.add("No hay errores semanticos");
		}
		return this.errors;
	}
	
	public boolean CheckAssignments() {
		for(int i = 0; i < tablaSimbolos.size(); i++ ) {
			ValoresTabla currentIdentifier = tablaSimbolos.get(i);
			if(currentIdentifier.tipo.equals("int") && this.isBooleanValue(currentIdentifier.valor)) {
				errors.add("Error semantico en linea " +currentIdentifier.renglon +", identificador "+ currentIdentifier.nombre +" Se intenta asignar un valor boolean a un entero");
			}
			if(currentIdentifier.tipo.equals("boolean") && this.isIntValue(currentIdentifier.valor)) {
				errors.add("Error semantico en linea " +currentIdentifier.renglon +", identificador "+ currentIdentifier.nombre +" Se intenta asignar un valor entero a un boolean");
			}
		}
		return true;
	}
	
	public boolean checkUndeclared() {
		String[] lines = code.split("\\r?\\n"); 
		for(int i = 0; i < lines.length; i++) { 
			if(lines[i].contains(" class ")) {
				continue;
			}
			List<String> identifiers = this.getAllIdentifiers(lines[i]);
			for(int j = 0; j < identifiers.size(); j++) {
				String currentIdentifier = identifiers.get(j);
				if(!this.existIdentifier(currentIdentifier,i)) {
					errors.add("Error semantico en linea "+(i+1)+", el identificador "+ currentIdentifier + " no ha sido declarado");
				}
			}
		}
		
		return true;
	}
	
	public boolean checkDuplicity() {
			
			for(int i = 0; i < tablaSimbolos.size(); i++ ) {
				ValoresTabla currentIdentifier = tablaSimbolos.get(i);
				int identifierLine = Integer.parseInt(currentIdentifier.renglon);
				ValoresTabla original = this.searchDuplicityByName(currentIdentifier.nombre,identifierLine);
				if(original != null) {
					errors.add("Error semantico en linea "+identifierLine+", el identificador "+ currentIdentifier.nombre + " ya ha sido declarado en la linea "+original.renglon);
				}
			}
			return true;
		}
	
	public boolean checkOperators() {
		
		String[] lines = code.split("\\r?\\n"); 
		for(int i = 0; i < lines.length; i++) { 
			List<String> expressions = this.getAllIdentifiers(lines[i],"[a-z]{1,40}[0-9]{1,40}[\\s]{0,3}(\\+|-|=|>|<|>=|<=|&|\\||%|!|\\^|\\(|\\=)[\\s]{0,3}[a-z]{1,40}[0-9]{1,40}");
			for(int j = 0; j < expressions.size(); j++) {
				String currentExpression = expressions.get(j);
				String operator = this.getOperator(currentExpression);
				if(currentExpression.indexOf(" ") == -1) {
					currentExpression = currentExpression.replace(operator, " "+operator+" ");
				}
				String[] expressionElements = currentExpression.split("[\\s]{1,10}");
				String result = this.checkCompatibility(expressionElements[0], expressionElements[2], expressionElements[1]);
				if(result != null) {
					errors.add("Error semantico en linea "+(i+1)+", " +  result);
				}
			}
		}
		
		return true;
	}
	
	
	
	public boolean isBooleanValue(String value) {
		return value.equals("false") || value.equals("true");
	}
	
	public boolean isIntValue(String value) {
		char[] chars = value.toCharArray();

	    for (char c : chars) {
	        if(Character.isLetter(c)) {
	            return false;
	        }
	    }

	    return true;
	}
	
	public List<String> getAllIdentifiers(String input) {
	    final String regex = "[a-z]{1,40}[0-9]{1,40}";

	    final Matcher m = Pattern.compile(regex).matcher(input);

	    final List<String> matches = new ArrayList<>();
	    while (m.find()) {
	        matches.add(m.group(0));
	    }

	    return matches;
	}
	
	private boolean existIdentifier(String name, int linea) {
		for(ValoresTabla simbolo: this.tablaSimbolos) {
			if(simbolo.nombre.equals(name) && Integer.parseInt(simbolo.renglon) <= (linea + 1)) {
				return true;
			}
		}
		return false;
	}
	
	private ValoresTabla searchDuplicityByName(String name,int linea) {
		for(ValoresTabla simbolo: this.tablaSimbolos) {
			if(simbolo.nombre.equals(name) && Integer.parseInt(simbolo.renglon) < linea ) {
				/*if(this.sameScope(simbolo)) {
					return simbolo;
				}
				return null;*/
				return simbolo;
			}
		}
		return null;
	}
	
	public List<String> getAllIdentifiers(String input,String regex) {
	    final Matcher m = Pattern.compile(regex).matcher(input);

	    final List<String> matches = new ArrayList<>();
	    while (m.find()) {
	        matches.add(m.group(0));
	    }

	    return matches;
	}
	
	private String getOperator(String expression) {
		String operator = null;
		for(int i =0; i< operators.length; i++ ){
            if(expression.contains(operators[i])){
                operator = operators[i];
            }
        }
		return operator;
	}
	
	private String checkCompatibility(String param1, String param2, String operator) {
		ValoresTabla param1Obj = getSymbolByName(param1);
		ValoresTabla param2Obj = getSymbolByName(param2);
		//String type = operatorsForNumbers.contains(operator) ? "int":"boolean";
		//if(param1Obj.tipo != param2Obj.tipo) {
			//return "los valores dados no son validos para el operador ("+operator+") valores :"+param1Obj.valor+", "+param2Obj.valor;
		//}
		if(param1Obj == null) {
			return null;
		}
		if(param2Obj == null) {
			return null;
		}
		if(operatorsForNumbers.contains(operator) && (param1Obj.tipo.equals("boolean") || param2Obj.tipo.equals("boolean"))) {
			 return "los valores dados no son validos para el operador ("+operator+") valores :"+param1Obj.valor+", "+param2Obj.valor;
		}
		/*if(operatorsForNumbers.contains(operator)) {
			 if(!param1Obj.tipo.equals("int") || !param2Obj.tipo.equals("int")) {
				 return "los valores dados no son validos para el operador ("+operator+") valores :"+param1Obj.valor+", "+param2Obj.valor;
			 }
			 return null;
		}
		if(operatorsForBooleans.contains(operator)) {
			if(!param1Obj.tipo.equals("int") || !param2Obj.tipo.equals("int")) {
				 return "los valores dados no son validos para el operador ("+operator+") valores :"+param1Obj.valor+", "+param2Obj.valor;
			 }
			return "";
		}*/
		return null;
	}
	
	private ValoresTabla getSymbolByName(String name) {
		for(ValoresTabla simbolo: this.tablaSimbolos) {
			if(simbolo.nombre.equals(name)) {
				return simbolo;
			}
		}
		return null;
	}

}
