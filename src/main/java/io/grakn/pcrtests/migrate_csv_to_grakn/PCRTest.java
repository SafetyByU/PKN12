package io.grakn.pcrtests.migrate_csv_to_grakn;

import grakn.client.GraknClient;



public class PCRTest extends Cohort implements IPCRTest{

	static final String PCRTestMetaTypeValue = "PCR";
	static final String PCRTestTypeValue_P = "P";
	static final String PCRTestTypeAttributeValue_P = "LongValueAttribute";
	static final String PCRTestTypeValue_T = "T";
	static final String PCRTestTypeAttributeValue_T = "LongValueAttribute";
	static final String PCRTestTypeValue_PdivT = "PdivT";
	static final String PCRTestTypeAttributeValue_PdivT = "DoubleValueAttribute";
	static final String PCRTestTypeValue_AVPdivT = "AVPdivT";
	static final String PCRTestTypeValue_AVDPdivT = "AVDPdivT";
	static final String PCRTestTypeAttributeValue_AVPdivT = "DoubleValueAttribute";
	static final String PCRTestTypeValue_VPdivT = "VPdivT";
	static final String PCRTestTypeAttributeValue_VPdivT = "DoubleValueAttribute";
	static final String PCRTestTypeValue_ACPdivT = "ACPdivT";
	static final String PCRTestTypeAttributeValue_ACPdivT = "DoubleValueAttribute";
	static final String PCRTestTypeValue_PPOSTCALCUL = "PPOSTCALCUL";
	static final String PCRTestTypeAttributeValue_PPOSTCALCUL = "StringValueAttribute";
	static final String PCRTestTypeValue_PPOSTCALCULLINKS = "PPOSTCALCULLINKS";
	static final String PCRTestTypeValue_PPOSTCALCULNEXTLINK = "PPOSTCALCULNEXTLINK";
	static final String PCRTestTypeAttributeValue_PPOSTCALCULLINKS = "StringValueAttribute";
	//static final String PCRTestTypeValue_cl_age90 = "cl_age90";
	//static final String PCRTestTypeAttributeValue_cl_age90 = "LongValueAttribute";
	//static final String PCRTestTypeValue_pop = "pop";
	//static final String PCRTestTypeAttributeValue_pop = "LongValueAttribute";

	static final String PCRTestClass_EventPCRTest = "EventPCRTest";
	static final String PCRTestClass_ValueEventPCRTestRelations = "ValueEventPCRTestRelations";
	static final String PCRTestClass_TypeValueEventPCRTestRelations = "TypeValueEventPCRTestRelations";
	static final String PCRTestClass_EventPCRTestRelations = "EventPCRTestRelations";
	static final String PCRTestClass_WhatPCRTest="WhatPCRTest";
	static final String PCRTestClass_PCRTimeDate="PCRTimeDate";
	static final String PCRTestClass_PCRPeriodOfTime="PCRPeriodOfTime";
	static final String PCRTestClass_PCRPeriodicRelation="PCRPeriodicRelation";

	static final String PCRTestClass_PCRTestSameDateLinks="EventPCRTestSameDateLinks";

	static final String TagInitPostCalculate = "0";
	static final String TagAveragePostCalculate = "AV";
	static final String TagSpeedPostCalculate = "S";
	static final String TagAccelerationPostCalculate = "AC";
	static final String TagNotPostCalculate = "-";

	static final String TagLinksPostCalculate = "L";
	
	
	/*Map<Long, String> cl_age90Id = new HashMap<>();
	
	public Map<Long, String> getCl_age90Id() {
		return cl_age90Id;
	}*/
	
	protected void initializeCaches(GraknClient.Session session)
	{
		super.initializeCaches(session);
		//initializeAgesCache(session);
	}

}
