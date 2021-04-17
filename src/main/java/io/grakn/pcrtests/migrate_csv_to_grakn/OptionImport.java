package io.grakn.pcrtests.migrate_csv_to_grakn;


enum TypeImport
{
	IMPORTDPT,
	IMPORTSHOP,
	IMPORTPCRTEST,
	IMPORTDPTCOUVREFEU,
	IMPORTSHOPCONFINEMENT,
	IMPORTWEATHER,
	POSTCALCULPCRTEST,
	POSTCALCULLINKSPCRTEST
}


public class OptionImport extends Option {


	private TypeImport typeImport=null;
	
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
		
	}
}
