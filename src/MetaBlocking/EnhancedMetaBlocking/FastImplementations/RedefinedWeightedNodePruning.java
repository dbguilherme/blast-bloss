package MetaBlocking.EnhancedMetaBlocking.FastImplementations;

import DataStructures.AbstractBlock;
import MetaBlocking.FastImplementations.WeightedNodePruning;
import Utilities.ExecuteBlockComparisons;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;

import java.util.List;

/**
 * @author stravanni
 */
public class RedefinedWeightedNodePruning extends WeightedNodePruning {

    protected double[] averageWeight;

    public RedefinedWeightedNodePruning(WeightingScheme scheme) {
        this("Redundancy Weighted Node Pruning (" + scheme + ")", scheme);
    }

    public RedefinedWeightedNodePruning(WeightingScheme scheme, ThresholdWeightingScheme threshold_type) {
        this("Redundancy Weighted Node Pruning (" + scheme + ")", scheme, threshold_type);
    }

    public RedefinedWeightedNodePruning(WeightingScheme scheme, boolean t) {
        this("Redundancy Weighted Node Pruning (" + scheme + ")", scheme, t);
    }

    protected RedefinedWeightedNodePruning(String description, WeightingScheme scheme) {
        super(description, scheme);
    }

    protected RedefinedWeightedNodePruning(String description, WeightingScheme scheme, boolean t) {
        super(description, scheme, t);
    }

    protected RedefinedWeightedNodePruning(String description, WeightingScheme scheme, ThresholdWeightingScheme threshld_type) {
        super(description, scheme, threshld_type);
    }

    protected boolean isValidComparison(int entityId, int neighborId, ExecuteBlockComparisons ebc) {
        double weight = getWeight(entityId, neighborId,ebc);
        boolean inNeighborhood1 = averageWeight[entityId] <= weight;
        boolean inNeighborhood2 = averageWeight[neighborId] <= weight;

        if (inNeighborhood1 || inNeighborhood2) {
            return entityId < neighborId;
        }
System.out.println("aquiii");
        return false;
    }

   // @Override
    protected void pruneEdges(List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc) {
        if (weightingScheme.equals(WeightingScheme.ARCS)) {
            for (int i = 0; i < noOfEntities; i++) {
                processArcsEntity(i);
                verifyValidEntities(i, newBlocks);
            }
        }
//        else if (weightingScheme.equals(WeightingScheme.CHI_ENTRO)) {
//            System.out.println("\n\n\n chi entro in redefined wnp\n\n\n");
//            for (int i = 0; i < noOfEntities; i++) {
//                processCHI_entro_Entity(i);
//                verifyValidEntities(i, newBlocks);
//            }
//        }
        else {
        	//newBlocks.clear();
        	
            for (int i = 0; i < noOfEntities; i++) {
                processEntity(i);
                verifyValidEntities(i, newBlocks,ebc);
            }
        }
        
    }

    @Override
    protected void setThreshold() {
        averageWeight = new double[noOfEntities];
        if (weightingScheme.equals(WeightingScheme.ARCS)) {
            for (int i = 0; i < noOfEntities; i++) {
                processArcsEntity(i);
                setThreshold(i);
                averageWeight[i] = threshold;
            }
        } else {
            for (int i = 0; i < noOfEntities; i++) {
                processEntity(i);
                setThreshold(i);
                averageWeight[i] = threshold;
            }
        }
        System.out.println("apagar");
    }

    //@Override
    protected void verifyValidEntities(int entityId, List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc) {
        retainedNeighbors.clear();
        if (!cleanCleanER) {
            for (int neighborId : validEntities) {
                if (isValidComparison(entityId, neighborId,ebc)) {
                    retainedNeighbors.add(neighborId);
                }
            }
            addDecomposedBlock(entityId, retainedNeighbors, newBlocks);
        } else {
            if (entityId < datasetLimit) {
                for (int neighborId : validEntities) {
                    if (isValidComparison(entityId, neighborId,ebc)) {
                        retainedNeighbors.add(neighborId - datasetLimit);
                    }
                }
                addDecomposedBlock(entityId, retainedNeighbors, newBlocks);
            } else {
                for (int neighborId : validEntities) {
                    if (isValidComparison(entityId, neighborId,ebc)) {
                        retainedNeighbors.add(neighborId);
                    }
                }
                addReversedDecomposedBlock(entityId - datasetLimit, retainedNeighbors, newBlocks);
            }
        }
    }
}
