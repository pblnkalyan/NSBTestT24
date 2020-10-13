package NsbGeneralRoutinesPack;

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
public class AgeCal {

    /**
     * @param args
     */
    public static void main(String[] args) {
        //2b
        String DateOfBirth = "19880328";
      //2a
        //DateOfBirth = "19880328";
        //1b
        //DateOfBirth = "20190923";
        
        //1a
        //DateOfBirth = "20190926";
        
        Date today = new Date();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        java.util.Date DateOfBirthFormat;
        int Age = 0;
        int Month = 0;
        int Days= 0;
        int NumberofdaysInPrevMonthCurDate = 0;
          
        try {
            DateOfBirthFormat = format2.parse(DateOfBirth);
            LocalDate localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(localDate.getMonthValue()==2){NumberofdaysInPrevMonthCurDate = 31;}
            if(localDate.getMonthValue()==3){NumberofdaysInPrevMonthCurDate = 29;}
            if(localDate.getMonthValue()==9){NumberofdaysInPrevMonthCurDate = 31;}
            
            LocalDate DOB = DateOfBirthFormat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int DayOfYear = localDate.getDayOfYear();
            int DOBDayOfYear = DOB.getDayOfYear();
           
            if ((localDate.getYear() % 4 == 0) & (localDate.getMonthValue() > 2) ){
                DayOfYear = DayOfYear - 1;
            }
            if ((DOB.getYear() % 4 == 0) & (DOB.getMonthValue() > 2) ){
                DOBDayOfYear = DOBDayOfYear - 1;
            }
            
            if(DOBDayOfYear > DayOfYear)
            {
                System.out.println("Case 1");
                Age = localDate.getYear() -  DOB.getYear() - 1;
                if(localDate.getDayOfMonth() <= DOB.getDayOfMonth() )
                {
                    System.out.println("Case 1.a");
                Month = 12 - DOB.getMonthValue() + localDate.getMonthValue() - 1;
                Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() + NumberofdaysInPrevMonthCurDate;
                }
                else{
                    System.out.println("Case 1.b");
                 Month = 12 - DOB.getMonthValue() + localDate.getMonthValue();
                 Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() ;
                }
                
                
            }
            else
            {
                System.out.println("Case 2");
                Age = localDate.getYear() -  DOB.getYear();
                if(localDate.getDayOfMonth() <= DOB.getDayOfMonth() )
                {
                System.out.println("Case 2.a  today" +localDate.getDayOfMonth() +  "   DOB" + DOB.getDayOfMonth());
                Month = localDate.getMonthValue() - DOB.getMonthValue() -1 ;
                Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() + NumberofdaysInPrevMonthCurDate;
                }
                else{
                   System.out.println("Case 2.b");
                Month = localDate.getMonthValue() - DOB.getMonthValue()  ;
                Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() ;
                }
            }
        } catch (ParseException e) {
        }
       System.out.println(Age+" (years) "+ Month +" (month) "+ Days + " (days)");
    }

}
