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

public class Customer {
  private final String code;
  private final Position position;

  // ----------------------------------------------------------------------------

  protected Customer(final String code, final Position position) {
    this.code = code;
    this.position = position;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the code for this customer
   */
  public String getCode() {
    return code;
  }

  /**
   * @return the position for this customer - to be able to calculate distances
   */
  public Position getPosition() {
    return position;
  }

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
    final Customer other = (Customer) obj;
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
    return "Customer#" + code + "[" + position + "]";
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
