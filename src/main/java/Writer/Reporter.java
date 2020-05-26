package Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

public interface Reporter {

    void reportTXT(OutputStream os) throws IOException;

    void reportXML(XMLStreamWriter writer) throws XMLStreamException;

}
