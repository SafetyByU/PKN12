package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import grakn.client.GraknClient.Session;

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
	
	public InputSetOfChronology(OptionImport optionImport, String filename, int myMaxGet, int minIndice, int maxIndice,DF_CASE myDateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{
		super (optionImport, filename,myMaxGet, minIndice, maxIndice);
		setOfChronology=new SetOfChronology();

		dateFilter=myDateFilter;
		this.minDate=myMinDate;
		this.maxDate=myMaxDate;

		this.mindays=myMindays;
		this.maxdays=myMaxdays;

	}
	
	public InputSetOfChronology(OptionImport optionImport, String filename, int myMaxGet, int minIndice, int maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{ 
		super (optionImport, filename,myMaxGet, minIndice, maxIndice);
		
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
}
