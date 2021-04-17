package io.grakn.pcrtests.migrate_csv_to_grakn;

import io.grakn.pcrtests.migrate_csv_to_grakn.OptionReport.TypeReport;

abstract public class Option  {

	protected String filename=null;
	protected String[] dept=null;
	protected String[] clage90=null;
	
	Option(String[] args)
	{
	
		Boolean nextfile=false;
		Boolean nextdept=false;
		Boolean nextclage90=false;
		
		for (String arg:args)
		{
			// filename
			if (nextfile==true)
			{
				filename=arg;
				nextfile=false;
			}
			// departement 
			if (nextdept==true)
			{
				dept=arg.split("-");
				nextdept=false;
			}
			// class age
			if (nextclage90==true)
			{
				clage90=arg.split("-");
				nextclage90=false;
			}
			
			// next arg is filename
			if (arg.equals("-f"))
			{
				nextfile=true;
			}
			// next arg is departement
			if (arg.equals("-dept"))
			{
				nextdept=true;
			}
			// next arg is class age
			if (arg.equals("-ca"))
			{
				nextclage90=true;
			}
			
		}

	}

	public String getFilename() {
		return filename;
	}

	public String[] getDept() {
		return dept;
	}

	public String[] getClage90() {
		return clage90;
	}
}
