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
import pl.pogodane.generators.model.RawDailySynopticalStationSDData;
import pl.pogodane.generators.model.RawDailySynopticalStationSDTData;
import pl.pogodane.generators.model.DailyStationEntryKey;
import pl.pogodane.mongo.DailySynopticalStationData;
import pl.pogodane.mongo.repositories.DailySynopticalStationDataRepository;

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
public class DailySynopticalStationDataGenerator extends AbstractStationDataGenerator {

   private Map<DailyStationEntryKey, DailySynopticalStationData> generatedData = new HashMap<>();
   @Autowired
   private DailySynopticalStationDataRepository repository;

   @Override public String rootDirectory() {
      return "/input/daily/synop";
   }

   @Override public FileVisitor<Path> unzippedDataFileProcessor() {
      return new UnzippedDataFileProcessor();
   }

   private class UnzippedDataFileProcessor extends SimpleFileVisitor<Path> {
      @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
         File tempFile = file.toFile();
         if (isNotCsvFile(tempFile)) {
            return FileVisitResult.CONTINUE;
         }
         log.info("Processing temp file: {}", file.getFileName());

         if (tempFile.getName().contains("s_d_t")) {
            handleSdtFile(tempFile);
         } else {
            handleSdFile(tempFile);
         }

         return FileVisitResult.CONTINUE;
      }

      private void handleSdtFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-daily-synoptical-sdt-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawDailySynopticalStationSDTData> rawDailySynopticalStationSDTData = (List<RawDailySynopticalStationSDTData>) result.getBean("rawDailySynopticalSDTDataList");
            for (RawDailySynopticalStationSDTData entry : rawDailySynopticalStationSDTData) {
               LocalDate date = DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay());
               DailyStationEntryKey dailyStationEntryKey = new DailyStationEntryKey(entry.getStationCode(), date);
               DailySynopticalStationData stationData = generatedData.getOrDefault(dailyStationEntryKey, new DailySynopticalStationData());
               updateWithSdtData(stationData, entry);
               generatedData.put(dailyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void handleSdFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-daily-synoptical-sd-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawDailySynopticalStationSDData> rawDailySynopticalStationSDData = (List<RawDailySynopticalStationSDData>) result.getBean("rawDailySynopticalSDDataList");
            for (RawDailySynopticalStationSDData entry : rawDailySynopticalStationSDData) {
               LocalDate date = DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay());
               DailyStationEntryKey dailyStationEntryKey = new DailyStationEntryKey(entry.getStationCode(), date);
               DailySynopticalStationData stationData = generatedData.getOrDefault(dailyStationEntryKey, new DailySynopticalStationData());
               updateWithSdData(stationData, entry);
               generatedData.put(dailyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithSdData(DailySynopticalStationData stationData, RawDailySynopticalStationSDData entry) {
         stationData.setDate(DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay()));
         stationData.setMaximumTemperature(new BigDecimal(entry.getMaximumTemperature()));
         stationData.setMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumTemperatureStatus()));
         stationData.setMinimalTemperature(new BigDecimal(entry.getMinimalTemperature()));
         stationData.setMinimalTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMinimalGroundTemperatureStatus()));
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setRainfallAmount(new BigDecimal(entry.getRainfallAmount()));
         stationData.setRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getRainfallAmountStatus()));
         stationData.setRainfallType(entry.getRainfallType());
         stationData.setSnowHeight(new BigDecimal(entry.getSnowHeight()));
         stationData.setSnowHeightMeasured(!NO_MEASURE_INDICATOR.equals(entry.getSnowHeightStatus()));
      }

      private void updateWithSdtData(DailySynopticalStationData stationData, RawDailySynopticalStationSDTData entry) {
         stationData.setDate(DateUtils.createLocalDate(entry.getYear(), entry.getMonth(), entry.getDay()));
         stationData.setStationId(entry.getStationCode());
         stationData.setAverageAirPressure(new BigDecimal(entry.getAveragePressure()));
         stationData.setAverageAirPressureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAveragePressureStatus()));
         stationData.setAverageCloudiness(new BigDecimal(entry.getAverageCloudiness()));
         stationData.setAverageCloudinessMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageCloudinessStatus()));
         stationData.setAverageHumidity(new BigDecimal(entry.getAverageHumidity()));
         stationData.setAverageHumidityMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageHumidityStatus()));
         stationData.setAverageWindSpeed(new BigDecimal(entry.getAverageWindSpeed()));
         stationData.setAverageWindSpeedMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageWindSpeedStatus()));
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
      }
   }

   @Override public void persistBatch() {
      log.info("Persisting batch...");
      if (generatedData.size() >= PERSISTANCE_BATCH_SIZE) {
         repository.insert(generatedData.values());
         generatedData.clear();
      }
      log.info("Data persisted");
   }
}
