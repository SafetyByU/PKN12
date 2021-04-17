package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.util.Collection;

import com.google.gson.JsonArray;

import grakn.client.GraknClient.Transaction;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Report;

public interface IReportSetOfChronology extends IReportSetOfData{

	Collection<Report> initialize(Collection<Report> reports);

	JsonArray queryDataToJson(Transaction transaction);

}
