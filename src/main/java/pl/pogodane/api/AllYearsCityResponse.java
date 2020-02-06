package pl.pogodane.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pogodane.mongo.YearlyCityData;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllYearsCityResponse {
   private List<YearlyCityData> yearlyCityData;
}
