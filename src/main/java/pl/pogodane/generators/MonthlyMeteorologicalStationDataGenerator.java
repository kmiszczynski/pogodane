package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.MonthlyStationEntryKey;
import pl.pogodane.generators.model.RawMonthlyMeteorologicalStationKMDData;
import pl.pogodane.generators.model.RawMonthlyMeteorologicalStationKMTData;
import pl.pogodane.mongo.MonthlyMeteorologicalStationData;
import pl.pogodane.mongo.repositories.MonthlyMeteorologicalStationDataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MonthlyMeteorologicalStationDataGenerator extends AbstractStationDataGenerator {
   private Map<MonthlyStationEntryKey, MonthlyMeteorologicalStationData> generatedData = new HashMap<>();
   @Autowired
   private MonthlyMeteorologicalStationDataRepository repository;

   @Override public String rootDirectory() {
      return "/input/monthly/klimat";
   }

   @Override public FileVisitor<Path> unzippedDataFileProcessor() {
      return new UnzippedDataFileProcessor();
   }

   @Override public void persistBatch() {
      log.info("Persisting batch...");
      if (generatedData.size() >= PERSISTANCE_BATCH_SIZE) {
         repository.insert(generatedData.values());
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

         if (tempFile.getName().contains("k_m_d")) {
            handleKmdFile(tempFile);
         } else {
            handleKmtFile(tempFile);
         }

         return FileVisitResult.CONTINUE;
      }

      private void handleKmtFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-monthly-meteorological-kmt-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawMonthlyMeteorologicalStationKMTData> rawMonthlyMeteorologicalStationKMTData = (List<RawMonthlyMeteorologicalStationKMTData>) result.getBean("rawMonthlyMeteorologicalStationKMTDataList");
            for (RawMonthlyMeteorologicalStationKMTData entry : rawMonthlyMeteorologicalStationKMTData) {
               MonthlyStationEntryKey monthlyStationEntryKey = new MonthlyStationEntryKey(entry.getStationCode(), entry.getYear(), entry.getMonth());
               MonthlyMeteorologicalStationData stationData = generatedData.getOrDefault(monthlyStationEntryKey, new MonthlyMeteorologicalStationData());
               updateWithKmtData(stationData, entry);
               generatedData.put(monthlyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithKmtData(MonthlyMeteorologicalStationData stationData, RawMonthlyMeteorologicalStationKMTData entry) {
         stationData.setStationId(entry.getStationCode());
         stationData.setYear(entry.getYear());
         stationData.setMonth(entry.getMonth());
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setAverageHumidity(new BigDecimal(entry.getAverageHumidity()));
         stationData.setAverageHumidityMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageHumidityStatus()));
         stationData.setAverageWindSpeed(new BigDecimal(entry.getAverageWindSpeed()));
         stationData.setAverageWindSpeedMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageWindSpeedStatus()));
         stationData.setAverageCloudiness(new BigDecimal(entry.getAverageCloudiness()));
         stationData.setAverageCloudinessMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageCloudinessStatus()));
      }

      private void handleKmdFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-monthly-meteorological-kmd-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawMonthlyMeteorologicalStationKMDData> rawMonthlyMeteorologicalStationKMDData = (List<RawMonthlyMeteorologicalStationKMDData>) result.getBean("rawMonthlyMeteorologicalStationKMDDataList");
            for (RawMonthlyMeteorologicalStationKMDData entry : rawMonthlyMeteorologicalStationKMDData) {
               MonthlyStationEntryKey monthlyStationEntryKey = new MonthlyStationEntryKey(entry.getStationCode(), entry.getYear(), entry.getMonth());
               MonthlyMeteorologicalStationData stationData = generatedData.getOrDefault(monthlyStationEntryKey, new MonthlyMeteorologicalStationData());
               updateWithKmdData(stationData, entry);
               generatedData.put(monthlyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithKmdData(MonthlyMeteorologicalStationData stationData, RawMonthlyMeteorologicalStationKMDData entry) {
         stationData.setStationId(entry.getStationCode());
         stationData.setYear(entry.getYear());
         stationData.setMonth(entry.getMonth());
         stationData.setMaximumTemperature(new BigDecimal(entry.getMaximumTemperature()));
         stationData.setMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumTemperatureStatus()));
         stationData.setMinimumTemperature(new BigDecimal(entry.getMinimalTemperature()));
         stationData.setMinimumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMinimalTemperatureStatus()));
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setRainfallAmount(new BigDecimal(entry.getRainfallAmount()));
         stationData.setRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getRainfallAmountStatus()));
         stationData.setMaximumRainfallAmount(new BigDecimal(entry.getMaximumRainfallAmount()));
         stationData.setMaximumRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumRainfallAmountStatus()));
         stationData.setAverageMaximumTemperature(new BigDecimal(entry.getAverageMaximumTemperature()));
         stationData.setAverageMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageMaximumTemperatureStatus()));
         stationData.setAverageMinimumTemperature(new BigDecimal(entry.getAverageMinimalTemperature()));
         stationData.setAverageMinimumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageMinimalTemperatureStatus()));
         stationData.setMaximumSnowHeight(new BigDecimal(entry.getMaximumSnowHeight()));
         stationData.setMaximumSnowHeightMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumSnowHeightStatus()));
         stationData.setDaysWithSnowSurface(Integer.valueOf(entry.getDaysWithSnowSurface()));
         stationData.setDaysWithRain(Integer.valueOf(entry.getDaysWithRain()));
         stationData.setDaysWithSnow(Integer.valueOf(entry.getDaysWithSnow()));
      }
   }
}
