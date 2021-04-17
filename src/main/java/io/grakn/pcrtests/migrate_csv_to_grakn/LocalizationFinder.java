package io.grakn.pcrtests.migrate_csv_to_grakn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import mjson.Json;

enum typeinforeverse
{
	reversedepartement
}
public class LocalizationFinder {

	String retrieve(Double lon, Double lat, typeinforeverse typeinfo)
	{
		String inline="";
		try {
			String requetereverse="https://api-adresse.data.gouv.fr/reverse/?lon="+lon+"&"+"lat="+lat;
			URL url = new URL(requetereverse);

			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			int responsecode = conn.getResponseCode();
			if (responsecode!=200)
			{
				throw new RuntimeException("HttpResponseCode: " + responsecode);
			}
			else
			{

				Scanner sc=new Scanner(url.openStream());
				while (sc.hasNext())
				{
					inline+=sc.nextLine();
				}
				System.out.println("\nJSON data in string format");
				System.out.println(inline);
				sc.close();

				if (inline!=null)
				{
					Json json=Json.read(inline);
					Json features=json.at("features");
					if (features!=null)
					{
						List<Json> aListFeatures=features.asJsonList();
						if (!aListFeatures.isEmpty())
						{
							for (Json feature : aListFeatures) {
								Json properties=feature.at("properties");

								Json context = properties.at("context");
								if (context!=null)
								{
									String scontext=context.asString();

									int separator=scontext.indexOf(',');
									String dept=scontext.substring(0, separator);
									return dept;
								}
							}
						}
					}
				}
			}

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		return "";
	}
}
