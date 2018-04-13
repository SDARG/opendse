package net.sf.opendse.encoding.mapping;

import com.google.inject.ImplementedBy;

import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;

/**
 * The {@link MappingConstraintManager} manages the
 * {@link MappingConstraintGenerator}s depending on the mapping mode of the
 * tasks.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(MappingConstraintManagerDefault.class)
public interface MappingConstraintManager {

	/**
	 * returns the constraint generator for the given mapping mode
	 * 
	 * @param mappingMode
	 * @return the {@link MappingConstraintGenerator} for the given mapping mode
	 */
	public MappingConstraintGenerator getMappingConstraintGenerator(MappingModes mappingMode);
}
