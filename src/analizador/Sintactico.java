package analizador;
import java.util.ArrayList;

//import analizadorlexico.copy.Lista3;

public class Sintactico<T> {
	ArrayList<Token> tokenRC;

	ArrayList<String> token;
	ArrayList<Integer> tipo;
	String tok = "", esperado = "";
	int type, contando = 0, flag = 0;
	String estructura = "";

	final int clase = 0, publico = 1, privado = 2, whilex = 3, entero = 4, booleano = 5, llaveizq = 6, llaveder = 7,
			EQ = 8, semi = 9, menor = 10, mayor = 11, d2EQ = 12, menorEQ = 13, mayorEQ = 14, diferente = 15, difEQ = 16,
			truex = 17, falsex = 18, brackizq = 19, brackder = 20, div = 21, mas = 22, menos = 23, mult = 24, ifx = 25,
			num = 50, ID = 52; // bool = 21,

//	public Sintactico(ArrayList<String> token, ArrayList<Integer> tipo) 
	public Sintactico(ArrayList<Token> tokenRC) {
		this.tokenRC = tokenRC;
//		this.token = token;
//		this.tipo = tipo; 
		try {

			this.tok = this.tokenRC.get(0).getToken();
			this.type = this.tokenRC.get(0).getTipo();
//			this.type = this..get(0);
//			this.tok = this.token.get(0);
		} catch (Exception e) {
			Main.consola.append("El archivo esta vacio");
		}
		Programa();
	}

	public void Advance() {
		try {
		type = tokenRC.get(contando).getTipo();
		tok = tokenRC.get(contando).getToken();
		}catch(Exception e) {
			
		}
	}

	public void eat(int esp) {
		if (type == esp)// if( ')' == ';' )
		{
			contando++;
			if (contando < tokenRC.size()) {
				Advance();
			}
		} else {
			error(esp);
		}
	}

	public void Programa() {

//		Sx s = null;
		if (type == publico || type == privado)
			eat(type);
		eat(clase);
		eat(ID);

		eat(llaveizq);

		while (type == publico || type == privado) {
			eat(type);
			Declaracion();
		}
		while (type == entero || type == booleano)
			Declaracion();
		if (this.type == whilex || this.type == ifx || this.type == entero || this.type == booleano)
			Statuto();
		eat(llaveder);

//		System.out.println((tokenRC.size()) + " contador = " + contando);
		if (contando < tokenRC.size())
			error(1);
		estructura = "estructura correcta";
//		System.out.println(estructura);

	}

	public void Declaracion() {
		String tok;
		switch (type) {
		case entero:
			eat(entero);

			tok = this.tok;
			eat(ID);
			if (type == EQ) {
				eat(EQ);

				if (type == num)
					eat(num);

				else if (type == falsex)
					eat(falsex);

				else // if(type == truex)
					eat(truex);
			}
			eat(semi);
			break;
		case booleano:
			eat(booleano);
			tok = this.tok;
			eat(ID);
			if (type == EQ) {
				eat(EQ);

				if (type == num) {
					eat(num);
				}

				else if (type == falsex) {
					eat(falsex);
				} else // if(type == truex)
				{
					eat(truex);
				}
			}
			eat(semi);

		}
	}

	public void VarDeclarator() {
		eat(EQ);

		if (type == num)
			eat(num);

		if (type == falsex)
			eat(falsex);

		if (type == truex)
			eat(truex);
	}

	public void Statuto() {
		switch (type) {
		case ifx:
			eat(ifx);
			eat(brackizq);

			TestingExp();
			eat(brackder);

			eat(llaveizq);

			while (type == whilex || type == ifx || type == ID || type == booleano || type == entero)
				Statuto(); // para llamar otro statement dentro del statement
			eat(llaveder);

			break;

		case whilex:
			eat(whilex);
			eat(brackizq);

			TestingExp();
			eat(brackder);
			eat(llaveizq);
			while (type == whilex || type == ifx || type == booleano || type == entero || type == publico
					|| type == privado)
				Statuto(); // para llamar otro statement dentro del statement
			eat(llaveder);
			break;
		case ID:
			eat(ID);
			eat(EQ);

			ArithmeticExp();
			eat(semi);
			while (type == whilex || type == ifx || type == booleano || type == entero)
				Statuto(); // para llamar otro statement dentro del statement
			break;
		case booleano:
			Declaracion();
			break;
		case entero:
			Declaracion();
			break;
		case publico:
			eat(publico);
			Declaracion();
			break;
		case privado:
			eat(privado);
			Declaracion();
			break;
		default:
			error();
		}
	}

	public void TestingExp() {

		switch (type) {
		case ID:
			if (type == ID) {
				eat(ID);
			} else// if(type == num)
				eat(num);

			if (LogicSimbols())
				if (type == ID) {
					eat(ID);
				} else // if(type == num)
					eat(num);
			break;
		default:
			error();
			break;
		}
	}

	public void ArithmeticExp() {

		switch (type) {
		case num:

			eat(num);

		{
			if (OperandoSimbols())
				eat(num);
		}

			break;
		default:
			error();
			break;
		}
	}

	public void error(int type) {
		String tipo = ValoresInversos(type);
		if (type == 0)
			tipo = "\nError sintactico, se esperaba una expresion *class* al comienzo";
		else if (type == 1)
			tipo = "\nError sintactico en los limites, se encontro al menos un token despues de la ultima llave cerrada, token ** "
					+ tok + " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** "
					+ tokenRC.get(contando).getColumna() + " **";
		else if (type == 2)
			tipo = "\nError sintactico en asignacion, se esperaba un operador y operando antes de ** " + tok
					+ " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** "
					+ tokenRC.get(contando).getColumna() + " **";
		else if (type == 3)
			tipo = "\nError sintactico en validacion, se esperaba un operador logico en lugar de ** " + tok
					+ " ** en linea ** " + tokenRC.get(contando).getRenglon() + " **, No. de token ** "
					+ tokenRC.get(contando).getColumna() + " **";
		else
			tipo = "\nError sintactico en token ** " + tok + " ** se esperaba un token ** "
					+ tipo + " **";

		Main.consola.append(tipo);
	}

	public void error() {
		Main.consola.append("Error en la sintaxis, con el siguiente token ** " + tok + " ** en linea ** "
				+ tokenRC.get(contando).getRenglon() + " **, No. de token ** " + tokenRC.get(contando).getColumna()
				+ " **");
	}

	public boolean LogicSimbols() {
		if (type == menor || type == mayor || type == menorEQ || type == mayorEQ
				|| type == d2EQ/* type == mayor || type == dobleEQ || */ ) {
			eat(type);
			return true;
		} else
			error(3);
		return false;
	}

	public boolean OperandoSimbols() {
		if (type == menos || type == mas || type == div || type == mult) {
			eat(type);
			return true;
		} else
			error(2);
		return false;
	}

	public String ValoresInversos(int type) {
		String devuelve,
				cadenas[] = { "class", "public", "private", "while", "int", "boolean", "{", "}", "=", ";", "<", ">", // 12...
																														// Aunque
																														// no
																														// se
																														// usa
																														// como
																														// tal
																														// el
																														// "!"
																														// solo,
																														// sirve
																														// para
																														// que
																														// no
																														// lance
																														// error
						"==", "<=", ">=", "!", "!=", "true", "false", "(", ")", "/", "+", "-", "*", "if" };
		if (type == 50)
			return devuelve = "numerico";
		if (type == 52)
			return devuelve = "identificador";
		devuelve = cadenas[type];

		return devuelve;
	}
}