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
import pl.pogodane.generators.model.RawMonthlySynopticalStationSMDData;
import pl.pogodane.generators.model.RawMonthlySynopticalStationSMTData;
import pl.pogodane.mongo.MonthlySynopticalStationData;
import pl.pogodane.mongo.repositories.MonthlySynopticalStationDataRepository;

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

@Slf4j
@Component
public class MonthlySynopticalStationDataGenerator extends AbstractStationDataGenerator implements DataGenerator {
   private Map<MonthlyStationEntryKey, MonthlySynopticalStationData> generatedData = new HashMap<>();
   @Autowired
   private MonthlySynopticalStationDataRepository repository;

   @Override public String rootDirectory() {
      return "/input/monthly/synop";
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

         if (tempFile.getName().contains("s_m_d")) {
            handleSmdFile(tempFile);
         } else {
            handleSmtFile(tempFile);
         }

         return FileVisitResult.CONTINUE;
      }

      private void handleSmtFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-monthly-synoptical-smt-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawMonthlySynopticalStationSMTData> rawMonthlySynopticalStationSMTData = (List<RawMonthlySynopticalStationSMTData>) result.getBean("rawMonthlySynopticalStationSMTDataList");
            for (RawMonthlySynopticalStationSMTData entry : rawMonthlySynopticalStationSMTData) {
               MonthlyStationEntryKey monthlyStationEntryKey = new MonthlyStationEntryKey(entry.getStationCode(), entry.getYear(), entry.getMonth());
               MonthlySynopticalStationData stationData = generatedData.getOrDefault(monthlyStationEntryKey, new MonthlySynopticalStationData());
               updateWithSmtData(stationData, entry);
               generatedData.put(monthlyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithSmtData(MonthlySynopticalStationData stationData, RawMonthlySynopticalStationSMTData entry) {
         stationData.setStationId(entry.getStationCode());
         stationData.setYear(entry.getYear());
         stationData.setMonth(entry.getMonth());
         stationData.setAverageCloudiness(new BigDecimal(entry.getAverageCloudiness()));
         stationData.setAverageCloudinessMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageCloudinessStatus()));
         stationData.setAverageWindSpeed(new BigDecimal(entry.getAverageWindSpeed()));
         stationData.setAverageWindSpeedMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageWindSpeedStatus()));
         stationData.setAverageHumidity(new BigDecimal(entry.getAverageHumidity()));
         stationData.setAverageHumidityMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageHumidityStatus()));
         stationData.setAverageAirPressure(new BigDecimal(entry.getAverageAirPressure()));
         stationData.setAverageAirPressureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageAirPressureStatus()));
      }

      private void handleSmdFile(File tempFile) {
         try {
            String fileContent = IOUtils.toString(new FileInputStream(tempFile), "UTF-8");
            Smooks smooks = new Smooks("smooks-monthly-synoptical-smd-station.xml");
            ExecutionContext executionContext = smooks.createExecutionContext();
            JavaResult result = new JavaResult();
            smooks.filterSource(executionContext, new StringSource(fileContent), result);
            List<RawMonthlySynopticalStationSMDData> rawMonthlySynopticalStationSMDData = (List<RawMonthlySynopticalStationSMDData>) result.getBean("rawMonthlySynopticalStationSMDDataList");
            for (RawMonthlySynopticalStationSMDData entry : rawMonthlySynopticalStationSMDData) {
               MonthlyStationEntryKey monthlyStationEntryKey = new MonthlyStationEntryKey(entry.getStationCode(), entry.getYear(), entry.getMonth());
               MonthlySynopticalStationData stationData = generatedData.getOrDefault(monthlyStationEntryKey, new MonthlySynopticalStationData());
               updateWithSmdData(stationData, entry);
               generatedData.put(monthlyStationEntryKey, stationData);
            }
         } catch (Exception e) {
            log.error("Exception occurred while parsing KD file", e);
         } finally {
            tempFile.delete();
         }
      }

      private void updateWithSmdData(MonthlySynopticalStationData stationData, RawMonthlySynopticalStationSMDData entry) {
         stationData.setStationId(entry.getStationCode());
         stationData.setYear(entry.getYear());
         stationData.setMonth(entry.getMonth());
         stationData.setMaximumTemperature(new BigDecimal(entry.getMaximumTemperature()));
         stationData.setMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumTemperatureStatus()));
         stationData.setAverageMaximumTemperature(new BigDecimal(entry.getAverageMaximumTemperature()));
         stationData.setAverageMaximumTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageMaximumTemperatureStatus()));
         stationData.setMinimalTemperature(new BigDecimal(entry.getMinimalTemperature()));
         stationData.setMinimalTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMinimalTemperatureStatus()));
         stationData.setAverageMinimalTemperature(new BigDecimal(entry.getAverageMinimalTemperature()));
         stationData.setAverageMinimalTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMinimalTemperatureStatus()));
         stationData.setAverageTemperature(new BigDecimal(entry.getAverageTemperature()));
         stationData.setAverageTemperatureMeasured(!NO_MEASURE_INDICATOR.equals(entry.getAverageTemperatureStatus()));
         stationData.setRainfallAmount(new BigDecimal(entry.getRainfallAmount()));
         stationData.setRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getRainfallAmountStatus()));
         stationData.setMaximumRainfallAmount(new BigDecimal(entry.getMaximumRainfallAmount()));
         stationData.setMaximumRainfallAmountMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumRainfallAmountStatus()));
         stationData.setMaximumSnowHeight(new BigDecimal(entry.getMaximumSnowHeight()));
         stationData.setMaximumSnowHeightMeasured(!NO_MEASURE_INDICATOR.equals(entry.getMaximumSnowHeightStatus()));
         stationData.setDaysWithSnowSurface(Integer.valueOf(entry.getDaysWithSnowSurface()));
         stationData.setDaysWithSnowSurfaceMeasured(!NO_MEASURE_INDICATOR.equals(entry.getDaysWithSnowSurfaceStatus()));
         stationData.setDaysWithRain(Integer.valueOf(entry.getDaysWithRain()));
         stationData.setDaysWithRainMeasured(!NO_MEASURE_INDICATOR.equals(entry.getDaysWithRainStatus()));
         stationData.setDaysWithSnow(Integer.valueOf(entry.getDaysWithSnow()));
         stationData.setDaysWithSnowMeasured(!NO_MEASURE_INDICATOR.equals(entry.getDaysWithSnowStatus()));

      }
   }
}
