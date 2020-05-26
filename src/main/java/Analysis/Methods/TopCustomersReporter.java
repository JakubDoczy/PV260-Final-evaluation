package Analysis.Methods;

import Analysis.Data.Customer;
import Writer.Reporter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class TopCustomersReporter implements Reporter {

    TopCustomers topCustomers;

    public TopCustomersReporter(TopCustomers topCustomers) {
        this.topCustomers = topCustomers;
    }

    @Override
    public void reportTXT(OutputStream os) throws IOException {

        List<Map.Entry<Customer, Long>> topCustomersList = topCustomers.getMaxList().getSortedData();
        String header = "Top customers" + System.lineSeparator();
        os.write(header.getBytes());

        for (int i = 0; i < topCustomersList.size(); i++) {
            Map.Entry<Customer, Long> customerEntry = topCustomersList.get(i);

            os.write(String.format("No. %d customer: %s, %d orders%n"
                    , i+1
                    , customerEntry.getKey().toString()
                    , customerEntry.getValue()).getBytes());
        }
    }

    @Override
    public void reportXML(XMLStreamWriter writer) throws XMLStreamException {
        List<Map.Entry<Customer, Long>> topCustomersList = topCustomers.getMaxList().getSortedData();

        writer.writeStartElement("top_customers");

        for (Map.Entry<Customer, Long> entry : topCustomersList) {
            writer.writeStartElement(entry.getKey().toString());
            writer.writeCharacters(entry.getValue().toString());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }
}
