package NsbSrilankaPack;

import java.util.List;

import com.temenos.api.LocalRefGroup;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.RelDelivOptClass;
import com.temenos.t24.api.records.customer.RelationCodeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebdistrictnsb.EbDistrictNsbRecord;


/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerNonIndividualNsb extends RecordLifecycle {

    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

/*INITIALISE:
************/
        CustomerRecord Customer = new CustomerRecord(currentRecord);
        //List<LocalRefGroup> lFullNamegrp =(List<LocalRefGroup>)Customer.getLocalRefGroups("L.FULL.NAME");

/*DEFAULT SHORT NAME WITH LOCAL FIELD FULL NAME:
***********************************************/
        LocalRefGroup grp = Customer.createLocalRefGroup("L.FULL.NAME");
        Customer.setShortName(grp.getLocalRefField("L.FULL.NAME").getValue().toString(), 0);
        
/*DEFAULT OCCUPATION FROM JOB.TITLE:
**********************************/
        for (int i = 0; i < Customer.getEmploymentStatus().size(); i++) {
            TField JobTitle = Customer.getEmploymentStatus().get(i).getJobTitle();
            Customer.getEmploymentStatus().get(i).setOccupation(JobTitle);
        }
        
        currentRecord.set(Customer.toStructure());
/*        super.defaultFieldValuesOnHotField(application, currentRecordId, currentRecord, currentInputValue, unauthorisedRecord,
                liveRecord, transact ionContext);*/
    }
    
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord Customer = new CustomerRecord(currentRecord);
/*DEFAULT PROVINCE FROM DISTRICT:
********************************/
        DataAccess DataObj = new DataAccess(this);
        TStructure DistrictNsb = DataObj.getRecord("EB.DISTRICT.NSB", Customer.getLocalRefField("L.DISTRICT").toString());
        EbDistrictNsbRecord ProvinceNsb = new EbDistrictNsbRecord(DistrictNsb);
        Customer.getLocalRefField("L.PROVINCE").setValue(ProvinceNsb.getProvince().toString());
        
/*DEFAULTING LEGAL HOLDER NAME WITH SHORT NAME:
*********************************************/
       TField ShortName = Customer.getShortName(0);
       for (int i = 0; i < Customer.getLegalId().size(); i++){
           if (Customer.getLegalId().get(i).getLegalHolderName().getValue().isEmpty()){
               Customer.getLegalId().get(i).setLegalHolderName(ShortName);
               }
       }
       
       currentRecord.set(Customer.toStructure());
/*        super.defaultFieldValues(application, currentRecordId, currentRecord, unauthorisedRecord, liveRecord,
                transactionContext);*/
    }
    
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
       
        CustomerRecord Customer = new CustomerRecord(currentRecord);
               
//SET ERROR TO MANDATE TAX ID:
//****************************
        if ((Customer.getLocalRefField("L.TAX.ID").getValue().isEmpty()) && (Customer.getLocalRefField("L.TAX.PAYER").getValue().equals("YES"))){
            Customer.getLocalRefField("L.TAX.ID").setError("EB-CUST.TAXID.NSB");
            }

//SET ERROR TO MANDATE AML RESULT:
//********************************
        if ((Customer.getAmlCheck().getValue().equals("YES")) && (Customer.getAmlResult().getValue().isEmpty())){
            Customer.getAmlResult().setError("EB-CUST.AML.RESULT.NSB");
            } 

/*MANDATE ROLE.MORE.INFO WHEN REVERS.REL.CODE IS "Equity":
*********************************************************/
        List<RelationCodeClass> CustRelationCodeList = Customer.getRelationCode();
        for (int relCodeCount = 1; relCodeCount < CustRelationCodeList.size(); relCodeCount++){
            if (CustRelationCodeList.get(relCodeCount).getReversRelCode().getValue().toString().equals("EQUITY")){
                List<RelDelivOptClass> RelDelivList = CustRelationCodeList.get(relCodeCount).getRelDelivOpt();
                for (int RelDelivCount = 0; RelDelivCount < RelDelivList.size(); RelDelivCount++){
                    RelDelivList.get(RelDelivCount).getRoleMoreInfo().setError("EB-CUST.REVRELCODE.NSB");   
                }
            }
        }
        
        currentRecord.set(Customer.toStructure());
        return Customer.getValidationResponse();
    }
}
