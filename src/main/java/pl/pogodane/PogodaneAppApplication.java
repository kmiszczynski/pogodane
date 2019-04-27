package pl.pogodane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pogodane.generators.BasicDataGenerator;

@SpringBootApplication
public class PogodaneAppApplication implements CommandLineRunner {

   @Autowired
   private BasicDataGenerator basicDataGenerator;

   private static final String LOAD_BASIC_DATA_PARAM = "--loadBasicData";

   public static void main(String[] args) {
      SpringApplication.run(PogodaneAppApplication.class, args);
   }

   @Override public void run(String... args) throws Exception {
      if (args.length > 0 && args[0].equals(LOAD_BASIC_DATA_PARAM)) {
         basicDataGenerator.generateBasicData();
      }
   }
}
