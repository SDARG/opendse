package net.sf.opendse.optimization;

import java.util.Collection;

import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterReference;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.PermutationGenotype;
import org.opt4j.core.genotype.SelectMapGenotype;
import org.opt4j.core.problem.Decoder;

public class ParameterDecoder implements Decoder<CompositeGenotype<String,Genotype>, ParameterMap> {
	
	@Override
	public ParameterMap decode(CompositeGenotype<String, Genotype> genotype) {
		ParameterMap map = new ParameterMap();
		
		SelectMapGenotype<ParameterReference, Object> select = genotype.get("SELECT");
		if(select != null){
			Collection<ParameterReference> keys = select.getKeys();
			
			for(ParameterReference key: keys){
				map.put(key, select.getValue(key));
			}
		}
		DoubleMapGenotype<ParameterReference> range = genotype.get("RANGE");
		if(range != null){
			Collection<ParameterReference> keys = range.getKeys();
			
			for(ParameterReference key: keys){
				Double value = range.getValue(key);
				value = ((ParameterRange)key.getParameter()).normalizeValue(value);
				
				map.put(key, value);
			}
		}
		
		for(String key: genotype.keySet()){
			if(key.startsWith("UID_")){
				PermutationGenotype<ParameterReference> permutation = genotype.get(key);
				
				int i = 1;
				for(ParameterReference ref: permutation){
					map.put(ref,i);
					i++;
				}
				
			}
		}
		
		
		return map;
	}

}
