package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputDepartementsCouvreFeu extends InputSetOfChronology {

	static final String DepartementConfinementTypeValue_MetaTypeValue = "DC";
	static final String DepartementConfinementClass_ValueEventRelations = "ValueEventDepartementConfinementRelations";
	static final String DepartementConfinementClass_TypeValueEventRelations = "TypeValueDepartementConfinementRelations";
	static final String DepartementConfinementClass_WhatDepartementConfinement="WhatDepartementConfinement";
	
	String DepartementConfinementTypeValue_departement="departement";
	static final String DepartementConfinementTypeAttributeValue_departement = "StringValueAttribute";
	
	String getMetaType() {return DepartementConfinementTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return DepartementConfinementClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return DepartementConfinementClass_TypeValueEventRelations;};


	InputDepartementsCouvreFeu(OptionImport optionImport,String filename)
	{
		super(optionImport, filename);
	}

	public InputDepartementsCouvreFeu (OptionImport optionImport,String filename, int myMaxGet, int minIndice, int maxIndice, DF_CASE myDateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport, filename,  myMaxGet,  minIndice,  maxIndice,  myDateFilter, myMinDate,  myMaxDate, myMindays, myMaxdays);
	}

	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {

		// TODO Auto-generated method stub
		return initialiseInputsDepartementCouvreFeu(inputs);
	}


	// événement confinements
	private Collection<Input> initialiseInputsDepartementCouvreFeu(Collection<Input>inputs) {

		// derived types

		inputs=initialiseDefine(inputs, "EventDepartementConfinement", "event");
		inputs=initialiseDefine(inputs, "DepartementGroup", "GroupWho");

		inputs=initialiseDefine(inputs, "ValueHorairesStart", "DoubleValue");
		inputs=initialiseDefine(inputs, "ValueHorairesEnd", "DoubleValue");

		inputs=initialiseDefine(inputs, "EventDepartementConfinementRelations", "eventrelations"+SuffixEventRelations);
		inputs=initialiseDefine(inputs, "ValueEventDepartementConfinementRelations", "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, "TypeValueEventDepartementConfinementRelations", "ValueTypeRelation"+SuffixValueTypeRelations);

		List<DefineAttribute> defineattributesWhatDepartementConfinement=new ArrayList<>();
		DefineAttribute attribut_departement = 	new DefineAttribute(DepartementConfinementTypeAttributeValue_departement,DepartementConfinementTypeValue_departement);

		defineattributesWhatDepartementConfinement.add(attribut_departement);

		inputs=initialiseDefine(inputs, DepartementConfinementClass_WhatDepartementConfinement, "IdentifiedWhat",defineattributesWhatDepartementConfinement);


		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WDC-1", "Description", "DEPARTEMENTCONFINEMENT");
		inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity", "W-1", "Description", "CONFINEMENT");
		inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity", "W-2", "Description", "COUVREFEU");
		
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "departement");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "type1");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "debutcouvre1");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "fincouvre1");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "detailhours1");
//
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "type2");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "debutcouvre2");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "fincouvre2");
//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "detailhours2");

		

		// data
		inputs.add(new Input("data/departementdecret") {
			@Override
			public String template(Json departementconfinement) {

				String graqlInsertQuery="";
				try
				{

					if (departementconfinement.at("departement")==null)
					{
						//List<String> titres = Arrays.asList("type1","debutcouvre1","fincouvre1","detailhours1",
						//		"type2","debutcouvre2","fincouvre2","detailhours2");
						retrieveColumnsTitles(departementconfinement);

						graqlInsertQuery = "insert ";
						for (String titre:titres)
						{
							if (titre.equals("departement")==false)
							{
								graqlInsertQuery += InputSingletonDefineQuery("",  "TypeValue",  "IdValue",  titre);
							}
						}
					}
					else
					{
						String departement=departementconfinement.at("departement").asString();
						indice++;

						if (indice<=maxIndice && indice>=minIndice)
						{
							graqlInsertQuery = "match ";
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "departement");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "type1");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "debutcouvre1");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "fincouvre1");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "detailhours1");
//
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "type2");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "debutcouvre2");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "fincouvre2");
//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "detailhours2");

							for (String titre:titres)
							{
								if (titre.equals("departement")==false)
									graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", titre);
							}
							
							graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description", "DEPARTEMENTCONFINEMENT");

							graqlInsertQuery += InputSingletonDefineQuery("","Departement", "CodeGLN", departement);


							// à poursuivre avec evenement simple confinement et périodique couvrefeu					
							graqlInsertQuery+= "\ninsert ";
							graqlInsertQuery+= "$IdentifiedWhat-W-1" +" isa " + DepartementConfinementClass_WhatDepartementConfinement + " , has Identity \"WCONFINEMENT-" + departement + "\"";
							
							graqlInsertQuery+=insertAttribut (DepartementConfinementTypeValue_departement, "\"" + departement + "\"");
							graqlInsertQuery+=  ";";

							graqlInsertQuery+= "$IdentifiedWhat-W-2" +" isa " + DepartementConfinementClass_WhatDepartementConfinement + " , has Identity \"WCOUVREFEU-" + departement + "\"";
							graqlInsertQuery+=insertAttribut (DepartementConfinementTypeValue_departement, "\"" + departement + "\"");
							graqlInsertQuery+=  ";";
							
							graqlInsertQuery+= "$DepartementGroup-" + indice.toString() +" isa DepartementGroup, has Identity \"DG-" + departement+"\";";
							
							//graqlInsertQuery+= "$Value-Departement-" + indice.toString() +" isa StringValue, has IdValue \"DD-" + indice.toString() + "\", has StringValueAttribute \"" + departement + "\";";

