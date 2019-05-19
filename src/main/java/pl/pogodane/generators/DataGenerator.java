package pl.pogodane.generators;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface DataGenerator {
   void generateData() throws IOException, SAXException, ExecutionException, InterruptedException;
}
