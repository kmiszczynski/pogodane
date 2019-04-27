package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RawLocation {
   private String name;
   private String type;
   private String borough;
   private String county;
   private String voivodeship;

   public String getEscapedName() {
      return escape(name);
   }

   public String getEscapedVoivodeship() {
      return escape(voivodeship);
   }

   private String escape(String input) {
      return input.replace("ą", "a")
         .replace("ć", "c")
         .replace("ę", "e")
         .replace("ł", "l")
         .replace("ń", "n")
         .replace("ó", "o")
         .replace("ś", "s")
         .replace("ź", "z")
         .replace("ż", "z")
         .replace("Ą", "A")
         .replace("Ć", "C")
         .replace("Ę", "E")
         .replace("Ł", "L")
         .replace("Ń", "N")
         .replace("Ó", "O")
         .replace("Ś", "S")
         .replace("Ź", "Z")
         .replace("Ż", "Z");
   }
}
