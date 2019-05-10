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
import pl.pogodane.generators.model.RawDailyMeteorologicalStationKDData;
import pl.pogodane.generators.model.RawDailyMeteorologicalStationKDTData;
import pl.pogodane.generators.model.DailyStationEntryKey;
import pl.pogodane.mongo.DailyMeteorologicalStationData;
import pl.pogodane.mongo.repositories.DailyMeteorologicalStationDataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DailyMeteorologicalStationDataGenerator extends AbstractStationDataGenerator {

   private Map<DailyStationEntryKey, DailyMeteorologicalStationData> generatedData = new HashMap<>();
   @Autowired
   private DailyMeteorologicalStationDataRepository dailyMeteorologicalStationDataRepository;

   @Override public String rootDirectory() {
      return "/input/daily/klimat";
   }

   @Override public FileVisitor<Path> unzippedDataFileProcessor() {
      return new UnzippedDataFileProcessor();
   }

   @Override public void persistBatch() {
      log.info("Persisting batch...");
      if (generatedData.size() >= PERSISTANCE_BATCH_SIZE) {
         dailyMeteorologicalStationDataRepository.insert(generatedData.values());
         generatedData.clear();
      }
      log.info("Data persisted");
   }

   private class UnzippedDataFileProcessor extends SimpleFileVisitor<Path> {
      @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         File tempFile = file.toFile();
         if (isNotCsvFile(tempFile)) {
            return FileVisitResult.CONTINUE;
         }
         log.info("Processing temp file: {}", file.getFileName());

         if (tempFile.getName().contains("k_d_t")) {
            handleKdtFile(tempFile);
         } else {
            handleKdFile(tempFile);
         }

         return FileVisitResult.CONTINUE;
      }

      private void handleKdFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-daily-meteorological-kd-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawDailyMeteorologicalStationKDData> rawDailyMeteorologicalStationKDData = (List<RawDailyMeteorologicalStationKDData>) result.getBean("rawDailyMeteorologicalStationKDDataList");
            for (RawDailyMeteorologicalStationKDData entry : rawDailyMeteorologicalStationKDData) {
               LocalDate date = DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay());
               DailyStationEntryKey dailyStationEntryKey = new DailyStationEntryKey(entry.getStationCode(), date);
               DailyMeteorologicalStationData stationData = generatedData.getOrDefault(dailyStationEntryKey, new DailyMeteorologicalStationData());
               updateWithKdData(stationData, entry);
               generatedData.put(dailyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithKdData(DailyMeteorologicalStationData stationData, RawDailyMeteorologicalStationKDData entry) {
         stationData.setStationId(entry.getStationCode());
         stationData.setDate(DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay()));
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setMinimumTemperature(new BigDecimal(entry.getMinimalTemperature()));
         stationData.setMinimumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMinimalTemperatureStatus()));
         stationData.setMaximumTemperature(new BigDecimal(entry.getMaximalTemperature()));
         stationData.setMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximalTemperatureStatus()));
         stationData.setRainfallAmount(new BigDecimal(entry.getRainfallAmount()));
         stationData.setRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getRainfallAmountStatus()));
         stationData.setRainfallType(entry.getRainfallType());
         stationData.setSnowHeight(new BigDecimal(entry.getSnowHeight()));
         stationData.setSnowHeightMeasured(!NO_MEASURE_INDICATOR.equals(entry.getSnowHeightStatus()));
      }

      private void handleKdtFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-daily-meteorological-kdt-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawDailyMeteorologicalStationKDTData> rawDailyMeteorologicalStationKDTData = (List<RawDailyMeteorologicalStationKDTData>) result.getBean("rawDailyMeteorologicalStationKDTDataList");
            for (RawDailyMeteorologicalStationKDTData entry : rawDailyMeteorologicalStationKDTData) {
               LocalDate date = DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay());
               DailyStationEntryKey dailyStationEntryKey = new DailyStationEntryKey(entry.getStationCode(), date);
               DailyMeteorologicalStationData stationData = generatedData.getOrDefault(dailyStationEntryKey, new DailyMeteorologicalStationData());
               updateWithKdtData(stationData, entry);
               generatedData.put(dailyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithKdtData(DailyMeteorologicalStationData stationData, RawDailyMeteorologicalStationKDTData entry) {
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setHumidity(new BigDecimal(entry.getAverageHumidity()));
         stationData.setHumidityMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageHumidityStatus()));
         stationData.setWindSpeed(new BigDecimal(entry.getAverageWindSpeed()));
         stationData.setWindSpeedMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageWindSpeedStatus()));
         stationData.setCloudiness(new BigDecimal(entry.getAverageCloudiness()));
         stationData.setCloudinessMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageCloudinessStatus()));
      }
   }
}
