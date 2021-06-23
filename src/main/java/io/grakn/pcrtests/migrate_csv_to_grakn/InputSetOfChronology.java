package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Session;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.answer.Void;
import grakn.client.concept.thing.Entity;
import grakn.client.concept.type.EntityType;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;

public abstract class InputSetOfChronology extends InputSetOfData implements IInputSetOfChronology
{
	SetOfChronology setOfChronology=null;

	public Map<LocalDateTime, String> getDateId() {
		return setOfChronology.getDateId();
	}

	enum DF_CASE
	{
		FALSE,
		TRUE,
		BOTH
	}


	DF_CASE dateFilter=DF_CASE.FALSE;
	LocalDate minDate=LocalDate.parse((CharSequence)"1900-01-01");
	LocalDate maxDate=LocalDate.parse((CharSequence)"9999-01-01");

	int mindays=0;
	int maxdays=255;

	public int getMindays() {
		return mindays;
	}
	public void setMindays(int mindays) {
		this.mindays = mindays;
	}
	public int getMaxdays() {
		return maxdays;
	}
	public void setMaxdays(int maxdays) {
		this.maxdays = maxdays;
	}
	public LocalDate getMinDate() {
		return minDate;
	}
	public void setMinDate(LocalDate minDate) {
		this.minDate = minDate;
	}
	public LocalDate getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(LocalDate maxDate) {
		this.maxDate = maxDate;
	}
	public DF_CASE isDatefilter() {
		return dateFilter;
	}
	public void setDatefilter(DF_CASE datefilter) {
		this.dateFilter = datefilter;
	}

	InputSetOfChronology(OptionImport optionImport,String filename)
	{
		super (optionImport, filename);
		setOfChronology=new SetOfChronology();

	}

