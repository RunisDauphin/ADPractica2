package Recopiladores;

import java.io.File;

/*Este proyecto está protegido bajo la licencia PBCI (Proyecto Base - Cruel e Inutilizado).
Esta licencia no da ninguna de las ventajas de la licencia PPLM (Projet de Préservation - Loi Maréchale)
a excepción del permiso de distribución a cada miembro por separado.
Este proyecto solo puede usarse con fines académicos, pero se prohíbe su reutilización
a toda persona que carezca del título de "autor".*/

public class Principal {
    public static void main(String[] args) {
        String localidad = args[0];
        String ruta = args[1];
        Reemplazador(localidad);
        Reemplazador(ruta);
        
        System.out.println("Introduzca el municipio que quiera analizar.");
        System.out.println("Si introduce \"Todos\" (sin comillas) se mostrarán los datos de todas las estaciones.");
    }
    
    public Principal() {
        /*Es bien sabido que R2/D2 "Arturito" es muy bueno procesando la API Stream.
        Pero el problema radica en que tenemos que ejecutar esto en robots Bomberman de 1983,
        robots mucho más antiguos y negados para la API Stream (pero muy buenos en poner bombas,
        eliminar enemigos y huir de sus propias bombas... la mayor parte de las veces).
        Incluso los de 2017 no lo implementan aún.
        Evidentemente, estos robots tienen que ser capaces de realizar tareas similares
        (como vimos en Bomberman Generation),
        aún careciendo de la API Stream, porque trabajan genial con cadenas y números.
        Por ello, se han buscado métodos más arcaicos para realizar la misma tarea.*/
    }
    
    public static void Reemplazador(String cadena) {
        if(cadena.contains("/")) {
            cadena.replaceAll("/", File.separator);
        }
        if(cadena.contains("á")) {
            cadena.replaceAll("á", "\u00E1");
        }
        if(cadena.contains("é")) {
            cadena.replaceAll("é", "\u00E9");
        }
        if(cadena.contains("í")) {
            cadena.replaceAll("í", "\u00ED");
        }
        if(cadena.contains("ó")) {
            cadena.replaceAll("ó", "\u00F3");
        }
        if(cadena.contains("ú")) {
            cadena.replaceAll("ú", "\u00FA");
        }
        if(cadena.contains("à")) {
            cadena.replaceAll("à", "\u00E0");
        }
        if(cadena.contains("è")) {
            cadena.replaceAll("è", "\u00E8");
        }
        if(cadena.contains("ì")) {
            cadena.replaceAll("ì", "\u00EC");
        }
        if(cadena.contains("ò")) {
            cadena.replaceAll("ò", "\u00F2");
        }
        if(cadena.contains("ù")) {
            cadena.replaceAll("ù", "\u00F9");
        }
        if(cadena.contains("â")) {
            cadena.replaceAll("â", "\u00E2");
        }
        if(cadena.contains("ê")) {
            cadena.replaceAll("ê", "\u00EA");
        }
        if(cadena.contains("î")) {
            cadena.replaceAll("î", "\u00EE");
        }
        if(cadena.contains("ô")) {
            cadena.replaceAll("ô", "\u00F4");
        }
        if(cadena.contains("û")) {
            cadena.replaceAll("û", "\u00FB");
        }
        if(cadena.contains("ä")) {
            cadena.replaceAll("ä", "\u00E4");
        }
        if(cadena.contains("ë")) {
            cadena.replaceAll("ë", "\u00EB");
        }
        if(cadena.contains("ï")) {
            cadena.replaceAll("ï", "\u00EF");
        }
        if(cadena.contains("ö")) {
            cadena.replaceAll("ö", "\u00F6");
        }
        if(cadena.contains("ü")) {
            cadena.replaceAll("ü", "\u00FC");
        }
        if(cadena.contains("ç")) {
            cadena.replaceAll("ç", "\u00E7");
        }
        if(cadena.contains("ñ")) {
            cadena.replaceAll("ñ", "\u00F1");
        }
        if(cadena.contains("Á")) {
            cadena.replaceAll("Á", "\u00C1");
        }
        if(cadena.contains("É")) {
            cadena.replaceAll("É", "\u00C9");
        }
        if(cadena.contains("Í")) {
            cadena.replaceAll("Í", "\u00CD");
        }
        if(cadena.contains("Ó")) {
            cadena.replaceAll("Ó", "\u00D3");
        }
        if(cadena.contains("Ú")) {
            cadena.replaceAll("Ú", "\u00DA");
        }
        if(cadena.contains("À")) {
            cadena.replaceAll("À", "\u00C0");
        }
        if(cadena.contains("È")) {
            cadena.replaceAll("È", "\u00C8");
        }
        if(cadena.contains("Ì")) {
            cadena.replaceAll("Ì", "\u00CC");
        }
        if(cadena.contains("Ò")) {
            cadena.replaceAll("Ò", "\u00D2");
        }
        if(cadena.contains("Ù")) {
            cadena.replaceAll("Ù", "\u00D9");
        }
        if(cadena.contains("Â")) {
            cadena.replaceAll("Â", "\u00C2");
        }
        if(cadena.contains("Ê")) {
            cadena.replaceAll("Ê", "\u00CA");
        }
        if(cadena.contains("Î")) {
            cadena.replaceAll("Î", "\u00CE");
        }
        if(cadena.contains("Ô")) {
            cadena.replaceAll("Ô", "\u00D4");
        }
        if(cadena.contains("Û")) {
            cadena.replaceAll("Û", "\u00DB");
        }
        if(cadena.contains("Ä")) {
            cadena.replaceAll("Ä", "\u00C4");
        }
        if(cadena.contains("Ë")) {
            cadena.replaceAll("Ë", "\u00CB");
        }
        if(cadena.contains("Ï")) {
            cadena.replaceAll("Ï", "\u00CF");
        }
        if(cadena.contains("Ö")) {
            cadena.replaceAll("Ö", "\u00D6");
        }
        if(cadena.contains("Ü")) {
            cadena.replaceAll("Ü", "\u00DC");
        }
        if(cadena.contains("Ç")) {
            cadena.replaceAll("Ç", "\u00C7");
        }
        if(cadena.contains("Ñ")) {
            cadena.replaceAll("Ñ", "\u00D1");
        }
    }
    
    /*No me extraña que Pilar Alegría diga que a las niñas les dan ansiedad las matemáticas:
    viendo las máquinas de matar que son los Bomberman, como para no temblar.*/
}
