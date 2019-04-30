package pl.pogodane.generators;

import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.RawLocation;
import pl.pogodane.generators.model.google.Bounds;
import pl.pogodane.generators.model.google.Point;
import pl.pogodane.mongo.City;

@Component
public class CityCreator {

   public City createCity(RawLocation city, Bounds cityBounds, Point cityLocation) {

      return City.builder()
         .name(city.getName())
         .lat(cityLocation.getLat())
         .lng(cityLocation.getLng())
         .northEastLat(cityBounds == null ? null : cityBounds.getNortheast().getLat())
         .northEastLng(cityBounds == null ? null : cityBounds.getNortheast().getLng())
         .southWestLat(cityBounds == null ? null : cityBounds.getSouthwest().getLat())
         .southWestLng(cityBounds == null ? null : cityBounds.getSouthwest().getLng())
         .voivodeship(city.getVoivodeship())
         .technicalId(city.getEscapedName() + "-" + city.getEscapedVoivodeship())
         .build();
   }
}
