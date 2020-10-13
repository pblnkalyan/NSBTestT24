package CommonUtilsNsb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CheckAgeFromNicNsb {
    
    public CheckAgeFromNicNsb(){}
    
    public String GetDateFromNicNsb(String LegalYearDob, int NoOfDays){
        String LegalDob = LegalYearDob + "-01-01";
        SimpleDateFormat LegalDobFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar CalenderInstance = Calendar.getInstance();
        try {
            CalenderInstance.setTime(LegalDobFormat.parse(LegalDob));
        } catch (ParseException excep) {
            excep.printStackTrace();
        }
        // Number of Days to add
        CalenderInstance.add(Calendar.DAY_OF_MONTH, NoOfDays);
        String LegalDobDate = LegalDobFormat.format(CalenderInstance.getTime());
        LegalDobDate =  LegalDobDate.toString().replace("-", "");
        return LegalDobDate;
    }
    
}
