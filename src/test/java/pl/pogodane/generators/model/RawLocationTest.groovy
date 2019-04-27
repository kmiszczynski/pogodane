package pl.pogodane.generators.model

import spock.lang.Specification

class RawLocationTest extends Specification {
   def 'should escape all chars in the name'() {
      given:
      RawLocation rawLocation = new RawLocation(
              name: 'ęóąśłżźćńĘÓĄŚŁŻŹĆŃ'
      )

      when:
      def result = rawLocation.getEscapedName()

      then:
      result == 'eoaslzzcnEOASLZZCN'
   }

   def 'should escape all chars in the voivodeship'() {
      given:
      RawLocation rawLocation = new RawLocation(
              voivodeship: 'ęóąśłżźćńĘÓĄŚŁŻŹĆŃ'
      )

      when:
      def result = rawLocation.getEscapedVoivodeship()

      then:
      result == 'eoaslzzcnEOASLZZCN'
   }
}
