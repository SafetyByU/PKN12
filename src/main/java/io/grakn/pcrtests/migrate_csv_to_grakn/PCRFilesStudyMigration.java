package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	private static InputDepartementsLinks inputdepartementslinks;
	private static InputClassOfValues inputclass_derivated;
	private static InputClassOfValues inputclass_temperature;

	private static InputVaccin inputvaccins;


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

	static final Long minindice = Long.valueOf(0);
	static final Long maxindice = Long.valueOf(7000000);
	static final int maxget = 10;
	static final int maxcommit = 1;

	static final Long shop_minindice = minindice;
	static final Long shop_maxindice = maxindice;

	static final Long dpt_minindice = minindice;
	static final Long dpt_maxindice = maxindice;

	static final Long cv_minindice = minindice;
	static final Long cv_maxindice = maxindice;
	
	static final Long dptlinks_minindice = minindice;
	static final Long dptlinks_maxindice = maxindice;

	static final Long dc_minindice = minindice;
	static final Long dc_maxindice = maxindice;

	static final Long sc_minindice = minindice;
	static final Long sc_maxindice = maxindice;

	static final Long w_minindice = minindice;
	static final Long w_maxindice = maxindice;



	static final Long pcr_minindice = minindice;
	static final Long pcr_maxindice = maxindice;


	static final Long vaccin_minindice = minindice;
	static final Long vaccin_maxindice = maxindice;


	static final LocalDate minDate = LocalDate.parse((CharSequence) "2020-01-01");
	static final LocalDate maxDate = LocalDate.parse((CharSequence) "2021-12-31");
	static final DF_CASE datefilter = DF_CASE.TRUE;

	static final DF_CASE dc_datefilter = DF_CASE.TRUE;
	static final LocalDate dc_minDate = minDate;
	static final LocalDate dc_maxDate = maxDate;

	static final int dc_mindays = 250;
	static final int dc_maxdays = 500;

	//static final DF_CASE sc_datefilter = DF_CASE.TRUE;
	static final LocalDate sc_minDate = minDate;
	static final LocalDate sc_maxDate = maxDate;

	static final int sc_mindays = 250;
	static final int sc_maxdays = 500;

	static final DF_CASE w_datefilter = DF_CASE.BOTH;
	static final LocalDate w_minDate = LocalDate.parse((CharSequence) "2021-03-20");;
	static final LocalDate w_maxDate = maxDate;

	static final int w_mindays = 0;
	static final int w_maxdays = 1000;

	static final DF_CASE pcr_datefilter = DF_CASE.BOTH;
	static final LocalDate pcr_minDate = minDate;
	static final LocalDate pcr_maxDate = maxDate;

	static final int pcr_mindays = 0;
	static final int pcr_maxdays = 1000;

	static final String pcr_firstindice = "0";

	static final DF_CASE vaccin_datefilter = DF_CASE.BOTH;
	static final LocalDate vaccin_minDate = minDate;
	static final LocalDate vaccin_maxDate = maxDate;

	static final int vaccin_mindays = 0;
	static final int vaccin_maxdays = 1000;

	static final String vaccin_firstindice = "0";

	static String fileHistorics="data/historics.json";

	static ArrayList<Json> historics;
	static JsonArray array=null;

	abstract static class Input {
		String path;
		Long indice;
		Long firstIndice;
		Boolean update=false;

		public Boolean getUpdate() {
			return update;
		}

		public void setUpdate(Boolean update) {
			this.update = update;
		}

		Boolean isdefine = false;

		public Input(String path) {
			this.path = path;

			this.firstIndice= Long.decode(pcr_firstindice);
			this.indice = firstIndice;
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

		public ArrayList<Json>  removeHistoric(ArrayList<Json> historics, String key)
		{
			for (Json historic : historics)
			{
				if (historic.has(key))
				{
					historics.remove(historic);
					break;
				}
			}
			return historics;
		}

		public void saveHistoric(ArrayList<Json> items, boolean finalsave)
		{
			// minIndice
			Json itemMinIndice = Json.object();
			itemMinIndice.set("minIndice",indice);
			items.add(itemMinIndice);

			Json itemFirstIndice = Json.object();
			itemFirstIndice.set("firstIndice",firstIndice);
			items.add(itemFirstIndice);
		}

		public void loadSpecificHistoric(JsonArray historicsInputPCRList )
		{

		}

		public void loadHistoric(JsonArray historicsInputPCRList )
		{

		}
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

		// init historics for incremental loading
		historics = new ArrayList<>();
		//saveHistorics();
		JsonArray jsonArray=loadHistorics();
		if (jsonArray!=null)
		{
			for (final Input input1 : inputs)
			{
				input1.loadHistoric(jsonArray);
			}
		}

		for (final Input input : inputs) {

			System.out.println("Loading from [" + input.getDataPath() + "] into Grakn ...");
			try {
				loadDataIntoGrakn(input, session);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// save historics of loading
		saveHistorics();

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
			inputvaccins.postCalculate(session);

			inputclass_derivated.postCalculate(session);
			inputclass_derivated.postCalculate(session);

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
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULNEXTDATEPCRTEST)
		{
			inputpcrtest = new InputPCRTest(pcrFilesOptions.getOptImp(), null, maxget, pcr_minindice, pcr_maxindice,
					pcr_datefilter, pcr_minDate, pcr_maxDate, pcr_mindays, pcr_maxdays);
			inputpcrtest.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULSAMEDATEPCRTEST)
		{
			inputpcrtest = new InputPCRTest(pcrFilesOptions.getOptImp(), null, maxget, pcr_minindice, pcr_maxindice,
					pcr_datefilter, pcr_minDate, pcr_maxDate, pcr_mindays, pcr_maxdays);
			inputpcrtest.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULVACCIN)
		{
			inputvaccins = new InputVaccin(pcrFilesOptions.getOptImp(), null, maxget, vaccin_minindice, vaccin_maxindice,
					vaccin_datefilter, vaccin_minDate, vaccin_maxDate, vaccin_mindays, vaccin_maxdays);
			inputvaccins.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULVACCINNEXTDATE)
		{
			inputvaccins = new InputVaccin(pcrFilesOptions.getOptImp(), null, maxget, vaccin_minindice, vaccin_maxindice,
					vaccin_datefilter, vaccin_minDate, vaccin_maxDate, vaccin_mindays, vaccin_maxdays);
			inputvaccins.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULVACCINSAMEDATEPCRTEST)
		{
			inputvaccins = new InputVaccin(pcrFilesOptions.getOptImp(), null, maxget, vaccin_minindice, vaccin_maxindice,
					vaccin_datefilter, vaccin_minDate, vaccin_maxDate, vaccin_mindays, vaccin_maxdays);
			inputvaccins.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULDEPARTEMENTCONFINEMENTCLEAN)
		{
			inputdepartementscouvrefeu = new InputDepartementsCouvreFeu(pcrFilesOptions.getOptImp(), null, maxget, dc_minindice, dc_maxindice,
					dc_datefilter, dc_minDate, dc_maxDate, dc_mindays, dc_maxdays);
			inputdepartementscouvrefeu.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULDEPARTEMENTSAMEDATEPCRTEST)
		{
			inputdepartementscouvrefeu = new InputDepartementsCouvreFeu(pcrFilesOptions.getOptImp(), null, maxget, dc_minindice, dc_maxindice,
					dc_datefilter, dc_minDate, dc_maxDate, dc_mindays, dc_maxdays);
			inputdepartementscouvrefeu.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULSHOPCONFINEMENTCLEAN)
		{
			inputshopsconfinement = new InputShopsConfinement(pcrFilesOptions.getOptImp(), null, maxget, sc_minindice, sc_maxindice,
					sc_minDate, sc_maxDate, sc_mindays, sc_maxdays);
			inputshopsconfinement.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULSHOPSAMEDATEPCRTEST)
		{
			inputshopsconfinement = new InputShopsConfinement(pcrFilesOptions.getOptImp(), null, maxget, sc_minindice, sc_maxindice,
					sc_minDate, sc_maxDate, sc_mindays, sc_maxdays);
			inputshopsconfinement.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULWEATHERCLEAN)
		{
			inputweather = new InputWeather(pcrFilesOptions.getOptImp(), null, maxget, w_minindice, w_maxindice,
					w_datefilter, w_minDate, w_maxDate, w_mindays, w_maxdays);
			inputweather.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULWEATHERNEXTDATE)
		{
			inputweather = new InputWeather(pcrFilesOptions.getOptImp(), null, maxget, w_minindice, w_maxindice,
					w_datefilter, w_minDate, w_maxDate, w_mindays, w_maxdays);
			inputweather.postCalculate(session);
		}
		else if (pcrFilesOptions.getOptImp().getTypeImport()==TypeImport.POSTCALCULWEATHERSAMEDATEPCRTEST)
		{
			inputweather = new InputWeather(pcrFilesOptions.getOptImp(), null, maxget, w_minindice, w_maxindice,
					w_datefilter, w_minDate, w_maxDate, w_mindays, w_maxdays);
			inputweather.postCalculate(session);
		}

	}

	static Collection<Input> initialiseInputs(OptionImport optionImport, Collection<Input> inputs) {

		// master data class of values
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTCLASSDERIVATED)
		{
			String filedpt="data/class_derivated";
			if (optionImport!=null)
			{
				if ( optionImport.getFilename()!=null)
				{
					filedpt=optionImport.getFilename();
				}
			}
			inputclass_derivated = new InputClassOfValues(optionImport, filedpt, maxget, cv_minindice, cv_maxindice);
			inputs = inputclass_derivated.initialize(inputs);
		}
		// master data class of values
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTCLASSTEMPERATURE)
		{
			String filedpt="data/class_temperature";
			if (optionImport!=null)
			{
				if ( optionImport.getFilename()!=null)
				{
					filedpt=optionImport.getFilename();
				}
			}
			inputclass_temperature = new InputClassOfValues(optionImport, filedpt, maxget, cv_minindice, cv_maxindice);
			inputs = inputclass_temperature.initialize(inputs);
		}
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
		// master data links departements
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTDPTLINKS)
		{
			String filedpt="data/departementlinks";
			if (optionImport!=null)
			{
				if ( optionImport.getFilename()!=null)
				{
					filedpt=optionImport.getFilename();
				}
			}
			inputdepartementslinks = new InputDepartementsLinks(optionImport, filedpt, maxget, dptlinks_minindice, dptlinks_maxindice);
			inputs = inputdepartementslinks.initialize(inputs);
		}
		// PCR TESTS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTPCRTEST)
		{
			String filepcr="data/sp-pos-quot-dep-2021-04-30-19h05";
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

		// VACCINS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTVACCINPERTYPE)
		{
			// 1st file per vaccin
			String filevaccin="data/vacsi-v-dep-2021-05-15-19h05";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					filevaccin=optionImport.getFilename();
				}
			}

			inputvaccins = new InputVaccin(optionImport, filevaccin, maxget, vaccin_minindice, vaccin_maxindice,
					vaccin_datefilter, vaccin_minDate, vaccin_maxDate, vaccin_mindays, vaccin_maxdays);

			inputs = inputvaccins.initialize(inputs);

		}

		// VACCINS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTVACCINPERAGE)
		{

			// 2nd file per age
			String filevaccin="data/vacsi-a-dep-2021-05-15-19h05";
			if (optionImport!=null)
			{
				if (optionImport.getFilename()!=null)
				{
					filevaccin=optionImport.getFilename();
				}
			}

			inputvaccins = new InputVaccin(optionImport, filevaccin, maxget, vaccin_minindice, vaccin_maxindice,
					vaccin_datefilter, vaccin_minDate, vaccin_maxDate, vaccin_mindays, vaccin_maxdays);

			inputs = inputvaccins.initialize(inputs);
		}
		// CONFINEMENTS DEPARTEMENT EVTS
		if (optionImport==null || optionImport.getTypeImport()==TypeImport.IMPORTDPTCOUVREFEU)
		{
			String filedpt="data/departementdecret2";
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
			String fileweather="data/temperature-quotidienne-departementale-250521";
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

	// save the historics of imports
	static JsonArray loadHistorics()
	{

		String filename=fileHistorics;

		JsonParser parser = new JsonParser();
		try {
			if (array==null)
			{
				FileReader fr= new FileReader(fileHistorics);

				Object obj = parser.parse(fr);

				// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
				array = (JsonArray) obj;

				Iterator<JsonElement> iterator = array.iterator();
				while (iterator.hasNext()) 
				{
					JsonElement elt = iterator.next();
					JsonObject json1=elt.getAsJsonObject();
					Set<Entry<String, JsonElement>> set1=json1.entrySet();

					Iterator<Entry<String, JsonElement>> it1=set1.iterator();
					while (it1.hasNext()) 
					{
						Entry<String, JsonElement> elt1 =  it1.next();
						JsonElement json2=elt1.getValue();
						if (json2.isJsonArray()==true)
						{
							JsonArray array2=json2.getAsJsonArray();

							Json itemInput = Json.object();
							ArrayList<Json> subHistorics = new ArrayList<>();

							Iterator<JsonElement>  iterator2=array2.iterator();
							while (iterator2.hasNext()) 
							{
								JsonElement elt2 =  iterator2.next();
								JsonObject jsonobj2 = elt2.getAsJsonObject();
								Set<Entry<String, JsonElement>> set2= jsonobj2.entrySet();
								Iterator<Entry<String, JsonElement>> it3=set2.iterator();
								while (it3.hasNext()) 
								{
									Entry<String, JsonElement> elt3 =  it3.next();
									Json item = Json.object();
									String s3=elt3.getValue().toString();
									try
									{
										Long l3=Long.parseLong(s3);
										item.set(elt3.getKey(),l3);
									}
									catch (NumberFormatException e)
									{
										int i=0;
										while(i<s3.length())
										{
											if (s3.charAt(i)=='"')
											{
												s3=s3.substring(0, i)+s3.substring(i+1, s3.length());
											}
											else
											{
												i++;
											}
										}
										item.set(elt3.getKey(),s3);
									}
									subHistorics.add(item);
								}
							}

							itemInput.set(elt1.getKey(), subHistorics);
							historics.add(itemInput);
						}
					}
				}
			}
			return array;
		} 
		catch (FileNotFoundException e)
		{
			//e.printStackTrace();
		}
		return null;
	}

	// load the previous historics of imports
	static void saveHistorics()
	{
		String filename=fileHistorics;

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(filename);

			String sjson=historics.toString();
			fileWriter.append(sjson);

			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String [] splitInserts(String insert)
	{
		String finalinserts[] = null;
		if (insert.contains("match")==true)
		{
			finalinserts = new String[1];
			finalinserts[0]=insert;
		}
		else
		{
			String inserts[] = insert.split(";");
			finalinserts = new String[inserts.length];
			int i=0;
			for (String oneinsert:inserts)
			{
				if (oneinsert.contains("insert")==false)
				{
					oneinsert="insert "+oneinsert;
				}
				oneinsert=oneinsert + " ;";
				finalinserts[i]=oneinsert;
				i++;
			}
		}
		return finalinserts;
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

				String graqlInsertQuery = input.template(item);

				if (graqlInsertQuery.equals("") == false) {


					// GraknClient.Transaction transaction =
					// session.transaction(GraknClient.Transaction.Type.WRITE);
					System.out.println("Executing Graql Query: " + graqlInsertQuery);
					/*String inserts[] = splitInserts(graqlInsertQuery);
					for (String insert:inserts)
					{*/
					try {

						if (close == true) {
							transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
							close = false;
						}
						nb++;

						QueryFuture<List<ConceptMap>> list = transaction.execute((GraqlInsert) parse(graqlInsertQuery));
						//System.out.println("Executing Graql Query: " + insert);

						if (nb % maxcommit == 0) {
							transaction.commit();
							close = true;
							input.saveHistoric(historics, false);
							PCRFilesStudyMigration.saveHistorics();
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
			}
			System.out.println("\nInserted " + items.size() + " items from [ " + input.getDataPath() + "] into Grakn.\n");
			input.saveHistoric(historics, true);
			PCRFilesStudyMigration.saveHistorics();

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
}
