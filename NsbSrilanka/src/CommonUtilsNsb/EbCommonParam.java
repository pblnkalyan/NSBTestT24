package CommonUtilsNsb;

//import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;

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
public class EbCommonParam {
    Map<String, String[]> Config; 
    
    public EbCommonParam(){
       this.Config = new HashMap<String, String[]>();   
    }
    
    public void Add_Config(String Record_Name, String[] Parameter_Name){
        //Parameter_Name is Uni
        this.Config.put(Record_Name, Parameter_Name);
    }
    
    public Map<String, String[]> get_config(){
        //Parameter_Name is Uni
        return this.Config;
    }
    
    public   Map<String, Map<String, List<TField>>> Get_Config_Param(DataAccess da){
        
        //Clean this.config
        
        Map<String, Map<String, List<TField>>> Output = new  HashMap<String, Map<String, List<TField>>>();
        Map<String, List<TField>> Param_Output = new  HashMap<String, List<TField>>();
        String Record_Id = "";
        String[] Parameters ;
        Map.Entry config;
        Iterator Config_Iterator = this.Config.entrySet().iterator();
        while(Config_Iterator.hasNext()){
            config = (Map.Entry) Config_Iterator.next();
           
            Record_Id = (String) config.getKey();
            Parameters = (String[]) config.getValue();
            TStructure record = da.getRecord("EB.COMMON.PARAM.NSB", Record_Id);
            EbCommonParamNsbRecord ecp = new EbCommonParamNsbRecord(record);
            Param_Output = new  HashMap<String, List<TField>>();
            for (ParamNameClass p : ecp.getParamName()) {
                for (String pn : Parameters) {
                    if (p.getParamName().toString().trim().equals(pn.trim())) {
                        Param_Output.put(pn.trim(), p.getParamValue());
                  }
                }
            }
            Output.put(Record_Id, Param_Output);
        }
        return Output;
    }
   
}
