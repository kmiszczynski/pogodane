package pl.pogodane.generators.model.google;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GeocodingResults {
   private String status;
   private List<GeocodingResult> results;
}
