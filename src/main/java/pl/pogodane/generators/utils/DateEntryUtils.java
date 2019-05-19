package pl.pogodane.generators.utils;

public class DateEntryUtils {
   public static String getMonthAsString(int month) {
      if (month < 10) {
         return "0" + month;
      } else {
         return String.valueOf(month);
      }
   }
}
