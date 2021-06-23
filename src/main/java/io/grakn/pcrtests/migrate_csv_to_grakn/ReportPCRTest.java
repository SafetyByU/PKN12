package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Session;
import grakn.client.GraknClient.Transaction;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Attribute;
import grakn.client.concept.thing.Entity;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Report;



public class ReportPCRTest extends ReportSetOfChronology implements IReportSetOfChronology{

	static double maxFrequencyAmplitude=0.02;
	
	static private String reportValueName="valuePDivT";
	static private String reportSpeedName="speedPDivT";
	static private String reportAccelerationName="accelerationPDivT";

	public static double getMaxFrequencyAmplitude() {
		return maxFrequencyAmplitude;
	}

	public static String getReportValueName() {
		return reportValueName;
	}

	public static String getReportSpeedName() {
		return reportSpeedName;
	}

	public String getReportAccelerationName() {
		return reportAccelerationName;
	}
	PCRTest pcrtest=null;

	public Map<Long, String> getCl_age90Id() {
		return pcrtest.getCl_age90Id();
	}

	protected void initializeCaches(GraknClient.Session session)
	{
		super.initializeCaches(session);
		pcrtest.initializeCaches(session);
	}

	class MyReportPCRTest extends Report {


		ReportPCRTest reportpcrtest;

		public MyReportPCRTest(String path, OptionReport myOptionreport, ReportPCRTest reportpcrtest) {
			super(path, myOptionreport);
			// TODO Auto-generated constructor stub
			this.reportpcrtest=reportpcrtest;
		}

		@Override
		protected JsonArray queryDataToJson(Transaction transaction) {
			// TODO Auto-generated method stub
			return reportpcrtest.queryDataToJson(transaction);
		}

		@Override
		protected void initializeCaches(Session session) {
			// TODO Auto-generated method stub
			reportpcrtest.initializeCaches(session);
		}

		@Override
		protected void convertJsonToCSV(JsonArray items, String suffixCSV) {
			// TODO Auto-generated method stub
			reportpcrtest.convertJsonToCSV(items, suffixCSV);
		}

		@Override
		protected JsonArray fourierTransform(JsonArray items, String typeValue) {
			// TODO Auto-generated method stub
			return reportpcrtest.fourierTransform( items,  typeValue);
		}

		@Override
		protected JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue, double min, double max, boolean inside) {
			// TODO Auto-generated method stub
			return reportpcrtest.filterFrequencies(frequencies, typeValue, min, max, inside);
		}
	
		@Override
		protected JsonArray inverseFourierTransform(JsonArray filteredfrequencies, TypeValue frequency) {
			// TODO Auto-generated method stub
			return reportpcrtest.inverseFourierTransform(filteredfrequencies, frequency);
		}

