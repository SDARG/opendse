package net.sf.opendse.model.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.ParameterReference;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

// TODO: Construct with "Specification"
public class SpecificationConstraintsMulti implements SpecificationConstraints {

	protected final Set<SpecificationConstraints> specificationConstraints;
	
	@Inject
	public SpecificationConstraintsMulti(Set<SpecificationConstraints> specificationConstraints) {
		super();
		this.specificationConstraints = specificationConstraints;
	}

	@Override
	public void doEncoding(Collection<Constraint> constraints) {
		for(SpecificationConstraints sc: specificationConstraints){
			sc.doEncoding(constraints);
		}
	}

	@Override
	public void doInterpreting(Specification implementation, Model model) {
		for(SpecificationConstraints sc: specificationConstraints){
			sc.doInterpreting(implementation, model);
		}
	}

	@Override
	public Set<ParameterReference> getActiveParameters() {
		Set<ParameterReference> parameters = new HashSet<ParameterReference>();
		for(SpecificationConstraints sc: specificationConstraints){
			Set<ParameterReference> ap = sc.getActiveParameters();
			if(ap != null){
				parameters.addAll(ap);
			}
		}
		return parameters;
	}
	
	

}