//							graqlInsertQuery+= "$ValueRelation-Departement-CONFINEMENT-" + indice.toString() +" (value : $Value-Departement-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-1" + ") isa ValueEventDepartementConfinementRelations" + ";";
//							graqlInsertQuery+= "$ValueRelation-Departement-COUVREFEU-" + indice.toString() +" (value : $Value-Departement-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-2"  + ") isa ValueEventDepartementConfinementRelations" + ";";
//							graqlInsertQuery+= "$ValueTypeRelation-Departement-" + indice.toString() +" (value : $Value-Departement-" + indice.toString() + " , typevalue : $TypeValue-departement) isa TypeValueEventDepartementConfinementRelations" + ";";


							// confinement & couvrefeu
							//List<String> titres = Arrays.asList("type1","debutcouvre1","fincouvre1","detailhours1",
							//		"type2","debutcouvre2","fincouvre2","detailhours2");

							int col=2;
							while (col<titres.size())
							{
								String whattype;
								String name=titres.get(col);

								if (name.equals("departement")==false)
								{
									if (name.contains("confinement"))
									{
										whattype="$IdentifiedWhat-W-1";
									}
									else
									{
										whattype="$IdentifiedWhat-W-2";
									}
									// création de l'événement
									graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col + " isa EventDepartementConfinement, has Id \"EDC-" + indice.toString() + "-" + col  + "\";";

									// création de la période
									DateTimeFormatter formatter= DateTimeFormatter.ofPattern("d/M/uuuu");
									LocalDate datedebut=LocalDate.parse((CharSequence)departementconfinement.at(titres.get(col)).asString(), formatter);
									LocalDate datefin=LocalDate.parse((CharSequence)departementconfinement.at(titres.get(col+1)).asString(), formatter);

									// horaires
									LocalTime startdetailhours=LocalTime.of(0, 0);
									LocalTime enddetailhours=LocalTime.of(0, 0);
									double doublestartdetailhours=0.0;
									double doubleenddetailhours=0.0;


									Json horaires=departementconfinement.at(titres.get(col+2));
									String namehoraires=titres.get(col+2);
									if (!horaires.isNull())
									{
										String [] listhoraires=horaires.asString().split(";");
										if (listhoraires.length==2)
										{
											startdetailhours=LocalTime.parse(listhoraires[0]);
											doublestartdetailhours=startdetailhours.getHour()+startdetailhours.getMinute()/60+startdetailhours.getMinute()/3600;

											enddetailhours=LocalTime.parse(listhoraires[1]);
											doubleenddetailhours=enddetailhours.getHour()+startdetailhours.getMinute()/60+startdetailhours.getMinute()/3600;

										}
									}

									graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa TimeDate, has Identity \"DTD-" + col + "-"+ indice.toString() + "\", has EventDate " + datedebut + ";";
									graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa TimeDate, has Identity \"DTF-" + col + "-"+ indice.toString() + "\", has EventDate " + datefin + ";";
									graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa PeriodOfTime, has Identity \"DPOfT-" + col + "-"+ indice.toString() + "\", has TypeFrequency \"Daily\";";

									graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";


									// horaires

									// debut
									graqlInsertQuery+= "$Value-HorairesDebut-" + col +"-"+ indice.toString() +" isa ValueHorairesStart, has IdValue \"DHD-" + col +"-"+ indice.toString() + "\", has DoubleValueAttribute " + doublestartdetailhours + ";";
									graqlInsertQuery+= "$ValueRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa ValueEventDepartementConfinementRelations" + ";";
									graqlInsertQuery+= "$ValueTypeRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa TypeValueEventDepartementConfinementRelations" + ";";

									// fin
									graqlInsertQuery+= "$Value-HorairesFin-" + col +"-"+ indice.toString() +" isa ValueHorairesEnd, has IdValue \"DHF-" + col +"-"+indice.toString() + "\", has DoubleValueAttribute " + doubleenddetailhours + ";";
									graqlInsertQuery+= "$ValueRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa ValueEventDepartementConfinementRelations" + ";";
									graqlInsertQuery+= "$ValueTypeRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa TypeValueEventDepartementConfinementRelations" + ";";

									// no as a master data
									// graqlInsertQuery+= "$Departement-" + col +"-" + indice.toString() +" isa Departement , has Identity \"DD-" + col +"-"+indice.toString() + "\", has CodeGLN \"" + departementconfinement.at("departement").asString() + "\";";

									// relations de l'événement
									graqlInsertQuery+= "$eventRelation-" + indice.toString() + "-" + col +" (registeredevent : $Event-" + indice.toString() + "-" + col + ", actor: $DepartementGroup-" + indice.toString() + ", localization:" + "$Departement-" + departement + ", time :$Period-" + col + "-" + indice.toString()+ ", goal :$Why-DEPARTEMENTCONFINEMENT" + ", object : "+ whattype + ") isa EventDepartementConfinementRelations ;";

									col+=4;
								}
							}
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
