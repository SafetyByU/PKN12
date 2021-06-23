package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.time.LocalDate;
import java.util.Collection;

import grakn.client.GraknClient;
import io.grakn.pcrtests.migrate_csv_to_grakn.PCRFilesStudyMigration.Input;
import mjson.Json;

public class InputShops extends InputSetOfData {

	static final String ShopTypeValue_MetaTypeValue = "SS";
	static final String ShopClass_ValueEventRelations = "ValueEventShopRelations";
	static final String ShopClass_TypeValueEventRelations = "TypeValueShopRelations";

	String getMetaType() {return ShopTypeValue_MetaTypeValue;}
	String GetValue_Eventrelation() {return ShopClass_ValueEventRelations;};
	String getTypevalue_Eventrelations() {return ShopClass_TypeValueEventRelations;};

	static final String ShopTypeValue_PPOSTCALCUL = "SPOSTCALCUL";
	static final String ShopTypeAttributeValue_PPOSTCALCUL = "StringValueAttribute";
	
	static final String TagInitPostCalculate = "0";
	
	public InputShops(OptionImport optionImport, String filename)
	{
		super(optionImport, filename);
	}
	
	public InputShops (OptionImport optionImport,String filename, int myMaxGet, Long minIndice, Long maxIndice)
	{	
		super ( optionImport, filename,  myMaxGet,  minIndice,  maxIndice);
	}
	
	@Override
	public Collection<Input> initialize(Collection<Input> inputs) {
			
		// TODO Auto-generated method stub
		return initialiseInputsShops(inputs);
	}

