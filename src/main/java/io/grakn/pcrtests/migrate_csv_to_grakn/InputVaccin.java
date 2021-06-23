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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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



public class InputVaccin extends InputSetOfChronology implements IInputSetOfChronology{

	static final String KEYINPUTVACCIN="InputVaccin";
	static final String KEYINPUTVACCINVA="InputVaccinVA";
	static final String KEYINPUTVACCINVT="InputVaccinVT";
	static final String KEYINPUTVACCINLINKSDATES="InputVaccinLinksDates";

	public Boolean getUpdate() {
		return update;
	}
	public void setUpdate(Boolean update) {
		this.update = update;
	}


	Vaccin vaccin=null;
	MyInputVaccin myInputVaccin=null;
	Boolean update=false;

	public MyInputVaccin getMyInputVaccin() {
		return myInputVaccin;
	}
	public void setMyInputVaccin(MyInputVaccin myInputVaccin) {
		this.myInputVaccin = myInputVaccin;
	}
	public Map<Long, String> getCl_age90Id() {
		return vaccin.getCl_age90Id();
	}


	static final int maxnextdays=5;
	static final int incdays=1;
	static final int avnb=3;

	static final String vaccin_firstindice = "0";

	static int linksdates_deptindex=0;
	static int linkssamedates_deptindex=0;
	static long ind_clage=0;
	static long ind_prec_clage=0;

	String getMetaType() {return Vaccin.VaccinMetaTypeValue;}
	String GetValue_Eventrelation() {return Vaccin.VaccinClass_ValueEventVaccinRelations;};
	String getTypevalue_Eventrelations() {return Vaccin.VaccinClass_TypeValueEventVaccinRelations;};

	class MyInputVaccin extends Input {



		InputVaccin inputVaccin;
		LocalDate dateVaccin=LocalDate.parse((CharSequence)"1900-01-01");;



		@Override
		public void saveHistoric(ArrayList<Json> historics, boolean finalsave)
		{

			ArrayList<Json> subHistorics = new ArrayList<>();

			super.saveHistoric(subHistorics, finalsave);

			// Vaccin Test
			if (finalsave==true)
			{
				// dateVaccin
				Json itemDateVaccin = Json.object();
				itemDateVaccin.set("dateVaccin",dateVaccin.toString());
				subHistorics.add(itemDateVaccin);
			}

			String keyInputVaccin=KEYINPUTVACCIN;
			if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERAGE)
			{
				keyInputVaccin=KEYINPUTVACCINVA;
			}
			if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERTYPE)
			{
				keyInputVaccin=KEYINPUTVACCINVT;
			}

			Json itemInputVaccin = Json.object();
			itemInputVaccin.set(keyInputVaccin, subHistorics);

			historics=removeHistoric(historics, keyInputVaccin);
			historics.add(itemInputVaccin);

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

				// ind_prec_clage
				Json itemInd_Prec_Clage = Json.object();
				itemDeptIndexLinksDates.set("ind_prec_clage",ind_prec_clage);
				subHistorics2.add(itemInd_Prec_Clage);	

				// ind_clage
				Json itemInd_Clage = Json.object();
				itemDeptIndexLinksDates.set("ind_clage",ind_clage);
				subHistorics2.add(itemInd_Clage);	

