package org.tomfolga.dbutils.schemadiff;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RowKey implements Comparable<RowKey> {

	private final MultiKey multiKey;

	public RowKey(Object[] keys) {
		this.multiKey = new MultiKey(keys);
	}

	@Override
	public int compareTo(RowKey o) {
		for (int i = 0; i < multiKey.size(); i++) {
			String key1 = (String) multiKey.getKey(i);
			String key2 = (String) o.getMultiKey().getKey(i);
			if (key1 != null && key2 == null) {
				return 1;
			}
			if (key1 == null && key2 != null) {
				return -1;
			}
			if (key1 == null && key2 == null) {
				return 0;
			}

			if (key1.compareTo(key2) != 0) {
				return key1.compareTo(key2);
			}
		}
		return 0;
	}

	public MultiKey getMultiKey() {
		return multiKey;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
