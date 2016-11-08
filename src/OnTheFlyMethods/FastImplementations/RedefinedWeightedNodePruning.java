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

import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import DataStructures.AbstractBlock;
import DataStructures.BilateralBlock;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;
import Utilities.Converter;
import Utilities.ExecuteBlockComparisons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RedefinedWeightedNodePruning extends MetaBlocking.EnhancedMetaBlocking.FastImplementations.RedefinedWeightedNodePruning {

    protected double totalComparisons;
    protected final AbstractDuplicatePropagation duplicatePropagation;

    public RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme) {
        this(adp, "Redefined Weighted Node Pruning ("+scheme+")", scheme);
    }

    public RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme, ThresholdWeightingScheme threshold_type) {
        this(adp, "Redefined Weighted Node Pruning ("+scheme+")", scheme, threshold_type);
    }

    public RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme, ThresholdWeightingScheme threshold_type, double totalBlocks) {
        this(adp, "Redefined Weighted Node Pruning ("+scheme+")", scheme, threshold_type);
        this.totalBlocks = totalBlocks;
    }

    public RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, WeightingScheme scheme, boolean t) {
        this(adp, "Redefined Weighted Node Pruning ("+scheme+")", scheme, t);
    }

    protected RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, String description, WeightingScheme scheme) {
        super(description, scheme);
        duplicatePropagation = adp;
        duplicatePropagation.resetDuplicates();
        totalComparisons = 0;
    }

    protected RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, String description, WeightingScheme scheme, ThresholdWeightingScheme threshold_type) {
        super(description, scheme, threshold_type);
        duplicatePropagation = adp;
        duplicatePropagation.resetDuplicates();
        totalComparisons = 0;
    }

    protected RedefinedWeightedNodePruning(AbstractDuplicatePropagation adp, String description, WeightingScheme scheme, boolean t) {
        super(description, scheme, t);
        duplicatePropagation = adp;
        duplicatePropagation.resetDuplicates();
        totalComparisons = 0;
    }


    public double[] getPerformance() {
        double[] metrics = new double[3];
        System.out.println(duplicatePropagation.getNoOfDuplicates() +"        "+  totalComparisons);
        metrics[0] = duplicatePropagation.getNoOfDuplicates() / ((double) duplicatePropagation.getExistingDuplicates()); //PC
        metrics[1] = duplicatePropagation.getNoOfDuplicates() / totalComparisons; //PQ
        metrics[2] = totalComparisons;
        return metrics;
    }

    @Override
    protected void verifyValidEntities(int entityId, List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc) {
    	int index;
    	retainedNeighbors.clear();
        if (!cleanCleanER) {
            for (int neighborId : validEntities) {
                if (isValidComparison(entityId, neighborId,ebc)) {
                    totalComparisons++;
                    duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
                }
            }
        } else {
            if (entityId < datasetLimit) {
            	//Iterator<Integer> temp = validEntitiesB.iterator();
                for (int neighborId : validEntities) {
                	// index=temp.next();
                	 if (isValidComparison(entityId, neighborId,ebc)) {
                        totalComparisons++;
                        duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
                        retainedNeighbors.add(neighborId - datasetLimit);
                    }
                }
                BilateralBlock bBlock = new BilateralBlock(entityId, retainedNeighbors);
               // addDecomposedBlock(entityId, retainedNeighbors, newBlocks);
            } else {
                for (int neighborId : validEntities) {
                    if (isValidComparison(entityId, neighborId,ebc)) {
                        totalComparisons++;
                        duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
                        
                        if(!duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId)) )
                        	System.out.println("weight--" );
                    }
                }
            }
        }
    }
}
