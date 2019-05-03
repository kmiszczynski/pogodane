package pl.pogodane.generators.model;

import java.time.LocalDate;

public class DateUtils {
   public static LocalDate createLocalDate(String year, String month, String day) {
      return LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
   }
}
