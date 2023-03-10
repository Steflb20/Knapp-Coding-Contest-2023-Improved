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

public interface CostFactors {
  /**
   * @return costs if there is at least one <code>OrderLine</code> that has not been shipped
   */
  double getUnfinishedOrderLinesPenalty();

  /**
   * @return costs per <code>OrderLine</code> that has not been shipped
   */
  double getUnfinishedOrderLinesCost();

  /**
   * Get the base cost per shipment.<p>
   * All products that are shipped from one warehouse to a single customer count as one shipment.
   *
   * @return base cost per shipment
   */
  double getShipmentBaseCosts();

  /**
   * Get the dynamic cost per shipment. The total size of a shipment is multiplied by this factor and the distance.<p>
   * All products that are shipped from one warehouse to a single customer count as one shipment.
   *
   * @return shipment cost per size/distance
   */
  double getShipmentCostsPerSizeAndDistance();
}
