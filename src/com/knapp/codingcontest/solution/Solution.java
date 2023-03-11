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

package com.knapp.codingcontest.solution;

import java.util.*;
import java.util.stream.Collectors;

import com.knapp.codingcontest.data.*;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.operations.InfoSnapshot;
import com.knapp.codingcontest.operations.Operations;

/**
 * This is the code YOU have to provide
 * <p>
 * param warehouse all the operations you should need -> Annotation
 */
public class Solution {
    public String getParticipantName() {
        return "STERNAD Florian";
    }

    public Institute getParticipantInstitution() {
        return Institute.HTBLA_Kaindorf_Sulm;
    }

    // ----------------------------------------------------------------------------

    protected final InputData input;
    protected final Operations operations;

    // ----------------------------------------------------------------------------

    public Solution(final InputData input, final Operations operations) {
        this.input = input;
        this.operations = operations;

        // TODO: prepare data structures (but may also be done in run() method below)
    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point.
     * <p>
     * calculation for shipments costs:
     * total_cost = sum{products per warehouse/customer} ((cost_base + (sum(size_products) * cost_size)) * distanz_warehouse_to_customer)
     * <p>
     * some hints:
     * - one shipments is: all products for one customer from one warehouse (will be handled and calculated automatically/internally)
     * - there are finite amounts of product stocks in the warehouses (stock will be adjusted automatically by using op.ship() method)
     * - not all warehouses have all products on stock - or stock might run out (may be checked via wh.hasStock())
     * <p>
     * optimization is possible along two factors:
     * - minimize warehouse/customer pairs (#shipments) - reduce cost_base impact on total costs
     * - minimize distances - shipments from closer warehouses are cheaper
     * <p>
     * some ideas for finding a better solution:
     * sometimes it might be beneficial to split an order to have most delivered from close warehouse and only some from farther
     * instead of trying to deliver everything from just one warehouse that is far away
     */
    public void run() throws Exception {
        List<Customer> customers = input.getOrderLines()
                .stream()
                .map(OrderLine::getCustomer)
                .toList();

        Map<Customer, List<OrderLine>> customerMap = new HashMap<>();

        for (Customer c : customers) {
            customerMap.put(
                    c,
                    input.getOrderLines()
                            .stream()
                            .filter(e -> e.getCustomer().equals(c))
                            .sorted((e1, e2) -> e2.getProduct().getSize() - e1.getProduct().getSize())
                            .toList()
            );
        }

        Map<Customer, List<OrderLine>> orderMap = new LinkedHashMap<>();

        customers = customers.stream()
                .sorted((c1, c2) -> customerMap.get(c2).size() - customerMap.get(c1).size())
                .toList();

        for (Customer c : customers) {
            orderMap.put(c, customerMap.get(c));
        }

        for (Map.Entry<Customer, List<OrderLine>> entry : orderMap.entrySet()) {
            Customer customer = entry.getKey();

            Position customerPosition = customer.getPosition();

            for (OrderLine oline : entry.getValue()) {
                Product product = oline.getProduct();

                List<Warehouse> warehouses = input.getWarehouses()
                        .stream()
                        .filter(e -> e.hasStock(product))
                        .toList();


                List<Double> diffs = warehouses
                        .stream()
                        .map(e -> e.getPosition().calculateDistance(customerPosition))
                        .sorted(Double::compareTo)
                        .toList();


                Warehouse nearestWarehouse = warehouses
                        .stream()
                        .filter(e -> e.getPosition().calculateDistance(customerPosition) == diffs.get(0))
                        .findFirst().get();


                operations.ship(oline, nearestWarehouse);
            }
        }

    }

    // ----------------------------------------------------------------------------
    // ----------------------------------------------------------------------------

    /**
     * Just for documentation purposes.
     * <p>
     * Method may be removed without any side-effects
     * <p>
     * divided into 4 sections
     *
     * <li><em>input methods</em>
     *
     * <li><em>main interaction methods</em>
     * - these methods are the ones that make (explicit) changes to the warehouse
     *
     * <li><em>information</em>
     * - information you might need for your solution
     *
     * <li><em>additional information</em>
     * - various other infos: statistics, information about (current) costs, ...
     */
    @SuppressWarnings("unused")
    private void apis() throws Exception {
        // ----- input -----

        final List<OrderLine> orderLines = input.getOrderLines();
        final List<Warehouse> warehouses = input.getWarehouses();

        final OrderLine orderLine = orderLines.get(0);
        final Warehouse warehouse = warehouses.get(0);

        // ----- main interaction methods -----

        operations.ship(orderLine, warehouse); // throws OrderLineAlreadyPackedException, NoStockInWarehouseException;

        // ----- information -----

        final boolean hasStock = warehouse.hasStock(orderLine.getProduct());
        final Map<Product, Integer> currentStocks = warehouse.getCurrentStocks();

        final double distance = orderLine.getCustomer().getPosition().calculateDistance(warehouse.getPosition());

        // ----- additional information -----

        final CostFactors costFactors = operations.getCostFactors();

        final InfoSnapshot info = operations.getInfoSnapshot();
        final int unfinishedOrderLineCount = info.getUnfinishedOrderLineCount();
        final double unfinishedOrderLinesCost = info.getUnfinishedOrderLinesCost();
        final double shipmentsCost = info.getShipmentsCost();
        final double totalCost = info.getTotalCost();
    }

    // ----------------------------------------------------------------------------
}
