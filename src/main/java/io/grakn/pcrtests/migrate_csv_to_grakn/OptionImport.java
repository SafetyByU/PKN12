package io.grakn.pcrtests.migrate_csv_to_grakn;


enum TypeImport
{
	IMPORTCLASSDERIVATED,
	IMPORTCLASSTEMPERATURE,
	IMPORTDPT,
	IMPORTDPTLINKS,
	IMPORTSHOP,
	IMPORTPCRTEST,
	IMPORTVACCINPERTYPE,
	IMPORTVACCINPERAGE,
	IMPORTDPTCOUVREFEU,
	IMPORTSHOPCONFINEMENT,
	IMPORTWEATHER,
	POSTCALCULPCRTEST,
	POSTCALCULLINKSPCRTEST,
	POSTCALCULNEXTDATEPCRTEST,
	POSTCALCULSAMEDATEPCRTEST,
	POSTCALCULWEATHER,
	POSTCALCULWEATHERNEXTDATE,
	POSTCALCULWEATHERCLEAN, 
	POSTCALCULWEATHERSAMEDATEPCRTEST,
	POSTCALCULVACCIN, 
	POSTCALCULLINKSVACCIN, 
	POSTCALCULVACCINNEXTDATE,
	POSTCALCULVACCINSAMEDATEPCRTEST,
	POSTCALCULDEPARTEMENTCONFINEMENTCLEAN, 
	POSTCALCULDEPARTEMENTSAMEDATEPCRTEST,
	POSTCALCULSHOPCONFINEMENTCLEAN,
	POSTCALCULSHOPSAMEDATEPCRTEST
}


public class OptionImport extends Option {


	private TypeImport typeImport=null;
	
	private Boolean onlyDoubleClean=true;

	private Boolean resetRelations=true;

	
	public Boolean getOnlyDoubleClean() {
		return onlyDoubleClean;
	}

	public void setOnlyDoubleClean(Boolean onlyDoubleClean) {
		this.onlyDoubleClean = onlyDoubleClean;
	}

	public TypeImport getTypeImport() {
		return typeImport;
	}

	static public OptionImport evaluateArgs (String[] args)
	{
		TypeImport typeImport=null;
		Boolean nextimport=false;

				
		for (String arg:args)
		{
			// import type
			if (nextimport==true)
			{
				typeImport=TypeImport.valueOf(arg);
				nextimport=false;
			}
			
			// option import
			if (arg.equals("-i"))
			{
				nextimport=true;
			}	
			
		}
		if (typeImport!=null)
			return new OptionImport(typeImport, args);
		else
			return null;
	}
	
	public OptionImport (TypeImport myTypeImport, String[] args)
	{
		super (args);
		typeImport=myTypeImport;
		
		for (String arg:args)
		{
			// update
			if (arg.equals("-c"))
			{
				onlyDoubleClean=false;
			}
			
			// reset
			if (arg.equals("-r"))
			{
				resetRelations=true;
			}
			
		}
		
	}

	public Boolean getResetRelations() {
		return resetRelations;
	}

	public void setResetRelations(Boolean resetRelations) {
		this.resetRelations = resetRelations;
	}
}
