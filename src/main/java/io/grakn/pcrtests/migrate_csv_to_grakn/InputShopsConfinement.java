package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Entity;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;
import io.grakn.pcrtests.migrate_csv_to_grakn.InputDepartementsCouvreFeu.MyInputDepartementsCouvreFeu;
import io.grakn.pcrtests.migrate_csv_to_grakn.InputShopsConfinement.MyInputShopsConfinement;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;


public class InputShopsConfinement extends InputSetOfChronology {

	static final String ShopConfinementTypeValue_MetaTypeValue = "SC";
	static final String ShopConfinementClass_ValueEventRelations = "ValueEventShopConfinementRelations";
	static final String ShopConfinementClass_TypeValueEventRelations = "TypeValueEventShopConfinementRelations";
	static final String ShopConfinementClass_WhatShopConfinement="WhatShopConfinement";
	static final String ShopConfinementClass_ShopConfinementTimeDate="ShopConfinementTimeDate";
	static final String ShopConfinementClass_EventShopConfinementRelations="EventShopConfinementRelations";
	static final String ShopConfinementClass_EventShopConfinements="EventShopConfinement";
	static final String ShopConfinementClass_ShopConfinementPeriodOfTime="ShopConfinementPeriodOfTime";

	String ShopConfinementTypeValue_category="category";
	static final String ShopConfinementTypeAttributeValue_category = "StringValueAttribute";
	String ShopConfinementTypeValue_subcategory="subcategory";
	static final String ShopConfinementTypeAttributeValue_subcategory = "StringValueAttribute";

	String getMetaType() {return ShopConfinementTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return ShopConfinementClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return ShopConfinementClass_TypeValueEventRelations;};

	static int linkssamedates_shopindex=0;


	MyInputShopsConfinement myInputShopsConfinement=null;


	public MyInputShopsConfinement getMyInputShopsConfinement() {
		return myInputShopsConfinement;
	}
	public void setMyInputShopsConfinement(MyInputShopsConfinement myInputShopsConfinement) {
		this.myInputShopsConfinement = myInputShopsConfinement;
	}

	class MyInputShopsConfinement extends Input {

		InputShopsConfinement inputShopsConfinement;

		public InputShopsConfinement getInputShopsConfinement() {
			return inputShopsConfinement;
		}


		public void setInputShopsConfinement(InputShopsConfinement inputShopsConfinement) {
			this.inputShopsConfinement = inputShopsConfinement;
		}


		@Override
		public void saveHistoric(ArrayList<Json> historics, boolean finalsave)
		{

			ArrayList<Json> subHistorics = new ArrayList<>();

			super.saveHistoric(subHistorics, finalsave);


			Json itemInputShopsConfinement = Json.object();
			itemInputShopsConfinement.set("InputShopsConfinement", subHistorics);

			historics=removeHistoric(historics, "InputShopsConfinement");
			historics.add(itemInputShopsConfinement);

			// sequence link post calculate
			ArrayList<Json> subHistorics2 = new ArrayList<>();

			if (finalsave==true)
			{
				// shopsamedateindex
				Json itemShopsIndexSameDateLinks = Json.object();
				itemShopsIndexSameDateLinks.set("linkssamedates_shopindex",linkssamedates_shopindex);
				subHistorics2.add(itemShopsIndexSameDateLinks);	
			}

			Json InputShopsConfinementLinksDates = Json.object();
			InputShopsConfinementLinksDates.set("InputShopsConfinementLinksDates", subHistorics2);

			historics=removeHistoric(historics, "InputShopsConfinementLinksDates");
			historics.add(InputShopsConfinementLinksDates);
		}


