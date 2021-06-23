package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import grakn.client.concept.thing.Entity;
import grakn.client.concept.type.EntityType;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;



public class InputPCRTest extends InputSetOfChronology implements IInputSetOfChronology{

	PCRTest pcrtest=null;
	MyInputPCRTest myInputPCRTest=null;

	public MyInputPCRTest getMyInputPCRTest() {
		return myInputPCRTest;
	}
	public void setMyInputPCRTest(MyInputPCRTest myInputPCRTest) {
		this.myInputPCRTest = myInputPCRTest;
	}
	public Map<Long, String> getCl_age90Id() {
		return pcrtest.getCl_age90Id();
	}

	static final int maxnextdays=5;
	static final int incdays=1;
	static final int avnb=3;

	static final String pcr_firstindice = "0";

	static int linksdates_deptindex=0;
	static int linkssamedates_deptindex=0;
	static long ind_clage=0;
	static long ind_prec_clage=0;

	String getMetaType() {return PCRTest.PCRTestMetaTypeValue;}
	String GetValue_Eventrelation() {return PCRTest.PCRTestClass_ValueEventPCRTestRelations;};
	String getTypevalue_Eventrelations() {return PCRTest.PCRTestClass_TypeValueEventPCRTestRelations;};

	class MyInputPCRTest extends Input {



		InputPCRTest inputpcrtest;
		LocalDate datepcr=LocalDate.parse((CharSequence)"1900-01-01");;



		@Override
		public void saveHistoric(ArrayList<Json> historics, boolean finalsave)
		{

			ArrayList<Json> subHistorics = new ArrayList<>();

			super.saveHistoric(subHistorics, finalsave);

			// PCR Test
			if (finalsave==true)
			{
				// datePCR
				Json itemDatePcr = Json.object();
				itemDatePcr.set("datepcr",datepcr.toString());
				subHistorics.add(itemDatePcr);
			}
			Json itemInputPCRTest = Json.object();
			itemInputPCRTest.set("InputPCRTest", subHistorics);

			historics=removeHistoric(historics, "InputPCRTest");
			historics.add(itemInputPCRTest);

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
			Json InputPCRTestLinksDates = Json.object();
			InputPCRTestLinksDates.set("InputPCRTestLinksDates", subHistorics2);

			historics=removeHistoric(historics, "InputPCRTestLinksDates");
			historics.add(InputPCRTestLinksDates);
		}


		@Override
		public void loadHistoric(JsonArray historicsInput )
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
					if (elt1.getKey().equals("InputPCRTest"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputPCRList = (JsonArray) elt1.getValue();
						if (historicsInputPCRList!=null)
							loadSpecificHistoric(historicsInputPCRList);
					}
					if (elt1.getKey().equals("InputPCRTestLinksDates"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputPCRListLinksDates = (JsonArray) elt1.getValue();
						if (historicsInputPCRListLinksDates!=null)
							loadSpecificHistoric(historicsInputPCRListLinksDates);
					}
				}
			}
		}

		@Override
		public void loadSpecificHistoric(JsonArray historicsInputPCRList )
		{
			super.loadHistoric(historicsInputPCRList );

			Iterator<JsonElement> iterator = historicsInputPCRList.iterator();
			while (iterator.hasNext()) {
				JsonElement elt = iterator.next();
				JsonObject jsonObject= elt.getAsJsonObject();
				if (jsonObject.get("datepcr")!=null)
				{
					JsonElement jsonDatePcr= jsonObject.get("datepcr");
					LocalDate minPcr=LocalDate.parse((CharSequence)jsonDatePcr.getAsString());
					if (minPcr.isAfter(minDate))
					{
						minDate=minPcr;
					}
				}

				if (jsonObject.get("firstIndice")!=null)
				{
					JsonElement jsonIndicePcr= jsonObject.get("firstIndice");
					Long firstIndicePcr=jsonIndicePcr.getAsLong();

					if (firstIndicePcr>indice)
					{
						indice=firstIndicePcr;
					}
				}

				if (jsonObject.get("minIndice")!=null)
				{
					JsonElement jsonIndicePcr= jsonObject.get("minIndice");
					Long minPcr=jsonIndicePcr.getAsLong();

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

		public MyInputPCRTest(String path, InputPCRTest inputpcrtest) {
			super(path);
			// TODO Auto-generated constructor stub
			this.inputpcrtest=inputpcrtest;
			inputpcrtest.setMyInputPCRTest(this);

			this.firstIndice= Long.decode(pcr_firstindice);
			this.indice = firstIndice;
		}

		@Override
		public String template(Json pcrtestcohorts) {
			String graqlInsertQuery="";
			if (pcrtestcohorts.at("dep")!=null)
			{

				indice++;

				datepcr=LocalDate.parse((CharSequence)pcrtestcohorts.at("jour").asString());


				if (((indice<=maxIndice) && (indice>=minIndice) && (dateFilter==DF_CASE.FALSE))
						|| (datepcr.isAfter(minDate) && datepcr.isBefore(maxDate) && (dateFilter==DF_CASE.TRUE))
						|| ((indice<=maxIndice) && (indice>=minIndice) && datepcr.isAfter(minDate) && datepcr.isBefore(maxDate) && (dateFilter==DF_CASE.BOTH)))

				{
					System.out.println("indice " + indice + "; date " + datepcr + "\n");
					String departement=pcrtestcohorts.at("dep").asString();
					if (departement.charAt(0)=='0')
					{
						departement=departement.substring(1);
					}
					graqlInsertQuery = "match ";
					//graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", PCRTestTypeValue_P);
					//graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_T);
					//graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_PdivT);
					graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTest.PCRTestTypeValue_PPOSTCALCUL);
					graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS);
					//graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_cl_age90);
					//graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_pop);
					graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description", "PCR-TEST");
					graqlInsertQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", departement);

					LocalDate datepcrmoins20=datepcr.minusDays(20);

					graqlInsertQuery+= "insert $Event-" + indice.toString() + " isa " + PCRTest.PCRTestClass_EventPCRTest +", has Id \"EPCR-" + indice.toString() + "\";\n";
					graqlInsertQuery+= "$IdentifiedPatientCohort-" + indice.toString() +" isa IdentifiedPatientCohort, has Identity \"" + departement+ "-"+ indice.toString()+ "\";\n";
					graqlInsertQuery+= "$TimeDate-" + indice.toString() +" isa "+ PCRTest.PCRTestClass_PCRTimeDate+", has Identity \"PCRTD-"+indice.toString()+ "\", has EventDate " + datepcr + ";\n";
					graqlInsertQuery+= "$TimeDate-20-" + indice.toString() +" isa "+ PCRTest.PCRTestClass_PCRTimeDate +", has Identity \"PCRTD-20-"+indice.toString()+ "\", has EventDate " + datepcrmoins20 + ";\n";
					//graqlInsertQuery+= "$Departement-" + indice.toString() +" isa Departement, has Identity \"PCRTR-"+indice.toString()+ "\", has CodeGLN \"" + pcrtestcohorts.at("dep").asLong()+ "\";\n";

					Long valueP=pcrtestcohorts.at("P").asLong();
					Long valueT=pcrtestcohorts.at("T").asLong();
					Double valuePdivT=0.0;
					if (valueT!=0.0)
					{
						valuePdivT=((double) valueP)/((double) valueT);
					}
					//Locale locale  = new Locale("en", "UK");
					//DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
					String sPdivT=GraphQlDoubleFormat(valuePdivT);

					Long valuecl_age90=pcrtestcohorts.at("cl_age90").asLong();

					Double dvaluepop=pcrtestcohorts.at("pop").asDouble();
					Long valuepop=dvaluepop.longValue();

					graqlInsertQuery+= "$What-" + indice.toString() +" isa " + PCRTest.PCRTestClass_WhatPCRTest 
							+", has Identity \"WPCR-"+indice.toString()
							+ "\", has Description \"PCR-RESULTS-STATS\"";
					//		+ ", has Attribut-"+ PCRTestTypeValue_cl_age90 + " " + valuecl_age90.toString() 
					graqlInsertQuery+=insertAttribut (Cohort.CohortTypeValue_cl_age90, valuecl_age90.toString());
					graqlInsertQuery+=insertAttribut (PCRTest.PCRTestTypeValue_P, valueP.toString());
					graqlInsertQuery+=insertAttribut (PCRTest.PCRTestTypeValue_T, valueT.toString());
					graqlInsertQuery+=insertAttribut (PCRTest.PCRTestTypeValue_PdivT, sPdivT);
					graqlInsertQuery+=insertAttribut (Cohort.CohortTypeValue_pop, valuepop.toString());

					graqlInsertQuery+=";\n";

					//graqlInsertQuery += insertValue (PCRTestTypeValue_P, indice.toString(), "LongValue", valueP.toString(), "$What-" + indice.toString());
					//graqlInsertQuery += insertValue (PCRTestTypeValue_T, indice.toString(), "LongValue", valueT.toString(), "$What-" + indice.toString());
					//graqlInsertQuery += insertValue (PCRTestTypeValue_PdivT, indice.toString(), "DoubleValue", sPdivT, "$What-" + indice.toString());
					//graqlInsertQuery += insertValue (PCRTestTypeValue_cl_age90, indice.toString(), "LongValue", valuecl_age90.toString(), "$What-" + indice.toString());
					//graqlInsertQuery += insertValue (PCRTestTypeValue_pop, indice.toString(), "LongValue", valuepop.toString(), "$What-" + indice.toString());

					graqlInsertQuery += insertValue (PCRTest.PCRTestTypeValue_PPOSTCALCUL, indice.toString(), "StringValue", PCRTest.TagInitPostCalculate, "$What-" + indice.toString());

					// relation between date
					graqlInsertQuery+= "$Period-"+ indice.toString() + " isa "+ PCRTest.PCRTestClass_PCRPeriodOfTime + ", has Identity \"PCRPoT-20-" + indice.toString() + "\", has TypeFrequency \"Daily\";";
					graqlInsertQuery+= "$PeriodRelation-" + indice.toString() +" (periodoftime : $Period-" + indice.toString() + " , startwhendate : " + "$TimeDate-20-" + indice.toString() + " , endwhendate : " + "$TimeDate-" + indice.toString() +  ") isa " + PCRTest.PCRTestClass_PCRPeriodicRelation + ";";

					graqlInsertQuery += insertValue (PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS, indice.toString(), "StringValue", PCRTest.TagInitPostCalculate, "$Period-" + indice.toString());

					graqlInsertQuery+= "$eventRelation-" + indice.toString() +" (registeredevent : $Event-" + indice.toString()+ ", actor: $IdentifiedPatientCohort-" + indice.toString()+", localization:$Departement-" + departement + ", time :$TimeDate-" + indice.toString()+", goal :$Why-PCR-TEST" + ", object :$What-"+ indice.toString()+") isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";\n";
				}
			}
			return (graqlInsertQuery);
		}
	}
	public InputPCRTest(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
		pcrtest= new PCRTest();

	}

