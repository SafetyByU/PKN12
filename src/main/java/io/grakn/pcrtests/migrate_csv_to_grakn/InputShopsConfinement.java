package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import grakn.client.GraknClient;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;


public class InputShopsConfinement extends InputSetOfChronology {

	static final String ShopConfinementTypeValue_MetaTypeValue = "SC";
	static final String ShopConfinementClass_ValueEventRelations = "ValueEventShopConfinementRelations";
	static final String ShopConfinementClass_TypeValueEventRelations = "TypeValueShopConfinementRelations";
	static final String ShopConfinementClass_WhatShopConfinement="WhatShopConfinement";

	String ShopConfinementTypeValue_category="category";
	static final String ShopConfinementTypeAttributeValue_category = "StringValueAttribute";
	String ShopConfinementTypeValue_subcategory="subcategory";
	static final String ShopConfinementTypeAttributeValue_subcategory = "StringValueAttribute";

	String getMetaType() {return ShopConfinementTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return ShopConfinementClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return ShopConfinementClass_TypeValueEventRelations;};



	// confinement & couvrefeu

	InputShopsConfinement(OptionImport optionImport,String filename)
	{
		super(optionImport, filename);
	}

	public InputShopsConfinement (OptionImport optionImport,String filename, int myMaxGet, int minIndice, int maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice,  myMinDate,  myMaxDate, myMindays,  myMaxdays);
	}

	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {

		// TODO Auto-generated method stub
		return initialiseInputsShopsConfinement(inputs);
	}

	// événement confinements
	protected Collection<Input> initialiseInputsShopsConfinement(Collection<Input>inputs) {

		// derived types
		inputs=initialiseDefine(inputs, "EventShopConfinement", "event");
		inputs=initialiseDefine(inputs, "ShopsGroup", "GroupWho");
		inputs=initialiseDefine(inputs, "EventShopConfinementRelations", "eventrelations"+SuffixEventRelations);
		inputs=initialiseDefine(inputs, "ValueEventShopConfinementRelations", "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, "TypeValueEventShopConfinementRelations", "ValueTypeRelation"+SuffixValueTypeRelations);

		List<DefineAttribute> defineattributesWhatShopConfinement=new ArrayList<>();
		DefineAttribute attribut_category = 	new DefineAttribute(ShopConfinementTypeAttributeValue_category,ShopConfinementTypeValue_category);
		DefineAttribute attribut_subcategory = 	new DefineAttribute(ShopConfinementTypeAttributeValue_subcategory,ShopConfinementTypeValue_subcategory);

		defineattributesWhatShopConfinement.add(attribut_category);
		defineattributesWhatShopConfinement.add(attribut_subcategory);

		inputs=initialiseDefine(inputs, ShopConfinementClass_WhatShopConfinement, "What",defineattributesWhatShopConfinement);

		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WSC-1", "Description", "SHOPCONFINEMENT");

		inputs=initialiseInsertEntitySingleton(inputs, "Country", "Identity", "Wfr", "CodeGLN", "France");

		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "category");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "subcategory");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "confinementdebut1");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status1d");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "confinementfin1");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status1f");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "couvrefeudebut2");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status2d");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "couvrefeufin2");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status2f");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "confinementdebut3");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status3d");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "confinementfin3");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status3f");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "couvrefeudebut4");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status4d");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "couvrefeufin4");
		//		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "status4f");

		/*for (String titre:titres)
		{
			if (titre!="category" && titre !="subcategory")
				inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", titre);
		}*/

		// data
		inputs.add(new Input("data/shopconfinement") {
			@Override
			public String template(Json shopsconfinement) {

				String graqlInsertQuery="";
				try
				{

					if (shopsconfinement.at("category")==null)
					{
						//List<String> titres = Arrays.asList("type1","debutcouvre1","fincouvre1","detailhours1",
						//		"type2","debutcouvre2","fincouvre2","detailhours2");

						retrieveColumnsTitles(shopsconfinement);

						graqlInsertQuery = "insert ";
						for (String titre:titres)
						{
							if (titre.equals("category")==false && titre.equals("subcategory")==false)
							{
								graqlInsertQuery += InputSingletonDefineQuery("",  "TypeValue",  "IdValue",  titre);
							}
						}
					}
					else
					{
						indice++;

						if (indice<=maxIndice && indice>=minIndice)
						{
							graqlInsertQuery = "match ";
							graqlInsertQuery += InputSingletonDefineQuery("", "Country", "CodeGLN", "France");

							//							graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "category");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "subcategory");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "confinementdebut1");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status1d");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "confinementfin1");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status1f");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "couvrefeudebut2");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status2d");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "couvrefeufin2");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status2f");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "confinementdebut3");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status3d");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "confinementfin3");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status3f");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "couvrefeudebut4");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status4d");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "couvrefeufin4");
							//							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", "status4f");

							for (String titre:titres)
							{
								if (titre.equals("category")==false && titre.equals("subcategory")==false)
									graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", titre );

							}

							graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description", "SHOPCONFINEMENT");

							String category=shopsconfinement.at("category").asString();
							String subcategory=shopsconfinement.at("subcategory").asString();
							String shopgroupcategory=category+"-"+subcategory;

							// à poursuivre avec evenement simple confinement et périodique couvrefeu					
							graqlInsertQuery+= "\ninsert ";
							graqlInsertQuery+= "$ShopsGroup-" + indice.toString() +" isa ShopsGroup, has Identity \"" + shopgroupcategory + "\";";

							graqlInsertQuery+= "$IdentifiedWhat-W-1" +" isa " + ShopConfinementClass_WhatShopConfinement + " , has Identity \"WCONFINEMENT-" + shopgroupcategory + "\"";

							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_category, "\""+ category + "\"");
							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_subcategory, "\""+ subcategory + "\"");

							graqlInsertQuery+=  ";";


							graqlInsertQuery+= "$IdentifiedWhat-W-2" +" isa " + ShopConfinementClass_WhatShopConfinement + ", has Identity \"WCOUVREFEU-" + shopgroupcategory + "\"";

							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_category, "\""+ category + "\"");
							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_subcategory, "\""+ subcategory + "\"");

							graqlInsertQuery+=  ";";

							//							graqlInsertQuery+= "$Value-Category-" + indice.toString() +" isa StringValue, has IdValue \"SCC-"+ indice.toString() + "\", has StringValueAttribute \"" + category + "\";";
							//							graqlInsertQuery+= "$Value-SubCategory-" + indice.toString() +" isa StringValue, has IdValue \"SCSC-"+ indice.toString() + "\", has StringValueAttribute \"" + subcategory + "\";";
							//							graqlInsertQuery+= "$ValueRelation-Category-CONFINEMENT-" + indice.toString() +" (value : $Value-Category-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-1" + ") isa ValueEventShopConfinementRelations" + ";";
							//							graqlInsertQuery+= "$ValueRelation-Category-COUVREFEU-" + indice.toString() +" (value : $Value-Category-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-2"  + ") isa ValueEventShopConfinementRelations" + ";";
							//							graqlInsertQuery+= "$ValueTypeRelation-Category-" + indice.toString() +" (value : $Value-Category-" + indice.toString() + " , typevalue : $TypeValue-category) isa TypeValueEventShopConfinementRelations" + ";";
							//							graqlInsertQuery+= "$ValueRelation-SubCategory-CONFINEMENT-" + indice.toString() +" (value : $Value-SubCategory-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-1" + ") isa ValueEventShopConfinementRelations" + ";";
							//							graqlInsertQuery+= "$ValueRelation-SubCategory-COUVREFEU-" + indice.toString() +" (value : $Value-SubCategory-" + indice.toString() + " , resource : " + "$IdentifiedWhat-W-2" + ") isa ValueEventShopConfinementRelations" + ";";
							//							graqlInsertQuery+= "$ValueTypeRelation-SubCategory-" + indice.toString() +" (value : $Value-SubCategory-" + indice.toString() + " , typevalue : $TypeValue-subcategory) isa TypeValueEventShopConfinementRelations" + ";";


							// confinement & couvrefeu
							/*List<String> titres = Arrays.asList("confinementdebut1","status1d","confinementfin1","status1f",
									"couvrefeudebut2","status2d","couvrefeufin2","status2f",
									"confinementdebut3","status3d","confinementfin3","status3f",
									"couvrefeudebut4","status4d","couvrefeufin4","status4f");*/

							//check(shopsconfinement, titres);

							int col=2;
							while (col<titres.size())
							{
								String whattype;
								String name=titres.get(col);

								if (name.contains("confinement"))
								{
									whattype="$IdentifiedWhat-W-1";
								}
								else
								{
									whattype="$IdentifiedWhat-W-2";
								}

								// création de la période
								DateTimeFormatter formatter= DateTimeFormatter.ofPattern("d/M/uuuu");
								Json sdatedebut=shopsconfinement.at(titres.get(col));
								LocalDate datedebut=LocalDate.of(2000, 1, 1);
								if (!sdatedebut.isNull())
								{
									LocalDate.parse((CharSequence)sdatedebut.asString(), formatter);
								}

								Long statusdebut=Integer.toUnsignedLong(0);
								Json stat1=shopsconfinement.at(titres.get(col+1));
								String namestatus1=titres.get(col+1);
								if (!stat1.isNull())
									statusdebut=stat1.asLong();

								Json sdatefin=shopsconfinement.at(titres.get(col+2));
								LocalDate datefin=LocalDate.of(2000, 1, 1);
								if (!sdatefin.isNull())
								{
									datefin=LocalDate.parse((CharSequence)sdatefin.asString(), formatter);
								}
								Long statusfin=Integer.toUnsignedLong(0);
								Json stat2=shopsconfinement.at(titres.get(col+3));
								String namestatus2=titres.get(col+3);
								if (!stat2.isNull())
									statusfin=stat2.asLong();

								if (!sdatedebut.isNull() && !sdatefin.isNull())
								{
									// création de l'événement
									graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col + " isa EventShopConfinement, has Id \"ESC-" + indice.toString() + "-" + col  + "\";";

									graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa TimeDate, has Identity \"TD-"+ indice.toString() + "-" + col + "\", has EventDate " + datedebut + ";";
									graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa TimeDate, has Identity \"TF-"+ indice.toString() + "-" + col + "\", has EventDate " + datefin + ";";
									graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa PeriodOfTime, has Identity \"PT-"+ indice.toString() + "-" + col + "\", has TypeFrequency \"Daily\";";

									graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";


									// status debut

									graqlInsertQuery+= "$Value-StatusDebut-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSD-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusdebut + ";";
									graqlInsertQuery+= "$ValueRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-debut-" + col +"-"+ indice.toString() + ") isa ValueEventShopConfinementRelations" + ";";
									graqlInsertQuery+= "$ValueTypeRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus1+ ") isa TypeValueEventShopConfinementRelations" + ";";

									// status fin

									graqlInsertQuery+= "$Value-StatusFin-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSF-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusfin + ";";
									graqlInsertQuery+= "$ValueRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-fin-" + col +"-"+ indice.toString() + ") isa ValueEventShopConfinementRelations" + ";";
									graqlInsertQuery+= "$ValueTypeRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus2+ " ) isa  TypeValueEventShopConfinementRelations" + ";";


									// relations de l'événement
									graqlInsertQuery+= "$eventRelation-" + indice.toString() + "-" + col +" (registeredevent : $Event-" + indice.toString() + "-" + col + ", actor: $ShopsGroup-" + indice.toString() + ", localization:$Country-France, time :$Period-" + col + "-" + indice.toString()+ ", goal :$Why-SHOPCONFINEMENT" + ", object : "+ whattype + ") isa EventShopConfinementRelations ;\n";
								}
								col+=4;
							}

						}
						else
						{
							int test=0;
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

	@Override
	public void postCalculate(GraknClient.Session session)
	{
	}
}
