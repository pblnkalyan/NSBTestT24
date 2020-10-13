package CommonUtilsNsb;

//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcommonparamnsb.EbCommonParamNsbRecord;
import com.temenos.t24.api.tables.ebcommonparamnsb.ParamNameClass;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class uGetParamValue {
    Map<String, List<TField>> Output;

    // Map<String, List<TField>> Output_MultiRecord = new HashMap< String,
    // List<TField>>();

    public uGetParamValue() {
        this.Output = new HashMap<String, List<TField>>();
    }

    public List<TField> getSingleParam(String recordId, String ParamName, DataAccess da) {
        TStructure record = da.getRecord("EB.COMMON.PARAM.NSB", recordId);
        EbCommonParamNsbRecord ecp = new EbCommonParamNsbRecord(record);
        // int index = ecp.getParamName().indexOf(ParamName);
        for (ParamNameClass p : ecp.getParamName()) {
            if (p.getParamName().toString().trim().equals(ParamName)) {
                return p.getParamValue();
            }
        }

        return null;
    }

    public Map<String, List<TField>> getMultipleParamFromSingleRecord(String recordId, String ParamName, DataAccess da,
            String PS, int indextype) {
        // indextype == 0 if index is just param name
        // indextype ==1 if index is record_name,paramname
        this.Output.clear();
        TStructure record = da.getRecord("EB.COMMON.PARAM.NSB", recordId);
        EbCommonParamNsbRecord ecp = new EbCommonParamNsbRecord(record);
        String index_pn = "";
        String[] ParamName_List = ParamName.split(PS);
        // int index = ecp.getParamName().indexOf(ParamName);
        for (ParamNameClass p : ecp.getParamName()) {
            for (String pn : ParamName_List) {
                if (p.getParamName().toString().trim().equals(pn.trim())) {
                    if (indextype == 0) {
                        index_pn = pn;
                    } else {
                        index_pn = recordId + ';' + pn;
                    }
                    this.Output.put(index_pn, p.getParamValue());

                }
            }
        }

        return this.Output;
    }

    public void Clear_Output() {
        this.Output.clear();
    }

    public Map<String, List<TField>> getMultipleParamFromMultipleRecordRepeatVariebles(String recordIds,
            String ParamNames, DataAccess da, String PS, String RS) {
        // indextype == 0 if index is just param name
        // indextype ==1 if index is record_name,paramname
        this.Output.clear();
        String[] r = recordIds.split(RS);
        String[] parm = ParamNames.split(RS);
        String recordId = "";
        String ParamName = "";
        for (int i = 0; i < r.length; i++) {
            recordId = r[i];
            ParamName = parm[i];
            TStructure record = da.getRecord("EB.COMMON.PARAM.NSB", recordId);
            EbCommonParamNsbRecord ecp = new EbCommonParamNsbRecord(record);
            String index_pn = "";
            String[] ParamName_List = ParamName.split(PS);
            // int index = ecp.getParamName().indexOf(ParamName);
            for (ParamNameClass p : ecp.getParamName()) {
                for (String pn : ParamName_List) {
                    if (p.getParamName().toString().trim().equals(pn.trim())) {
                        index_pn = recordId + ';' + pn;
                        this.Output.put(index_pn, p.getParamValue());
                    }
                }
            }

        }
        return this.Output;

    }

}
