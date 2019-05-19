package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.RawMonthlyRainfallStationData;
import pl.pogodane.mongo.MonthlyRainfallStationData;
import pl.pogodane.mongo.repositories.MonthlyRainfallStationDataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MonthlyRainfallStationDataGenerator extends AbstractStationDataGenerator implements DataGenerator {
   @Autowired
   private MonthlyRainfallStationDataRepository repository;

   @Override public String rootDirectory() {
      return "/input/monthly/opad";
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
            Smooks smooks = new Smooks("smooks-monthly-rainfall-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawMonthlyRainfallStationData> rawMonthlyRainfallStationDataList = (List<RawMonthlyRainfallStationData>) result.getBean("rawMonthlyRainfallStationDataList");
            List<MonthlyRainfallStationData> mappedData = rawMonthlyRainfallStationDataList
               .stream()
               .map(this::map)
               .collect(Collectors.toList());

            repository.saveAll(mappedData);
         } catch (Exception e) {
            log.error("Exception occured while processing CSV file.", e);
         } finally {
            tempFile.delete();
            log.info("Temp file deleted");
         }
         return FileVisitResult.CONTINUE;
      }

      private MonthlyRainfallStationData map(RawMonthlyRainfallStationData input) {
         return MonthlyRainfallStationData.builder()
            .stationId(input.getStationCode())
            .year(input.getYear())
            .month(input.getMonth())
            .rainfallAmount(new BigDecimal(input.getRainfallAmount()))
            .rainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(input.getRainfallAmountStatus()))
            .daysWithSnow(Integer.valueOf(input.getDaysWithSnow()))
            .daysWithSnowMeasured(!NO_MEASURE_INDICATOR.equals(input.getDaysWithSnowStatus()))
            .maximumRainfallAmount(new BigDecimal(input.getMaximumRainfallAmount()))
            .maximumRainfallMeasured(!NO_MEASURE_INDICATOR.equals(input.getMaximumRainfallAmountStatus()))
            .build();
      }
   }
}
