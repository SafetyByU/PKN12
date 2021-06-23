package io.grakn.pcrtests.migrate_csv_to_grakn;

import static graql.lang.Graql.parse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import grakn.client.GraknClient;
import grakn.client.GraknClient.Transaction.QueryFuture;
import grakn.client.answer.Answer;
import grakn.client.answer.ConceptMap;
import grakn.client.concept.thing.Attribute;
import grakn.client.concept.thing.Entity;
import grakn.client.concept.type.EntityType;
import grakn.client.exception.GraknClientException;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import mjson.Json;



public class Vaccin extends Cohort implements IVaccin{
	
	static final String VaccinMetaTypeValue = "Vaccin";
	
	static final String VaccinTypeValue_TypeVaccin1 = "TypeVaccin1";
	static final String VaccinTypeAttributeValue_TypeVaccin1 = "StringValueAttribute";
	static final String VaccinTypeValue_TypeVaccin2 = "TypeVaccin2";
	static final String VaccinTypeAttributeValue_TypeVaccin2 = "StringValueAttribute";
	
	static final String VaccinTypeValue_Dose1 = "ValueDose1";
	static final String VaccinTypeAttributeValue_Dose1 = "DoubleValueAttribute";
	static final String VaccinTypeValue_Dose2 = "ValueDose2";
	static final String VaccinTypeAttributeValue_Dose2 = "DoubleValueAttribute";
	static final String VaccinTypeValue_CumulDose1 = "ValueCumulDose1";
	static final String VaccinTypeAttributeValue_CumulDose1 = "DoubleValueAttribute";
	static final String VaccinTypeValue_CumulDose2 = "ValueCumulDose2";
	static final String VaccinTypeAttributeValue_CumulDose2 = "DoubleValueAttribute";

		
	static final String VaccinTypeValue_Coverage1 = "ValueCoverage1";
	static final String VaccinTypeAttributeValue_Coverage1 = "DoubleValueAttribute";
		static final String VaccinTypeValue_Coverage2 = "ValueCoverage2";
	static final String VaccinTypeAttributeValue_Coverage2 = "DoubleValueAttribute";
	
	static final String VaccinTypeValue_AVCoverage1 = "AVCoverage1";
	static final String VaccinTypeAttributeValue_AVCoverage1 = "DoubleValueAttribute";
	static final String VaccinTypeValue_AVCoverage2 = "AVCoverage2";
	static final String VaccinTypeAttributeValue_AVCoverage2 = "DoubleValueAttribute";

	static final String VaccinTypeValue_VCoverage1 = "VCoverage1";
	static final String VaccinTypeAttributeValue_VCoverage1 = "DoubleValueAttribute";
	static final String VaccinTypeValue_VCoverage2 = "VCoverage2";
	static final String VaccinTypeAttributeValue_VCoverage2 = "DoubleValueAttribute";
	
	static final String VaccinTypeValue_ACoverage1 = "ACoverage1";
	static final String VaccinTypeAttributeValue_ACoverage1 = "DoubleValueAttribute";
	static final String VaccinTypeValue_ACoverage2 = "ACoverage2";
	static final String VaccinTypeAttributeValue_ACoverage2 = "DoubleValueAttribute";
	
	static final String VaccinTypeValue_VACCINPOSTCALCUL = "VACCINPOSTCALCUL";
	static final String VaccinTypeAttributeValue_VACCINPOSTCALCUL = "StringValueAttribute";
		
	static final String VaccinTypeValue_VACCINPOSTCALCULNEXTLINK = "VACCINPOSTCALCULNEXTLINK";
	static final String VaccinTypeAttributeValue_VACCINPOSTCALCULLINKS = "StringValueAttribute";
	
	//static final String VaccinTypeValue_cl_age90 = "cl_age90";
	//static final String VaccinTypeAttributeValue_cl_age90 = "LongValueAttribute";
	
	//static final String VaccinTypeValue_pop = "pop";
	//static final String VaccinTypeAttributeValue_pop = "LongValueAttribute";

	static final String VaccinClass_EventVaccin = "EventVaccin";
	static final String VaccinClass_ValueEventVaccinRelations = "ValueEventVaccinRelations";
	static final String VaccinClass_TypeValueEventVaccinRelations = "TypeValueEventVaccinRelations";
	static final String VaccinClass_EventVaccinRelations = "EventVaccinRelations";
	static final String VaccinClass_WhatVaccin="WhatVaccin";
	static final String VaccinClass_VaccinTimeDate="VaccinTimeDate";

	static final String TagInitPostCalculate = "0";
	static final String TagAveragePostCalculate = "AV";
	static final String TagSpeedPostCalculate = "S";
	static final String TagAccelerationPostCalculate = "AC";
	static final String TagNotPostCalculate = "-";

	static final String TagLinksPostCalculate = "L";
	
	protected void initializeCaches(GraknClient.Session session)
	{
		super.initializeCaches(session);
	}
}
