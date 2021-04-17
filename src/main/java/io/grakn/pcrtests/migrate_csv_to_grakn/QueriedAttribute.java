package io.grakn.pcrtests.migrate_csv_to_grakn;

class QueriedAttribute {
	String typevalue;
	Object value;

	public QueriedAttribute(String typevalue, Object value) {
		this.typevalue=typevalue;
		this.value = value;
	}
	Object getValue() {
		return value;
	}

	String getTypeValue() {
		return typevalue;
	}
}
