package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.Answer;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Attribute;
import grakn.client.concept.thing.Entity;
import grakn.client.concept.type.EntityType;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;



public class Cohort implements ICohort{

	static final String CohortTypeValue_cl_age90 = "cl_age90";
	static final String CohortTypeAttributeValue_cl_age90 = "LongValueAttribute";
	
	static final String CohortTypeValue_pop = "pop";
	static final String CohortTypeAttributeValue_pop = "LongValueAttribute";

	
	
	Map<Long, String> cl_age90Id = new HashMap<>();
	
	public Map<Long, String> getCl_age90Id() {
		return cl_age90Id;
	}
	
	protected void initializeCaches(GraknClient.Session session)
	{
		//super.initializeCaches(session);
		initializeAgesCache(session);
	}

	protected void initializeAgesCache(GraknClient.Session session)
	{
		try
		{

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			String graqlQuery1 = "match ";
			graqlQuery1+=		"$cl_age90 isa Attribut-" + CohortTypeValue_cl_age90 +";get;";	

			QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery1));
			List<ConceptMap> answers1= map1.get();

			for(ConceptMap answer1:answers1)
			{	
				Attribute<Object> cl_age90= answer1.get("cl_age90").asAttribute();
				String idCl_age90=cl_age90.id().toString();
				//System.out.println("idEventDate1 : "  + idEventDate1);

				Long valuecl_age90 = (Long) cl_age90.value();		
				//System.out.println("date : "  + valuedate1);

				cl_age90Id.put(valuecl_age90, idCl_age90);

			} // for answer1

		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
}
