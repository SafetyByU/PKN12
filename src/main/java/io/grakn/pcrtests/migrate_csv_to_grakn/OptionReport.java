package io.grakn.pcrtests.migrate_csv_to_grakn;

public class OptionReport extends Option {

	public boolean isOffline() {
		return offline;
	}

	enum TypeReport
	{
		REPORTPCRTEST
	}
	private TypeReport typeReport=null;
	private boolean offline = false;
	
	static public OptionReport evaluateArgs (String[] args)
	{
		TypeReport typeReport=null;
		Boolean offline=false;
		
		Boolean nextreport=false;
		
		for (String arg:args)
		{
			// report type
			if (nextreport==true)
			{
				typeReport=TypeReport.valueOf(arg);
				nextreport=false;
			}
			
			// option report
			if (arg.equals("-r"))
			{
				nextreport=true;
			}	
			// option report
			if (arg.equals("-ro"))
			{
				nextreport=true;
				offline=true;
			}
		}
		if (typeReport!=null)
			return new OptionReport(typeReport, offline, args);
		else
			return null;
	}
	
	public TypeReport getTypeReport() {
		return typeReport;
	}

	public OptionReport (TypeReport myTypeReport, boolean myOffline, String[] args)
	{
		super (args);
		typeReport=myTypeReport;
		offline=myOffline;
		
	}
}
