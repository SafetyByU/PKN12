package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import grakn.client.GraknClient;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputWeather extends InputSetOfChronology {

	static final String WeatherTypeValue_MetaTypeValue = "WD";


	static final String WeatherClass_EventWeather = "EventWeather";
	static final String WeatherClass_EventWeatherDepartementGroup = "EventWeatherDepartementGroup";
	static final String WeatherClass_ValueEventWeatherRelations = "ValueEventWeatherRelations";
	static final String WeatherClass_TypeValueEventWeatherRelations = "TypeValueWeatherRelations";
	static final String WeatherClass_EventWeatherRelations = "EventWeatherRelations";

	static final String WeatherClass_WhatWeather="WhatWeather";
	static final String WeatherTypeValue_Departement = "Departement";
	static final String WeatherTypeAttributeValue_Departement = "StringValueAttribute";
	static final String WeatherTypeValue_TMin = "TMin";
	static final String WeatherTypeAttributeValue_TMin = "DoubleValueAttribute";
	static final String WeatherTypeValue_TMax = "TMax";
	static final String WeatherTypeAttributeValue_TMax = "DoubleValueAttribute";
	static final String WeatherTypeValue_TMoy = "TMoy";
	static final String WeatherTypeAttributeValue_TMoy = "DoubleValueAttribute";
	
	static final String WeatherTypeValue_PPOSTCALCUL = "WPOSTCALCUL";
	static final String WeatherTypeAttributeValue_PPOSTCALCUL = "StringValueAttribute";
	
	static final String TagInitPostCalculate = "0";
	
	String getMetaType() {return WeatherTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return WeatherClass_ValueEventWeatherRelations;};
	String getTypevalue_Eventrelations() {return WeatherClass_TypeValueEventWeatherRelations;};

	public InputWeather(OptionImport optionImport, String filename) {
		super(optionImport, filename);
	}

	public InputWeather (OptionImport optionImport, String filename, int myMaxGet, int minIndice, int maxIndice, DF_CASE dateFilter, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice,  dateFilter, myMinDate,  myMaxDate,  myMindays,  myMaxdays);
	}
	
	public InputWeather (OptionImport optionImport, String filename, int myMaxGet, int minIndice, int maxIndice, LocalDate myMinDate, LocalDate myMaxDate, int myMindays, int myMaxdays)
	{	
		super (optionImport, filename,  myMaxGet,  minIndice,  maxIndice, myMinDate,  myMaxDate, myMindays, myMaxdays);
	}
	
	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {

		// TODO Auto-generated method stub
		return initialiseInputsWeather(inputs);
	}

	
	// événement confinements
	protected Collection<Input> initialiseInputsWeather(Collection<Input> inputs) {

		// derived types

		inputs = initialiseDefine(inputs, WeatherClass_EventWeather, "event");
		inputs = initialiseDefine(inputs, WeatherClass_EventWeatherDepartementGroup, "GroupWho");

		inputs = initialiseDefine(inputs, WeatherClass_EventWeatherRelations, "eventrelations" + SuffixEventRelations);
		inputs = initialiseDefine(inputs, WeatherClass_ValueEventWeatherRelations,
				"ValueRelation" + SuffixValueRelations);
		inputs = initialiseDefine(inputs, WeatherClass_TypeValueEventWeatherRelations,
				"ValueTypeRelation" + SuffixValueTypeRelations);

		List<DefineAttribute> defineattributesWhatWeather=new ArrayList<>();
		DefineAttribute attribut_tmin = 	new DefineAttribute(WeatherTypeAttributeValue_TMin,WeatherTypeValue_TMin);
		DefineAttribute attribut_tmax = 	new DefineAttribute(WeatherTypeAttributeValue_TMax,WeatherTypeValue_TMax);
		DefineAttribute attribut_tmoy = 	new DefineAttribute(WeatherTypeAttributeValue_TMoy,WeatherTypeValue_TMoy);

		defineattributesWhatWeather.add(attribut_tmin);
		defineattributesWhatWeather.add(attribut_tmax);
		defineattributesWhatWeather.add(attribut_tmoy);
		
		inputs=initialiseDefine(inputs, WeatherClass_WhatWeather, "IdentifiedWhat",defineattributesWhatWeather);
		
		// singletons
		inputs = initialiseInsertEntitySingleton(inputs, "Why", "Identity", "WWE-1", "Description", "WEATHER");
//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_Departement);
//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMin);
//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMax);
//		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_TMoy);
		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", WeatherTypeValue_PPOSTCALCUL);

		// data
		inputs.add(new Input(setOfData.getFilename()) {
			@Override
			public String template(Json weather) {

				String graqlInsertQuery = "";
				try {

					if (weather.at("code_insee_departement") != null) {

						String departement = weather.at("code_insee_departement").asString();

						String sdate = weather.at("date_obs").asString();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-M-d");

						LocalDate date = LocalDate.parse(sdate, formatter);

						// System.out.println ("Indice : " + indice + " date : " + date);
						indice++;

						if ((indice <= maxIndice && indice >= minIndice && (dateFilter == DF_CASE.FALSE ))
								|| (date.isAfter(minDate) && date.isBefore(maxDate) && (dateFilter == DF_CASE.TRUE))
								|| (date.isAfter(minDate) && date.isBefore(maxDate) && (indice <= maxIndice) && (indice >= minIndice) && (dateFilter == DF_CASE.BOTH))) 

						{

							Double TMin = weather.at("tmin").asDouble();
							Double TMax = weather.at("tmax").asDouble();
							Double TMoy = weather.at("tmoy").asDouble();

							graqlInsertQuery = "match ";
//							graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
//									WeatherTypeValue_Departement);
//							graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
//									WeatherTypeValue_TMin);
//							graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
//									WeatherTypeValue_TMax);
//							graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue",
//									WeatherTypeValue_TMoy);
							graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", WeatherTypeValue_PPOSTCALCUL);
							
							graqlInsertQuery += InputSingletonDefineQuery("", "Why", "Description", "WEATHER");
							graqlInsertQuery += InputSingletonDefineQuery("", "Departement", "CodeGLN", departement);

							// à poursuivre avec evenement simple confinement et périodique couvrefeu
							graqlInsertQuery += "\ninsert ";

							graqlInsertQuery += "$IdentifiedWhat isa " + WeatherClass_WhatWeather +" , has Identity \"WEATHER-"
									+ indice.toString() + "-" + departement + "\"";
							
							graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMin, TMin.toString());
							graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMax, TMax.toString());
							graqlInsertQuery+=insertAttribut (WeatherTypeValue_TMoy, TMoy.toString());
							graqlInsertQuery+= ";";
									
							graqlInsertQuery += "$DepartementGroup isa DepartementGroup, has Identity \"WDG-"
									+ indice.toString() + "-" + departement + "\";";

//							graqlInsertQuery += insertValue(
//									WeatherTypeValue_Departement, indice.toString(), "StringValue", departement,
//									"$DepartementGroup");
//							graqlInsertQuery += insertValue(WeatherTypeValue_TMin,
//									indice.toString(), "DoubleValue", TMin.toString(), "$IdentifiedWhat");
//							graqlInsertQuery += insertValue(WeatherTypeValue_TMax,
//									indice.toString(), "DoubleValue", TMax.toString(), "$IdentifiedWhat");
//							graqlInsertQuery += insertValue(WeatherTypeValue_TMoy,
//									indice.toString(), "DoubleValue", TMoy.toString(), "$IdentifiedWhat");
							graqlInsertQuery += insertValue (WeatherTypeValue_PPOSTCALCUL, 
									indice.toString(), "StringValue", TagInitPostCalculate, "$IdentifiedWhat");

							graqlInsertQuery += "$TimeDate isa TimeDate, has Identity \"WTD-" + indice.toString()
									+ "\", has EventDate " + date + ";";

							graqlInsertQuery += "$Event isa EventWeather, has Id \"EW-" + indice.toString() + "\";";

							graqlInsertQuery += "$eventRelation(registeredevent : $Event, actor: $DepartementGroup, localization: $Departement-"
									+ departement + ", time :$TimeDate, goal :$Why-WEATHER"
									+ ", object : $IdentifiedWhat) isa " + WeatherClass_EventWeatherRelations + ";";

						}

					}

				} catch (Exception e) {
					int y = 0;
				}
				return (graqlInsertQuery);
			}
		});
		return inputs;
	}
	
	public void postCalculate(GraknClient.Session session)
	{
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
