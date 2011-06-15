/**
 * 
 */
package org.petalslink.dsb.kernel.tools.rest;

/**
 * @author chamerling
 *
 */
public class RESTServiceInformationBean {

    Object implem;

    Class<?> clazz;

    String componentName;

    String url;

    String name;

    /**
     * @return the implem
     */
    public Object getImplem() {
        return implem;
    }

    /**
     * @param implem
     *            the implem to set
     */
    public void setImplem(Object implem) {
        this.implem = implem;
    }

    /**
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @param clazz
     *            the clazz to set
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * @return the componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * @param componentName
     *            the componentName to set
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RESTServiceInformationBean [implem=");
        builder.append(implem);
        builder.append(", clazz=");
        builder.append(clazz);
        builder.append(", componentName=");
        builder.append(componentName);
        builder.append(", url=");
        builder.append(url);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }
}