	public InputPCRTest(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, DF_CASE dateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport,filename,  myMaxGet,  minIndice,  maxIndice,  dateFilter, myMinDate,  myMaxDate, myMindays,  myMaxdays);
		pcrtest= new PCRTest();

	}

	public InputPCRTest(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport, filename,  myMaxGet,  minIndice,  maxIndice, myMinDate,  myMaxDate,  myMindays,  myMaxdays);
		pcrtest= new PCRTest();

	}

	public Collection<Input> initialize (Collection<Input>inputs) {
		// TODO Auto-generated method stub
		return initialiseInputsPcrTests(inputs);
	}

	// événement confinements
	private Collection<Input> initialiseInputsPcrTests (Collection<Input>inputs) {

		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_EventPCRTest, "event");
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_EventPCRTestRelations, "eventrelations"+SuffixEventRelations);
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_ValueEventPCRTestRelations, "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_TypeValueEventPCRTestRelations, "ValueTypeRelation"+SuffixValueTypeRelations);
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_PCRTimeDate, "TimeDate");
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_PCRPeriodOfTime, "PeriodOfTime");
		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_PCRPeriodicRelation, "PeriodicRelation"+SuffixPeriodicRelations);

		List<DefineAttribute> defineattributesWhatPCRTest=new ArrayList<>();
		DefineAttribute attribut_cl_90 = 	new DefineAttribute(Cohort.CohortTypeAttributeValue_cl_age90,Cohort.CohortTypeValue_cl_age90);
		DefineAttribute attribut_P = 	new DefineAttribute(PCRTest.PCRTestTypeAttributeValue_P,PCRTest.PCRTestTypeValue_P);
		DefineAttribute attribut_T = 	new DefineAttribute(PCRTest.PCRTestTypeAttributeValue_T,PCRTest.PCRTestTypeValue_T);
		DefineAttribute attribut_PdivT = 	new DefineAttribute(PCRTest.PCRTestTypeAttributeValue_PdivT,PCRTest.PCRTestTypeValue_PdivT);
		DefineAttribute attribut_pop = 	new DefineAttribute(Cohort.CohortTypeAttributeValue_pop,Cohort.CohortTypeValue_pop);

		defineattributesWhatPCRTest.add(attribut_P);
		defineattributesWhatPCRTest.add(attribut_T);
		defineattributesWhatPCRTest.add(attribut_PdivT);
		defineattributesWhatPCRTest.add(attribut_cl_90);
		defineattributesWhatPCRTest.add(attribut_pop);

		inputs=initialiseDefine(inputs, PCRTest.PCRTestClass_WhatPCRTest, "What",defineattributesWhatPCRTest);

		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WPCR", "Description", "PCR-TEST");

		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTestTypeValue_P);

		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTestTypeValue_T);

		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTestTypeValue_PdivT);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTest.PCRTestTypeValue_AVPdivT);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTest.PCRTestTypeValue_VPdivT);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTest.PCRTestTypeValue_ACPdivT);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTest.PCRTestTypeValue_PPOSTCALCUL);

		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTestTypeValue_cl_age90);

		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTestTypeValue_pop);

		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS);


		// data
		inputs.add(new MyInputPCRTest(setOfData.getFilename(), this));

		return inputs;
	}

	protected void initializeCaches(GraknClient.Session session)
	{
		super.initializeCaches(session);
		pcrtest.initializeCaches(session);
	}

	public void postCalculate(GraknClient.Session session)
	{

		super.postCalculate(session);

		if (myInputPCRTest == null)
		{
			myInputPCRTest=new MyInputPCRTest("", this);
			JsonArray jsonArray=PCRFilesStudyMigration.loadHistorics();
			myInputPCRTest.loadHistoric(jsonArray);
		}

		//ReinitPostCalculationTag(session, PCRTestTypeValue_PPOSTCALCUL, TagAveragePostCalculate, TagInitPostCalculate);
		//PostAveragePDivTReinitCalculation(session);

		//PostAveragePDivTCalculation(session);
		//PostCleanDoubleDataByClass(session);
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULPCRTEST)
		{
			PostAveragePDivTCalculationByClass(session, optionImport);
		}
		/*PostSpeedCalculation(session);


		PostAccelerationCalculation(session);
		 */
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULLINKSPCRTEST)
		{
			ReinitPostCalculationTag(session, PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS, PCRTest.TagLinksPostCalculate, PCRTest.TagInitPostCalculate);
			PCRTestPrecedentEventsCalculation(session);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULNEXTDATEPCRTEST)
		{
			LinkNextEvent(session);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULSAMEDATEPCRTEST)
		{
			LinkSameDateEvents(session);
		}

	}

	// Post calculation of derivated (acceleration)
	private void PostAccelerationCalculation(GraknClient.Session session)  {


		try
		{
			Boolean okanswers=true;
			int nb=0;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			while (okanswers==true)
			{
				okanswers=false;

				/*String graqlQuery1 = "match $valuepostcalcul isa Value"
						+	", has IdValue $idvaluepostcalcul"
						+	", has TypeValueId \"" +  PCRTestTypeValue_PPOSTCALCUL +"\""
						+ 	", has StringValueAttribute \"" + TagSpeedPostCalculate+ "\";";
				graqlQuery1+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
				graqlQuery1+=		"(registeredevent : $event, time : $timedate, localization : $S, object : $resource) isa EventPCRTestRelations;";
				graqlQuery1+=		"$resource isa Resource, has Identity $identity, has Attribut-"+ PCRTestTypeValue_cl_age90+"$cl_age90;";	
				graqlQuery1+=		"$region isa Region, has CodeGLN $codeGLN;";	
				graqlQuery1+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
				graqlQuery1+=		maxget + ";\n";*/


				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+	", has TypeValueId \"" +  PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\""
						+ 	", has StringValueAttribute \"" + PCRTest.TagSpeedPostCalculate+ "\";";
				graqlQuery11+= 	"get;limit "+ maxGet + ";\n";
				System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();


				for(ConceptMap answer11:answers11)
				{				
					okanswers=true;

					System.out.println(answer11.toString());

					Entity value1= answer11.get("valuepostcalcul").asEntity();
					String idpostcalculvalue1=value1.id().toString();

					String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1+";";
					graqlQuery12+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
					graqlQuery12+=		"$resource isa Resource, has Identity $identity, has Attribut-"+ Cohort.CohortTypeValue_cl_age90+"$cl_age90;";	
					graqlQuery12+=		"$Departement isa Departement, has CodeGLN $codeGLN;";	
					graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
					graqlQuery12+=		maxGet + ";\n";

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{	
						LocalDateTime valuedate1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
						System.out.println(valuedate1);

						String identity1 = (String) answer12.get("identity").asAttribute().value();
						System.out.println(identity1);

						String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
						System.out.println(codeGLN);

						/*String idpostcalculvalue1 = (String) answer12.get("idvaluepostcalcul").asAttribute().value();
						System.out.println(idpostcalculvalue1);*/

						Long cl_age90 = (Long) answer12.get("cl_age90").asAttribute().value();
						System.out.println(cl_age90);

						int posnum=identity1.lastIndexOf('-');
						String valnum1=identity1.substring(posnum+1);


						/*String graqlQuery2 = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
					graqlQuery2+=		"(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
					graqlQuery2+=		"(value : $valuecl_age90, typevalue : $typecl_age90) isa ValueTypeRelation;";
					graqlQuery2+=		"$typecl_age90 isa TypeValue, has IdValue  \"cl_age90\";";
					graqlQuery2+=		"$valuecl_age90 isa LongValue, has LongValueAttribute $attributecl_age90;";
					graqlQuery2+=		"(value : $valueVPdivT, resource : $resource) isa ValueRelation;";
					graqlQuery2+=		"(value : $valueVPdivT, typevalue : $typeVPdivT) isa ValueTypeRelation;";
					graqlQuery2+=		"$typeVPdivT isa TypeValue, has IdValue  \"VPdivT\";";
					graqlQuery2+=		"$valueVPdivT isa DoubleValue, has DoubleValueAttribute $attributeVPdivT; get;\n";
						 */

						String graqlQuery2 = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
						graqlQuery2+=		"(value : $value, resource : $resource) isa ValueRelation; get;\n";
						//graqlQuery2+=		"$value isa Value, has TypeValueId $typevalueid; get;\n";

						QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery2));
						List<ConceptMap> answers2= map2.get();

						//System.out.println(answers2.toString());

						Double valueVPdivT1=0.0;
						Long valueattributecl_age90=(long) 0;

						for(ConceptMap answer2:answers2)
						{					
							Entity value= answer2.get("value").asEntity();

							QueriedAttribute queriedattribute=QueryAttributeValue(transaction, value);

							if (queriedattribute.getTypeValue().compareTo(PCRTest.PCRTestTypeValue_VPdivT)==0)
							{
								valueVPdivT1 = (Double) queriedattribute.getValue();
							}

							if (queriedattribute.getTypeValue().compareTo(Cohort.CohortTypeValue_cl_age90)==0)
							{
								valueattributecl_age90  = (Long) queriedattribute.getValue();
							}
						}
						Boolean nextdate=false;
						int plusdays=incdays; 
						while (nextdate==false && plusdays < maxnextdays)
						{
							LocalDateTime valuedate2=valuedate1.plusDays(plusdays);

							String graqlQuery31 = "match $Departement isa Departement, has CodeGLN "+ codeGLN + ";";
							graqlQuery31+=		"$timedate isa TimeDate, has EventDate " + valuedate2 + ";";
							graqlQuery31+= 		"(registeredevent : $event, time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
							graqlQuery31+=		"$resource isa "+ PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 + " " + cl_age90 + ";get;\n";
							//graqlQuery3+=		"(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
							//graqlQuery3+=		"$valuecl_age90 isa LongValue, has LongValueAttribute "+ valueattributecl_age90 + ", has TypeValueId \""+   PCRTestTypeValue_cl_age90 + "\";get;\n";
							//graqlQuery3+=		"$resource isa Resource, has Identity $identity; get;\n";	

							QueryFuture<List<ConceptMap>> map31 = transaction.execute((GraqlGet)parse(graqlQuery31));
							List<ConceptMap> answers31= map31.get();

							if (answers31.isEmpty()==false)
							{
								for(ConceptMap answer31:answers31)
								{				
									//System.out.println(answer31.toString());
									Entity resource31= answer31.get("resource").asEntity();

									/*String graqlQuery32 = "match $resource id " + resource31.id() + ";";
								graqlQuery32+=		"$valuecl_age90 isa LongValue, has LongValueAttribute "+ valueattributecl_age90 + ", has TypeValueId \""+   PCRTestTypeValue_cl_age90 + "\";";
								graqlQuery32+=		"(value : $valuecl_age90, resource : $resource) isa ValueRelation;get;\n";
								//graqlQuery3+=		"$resource isa Resource, has Identity $identity; get;\n";	
								QueryFuture<List<ConceptMap>> map32 = transaction.execute((GraqlGet)parse(graqlQuery32));
								List<ConceptMap> answers32= map32.get();

								if (answers32.isEmpty()==false)
								{
									for(ConceptMap answer32:answers32)
									{				

										Entity resource32= answer32.get("resource").asEntity();
									 */
									String graqlQuery5 = "match $resource id "+ resource31.id()+ ";";				
									graqlQuery5+=		"(value : $valueVPdivT, resource : $resource) isa ValueRelation;";
									graqlQuery5+=		"$valueVPdivT isa DoubleValue, has DoubleValueAttribute $attributeVPdivT, has TypeValueId \""+   PCRTest.PCRTestTypeValue_VPdivT + "\"; get;\n";

									QueryFuture<List<ConceptMap>> map5 = transaction.execute((GraqlGet)parse(graqlQuery5));
									List<ConceptMap> answers5= map5.get();

									//System.out.println(answers5.toString());

									for(ConceptMap answer5:answers5)
									{	
										Double valueVPdivT2 = (Double) answer5.get("attributeVPdivT").asAttribute().value();
										//System.out.println(valuePdivT2);


										Double accelerationPdivT=(valueVPdivT2-valueVPdivT1)/plusdays;

										// delete old post calcul
										QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

										// insertion des valeurs
										String graqlInsertQuery = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
										graqlInsertQuery+=		"$TypeValue-ACPdivT isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_ACPdivT + "\";";
										graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\";";

										graqlInsertQuery+= "insert ";
										//										Locale locale  = new Locale("en", "UK");
										//										DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
										//										String saccelerationPdivT=df.format(accelerationPdivT);
										String saccelerationPdivT=GraphQlDoubleFormat(accelerationPdivT);
										graqlInsertQuery += insertValue (PCRTest.PCRTestTypeValue_ACPdivT, valnum1, "DoubleValue", saccelerationPdivT, "$resource");

										// update de postcalcul
										graqlInsertQuery += PCRPostCalculationQuerySet(valnum1, PCRTest.TagAccelerationPostCalculate);

										QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
										List<ConceptMap> answers7= map7.get();

										System.out.println(accelerationPdivT);
										System.out.println(answers7.toString());


										nextdate=true;
										nb++;
										System.out.println("PostCalculated : " + nb);
									}
									//}
									//}
								}
								plusdays=plusdays+incdays;
							}
							else
							{
								plusdays=plusdays+incdays;
							}
						}
						// post calculation not possible
						if (plusdays>=maxnextdays)
						{
							// delete old post calcul
							QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

							// insert of postcalculate
							String graqlInsertQuery = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
							graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \"PPOSTCALCUL\";";

							graqlInsertQuery+= "insert ";

							// update de postcalcul
							graqlInsertQuery+= PCRPostCalculationQuerySet(valnum1, PCRTest.TagNotPostCalculate);

							QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
							List<ConceptMap> answers7= map7.get();

							System.out.println(answers7.toString());

							nb++;
							System.out.println("PostCalculated : " + nb);
						}
					}
				}
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			}
			transaction.commit();
			System.out.println("PostCalculation Acceleration OK.\n");

		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Post calculation of derivated (speed)
	private void PostSpeedCalculation(GraknClient.Session session)  {


		try
		{
			Boolean okanswers = true;
			int nb=0;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			while (okanswers==true)
			{
				okanswers=false;

				/*String graqlQuery1 = "match $valuepostcalcul isa Value"
						+	", has IdValue $idvaluepostcalcul"
						+	", has TypeValueId \"" +  PCRTestTypeValue_PPOSTCALCUL +"\""
						+ 	", has StringValueAttribute \"" + TagAveragePostCalculate+ "\";";
				graqlQuery1+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
				graqlQuery1+=		"(registeredevent : $event, time : $timedate, localization : $region, object : $resource) isa EventPCRTestRelations;";
				graqlQuery1+=		"$resource isa Resource, has Identity $identity, has Attribut-"+ PCRTestTypeValue_cl_age90+"$cl_age90;";	
				graqlQuery1+=		"$region isa Region, has CodeGLN $codeGLN;";	
				graqlQuery1+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
				graqlQuery1+=		maxget + ";\n";*/

				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+	", has TypeValueId \"" +  PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\""
						+ 	", has StringValueAttribute \"" + PCRTest.TagAveragePostCalculate+ "\";";
				graqlQuery11+= 	"get;limit "+ maxGet + ";\n";
				System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				for(ConceptMap answer11:answers11)
				{			
					okanswers = true;

					System.out.println(answer11.toString());

					Entity value1= answer11.get("valuepostcalcul").asEntity();
					String idpostcalculvalue1=value1.id().toString();

					String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1 + ";";
					graqlQuery12+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
					graqlQuery12+=		"$resource isa Resource, has Identity $identity, has Attribut-"+ Cohort.CohortTypeValue_cl_age90+"$cl_age90;";	
					graqlQuery12+=		"$Departement isa Departement, has CodeGLN $codeGLN;";	
					graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
					graqlQuery12+=		maxGet + ";\n";

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{	
						LocalDateTime valuedate1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
						System.out.println(valuedate1);

						String identity1 = (String) answer12.get("identity").asAttribute().value();
						System.out.println(identity1);

						String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
						System.out.println(codeGLN);

						/*String idpostcalculvalue1 = (String) answer1.get("idvaluepostcalcul").asAttribute().value();
					System.out.println(idpostcalculvalue1);*/

						Long cl_age90 = (Long) answer12.get("cl_age90").asAttribute().value();
						System.out.println(cl_age90);

						int posnum=identity1.lastIndexOf('-');
						String valnum1=identity1.substring(posnum+1);

						String graqlQuery2 = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
						graqlQuery2+=		"(value : $value, resource : $resource) isa ValueRelation; get;\n";

						//System.out.println(graqlQuery2);

						QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery2));
						List<ConceptMap> answers2= map2.get();

						//System.out.println(answers2.toString());

						Double valueAVPdivT1=0.0;
						Long valueattributecl_age90=(long) 0;

						for(ConceptMap answer2:answers2)
						{					
							Entity value= answer2.get("value").asEntity();

							QueriedAttribute queriedattribute=QueryAttributeValue(transaction, value);

							if (queriedattribute.getTypeValue().compareTo(PCRTest.PCRTestTypeValue_AVPdivT)==0)
							{
								valueAVPdivT1 = (Double) queriedattribute.getValue();
							}

							if (queriedattribute.getTypeValue().compareTo(Cohort.CohortTypeValue_cl_age90)==0)
							{
								valueattributecl_age90  = (Long) queriedattribute.getValue();
							}
						}
						Boolean nextdate=false;
						int plusdays=incdays;
						while (nextdate==false && plusdays < maxnextdays)
						{
							LocalDateTime valuedate2=valuedate1.plusDays(plusdays);

							String graqlQuery31 = "match $Departement isa Departement, has CodeGLN "+ codeGLN + ";";
							graqlQuery31+=		"$timedate isa TimeDate, has EventDate " + valuedate2 + ";";
							graqlQuery31+= 		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
							graqlQuery31+=		"$resource isa "+ PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 + " " + cl_age90 + ";get;\n";
							//graqlQuery3+=		"(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
							//graqlQuery3+=		"$valuecl_age90 isa LongValue, has LongValueAttribute "+ valueattributecl_age90 + ", has TypeValueId \""+   PCRTestTypeValue_cl_age90 + "\";get;\n";
							//graqlQuery3+=		"$resource isa Resource, has Identity $identity; get;\n";	

							QueryFuture<List<ConceptMap>> map31 = transaction.execute((GraqlGet)parse(graqlQuery31));
							List<ConceptMap> answers31= map31.get();

							if (answers31.isEmpty()==false)
							{
								for(ConceptMap answer31:answers31)
								{	
									//System.out.println(answer31.toString());
									Entity resource31= answer31.get("resource").asEntity();

									String graqlQuery5 = "match $resource id "+ resource31.id()+ ";";				
									graqlQuery5+=		"(value : $valueAVPdivT, resource : $resource) isa ValueRelation;";
									graqlQuery5+=		"$valueAVPdivT isa DoubleValue, has DoubleValueAttribute $attributeAVPdivT, has TypeValueId \""+   PCRTest.PCRTestTypeValue_AVPdivT + "\"; get;\n";

									QueryFuture<List<ConceptMap>> map5 = transaction.execute((GraqlGet)parse(graqlQuery5));
									List<ConceptMap> answers5= map5.get();

									//System.out.println(answers5.toString());

									for(ConceptMap answer5:answers5)
									{	
										Double valueAVPdivT2 = (Double) answer5.get("attributeAVPdivT").asAttribute().value();
										//System.out.println(valuePdivT2);


										Double speedPdivT=(valueAVPdivT2-valueAVPdivT1)/plusdays;

										// delete old post calcul
										QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);


										// insertion des valeurs
										String graqlInsertQuery = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
										graqlInsertQuery+=		"$TypeValue-VPdivT isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_VPdivT + "\";";
										graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\";";

										graqlInsertQuery+= "insert ";
										//										Locale locale  = new Locale("en", "UK");
										//										DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
										//										String sspeedPdivT=df.format(speedPdivT);
										String sspeedPdivT=GraphQlDoubleFormat(speedPdivT);
										graqlInsertQuery += insertValue (PCRTest.PCRTestMetaTypeValue, PCRTest.PCRTestTypeValue_VPdivT, valnum1, "DoubleValue", sspeedPdivT, "$resource", PCRTest.PCRTestClass_ValueEventPCRTestRelations, PCRTest.PCRTestClass_TypeValueEventPCRTestRelations);

										// update de postcalcul
										graqlInsertQuery += PCRPostCalculationQuerySet(valnum1, PCRTest.TagSpeedPostCalculate);

										QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
										List<ConceptMap> answers7= map7.get();

										System.out.println(answers7.toString());
										System.out.println(speedPdivT);

										nextdate=true;
										nb++;
										System.out.println("PostCalculated : " + nb);
									}
								}
								plusdays=plusdays+incdays;
							} //else
							else
							{
								plusdays=plusdays+incdays;
							}
						} // while
						if (plusdays>=maxnextdays)
						{
							// delete old post calcul
							QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

							// insert of postcalculate
							String graqlInsertQuery = "match $resource isa Resource, has Identity \""+ identity1+ "\";";				
							graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \"PPOSTCALCUL\";";

							graqlInsertQuery+= "insert ";

							// update de postcalcul
							graqlInsertQuery+= PCRPostCalculationQuerySet(valnum1, PCRTest.TagNotPostCalculate);

							QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
							List<ConceptMap> answers7= map7.get();

							System.out.println(answers7.toString());

							nb++;
							System.out.println("PostCalculated : " + nb);

						} // while nextdate
					} // for answer1

				}
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			} // while ok

			transaction.commit();
			System.out.println("PostCalculation Speed OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Post calculation of precedents dated events
	private void PCRTestPrecedentEventDateLinkInsertion(GraknClient.Transaction transaction, 
			Entity timedateevent,
			Entity datestart,
			Entity dateend,
			String idpcrperiofoftime)  
	{

		String graqlInsertQuery = "match ";

		graqlInsertQuery+= "$dateprec id " + timedateevent.id().toString() + ";";
		graqlInsertQuery+= "$datestart id " + datestart.id().toString() + ";";
		graqlInsertQuery+= "$dateend id " + dateend.id().toString() + ";";

		graqlInsertQuery+= "insert";
		graqlInsertQuery+= "$PrecedentDateEventLink-" + idpcrperiofoftime;


		graqlInsertQuery+= 	" (relativedate : $dateprec, belowdate : $datestart, abovedate : $dateend) isa DatesBetweenRelation ;\n";

		QueryFuture<List<ConceptMap>> map3 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
		List<ConceptMap> answers3= map3.get();

		System.out.println(answers3.toString());



	}


	// Post calculation of precedents dated events
	private void PCRTestPrecedentEventsCalculation(GraknClient.Session session)  {


		try
		{
			Boolean okanswers = true;
			//Set<String> keysdept=getDepartementId().keySet();

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;

			String[] depts=null;

			boolean dpt=false;
			String idDepartement = "";

			if (optionImport.getDept()!=null)
			{
				if (optionImport.getDept().length>0)
				{
					depts=optionImport.getDept();
					dpt=true;
				}
				idDepartement = getDepartementId().get(depts[0]);

			}


			while (okanswers==true && nb<maxIndice)
			{
				okanswers=false;

				String graqlQuery1 = "match $valuepostcalcul isa StringValue"
						+	", has TypeValueId \"" +  PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS +"\""
						+ 	", has IdValue $identity;";

				graqlQuery1 += "(resource : $pcrperiodoftime, value : $valuepostcalcul) isa " + PCRTest.PCRTestClass_ValueEventPCRTestRelations+ ";";

				if (dpt==true)
				{
					graqlQuery1+=		"(periodoftime : $pcrperiodoftime, startwhendate : $timedate-n, endwhendate : $timedate) isa PeriodicRelation;";
					graqlQuery1+=		"(time : $timedate, localization : $departement) isa EventPCRTestRelations;";
					graqlQuery1+=		"$departement id "+ idDepartement + " ;";

				}
				graqlQuery1+= 	"get;limit "+ maxGet + ";\n";

				System.out.println(graqlQuery1);

				QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery1));
				List<ConceptMap> answers1= map1.get();

				for(ConceptMap answer1:answers1)
				{			
					okanswers = true;

					//					System.out.println(answer1.toString());

					Entity pcrperioftime= answer1.get("pcrperiodoftime").asEntity();
					String idpcrperiofoftime=pcrperioftime.id().toString();

					String identityPost = (String) answer1.get("identity").asAttribute().value();
					System.out.println(identityPost);

					int posnum=identityPost.lastIndexOf('-');
					String valnumPoT=identityPost.substring(posnum+1);

					Entity postcalculvalue1= answer1.get("valuepostcalcul").asEntity();
					String idpostcalculvalue1=postcalculvalue1.id().toString();


					String graqlQuery12 = "match $pcrperiodoftime id " + idpcrperiofoftime + ";";
					graqlQuery12+=		"(periodoftime : $pcrperiodoftime, startwhendate : $timedate-n, endwhendate : $timedate) isa PeriodicRelation;";
					graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate;";				
					graqlQuery12+=		"$timedate-n isa TimeDate, has EventDate $attributedate-n;";				
					graqlQuery12+=		"(time : $timedate, localization : $departement) isa EventPCRTestRelations;";
					graqlQuery12+=		"$departement isa Departement, has CodeGLN $codeGLN;";

					if (dpt==false)
					{
						graqlQuery12+=		"$departement isa Departement, has CodeGLN $codeGLN;";
					}
					else
					{
						graqlQuery12+="$departement id "+ idDepartement+" ;";
					}
					graqlQuery12+=		"get;limit " + maxGet + ";\n";

					System.out.println(graqlQuery12);

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{	
						LocalDateTime valuedatestart = (LocalDateTime) answer12.get("attributedate-n").asAttribute().value();
						System.out.println(valuedatestart);

						Entity datestart= answer12.get("timedate-n").asEntity();

						LocalDateTime valuedateend = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
						System.out.println(valuedateend);
						Entity dateend= answer12.get("timedate").asEntity();

						String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
						System.out.println("codeGLN : "  + codeGLN);

						// first list with periodic relation
						////////////////////////////////////
						String graqlQuery21 = "match $timedateevent isa! TimeDate, has EventDate $dateprec;";	
						graqlQuery21+=		"$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
						graqlQuery21+=		"$periodicrelation (periodoftime : $period, startwhendate : $timedateevent) isa PeriodicRelation;";
						graqlQuery21+=		"(time : $period, localization : $Localization) isa eventrelations;";
						graqlQuery21+=		"{$Localization isa Departement, has CodeGLN \"" + codeGLN +"\";}";
						graqlQuery21+=		" or {$Localization isa Country, has CodeGLN \"France\";};";
						graqlQuery21+=		"get;\n";

						//						System.out.println(graqlQuery21);

						QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
						List<ConceptMap> answers21= map21.get();



						//						System.out.println(answers21.toString());


						for(ConceptMap answer21:answers21)
						{					
							Entity timedateevent= answer21.get("timedateevent").asEntity();

							EntityType type =timedateevent.type();
							String stype=type.label().getValue();

							if (stype.equals("PCRTimeDate")==false)
							{
								// a poursuivre création des liens entre les dates
								// ajout du lien de postcalculation						
								PCRTestPrecedentEventDateLinkInsertion(transaction, 
										timedateevent,
										datestart,
										dateend,
										idpcrperiofoftime);	
							}
						}

						//2nd list
						String graqlQuery22 = "match $timedateevent isa! TimeDate, has EventDate $dateprec;";	
						graqlQuery22+=		"$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
						graqlQuery22+=		"$periodicrelation (periodoftime : $period, endwhendate : $timedateevent) isa PeriodicRelation;";
						graqlQuery22+=		"(time : $period, localization : $Localization) isa eventrelations;";
						graqlQuery22+=		"{$Localization isa Departement, has CodeGLN \"" + codeGLN +"\";}";
						graqlQuery22+=		" or {$Localization isa Country, has CodeGLN \"France\";};";
						graqlQuery22+=		"get;\n";

						//						System.out.println(graqlQuery22);

						QueryFuture<List<ConceptMap>> map22 = transaction.execute((GraqlGet)parse(graqlQuery22));
						List<ConceptMap> answers22= map22.get();

						//					System.out.println(answers22.toString());

						for(ConceptMap answer22:answers22)
						{	
							Entity timedateevent= answer22.get("timedateevent").asEntity();

							EntityType type =timedateevent.type();
							String stype=type.label().getValue();

							if (stype.equals("PCRTimeDate")==false)
							{
								// a poursuivre création des liens entre les dates
								// ajout du lien de postcalculation						
								PCRTestPrecedentEventDateLinkInsertion(transaction, 
										timedateevent,
										datestart,
										dateend,
										idpcrperiofoftime);
							}

						}

						//3rd list
						String graqlQuery23 = "match $timedateevent isa! TimeDate, has EventDate $dateprec;";	
						graqlQuery23+=		"$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
						graqlQuery23+=		"(time : $timedateevent, localization : $Localization) isa eventrelations;";
						graqlQuery23+=		"{$Localization isa Departement, has CodeGLN \"" + codeGLN +"\";}";
						graqlQuery23+=		" or {$Localization isa Country, has CodeGLN \"France\";};";
						graqlQuery23+=		"get;\n";

						//						System.out.println(graqlQuery23);

						QueryFuture<List<ConceptMap>> map23 = transaction.execute((GraqlGet)parse(graqlQuery23));
						List<ConceptMap> answers23= map23.get();

						//					System.out.println(answers23.toString());

						for(ConceptMap answer23:answers23)
						{	
							Entity timedateevent= answer23.get("timedateevent").asEntity();

							EntityType type =timedateevent.type();
							String stype=type.label().getValue();

							if (stype.equals("PCRTimeDate")==false)
							{
								// a poursuivre création des liens entre les dates
								// ajout du lien de postcalculation						
								PCRTestPrecedentEventDateLinkInsertion(transaction, 
										timedateevent,
										datestart,
										dateend,
										idpcrperiofoftime);
							}
						}
						nb++;
						System.out.println("PostCalculated : " + nb);

						// delete old post calcul
						QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

						// insertion des valeurs
						String graqlInsertQuery = "match $resource id " + idpcrperiofoftime + ";";				
						graqlInsertQuery+=		"$TypeValue-PPOSTCALCULLINKS isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS +"\";";

						graqlInsertQuery+= "insert ";

						// update de postcalcul
						graqlInsertQuery += PostCalculationQuerySet(PCRTest.PCRTestTypeValue_PPOSTCALCULLINKS, valnumPoT, PCRTest.TagLinksPostCalculate);

						QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
						List<ConceptMap> answers7= map7.get();

					}

				} // for answer1
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			} // while ok

			transaction.commit();
			System.out.println("PostCalculation OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}




	// Post calculation of derivated (speed)
	private void PostAveragePDivTReinitCalculation(GraknClient.Session session)  {

		try
		{
			Boolean okanswers = true;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;

			while (okanswers==true && nb<maxIndice)
			{
				okanswers=false;

				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+	", has TypeValueId $TypeValueId; $TypeValueId ==\"" + PCRTest.PCRTestTypeValue_PPOSTCALCUL+ "-" + PCRTest.TagAveragePostCalculate + "\";";
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
					graqlQuery12+=		"(object : $resource) isa EventPCRTestRelations;";
					//graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
					graqlQuery12+=		"$resource isa Resource, has Identity $identity";	
					//graqlQuery12+=		queryAttribut (PCRTestTypeValue_cl_age90, "cl_age90");	
					//graqlQuery12+=		queryAttribut (PCRTestTypeValue_PdivT, "attributePdivT");	
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
						deleteValueByType(transaction, idResource, PCRTest.PCRTestTypeValue_AVPdivT);

						// delete old post calcul
						QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

						// insertion des valeurs
						String graqlInsertQuery = "match $resource id "+ idResource+ ";";				
						graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\";";

						graqlInsertQuery+= "insert ";

						// update de postcalcul
						graqlInsertQuery += PCRPostCalculationQuerySet(valnum1, PCRTest.TagInitPostCalculate);

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
			System.out.println("PostCalculation Average PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}


	// Post calculation of derivated (speed)
	private void PostAveragePDivTCalculation(GraknClient.Session session)  {




		try
		{
			Boolean okanswers = true;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			int nb=0;

			while (okanswers==true && nb<maxIndice)
			{
				okanswers=false;

				/*String graqlQuery1 = "match $valuepostcalcul isa Value"
							+	", has IdValue $idvaluepostcalcul"
							+	", has TypeValueId \"" +  PCRTestTypeValue_PPOSTCALCUL +"\""
							+ 	", has StringValueAttribute \"" + TagInitPostCalculate+ "\";";
					graqlQuery1+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery1+=		"(registeredevent : $event, time : $timedate, localization : $region, object : $resource) isa EventPCRTestRelations;";
					graqlQuery1+=		"$resource isa Resource, has Identity $identity, has Attribut-"+ PCRTestTypeValue_cl_age90+"$cl_age90;";	
					graqlQuery1+=		"$region isa Region, has CodeGLN $codeGLN;";	
					graqlQuery1+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
					graqlQuery1+=		maxget + ";\n";*/
				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+	", has TypeValueId $TypeValueId; {$TypeValueId ==\"" +  PCRTest.PCRTestTypeValue_PPOSTCALCUL + "\";} or { $TypeValueId ==\"" + PCRTest.PCRTestTypeValue_PPOSTCALCUL+ "-" + PCRTest.TagInitPostCalculate + "\";};";
				//						+ 	", has StringValueAttribute \"" + TagInitPostCalculate+ "\";";
				graqlQuery11+= 	"get;limit "+ maxGet + ";\n";
				System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				for(ConceptMap answer11:answers11)
				{			
					okanswers = true;

					//						System.out.println(answer11.toString());

					Entity value1= answer11.get("valuepostcalcul").asEntity();
					String idpostcalculvalue1=value1.id().toString();

					String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1 + ";";
					graqlQuery12+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
					graqlQuery12+=		"$resource isa Resource, has Identity $identity";	
					graqlQuery12+=		queryAttribut (Cohort.CohortTypeValue_cl_age90, "cl_age90");	
					graqlQuery12+=		queryAttribut (PCRTest.PCRTestTypeValue_PdivT, "attributePdivT");	
					graqlQuery12+=		";";	
					graqlQuery12+=		"$Departement isa Departement;";	
					graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate; get;limit ";				
					graqlQuery12+=		maxGet + ";\n";

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{	
						LocalDateTime valuedate1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
						System.out.println(valuedate1);

						String identity1 = (String) answer12.get("identity").asAttribute().value();
						System.out.println(identity1);

						Entity departement1= answer12.get("Departement").asEntity();
						String idDepartement=departement1.id().toString();
						System.out.println("idDepartement :" + idDepartement);

						Entity resource1= answer12.get("resource").asEntity();
						String idResource=resource1.id().toString();
						System.out.println("idResource :" + idResource);

						String id_cl_age90 = answer12.get("cl_age90").asAttribute().id().toString();
						System.out.println("cl_age90 :" + id_cl_age90);

						int posnum=identity1.lastIndexOf('-');
						String valnum1=identity1.substring(posnum+1);

						Double valuePdivT1 = (Double) answer12.get("attributePdivT").asAttribute().value();
						System.out.println("attributePdivT :" + valuePdivT1);

						//System.out.println(answers2.toString());
						Double 	sumvaluePdivT=valuePdivT1;
						int 	nbPdivT=1;

						int plusdays=incdays;

						LocalDateTime valuedate2=valuedate1.plusDays(plusdays);
						LocalDateTime valuedate3=valuedate2.plusDays(plusdays);

						String idDate2=getDateId().get(valuedate2);
						String idDate3=getDateId().get(valuedate3);

						if (idDate2!=null && idDate3!=null)
						{
							String graqlQuery31 = "match $Departement id "+ idDepartement + ";";
							graqlQuery31+= 		"{$date id " + idDate2 + ";} or {$date id " + idDate3 + ";};";
							graqlQuery31+=		"$timedate isa TimeDate, has EventDate $date;";
							//graqlQuery31+= 		"{$date==" + valuedate2 + ";} or {$date==" + valuedate3 + ";};";
							graqlQuery31+= 		"(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;";
							graqlQuery31+=		"$id_cl_age90 id " + id_cl_age90 + ";";
							graqlQuery31+=		"$resource isa "+ PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $id_cl_age90;";
							//graqlQuery31+=		queryAttribut (PCRTestTypeValue_PdivT, "attributePdivT");
							graqlQuery31+=		"get;\n";
							//graqlQuery3+=		"(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
							//graqlQuery3+=		"$valuecl_age90 isa LongValue, has LongValueAttribute "+ valueattributecl_age90 + ", has TypeValueId \""+   PCRTestTypeValue_cl_age90 + "\";get;\n";


							QueryFuture<List<ConceptMap>> map31 = transaction.execute((GraqlGet)parse(graqlQuery31));
							List<ConceptMap> answers31= map31.get();

							if (answers31.isEmpty()==false)
							{
								for(ConceptMap answer31:answers31)
								{				
									//System.out.println(answer31.toString());
									Entity resource31= answer31.get("resource").asEntity();

									String graqlQuery32 = "match $resource id " + resource31.id() + ";";
									graqlQuery32+=		"$resource isa "+ PCRTest.PCRTestClass_WhatPCRTest;
									graqlQuery32+=		queryAttribut (PCRTest.PCRTestTypeValue_PdivT, "attributePdivT");
									graqlQuery32+=		";get;\n";

									QueryFuture<List<ConceptMap>> map32 = transaction.execute((GraqlGet)parse(graqlQuery32));
									List<ConceptMap> answers32= map32.get();


									if (answers32.isEmpty()==false)
									{
										for(ConceptMap answer32:answers32)
										{	
											Double valuePdivT2 = (Double) answer32.get("attributePdivT").asAttribute().value();
											//System.out.println(valuePdivT2);

											sumvaluePdivT+=valuePdivT2;
											nbPdivT++;
										}
									}
								}
							}
						}
						//}
						//}

						if (nbPdivT>0)
							sumvaluePdivT=sumvaluePdivT/nbPdivT;

						// old calculation delete
						deleteValueByType(transaction, idResource, PCRTest.PCRTestTypeValue_AVPdivT);

						// delete old post calcul
						QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);

						// insertion des valeurs
						String graqlInsertQuery = "match $resource id "+ idResource+ ";";				
						graqlInsertQuery+=		"$TypeValue-AVPdivT isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_AVPdivT + "\";";
						graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\";";

						graqlInsertQuery+= "insert ";

						String ssumvaluePdivT=GraphQlDoubleFormat(sumvaluePdivT);
						graqlInsertQuery += insertValue (PCRTest.PCRTestMetaTypeValue, PCRTest.PCRTestTypeValue_AVPdivT, valnum1, "DoubleValue", ssumvaluePdivT, "$resource", PCRTest.PCRTestClass_ValueEventPCRTestRelations, PCRTest.PCRTestClass_TypeValueEventPCRTestRelations);

						// update de postcalcul
						graqlInsertQuery += PCRPostCalculationQuerySet(valnum1, PCRTest.TagAveragePostCalculate);

						QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
						List<ConceptMap> answers7= map7.get();

						//							System.out.println(answers7.toString());
						System.out.println("ssumvaluePdivT : "+ssumvaluePdivT);
						System.out.println("nbPdivT : "+ nbPdivT);
						nb++;
						System.out.println("PostCalculated : " + nb);
					}


				} // for answer1
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			} // while ok

			transaction.commit();
			System.out.println("PostCalculation Average PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}


	// Post calculation of average
	private void PostAveragePDivTCalculationByClass (GraknClient.Session session, OptionImport optionImport)  {

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
						graqlQuery11 += 	"$what isa " + PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90, has Identity $identity";
						graqlQuery11+=		queryAttribut (PCRTest.PCRTestTypeValue_PdivT, "attributePdivT");	
						graqlQuery11+=		";";	
						graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";
						graqlQuery11+=		"$timedate isa TimeDate, has EventDate $attributedate;";				
						//graqlQuery11+=		"(resource : $what, value : $value) isa ValueEventPCRTestRelations;";	
						//graqlQuery11+=		"$value isa DoubleValueDerivated, has TypeValueId \"AVPdivT\", has IdValue $idvalue;";				
						//graqlQuery11+=		"get;limit 1;\n";
						if (mindays>0)
							graqlQuery11+=		"get;sort $attributedate asc;offset "+ mindays + ";limit " + maxdays + ";\n";
						else
							graqlQuery11+=		"get;sort $attributedate asc;limit " + maxdays + ";\n";

						System.out.println(graqlQuery11);

						QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
						List<ConceptMap> answers11= map11.get();

						System.out.println("derivated AVPdivT existing values : "+ answers11.size());

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

									//String idvalue= (String)answer11.get("idvalue").asAttribute().value();
									//System.out.println("delete idvalue : "+ idvalue);

									deleteValueByType(transaction, idResource, PCRTest.PCRTestTypeValue_AVPdivT);
								}
								isup++;
							}

							/*String graqlQuery12 = "match $Departement id "+ idDepartement + ";";
							graqlQuery12 += 	"$Cl_age90 id "+ idCl_age90 + ";";
							graqlQuery12 += 	"$what isa " + PCRTestClass_WhatPCRTest + ", has Attribut-"+ PCRTestTypeValue_cl_age90 +" $Cl_age90, has Identity $identity";
							graqlQuery12+=		queryAttribut (PCRTestTypeValue_PdivT, "attributePdivT");	
							graqlQuery12+=		";";	
							graqlQuery12+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";
							graqlQuery12+=		"$timedate isa TimeDate, has EventDate $attributedate;";				
							graqlQuery12+=		"get;sort $attributedate asc;";
							//graqlQuery11+=		"get;limit 1;\n";
							if (mindays>0)
								graqlQuery12+=		"offset "+ mindays + ";limit " + maxdays + ";\n";
							else
								graqlQuery12+=		"limit " + maxdays + ";\n";
							System.out.println(graqlQuery12);

							QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
							List<ConceptMap> answers12= map12.get();
							 */
							int 	nbPdivT=0;

							int size=answers11.size();
							Double	sumvaluePdivT[]=new Double[size+avnb];
							LocalDateTime[] valuedates=new LocalDateTime[size+avnb];
							Double	valueAVPdivT[]=new Double[size+avnb];
							Double	valueVPdivT[] =new Double[size+avnb];
							Double	valueAPdivT[]=new Double[size+avnb];

							// init
							for (int ival=0;ival<size+avnb;ival++)
							{
								sumvaluePdivT[ival]=0.0;
							}

							for(ConceptMap answer11:answers11)
							{
								Double valuePdivT1 = (Double) answer11.get("attributePdivT").asAttribute().value();
								System.out.println("attributePdivT :" + valuePdivT1);

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
								graqlInsertQuery+=		"$TypeValue-AVPdivT isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_AVPdivT + "\";";
								graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ PCRTest.PCRTestTypeValue_PPOSTCALCUL +"\";";
								;
								String insertattributes="";

								graqlInsertQuery+= "insert ";

								// sum
								//////////
								for (int ival=0;ival<avnb;ival++)
								{
									int jval=nbPdivT+ival;
									sumvaluePdivT[jval]+=valuePdivT1;
								}

								// date
								valuedates[nbPdivT]=valuedate1;

								// average
								if (nbPdivT-avnb>0)
								{
									valueAVPdivT[nbPdivT]=sumvaluePdivT[nbPdivT]/avnb;
								}
								else
								{
									valueAVPdivT[nbPdivT]=sumvaluePdivT[nbPdivT]/(nbPdivT+1);
								}

								String averagevaluePdivT=GraphQlDoubleFormat(valueAVPdivT[nbPdivT]);
								insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_value, averagevaluePdivT);

								if (valueAVPdivT[nbPdivT]<0.0)
								{
									System.out.println("valueAVPdivT[nbPdivT] négatif");
								}
								// speed
								if (nbPdivT>0)
								{
									LocalDateTime date1 = valuedates[nbPdivT-1];
									LocalDateTime date2 = valuedates[nbPdivT];
									long delay=date1.until(date2, ChronoUnit.DAYS);
									if (delay!=0 && valueAVPdivT[nbPdivT]!=null && valueAVPdivT[nbPdivT-1]!=null)
									{
										valueVPdivT[nbPdivT]=(valueAVPdivT[nbPdivT]-valueAVPdivT[nbPdivT-1])
												/delay;

										// speed insertion
										String speedvaluePdivT=GraphQlDoubleFormat(valueVPdivT[nbPdivT]);
										insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_speed, speedvaluePdivT);

										// acceleration
										if (nbPdivT>1 && valueVPdivT[nbPdivT]!=null && valueVPdivT[nbPdivT-1]!=null)
										{
											valueAPdivT[nbPdivT]=(valueVPdivT[nbPdivT]-valueVPdivT[nbPdivT-1])
													/delay;

											// acceleration insertion
											String accelerationvaluePdivT=GraphQlDoubleFormat(valueAPdivT[nbPdivT]);
											insertattributes+=insertAttributWithoutPrefix (SetOfChronology.AttributeValue_acceleration, accelerationvaluePdivT);

											if (valueAPdivT[nbPdivT]<0.0)
											{
												System.out.println("valueAPdivT[nbPdivT] négatif");
											}
										}
										graqlInsertQuery += insertValueWithAttributes (PCRTest.PCRTestMetaTypeValue, PCRTest.PCRTestTypeValue_AVPdivT, valnum1, "DoubleValueDerivated", insertattributes, "$what", PCRTest.PCRTestClass_ValueEventPCRTestRelations, PCRTest.PCRTestClass_TypeValueEventPCRTestRelations);

										if ((nbPdivT>=avnb-1) && (nbPdivT<maxdays-mindays))
										{
											System.out.println("query : "+ graqlInsertQuery);

											QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
											List<ConceptMap> answers2= map2.get();
										}
									}

								}
								nbPdivT++;
								System.out.println("nbPdivT : "+ nbPdivT);
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
			System.out.println("PostCalculation Stat PCR OK.\n");
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
							graqlQuery11 += 	"$what isa " + PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90, has Identity $identity;";
							graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";
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
			System.out.println("Clean Double PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	private // Post calculation set
	String PCRPostCalculationQuerySet(String valnum1, String set)  {

		/*String graqlInsertQuery= "$Value-PPOSTCALCUL isa StringValue, has IdValue \"PCRPPOSTCALCUL-"+valnum1+ "\", has StringValueAttribute \"" + set + "\";\n";
		graqlInsertQuery+= "$ValueRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, resource : $resource) isa ValueEventPCRTestRelations" + ";\n";
		graqlInsertQuery+= "$ValueTypeRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, typevalue : $TypeValue-PPOSTCALCUL) isa TypeValueEventPCRTestRelations ;\n";
		 */

		//String graqlInsertQuery = insertValue (PCRTestTypeValue_PPOSTCALCUL, valnum1, "StringValue", set, "$resource", PCRTestClass_ValueEventPCRTestRelations, PCRTestClass_TypeValueEventPCRTestRelations);

		String graqlInsertQuery = PostCalculationQuerySet(PCRTest.PCRTestTypeValue_PPOSTCALCUL, valnum1, set);

		return graqlInsertQuery;
	}


	String getMetaTypeValue()
	{
		return PCRTest.PCRTestMetaTypeValue;
	}

	// class relations value
	String getClass_ValueEventRelations()
	{
		return PCRTest.PCRTestClass_ValueEventPCRTestRelations;
	}

	// class relations type value
	String getClass_TypeValueEventRelations()
	{
		return PCRTest.PCRTestClass_TypeValueEventPCRTestRelations;
	}


	// Post calculation of precedents dated events
	private void LinkNextEvent(GraknClient.Session session)  
	{

		try
		{
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;;

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
						graqlQuery11 += 	"$what isa " + PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90;";
						graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";
						graqlQuery11+=		"$timedate isa PCRTimeDate, has EventDate $attributedate;";				

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
					getMyInputPCRTest().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Next Date PCR OK.\n");
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
					int nextrapole=(ndept*keyscl_age90.size()*254);
					System.out.println("nextrapole:"+nextrapole);

					//if (nextrapole>minIndice)
					//{
					//long ind_clage=0;
					//long ind_prec_clage=0;

					for (Long keycl_age90 : clsage90)
					{


						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false; 
						}

						if (keycl_age90>=ind_clage)
						{
							String idCl_age90 = getCl_age90Id().get(keycl_age90);
							ind_clage=keycl_age90;
							
							String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
							graqlQuery11 += 	"$Cl_age90 id "+ idCl_age90 + ";";
							graqlQuery11 += 	"$what isa " + PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90;";
							graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";
							graqlQuery11+=		"$timedate isa PCRTimeDate, has EventDate $attributedate;";				

							if (mindays>0)
								graqlQuery11+=		"get;sort $attributedate asc;offset "+ mindays + ";limit " + maxdays + ";\n";
							else
								graqlQuery11+=		"get;sort $attributedate asc;limit " + maxdays + ";\n";

							System.out.println(graqlQuery11);

							QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
							List<ConceptMap> answers11= map11.get();

							int ndate=0;
							for(ConceptMap answer11:answers11)
							{
								ndate++;
								Entity time1= answer11.get("timedate").asEntity();
								String idTime1=time1.id().toString();
								//System.out.println("idTime :" + idTime1);

								LocalDateTime date1 = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
								//if (ndate==1 || ndate>=answers11.size()-1)
								System.out.println("date :" + date1);

								DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");


								// first list with periodic relation
								////////////////////////////////////
								String graqlQuery21 = "match ";
								graqlQuery21 += "{$timedateevent isa ShopConfinementTimeDate, has EventDate " + date1.format(dateformat) + ";}";
								graqlQuery21 += " or {$timedateevent isa DepartementConfinementTimeDate, has EventDate " + date1.format(dateformat) + ";};";
								//graqlQuery21+=		"not {$timedateevent isa " + PCRTest.PCRTestClass_PCRTimeDate + ";};";
								graqlQuery21+=		"$periodicrelation (periodoftime : $period, endwhendate : $timedateevent) isa PeriodicRelation;";
								//graqlQuery21 		+= "$time1 id "+ idTime1+ ";";				
								//graqlQuery21		+= "not {(time: $time1, time: $timedateevent) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";};";
								graqlQuery21+=		"{$Localization id " + idDepartement + ";}";
								graqlQuery21+=		" or {$Localization isa Country, has CodeGLN \"France\";};";
								graqlQuery21+=		"(time : $period, localization : $Localization) isa eventrelations;";
								graqlQuery21+=		"get;\n";

								//System.out.println(graqlQuery21);

								QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
								List<ConceptMap> answers21= map21.get();

								for(ConceptMap answer21:answers21)
								{					
									Entity timedateevent= answer21.get("timedateevent").asEntity();

									EntityType type =timedateevent.type();
									String stype=type.label().getValue();

									if (stype.equals(PCRTest.PCRTestClass_PCRTimeDate)==false)
									{
										createSameDateLink(transaction, time1, timedateevent, idDepartement);
									}
								}

								// 2nd list with periodic relation
								////////////////////////////////////
								String graqlQuery22 = "match ";
								graqlQuery22 += "{$timedateevent isa ShopConfinementTimeDate, has EventDate " + date1.format(dateformat) + ";}";
								graqlQuery22 += "or {$timedateevent isa DepartementConfinementTimeDate, has EventDate " + date1.format(dateformat) + ";};";
								//graqlQuery22+=		"not {$timedateevent isa " + PCRTest.PCRTestClass_PCRTimeDate + ";};";
								//graqlQuery22 			+= "$time1 id "+ idTime1+ ";";				
								//graqlQuery22			+= "not {(time: $time1, time: $timedateevent) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";};";
								graqlQuery22+=		"$periodicrelation (periodoftime : $period, startwhendate : $timedateevent) isa PeriodicRelation;";
								graqlQuery22+=		"{$Localization id " + idDepartement + ";}";
								graqlQuery22+=		" or {$Localization isa Country, has CodeGLN \"France\";};";
								graqlQuery22+=		"(time : $period, localization : $Localization) isa eventrelations;";
								graqlQuery22+=		"get;\n";

								//System.out.println(graqlQuery22);

								QueryFuture<List<ConceptMap>> map22 = transaction.execute((GraqlGet)parse(graqlQuery22));
								List<ConceptMap> answers22= map22.get();

								for(ConceptMap answer22:answers22)
								{					
									Entity timedateevent= answer22.get("timedateevent").asEntity();

									EntityType type =timedateevent.type();
									String stype=type.label().getValue();

									if (stype.equals(PCRTest.PCRTestClass_PCRTimeDate)==false)
									{
										createSameDateLink(transaction, time1, timedateevent, idDepartement);
									}
								}

								//3rd list
								String graqlQuery23 = "match $timedateevent isa WeatherTimeDate, has EventDate " + date1.format(dateformat) + ";";
								//graqlQuery23 			+= "$time1 id "+ idTime1+ ";";				
								//graqlQuery23			+= "not {(time: $time1, time: $timedateevent) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";};";
								graqlQuery23+=		"$Localization id " + idDepartement + ";";
								graqlQuery23+=		"(time : $timedateevent, localization : $Localization) isa " + InputWeather.WeatherClass_EventWeatherRelations + ";";
								//graqlQuery23+=		"not {$timedateevent isa " + PCRTest.PCRTestClass_PCRTimeDate + ";};";
								graqlQuery23+=		"get;\n";

								//System.out.println(graqlQuery23);

								QueryFuture<List<ConceptMap>> map23 = transaction.execute((GraqlGet)parse(graqlQuery23));
								List<ConceptMap> answers23= map23.get();

								//System.out.println(answers23.toString());

								for(ConceptMap answer23:answers23)
								{	
									Entity timedateevent= answer23.get("timedateevent").asEntity();

									EntityType type =timedateevent.type();
									String stype=type.label().getValue();

									if (stype.equals(PCRTest.PCRTestClass_PCRTimeDate)==false)
									{
										createSameDateLink(transaction, time1, timedateevent, idDepartement);
									}
								}


								//4th list
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
								String graqlQuery24 = "match $timedateevent isa VaccinTimeDate, has EventDate " + date1.format(dateformat) + ";";
								//graqlQuery24 			+= "$time1 id "+ idTime1+ ";";				
								//graqlQuery24			+= "not {(time: $time1, time: $timedateevent) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";};";
								graqlQuery24			+=	"$Localization id " + idDepartement + ";";
								graqlQuery24			+=		"(time : $timedateevent, localization : $Localization, object : $what) isa " + Vaccin.VaccinClass_EventVaccinRelations + ";";
								graqlQuery24			+= "$what isa " + Vaccin.VaccinClass_WhatVaccin+", has " + "Attribut-"+ Cohort.CohortTypeValue_cl_age90 + " $keycl_age90;";
								graqlQuery24			+= graqlQueryAge;
								//graqlQuery23+=		"not {$timedateevent isa " + PCRTest.PCRTestClass_PCRTimeDate + ";};";

								graqlQuery24+=		"get;\n";

								//System.out.println(graqlQuery23);

								QueryFuture<List<ConceptMap>> map24 = transaction.execute((GraqlGet)parse(graqlQuery24));
								List<ConceptMap> answers24= map24.get();

								//System.out.println(answers23.toString());

								for(ConceptMap answer24:answers24)
								{	
									Entity timedateevent= answer24.get("timedateevent").asEntity();

									EntityType type =timedateevent.type();
									String stype=type.label().getValue();

									if (stype.equals(Vaccin.VaccinClass_VaccinTimeDate)==true)
									{
										//boolean sameclassage=verifSameClassAge(transaction, timedateevent, keycl_age90);

										//if (sameclassage==true)
										{
											createSameDateLink(transaction, time1, timedateevent, idDepartement);
										}
									}
									else if (stype.equals(PCRTest.PCRTestClass_PCRTimeDate)==false)
									{
										createSameDateLink(transaction, time1, timedateevent, idDepartement);
									}
								}
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
						getMyInputPCRTest().saveHistoric(PCRFilesStudyMigration.historics, true);
						PCRFilesStudyMigration.saveHistorics();

						ind_prec_clage=ind_clage;
					}
					ind_clage=0;
					ind_prec_clage=0;
					linkssamedates_deptindex=ndept;
					getMyInputPCRTest().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Next Date PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// create link sequence between time event
	protected boolean verifSameClassAge(GraknClient.Transaction transaction, Entity timeVaccin, String classAge)  
	{
		boolean sameclass=true;

		// verif existence
		String graqlQuery 	= "match $timeVaccin id "+ timeVaccin.id().toString()+ ";";				
		graqlQuery			+= "(time: $timeVaccin, object: $what) isa " + Vaccin.VaccinClass_EventVaccinRelations+";";
		graqlQuery			+= "$what isa " + Vaccin.VaccinClass_WhatVaccin+", has " + "Attribut-"+ Cohort.CohortTypeValue_cl_age90 + " $clage;";
		graqlQuery			+= "get;limit 1;\n";

		//System.out.println("query : "+ graqlQuery);

		QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery));
		List<ConceptMap> answers1= map1.get();

		if (answers1.isEmpty())
		{
			if (classAge.equals("0")==false)
			{
				sameclass=false;
			}
		}
		else
		{
			for(ConceptMap answer1:answers1)
			{	
				String clage = (String) answer1.get("clage").asAttribute().value().toString();
				if (clage==null || clage.equals("0"))
				{
					if (classAge.equals("0")==true)
					{
						sameclass=true;
					}
					else
					{
						sameclass=false;
					}
				}
				else
				{
					if (classAge.equals(clage)==true)
					{
						sameclass=true;
					}
					else
					{
						sameclass=false;
					}
				}
			}
		}
		return sameclass;
	}

//	// create link sequence between time event
//	protected void createSameDateLink(GraknClient.Transaction transaction, Entity time1, Entity time2, String idLocalization)  
//	{
//		// verif existence
//		String graqlQuery 	= "match $time1 id "+ time1.id().toString()+ ";";				
//		graqlQuery 			+= "$time2 id "+ time2.id().toString()+ ";";				
//		graqlQuery			+= "(time: $time1, time: $time2) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+";get;limit 1;\n";
//
//		//System.out.println("query : "+ graqlQuery);
//
//		QueryFuture<List<ConceptMap>> map2 = transaction.execute((GraqlGet)parse(graqlQuery));
//		List<ConceptMap> answers2= map2.get();
//
//		if (answers2.isEmpty())
//		{
//			// relations creation
//			String graqlInsertQuery 	= "match $time1 id "+ time1.id().toString()+ ";";				
//			graqlInsertQuery 			+= "$time2 id "+ time2.id().toString()+ ";";		
//			if (idLocalization!=null)
//			{
//				graqlInsertQuery 			+= "$localization1 id "+ idLocalization+ ";";		
//				graqlInsertQuery			+= "insert (time: $time1, time: $time2, localization : $localization1) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+ ";\n";
//
//			}
//
//			else
//			{
//				graqlInsertQuery			+= "insert (time: $time1, time: $time2) isa " + PCRTest.PCRTestClass_PCRTestSameDateLinks+ ";\n";
//			}
//			System.out.println("query : "+ graqlInsertQuery);
//
//			QueryFuture<List<ConceptMap>> map3 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
//			List<ConceptMap> answers3= map3.get();
//		}
//	}
}
