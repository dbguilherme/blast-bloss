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
package BlockProcessing;

import DataStructures.AbstractBlock;
import DataStructures.BilateralBlock;
import DataStructures.DecomposedBlock;
import DataStructures.FastEntityIndex;
import DataStructures.UnilateralBlock;
import Utilities.Converter;
import Utilities.ExecuteBlockComparisons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;

/**
 *
 * @author gap2
 */
public abstract class AbstractFastEfficiencyMethod extends AbstractEfficiencyMethod {

    protected boolean cleanCleanER;

    protected int datasetLimit;
    protected int noOfBlocks;
    protected int noOfEntities;

    protected FastEntityIndex entityIndex;
    protected BilateralBlock[] bBlocks;
    protected final Set<Integer> validEntities;
    protected final List<Integer> validEntitiesB;
    protected UnilateralBlock[] uBlocks;

    public AbstractFastEfficiencyMethod(String nm) {
        super(nm);
        validEntities = new HashSet<>();
        validEntitiesB = new ArrayList<>();
    }

    protected void addDecomposedBlock(int entityId, Collection<Integer> neighbors, List<AbstractBlock> newBlocks) {
        if (neighbors.isEmpty()) {
            return;
        }

        int[] entityIds1 = replicateId(entityId, neighbors.size());
        int[] entityIds2 = Converter.convertCollectionToArray(neighbors);
        newBlocks.add(new DecomposedBlock(cleanCleanER, entityIds1, entityIds2));
    }
    
    protected void addReversedDecomposedBlock(int entityId, Collection<Integer> neighbors, List<AbstractBlock> newBlocks) {
        if (neighbors.isEmpty()) {
            return;
        }

        int[] entityIds1 = Converter.convertCollectionToArray(neighbors);
        int[] entityIds2 = replicateId(entityId, neighbors.size());
        newBlocks.add(new DecomposedBlock(cleanCleanER, entityIds1, entityIds2));
    }
    
    protected abstract void applyMainProcessing(List<AbstractBlock> blocks, AbstractDuplicatePropagation adp, ExecuteBlockComparisons ebc);

    //@Override
    public void applyProcessing(List<AbstractBlock> blocks, AbstractDuplicatePropagation adp, ExecuteBlockComparisons ebc) {
        entityIndex = new FastEntityIndex(blocks);
        
        cleanCleanER = entityIndex.isCleanCleanER();
        datasetLimit = entityIndex.getDatasetLimit();
        noOfBlocks = blocks.size();
        noOfEntities = entityIndex.getNoOfEntities();
        bBlocks = entityIndex.getBilateralBlocks();
        uBlocks = entityIndex.getUnilateralBlocks();

        //blocks.clear();
        applyMainProcessing(blocks,adp,ebc);
        //blocks= bBlocks;
        System.out.println("bBlocks.length "+ bBlocks.length);
    }
    
    public void applyProcessing2(List<AbstractBlock> blocks, AbstractDuplicatePropagation adp, ExecuteBlockComparisons ebc) {
    	 applyMainProcessing(blocks,adp,ebc);
    }
    
    protected int[] replicateId(int entityId, int times) {
        int counter = 0;
        int[] array = new int[times];
        for (int i = 0; i < times; i++) {
            array[counter++] = entityId;
        }
        return array;
    }
}
