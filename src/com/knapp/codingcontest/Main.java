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

package com.knapp.codingcontest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.knapp.codingcontest.core.InfoSnapshotInternal;
import com.knapp.codingcontest.core.InputDataInternal;
import com.knapp.codingcontest.core.OperationsInternal;
import com.knapp.codingcontest.core.PrepareUpload;
import com.knapp.codingcontest.operations.CostFactors;
import com.knapp.codingcontest.solution.Solution;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 *   => but changing the output may lead to invalid results on upload!
 * ----------------------------------------------------------------------------
 */
public class Main {
  // ----------------------------------------------------------------------------

  public static void main(final String... args) throws Exception {
    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
    System.out.println("vvv   KNAPP Coding Contest #11: STARTING...    vvv");
    System.out.println(String.format("vvv                %s                    vvv", Main.DATE_FORMAT.format(new Date())));
    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");

    System.out.println("# ------------------------------------------------");
    System.out.println("# ... LOADING INPUT ...");
    final InputDataInternal iinput = new InputDataInternal(new OperationsInternal.CostFactorsInternal());
    iinput.readData();
    final InputDataInternal.InputStat istat = iinput.inputStat();

    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RUN YOUR SOLUTION ...");
    final long start = System.currentTimeMillis();
    final Solution solution = new Solution(iinput, iinput.getOperations());
    Throwable throwable = null;
    try {
      solution.run();
    } catch (final Throwable _throwable) {
      throwable = _throwable;
    }
    final long end = System.currentTimeMillis();
    System.out.println("# ... DONE ... (" + Main.formatInterval(end - start) + ")");

    System.out.println("# ------------------------------------------------");
    System.out.println("# ... WRITING OUTPUT/RESULT ...");
    PrepareUpload.createZipFile(solution, iinput.getOperations());
    System.out.println(">>> Created " + PrepareUpload.FILENAME_RESULT + " & " + PrepareUpload.FILENAME_UPLOAD_ZIP);
    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    System.out.println("^^^   KNAPP Coding Contest #11: FINISHED       ^^^");
    System.out.println(String.format("^^^                %s                    ^^^", Main.DATE_FORMAT.format(new Date())));
    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
    System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

    Main.printResults(solution, istat, iinput.getOperations());

    if (throwable != null) {
      System.out.println("");
      System.out.println("# ... Ooops ...");
      System.out.println("");
      throwable.printStackTrace(System.out);
    }
  }

  @SuppressWarnings("boxing")
  public static String formatInterval(final long interval) {
    final int h = (int) ((interval / (1000 * 60 * 60)) % 60);
    final int m = (int) ((interval / (1000 * 60)) % 60);
    final int s = (int) ((interval / 1000) % 60);
    final int ms = (int) (interval % 1000);
    return String.format("%02d:%02d:%02d.%03d", h, m, s, ms);
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  @SuppressWarnings("boxing")
  public static void printResults(final Solution solution, final InputDataInternal.InputStat istat,
      final OperationsInternal iop) throws Exception {
    final InfoSnapshotInternal info = iop.getInfoSnapshot();
    final CostFactors c = iop.getCostFactors();

    //
    final long t_sh = info.totalShipmentCount();
    final double t_szxd = info.totalSizeByDistance();
    final double t_d = info.totalDistance();

    //
    final int uo = info.getUnfinishedOrderLineCount();
    final double c_uo_p = uo > 0 ? c.getUnfinishedOrderLinesPenalty() : 0;
    final double c_uo_ = info.getUnfinishedOrderLinesCost() - c_uo_p;
    final double c_s = info.getShipmentsCost();
    final double c_t = info.getTotalCost();

    final double c_sh = info.totalShipmentCount() * c.getShipmentBaseCosts();
    final double c_szxd = c_s - c_sh;

    //
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
    System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

    //
    System.out.println(String.format("  --------------------------------------------------------------"));
    System.out.println(String.format("    INPUT STATISTICS                                            "));
    System.out.println(String.format("  ------------------------------------- : ----------------------"));
    System.out.println(String.format("      #warehouses                       :  %8d", istat.countWarehouses));
    System.out.println(String.format("      #customers                        :  %8d", istat.countCustomers));
    System.out.println(String.format("      #order-lines                      :  %8d", istat.countOrderLines));
    System.out.println(String.format("      #products                         :  %8d", istat.countUniqueProducts));
    System.out.println(String.format("      order-lines / customer            :  %10.1f", istat.avgOrderLinesPerCustomer));

    //
    System.out.println(String.format("  --------------------------------------------------------------"));
    System.out.println(String.format("    RESULT STATISTICS                                           "));
    System.out.println(String.format("  ------------------------------------- : ----------------------"));
    System.out.println(String.format("      total shipment count              :  %10d", t_sh));
    System.out.println(String.format("      sum(size*distance)                :  %10.1f", t_szxd));
    System.out.println(String.format("      total distanze                    :  %10.1f", t_d));

    System.out.println(">> Calc for penalty costs:      " + OperationsInternal.COST_EVAL_OPEN_LINES_[uo == 0 ? 0 : 1]);
    System.out.println(">> Calc for per shipment costs: " + OperationsInternal.COST_EVAL_SHIPMENT);

    //
    System.out.println(String.format("  ============================================================================="));
    System.out.println(String.format("    RESULTS                                                                    "));
    System.out.println(String.format("  ===================================== : ============ | ======================"));
    System.out.println(String.format("      what                              :       costs  |  (details: count,...)"));
    System.out.println(String.format("  ------------------------------------- : ------------ | ----------------------"));
    System.out.println(String.format("   -> penalty unfinished order-lines    :  %10.1f  |   %6d", c_uo_p, uo));
    System.out.println(String.format("   -> costs/unfinished order-lines      :  %10.1f  |   %6d", c_uo_, uo));
    System.out.println(String.format("   -> #shipments                        :  %10.1f  |   %6d", c_sh, t_sh));
    System.out.println(String.format("   -> sum(dist*size)                    :  %10.1f  |   %6.1f", c_szxd, t_szxd));
    System.out.println(String.format("  ------------------------------------- : ------------ | ----------------------"));
    System.out.println(String.format("   => TOTAL COST                           %10.1f", c_t));
    System.out.println(String.format("                                          ============"));
  }

  // ----------------------------------------------------------------------------

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

  // ----------------------------------------------------------------------------
}
