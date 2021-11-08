package Recopiladores;

public class CADM {
    private int codProvincia;
    private String provincia;
    private int codMunicipio;
    private String municipio;
    private int puntoestacion;
    private int magnitud;
    private String puntomuestreo;
    private int ciclo;
    private int mes;
    private int dia;
    private int hora;
    private double valor;

    public CADM(
            int codProvincia,
            String provincia,
            int codMunicipio,
            String municipio,
            int puntoestacion,
            int magnitud,
            String puntomuestreo,
            int ciclo,
            int mes,
            int dia,
            int hora,
            double valor
    ) {
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

    public int obtCodProvincia() {
        return codProvincia;
    }

    public void metCodProvincia(int codProvincia) {
        this.codProvincia = codProvincia;
    }

    public String obtProvincia() {
        return provincia;
    }

    public void metProvincia(String provincia) {
        this.provincia = provincia;
    }

    public int obtCodMunicipio() {
        return codMunicipio;
    }

    public void metCodMunicipio(int codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    public String obtMunicipio() {
        return municipio;
    }

    public void metMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int obtPuntoestacion() {
        return puntoestacion;
    }

    public void metPuntoestacion(int puntoestacion) {
        this.puntoestacion = puntoestacion;
    }

    public int obtMagnitud() {
        return magnitud;
    }

    public void metMagnitud(int magnitud) {
        this.magnitud = magnitud;
    }

    public String obtPuntomuestreo() {
        return puntomuestreo;
    }

    public void metPuntomuestreo(String puntomuestreo) {
        this.puntomuestreo = puntomuestreo;
    }

    public int obtCiclo() {
        return ciclo;
    }

    public void metCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    public int obtMes() {
        return mes;
    }

    public void metMes(int mes) {
        this.mes = mes;
    }

    public int obtDia() {
        return dia;
    }

    public void metDia(int dia) {
        this.dia = dia;
    }

    public int obtHora() {
        return hora;
    }

    public void metHora(int hora) {
        this.hora = hora;
    }

    public double obtValor() {
        return valor;
    }

    public void metValor(double valor) {
        this.valor = valor;
    }
}
