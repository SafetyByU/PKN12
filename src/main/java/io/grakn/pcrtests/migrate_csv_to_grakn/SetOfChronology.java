package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Attribute;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;

enum RelationTypeEvent
{
	EVENTRELATION,
	WHORELATION,
	WHATRELATION,
	WHYRELATION,
	TIMERELATION,
	WHERERELATION
}
public class SetOfChronology implements ISetOfChronology{

	static final String AttributeValue_value="DoubleValueAttribute";
	static final String AttributeValue_speed="SpeedValueAttribute";
	static final String AttributeValue_acceleration="AccelerationValueAttribute";
	
	Map<LocalDateTime, String> dateId = new HashMap<>();

	public Map<LocalDateTime, String> getDateId() {
		return dateId;
	}
	
	LocalDate minDate=LocalDate.parse((CharSequence)"1900-01-01");
	LocalDate maxDate=LocalDate.parse((CharSequence)"9999-01-01");
	
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
	
	public SetOfChronology()
	{
	}
	
	public SetOfChronology(LocalDate myMinDate, LocalDate myMaxDate)
	{
	
		this.minDate=myMinDate;
		this.maxDate=myMaxDate;
		
	}
	
	protected void initializeDatesCache(GraknClient.Session session)
	{
		try
		{

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			String graqlQuery1 = "match ";
			graqlQuery1+=		"$eventDate isa EventDate;get;";	

			QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery1));
			List<ConceptMap> answers1= map1.get();

			for(ConceptMap answer1:answers1)
			{	
				Attribute<Object> eventDate1= answer1.get("eventDate").asAttribute();
				String idEventDate1=eventDate1.id().toString();
				//System.out.println("idEventDate1 : "  + idEventDate1);

				LocalDateTime valuedate1 = (LocalDateTime) eventDate1.value();		
				//System.out.println("date : "  + valuedate1);

				dateId.put(valuedate1, idEventDate1);

			} // for answer1

		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
	protected void initializeCaches(GraknClient.Session session)
	{
		//super.initializeCaches(session);
		initializeDatesCache(session);
	}

}
