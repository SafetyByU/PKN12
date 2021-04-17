package io.grakn.pcrtests.migrate_csv_to_grakn;

public class PCRFilesOptions {
	
	private Boolean options=false;
	private OptionImport optImp =null;
	private OptionReport optRep = null;
	
	PCRFilesOptions (String[] args)
	{
		optImp = OptionImport.evaluateArgs(args);
		optRep = OptionReport.evaluateArgs(args);
		
		if (optImp!=null || optRep != null)
			options=true;
	}

	public Boolean getOptions() {
		return options;
	}

	public OptionImport getOptImp() {
		return optImp;
	}

	public OptionReport getOptRep() {
		return optRep;
	}
}
