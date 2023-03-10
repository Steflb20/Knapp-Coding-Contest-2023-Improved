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

import java.util.Collection;

import com.knapp.codingcontest.core.InputDataInternal.MyCustomer;
import com.knapp.codingcontest.core.InputDataInternal.MyWarehouse;
import com.knapp.codingcontest.data.Customer;
import com.knapp.codingcontest.data.OrderLine;
import com.knapp.codingcontest.data.Product;
import com.knapp.codingcontest.data.Warehouse;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.Operations;
import com.knapp.codingcontest.operations.ex.NoStockInWarehouseException;
import com.knapp.codingcontest.operations.ex.OrderLineAlreadyPackedException;

public class OperationsInternal implements Operations {
  // ----------------------------------------------------------------------------

  public final InputDataInternal iinput;
  private final CostFactors costFactors;

  // ----------------------------------------------------------------------------

  public OperationsInternal(final InputDataInternal iinput, final CostFactors costFactors) {
    this.iinput = iinput;
    this.costFactors = costFactors;
  }

  // ----------------------------------------------------------------------------

  public static final String COST_EVAL_OPEN_LINES_[] = { //
      "no open lines => no penalty", //
      "(cost_unfinished_penalty + (unfinished_line_count * cost_unfinished_line))", //
  };
  public static final String COST_EVAL_SHIPMENT = //
      "(cost_base + (sum(product_size) * distance_warehouse_customer) * cost_size)";

  public double costUnfinishedOrderLines(final int unfinishedOrderLineCount) {
    return (unfinishedOrderLineCount == 0) ? 0 //
        : (getCostFactors().getUnfinishedOrderLinesPenalty()
            + (getCostFactors().getUnfinishedOrderLinesCost() * unfinishedOrderLineCount));
  }

  /**
   * Calculate the costs for a shipment.
   *
   *  Mostly useful to build an algorithm to create all shipments before actually shipping them, while keeping actual costs low.
   *  Good results may be achieved without the need to calculate costs, by reducing #shippings, distances, ..
   *
   *  Note: this does NOT do any checks - be it available stock, or even if product (still) has to be shipped for customer
   */
  public double costSingleShipment(final Warehouse wh, final Customer customer, final Collection<? extends Product> products) {
    return costSingleShipment(wh, customer, products, getCostFactors());
  }

  // ............................................................................

  @Override
  public void ship(final OrderLine orderLine, final Warehouse warehouse)
      throws OrderLineAlreadyPackedException, NoStockInWarehouseException {
    if (!warehouse.hasStock(orderLine.getProduct())) {
      throw new NoStockInWarehouseException("no (more) stock for " + orderLine.getCustomer().getCode() + ":<"
          + orderLine.getProduct().getCode() + "> in <" + warehouse.getCode() + ">");
    }
    if (!shipped(orderLine, warehouse)) {
      throw new OrderLineAlreadyPackedException(
          "no more open order-lines for " + orderLine.getCustomer().getCode() + ":<" + orderLine.getProduct().getCode() + ">");
    }
    iinput._result.add(new WarehouseOperation.Ship(warehouse, orderLine));
  }

  // ----------------------------------------------------------------------------

  @Override
  public InfoSnapshotInternal getInfoSnapshot() {
    return new InfoSnapshotInternal(this);
  }

  @Override
  public CostFactors getCostFactors() {
    return costFactors;
  }

  // ----------------------------------------------------------------------------

  public Iterable<WarehouseOperation> result() {
    return iinput._result;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  double costSingleShipment(final Warehouse wh, final Customer customer, final Collection<? extends Product> products,
      final CostFactors c) {
    final double cost_base = c.getShipmentBaseCosts();

    final int sum_product_size = products.stream().mapToInt(p -> p.getSize()).sum();
    final double cost_size = c.getShipmentCostsPerSizeAndDistance();

    final double distance_warehouse_customer = wh.getPosition().calculateDistance(customer.getPosition());

    return (cost_base + ((sum_product_size * distance_warehouse_customer) * cost_size));
  }

  private boolean shipped(final OrderLine orderLine, final Warehouse warehouse) {
    final MyWarehouse mw = iinput._warehouses.get(warehouse.getCode());
    final int[] stw = mw.productStock.get(orderLine.getProduct().getCode());
    final MyCustomer mc = iinput._customers.get(orderLine.getCustomer().getCode());
    final int[] stc = mc.products.get(orderLine.getProduct().getCode());
    if ((stc == null) || (stc[0] == 0)) {
      return false;
    }
    stc[0]--;
    stw[0]--;
    return true;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static final class CostFactorsInternal implements CostFactors {
    @Override
    public double getUnfinishedOrderLinesPenalty() {
      return 20_000.0;
    }

    @Override
    public double getUnfinishedOrderLinesCost() {
      return 2.0;
    }

    @Override
    public double getShipmentBaseCosts() {
      return 1.0;
    }

    // scale shipment costs about 1.2 to costs related to per size*distance/1000

    @Override
    public double getShipmentCostsPerSizeAndDistance() {
      // scale of 1000.0 to be in same general amount
      return 0.00083;
    }
  }

  // ----------------------------------------------------------------------------
}
