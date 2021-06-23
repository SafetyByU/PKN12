package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Entity;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputWeather extends InputSetOfChronology {

	static final String WeatherTypeValue_MetaTypeValue = "WD";


	static final String WeatherClass_EventWeather = "EventWeather";
	static final String WeatherClass_EventWeatherDepartementGroup = "EventWeatherDepartementGroup";
	static final String WeatherClass_ValueEventWeatherRelations = "ValueEventWeatherRelations";
	static final String WeatherClass_TypeValueEventWeatherRelations = "TypeValueWeatherRelations";
	static final String WeatherClass_EventWeatherRelations = "EventWeatherRelations";
	static final String WeatherClass_WeatherTimeDate="WeatherTimeDate";
	static final String WeatherClass_DepartementGroup="DepartementGroupConfinement";

	static final String WeatherClass_WhatWeather="WhatWeather";
	static final String WeatherTypeValue_Departement = "Departement";
	static final String WeatherTypeAttributeValue_Departement = "StringValueAttribute";
	static final String WeatherTypeValue_TMin = "TMin";
	static final String WeatherTypeAttributeValue_TMin = "DoubleValueAttribute";
	static final String WeatherTypeValue_TMax = "TMax";
	static final String WeatherTypeAttributeValue_TMax = "DoubleValueAttribute";
	static final String WeatherTypeValue_TMoy = "TMoy";
	static final String WeatherTypeAttributeValue_TMoy = "DoubleValueAttribute";

	static final String WeatherTypeValue_PPOSTCALCUL = "WPOSTCALCUL";
	static final String WeatherTypeAttributeValue_PPOSTCALCUL = "StringValueAttribute";

	static final String TagInitPostCalculate = "0";

	static final String w_firstindice = "120000";

	Boolean onlyDoubleClean=true;

	static int linksdates_deptindex=0;
	static int linkssamedates_deptindex=0;

	MyInputWeather myInputWeather=null;

	public MyInputWeather getMyInputWeather() {
		return myInputWeather;
	}
	public void setMyInputWeather(MyInputWeather myInputWeather) {
		this.myInputWeather = myInputWeather;
	}
	String getMetaType() {return WeatherTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return WeatherClass_ValueEventWeatherRelations;};
	String getTypevalue_Eventrelations() {return WeatherClass_TypeValueEventWeatherRelations;};


	public InputWeather(OptionImport optionImport, String filename) {
		super(optionImport, filename);

	}

	public InputWeather (OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, DF_CASE dateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice,  dateFilter, myMinDate,  myMaxDate,  myMindays,  myMaxdays);

		if (optionImport!=null)
			onlyDoubleClean=optionImport.getOnlyDoubleClean();
	}

	public InputWeather (OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport, filename,  myMaxGet,  minIndice,  maxIndice, myMinDate,  myMaxDate, myMindays, myMaxdays);
	}

	class MyInputWeather extends Input {



		InputWeather inputweather;
		LocalDate date=LocalDate.parse((CharSequence)"1900-01-01");;

		@Override
		public void saveHistoric(ArrayList<Json> historics, boolean finalsave)
		{

			ArrayList<Json> subHistorics = new ArrayList<>();

			super.saveHistoric(subHistorics, finalsave);

			if (finalsave==true)
			{
				// datePCR
				Json itemDateWeather = Json.object();
				itemDateWeather.set("dateweather",date.toString());
				subHistorics.add(itemDateWeather);
			}
			Json itemInputWeather = Json.object();
			itemInputWeather.set("InputWeather", subHistorics);

			historics=removeHistoric(historics, "InputWeather");
			historics.add(itemInputWeather);

			// sequence link post calculate
			ArrayList<Json> subHistorics2 = new ArrayList<>();
			if (finalsave==true)
			{
				// deptindex
				Json itemDeptIndexLinksDates = Json.object();
				itemDeptIndexLinksDates.set("linksdates_deptindex",linksdates_deptindex);
				subHistorics2.add(itemDeptIndexLinksDates);				

				// deptsamedateindex
				Json itemDeptIndexSameDateLinks = Json.object();
				itemDeptIndexSameDateLinks.set("linkssamedates_deptindex",linkssamedates_deptindex);
				subHistorics2.add(itemDeptIndexSameDateLinks);	
			}

			Json InputWeatherLinksDates = Json.object();
			InputWeatherLinksDates.set("InputWeatherLinksDates", subHistorics2);

			historics=removeHistoric(historics, "InputWeatherLinksDates");
			historics.add(InputWeatherLinksDates);

		}

		@Override
		public void loadHistoric(JsonArray historicsInput)
		{

			Iterator<JsonElement> iterator = historicsInput.iterator();
			while (iterator.hasNext()) 
			{
				JsonElement elt = iterator.next();

				JsonObject json1=elt.getAsJsonObject();
				Set<Entry<String, JsonElement>> set1=json1.entrySet();

				Iterator<Entry<String, JsonElement>> it1=set1.iterator();
				while (it1.hasNext()) 
				{
					Entry<String, JsonElement> elt1 =  it1.next();
					if (elt1.getKey().equals("InputWeather"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputWeatherList = (JsonArray) elt1.getValue();
						if (historicsInputWeatherList!=null)
							loadSpecificHistoric(historicsInputWeatherList);
					}
					if (elt1.getKey().equals("InputWeatherLinksDates"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputWeatherListLinksDates = (JsonArray) elt1.getValue();
						if (historicsInputWeatherListLinksDates!=null)
							loadSpecificHistoric(historicsInputWeatherListLinksDates);
					}
				}
			}

		}

		@Override
		public void loadSpecificHistoric(JsonArray historicsInputWeatherList )
		{
			super.loadHistoric(historicsInputWeatherList );

			Iterator<JsonElement> iterator = historicsInputWeatherList.iterator();
			while (iterator.hasNext()) {
				JsonElement elt = iterator.next();
				JsonObject jsonObject= elt.getAsJsonObject();
				if (jsonObject.get("dateweather")!=null)
				{
					JsonElement jsonDateWeather= jsonObject.get("dateweather");
					LocalDate minWeather=LocalDate.parse((CharSequence)jsonDateWeather.getAsString());
					if (minWeather.isAfter(minDate))
					{
						minDate=minWeather;
					}
				}

				if (jsonObject.get("firstIndice")!=null)
				{
					JsonElement jsonIndicePcr= jsonObject.get("firstIndice");
					Long firstIndiceWeather=jsonIndicePcr.getAsLong();

					if (firstIndiceWeather>indice)
					{
						indice=firstIndiceWeather;
					}
				}

				if (jsonObject.get("minIndice")!=null)
				{
					JsonElement jsonIndicePcr= jsonObject.get("minIndice");
					Long minWeather=jsonIndicePcr.getAsLong();

					if (minWeather+1>minIndice)
					{
						minIndice=minWeather+1;
					}
				}
				if (jsonObject.get("linksdates_deptindex")!=null)
				{
					JsonElement jsondeptIndex= jsonObject.get("linksdates_deptindex");
					linksdates_deptindex=jsondeptIndex.getAsInt();

				}

				if (jsonObject.get("linkssamedates_deptindex")!=null)
				{
					JsonElement jsondeptIndex= jsonObject.get("linkssamedates_deptindex");
					linkssamedates_deptindex=jsondeptIndex.getAsInt();

				}
			}
		}

		public MyInputWeather(String path, InputWeather inputweather) {
			super(path);
			// TODO Auto-generated constructor stub
			this.inputweather=inputweather;
			inputweather.setMyInputWeather(this);

			this.firstIndice= Long.decode(w_firstindice);
			this.indice = firstIndice;

		}

		@Override
		public String template(Json weather) {

			String graqlInsertQuery = "";
			try {

				if (weather.at("code_insee_departement") != null) {

					String departement = weather.at("code_insee_departement").asString();

					String sdate = weather.at("date_obs").asString();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-M-d");

					date = LocalDate.parse(sdate, formatter);

					// System.out.println ("Indice : " + indice + " date : " + date);
					indice++;

					if ((indice <= maxIndice && indice >= minIndice && (dateFilter == DF_CASE.FALSE ))
							|| (date.isAfter(minDate) && date.isBefore(maxDate) && (dateFilter == DF_CASE.TRUE))
							|| (date.isAfter(minDate) && date.isBefore(maxDate) && (indice <= maxIndice) && (indice >= minIndice) && (dateFilter == DF_CASE.BOTH))) 

					{

						Double TMin = weather.at("tmin").asDouble();
						Double TMax = weather.at("tmax").asDouble();
						Double TMoy = weather.at("tmoy").asDouble();

						graqlInsertQuery = "match ";
						//						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
						//								WeatherTypeValue_Departement);
						//						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
						//								WeatherTypeValue_TMin);
						//						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
						//								WeatherTypeValue_TMax);
						//						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
						//								WeatherTypeValue_TMoy);
						graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", WeatherTypeValue_PPOSTCALCUL);

						graqlInsertQuery += InputSingletonDefineQuery("", "Why", "Description", "WEATHER");
						graqlInsertQuery += InputSingletonDefineQuery("", "Departement", "CodeGLN", departement);

						// à poursuivre avec evenement simple confinement et périodique couvrefeu
						graqlInsertQuery += "\ninsert ";

						graqlInsertQuery += "$IdentifiedWhat isa " + WeatherClass_WhatWeather +" , has Identity \"WEATHER-"
								+ indice.toString() + "-" + departement + "\"";

						graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMin, TMin.toString());
						graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMax, TMax.toString());
						graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMoy, TMoy.toString());
						graqlInsertQuery+= ";";

						graqlInsertQuery += "$DepartementGroup isa " + WeatherClass_DepartementGroup +", has Identity \"WDG-"
								+ indice.toString() + "-" + departement + "\";";

						graqlInsertQuery += insertValue (WeatherTypeValue_PPOSTCALCUL, 
								indice.toString(), "StringValue", TagInitPostCalculate, "$IdentifiedWhat");

						graqlInsertQuery += "$TimeDate isa " + WeatherClass_WeatherTimeDate + ", has Identity \"WTD-" + indice.toString()
						+ "\", has EventDate " + date + ";";

						graqlInsertQuery += "$Event isa EventWeather, has Id \"EW-" + indice.toString() + "\";";

						graqlInsertQuery += "$eventRelation(registeredevent : $Event, actor: $DepartementGroup, localization: $Departement-"
								+ departement + ", time :$TimeDate, goal :$Why-WEATHER"
								+ ", object : $IdentifiedWhat) isa " + WeatherClass_EventWeatherRelations + ";";

					}

				}

			} catch (Exception e) {
			}
			return (graqlInsertQuery);
		}
	}


	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {

		// TODO Auto-generated method stub
		return initialiseInputsWeather(inputs);
	}


	// événement confinements
	protected Collection<Input> initialiseInputsWeather(Collection<Input> inputs) {

		// derived types

		inputs = initialiseDefine(inputs, WeatherClass_EventWeather, "event");
		inputs = initialiseDefine(inputs, WeatherClass_EventWeatherDepartementGroup, "GroupWho");

		inputs = initialiseDefine(inputs, WeatherClass_EventWeatherRelations, "eventrelations" + SuffixEventRelations);
		inputs = initialiseDefine(inputs, WeatherClass_ValueEventWeatherRelations,
				"ValueRelation" + SuffixValueRelations);
		inputs = initialiseDefine(inputs, WeatherClass_TypeValueEventWeatherRelations,
				"ValueTypeRelation" + SuffixValueTypeRelations);
		inputs=initialiseDefine(inputs, WeatherClass_WeatherTimeDate, "TimeDate");
		inputs=initialiseDefine(inputs, WeatherClass_DepartementGroup, "DepartementGroup");


		List<DefineAttribute> defineattributesWhatWeather=new ArrayList<>();
		DefineAttribute attribut_tmin = 	new DefineAttribute(WeatherTypeAttributeValue_TMin,WeatherTypeValue_TMin);
		DefineAttribute attribut_tmax = 	new DefineAttribute(WeatherTypeAttributeValue_TMax,WeatherTypeValue_TMax);
		DefineAttribute attribut_tmoy = 	new DefineAttribute(WeatherTypeAttributeValue_TMoy,WeatherTypeValue_TMoy);

		defineattributesWhatWeather.add(attribut_tmin);
		defineattributesWhatWeather.add(attribut_tmax);
		defineattributesWhatWeather.add(attribut_tmoy);

		inputs=initialiseDefine(inputs, WeatherClass_WhatWeather, "IdentifiedWhat",defineattributesWhatWeather);

		// singletons
		inputs = initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WWE-1", "Description", "WEATHER");
		//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_Departement);
		//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMin);
		//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMax);
		//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMoy);
		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_PPOSTCALCUL);

		// data
		inputs.add(new MyInputWeather(setOfData.getFilename(), this));

		return inputs;
	}

	@Override
	String getMetaTypeValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getClass_ValueEventRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getClass_TypeValueEventRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	// Clean existing weather events
	private void PostCleanDoubleDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			GraknClient.Transaction transaction= null;;

			Set<String> keysdept=getDepartementId().keySet();
			Set<LocalDateTime> keysdate=getDateId().keySet();

			int ndept=0;
			for (String keydept : keysdept)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				int nextrapole=(ndept*254);
				System.out.println("nextrapole:"+nextrapole);

				if (nextrapole>minIndice)
				{
					int nbdate=0;

					for (LocalDateTime keydate : keysdate)
					{
						//String idDate = getDateId().get(keydate);
						nbdate++;

						//idDate="V2948247624";

						//System.out.println("nbadate "+ nbdate);


						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}

						// query more than one value
						String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
						//graqlQuery11 += 	"$eventdate id "+ idDate + ";";
						graqlQuery11 += 	"$timedate isa TimeDate, has EventDate "+ keydate + ";";
						graqlQuery11 += 	"$what isa " + WeatherClass_WhatWeather + ", has Identity $identity;";
						graqlQuery11+=		"$e (time : $timedate, localization : $Departement, object : $what) isa EventWeatherRelations;";
						if (onlyDoubleClean==true)
						{
							graqlQuery11+=		"get;offset 1;\n";
						}
						else
						{
							graqlQuery11+=		"get;\n";
						}

						//System.out.println(graqlQuery11);

						QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
						List<ConceptMap> answers11= map11.get();

						System.out.println("ndept\\nbdate : " + ndept+ "\\" + nbdate + " (what existing doubles or old values) : "+ answers11.size());

						for(ConceptMap answer11:answers11)
						{

							Entity resource= answer11.get("what").asEntity();
							String idResource=resource.id().toString();

							deleteEventObjectById(transaction, idResource, RelationTypeEvent.WHATRELATION);
						}
						try
						{
							transaction.commit();
							//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = true;
						}
						catch (GraknClientException e) {
							System.out.println(e);
							boolean tobecontinued=false;
							if (e.getMessage().contains("There is more than one thing")
									&& e.getMessage().contains("that owns the key")) {
								tobecontinued=true;
							}
							if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
									&& e.getMessage().contains("Received Rst Stream")) {
								tobecontinued=true;
							}
							if (tobecontinued==true)
							{
								System.out.println("rest of the code...");
								close = true;
							} else {
								throw (e);
							}
						}
					}

				}
				if (close==false)
					transaction.commit();
			}
			System.out.println("Clean Double Weather OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Clean existing weather events
	private void PostCleanValueRelationDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			boolean okclean=true;
			int total=0;
			GraknClient.Transaction transaction= null;;

			while (okclean==true)
			{
				okclean=false;

				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false;
				}

				// query more than one value
				String graqlQuery11 = "match $wr (typevalue :$t, value : $v) isa "+ WeatherClass_TypeValueEventWeatherRelations + ";";
				if (onlyDoubleClean==true)
				{
					graqlQuery11+=		"get;offset 1;limit "+ maxGet + ";\n";
				}
				else
				{
					graqlQuery11+=		"get;limit "+ maxGet + ";\n";
				}

				System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				total+=answers11.size();
				System.out.println("typevaluerelations existing values : "+ answers11.size() +"\\" +total);

				for(ConceptMap answer11:answers11)
				{
					okclean=true;
					Entity value= answer11.get("v").asEntity();
					String idValue=value.id().toString();

					deleteTheValueById(transaction, idValue);
				}
				try
				{
					transaction.commit();
					//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = true;
				}
				catch (GraknClientException e) {
					System.out.println(e);
					boolean tobecontinued=false;
					if (e.getMessage().contains("There is more than one thing")
							&& e.getMessage().contains("that owns the key")) {
						tobecontinued=true;
					}
					if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
							&& e.getMessage().contains("Received Rst Stream")) {
						tobecontinued=true;
					}
					if (tobecontinued==true)
					{
						System.out.println("rest of the code...");
						close = true;
					} else {
						throw (e);
					}
				}


				if (close==false)
					transaction.commit();
			}
			System.out.println("Clean TypeValueRelations Weather OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}


	public void postCalculate(GraknClient.Session session)
	{

		super.postCalculate(session);

		if (myInputWeather == null)
		{
			myInputWeather=new MyInputWeather("", this);
			JsonArray jsonArray=PCRFilesStudyMigration.loadHistorics();
			myInputWeather.loadHistoric(jsonArray);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULWEATHERCLEAN)
		{
			PostCleanDoubleDataByClass(session);
			PostCleanValueRelationDataByClass(session);
		}
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULWEATHERNEXTDATE)
		{
			LinkNextEvent(session);
		}
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULWEATHERSAMEDATEPCRTEST)
		{
			LinkSameDateEvents(session);
		}
	}

	// Post calculation of precedents dated events
	private void LinkNextEvent(GraknClient.Session session)  
	{

		try
		{
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;



			//GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			Set<String> keysdept=getDepartementId().keySet();

			String[] depts=null;

			boolean dpt=false;
			if (optionImport.getDept()!=null)
			{
				if (optionImport.getDept().length>0)
				{
					depts=optionImport.getDept();
					dpt=true;
				}
			}
			if (dpt==false)
			{
				//depts=(String[])(keysdept.toArray());
				depts=new String[keysdept.size()];
				int i=0;
				for (String keydept:keysdept)
				{
					depts[i]=keydept;
					i++;
				}
			}


			int ndept=0;
			for (String keydept : depts)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				if (ndept>=linksdates_deptindex)
				{
					int nextrapole=(ndept*254);
					System.out.println("nextrapole:"+nextrapole);




					if (close == true) {
						transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
						close = false;
					}

					String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
					graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa "+ WeatherClass_EventWeatherRelations + ";";
					graqlQuery11+=		"$timedate isa " + WeatherClass_WeatherTimeDate + ", has EventDate $attributedate;";				

					if (mindays>0)
						graqlQuery11+=		"get;sort $attributedate asc;offset "+ mindays + ";limit " + maxdays + ";\n";
					else
						graqlQuery11+=		"get;sort $attributedate asc;limit " + maxdays + ";\n";

					System.out.println(graqlQuery11);

					QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
					List<ConceptMap> answers11= map11.get();
					String idTimePrevious=null;
					if (answers11.size()<(maxdays - mindays))
					{

						for(ConceptMap answer11:answers11)
						{

							Entity time1= answer11.get("timedate").asEntity();
							String idTimeNext=time1.id().toString();
							System.out.println("idTime :" + idTimeNext);

							if (idTimePrevious!=null)
							{
								createNextDateLink(transaction, idTimePrevious,  idTimeNext);
							}
							idTimePrevious=idTimeNext;

						}
						try
						{
							transaction.commit();
							//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = true;
						}
						catch (GraknClientException e) {
							System.out.println(e);
							boolean tobecontinued=false;
							if (e.getMessage().contains("There is more than one thing")
									&& e.getMessage().contains("that owns the key")) {
								tobecontinued=true;
							}
							if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
									&& e.getMessage().contains("Received Rst Stream")) {
								tobecontinued=true;
							}
							if (tobecontinued==true)
							{
								System.out.println("rest of the code...");
								close = true;
							} else {
								throw (e);
							}
						}

					}
					linksdates_deptindex=ndept;
					getMyInputWeather().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Next Date Weather OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
	// Post calculation of precedents dated events
	private void LinkSameDateEvents(GraknClient.Session session)  
	{

		try
		{
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;;

			Set<String> keysdept=getDepartementId().keySet();

			String[] depts=null;

			boolean dpt=false;

			if (optionImport.getDept()!=null)
			{
				if (optionImport.getDept().length>0)
				{
					depts=optionImport.getDept();
					dpt=true;
				}
			}
			if (dpt==false)
			{
				//depts=(String[])(keysdept.toArray());
				depts=new String[keysdept.size()];
				int i=0;
				for (String keydept:keysdept)
				{
					depts[i]=keydept;
					i++;
				}
			}

			int ndept=0;

			for (String keydept : depts)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				if (ndept>=linkssamedates_deptindex)
				{
					int nextrapole=(ndept*254);
					System.out.println("nextrapole:"+nextrapole);


					if (close == true) {
						transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
						close = false; 
					}


					// list of weather date
					////////////////////////////
					String graqlQuery11 = "match ";
					graqlQuery11 += "$Departement id "+ idDepartement + ";";
					graqlQuery11+=	"(time : $timedate, localization : $Departement) isa " + WeatherClass_EventWeatherRelations+";";
					graqlQuery11+=	"$timedate isa " + WeatherClass_WeatherTimeDate + ", has EventDate $attributedate;";				
					graqlQuery11+=		"get;sort $attributedate asc;\n";

					System.out.println(graqlQuery11);

					QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
					List<ConceptMap> answers11= map11.get();

					int ndate=0;
					int index=0;
					for(ConceptMap answer11:answers11)
					{
						
						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false; 
						}
						
						index++;
						System.out.println("index :" + index + "/" + answers11.size());
						ndate++;

						Entity time1= answer11.get("timedate").asEntity();
						//String idTime1=time1.id().toString();

						LocalDateTime date1 = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
						System.out.println("date :" + date1);

						//DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");


						// PCR Test at same date
						////////////////////////
						String graqlQuery21 = "match ";
						graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1 + ";";
						graqlQuery21+=	"$Departement id " + idDepartement + ";";
						graqlQuery21+=	"(time : $timedateevent, localization : $Departement) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
						graqlQuery21+=	"get;\n";

						//System.out.println(graqlQuery21);

						QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
						List<ConceptMap> answers21= map21.get();

						for(ConceptMap answer21:answers21)
						{					
							Entity timedateevent= answer21.get("timedateevent").asEntity();

							boolean reset=getOptionImport().getResetRelations();
							if (reset==true)
								removeSameDateLink(transaction, time1, timedateevent, idDepartement);

							createSameDateLink(transaction, time1, timedateevent, idDepartement);
						}


						try
						{
							transaction.commit();
							//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = true;
						}
						catch (GraknClientException e) {
							System.out.println(e);
							boolean tobecontinued=false;
							if (e.getMessage().contains("There is more than one thing")
									&& e.getMessage().contains("that owns the key")) {
								tobecontinued=true;
							}
							if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
									&& e.getMessage().contains("Received Rst Stream")) {
								tobecontinued=true;
							}
							if (tobecontinued==true)
							{
								System.out.println("rest of the code...");
								close = true;
							} else {
								throw (e);
							}

						}
					}
					//}

					linkssamedates_deptindex=ndept;
					getMyInputWeather().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Weather Next Date PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
}
