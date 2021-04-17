package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Session;
import grakn.client.GraknClient.Transaction;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;
enum TypeValue
{
	AVERAGE,
	SPEED,
	ACCELERATION,
	FREQUENCY,
	TEMPORAL,
	AMPLITUDE,
	AMPLITUDE_REAL,
	AMPLITUDE_IMAGINARY
}

public abstract class ReportSetOfData implements IReportSetOfData{

	SetOfData setOfData;
	OptionReport optionReport;

	public SetOfData getSetOfData() {
		return setOfData;
	}

	public OptionReport getOptionReport() {
		return optionReport;
	}

	ReportSetOfData(String filename, OptionReport myOptionReport)
	{
		setOfData=new SetOfData();
		setOfData.setFilename(filename);
		optionReport=myOptionReport;
	}

	public Map<String, String> getDepartementId() {
		return setOfData.getDepartementId();
	}

	protected void initializeCaches(Session session) {
		setOfData.initializeCaches(session);
	}

	public void postCalculate(GraknClient.Session session)
	{
		initializeCaches(session);
	}

	protected abstract JsonArray queryDataToJson(Transaction transaction);

	public ArrayList <String> ExtractJsonToTitles (JsonArray jsonarray){
		ArrayList <String> titles = new ArrayList<String>();

		Iterator<JsonElement> it=jsonarray.iterator();
		while (it.hasNext())
		{
			JsonElement json=it.next();
			JsonObject jsonobj=json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

			for (Entry<String, JsonElement>entry : entries)
			{
				String title=entry.getKey();
				if (titles.contains(title)==false)
				{
					titles.add(title);
				}
			}
		}
		return titles;
	}


