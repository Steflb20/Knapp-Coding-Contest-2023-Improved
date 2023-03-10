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

public class OrderLine {
  private final Customer customer;
  private final Product product;

  // ----------------------------------------------------------------------------

  protected OrderLine(final Customer customer, final Product product) {
    this.customer = customer;
    this.product = product;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return the customer to whom this product is to be shipped
   */
  public Customer getCustomer() {
    return customer;
  }

  /**
   * @return the product to be shipped
   */
  public Product getProduct() {
    return product;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "OrderLine[customer=" + customer.getCode() + ", product=" + product.getCode() + "]";
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
}