	// shops by departement
	protected Collection<Input> initialiseInputsShops(Collection<Input>inputs) {

		// derived types
		inputs=initialiseDefine(inputs, "Shop", "IdentifiedWho");
		inputs=initialiseDefine(inputs, "ValueShopRelations", "ValueRelation"+SuffixValueRelations);
		inputs=initialiseDefine(inputs, "TypeValueShopRelations", "ValueTypeRelation"+SuffixValueTypeRelations);

		// singletons
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Name");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Category");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "SubCategory");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Brand");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "WikiData");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "UrlHours");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Infos");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Status");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "OpeningHours");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Lat");
		inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Lon");
		//inputs=initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", "Dept");
		inputs = initialiseInsertEntitySingleton(inputs, "TypeValue", "IdValue", ShopTypeValue_PPOSTCALCUL);
		// data
		inputs.add(new Input(setOfData.getFilename()) {
			@Override
			public String template(Json shops) {

				String graqlInsertQuery="";
				if (shops.at("osm_id")!=null)
				{

					indice++;

					if (indice%100000==0)
					{
						try {
							System.out.println("pause 2 s pour passer antispam ;indice :"+indice+"\n");
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (indice<=maxIndice && indice>=minIndice)
					{
						graqlInsertQuery = "match ";
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Name");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Category");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "SubCategory");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Brand");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "WikiData");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "UrlHours");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Infos");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Status");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "OpeningHours");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Lat");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Lon");
						graqlInsertQuery += InputSingletonDefineQuery("", "TypeValue", "IdValue", "Dept");

						graqlInsertQuery += InputSingletonDefineQuery("","TypeValue", "IdValue", ShopTypeValue_PPOSTCALCUL);

						// data
						graqlInsertQuery+= "insert $Who-Shop-" + indice.toString() + " isa Shop, has Identity \"" + shops.at("osm_id").asString() + "\";";

						if (!shops.at("name").isNull())
						{
							String name=shops.at("name").asString().replace('"', ' ');
							graqlInsertQuery+= "$Value-Shop-Name-" + indice.toString() +" isa StringValue, has IdValue \"SN-" + indice.toString() + "\", has StringValueAttribute \"" + name + "\";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-Name-" + indice.toString() +" (value : $Value-Shop-Name-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-Name-" + indice.toString() +" (value : $Value-Shop-Name-" + indice.toString() + " , typevalue : $TypeValue-Name) isa TypeValueShopRelations" + ";\n";
						}

						if (!shops.at("category").isNull())
						{
							String category = shops.at("category").asString();
							graqlInsertQuery+= "$Value-Shop-Category" +" isa StringValue, has IdValue \"SC-" + indice.toString() + "\", has StringValueAttribute \"" + category + "\";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-Category" +" (value : $Value-Shop-Category , typevalue : $TypeValue-Category) isa TypeValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-Category-" + indice.toString() +" (value : $Value-Shop-Category, resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
						}

						if (!shops.at("subcategory").isNull())
						{
							String subcategory = shops.at("subcategory").asString();

							graqlInsertQuery+= "$Value-Shop-SubCategory isa StringValue, has IdValue \"SSC-" + indice.toString() + "\", has StringValueAttribute \"" + subcategory + "\";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-SubCategory (value : $Value-Shop-SubCategory, typevalue : $TypeValue-SubCategory) isa TypeValueShopRelations" + ";\n";

							graqlInsertQuery+= "$ValueRelation-Shop-SubCategory-" + indice.toString() +" (value : $Value-Shop-SubCategory, resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
						}

						if (!shops.at("brand").isNull())
						{
							String brand = shops.at("brand").asString().replace('"', ' ');

							graqlInsertQuery+= "$Value-Shop-Brand isa StringValue, has IdValue \"SB-" + indice.toString() + "\", has StringValueAttribute \"" + brand + "\";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-Brand" + " (value : $Value-Shop-Brand , typevalue : $TypeValue-Brand) isa TypeValueShopRelations" + ";\n";

							graqlInsertQuery+= "$ValueRelation-Shop-Brand-" + indice.toString() +" (value : $Value-Shop-Brand , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
						}

						if (!shops.at("wikidata").isNull())
						{
							String wikidata = shops.at("wikidata").asString().replace('"', ' ');

							graqlInsertQuery+= "$Value-Shop-WikiData-" + indice.toString() +" isa StringValue, has IdValue \"SW-" + indice.toString() + "\", has StringValueAttribute \"" + wikidata + "\";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-WikiData-" + indice.toString() +" (value : $Value-Shop-WikiData-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-WikiData-" + indice.toString() +" (value : $Value-Shop-WikiData-" + indice.toString() + " , typevalue : $TypeValue-WikiData) isa TypeValueShopRelations" + ";\n";
						}

						if (!shops.at("url_hours").isNull())
						{
							String url_hours = shops.at("url_hours").asString().replace('"', ' ');
							
							graqlInsertQuery+= "$Value-Shop-UrlHours-" + indice.toString() +" isa StringValue, has IdValue \"SU-" + indice.toString() + "\", has StringValueAttribute \"" + url_hours + "\";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-UrlHours-" + indice.toString() +" (value : $Value-Shop-UrlHours-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueTypeRelation-ShopUrlHours-" + indice.toString() +" (value : $Value-Shop-UrlHours-" + indice.toString() + " , typevalue : $TypeValue-UrlHours) isa TypeValueShopRelations" + ";\n";
						}

						if (!shops.at("infos").isNull())
						{
							String infos = shops.at("infos").asString().replace('"', ' ');

							graqlInsertQuery+= "$Value-Shop-Infos-" + indice.toString() +" isa StringValue, has IdValue \"SI-" + indice.toString() + "\", has StringValueAttribute \"" + infos + "\";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-Infos-" + indice.toString() +" (value : $Value-Shop-Infos-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-Infos-" + indice.toString() +" (value : $Value-Shop-Infos-" + indice.toString() + " , typevalue : $TypeValue-Infos) isa TypeValueShopRelations" + ";\n";
						}

						if (!shops.at("status").isNull())
						{
							String status = shops.at("status").asString().replace('"', ' ');

							graqlInsertQuery+= "$Value-Shop-Status isa StringValue, has IdValue \"SS-" + indice.toString() + "\", has StringValueAttribute \"" + status + "\";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-Status (value : $Value-Shop-Status, typevalue : $TypeValue-Status) isa TypeValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-Status-" + indice.toString() +" (value : $Value-Shop-Status, resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
						}

						if (!shops.at("opening_hours").isNull())
						{
							String opening_hours = shops.at("opening_hours").asString().replace('"', ' ');
							
							graqlInsertQuery+= "$Value-Shop-OpeningHours-" + indice.toString() + " isa StringValue, has IdValue \"SO-" + indice.toString() + "\", has StringValueAttribute \"" + opening_hours + "\";\n";
							graqlInsertQuery+= "$ValueRelation-Shop-OpeningHours-" + indice.toString() +" (value : $Value-Shop-OpeningHours-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
							graqlInsertQuery+= "$ValueTypeRelation-Shop-OpeningHours-" + indice.toString() +" (value : $Value-Shop-OpeningHours-" + indice.toString() + " , typevalue : $TypeValue-OpeningHours) isa TypeValueShopRelations" + ";\n";
						}

						if (!shops.at("lon").isNull() && !shops.at("lat").isNull())
						{
							Double lon=0.0;
							try
							{
								lon=shops.at("lon").asDouble();
								String sLon=GraphQlDoubleFormat(lon);
								graqlInsertQuery+= "$Value-Shop-Lon-" + indice.toString() +" isa DoubleValue, has IdValue \"SLO-" + indice.toString() + "\", has DoubleValueAttribute " + sLon + ";\n";
								graqlInsertQuery+= "$ValueRelation-Shop-Lon-" + indice.toString() +" (value : $Value-Shop-Lon-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
								graqlInsertQuery+= "$ValueTypeRelation-Shop-Lon-" + indice.toString() +" (value : $Value-Shop-Lon-" + indice.toString() + " , typevalue : $TypeValue-Lon) isa TypeValueShopRelations" + ";\n";


								Double lat=0.0;
								lat=shops.at("lat").asDouble();
								String sLat=GraphQlDoubleFormat(lat);
								graqlInsertQuery+= "$Value-Shop-Lat-" + indice.toString() +" isa DoubleValue, has IdValue \"SLA-" + indice.toString() + "\", has DoubleValueAttribute " + sLat + ";\n";
								graqlInsertQuery+= "$ValueRelation-Shop-Lat-" + indice.toString() +" (value : $Value-Shop-Lat-" + indice.toString() + " , resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";
								graqlInsertQuery+= "$ValueTypeRelation-Shop-Lat-" + indice.toString() +" (value : $Value-Shop-Lat-" + indice.toString() + " , typevalue : $TypeValue-Lat) isa TypeValueShopRelations" + ";\n";

								LocalizationFinder finder = new LocalizationFinder();
								String departement=finder.retrieve(lon, lat,typeinforeverse.reversedepartement);

								graqlInsertQuery+= "$Value-Shop-Dept isa StringValue, has IdValue \"SD-" + indice.toString() + "\", has StringValueAttribute \"" + departement+ "\";\n";
								graqlInsertQuery+= "$ValueRelation-Shop-Dept (value : $Value-Shop-Dept, resource : " + "$Who-Shop-" + indice.toString() + ") isa ValueShopRelations" + ";\n";

								graqlInsertQuery+= "$ValueTypeRelation-Shop-Dept-" + indice.toString() +" (value : $Value-Shop-Dept, typevalue : $TypeValue-Dept) isa TypeValueShopRelations" + ";\n";
							
								graqlInsertQuery += insertValue (ShopTypeValue_PPOSTCALCUL, 
										indice.toString(), "StringValue", TagInitPostCalculate, "$IdentifiedWhat");

							
							}
							catch(Exception e)
							{
								System.out.println ("Ligne mal formattée ou incomplète : " + shops.toString());
							}
						}
						else
						{
							int test=0;
						}
					}
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
