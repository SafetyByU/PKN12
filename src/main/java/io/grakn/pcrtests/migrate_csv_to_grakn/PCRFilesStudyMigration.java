package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.gson.JsonArray;
import grakn.client.GraknClient;
import grakn.client.GraknClient.Session;
import grakn.client.GraknClient.Transaction;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlInsert;
import io.grakn.pcrtests.migrate_csv_to_grakn.InputSetOfChronology.DF_CASE;
import io.grakn.pcrtests.migrate_csv_to_grakn.OptionReport.TypeReport;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class PCRFilesStudyMigration {

	private static InputPCRTest inputpcrtest;
	private static InputShops inputshops;
	private static InputShopsConfinement inputshopsconfinement;
	private static InputDepartementsCouvreFeu inputdepartementscouvrefeu;
	private static InputWeather inputweather;
	private static InputDepartements inputdepartements;

	private static ReportPCRTest reportpcrtests;

	/*
	 * static final String SuffixEventRelations = ",\nrelates registeredevent, " +
	 * "relates actor, " + "relates object, " + "relates goal, " +
	 * "relates localization, " + "relates time";
	 * 
	 * static final String SuffixValueRelations = ",\nrelates resource, " +
	 * "relates value"; static final String SuffixValueTypeRelations =
	 * ",\nrelates typevalue, " + "relates value";
	 * 
	 * static final String SuffixPeriodicRelations = ",\nrelates periodoftime, " +
	 * "relates startwhendate," + "relates endwhendate";
	 */
	/*
	 * static final String TagInitPostCalculate = "0"; static final String
	 * TagAveragePostCalculate = "AV"; static final String TagSpeedPostCalculate =
	 * "S"; static final String TagAccelerationPostCalculate = "AC"; static final
	 * String TagNotPostCalculate = "-";
	 * 
	 * static final String TagLinksPostCalculate = "L";
	 */

	static final int minindice = 0;
	static final int maxindice = 4000000;
	static final int maxget = 50;
	static final int maxcommit = 1;

	static final int shop_minindice = minindice;
	static final int shop_maxindice = -1;

	static final int dpt_minindice = minindice;
	static final int dpt_maxindice = -1;

	static final int dc_minindice = minindice;
	static final int dc_maxindice = -1;

	static final int sc_minindice = minindice;
	static final int sc_maxindice = -1;

	static final int w_minindice = minindice;
	static final int w_maxindice = -1;

	static final int pcr_minindice = minindice;
	static final int pcr_maxindice = maxindice;

	static final LocalDate minDate = LocalDate.parse((CharSequence) "2021-02-26");
	static final LocalDate maxDate = LocalDate.parse((CharSequence) "2021-03-28");
	static final DF_CASE datefilter = DF_CASE.TRUE;

	static final DF_CASE dc_datefilter = DF_CASE.TRUE;
	static final LocalDate dc_minDate = minDate;
	static final LocalDate dc_maxDate = maxDate;

	static final int dc_mindays = 250;
	static final int dc_maxdays = 500;

	static final LocalDate sc_minDate = minDate;
	static final LocalDate sc_maxDate = maxDate;

	static final int sc_mindays = 250;
	static final int sc_maxdays = 500;

	static final DF_CASE w_datefilter = DF_CASE.FALSE;
	static final LocalDate w_minDate = minDate;
	static final LocalDate w_maxDate = maxDate;

	static final int w_mindays = 250;
	static final int w_maxdays = 500;

	static final DF_CASE pcr_datefilter = DF_CASE.BOTH;
	static final LocalDate pcr_minDate = minDate;
	static final LocalDate pcr_maxDate = maxDate;

	static final int pcr_mindays = 280;
	static final int pcr_maxdays = 500;

	static final String pcr_firstindice = "2000000";

	abstract static class Input {
		String path;
		Long indice;
		Boolean isdefine = false;

		public Input(String path) {
			this.path = path;
			this.indice = Long.decode(pcr_firstindice);
		}

		String getDataPath() {
			return path;
		}

		Long getIndice() {
			return indice;
		}

		Boolean getIsDefine() {
			return isdefine;
		}

		abstract String template(Json data);
	}

	abstract static class Report {
		String path;
		OptionReport optionReport;

		public String getPath() {
			return path;
		}

		public OptionReport getOptionReport() {
			return optionReport;
		}

		public Report(String path, OptionReport myOptionreport) {
			this.path = path;
			this.optionReport = myOptionreport;
		}

		String getDataPath() {
			return path;
		}

		protected abstract JsonArray queryDataToJson(Transaction transaction);

		protected abstract void initializeCaches(Session session);

		protected abstract void convertJsonToCSV(JsonArray items, String suffixCSV);
		
		protected abstract JsonArray parseCSVToJson(String suffixcsv) throws FileNotFoundException;

		protected abstract JsonArray fourierTransform(JsonArray items, String typeValue);

		protected abstract JsonArray extractValues(JsonArray items, String typeValue);

		protected abstract JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue, double min, double max, boolean inside);

		protected abstract JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue, boolean inside);

		protected abstract JsonArray inverseFourierTransform(JsonArray filteredfrequencies, TypeValue frequency);

		protected abstract JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue);

		protected abstract String getReportAccelerationName();

		protected abstract JsonArray substractValues(JsonArray filteredtemporals, JsonArray temporals);

	}

	abstract static class InputSingleton extends Input {

		public InputSingleton() {
			super("");
		}

		abstract String template();
	}

	abstract static class InputSingletonDefine extends InputSingleton {

		String type;
		String subtype;
		List<DefineAttribute> attributes;

		public InputSingletonDefine(String subtype, String type) {

			super();
			this.type = type;
			this.subtype = subtype;
			isdefine = true;
		}

		public InputSingletonDefine(String subtype, String type, List<DefineAttribute> attributes) {

			super();
			this.type = type;
			this.subtype = subtype;
			this.attributes = attributes;
			isdefine = true;
		}

		abstract String template();
	}

	abstract static class InputSingletonInsertEntity extends InputSingleton {

		String type;
		String key;
		String keyvalue;
		String typevalue2 = "";
		String value2 = "";

		public InputSingletonInsertEntity(String type, String key, String keyvalue) {

			super();
			this.type = type;
			this.key = key;
			this.keyvalue = keyvalue;
		}

		public InputSingletonInsertEntity(String type, String key, String keyvalue, String typevalue2, String value2) {

			super();
			this.type = type;
			this.key = key;
			this.keyvalue = keyvalue;
			this.typevalue2 = typevalue2;
			this.value2 = value2;
		}

		abstract String template();
	}

	public static void main(String[] args) throws FileNotFoundException {

		PCRFilesOptions pcrFilesOptions = new PCRFilesOptions(args);
		if (pcrFilesOptions.getOptions()==false
				|| pcrFilesOptions.getOptImp()!=null)
		{
			Collection<Input> inputs = new ArrayList<>();

			inputs = initialiseInputs(pcrFilesOptions.getOptImp(), inputs);
			connectAndMigrate(pcrFilesOptions, inputs);
		}
		else if (pcrFilesOptions.getOptRep()!=null)
		{
			Collection<Report> reports = new ArrayList<>();
			reports = initialiseReports(pcrFilesOptions.getOptRep(), reports);
			connectAndReport(reports);

		}
	}

	static void connectAndMigrate(PCRFilesOptions pcrFilesOptions, Collection<Input> inputs) throws FileNotFoundException {
		GraknClient client = new GraknClient("localhost:48555");
		final GraknClient.Session session = client.session("grakn");

		for (final Input input : inputs) {

			System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
			try {
				loadDataIntoGrakn(input, session);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// post calculation
		System.out.println("Post calculation into Grakn ...");

		/*
		 * //ReinitPostCalculationTag(session, PCRTestTypeValue_PPOSTCALCULLINKS,
		 * TagLinksPostCalculate, TagInitPostCalculate);
		 * PCRTestPrecedentEventsCalculation(session);
		 * 
		 * PostAveragePDivTCalculation(session); PostSpeedCalculation(session);
		 * 
		 * //ReinitPostCalculationTag(session, PCRTestTypeValue_PPOSTCALCUL,
		 * TagAccelerationPostCalculate, TagSpeedPostCalculate);
		 * 
		 * PostAccelerationCalculation(session);
		 */
		postcalculates(pcrFilesOptions, session);

		session.close();
		client.close();
	}

	static void connectAndReport(Collection<Report> reports) throws FileNotFoundException {
		GraknClient client = new GraknClient("localhost:48555");
		final GraknClient.Session session = client.session("grakn");

		for (final Report report : reports) {

			System.out.println("Report [" + report.getDataPath() + "] from Grakn ...");
			try {
				reportDataFromGrakn(report, session);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		session.close();
		client.close();
	}

	static void postcalculates(PCRFilesOptions pcrFilesOptions, GraknClient.Session session) {

		if (pcrFilesOptions.getOptImp()==null)
		{
			inputpcrtest.postCalculate(session);
			inputshops.postCalculate(session);
			inputshopsconfinement.postCalculate(session);
			inputdepartementscouvrefeu.postCalculate(session);
			inputweather.postCalculate(session);
			inputdepartements.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULPCRTEST)
		{
			inputpcrtest = new InputPCRTest(pcrFilesOptions.getOptImp(), null, maxget, pcr_minindice, pcr_maxindice,
					pcr_datefilter, pcr_minDate, pcr_maxDate, pcr_mindays, pcr_maxdays);
			inputpcrtest.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULLINKSPCRTEST)
		{
			inputpcrtest = new InputPCRTest(pcrFilesOptions.getOptImp(), null, maxget, pcr_minindice, pcr_maxindice,
					pcr_datefilter, pcr_minDate, pcr_maxDate, pcr_mindays, pcr_maxdays);
			inputpcrtest.postCalculate(session);
		}


	}

	/*
	 * static Collection<Input> initialiseDefine(Collection<Input> inputs, String
	 * subtype, String type) { inputs.add(new InputSingletonDefine(subtype, type) {
	 * 
	 * @Override public String template() { String define = "define "+ subtype +
	 * " sub "+ type +";\n"; return define; }
	 * 
	 * @Override String template(Json data) { // TODO Auto-generated method stub
	 * return null; } }); return inputs; }
	 */
	/*
	 * static Collection<Input> initialiseDefine(Collection<Input> inputs, String
	 * subtype, String type, List<DefineAttribute> attributes) { inputs.add(new
	 * InputSingletonDefine(subtype, type, attributes) {
	 * 
	 * @Override public String template() { String define = "define \n"; for
	 * (DefineAttribute attribut : attributes) { define+= "Attribut-" +
	 * attribut.getName() + " sub " + attribut.getType() + ";\n"; }
	 * 
	 * define += subtype + " sub "+ type + " "; for (DefineAttribute attribut :
	 * attributes) { define+=", has " + "Attribut-"+ attribut.getName(); } define
	 * +=";\n";
	 * 
	 * return define; }
	 * 
	 * @Override String template(Json data) { // TODO Auto-generated method stub
	 * return null; } }); return inputs; }
	 */
	/*
	 * static String InputSingletonDefineQuery(String prefix, String type, String
	 * key, String keyvalue) { String query = prefix + "$"+ type + "-" + keyvalue +
	 * " isa " + type + ", has " + key + " \"" + keyvalue + "\";" ; return query; }
	 */
	/*
	 * static String InputSingletonDefineQuery(String prefix, String type, String
	 * key, String keyvalue, String typevalue2, String value2) { String query =
	 * prefix + "$"+ type + "-" + keyvalue + " isa " + type + ", has " + key + " \""
	 * + keyvalue + "\", has " + typevalue2 + " \""+ value2 + "\";"; return query; }
	 */
	/*
	 * static Collection<Input> initialiseInsertEntitySingleton(Collection<Input>
	 * inputs, String type, String key, String keyvalue) { inputs.add(new
	 * InputSingletonInsertEntity(type, key, keyvalue) {
	 * 
	 * @Override public String template() { String insert =
	 * InputSingletonDefineQuery("insert ", type, key, keyvalue); return insert; }
	 * 
	 * @Override String template(Json data) { // TODO Auto-generated method stub
	 * return null; } }); return inputs; }
	 */
	/*
	 * static Collection<Input> initialiseInsertEntitySingleton(Collection<Input>
	 * inputs, String type, String key, String keyvalue, String typevalue2, String
	 * value2) { inputs.add(new InputSingletonInsertEntity(type, key,
	 * keyvalue,typevalue2, value2) {
	 * 
	 * @Override public String template() { String insert =
	 * InputSingletonDefineQuery("insert ", type, key, keyvalue, typevalue2,
	 * value2); return insert; }
	 * 
	 * @Override String template(Json data) { // TODO Auto-generated method stub
	 * return null; } }); return inputs; }
	 */
	/*
	 * 
	 * static final String PCRTestMetaTypeValue = "PCR"; static final String
	 * PCRTestTypeValue_P = "P"; static final String PCRTestTypeAttributeValue_P =
	 * "LongValueAttribute"; static final String PCRTestTypeValue_T = "T"; static
	 * final String PCRTestTypeAttributeValue_T = "LongValueAttribute"; static final
	 * String PCRTestTypeValue_PdivT = "PdivT"; static final String
	 * PCRTestTypeAttributeValue_PdivT = "DoubleValueAttribute"; static final String
	 * PCRTestTypeValue_AVPdivT = "AVPdivT"; static final String
	 * PCRTestTypeAttributeValue_AVPdivT = "DoubleValueAttribute"; static final
	 * String PCRTestTypeValue_VPdivT = "VPdivT"; static final String
	 * PCRTestTypeAttributeValue_VPdivT = "DoubleValueAttribute"; static final
	 * String PCRTestTypeValue_ACPdivT = "ACPdivT"; static final String
	 * PCRTestTypeAttributeValue_ACPdivT = "DoubleValueAttribute"; static final
	 * String PCRTestTypeValue_PPOSTCALCUL = "PPOSTCALCUL"; static final String
	 * PCRTestTypeAttributeValue_PPOSTCALCUL = "StringValueAttribute"; static final
	 * String PCRTestTypeValue_PPOSTCALCULLINKS = "PPOSTCALCULLINKS"; static final
	 * String PCRTestTypeAttributeValue_PPOSTCALCULLINKS = "StringValueAttribute";
	 * static final String PCRTestTypeValue_cl_age90 = "cl_age90"; static final
	 * String PCRTestTypeAttributeValue_cl_age90 = "LongValueAttribute"; static
	 * final String PCRTestTypeValue_pop = "pop"; static final String
	 * PCRTestTypeAttributeValue_pop = "LongValueAttribute";
	 * 
	 * static final String PCRTestClass_EventPCRTest = "EventPCRTest"; static final
	 * String PCRTestClass_ValueEventPCRTestRelations =
	 * "ValueEventPCRTestRelations"; static final String
	 * PCRTestClass_TypeValueEventPCRTestRelations =
	 * "TypeValueEventPCRTestRelations"; static final String
	 * PCRTestClass_EventPCRTestRelations = "EventPCRTestRelations"; static final
	 * String PCRTestClass_WhatPCRTest="WhatPCRTest"; static final String
	 * PCRTestClass_PCRTimeDate="PCRTimeDate"; static final String
	 * PCRTestClass_PCRPeriodOfTime="PCRPeriodOfTime"; static final String
	 * PCRTestClass_PCRPeriodicRelation="PCRPeriodicRelation";
	 */
	/*
	 * static // insertion of a value String insertValue (String metatype, String
	 * type, String indice, String TypeValue, String value, String resource, String
	 * value_eventrelation, String typevalue_eventrelations) {
	 * 
	 * String graqlInsertQuery="";
	 * 
	 * if (TypeValue=="StringValue") { graqlInsertQuery+= "$Value-" + type + "-" +
	 * indice + " isa " + TypeValue + ", has IdValue \""+ metatype +
	 * type+"-"+indice+"\"" + ", has TypeValueId \"" + type +"\"" + ", has " +
	 * TypeValue + "Attribute \"" + value + "\";\n"; } else { graqlInsertQuery+=
	 * "$Value-" + type + "-" + indice + " isa " + TypeValue + ", has IdValue \""+
	 * metatype + type+"-"+indice+"\"" + ", has TypeValueId \"" + type +"\"" +
	 * ", has " + TypeValue + "Attribute " + value + ";\n"; }
	 * 
	 * graqlInsertQuery += "$ValueRelation-" + type + "-" + indice
	 * +" (value : $Value-" + type +"-" + indice + " , resource : " + resource +
	 * ") isa " + value_eventrelation + ";\n";
	 * 
	 * graqlInsertQuery += "$ValueTypeRelation-" + type + "-" + indice
	 * +" (value : $Value-"+ type + "-" + indice + " , typevalue : $TypeValue-"+
	 * type + ") isa "+ typevalue_eventrelations + ";\n";
	 * 
	 * return graqlInsertQuery; }
	 */
	/*
	 * // événement confinements static Collection<Input> initialiseInputsPcrTests
	 * (Collection<Input>inputs) {
	 * 
	 * inputpcrtest = new InputPCRTest("data/sp-pos-quot-dep-2020-12-31-19h20");
	 * inputs=inputpcrtest.initialize(inputs);
	 * 
	 * inputs=initialiseDefine(inputs, PCRTestClass_EventPCRTest, "event");
	 * inputs=initialiseDefine(inputs, PCRTestClass_EventPCRTestRelations,
	 * "eventrelations"+SuffixEventRelations); inputs=initialiseDefine(inputs,
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * "ValueRelation"+SuffixValueRelations); inputs=initialiseDefine(inputs,
	 * PCRTestClass_TypeValueEventPCRTestRelations,
	 * "ValueTypeRelation"+SuffixValueTypeRelations);
	 * inputs=initialiseDefine(inputs, PCRTestClass_PCRTimeDate, "TimeDate");
	 * inputs=initialiseDefine(inputs, PCRTestClass_PCRPeriodOfTime,
	 * "PeriodOfTime"); inputs=initialiseDefine(inputs,
	 * PCRTestClass_PCRPeriodicRelation,
	 * "PeriodicRelation"+SuffixPeriodicRelations);
	 * 
	 * List<DefineAttribute> defineattributesWhatPCRTest=new ArrayList<>();
	 * defineattributesWhatPCRTest.add((new
	 * DefineAttribute(PCRTestTypeAttributeValue_cl_age90,PCRTestTypeValue_cl_age90)
	 * )); inputs=initialiseDefine(inputs, PCRTestClass_WhatPCRTest,
	 * "What",defineattributesWhatPCRTest);
	 * 
	 * // singletons inputs=initialiseInsertEntitySingleton(inputs, "Why",
	 * "Identity", "WPCR", "Description", "PCR-TEST");
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_P);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_T);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_PdivT);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_AVPdivT);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_VPdivT);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_ACPdivT);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_PPOSTCALCUL);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_cl_age90);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_pop);
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * PCRTestTypeValue_PPOSTCALCULLINKS);
	 * 
	 * 
	 * // data inputs.add(new Input("data/sp-pos-quot-dep-2020-12-11-19h20") {
	 * 
	 * @Override public String template(Json pcrtestcohorts) { String
	 * graqlInsertQuery=""; if (pcrtestcohorts.at("dep")!=null) {
	 * 
	 * indice++;
	 * 
	 * LocalDate
	 * datepcr=LocalDate.parse((CharSequence)pcrtestcohorts.at("jour").asString());
	 * 
	 * 
	 * if ((indice<=maxindice && indice>=minindice && datefilter==false) ||
	 * (datepcr.isAfter(minDate) && datepcr.isBefore(maxDate) && datefilter==true))
	 * { String departement=Long.toString(pcrtestcohorts.at("dep").asLong());
	 * 
	 * graqlInsertQuery = "match "; graqlInsertQuery +=
	 * InputSingletonDefineQuery("", "TypeValue", "IdValue", PCRTestTypeValue_P);
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * PCRTestTypeValue_T); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_PdivT);
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * PCRTestTypeValue_PPOSTCALCUL); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * PCRTestTypeValue_PPOSTCALCULLINKS); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * PCRTestTypeValue_cl_age90); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", PCRTestTypeValue_pop);
	 * graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description",
	 * "PCR-TEST"); graqlInsertQuery += InputSingletonDefineQuery("","Departement",
	 * "CodeGLN", departement);
	 * 
	 * LocalDate datepcrmoins20=datepcr.minusDays(20);
	 * 
	 * graqlInsertQuery+= "insert $Event-" + indice.toString() + " isa " +
	 * PCRTestClass_EventPCRTest +", has Id \"EPCR-" + indice.toString() + "\";\n";
	 * graqlInsertQuery+= "$IdentifiedPatientCohort-" + indice.toString()
	 * +" isa IdentifiedPatientCohort, has Identity \"" +
	 * pcrtestcohorts.at("dep").asLong()+ "-"+ indice.toString()+ "\";\n";
	 * graqlInsertQuery+= "$TimeDate-" + indice.toString() +" isa "+
	 * PCRTestClass_PCRTimeDate+", has Identity \"PCRTD-"+indice.toString()+
	 * "\", has EventDate " + datepcr + ";\n"; graqlInsertQuery+= "$TimeDate-20-" +
	 * indice.toString() +" isa "+ PCRTestClass_PCRTimeDate
	 * +", has Identity \"PCRTD-20-"+indice.toString()+ "\", has EventDate " +
	 * datepcrmoins20 + ";\n"; //graqlInsertQuery+= "$Departement-" +
	 * indice.toString()
	 * +" isa Departement, has Identity \"PCRTR-"+indice.toString()+
	 * "\", has CodeGLN \"" + pcrtestcohorts.at("dep").asLong()+ "\";\n";
	 * 
	 * Long valueP=pcrtestcohorts.at("P").asLong(); Long
	 * valueT=pcrtestcohorts.at("T").asLong(); Double valuePdivT=0.0; if
	 * (valueT!=0.0) { valuePdivT=((double) valueP)/((double) valueT); } Locale
	 * locale = new Locale("en", "UK"); DecimalFormat df = new
	 * DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
	 * String sPdivT=df.format(valuePdivT);
	 * 
	 * Long valuecl_age90=pcrtestcohorts.at("cl_age90").asLong(); Long
	 * valuepop=pcrtestcohorts.at("pop").asLong();
	 * 
	 * graqlInsertQuery+= "$What-" + indice.toString() +" isa " +
	 * PCRTestClass_WhatPCRTest +", has Identity \"WPCR-"+indice.toString()+
	 * "\", has Description \"PCR-RESULTS-STATS\", has Attribut-"+
	 * PCRTestTypeValue_cl_age90 + " " + valuecl_age90.toString() + ";\n";
	 * 
	 * graqlInsertQuery += insertValue (PCRTestMetaTypeValue, PCRTestTypeValue_P,
	 * indice.toString(), "LongValue", valueP.toString(), "$What-" +
	 * indice.toString(), PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue, PCRTestTypeValue_T, indice.toString(), "LongValue",
	 * valueT.toString(), "$What-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue, PCRTestTypeValue_PdivT, indice.toString(),
	 * "DoubleValue", sPdivT, "$What-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue, PCRTestTypeValue_cl_age90, indice.toString(),
	 * "LongValue", valuecl_age90.toString(), "$What-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue,PCRTestTypeValue_pop, indice.toString(), "LongValue",
	 * valuepop.toString(), "$What-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue,PCRTestTypeValue_PPOSTCALCUL, indice.toString(),
	 * "StringValue", TagInitPostCalculate, "$What-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * // relation between date graqlInsertQuery+= "$Period-"+ indice.toString() +
	 * " isa "+ PCRTestClass_PCRPeriodOfTime + ", has Identity \"PCRPD-20-" +
	 * indice.toString() + "\", has TypeFrequency \"Daily\";"; graqlInsertQuery+=
	 * "$PeriodRelation-" + indice.toString() +" (periodoftime : $Period-" +
	 * indice.toString() + " , startwhendate : " + "$TimeDate-20-" +
	 * indice.toString() + " , endwhendate : " + "$TimeDate-" + indice.toString() +
	 * ") isa " + PCRTestClass_PCRPeriodicRelation + ";";
	 * 
	 * graqlInsertQuery += insertValue (PCRTestMetaTypeValue,
	 * PCRTestTypeValue_PPOSTCALCULLINKS, indice.toString(), "StringValue",
	 * TagInitPostCalculate, "$Period-" + indice.toString(),
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * graqlInsertQuery+= "$eventRelation-" + indice.toString()
	 * +" (registeredevent : $Event-" + indice.toString()+
	 * ", actor: $IdentifiedPatientCohort-" +
	 * indice.toString()+", localization:$Departement-" + departement +
	 * ", time :$TimeDate-" + indice.toString()+", goal :$Why-PCR-TEST" +
	 * ", object :$What-"+ indice.toString()+") isa " +
	 * PCRTestClass_EventPCRTestRelations+";\n"; } } return (graqlInsertQuery); }
	 * }); return inputs; }
	 */

	/*
	 * static void check(Json json, List<String> titres) { int col=0; while
	 * (col<titres.size()) { if (json.at(titres.get(col)).isNull()) {
	 * System.out.println("Titre non trouvé :" + titres.get(col)); } col++; } }
	 */

	/*
	 * // événement confinements static Collection<Input>
	 * initialiseInputsShopsConfinement(Collection<Input>inputs) {
	 * 
	 * inputshopsconfinement = new InputShopsConfinement("data/shopconfinement");
	 * inputs=inputshopsconfinement.initialize(inputs);
	 * 
	 * 
	 * // derived types inputs=initialiseDefine(inputs, "EventShopConfinement",
	 * "event"); inputs=initialiseDefine(inputs, "ShopsGroup", "GroupWho");
	 * inputs=initialiseDefine(inputs, "EventShopConfinementRelations",
	 * "eventrelations"+SuffixEventRelations); inputs=initialiseDefine(inputs,
	 * "ValueEventShopConfinementRelations", "ValueRelation"+SuffixValueRelations);
	 * inputs=initialiseDefine(inputs, "TypeValueEventShopConfinementRelations",
	 * "ValueTypeRelation"+SuffixValueTypeRelations);
	 * 
	 * // singletons inputs=initialiseInsertEntitySingleton(inputs, "Why",
	 * "Identity", "WSC-1", "Description", "SHOPCONFINEMENT");
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "Country", "Identity", "Wfr",
	 * "CodeGLN", "France"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "category");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "subcategory"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "confinementdebut1");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "status1d"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "confinementfin1"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "status1f");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "couvrefeudebut2"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "status2d");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "couvrefeufin2"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "status2f"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "confinementdebut3");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "status3d"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "confinementfin3"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "status3f");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "couvrefeudebut4"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "status4d");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "couvrefeufin4"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "status4f"); // data inputs.add(new Input("data/shopconfinement")
	 * {
	 * 
	 * @Override public String template(Json shopsconfinement) {
	 * 
	 * String graqlInsertQuery=""; try {
	 * 
	 * if (shopsconfinement.at("category")!=null) {
	 * 
	 * indice++;
	 * 
	 * if (indice<=maxindice && indice>=minindice) { graqlInsertQuery = "match ";
	 * graqlInsertQuery += InputSingletonDefineQuery("", "Country", "CodeGLN",
	 * "France"); graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue",
	 * "IdValue", "category"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "subcategory");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "confinementdebut1"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status1d");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "confinementfin1"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status1f");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "couvrefeudebut2"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status2d");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "couvrefeufin2"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status2f");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "confinementdebut3"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status3d");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "confinementfin3"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status3f");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "couvrefeudebut4"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status4d");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "couvrefeufin4"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "status4f");
	 * 
	 * 
	 * graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description",
	 * "SHOPCONFINEMENT");
	 * 
	 * String category=shopsconfinement.at("category").asString(); String
	 * subcategory=shopsconfinement.at("subcategory").asString(); String
	 * shopgroupcategory=category+"-"+subcategory;
	 * 
	 * // à poursuivre avec evenement simple confinement et périodique couvrefeu
	 * graqlInsertQuery+= "\ninsert "; graqlInsertQuery+= "$ShopsGroup-" +
	 * indice.toString() +" isa ShopsGroup, has Identity \"" + shopgroupcategory +
	 * "\";"; graqlInsertQuery+= "$IdentifiedWhat-W-1"
	 * +" isa IdentifiedWhat, has Identity \"WCONFINEMENT-" + shopgroupcategory +
	 * "\";"; graqlInsertQuery+= "$IdentifiedWhat-W-2"
	 * +" isa IdentifiedWhat, has Identity \"WCOUVREFEU-" + shopgroupcategory +
	 * "\";";
	 * 
	 * graqlInsertQuery+= "$Value-Category-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SCC-"+ indice.toString() +
	 * "\", has StringValueAttribute \"" + category + "\";"; graqlInsertQuery+=
	 * "$Value-SubCategory-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SCSC-"+ indice.toString() +
	 * "\", has StringValueAttribute \"" + subcategory + "\";"; graqlInsertQuery+=
	 * "$ValueRelation-Category-CONFINEMENT-" + indice.toString()
	 * +" (value : $Value-Category-" + indice.toString() + " , resource : " +
	 * "$IdentifiedWhat-W-1" + ") isa ValueEventShopConfinementRelations" + ";";
	 * graqlInsertQuery+= "$ValueRelation-Category-COUVREFEU-" + indice.toString()
	 * +" (value : $Value-Category-" + indice.toString() + " , resource : " +
	 * "$IdentifiedWhat-W-2" + ") isa ValueEventShopConfinementRelations" + ";";
	 * graqlInsertQuery+= "$ValueTypeRelation-Category-" + indice.toString()
	 * +" (value : $Value-Category-" + indice.toString() +
	 * " , typevalue : $TypeValue-category) isa TypeValueEventShopConfinementRelations"
	 * + ";"; graqlInsertQuery+= "$ValueRelation-SubCategory-CONFINEMENT-" +
	 * indice.toString() +" (value : $Value-SubCategory-" + indice.toString() +
	 * " , resource : " + "$IdentifiedWhat-W-1" +
	 * ") isa ValueEventShopConfinementRelations" + ";"; graqlInsertQuery+=
	 * "$ValueRelation-SubCategory-COUVREFEU-" + indice.toString()
	 * +" (value : $Value-SubCategory-" + indice.toString() + " , resource : " +
	 * "$IdentifiedWhat-W-2" + ") isa ValueEventShopConfinementRelations" + ";";
	 * graqlInsertQuery+= "$ValueTypeRelation-SubCategory-" + indice.toString()
	 * +" (value : $Value-SubCategory-" + indice.toString() +
	 * " , typevalue : $TypeValue-subcategory) isa TypeValueEventShopConfinementRelations"
	 * + ";";
	 * 
	 * // confinement & couvrefeu List<String> titres =
	 * Arrays.asList("confinementdebut1","status1d","confinementfin1","status1f",
	 * "couvrefeudebut2","status2d","couvrefeufin2","status2f",
	 * "confinementdebut3","status3d","confinementfin3","status3f",
	 * "couvrefeudebut4","status4d","couvrefeufin4","status4f");
	 * 
	 * //check(shopsconfinement, titres);
	 * 
	 * int col=0; while (col<titres.size()) { String whattype; String
	 * name=titres.get(col);
	 * 
	 * if (name.contains("confinement")) { whattype="$IdentifiedWhat-W-1"; } else {
	 * whattype="$IdentifiedWhat-W-2"; } // création de l'événement
	 * graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col +
	 * " isa EventShopConfinement, has Id \"ESC-" + indice.toString() + "-" + col +
	 * "\";";
	 * 
	 * // création de la période LocalDate
	 * datedebut=LocalDate.parse((CharSequence)shopsconfinement.at(titres.get(col)).
	 * asString());
	 * 
	 * Long statusdebut=Integer.toUnsignedLong(0); Json
	 * stat1=shopsconfinement.at(titres.get(col+1)); String
	 * namestatus1=titres.get(col+1); if (!stat1.isNull())
	 * statusdebut=stat1.asLong(); LocalDate
	 * datefin=LocalDate.parse((CharSequence)shopsconfinement.at(titres.get(col+2)).
	 * asString()); Long statusfin=Integer.toUnsignedLong(0); Json
	 * stat2=shopsconfinement.at(titres.get(col+3)); String
	 * namestatus2=titres.get(col+3); if (!stat2.isNull()) statusfin=stat2.asLong();
	 * 
	 * graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString()
	 * +" isa TimeDate, has Identity \"TD-"+ indice.toString() + "-" + col +
	 * "\", has EventDate " + datedebut + ";"; graqlInsertQuery+= "$TimeDate-fin-" +
	 * col +"-"+ indice.toString() +" isa TimeDate, has Identity \"TF-"+
	 * indice.toString() + "-" + col + "\", has EventDate " + datefin + ";";
	 * graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() +
	 * " isa PeriodOfTime, has Identity \"PT-"+ indice.toString() + "-" + col +
	 * "\", has TypeFrequency \"Daily\";";
	 * 
	 * graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString()
	 * +" (periodoftime : $Period-" + col +"-" + indice.toString() +
	 * " , startwhendate : " + "$TimeDate-debut-" + col +"-"+ indice.toString() +
	 * " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +
	 * ") isa PeriodicRelation" + ";";
	 * 
	 * 
	 * // status debut
	 * 
	 * graqlInsertQuery+= "$Value-StatusDebut-" + col +"-"+ indice.toString()
	 * +" isa LongValue, has IdValue \"SSD-"+ indice.toString() + "-" + col +
	 * "\", has LongValueAttribute " + statusdebut + ";"; graqlInsertQuery+=
	 * "$ValueRelation-StatusDebut-" + col +"-"+ indice.toString()
	 * +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() +
	 * " , resource : " + "$TimeDate-debut-" + col +"-"+ indice.toString() +
	 * ") isa ValueEventShopConfinementRelations" + ";"; graqlInsertQuery+=
	 * "$ValueTypeRelation-StatusDebut-" + col +"-"+ indice.toString()
	 * +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() +
	 * " , typevalue : $TypeValue-" + namestatus1+
	 * ") isa TypeValueEventShopConfinementRelations" + ";";
	 * 
	 * // status fin
	 * 
	 * graqlInsertQuery+= "$Value-StatusFin-" + col +"-"+ indice.toString()
	 * +" isa LongValue, has IdValue \"SSF-"+ indice.toString() + "-" + col +
	 * "\", has LongValueAttribute " + statusfin + ";"; graqlInsertQuery+=
	 * "$ValueRelation-StatusFin-" + col +"-"+ indice.toString()
	 * +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() +
	 * " , resource : " + "$TimeDate-fin-" + col +"-"+ indice.toString() +
	 * ") isa ValueEventShopConfinementRelations" + ";"; graqlInsertQuery+=
	 * "$ValueTypeRelation-StatusFin-" + col +"-"+ indice.toString()
	 * +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() +
	 * " , typevalue : $TypeValue-" + namestatus2+
	 * " ) isa  TypeValueEventShopConfinementRelations" + ";";
	 * 
	 * 
	 * // relations de l'événement graqlInsertQuery+= "$eventRelation-" +
	 * indice.toString() + "-" + col +" (registeredevent : $Event-" +
	 * indice.toString() + "-" + col + ", actor: $ShopsGroup-" + indice.toString() +
	 * ", localization:$Country-France, time :$Period-" + col + "-" +
	 * indice.toString()+ ", goal :$Why-SHOPCONFINEMENT" + ", object : "+ whattype +
	 * ") isa EventShopConfinementRelations ;\n";
	 * 
	 * col+=4; }
	 * 
	 * } else { int test=0; } } } catch (Exception e) { int y=0; } return
	 * (graqlInsertQuery); } }); return inputs; }
	 */

	/*
	 * // master data département static Collection<Input>
	 * initialiseInputsDepartements(Collection<Input>inputs) {
	 * 
	 * inputdepartements = new InputDepartements("data/departement");
	 * inputs=inputdepartements.initialize(inputs);
	 * 
	 * /* // data inputs.add(new Input("data/departement") {
	 * 
	 * @Override public String template(Json departement) {
	 * 
	 * String graqlInsertQuery=""; try {
	 * 
	 * if (departement.at("Code INSEE")!=null) { indice++;
	 * 
	 * if (indice<=maxindice && indice>=minindice) {
	 * 
	 * String codeGLN=departement.at("Code INSEE").asString(); String
	 * labelGLN=departement.at("Département").asString();
	 * 
	 * graqlInsertQuery+= "\ninsert ";
	 * 
	 * graqlInsertQuery+= "$Departement-" + indice.toString()
	 * +" isa Departement , has Identity \"DD-"+indice.toString() +
	 * "\", has CodeGLN \"" + codeGLN + "\";"; graqlInsertQuery+= "$GLNLabel-" +
	 * indice.toString() +" isa GLNLabel , has Language \"fr\", has LabelGLN \"" +
	 * labelGLN + "\";";
	 * 
	 * graqlInsertQuery+= "$ValueRelation-Departement-Label-" + indice.toString()
	 * +" (GLNcode : $Departement-" + indice.toString() + " , GLNlabel : $GLNLabel-"
	 * + indice.toString()+") isa GLNRelation;"; } } } catch (Exception e) { int
	 * y=0; } return (graqlInsertQuery); } }); return inputs; }
	 */

	/*
	 * // événement confinements static Collection<Input>
	 * initialiseInputsDepartementCouvreFeu(Collection<Input>inputs) {
	 * 
	 * inputdepartementscouvrefeu = new
	 * InputDepartementsCouvreFeu("data/departementdecret");
	 * inputs=inputdepartementscouvrefeu.initialize(inputs);
	 * 
	 * // derived types
	 * 
	 * inputs=initialiseDefine(inputs, "EventDepartementConfinement", "event");
	 * inputs=initialiseDefine(inputs, "DepartementGroup", "GroupWho");
	 * 
	 * inputs=initialiseDefine(inputs, "ValueHorairesStart", "DoubleValue");
	 * inputs=initialiseDefine(inputs, "ValueHorairesEnd", "DoubleValue");
	 * 
	 * inputs=initialiseDefine(inputs, "EventDepartementConfinementRelations",
	 * "eventrelations"+SuffixEventRelations); inputs=initialiseDefine(inputs,
	 * "ValueEventDepartementConfinementRelations",
	 * "ValueRelation"+SuffixValueRelations); inputs=initialiseDefine(inputs,
	 * "TypeValueEventDepartementConfinementRelations",
	 * "ValueTypeRelation"+SuffixValueTypeRelations);
	 * 
	 * 
	 * 
	 * // singletons inputs=initialiseInsertEntitySingleton(inputs, "Why",
	 * "Identity", "WDC-1", "Description", "DEPARTEMENTCONFINEMENT");
	 * inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity",
	 * "W-1", "Description", "CONFINEMENT");
	 * inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity",
	 * "W-2", "Description", "COUVREFEU");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "departement"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "type1"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "debutcouvre1");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "fincouvre1"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "detailhours1");
	 * 
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "type2"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "debutcouvre2"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "fincouvre2");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "detailhours2");
	 * 
	 * // data inputs.add(new Input("data/departementdecret") {
	 * 
	 * @Override public String template(Json departementconfinement) {
	 * 
	 * String graqlInsertQuery=""; try {
	 * 
	 * if (departementconfinement.at("departement")!=null) { String
	 * departement=departementconfinement.at("departement").asString();
	 * 
	 * 
	 * indice++;
	 * 
	 * if (indice<=maxindice && indice>=minindice) { graqlInsertQuery = "match ";
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "departement"); graqlInsertQuery += InputSingletonDefineQuery("","TypeValue",
	 * "IdValue", "type1"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "debutcouvre1");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "fincouvre1"); graqlInsertQuery += InputSingletonDefineQuery("","TypeValue",
	 * "IdValue", "detailhours1");
	 * 
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "type2"); graqlInsertQuery += InputSingletonDefineQuery("","TypeValue",
	 * "IdValue", "debutcouvre2"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", "fincouvre2");
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * "detailhours2");
	 * 
	 * graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description",
	 * "DEPARTEMENTCONFINEMENT");
	 * 
	 * graqlInsertQuery += InputSingletonDefineQuery("","Departement", "CodeGLN",
	 * departement);
	 * 
	 * 
	 * // à poursuivre avec evenement simple confinement et périodique couvrefeu
	 * graqlInsertQuery+= "\ninsert "; graqlInsertQuery+= "$IdentifiedWhat-W-1"
	 * +" isa IdentifiedWhat, has Identity \"WCONFINEMENT-" + departement + "\";";
	 * graqlInsertQuery+= "$IdentifiedWhat-W-2"
	 * +" isa IdentifiedWhat, has Identity \"WCOUVREFEU-" + departement + "\";";
	 * graqlInsertQuery+= "$DepartementGroup-" + indice.toString()
	 * +" isa DepartementGroup, has Identity \"DG-" + departement+"\";";
	 * graqlInsertQuery+= "$Value-Departement-" + indice.toString()
	 * +" isa StringValue, has IdValue \"DD-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + departement + "\";";
	 * 
	 * graqlInsertQuery+= "$ValueRelation-Departement-CONFINEMENT-" +
	 * indice.toString() +" (value : $Value-Departement-" + indice.toString() +
	 * " , resource : " + "$IdentifiedWhat-W-1" +
	 * ") isa ValueEventDepartementConfinementRelations" + ";"; graqlInsertQuery+=
	 * "$ValueRelation-Departement-COUVREFEU-" + indice.toString()
	 * +" (value : $Value-Departement-" + indice.toString() + " , resource : " +
	 * "$IdentifiedWhat-W-2" + ") isa ValueEventDepartementConfinementRelations" +
	 * ";"; graqlInsertQuery+= "$ValueTypeRelation-Departement-" + indice.toString()
	 * +" (value : $Value-Departement-" + indice.toString() +
	 * " , typevalue : $TypeValue-departement) isa TypeValueEventDepartementConfinementRelations"
	 * + ";";
	 * 
	 * 
	 * // confinement & couvrefeu List<String> titres =
	 * Arrays.asList("type1","debutcouvre1","fincouvre1","detailhours1",
	 * "type2","debutcouvre2","fincouvre2","detailhours2");
	 * 
	 * int col=1; while (col<titres.size()) { String whattype; String
	 * name=titres.get(col);
	 * 
	 * if (name.contains("confinement")) { whattype="$IdentifiedWhat-W-1"; } else {
	 * whattype="$IdentifiedWhat-W-2"; } // création de l'événement
	 * graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col +
	 * " isa EventDepartementConfinement, has Id \"EDC-" + indice.toString() + "-" +
	 * col + "\";";
	 * 
	 * // création de la période LocalDate
	 * datedebut=LocalDate.parse((CharSequence)departementconfinement.at(titres.get(
	 * col)).asString()); LocalDate
	 * datefin=LocalDate.parse((CharSequence)departementconfinement.at(titres.get(
	 * col+1)).asString());
	 * 
	 * // horaires LocalTime startdetailhours=LocalTime.of(0, 0); LocalTime
	 * enddetailhours=LocalTime.of(0, 0); double doublestartdetailhours=0.0; double
	 * doubleenddetailhours=0.0;
	 * 
	 * 
	 * Json horaires=departementconfinement.at(titres.get(col+2)); String
	 * namehoraires=titres.get(col+2); if (!horaires.isNull()) { String []
	 * listhoraires=horaires.asString().split(";"); if (listhoraires.length==2) {
	 * startdetailhours=LocalTime.parse(listhoraires[0]);
	 * doublestartdetailhours=startdetailhours.getHour()+startdetailhours.getMinute(
	 * )/60+startdetailhours.getMinute()/3600;
	 * 
	 * enddetailhours=LocalTime.parse(listhoraires[1]);
	 * doubleenddetailhours=enddetailhours.getHour()+startdetailhours.getMinute()/60
	 * +startdetailhours.getMinute()/3600;
	 * 
	 * } }
	 * 
	 * graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString()
	 * +" isa TimeDate, has Identity \"DTD-" + col + "-"+ indice.toString() +
	 * "\", has EventDate " + datedebut + ";"; graqlInsertQuery+= "$TimeDate-fin-" +
	 * col +"-"+ indice.toString() +" isa TimeDate, has Identity \"DTF-" + col +
	 * "-"+ indice.toString() + "\", has EventDate " + datefin + ";";
	 * graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() +
	 * " isa PeriodOfTime, has Identity \"DPOfT-" + col + "-"+ indice.toString() +
	 * "\", has TypeFrequency \"Daily\";";
	 * 
	 * graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString()
	 * +" (periodoftime : $Period-" + col +"-" + indice.toString() +
	 * " , startwhendate : " + "$TimeDate-debut-" + col +"-"+ indice.toString() +
	 * " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +
	 * ") isa PeriodicRelation" + ";";
	 * 
	 * 
	 * // horaires
	 * 
	 * // debut graqlInsertQuery+= "$Value-HorairesDebut-" + col +"-"+
	 * indice.toString() +" isa ValueHorairesStart, has IdValue \"DHD-" + col +"-"+
	 * indice.toString() + "\", has DoubleValueAttribute " + doublestartdetailhours
	 * + ";"; graqlInsertQuery+= "$ValueRelation-HorairesDebut-" + col +"-"+
	 * indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+
	 * indice.toString() + " , resource : " + "$Period-" + col +"-"+
	 * indice.toString() + ") isa ValueEventDepartementConfinementRelations" + ";";
	 * graqlInsertQuery+= "$ValueTypeRelation-HorairesDebut-" + col +"-"+
	 * indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+
	 * indice.toString() + " , typevalue : $TypeValue-" + namehoraires+
	 * ") isa TypeValueEventDepartementConfinementRelations" + ";";
	 * 
	 * // fin graqlInsertQuery+= "$Value-HorairesFin-" + col +"-"+ indice.toString()
	 * +" isa ValueHorairesEnd, has IdValue \"DHF-" + col +"-"+indice.toString() +
	 * "\", has DoubleValueAttribute " + doubleenddetailhours + ";";
	 * graqlInsertQuery+= "$ValueRelation-HorairesFin-" + col +"-"+
	 * indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+
	 * indice.toString() + " , resource : " + "$Period-" + col +"-"+
	 * indice.toString() + ") isa ValueEventDepartementConfinementRelations" + ";";
	 * graqlInsertQuery+= "$ValueTypeRelation-HorairesFin-" + col +"-"+
	 * indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+
	 * indice.toString() + " , typevalue : $TypeValue-" + namehoraires+
	 * ") isa TypeValueEventDepartementConfinementRelations" + ";";
	 * 
	 * // no as a master data // graqlInsertQuery+= "$Departement-" + col +"-" +
	 * indice.toString() +" isa Departement , has Identity \"DD-" + col
	 * +"-"+indice.toString() + "\", has CodeGLN \"" +
	 * departementconfinement.at("departement").asString() + "\";";
	 * 
	 * // relations de l'événement graqlInsertQuery+= "$eventRelation-" +
	 * indice.toString() + "-" + col +" (registeredevent : $Event-" +
	 * indice.toString() + "-" + col + ", actor: $DepartementGroup-" +
	 * indice.toString() + ", localization:" + "$Departement-" + departement +
	 * ", time :$Period-" + col + "-" + indice.toString()+
	 * ", goal :$Why-DEPARTEMENTCONFINEMENT" + ", object : "+ whattype +
	 * ") isa EventDepartementConfinementRelations ;";
	 * 
	 * col+=4; } } else { int test=0; } }
	 * 
	 * } catch (Exception e) { int y=0; } return (graqlInsertQuery); } }); return
	 * inputs; }
	 */

	/*
	 * static final String WeatherTypeValue_MetaTypeValue = "DPT"; static final
	 * String WeatherTypeValue_Departement = "Departement"; static final String
	 * WeatherTypeAttributeValue_Departement = "StringValueAttribute"; static final
	 * String WeatherTypeValue_TMin = "TMin"; static final String
	 * WeatherTypeAttributeValue_TMin = "DoubleValueAttribute"; static final String
	 * WeatherTypeValue_TMax = "TMax"; static final String
	 * WeatherTypeAttributeValue_TMax = "DoubleValueAttribute"; static final String
	 * WeatherTypeValue_TMoy = "TMoy"; static final String
	 * WeatherTypeAttributeValue_TMoy = "DoubleValueAttribute";
	 * 
	 * static final String WeatherClass_EventWeather = "EventWeather"; static final
	 * String WeatherClass_EventWeatherDepartementGroup =
	 * "EventWeatherDepartementGroup"; static final String
	 * WeatherClass_ValueEventWeatherRelations = "ValueEventWeatherRelations";
	 * static final String WeatherClass_TypeValueEventWeatherRelations =
	 * "TypeValueWeatherRelations"; static final String
	 * WeatherClass_EventWeatherRelations = "EventWeatherRelations"; static final
	 * String WeatherClass_WhatPCRTest="WhatPCRTest";
	 * 
	 * 
	 * 
	 * // événement confinements static Collection<Input>
	 * initialiseInputsWeather(Collection<Input>inputs) {
	 * 
	 * inputweather = new
	 * InputWeather("data/temperature-quotidienne-departementale");
	 * inputs=inputweather.initialize(inputs);
	 * 
	 * /* // derived types
	 * 
	 * inputs=initialiseDefine(inputs, WeatherClass_EventWeather, "event");
	 * inputs=initialiseDefine(inputs, WeatherClass_EventWeatherDepartementGroup,
	 * "GroupWho");
	 * 
	 * inputs=initialiseDefine(inputs, WeatherClass_EventWeatherRelations,
	 * "eventrelations"+SuffixEventRelations); inputs=initialiseDefine(inputs,
	 * WeatherClass_ValueEventWeatherRelations,
	 * "ValueRelation"+SuffixValueRelations); inputs=initialiseDefine(inputs,
	 * WeatherClass_TypeValueEventWeatherRelations,
	 * "ValueTypeRelation"+SuffixValueTypeRelations);
	 * 
	 * 
	 * 
	 * // singletons inputs=initialiseInsertEntitySingleton(inputs, "Why",
	 * "Identity", "WWE-1", "Description", "WEATHER");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * WeatherTypeValue_Departement); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", WeatherTypeValue_TMin);
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * WeatherTypeValue_TMax); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", WeatherTypeValue_TMoy);
	 * 
	 * // data inputs.add(new Input("data/temperature-quotidienne-departementale") {
	 * 
	 * @Override public String template(Json weather) {
	 * 
	 * String graqlInsertQuery=""; try {
	 * 
	 * if (weather.at("Code INSEE département")!=null) {
	 * 
	 * 
	 * String departement=weather.at("Code INSEE département").asString();
	 * 
	 * String sdate=weather.at("Date").asString(); DateTimeFormatter formatter=
	 * DateTimeFormatter.ofPattern("dd/MM/yyyy");
	 * 
	 * LocalDate date=LocalDate.parse(sdate, formatter);
	 * 
	 * //System.out.println ("Indice : " + indice + " date : " + date); indice++;
	 * 
	 * if ((indice<=maxindice && indice>=minindice && datefilter==false) ||
	 * (date.isAfter(minDate) && date.isBefore(maxDate) && datefilter==true)) {
	 * 
	 * Double TMin=weather.at("TMin (°C)").asDouble(); Double
	 * TMax=weather.at("TMax (°C)").asDouble(); Double
	 * TMoy=weather.at("TMoy (°C)").asDouble();
	 * 
	 * graqlInsertQuery = "match "; graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * WeatherTypeValue_Departement); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", WeatherTypeValue_TMin);
	 * graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue",
	 * WeatherTypeValue_TMax); graqlInsertQuery +=
	 * InputSingletonDefineQuery("","TypeValue", "IdValue", WeatherTypeValue_TMoy);
	 * 
	 * graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description",
	 * "WEATHER"); graqlInsertQuery += InputSingletonDefineQuery("","Departement",
	 * "CodeGLN", departement);
	 * 
	 * 
	 * // à poursuivre avec evenement simple confinement et périodique couvrefeu
	 * graqlInsertQuery+= "\ninsert ";
	 * 
	 * graqlInsertQuery+=
	 * "$IdentifiedWhat isa IdentifiedWhat, has Identity \"WEATHER-" +
	 * indice.toString() + "-"+ departement + "\";"; graqlInsertQuery+=
	 * "$DepartementGroup isa DepartementGroup, has Identity \"WDG-" +
	 * indice.toString() + "-" + departement+"\";";
	 * 
	 * graqlInsertQuery += insertValue (WeatherTypeValue_MetaTypeValue,
	 * WeatherTypeValue_Departement, indice.toString(), "StringValue", departement,
	 * "$DepartementGroup", WeatherClass_ValueEventWeatherRelations,
	 * WeatherClass_TypeValueEventWeatherRelations); graqlInsertQuery += insertValue
	 * (WeatherTypeValue_MetaTypeValue, WeatherTypeValue_TMin, indice.toString(),
	 * "DoubleValue", TMin.toString(), "$IdentifiedWhat",
	 * WeatherClass_ValueEventWeatherRelations,
	 * WeatherClass_TypeValueEventWeatherRelations); graqlInsertQuery += insertValue
	 * (WeatherTypeValue_MetaTypeValue, WeatherTypeValue_TMax, indice.toString(),
	 * "DoubleValue", TMax.toString(), "$IdentifiedWhat",
	 * WeatherClass_ValueEventWeatherRelations,
	 * WeatherClass_TypeValueEventWeatherRelations); graqlInsertQuery += insertValue
	 * (WeatherTypeValue_MetaTypeValue, WeatherTypeValue_TMoy, indice.toString(),
	 * "DoubleValue", TMoy.toString(), "$IdentifiedWhat",
	 * WeatherClass_ValueEventWeatherRelations,
	 * WeatherClass_TypeValueEventWeatherRelations);
	 * 
	 * graqlInsertQuery+= "$TimeDate isa TimeDate, has Identity \"WTD-" +
	 * indice.toString() + "\", has EventDate " + date + ";";
	 * 
	 * graqlInsertQuery+= "$Event isa EventWeather, has Id \"EW-" +
	 * indice.toString() + "\";";
	 * 
	 * graqlInsertQuery+=
	 * "$eventRelation(registeredevent : $Event, actor: $DepartementGroup, localization: $Departement-"
	 * +departement+", time :$TimeDate, goal :$Why-WEATHER" +
	 * ", object : $IdentifiedWhat) isa " + WeatherClass_EventWeatherRelations +
	 * ";";
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { int y=0; } return (graqlInsertQuery); } }); return
	 * inputs; }
	 */

	/*
	 * // shops by departement static Collection<Input>
	 * initialiseInputsShops(Collection<Input>inputs) {
	 * 
	 * inputshops = new InputShops("data/poi_osm_FR");
	 * inputs=inputshops.initialize(inputs);
	 * 
	 * // derived types inputs=initialiseDefine(inputs, "Shop", "IdentifiedWho");
	 * inputs=initialiseDefine(inputs, "ValueShopRelations",
	 * "ValueRelation"+SuffixValueRelations); inputs=initialiseDefine(inputs,
	 * "TypeValueShopRelations", "ValueTypeRelation"+SuffixValueTypeRelations);
	 * 
	 * // singletons inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "Name"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "Category");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "SubCategory"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "Brand"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "WikiData");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "UrlHours"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "Infos"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "Status");
	 * inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "OpeningHours"); inputs=initialiseInsertEntitySingleton(inputs, "TypeValue",
	 * "IdValue", "Lat"); inputs=initialiseInsertEntitySingleton(inputs,
	 * "TypeValue", "IdValue", "Lon");
	 * //inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue",
	 * "Dept");
	 * 
	 * // data inputs.add(new Input("data/poi_osm_FR") {
	 * 
	 * @Override public String template(Json shops) {
	 * 
	 * String graqlInsertQuery=""; if (!shops.at("osm_id").isNull()) {
	 * 
	 * indice++;
	 * 
	 * if (indice%100000==0) { try {
	 * System.out.println("pause 2 s pour passer antispam ;indice :"+indice+"\n");
	 * Thread.sleep(2000); } catch (InterruptedException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 * 
	 * if (indice<=maxindice && indice>=minindice) { graqlInsertQuery = "match ";
	 * graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
	 * "Name"); graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue",
	 * "IdValue", "Category"); graqlInsertQuery += InputSingletonDefineQuery("",
	 * "TypeValue", "IdValue", "SubCategory"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("", "TypeValue", "IdValue", "Brand");
	 * graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
	 * "WikiData"); graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue",
	 * "IdValue", "UrlHours"); graqlInsertQuery += InputSingletonDefineQuery("",
	 * "TypeValue", "IdValue", "Infos"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("", "TypeValue", "IdValue", "Status");
	 * graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
	 * "OpeningHours"); graqlInsertQuery += InputSingletonDefineQuery("",
	 * "TypeValue", "IdValue", "Lat"); graqlInsertQuery +=
	 * InputSingletonDefineQuery("", "TypeValue", "IdValue", "Lon");
	 * graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
	 * "Dept");
	 * 
	 * // data graqlInsertQuery+= "insert $Who-Shop-" + indice.toString() +
	 * " isa Shop, has Identity \"" + shops.at("osm_id").asString() + "\";";
	 * 
	 * if (!shops.at("name").isNull()) { String
	 * name=shops.at("name").asString().replace('"', ' '); graqlInsertQuery+=
	 * "$Value-Shop-Name-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SN-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + name + "\";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-Name-" + indice.toString()
	 * +" (value : $Value-Shop-Name-" + indice.toString() + " , resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-Name-" + indice.toString()
	 * +" (value : $Value-Shop-Name-" + indice.toString() +
	 * " , typevalue : $TypeValue-Name) isa TypeValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("category").isNull()) { String category =
	 * shops.at("category").asString(); graqlInsertQuery+= "$Value-Shop-Category"
	 * +" isa StringValue, has IdValue \"SC-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + category + "\";\n"; graqlInsertQuery+=
	 * "$ValueTypeRelation-Shop-Category"
	 * +" (value : $Value-Shop-Category , typevalue : $TypeValue-Category) isa TypeValueShopRelations"
	 * + ";\n"; graqlInsertQuery+= "$ValueRelation-Shop-Category-" +
	 * indice.toString() +" (value : $Value-Shop-Category, resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("subcategory").isNull()) { String subcategory =
	 * shops.at("subcategory").asString();
	 * 
	 * graqlInsertQuery+=
	 * "$Value-Shop-SubCategory isa StringValue, has IdValue \"SSC-" +
	 * indice.toString() + "\", has StringValueAttribute \"" + subcategory +
	 * "\";\n"; graqlInsertQuery+=
	 * "$ValueTypeRelation-Shop-SubCategory (value : $Value-Shop-SubCategory, typevalue : $TypeValue-SubCategory) isa TypeValueShopRelations"
	 * + ";\n";
	 * 
	 * graqlInsertQuery+= "$ValueRelation-Shop-SubCategory-" + indice.toString()
	 * +" (value : $Value-Shop-SubCategory, resource : " + "$Who-Shop-" +
	 * indice.toString() + ") isa ValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("brand").isNull()) { String brand =
	 * shops.at("brand").asString().replace('"', ' ');
	 * 
	 * graqlInsertQuery+= "$Value-Shop-Brand isa StringValue, has IdValue \"SB-" +
	 * indice.toString() + "\", has StringValueAttribute \"" + brand + "\";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-Brand" +
	 * " (value : $Value-Shop-Brand , typevalue : $TypeValue-Brand) isa TypeValueShopRelations"
	 * + ";\n";
	 * 
	 * graqlInsertQuery+= "$ValueRelation-Shop-Brand-" + indice.toString()
	 * +" (value : $Value-Shop-Brand , resource : " + "$Who-Shop-" +
	 * indice.toString() + ") isa ValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("wikidata").isNull()) { String wikidata =
	 * shops.at("wikidata").asString().replace('"', ' ');
	 * 
	 * graqlInsertQuery+= "$Value-Shop-WikiData-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SW-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + wikidata + "\";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-WikiData-" + indice.toString()
	 * +" (value : $Value-Shop-WikiData-" + indice.toString() + " , resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-WikiData-" + indice.toString()
	 * +" (value : $Value-Shop-WikiData-" + indice.toString() +
	 * " , typevalue : $TypeValue-WikiData) isa TypeValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("url_hours").isNull()) { String url_hours =
	 * shops.at("url_hours").asString(); url_hours.replace('"', ' ');
	 * graqlInsertQuery+= "$Value-Shop-UrlHours-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SU-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + url_hours + "\";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-UrlHours-" + indice.toString()
	 * +" (value : $Value-Shop-UrlHours-" + indice.toString() + " , resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-ShopUrlHours-" + indice.toString()
	 * +" (value : $Value-Shop-UrlHours-" + indice.toString() +
	 * " , typevalue : $TypeValue-UrlHours) isa TypeValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("infos").isNull()) { String infos =
	 * shops.at("infos").asString().replace('"', ' ');
	 * 
	 * graqlInsertQuery+= "$Value-Shop-Infos-" + indice.toString()
	 * +" isa StringValue, has IdValue \"SI-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + infos + "\";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-Infos-" + indice.toString()
	 * +" (value : $Value-Shop-Infos-" + indice.toString() + " , resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-Infos-" + indice.toString()
	 * +" (value : $Value-Shop-Infos-" + indice.toString() +
	 * " , typevalue : $TypeValue-Infos) isa TypeValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("status").isNull()) { String status =
	 * shops.at("status").asString();
	 * 
	 * graqlInsertQuery+= "$Value-Shop-Status isa StringValue, has IdValue \"SS-" +
	 * indice.toString() + "\", has StringValueAttribute \"" + status + "\";\n";
	 * graqlInsertQuery+=
	 * "$ValueTypeRelation-Shop-Status (value : $Value-Shop-Status, typevalue : $TypeValue-Status) isa TypeValueShopRelations"
	 * + ";\n"; graqlInsertQuery+= "$ValueRelation-Shop-Status-" + indice.toString()
	 * +" (value : $Value-Shop-Status, resource : " + "$Who-Shop-" +
	 * indice.toString() + ") isa ValueShopRelations" + ";\n"; }
	 * 
	 * if (!shops.at("opening_hours").isNull()) { String opening_hours =
	 * shops.at("opening_hours").asString(); opening_hours.replace('"', ' ');
	 * graqlInsertQuery+= "$Value-Shop-OpeningHours-" + indice.toString() +
	 * " isa StringValue, has IdValue \"SO-" + indice.toString() +
	 * "\", has StringValueAttribute \"" + opening_hours + "\";\n";
	 * graqlInsertQuery+= "$ValueRelation-Shop-OpeningHours-" + indice.toString()
	 * +" (value : $Value-Shop-OpeningHours-" + indice.toString() + " , resource : "
	 * + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-OpeningHours-" +
	 * indice.toString() +" (value : $Value-Shop-OpeningHours-" + indice.toString()
	 * + " , typevalue : $TypeValue-OpeningHours) isa TypeValueShopRelations" +
	 * ";\n"; }
	 * 
	 * if (!shops.at("lon").isNull() && !shops.at("lat").isNull()) { Double lon=0.0;
	 * try { lon=shops.at("lon").asDouble(); graqlInsertQuery+= "$Value-Shop-Lon-" +
	 * indice.toString() +" isa DoubleValue, has IdValue \"SLO-" + indice.toString()
	 * + "\", has DoubleValueAttribute " + lon + ";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-Lon-" + indice.toString() +" (value : $Value-Shop-Lon-"
	 * + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() +
	 * ") isa ValueShopRelations" + ";\n"; graqlInsertQuery+=
	 * "$ValueTypeRelation-Shop-Lon-" + indice.toString()
	 * +" (value : $Value-Shop-Lon-" + indice.toString() +
	 * " , typevalue : $TypeValue-Lon) isa TypeValueShopRelations" + ";\n";
	 * 
	 * 
	 * Double lat=0.0; lat=shops.at("lat").asDouble(); graqlInsertQuery+=
	 * "$Value-Shop-Lat-" + indice.toString()
	 * +" isa DoubleValue, has IdValue \"SLA-" + indice.toString() +
	 * "\", has DoubleValueAttribute " + lat + ";\n"; graqlInsertQuery+=
	 * "$ValueRelation-Shop-Lat-" + indice.toString() +" (value : $Value-Shop-Lat-"
	 * + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() +
	 * ") isa ValueShopRelations" + ";\n"; graqlInsertQuery+=
	 * "$ValueTypeRelation-Shop-Lat-" + indice.toString()
	 * +" (value : $Value-Shop-Lat-" + indice.toString() +
	 * " , typevalue : $TypeValue-Lat) isa TypeValueShopRelations" + ";\n";
	 * 
	 * LocalizationFinder finder = new LocalizationFinder(); String
	 * departement=finder.retrieve(lon, lat,typeinforeverse.reversedepartement);
	 * 
	 * graqlInsertQuery+= "$Value-Shop-Dept isa StringValue, has IdValue \"SD-" +
	 * indice.toString() + "\", has StringValueAttribute \"" + departement+ "\";\n";
	 * graqlInsertQuery+=
	 * "$ValueRelation-Shop-Dept (value : $Value-Shop-Dept, resource : " +
	 * "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
	 * 
	 * graqlInsertQuery+= "$ValueTypeRelation-Shop-Dept-" + indice.toString()
	 * +" (value : $Value-Shop-Dept, typevalue : $TypeValue-Dept) isa TypeValueShopRelations"
	 * + ";\n"; } catch(Exception e) { System.out.println
	 * ("Ligne mal formattée ou incomplète : " + shops.toString()); } } else { int
	 * test=0; } } } return (graqlInsertQuery);
	 * 
	 * } }); return inputs; }
	 */

	static Collection<Input> initialiseInputs(OptionImport optionImport, Collection<Input> inputs) {

		// master data départements
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTDPT)
		{
			String filedpt="data/departement";
			if (optionImport!=null)
			{
				if ( optionImport.getFilename()!=null)
				{
					filedpt=optionImport.getFilename();
				}
			}
			inputdepartements = new InputDepartements(optionImport, filedpt, maxget, dpt_minindice, dpt_maxindice);
			inputs = inputdepartements.initialize(inputs);
		}

		// PCR TESTS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTPCRTEST)
		{
			String filepcr="data/sp-pos-quot-dep-2021-03-23-17h40";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					filepcr=optionImport.getFilename();
				}
			}

			inputpcrtest = new InputPCRTest(optionImport, filepcr, maxget, pcr_minindice, pcr_maxindice,
					pcr_datefilter, pcr_minDate, pcr_maxDate, pcr_mindays, pcr_maxdays);

			inputs = inputpcrtest.initialize(inputs);
		}

		// CONFINEMENTS DEPARTEMENT EVTS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTDPTCOUVREFEU)
		{
			String filedpt="data/departementdecret";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					filedpt=optionImport.getFilename();
				}
			}

			inputdepartementscouvrefeu = new InputDepartementsCouvreFeu(optionImport, filedpt, maxget, dc_minindice,
					dc_maxindice, dc_datefilter, dc_minDate, dc_maxDate,  dc_mindays, dc_maxdays);

			inputs = inputdepartementscouvrefeu.initialize(inputs);

		}

		// CONFINEMENTS SHOP EVTS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTSHOPCONFINEMENT)
		{
			String fileshop="data/shopconfinement";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					fileshop=optionImport.getFilename();
				}
			}

			inputshopsconfinement = new InputShopsConfinement(optionImport, fileshop, maxget, sc_minindice, sc_maxindice,
					sc_minDate, sc_maxDate, sc_mindays, sc_maxdays);
			;

			inputs = inputshopsconfinement.initialize(inputs);
		}
		// weather
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTWEATHER)
		{
			String fileweather="data/temperature-quotidienne-departementale";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					fileweather=optionImport.getFilename();
				}
			}

			inputweather = new InputWeather(optionImport, fileweather, maxget, w_minindice, w_maxindice,
					w_datefilter, w_minDate, w_maxDate, w_mindays, w_maxdays);

			inputs = inputweather.initialize(inputs);
		}

		// SHOPS / DEPARTMENT

		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTSHOP)
		{
			String fileshops="data/poi_osm_FR";
			if (optionImport!=null) 
			{
				if (optionImport.getFilename()!=null)
				{
					fileshops=optionImport.getFilename();
				}
			}

			inputshops = new InputShops(optionImport, fileshops, maxget, shop_minindice, shop_maxindice);

			// inputs=inputshops.initialize(inputs);
		}
		return inputs;
	}

	static Collection<Report> initialiseReports(OptionReport optionReport, Collection<Report> reports) {

		// master data départements
		if (optionReport==null || optionReport.getTypeReport()==TypeReport.REPORTPCRTEST)
		{
			String filereportpcrtests="data/reportpcrtests";
			if (optionReport!=null)
			{
				if ( optionReport.getFilename()!=null)
				{
					filereportpcrtests="data/"+optionReport.getFilename();
				}
			}
			reportpcrtests = new ReportPCRTest(filereportpcrtests, optionReport);
			reports = reportpcrtests.initialize(reports);
		}

		return reports;
	}

	static String removetrailinglinebreak(String query) {
		int len = query.length();

		while (len >= 1 & query.charAt(len - 1) == '\n') {
			query = query.substring(0, len - 1);
			len--;
		}
		return query;
	}

	static void loadDataIntoGrakn(Input input, GraknClient.Session session) throws FileNotFoundException {

		if (input.getDataPath().equals(""))

		{
			try {
				GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
				String graqlQuery = ((InputSingleton) (input)).template();
				System.out.println("Executing Graql Query: " + graqlQuery);

				if (input.getIsDefine())
					transaction.execute((GraqlDefine) parse(graqlQuery));
				else
					transaction.execute((GraqlInsert) parse(graqlQuery));
				transaction.commit();
				System.out.println("\nInserted Data Singleton into Grakn.\n");
			} catch (GraknClientException e) {
				System.out.println(e);
				if (e.getMessage().contains("There is more than one thing")
						&& e.getMessage().contains("that owns the key")) {
					System.out.println("rest of the code...");
				} else {
					throw (e);
				}
			}
		} else {
			ArrayList<Json> items = InputSetOfData.parseDataToJson(input);
			boolean close = true;
			int nb = 0;
			GraknClient.Transaction transaction = null;
			for (Json item : items) {
				try {
					String graqlInsertQuery = input.template(item);

					if (graqlInsertQuery.equals("") == false) {
						nb++;
						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}
						// GraknClient.Transaction transaction =
						// session.transaction(GraknClient.Transaction.Type.WRITE);
						System.out.println("Executing Graql Query: " + graqlInsertQuery);
						QueryFuture<List<ConceptMap>> list = transaction.execute((GraqlInsert) parse(graqlInsertQuery));
						if (nb % maxcommit == 0) {
							transaction.commit();
							close = true;
						}

					}
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
			System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + "] into Grakn.\n");
			if (close == false) {
				try {
					transaction.commit();
				} catch (GraknClientException e) {
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

	static void reportDataFromGrakn(Report report, GraknClient.Session session) throws FileNotFoundException {

		try {

			JsonArray items = null;
			if (report.getOptionReport().isOffline()==false)
			{
				GraknClient.Transaction transaction  = session.transaction(GraknClient.Transaction.Type.WRITE);

				report.initializeCaches(session);

				// extract data for report
				items = report.queryDataToJson(transaction);

				// generate a CSV File
				report.convertJsonToCSV(items,"");
			}
			else
			{
				items = report.parseCSVToJson("");
			}
			//JsonArray filteredItems =items=report.filterDatas(items);

			// identify frequencies
			JsonArray frequencies=report.fourierTransform(items, report.getReportAccelerationName());
			report.convertJsonToCSV(frequencies,"-A-f");

			// filter frequencies
			JsonArray filteredfrequencies=report.filterFrequencies(frequencies, TypeValue.FREQUENCY, false);
			report.convertJsonToCSV(filteredfrequencies,"-A-ff");

			// return to temporal space
			JsonArray filteredtemporals=report.inverseFourierTransform(filteredfrequencies, TypeValue.AMPLITUDE);
			report.convertJsonToCSV(filteredtemporals,"-A-ff-temp");

			// substract frequencies
			JsonArray temporals=report.extractValues(items, report.getReportAccelerationName());
			JsonArray filteredtemporals2=report.substractValues(temporals, filteredtemporals);
			report.convertJsonToCSV(filteredtemporals2,"-A-ff-temp2");
			
		}
		catch (GraknClientException e) {
			System.out.println(e);

			throw (e);
		}


	}


	/*
	 * static // Post calculation set String PCRPostCalculationQuerySet(String
	 * nametag, String valnum1, String set) {
	 * 
	 * 
	 * String graqlInsertQuery = insertValue (PCRTestMetaTypeValue,nametag, valnum1,
	 * "StringValue", set, "$resource", PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * 
	 * return graqlInsertQuery; }
	 */
	/*
	 * static // Post calculation set String PCRPostCalculationQuerySet(String
	 * valnum1, String set) {
	 * 
	 * //String graqlInsertQuery = insertValue (PCRTestTypeValue_PPOSTCALCUL,
	 * valnum1, "StringValue", set, "$resource",
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * String graqlInsertQuery
	 * =PCRPostCalculationQuerySet(PCRTestTypeValue_PPOSTCALCUL, valnum1, set);
	 * 
	 * return graqlInsertQuery; }
	 */
	/*
	 * static // Query specific value QueriedAttribute
	 * QueryAttributeValue(GraknClient.Transaction transaction, Entity value) {
	 * 
	 * String entitytype= value.type().label().getValue();
	 * 
	 * String graqlQueryValue = "match $value id "+ value.id() +
	 * ", has TypeValueId $typevalueid, has "+ entitytype
	 * +"Attribute $attribute ; get;\n";
	 * 
	 * 
	 * 
	 * //System.out.println(graqlQueryValue);
	 * 
	 * QueryFuture<List<ConceptMap>> mapQueryValue =
	 * transaction.execute((GraqlGet)parse(graqlQueryValue)); List<ConceptMap>
	 * answersQueryValue= mapQueryValue.get();
	 * 
	 * for(ConceptMap answerQueryValue : answersQueryValue) { Object objectvalue =
	 * answerQueryValue.get("attribute").asAttribute().value();
	 * //System.out.println(objectvalue);
	 * 
	 * String valuetypeid =
	 * (String)answerQueryValue.get("typevalueid").asAttribute().value();
	 * //System.out.println(valuetypeid);
	 * 
	 * QueriedAttribute queriedattribute=new QueriedAttribute(valuetypeid,
	 * objectvalue);
	 * 
	 * 
	 * return queriedattribute; } return null; }
	 */
	/*
	 * 
	 * static // Post calculation delete QueryFuture<? extends List<? extends
	 * Answer>> PostCalculationQueryDelete(GraknClient.Transaction transaction,
	 * String idvaluepostcalculvalue1) {
	 * 
	 * // delete old post calcul GraqlDelete deletequery = match(
	 * var("value").isa("Value").has("IdValue", idvaluepostcalculvalue1))
	 * .delete(var("value").isa("Value"));
	 * 
	 * 
	 * QueryFuture<? extends List<? extends Answer>> map61 =
	 * transaction.execute(deletequery);
	 * 
	 * return map61;
	 * 
	 * }
	 */
	/*
	 * static // Post calculation delete QueryFuture<? extends List<? extends
	 * Answer>> PostCalculationQueryDeleteById(GraknClient.Transaction transaction,
	 * String idpostcalculvalue1) {
	 * 
	 * // delete old post calcul GraqlDelete deletequery = match(
	 * var("value").id(idpostcalculvalue1)) .delete(var("value").isa("Value"));
	 * 
	 * 
	 * QueryFuture<? extends List<? extends Answer>> map61 =
	 * transaction.execute(deletequery);
	 * 
	 * return map61;
	 * 
	 * }
	 */

	/*
	 * // Post calculation of derivated (acceleration) static void
	 * PostAccelerationCalculation(GraknClient.Session session) {
	 * 
	 * 
	 * try { Boolean okanswers=true; int nb=0;
	 * 
	 * GraknClient.Transaction transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE);
	 * 
	 * while (okanswers==true) { okanswers=false;
	 * 
	 * String graqlQuery11 = "match $valuepostcalcul isa Value" +
	 * ", has TypeValueId \"" + PCRTestTypeValue_PPOSTCALCUL +"\"" +
	 * ", has StringValueAttribute \"" + TagSpeedPostCalculate+ "\";";
	 * graqlQuery11+= "get;limit "+ maxget + ";\n";
	 * System.out.println(graqlQuery11);
	 * 
	 * QueryFuture<List<ConceptMap>> map11 =
	 * transaction.execute((GraqlGet)parse(graqlQuery11)); List<ConceptMap>
	 * answers11= map11.get();
	 * 
	 * 
	 * for(ConceptMap answer11:answers11) { okanswers=true;
	 * 
	 * System.out.println(answer11.toString());
	 * 
	 * Entity value1= answer11.get("valuepostcalcul").asEntity(); String
	 * idpostcalculvalue1=value1.id().toString();
	 * 
	 * String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1+";";
	 * graqlQuery12+=
	 * "(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
	 * graqlQuery12+=
	 * "(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery12+=
	 * "$resource isa Resource, has Identity $identity, has Attribut-"+
	 * PCRTestTypeValue_cl_age90+"$cl_age90;"; graqlQuery12+=
	 * "$Departement isa Departement, has CodeGLN $codeGLN;"; graqlQuery12+=
	 * "$timedate isa TimeDate, has EventDate $attributedate; get;limit ";
	 * graqlQuery12+= maxget + ";\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map12 =
	 * transaction.execute((GraqlGet)parse(graqlQuery12)); List<ConceptMap>
	 * answers12= map12.get();
	 * 
	 * for(ConceptMap answer12:answers12) { LocalDateTime valuedate1 =
	 * (LocalDateTime) answer12.get("attributedate").asAttribute().value();
	 * System.out.println(valuedate1);
	 * 
	 * String identity1 = (String) answer12.get("identity").asAttribute().value();
	 * System.out.println(identity1);
	 * 
	 * String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
	 * System.out.println(codeGLN);
	 * 
	 * Long cl_age90 = (Long) answer12.get("cl_age90").asAttribute().value();
	 * System.out.println(cl_age90);
	 * 
	 * int posnum=identity1.lastIndexOf('-'); String
	 * valnum1=identity1.substring(posnum+1);
	 * 
	 * String graqlQuery2 = "match $resource isa Resource, has Identity \""+
	 * identity1+ "\";"; graqlQuery2+=
	 * "(value : $value, resource : $resource) isa ValueRelation; get;\n";
	 * //graqlQuery2+= "$value isa Value, has TypeValueId $typevalueid; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map2 =
	 * transaction.execute((GraqlGet)parse(graqlQuery2)); List<ConceptMap> answers2=
	 * map2.get();
	 * 
	 * //System.out.println(answers2.toString());
	 * 
	 * Double valueVPdivT1=0.0; Long valueattributecl_age90=(long) 0;
	 * 
	 * for(ConceptMap answer2:answers2) { Entity value=
	 * answer2.get("value").asEntity();
	 * 
	 * QueriedAttribute queriedattribute=QueryAttributeValue(transaction, value);
	 * 
	 * if (queriedattribute.getTypeValue().compareTo(PCRTestTypeValue_VPdivT)==0) {
	 * valueVPdivT1 = (Double) queriedattribute.getValue(); }
	 * 
	 * if (queriedattribute.getTypeValue().compareTo(PCRTestTypeValue_cl_age90)==0)
	 * { valueattributecl_age90 = (Long) queriedattribute.getValue(); } } Boolean
	 * nextdate=false; int plusdays=incdays; while (nextdate==false && plusdays <
	 * maxdays) { LocalDateTime valuedate2=valuedate1.plusDays(plusdays);
	 * 
	 * String graqlQuery31 = "match $Departement isa Departement, has CodeGLN "+
	 * codeGLN + ";"; graqlQuery31+= "$timedate isa TimeDate, has EventDate " +
	 * valuedate2 + ";"; graqlQuery31+=
	 * "(registeredevent : $event, time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery31+= "$resource isa "+ PCRTestClass_WhatPCRTest +
	 * ", has Attribut-"+ PCRTestTypeValue_cl_age90 + " " + cl_age90 + ";get;\n";
	 * //graqlQuery3+=
	 * "(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
	 * //graqlQuery3+= "$valuecl_age90 isa LongValue, has LongValueAttribute "+
	 * valueattributecl_age90 + ", has TypeValueId \""+ PCRTestTypeValue_cl_age90 +
	 * "\";get;\n"; //graqlQuery3+=
	 * "$resource isa Resource, has Identity $identity; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map31 =
	 * transaction.execute((GraqlGet)parse(graqlQuery31)); List<ConceptMap>
	 * answers31= map31.get();
	 * 
	 * if (answers31.isEmpty()==false) { for(ConceptMap answer31:answers31) {
	 * //System.out.println(answer31.toString()); Entity resource31=
	 * answer31.get("resource").asEntity();
	 * 
	 * String graqlQuery5 = "match $resource id "+ resource31.id()+ ";";
	 * graqlQuery5+=
	 * "(value : $valueVPdivT, resource : $resource) isa ValueRelation;";
	 * graqlQuery5+=
	 * "$valueVPdivT isa DoubleValue, has DoubleValueAttribute $attributeVPdivT, has TypeValueId \""
	 * + PCRTestTypeValue_VPdivT + "\"; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map5 =
	 * transaction.execute((GraqlGet)parse(graqlQuery5)); List<ConceptMap> answers5=
	 * map5.get();
	 * 
	 * //System.out.println(answers5.toString());
	 * 
	 * for(ConceptMap answer5:answers5) { Double valueVPdivT2 = (Double)
	 * answer5.get("attributeVPdivT").asAttribute().value();
	 * //System.out.println(valuePdivT2);
	 * 
	 * 
	 * Double accelerationPdivT=(valueVPdivT2-valueVPdivT1)/plusdays;
	 * 
	 * // delete old post calcul QueryFuture<? extends List<? extends Answer>> map61
	 * = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * // insertion des valeurs String graqlInsertQuery =
	 * "match $resource isa Resource, has Identity \""+ identity1+ "\";";
	 * graqlInsertQuery+= "$TypeValue-ACPdivT isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_ACPdivT + "\";"; graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_PPOSTCALCUL +"\";";
	 * 
	 * graqlInsertQuery+= "insert "; Locale locale = new Locale("en", "UK");
	 * DecimalFormat df = new DecimalFormat("#.####################", new
	 * DecimalFormatSymbols(locale)); String
	 * saccelerationPdivT=df.format(accelerationPdivT); graqlInsertQuery +=
	 * insertValue (PCRTestMetaTypeValue, PCRTestTypeValue_ACPdivT, valnum1,
	 * "DoubleValue", saccelerationPdivT, "$resource",
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * // update de postcalcul graqlInsertQuery +=
	 * PCRPostCalculationQuerySet(valnum1, TagAccelerationPostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * System.out.println(accelerationPdivT);
	 * System.out.println(answers7.toString());
	 * 
	 * 
	 * nextdate=true; nb++; System.out.println("PostCalculated : " + nb); } //} //}
	 * } plusdays=plusdays+incdays; } else { plusdays=plusdays+incdays; } } // post
	 * calculation not possible if (plusdays>=maxdays) { // delete old post calcul
	 * QueryFuture<? extends List<? extends Answer>> map61 =
	 * PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * // insert of postcalculate String graqlInsertQuery =
	 * "match $resource isa Resource, has Identity \""+ identity1+ "\";";
	 * graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \"PPOSTCALCUL\";";
	 * 
	 * graqlInsertQuery+= "insert ";
	 * 
	 * // update de postcalcul graqlInsertQuery+=
	 * PCRPostCalculationQuerySet(valnum1, TagNotPostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * System.out.println(answers7.toString());
	 * 
	 * nb++; System.out.println("PostCalculated : " + nb); } } }
	 * transaction.commit(); transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); }
	 * transaction.commit();
	 * System.out.println("PostCalculation Acceleration OK.\n");
	 * 
	 * } catch(GraknClientException e){ System.out.println(e); throw(e); } }
	 */
	/*
	 * // Post calculation of derivated (speed) static void
	 * ReinitPostCalculationTag(GraknClient.Session session, String nametag, String
	 * previousvaluetag, String newvaluetag) {
	 * 
	 * 
	 * try { Boolean okanswers = true; int nb=0;
	 * 
	 * GraknClient.Transaction transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE);
	 * 
	 * while (okanswers==true) { okanswers=false;
	 * 
	 * 
	 * String graqlQuery11 = "match $valuepostcalcul isa Value" +
	 * ", has IdValue $idvalue" + ", has TypeValueId \"" + nametag +"\"" +
	 * ", has StringValueAttribute \"" + previousvaluetag+ "\";"; graqlQuery11+=
	 * "get;limit "+ maxget + ";\n"; System.out.println(graqlQuery11);
	 * 
	 * QueryFuture<List<ConceptMap>> map11 =
	 * transaction.execute((GraqlGet)parse(graqlQuery11)); List<ConceptMap>
	 * answers11= map11.get();
	 * 
	 * for(ConceptMap answer11:answers11) { okanswers = true;
	 * 
	 * System.out.println(answer11.toString());
	 * 
	 * Entity value1= answer11.get("valuepostcalcul").asEntity(); String
	 * idpostcalculvalue1=value1.id().toString();
	 * 
	 * String idvalue1 = (String) answer11.get("idvalue").asAttribute().value();
	 * System.out.println(idvalue1);
	 * 
	 * int posnum=idvalue1.indexOf('-'); String
	 * valnum1=idvalue1.substring(posnum+1);
	 * 
	 * String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1+ ";";
	 * graqlQuery12+=
	 * "(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
	 * graqlQuery12+= maxget + ";\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map12 =
	 * transaction.execute((GraqlGet)parse(graqlQuery12)); List<ConceptMap>
	 * answers12= map12.get();
	 * 
	 * for(ConceptMap answer12:answers12) {
	 * 
	 * Entity resource= answer12.get("resource").asEntity();
	 * 
	 * // delete old post calcul QueryFuture<? extends List<? extends Answer>> map61
	 * = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * 
	 * // insertion des valeurs String graqlInsertQuery = "match $resource id "+
	 * resource.id().toString()+ ";"; graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ nametag +"\";";
	 * 
	 * graqlInsertQuery+= "insert ";
	 * 
	 * // update de postcalcul graqlInsertQuery +=
	 * PCRPostCalculationQuerySet(valnum1, newvaluetag);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * System.out.println(answers7.toString());
	 * 
	 * nb++; System.out.println("Reinit : " + nb); }// for answer12 } // for
	 * answer11 transaction.commit(); transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); } // while ok
	 * 
	 * transaction.commit(); System.out.println("Reinit " + nametag + "OK.\n"); }
	 * catch(GraknClientException e){ System.out.println(e); throw(e); } }
	 */
	/*
	 * // Post calculation of derivated (speed) static void
	 * PostSpeedCalculation(GraknClient.Session session) {
	 * 
	 * 
	 * try { Boolean okanswers = true; int nb=0;
	 * 
	 * GraknClient.Transaction transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE);
	 * 
	 * while (okanswers==true) { okanswers=false;
	 * 
	 * String graqlQuery11 = "match $valuepostcalcul isa Value" +
	 * ", has TypeValueId \"" + PCRTestTypeValue_PPOSTCALCUL +"\"" +
	 * ", has StringValueAttribute \"" + TagAveragePostCalculate+ "\";";
	 * graqlQuery11+= "get;limit "+ maxget + ";\n";
	 * System.out.println(graqlQuery11);
	 * 
	 * QueryFuture<List<ConceptMap>> map11 =
	 * transaction.execute((GraqlGet)parse(graqlQuery11)); List<ConceptMap>
	 * answers11= map11.get();
	 * 
	 * for(ConceptMap answer11:answers11) { okanswers = true;
	 * 
	 * System.out.println(answer11.toString());
	 * 
	 * Entity value1= answer11.get("valuepostcalcul").asEntity(); String
	 * idpostcalculvalue1=value1.id().toString();
	 * 
	 * String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1 +
	 * ";"; graqlQuery12+=
	 * "(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
	 * graqlQuery12+=
	 * "(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery12+=
	 * "$resource isa Resource, has Identity $identity, has Attribut-"+
	 * PCRTestTypeValue_cl_age90+"$cl_age90;"; graqlQuery12+=
	 * "$Departement isa Departement, has CodeGLN $codeGLN;"; graqlQuery12+=
	 * "$timedate isa TimeDate, has EventDate $attributedate; get;limit ";
	 * graqlQuery12+= maxget + ";\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map12 =
	 * transaction.execute((GraqlGet)parse(graqlQuery12)); List<ConceptMap>
	 * answers12= map12.get();
	 * 
	 * for(ConceptMap answer12:answers12) { LocalDateTime valuedate1 =
	 * (LocalDateTime) answer12.get("attributedate").asAttribute().value();
	 * System.out.println(valuedate1);
	 * 
	 * String identity1 = (String) answer12.get("identity").asAttribute().value();
	 * System.out.println(identity1);
	 * 
	 * String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
	 * System.out.println(codeGLN);
	 * 
	 * Long cl_age90 = (Long) answer12.get("cl_age90").asAttribute().value();
	 * System.out.println(cl_age90);
	 * 
	 * int posnum=identity1.lastIndexOf('-'); String
	 * valnum1=identity1.substring(posnum+1);
	 * 
	 * String graqlQuery2 = "match $resource isa Resource, has Identity \""+
	 * identity1+ "\";"; graqlQuery2+=
	 * "(value : $value, resource : $resource) isa ValueRelation; get;\n";
	 * 
	 * //System.out.println(graqlQuery2);
	 * 
	 * QueryFuture<List<ConceptMap>> map2 =
	 * transaction.execute((GraqlGet)parse(graqlQuery2)); List<ConceptMap> answers2=
	 * map2.get();
	 * 
	 * //System.out.println(answers2.toString());
	 * 
	 * Double valueAVPdivT1=0.0; Long valueattributecl_age90=(long) 0;
	 * 
	 * for(ConceptMap answer2:answers2) { Entity value=
	 * answer2.get("value").asEntity();
	 * 
	 * QueriedAttribute queriedattribute=QueryAttributeValue(transaction, value);
	 * 
	 * if (queriedattribute.getTypeValue().compareTo(PCRTestTypeValue_AVPdivT)==0) {
	 * valueAVPdivT1 = (Double) queriedattribute.getValue(); }
	 * 
	 * if (queriedattribute.getTypeValue().compareTo(PCRTestTypeValue_cl_age90)==0)
	 * { valueattributecl_age90 = (Long) queriedattribute.getValue(); } } Boolean
	 * nextdate=false; int plusdays=incdays; while (nextdate==false && plusdays <
	 * maxdays) { LocalDateTime valuedate2=valuedate1.plusDays(plusdays);
	 * 
	 * String graqlQuery31 = "match $Departement isa Departement, has CodeGLN "+
	 * codeGLN + ";"; graqlQuery31+= "$timedate isa TimeDate, has EventDate " +
	 * valuedate2 + ";"; graqlQuery31+=
	 * "(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery31+= "$resource isa "+ PCRTestClass_WhatPCRTest +
	 * ", has Attribut-"+ PCRTestTypeValue_cl_age90 + " " + cl_age90 + ";get;\n";
	 * //graqlQuery3+=
	 * "(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
	 * //graqlQuery3+= "$valuecl_age90 isa LongValue, has LongValueAttribute "+
	 * valueattributecl_age90 + ", has TypeValueId \""+ PCRTestTypeValue_cl_age90 +
	 * "\";get;\n"; //graqlQuery3+=
	 * "$resource isa Resource, has Identity $identity; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map31 =
	 * transaction.execute((GraqlGet)parse(graqlQuery31)); List<ConceptMap>
	 * answers31= map31.get();
	 * 
	 * if (answers31.isEmpty()==false) { for(ConceptMap answer31:answers31) {
	 * //System.out.println(answer31.toString()); Entity resource31=
	 * answer31.get("resource").asEntity();
	 * 
	 * String graqlQuery5 = "match $resource id "+ resource31.id()+ ";";
	 * graqlQuery5+=
	 * "(value : $valueAVPdivT, resource : $resource) isa ValueRelation;";
	 * graqlQuery5+=
	 * "$valueAVPdivT isa DoubleValue, has DoubleValueAttribute $attributeAVPdivT, has TypeValueId \""
	 * + PCRTestTypeValue_AVPdivT + "\"; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map5 =
	 * transaction.execute((GraqlGet)parse(graqlQuery5)); List<ConceptMap> answers5=
	 * map5.get();
	 * 
	 * //System.out.println(answers5.toString());
	 * 
	 * for(ConceptMap answer5:answers5) { Double valueAVPdivT2 = (Double)
	 * answer5.get("attributeAVPdivT").asAttribute().value();
	 * //System.out.println(valuePdivT2);
	 * 
	 * 
	 * Double speedPdivT=(valueAVPdivT2-valueAVPdivT1)/plusdays;
	 * 
	 * // delete old post calcul QueryFuture<? extends List<? extends Answer>> map61
	 * = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * 
	 * // insertion des valeurs String graqlInsertQuery =
	 * "match $resource isa Resource, has Identity \""+ identity1+ "\";";
	 * graqlInsertQuery+= "$TypeValue-VPdivT isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_VPdivT + "\";"; graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_PPOSTCALCUL +"\";";
	 * 
	 * graqlInsertQuery+= "insert "; Locale locale = new Locale("en", "UK");
	 * DecimalFormat df = new DecimalFormat("#.####################", new
	 * DecimalFormatSymbols(locale)); String sspeedPdivT=df.format(speedPdivT);
	 * graqlInsertQuery += insertValue (PCRTestMetaTypeValue,
	 * PCRTestTypeValue_VPdivT, valnum1, "DoubleValue", sspeedPdivT, "$resource",
	 * PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * // update de postcalcul graqlInsertQuery +=
	 * PCRPostCalculationQuerySet(valnum1, TagSpeedPostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * System.out.println(answers7.toString()); System.out.println(speedPdivT);
	 * 
	 * nextdate=true; nb++; System.out.println("PostCalculated : " + nb); } }
	 * plusdays=plusdays+incdays; } //else else { plusdays=plusdays+incdays; } } //
	 * while if (plusdays>=maxdays) { // delete old post calcul QueryFuture<?
	 * extends List<? extends Answer>> map61 =
	 * PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * // insert of postcalculate String graqlInsertQuery =
	 * "match $resource isa Resource, has Identity \""+ identity1+ "\";";
	 * graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \"PPOSTCALCUL\";";
	 * 
	 * graqlInsertQuery+= "insert ";
	 * 
	 * // update de postcalcul graqlInsertQuery+=
	 * PCRPostCalculationQuerySet(valnum1, TagNotPostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * System.out.println(answers7.toString());
	 * 
	 * nb++; System.out.println("PostCalculated : " + nb);
	 * 
	 * } // while nextdate } // for answer1
	 * 
	 * } transaction.commit(); transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); } // while ok
	 * 
	 * transaction.commit(); System.out.println("PostCalculation Speed OK.\n"); }
	 * catch(GraknClientException e){ System.out.println(e); throw(e); } }
	 */
	/*
	 * // Post calculation of precedents dated events static void
	 * PCRTestPrecedentEventDateLinkInsertion(GraknClient.Transaction transaction,
	 * Entity timedateevent, Entity datestart, Entity dateend, String
	 * idpcrperiofoftime) {
	 * 
	 * String graqlInsertQuery = "match ";
	 * 
	 * graqlInsertQuery+= "$dateprec id " + timedateevent.id().toString() + ";";
	 * graqlInsertQuery+= "$datestart id " + datestart.id().toString() + ";";
	 * graqlInsertQuery+= "$dateend id " + dateend.id().toString() + ";";
	 * 
	 * graqlInsertQuery+= "insert"; graqlInsertQuery+= "$PrecedentDateEventLink-" +
	 * idpcrperiofoftime;
	 * 
	 * 
	 * graqlInsertQuery+=
	 * " (relativedate : $dateprec, belowdate : $datestart, abovedate : $dateend) isa DatesBetweenRelation ;\n"
	 * ;
	 * 
	 * QueryFuture<List<ConceptMap>> map3 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers3= map3.get();
	 * 
	 * System.out.println(answers3.toString());
	 * 
	 * 
	 * 
	 * }
	 */

	/*
	 * // Post calculation of precedents dated events static void
	 * PCRTestPrecedentEventsCalculation(GraknClient.Session session) {
	 * 
	 * 
	 * try { Boolean okanswers = true;
	 * 
	 * GraknClient.Transaction transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); int nb=0;
	 * 
	 * while (okanswers==true && nb<maxindice) { okanswers=false;
	 * 
	 * String graqlQuery1 = "match $valuepostcalcul isa Value" +
	 * ", has TypeValueId \"" + PCRTestTypeValue_PPOSTCALCULLINKS +"\"" +
	 * ", has StringValueAttribute \"" + TagInitPostCalculate+ "\"" +
	 * ", has IdValue $identity;";
	 * 
	 * graqlQuery1 += "(resource : $pcrperiodoftime, value : $valuepostcalcul) isa "
	 * + PCRTestClass_ValueEventPCRTestRelations+ ";"; graqlQuery1+= "get;limit "+
	 * maxget + ";\n";
	 * 
	 * System.out.println(graqlQuery1);
	 * 
	 * QueryFuture<List<ConceptMap>> map1 =
	 * transaction.execute((GraqlGet)parse(graqlQuery1)); List<ConceptMap> answers1=
	 * map1.get();
	 * 
	 * for(ConceptMap answer1:answers1) { okanswers = true;
	 * 
	 * // System.out.println(answer1.toString());
	 * 
	 * Entity pcrperioftime= answer1.get("pcrperiodoftime").asEntity(); String
	 * idpcrperiofoftime=pcrperioftime.id().toString();
	 * 
	 * String identityPost = (String) answer1.get("identity").asAttribute().value();
	 * System.out.println(identityPost);
	 * 
	 * int posnum=identityPost.lastIndexOf('-'); String
	 * valnumPoT=identityPost.substring(posnum+1);
	 * 
	 * Entity postcalculvalue1= answer1.get("valuepostcalcul").asEntity(); String
	 * idpostcalculvalue1=postcalculvalue1.id().toString();
	 * 
	 * 
	 * String graqlQuery12 = "match $pcrperiodoftime id " + idpcrperiofoftime + ";";
	 * graqlQuery12+=
	 * "(periodoftime : $pcrperiodoftime, startwhendate : $timedate-n, endwhendate : $timedate) isa PeriodicRelation;"
	 * ; graqlQuery12+= "$timedate isa TimeDate, has EventDate $attributedate;";
	 * graqlQuery12+= "$timedate-n isa TimeDate, has EventDate $attributedate-n;";
	 * graqlQuery12+=
	 * "(time : $timedate, localization : $departement) isa EventPCRTestRelations;";
	 * graqlQuery12+= "$departement isa Departement, has CodeGLN $codeGLN;";
	 * graqlQuery12+= "get;limit " + maxget + ";\n";
	 * 
	 * System.out.println(graqlQuery12);
	 * 
	 * QueryFuture<List<ConceptMap>> map12 =
	 * transaction.execute((GraqlGet)parse(graqlQuery12)); List<ConceptMap>
	 * answers12= map12.get();
	 * 
	 * for(ConceptMap answer12:answers12) { LocalDateTime valuedatestart =
	 * (LocalDateTime) answer12.get("attributedate-n").asAttribute().value();
	 * System.out.println(valuedatestart);
	 * 
	 * Entity datestart= answer12.get("timedate-n").asEntity();
	 * 
	 * LocalDateTime valuedateend = (LocalDateTime)
	 * answer12.get("attributedate").asAttribute().value();
	 * System.out.println(valuedateend); Entity dateend=
	 * answer12.get("timedate").asEntity();
	 * 
	 * String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
	 * System.out.println("codeGLN : " + codeGLN);
	 * 
	 * // first list with periodic relation ////////////////////////////////////
	 * String graqlQuery21 =
	 * "match $timedateevent isa TimeDate, has EventDate $dateprec;"; graqlQuery21+=
	 * "$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
	 * graqlQuery21+=
	 * "$periodicrelation (periodoftime : $period, startwhendate : $timedateevent) isa PeriodicRelation;"
	 * ; graqlQuery21+=
	 * "(time : $period, localization : $Localization) isa eventrelations;";
	 * graqlQuery21+= "{$Localization isa Departement, has CodeGLN \"" + codeGLN
	 * +"\";}"; graqlQuery21+=
	 * " or {$Localization isa Country, has CodeGLN \"France\";};"; graqlQuery21+=
	 * "get;\n";
	 * 
	 * // System.out.println(graqlQuery21);
	 * 
	 * QueryFuture<List<ConceptMap>> map21 =
	 * transaction.execute((GraqlGet)parse(graqlQuery21)); List<ConceptMap>
	 * answers21= map21.get();
	 * 
	 * 
	 * 
	 * // System.out.println(answers21.toString());
	 * 
	 * 
	 * for(ConceptMap answer21:answers21) { Entity timedateevent=
	 * answer21.get("timedateevent").asEntity();
	 * 
	 * EntityType type =timedateevent.type(); String stype=type.label().getValue();
	 * 
	 * if (stype.equals("PCRTimeDate")==false) { // a poursuivre création des liens
	 * entre les dates // ajout du lien de postcalculation
	 * PCRTestPrecedentEventDateLinkInsertion(transaction, timedateevent, datestart,
	 * dateend, idpcrperiofoftime); } }
	 * 
	 * //2nd list String graqlQuery22 =
	 * "match $timedateevent isa TimeDate, has EventDate $dateprec;"; graqlQuery22+=
	 * "$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
	 * graqlQuery22+=
	 * "$periodicrelation (periodoftime : $period, endwhendate : $timedateevent) isa PeriodicRelation;"
	 * ; graqlQuery22+=
	 * "(time : $period, localization : $Localization) isa eventrelations;";
	 * graqlQuery22+= "{$Localization isa Departement, has CodeGLN \"" + codeGLN
	 * +"\";}"; graqlQuery22+=
	 * " or {$Localization isa Country, has CodeGLN \"France\";};"; graqlQuery22+=
	 * "get;\n";
	 * 
	 * // System.out.println(graqlQuery22);
	 * 
	 * QueryFuture<List<ConceptMap>> map22 =
	 * transaction.execute((GraqlGet)parse(graqlQuery22)); List<ConceptMap>
	 * answers22= map22.get();
	 * 
	 * // System.out.println(answers22.toString());
	 * 
	 * for(ConceptMap answer22:answers22) { Entity timedateevent=
	 * answer22.get("timedateevent").asEntity();
	 * 
	 * EntityType type =timedateevent.type(); String stype=type.label().getValue();
	 * 
	 * if (stype.equals("PCRTimeDate")==false) { // a poursuivre création des liens
	 * entre les dates // ajout du lien de postcalculation
	 * PCRTestPrecedentEventDateLinkInsertion(transaction, timedateevent, datestart,
	 * dateend, idpcrperiofoftime); }
	 * 
	 * }
	 * 
	 * //3rd list String graqlQuery23 =
	 * "match $timedateevent isa TimeDate, has EventDate $dateprec;"; graqlQuery23+=
	 * "$dateprec >" + valuedatestart + ";$dateprec<"+ valuedateend + ";";
	 * graqlQuery23+=
	 * "(time : $timedateevent, localization : $Localization) isa eventrelations;";
	 * graqlQuery23+= "{$Localization isa Departement, has CodeGLN \"" + codeGLN
	 * +"\";}"; graqlQuery23+=
	 * " or {$Localization isa Country, has CodeGLN \"France\";};"; graqlQuery23+=
	 * "get;\n";
	 * 
	 * // System.out.println(graqlQuery23);
	 * 
	 * QueryFuture<List<ConceptMap>> map23 =
	 * transaction.execute((GraqlGet)parse(graqlQuery23)); List<ConceptMap>
	 * answers23= map23.get();
	 * 
	 * // System.out.println(answers23.toString());
	 * 
	 * for(ConceptMap answer23:answers23) { Entity timedateevent=
	 * answer23.get("timedateevent").asEntity();
	 * 
	 * EntityType type =timedateevent.type(); String stype=type.label().getValue();
	 * 
	 * if (stype.equals("PCRTimeDate")==false) { // a poursuivre création des liens
	 * entre les dates // ajout du lien de postcalculation
	 * PCRTestPrecedentEventDateLinkInsertion(transaction, timedateevent, datestart,
	 * dateend, idpcrperiofoftime); } } nb++; System.out.println("PostCalculated : "
	 * + nb);
	 * 
	 * // delete old post calcul QueryFuture<? extends List<? extends Answer>> map61
	 * = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * // insertion des valeurs String graqlInsertQuery = "match $resource id " +
	 * idpcrperiofoftime + ";"; graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCULLINKS isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_PPOSTCALCULLINKS +"\";";
	 * 
	 * graqlInsertQuery+= "insert ";
	 * 
	 * // update de postcalcul graqlInsertQuery +=
	 * PCRPostCalculationQuerySet(PCRTestTypeValue_PPOSTCALCULLINKS, valnumPoT,
	 * TagLinksPostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * }
	 * 
	 * } // for answer1 transaction.commit(); transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); } // while ok
	 * 
	 * transaction.commit(); System.out.println("PostCalculation OK.\n"); }
	 * catch(GraknClientException e){ System.out.println(e); throw(e); } }
	 */
	/*
	 * // Post calculation of derivated (speed) static void
	 * PostAveragePDivTCalculation(GraknClient.Session session) {
	 * 
	 * 
	 * try { Boolean okanswers = true;
	 * 
	 * GraknClient.Transaction transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); int nb=0;
	 * 
	 * while (okanswers==true && nb<maxindice) { okanswers=false;
	 * 
	 * String graqlQuery11 = "match $valuepostcalcul isa Value" +
	 * ", has TypeValueId \"" + PCRTestTypeValue_PPOSTCALCUL +"\"" +
	 * ", has StringValueAttribute \"" + TagInitPostCalculate+ "\";"; graqlQuery11+=
	 * "get;limit "+ maxget + ";\n"; System.out.println(graqlQuery11);
	 * 
	 * QueryFuture<List<ConceptMap>> map11 =
	 * transaction.execute((GraqlGet)parse(graqlQuery11)); List<ConceptMap>
	 * answers11= map11.get();
	 * 
	 * for(ConceptMap answer11:answers11) { okanswers = true;
	 * 
	 * // System.out.println(answer11.toString());
	 * 
	 * Entity value1= answer11.get("valuepostcalcul").asEntity(); String
	 * idpostcalculvalue1=value1.id().toString();
	 * 
	 * String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1 +
	 * ";"; graqlQuery12+=
	 * "(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
	 * graqlQuery12+=
	 * "(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery12+=
	 * "$resource isa Resource, has Identity $identity, has Attribut-"+
	 * PCRTestTypeValue_cl_age90+"$cl_age90;"; graqlQuery12+=
	 * "$Departement isa Departement, has CodeGLN $codeGLN;"; graqlQuery12+=
	 * "$timedate isa TimeDate, has EventDate $attributedate; get;limit ";
	 * graqlQuery12+= maxget + ";\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map12 =
	 * transaction.execute((GraqlGet)parse(graqlQuery12)); List<ConceptMap>
	 * answers12= map12.get();
	 * 
	 * for(ConceptMap answer12:answers12) { LocalDateTime valuedate1 =
	 * (LocalDateTime) answer12.get("attributedate").asAttribute().value();
	 * System.out.println(valuedate1);
	 * 
	 * String identity1 = (String) answer12.get("identity").asAttribute().value();
	 * System.out.println(identity1);
	 * 
	 * String codeGLN = (String) answer12.get("codeGLN").asAttribute().value();
	 * System.out.println("codeGLN : " + codeGLN);
	 * 
	 * Long cl_age90 = (Long) answer12.get("cl_age90").asAttribute().value();
	 * System.out.println("cl_age90 :" + cl_age90);
	 * 
	 * int posnum=identity1.lastIndexOf('-'); String
	 * valnum1=identity1.substring(posnum+1);
	 * 
	 * String graqlQuery2 = "match $resource isa Resource, has Identity \""+
	 * identity1+ "\";"; graqlQuery2+=
	 * "(value : $value, resource : $resource) isa ValueRelation; get;\n";
	 * 
	 * //System.out.println(graqlQuery2);
	 * 
	 * QueryFuture<List<ConceptMap>> map2 =
	 * transaction.execute((GraqlGet)parse(graqlQuery2)); List<ConceptMap> answers2=
	 * map2.get();
	 * 
	 * //System.out.println(answers2.toString());
	 * 
	 * Double valuePdivT1=0.0; Double sumvaluePdivT=0.0; int nbPdivT=0;
	 * 
	 * //Long valueattributecl_age90=(long) 0;
	 * 
	 * for(ConceptMap answer2:answers2) { Entity value=
	 * answer2.get("value").asEntity();
	 * 
	 * QueriedAttribute queriedattribute=QueryAttributeValue(transaction, value);
	 * 
	 * if (queriedattribute.getTypeValue().compareTo(PCRTestTypeValue_PdivT)==0) {
	 * valuePdivT1 = (Double) queriedattribute.getValue();
	 * sumvaluePdivT=valuePdivT1; nbPdivT++; }
	 * 
	 * }
	 * 
	 * int plusdays=incdays;
	 * 
	 * LocalDateTime valuedate2=valuedate1.plusDays(plusdays); LocalDateTime
	 * valuedate3=valuedate2.plusDays(plusdays);
	 * 
	 * String graqlQuery31 = "match $Departement isa Departement, has CodeGLN "+
	 * codeGLN + ";"; graqlQuery31+= "$timedate isa TimeDate, has EventDate $date;";
	 * graqlQuery31+= "{$date==" + valuedate2 + ";} or {$date==" + valuedate3 +
	 * ";};"; graqlQuery31+=
	 * "(time : $timedate, localization : $Departement, object : $resource) isa EventPCRTestRelations;"
	 * ; graqlQuery31+= "$resource isa "+ PCRTestClass_WhatPCRTest +
	 * ", has Attribut-"+ PCRTestTypeValue_cl_age90 + " " + cl_age90 + ";get;\n";
	 * //graqlQuery3+=
	 * "(value : $valuecl_age90, resource : $resource) isa ValueRelation;";
	 * //graqlQuery3+= "$valuecl_age90 isa LongValue, has LongValueAttribute "+
	 * valueattributecl_age90 + ", has TypeValueId \""+ PCRTestTypeValue_cl_age90 +
	 * "\";get;\n";
	 * 
	 * 
	 * QueryFuture<List<ConceptMap>> map31 =
	 * transaction.execute((GraqlGet)parse(graqlQuery31)); List<ConceptMap>
	 * answers31= map31.get();
	 * 
	 * if (answers31.isEmpty()==false) { for(ConceptMap answer31:answers31) {
	 * //System.out.println(answer31.toString()); Entity resource31=
	 * answer31.get("resource").asEntity();
	 * 
	 * String graqlQuery5 = "match $resource id "+ resource31.id()+ ";";
	 * graqlQuery5+=
	 * "(value : $valuePdivT, resource : $resource) isa ValueRelation;";
	 * graqlQuery5+=
	 * "$valuePdivT isa DoubleValue, has DoubleValueAttribute $attributePdivT, has TypeValueId \""
	 * + PCRTestTypeValue_PdivT + "\"; get;\n";
	 * 
	 * QueryFuture<List<ConceptMap>> map5 =
	 * transaction.execute((GraqlGet)parse(graqlQuery5)); List<ConceptMap> answers5=
	 * map5.get();
	 * 
	 * //System.out.println(answers5.toString());
	 * 
	 * for(ConceptMap answer5:answers5) {
	 * 
	 * 
	 * Double valuePdivT2 = (Double)
	 * answer5.get("attributePdivT").asAttribute().value();
	 * //System.out.println(valuePdivT2);
	 * 
	 * sumvaluePdivT+=valuePdivT2; nbPdivT++;
	 * 
	 * } } } //} //}
	 * 
	 * sumvaluePdivT=sumvaluePdivT/nbPdivT;
	 * 
	 * // delete old post calcul QueryFuture<? extends List<? extends Answer>> map61
	 * = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);
	 * 
	 * // insertion des valeurs String graqlInsertQuery =
	 * "match $resource isa Resource, has Identity \""+ identity1+ "\";";
	 * graqlInsertQuery+= "$TypeValue-AVPdivT isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_AVPdivT + "\";"; graqlInsertQuery+=
	 * "$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+
	 * PCRTestTypeValue_PPOSTCALCUL +"\";";
	 * 
	 * graqlInsertQuery+= "insert "; Locale locale = new Locale("en", "UK");
	 * DecimalFormat df = new DecimalFormat("#.####################", new
	 * DecimalFormatSymbols(locale)); String
	 * ssumvaluePdivT=df.format(sumvaluePdivT); graqlInsertQuery += insertValue
	 * (PCRTestMetaTypeValue, PCRTestTypeValue_AVPdivT, valnum1, "DoubleValue",
	 * ssumvaluePdivT, "$resource", PCRTestClass_ValueEventPCRTestRelations,
	 * PCRTestClass_TypeValueEventPCRTestRelations);
	 * 
	 * // update de postcalcul graqlInsertQuery +=
	 * PCRPostCalculationQuerySet(valnum1, TagAveragePostCalculate);
	 * 
	 * QueryFuture<List<ConceptMap>> map7 =
	 * transaction.execute((GraqlInsert)parse(graqlInsertQuery)); List<ConceptMap>
	 * answers7= map7.get();
	 * 
	 * // System.out.println(answers7.toString());
	 * System.out.println(ssumvaluePdivT);
	 * 
	 * nb++; System.out.println("PostCalculated : " + nb); }
	 * 
	 * } // for answer1 transaction.commit(); transaction =
	 * session.transaction(GraknClient.Transaction.Type.WRITE); } // while ok
	 * 
	 * transaction.commit(); System.out.println("PostCalculation OK.\n"); }
	 * catch(GraknClientException e){ System.out.println(e); throw(e); } }
	 */
	/*
	 * static ArrayList<Json> parseDataToJson(Input input) throws
	 * FileNotFoundException { ArrayList<Json> items = new ArrayList<>();
	 * 
	 * CsvParserSettings settings = new CsvParserSettings();
	 * settings.setLineSeparatorDetectionEnabled(true);
	 * settings.setDelimiterDetectionEnabled(true, ';'); CsvParser parser = new
	 * CsvParser(settings); parser.beginParsing(getReader(input.getDataPath() +
	 * ".csv"));
	 * 
	 * String[] columns = parser.parseNext(); String[] row;
	 * 
	 * try { while ((row = parser.parseNext()) != null) { Json item = Json.object();
	 * for (int i = 0; i <row.length; i++) { item.set(columns[i], row[i]); //
	 * System.out.println("row : "+ row[i] + " i  :" + i); } items.add(item); } }
	 * catch (Exception e) { int a=0; } return items; } public static Reader
	 * getReader(String RelativePath) throws FileNotFoundException { return new
	 * InputStreamReader(new FileInputStream(RelativePath)); }
	 */
}
