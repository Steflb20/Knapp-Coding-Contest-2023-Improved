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

package com.knapp.codingcontest.operations;

import com.knapp.codingcontest.data.OrderLine;
import com.knapp.codingcontest.data.Warehouse;
import com.knapp.codingcontest.operations.ex.NoStockInWarehouseException;
import com.knapp.codingcontest.operations.ex.OrderLineAlreadyPackedException;

public interface Operations {
  /**
   * Ship a single product from a warehouse to a customer to fulfill an <code>OrderLine</code>.
   *
   *  Validation is done to ensure stock availability and correctness of products for customer
   *  Then internal state is adjusted accordingly: stock, customer/product, ...
   *
   * @param orderLine
   * @param warehouse
   * @throws OrderLineAlreadyPackedException
   * @throws NoStockInWarehouseException
   */
  void ship(OrderLine orderLine, Warehouse warehouse) throws OrderLineAlreadyPackedException, NoStockInWarehouseException;

  /**
   * @return a snapshot of various information: costs so far, unfinished count
   */
  InfoSnapshot getInfoSnapshot();

  /**
   * @return the cost factors used
   */
  CostFactors getCostFactors();
}
