package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Entity;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlGet;


public class SetOfData implements ISetOfData{

	protected String filename;

	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	//List<String> titres=new ArrayList<String>();
	Map<String, String> departementId = new HashMap<>();

	public Map<String, String> getDepartementId() {
		return departementId;
	}
	
	protected void initializeDepartmentsCache(GraknClient.Session session)
	{
		try
		{

			GraknClient.Transaction transaction = session.transaction(GraknClient.Transaction.Type.WRITE);

			String graqlQuery1 = "match ";
			graqlQuery1+=		"$departement isa Departement, has CodeGLN $codeGLN;get;";	

			QueryFuture<List<ConceptMap>> map1 = transaction.execute((GraqlGet)parse(graqlQuery1));
			List<ConceptMap> answers1= map1.get();

			for(ConceptMap answer1:answers1)
			{	
				Entity departement1= answer1.get("departement").asEntity();
				String idDepartement=departement1.id().toString();
				//System.out.println("idDepartement : "  + idDepartement);

				String codeGLN = (String) answer1.get("codeGLN").asAttribute().value();
				//System.out.println("codeGLN : "  + codeGLN);

				departementId.put(codeGLN, idDepartement);

			} // for answer1

		}
		catch(GraknClientException e){
			System.out.println(e);
			throw(e);
		}  
	}

	protected void initializeCaches(GraknClient.Session session)
	{
		initializeDepartmentsCache(session);
	}

}
