package NsbSrilankaPack;

import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerEnquiryNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        /*int FieldNameIndex = 0;
        for (int i = 0; i < filterCriteria.size(); i++){
            if (filterCriteria.get(i).getFieldname().equals("TARGET.CODE")){
                FieldNameIndex = i;
                break;
            }
        }
        */
        //String SectorValue = filterCriteria.get(FieldNameIndex).getValue().toString();
/*        //int TargetCodeIndex = filterCriteria.indexOf("TARGET.CODE");
        //String SectorValue = filterCriteria.get(0).getValue().toString();
        filterCriteria.get(FieldNameIndex).setFieldname("TARGET.CODE");
        filterCriteria.get(FieldNameIndex).setOperand("LK");
        filterCriteria.get(FieldNameIndex).setValue(SectorValue.charAt(0)+ "...");
*/
        String SectorValue = "1904";
        FilterCriteria TargetFilter = new FilterCriteria();
        TargetFilter.setFieldname("DESCRIPTION");
        TargetFilter.setOperand("EQ");
        TargetFilter.setValue(SectorValue); //.charAt(0) + "...");
        filterCriteria.add(TargetFilter);

        return filterCriteria;

        //return super.setFilterCriteria(filterCriteria, enquiryContext);
    }
}
