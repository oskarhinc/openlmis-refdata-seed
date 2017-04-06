package mw.gov.health.lmis;

import static mw.gov.health.lmis.utils.FileNames.GEOGRAPHIC_LEVELS;
import static mw.gov.health.lmis.utils.FileNames.GEOGRAPHIC_ZONES;
import static mw.gov.health.lmis.utils.FileNames.PROGRAMS;
import static mw.gov.health.lmis.utils.FileNames.ROLES;
import static mw.gov.health.lmis.utils.FileNames.STOCK_ADJUSTMENT_REASONS;
import static mw.gov.health.lmis.utils.FileNames.getFullMappingFileName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mw.gov.health.lmis.converter.Converter;
import mw.gov.health.lmis.converter.Mapping;
import mw.gov.health.lmis.converter.MappingConverter;
import mw.gov.health.lmis.reader.GenericReader;
import mw.gov.health.lmis.upload.AuthService;
import mw.gov.health.lmis.upload.GeographicLevelService;
import mw.gov.health.lmis.upload.GeographicZoneService;
import mw.gov.health.lmis.upload.ProgramService;
import mw.gov.health.lmis.upload.RoleService;
import mw.gov.health.lmis.upload.StockAdjustmentReasonService;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);

  @Autowired
  private Configuration configuration;

  @Autowired
  private ProgramService programService;

  @Autowired
  private StockAdjustmentReasonService stockAdjustmentReasonService;

  @Autowired
  private GeographicLevelService geographicLevelService;

  @Autowired
  private GeographicZoneService geographicZoneService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private GenericReader reader;

  @Autowired
  private Converter converter;

  @Autowired
  private MappingConverter mappingConverter;

  @Autowired
  private AuthService authService;

  /**
   * Seeds data into OLMIS.
   */
  public void seedData() {
    LOGGER.info("Seeding Programs");
    List<Map<String, String>> csvs = reader.readFromFile(PROGRAMS);
    List<Mapping> mappings = mappingConverter.getMappingForFile(new File(getFullMappingFileName(
        configuration.getDirectory(), PROGRAMS)));
    for (Map<String, String> csv : csvs) {
      String json = converter.convert(csv, mappings);
      LOGGER.info(json);
      programService.createResource(json);
    }

    LOGGER.info("Seeding GeographicLevels");
    csvs = reader.readFromFile(GEOGRAPHIC_LEVELS);
    mappings = mappingConverter.getMappingForFile(new File(getFullMappingFileName(
        configuration.getDirectory(), GEOGRAPHIC_LEVELS)));
    for (Map<String, String> csv : csvs) {
      String json = converter.convert(csv, mappings);
      LOGGER.info(json);
      geographicLevelService.createResource(json);
    }

    LOGGER.info("Seeding GeographicZones");
    csvs = reader.readFromFile(GEOGRAPHIC_ZONES);
    mappings = mappingConverter.getMappingForFile(new File(getFullMappingFileName(
        configuration.getDirectory(), GEOGRAPHIC_ZONES)));
    for (Map<String, String> csv : csvs) {
      String json = converter.convert(csv, mappings);
      LOGGER.info(json);
      geographicZoneService.createResource(json);
    }

    LOGGER.info("Seeding StockAdjustmentReasons");
    csvs = reader.readFromFile(STOCK_ADJUSTMENT_REASONS);
    mappings = mappingConverter.getMappingForFile(new File(getFullMappingFileName(
        configuration.getDirectory(), STOCK_ADJUSTMENT_REASONS)));
    for (Map<String, String> csv : csvs) {
      String json = converter.convert(csv, mappings);
      LOGGER.info(json);
      stockAdjustmentReasonService.createResource(json);
    }

    LOGGER.info("Seeding Roles");
    csvs = reader.readFromFile(ROLES);
    mappings = mappingConverter.getMappingForFile(new File(getFullMappingFileName(
        configuration.getDirectory(), ROLES)));
    for (Map<String, String> csv : csvs) {
      String json = converter.convert(csv, mappings);
      LOGGER.info(json);
      roleService.createResource(json);
    }
  }
}
