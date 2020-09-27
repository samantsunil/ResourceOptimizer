/*
 * The MIT License
 *
 * Copyright 2020 sunil.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mycompany.resourceoptimizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author sunil
 */
public class ResourceOptimizer {

    public ResourceOptimizer() {

    }
    static int current_capacity_ingestion, current_capacity_processing, current_capacity_storage;
    static String current_allocation_ingestion, current_allocation_processing, current_allocation_storage;
    static float previous_Period_total_cost;
    static Map<String, Integer> ingestion_resources = new HashMap<>(); //to store current ingestion resoure allocation
    static Map<String, Integer> processing_resources = new HashMap<>(); // current processing RA
    static Map<String, Integer> storage_resources = new HashMap<>(); //storage RA
    static FileWriter ingestionRA = null;
    static FileWriter processingRA = null;
    static FileWriter storageRA = null;

    public static int[] read() throws IOException {
        File inputWorkbook = new File("D:\\dpp.xls");
        Workbook w;
        int[] dataArr = null;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);
            dataArr = new int[sheet.getRows()];
            for (int j = 0; j < sheet.getColumns(); j++) {
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell cell = sheet.getCell(j, i);
                    dataArr[i] = Integer.parseInt(cell.getContents());
                }
            }
        } catch (BiffException e) {
        }
        return dataArr;
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

    public static boolean checkForEmptyAllocation(Map<String, Integer> resourceAllocation) {
        if (resourceAllocation.get("m2.small") > 0) {
            return true;
        } else if (resourceAllocation.get("m2.medium") > 0) {
            return true;
        } else if (resourceAllocation.get("m2.large") > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static float DCOHomogeneous(int w1, int w2, int w3, int e2eQoS, int deltaA, int deltaB, Float[] price, boolean init, boolean ingSO, boolean ingSI, boolean proSO, boolean proSI, boolean stoSO, boolean stoSI) {
        @SuppressWarnings("UnusedAssignment")
        float total_cost = 0.0F;

        try {

            // int[][] S1_W = new int[][]{{1, 10, 30, 31, 31, 31}, {1, 10, 30, 50, 51, 51}, {1, 10, 30, 50, 80, 90}}; //aws - t2- micro, small, medium
            int[][] S1_W = new int[][]{{1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000}};
            //int[][] S2_W = new int[][]{{1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 6}};
            int[][] S2_W = new int[][]{{1000, 5000, 10000, 20000, 30000, 40000, 50000, 50001, 50001, 50001, 50001, 50001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 60001, 60001, 60001, 60001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000}};
            // int[][] S3_W = new int[][]{{1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 20, 25, 30}};
            int[][] S3_W = new int[][]{{1000, 5000, 10000, 10001, 10001, 10001, 10001},
            {1000, 5000, 10000, 20000, 30000, 30001, 30001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000}};
            //int[][] S1_Q = new int[][]{{73, 75, 79, 5000, 5000, 5000}, {28, 35, 44, 45, 5000, 5000}, {16, 25, 31, 33, 52, 60}};
            int[][] S1_Q = new int[][]{{8, 11, 14, 22, 29},
            {7, 9, 10, 13, 18},
            {5, 7, 8, 10, 12}};
            //int[][] S2_Q = new int[][]{{300, 400, 700, 900, 5000, 5000}, {300, 400, 700, 900, 5000, 5000}, {200, 400, 700, 800, 850, 900}};
            int[][] S2_Q = new int[][]{{45, 90, 200, 400, 600, 800, 900, 5000, 5000, 5000, 5000, 5000},
            {30, 60, 200, 400, 500, 600, 800, 900, 5000, 5000, 5000, 5000},
            {25, 50, 100, 200, 250, 350, 450, 500, 600, 700, 800, 900}};
            //int[][] S3_Q = new int[][]{{15, 17, 20, 25, 51, 5000, 5000}, {15, 16, 18, 25, 35, 5000, 5000}, {15, 15, 16, 17, 20, 25, 40}};
            int[][] S3_Q = new int[][]{{2, 20, 260, 5000, 5000, 5000, 5000},
            {1, 2, 6, 40, 140, 5000, 5000},
            {1, 1, 2, 4, 9, 25, 120}};

            /*
            //to consider m2.small instance only
            int[][] S1_W = new int[][]{{1000, 5000, 10000, 20000, 30000},{0, 0, 0, 0, 0}};
            int[][] S1_Q = new int[][]{{8, 11, 14, 22, 29}, {10000, 10000, 10000, 10000, 10000}};
            int[][] S2_W = new int[][]{{1000, 5000, 10000, 20000, 30000, 40000, 50000}, {0, 0, 0, 0, 0, 0, 0}};
            int[][] S2_Q = new int[][]{{45, 90, 200, 400, 600, 800, 900}, {10000, 10000, 10000, 10000, 10000, 10000, 10000}};
            int[][] S3_W = new int[][]{{1000, 5000, 10000}, {0, 0, 0}};
            int[][] S3_Q = new int[][]{{2, 20, 260}, {10000, 10000, 10000}};
             */
            int delta_A = deltaA;//GetPropertyFileKeyValues.getDeltaMinAQoS(); //based on the minimum latency required in layer 2
            int delta_B = deltaB;//GetPropertyFileKeyValues.getDeltaMinBQoS(); //based on minimum latency required in layer 3

            int aggQoS = delta_A + delta_B;
            List<String> soln = new ArrayList<>();
            List<Float> totCost = new ArrayList<>();
            List<Integer> qos = new ArrayList<>();
            List<Integer> totCapacity = new ArrayList<>();

            List<String> soln2 = new ArrayList<>();
            List<Float> totCost2 = new ArrayList<>();
            List<Integer> qos2 = new ArrayList<>();
            List<Integer> totCapacity2 = new ArrayList<>();

            List<String> soln3 = new ArrayList<>();
            List<Float> totCost3 = new ArrayList<>();
            List<Integer> qos3 = new ArrayList<>();
            List<Integer> totCapacity3 = new ArrayList<>();
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
                                    soln.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost.add(z * price[i]);
                                    qos.add(S1_Q[i][j]);
                                    totCapacity.add(z * S1_W[i][j]);
                                }
                            }
                        }

                        if (soln.isEmpty()) {
                            System.out.println("no solution exists. please choose different QoS or different min thresholds");
                            return 0.0F;
                        }
                        String[] instS1 = soln.toArray(new String[0]);
                        Float[] costS1 = totCost.toArray(new Float[0]);
                        Integer[] qosS1 = qos.toArray(new Integer[0]);
                        Integer[] capacityS1 = totCapacity.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS1.length; i++) {
                            for (int j = i + 1; j < instS1.length; j++) {
                                if (costS1[j] < costS1[i]) {
                                    tmp = costS1[i];
                                    tmp_val = instS1[i];
                                    tm_qos = qosS1[i];
                                    tmp_capacity = capacityS1[i];
                                    costS1[i] = costS1[j];
                                    instS1[i] = instS1[j];
                                    qosS1[i] = qosS1[j];
                                    costS1[j] = tmp;
                                    instS1[j] = tmp_val;
                                    qosS1[j] = tm_qos;
                                    capacityS1[j] = tmp_capacity;
                                }
                            }
                        }
                        aggQoS = aggQoS + qosS1[0];
                        if (init) {
                            total_cost = total_cost + costS1[0];
                            current_capacity_ingestion = capacityS1[0];
                            //current_allocation_ingestion = instS1[0];
                            if (instS1[0].indexOf(',') != -1) {
                                String[] extStr = instS1[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS1[0].split("x");
                                ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                        } else {
                            if (ingSI) {
                                if (!(current_capacity_ingestion == capacityS1[0])) {
                                    if (instS1[0].indexOf(',') != -1) {
                                        String[] extStr = instS1[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (ingestion_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(ingestion_resources)) {
                                                    current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                    if (ingestion_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                    }
                                                    if (ingestion_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                    }
                                                    if (ingestion_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                    }

                                                } else {
                                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (ingestion_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                    }
                                                    if (ingestion_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                    }
                                                    if (ingestion_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                    }
                                                }
                                            } else if (ingestion_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                                ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }

                                            } else {
                                                // previous_Period_total_cost = previous_Period_total_cost + costS1[0];
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                            }
                                        }
                                    } else {
                                        String[] split = instS1[0].split("x");
                                        if (ingestion_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            ingestion_resources.put(split[1], ingestion_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(ingestion_resources)) {

                                                current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];

                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }

                                            } else {
                                                ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                            }
                                        } else if (ingestion_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                            // previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                            ingestion_resources.put(split[1], ingestion_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (ingestion_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                            }
                                            if (ingestion_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                            }
                                            if (ingestion_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                            }

                                        } else {
                                            if (ingestion_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                            }
                                            if (ingestion_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                            }
                                            if (ingestion_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                            }
                                        }
                                    }
                                } else if (current_capacity_ingestion == capacityS1[0]) {
                                    //current_capacity_ingestion = capacityS1[0];
                                    if (ingestion_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                    }
                                    if (ingestion_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                    }
                                    if (ingestion_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                    }
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } else if (ingSO) {

                                if (instS1[0].indexOf(',') != -1) {
                                    String[] extStr = instS1[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS1[0].split("x");
                                    ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (ingestion_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                }
                                if (ingestion_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                }
                                if (ingestion_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                }
                                //previous_Period_total_cost = previous_Period_total_cost + costS1[0];
                                current_capacity_ingestion = current_capacity_ingestion + capacityS1[0];

                            } else {
                                System.out.println("code reach here!");
                            }
                        }
                        System.out.println("Instances required for Kafka: " + instS1[0] + "\n");

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
                                    soln2.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost2.add(z * price[i]);
                                    qos2.add(S2_Q[i][j]);
                                    totCapacity2.add(z * S2_W[i][j]);
                                }

                            }
                        }
                        if (soln2.isEmpty()) {
                            return 0.0F;
                        }

                        String[] instS2 = soln2.toArray(new String[0]);
                        Float[] costS2 = totCost2.toArray(new Float[0]);
                        Integer[] qosS2 = qos2.toArray(new Integer[0]);
                        Integer[] capacityS2 = totCapacity2.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS2.length; i++) {
                            for (int j = i + 1; j < instS2.length; j++) {
                                if (costS2[j] < costS2[i]) {
                                    tmp = costS2[i];
                                    tmp_val = instS2[i];
                                    tm_qos = qosS2[i];
                                    tmp_capacity = capacityS2[i];
                                    costS2[i] = costS2[j];
                                    instS2[i] = instS2[j];
                                    qosS2[i] = qosS2[j];
                                    costS2[j] = tmp;
                                    instS2[j] = tmp_val;
                                    qosS2[j] = tm_qos;
                                    capacityS2[j] = tmp_capacity;
                                }
                            }
                        }
                        aggQoS = aggQoS + qosS2[0];
                        if (init) {
                            total_cost = total_cost + costS2[0];
                            current_capacity_processing = capacityS2[0];
                            if (instS2[0].indexOf(',') != -1) {
                                String[] extStr = instS2[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS2[0].split("x");
                                processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                        } // total_cost = total_cost + costS1[0];
                        else {
                            if (proSI) {
                                if (!(current_capacity_processing == capacityS2[0])) {
                                    if (instS2[0].indexOf(',') != -1) {
                                        String[] extStr = instS2[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (processing_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(processing_resources)) {
                                                    current_capacity_processing = current_capacity_processing - capacityS2[0];

                                                    if (processing_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                    }
                                                    if (processing_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                    }
                                                    if (processing_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                    }

                                                } else {
                                                    processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (processing_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                    }
                                                    if (processing_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                    }
                                                    if (processing_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                    }
                                                }
                                            } else if (processing_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_processing = current_capacity_processing - capacityS2[0];

                                                processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                            } else {
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                            }
                                        }
                                    } else {
                                        String[] split = instS2[0].split("x");
                                        if (processing_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            processing_resources.put(split[1], processing_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(processing_resources)) {
                                                //previous_Period_total_cost = previous_Period_total_cost + costS2[0];
                                                current_capacity_processing = current_capacity_processing - capacityS2[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS2[0];

                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }

                                            } else {
                                                processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                            }
                                        } else if (processing_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_processing = current_capacity_processing - capacityS2[0];
                                            //previous_Period_total_cost = previous_Period_total_cost - costS2[0];
                                            processing_resources.put(split[1], processing_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (processing_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                            }
                                            if (processing_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                            }
                                            if (processing_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                            }

                                        } else {
                                            if (processing_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                            }
                                            if (processing_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                            }
                                            if (processing_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                            }
                                            //previous_Period_total_cost = previous_Period_total_cost + costS2[0];
                                        }
                                    }
                                } else if (current_capacity_processing == capacityS2[0]) {
                                    //current_capacity_processing = capacityS2[0];
                                    if (processing_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                    }
                                    if (processing_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                    }
                                    if (processing_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                    }
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } //scale -out processing
                            else if (proSO) {

                                if (instS2[0].indexOf(',') != -1) {
                                    String[] extStr = instS2[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS2[0].split("x");
                                    processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (processing_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                }
                                if (processing_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                }
                                if (processing_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                }
                                current_capacity_processing = current_capacity_processing + capacityS2[0];

                            }
                        }

                        System.out.println("Instances required for processing layer: " + instS2[0] + "\n");
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
                                    soln3.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost3.add(z * price[i]);
                                    qos3.add(S3_Q[i][j]);
                                    totCapacity3.add(z * S3_W[i][j]);
                                }
                            }
                        }
                        if (soln3.isEmpty()) {
                            return 0.0F;
                        }
                        String[] instS3 = soln3.toArray(new String[0]);
                        Float[] costS3 = totCost3.toArray(new Float[0]);
                        Integer[] qosS3 = qos3.toArray(new Integer[0]);
                        Integer[] capacityS3 = totCapacity3.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS3.length; i++) {
                            for (int j = i + 1; j < instS3.length; j++) {
                                if (costS3[j] < costS3[i]) {
                                    tmp = costS3[i];
                                    tmp_val = instS3[i];
                                    tm_qos = qosS3[i];
                                    tmp_capacity = capacityS3[i];
                                    costS3[i] = costS3[j];
                                    instS3[i] = instS3[j];
                                    qosS3[i] = qosS3[j];
                                    costS3[j] = tmp;
                                    instS3[j] = tmp_val;
                                    qosS3[j] = tm_qos;
                                    capacityS3[j] = tmp_capacity;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS3[0];
                        // total_cost = total_cost + costS3[0];
                        if (init) {
                            total_cost = total_cost + costS3[0];
                            current_capacity_storage = capacityS3[0];
                            if (instS3[0].indexOf(',') != -1) {
                                String[] extStr = instS3[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS3[0].split("x");
                                storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                        } // total_cost = total_cost + costS1[0];
                        else {
                            if (stoSI) {
                                if (!(current_capacity_storage == capacityS3[0])) {
                                    if (instS3[0].indexOf(',') != -1) {
                                        String[] extStr = instS3[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (storage_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(storage_resources)) {
                                                    current_capacity_storage = current_capacity_storage - capacityS3[0];

                                                    if (storage_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                    }
                                                    if (storage_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                    }
                                                    if (storage_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                    }

                                                } else {
                                                    storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (storage_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                    }
                                                    if (storage_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                    }
                                                    if (storage_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                    }
                                                }
                                            } else if (storage_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_storage = current_capacity_storage - capacityS3[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS3[0];
                                                storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                            } else {
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                            }
                                        }
                                    } else {
                                        String[] split = instS3[0].split("x");
                                        if (storage_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            storage_resources.put(split[1], storage_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(storage_resources)) {
                                                current_capacity_storage = current_capacity_storage - capacityS3[0];

                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }

                                            } else {
                                                storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                            }
                                        } else if (storage_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_storage = current_capacity_storage - capacityS3[0];
                                            //previous_Period_total_cost = previous_Period_total_cost - costS3[0];
                                            storage_resources.put(split[1], storage_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (storage_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                            }
                                            if (storage_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                            }
                                            if (storage_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                            }
                                        } else {
                                            if (storage_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                            }
                                            if (storage_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                            }
                                            if (storage_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                            }
                                        }
                                    }
                                } else if (current_capacity_storage == capacityS3[0]) {
                                    //current_capacity_storage = capacityS3[0];
                                    if (storage_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                    }
                                    if (storage_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                    }
                                    if (storage_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                    }
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } else if (stoSO) {

                                if (instS3[0].indexOf(',') != -1) {
                                    String[] extStr = instS3[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS3[0].split("x");
                                    storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (storage_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                }
                                if (storage_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                }
                                if (storage_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                }
                                // previous_Period_total_cost = previous_Period_total_cost + costS3[0];
                                current_capacity_storage = current_capacity_storage + capacityS3[0];

                            }
                        }
                        //current_capacity_storage = capacityS3[0];
                        System.out.println("Instances required for storage layer: " + instS3[0] + "\n");
                        break;
                    }
                    default:
                        break;
                }

            }
            System.out.println("Total cost: " + String.valueOf(total_cost));
            System.out.println("total end-to-end QoS: " + String.valueOf(aggQoS));
            System.out.println("Total cost: " + String.valueOf(previous_Period_total_cost));

        } catch (NumberFormatException ex) {
            System.out.println("Error:" + ex.getMessage());

        }
        if (init) {
            previous_Period_total_cost = total_cost;
            return total_cost;
        } else {
            return previous_Period_total_cost;
        }

    }

    public static float DCOStratgey(int w1, int w2, int w3, int e2eQoS, int deltaA, int deltaB, Float[] price, boolean init, boolean ingSO, boolean ingSI, boolean proSO, boolean proSI, boolean stoSO, boolean stoSI) throws IOException {
        @SuppressWarnings("UnusedAssignment")
        float total_cost = 0.0F;

        try {
            // int[][] S1_W = new int[][]{{1, 10, 30, 31, 31, 31}, {1, 10, 30, 50, 51, 51}, {1, 10, 30, 50, 80, 90}}; //aws - t2- micro, small, medium
            int[][] S1_W = new int[][]{{1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000},
            {1000, 5000, 10000, 20000, 30000}};
            //int[][] S2_W = new int[][]{{1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 5}, {1, 2, 3, 4, 5, 6}};
            int[][] S2_W = new int[][]{{1000, 5000, 10000, 20000, 30000, 40000, 50000, 50001, 50001, 50001, 50001, 50001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 60001, 60001, 60001, 60001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000}};
            // int[][] S3_W = new int[][]{{1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 18, 19, 19}, {1, 5, 10, 15, 20, 25, 30}};
            int[][] S3_W = new int[][]{{1000, 5000, 10000, 10001, 10001, 10001, 10001},
            {1000, 5000, 10000, 20000, 30000, 30001, 30001},
            {1000, 5000, 10000, 20000, 30000, 40000, 50000}};
            //int[][] S1_Q = new int[][]{{73, 75, 79, 5000, 5000, 5000}, {28, 35, 44, 45, 5000, 5000}, {16, 25, 31, 33, 52, 60}};
            int[][] S1_Q = new int[][]{{8, 11, 14, 22, 29},
            {7, 9, 10, 13, 18},
            {5, 7, 8, 10, 12}};
            //int[][] S2_Q = new int[][]{{300, 400, 700, 900, 5000, 5000}, {300, 400, 700, 900, 5000, 5000}, {200, 400, 700, 800, 850, 900}};
            int[][] S2_Q = new int[][]{{45, 90, 200, 400, 600, 800, 900, 5000, 5000, 5000, 5000, 5000},
            {30, 60, 200, 400, 500, 600, 800, 900, 5000, 5000, 5000, 5000},
            {25, 50, 100, 200, 250, 350, 450, 500, 600, 700, 800, 900}};
            //int[][] S3_Q = new int[][]{{15, 17, 20, 25, 51, 5000, 5000}, {15, 16, 18, 25, 35, 5000, 5000}, {15, 15, 16, 17, 20, 25, 40}};
            int[][] S3_Q = new int[][]{{2, 20, 260, 5000, 5000, 5000, 5000},
            {1, 2, 6, 40, 140, 5000, 5000},
            {1, 1, 2, 4, 9, 25, 120}};
            int delta_A = deltaA;//GetPropertyFileKeyValues.getDeltaMinAQoS(); //based on the minimum latency required in layer 2
            int delta_B = deltaB;//GetPropertyFileKeyValues.getDeltaMinBQoS(); //based on minimum latency required in layer 3

            int aggQoS = delta_A + delta_B;
            List<String> soln = new ArrayList<>();
            List<Float> totCost = new ArrayList<>();
            List<Integer> qos = new ArrayList<>();
            List<Integer> totCapacity = new ArrayList<>();

            List<String> soln2 = new ArrayList<>();
            List<Float> totCost2 = new ArrayList<>();
            List<Integer> qos2 = new ArrayList<>();
            List<Integer> totCapacity2 = new ArrayList<>();

            List<String> soln3 = new ArrayList<>();
            List<Float> totCost3 = new ArrayList<>();
            List<Integer> qos3 = new ArrayList<>();
            List<Integer> totCapacity3 = new ArrayList<>();
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
                                    soln.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost.add(z * price[i]);
                                    qos.add(S1_Q[i][j]);
                                    totCapacity.add(z * S1_W[i][j]);
                                }
                            }
                        }
                        // new code added
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
                                        totCapacity.add((x * S1_W[0][kk]) + (y * S1_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        //new code end - for heterogeneous
                        if (soln.isEmpty()) {
                            System.out.println("no solution exists. please choose different QoS or different min thresholds");
                            return 0.0F;
                        }
                        String[] instS1 = soln.toArray(new String[0]);
                        Float[] costS1 = totCost.toArray(new Float[0]);
                        Integer[] qosS1 = qos.toArray(new Integer[0]);
                        Integer[] capacityS1 = totCapacity.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS1.length; i++) {
                            for (int j = i + 1; j < instS1.length; j++) {
                                if (costS1[j] < costS1[i]) {
                                    tmp = costS1[i];
                                    tmp_val = instS1[i];
                                    tm_qos = qosS1[i];
                                    tmp_capacity = capacityS1[i];
                                    costS1[i] = costS1[j];
                                    instS1[i] = instS1[j];
                                    qosS1[i] = qosS1[j];
                                    costS1[j] = tmp;
                                    instS1[j] = tmp_val;
                                    qosS1[j] = tm_qos;
                                    capacityS1[j] = tmp_capacity;
                                }
                            }
                        }
                        aggQoS = aggQoS + qosS1[0];
                        if (init) {
                            total_cost = total_cost + costS1[0];
                            current_capacity_ingestion = capacityS1[0];
                            //current_allocation_ingestion = instS1[0];
                            if (instS1[0].indexOf(',') != -1) {
                                String[] extStr = instS1[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS1[0].split("x");
                                ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                            insertCurrentRACSV(ingestion_resources, ingestionRA);
                        } else {
                            if (ingSI) {
                                if (!(current_capacity_ingestion == capacityS1[0])) {
                                    if (instS1[0].indexOf(',') != -1) {
                                        String[] extStr = instS1[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (ingestion_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(ingestion_resources)) {
                                                    current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                    if (ingestion_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                    }
                                                    if (ingestion_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                    }
                                                    if (ingestion_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(ingestion_resources, ingestionRA);

                                                } else {
                                                    ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (ingestion_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                    }
                                                    if (ingestion_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                    }
                                                    if (ingestion_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(ingestion_resources, ingestionRA);
                                                }
                                            } else if (ingestion_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                                ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(ingestion_resources, ingestionRA);

                                            } else {
                                                // previous_Period_total_cost = previous_Period_total_cost + costS1[0];
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(ingestion_resources, ingestionRA);
                                            }
                                        }
                                    } else {
                                        String[] split = instS1[0].split("x");
                                        if (ingestion_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            ingestion_resources.put(split[1], ingestion_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(ingestion_resources)) {

                                                current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];

                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(ingestion_resources, ingestionRA);

                                            } else {
                                                ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (ingestion_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                                }
                                                if (ingestion_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                                }
                                                if (ingestion_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(ingestion_resources, ingestionRA);
                                            }
                                        } else if (ingestion_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                            // previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                            ingestion_resources.put(split[1], ingestion_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (ingestion_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                            }
                                            if (ingestion_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                            }
                                            if (ingestion_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(ingestion_resources, ingestionRA);

                                        } else {
                                            if (ingestion_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                            }
                                            if (ingestion_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                            }
                                            if (ingestion_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(ingestion_resources, ingestionRA);
                                        }
                                    }
                                } else if (current_capacity_ingestion == capacityS1[0]) {
                                    //current_capacity_ingestion = capacityS1[0];
                                    if (ingestion_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                    }
                                    if (ingestion_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                    }
                                    if (ingestion_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                    }
                                    insertCurrentRACSV(ingestion_resources, ingestionRA);
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } else if (ingSO) {

                                if (instS1[0].indexOf(',') != -1) {
                                    String[] extStr = instS1[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS1[0].split("x");
                                    ingestion_resources.put(split[1], ingestion_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (ingestion_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * ingestion_resources.get("m2.small");
                                }
                                if (ingestion_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * ingestion_resources.get("m2.medium");
                                }
                                if (ingestion_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * ingestion_resources.get("m2.large");
                                }
                                //previous_Period_total_cost = previous_Period_total_cost + costS1[0];
                                current_capacity_ingestion = current_capacity_ingestion + capacityS1[0];
                                insertCurrentRACSV(ingestion_resources, ingestionRA);

                            } else {
                                System.out.println("code reach here!");
                            }
                        }
                        System.out.println("Instances required for Kafka: " + instS1[0] + "\n");

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
                                    soln2.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost2.add(z * price[i]);
                                    qos2.add(S2_Q[i][j]);
                                    totCapacity2.add(z * S2_W[i][j]);
                                }

                            }
                        }
                        if (soln2.isEmpty()) {
                            return 0.0F;
                        }
                        //new code starts here

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
                                        totCapacity2.add((x * S2_W[0][kk]) + (y * S2_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        //new code end
                        String[] instS2 = soln2.toArray(new String[0]);
                        Float[] costS2 = totCost2.toArray(new Float[0]);
                        Integer[] qosS2 = qos2.toArray(new Integer[0]);
                        Integer[] capacityS2 = totCapacity2.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS2.length; i++) {
                            for (int j = i + 1; j < instS2.length; j++) {
                                if (costS2[j] < costS2[i]) {
                                    tmp = costS2[i];
                                    tmp_val = instS2[i];
                                    tm_qos = qosS2[i];
                                    tmp_capacity = capacityS2[i];
                                    costS2[i] = costS2[j];
                                    instS2[i] = instS2[j];
                                    qosS2[i] = qosS2[j];
                                    costS2[j] = tmp;
                                    instS2[j] = tmp_val;
                                    qosS2[j] = tm_qos;
                                    capacityS2[j] = tmp_capacity;
                                }
                            }
                        }
                        aggQoS = aggQoS + qosS2[0];
                        if (init) {
                            total_cost = total_cost + costS2[0];
                            current_capacity_processing = capacityS2[0];
                            if (instS2[0].indexOf(',') != -1) {
                                String[] extStr = instS2[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS2[0].split("x");
                                processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                            insertCurrentRACSV(processing_resources, processingRA);
                        } // total_cost = total_cost + costS1[0];
                        else {
                            if (proSI) {
                                if (!(current_capacity_processing == capacityS2[0])) {
                                    if (instS2[0].indexOf(',') != -1) {
                                        String[] extStr = instS2[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (processing_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(processing_resources)) {
                                                    current_capacity_processing = current_capacity_processing - capacityS2[0];

                                                    if (processing_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                    }
                                                    if (processing_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                    }
                                                    if (processing_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(processing_resources, processingRA);

                                                } else {
                                                    processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (processing_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                    }
                                                    if (processing_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                    }
                                                    if (processing_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(processing_resources, processingRA);
                                                }
                                            } else if (processing_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_processing = current_capacity_processing - capacityS2[0];

                                                processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(processing_resources, processingRA);
                                            } else {
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(processing_resources, processingRA);
                                            }
                                        }
                                    } else {
                                        String[] split = instS2[0].split("x");
                                        if (processing_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            processing_resources.put(split[1], processing_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(processing_resources)) {
                                                //previous_Period_total_cost = previous_Period_total_cost + costS2[0];
                                                current_capacity_processing = current_capacity_processing - capacityS2[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS2[0];

                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(processing_resources, processingRA);

                                            } else {
                                                processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (processing_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                                }
                                                if (processing_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                                }
                                                if (processing_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(processing_resources, processingRA);
                                            }
                                        } else if (processing_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_processing = current_capacity_processing - capacityS2[0];
                                            //previous_Period_total_cost = previous_Period_total_cost - costS2[0];
                                            processing_resources.put(split[1], processing_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (processing_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                            }
                                            if (processing_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                            }
                                            if (processing_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(processing_resources, processingRA);

                                        } else {
                                            if (processing_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                            }
                                            if (processing_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                            }
                                            if (processing_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(processing_resources, processingRA);
                                            //previous_Period_total_cost = previous_Period_total_cost + costS2[0];
                                        }
                                    }
                                } else if (current_capacity_processing == capacityS2[0]) {
                                    //current_capacity_processing = capacityS2[0];
                                    if (processing_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                    }
                                    if (processing_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                    }
                                    if (processing_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                    }
                                    insertCurrentRACSV(processing_resources, processingRA);
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } //scale -out processing
                            else if (proSO) {

                                if (instS2[0].indexOf(',') != -1) {
                                    String[] extStr = instS2[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        processing_resources.put(innerSplit[1], processing_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS2[0].split("x");
                                    processing_resources.put(split[1], processing_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (processing_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * processing_resources.get("m2.small");
                                }
                                if (processing_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * processing_resources.get("m2.medium");
                                }
                                if (processing_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * processing_resources.get("m2.large");
                                }
                                current_capacity_processing = current_capacity_processing + capacityS2[0];
                                insertCurrentRACSV(processing_resources, processingRA);
                            }
                        }

                        System.out.println("Instances required for processing layer: " + instS2[0] + "\n");
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
                                    soln3.add(String.valueOf(z) + 'x' + instance_type);
                                    totCost3.add(z * price[i]);
                                    qos3.add(S3_Q[i][j]);
                                    totCapacity3.add(z * S3_W[i][j]);
                                }
                            }
                        }
                        if (soln3.isEmpty()) {
                            return 0.0F;
                        }
                        //new code
                        // new code added
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
                                        totCapacity3.add((x * S3_W[0][kk]) + (y * S3_W[i + 1][j + 1]));
                                    }
                                }
                            }
                            kk++;
                        }
                        //new code end - for heterogeneous
                        String[] instS3 = soln3.toArray(new String[0]);
                        Float[] costS3 = totCost3.toArray(new Float[0]);
                        Integer[] qosS3 = qos3.toArray(new Integer[0]);
                        Integer[] capacityS3 = totCapacity3.toArray(new Integer[0]);
                        float tmp = 0.0F;
                        String tmp_val = null;
                        int tm_qos = 0;
                        int tmp_capacity = 0;
                        for (int i = 0; i < instS3.length; i++) {
                            for (int j = i + 1; j < instS3.length; j++) {
                                if (costS3[j] < costS3[i]) {
                                    tmp = costS3[i];
                                    tmp_val = instS3[i];
                                    tm_qos = qosS3[i];
                                    tmp_capacity = capacityS3[i];
                                    costS3[i] = costS3[j];
                                    instS3[i] = instS3[j];
                                    qosS3[i] = qosS3[j];
                                    costS3[j] = tmp;
                                    instS3[j] = tmp_val;
                                    qosS3[j] = tm_qos;
                                    capacityS3[j] = tmp_capacity;
                                }

                            }
                        }
                        aggQoS = aggQoS + qosS3[0];
                        // total_cost = total_cost + costS3[0];
                        if (init) {
                            total_cost = total_cost + costS3[0];
                            current_capacity_storage = capacityS3[0];
                            if (instS3[0].indexOf(',') != -1) {
                                String[] extStr = instS3[0].split(",");
                                for (String dt : extStr) {
                                    String[] innerSplit = dt.split("x");
                                    storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                }
                            } else {
                                String[] split = instS3[0].split("x");
                                storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                            }
                            insertCurrentRACSV(storage_resources, storageRA);
                        } // total_cost = total_cost + costS1[0];
                        else {
                            if (stoSI) {
                                if (!(current_capacity_storage == capacityS3[0])) {
                                    if (instS3[0].indexOf(',') != -1) {
                                        String[] extStr = instS3[0].split(",");
                                        for (String dt : extStr) {
                                            String[] innerSplit = dt.split("x");
                                            //ingestion_resources.put(innerSplit[1], ingestion_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                            if (storage_resources.get(innerSplit[1]) == Integer.parseInt(innerSplit[0])) {
                                                storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (checkForEmptyAllocation(storage_resources)) {
                                                    current_capacity_storage = current_capacity_storage - capacityS3[0];

                                                    if (storage_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                    }
                                                    if (storage_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                    }
                                                    if (storage_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(storage_resources, storageRA);

                                                } else {
                                                    storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                                    if (storage_resources.get("m2.small") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                    }
                                                    if (storage_resources.get("m2.medium") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                    }
                                                    if (storage_resources.get("m2.large") > 0) {
                                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                    }
                                                    insertCurrentRACSV(storage_resources, storageRA);
                                                }
                                            } else if (storage_resources.get(innerSplit[1]) > Integer.parseInt(innerSplit[0])) {
                                                current_capacity_storage = current_capacity_storage - capacityS3[0];
                                                //previous_Period_total_cost = previous_Period_total_cost - costS3[0];
                                                storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) - Integer.parseInt(innerSplit[0]));
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(storage_resources, storageRA);
                                            } else {
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(storage_resources, storageRA);
                                            }
                                        }
                                    } else {
                                        String[] split = instS3[0].split("x");
                                        if (storage_resources.get(split[1]) == Integer.parseInt(split[0])) {
                                            storage_resources.put(split[1], storage_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (checkForEmptyAllocation(storage_resources)) {
                                                current_capacity_storage = current_capacity_storage - capacityS3[0];

                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(storage_resources, storageRA);

                                            } else {
                                                storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                                                if (storage_resources.get("m2.small") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                                }
                                                if (storage_resources.get("m2.medium") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                                }
                                                if (storage_resources.get("m2.large") > 0) {
                                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                                }
                                                insertCurrentRACSV(storage_resources, storageRA);
                                            }
                                        } else if (storage_resources.get(split[1]) > Integer.parseInt(split[0])) {
                                            current_capacity_storage = current_capacity_storage - capacityS3[0];
                                            //previous_Period_total_cost = previous_Period_total_cost - costS3[0];
                                            storage_resources.put(split[1], storage_resources.get(split[1]) - Integer.parseInt(split[0]));
                                            if (storage_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                            }
                                            if (storage_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                            }
                                            if (storage_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(storage_resources, storageRA);
                                        } else {
                                            if (storage_resources.get("m2.small") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                            }
                                            if (storage_resources.get("m2.medium") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                            }
                                            if (storage_resources.get("m2.large") > 0) {
                                                previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                            }
                                            insertCurrentRACSV(storage_resources, storageRA);
                                        }
                                    }
                                } else if (current_capacity_storage == capacityS3[0]) {
                                    //current_capacity_storage = capacityS3[0];
                                    if (storage_resources.get("m2.small") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                    }
                                    if (storage_resources.get("m2.medium") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                    }
                                    if (storage_resources.get("m2.large") > 0) {
                                        previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                    }
                                    insertCurrentRACSV(storage_resources, storageRA);
                                } else {
                                    //current_capacity_ingestion = current_capacity_ingestion - capacityS1[0];
                                    //previous_Period_total_cost = previous_Period_total_cost - costS1[0];
                                    //current_allocation_ingestion = current_allocation_ingestion;
                                }

                            } else if (stoSO) {

                                if (instS3[0].indexOf(',') != -1) {
                                    String[] extStr = instS3[0].split(",");
                                    for (String dt : extStr) {
                                        String[] innerSplit = dt.split("x");
                                        storage_resources.put(innerSplit[1], storage_resources.get(innerSplit[1]) + Integer.parseInt(innerSplit[0]));
                                    }
                                } else {
                                    String[] split = instS3[0].split("x");
                                    storage_resources.put(split[1], storage_resources.get(split[1]) + Integer.parseInt(split[0]));
                                }
                                if (storage_resources.get("m2.small") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[0] * storage_resources.get("m2.small");
                                }
                                if (storage_resources.get("m2.medium") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[1] * storage_resources.get("m2.medium");
                                }
                                if (storage_resources.get("m2.large") > 0) {
                                    previous_Period_total_cost = previous_Period_total_cost + price[2] * storage_resources.get("m2.large");
                                }
                                // previous_Period_total_cost = previous_Period_total_cost + costS3[0];
                                current_capacity_storage = current_capacity_storage + capacityS3[0];
                                insertCurrentRACSV(storage_resources, storageRA);
                            }
                        }
                        //current_capacity_storage = capacityS3[0];
                        System.out.println("Instances required for storage layer: " + instS3[0] + "\n");
                        break;
                    }
                    default:
                        break;
                }

            }
            System.out.println("Total cost: " + String.valueOf(total_cost));
            System.out.println("total end-to-end QoS: " + String.valueOf(aggQoS));
            System.out.println("Total cost: " + String.valueOf(previous_Period_total_cost));


        } catch (NumberFormatException ex) {
            System.out.println("Error:" + ex.getMessage());

        }
        if (init) {
            previous_Period_total_cost = total_cost;
            return total_cost;
        } else {
            return previous_Period_total_cost;
        }

    }

    public static void main(String args[]) throws IOException {
        Float[] price = {0.0385F, 0.0928F, 0.1856F}; //OD
        float totalCost = 0.0F;
        //Float[] price = {0.032F, 0.065F, 0.13F}; //SB - 6hrs
        // Float[] price = {0.032F, 0.0928F, 0.1856F}; //mixed - first spot block, 2 -ODs
        //Float[] price = {0.032F, 0.065F, 0.1856F}; // mixed - 2 SB , 1 OD
        // Float[] price = {0.0385F, 0.065F, 0.13F}; //mixed - 1OD, 2 SB
        //Float[] price = {0.0385F, 0.0928F}; //OD - homogeneous across all layers
        ingestion_resources.put("m2.small", 0);
        ingestion_resources.put("m2.medium", 0);
        ingestion_resources.put("m2.large", 0);
        processing_resources.put("m2.small", 0);
        processing_resources.put("m2.medium", 0);
        processing_resources.put("m2.large", 0);
        storage_resources.put("m2.small", 0);
        storage_resources.put("m2.medium", 0);
        storage_resources.put("m2.large", 0);
        try {
            ingestionRA = new FileWriter("C:\\Code\\ingestion.csv");
            processingRA = new FileWriter("C:\\Code\\processing.csv");
            storageRA = new FileWriter("C:\\Code\\storage.csv");
            ingestionRA.append("small");
            ingestionRA.append(",");
            ingestionRA.append("medium");
            ingestionRA.append(",");
            ingestionRA.append("large");
            ingestionRA.append("\n");
            processingRA.append("small");
            processingRA.append(",");
            processingRA.append("medium");
            processingRA.append(",");
            processingRA.append("large");
            processingRA.append("\n");
            storageRA.append("small");
            storageRA.append(",");
            storageRA.append("medium");
            storageRA.append(",");
            storageRA.append("large");
            storageRA.append("\n");
        } catch (IOException ex) {
            Logger.getLogger(ResourceOptimizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter end-to-end latency requirement:");
        int e2eQoS = scanner.nextInt();
        System.out.println("Enter deltaA:");
        int deltaA = scanner.nextInt();
        System.out.println("Enter deltaB:");
        int deltaB = scanner.nextInt();
        current_capacity_processing = current_capacity_ingestion = current_capacity_storage = 0;
        current_allocation_ingestion = current_allocation_processing = current_allocation_storage = null;
        previous_Period_total_cost = 0.0F;
        boolean ingSO, ingSI, proSO, proSI, stoSO, stoSI;
        int[] workloads = read(); //read workload data from excel file - 31 days NYC taxi data.
        int w1, w2, w3;
        System.out.println("Select stratgey:(DCO/FCO)");
        String strategy = scanner.next();
        if (null == strategy) {
            System.out.println("Please select an appropriate scaling strategy option:");
        } else {
            switch (strategy) {
                case "DCO":
//        for (int i = 0; i < price.length; i++) {
//            System.out.println("Enter price for instance0" + (i + 1) + ":");
//            price[i] = scanner.nextFloat();
//        }
//        do {
//
//            System.out.println("Enter workload for ingestion layer:");
//            int w1 = scanner.nextInt();
//            System.out.println("Enter workload for processing layer");
//            int w2 = scanner.nextInt();
//            System.out.println("Enter workload for storage layer");
//            int w3 = scanner.nextInt();
//            getResourceAllocation(w1, w2, w3, e2eQoS, deltaA, deltaB, price);
//            //MKPSolver.MKPSolver(w1, w2, w3, e2eQoS, deltaA, deltaB, price);
//            System.out.println("Do you want to continue?");
//            choice = scanner.next();
//        } while ("yes".equals(choice) || "y".equals(choice));
                case "dco":
                    for (int i = 0; i < workloads.length; i++) {
                        if (i == 0) {
                            w3 = (int) (0.25 * workloads[i]);
                            DCOStratgey(workloads[i], workloads[i], w3, e2eQoS, deltaA, deltaB, price, true, false, false, false, false, false, false);
                            //DCOHomogeneous(workloads[i], workloads[i], w3, e2eQoS, deltaA, deltaB, price, true, false, false, false, false, false, false);
                        } else {
                            if (current_capacity_ingestion > workloads[i]) {
                                ingSI = true;
                                ingSO = false;
                                w1 = current_capacity_ingestion - workloads[i];
                            } else if (current_capacity_ingestion < workloads[i]) {
                                ingSO = true;
                                ingSI = false;
                                w1 = workloads[i] - current_capacity_ingestion;
                            } else {
                                ingSO = ingSI = false;
                                w1 = 0;
                            }
                            if (current_capacity_processing > workloads[i]) {
                                proSI = true;
                                proSO = false;
                                w2 = current_capacity_processing - workloads[i];

                            } else if (current_capacity_processing < workloads[i]) {
                                proSO = true;
                                proSI = false;
                                w2 = workloads[i] - current_capacity_processing;
                            } else {
                                proSO = proSI = false;
                                w2 = 0;
                            }
                            if (current_capacity_storage > (int) (0.25 * workloads[i])) {
                                stoSI = true;
                                stoSO = false;
                                w3 = current_capacity_storage - (int) (0.25 * workloads[i]);
                            } else if (current_capacity_storage < (int) (0.25 * workloads[i])) {
                                stoSO = true;
                                stoSI = false;
                                w3 = (int) (0.25 * workloads[i]) - current_capacity_storage;

                            } else {
                                stoSO = stoSI = false;
                                w3 = 0;
                            }
                            DCOStratgey(w1, w2, w3, e2eQoS, deltaA, deltaB, price, false, ingSO, ingSI, proSO, proSI, stoSO, stoSI);
                            //DCOHomogeneous(w1, w2, w3, e2eQoS, deltaA, deltaB, price, false, ingSO, ingSI, proSO, proSI, stoSO, stoSI);
                        }
                    }
                    System.out.println("Total cost of RA using DCO:" + previous_Period_total_cost);
                    break;
                case "FCO":
                case "fco":
                    for (int i = 0; i < workloads.length; i++) {
                        w3 = (int) (0.25 * workloads[i]);
                        totalCost = totalCost + HeterogeneousFCO.FCOStrategy(workloads[i], workloads[i], w3, e2eQoS, deltaA, deltaB, price);
                        //totalCost = totalCost + Homogeneous.HomogeneousFCO(workloads[i], workloads[i], w3, e2eQoS, deltaA, deltaB, price);
                    }
                    System.out.println("Total cost of RA using FCO:" + totalCost);
                    break;
                default:
                    System.out.println("Please select an appropriate scaling strategy option:");
                    break;
            }
        }
            ingestionRA.flush();
            ingestionRA.close();
            processingRA.flush();
            processingRA.close();
            storageRA.flush();
            storageRA.close();
    }
}
