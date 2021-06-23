package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.util.Collection;

import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputClassOfValues extends InputSetOfData {

	static final String ClassOfValuesTypeValue_MetaTypeValue = "COV";
	static final String ClassOfValues_ValueEventRelations = "ValueClassOfValuesRelations";
	static final String ClassOfValues_TypeValueEventRelations = "TypeValueClassOfValuesRelations";
	static final String ClassOfValues= "ClassValues";
	String getMetaType() {return ClassOfValuesTypeValue_MetaTypeValue;}
	
	public InputClassOfValues(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
	}
	
	public InputClassOfValues(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice);
	}
	
	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {
			
		// TODO Auto-generated method stub
		return initialiseInputsClassOfValues(inputs);
	}

	// master data département
	private Collection<Input> initialiseInputsClassOfValues(Collection<Input>inputs) {

		// data
		inputs.add(new Input(setOfData.getFilename()) {
			@Override
			public String template(Json classOfValues) {

				String graqlInsertQuery="";
				try
				{
					if (classOfValues.at("TypeValue")!=null)
					{
						indice++;
						if (indice<=maxIndice && indice>=minIndice)
						{

							String typeValue=classOfValues.at("TypeValue").asString();
							double min=-9999999999.9;
							if (classOfValues.at("Min").isNull()==false)
							{
								String smin=classOfValues.at("Min").asString();
								min=Double.parseDouble(smin);
							}
							double max= 9999999999.9;
							if (classOfValues.at("Max").isNull()==false)
							{
								String smax=classOfValues.at("Max").asString();
								max=Double.parseDouble(smax);
							}
							String labelValue=classOfValues.at("Label").asString();

							String keyValue=typeValue+"-"+labelValue;
							keyValue=Integer.toHexString(keyValue.hashCode());
							
							graqlInsertQuery=  "insert ";

							graqlInsertQuery+= "$ClassOfValues" + indice.toString() +" isa "+ ClassOfValues;
							graqlInsertQuery+= ", has IdValue \""+ ClassOfValuesTypeValue_MetaTypeValue+ "-"+typeValue+"-"+keyValue + "\"";
							graqlInsertQuery+=	", has TypeValueId \"" + typeValue + "\"";
							graqlInsertQuery+=	", has MinValueAttribute " + GraphQlDoubleFormat(min);
							graqlInsertQuery+=	", has MaxValueAttribute " + GraphQlDoubleFormat(max);
							graqlInsertQuery+=	", has LabelValueAttribute \"" + labelValue + "\"";
							graqlInsertQuery+=  ";";
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
		return ClassOfValues_ValueEventRelations;
	}

	@Override
	String getClass_TypeValueEventRelations() {
		// TODO Auto-generated method stub
		return ClassOfValues_TypeValueEventRelations;
	}

	@Override
	String GetValue_Eventrelation() {
		// TODO Auto-generated method stub
		return ClassOfValues_ValueEventRelations;
	}

	@Override
	String getTypevalue_Eventrelations() {
		// TODO Auto-generated method stub
		return ClassOfValues_TypeValueEventRelations;
	}
}
