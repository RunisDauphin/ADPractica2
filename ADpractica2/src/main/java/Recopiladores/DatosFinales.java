public class DatosFinales {
	
    private int codProvincia;
    private String provincia;
    private int codMunicipio;
    private String municipio;
    private int puntoestacion;
    private int magnitud;
    private int puntomuestreo;
    private int ciclo;
    private int mes;
    private int dia;
    private int hora;
    private double valor;

	public DatosFinales() {
		
	}
	public DatosFinales(int codProvincia, String provincia, int codMunicipio, String municipio, int puntoestacion, int magnitud, int puntomuestreo, int ciclo, int mes, int dia, int hora, double valor) {
		this.codProvincia = codProvincia;
		this.provincia = provincia;
		this.codMunicipio = codMunicipio;
		this.municipio = municipio;
		this.puntoestacion = puntoestacion;
		this.magnitud = magnitud;
		this.puntomuestreo = puntomuestreo;
		this.ciclo = ciclo;
		this.mes = mes;
		this.dia = dia;
		this.hora = hora;
		this.valor = valor;
		
	}
}