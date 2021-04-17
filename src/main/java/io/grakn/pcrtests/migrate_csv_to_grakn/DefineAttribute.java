package io.grakn.pcrtests.migrate_csv_to_grakn;

class DefineAttribute {
	String type;
	String name;

	public DefineAttribute(String type, String name) {
		this.type=type;
		this.name = name;
	}
	String getName() {
		return name;
	}

	String getType() {
		return type;
	}
}
