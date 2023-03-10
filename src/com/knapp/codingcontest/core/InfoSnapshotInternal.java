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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.knapp.codingcontest.core.InputDataInternal.MyCustomer;
import com.knapp.codingcontest.core.InputDataInternal.MyProduct;
import com.knapp.codingcontest.core.InputDataInternal.MyWarehouse;
import com.knapp.codingcontest.core.WarehouseOperation.Ship;
import com.knapp.codingcontest.operations.InfoSnapshot;

public class InfoSnapshotInternal implements InfoSnapshot {
  // ----------------------------------------------------------------------------

  private final long totalShipmentCount;
  private final double totalSizeByDistance;
  private final double totalDistance;
  private final double totalSize;

  private final Map<String, Map<String, Shipment>> shipments = new TreeMap<>();

  private final int unfinishedOrderLineCount;

  private final double unfinishedOrderLinesCost;
  private final double shipmentsBaseCost;
  private final double shipmentsSizeDistCost;
  private final double totalCost;

  // ----------------------------------------------------------------------------

  protected InfoSnapshotInternal(final OperationsInternal iop) {
    final InputDataInternal in = iop.iinput;

    final List<ShippedProduct> shippedProducts = new ArrayList<>();
    for (final WarehouseOperation o : iop.result()) {
      if (o instanceof WarehouseOperation.Ship) {
        final WarehouseOperation.Ship ship = (WarehouseOperation.Ship) o;
        shippedProducts.add(new ShippedProduct(ship, in));
      }
    }

    storeShipments(shippedProducts);

    totalShipmentCount = shippedProducts.stream().map(s -> t(s.customer, s.warehouse)).distinct().count();
    totalSizeByDistance = shippedProducts.stream()
        .map(s -> t(t(s.customer, s.warehouse), s.product))
        .collect(Collectors.groupingBy(t -> t.v1()))
        .entrySet()
        .stream()
        .mapToDouble(t -> t.getKey().v1().getPosition().calculateDistance(t.getKey().v2().getPosition())
            * t.getValue().stream().mapToDouble(p -> p.v2().getSize()).sum())
        .sum();
    totalDistance = shippedProducts.stream()
        .map(s -> t(s.customer, s.warehouse))
        .distinct()
        .mapToDouble(t -> t.v1().getPosition().calculateDistance(t.v2().getPosition()))
        .sum();
    totalSize = shippedProducts.stream()
        .map(s -> t(s.product, s.warehouse))
        .collect(Collectors.groupingBy(Tuple::v2))
        .entrySet()
        .stream()
        .mapToLong(e -> e.getValue().stream().mapToLong(t -> t.v1().getSize()).sum())
        .sorted()
        .sum();

    unfinishedOrderLineCount = in._orderLines.size() - shippedProducts.size();

    unfinishedOrderLinesCost = iop.costUnfinishedOrderLines(unfinishedOrderLineCount);

    shipmentsBaseCost = shippedProducts.stream()
        .collect(Collectors.groupingBy(s -> t(s.warehouse, s.customer)))
        .keySet()
        .stream()
        .count() * iop.getCostFactors().getShipmentBaseCosts();

    shipmentsSizeDistCost = shippedProducts.stream()
        .collect(Collectors.groupingBy(s -> t(s.warehouse, s.customer)))
        .entrySet()
        .stream()
        .mapToDouble(e -> iop.costSingleShipment(e.getKey().v1(), e.getKey().v2(),
            e.getValue().stream().map(p -> p.product).collect(Collectors.toList())))
        .sum() - shipmentsBaseCost;
    ;

    totalCost = unfinishedOrderLinesCost + shipmentsBaseCost + shipmentsSizeDistCost;
  }

  // ----------------------------------------------------------------------------

