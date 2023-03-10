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

/**
 *
 */
public interface InfoSnapshot {
  // ----------------------------------------------------------------------------

  /**
   * @return number of unfinished order-lines
   */
  int getUnfinishedOrderLineCount();

  // ----------------------------------------------------------------------------

  /**
   * @return costs of current unfinished order-lines
   */
  double getUnfinishedOrderLinesCost();

  /**
   * @return costs of current shipments
   */
  double getShipmentsCost();

  /**
   * The total result used for ranking.
   *
   *   (Excludes time-based ranking factor)
   *
   * @return
   */
  double getTotalCost();

  // ----------------------------------------------------------------------------
}
