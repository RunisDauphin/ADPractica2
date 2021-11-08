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

public class RecopiladorCADM {
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
    
    ArrayList<CADM> estaciones = null;
    CADM estacion = null;
    
    public RecopiladorCADM(String localidad, String rutadestino) {
        List<CADM> lista = new ArrayList<CADM>();
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
                            estacion = new CADM(
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
            Logger.getLogger(RecopiladorCADM.class.getName()).log(Level.SEVERE, null, exception);
        }
        catch (IOException exception) {
            Logger.getLogger(RecopiladorCADM.class.getName()).log(Level.SEVERE, null, exception);
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
                    Logger.getLogger(RecopiladorCADM.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(1);
            }
        }
        
        /*En la base de datos no aparece más de una estación por municipio (corroborado manualmente).
        A efectos prácticos, el programa cree que solo hay una estación por municipio,
        independientemente de cuántas haya en el municipio,
        pero coge todos los datos de todas las estaciones que vengan del municipio.*/
        
        System.out.println("Muestras de SO2 tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 1, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 1, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 1, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 1, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 1, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 1, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 1, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 1, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de CO tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 6, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 6, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 6, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 6, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 6, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 6, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 6, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 6, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de NO tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 7, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 7, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 7, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 7, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 7, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 7, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 7, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 7, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de NO2 tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 8, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 8, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 8, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 8, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 8, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 8, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 8, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 8, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de <PM2,5 tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 9, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 9, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 9, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 9, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 9, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 9, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 9, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 9, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de <PM10 tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 10, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 10, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 10, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 10, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 10, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 10, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 10, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 10, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de NOx tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 12, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 12, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 12, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 12, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 12, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 12, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 12, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 12, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de O3 tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 14, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 14, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 14, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 14, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 14, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 14, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 14, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 14, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de tolueno (metilbenceno) tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 20, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 20, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 20, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 20, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 20, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 20, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 20, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 20, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de carb\u00F3n negro tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 22, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 22, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 22, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 22, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 22, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 22, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 22, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 22, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de benceno tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 30, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 30, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 30, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 30, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 30, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 30, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 30, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 30, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de hidrocarburos totales tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 42, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 42, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 42, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 42, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 42, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 42, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 42, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 42, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de hidrocarburos no met\u00E1nicos tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 44, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 44, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 44, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 44, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 44, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 44, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 44, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 44, localidad)).obtHora() + ":00.");
        
        System.out.println("Muestras de metaparaxilenos (dimetilbencenos) tomadas en " + localidad + " entre el " +
                estaciones.get(inicioplazo(estaciones, 431, localidad)).obtDia() + "/" +
                estaciones.get(inicioplazo(estaciones, 431, localidad)).obtMes() + "/" +
                estaciones.get(inicioplazo(estaciones, 431, localidad)).obtCiclo() + ", a las" +
                estaciones.get(inicioplazo(estaciones, 431, localidad)).obtHora() + ":00" +
                "y el" +
                estaciones.get(finplazo(estaciones, 431, localidad)).obtDia() + "/" +
                estaciones.get(finplazo(estaciones, 431, localidad)).obtMes() + "/" +
                estaciones.get(finplazo(estaciones, 431, localidad)).obtCiclo() + ", a las" +
                estaciones.get(finplazo(estaciones, 431, localidad)).obtHora() + ":00.");
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de SO2 registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 1, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 1, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 1, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 1, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 1, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de CO registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 6, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 6, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 6, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 6, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 6, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de NO registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 7, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 7, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 7, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 7, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 7, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de NO2 registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 8, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 8, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 8, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 8, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 8, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de <PM2,5 registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 9, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 9, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 9, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 9, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 9, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de <PM10 registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 10, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 10, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 10, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 10, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 10, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de NOx registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 12, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 12, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 12, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 12, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 12, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de O3 registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 14, localidad)).obtValor() + " \u00B5g/m3, o" + 
                estaciones.get(maximo(estaciones, 14, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 14, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 14, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 14, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de tolueno (metilbenceno) registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 20, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 20, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 20, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 20, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 20, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de carb\u00F3n negro registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 22, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 22, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 22, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 22, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 22, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de benceno registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 30, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 30, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 30, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 30, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 30, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de hidrocarburos totales registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 42, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 42, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 42, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 42, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 42, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de hidrocarburos no met\u00E1nicos registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 44, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 44, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 44, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 44, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 44, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00E1ximo de concentraci\u00F3n de metaparaxilenos (dimetilbencenos) registrado en " +
                localidad + ": " +
                estaciones.get(maximo(estaciones, 431, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(maximo(estaciones, 431, localidad)).obtDia() + "/" +
                estaciones.get(maximo(estaciones, 431, localidad)).obtMes() + "/" +
                estaciones.get(maximo(estaciones, 431, localidad)).obtCiclo() + ", a las " +
                estaciones.get(maximo(estaciones, 431, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de SO2 registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 1, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 1, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 1, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 1, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 1, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de CO registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 6, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 6, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 6, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 6, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 6, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de NO registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 7, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 7, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 7, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 7, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 7, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de NO2 registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 8, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 8, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 8, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 8, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 8, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de <PM2,5 registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 9, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 9, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 9, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 9, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 9, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de <PM10 registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 10, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 10, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 10, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 10, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 10, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de NOx registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 12, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 12, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 12, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 12, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 12, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de O3 registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 14, localidad)).obtValor() + " \u00B5g/m3, o" + 
                estaciones.get(minimo(estaciones, 14, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 14, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 14, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 14, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de tolueno (metilbenceno) registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 20, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 20, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 20, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 20, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 20, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de carb\u00F3n negro registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 22, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 22, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 22, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 22, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 22, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de benceno registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 30, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 30, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 30, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 30, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 30, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de hidrocarburos totales registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 42, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 42, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 42, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 42, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 42, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de hidrocarburos no met\u00E1nicos registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 44, localidad)).obtValor() + " mg/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 44, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 44, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 44, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 44, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor m\u00EDnimo de concentraci\u00F3n de metaparaxilenos (dimetilbencenos) registrado en " +
                localidad + ": " +
                estaciones.get(minimo(estaciones, 431, localidad)).obtValor() + " \u00B5g/m3, ocurrido el " +
                estaciones.get(minimo(estaciones, 431, localidad)).obtDia() + "/" +
                estaciones.get(minimo(estaciones, 431, localidad)).obtMes() + "/" +
                estaciones.get(minimo(estaciones, 431, localidad)).obtCiclo() + ", a las " +
                estaciones.get(minimo(estaciones, 431, localidad)).obtValor() + "h."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de SO2 registrado en " +
                localidad + ": " +
                media(estaciones, 1, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de CO registrado en " +
                localidad + ": " +
                media(estaciones, 6, localidad) + " mg/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de NO registrado en " +
                localidad + ": " +
                media(estaciones, 7, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de NO2 registrado en " +
                localidad + ": " +
                media(estaciones, 8, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de <PM2,5 registrado en " +
                localidad + ": " +
                media(estaciones, 9, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de <PM10 registrado en " +
                localidad + ": " +
                media(estaciones, 10, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de NOx registrado en " +
                localidad + ": " +
                media(estaciones, 12, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de O3 registrado en " +
                localidad + ": " +
                media(estaciones, 14, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de tolueno (metilbenceno) registrado en " +
                localidad + ": " +
                media(estaciones, 20, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de carb\u00F3n negro registrado en " +
                localidad + ": " +
                media(estaciones, 22, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de benceno registrado en " +
                localidad + ": " +
                media(estaciones, 30, localidad) + " \u00B5g/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de hidrocarburos totales registrado en " +
                localidad + ": " +
                media(estaciones, 42, localidad) + " mg/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de hidrocarburos no met\u00E1nicos registrado en " +
                localidad + ": " +
                media(estaciones, 44, localidad) + " mg/m3."
        );
        
        System.out.println("Valor medio de concentraci\u00F3n de metaparaxilenos (dimetilbencenos) registrado en " +
                localidad + ": " +
                media(estaciones, 431, localidad) + " \u00B5g/m3."
        );

        try {
            System.in.read();
        }
        catch (IOException ex) {
            Logger.getLogger(RecopiladorCADM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int ordenar(ArrayList<CADM> conjunto, CADM elemento) {
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
                                        return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                                    }
                                    else {
                                        return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                                    }
                                }
                                else if(conjunto.get(conjunto.size() / 2).obtHora() > elemento.obtHora()) {
                                    return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                                }
                                else {
                                    return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                                }
                            }
                            else if(conjunto.get(conjunto.size() / 2).obtDia() > elemento.obtDia()) {
                                return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                            }
                            else {
                                return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                            }
                        }
                        else if(conjunto.get(conjunto.size() / 2).obtMes() > elemento.obtMes()) {
                            return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                        }
                        else {
                            return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                        }
                    }
                    else if(conjunto.get(conjunto.size() / 2).obtCiclo() > elemento.obtCiclo()) {
                        return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                    }
                    else {
                        return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                    }
                }
                else if(conjunto.get(conjunto.size() / 2).obtCodMunicipio() > elemento.obtCodMunicipio()) {
                    return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
                }
                else {
                    return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
                }
            }
            else if(conjunto.get(conjunto.size() / 2).obtCodProvincia() > elemento.obtCodProvincia()) {
                return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
            }
            else {
                return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
            }
        }
        else if(conjunto.get(conjunto.size() / 2).obtMagnitud() > elemento.obtMagnitud()) {
            return ordenar((ArrayList<CADM>) conjunto.subList(0, conjunto.size() / 2), elemento);
        }
        return ordenar((ArrayList<CADM>) conjunto.subList(conjunto.size() / 2, conjunto.size()), elemento) + conjunto.size() / 2;
    }
    
    public int maximo(ArrayList<CADM> conjunto, int magnitud, String localidad) {
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
    
    public int minimo(ArrayList<CADM> conjunto, int magnitud, String localidad) {
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
    
    public double media(ArrayList<CADM> conjunto, int magnitud, String localidad) {
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
    
    public int inicioplazo(ArrayList<CADM> conjunto, int magnitud, String localidad) {
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
    
    public int finplazo(ArrayList<CADM> conjunto, int magnitud, String localidad) {
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