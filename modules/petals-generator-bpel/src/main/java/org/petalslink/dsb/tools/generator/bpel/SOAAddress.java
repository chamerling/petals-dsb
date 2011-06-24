package org.petalslink.dsb.tools.generator.bpel;

import javax.xml.namespace.QName;

public class SOAAddress {

    public enum MEP {
        InOut, InOnly, RobustInOnly, InOptionalOut;
    }

    private String endpoint;

    private QName service;

    private QName itf;

    private String address;

    private QName operation;

    private MEP mep;

    public SOAAddress(String endpoint, QName service, QName itf) {
        this.endpoint = endpoint;

        this.service = service;

        this.itf = itf;

        this.address = null;

        this.operation = null;

        this.mep = null;
    }

    public SOAAddress(String endpoint, QName service, QName _interface, String address) {
        this.endpoint = endpoint;

        this.service = service;

        this.itf = _interface;

        this.address = address;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public QName getService() {
        return service;
    }

    public void setService(QName service) {
        this.service = service;
    }

    public QName getInterface() {
        return itf;
    }

    public void setInterface(QName interface1) {
        itf = interface1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public QName getOperation() {
        return operation;
    }

    public void setOperation(QName operation) {
        this.operation = operation;
    }

    public MEP getMep() {
        return mep;
    }

    public void setMep(MEP mep) {
        this.mep = mep;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
        result = prime * result + ((itf == null) ? 0 : itf.hashCode());
        result = prime * result + ((mep == null) ? 0 : mep.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SOAAddress)) {
            return false;
        }
        SOAAddress other = (SOAAddress) obj;
        if (address == null) {
            if (other.address != null) {
                return false;
            }
        } else if (!address.equals(other.address)) {
            return false;
        }
        if (endpoint == null) {
            if (other.endpoint != null) {
                return false;
            }
        } else if (!endpoint.equals(other.endpoint)) {
            return false;
        }
        if (itf == null) {
            if (other.itf != null) {
                return false;
            }
        } else if (!itf.equals(other.itf)) {
            return false;
        }
        if (mep != other.mep) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (service == null) {
            if (other.service != null) {
                return false;
            }
        } else if (!service.equals(other.service)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SOAAddress [endpoint=");
        builder.append(endpoint);
        builder.append(", service=");
        builder.append(service);
        builder.append(", itf=");
        builder.append(itf);
        builder.append(", address=");
        builder.append(address);
        builder.append(", operation=");
        builder.append(operation);
        builder.append(", mep=");
        builder.append(mep);
        builder.append("]");
        return builder.toString();
    }
}
