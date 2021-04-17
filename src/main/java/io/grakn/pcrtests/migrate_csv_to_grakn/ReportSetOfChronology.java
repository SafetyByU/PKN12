package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.match;
import static graql.lang.Graql.parse;
import static graql.lang.Graql.var;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Session;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.Answer;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Attribute;
import grakn.client.concept.thing.Entity;
import grakn.client.exception.GraknClientException;
import graql.lang.Graql;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.InputSingletonDefine;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.InputSingletonInsertEntity;
import mjson.Json;


public abstract class ReportSetOfChronology extends ReportSetOfData implements IReportSetOfChronology{

	SetOfChronology setOfChronology;
	
	public Map<LocalDateTime, String> getDateId() {
		return setOfChronology.getDateId();
	}
	
	public ReportSetOfChronology(String filereportpcrtests, OptionReport optionReport) {
		// TODO Auto-generated constructor stub
		super(filereportpcrtests, optionReport);
		
		setOfChronology=new SetOfChronology();
	}
	
	protected void initializeCaches(Session session) {
		super.initializeCaches(session);
		setOfChronology.initializeCaches(session);
	}
}