  private void storeShipments(final List<ShippedProduct> shippedProducts) {
    final List<Tuple<MyWarehouse, Map<MyCustomer, List<ShippedProduct>>>> wcps = shippedProducts.stream()
        .collect(Collectors.groupingBy(s -> s.warehouse))
        .entrySet()
        .stream()
        .map(e -> t(e.getKey(), e.getValue().stream().collect(Collectors.groupingBy(s -> s.customer))))
        .collect(Collectors.toList());
    for (final Tuple<MyWarehouse, Map<MyCustomer, List<ShippedProduct>>> wcp : wcps) {
      for (final Map.Entry<MyCustomer, List<ShippedProduct>> cp : wcp.v2.entrySet()) {
        shipments.computeIfAbsent(cp.getKey().getCode(), cc -> new TreeMap<>())
            .computeIfAbsent(wcp.v1().getCode(), wc -> new Shipment(cp.getValue()));
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("InfoSnapshotInternal[")
        .append("totalShipmentCount=")
        .append(totalShipmentCount)
        .append(", totalDistance=")
        .append(totalDistance)
        .append(", totalSize=")
        .append(totalSize)

        .append(", shipments=")
        .append(shipments)
        .append(", unfinishedOrderLineCount=")
        .append(unfinishedOrderLineCount)

        .append(", unfinishedOrderLinesCost=")
        .append(unfinishedOrderLinesCost)

        .append(", shipmentsBaseCost=")
        .append(shipmentsBaseCost)
        .append(", shipmentsSizeDistCost=")
        .append(shipmentsSizeDistCost)
        .append(", totalCost=")
        .append(totalCost)
        .append("]");
    return sb.toString();
  }

  // ----------------------------------------------------------------------------

  public final long totalShipmentCount() {
    return totalShipmentCount;
  }

  public double totalDistance() {
    return totalDistance;
  }

  public double totalSizeByDistance() {
    return totalSizeByDistance;
  }

  public Map<String, Map<String, Shipment>> shipments() {
    return shipments;
  }

  // ----------------------------------------------------------------------------

  @Override
  public int getUnfinishedOrderLineCount() {
    return unfinishedOrderLineCount;
  }

  @Override
  public double getUnfinishedOrderLinesCost() {
    return unfinishedOrderLinesCost;
  }

  @Override
  public double getShipmentsCost() {
    return getShipmentsBaseCost() + getShipmentsSizeDistCost();
  }

  public double getShipmentsBaseCost() {
    return shipmentsBaseCost;
  }

  public double getShipmentsSizeDistCost() {
    return shipmentsSizeDistCost;
  }

  @Override
  public double getTotalCost() {
    return totalCost;
  }

  // ----------------------------------------------------------------------------

  <T1, T2> Tuple<T1, T2> t(final T1 v1, final T2 v2) {
    return new Tuple<>(v1, v2);
  }

  public static final class Tuple<T1, T2> implements Serializable {
    private static final long serialVersionUID = 1L;

    public final T1 v1;
    public final T2 v2;

    public Tuple(final T1 v1, final T2 v2) {
      this.v1 = v1;
      this.v2 = v2;
    }

    public T1 v1() {
      return v1;
    }

    public T2 v2() {
      return v2;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((v1 == null) ? 0 : v1.hashCode());
      return (prime * result) + ((v2 == null) ? 0 : v2.hashCode());
    }

    @Override
    public String toString() {
      return "Tuple[ " + this.v1 + " | " + this.v2 + " ]";
    }

    @Override
    public boolean equals(final Object other_) {
      if (!(other_ instanceof Tuple)) {
        return false;
      }
      final Tuple<?, ?> other = (Tuple<?, ?>) other_;
      return Tuple.isEqual(this.v1, other.v1) //
          && Tuple.isEqual(this.v2, other.v2);
    }

    private static boolean isEqual(final Object thisMember, final Object otherMember) {
      return (((thisMember == null) && (otherMember == null)) //
          || ((thisMember != null) && (thisMember.equals(otherMember))));
    }
  }

  // ----------------------------------------------------------------------------

  static class ShippedProduct {
    final MyWarehouse warehouse;
    final MyCustomer customer;
    final MyProduct product;

    ShippedProduct(final Ship ship, final InputDataInternal in) {
      warehouse = in._warehouses.get(ship.whCode);
      customer = in._customers.get(ship.custCode);
      product = in._products.get(ship.prodCode);
    }

    MyWarehouse getWarehouse() {
      return warehouse;
    }

    MyCustomer getCustomer() {
      return customer;
    }

    MyProduct getProduct() {
      return product;
    }
  }

  public static final class Shipment implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int count;
    public final int size;
    public final Map<String, Long> products;

    public Shipment(final List<ShippedProduct> shippedProducts) {
      count = shippedProducts.size();
      size = shippedProducts.stream().mapToInt(s -> s.product.getSize()).sum();
      products = shippedProducts.stream().collect(Collectors.groupingBy(s -> s.product.getCode(), Collectors.counting()));
    }

    @Override
    public String toString() {
      return "[#" + products.size() + "/" + count + ":" + size + "]";
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