		@Override
		protected JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue, boolean inside) {
			// TODO Auto-generated method stub
			return reportpcrtest.filterFrequencies(frequencies, typeValue, inside);
		}

		@Override
		protected String getReportAccelerationName() {
			// TODO Auto-generated method stub
			return reportpcrtest.getReportAccelerationName();
		}

		@Override
		protected JsonArray parseCSVToJson(String suffixcsv) throws FileNotFoundException {
			// TODO Auto-generated method stub
			return reportpcrtest.parseCSVToJson(suffixcsv);
		}

		@Override
		protected JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue) {
			// TODO Auto-generated method stub
			return reportpcrtest.filterFrequencies(frequencies, typeValue);
		}

		@Override
		protected JsonArray extractValues(JsonArray items, String typeValue) {
			// TODO Auto-generated method stub
			return reportpcrtest.extractValues(items, typeValue);

		}

		@Override
		protected JsonArray substractValues(JsonArray filteredtemporals, JsonArray temporals) {
			// TODO Auto-generated method stub
			return reportpcrtest.substractValues(filteredtemporals, temporals);
		}

	}

	public ReportPCRTest(String filereportpcrtests, OptionReport optionReport) {
		super(filereportpcrtests, optionReport);
		// TODO Auto-generated constructor stub
		pcrtest= new PCRTest(); 
	}

	public JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue) {
		// TODO Auto-generated method stub
		return filterFrequencies (frequencies,  typeValue, 0.0,  maxFrequencyAmplitude, true);
	}

	public JsonArray filterFrequencies(JsonArray frequencies, TypeValue typeValue, boolean inside) {
		// TODO Auto-generated method stub
		return filterFrequencies (frequencies, typeValue, 0.0,  maxFrequencyAmplitude, inside);
	}
	
	@Override
	public Collection<Report> initialize (Collection<Report>reports) {
		// TODO Auto-generated method stub
		return initialiseReportsPcrTests(reports);
	}

	// reports PCR
	private Collection<Report> initialiseReportsPcrTests (Collection<Report>reports) {

		// data
		reports.add(new MyReportPCRTest(setOfData.getFilename(), getOptionReport(), this));

		return reports;
	}

	@Override
	public JsonArray queryDataToJson(Transaction transaction) {
		// TODO Auto-generated method stub

		JsonArray jsonarraypcr = new JsonArray();

		try
		{

			Set<String> keysdept=getDepartementId().keySet();
			Set<Long> keyscl_age90=getCl_age90Id().keySet();
			Set<LocalDateTime> keysdate=getDateId().keySet();

			String[] detps=null;
			String[] clsage90=null;

			if (optionReport.getDept().length>0)
			{
				detps=optionReport.getDept();
			}
			else
			{
				detps=(String[])(keysdept.toArray());
			}

			if (optionReport.getDept().length>0)
			{
				clsage90=optionReport.getClage90();
			}
			else
			{
				clsage90=new String[keyscl_age90.size()];
				int i=0;
				for (Long keycl_age90:keyscl_age90)
				{
					clsage90[i]=keycl_age90.toString();
					i++;
				}
			}

			for (String dept : detps)
			{
				String idDepartement = getDepartementId().get(dept);

				for (String clage90 : clsage90)
				{
					String idCl_age90 = getCl_age90Id().get(Long.parseLong(clage90));

					String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
					graqlQuery11 += 	"$Cl_age90 id "+ idCl_age90 + ";";
					graqlQuery11 += 	"$what isa " + PCRTest.PCRTestClass_WhatPCRTest + ", has Attribut-"+ Cohort.CohortTypeValue_cl_age90 +" $Cl_age90, has Identity $identity;";
					graqlQuery11+=		"(time : $timedate, localization : $Departement, object : $what) isa EventPCRTestRelations;";			
					graqlQuery11+=		"$timedate isa TimeDate, has EventDate $attributedate;";				
					//					graqlQuery11+=		"(resource : $what, value : $value) isa ValueRelation;";	
					//					graqlQuery11+=		"$value isa DoubleValueDerivated, has TypeValueId \"" + PCRTest.PCRTestTypeValue_AVPdivT + "\"";	
					//					graqlQuery11+=		", has " + SetOfChronology.AttributeValue_value + " $AVPdivT";	
					//					graqlQuery11+=		", has " + SetOfChronology.AttributeValue_speed +" $SPDivT";
					//					graqlQuery11+=		", has " + SetOfChronology.AttributeValue_acceleration + " $APDivT;";
					graqlQuery11+=		"get;sort $attributedate asc;\n";

					System.out.println(graqlQuery11);

					QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
					List<ConceptMap> answers11= map11.get();

					System.out.println("derivated what existing values : "+ answers11.size());

					for(ConceptMap answer11:answers11)
					{
						JsonObject jo = new JsonObject();


						Entity what= answer11.get("what").asEntity();
						String idWhat=what.id().toString();

						String identity = (String) answer11.get("identity").asAttribute().value();
						System.out.println("identity :" + identity);
						jo.addProperty("identity", identity);

						jo.addProperty("dept", dept);
						jo.addProperty("clage90", clage90);

						LocalDateTime valuedate = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
						System.out.println(valuedate);

						jo.addProperty("valuedate", valuedate.toString());
						String graqlQuery12 = "match $what id "+ idWhat + ";";
						graqlQuery12+=		"(resource : $what, value : $value) isa ValueRelation;";	
						graqlQuery12+=		"$value isa DoubleValueDerivated, has TypeValueId \"" + PCRTest.PCRTestTypeValue_AVPdivT + "\"";	
						graqlQuery12+=		", has " + SetOfChronology.AttributeValue_value + " $AVPdivT";	
						graqlQuery12+=		", has " + SetOfChronology.AttributeValue_speed +" $SPdivT";
						graqlQuery12+=		", has " + SetOfChronology.AttributeValue_acceleration + " $APdivT;";
						graqlQuery12+=		"get;\n";

						System.out.println(graqlQuery12);

						QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
						List<ConceptMap> answers12= map12.get();

						for(ConceptMap answer12:answers12)
						{


							Attribute<Object> at1= answer12.get("AVPdivT").asAttribute();
							Attribute<Object> at2= answer12.get("SPdivT").asAttribute();
							Attribute<Object> at3= answer12.get("APdivT").asAttribute();

							if (at1.id().compareTo(at2.id())!=0  &&
									at1.id().compareTo(at3.id())!=0) 
							{
								Double valuePdivT = (Double) answer12.get("AVPdivT").asAttribute().value();
								System.out.println("attributePdivT :" + valuePdivT);
								jo.addProperty(reportValueName, valuePdivT);

								Double speedPdivT = (Double) answer12.get("SPdivT").asAttribute().value();
								System.out.println("SpeedPdivT :" + speedPdivT);
								jo.addProperty(reportSpeedName, speedPdivT);

								Double accelerationPdivT = (Double) answer12.get("APdivT").asAttribute().value();
								System.out.println("AccelerationPdivT :" + accelerationPdivT);
								jo.addProperty(reportAccelerationName, accelerationPdivT);
							}
						}
						jsonarraypcr.add(jo);

					}
				}
			}
		}

		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
		return jsonarraypcr;
	}
	

	


}
