package SampleAaRoutines;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class AaActivityRoutine extends ActivityLifecycle {

    @Override
    public void defaultFieldValues(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {
        // TODO Auto-generated method stub

        AaArrAccountRecord aAccount = new AaArrAccountRecord();
        String ArrStatus = arrangementContext.getActivityStatus();
        aAccount.setShortTitle(ArrStatus, 0);
        aAccount.setShortTitle("abcd", 0);
        record.set(aAccount.toStructure());

        super.defaultFieldValues(accountDetailRecord, arrangementActivityRecord, arrangementContext, arrangementRecord,
                masterActivityRecord, productPropertyRecord, productRecord, record);
    }

    @Override
    public TValidationResponse validateRecord(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {
        // TODO Auto-generated method stub

        Contract contract = new Contract(this);
        String arrId = arrangementContext.getArrangementId();
        contract.setContractId(arrId);

        return super.validateRecord(accountDetailRecord, arrangementActivityRecord, arrangementContext,
                arrangementRecord, masterActivityRecord, productPropertyRecord, productRecord, record);
    }

}
