package io.wurmatron.mining_goggles.utils;

import java.util.*;

public class WavelengthCalculator {

  public static int[] computeWavelength(int[][] minMaxWavelengths) {
    minMaxWavelengths = removeEmptyValues(minMaxWavelengths);
    if (minMaxWavelengths.length == 1) {
      return minMaxWavelengths[0];
    } else if (minMaxWavelengths.length == 2) {
      // Step 0 - Compute Initial Values / Differences
      int lowDiff = minMaxWavelengths[0][1] - minMaxWavelengths[0][0];
      int highDiff = minMaxWavelengths[1][1] - minMaxWavelengths[1][0];
      if (lowDiff == 0) {
        lowDiff = 1;
      }
      if (highDiff == 0) {
        highDiff = 1;
      }
      lowDiff = Math.abs(1000 / lowDiff);
      highDiff = Math.abs(1000 / highDiff);
      double scale =
          Math.max(highDiff, lowDiff) + (int) (Math.min(highDiff, lowDiff) / 2);
      // Step 1 - Compute wavelength diff
      double step1Min =
          (double) minMaxWavelengths[0][0] / (double) minMaxWavelengths[1][0];
      double step1Max =
          (double) minMaxWavelengths[0][1] / (double) minMaxWavelengths[1][1];
      // Step 2 - Adjust to "scale" (crystal "efficiency")
      double step2A = step1Min * scale;
      double step2B = step1Max * scale;
      // Step 3 - Compute final
      double step3A = step2A + minMaxWavelengths[1][0];
      double step3B = step2B + minMaxWavelengths[0][0];
      // Verify Output
      return new int[]{(int) Math.round(Math.min(step3A, step3B)),
          (int) Math.round(Math.max(step3A, step3B))};
    }
    return new int[]{-1, -1};
  }

  private static int[][] removeEmptyValues(int[][] val) {
    List<int[]> arr = new ArrayList<>();
    int[] prev = new int[] {-1, -1};
    for (int[] x : val) {
      if (x != null && x.length == 2 && x[0] > -1 && x[1] > -1 && prev[0] != x[0] && prev[1] != x[1]) {
        arr.add(x);
      }
      prev = x;
    }
    return arr.toArray(new int[0][0]);
  }
}
