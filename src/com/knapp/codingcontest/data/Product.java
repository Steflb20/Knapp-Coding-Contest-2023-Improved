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

public class Product {
  private final String code;
  private final int size;

  // ----------------------------------------------------------------------------

  protected Product(final String code, final int size) {
    this.code = code;
    this.size = size;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the code for this product
   */
  public String getCode() {
    return code;
  }

  /**
   * @return the size for a single item of this product
   */
  public int getSize() {
    return size;
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
    final Product other = (Product) obj;
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
    return "Product#" + code + "[size=" + size + "]";
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
