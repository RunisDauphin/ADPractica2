package Recopiladores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

public class RecopiladorCADMM {
    Marshaller marshaller;
    Unmarshaller unmarshaller;
    
    Principal principal = new Principal();
    String ruta = "." + File.separator + "calidad_aire_datos_mes.csv";
    String linea = "";
    int contador = 0;
    String localidad = null;
    String rutadestino = null;
    int codProvincia = 0;
    String provincia = "";
    int codMunicipio = 0;
    String municipio = "";
    int puntoestacion = 0;
    int magnitud = 0;
    String puntomuestreo = "";
    int ciclo = -1;
    int mes = -1;
    int dia = -1;
    int hora = -1;
    double valor = -1.0;
    
    ArrayList<CADMM> estaciones = null;
    CADMM estacion = null;
    
    public RecopiladorCADMM(String localidad, String rutadestino) {
        List<CADMM> lista = new ArrayList<CADMM>();
        this.localidad = localidad;
        this.rutadestino = rutadestino;
        try {
            BufferedReader deposito = new BufferedReader(new FileReader(ruta));
            estaciones = new ArrayList();

            while((linea = deposito.readLine()) != null) {
                contador = contador + 1;
                if(contador == 1) {
                    continue;
                }
                String[] valores = linea.split(";");
                if(contador > 1) {
                    codProvincia = Integer.parseInt(valores[0]);
                    provincia = encontrarprovincia(codProvincia);
                    codMunicipio = Integer.parseInt(valores[1]);
                    municipio = encontrarmunicipio(codMunicipio);
                    puntoestacion = Integer.parseInt(valores[2]);
                    magnitud = Integer.parseInt(valores[3]);
                    puntomuestreo = valores[4].substring(0, 7);
                    ciclo = Integer.parseInt(valores[5]);
                    mes = Integer.parseInt(valores[6]);
                    dia = Integer.parseInt(valores[7]);
                    hora = -1;
                    valor = -1.0;
                    for(int i = 0; i < 24 ; i = i + 1) {
                        if(valores[9 + 2 * i].charAt(0) == 'V' && valores[8 + 2 * i] != null) {
                            hora = i;
                            String temporal = valores[8 + 2 * i];
                            if(temporal.contains(",")) {
                                temporal.replaceAll(",", ".");
                            }
                            valor = Double.parseDouble(temporal);
                            estacion = new CADMM(
                                    codProvincia,
                                    provincia,
                                    codMunicipio,
                                    municipio,
                                    puntoestacion,
                                    magnitud,
                                    puntomuestreo,
                                    ciclo,
                                    mes,
                                    dia,
                                    hora,
                                    valor
                            );
                            if(estaciones.size() > 0) {
                                estaciones.add(ordenar(estaciones, estacion), estacion);
                            }
                            else {
                                estaciones.add(estacion);
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException exception) {
            Logger.getLogger(RecopiladorCADMM.class.getName()).log(Level.SEVERE, null, exception);
        }
        catch (IOException exception) {
            Logger.getLogger(RecopiladorCADMM.class.getName()).log(Level.SEVERE, null, exception);
        }
        
        for(int i = 0; i < estaciones.size(); i = i + 1) {
            lista.add(estaciones.get(i));
        }
        
        for(int i = 0; i < estaciones.size(); i = i + 1) {
            if(estaciones.get(i).obtMunicipio().compareTo(localidad) == 0) {
                break;
            }
            if((estaciones.get(estaciones.size() - 1).obtMunicipio().compareTo(localidad)) != 0 && i == estaciones.size() - 1) {
                System.out.println("La localidad no est\u00E1 en la base de datos. Presione una tecla para salir.");
                System.err.println("La localidad no est\u00E1 en la base de datos. Presione una tecla para salir.");
                try {
                    System.in.read();
                }
                catch (IOException ex) {
                    Logger.getLogger(RecopiladorCADMM.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(1);
            }
        }
        
        /*En la base de datos no aparece más de una estación por municipio (corroborado manualmente).
        A efectos prácticos, el programa cree que solo hay una estación por municipio,
        independientemente de cuántas haya en el municipio,
        pero coge todos los datos de todas las estaciones que vengan del municipio.*/
        
        System.out.println("Muestras de velocidad del viento tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 81, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 81, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 81, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 81, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 81, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 81, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 81, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 81, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de temperatura tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 83, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 83, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 83, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 83, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 83, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 83, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 83, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 83, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de humedad tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 86, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 86, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 86, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 86, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 86, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 86, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 86, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 86, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de presi\u00F3n atmosf\u00E9rica tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 87, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 87, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 87, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 87, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 87, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 87, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 87, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 87, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de radiaci\u00F3n solar tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 88, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 88, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 88, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 88, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 88, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 88, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 88, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 88, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de precipitaciones (agua ca\u00EDda) tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 89, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 89, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 89, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 89, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 89, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 89, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 89, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 89, localidad)).obtHora() + ":00.");
        
        System.out.println("Valor m\u00E1ximo de velocidad del viento registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 81, localidad)).obtValor() + " m/s, ocurrido el " +
                estaciones.get(maximo(estaciones, 81, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 81, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 81, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 81, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de temperatura registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 83, localidad)).obtValor() + " ºC, o" + 
                estaciones.get(maximo(estaciones, 83, localidad)).obtValor() + 273 + " K, ocurrido el " +
                estaciones.get(maximo(estaciones, 83, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 83, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 83, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 83, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de humedad registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 86, localidad)).obtValor() + " %, ocurrido el " +
                estaciones.get(maximo(estaciones, 86, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 86, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 86, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 86, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de presi\u00F3n atmosf\u00E9rica registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 87, localidad)).obtValor() + " milibares, ocurrido el " +
                estaciones.get(maximo(estaciones, 87, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 87, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 87, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 87, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de radicaci\u00F3n solar registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 88, localidad)).obtValor() + " W/m2, ocurrido el " +
                estaciones.get(maximo(estaciones, 88, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 88, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 88, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 88, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de precipitaciones (agua ca\u00EDda) registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 89, localidad)).obtValor() + " l/m2, ocurrido el " +
                estaciones.get(maximo(estaciones, 89, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 89, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 89, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 89, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de velocidad del viento registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 81, localidad)).obtValor() + " m/s, ocurrido el " +
                estaciones.get(minimo(estaciones, 81, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 81, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 81, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 81, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de temperatura registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 83, localidad)).obtValor() + " ºC, o" + 
                estaciones.get(minimo(estaciones, 83, localidad)).obtValor() + 273 + " K, ocurrido el " +
                estaciones.get(minimo(estaciones, 83, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 83, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 83, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 83, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de humedad registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 86, localidad)).obtValor() + " %, ocurrido el " +
                estaciones.get(minimo(estaciones, 86, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 86, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 86, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 86, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de presi\u00F3n atmosf\u00E9rica registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 87, localidad)).obtValor() + " milibares, ocurrido el " +
                estaciones.get(minimo(estaciones, 87, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 87, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 87, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 87, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de radicaci\u00F3n solar registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 88, localidad)).obtValor() + " W/m2, ocurrido el " +
                estaciones.get(minimo(estaciones, 88, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 88, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 88, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 88, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de precipitaciones (agua ca\u00EDda) registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 89, localidad)).obtValor() + " l/m2, ocurrido el " +
                estaciones.get(minimo(estaciones, 89, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 89, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 89, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 89, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor medio de velocidad del viento registrado en " +
                localidad + ": " +
                media(estaciones, 81, localidad) + " m/s."
        );
        
        System.out.println("Valor medio de temperatura registrado en " +
                localidad + ": " +
                media(estaciones, 83, localidad) + " ºC, o" + 
                media(estaciones, 83, localidad) + 273 + " K."
        );
        
        System.out.println("Valor medio de humedad registrado en " +
                localidad + ": " +
                media(estaciones, 86, localidad) + " %."
        );
        
        System.out.println("Valor medio de presi\u00F3n atmosf\u00E9rica registrado en " +
                localidad + ": " +
                media(estaciones, 87, localidad) + " milibares."
        );
        
        System.out.println("Valor medio de radicaci\u00F3n solar registrado en " +
                localidad + ": " +
                media(estaciones, 88, localidad) + " W/m2."
        );
        
        System.out.println("Valor medio de precipitaciones (agua ca\u00EDda) registrado en " +
                localidad + ": " +
                media(estaciones, 89, localidad) + " l/m2."
        );

        try {
            System.in.read();
        }
        catch (IOException ex) {
            Logger.getLogger(RecopiladorCADMM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int ordenar(ArrayList<CADMM> conjunto, CADMM elemento) {
        if(conjunto.size() == 1) {
            if(conjunto.get(0).obtMagnitud() < elemento.obtMagnitud()) {
                return 1;
            }
            else if(conjunto.get(0).obtMagnitud() > elemento.obtMagnitud()) {
                return 0;
            }
            else {
                if(conjunto.size() == 1) {
                    if(conjunto.get(0).obtCodProvincia() < elemento.obtCodProvincia()) {
                        return 1;
                    }
                    else if(conjunto.get(0).obtCodProvincia() > elemento.obtCodProvincia()) {
                        return 0;
                    }
                    else {
                        if(conjunto.get(0).obtCodMunicipio() < elemento.obtCodMunicipio()) {
                            return 1;
                        }
                        else if(conjunto.get(0).obtCodMunicipio() > elemento.obtCodMunicipio()) {
                            return 0;
                        }
                        else {
                            if(conjunto.size() == 1) {
                                if(conjunto.get(0).obtCiclo() < elemento.obtCiclo()) {
                                    return 1;
                                }
                                else if(conjunto.get(0).obtCiclo() > elemento.obtCiclo()) {
                                    return 0;
                                }
                                else {
                                    if(conjunto.size() == 1) {
                                        if(conjunto.get(0).obtMes() < elemento.obtMes()) {
                                            return 1;
                                        }
                                        else if(conjunto.get(0).obtMes() > elemento.obtMes()) {
                                            return 0;
                                        }
                                        else {
                                            if(conjunto.size() == 1) {
                                                if(conjunto.get(0).obtDia() < elemento.obtDia()) {
                                                    return 1;
                                                }
                                                else if(conjunto.get(0).obtDia() > elemento.obtDia()) {
                                                    return 0;
                                                }
                                                else {
                                                    if(conjunto.get(0).obtHora() < elemento.obtHora()) {
                                                        return 1;
                                                    }
                                                    else if(conjunto.get(0).obtHora() > elemento.obtHora()) {
                                                        return 0;
                                                    }
                                                    else {
                                                        if(conjunto.size() == 1) {
                                                            if(conjunto.get(0).obtValor() < elemento.obtValor()) {
                                                                return 1;
                                                            }
                                                            else {
                                                                return 0;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if(conjunto.get(conjunto.size() / 2).obtMagnitud() == elemento.obtMagnitud()) {
            if(conjunto.get(conjunto.size() / 2).obtProvincia().compareTo(elemento.obtProvincia()) == 0) {
                if(conjunto.get(conjunto.size() / 2).obtMunicipio().compareTo(elemento.obtMunicipio()) == 0) {
                    if(conjunto.get(conjunto.size() / 2).obtCiclo() == elemento.obtCiclo()) {
                        if(conjunto.get(conjunto.size() / 2).obtMes() == elemento.obtMes()) {
                            if(conjunto.get(conjunto.size() / 2).obtDia() == elemento.obtDia()) {
                                if(conjunto.get(conjunto.size() / 2).obtHora() == elemento.obtHora()) {
                                    if(conjunto.get(conjunto.size() / 2).obtValor() == elemento.obtValor()) {
                                        return conjunto.size()/2;
                                    }
                                    else if(conjunto.get(conjunto.size() / 2).obtValor() > elemento.obtValor()) {
                                        return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                                    }
                                    else {
                                        return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                                    }
                                }
                                else if(conjunto.get(conjunto.size() / 2).obtHora() > elemento.obtHora()) {
                                    return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                                }
                                else {
                                    return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                                }
                            }
                            else if(conjunto.get(conjunto.size() / 2).obtDia() > elemento.obtDia()) {
                                return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                            }
                            else {
                                return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                            }
                        }
                        else if(conjunto.get(conjunto.size() / 2).obtMes() > elemento.obtMes()) {
                            return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                        }
                        else {
                            return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                        }
                    }
                    else if(conjunto.get(conjunto.size() / 2).obtCiclo() > elemento.obtCiclo()) {
                        return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                    }
                    else {
                        return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                    }
                }
                else if(conjunto.get(conjunto.size() / 2).obtCodMunicipio() > elemento.obtCodMunicipio()) {
                    return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                }
                else {
                    return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                }
            }
            else if(conjunto.get(conjunto.size() / 2).obtCodProvincia() > elemento.obtCodProvincia()) {
                return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
            }
            else {
                return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
            }
        }
        else if(conjunto.get(conjunto.size() / 2).obtMagnitud() > elemento.obtMagnitud()) {
            return ordenar((ArrayList<CADMM>) conjunto.subList(0, conjunto.size() / 2), elemento);
        }
        return ordenar((ArrayList<CADMM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
    }
    
    public int maximo(ArrayList<CADMM> conjunto, int magnitud, String localidad) {
        int indice = -1;
        double dato = 0;
        double acumulador = 0;
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtValor();
                if(i == 0 || (i != 0 && dato > acumulador)) {
                        indice = i;
                        acumulador = dato;
                }
            }
        }

        return indice;
    }
    
    public int minimo(ArrayList<CADMM> conjunto, int magnitud, String localidad) {
        int indice = -1;
        double dato = 0;
        double acumulador = 0;
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtValor();
                if(i == 0 || (i != 0 && dato < acumulador)) {
                    indice = i;
                    acumulador = dato;
                }
            }
        }

        return indice;
    }
    
    public double media(ArrayList<CADMM> conjunto, int magnitud, String localidad) {
        double dato = 0;
        double acumulador = -1;
        double talla = 0;
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtValor();
                acumulador = acumulador + dato;
                talla = talla + 1;
            }
        }

        return acumulador / talla;
    }
    
    public int inicioplazo(ArrayList<CADMM> conjunto, int magnitud, String localidad) {
        int indice = -1;
        double dato = 0;
        double ciclo = 0;
        double mes = 0;
        double dia = 0;
        double hora = 0;
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtCiclo();
                if(i == 0 || (i != 0 && dato < ciclo)) {
                    indice = i;
                    ciclo = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtMes();
                if((i == 0 || (i != 0 && dato < mes)) &&
                        conjunto.get(i).obtCiclo() == ciclo) {
                    indice = i;
                    mes = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtDia();
                if((i == 0 || (i != 0 && dato < dia)) &&
                        conjunto.get(i).obtCiclo() == ciclo &&
                        conjunto.get(i).obtMes() == mes) {
                    indice = i;
                    dia = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtHora();
                if((i == 0 || (i != 0 && dato < hora)) &&
                        conjunto.get(i).obtCiclo() == ciclo &&
                        conjunto.get(i).obtMes() == mes &&
                        conjunto.get(i).obtDia() == dia) {
                    indice = i;
                    hora = dato;
                }
            }
        }

        return indice;
    }
    
    public int finplazo(ArrayList<CADMM> conjunto, int magnitud, String localidad) {
        int indice = -1;
        double dato = 0;
        double ciclo = 0;
        double mes = 0;
        double dia = 0;
        double hora = 0;
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtCiclo();
                if(i == 0 || (i != 0 && dato > ciclo)) {
                    indice = i;
                    ciclo = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtMes();
                if((i == 0 || (i != 0 && dato > mes)) &&
                        conjunto.get(i).obtCiclo() == ciclo) {
                    indice = i;
                    mes = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtDia();
                if((i == 0 || (i != 0 && dato > dia)) &&
                        conjunto.get(i).obtCiclo() == ciclo &&
                        conjunto.get(i).obtMes() == mes) {
                    indice = i;
                    dia = dato;
                }
            }
        }
        for(int i = 0; i < conjunto.size(); i = i + 1) {
            if(localidad.compareTo("Todos") == 0 || conjunto.get(i).obtMunicipio().compareTo(localidad) == 0) {
                dato = conjunto.get(i).obtHora();
                if((i == 0 || (i != 0 && dato > hora)) &&
                        conjunto.get(i).obtCiclo() == ciclo &&
                        conjunto.get(i).obtMes() == mes &&
                        conjunto.get(i).obtDia() == dia) {
                    indice = i;
                    hora = dato;
                }
            }
        }

        return indice;
    }
    
    public String encontrarprovincia(int codProvincia) {
        switch(codProvincia) {
            case 28:
                return "Madrid";
            default:
                return "";
        }
    }
    
    public String encontrarmunicipio(int codMunicipio) {
        switch(codMunicipio) {
            case 5:
                return "Alcal\u00E1 de Henares";
            case 6:
                return "Alcobendas";
            case 7:
                return "Alcorc\u00F3n";
            case 9:
                return "Algete";
            case 13:
                return "Aranjuez";
            case 14:
                return "Arganda del Rey";
            case 16:
                return "El Atazar";
            case 45:
                return "Colmenar Viejo";
            case 47:
                return "Collado Villalba";
            case 49:
                return "Coslada";
            case 58:
                return "Fuenlabrada";
            case 65:
                return "Getafe";
            case 67:
                return "Guadalix de la Sierra";
            case 74:
                return "Legan\u00E9s";
            case 80:
                return "Majadahonda";
            case 92:
                return "M\u00F3stoles";
            case 102:
                return "Orusco de Taju\u00F1a";
            case 120:
                return "Puerto de Cotos";
            case 123:
                return "Rivas-Vaciamadrid";
            case 133:
                return "San Mart\u00EDn de Valdeiglesias";
            case 148:
                return "Torrej\u00F3n de Ardoz";
            case 161:
                return "Valdemoro";
            case 171:
                return "Villa del Prado";
            case 180:
                return "Villarejo de Salvan\u00E9s";
            default:
                return "";
        }
    }
}