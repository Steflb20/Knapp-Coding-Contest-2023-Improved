/* -*- java -*-
# =========================================================================== #
#                                                                             #
#                         Copyright (C) KNAPP AG                              #
#                                                                             #
#       The copyright to the computer program(s) herein is the property       #
#       of Knapp.  The program(s) may be used   and/or copied only with       #
#       the  written permission of  Knapp  or in  accordance  with  the       #
#       terms and conditions stipulated in the agreement/contract under       #
#       which the program(s) have been supplied.                              #
#                                                                             #
# =========================================================================== #
*/

package com.knapp.codingcontest.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InputData {
  // ----------------------------------------------------------------------------

  private static final String PATH_INPUT_DATA;
  static {
    try {
      PATH_INPUT_DATA = new File("./data").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------

  private final String dataPath;

  private final Map<String, Product> products = new HashMap<>();
  private final Map<String, Warehouse> warehouses = new HashMap<>();
  private final Map<String, Customer> customers = new HashMap<>();
  private final List<OrderLine> orderLines = new ArrayList<>();

  // ----------------------------------------------------------------------------

  protected InputData() {
    this(InputData.PATH_INPUT_DATA);
  }

  protected InputData(final String dataPath) {
    this.dataPath = dataPath;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "InputData@" + dataPath;
  }

  // ----------------------------------------------------------------------------

  protected void readData() throws IOException {
    readProducts();
    readWarehouses();
    readWarehousesStocks();
    readCustomers();
    readOrderLines();
  }

  // ----------------------------------------------------------------------------

  /**
   * @return a list of all warehouses
   */
  public List<Warehouse> getWarehouses() {
    return Collections.unmodifiableList(new ArrayList<>(warehouses.values()));
  }

  /**
   * @return a list of all order-lines
   */
  public List<OrderLine> getOrderLines() {
    return Collections.unmodifiableList(orderLines);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readProducts() throws IOException {
    final Reader fr = new FileReader(fullFileName("products.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // product-code;size;
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final int size = Integer.parseInt(columns[1]);
        final Product product = newProduct(code, size);
        products.put(code, product);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  protected abstract Product newProduct(String code, int size);

  // ----------------------------------------------------------------------------

  private void readWarehouses() throws IOException {
    final Reader fr = new FileReader(fullFileName("warehouses.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // code;x;y;
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final int x = Integer.parseInt(columns[1]);
        final int y = Integer.parseInt(columns[2]);
        final Position position = new Position(x, y);
        final Warehouse warehouse = newWarehouse(code, position);
        warehouses.put(code, warehouse);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  protected abstract Warehouse newWarehouse(String code, Position position);

  // ----------------------------------------------------------------------------

  private void readWarehousesStocks() throws IOException {
    final Reader fr = new FileReader(fullFileName("warehouses-stocks.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // whcode;prodcode;stock;
        final String[] columns = splitCsv(line);
        final String whCode = columns[0];
        final String prodCode = columns[1];
        final int stock = Integer.parseInt(columns[2]);
        addWarehouseStock(warehouses.get(whCode), products.get(prodCode), stock);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  protected abstract void addWarehouseStock(Warehouse warehouse, Product product, int stock);

  // ----------------------------------------------------------------------------

  private void readCustomers() throws IOException {
    final Reader fr = new FileReader(fullFileName("customers.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // customer-code;x;y;
        final String[] columns = splitCsv(line);
        final String code = columns[0];
        final int x = Integer.parseInt(columns[1]);
        final int y = Integer.parseInt(columns[2]);
        final Position position = new Position(x, y);
        final Customer customer = newCustomer(code, position);
        customers.put(code, customer);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  protected abstract Customer newCustomer(String code, Position position);

  // ----------------------------------------------------------------------------

  private void readOrderLines() throws IOException {
    final Reader fr = new FileReader(fullFileName("order-lines.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // customer-code;product-code;
        final String[] columns = splitCsv(line);
        final String custCode = columns[0];
        final String prodCode = columns[1];
        final OrderLine orderLine = newOrderLine(customers.get(custCode), products.get(prodCode));
        orderLines.add(orderLine);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  protected abstract OrderLine newOrderLine(Customer customer, Product product);

  // ----------------------------------------------------------------------------

  protected File fullFileName(final String fileName) {
    final String fullFileName = dataPath + File.separator + fileName;
    return new File(fullFileName);
  }

  protected void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------

  protected String[] splitCsv(final String line) {
    return line.split(";");
  }

  // ----------------------------------------------------------------------------
  // ............................................................................
}
