package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.DateUtils;
import pl.pogodane.generators.model.RawDailyRainfallStationData;
import pl.pogodane.mongo.DailyRainfallStationData;
import pl.pogodane.mongo.repositories.DailyRainfallStationDataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DailyRainfallStationDataGenerator extends AbstractStationDataGenerator {

   private static final String ROOT = "/input/daily/opad";

   @Autowired
   private DailyRainfallStationDataRepository dailyRainfallStationDataRepository;

   @Override public String rootDirectory() {
      return ROOT;
   }

   @Override public FileVisitor<Path> unzippedDataFileProcessor() {
      return new UnzippedDataFileProcessor();
   }

   @Override public void persistBatch() {

   }

   private class UnzippedDataFileProcessor extends SimpleFileVisitor<Path> {
      @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         File tempFile = file.toFile();
         if (isNotCsvFile(tempFile)) {
            return FileVisitResult.CONTINUE;
         }
         log.info("Processing temp file: {}", file.getFileName());

         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-daily-rainfall-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawDailyRainfallStationData> rawDailyRainfallStationDataList = (List<RawDailyRainfallStationData>) result.getBean("rawDailyRainfallStationDataList");
            List<DailyRainfallStationData> mappedData = rawDailyRainfallStationDataList
               .stream()
               .map(this::map)
               .collect(Collectors.toList());

            dailyRainfallStationDataRepository.saveAll(mappedData);
         } catch (Exception e) {
            log.error("Exception occured while processing CSV file.", e);
         } finally {
            tempFile.delete();
            log.info("Temp file deleted");
         }
         return FileVisitResult.CONTINUE;
      }

      private DailyRainfallStationData map(RawDailyRainfallStationData input) {
         LocalDate date = DateUtils.createLocalDate(input.getYear(), input.getMonth(), input.getDay());
         boolean noRainfallData = NO_MEASURE_INDICATOR.equals(input.getRainfallStatus());
         boolean noSnowData = NO_MEASURE_INDICATOR.equals(input.getSnowStatus());

         return DailyRainfallStationData.builder()
            .date(date)
            .stationId(input.getStationCode())
            .rainfallAmount(new BigDecimal(input.getRainfallAmount()))
            .noRainfallData(noRainfallData)
            .rainfallType(input.getRainfallType())
            .snowHeight(new BigDecimal(input.getSnowHeight()))
            .noSnowData(noSnowData)
            .build();
      }
   }
}
