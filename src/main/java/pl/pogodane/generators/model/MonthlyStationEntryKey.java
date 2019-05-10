package pl.pogodane.generators.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStationEntryKey {
   private String stationCode;
   private String year;
   private String month;

   @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MonthlyStationEntryKey that = (MonthlyStationEntryKey) o;
      return Objects.equals(stationCode, that.stationCode) &&
         Objects.equals(year, that.year) &&
         Objects.equals(month, that.month);
   }

   @Override public int hashCode() {
      return Objects.hash(stationCode, year, month);
   }
}
