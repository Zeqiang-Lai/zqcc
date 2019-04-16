package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class XMLHelper {
    private StringBuffer content = new StringBuffer();

    public void append(List<XMLPrintable> elements) {
        for(XMLPrintable element : elements) {
            content.append(element.toXMLString()).append("\n");
        }
    }

    public void append(XMLPrintable element) {
        content.append(element.toXMLString()).append("\n");
    }

    public void append(String text) {
        content.append(text).append("\n");
    }

    public String getContent() {
        return new String(content);
    }

    public void format() {
        // TODO: format xml content with proper indent.
    }

    public int generate(String filePath) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filePath);
        out.println(content);
        out.close();
        return content.length();
    }
}