				ind_prec_clage=ind_clage;
			}
			Json InputVaccinLinksDates = Json.object();
			InputVaccinLinksDates.set(KEYINPUTVACCINLINKSDATES, subHistorics2);

			historics=removeHistoric(historics, KEYINPUTVACCINLINKSDATES);
			historics.add(InputVaccinLinksDates);
		}


		@Override
		public void loadHistoric(JsonArray historicsInput )
		{

			String keyInputVaccin=KEYINPUTVACCIN;
			if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERAGE)
			{
				keyInputVaccin=KEYINPUTVACCINVA;
			}
			if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERTYPE)
			{
				keyInputVaccin=KEYINPUTVACCINVT;
			}

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
					if (elt1.getKey().equals(keyInputVaccin))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputVaccinList = (JsonArray) elt1.getValue();
						if (historicsInputVaccinList!=null)
							loadSpecificHistoric(historicsInputVaccinList);
					}
					if (elt1.getKey().equals(KEYINPUTVACCINLINKSDATES))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputVaccinListLinksDates = (JsonArray) elt1.getValue();
						if (historicsInputVaccinListLinksDates!=null)
							loadSpecificHistoric(historicsInputVaccinListLinksDates);
					}
				}
			}
		}

		@Override
		public void loadSpecificHistoric(JsonArray historicsInputVaccinList )
		{
			super.loadHistoric(historicsInputVaccinList );

			Iterator<JsonElement> iterator = historicsInputVaccinList.iterator();
			while (iterator.hasNext()) {
				JsonElement elt = iterator.next();
				JsonObject jsonObject= elt.getAsJsonObject();
				if (jsonObject.get("dateVaccin")!=null)
				{
					JsonElement jsonDateVaccin= jsonObject.get("dateVaccin");
					LocalDate minVaccin=LocalDate.parse((CharSequence)jsonDateVaccin.getAsString());
					if (minVaccin.isAfter(minDate))
					{
						minDate=minVaccin;
					}
				}

				if (jsonObject.get("firstIndice")!=null)
				{
					JsonElement jsonIndiceVaccin= jsonObject.get("firstIndice");
					Long firstIndiceVaccin=jsonIndiceVaccin.getAsLong();

					if (firstIndiceVaccin>indice)
					{
						indice=firstIndiceVaccin;
					}
				}

				if (jsonObject.get("minIndice")!=null)
				{
					JsonElement jsonIndiceVaccin= jsonObject.get("minIndice");
					Long minPcr=jsonIndiceVaccin.getAsLong();

					if (minPcr+1>minIndice)
					{
						minIndice=minPcr+1;
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

				if (jsonObject.get("ind_prec_clage")!=null)
				{
					JsonElement jsonInd_prec_clage= jsonObject.get("ind_prec_clage");
					ind_prec_clage=jsonInd_prec_clage.getAsLong();

				}

				if (jsonObject.get("ind_clage")!=null)
				{
					JsonElement jsonInd_Clage= jsonObject.get("ind_clage");
					ind_clage=jsonInd_Clage.getAsLong();

				}
			}
		}

		public MyInputVaccin(String path, InputVaccin inputVaccin) {
			super(path);
			// TODO Auto-generated constructor stub
			this.inputVaccin=inputVaccin;
			inputVaccin.setMyInputVaccin(this);

			this.firstIndice= Long.decode(vaccin_firstindice);
			this.indice = firstIndice;
		}

		@Override
		public String template(Json Vaccincohorts) {
			String graqlInsertQuery="";
			if (Vaccincohorts.at("dep")!=null)
			{

				indice++;

				dateVaccin=LocalDate.parse((CharSequence)Vaccincohorts.at("jour").asString());


				if (((indice<=maxIndice) && (indice>=minIndice) && (dateFilter==DF_CASE.FALSE))
						|| (dateVaccin.isAfter(minDate) && dateVaccin.isBefore(maxDate) && (dateFilter==DF_CASE.TRUE))
						|| ((indice<=maxIndice) && (indice>=minIndice) && dateVaccin.isAfter(minDate) && dateVaccin.isBefore(maxDate) && (dateFilter==DF_CASE.BOTH)))

				{
					String vaccinprefixid="V";
					if (inputVaccin.getOptionImport()!=null)
					{
						if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERAGE)
						{
							vaccinprefixid="VA";
						}
						if (inputVaccin.getOptionImport().getTypeImport()==TypeImport.IMPORTVACCINPERTYPE)
						{
							vaccinprefixid="VT";
						}
					}

					System.out.println("indice " + indice + "; date " + dateVaccin + "\n");
					String departement=Vaccincohorts.at("dep").asString();
					if (departement.charAt(0)=='0')
					{
						departement=departement.substring(1);
					}

					Long valuecl_age90=Long.valueOf(0);
					Json jsonclage=Vaccincohorts.at("clage_vacsi");
					if (jsonclage!=null)
					{
						if (jsonclage.isNull()==false)
							valuecl_age90=jsonclage.asLong();
					}

					graqlInsertQuery = "match ";
					graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", Vaccin.VaccinTypeValue_VACCINPOSTCALCUL);
					graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description", "Vaccin");
					graqlInsertQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", departement);

					//LocalDate dateVaccinmoins20=dateVaccin.minusDays(20);

					graqlInsertQuery+= "insert $Event-" + indice.toString() + " isa " + Vaccin.VaccinClass_EventVaccin +", has Id \"EVaccin-"+vaccinprefixid+"-" + indice.toString() + "\";\n";
					graqlInsertQuery+= "$IdentifiedPatientCohort-" + indice.toString() +" isa IdentifiedPatientCohort, has Identity \""+vaccinprefixid+"-" + departement+ "-"+ indice.toString()+ "\";\n";
					graqlInsertQuery+= "$TimeDate-" + indice.toString() +" isa "+ Vaccin.VaccinClass_VaccinTimeDate+", has Identity \"Vaccin-"+vaccinprefixid+"-TD-"+indice.toString()+ "\", has EventDate " + dateVaccin + ";\n";

					String valueTypeVaccin=null;
					Json jsonvaccin=Vaccincohorts.at("vaccin");
					if (jsonvaccin!=null)
					{
						if (jsonvaccin.isNull()==false)
							valueTypeVaccin=jsonvaccin.asString();
					}

					Long valueDose1=Long.valueOf(0);
					Json jsondose1=Vaccincohorts.at("n_dose1");
					if (jsondose1!=null)
					{
						if (jsondose1.isNull()==false)
							valueDose1=jsondose1.asLong();
					}

					Double valueCumulDose1=0.0;
					Json jsoncumdose1=Vaccincohorts.at("n_cum_dose1");
					if (jsoncumdose1!=null)
					{			
						if (jsoncumdose1.isNull()==false)
							valueCumulDose1=jsoncumdose1.asDouble();
					}

					Long valueDose2=Long.valueOf(0);
					Json jsondose2=Vaccincohorts.at("n_dose2");
					if (jsondose2==null)
					{	
						jsondose2=Vaccincohorts.at("n_complet");
						if (jsondose2!=null)
						{
							if (jsondose2.isNull()==false)
							{
								valueDose2=jsondose2.asLong();
							}
						}
					}
					else
					{
						if (jsondose2.isNull()==false)
						{
							valueDose2=jsondose2.asLong();
						}
					}
					Double valueCumulDose2=0.0;

					Json jsoncumdose2=Vaccincohorts.at("n_cum_dose2");
					if (jsoncumdose2!=null)
					{			
						if (jsoncumdose2.isNull()==false)
						{
							valueCumulDose2=jsoncumdose2.asDouble();
						}
					}
					Double dvaluepop=0.0;
					Json jsonpop=Vaccincohorts.at("pop");
					if (jsonpop!=null)
					{
						if (jsonpop.isNull()==false)
						{
							dvaluepop=jsonpop.asDouble();
						}
					}
					Double valueCoverage1=0.0;
					Double valueCoverage2=0.0;

					if (dvaluepop!=0.0)
					{
						valueCoverage1=((double) valueDose1)/((double) dvaluepop);
						valueCoverage2=((double) valueDose2)/((double) dvaluepop);

					}
					//Locale locale  = new Locale("en", "UK");
					//DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
					String sValueCoverage1=GraphQlDoubleFormat(valueCoverage1);
					String sValueCoverage2=GraphQlDoubleFormat(valueCoverage2);


					//Double dvaluepop=Vaccincohorts.at("pop").asDouble();
					Long valuepop=dvaluepop.longValue();

					graqlInsertQuery+= "$What-" + indice.toString() +" isa " + Vaccin.VaccinClass_WhatVaccin 
							+", has Identity \"WVaccin-"+vaccinprefixid+"-"+indice.toString()
							+ "\", has Description \"Vaccin-RESULTS-STATS\"";
					//		+ ", has Attribut-"+ VaccinTypeValue_cl_age90 + " " + valuecl_age90.toString() 


					graqlInsertQuery+=insertAttribut (Cohort.CohortTypeValue_cl_age90, valuecl_age90.toString());

					if (valueTypeVaccin!=null)
					{
						valueTypeVaccin="\""+valueTypeVaccin+"\"";
						graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_TypeVaccin1, valueTypeVaccin);
						graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_TypeVaccin2, valueTypeVaccin);
					}

					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_Dose1, valueDose1.toString());
					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_CumulDose1, valueCumulDose1.toString());
					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_Dose2, valueDose2.toString());
					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_CumulDose2, valueCumulDose2.toString());

					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_Coverage1, sValueCoverage1);
					graqlInsertQuery+=insertAttribut (Vaccin.VaccinTypeValue_Coverage2, sValueCoverage2);


					graqlInsertQuery+=insertAttribut (Cohort.CohortTypeValue_pop, valuepop.toString());

					graqlInsertQuery+=";\n";

					graqlInsertQuery += insertValue (Vaccin.VaccinTypeValue_VACCINPOSTCALCUL, vaccinprefixid+"-"+indice.toString(), "StringValue", Vaccin.TagInitPostCalculate, "$What-" + indice.toString());

					graqlInsertQuery+= "$eventRelation-" + indice.toString() +" (registeredevent : $Event-" + indice.toString()+ ", actor: $IdentifiedPatientCohort-" + indice.toString()+", localization:$Departement-" + departement + ", time :$TimeDate-" + indice.toString()+", goal :$Why-Vaccin" + ", object :$What-"+ indice.toString()+") isa " + Vaccin.VaccinClass_EventVaccinRelations+";\n";
				}
			}
			return (graqlInsertQuery);
		}
	}
	public InputVaccin(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
		vaccin= new Vaccin();

	}

	public InputVaccin(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, DF_CASE dateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport,filename,  myMaxGet,  minIndice,  maxIndice,  dateFilter, myMinDate,  myMaxDate, myMindays,  myMaxdays);
		vaccin= new Vaccin();

	}

	public InputVaccin(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport, filename,  myMaxGet,  minIndice,  maxIndice, myMinDate,  myMaxDate,  myMindays,  myMaxdays);
		vaccin= new Vaccin();

	}

	public Collection<Input> initialize (Collection<Input>inputs) {
		// TODO Auto-generated method stub
		return initialiseInputsVaccins(inputs);
	}

	// événement confinements
	private Collection<Input> initialiseInputsVaccins (Collection<Input>inputs) {

		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_EventVaccin, "event");
		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_EventVaccinRelations, "eventrelations"+SuffixEventRelations);
		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_ValueEventVaccinRelations, "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_TypeValueEventVaccinRelations, "ValueTypeRelation"+SuffixValueTypeRelations);
		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_VaccinTimeDate, "TimeDate");

		List<DefineAttribute> defineattributesWhatVaccin=new ArrayList<>();
		DefineAttribute attribut_cl_90 = 	new DefineAttribute(Cohort.CohortTypeAttributeValue_cl_age90,Cohort.CohortTypeValue_cl_age90);
		DefineAttribute attribut_Dose1 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_Dose1,Vaccin.VaccinTypeValue_Dose1);
		DefineAttribute attribut_Dose2 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_Dose2,Vaccin.VaccinTypeValue_Dose2);
		DefineAttribute attribut_CumulDose1 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_CumulDose1,Vaccin.VaccinTypeValue_CumulDose1);
		DefineAttribute attribut_CumulDose2 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_CumulDose2,Vaccin.VaccinTypeValue_CumulDose2);		
		DefineAttribute attribut_Coverage1 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_Coverage1,Vaccin.VaccinTypeValue_Coverage1);
		DefineAttribute attribut_Coverage2 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_Coverage2,Vaccin.VaccinTypeValue_Coverage2);

		DefineAttribute attribut_TypeVaccin1 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_TypeVaccin1,Vaccin.VaccinTypeValue_TypeVaccin1);
		DefineAttribute attribut_TypeVaccin2 = 	new DefineAttribute(Vaccin.VaccinTypeAttributeValue_TypeVaccin2,Vaccin.VaccinTypeValue_TypeVaccin2);
		DefineAttribute attribut_pop = 	new DefineAttribute(Cohort.CohortTypeAttributeValue_pop,Cohort.CohortTypeValue_pop);

		defineattributesWhatVaccin.add(attribut_TypeVaccin1);
		defineattributesWhatVaccin.add(attribut_Dose1);
		defineattributesWhatVaccin.add(attribut_CumulDose1);
		defineattributesWhatVaccin.add(attribut_Coverage1);

		defineattributesWhatVaccin.add(attribut_TypeVaccin2);
		defineattributesWhatVaccin.add(attribut_Dose2);
		defineattributesWhatVaccin.add(attribut_CumulDose2);
		defineattributesWhatVaccin.add(attribut_Coverage2);

		defineattributesWhatVaccin.add(attribut_cl_90);
		defineattributesWhatVaccin.add(attribut_pop);

		inputs=initialiseDefine(inputs, Vaccin.VaccinClass_WhatVaccin, "What",defineattributesWhatVaccin);

		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WVaccin", "Description", "Vaccin");

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_AVCoverage1);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_AVCoverage2);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_VCoverage1);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_VCoverage2);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_ACoverage1);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_ACoverage2);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", Vaccin.VaccinTypeValue_VACCINPOSTCALCUL);

		// data
		inputs.add(new MyInputVaccin(setOfData.getFilename(), this));

		return inputs;
	}

	protected void initializeCaches(GraknClient.Session session)
	{
		super.initializeCaches(session);
		vaccin.initializeCaches(session);
	}

	public void postCalculate(GraknClient.Session session)
	{

		super.postCalculate(session);

		if (myInputVaccin == null)
		{
			myInputVaccin=new MyInputVaccin("", this);
			JsonArray jsonArray=PCRFilesStudyMigration.loadHistorics();
			myInputVaccin.loadHistoric(jsonArray);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULVACCIN)
		{
			PostAverageCalculationByClass(session, optionImport);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULVACCINNEXTDATE)
		{
			LinkNextEvent(session);
		}
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULVACCINSAMEDATEPCRTEST)
		{
			LinkSameDateEvents(session);
		}

	}


	// Post calculation of derivated (speed)
	private void PostAverageReinitCalculation(GraknClient.Session session)  {

		try
		{
			Boolean okanswers = true;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;

			while (okanswers==true && nb<maxIndice)
			{
				okanswers=false;

				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+	", has TypeValueId $TypeValueId; $TypeValueId ==\"" + Vaccin.VaccinTypeValue_VACCINPOSTCALCUL+ "-" + Vaccin.TagAveragePostCalculate + "\";";
				//						+ 	", has StringValueAttribute \"" + TagInitPostCalculate+ "\";";
				graqlQuery11+= 	"get;limit "+ maxGet + ";\n";
				System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				System.out.println("to delete :" + answers11.size());

				for(ConceptMap answer11:answers11)
				{			
					okanswers = true;

					//						System.out.println(answer11.toString());

					Entity value1= answer11.get("valuepostcalcul").asEntity();
					String idpostcalculvalue1=value1.id().toString();

					String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1 + ";";
					graqlQuery12+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery12+=		"(object : $resource) isa EventVaccinRelations;";
					//graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $resource) isa EventVaccinRelations;";
					graqlQuery12+=		"$resource isa Resource, has Identity $identity";	
					//graqlQuery12+=		queryAttribut (VaccinTypeValue_cl_age90, "cl_age90");	
					//graqlQuery12+=		queryAttribut (VaccinTypeValue_PdivT, "attributePdivT");	
					graqlQuery12+=		";";	
					//graqlQuery12+=		"$Departement isa Departement;";	
					//graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
					graqlQuery12+=		"get;limit ";				
					graqlQuery12+=		maxGet + ";\n";

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();


					for(ConceptMap answer12:answers12)
					{	
						/*LocalDateTime valuedate1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
							System.out.println(valuedate1);*/

						String identity1 = (String) answer12.get("identity").asAttribute().value();
						//System.out.println(identity1);

						/*Entity departement1= answer12.get("Departement").asEntity();
							String idDepartement=departement1.id().toString();
							System.out.println("idDepartement :" + idDepartement);*/

						Entity resource1= answer12.get("resource").asEntity();
						String idResource=resource1.id().toString();
						//System.out.println("idResource :" + idResource);

						/*String id_cl_age90 = answer12.get("cl_age90").asAttribute().id().toString();
							System.out.println("cl_age90 :" + id_cl_age90);*/

						int posnum=identity1.lastIndexOf('-');
						String valnum1=identity1.substring(posnum+1);

						/*Double valuePdivT1 = (Double) answer12.get("attributePdivT").asAttribute().value();
							System.out.println("attributePdivT :" + valuePdivT1);*/

						// old calculation delete
						deleteValueByType(transaction, idResource, Vaccin.VaccinTypeValue_AVCoverage1);
						deleteValueByType(transaction, idResource, Vaccin.VaccinTypeValue_AVCoverage2);

						// delete old post calcul
						QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

						// insertion des valeurs
						String graqlInsertQuery = "match $resource id "+ idResource+ ";";				
						graqlInsertQuery+=		"$TypeValue-VACCINPOSTCALCUL isa TypeValue, has IdValue  \""+ Vaccin.VaccinTypeValue_VACCINPOSTCALCUL +"\";";

						graqlInsertQuery+= "insert ";

						// update de postcalcul
						graqlInsertQuery += VaccinPostCalculationQuerySet(valnum1, Vaccin.TagInitPostCalculate);

						QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
						List<ConceptMap> answers7= map7.get();

						nb++;
						System.out.println("Delete : " + nb);
					}


				} // for answer1
				System.out.println("Delete : " + nb);
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			} // while ok

			transaction.commit();
			System.out.println("PostCalculation Average Vaccin OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Post calculation of average
	private void PostAverageCalculationByClass (GraknClient.Session session, OptionImport optionImport)  {

		try
		{
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;;

			//GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;


			Set<String> keysdept=getDepartementId().keySet();
			Set<Long> keyscl_age90=getCl_age90Id().keySet();

			String[] depts=null;
			String[] clsage90=null;

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

			boolean cl=false;
			if (optionImport.getClage90()!=null)
			{
				if (optionImport.getClage90().length>0)
				{
					clsage90=optionImport.getClage90();
					cl=true;
				}
			}
			if (cl==false)
			{
				clsage90=new String[keyscl_age90.size()];
				int i=0;
				for (Long keycl_age90:keyscl_age90)
				{
					clsage90[i]=keycl_age90.toString();
					i++;
				}
			}

			int ndept=0;
			for (String keydept : depts)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				int nextrapole=(ndept*keyscl_age90.size()*254);
				System.out.println("nextrapole:"+nextrapole);

				if (nextrapole>minIndice)
				{
					for (String keycl_age90 : clsage90)
					{

						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}

						String idCl_age90 = getCl_age90Id().get(Long.parseLong(keycl_age90));

						String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
						graqlQuery11 += 	"$Cl_age90 id "+ idCl_age90 + ";";
						graqlQuery11 += 	"$what isa " + Vaccin.VaccinClass_WhatVaccin + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90, has Identity $identity";
						graqlQuery11+=		queryAttribut (Vaccin.VaccinTypeValue_Coverage1, "attributeCoverage1");	
						graqlQuery11+=		queryAttribut (Vaccin.VaccinTypeValue_Coverage2, "attributeCoverage2");	
						graqlQuery11+=		";";	
						graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventVaccinRelations;";
						graqlQuery11+=		"$timedate isa VaccinTimeDate, has EventDate $attributedate;";				
						if (mindays>0)
							graqlQuery11+=		"get;sort $attributedate asc;offset "+ mindays + ";limit " + maxdays + ";\n";
						else
							graqlQuery11+=		"get;sort $attributedate asc;limit " + maxdays + ";\n";

						System.out.println(graqlQuery11);

						QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
						List<ConceptMap> answers11= map11.get();

						System.out.println("derivated AVCoverage existing values : "+ answers11.size());

						if (answers11.size()<(maxdays - mindays))
						{
							// suppress old values
							int isup=0;
							for(ConceptMap answer11:answers11)
							{
								if (isup>=avnb-1)
								{
									Entity resource= answer11.get("what").asEntity();
									String idResource=resource.id().toString();

									deleteValueByType(transaction, idResource, Vaccin.VaccinTypeValue_AVCoverage1);
									deleteValueByType(transaction, idResource, Vaccin.VaccinTypeValue_AVCoverage2);
								}
								isup++;
							}

							int 	nbCoverage=0;

							int size=answers11.size();
							Double	sumvalueCoverage1[]=new Double[size+avnb];
							Double	sumvalueCoverage2[]=new Double[size+avnb];
							LocalDateTime[] valuedates=new LocalDateTime[size+avnb];
							Double	valueAVCoverage1[]=new Double[size+avnb];
							Double	valueAVCoverage2[]=new Double[size+avnb];
							Double	valueVCoverage1[] =new Double[size+avnb];
							Double	valueVCoverage2[] =new Double[size+avnb];
							Double	valueACoverage1[]=new Double[size+avnb];
							Double	valueACoverage2[]=new Double[size+avnb];

							// init
							for (int ival=0;ival<size+avnb;ival++)
							{
								sumvalueCoverage1[ival]=0.0;
								sumvalueCoverage2[ival]=0.0;

							}

							for(ConceptMap answer11:answers11)
							{
								Double valueCoverage1_1 = (Double) answer11.get("attributeCoverage1").asAttribute().value();
								System.out.println("attributeCoverage1 :" + valueCoverage1_1);

								Double valueCoverage2_1 = (Double) answer11.get("attributeCoverage2").asAttribute().value();
								System.out.println("attributeCoverage2 :" + valueCoverage2_1);


								LocalDateTime valuedate1 = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
								System.out.println(valuedate1);

								Entity what1= answer11.get("what").asEntity();
								String idWhat=what1.id().toString();
								System.out.println("idWhat :" + idWhat);

								String identity1 = (String) answer11.get("identity").asAttribute().value();
								System.out.println(identity1);

								int posnum=identity1.lastIndexOf('-');
								String valnum1=identity1.substring(posnum+1);

								// insertion des valeurs
								String graqlInsertQuery = "match $what id "+ idWhat+ ";";				
								graqlInsertQuery+=		"$TypeValue-AVCoverage1 isa TypeValue, has IdValue  \""+ Vaccin.VaccinTypeValue_AVCoverage1 + "\";";
								graqlInsertQuery+=		"$TypeValue-AVCoverage2 isa TypeValue, has IdValue  \""+ Vaccin.VaccinTypeValue_AVCoverage2 + "\";";
								graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ Vaccin.VaccinTypeValue_VACCINPOSTCALCUL +"\";";
								;
								String insertattributes="";

								graqlInsertQuery+= "insert ";

								// sum
								//////////
								for (int ival=0;ival<avnb;ival++)
								{
									int jval=nbCoverage+ival;
									sumvalueCoverage1[jval]+=valueCoverage1_1;
									sumvalueCoverage2[jval]+=valueCoverage2_1;
								}

								// date
								valuedates[nbCoverage]=valuedate1;

								// averages
								///////////
								if (nbCoverage-avnb>0)
								{
									valueAVCoverage1[nbCoverage]=sumvalueCoverage1[nbCoverage]/avnb;
									valueAVCoverage2[nbCoverage]=sumvalueCoverage2[nbCoverage]/avnb;
								}
								else
								{
									valueAVCoverage1[nbCoverage]=sumvalueCoverage1[nbCoverage]/(nbCoverage+1);
									valueAVCoverage2[nbCoverage]=sumvalueCoverage2[nbCoverage]/(nbCoverage+1);
								}

								String averagevalueCoverage1=GraphQlDoubleFormat(valueAVCoverage1[nbCoverage]);
								insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_value, averagevalueCoverage1);
								String averagevalueCoverage2=GraphQlDoubleFormat(valueAVCoverage1[nbCoverage]);
								insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_value, averagevalueCoverage2);

								if (valueAVCoverage1[nbCoverage]<0.0)
								{
									System.out.println("valueAVCoverage1[nbCoverage] négatif");
								}
								if (valueAVCoverage2[nbCoverage]<0.0)
								{
									System.out.println("valueAVCoverage2[nbCoverage] négatif");
								}

								// speeds
								/////////
								if (nbCoverage>0)
								{
									LocalDateTime date1 = valuedates[nbCoverage-1];
									LocalDateTime date2 = valuedates[nbCoverage];
									long delay=date1.until(date2, ChronoUnit.DAYS);

									// coverage1
									////////////
									if (delay!=0 && valueAVCoverage1[nbCoverage]!=null && valueAVCoverage1[nbCoverage-1]!=null)
									{
										valueVCoverage1[nbCoverage]=(valueAVCoverage1[nbCoverage]-valueAVCoverage1[nbCoverage-1])
												/delay;

										// speed insertion
										String speedvalueCoverage1=GraphQlDoubleFormat(valueVCoverage1[nbCoverage]);
										insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_speed, speedvalueCoverage1);

										// acceleration
										if (nbCoverage>1 && valueVCoverage1[nbCoverage]!=null && valueVCoverage1[nbCoverage-1]!=null)
										{
											valueACoverage1[nbCoverage]=(valueVCoverage1[nbCoverage]-valueVCoverage1[nbCoverage-1])
													/delay;

											// acceleration insertion
											String accelerationvalueCoverage1=GraphQlDoubleFormat(valueACoverage1[nbCoverage]);
											insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_acceleration, accelerationvalueCoverage1);

											if (valueACoverage1[nbCoverage]<0.0)
											{
												System.out.println("valueACoverage1[nbCoverage] négatif");
											}
										}
										graqlInsertQuery += insertValueWithAttributes (Vaccin.VaccinMetaTypeValue, Vaccin.VaccinTypeValue_AVCoverage1, valnum1, "DoubleValueDerivated", insertattributes, "$what", Vaccin.VaccinClass_ValueEventVaccinRelations, Vaccin.VaccinClass_TypeValueEventVaccinRelations);

										if ((nbCoverage>=avnb-1) && (nbCoverage<maxdays-mindays))
										{
											System.out.println("query : "+ graqlInsertQuery);

											QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
											List<ConceptMap> answers2= map2.get();
										}
									}

									// coverage2
									////////////
									if (delay!=0 && valueAVCoverage2[nbCoverage]!=null && valueAVCoverage2[nbCoverage-1]!=null)
									{
										valueVCoverage2[nbCoverage]=(valueAVCoverage2[nbCoverage]-valueAVCoverage2[nbCoverage-1])
												/delay;

										// speed insertion
										String speedvalueCoverage2=GraphQlDoubleFormat(valueVCoverage2[nbCoverage]);
										insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_speed, speedvalueCoverage2);

										// acceleration
										if (nbCoverage>1 && valueVCoverage2[nbCoverage]!=null && valueVCoverage2[nbCoverage-1]!=null)
										{
											valueACoverage2[nbCoverage]=(valueVCoverage2[nbCoverage]-valueVCoverage2[nbCoverage-1])
													/delay;

											// acceleration insertion
											String accelerationvalueCoverage2=GraphQlDoubleFormat(valueACoverage2[nbCoverage]);
											insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_acceleration, accelerationvalueCoverage2);

											if (valueACoverage2[nbCoverage]<0.0)
											{
												System.out.println("valueACoverage2[nbCoverage] négatif");
											}
										}
										graqlInsertQuery += insertValueWithAttributes (Vaccin.VaccinMetaTypeValue, Vaccin.VaccinTypeValue_AVCoverage2, valnum1, "DoubleValueDerivated", insertattributes, "$what", Vaccin.VaccinClass_ValueEventVaccinRelations, Vaccin.VaccinClass_TypeValueEventVaccinRelations);

										if ((nbCoverage>=avnb-1) && (nbCoverage<maxdays-mindays))
										{
											System.out.println("query : "+ graqlInsertQuery);

											QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
											List<ConceptMap> answers2= map2.get();
										}
									}

								}
								nbCoverage++;
								System.out.println("nbCoverage : "+ nbCoverage);
								System.out.println("(nextrapole:"+nextrapole+")");
								nb++;
								System.out.println("PostCalculated : " + nb);
							}
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
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Stat Vaccin OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}


	// Post calculation of average
	private void PostCleanDoubleDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			GraknClient.Transaction transaction= null;;

			Set<String> keysdept=getDepartementId().keySet();
			Set<Long> keyscl_age90=getCl_age90Id().keySet();
			Set<LocalDateTime> keysdate=getDateId().keySet();

			int ndept=0;
			for (String keydept : keysdept)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				int nextrapole=(ndept*keyscl_age90.size()*254);
				System.out.println("nextrapole:"+nextrapole);

				if (nextrapole>minIndice)
				{
					for (Long keycl_age90 : keyscl_age90)
					{
						String idCl_age90 = getCl_age90Id().get(keycl_age90);

						for (LocalDateTime keydate : keysdate)
						{
							String idDate = getDateId().get(keydate);


							if (close == true) {
								transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
								close = false;
							}

							// query more than one value
							String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
							graqlQuery11 += 	"$Cl_age90 id "+ idCl_age90 + ";";
							graqlQuery11 += 	"$timedate id "+ idDate + ";";
							graqlQuery11 += 	"$what isa " + Vaccin.VaccinClass_WhatVaccin + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90, has Identity $identity;";
							graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventVaccinRelations;";
							graqlQuery11+=		"get;offset 1;\n";

							System.out.println(graqlQuery11);

							QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
							List<ConceptMap> answers11= map11.get();

							System.out.println("what existing doubles : "+ answers11.size());

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
				}
				if (close==false)
					transaction.commit();
			}
			System.out.println("Clean Double Vaccin OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	private // Post calculation set
	String VaccinPostCalculationQuerySet(String valnum1, String set)  {

		/*String graqlInsertQuery= "$Value-PPOSTCALCUL isa StringValue, has IdValue \"VaccinPPOSTCALCUL-"+valnum1+ "\", has StringValueAttribute \"" + set + "\";\n";
		graqlInsertQuery+= "$ValueRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, resource : $resource) isa ValueEventVaccinRelations" + ";\n";
		graqlInsertQuery+= "$ValueTypeRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, typevalue : $TypeValue-PPOSTCALCUL) isa TypeValueEventVaccinRelations ;\n";
		 */

		//String graqlInsertQuery = insertValue (VaccinTypeValue_PPOSTCALCUL, valnum1, "StringValue", set, "$resource", VaccinClass_ValueEventVaccinRelations, VaccinClass_TypeValueEventVaccinRelations);

		String graqlInsertQuery = PostCalculationQuerySet(Vaccin.VaccinTypeValue_VACCINPOSTCALCUL, valnum1, set);

		return graqlInsertQuery;
	}


	String getMetaTypeValue()
	{
		return Vaccin.VaccinMetaTypeValue;
	}

	// class relations value
	String getClass_ValueEventRelations()
	{
		return Vaccin.VaccinClass_ValueEventVaccinRelations;
	}

	// class relations type value
	String getClass_TypeValueEventRelations()
	{
		return Vaccin.VaccinClass_TypeValueEventVaccinRelations;
	}


	// Post calculation of precedents dated events
	private void LinkNextEvent(GraknClient.Session session)  
	{

		try
		{
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;;

			//GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;


			Set<String> keysdept=getDepartementId().keySet();
			Set<Long> keyscl_age90=getCl_age90Id().keySet();

			String[] depts=null;
			String[] clsage90=null;

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

			boolean cl=false;
			if (optionImport.getClage90()!=null)
			{
				if (optionImport.getClage90().length>0)
				{
					clsage90=optionImport.getClage90();
					cl=true;
				}
			}
			if (cl==false)
			{
				clsage90=new String[keyscl_age90.size()];
				int i=0;
				for (Long keycl_age90:keyscl_age90)
				{
					clsage90[i]=keycl_age90.toString();
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
					int nextrapole=(ndept*keyscl_age90.size()*254);
					System.out.println("nextrapole:"+nextrapole);

					//if (nextrapole>minIndice)
					//{
					for (String keycl_age90 : clsage90)
					{

						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}

						String idCl_age90 = getCl_age90Id().get(Long.parseLong(keycl_age90));


						String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
						graqlQuery11 += 	"$Cl_age90 id "+ idCl_age90 + ";";
						graqlQuery11 += 	"$what isa " + Vaccin.VaccinClass_WhatVaccin + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90;";
						graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventVaccinRelations;";
						graqlQuery11+=		"$timedate isa TimeDate, has EventDate $attributedate;";				

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
						//}
					}
					linksdates_deptindex=ndept;
					getMyInputVaccin().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Next Date Vaccin OK.\n");
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
			Set<Long> keyscl_age90=getCl_age90Id().keySet();

			String[] depts=null;
			List<Long> clsage90 = new ArrayList<Long>();

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

			boolean cl=false;
			if (optionImport.getClage90()!=null)
			{
				if (optionImport.getClage90().length>0)
				{
					for (String aclass:optionImport.getClage90())
					{
						clsage90.add(Long.decode(aclass));
					}
					cl=true;
				}
			}
			if (cl==false)
			{

				int i=0;
				for (Long keycl_age90:keyscl_age90)
				{
					clsage90.add(keycl_age90);
					i++;
				}
			}
			Collections.sort(clsage90);
			
			int ndept=0;

			for (String keydept : depts)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				if (ndept>=linkssamedates_deptindex)
				{
					int nextrapole=(ndept*254);
					System.out.println("nextrapole:"+nextrapole);


					for (Long keycl_age90 : clsage90)
					{

						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}

						//String idCl_age90 = getCl_age90Id().get(Long.parseLong(keycl_age90));
						if (keycl_age90>=ind_clage)
						{
							String idCl_age90 = getCl_age90Id().get(keycl_age90);
							ind_clage=keycl_age90;

							// list of vaccin date
							////////////////////////////

							String graqlQueryAge;
							if (ind_clage ==0 && ind_prec_clage==0)
							{
								graqlQueryAge="$keycl_age90 "+ind_clage+";";
							}
							else if (ind_prec_clage==0)
							{
								graqlQueryAge			= "$keycl_age90<="+ind_clage+";";
								graqlQueryAge			+= "$keycl_age90>=1;";
							}
							else
							{
								graqlQueryAge			= "$keycl_age90<="+ind_clage+";";
								graqlQueryAge			+= "$keycl_age90>"+ind_prec_clage+";";
							}

							String graqlQuery11 = "match ";
							graqlQuery11 += "$Departement id "+ idDepartement + ";";
							graqlQuery11 += "$what isa " + Vaccin.VaccinClass_WhatVaccin + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $keycl_age90;";
							graqlQuery11 += graqlQueryAge;
							graqlQuery11+=	"(time : $timedate, localization : $Departement, object : $what) isa " + Vaccin.VaccinClass_EventVaccinRelations+";";
							graqlQuery11+=	"$timedate isa " + Vaccin.VaccinClass_VaccinTimeDate + ", has EventDate $attributedate;";				
							graqlQuery11+=	"get;sort $attributedate asc;\n";

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
								graqlQuery21 += "$Cl_age90 id "+ idCl_age90 + ";";
								graqlQuery21 += "$what isa " + PCRTest.PCRTestClass_WhatPCRTest+ ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90;";
								graqlQuery21+=	"$Departement id " + idDepartement + ";";
								graqlQuery21+=	"(time : $timedateevent, localization : $Departement, object : $what) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
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
							getMyInputVaccin().saveHistoric(PCRFilesStudyMigration.historics, true);
							PCRFilesStudyMigration.saveHistorics();
							ind_prec_clage=ind_clage;
						}
					}
					ind_clage=0;
					ind_prec_clage=0;
					linkssamedates_deptindex=ndept;
					getMyInputVaccin().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Vaccin Next Date PCR OK.\n");
		}

		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
}
