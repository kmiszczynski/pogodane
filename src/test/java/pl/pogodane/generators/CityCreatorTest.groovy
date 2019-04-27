package pl.pogodane.generators

import pl.pogodane.generators.model.RawLocation
import pl.pogodane.generators.model.google.Bounds
import pl.pogodane.generators.model.google.Point
import pl.pogodane.mongo.Station
import spock.lang.Specification
import spock.lang.Unroll

class CityCreatorTest extends Specification {
   CityCreator cityStationCreator = new CityCreator()

   @Unroll
   def "station #scenario"() {
      given:
      def city = new RawLocation(
              name: 'krakow',
              voivodeship: 'malopolskie'
      )
      def bounds = new Bounds(
              northeast: new Point(lat: 50.023654, lng: 18.6867324),
              southwest: new Point(lat: 49.8965761, lng: 18.5138196)
      )
      def cityLocation = new Point(lat: 49.9454207, lng: 18.6101103)
      def station = new Station(
              lat: stationLat,
              lng: stationLng
      )

      when:
      def result = cityStationCreator.createCityStationsForCity(city, bounds, cityLocation, [station])

      then:
      result.getName() == 'krakow'
      result.getVoivodeship() == 'malopolskie'
      result.getCityStations().get(0).isInCityBounds() == inCityBounds

      where:
      scenario                                                 | stationLat | stationLng | inCityBounds
      'should be in city bounds'                               | 50.023654  | 18.6867324 | true
      'should be in city bounds when exactly on bound'         | 50         | 18.52      | true
      'should not be in city bounds when stationLat is lower'  | 48         | 18.52      | false
      'should not be in city bounds when stationLat is higher' | 51         | 18.52      | false
      'should not be in city bounds when stationLng is lower'  | 50         | 17.52      | false
      'should not be in city bounds when stationLng is higher' | 50         | 19.52      | false
   }

   def 'station in city bounds should have higher rank'() {
      given:
      def city = new RawLocation(
              name: 'krakow',
              voivodeship: 'malopolskie'
      )
      def bounds = new Bounds(
              northeast: new Point(lat: 49.9, lng: 18.6),
              southwest: new Point(lat: 49.8, lng: 18.5)
      )
      def cityLocation = new Point(lat: 49.8, lng: 18.5)
      def stationInCityBounds = new Station(
              stationName: 'a',
              lat: 49.9,
              lng: 18.6
      )
      def stationOutOfCityBounds = new Station(
              stationName: 'b',
              lat: 49.7,
              lng: 18.4
      )
      when:
      def result = cityStationCreator.createCityStationsForCity(city, bounds, cityLocation, [stationInCityBounds, stationOutOfCityBounds])

      then:
      def result1 = result.getCityStations().get(0)
      def result2 = result.getCityStations().get(1)
      result1.getStationName() == 'a'
      result2.getStationName() == 'b'
      result1.getRank() > result2.getRank()
   }

   def 'should only return 3 stations for city'() {
      given:
      def city = new RawLocation(
              name: 'krakow',
              voivodeship: 'malopolskie'
      )
      def bounds = new Bounds(
              northeast: new Point(lat: 49.9, lng: 18.6),
              southwest: new Point(lat: 49.8, lng: 18.5)
      )
      def cityLocation = new Point(lat: 49.8, lng: 18.5)
      def station1 = new Station(
              stationName: 'a',
              lat: 49.8,
              lng: 18.5
      )
      def station2 = new Station(
              stationName: 'b',
              lat: 49.7,
              lng: 18.4
      )
      def station3 = new Station(
              stationName: 'c',
              lat: 49.6,
              lng: 18.3
      )
      def station4 = new Station(
              stationName: 'd',
              lat: 49.5,
              lng: 18.2
      )
      when:
      def result = cityStationCreator.createCityStationsForCity(city, bounds, cityLocation, [station1, station2, station3, station4])

      then:
      result.getCityStations().size() == 3
      result.getCityStations().get(0).getStationName() == 'a'
      result.getCityStations().get(1).getStationName() == 'b'
      result.getCityStations().get(2).getStationName() == 'c'
   }
}
