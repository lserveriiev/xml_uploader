package uploader.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uploader.data.Xml;

import java.io.*;

@Service
public class XmlReaderService {

    private XmlMapper xmlMapper;

    @Autowired
    public XmlReaderService(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    private String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        return sb.toString();
    }

    public Xml loadXml(File file) throws IOException {
        String xml = inputStreamToString(new FileInputStream(file));

        return xmlMapper.readValue(xml, Xml.class);
    }
}