		@Override
		public void loadHistoric(JsonArray historicsInput )
		{

			Iterator<JsonElement> iterator = historicsInput.iterator();
			while (iterator.hasNext()) 
			{
				JsonElement elt = iterator.next();

				JsonObject json1=elt.getAsJsonObject();
				Set<Entry<String, JsonElement>> set1=json1.entrySet();

				Iterator<Entry<String, JsonElement>> it1=set1.iterator();
				while (it1.hasNext()) 
				{
					Entry<String, JsonElement> elt1 =  it1.next();
					if (elt1.getKey().equals("InputShopsConfinement"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputShopsConfinementList = (JsonArray) elt1.getValue();
						if (historicsInputShopsConfinementList!=null)
							loadSpecificHistoric(historicsInputShopsConfinementList);
					}
					if (elt1.getKey().equals("InputShopsConfinementLinksDates"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputShopsConfinementListLinksDates = (JsonArray) elt1.getValue();
						if (historicsInputShopsConfinementListLinksDates!=null)
							loadSpecificHistoric(historicsInputShopsConfinementListLinksDates);
					}
				}
			}
		}

		@Override
		public void loadSpecificHistoric(JsonArray historicsInputShopsConfinementList )
		{
			super.loadHistoric(historicsInputShopsConfinementList );

			Iterator<JsonElement> iterator = historicsInputShopsConfinementList.iterator();
			while (iterator.hasNext()) {
				JsonElement elt = iterator.next();
				JsonObject jsonObject= elt.getAsJsonObject();


				if (jsonObject.get("linkssamedates_shopindex")!=null)
				{
					JsonElement jsondeptIndex= jsonObject.get("linkssamedates_shopindex");
					linkssamedates_shopindex=jsondeptIndex.getAsInt();

				}
			}
		}

		public MyInputShopsConfinement(String path, InputShopsConfinement inputShopsConfinement) {
			super(path);
			// TODO Auto-generated constructor stub
			this.inputShopsConfinement=inputShopsConfinement;
			inputShopsConfinement.setMyInputShopsConfinement(this);
		}

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
								datedebut=LocalDate.parse((CharSequence)sdatedebut.asString(), formatter);
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
								graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col + " isa " + ShopConfinementClass_EventShopConfinements + ", has Id \"ESC-" + indice.toString() + "-" + col  + "\";";

								graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa " + ShopConfinementClass_ShopConfinementTimeDate + ", has Identity \"SCTD-"+ indice.toString() + "-" + col + "\", has EventDate " + datedebut + ";";
								graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa "+ ShopConfinementClass_ShopConfinementTimeDate + ", has Identity \"SCTF-"+ indice.toString() + "-" + col + "\", has EventDate " + datefin + ";";
								graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa " + ShopConfinementClass_ShopConfinementPeriodOfTime + ", has Identity \"SCPT-"+ indice.toString() + "-" + col + "\", has TypeFrequency \"Daily\";";

								graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";


								// status debut

								graqlInsertQuery+= "$Value-StatusDebut-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSD-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusdebut + ", has TypeValueId \"" + namestatus1+ "\";";
								graqlInsertQuery+= "$ValueRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-debut-" + col +"-"+ indice.toString() + ") isa "+ ShopConfinementClass_ValueEventRelations + ";";
								graqlInsertQuery+= "$ValueTypeRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus1+ ") isa " + ShopConfinementClass_TypeValueEventRelations + ";";

								// status fin

								graqlInsertQuery+= "$Value-StatusFin-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSF-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusfin + ", has TypeValueId \"" + namestatus2+ "\";";
								graqlInsertQuery+= "$ValueRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-fin-" + col +"-"+ indice.toString() + ") isa " + ShopConfinementClass_ValueEventRelations + ";";
								graqlInsertQuery+= "$ValueTypeRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus2+ " ) isa  " + ShopConfinementClass_TypeValueEventRelations + ";";


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
	}

	// confinement & couvrefeu

	InputShopsConfinement(OptionImport optionImport,String filename)
	{
		super(optionImport, filename);
	}

