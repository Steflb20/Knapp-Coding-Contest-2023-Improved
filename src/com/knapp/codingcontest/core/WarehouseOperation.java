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

import com.knapp.codingcontest.data.OrderLine;
import com.knapp.codingcontest.data.Warehouse;

public abstract class WarehouseOperation {
  // ----------------------------------------------------------------------------

  public abstract String toResultString();

  // ----------------------------------------------------------------------------

  public static class Ship extends WarehouseOperation {
    public final String whCode;
    public final String prodCode;
    public final String custCode;

    public Ship(final Warehouse warehouse, final OrderLine orderLine) {
      whCode = warehouse.getCode();
      prodCode = orderLine.getProduct().getCode();
      custCode = orderLine.getCustomer().getCode();
    }

    @Override
    public String toResultString() {
      return String.format("Ship;%s;%s;%s;", whCode, prodCode, custCode);
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
