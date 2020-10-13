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
/*
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CalculateAgeNsb {
    
    /*** VAriables begin */
    LocalDate DOB;
    LocalDate localDate;
    
    /*** VAriables End */
    
    /****** Constructors begin *****/
   
    public CalculateAgeNsb(String DateOfBirth){
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        try {
            java.util.Date DateOfBirthFormat = format2.parse(DateOfBirth);
            Date today = new Date();
            this.localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            this.DOB = DateOfBirthFormat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
          
        }
        
    }
 /******** Constructors End ****/
    
    /*** Functions begin */
    public  int getNoDaysInPrevMonth(int Today_Month , int Today_Year){
        if(Today_Month == 12){return(30);}
        if(Today_Month == 11){return(31);}
        if(Today_Month == 10){return(30);}
        if(Today_Month == 9){return(31);}
        if(Today_Month == 8){return(31);}
        if(Today_Month == 7){return(30);}
        if(Today_Month == 6){return(31);}
        if(Today_Month == 5){return(30);}
        if(Today_Month == 4){return(31);}
        if(Today_Month == 3){
            if((Today_Year % 4 == 0 && Today_Year % 100 != 0) || Today_Year % 400 == 0){
                return(29);}
            else{
                return(28);}}
        if(Today_Month == 2){return(31);}
        if(Today_Month == 1){return(31);}
        return 0;
    }
    
    public int get_age_integer(){
        int Age = 0;
        int Month = 0;
        int Days= 0;
        int NumberofdaysInPrevMonthCurDate = 0;
//        String retval ="";
        NumberofdaysInPrevMonthCurDate = this.getNoDaysInPrevMonth(this.localDate.getMonthValue(), this.localDate.getYear());
        
        
        int DayOfYear = this.localDate.getDayOfYear();
        int DOBDayOfYear = DOB.getDayOfYear();
       
        LocalDate DOB = this.DOB;
        LocalDate localDate = this.localDate;
        
        if ((localDate.getYear() % 4 == 0) & (localDate.getMonthValue() > 2) ){
            DayOfYear = DayOfYear - 1;
        }
        if ((DOB.getYear() % 4 == 0) & (DOB.getMonthValue() > 2) ){
            DOBDayOfYear = DOBDayOfYear - 1;
        }
        
        if(DOBDayOfYear > DayOfYear)
        {
            //System.out.println("Case 1");
            Age = localDate.getYear() -  DOB.getYear() - 1;
            if(localDate.getDayOfMonth() <= DOB.getDayOfMonth() )
            {
               //System.out.println("Case 1.a");
            Month = 12 - DOB.getMonthValue() + localDate.getMonthValue() - 1;
            Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() + NumberofdaysInPrevMonthCurDate;
            }
            else{
                //System.out.println("Case 1.b");
             Month = 12 - DOB.getMonthValue() + localDate.getMonthValue();
             Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() ;
            }
            
            
        }
        else
        {
            //System.out.println("Case 2");
            Age = localDate.getYear() -  DOB.getYear();
            if(localDate.getDayOfMonth() <= DOB.getDayOfMonth() )
            {
            //System.out.println("Case 2.a  today" +localDate.getDayOfMonth() +  "   DOB" + DOB.getDayOfMonth());
            Month = localDate.getMonthValue() - DOB.getMonthValue() -1 ;
            Days = localDate.getDayOfMonth() - DOB.getDayOfMonth() + NumberofdaysInPrevMonthCurDate;
            }
            else{
               //System.out.println("Case 2.b");
            Month = localDate.getMonthValue() - DOB.getMonthValue();            
            Days = localDate.getDayOfMonth() - DOB.getDayOfMonth();
            }
        }
        if (Month == 0 & Days == 0){
            Age += 1;
        }
        
        return (Age); //+ Month + "M " + Days + "D");
    }
    /*** Function End */
    }


