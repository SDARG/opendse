package net.sf.opendse.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterRangeDiscrete;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.model.parameter.ParameterUniqueID;

public class ClassDictionaryDefault implements ClassDictionary {

	protected final BidiMap<String, Class<?>> classMap;
	protected final Set<Class<?>> primitives;

	public ClassDictionaryDefault() {
		this.classMap = generateClassMap();
		this.primitives = generatePrimitives();
	}

	@Override
	public String getType(Class<?> clazz) {
		return classMap.containsValue(clazz) ? //
				classMap.getKey(clazz) : //
				clazz.getCanonicalName().toString();
	}

	@Override
	public boolean hasClassName(String name) {
		return classMap.containsKey(name);
	}

	@Override
	public Class<?> getClass(String name) {
		return classMap.get(name);
	}

	@Override
	public boolean isPrimitive(Class<?> clazz) {
		return clazz.isPrimitive() || primitives.contains(clazz);
	}

	protected final Set<Class<?>> generatePrimitives() {
		Set<Class<?>> result = new HashSet<>();
		result.add(Boolean.class);
		result.add(Byte.class);
		result.add(Integer.class);
		result.add(Character.class);
		result.add(Short.class);
		result.add(Float.class);
		result.add(Long.class);
		result.add(Double.class);
		return result;
	}

	protected final BidiMap<String, Class<?>> generateClassMap() {
		BidiMap<String, Class<?>> result = new DualHashBidiMap<>();
		result.put("INT", Integer.class);
		result.put("DOUBLE", Double.class);
		result.put("STRING", String.class);
		result.put("BOOL", Boolean.class);
		result.put("RANGE", ParameterRange.class);
		result.put("DISCRETERANGE", ParameterRangeDiscrete.class);
		result.put("SELECT", ParameterSelect.class);
		result.put("UID", ParameterUniqueID.class);
		result.put("resource", Resource.class);
		result.put("link", Link.class);
		result.put("task", Task.class);
		result.put("communication", Communication.class);
		result.put("dependency", Dependency.class);
		result.put("mapping", Mapping.class);
		result.put("SET", HashSet.class);
		result.put("LIST", ArrayList.class);
		return result;
	}
}
