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
public class CheckNicGenderDobNsb {

    public String Gender = "";
    public String LegalYearDob = "";
    public int NoOfDays = 0;

    public CheckNicGenderDobNsb() {

    }

    public void set_Gender(String g) {
        this.Gender = g;
    }

    public void set_LegalYearDob(String LegalYearDob) {
        this.LegalYearDob = LegalYearDob;
    }

    public void set_NoOfDays(int NoOfDays) {
        this.NoOfDays = NoOfDays;
    }

    public String get_Gender() {
        return this.Gender;
    }

    public String get_LegalYearDob() {
        return this.LegalYearDob;
    }

    public int get_NoOfDays() {
        return this.NoOfDays;
    }
    public void GetNicGenderDobNsb(String LegalIDMand) {

        int LegalIdLength = LegalIDMand.toString().length();
        if (LegalIdLength == 12) {
            LegalYearDob = LegalIDMand.substring(0, 4);
            int SecondSubString = Integer.parseInt(LegalIDMand.substring(4, 7));
            if (SecondSubString > 500) {
                Gender = "FEMALE";
                NoOfDays = SecondSubString - 500;
            } else {
                Gender = "MALE";
                NoOfDays = SecondSubString;
            }
        } else if (LegalIdLength == 10) {
            LegalYearDob = "19" + LegalIDMand.substring(0, 2);
            int SecondSubString = Integer.parseInt(LegalIDMand.substring(2, 5));
            if (SecondSubString > 500) {
                Gender = "FEMALE";
                NoOfDays = SecondSubString - 500;
            } else {
                Gender = "MALE";
                NoOfDays = SecondSubString;
            }
        }

        set_Gender(Gender);
        set_LegalYearDob(LegalYearDob);
        set_NoOfDays(NoOfDays);

    }
    
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