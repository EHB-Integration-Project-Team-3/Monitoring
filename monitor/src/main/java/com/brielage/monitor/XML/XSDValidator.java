package com.brielage.monitor.XML;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

public enum XSDValidator {
    ;

    private static final SchemaFactory schemaFactory =
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    public static Schema getSchema(String what)
            throws SAXException {
        return schemaFactory.newSchema(
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResource(what + ".xsd"));
    }

    public static boolean validate(Schema schema,
                                   String xmlString) {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
