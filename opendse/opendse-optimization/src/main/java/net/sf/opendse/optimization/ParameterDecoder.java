/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
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
