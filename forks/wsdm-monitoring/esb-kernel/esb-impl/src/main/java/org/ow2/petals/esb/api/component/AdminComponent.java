package org.ow2.petals.esb.api.component;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.api.entity.AdminProvider;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.component.Component;

@Interface(name="adminComponent")
public interface AdminComponent extends Component {

	AdminProvider createAdminProvider() throws ESBException;
}
