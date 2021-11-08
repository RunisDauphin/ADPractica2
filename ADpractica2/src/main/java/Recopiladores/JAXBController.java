package Recopiladores;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/*¡No toques esto!
¡Que te veo!
¡Dejalo tal cual!
Ya trabaja el código por ti.*/

public class JAXBController {
    private static JAXBController instance;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private XML estacion;

    private JAXBController() {
    }

    public static JAXBController obtInstance() {
        if (instance == null) {
            instance = new JAXBController();
        }
        return instance;
    }

    private void convertObjectToXML(XML estacion) throws JAXBException {
        this.estacion = estacion;
        // Set context
        JAXBContext context = JAXBContext.newInstance(XML.class);
        // Marshall --> Object to XML
        this.marshaller = context.createMarshaller();
        this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    }

    public void metXML(XML estacion) throws JAXBException {
        convertObjectToXML(estacion);
    }

    public void writeXMLFile(String uri) throws JAXBException {
        this.marshaller.marshal(estacion, new File(uri));
        System.out.println("Fichero XML generado con éxito");
    }

    public void printXML() throws JAXBException {
        this.marshaller.marshal(estacion, System.out);
    }

    private XML convertXMLToObject(String uri) throws JAXBException {
        // Set context
        JAXBContext context = JAXBContext.newInstance(XML.class);
        this.unmarshaller = context.createUnmarshaller();
        // Unmarshall --> XML toObject
        this.estacion = (XML) this.unmarshaller.unmarshal(new File(uri));
        return this.estacion;
    }

    public XML obtXML(String uri) throws JAXBException {
        return convertXMLToObject(uri);
    }
}
