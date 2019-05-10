package pl.pogodane.generators.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyStationEntryKey {
   private String stationID;
   private LocalDate date;

   @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DailyStationEntryKey that = (DailyStationEntryKey) o;
      return stationID.equals(that.stationID) &&
         date.equals(that.date);
   }

   @Override public int hashCode() {
      return Objects.hash(stationID, date);
   }
}
