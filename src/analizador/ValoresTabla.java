package analizador;

public class ValoresTabla implements IPresentable{

	public String rango, tipo, nombre, valor, renglon, columna, ambito;
	
	public ValoresTabla(String ran, String tip, String nom, String val, String reng, String col) {
		
		rango = ran;
		tipo = tip;
		nombre = nom;
		valor = val;
		renglon = reng;
		columna = col;
	}

	@Override
	public String[] getValues() {
		String[] values = {nombre,tipo,valor,renglon,columna};
		return values;
	}
	
}