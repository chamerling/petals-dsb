package org.ow2.petals.monitoring.core.connexion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for endpoint complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;endpoint&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;service&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;interface&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;container&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;component&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;subdomain&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "endpoint", propOrder = { "service", "_interface", "container",
		"component", "subdomain", "description" })
public class Endpoint {

	protected QName service;
	@XmlElement(name = "interface")
	protected List<QName> _interface;
	protected String container;
	protected String component;
	protected String subdomain;
	protected String description;
	@XmlAttribute
	protected String name;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Endpoint other = (Endpoint) obj;
		if (this._interface == null) {
			if (other._interface != null) {
				return false;
			}
		} else if (!this._interface.equals(other._interface)) {
			return false;
		}
		if (this.component == null) {
			if (other.component != null) {
				return false;
			}
		} else if (!this.component.equals(other.component)) {
			return false;
		}
		if (this.container == null) {
			if (other.container != null) {
				return false;
			}
		} else if (!this.container.equals(other.container)) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.service == null) {
			if (other.service != null) {
				return false;
			}
		} else if (!this.service.equals(other.service)) {
			return false;
		}
		if (this.subdomain == null) {
			if (other.subdomain != null) {
				return false;
			}
		} else if (!this.subdomain.equals(other.subdomain)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the value of the component property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getComponent() {
		return this.component;
	}

	/**
	 * Gets the value of the container property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getContainer() {
		return this.container;
	}

	/**
	 * Gets the value of the description property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Gets the value of the interface property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the interface property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInterface().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QName }
	 * 
	 * 
	 */
	public List<QName> getInterface() {
		if (this._interface == null) {
			this._interface = new ArrayList<QName>();
		}
		return this._interface;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the value of the service property.
	 * 
	 * @return possible object is {@link QName }
	 * 
	 */
	public QName getService() {
		return this.service;
	}

	/**
	 * Gets the value of the subdomain property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSubdomain() {
		return this.subdomain;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this._interface == null) ? 0 : this._interface.hashCode());
		result = prime * result
				+ ((this.component == null) ? 0 : this.component.hashCode());
		result = prime * result
				+ ((this.container == null) ? 0 : this.container.hashCode());
		result = prime
				* result
				+ ((this.description == null) ? 0 : this.description.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.service == null) ? 0 : this.service.hashCode());
		result = prime * result
				+ ((this.subdomain == null) ? 0 : this.subdomain.hashCode());
		return result;
	}

	/**
	 * Sets the value of the component property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setComponent(final String value) {
		this.component = value;
	}

	/**
	 * Sets the value of the container property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setContainer(final String value) {
		this.container = value;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescription(final String value) {
		this.description = value;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(final String value) {
		this.name = value;
	}

	/**
	 * Sets the value of the service property.
	 * 
	 * @param value
	 *            allowed object is {@link QName }
	 * 
	 */
	public void setService(final QName value) {
		this.service = value;
	}

	/**
	 * Sets the value of the subdomain property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSubdomain(final String value) {
		this.subdomain = value;
	}

	@Override
	public String toString() {
		return "Endpoint [_interface=" + this._interface + ", component="
				+ this.component + ", container=" + this.container
				+ ", description=" + this.description + ", name=" + this.name
				+ ", service=" + this.service + ", subdomain=" + this.subdomain
				+ "]";
	}

}
