package NsbSrilankaPack;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerQuickCifNSB extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        super.defaultFieldValues(application, currentRecordId, currentRecord, unauthorisedRecord, liveRecord,
                transactionContext);
    }

}
