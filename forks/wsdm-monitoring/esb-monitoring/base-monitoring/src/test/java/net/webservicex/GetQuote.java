package net.webservicex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;symbol&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "symbol" })
@XmlRootElement(name = "GetQuote")
public class GetQuote {

	protected String symbol;

	/**
	 * Gets the value of the symbol property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * Sets the value of the symbol property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSymbol(final String value) {
		this.symbol = value;
	}

}
