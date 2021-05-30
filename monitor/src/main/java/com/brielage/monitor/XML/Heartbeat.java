package com.brielage.monitor.XML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


@SuppressWarnings("unused")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "header",
        "timeStamp"
})
@XmlRootElement(name = "heartbeat")
public class Heartbeat {
    @XmlElement(required = true)
    protected Heartbeat.Header header;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;

    public Heartbeat.Header getHeader() {
        return header;
    }

    public void setHeader(Heartbeat.Header value) {
        this.header = value;
    }

    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String value)
            throws DatatypeConfigurationException {
        this.timeStamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "status",
            "source"
    })
    public static class Header {
        @XmlElement(required = true)
        protected String status;
        @XmlElement(required = true)
        protected String source;

        public String getStatus() {
            return status;
        }

        public void setStatus(String value) {
            this.status = value;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String value) {
            this.source = value;
        }
    }

    public String getSource(){
        return header.source;
    }

    public String getStatus(){
        return header.status;
    }

    @Override
    public String toString() {
        return "timestamp;" + timeStamp + "; " +
                "source;" + getHeader().getSource() + "; " +
                "status;" + getHeader().getStatus();
    }
}
