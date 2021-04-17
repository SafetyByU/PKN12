package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.util.Collection;
import java.util.List;

import grakn.client.GraknClient;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;

public interface IInputSetOfData {
	
	public Collection<Input> initialize (Collection<Input>inputs);
	
	public void postCalculate(GraknClient.Session session);
	
}