	public InputShopsConfinement (OptionImport optionImport,String filename, int myMaxGet, Long minIndice, Long maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
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
		inputs=initialiseDefine(inputs, ShopConfinementClass_EventShopConfinements, "event");
		inputs=initialiseDefine(inputs, "ShopsGroup", "GroupWho");
		inputs=initialiseDefine(inputs, ShopConfinementClass_EventShopConfinementRelations, "eventrelations"+SuffixEventRelations);
		inputs=initialiseDefine(inputs, ShopConfinementClass_ValueEventRelations, "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, ShopConfinementClass_TypeValueEventRelations, "ValueTypeRelation"+SuffixValueTypeRelations);
		inputs=initialiseDefine(inputs, ShopConfinementClass_ShopConfinementTimeDate, "TimeDate");
		inputs=initialiseDefine(inputs, ShopConfinementClass_ShopConfinementPeriodOfTime, "PeriodOfTime");

		List<DefineAttribute> defineattributesWhatShopConfinement=new ArrayList<>();
		DefineAttribute attribut_category = 	new DefineAttribute(ShopConfinementTypeAttributeValue_category,ShopConfinementTypeValue_category);
		DefineAttribute attribut_subcategory = 	new DefineAttribute(ShopConfinementTypeAttributeValue_subcategory,ShopConfinementTypeValue_subcategory);

		defineattributesWhatShopConfinement.add(attribut_category);
		defineattributesWhatShopConfinement.add(attribut_subcategory);

		inputs=initialiseDefine(inputs, ShopConfinementClass_WhatShopConfinement, "What",defineattributesWhatShopConfinement);

		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WSC-1", "Description", "SHOPCONFINEMENT");

		inputs=initialiseInsertEntitySingleton(inputs, "Country", "Identity", "Wfr", "CodeGLN", "France");

		// data

		inputs.add(new MyInputShopsConfinement(setOfData.getFilename(), this));

		//		inputs.add(new Input("data/shopconfinement") {
		//			@Override
		//			public String template(Json shopsconfinement) {
		//
		//				String graqlInsertQuery="";
		//				try
		//				{
		//
		//					if (shopsconfinement.at("category")==null)
		//					{
		//						//List<String> titres = Arrays.asList("type1","debutcouvre1","fincouvre1","detailhours1",
		//						//		"type2","debutcouvre2","fincouvre2","detailhours2");
		//
		//						retrieveColumnsTitles(shopsconfinement);
		//
		//						graqlInsertQuery = "insert ";
		//						for (String titre:titres)
		//						{
		//							if (titre.equals("category")==false && titre.equals("subcategory")==false)
		//							{
		//								graqlInsertQuery += InputSingletonDefineQuery("",  "TypeValue",  "IdValue",  titre);
		//							}
		//						}
		//					}
		//					else
		//					{
		//						indice++;
		//
		//						if (indice<=maxIndice && indice>=minIndice)
		//						{
		//							graqlInsertQuery = "match ";
		//							graqlInsertQuery += InputSingletonDefineQuery("", "Country", "CodeGLN", "France");
		//
		//							for (String titre:titres)
		//							{
		//								if (titre.equals("category")==false && titre.equals("subcategory")==false)
		//									graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", titre );
		//
		//							}
		//
		//							graqlInsertQuery += InputSingletonDefineQuery("","Why", "Description", "SHOPCONFINEMENT");
		//
		//							String category=shopsconfinement.at("category").asString();
		//							String subcategory=shopsconfinement.at("subcategory").asString();
		//							String shopgroupcategory=category+"-"+subcategory;
		//
		//							// à poursuivre avec evenement simple confinement et périodique couvrefeu					
		//							graqlInsertQuery+= "\ninsert ";
		//							graqlInsertQuery+= "$ShopsGroup-" + indice.toString() +" isa ShopsGroup, has Identity \"" + shopgroupcategory + "\";";
		//
		//							graqlInsertQuery+= "$IdentifiedWhat-W-1" +" isa " + ShopConfinementClass_WhatShopConfinement + " , has Identity \"WCONFINEMENT-" + shopgroupcategory + "\"";
		//
		//							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_category, "\""+ category + "\"");
		//							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_subcategory, "\""+ subcategory + "\"");
		//
		//							graqlInsertQuery+=  ";";
		//
		//
		//							graqlInsertQuery+= "$IdentifiedWhat-W-2" +" isa " + ShopConfinementClass_WhatShopConfinement + ", has Identity \"WCOUVREFEU-" + shopgroupcategory + "\"";
		//
		//							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_category, "\""+ category + "\"");
		//							graqlInsertQuery+=insertAttribut (ShopConfinementTypeValue_subcategory, "\""+ subcategory + "\"");
		//
		//							graqlInsertQuery+=  ";";
		//
		//							int col=2;
		//							while (col<titres.size())
		//							{
		//								String whattype;
		//								String name=titres.get(col);
		//
		//								if (name.contains("confinement"))
		//								{
		//									whattype="$IdentifiedWhat-W-1";
		//								}
		//								else
		//								{
		//									whattype="$IdentifiedWhat-W-2";
		//								}
		//
		//								// création de la période
		//								DateTimeFormatter formatter= DateTimeFormatter.ofPattern("d/M/uuuu");
		//								Json sdatedebut=shopsconfinement.at(titres.get(col));
		//								LocalDate datedebut=LocalDate.of(2000, 1, 1);
		//								if (!sdatedebut.isNull())
		//								{
		//									datedebut=LocalDate.parse((CharSequence)sdatedebut.asString(), formatter);
		//								}
		//
		//								Long statusdebut=Integer.toUnsignedLong(0);
		//								Json stat1=shopsconfinement.at(titres.get(col+1));
		//								String namestatus1=titres.get(col+1);
		//								if (!stat1.isNull())
		//									statusdebut=stat1.asLong();
		//
		//								Json sdatefin=shopsconfinement.at(titres.get(col+2));
		//								LocalDate datefin=LocalDate.of(2000, 1, 1);
		//								if (!sdatefin.isNull())
		//								{
		//									datefin=LocalDate.parse((CharSequence)sdatefin.asString(), formatter);
		//								}
		//								Long statusfin=Integer.toUnsignedLong(0);
		//								Json stat2=shopsconfinement.at(titres.get(col+3));
		//								String namestatus2=titres.get(col+3);
		//								if (!stat2.isNull())
		//									statusfin=stat2.asLong();
		//
		//								if (!sdatedebut.isNull() && !sdatefin.isNull())
		//								{
		//									// création de l'événement
		//									graqlInsertQuery+= "$Event-" + indice.toString() + "-" + col + " isa " + ShopConfinementClass_EventShopConfinements + ", has Id \"ESC-" + indice.toString() + "-" + col  + "\";";
		//
		//									graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa " + ShopConfinementClass_ShopConfinementTimeDate + ", has Identity \"SCTD-"+ indice.toString() + "-" + col + "\", has EventDate " + datedebut + ";";
		//									graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa "+ ShopConfinementClass_ShopConfinementTimeDate + ", has Identity \"SCTF-"+ indice.toString() + "-" + col + "\", has EventDate " + datefin + ";";
		//									graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa " + ShopConfinementClass_ShopConfinementPeriodOfTime + ", has Identity \"SCPT-"+ indice.toString() + "-" + col + "\", has TypeFrequency \"Daily\";";
		//
		//									graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";
		//
		//
		//									// status debut
		//
		//									graqlInsertQuery+= "$Value-StatusDebut-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSD-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusdebut + ", has TypeValueId \"" + namestatus1+ "\";";
		//									graqlInsertQuery+= "$ValueRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-debut-" + col +"-"+ indice.toString() + ") isa "+ ShopConfinementClass_ValueEventRelations + ";";
		//									graqlInsertQuery+= "$ValueTypeRelation-StatusDebut-" + col +"-"+ indice.toString() +" (value : $Value-StatusDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus1+ ") isa " + ShopConfinementClass_TypeValueEventRelations + ";";
		//
		//									// status fin
		//
		//									graqlInsertQuery+= "$Value-StatusFin-" + col +"-"+ indice.toString() +" isa LongValue, has IdValue \"SSF-"+ indice.toString() + "-" + col + "\", has LongValueAttribute " + statusfin + ", has TypeValueId \"" + namestatus2+ "\";";
		//									graqlInsertQuery+= "$ValueRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , resource : " + "$TimeDate-fin-" + col +"-"+ indice.toString() + ") isa " + ShopConfinementClass_ValueEventRelations + ";";
		//									graqlInsertQuery+= "$ValueTypeRelation-StatusFin-" + col +"-"+ indice.toString() +" (value : $Value-StatusFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namestatus2+ " ) isa  " + ShopConfinementClass_TypeValueEventRelations + ";";
		//
		//
		//									// relations de l'événement
		//									graqlInsertQuery+= "$eventRelation-" + indice.toString() + "-" + col +" (registeredevent : $Event-" + indice.toString() + "-" + col + ", actor: $ShopsGroup-" + indice.toString() + ", localization:$Country-France, time :$Period-" + col + "-" + indice.toString()+ ", goal :$Why-SHOPCONFINEMENT" + ", object : "+ whattype + ") isa EventShopConfinementRelations ;\n";
		//								}
		//								col+=4;
		//							}
		//
		//						}
		//						else
		//						{
		//							int test=0;
		//						}
		//					}
		//				}
		//				catch (Exception e)
		//				{
		//					int y=0;
		//				}
		//				return (graqlInsertQuery);
		//			}
		//		});
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

	public void postCalculate(GraknClient.Session session)
	{

		super.postCalculate(session);

		if (myInputShopsConfinement== null)
		{
			myInputShopsConfinement=new MyInputShopsConfinement("", this);
			JsonArray jsonArray=PCRFilesStudyMigration.loadHistorics();
			myInputShopsConfinement.loadHistoric(jsonArray);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULSHOPCONFINEMENTCLEAN)
		{
			PostCleanDataByClass(session);
			PostCleanPeriodDataByClass(session);
			PostCleanTimeDataByClass(session);
		}

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULSHOPSAMEDATEPCRTEST)
		{
			LinkSameDateEvents(session);
		}
	}

	// Clean Old Datas
	private void PostCleanDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			boolean ok = true;
			GraknClient.Transaction transaction= null;;
			int index=0;

			while (ok==true)
			{
				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false;
				}

				// query more than one value
				String graqlQuery11 = "match ";
				graqlQuery11 += 	"$what isa " + ShopConfinementClass_WhatShopConfinement + ", has Identity $identity;";
				graqlQuery11+=		"(object : $what) isa "+ ShopConfinementClass_EventShopConfinementRelations + ";";
				graqlQuery11+=		"get;";
				graqlQuery11+=		"limit " + maxGet + ";";

				//System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				System.out.println("(What existing old values) : "+ answers11.size());
				ok=false;
				if (answers11.size()>0)
				{
					ok=true;
				}

				for(ConceptMap answer11:answers11)
				{

					System.out.println("delete : "+ index);

					Entity resource= answer11.get("what").asEntity();
					String idResource=resource.id().toString();

					deleteEventObjectById(transaction, idResource, RelationTypeEvent.WHATRELATION);
					index++;
				}
				try
				{
					transaction.commit();
					//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = true;
				}
				catch (GraknClientException e) {
					System.out.println(e);
					boolean tobecontinued=false;
					if (e.getMessage().contains("There is more than one thing")
							&& e.getMessage().contains("that owns the key")) {
						tobecontinued=true;
					}
					if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
							&& e.getMessage().contains("Received Rst Stream")) {
						tobecontinued=true;
					}
					if (tobecontinued==true)
					{
						System.out.println("rest of the code...");
						close = true;
					} else {
						throw (e);
					}

					if (close==false)
						transaction.commit();
				}
			}
			System.out.println("Clean Double Confinement Shop OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Clean Old Datas
	private void PostCleanPeriodDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			boolean ok = true;
			GraknClient.Transaction transaction= null;;
			int index=0;

			while (ok==true)
			{
				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false;
				}

				// query more than one value
				String graqlQuery11 = "match ";
				graqlQuery11 += 	"$pt isa " + ShopConfinementClass_ShopConfinementPeriodOfTime +", has Identity $identity;";
				//graqlQuery11+=		"$identity contains \"SCPT\";";
				graqlQuery11+=		"get;";
				graqlQuery11+=		"limit " + maxGet + ";";

				//System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				System.out.println("(PeriodOfTime existing old values) : "+ answers11.size());
				ok=false;
				if (answers11.size()>0)
				{
					ok=true;
				}

				for(ConceptMap answer11:answers11)
				{

					System.out.println("delete : "+ index);

					Entity period= answer11.get("pt").asEntity();
					String idPt=period.id().toString();

					deleteThePeriodOfTimeById(transaction, idPt);
					index++;
				}
				try
				{
					transaction.commit();
					//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = true;
				}
				catch (GraknClientException e) {
					System.out.println(e);
					boolean tobecontinued=false;
					if (e.getMessage().contains("There is more than one thing")
							&& e.getMessage().contains("that owns the key")) {
						tobecontinued=true;
					}
					if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
							&& e.getMessage().contains("Received Rst Stream")) {
						tobecontinued=true;
					}
					if (tobecontinued==true)
					{
						System.out.println("rest of the code...");
						close = true;
					} else {
						throw (e);
					}

					if (close==false)
						transaction.commit();
				}
			}
			System.out.println("Clean Period Of Time Confinement Shop OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	// Clean Old Datas
	private void PostCleanTimeDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			boolean ok = true;
			GraknClient.Transaction transaction= null;;
			int index=0;

			while (ok==true)
			{
				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false;
				}

				// query more than one value
				String graqlQuery11 = "match ";
				graqlQuery11 += 	"$t isa " + ShopConfinementClass_ShopConfinementTimeDate +", has Identity $identity;";
				//graqlQuery11+=		"$identity contains \"SCPT\";";
				graqlQuery11+=		"get;";
				graqlQuery11+=		"limit " + maxGet + ";";

				//System.out.println(graqlQuery11);

				QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
				List<ConceptMap> answers11= map11.get();

				System.out.println("(Time existing old values) : "+ answers11.size());
				ok=false;
				if (answers11.size()>0)
				{
					ok=true;
				}

				for(ConceptMap answer11:answers11)
				{

					System.out.println("delete : "+ index);

					Entity period= answer11.get("t").asEntity();
					String idPt=period.id().toString();

					deleteTheTimeAndValuesById(transaction, idPt);
					index++;
				}
				try
				{
					transaction.commit();
					//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = true;
				}
				catch (GraknClientException e) {
					System.out.println(e);
					boolean tobecontinued=false;
					if (e.getMessage().contains("There is more than one thing")
							&& e.getMessage().contains("that owns the key")) {
						tobecontinued=true;
					}
					if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
							&& e.getMessage().contains("Received Rst Stream")) {
						tobecontinued=true;
					}
					if (tobecontinued==true)
					{
						System.out.println("rest of the code...");
						close = true;
					} else {
						throw (e);
					}

					if (close==false)
						transaction.commit();
				}
			}
			System.out.println("Clean Time Date Confinement Shop OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
	// Post calculation of precedents dated events
	private void LinkSameDateEvents(GraknClient.Session session)  
	{

		try
		{
			boolean close = true;
			GraknClient.Transaction transaction= null;;

			if (close == true) {
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
				close = false; 
			}


			// first case (startwhendate)
			////////////////////////////
			String graqlQuery11 = "match ";
			//graqlQuery11 += "$Localization isa Country, has CodeGLN \"France\";";
			graqlQuery11+=	"(time : $period) isa " + ShopConfinementClass_EventShopConfinementRelations+";";
			graqlQuery11+=	"$periodicrelation (periodoftime : $period, startwhendate : $timedate) isa PeriodicRelation;";
			graqlQuery11+=	"$timedate isa " + ShopConfinementClass_ShopConfinementTimeDate + ", has EventDate $attributedate;";				

			graqlQuery11+=		"get;sort $attributedate asc;\n";

			System.out.println(graqlQuery11);

			QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
			List<ConceptMap> answers11= map11.get();

			int ndate=0;
			LocalDateTime precdate=null;
			//boolean temporizelist=false;
			List<Entity> times = new ArrayList<>();
			int indextimes=0;
			for(ConceptMap answer11:answers11)
			{
				indextimes++;

				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false; 
				}

				ndate++;


				Entity time1= answer11.get("timedate").asEntity();
				String idTime1=time1.id().toString();

				LocalDateTime date1 = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
				System.out.println("date :" + date1);

				if (ndate>=linkssamedates_shopindex)
				{
					if (precdate==null)
					{
						//temporizelist=true;
						precdate=date1;
					}

					if (precdate.isEqual(date1))
					{
						times.add(time1);
					}

					boolean treatlist=false;
					if (precdate.isEqual(date1)==false || indextimes>=answers11.size())
					{
						treatlist=true;
						boolean lastTreat=false;
						while (treatlist==true)
						{

							DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");

							if (close == true) {
								transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
								close = false; 
							}

							// PCR Test at same date
							////////////////////////
							boolean query=true;
							List<ConceptMap> answers21= null;
							int decalday=0;
							while (query==true)
							{
								String graqlQuery21 = "match ";
								//graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ precdate + ";";
								if (decalday==0)
									graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1 + ";";
								else
									graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1.plusDays(decalday) + ";";

								//graqlQuery21+=	"$Departement id " + idDepartement + ";";
								graqlQuery21+=	"(time : $timedateevent, localization : $Departement) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
								graqlQuery21+=	"get;\n";

								//System.out.println(graqlQuery21);

								QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
								answers21= map21.get();

								decalday++;
								if (answers21.size()>0 || decalday>=3)
								{
									query=false;
								}

							}

							int index2=0;
							for(ConceptMap answer21:answers21)
							{			
								index2++;
								System.out.println("index 2 :" + index2 + "/" + answers21.size());
								if (close == true) {
									transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
									close = false; 
								}

								Entity dept1= answer21.get("Departement").asEntity();
								String iddept1=dept1.id().toString();

								Entity timedateevent= answer21.get("timedateevent").asEntity();

								for (Entity time:times)
								{
									createSameDateLink(transaction, time, timedateevent, iddept1);
								}
								try
								{
									transaction.commit();
									//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
									close = true;
								}
								catch (GraknClientException e) {
									System.out.println(e);
									boolean tobecontinued=false;
									if (e.getMessage().contains("There is more than one thing")
											&& e.getMessage().contains("that owns the key")) {
										tobecontinued=true;
									}
									if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
											&& e.getMessage().contains("Received Rst Stream")) {
										tobecontinued=true;
									}
									if (tobecontinued==true)
									{
										System.out.println("rest of the code...");
										close = true;
									} else {
										throw (e);
									}

								} // catch
							} // answer21

							times.clear();
							times.add(time1);

							treatlist=false;
							if (indextimes>=answers11.size() && lastTreat==false)
							{
								treatlist=true;
								lastTreat=true;
							}
						} // while treatlist==true
					} // if predate!=date1
					linkssamedates_shopindex=ndate;
					getMyInputShopsConfinement().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
				precdate=date1;				

			}

			if (close == true) 
			{
				transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
				close = false; 
			}

			// second case (endwhendate)
			////////////////////////////
			String graqlQuery12 = "match ";
			//graqlQuery12 += "$Localization isa Country, has CodeGLN \"France\"";
			graqlQuery12+=	"(time : $period) isa " + ShopConfinementClass_EventShopConfinementRelations+";";
			graqlQuery12+=	"$periodicrelation (periodoftime : $period, endwhendate : $timedate) isa PeriodicRelation;";
			graqlQuery12+=	"$timedate isa " + ShopConfinementClass_ShopConfinementTimeDate + ", has EventDate $attributedate;";				

			graqlQuery12+=		"get;sort $attributedate asc;\n";

			//			graqlQuery12+=		"get;\n";

			System.out.println(graqlQuery12);

			QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
			List<ConceptMap> answers12= map12.get();

			times.clear();
			precdate=null;
			indextimes=0;
			for(ConceptMap answer12:answers12)
			{
				indextimes++;

				if (close == true) {
					transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = false; 
				}

				ndate++;

				Entity time1= answer12.get("timedate").asEntity();
				String idTime1=time1.id().toString();

				LocalDateTime date1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
				System.out.println("date :" + date1);

				if (ndate>=linkssamedates_shopindex)
				{
					DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");

					if (precdate==null)
					{
						//temporizelist=true;
						precdate=date1;
					}

					if (precdate.isEqual(date1))
					{
						times.add(time1);
					}

					boolean treatlist=false;
					if (precdate.isEqual(date1)==false || indextimes>=answers11.size())
					{
						treatlist=true;
						boolean lastTreat=false;

						while (treatlist==true)
						{
							if (close == true) {
								transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
								close = false; 
							}

							// PCR Test at same date
							////////////////////////
							boolean query=true;
							List<ConceptMap> answers21= null;
							int decalday=0;
							while (query==true)
							{
								String graqlQuery21 = "match ";

								//graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate $date1;";
								if (decalday==0)
									graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1 + ";";
								else
									graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1.plusDays(decalday) + ";";

								//graqlQuery21+=	"$Departement id " + idDepartement + ";";
								graqlQuery21+=	"(time : $timedateevent, localization : $Departement) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
								graqlQuery21+=	"get;\n";

								//System.out.println(graqlQuery21);

								QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
								answers21= map21.get();

								decalday++;
								if (answers21.size()>0 || decalday>=3)
								{
									query=false;
								}
							}
							int index2=0;
							for(ConceptMap answer21:answers21)
							{				
								index2++;
								System.out.println("index 2 :" + index2 + "/" + answers21.size());

								if (close == true) {
									transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
									close = false; 
								}

								Entity dept1= answer21.get("Departement").asEntity();
								String iddept1=dept1.id().toString();

								Entity timedateevent= answer21.get("timedateevent").asEntity();

								for (Entity time:times)
								{
									createSameDateLink(transaction, time1, timedateevent, iddept1);
								}
								try
								{
									transaction.commit();
									//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
									close = true;
								}
								catch (GraknClientException e) 
								{
									System.out.println(e);
									boolean tobecontinued=false;
									if (e.getMessage().contains("There is more than one thing")
											&& e.getMessage().contains("that owns the key")) {
										tobecontinued=true;
									}
									if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
											&& e.getMessage().contains("Received Rst Stream")) {
										tobecontinued=true;
									}
									if (tobecontinued==true)
									{
										System.out.println("rest of the code...");
										close = true;
									} else {
										throw (e);
									}
								} // catch 
							} // for answer21
							times.clear();
							times.add(time1);

							treatlist=false;
							if (indextimes>=answers11.size() && lastTreat==false)
							{
								treatlist=true;
								lastTreat=true;
							}
						} // while treatlist==true
					} // if predate!=date1
					linkssamedates_shopindex=ndate;
					getMyInputShopsConfinement().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
				precdate=date1;
			}


			try
			{
				if (close==false)
				{
					transaction.commit();
					//transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
					close = true;
				}
			}
			catch (GraknClientException e) {
				System.out.println(e);
				boolean tobecontinued=false;
				if (e.getMessage().contains("There is more than one thing")
						&& e.getMessage().contains("that owns the key")) {
					tobecontinued=true;
				}
				if (e.getMessage().contains("INTERNAL: HTTP/2 error code: PROTOCOL_ERROR")
						&& e.getMessage().contains("Received Rst Stream")) {
					tobecontinued=true;
				}
				if (tobecontinued==true)
				{
					System.out.println("rest of the code...");
					close = true;
				} else {
					throw (e);
				}

			}
			//}

			getMyInputShopsConfinement().saveHistoric(PCRFilesStudyMigration.historics, true);
			PCRFilesStudyMigration.saveHistorics();


			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation  Same Links Shops Confinement OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
}
