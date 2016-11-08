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

package MetaBlocking.EnhancedMetaBlocking;

import DataStructures.AbstractBlock;
import DataStructures.UnilateralBlock;
import MetaBlocking.WeightedNodePruning;
import MetaBlocking.WeightingScheme;
import java.util.List;

/**
 *
 * @author gap2
 */

public class PartitionWeightedNodePruning extends WeightedNodePruning {
    
    public PartitionWeightedNodePruning(WeightingScheme scheme) {
        super("Partition Weighted Node Pruning", scheme);
    }

    @Override
    public void applyProcessing(List<AbstractBlock> blocks) {
        if (blocks.get(0) instanceof UnilateralBlock) {
            System.err.println("Partition Weighted Node Pruning does not apply to unilateral block collections!");
            System.exit(-1);
        }
        
        getStatistics(blocks);
        
        cleanCleanER = true;
        blocksArray = blocks.toArray(new AbstractBlock[blocks.size()]);
        blocks.clear();
        
        if (entityIndex.getValidEntities1() < entityIndex.getValidEntities2()) {
            //smaller partition
            processPartition(0, entityIndex.getDatasetLimit(), blocks);
        } else {
            processPartition(entityIndex.getDatasetLimit(), entityIndex.getNoOfEntities(), blocks);
        }
    }
}