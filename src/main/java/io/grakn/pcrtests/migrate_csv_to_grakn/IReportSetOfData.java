package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.util.Collection;
import java.util.List;

import grakn.client.GraknClient;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Report;

public interface IReportSetOfData {
	
	public Collection<Report> initialize (Collection<Report>reports);
}
