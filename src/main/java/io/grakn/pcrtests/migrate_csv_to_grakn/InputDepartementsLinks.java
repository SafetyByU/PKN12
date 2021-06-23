package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.util.Collection;

import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputDepartementsLinks extends InputSetOfData {

	static final String DepartementLinksTypeValue_MetaTypeValue = "DL";
	static final String DepartementLinksClass_ValueEventRelations = "ValueDepartementLinksRelations";
	static final String DepartementLinksClass_TypeValueEventRelations = "TypeValueDepartementLinksRelations";
	static final String DepartementTypeValue_Distance = "Distance";

	static final String Neighbour 		= "Neighbour";
	static final String Axis 			= "Axis";
	static final String Distance 		= "Distance";

	String getMetaType() {return DepartementLinksTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return DepartementLinksClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return DepartementLinksClass_TypeValueEventRelations;};

	
	public InputDepartementsLinks(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
	}
	
	public InputDepartementsLinks(OptionImport optionImport, String filename, int myMaxGet, Long minIndice, Long maxIndice)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice);
	}
	
	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {
			
		// TODO Auto-generated method stub
		return initialiseInputsDepartementsLinks(inputs);
	}

	// master data département
	private Collection<Input> initialiseInputsDepartementsLinks(Collection<Input>inputs) {

		// data
		inputs.add(new Input(setOfData.getFilename()) {
			@Override
			public String template(Json departementlinks) {

				String graqlInsertQuery="";
				
				try
				{
					if (departementlinks.at("Departement")==null)
					{
						retrieveColumnsTitles(departementlinks);
					}
					else
					{


						indice++;
						if (indice<=maxIndice && indice>=minIndice)
						{
							String graqlMatchQuery="match ";
							graqlInsertQuery="\ninsert ";

							String codeGLN=departementlinks.at("Departement").asString();
							graqlMatchQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", codeGLN);

							int col=1;
							Json jsongln2=null;
							String codeGLN2=null;
							double distance=-1.0;
							for (String titre:titres)
							{
								if (titre.contains(Neighbour))
								{
									jsongln2=departementlinks.at(titre);
									if (jsongln2.isNull()==false)
									{
										codeGLN2=departementlinks.at(titre).asString();
									
										graqlMatchQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", codeGLN2);
										graqlInsertQuery+= "$neighbourWhereRelation-" + indice.toString() + "-" + col +" (neighbourfrom : $Departement-" + codeGLN + ", neighbourto: $Departement-"+ codeGLN2+") isa NeighbourWhereRelation;";
									}
								
								}
								if (titre.contains(Axis))
								{
									jsongln2=departementlinks.at(titre);
									if (jsongln2.isNull()==false)
									{
										codeGLN2=departementlinks.at(titre).asString();
									}
								}
								if (titre.contains(Distance))
								{
									if (jsongln2.isNull()==false)
									{
										distance=departementlinks.at(titre).asDouble();


										graqlMatchQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", codeGLN2);
										graqlInsertQuery+= "$Value-Distance-"  + indice.toString() +"-"+ col ;
										graqlInsertQuery+= " isa DistanceDoubleValue";
										graqlInsertQuery+=", has DoubleValueAttribute " + distance;
										graqlInsertQuery+= 	", has IdValue \""+ DepartementLinksTypeValue_MetaTypeValue+ DepartementTypeValue_Distance+"-"+indice+"-"+ col+"\";";
										graqlInsertQuery+= "$axisWhereRelation-" + indice.toString() + "-" + col +" (where1 : $Departement-" + codeGLN + ", where2: $Departement-"+ codeGLN2 + ",distance : $Value-Distance-"  + indice.toString() + "-"+ col + ") isa AxisWhereRelation;";
									}
								}
								col++;
							}
							graqlInsertQuery=graqlMatchQuery+graqlInsertQuery;
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