	public void convertJsonToCSV (JsonArray jsonarray, String suffixcsv){

		try {


			String filename=setOfData.getFilename()+"-"+suffixcsv+".csv";

			FileWriter csvWriter = new FileWriter(filename);


			// titles
			ArrayList<String> titles= ExtractJsonToTitles(jsonarray);

			Boolean firstTitle=true;
			for (String title : titles) {
				if (firstTitle==false)
				{
					csvWriter.append(";");
				}
				csvWriter.append(title);
				firstTitle=false;
			}
			csvWriter.append("\n");

			// next values
			Iterator<JsonElement> it=jsonarray.iterator();
			while (it.hasNext())
			{
				JsonElement json=it.next();
				JsonObject jsonobj=json.getAsJsonObject();
				Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

				Boolean firstElt=true;
				for (Entry<String, JsonElement>entry : entries)
				{
					JsonElement jsonelt=entry.getValue();
					if (firstElt==false)
					{
						csvWriter.append(";");
					}
					if (jsonelt.isJsonNull()==false)
					{
						JsonPrimitive jsonprimitive=jsonelt.getAsJsonPrimitive();

						if (jsonprimitive.isString())
						{
							csvWriter.append(jsonprimitive.getAsString());
						}
						else if (jsonprimitive.isNumber())
						{
							double dbl=jsonelt.getAsDouble();
							Locale locale  = new Locale("en", "UK");
							DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));
							String stdbl=df.format(dbl);
							stdbl=stdbl.replace('.', ',');
							csvWriter.append(stdbl);

						}
						else
						{
							csvWriter.append(jsonprimitive.toString());
						}

					}
					firstElt=false;
				}
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	static protected Reader getReader(String RelativePath) throws FileNotFoundException {
		return new InputStreamReader(new FileInputStream(RelativePath));
	}

	public JsonArray parseCSVToJson(String suffixcsv) throws FileNotFoundException {
		JsonArray items = new JsonArray();
		//ArrayList<Json> items = new ArrayList<>();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setDelimiterDetectionEnabled(true, ';');
		CsvParser parser = new CsvParser(settings);

		String filename=setOfData.getFilename()+"-"+suffixcsv+".csv";
		parser.beginParsing(getReader(filename));

		String[] columns = parser.parseNext();
		String[] row;

		// put the title of columns
		//Json item1 = Json.object();
		JsonObject item1 = new JsonObject();
		for (int i = 0; i <columns.length; i++) {
			String coli= "" + i;
			//item1.set(coli,columns[i]);
			item1.addProperty(coli, columns[i]);
		}
		items.add(item1);
		try
		{
			while ((row = parser.parseNext()) != null) {
				//Json item = Json.object();
				JsonObject item = new JsonObject();

				for (int i = 0; i <row.length; i++) {

					String stdbl=row[i];
					stdbl=stdbl.replace(',', '.');

					//					Locale locale  = new Locale("en", "UK");
					//					DecimalFormat df = new DecimalFormat("#.####################", new DecimalFormatSymbols(locale));

					boolean parse=false;
					try
					{
						int number=Integer.parseInt(stdbl);
						item.addProperty(columns[i], stdbl);
						parse = true;
					}
					catch(NumberFormatException e)
					{
					}
					if (parse==false)
					{
						try
						{
							double number=Double.parseDouble(stdbl);
							item.addProperty(columns[i], number);
							parse = true;
						}
						catch(NumberFormatException e)
						{
						}
					}
					if (parse==false)
					{
						item.addProperty(columns[i], row[i]);
					}

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
	// fourier transform
	public JsonArray fourierTransform (JsonArray jsonarray, TypeValue typevalue, TransformType transformType) 
	{
		return fourierTransform (jsonarray, typevalue.name(), transformType);
	}

	// fourier transform
	public JsonArray fourierTransform (JsonArray jsonarray, String typevalue, TransformType transformType) 
	{
		// values to transform
		ArrayList <Double> dataarray = new ArrayList<>();
		Iterator<JsonElement> it=jsonarray.iterator();
		while (it.hasNext())
		{
			JsonElement json=it.next();
			JsonObject jsonobj=json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

			for (Entry<String, JsonElement>entry : entries)
			{
				JsonElement jsonelt=entry.getValue();
				String title=entry.getKey();

				if (title.equals(typevalue))
				{
					if (jsonelt.isJsonNull()==false)
					{
						JsonPrimitive jsonprimitive=jsonelt.getAsJsonPrimitive();

						if (jsonprimitive.isNumber())
						{
							double dbl=jsonelt.getAsDouble();
							dataarray.add(dbl);

						}				
					}
				}
			}
		}
		// table in base 2 lenght for FFT calculaltion
		int sizepower2=dataarray.size();
		sizepower2=(int) (Math.log(sizepower2)/Math.log(2));
		sizepower2=(int) Math.pow(2, sizepower2);
		double[] datas= new double [sizepower2];
		int i=0;
		for (double data : dataarray)
		{
			datas[i]=data;
			i++;
			if (i>=sizepower2)
				break;
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] complexs = fastFourierTransformer.transform(datas, transformType);

		JsonArray jsonarrayFFT = new JsonArray();


		for(int j=0; j<complexs.length; ++j) {
			final double abs = complexs[j].abs();
			//double powerSpectrum = abs * abs;
			double powerSpectrum = abs;

			JsonObject jo = new JsonObject();
			if (transformType.equals(TransformType.FORWARD))
				jo.addProperty(TypeValue.FREQUENCY.name(), j);
			else 
				jo.addProperty(TypeValue.TEMPORAL.name(), j);

			jo.addProperty(TypeValue.AMPLITUDE.name(), powerSpectrum);
			jo.addProperty(TypeValue.AMPLITUDE_REAL.name(), complexs[j].getReal());
			jo.addProperty(TypeValue.AMPLITUDE_IMAGINARY.name(), complexs[j].getImaginary());

			jsonarrayFFT.add(jo);
		}

		return jsonarrayFFT;
	}

	// fourier transform to identify frequencies
	public JsonArray fourierTransform (JsonArray jsonarray, String typevalue) 
	{

		return fourierTransform(jsonarray, typevalue, TransformType.FORWARD);
	}

	// inverse fourier transform to come back to temporal model
	public JsonArray inverseFourierTransform (JsonArray jsonarray, TypeValue typevalue) 
	{

		return fourierTransform(jsonarray, typevalue, TransformType.INVERSE);
	}

	// fourier transform to identify frequencies
	public JsonArray filterFrequencies (JsonArray jsonarray, TypeValue typeValue, double min, double max, boolean inside)
	{
		JsonArray jsonarrayFiltered = new JsonArray();

		// max of values
		double maxvalue=0.0;
		Iterator<JsonElement> it=jsonarray.iterator();
		while (it.hasNext())
		{
			JsonElement json=it.next();
			JsonObject jsonobj=json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

			for (Entry<String, JsonElement>entry : entries)
			{

				String title=entry.getKey();

				if (title.equals(TypeValue.AMPLITUDE.name())==true)
				{
					JsonElement jsonelt=entry.getValue();
					double dbl=jsonelt.getAsDouble();

					if (dbl>maxvalue)
						maxvalue=dbl;
				}
			}
		}
		// values to transform
		double relmin=maxvalue*min;
		double relmax=maxvalue*max;


		Iterator<JsonElement> it2=jsonarray.iterator();
		while (it2.hasNext())
		{
			JsonElement json=it2.next();
			JsonObject jsonobj=json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

			JsonObject jsonobjfiltered= new JsonObject();
			boolean cut=false;

			for (Entry<String, JsonElement>entry : entries)
			{

				String title=entry.getKey();


				if (title.equals(typeValue.name())==true)
				{
					JsonElement jsonelt=entry.getValue();
					jsonobjfiltered.add(typeValue.name(), jsonelt);
				}

				if (title.equals(TypeValue.AMPLITUDE.name())==true)
				{
					JsonElement jsonelt=entry.getValue();
					double dbl=jsonelt.getAsDouble();


					if ((inside==false && (dbl<relmin || dbl>relmax))
							|| (inside == true  && (dbl>relmin && dbl<relmax)))
					{
						jsonobjfiltered.add(TypeValue.AMPLITUDE.name(), jsonelt);
					}
					else
					{
						JsonElement jsoncut=new JsonPrimitive(0.0);
						jsonobjfiltered.add(TypeValue.AMPLITUDE.name(), jsoncut);
						cut=true;
					}
				}

				if (title.equals(TypeValue.AMPLITUDE_REAL.name())==true)
				{
					JsonElement jsonelt=entry.getValue();
					double dbl=jsonelt.getAsDouble();


					if (cut==false)
					{
						jsonobjfiltered.add(TypeValue.AMPLITUDE_REAL.name(), jsonelt);
					}
					else
					{
						JsonElement jsoncut=new JsonPrimitive(0.0);
						jsonobjfiltered.add(TypeValue.AMPLITUDE_REAL.name(), jsoncut);
					}
				}

				if (title.equals(TypeValue.AMPLITUDE_IMAGINARY.name())==true)
				{
					JsonElement jsonelt=entry.getValue();
					double dbl=jsonelt.getAsDouble();


					if (cut==false)
					{
						jsonobjfiltered.add(TypeValue.AMPLITUDE_IMAGINARY.name(), jsonelt);
					}
					else
					{
						JsonElement jsoncut=new JsonPrimitive(0.0);
						jsonobjfiltered.add(TypeValue.AMPLITUDE_IMAGINARY.name(), jsoncut);
					}
				}
			}
			jsonarrayFiltered.add(jsonobjfiltered);

		}
		return jsonarrayFiltered;
	}


	public JsonArray extractValues(JsonArray jsonarray, String typeValue) {
		// values to extract
		JsonArray jsonarrayExtracted = new JsonArray();
		Iterator<JsonElement> it=jsonarray.iterator();
		int j=0;
		while (it.hasNext())
		{
			JsonElement json=it.next();
			JsonObject jsonobj=json.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries=jsonobj.entrySet();

			for (Entry<String, JsonElement>entry : entries)
			{
				JsonElement jsonelt=entry.getValue();
				String title=entry.getKey();

				if (title.equals(typeValue))
				{
					JsonObject jsonobjextract= new JsonObject();

					jsonobjextract.addProperty(TypeValue.TEMPORAL.name(), j);

					jsonobjextract.add(TypeValue.AMPLITUDE.name(), jsonelt);
					jsonarrayExtracted.add(jsonobjextract);
				}
			}
			j++;
		}

		return jsonarrayExtracted;
	}

	public JsonArray substractValues(JsonArray temporals, JsonArray filtertemporals) {

		JsonArray jsonarrayFiltered = new JsonArray();

		// max of values
		Iterator<JsonElement> it1=temporals.iterator();
		Iterator<JsonElement> it2=filtertemporals.iterator();
		int j=0;

		while (it1.hasNext())
		{

			JsonElement json1=it1.next();
			JsonObject jsonobj1=json1.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries1=jsonobj1.entrySet();

			if (it2.hasNext()==false)
			{
				it2=filtertemporals.iterator();
			}

			JsonElement json2=it2.next();
			JsonObject jsonobj2=json2.getAsJsonObject();
			Set<Entry<String, JsonElement>> entries2=jsonobj2.entrySet();

			double dbl=0.0;
			for (Entry<String, JsonElement>entry1 : entries1)
			{

				String title1=entry1.getKey();
				
				
				if (title1.equals(TypeValue.AMPLITUDE.name())==true)
				{
					JsonElement jsonelt1=entry1.getValue();
					dbl=jsonelt1.getAsDouble();
				}
			}
			double dbl2=0.0;

			for (Entry<String, JsonElement>entry2 : entries2)
			{
				String title2=entry2.getKey();

				if (title2.equals(TypeValue.AMPLITUDE.name())==true)
				{
					JsonElement jsonelt=entry2.getValue();
					dbl2=jsonelt.getAsDouble();


				}
				if (title2.equals(TypeValue.AMPLITUDE_REAL.name())==true)
				{
					JsonElement jsonelt=entry2.getValue();
					Double real2=jsonelt.getAsDouble();
					if (real2<0)
					{
						dbl2=-dbl2;
					}
					dbl2=dbl-dbl2;
					JsonElement jsoncut=new JsonPrimitive(dbl2);
					JsonObject jsonobjfiltered= new JsonObject();
					jsonobjfiltered.addProperty(TypeValue.TEMPORAL.name(), j);
					jsonobjfiltered.add(TypeValue.AMPLITUDE.name(), jsoncut);
					jsonarrayFiltered.add(jsonobjfiltered);
				}
			}
			j++;
		}
		return jsonarrayFiltered;
	}
}
