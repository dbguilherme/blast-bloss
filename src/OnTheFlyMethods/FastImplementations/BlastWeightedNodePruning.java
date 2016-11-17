/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    Copyright (C) 2015 George Antony Papadakis (gpapadis@yahoo.gr)
 */
package OnTheFlyMethods.FastImplementations;

import javax.crypto.EncryptedPrivateKeyInfo;

import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;
import Utilities.ExecuteBlockComparisons;

public class BlastWeightedNodePruning extends RedefinedWeightedNodePruning {

    //private boolean threshold_reiprocal;
    //private ThresholdWeightingScheme threshold_type;
	AbstractDuplicatePropagation adp;

    public BlastWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme, ThresholdWeightingScheme threshold_type) {
        super(adp, "Reciprocal Weighted Node Pruning (" + scheme + ")", scheme, threshold_type);
        //this.threshold_type = threshold_type;
        //this.threshold_reiprocal = threshold_reiprocal;
    }

    public BlastWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme, ThresholdWeightingScheme threshold_type, double totalBlocks) {
        super(adp, "Reciprocal Weighted Node Pruning (" + scheme + ")", scheme, threshold_type);
        //this.threshold_type = threshold_type;
        //this.threshold_reiprocal = threshold_reiprocal;
        this.totalBlocks = totalBlocks;
        this.adp=adp;
    }

    int erro=0;
    int acerto=0;
  //  @Override
    protected boolean isValidComparison(int entityId, int neighborId, ExecuteBlockComparisons ebc) {
    	 double weight = getWeight(entityId, neighborId, ebc);
         boolean inNeighborhood1 = averageWeight[entityId] <= weight;
         boolean inNeighborhood2 = averageWeight[neighborId] <= weight;

         switch (threshold_type) {
             case AVG:
                 if (inNeighborhood1 && inNeighborhood2) {
                     return entityId < neighborId;
                 }
                 break;
             case AM2:
                 //System.out.println("AM2");
                 double th_12 = (averageWeight[entityId] + averageWeight[neighborId]) / 2;
                 //double th_12 = Math.sqrt(Math.pow(averageWeight[entityId], 2) + Math.pow(averageWeight[neighborId], 2)) / 2;

                 if (th_12 <= weight) {
                     //if (Math.max(averageWeight[entityId],averageWeight[neighborId]) <= weight) {
                     return entityId < neighborId;
                 }
                 break;
             case AM3:
//                 if ((averageWeight[entityId] + averageWeight[neighborId]) / 4 <= weight) {
//                     //if (Math.max(averageWeight[entityId],averageWeight[neighborId]) <= weight) {
//                     return entityId < neighborId;
//                 }
//                 break;
                 double th12 = Math.sqrt(Math.pow(averageWeight[entityId], 2) + Math.pow(averageWeight[neighborId], 2)) / 4;
                 if (th12 <= weight) {
                     //if (Math.max(averageWeight[entityId],averageWeight[neighborId]) <= weight) {
                     return entityId < neighborId;
                 }
                 break;
         }
         return false;
    }
}
