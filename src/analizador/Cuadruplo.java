package analizador;

public class Cuadruplo implements IPresentable{
	
	public Cuadruplo(String operador, String operando1, String operando2, String resultado, String expresion) {
		this.resultado = resultado;
		this.operador = operador;
		this.operando1 = operando1;
		this.operando2 = operando2;
		this.expresion = expresion;
	}
	
	private String resultado;
	private String operador;
	private String operando1;
	private String operando2;
	private String expresion;
	
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getOperador() {
		return operador;
	}
	public void setOperador(String operador) {
		this.operador = operador;
	}
	public String getOperando1() {
		return operando1;
	}
	public void setOperando1(String operando1) {
		this.operando1 = operando1;
	}
	public String getOperando2() {
		return operando2;
	}
	public void setOperando2(String operando2) {
		this.operando2 = operando2;
	}
	public String getExpresion() {
		return expresion;
	}
	public void setExpresion(String expresion) {
		this.expresion = expresion;
	}
	@Override
	public String[] getValues() {
		String[] values = {expresion,operador,operando1,operando2,resultado};
		return values;
	}

}
