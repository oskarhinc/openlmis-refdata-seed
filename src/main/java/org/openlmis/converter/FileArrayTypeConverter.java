/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.converter;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.openlmis.Configuration;
import org.openlmis.reader.Reader;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

@Component
class FileArrayTypeConverter extends BaseTypeConverter {

  @Autowired
  private Configuration configuration;

  @Autowired
  private Reader reader;

  @Autowired
  private MappingConverter mappingConverter;

  @Autowired
  private Converter converter;

  @Override
  public boolean supports(String type) {
    return startsWithIgnoreCase(type, "TO_ARRAY_FROM_FILE_BY");
  }

  @Override
  public void convert(JsonObjectBuilder builder, Mapping mapping, String value) {
    List<String> codes = getArrayValues(value);
    String by = getBy(mapping.getType());

    String parent = configuration.getDirectory();
    String inputFileName = new File(parent, mapping.getEntityName()).getAbsolutePath();
    List<Map<String, String>> csvs = reader.readFromFile(new File(inputFileName));
    csvs.removeIf(map -> !codes.contains(map.get(by)));

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

    if (!csvs.isEmpty()) {
      String mappingFileName = inputFileName.replace(".csv", "_mapping.csv");
      List<Mapping> mappings = mappingConverter.getMappingForFile(new File(mappingFileName));

      for (Map<String, String> csv : csvs) {
        String json = converter.convert(csv, mappings).toString();

        try (JsonReader jsonReader = Json.createReader(new StringReader(json))) {
          arrayBuilder.add(jsonReader.readObject());
        }
      }
    }

    builder.add(mapping.getTo(), arrayBuilder);
  }

}
