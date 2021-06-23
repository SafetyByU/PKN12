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

import com.google.gson.JsonArray;
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


public abstract class InputSetOfData implements IInputSetOfData{

	OptionImport optionImport;
	
	public OptionImport getOptionImport() {
		return optionImport;
	}

	SetOfData setOfData=null;
	
	public Map<String, String> getDepartementId() {
		return setOfData.getDepartementId();
	}
	
	List<String> titres=new ArrayList<String>();
	
	//static final String AttributeValue_speed="SpeedValueAttribute";
	//static final String AttributeValue_acceleration="AccelerationValueAttribute";


	static final String SuffixEventRelations = ",\nrelates registeredevent, "
			+ "relates actor, "
			+ "relates object, "
			+ "relates goal, "
			+ "relates localization, "
			+ "relates time";

	static final String SuffixValueRelations = ",\nrelates resource, "
			+ "relates value";
	static final String SuffixValueTypeRelations = ",\nrelates typevalue, "
			+ "relates value";

	static final String SuffixPeriodicRelations = ",\nrelates periodoftime, "
			+ "relates startwhendate,"
			+ "relates endwhendate";

	static final String AttributPrefix="Attribut-";


	Long minIndice=Long.valueOf(0);
	Long maxIndice=Long.valueOf(1000000000);
	int maxGet=250;
	Long indice;

	//protected String filename;


	public Long getMinindice() {
		return minIndice;
	}
	public void setMinindice(Long minIndice) {
		this.minIndice = minIndice;
	}
	public Long getMaxindice() {
		return maxIndice;
	}
	public void setMaxindice(Long maxIndice) {
		this.maxIndice = maxIndice;
	}
	public int getMaxget() {
		return maxGet;
	}
	public void setMaxget(int maxget) {
		this.maxGet = maxget;
	}

	InputSetOfData(OptionImport myOptionImport,String filename)
	{
		optionImport=myOptionImport;
		this.setOfData.setFilename(filename);
	}

	public InputSetOfData(OptionImport myOptionImport,String filename, int myMaxGet, Long minIndice, Long maxIndice)
	{
		optionImport=myOptionImport;
			
		setOfData=new SetOfData();
		
		this.setOfData.setFilename(filename);

		this.maxGet=myMaxGet;
		this.minIndice=minIndice;
		this.maxIndice=maxIndice;
	}

	public void postCalculate(GraknClient.Session session)
	{
		initializeCaches(session);
	}

	protected void initializeCaches(Session session) {
		setOfData.initializeCaches(session);
	}
	
