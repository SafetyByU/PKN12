package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.util.Collection;

import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputDepartements extends InputSetOfData {

	static final String DepartementTypeValue_MetaTypeValue = "DD";
	static final String DepartementClass_ValueEventRelations = "ValueEventDepartementRelations";
	static final String DepartementClass_TypeValueEventRelations = "TypeValueDepartementRelations";

	String getMetaType() {return DepartementTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return DepartementClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return DepartementClass_TypeValueEventRelations;};

	
	public InputDepartements(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
	}
	
	public InputDepartements(OptionImport optionImport, String filename, int myMaxGet, int minIndice, int maxIndice)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice);
	}
	
	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {
			
		// TODO Auto-generated method stub
		return initialiseInputsDepartements(inputs);
	}

	// master data département
	private Collection<Input> initialiseInputsDepartements(Collection<Input>inputs) {

		// data
		inputs.add(new Input(setOfData.getFilename()) {
			@Override
			public String template(Json departement) {

				String graqlInsertQuery="";
				try
				{
					if (departement.at("Code INSEE")!=null)
					{
						indice++;
						if (indice<=maxIndice && indice>=minIndice)
						{

							String codeGLN=departement.at("Code INSEE").asString();
							String labelGLN=departement.at("Département").asString();
							
							graqlInsertQuery+= "\ninsert ";

							graqlInsertQuery+= "$Departement-" + indice.toString() +" isa Departement , has Identity \"DD-"+indice.toString() + "\", has CodeGLN \"" + codeGLN + "\";";
							graqlInsertQuery+= "$GLNLabel-" + indice.toString() +" isa GLNLabel , has Language \"fr\", has LabelGLN \"" + labelGLN + "\";";

							graqlInsertQuery+= "$ValueRelation-Departement-Label-" + indice.toString() +" (GLNcode : $Departement-" + indice.toString() + " , GLNlabel : $GLNLabel-" + indice.toString()+") isa GLNRelation;";
						}

					}
				}
				catch (Exception e)
				{
					int y=0;
				}
				return (graqlInsertQuery);
			}
		});
		return inputs;
	}

	@Override
	String getMetaTypeValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getClass_ValueEventRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getClass_TypeValueEventRelations() {
		// TODO Auto-generated method stub
		return null;
	}
}
