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

package com.knapp.codingcontest.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.knapp.codingcontest.data.Customer;
import com.knapp.codingcontest.data.InputData;
import com.knapp.codingcontest.data.OrderLine;
import com.knapp.codingcontest.data.Position;
import com.knapp.codingcontest.data.Product;
import com.knapp.codingcontest.data.Warehouse;
import com.knapp.codingcontest.operations.CostFactors;

public class InputDataInternal extends InputData {
  // ----------------------------------------------------------------------------

  protected final Map<String, MyProduct> _products = new HashMap<>();
  protected final Map<String, MyWarehouse> _warehouses = new HashMap<>();
  protected final Map<String, MyCustomer> _customers = new HashMap<>();
  protected final List<MyOrderLine> _orderLines = new ArrayList<>();

  protected final List<WarehouseOperation> _result = new ArrayList<>();

  private final OperationsInternal operations;

  // ----------------------------------------------------------------------------

  public InputDataInternal(final CostFactors costs) {
    operations = new OperationsInternal(this, costs);
  }

  public InputDataInternal(final String dataPath, final CostFactors costs) {
    super(dataPath);
    operations = new OperationsInternal(this, costs);
  }

  // ----------------------------------------------------------------------------

  @Override
  public void readData() throws IOException {
    super.readData();
  }

  // ----------------------------------------------------------------------------

  @Override
  protected MyProduct newProduct(final String code, final int size) {
    final MyProduct myProduct = new MyProduct(code, size);
    _products.put(code, myProduct);
    return myProduct;
  }

  @Override
  protected MyWarehouse newWarehouse(final String code, final Position position) {
    final MyWarehouse myWarehouse = new MyWarehouse(this, code, position);
    _warehouses.put(code, myWarehouse);
    return myWarehouse;
  }

  @Override
  protected void addWarehouseStock(final Warehouse warehouse, final Product product, final int stock) {
    _warehouses.get(warehouse.getCode()).productStock.computeIfAbsent(product.getCode(), c -> new int[] { 0 })[0] += stock;
    _products.get(product.getCode()).stockQuantity += stock;
  }

  @Override
  protected MyCustomer newCustomer(final String code, final Position position) {
    final MyCustomer myCustomer = new MyCustomer(code, position);
    _customers.put(code, myCustomer);
    return myCustomer;
  }

  @Override
  protected MyOrderLine newOrderLine(final Customer customer, final Product product) {
    final MyOrderLine myOrderLine = new MyOrderLine(customer, product);
    _orderLines.add(myOrderLine);
    _customers.get(customer.getCode()).products.computeIfAbsent(product.getCode(), c -> new int[] { 0 })[0]++;
    _products.get(product.getCode()).requestedQuantity++;
    return myOrderLine;
  }

  // ----------------------------------------------------------------------------

  public OperationsInternal getOperations() {
    return operations;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static class MyProduct extends Product {
    int stockQuantity;
    int requestedQuantity;

    public MyProduct(final String code, final int size) {
      super(code, size);
    }

    @Override
    public String toString() {
      return "MyProduct[stockQuantity=" + stockQuantity + ", requestedQuantity=" + requestedQuantity + ", getCode()="
          + getCode() + ", getSize()=" + getSize() + "]";
    }

  }

  public static class MyWarehouse extends Warehouse {
    private final InputDataInternal in;
    final Map<String, int[]> productStock = new TreeMap<>();

    public MyWarehouse(final InputDataInternal in, final String code, final Position position) {
      super(code, position);
      this.in = in;
    }

    @Override
    public boolean hasStock(final Product product) {
      final int[] stw = productStock.get(product.getCode());
      if ((stw != null) && (stw[0] > 0)) {
        return true;
      }
      return false;
    }

    @Override
    public Map<Product, Integer> getCurrentStocks() {
      return productStock.entrySet()
          .stream()
          .collect(Collectors.collectingAndThen(
              Collectors.toMap(e -> in._products.get(e.getKey()), e -> Integer.valueOf(e.getValue()[0])),
              Collections::unmodifiableMap));
    }
  }

  public static class MyCustomer extends Customer {
    final Map<String, int[]> products = new TreeMap<>();

    public MyCustomer(final String code, final Position position) {
      super(code, position);
    }

    @Override
    public String toString() {
      return "MyCustomer[getCode()=" + getCode() + ", getPosition()=" + getPosition() + "]";
    }
  }

  public static class MyOrderLine extends OrderLine {
    public MyOrderLine(final Customer customer, final Product product) {
      super(customer, product);
    }

    @Override
    public String toString() {
      return "MyOrderLine[getCustomer()=" + getCustomer().getCode() + ", getProduct()=" + getProduct().getCode() + "]";
    }
  }

  // ----------------------------------------------------------------------------

  public InputStat inputStat() {
    return new InputStat(this);
  }

  public static final class InputStat {
    public final int countWarehouses;
    public final int countCustomers;
    public final int countOrderLines;
    public final int countUniqueProducts;

    public final double avgOrderLinesPerCustomer;

    private InputStat(final InputDataInternal iinput) {
      countWarehouses = iinput._warehouses.size();
      countCustomers = iinput._customers.size();
      countOrderLines = iinput._orderLines.size();
      countUniqueProducts = iinput._products.size();
      avgOrderLinesPerCustomer = (double) countOrderLines / countCustomers;
    }
  }

  // ----------------------------------------------------------------------------
}