	public InputSetOfChronology(OptionImport optionImport, String filename, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{
		super(optionImport, filename);

		setOfChronology=new SetOfChronology();

		dateFilter=DF_CASE.TRUE;
		this.minDate=myMinDate;
		this.maxDate=myMaxDate;

		this.mindays=myMindays;
		this.maxdays=myMaxdays;
	}

	public InputSetOfChronology(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice,DF_CASE myDateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{
		super (optionImport, filename,myMaxGet, minIndice, maxIndice);
		setOfChronology=new SetOfChronology();

		dateFilter=myDateFilter;
		this.minDate=myMinDate;
		this.maxDate=myMaxDate;

		this.mindays=myMindays;
		this.maxdays=myMaxdays;

	}

	public InputSetOfChronology(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{ 
		super (optionImport, filename,myMaxGet, minIndice, maxIndice);
		setOfChronology=new SetOfChronology();

		dateFilter=DF_CASE.TRUE;
		this.minDate=myMinDate;
		this.maxDate=myMaxDate;

		this.mindays=myMindays;
		this.maxdays=myMaxdays;

	}

	protected void initializeCaches(Session session) {
		super.initializeCaches(session);
		setOfChronology.initializeCaches(session);
	}
	// create link sequence between time event
	protected void createNextDateLink(GraknClient.Transaction transaction, String idTimePrevious, String idTimeNext)  
	{
		// verif existence
		String graqlQuery 	= "match $timeprevious id "+ idTimePrevious+ ";";				
		graqlQuery 			+= "$timenext id "+ idTimeNext+ ";";				
		graqlQuery			+= "(previousdate: $timeprevious, nextdate: $timenext) isa SequencedDatesRelation;get;limit 1;\n";

		System.out.println("query : "+ graqlQuery);

		QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery));
		List<ConceptMap> answers2= map2.get();

		if (answers2.isEmpty())
		{
			// relations creation
			String graqlInsertQuery 	= "match $timeprevious id "+ idTimePrevious+ ";";				
			graqlInsertQuery 			+= "$timenext id "+ idTimeNext+ ";";				
			graqlInsertQuery			+= "insert (previousdate: $timeprevious, nextdate: $timenext) isa SequencedDatesRelation;\n";

			System.out.println("query : "+ graqlInsertQuery);

			QueryFuture<List<ConceptMap>> map3 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
			List<ConceptMap> answers3= map2.get();
		}
	}

	// create link sequence between time event
	protected String getWhatOfTime(GraknClient.Transaction transaction, Entity time)  
	{
		EntityType typetime=time.type();

		String graqlQuery="";
		List<ConceptMap> answers1=null;
		List<ConceptMap> answers2=null;

		if (typetime.label().toString().equals(InputWeather.WeatherClass_WeatherTimeDate)
				|| typetime.label().toString().equals(Vaccin.VaccinClass_VaccinTimeDate)
				|| typetime.label().toString().equals(PCRTest.PCRTestClass_PCRTimeDate))
		{
			// verif existence
			graqlQuery 	= "match $time id "+ time.id().toString()+ ";";				
			graqlQuery			+= "(time: $time, object: $what) isa " + "eventrelations "+";get;limit 1;\n";

			QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery));
			answers1= map1.get();

		}
		if (typetime.label().toString().equals(InputDepartementsCouvreFeu.DepartementConfinementClass_DepartementConfinementTimeDate)
				|| typetime.label().toString().equals(InputShopsConfinement.ShopConfinementClass_ShopConfinementTimeDate))
		{
			// verif existence
			graqlQuery 	= "match $time id "+ time.id().toString()+ ";";				
			graqlQuery	+= "(startwhendate: $time, periodoftime: $periodoftime) isa PeriodicRelation;";
			graqlQuery	+= "(time: $time, object: $what) isa " + "eventrelations"+";get;limit 1;\n";

			QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery));
			answers1= map1.get();

		}



		if (answers1.isEmpty()==false)
		{
			for(ConceptMap answer1:answers1)
			{

				Entity resource= answer1.get("what").asEntity();
				String idResource=resource.id().toString();

				return idResource;
			}
		}

		if (typetime.label().toString().equals(InputDepartementsCouvreFeu.DepartementConfinementClass_DepartementConfinementTimeDate)
				|| typetime.label().toString().equals(InputShopsConfinement.ShopConfinementClass_ShopConfinementTimeDate))
		{
			// verif existence
			graqlQuery 	= "match $time id "+ time.id().toString()+ ";";				
			graqlQuery	+= "(endwhendate: $time, periodoftime: $periodoftime) isa PeriodicRelation;";
			graqlQuery	+= "(time: $time, object: $what) isa " + "eventrelations"+";get;limit 1;\n";

			QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery));
			answers2= map2.get();

		}

		if (answers2.isEmpty()==false)
		{
			for(ConceptMap answer2:answers2)
			{

				Entity resource= answer2.get("what").asEntity();
				String idResource=resource.id().toString();

				return idResource;
			}
		}
		return null;
	}
	
	// create link sequence between time event
	protected void createSameDateLink(GraknClient.Transaction transaction, Entity time1, Entity time2, String idLocalization)  
	{
		// verif existence
		String graqlQuery 	= "match $time1 id "+ time1.id().toString()+ ";";				
		graqlQuery 			+= "$time2 id "+ time2.id().toString()+ ";";				
		graqlQuery			+= "($time1, $time2) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";get;limit 1;\n";

		//System.out.println("query : "+ graqlQuery);

		QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery));
		List<ConceptMap> answers2= map2.get();

		if (answers2.isEmpty())
		{

			String what1=getWhatOfTime(transaction,time1);
			String what2=getWhatOfTime(transaction,time2);
			
			String assertionmatchwhat="";
			String relationswhat="";
			if (what1!=null)
			{
				assertionmatchwhat+="$what1 id " + what1 + ";";
				relationswhat+=", object1 : $what1";
			}
			if (what2!=null)
			{
				assertionmatchwhat+="$what2 id " + what2 + ";";
				relationswhat+=", object2 : $what2";
			}
			
			// relations creation
			String graqlInsertQuery 	= "match $time1 id "+ time1.id().toString()+ ";";				
			graqlInsertQuery			+= assertionmatchwhat;
			graqlInsertQuery 			+= "$time2 id "+ time2.id().toString()+ ";";		
			if (idLocalization!=null)
			{
				graqlInsertQuery 			+= "$localization1 id "+ idLocalization+ ";";		
				graqlInsertQuery			+= "insert (time1: $time1, time2: $time2, localization : $localization1" + relationswhat + ") isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+ ";\n";

			}

			else
			{
				graqlInsertQuery			+= "insert (time1: $time1, time2: $time2" + relationswhat + ") isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+ ";\n";
			}
			System.out.println("query : "+ graqlInsertQuery);

			QueryFuture<List<ConceptMap>> map3 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
			List<ConceptMap> answers3= map3.get();
		}
	}

	// create link sequence between time event
	protected void removeSameDateLink(GraknClient.Transaction transaction, Entity time1, Entity time2, String idLocalization)  
	{
		// verif existence
		String graqlQuery 	= "match $time1 id "+ time1.id().toString()+ ";";				
		graqlQuery 			+= "$time2 id "+ time2.id().toString()+ ";";				
		graqlQuery			+= "$r ($time1, $time2) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";";
		graqlQuery			+= "delete $r isa "+ PCRTest.PCRTestClass_PCRTestSameDateLinks+";\n";

		//System.out.println("query : "+ graqlQuery);

		QueryFuture<List<Void>> map2 = transaction.execute((GraqlDelete)parse(graqlQuery));
		List<Void> answers2= map2.get();
	}

}
