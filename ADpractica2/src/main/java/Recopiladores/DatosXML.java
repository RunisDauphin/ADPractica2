import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class DatosXML {
    public DatosXML() throws JAXBException {
        DatosFinales datos = new DatosFinales();//pasas los datos aqui
        StringWriter sw = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(DatosFinales.class);
        Marshaller jaxbMarshaller;
        jaxbMarshaller = context.createMarshaller();
        jaxbMarshaller.marshal(datos, sw);
        String xmlString = sw.toString();
        System.out.println(xmlString);
    }
}
