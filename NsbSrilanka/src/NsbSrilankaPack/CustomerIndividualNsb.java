package NsbSrilankaPack;

import java.util.List;
//import java.util.Map;
import java.util.Map;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
//import com.temenos.t24.api.records.customer.EmploymentStatusClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebdistrictnsb.EbDistrictNsbRecord;

import CommonUtilsNsb.CalculateAgeNsb;
import CommonUtilsNsb.CheckAgeFromNicNsb;
import CommonUtilsNsb.CheckNicGenderDobNsb;
import CommonUtilsNsb.EbCommonParam;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerIndividualNsb extends RecordLifecycle {

    private LegalIdClass e;

    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        /*
         * INITIALISE:
         ************/
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        TField Title = CustomerRec.getTitle();
        TField GivenName = CustomerRec.getGivenNames();
        TField FamilyName = CustomerRec.getFamilyName();
        List<LocalRefGroup> lFullNamegrp = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FULL.NAME");

        /*
         * SETTING NAMES TO UPPER CASE:
         *****************************/
        CustomerRec.setGivenNames(GivenName.toString().toUpperCase());
        CustomerRec.setFamilyName(FamilyName.toString().toUpperCase());

        /*
         * DEFAULTING LOCAL FIELD FULL NAME WITH GIVEN.NAME & FAMILY NAME:
         ****************************************************************/

        LocalRefGroup grp = CustomerRec.createLocalRefGroup("L.FULL.NAME");
        grp.getLocalRefField("L.FULL.NAME")
                .setValue(GivenName.toString().toUpperCase() + ' ' + FamilyName.toString().toUpperCase());

        int lFullNameLength = lFullNamegrp.size();
        while (lFullNameLength > 0) {
            lFullNamegrp.remove(lFullNameLength - 1);
            lFullNameLength = lFullNameLength - 1;
        }
        lFullNamegrp.add(grp);
        /*
         * DEFAULTING SHORT.NAME WITH TITLE, GIVEN.NAME & FAMILY NAME:
         ************************************************************/
        String[] FamilyNameSplit = FamilyName.toString().split(" ");
        String ShortNameValue = Character.toString(FamilyNameSplit[0].charAt(0));
        for (int count = 1; count < FamilyNameSplit.length; count++) {
            ShortNameValue = ShortNameValue + " " + FamilyNameSplit[count].charAt(0);
        }
        CustomerRec.setShortName(Title.toString().toUpperCase() + ' ' + ShortNameValue.toUpperCase() + ' '
                + GivenName.toString().toUpperCase(), 0);
        /*
         * DEFAULTING MNEMONIC WITH C-CUSTOMER.NUMBER:
         ********************************************
         * CustomerRec.setMnemonic("C-" + currentRecordId);
         * CustomerRec.setAccountOfficer(CustomerRecgetCompanyBook().getValue())
         * ;
         */
        /*
         * DEFAULT OCCUPATION FROM JOB.TITLE:
         **********************************/
        for (int i = 0; i < CustomerRec.getEmploymentStatus().size(); i++) {
            TField JobTitle = CustomerRec.getEmploymentStatus().get(i).getJobTitle();
            CustomerRec.getEmploymentStatus().get(i).setOccupation(JobTitle);
        }

        currentRecord.set(CustomerRec.toStructure());
        /*
         * super.defaultFieldValuesOnHotField(application, currentRecordId,
         * currentRecord, currentInputValue, unauthorisedRecord, liveRecord,
         * transact ionContext);
         */
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        /*
         * DEFAULT PROVINCE FROM DISTRICT:
         ********************************/
        DataAccess DataObj = new DataAccess(this);
        TStructure DistrictNsb = DataObj.getRecord("EB.DISTRICT.NSB",
                CustomerRec.getLocalRefField("L.DISTRICT").toString());

        EbDistrictNsbRecord ProvinceNsb = new EbDistrictNsbRecord(DistrictNsb);
        CustomerRec.getLocalRefField("L.PROVINCE").setValue(ProvinceNsb.getProvince().toString());

        /*
         * CALCULATE AGE OF CUSTOMER:
         ***************************/
        CalculateAgeNsb nsb_obj = new CalculateAgeNsb(CustomerRec.getDateOfBirth().toString());
        int Age = nsb_obj.get_age_integer();
        CustomerRec.getLocalRefField("L.CUST.AGE").setValue(String.valueOf(Age) + " Years");

        /*
         * DEFAULT LEGAL DOC NAME WITH BIRTH CERTIFICATE FOR CUSTOMER AGE UNDER
         * 16:
         **************************************************************************/

        // String StringAge =
        // CustomerRec.getLocalRefField("L.CUST.AGE").getValue().toString().substring(0,
        // 2);
        /*
         * int LegalIdSize = CustomerRec.getLegalId().size(); int IntAge = Age;
         * if (LegalIdSize == 0){
         * CustomerRec.setBuildingNumber(String.valueOf(IntAge)); }
         */
        CustomerRec.getLegalId().get(0).setLegalDocName("NIC");

        // CustomerRec.getLegalId().get(0).setLegalDocName("BIRTH.CERTIFICATE");
        /*
         * if ((LegalIdSize == 0) && (IntAge < 16)) {
         * CustomerRec.getLegalId().get(0).setLegalDocName("BIRTH.CERTIFICATE");
         * } /*else if ((LegalIdSize == 0) && (Age >= 16)) { if
         * (CustomerRec.getNationality().getValue().toString().equals("LK")) {
         * CustomerRec.getLegalId().get(0).setLegalDocName("NIC"); } else {
         * CustomerRec.getLegalId().get(0).setLegalDocName("NATIONAL.ID"); } }
         * /* else { for (int LegalIdClassCount = 0; LegalIdClassCount <
         * LegalIdSize; LegalIdClassCount++) {
         * 
         * int BirthCertificateIndex =
         * CustomerRec.getLegalId().get(LegalIdClassCount).getLegalDocName().
         * getValue() .indexOf("BIRTH.CERTIFICATE"); int NicIndex =
         * CustomerRec.getLegalId().get(LegalIdClassCount).getLegalDocName().
         * getValue() .indexOf("NIC"); int NationalIdIndex =
         * CustomerRec.getLegalId().get(LegalIdClassCount).getLegalDocName().
         * getValue() .indexOf("NATIONAL.ID");
         * 
         * if ((Age < 16) & (BirthCertificateIndex < 0)) {
         * CustomerRec.getLegalId().get(LegalIdSize).setLegalDocName(
         * "BIRTH.CERTIFICATE"); } if ((Age >= 16) &&
         * (CustomerRec.getNationality().getValue().toString().equals("LK")) &&
         * (NicIndex < 0)) {
         * CustomerRec.getLegalId().get(LegalIdSize).setLegalDocName("NIC"); }
         * if ((Age >= 16) &&
         * (CustomerRec.getNationality().getValue().toString().equals("LK")) &&
         * (NationalIdIndex < 0)) {
         * CustomerRec.getLegalId().get(LegalIdSize).setLegalDocName(
         * "NATIONAL.ID") ; } } }
         * 
         * /********** TESTIGN EB.COMMON.PARAM.NSB
         *****************************/

        /*
         * String EcpRecordId = "TEST;CUSTOMER"; String EcpParamName =
         * "A#B;TARGET.EPF#LEGAL.DOC.NAME"; uGetParamValue ebcp = new
         * uGetParamValue(); Map<String, List<TField>> Config =
         * ebcp.getMultipleParamFromMultipleRecordRepeatVariebles(EcpRecordId,
         * EcpParamName, DataObj, "#", ";");
         * CustomerRec.setBuildingName(Config.get("TEST;A").get(0).getValue().
         * toString());
         * CustomerRec.getLocalRefField("L.STUDENT.FILE").setValue(Config.get(
         * "TEST;A").get(1).getValue().toString());
         * CustomerRec.getLocalRefField("L.STU.ACC.NO").setValue(Config.get(
         * "TEST;B").get(0).getValue().toString());
         * CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Config.get(
         * "TEST;B").get(1).getValue().toString());
         * CustomerRec.getLocalRefField("L.STU.ADDRESS").setValue(Config.get(
         * "CUSTOMER;TARGET.EPF").get(0).getValue().toString());
         * 
         * CustomerRec.getLocalRefField("L.STUDENT.ID").setValue(Config.get(
         * "CUSTOMER;LEGAL.DOC.NAME").get(0).getValue().toString());
         * CustomerRec.getLocalRefField("").setValue(Config.get(
         * "CUSTOMER;LEGAL.DOC.NAME").get(1).getValue().toString());
         */

        EbCommonParam Config = new EbCommonParam();
        Config.Add_Config("TEST", new String[]{"A","B"});
        Config.Add_Config("CUSTOMER", new String[] { "TARGET.EPF", "LEGAL.DOC.NAME" });
        Map<String, Map<String, List<TField>>> Param_Config = Config.Get_Config_Param(DataObj);
        Map<String, String[]> ConfigAdded = Config.get_config();
        CustomerRec.getLocalRefField("L.STUDENT.ID").setValue(Param_Config.get("CUSTOMER").get("TARGET.EPF").get(0).getValue());
        CustomerRec.getLocalRefField("L.STU.TUT.LIM").setValue(Param_Config.get("CUSTOMER").get("TARGET.EPF").get(1).getValue());
        CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Param_Config.get("TEST").get("A").get(0).getValue());
        CustomerRec.getLocalRefField("L.STUDENT.FILE").setValue(Param_Config.get("TEST").get("A").get(1).getValue().toString());
        CustomerRec.getLocalRefField("L.STU.ACC.NO").setValue(Param_Config.get("TEST").get("B").get(0).getValue());
        CustomerRec.getLocalRefField("L.STU.EDU.INST").setValue(Param_Config.get("CUSTOMER").get("LEGAL.DOC.NAME").get(0).getValue().toString());
        
        // CustomerRec.setBuildingNumber(Param_Config.get("TEST").get("A").get(0).getValue());
        // 
        // CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Param_Config.get("TEST").get("A").get(0).getValue());
        // CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Param_Config.get("TEST").get("B").get(1).getValue());
        // 
        // CustomerRec.getLocalRefField("L.STUDENT.ID").setValue(Param_Config.get("CUSTOMER").get("LEGAL.DOC.NAME").get(0).getValue());
        // .get("TEST").get("A").get(0).getValue().toString());
        // 
        /*
         * CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Param_Config
         * .get("TEST").get("A").get(1).getValue());
         * CustomerRec.getLocalRefField("L.STU.ACC.NO").setValue(Param_Config.
         * get("TEST").get("B").get(0).getValue());
         * CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(Param_Config
         * .get("TEST").get("B").get(1).getValue());
         */
        /*
         * TStructure record = DataObj.getRecord("EB.COMMON.PARAM.NSB",
         * "CUSTOMER"); EbCommonParamNsbRecord ecp = new
         * EbCommonParamNsbRecord(record); String ParamName = "TARGET.EPF"; int
         * ParamLoop = 0; while (ParamLoop <= ecp.getParamName().size()) {
         * ParamNameClass pnc = ecp.getParamName(0); if
         * (pnc.getParamName().getValue().indexOf(ParamName) >= 0) { String Ssss
         * = pnc.getParamValue().get(0).getValue().toString();
         * 
         * break; } ParamLoop++; }
         */

        /*
         * DEFAULTING LEGAL HOLDER NAME WITH SHORT NAME:
         *********************************************/
        TField ShortName = CustomerRec.getShortName(0);
        for (int i = 0; i < CustomerRec.getLegalId().size(); i++) {
            if (CustomerRec.getLegalId().get(i).getLegalHolderName().getValue().isEmpty()) {
                CustomerRec.getLegalId().get(i).setLegalHolderName(ShortName);
            }
        }

        currentRecord.set(CustomerRec.toStructure());
        /*
         * super.defaultFieldValues(application, currentRecordId, currentRecord,
         * unauthorisedRecord, liveRecord, transactionContext);
         */
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);

        /*
         * DEFAULT LEGAL DOC NAME WITH BIRTH CERTIFICATE FOR CUSTOMER AGE UNDER
         * 16:
         **************************************************************************/
        /*
         * for (int NicCount = 0; NicCount < CustomerRec.getLegalId().size();
         * NicCount++) { String Nationality =
         * CustomerRec.getNationality().getValue(); String LegalIDValue =
         * CustomerRec.getLegalId().get(NicCount).getLegalId().getValue();
         * String LegalDocNameValue =
         * CustomerRec.getLegalId().get(NicCount).getLegalDocName().getValue();
         * 
         * if ((Nationality.toString().equals("LK")) && (LegalIDValue.isEmpty())
         * && (LegalDocNameValue.equals("NIC"))) {
         * CustomerRec.getLegalId().get(NicCount).getLegalId().setError(
         * "EB-CUST.NIC.LK.NSB"); } else if
         * ((!Nationality.toString().equals("LK")) && (LegalIDValue.isEmpty())
         * && (LegalDocNameValue.equals("NATIONAL.ID"))) {
         * CustomerRec.getLegalId().get(NicCount).getLegalId().setError(
         * "EB-CUST.PASSPORT.NLK.NSB"); } else if ((!LegalIDValue.isEmpty()) &&
         * (LegalDocNameValue.equals("NIC"))) { /* VALIDATE LEGAL ID IF NIC:
         **************************/
        /*
         * CheckNicGenderDobNsb CheckNicGenderDob = new CheckNicGenderDobNsb();
         * CheckNicGenderDob.GetNicGenderDobNsb(LegalIDValue);
         * 
         * String Gender = CheckNicGenderDob.get_Gender(); String LegalYearDob =
         * CheckNicGenderDob.get_LegalYearDob(); int NoOfDays =
         * CheckNicGenderDob.get_NoOfDays();
         * 
         * // CheckAgeFromNicNsb CheckAgeNic = new CheckAgeFromNicNsb(); String
         * AgeNic = CheckNicGenderDob.GetDateFromNicNsb(LegalYearDob, NoOfDays);
         * 
         * if (!CustomerRec.getGender().getValue().toString().equals(Gender)) {
         * CustomerRec.getGender().setError("EB-CUST.GENDER.NSB"); } if
         * (!CustomerRec.getDateOfBirth().getValue().toString().equals(AgeNic))
         * { CustomerRec.getDateOfBirth().setError("EB-CUST.DOB.NSB"); } } }
         */
        /*
         * SET ERROR TO MANDATE LEGAL EXPIRY DATE:
         ****************************************/
        String Target = CustomerRec.getTarget().toString();
        for (int i = 0; i < CustomerRec.getLegalId().size(); i++) {
            String LegalDocName = CustomerRec.getLegalId().get(i).getLegalDocName().getValue().toString();
            TField LegalExpDate = CustomerRec.getLegalId().get(i).getLegalExpDate();
            if ((LegalDocName.equals("BUSINESS.LICENSE")) && (LegalExpDate.getValue().isEmpty())) {
                CustomerRec.getLegalId().get(i).getLegalExpDate().setError("EB-CUST.EXP.DT.NSB");
            }
        }

        /*
         * SET ERROR TO MANDATE EPF NUMBER AND ALSO CHECK FORMAT TO BE 4 OR 5
         * DIGITS:
         ***************************************************************************/

        if ((CustomerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty()) && (Target.equals("1004"))) {
            CustomerRec.getLocalRefField("L.EPF.NUMBER").setError("EB-CUST.EPF.NO.NSB");
        }

        if (!CustomerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty()) {
            String EpfNumber = CustomerRec.getLocalRefField("L.EPF.NUMBER").getValue().toString();
            int EpfNumberDigits = Integer.parseInt(EpfNumber);
            if ((EpfNumberDigits / 1000 <= 1 || EpfNumberDigits / 1000 >= 99)) {
                CustomerRec.getLocalRefField("L.EPF.NUMBER").setError("EB-CUST.EPF.DIGITS.NSB");
            }
        }

        // SET ERROR TO MANDATE TAX ID:
        // ****************************
        if ((CustomerRec.getLocalRefField("L.TAX.ID").getValue().isEmpty())
                && (CustomerRec.getLocalRefField("L.TAX.PAYER").getValue().equals("YES"))) {
            CustomerRec.getLocalRefField("L.TAX.ID").setError("EB-CUST.TAXID.NSB");
        }

        // SET ERROR TO MANDATE AML RESULT:
        // ********************************
        if ((CustomerRec.getAmlCheck().getValue().equals("YES")) && (CustomerRec.getAmlResult().getValue().isEmpty())) {
            CustomerRec.getAmlResult().setError("EB-CUST.AML.RESULT.NSB");
        }

        // SET ERROR TO MANDATE DEATH NOTIFY DATE:
        // ***************************************
        if ((CustomerRec.getLocalRefField("L.DEATH.NOTIFY").getValue().isEmpty())
                && (!CustomerRec.getLocalRefField("L.DEATH.DATE").getValue().toString().isEmpty())) {
            CustomerRec.getLocalRefField("L.DEATH.NOTIFY").setError("EB-CUST.DEATH.NOTIFY.NSB");
        }

        // SET ERROR TO DEFAULT & MANDATE FIELDS UNDER FATCA TAB:
        // ******************************************************
        // Default FATCA FULL NAME:
        // ************************
        if (CustomerRec.getLocalRefField("L.US.PERSON").getValue().equals("YES")) {
            List<LocalRefGroup> lFtcFullNamegrp = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FTC.FULLNAME");
            LocalRefGroup FtcFnGrp = CustomerRec.createLocalRefGroup("L.FTC.FULLNAME");
            FtcFnGrp.getLocalRefField("L.FTC.FULLNAME").setValue(CustomerRec.getShortName(0).toString());
            int lFtcFullNameLength = lFtcFullNamegrp.size();
            while (lFtcFullNameLength > 0) {
                lFtcFullNamegrp.remove(lFtcFullNameLength - 1);
                lFtcFullNameLength = lFtcFullNameLength - 1;
            }
            lFtcFullNamegrp.add(FtcFnGrp);

            // DEFAULT FATCA ADDRESS:
            // **********************

            String Address = "";
            for (int xAddr = 0; xAddr < CustomerRec.getAddress().size(); xAddr++) {
                String AddressIndex = CustomerRec.getAddress(xAddr).get(0).getValue().toString();
                if (Address.isEmpty()) {
                    Address = AddressIndex;
                } else {
                    Address = Address + ", " + AddressIndex;
                }
                String Street = CustomerRec.getStreet(0).toString();
                String TownCountry = CustomerRec.getTownCountry(0).getValue().toString();
                String lFtcAddressFormat = Street + ", " + Address + ", " + TownCountry;

                List<LocalRefGroup> lFtcAddr = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FTC.ADDRESS");
                LocalRefGroup lFtcAddrGrp = CustomerRec.createLocalRefGroup("L.FTC.ADDRESS");
                lFtcAddrGrp.getLocalRefField("L.FTC.ADDRESS").setValue(lFtcAddressFormat);
                int lFtcAddrLength = lFtcAddr.size();
                while (lFtcAddrLength > 0) {
                    lFtcAddr.remove(lFtcAddrLength - 1);
                    lFtcAddrLength = lFtcAddrLength - 1;
                }
                lFtcAddr.add(lFtcAddrGrp);
            }

            // DEFAULT TELEPHONE NUMBER:
            // *************************
            if (CustomerRec.getPhone1().get(0).getPhone1().getValue().toString().isEmpty()) {
                CustomerRec.getPhone1().get(0).getPhone1().setError("EB-CUST.PHONE1.NSB");
            } else {
                CustomerRec.getLocalRefField("L.FTC.TEL.NO")
                        .setValue(CustomerRec.getPhone1().get(0).getPhone1().getValue().toString());
            }

            // DEFAULT MOBILE NUMBER:
            // **********************
            if (CustomerRec.getPhone1().get(0).getSms1().getValue().toString().isEmpty()) {
                CustomerRec.getPhone1().get(0).getSms1().setError("EB-CUST.SMS1.NSB");
            } else {
                CustomerRec.getLocalRefField("L.FTC.MOB.NO")
                        .setValue(CustomerRec.getPhone1().get(0).getSms1().getValue().toString());
            }

            // DEFAULT EMAIL ID:
            // *****************
            if (CustomerRec.getPhone1().get(0).getEmail1().getValue().toString().isEmpty()) {
                CustomerRec.getPhone1().get(0).getEmail1().setError("EB-CUST.EMAIL1.NSB");
                ;
            } else {
                CustomerRec.getLocalRefField("L.FTC.EMAIL")
                        .setValue(CustomerRec.getPhone1().get(0).getEmail1().getValue().toString());
            }

            // MANDATE FATCA ACCOUNT NUMBER IN NSB:
            // ************************************

            if (CustomerRec.getLocalRefField("L.FTC.ACC.NO").getValue().toString().isEmpty()) {
                CustomerRec.getLocalRefField("L.FTC.ACC.NO").setError("EB-CUST.FAT.ACNO.NSB");
            }

            // MANDATE FATCA BIRTH PLACE:
            // **************************

            if (CustomerRec.getLocalRefField("L.FTC.BIRTH.PL").getValue().toString().isEmpty()) {
                CustomerRec.getLocalRefField("L.FTC.BIRTH.PL").setError("EB-CUST.FAT.BIRTHPL.NSB");
            }

            // MANDATE FATCA PERMANENT ADDRESS IN US:
            // **************************************

            List<LocalRefGroup> lFtcUsPrAddr = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FTC.US.PRADDR");
            if (lFtcUsPrAddr.isEmpty()) {
                LocalRefGroup FtcUsPrGrp = CustomerRec.createLocalRefGroup("L.FTC.US.PRADDR");
                FtcUsPrGrp.getLocalRefField("L.FTC.US.PRADDR").setValue(null);
                FtcUsPrGrp.getLocalRefField("L.FTC.US.PRADDR").setError("EB-CUST.FAT.PRADDRESS.US.NSB");
                lFtcUsPrAddr.add(FtcUsPrGrp);
            }

            // MANDATE FATCA CORRESPONDENT ADDRESS IN US:
            // ******************************************

            List<LocalRefGroup> lFtcUsCrAddrGrp = CustomerRec.getLocalRefGroups("L.FTC.US.CRADDR");
            if (lFtcUsCrAddrGrp.isEmpty()) {
                LocalRefGroup FtcUsPrGrp1 = CustomerRec.createLocalRefGroup("L.FTC.US.CRADDR");
                FtcUsPrGrp1.getLocalRefField("L.FTC.US.CRADDR").setValue(null);
                FtcUsPrGrp1.getLocalRefField("L.FTC.US.CRADDR").setError("EB-CUST.FAT.CRADDRESS.US.NSB");
                lFtcUsCrAddrGrp.add(FtcUsPrGrp1);
            }

            // MANDATE FATCA US TELEPHONE NUMBER:
            // **********************************

            if (CustomerRec.getLocalRefField("L.FTC.US.TELNO").getValue().toString().isEmpty()) {
                CustomerRec.getLocalRefField("L.FTC.US.TELNO").setError("EB-CUST.FAT.TELNO.US.NSB");
            }
        }

        // MANDATE FATCA COUNTRY NAMES, PASSPORT & ID / SSN:
        // *************************************************
        if (CustomerRec.getLocalRefField("L.FTC.DUAL.CTZN").toString().equals("YES")) {
            List<LocalRefGroup> lFtcCountries = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FTC.COUNTRIES");
            if (lFtcCountries.isEmpty()) {
                LocalRefGroup lFtcCountriesGrp = CustomerRec.createLocalRefGroup("L.FTC.COUNTRIES");
                lFtcCountriesGrp.getLocalRefField("L.FTC.COUNTRIES").setValue(null);
                lFtcCountriesGrp.getLocalRefField("L.FTC.COUNTRIES").setError("EB-CUST.FAT.CNTRYS.NSB");
                lFtcCountries.add(lFtcCountriesGrp);
            }

            List<LocalRefGroup> lFtcPassport = (List<LocalRefGroup>) CustomerRec.getLocalRefGroups("L.FTC.PASSPORTS");
            if (lFtcPassport.isEmpty()) {
                LocalRefGroup lFtcPassportGrp = CustomerRec.createLocalRefGroup("L.FTC.PASSPORTS");
                lFtcPassportGrp.getLocalRefField("L.FTC.PASSPORTS").setValue(null);
                lFtcPassportGrp.getLocalRefField("L.FTC.PASSPORTS").setError("EB-CUST.FAT.PASSPORT.NSB");
                lFtcPassport.add(lFtcPassportGrp);
            }

            if (CustomerRec.getLocalRefField("L.FTC.ID.SSN").getValue().toString().isEmpty()) {
                CustomerRec.getLocalRefField("L.FTC.ID.SSN").setError("EB-CUST.FAT.ID.TAXSSN.NSB");
            }
        }

        for (int xEmpStat = 0; xEmpStat < CustomerRec.getEmploymentStatus().size(); xEmpStat++) {
            if (CustomerRec.getEmploymentStatus().get(xEmpStat).getEmploymentStatus().getValue().toString()
                    .equals("STUDENT")) {

                if (CustomerRec.getLocalRefField("L.TRAN.PURPOSE").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.TRAN.PURPOSE").setError("EB-CUST.TRANPURPOSE.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STUDENT.FILE").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STUDENT.FILE").setError("EB-CUST.STUDENTFILE.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.ACC.NO").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.ACC.NO").setError("EB-CUST.STUACCNO.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.ACC.CCY").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.ACC.CCY").setError("EB-CUST.STUACCCCY.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.PAYE.NAME").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.PAYE.NAME").setError("EB-CUST.STUPAYENAME.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.ADDRESS").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.ADDRESS").setError("EB-CUST.STUADDRESS.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.EDU.INST").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.EDU.INST").setError("EB-CUST.STUEDUINST.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STUDENT.ID").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STUDENT.ID").setError("EB-CUST.STUDENTID.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.ENROL.DT").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.ENROL.DT").setError("EB-CUST.STUENROLDT.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.CRS.PERID").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.CRS.PERID").setError("EB-CUST.STUCRSPERID.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.TUT.LIM").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.TUT.LIM").setError("EB-CUST.STUTUTLIM.NSB");
                }

                if (CustomerRec.getLocalRefField("L.STU.LIV.EXP").getValue().toString().isEmpty()) {
                    CustomerRec.getLocalRefField("L.STU.LIV.EXP").setError("EB-CUST.STULIVEXP.NSB");
                }
            }
        }

        // SETTING ALL THE ABOVE CHANGES UNDER validateRecord METHOD TO
        // currentRecord:
        // ***************************************************************************

        currentRecord.set(CustomerRec.toStructure());
        return CustomerRec.getValidationResponse();

        /*
         * return super.validateRecord(application, currentRecordId,
         * currentRecord, unauthorisedRecord, liveRecord, transactionContext);
         */

    }
}
