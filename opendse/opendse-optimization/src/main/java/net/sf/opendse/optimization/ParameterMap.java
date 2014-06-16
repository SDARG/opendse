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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.opendse.model.parameter.ParameterReference;

import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.map.LazyMap;

@SuppressWarnings("unchecked")
public class ParameterMap extends HashMap<ParameterReference, Object> {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("rawtypes")
	protected Map<String, List<ParameterReference>> map = LazyMap.decorate(
			new HashMap<String, List<ParameterReference>>(), new InstantiateFactory(ArrayList.class));

	@Override
	public Object put(ParameterReference key, Object value) {
		map.get(key.getId()).add(key);
		return super.put(key, value);
	}
	
	public List<ParameterReference> get(String id){
		return map.get(id);
	}

}
