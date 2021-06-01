package com.brielage.monitor.XML;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;

public class XSDValidatorTest {
    private final Schema schema = XSDValidator.getSchema("heartbeat");

    public XSDValidatorTest() throws SAXException {
    }

    @Test
    public void validate() {
        String xmlString =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<heartbeat xsi:noNamespaceSchemaLocation=\"schema.xsd\" \n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> <!--change path to location of xsd document if not in same folder & change filename if you saved the xsd under a different name-->\n" +
                        "    <header>\n" +
                        "        <status>ONLINE</status><!-- enum ONLINE| ERROR-->\n" +
                        "        <source>CANVAS</source> <!--enum CANVAS| FRONTEND | PLANNING -->\n" +
                        "    </header>\n" +
                        "    <timeStamp>2021-05-30T21:00:00</timeStamp>\n" +
                        "</heartbeat>";

        Assert.assertTrue(XSDValidator.validate(schema, xmlString));
    }

    @Test
    public void validateWrongXML() {
        String xmlString =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Heartbeat xsi:noNamespaceSchemaLocation=\"schema.xsd\" \n" +
                        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> <!--change path to location of xsd document if not in same folder & change filename if you saved the xsd under a different name-->\n" +
                        "    <header>\n" +
                        "        <status>ONLINE</status><!-- enum ONLINE| ERROR-->\n" +
                        "        <source>CANVAS</source> <!--enum CANVAS| FRONTEND | PLANNING -->\n" +
                        "    </header>\n" +
                        "    <timeStamp>2021-05-30T21:00:00</timeStamp>\n" +
                        "</heartbeat>";

        Assert.assertFalse(XSDValidator.validate(schema, xmlString));
    }
}
