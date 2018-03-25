package com.github.liudaomanbu.excel.config;

import com.google.common.collect.Iterables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ParserConfigTest {
  private final ParserConfig parserConfig = ParserConfig.GLOBAL;

  @Test
  public void  testIsCellDataType() {
    try {
      parserConfig.isBoolean(Boolean.TRUE);
      parserConfig.isBoolean(Boolean.FALSE);
      parserConfig.isString(Boolean.TRUE);
      parserConfig.isString(Boolean.FALSE);
      parserConfig.isDouble(Boolean.TRUE);
      parserConfig.isDouble(Boolean.FALSE);
      parserConfig.isDate(Boolean.TRUE);
      parserConfig.isDate(Boolean.FALSE);

      parserConfig.isBoolean(Math.random());
      parserConfig.isString(Math.random());
      parserConfig.isDouble(Math.random());
      parserConfig.isDate(Math.random());

      parserConfig.isBoolean(new Date());
      parserConfig.isString(new Date());
      parserConfig.isDouble(new Date());
      parserConfig.isDate(new Date());

      parserConfig.isBoolean(UUID.randomUUID().toString());
      Assertions.assertTrue(parserConfig.isBoolean(Boolean.TRUE.toString()));
      Assertions.assertTrue(parserConfig.isBoolean(Boolean.FALSE.toString()));
      parserConfig.isString(UUID.randomUUID().toString());
      parserConfig.isDouble(UUID.randomUUID().toString());
      Assertions.assertTrue(parserConfig.isDouble(Math.random() + ""));
      parserConfig.isDate(UUID.randomUUID().toString());
      Assertions.assertTrue(parserConfig.isDate(LocalDate.now().toString()));
      Assertions.assertTrue(parserConfig.isDate(LocalTime.now().toString()));
      Assertions.assertTrue(parserConfig.isDate(LocalTime.now().toString()));
    } catch (Exception e) {
      Assertions.assertTrue(false);
    }
  }

  @Test
  public void testFommater() {
    LocalDateTime localDateTime = LocalDateTime.now();
    System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(localDateTime));
    System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime));
//    System.out.println(DateTimeFormatter.ISO_DATE_TIME.parse("2015-05-12"));
    System.out.println(DateTimeFormatter.ISO_DATE_TIME.parse("2015-05-12T22:11:33.44"));
//    System.out.println(DateTimeFormatter.ISO_DATE_TIME.parse("22:11:33.44"));
//    System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("2015-05-12"));
    System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("2015-05-12T22:11:33.44"));
//    System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse("22:11:33.44"));
  }
}