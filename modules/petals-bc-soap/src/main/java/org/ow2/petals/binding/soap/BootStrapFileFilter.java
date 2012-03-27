package org.ow2.petals.binding.soap;

import java.io.File;
import java.io.FileFilter;

import static org.ow2.petals.binding.soap.SoapConstants.Axis2.MODULE_ARCHIVE_EXTENSION;

public class BootStrapFileFilter implements FileFilter {
	@Override
	public boolean accept(final File pathname) {
		return pathname.getName().endsWith("." + MODULE_ARCHIVE_EXTENSION);
	}
}