	protected Collection<Input> initialiseDefine(Collection<Input> inputs, String subtype, String type)
	{	
		inputs.add(new InputSingletonDefine(subtype, type) {
			@Override
			public String template() {
				String define =  "define "+  subtype + " sub "+ type +";\n";
				return 	define;
			}

			@Override
			String template(Json data) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return inputs;
	}

	protected String initialiseDefine(String subtype, String type, List<DefineAttribute> attributes)
	{	

		String define =  "define \n";
		for (DefineAttribute attribut : attributes)
		{
			define+= AttributPrefix + attribut.getName() + " sub " + attribut.getType() + ";\n";
		}

		define += subtype + " sub "+ type + " ";
		for (DefineAttribute attribut : attributes)
		{
			define+=", has " + "Attribut-"+ attribut.getName();
		}
		define +=";\n";

		return 	define;

	}

	protected Collection<Input> initialiseDefine(Collection<Input> inputs, String subtype, String type, List<DefineAttribute> attributes)
	{	
		inputs.add(new InputSingletonDefine(subtype, type, attributes) {
			@Override
			public String template() {
				/*String define =  "define \n";
				for (DefineAttribute attribut : attributes)
				{
					define+= AttributPrefix + attribut.getName() + " sub " + attribut.getType() + ";\n";
				}

				define += subtype + " sub "+ type + " ";
				for (DefineAttribute attribut : attributes)
				{
					define+=", has " + "Attribut-"+ attribut.getName();
				}
				define +=";\n";

				return 	define;*/
				return initialiseDefine(subtype, type, attributes);
			}

			@Override
			String template(Json data) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return inputs;
	}

	protected String InputSingletonDefineQuery(String prefix, String type, String key, String keyvalue)
	{
		String query = prefix + "$"+ type + "-" + keyvalue + " isa " +  type + ", has " + key +  " \"" + keyvalue + "\";" ;
		return query;
	}

	public String InputSingletonDefineQuery(String prefix, String type, String key, String keyvalue, String typevalue2, String value2)
	{
		String query = prefix + "$"+ type + "-" + keyvalue + " isa " +  type + ", has " + key +  " \"" + keyvalue + "\", has " + typevalue2 +  " \""+ value2 + "\";";
		return query;
	}
	protected Collection<Input> initialiseInsertEntitySingleton(Collection<Input> inputs, String type, String key, String keyvalue)
	{	
		inputs.add(new InputSingletonInsertEntity(type, key, keyvalue) {
			@Override
			public String template() {
				String insert = InputSingletonDefineQuery("insert ",  type,  key,  keyvalue);
				return 	insert;
			}

			@Override
			String template(Json data) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return inputs;
	}
	protected Collection<Input> initialiseInsertEntitySingleton(Collection<Input> inputs, String type, String key, String keyvalue, String typevalue2, String value2)
	{	
		inputs.add(new InputSingletonInsertEntity(type, key, keyvalue,typevalue2, value2) {
			@Override
			public String template() {
				String insert = InputSingletonDefineQuery("insert ",  type,  key,  keyvalue, typevalue2, value2);
				return 	insert;
			}

			@Override
			String template(Json data) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return inputs;
	}

	protected // insertion of a value
	String insertValue (String metatype, String type, String indice, String TypeValue, String value, String resource, String value_eventrelation, String typevalue_eventrelations) 
	{

		String graqlInsertQuery="";

		if (TypeValue=="StringValue")
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype +  type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + type +"\""
					+ 	", has " + TypeValue + "Attribute \"" + value + "\";\n";
		}
		else
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype + type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + type +"\""
					+ 	", has " + TypeValue + "Attribute " + value + ";\n";
		}

		graqlInsertQuery	+= "$ValueRelation-" + type + "-" + indice 
				+" (value : $Value-" + type +"-" + indice
				+ " , resource : " + resource + ") isa " + value_eventrelation + ";\n";

		graqlInsertQuery	+= "$ValueTypeRelation-" + type + "-" + indice 
				+" (value : $Value-"+ type + "-" + indice 
				+ " , typevalue : $TypeValue-"+ type + ") isa "+ typevalue_eventrelations + ";\n";

		return graqlInsertQuery;
	}

	protected // insertion of a value
	String insertValueWithAttributes (String metatype, String type, String indice, String TypeValue, String insertAttributes, String resource, String value_eventrelation, String typevalue_eventrelations) 
	{

		String graqlInsertQuery="";

		if (TypeValue=="StringValue")
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype +  type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + type +"\""
					+ 	insertAttributes+ "\";\n";
		}
		else
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype + type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + type +"\""
					+ 	insertAttributes + ";\n";
		}

		graqlInsertQuery	+= "$ValueRelation-" + type + "-" + indice 
				+" (value : $Value-" + type +"-" + indice
				+ " , resource : " + resource + ") isa " + value_eventrelation + ";\n";

		graqlInsertQuery	+= "$ValueTypeRelation-" + type + "-" + indice 
				+" (value : $Value-"+ type + "-" + indice 
				+ " , typevalue : $TypeValue-"+ type + ") isa "+ typevalue_eventrelations + ";\n";

		return graqlInsertQuery;
	}

	protected // insertion of a value
	String insertValueWithAttributes (String metatype, String type, String subtype, String indice, String TypeValue, String insertAttributes, String resource, String value_eventrelation, String typevalue_eventrelations) 
	{

		String graqlInsertQuery="";

		if (TypeValue=="StringValue")
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype +  type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + subtype +"\""
					+ 	", has " + TypeValue 
					+ 	insertAttributes+ "\";\n";
		}
		else
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype + type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + subtype +"\""
					+ 	", has " + TypeValue 
					+ 	insertAttributes+ "\";\n";
		}

		graqlInsertQuery	+= "$ValueRelation-" + type + "-" + indice 
				+" (value : $Value-" + type +"-" + indice
				+ " , resource : " + resource + ") isa " + value_eventrelation + ";\n";

		graqlInsertQuery	+= "$ValueTypeRelation-" + type + "-" + indice 
				+" (value : $Value-"+ type + "-" + indice 
				+ " , typevalue : $TypeValue-"+ type + ") isa "+ typevalue_eventrelations + ";\n";

		return graqlInsertQuery;
	}

	protected // insertion of a value
	String insertValue (String metatype, String type, String subtype, String indice, String TypeValue, String value, String resource, String value_eventrelation, String typevalue_eventrelations) 
	{

		String graqlInsertQuery="";

		if (TypeValue=="StringValue")
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype +  type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + subtype +"\""
					+ 	", has " + TypeValue + "Attribute \"" + value + "\";\n";
		}
		else
		{
			graqlInsertQuery+= 	"$Value-" + type + "-" + indice
					+ " isa " + TypeValue
					+ 	", has IdValue \""+ metatype + type+"-"+indice+"\""
					+ 	", has TypeValueId \"" + subtype +"\""
					+ 	", has " + TypeValue + "Attribute " + value + ";\n";
		}

		graqlInsertQuery	+= "$ValueRelation-" + type + "-" + indice 
				+" (value : $Value-" + type +"-" + indice
				+ " , resource : " + resource + ") isa " + value_eventrelation + ";\n";

		graqlInsertQuery	+= "$ValueTypeRelation-" + type + "-" + indice 
				+" (value : $Value-"+ type + "-" + indice 
				+ " , typevalue : $TypeValue-"+ type + ") isa "+ typevalue_eventrelations + ";\n";

		return graqlInsertQuery;
	}

	protected // insertion of an attribut
	String insertAttribut (String typeattribut, String value) 
	{
		String hasattribut = ", has "+ AttributPrefix + typeattribut + " " + value;

		return hasattribut;
	}

	protected // insertion of an attribut
	String insertAttributWithoutPrefix (String typeattribut, String value) 
	{
		String hasattribut = ", has "+ typeattribut + " " + value;

		return hasattribut;
	}

	protected // insertion of an attribut
	String queryAttribut (String typeattribut, String value) 
	{
		String hasattribut = ", has "+ AttributPrefix + typeattribut + " $" + value;

		return hasattribut;
	}


	protected // insertion of a value
	String insertValue (String type, String indice, String TypeValue, String value, String resource) 
	{

		return insertValue (getMetaType(), type, indice, TypeValue, value, resource, GetValue_Eventrelation(), getTypevalue_Eventrelations());
	}

	abstract String getMetaType();
	abstract String GetValue_Eventrelation();
	abstract String getTypevalue_Eventrelations();

	protected void check(Json json, List<String> titres)
	{
		int col=0;
		while (col<titres.size())
		{
			if (json.at(titres.get(col)).isNull())
			{
				System.out.println("Titre non trouvé :" + titres.get(col));
			}
			col++;
		}
	}

	static protected ArrayList<Json> parseDataToJson(Input input) throws FileNotFoundException {
		ArrayList<Json> items = new ArrayList<>();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setDelimiterDetectionEnabled(true, ';');
		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(getReader(input.getDataPath() + ".csv"));

		String[] columns = parser.parseNext();
		String[] row;

		// put the title of columns
		Json item1 = Json.object();
		for (int i = 0; i <columns.length; i++) {
			String coli= "" + i;
			item1.set(coli,columns[i]);
		}
		items.add(item1);
		try
		{
			while ((row = parser.parseNext()) != null) {
				Json item = Json.object();
				for (int i = 0; i <row.length; i++) {
					item.set(columns[i], row[i]);
					// System.out.println("row : "+ row[i] + " i  :" + i);
				}
				items.add(item);
			}
		}
		catch (Exception e)
		{
			int a=0;
		}
		return items;
	}
	

	
	static protected Reader getReader(String RelativePath) throws FileNotFoundException {
		return new InputStreamReader(new FileInputStream(RelativePath));
	}




	protected // Query specific value
	QueriedAttribute QueryAttributeValue(GraknClient.Transaction transaction, Entity value)  {

		String entitytype= value.type().label().getValue();

		String graqlQueryValue = "match $value id "+ value.id() + ", has TypeValueId $typevalueid, has "+  entitytype +"Attribute $attribute ; get;\n";

		//System.out.println(graqlQueryValue);

		QueryFuture<List<ConceptMap>> mapQueryValue = transaction.execute((GraqlGet)parse(graqlQueryValue));
		List<ConceptMap> answersQueryValue= mapQueryValue.get();

		for(ConceptMap answerQueryValue : answersQueryValue)
		{
			Object objectvalue =  answerQueryValue.get("attribute").asAttribute().value();
			//System.out.println(objectvalue);

			String valuetypeid =  (String) answerQueryValue.get("typevalueid").asAttribute().value().toString();

			//System.out.println(valuetypeid);

			QueriedAttribute queriedattribute=new QueriedAttribute(valuetypeid, objectvalue);


			return queriedattribute;
		}
		return null;
	}


	protected // Post calculation delete
	QueryFuture<? extends List<? extends Answer>> PostCalculationQueryDelete(GraknClient.Transaction transaction, String idvaluepostcalculvalue1)  {

		// delete old post calcul
		GraqlDelete deletequery = match(
				var("value").isa("Value").has("IdValue", idvaluepostcalculvalue1))
				.delete(var("value").isa("Value"));


		QueryFuture<? extends List<? extends Answer>> map61  = transaction.execute(deletequery);

		return map61;

	}

	protected // Post calculation delete
	QueryFuture<? extends List<? extends Answer>> PostCalculationQueryDeleteById(GraknClient.Transaction transaction, String idpostcalculvalue1)  {

		// delete old post calcul
		/*GraqlDelete deletequery = match(
				var("value").id(idpostcalculvalue1))
				.delete(var("value").isa("Value"));


		QueryFuture<? extends List<? extends Answer>> map61  = transaction.execute(deletequery);
		 */
		return deleteValueById(transaction, idpostcalculvalue1);
	}


	/*protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteValueById(GraknClient.Transaction transaction, String idvalue)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$value id " + idvalue + ";";
		deleteQuery+= "$relationtypevalue (value : $value) isa ValueTypeRelation;";
		deleteQuery+= "$relationvalue (value : $value) isa ValueRelation;";

		deleteQuery+= "delete $relationvalue isa ValueRelation;";
		deleteQuery+= "delete $relationtypevalue isa ValueTypeRelation;";
		deleteQuery+= "delete $value isa Value;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}*/

	protected // value delete by type for a specific resource
	void deleteValueByType(GraknClient.Transaction transaction, String idresource, String type)  {

		String graqlQueryValue = "match $resource id "+ idresource + "; (resource:$resource, value:$value) isa ValueRelation; $value isa Value, has TypeValueId \""+  type + "\", has IdValue $idvalue; get;\n";

		QueryFuture<List<ConceptMap>> mapQueryValue = transaction.execute((GraqlGet)parse(graqlQueryValue));
		List<ConceptMap> answersQueryValue= mapQueryValue.get();

		for (ConceptMap answer:answersQueryValue)
		{
			String idvalue= (String)answer.get("idvalue").asAttribute().value();
			System.out.println("delete idvalue : "+ idvalue);

			Entity value= answer.get("value").asEntity();
			deleteValueById(transaction, value.id().toString());
		}

	}

	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteValueById(GraknClient.Transaction transaction, String idvalue)  {

		QueryFuture<? extends List<? extends Answer>> map2  = deleteRelationTypeValueById(transaction, idvalue);

		QueryFuture<? extends List<? extends Answer>> map3  = deleteRelationValueById(transaction, idvalue);

		QueryFuture<? extends List<? extends Answer>> map1  = deleteTheValueById(transaction, idvalue);

		return map3;

	}

	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteTheValueById(GraknClient.Transaction transaction, String idvalue)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$value id " + idvalue + ";";	
		deleteQuery+= "delete $value isa Value;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteTheResourceById(GraknClient.Transaction transaction, String idresource)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$resource id " + idresource + ";";	
		deleteQuery+= "delete $resource isa Resource;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteThePeriodOfTimeById(GraknClient.Transaction transaction, String idpt)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$pt id " + idpt + ";";	
		deleteQuery+= "delete $pt isa PeriodOfTime;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}
	
	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteTheTimeAndValuesById(GraknClient.Transaction transaction, String idt)  {

		// delete linked values
		QueryFuture<? extends List<? extends Answer>> map1  = deleteAllValuesByResourceId(transaction, idt);
		
		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$t id " + idt + ";";	
		deleteQuery+= "delete $t isa TimeDate;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}
	
	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteRelationTypeValueById(GraknClient.Transaction transaction, String idvalue)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$value id " + idvalue + ";";
		deleteQuery+= "$relationtypevalue (value : $value) isa ValueTypeRelation;";

		deleteQuery+= "delete $relationtypevalue isa ValueTypeRelation;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}
	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteRelationValueById(GraknClient.Transaction transaction, String idvalue)  {

		String deleteQuery="";

		deleteQuery= "match ";
		deleteQuery+= "$value id " + idvalue + ";";
		deleteQuery+= "$relationvalue (value : $value) isa ValueRelation;";

		deleteQuery+= "delete $relationvalue isa ValueRelation;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // delete all value from resource
	QueryFuture<? extends List<? extends Answer>> deleteAllValuesByResourceId(GraknClient.Transaction transaction, String idresource)  {

		String deleteQuery= "match $relationtypevalue (value : $value) isa ValueTypeRelation;";
		deleteQuery+= "$relationvalue (value : $value, resource : $resource) isa ValueRelation;";
		deleteQuery+= "$resource id " + idresource + ";\n";

		// delete ValueTypeRelation
		String deleteQuery2= deleteQuery + "delete $relationtypevalue isa ValueTypeRelation;\n";
		final GraqlDelete parseddelete2 = Graql.parse(deleteQuery2).asDelete();

		QueryFuture<? extends List<? extends Answer>> map2  = transaction.execute(parseddelete2);
		
		// query without typerelation already deleted
		deleteQuery= "match $relationvalue (value : $value, resource : $resource) isa ValueRelation;";
		deleteQuery+= "$resource id " + idresource + ";\n";

		// delete Value
		String deleteQuery3 = deleteQuery + "delete $value isa Value;";
		final GraqlDelete parseddelete3 = Graql.parse(deleteQuery3).asDelete();

		QueryFuture<? extends List<? extends Answer>> map3  = transaction.execute(parseddelete3);
		
		// delete ValueRelation
		String deleteQuery1= deleteQuery + "delete $relationvalue isa ValueRelation;";
		final GraqlDelete parseddelete1 = Graql.parse(deleteQuery1).asDelete();

		QueryFuture<? extends List<? extends Answer>> map1  = transaction.execute(parseddelete1);
		

		
		return map3;

	}

	protected // event delete
	QueryFuture<? extends List<? extends Answer>> deleteEventById(GraknClient.Transaction transaction, String idevent)  {

		String deleteQuery="";

		deleteQuery+= "match $eventrelations ("
				+ "registeredevent : $event,"
				+ "actor : $actor"
				+ "object : $object"
				+ "goal : $goal"
				+ "localization : $localization"
				+ "time : $time)"
				+ "isa eventrelations;\n";
		deleteQuery+= "$event id " + idevent + "\n";

		deleteQuery+= "delete $event isa event;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // event delete
	QueryFuture<? extends List<? extends Answer>> deleteLinkedEventByResourceId(GraknClient.Transaction transaction, String idresource, RelationTypeEvent relationtypevent)  {

		String deleteQuery="";

		deleteQuery+= "match $eventrelations (";
		deleteQuery+=  "registeredevent : $event,";
		
		switch (relationtypevent)
		{	
		case WHORELATION : 
			deleteQuery+=  "actor : $resource)";
			break;
		case WHATRELATION : 
			deleteQuery+=  "object : $resource)";
			break;
		case WHYRELATION : 
			deleteQuery+=  "goal : $resource)";
			break;
		case TIMERELATION : 
			deleteQuery+=  "time : $resource)";
			break;
		case WHERERELATION : 
			deleteQuery+=  "localization : $resource)";
			break;
		default:
			break;
		}
		deleteQuery+=  " isa eventrelations;\n";
		
		deleteQuery+= "$resource id " + idresource + ";";

		deleteQuery+= "delete $event isa event;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // event delete
	QueryFuture<? extends List<? extends Answer>> deleteLinkedTimeEventByResourceId(GraknClient.Transaction transaction, String idresource, RelationTypeEvent relationtypevent)  {

		String deleteQuery="";

		deleteQuery+= "match $eventrelations (";
		deleteQuery+=  "time : $time,";
		
		switch (relationtypevent)
		{	
		case WHORELATION : 
			deleteQuery+=  "actor : $resource)";
			break;
		case WHATRELATION : 
			deleteQuery+=  "object : $resource)";
			break;
		case WHYRELATION : 
			deleteQuery+=  "goal : $resource)";
			break;
		case TIMERELATION : 
			deleteQuery+=  "time : $resource)";
			break;
		case WHERERELATION : 
			deleteQuery+=  "localization : $resource)";
			break;
		default:
			break;
		}
		deleteQuery+=  " isa eventrelations;";
		
		deleteQuery+= "$resource id " + idresource + ";";

		deleteQuery+= "delete $time isa When;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}
	
	protected // event delete
	QueryFuture<? extends List<? extends Answer>> deleteLinkedAllValuesTimeEventByResourceId(GraknClient.Transaction transaction, String idresource, RelationTypeEvent relationtypevent)  {

		String deleteQuery="";

		deleteQuery+= "match $eventrelations (";
		deleteQuery+=  "time : $time,";
		
		switch (relationtypevent)
		{	
		case WHORELATION : 
			deleteQuery+=  "actor : $resource)";
			break;
		case WHATRELATION : 
			deleteQuery+=  "object : $resource)";
			break;
		case WHYRELATION : 
			deleteQuery+=  "goal : $resource)";
			break;
		case TIMERELATION : 
			deleteQuery+=  "time : $resource)";
			break;
		case WHERERELATION : 
			deleteQuery+=  "localization : $resource)";
			break;
		default:
			break;
		}
		deleteQuery+=  " isa eventrelations;";
		
		deleteQuery+= "$resource id " + idresource + ";";

		deleteQuery+= "(value : $value, resource : $time) isa ValueRelation;\n";
		
		deleteQuery+= "delete $value isa Value;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}
	
	
	protected // event delete
	QueryFuture<? extends List<? extends Answer>> deleteEventRelationByResourceId(GraknClient.Transaction transaction, String idresource, RelationTypeEvent relationtypevent)  {

		String deleteQuery="";

		deleteQuery+= "match $eventrelations (";

		switch (relationtypevent)
		{
		case EVENTRELATION : 
			deleteQuery+=  "registeredevent : $resource)";	
			break;
		case WHORELATION : 
			deleteQuery+=  "actor : $resource)";
			break;
		case WHATRELATION : 
			deleteQuery+=  "object : $resource)";
			break;
		case WHYRELATION : 
			deleteQuery+=  "goal : $resource)";
			break;
		case TIMERELATION : 
			deleteQuery+=  "time : $resource)";
			break;
		case WHERERELATION : 
			deleteQuery+=  "localization : $resource)";
			break;
		}
		deleteQuery+=  " isa eventrelations;";
		deleteQuery+= "$resource id " + idresource + ";";

		deleteQuery+= "delete $eventrelations isa eventrelations;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	protected // value delete
	QueryFuture<? extends List<? extends Answer>> deleteEventObjectById(GraknClient.Transaction transaction, String idresource, RelationTypeEvent relationtypevent)  {

		// suppress all kinked resources
		QueryFuture<? extends List<? extends Answer>> map1  = deleteAllValuesByResourceId(transaction, idresource);

		// suppress linked event
		QueryFuture<? extends List<? extends Answer>> map2  = deleteLinkedEventByResourceId(transaction, idresource, relationtypevent);

		// suppress linked time values
		QueryFuture<? extends List<? extends Answer>> map21  = deleteLinkedAllValuesTimeEventByResourceId(transaction, idresource, relationtypevent);

		
		// suppress linked time
		QueryFuture<? extends List<? extends Answer>> map22  = deleteLinkedTimeEventByResourceId(transaction, idresource, relationtypevent);

		
		// suppress event relations
		QueryFuture<? extends List<? extends Answer>> map3  = deleteEventRelationByResourceId(transaction, idresource, relationtypevent);

		// suppress resource
		QueryFuture<? extends List<? extends Answer>> map4  = deleteTheResourceById(transaction, idresource);

		return map4;

	}

	protected // periodic time delete
	QueryFuture<? extends List<? extends Answer>> deletePeriodOfTimeById(GraknClient.Transaction transaction, String idevent)  {

		String deleteQuery="";

		deleteQuery+= "match $periodicrelation ("
				+ "periodoftime : $periodoftime,"
				+ "startwhendate : $date1,"
				+ "start enddate : $date2,)"
				+ "isa PeriodicRelation;\n";
		deleteQuery+= "delete $date1 isa TimeDate;";
		deleteQuery+= "delete $date2 isa TimeDate;";
		deleteQuery+= "delete $periodoftime isa PeriodOfTime;";
		deleteQuery+= "delete $periodicrelation isa periodicrelation;";

		final GraqlDelete parseddelete = Graql.parse(deleteQuery).asDelete();

		QueryFuture<? extends List<? extends Answer>> map  = transaction.execute(parseddelete);

		return map;

	}

	// metatype
	abstract String getMetaTypeValue();

	// class relations value
	abstract String getClass_ValueEventRelations();

	// class relations type value
	abstract String getClass_TypeValueEventRelations();

	// Post calculation set
	protected String PostCalculationQuerySet(String nametag, String valnum1, String set)  {

		/*String graqlInsertQuery= "$Value-PPOSTCALCUL isa StringValue, has IdValue \"PCRPPOSTCALCUL-"+valnum1+ "\", has StringValueAttribute \"" + set + "\";\n";
		graqlInsertQuery+= "$ValueRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, resource : $resource) isa ValueEventPCRTestRelations" + ";\n";
		graqlInsertQuery+= "$ValueTypeRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, typevalue : $TypeValue-PPOSTCALCUL) isa TypeValueEventPCRTestRelations ;\n";
		 */

		String graqlInsertQuery = insertValue (getMetaTypeValue(),nametag, nametag+"-"+set, valnum1, "StringValue", set, "$resource", getClass_ValueEventRelations(), getClass_TypeValueEventRelations());


		return graqlInsertQuery;
	}

	// Post calculation set
	protected String PCRPostCalculationQuerySet(String prefixId, String nametag, String valnum1, String set)  {

		/*String graqlInsertQuery= "$Value-PPOSTCALCUL isa StringValue, has IdValue \"PCRPPOSTCALCUL-"+valnum1+ "\", has StringValueAttribute \"" + set + "\";\n";
		graqlInsertQuery+= "$ValueRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, resource : $resource) isa ValueEventPCRTestRelations" + ";\n";
		graqlInsertQuery+= "$ValueTypeRelationPPOSTCALCUL (value : $Value-PPOSTCALCUL, typevalue : $TypeValue-PPOSTCALCUL) isa TypeValueEventPCRTestRelations ;\n";
		 */

		String graqlInsertQuery = insertValue (prefixId,nametag, valnum1, "StringValue", set, "$resource", getClass_ValueEventRelations(), getClass_TypeValueEventRelations());


		return graqlInsertQuery;
	}

	// Post calculation of derivated (speed)
	protected void ReinitPostCalculationTag(GraknClient.Session session, String nametag, String previousvaluetag, String newvaluetag)  {


		try
		{
			Boolean okanswers = true;
			int nb=0;

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			while (okanswers==true)
			{
				okanswers=false;


				String graqlQuery11 = "match $valuepostcalcul isa Value"
						+ ", has IdValue $idvalue"
						+ ", has TypeValueId \"" +  nametag +"-" + previousvaluetag + "\";";
				//						+ 	", has StringValueAttribute \"" + previousvaluetag+ "\";";
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

					String idvalue1 = (String) answer11.get("idvalue").asAttribute().value();
					System.out.println(idvalue1);

					int posnum=idvalue1.indexOf('-');
					String valnum1=idvalue1.substring(posnum+1);

					String graqlQuery12 = "match $valuepostcalcul id " + idpostcalculvalue1+ ";";
					graqlQuery12+=		"(value : $valuepostcalcul, resource : $resource) isa ValueRelation;";
					graqlQuery12+= 	"get;limit "+ maxGet + ";\n";

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{	

						Entity resource= answer12.get("resource").asEntity();

						// delete old post calcul
						QueryFuture<? extends List<? extends Answer>> map61  = PostCalculationQueryDeleteById(transaction, idpostcalculvalue1);


						// insertion des valeurs
						String graqlInsertQuery = "match $resource id "+ resource.id().toString()+ ";";				
						graqlInsertQuery+=		"$TypeValue-PPOSTCALCUL isa TypeValue, has IdValue  \""+ nametag +"\";";

						graqlInsertQuery+= "insert ";

						// update de postcalcul
						graqlInsertQuery += PostCalculationQuerySet(nametag, valnum1, newvaluetag);

						QueryFuture<List<ConceptMap>> map7 = transaction.execute((GraqlInsert)parse(graqlInsertQuery));
						List<ConceptMap> answers7= map7.get();

						System.out.println(answers7.toString());

						nb++;
						System.out.println("Reinit : " + nb);
					}// for answer12
				} // for answer11
				transaction.commit();
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
			} // while ok

			transaction.commit();
			System.out.println("Reinit " + nametag + "OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	protected void retrieveColumnsTitles(Json map)
	{

		Collection<String> keys = map.asMap().keySet();

		List<Integer> icols = new ArrayList<Integer>(); 
		for (String key :keys)
		{
			int ikey=Integer.parseInt(key);
			icols.add(ikey);
		}

		Collections.sort(icols);

		for (Integer icol:icols)
		{
			String col=""+icol;

			String titre = map.at(col).asString();
			titres.add(titre);
		}
	}

	String GraphQlDoubleFormat (double value)
	{
		Locale locale  = new Locale("en", "UK");
		DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
		String st=df.format(value);

		return st;
	}
	
	


}
