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

public class Position {
  private final int x;
  private final int y;

  // ----------------------------------------------------------------------------

  public Position(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  // ----------------------------------------------------------------------------

  /**
   * @return x value of position
   */
  public int getX() {
    return x;
  }

  /**
   * @return y value of position
   */
  public int getY() {
    return y;
  }

  // ----------------------------------------------------------------------------

  /**
   * Calculate the distance between this position and another position.
   *
   * @param other Other position
   * @return distance between this instance of Position and other 
   */
  public double calculateDistance(final Position other) {
    return Math.sqrt(pow2(other.getX() - getX()) + pow2(other.getY() - getY()));
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "Position[x=" + x + ", y=" + y + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + x;
    result = (prime * result) + y;
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
    final Position other = (Position) obj;
    if (x != other.x) {
      return false;
    }
    if (y != other.y) {
      return false;
    }
    return true;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private int pow2(final int v) {
    return v * v;
  }

  // ----------------------------------------------------------------------------
}
