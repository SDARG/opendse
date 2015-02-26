/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
