package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public abstract class AbstractStationDataGenerator {

   protected static final String NO_MEASURE_INDICATOR = "8";
   private static final String TEMP_DIR = "/input/temp";

   public void generateData() {
      FileVisitor<Path> fileVisitor = new ZippedFileProcessor();
      try {
         Files.walkFileTree(Paths.get(rootDirectory()), fileVisitor);
      } catch (IOException e) {
         log.error("Exception occurred while walking through zipped files", e);
      }
   }

   protected boolean isNotCsvFile(File tempFile) {
      return !tempFile.getName().contains(".csv");
   }

   private class ZippedFileProcessor extends SimpleFileVisitor<Path> {
      @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         File inputFile = file.toFile();
         if (!inputFile.getName().contains(".zip")) {
            return FileVisitResult.CONTINUE;
         }
         String absolutePath = inputFile.getAbsolutePath();
         try {
            // unzip file
            ZipFile zipFile = new ZipFile(absolutePath);
            zipFile.extractAll(TEMP_DIR);
            // process unzipped files
            FileVisitor<Path> fileVisitor = unzippedDataFileProcessor();
            Files.walkFileTree(Paths.get(TEMP_DIR), fileVisitor);
            persistBatch();
         } catch (ZipException e) {
            log.error("Exception occurred while unzipping data file", e);
         } catch (IOException e) {
            log.error("Exception occurred while walking through unzipped files", e);
         }
         return FileVisitResult.CONTINUE;
      }
   }

   public abstract String rootDirectory();

   public abstract FileVisitor<Path> unzippedDataFileProcessor();

   public abstract void persistBatch();
}
