package CommonUtilsNsb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class ConvertTot24DateFormat {

    public ConvertTot24DateFormat(){
        
    }
    Date formatDatetoT24(Date d) throws ParseException
    {
        LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        String DateFormatted  = "";
        if(localDate.getMonthValue() < 10)
        {DateFormatted = Integer.toString(localDate.getYear()) +"0"+ Integer.toString(localDate.getMonthValue()) + Integer.toString(localDate.getDayOfMonth());        }
        else{DateFormatted = Integer.toString(localDate.getYear()) + Integer.toString(localDate.getMonthValue()) + Integer.toString(localDate.getDayOfMonth()); }
        
        SimpleDateFormat format3 = new SimpleDateFormat("yyyymmdd");
        Date   date3 = format3.parse ( DateFormatted);  
        return date3;
    }
}
