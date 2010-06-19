package org.tomfolga.dbutils.schemadiff;

import java.util.Map;
import java.util.Properties;

public class VariableSubstitutor {

	public static String replace(String originalString, Properties variables) {
		String modifiedString = originalString;
		for (Map.Entry<Object,Object> variable : variables.entrySet()) {
			String variableName = (String) variable.getKey();
			String variableValue = (String) variable.getValue();
			modifiedString = modifiedString.replace("${"+variableName+"}", variableValue);
		}
		return modifiedString;
		
	}
}
