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

import java.util.Map;

public abstract class Warehouse {
  private final String code;
  private final Position position;

  // ----------------------------------------------------------------------------

  protected Warehouse(final String code, final Position position) {
    this.code = code;
    this.position = position;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the code for this warehouse
   */
  public String getCode() {
    return code;
  }

  /**
   * @return the position for this warehouse - to be able to calculate distances
   */
  public Position getPosition() {
    return position;
  }

  // ----------------------------------------------------------------------------

  /**
   * Check to see if the warehouse (has at least one) item on stock.
   *
   * @param orderLine
   * @return <code>true</code> if stock is available
   */
  public abstract boolean hasStock(Product product);

  /**
   * A snapshot of the current available stock(s) for this warehouse
   *
   * @return read-only snapshot of stock(s)
   */
  public abstract Map<Product, Integer> getCurrentStocks();

  // ----------------------------------------------------------------------------

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((code == null) ? 0 : code.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Warehouse other = (Warehouse) obj;
    if (code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!code.equals(other.code)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Warehouse#" + code + "[" + position + "]";
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
