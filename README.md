# ResourceOptimizer
DPP resource optimizer based on FCO and DCO optimization strategies.

A custom implementation of Resource Optimizer component as part of the AuraEN application that supports to compute for cost optimal resource allocation for services in a data 
pipeline.

Two strategies supported: Full capacity optimization (FCO) and delta capacity optimization (DCO) under different resource and contract combinations.
Input required:- Cloud pricing for chosen instance types.
               - Sustainable QoS profile for all instance types used for each service - in the form of 2D array.
               - End-to-end QoS constraint.
               - QoS thresholds for processing and ingestion service (based on the e2e QoS and QoS profile data for all services).
               
  Build: Maven build java project.
