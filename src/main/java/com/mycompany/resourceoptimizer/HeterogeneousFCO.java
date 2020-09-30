/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.resourceoptimizer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class HeterogeneousFCO {

    public HeterogeneousFCO() {

    }
     public static void insertCurrentRACSV(Map<String, Integer> dataDict, FileWriter csvFile) {
        try {
            csvFile.append(dataDict.get("m2.small").toString());
            csvFile.append(",");
            csvFile.append(dataDict.get("m2.medium").toString());
            csvFile.append(",");
            csvFile.append(dataDict.get("m2.large").toString());
            csvFile.append("\n");
        } catch (IOException ex) {
            Logger.getLogger(ResourceOptimizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static float FCOStrategy(int w1, int w2, int w3, int e2eQoS, int delta_A, int delta_B, Float[] price) {
        @SuppressWarnings("UnusedAssignment")
        float total_cost = 0.0F;
        try {

            /*            
            //homogeneous across all layers..
            int[][] S1_W = new int[][]{{1000, 5000, 10000, 20000, 30000},
                                                {0, 0, 0, 0, 0}};
            int[][] S2_W = new int[][]{{1000, 5000, 10000, 20000, 30000, 40000, 50000}, {0, 0, 0, 0, 0, 0, 0}};
            int[][] S3_W = new int[][]{{1000, 5000, 10000}, {0, 0, 0}};
            int[][] S1_Q = new int[][]{{8, 11, 14, 22, 29}, {10000, 10000, 10000, 10000, 10000}};
            int[][] S2_Q = new int[][]{{45, 90, 200, 400, 600, 800, 900}, {10000, 10000, 10000, 10000, 10000, 10000, 10000}};
            int[][] S3_Q = new int[][]{{2, 20, 260}, {10000, 10000, 10000}};
             */
            // int[][] S1_W = new int[][]{{1, 10, 30, 31, 31, 31}, {1, 10, 30, 50, 51, 51}, {1, 10, 30, 50, 80, 90}}; //aws - t2- micro, small, medium
            int[][] S1_W = new int[][]{{1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000}};
            //int[][] S2_W = new int[][]{{1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 6}}; //aws
            int[][] S2_W = new int[][]{{1000, 5000, 10000, 20000, 30000, 40000, 50000, 50001, 50001, 50001, 50001, 50001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 60001, 60001, 60001, 60001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000}};
            // int[][] S3_W = new int[][]{{1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 20, 25, 30}}; //aws
            int[][] S3_W = new int[][]{{1000, 5000, 10000, 10001, 10001, 10001, 10001},
            {1000, 5000, 10000, 20000, 30000, 30001, 30001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000}};
            //int[][] S1_Q = new int[][]{{73, 75, 79, 5000, 5000, 5000}, {28, 35, 44, 45, 5000, 5000}, {16, 25, 31, 33, 52, 60}};
            int[][] S1_Q = new int[][]{{8, 11, 14, 22, 29},
            {7, 9, 10, 13, 18},
            {5, 7, 8, 10, 12}};
            //int[][] S2_Q = new int[][]{{300, 400, 700, 900, 5000, 5000}, {300, 400, 700, 900, 5000, 5000}, {200, 400, 700, 800, 850, 900}};//aws
            int[][] S2_Q = new int[][]{{45, 90, 200, 400, 600, 800, 900, 5000, 5000, 5000, 5000, 5000},
            {30, 60, 200, 400, 500, 600, 800, 900, 5000, 5000, 5000, 5000},
            {25, 50, 100, 200, 250, 350, 450, 500, 600, 700, 800, 900}};
            //int[][] S3_Q = new int[][]{{15, 17, 20, 25, 51, 5000, 5000}, {15, 16, 18, 25, 35, 5000, 5000}, {15, 15, 16, 17, 20, 25, 40}}; //aws
            int[][] S3_Q = new int[][]{{2, 20, 260, 5000, 5000, 5000, 5000},
            {1, 2, 6, 40, 140, 5000, 5000},
            {1, 1, 2, 4, 9, 25, 120}};
            //int delta_A = deltaA;//GetPropertyFileKeyValues.getDeltaMinAQoS(); //based on the minimum latency required in layer 2
            //int delta_B = deltaB;//GetPropertyFileKeyValues.getDeltaMinBQoS(); //based on minimum latency required in layer 3

            int aggQoS = delta_A + delta_B;
            List<String> soln = new ArrayList<>();
            List<Float> totCost = new ArrayList<>();
            List<Integer> qos = new ArrayList<>();
            List<String> soln2 = new ArrayList<>();
            List<Float> totCost2 = new ArrayList<>();
            List<Integer> qos2 = new ArrayList<>();
            List<String> soln3 = new ArrayList<>();
            List<Float> totCost3 = new ArrayList<>();
            List<Integer> qos3 = new ArrayList<>();
            float nextCost = 0.0F;
            String nextInstances = null;
            int nextQoS = 0;
            for (int k = 1; k <= 3; k++) {
                switch (k) {
                    case 1: {
                        String instance_type = null;
                        for (int i = 0; i < S1_W.length; i++) {
                            for (int j = 0; j < S1_W[0].length; j++) {
                                if ((aggQoS + S1_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S1_W[i][j] >= w1) {
                                        z = 1;
                                    } else {
                                        while ((z * S1_W[i][j]) < w1) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "m2.small";
                                            break;
                                        case 1:
                                            instance_type = "m2.medium";
                                            break;
                                        default:
                                            instance_type = "m2.large";
                                            break;
                                    }
                                    soln.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost.add(z * price[i]);
                                    qos.add(S1_Q[i][j]);
                                }
                            }
                        }
                        //new code for heterogeneity
                        String inst_type1 = null;
                        String inst_type2 = null;
                        int kk = 0;
                        while (kk < S1_W[0].length - 1) {

                            for (int i = 0; i < S1_W.length - 1; i++) {
                                for (int j = 0; j < S1_W[0].length - 1; j++) {

                                    if ((aggQoS + Math.max(S1_Q[0][kk], S1_Q[i + 1][j + 1])) <= e2eQoS) {
                                        int x = 1, y = 1;
                                        if (x * S1_W[0][kk] + y * S1_W[i + 1][j + 1] >= w1) {
                                            x = y = 1;

                                        } else {
                                            while ((x * S1_W[0][kk] + y * S1_W[i + 1][j + 1]) < w1) {
                                                x++;
                                                if (x > 2) {
                                                    y++;
                                                }
                                            }
                                        }
                                        if (i == 0) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.medium";
                                        } else if (i == 1) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.large";
                                        }
                                        soln.add(String.valueOf(x) + "x" + inst_type1 + "," + String.valueOf(y) + "x" + inst_type2);
                                        totCost.add(x * price[0] + y * price[i + 1]);
                                        qos.add(Math.max(S1_Q[0][kk], S1_Q[i + 1][j + 1]));
                                        // totCapacity.add((x * S1_W[0][kk]) + (y * S1_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        if (soln.isEmpty()) {
                            System.out.println("no solution exists. please choose different QoS or different min thresholds");
                            return 0.0F;
                        }

                        String[] instS1 = soln.toArray(new String[0]);
                        Float[] costS1 = totCost.toArray(new Float[0]);
                        Integer[] qosS1 = qos.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS1.length; i++) {
                            for (int j = i + 1; j < instS1.length; j++) {
                                if (costS1[j] < costS1[i]) {
                                    tmp = costS1[i];
                                    tmp_val = instS1[i];
                                    tm_qos = qosS1[i];
                                    costS1[i] = costS1[j];
                                    instS1[i] = instS1[j];
                                    qosS1[i] = qosS1[j];
                                    costS1[j] = tmp;
                                    instS1[j] = tmp_val;
                                    qosS1[j] = tm_qos;
                                }
                            }
                        }
                        aggQoS = aggQoS + qosS1[0];
                        total_cost = total_cost + costS1[0];
                        if (costS1.length > 1) {
                            nextCost = costS1[1];
                            nextInstances = instS1[1];
                            nextQoS = qosS1[1];
                        }
                        // MainForm.txtAreaIngestionResources.append("Instances required for ingestion layer: " + instS1[0] + "\n");
                        //System.out.print("Kafka: " + instS1[0] + ", ");
                        

                        break;
                    }
                    case 2: {
                        aggQoS = aggQoS - delta_A;
                        String instance_type = null;
                        for (int i = 0; i < S2_W.length; i++) {
                            for (int j = 0; j < S2_W[0].length; j++) {
                                if ((aggQoS + S2_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S2_W[i][j] >= w2) {
                                        z = 1;
                                    } else {
                                        while ((z * S2_W[i][j]) < w2) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "m2.small";
                                            break;
                                        case 1:
                                            instance_type = "m2.medium";
                                            break;
                                        default:
                                            instance_type = "m2.large";
                                            break;
                                    }
                                    soln2.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost2.add(z * price[i]);
                                    qos2.add(S2_Q[i][j]);
                                }

                            }
                        }
                        //new code - heterogeneity
                        String inst_type1 = null;
                        String inst_type2 = null;
                        int kk = 0;
                        while (kk < S2_W[0].length - 1) {

                            for (int i = 0; i < S2_W.length - 1; i++) {
                                for (int j = 0; j < S2_W[0].length - 1; j++) {

                                    if ((aggQoS + Math.max(S2_Q[0][kk], S2_Q[i + 1][j + 1])) <= e2eQoS) {
                                        int x = 1, y = 1;
                                        if (x * S2_W[0][kk] + y * S2_W[i + 1][j + 1] >= w2) {
                                            x = y = 1;

                                        } else {
                                            while ((x * S2_W[0][kk] + y * S2_W[i + 1][j + 1]) < w2) {
                                                x++;
                                                if (x > 2) {
                                                    y++;
                                                }
                                            }
                                        }
                                        if (i == 0) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.medium";
                                        } else if (i == 1) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.large";
                                        }
                                        soln2.add(String.valueOf(x) + "x" + inst_type1 + "," + String.valueOf(y) + "x" + inst_type2);
                                        totCost2.add(x * price[0] + y * price[i + 1]);
                                        qos2.add(Math.max(S2_Q[0][kk], S2_Q[i + 1][j + 1]));
                                        // totCapacity2.add((x * S2_W[0][kk]) + (y * S2_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        if (soln2.isEmpty()) {
                            return 0.0F;
                        }
                        String[] instS2 = soln2.toArray(new String[0]);
                        Float[] costS2 = totCost2.toArray(new Float[0]);
                        Integer[] qosS2 = qos2.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS2.length; i++) {
                            for (int j = i + 1; j < instS2.length; j++) {
                                if (costS2[j] < costS2[i]) {
                                    tmp = costS2[i];
                                    tmp_val = instS2[i];
                                    tm_qos = qosS2[i];
                                    costS2[i] = costS2[j];
                                    instS2[i] = instS2[j];
                                    qosS2[i] = qosS2[j];
                                    costS2[j] = tmp;
                                    instS2[j] = tmp_val;
                                    qosS2[j] = tm_qos;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS2[0];
                        total_cost = total_cost + costS2[0];
                        //System.out.println("spark: " + instS2[0] + "");
                        System.out.println(instS2[0]);
                        break;
                    }
                    case 3: {
                        aggQoS = aggQoS - delta_B;
                        String instance_type = null;
                        for (int i = 0; i < S3_W.length; i++) {
                            for (int j = 0; j < S3_W[0].length; j++) {
                                if ((aggQoS + S3_Q[i][j]) <= e2eQoS) {
                                    int z = 1;
                                    if (z * S3_W[i][j] >= w3) {
                                        z = 1;
                                    } else {
                                        while ((z * S3_W[i][j]) < w3) {
                                            z++;
                                        }
                                    }
                                    switch (i) {
                                        case 0:
                                            instance_type = "m2.small";
                                            break;
                                        case 1:
                                            instance_type = "m2.medium";
                                            break;
                                        default:
                                            instance_type = "m2.large";
                                            break;
                                    }
                                    soln3.add(String.valueOf(z) + 'X' + instance_type);
                                    totCost3.add(z * price[i]);
                                    qos3.add(S3_Q[i][j]);
                                }

                            }
                        }
                        //new code - heterogeneity
                        String inst_type1 = null;
                        String inst_type2 = null;
                        int kk = 0;
                        while (kk < S3_W[0].length - 1) {

                            for (int i = 0; i < S3_W.length - 1; i++) {
                                for (int j = 0; j < S3_W[0].length - 1; j++) {

                                    if ((aggQoS + Math.max(S3_Q[0][kk], S3_Q[i + 1][j + 1])) <= e2eQoS) {
                                        int x = 1, y = 1;
                                        if (x * S3_W[0][kk] + y * S3_W[i + 1][j + 1] >= w3) {
                                            x = y = 1;

                                        } else {
                                            while ((x * S3_W[0][kk] + y * S3_W[i + 1][j + 1]) < w3) {
                                                x++;
                                                if (x > 2) {
                                                    y++;
                                                }
                                            }
                                        }
                                        if (i == 0) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.medium";
                                        } else if (i == 1) {
                                            inst_type1 = "m2.small";
                                            inst_type2 = "m2.large";
                                        }
                                        soln3.add(String.valueOf(x) + "x" + inst_type1 + "," + String.valueOf(y) + "x" + inst_type2);
                                        totCost3.add(x * price[0] + y * price[i + 1]);
                                        qos3.add(Math.max(S3_Q[0][kk], S3_Q[i + 1][j + 1]));
                                        //totCapacity3.add((x * S3_W[0][kk]) + (y * S3_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        if (soln3.isEmpty()) {
                            return 0.0F;
                        }
                        String[] instS3 = soln3.toArray(new String[0]);
                        Float[] costS3 = totCost3.toArray(new Float[0]);
                        Integer[] qosS3 = qos3.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        for (int i = 0; i < instS3.length; i++) {
                            for (int j = i + 1; j < instS3.length; j++) {
                                if (costS3[j] < costS3[i]) {
                                    tmp = costS3[i];
                                    tmp_val = instS3[i];
                                    tm_qos = qosS3[i];
                                    costS3[i] = costS3[j];
                                    instS3[i] = instS3[j];
                                    qosS3[i] = qosS3[j];
                                    costS3[j] = tmp;
                                    instS3[j] = tmp_val;
                                    qosS3[j] = tm_qos;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS3[0];
                        total_cost = total_cost + costS3[0];

                        //System.out.println("cassandra: " + instS3[0] + "");
                        //System.out.print(instS3[0] + '\t');
                        // String[] split = instS3[0].split("X");

                        //dppResourcesCount[2] = Integer.parseInt(split[0]);
                        //dppInstanceType[2] = split[1];
                        break;
                    }
                    default:
                        break;
                }

            }
           // System.out.println("Total cost: " + String.valueOf(total_cost));
           // System.out.println("total end-to-end QoS: " + String.valueOf(aggQoS));

        } catch (NumberFormatException ex) {

        }
        return total_cost;

    }

}
