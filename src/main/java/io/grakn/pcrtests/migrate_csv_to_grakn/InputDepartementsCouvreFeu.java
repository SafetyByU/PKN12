package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import grakn.client.concept.type.EntityType;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;
import io.grakn.pcrtests.migrate_csv_to_grakn.InputPCRTest.MyInputPCRTest;
import io.grakn.pcrtests.migrate_csv_to_grakn.InputSetOfChronology.DF_CASE;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputDepartementsCouvreFeu extends InputSetOfChronology {

	static final String DepartementConfinementTypeValue_MetaTypeValue = "DC";
	static final String DepartementConfinementClass_ValueEventRelations = "ValueEventDepartementConfinementRelations";
	static final String DepartementConfinementClass_TypeValueEventRelations = "TypeValueEventDepartementConfinementRelations";
	static final String DepartementConfinementClass_WhatDepartementConfinement="WhatDepartementConfinement";
	static final String DepartementConfinementClass_DepartementConfinementTimeDate="DepartementConfinementTimeDate";
	static final String DepartementConfinementClass_EventDepartementConfinementRelations="EventDepartementConfinementRelations";
	static final String DepartementConfinementClass_DepartementGroup="DepartementGroupConfinement";
	static final String DepartementConfinementClass_DepartementConfinementPeriodOfTime="DepartementConfinementPeriodOfTime";


	String DepartementConfinementTypeValue_departement="departement";
	static final String DepartementConfinementTypeAttributeValue_departement = "StringValueAttribute";

	String getMetaType() {return DepartementConfinementTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return DepartementConfinementClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return DepartementConfinementClass_TypeValueEventRelations;};

	static int linkssamedates_deptindex=0;
	MyInputDepartementsCouvreFeu myInputDepartementsCouvreFeu=null;
	
	public MyInputDepartementsCouvreFeu getMyInputDepartementsCouvreFeu() {
		return myInputDepartementsCouvreFeu;
	}
	public void setMyInputDepartementsCouvreFeu(MyInputDepartementsCouvreFeu myInputDepartementsCouvreFeu) {
		this.myInputDepartementsCouvreFeu = myInputDepartementsCouvreFeu;
	}

	class MyInputDepartementsCouvreFeu extends Input {



		InputDepartementsCouvreFeu inputDepartementsCouvreFeu;

		public InputDepartementsCouvreFeu getInputDepartementsCouvreFeu() {
			return inputDepartementsCouvreFeu;
		}


		public void setInputDepartementsCouvreFeu(InputDepartementsCouvreFeu inputDepartementsCouvreFeu) {
			this.inputDepartementsCouvreFeu = inputDepartementsCouvreFeu;
		}


		@Override
		public void saveHistoric(ArrayList<Json> historics, boolean finalsave)
		{

			ArrayList<Json> subHistorics = new ArrayList<>();

			super.saveHistoric(subHistorics, finalsave);


			Json itemInputDepartementsCouvreFeu = Json.object();
			itemInputDepartementsCouvreFeu.set("InputDepartementsCouvreFeu", subHistorics);

			historics=removeHistoric(historics, "InputDepartementsCouvreFeu");
			historics.add(itemInputDepartementsCouvreFeu);

			// sequence link post calculate
			ArrayList<Json> subHistorics2 = new ArrayList<>();

			if (finalsave==true)
			{
				// deptsamedateindex
				Json itemDeptIndexSameDateLinks = Json.object();
				itemDeptIndexSameDateLinks.set("linkssamedates_deptindex",linkssamedates_deptindex);
				subHistorics2.add(itemDeptIndexSameDateLinks);	
			}
			
			Json InputDepartementsCouvreFeuLinksDates = Json.object();
			InputDepartementsCouvreFeuLinksDates.set("InputDepartementsCouvreFeuLinksDates", subHistorics2);

			historics=removeHistoric(historics, "InputDepartementsCouvreFeuLinksDates");
			historics.add(InputDepartementsCouvreFeuLinksDates);
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
					if (elt1.getKey().equals("InputDepartementsCouvreFeu"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputDepartementsCouvreFeuList = (JsonArray) elt1.getValue();
						if (historicsInputDepartementsCouvreFeuList!=null)
							loadSpecificHistoric(historicsInputDepartementsCouvreFeuList);
					}
					if (elt1.getKey().equals("InputDepartementsCouvreFeuLinksDates"))
					{

						// A JSON array. JSONObject supports java.util.List interface.
						JsonArray historicsInputDepartementsCouvreFeuListLinksDates = (JsonArray) elt1.getValue();
						if (historicsInputDepartementsCouvreFeuListLinksDates!=null)
							loadSpecificHistoric(historicsInputDepartementsCouvreFeuListLinksDates);
					}
				}
			}
		}

		@Override
		public void loadSpecificHistoric(JsonArray historicsInputDepartementsCouvreFeuList )
		{
			super.loadHistoric(historicsInputDepartementsCouvreFeuList );

			Iterator<JsonElement> iterator = historicsInputDepartementsCouvreFeuList.iterator();
			while (iterator.hasNext()) {
				JsonElement elt = iterator.next();
				JsonObject jsonObject= elt.getAsJsonObject();
	

				if (jsonObject.get("linkssamedates_deptindex")!=null)
				{
					JsonElement jsondeptIndex= jsonObject.get("linkssamedates_deptindex");
					linkssamedates_deptindex=jsondeptIndex.getAsInt();

				}
			}
		}

		public MyInputDepartementsCouvreFeu(String path, InputDepartementsCouvreFeu inputDepartementsCouvreFeu) {
			super(path);
			// TODO Auto-generated constructor stub
			this.inputDepartementsCouvreFeu=inputDepartementsCouvreFeu;
			inputDepartementsCouvreFeu.setMyInputDepartementsCouvreFeu(this);
		}

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

						graqlInsertQuery+= "$DepartementGroup-" + indice.toString() +" isa " + DepartementConfinementClass_DepartementGroup + ", has Identity \"DCG-" + departement+"\";";

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
								Json sdatedebut=departementconfinement.at(titres.get(col));
								LocalDate datedebut=LocalDate.parse((CharSequence) "9999-01-01");
								if (sdatedebut.isString())
									datedebut=LocalDate.parse((CharSequence)sdatedebut.asString(), formatter);

								Json sdatefin=departementconfinement.at(titres.get(col+1));
								LocalDate datefin=LocalDate.parse((CharSequence) "9999-01-01");
								if (sdatefin.isString())
									datefin=LocalDate.parse((CharSequence)sdatefin.asString(), formatter);

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

								graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has Identity \"DTD-" + col + "-"+ indice.toString() + "\", has EventDate " + datedebut + ";";
								graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has Identity \"DTF-" + col + "-"+ indice.toString() + "\", has EventDate " + datefin + ";";
								graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa " + DepartementConfinementClass_DepartementConfinementPeriodOfTime + ", has Identity \"DPOfT-" + col + "-"+ indice.toString() + "\", has TypeFrequency \"Daily\";";

								graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";


								// horaires

								// debut
								graqlInsertQuery+= "$Value-HorairesDebut-" + col +"-"+ indice.toString() +" isa ValueHorairesStart, has IdValue \"DHD-" + col +"-"+ indice.toString() + "\", has DoubleValueAttribute " + doublestartdetailhours + ";";
								graqlInsertQuery+= "$ValueRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa " + DepartementConfinementClass_ValueEventRelations + ";";
								graqlInsertQuery+= "$ValueTypeRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa " + DepartementConfinementClass_TypeValueEventRelations + ";";

								// fin
								graqlInsertQuery+= "$Value-HorairesFin-" + col +"-"+ indice.toString() +" isa ValueHorairesEnd, has IdValue \"DHF-" + col +"-"+indice.toString() + "\", has DoubleValueAttribute " + doubleenddetailhours + ";";
								graqlInsertQuery+= "$ValueRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa "+ DepartementConfinementClass_ValueEventRelations + ";";
								graqlInsertQuery+= "$ValueTypeRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa " + DepartementConfinementClass_TypeValueEventRelations + ";";

								// no as a master data
								// graqlInsertQuery+= "$Departement-" + col +"-" + indice.toString() +" isa Departement , has Identity \"DD-" + col +"-"+indice.toString() + "\", has CodeGLN \"" + departementconfinement.at("departement").asString() + "\";";

								// relations de l'événement
								graqlInsertQuery+= "$eventRelation-" + indice.toString() + "-" + col +" (registeredevent : $Event-" + indice.toString() + "-" + col + ", actor: $DepartementGroup-" + indice.toString() + ", localization:" + "$Departement-" + departement + ", time :$Period-" + col + "-" + indice.toString()+ ", goal :$Why-DEPARTEMENTCONFINEMENT" + ", object : "+ whattype + ") isa " + DepartementConfinementClass_EventDepartementConfinementRelations+ " ;";

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
	}

	InputDepartementsCouvreFeu(OptionImport optionImport,String filename)
	{
		super(optionImport, filename);
	}

	public InputDepartementsCouvreFeu (OptionImport optionImport,String filename, int myMaxGet, Long minIndice, Long maxIndice, DF_CASE myDateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
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
		inputs=initialiseDefine(inputs, DepartementConfinementClass_DepartementConfinementTimeDate, "TimeDate");
		inputs=initialiseDefine(inputs, DepartementConfinementClass_DepartementGroup, "DepartementGroup");

		List<DefineAttribute> defineattributesWhatDepartementConfinement=new ArrayList<>();
		DefineAttribute attribut_departement = 	new DefineAttribute(DepartementConfinementTypeAttributeValue_departement,DepartementConfinementTypeValue_departement);

		defineattributesWhatDepartementConfinement.add(attribut_departement);

		inputs=initialiseDefine(inputs, DepartementConfinementClass_WhatDepartementConfinement, "IdentifiedWhat",defineattributesWhatDepartementConfinement);


		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WDC-1", "Description", "DEPARTEMENTCONFINEMENT");
		inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity", "W-1", "Description", "CONFINEMENT");
		inputs=initialiseInsertEntitySingleton(inputs, "IdentifiedWhat", "Identity", "W-2", "Description", "COUVREFEU");

		// data
		inputs.add(new MyInputDepartementsCouvreFeu(setOfData.getFilename(), this));

		/*inputs.add(new Input("data/departementdecret2") {
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

							graqlInsertQuery+= "$DepartementGroup-" + indice.toString() +" isa " + DepartementConfinementClass_DepartementGroup + ", has Identity \"DCG-" + departement+"\";";

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
									Json sdatedebut=departementconfinement.at(titres.get(col));
									LocalDate datedebut=LocalDate.parse((CharSequence) "9999-01-01");
									if (sdatedebut.isString())
										datedebut=LocalDate.parse((CharSequence)sdatedebut.asString(), formatter);

									Json sdatefin=departementconfinement.at(titres.get(col+1));
									LocalDate datefin=LocalDate.parse((CharSequence) "9999-01-01");
									if (sdatefin.isString())
										datefin=LocalDate.parse((CharSequence)sdatefin.asString(), formatter);

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

									graqlInsertQuery+= "$TimeDate-debut-" + col +"-"+ indice.toString() +" isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has Identity \"DTD-" + col + "-"+ indice.toString() + "\", has EventDate " + datedebut + ";";
									graqlInsertQuery+= "$TimeDate-fin-" + col +"-"+ indice.toString() +" isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has Identity \"DTF-" + col + "-"+ indice.toString() + "\", has EventDate " + datefin + ";";
									graqlInsertQuery+= "$Period-" + col +"-"+ indice.toString() + " isa " + DepartementConfinementClass_DepartementConfinementPeriodOfTime + ", has Identity \"DPOfT-" + col + "-"+ indice.toString() + "\", has TypeFrequency \"Daily\";";

									graqlInsertQuery+= "$PeriodRelation-" + col +"-" + indice.toString() +" (periodoftime : $Period-" + col +"-" + indice.toString() + " , startwhendate : " + "$TimeDate-debut-"  + col +"-"+ indice.toString() + " , endwhendate : " + "$TimeDate-fin-" + col + "-" + indice.toString() +  ") isa PeriodicRelation" + ";";


									// horaires

									// debut
									graqlInsertQuery+= "$Value-HorairesDebut-" + col +"-"+ indice.toString() +" isa ValueHorairesStart, has IdValue \"DHD-" + col +"-"+ indice.toString() + "\", has DoubleValueAttribute " + doublestartdetailhours + ";";
									graqlInsertQuery+= "$ValueRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa " + DepartementConfinementClass_ValueEventRelations + ";";
									graqlInsertQuery+= "$ValueTypeRelation-HorairesDebut-" + col +"-"+ indice.toString() +" (value : $Value-HorairesDebut-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa " + DepartementConfinementClass_TypeValueEventRelations + ";";

									// fin
									graqlInsertQuery+= "$Value-HorairesFin-" + col +"-"+ indice.toString() +" isa ValueHorairesEnd, has IdValue \"DHF-" + col +"-"+indice.toString() + "\", has DoubleValueAttribute " + doubleenddetailhours + ";";
									graqlInsertQuery+= "$ValueRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , resource : " + "$Period-" + col +"-"+ indice.toString() + ") isa "+ DepartementConfinementClass_ValueEventRelations + ";";
									graqlInsertQuery+= "$ValueTypeRelation-HorairesFin-" + col +"-"+ indice.toString() +" (value : $Value-HorairesFin-" + col +"-"+ indice.toString() + " , typevalue : $TypeValue-" + namehoraires+ ") isa " + DepartementConfinementClass_TypeValueEventRelations + ";";

									// no as a master data
									// graqlInsertQuery+= "$Departement-" + col +"-" + indice.toString() +" isa Departement , has Identity \"DD-" + col +"-"+indice.toString() + "\", has CodeGLN \"" + departementconfinement.at("departement").asString() + "\";";

									// relations de l'événement
									graqlInsertQuery+= "$eventRelation-" + indice.toString() + "-" + col +" (registeredevent : $Event-" + indice.toString() + "-" + col + ", actor: $DepartementGroup-" + indice.toString() + ", localization:" + "$Departement-" + departement + ", time :$Period-" + col + "-" + indice.toString()+ ", goal :$Why-DEPARTEMENTCONFINEMENT" + ", object : "+ whattype + ") isa " + DepartementConfinementClass_EventDepartementConfinementRelations+ " ;";

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
		});*/
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

		if (myInputDepartementsCouvreFeu== null)
		{
			myInputDepartementsCouvreFeu=new MyInputDepartementsCouvreFeu("", this);
			JsonArray jsonArray=PCRFilesStudyMigration.loadHistorics();
			myInputDepartementsCouvreFeu.loadHistoric(jsonArray);
		}
		

		if (optionImport.getTypeImport()==TypeImport.POSTCALCULDEPARTEMENTCONFINEMENTCLEAN)
		{
			PostCleanDoubleDataByClass(session);
		}
		if (optionImport.getTypeImport()==TypeImport.POSTCALCULDEPARTEMENTSAMEDATEPCRTEST)
		{
			LinkSameDateEvents(session);
		}
	}

	// Post calculation of average
	private void PostCleanDoubleDataByClass (GraknClient.Session session)  {

		try
		{

			boolean close = true;
			GraknClient.Transaction transaction= null;;

			Set<String> keysdept=getDepartementId().keySet();

			int ndept=0;
			for (String keydept : keysdept)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				int nextrapole=(ndept*254);
				System.out.println("nextrapole:"+nextrapole);

				if (nextrapole>minIndice)
				{

					if (close == true) {
						transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
						close = false;
					}

					// query more than one value
					String graqlQuery11 = "match $Departement id "+ idDepartement + ";";
					graqlQuery11 += 	"$what isa " + DepartementConfinementClass_WhatDepartementConfinement + ", has Identity $identity;";
					graqlQuery11+=		"(localization : $Departement, object : $what) isa "+ DepartementConfinementClass_EventDepartementConfinementRelations + ";";
					graqlQuery11+=		"get;";

					//System.out.println(graqlQuery11);

					QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
					List<ConceptMap> answers11= map11.get();

					System.out.println("ndept : " + ndept + " (what existing doubles or old values) : "+ answers11.size());

					for(ConceptMap answer11:answers11)
					{

						Entity resource= answer11.get("what").asEntity();
						String idResource=resource.id().toString();

						deleteEventObjectById(transaction, idResource, RelationTypeEvent.WHATRELATION);
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

					}

				}
				if (close==false)
					transaction.commit();
			}
			System.out.println("Clean Double Departement Confinement OK.\n");
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
			Boolean okanswers = true;
			boolean close = true;
			GraknClient.Transaction transaction= null;;

			Set<String> keysdept=getDepartementId().keySet();

			String[] depts=null;

			boolean dpt=false;

			if (optionImport.getDept()!=null)
			{
				if (optionImport.getDept().length>0)
				{
					depts=optionImport.getDept();
					dpt=true;
				}
			}
			if (dpt==false)
			{
				//depts=(String[])(keysdept.toArray());
				depts=new String[keysdept.size()];
				int i=0;
				for (String keydept:keysdept)
				{
					depts[i]=keydept;
					i++;
				}
			}

			int ndept=0;

			for (String keydept : depts)
			{
				String idDepartement = getDepartementId().get(keydept);
				ndept++;

				if (ndept>=linkssamedates_deptindex)
				{
					int nextrapole=(ndept*254);
					System.out.println("nextrapole:"+nextrapole);


					if (close == true) {
						transaction = session.transaction(GraknClient.Transaction.Type.WRITE);
						close = false; 
					}


					// first case (startwhendate)
					////////////////////////////
					String graqlQuery11 = "match ";
					graqlQuery11 += "$Departement id "+ idDepartement + ";";
					graqlQuery11+=	"(time : $period, localization : $Departement) isa " + DepartementConfinementClass_EventDepartementConfinementRelations+";";
					graqlQuery11+=	"$periodicrelation (periodoftime : $period, startwhendate : $timedate) isa PeriodicRelation;";
					graqlQuery11+=	"$timedate isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has EventDate $attributedate;";				

					graqlQuery11+=		"get;\n";

					System.out.println(graqlQuery11);

					QueryFuture<List<ConceptMap>> map11 = transaction.execute((GraqlGet)parse(graqlQuery11));
					List<ConceptMap> answers11= map11.get();

					int ndate=0;
					for(ConceptMap answer11:answers11)
					{
						ndate++;

						Entity time1= answer11.get("timedate").asEntity();
						String idTime1=time1.id().toString();

						LocalDateTime date1 = (LocalDateTime) answer11.get("attributedate").asAttribute().value();
						System.out.println("date :" + date1);

						DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");


						// PCR Test at same date
						////////////////////////
						String graqlQuery21 = "match ";
						graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1 + ";";
						graqlQuery21+=	"$Departement id " + idDepartement + ";";
						graqlQuery21+=	"(time : $timedateevent, localization : $Departement) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
						graqlQuery21+=	"get;\n";

						//System.out.println(graqlQuery21);

						QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
						List<ConceptMap> answers21= map21.get();

						for(ConceptMap answer21:answers21)
						{					
							Entity timedateevent= answer21.get("timedateevent").asEntity();
							createSameDateLink(transaction, time1, timedateevent, idDepartement);
						}
					}
					
					
					// second case (endwhendate)
					////////////////////////////
					String graqlQuery12 = "match ";
					graqlQuery12 += "$Departement id "+ idDepartement + ";";
					graqlQuery12+=	"(time : $period, localization : $Departement) isa " + DepartementConfinementClass_EventDepartementConfinementRelations+";";
					graqlQuery12+=	"$periodicrelation (periodoftime : $period, endwhendate : $timedate) isa PeriodicRelation;";
					graqlQuery12+=	"$timedate isa " + DepartementConfinementClass_DepartementConfinementTimeDate + ", has EventDate $attributedate;";				

					graqlQuery12+=		"get;\n";

					System.out.println(graqlQuery12);

					QueryFuture<List<ConceptMap>> map12 = transaction.execute((GraqlGet)parse(graqlQuery12));
					List<ConceptMap> answers12= map12.get();

					for(ConceptMap answer12:answers12)
					{
						ndate++;

						Entity time1= answer12.get("timedate").asEntity();
						String idTime1=time1.id().toString();

						LocalDateTime date1 = (LocalDateTime) answer12.get("attributedate").asAttribute().value();
						System.out.println("date :" + date1);

						DateTimeFormatter dateformat=DateTimeFormatter.ofPattern("yyyy-MM-dd");


						// PCR Test at same date
						////////////////////////
						String graqlQuery21 = "match ";
						graqlQuery21 += "$timedateevent isa PCRTimeDate, has EventDate "+ date1 + ";";
						graqlQuery21+=	"$Departement id " + idDepartement + ";";
						graqlQuery21+=	"(time : $timedateevent, localization : $Departement) isa " + PCRTest.PCRTestClass_EventPCRTestRelations+";";
						graqlQuery21+=	"get;\n";

						//System.out.println(graqlQuery21);

						QueryFuture<List<ConceptMap>> map21 = transaction.execute((GraqlGet)parse(graqlQuery21));
						List<ConceptMap> answers21= map21.get();

						for(ConceptMap answer21:answers21)
						{					
							Entity timedateevent= answer21.get("timedateevent").asEntity();
							createSameDateLink(transaction, time1, timedateevent, idDepartement);
						}
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

					}
					//}

					linkssamedates_deptindex=ndept;
					getMyInputDepartementsCouvreFeu().saveHistoric(PCRFilesStudyMigration.historics, true);
					PCRFilesStudyMigration.saveHistorics();
				}
			}
			if (close==false)
				transaction.commit();
			System.out.println("PostCalculation Next Date PCR OK.\n");
		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}
}
