package MetaBlocking.FastImplementations;

import DataStructures.AbstractBlock;
import MetaBlocking.WeightingScheme;
import Utilities.ExecuteBlockComparisons;

import java.util.List;

/**
 * @author stravanni
 */
public class WeightedEdgePruning extends AbstractFastMetablocking {

    protected double noOfEdges;
    double max_min;
    int ii;

    public WeightedEdgePruning(WeightingScheme scheme) {
        this("Fast Weighted Edge Pruning (" + scheme + ")", scheme);
        max_min = Double.MAX_VALUE;
        ii = 0;
    }

    protected WeightedEdgePruning(String description, WeightingScheme scheme) {
        super(description, scheme);
        nodeCentric = false;
    }

    protected void processArcsEntity(int entityId) {
        validEntities.clear();
        final int[] associatedBlocks = entityIndex.getEntityBlocks(entityId, 0);
        if (associatedBlocks.length == 0) {
            return;
        }

        for (int blockIndex : associatedBlocks) {
            double blockComparisons = cleanCleanER ? bBlocks[blockIndex].getNoOfComparisons() : uBlocks[blockIndex].getNoOfComparisons();
            setNormalizedNeighborEntities(blockIndex, entityId);
            for (int neighborId : neighbors) {
                if (flags[neighborId] != entityId) {
                    counters[neighborId] = 0;
                    flags[neighborId] = entityId;
                }

                counters[neighborId] += 1 / blockComparisons;
                validEntities.add(neighborId);
            }
        }
    }

    protected void processArcs_entro_Entity(int entityId) {
        validEntities.clear();
        final int[] associatedBlocks = entityIndex.getEntityBlocks(entityId, 0);
        if (associatedBlocks.length == 0) {
            return;
        }

        for (int blockIndex : associatedBlocks) {
            double blockComparisons = cleanCleanER ? bBlocks[blockIndex].getNoOfComparisons() : uBlocks[blockIndex].getNoOfComparisons();
            setNormalizedNeighborEntities(blockIndex, entityId);
            for (int neighborId : neighbors) {
                if (flags[neighborId] != entityId) {
                    counters[neighborId] = 0;
                    flags[neighborId] = entityId;
                }

                counters[neighborId] += 1 / blockComparisons;
                counters_entro[neighborId] += entityIndex.getEntropyBlock(blockIndex);
                validEntities.add(neighborId);
            }
        }
    }

//    protected void processCHI_entro_Entity(int entityId) {
//        validEntities.clear();
//        final int[] associatedBlocks = entityIndex.getEntityBlocks(entityId, 0);
//        if (associatedBlocks.length == 0) {
//            return;
//        }
//
//        for (int blockIndex : associatedBlocks) {
//            //double blockComparisons = cleanCleanER ? bBlocks[blockIndex].getNoOfComparisons() : uBlocks[blockIndex].getNoOfComparisons();
//            setNormalizedNeighborEntities(blockIndex, entityId);
//            for (int neighborId : neighbors) {
//                if (flags[neighborId] != entityId) {
//                    counters[neighborId] = 0;
//                    counters_entro[neighborId] = 0;
//                    flags[neighborId] = entityId;
//                }
//
//                //counters[neighborId] += 1 / blockComparisons;
//                counters[neighborId]++;
//                counters_entro[neighborId] += entityIndex.getEntropyBlock(blockIndex);
//
//                validEntities.add(neighborId);
//            }
//        }
//    }

    protected void processEntity(int entityId) {
        validEntities.clear();
       // validEntitiesB.clear();
        
        for(int i=0; i< flags.length;i++){
        	 flags[i] = -1;
             counters[i] = 0;
             counters_entro[i] = 0;
                
       }
       
        
        final int[] associatedBlocks = entityIndex.getEntityBlocks(entityId, 0);
        if (associatedBlocks.length == 0) {
            return;
        }

        
        for (int blockIndex : associatedBlocks) {
            setNormalizedNeighborEntities(blockIndex, entityId);
            
            for (int neighborId : neighbors) {
            //	if(entityId==1178 && neighborId==2562)
             //   	System.out.println("ok");
                if (flags[neighborId] != entityId) {
                    counters[neighborId] = 0;
                    counters_entro[neighborId] = 0;
                    flags[neighborId] = entityId;
                }
               // if(neighborId==(4592+datasetLimit))
                //	System.out.println("ok");
                counters[neighborId]++;
                counters_entro[neighborId] += entityIndex.getEntropyBlock(blockIndex);
              // if(counters[neighborId]>5)
              //  System.out.println(counters[neighborId]+  " counters_entro: " + counters_entro[neighborId] + " " + neighborId);
                validEntities.add(neighborId);
                //validEntitiesB.add(blockIndex);
                		//if(entityId==1178 && neighborId==2562)
                        //	System.out.println("ok");
            }
        }
    }

   // @Override
    protected void pruneEdges(List<AbstractBlock> newBlocks) {
        int limit = cleanCleanER ? datasetLimit : noOfEntities;
        if (weightingScheme.equals(WeightingScheme.ARCS)) {
            for (int i = 0; i < limit; i++) {
                processArcsEntity(i);
                verifyValidEntities(i, newBlocks);
            }
        } else {
            for (int i = 0; i < limit; i++) {
                processEntity(i);
                verifyValidEntities(i, newBlocks);
            }
        }
    }

    @Override
    protected void setThreshold() {
        noOfEdges = 0;
        threshold = 0;

        int limit = cleanCleanER ? datasetLimit : noOfEntities;
        if (weightingScheme.equals(WeightingScheme.ARCS)) {
            for (int i = 0; i < limit; i++) {
                processArcsEntity(i);
                updateThreshold(i);
            }
        } else {
            for (int i = 0; i < limit; i++) {
                processEntity(i);
                updateThreshold(i);
            }
        }

        threshold /= noOfEdges;
        //threshold = threshold/2;
        //threshold /= (ii*2);
        //System.out.println("\n\n\nglobal threshold: " + threshold);
        //threshold = max_min;
        //System.out.println("\n\n\nglobal max_min  : " + max_min);
    }

    protected void updateThreshold(int entityId) {
//        noOfEdges += validEntities.size();
//        double max = 0;
//        for (int neighborId : validEntities) {
//            //threshold += getWeight(entityId, neighborId);
//            max = Math.max(getWeight(entityId, neighborId),max);
//        }
//        //System.out.println("max local: " + max);
//        if (max > 0.0) {
//            //System.out.println("max local: " + max);
//            //ii ++;
//            //threshold += max;
//            max_min = Math.min(max_min, max);
//        }
//
////        if (max > 0.0 && max < 1.0) {
////            System.out.println("max local: " + max);
////        }
//        //max_min += max;
        noOfEdges += validEntities.size();
        for (int neighborId : validEntities) {
            threshold += getWeight(entityId, neighborId);
        }
    }

    protected boolean verifyValidEntities(int entityId, int i, List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc) {
        retainedNeighbors.clear();
        if (!cleanCleanER) {
            for (int neighborId : validEntities) {
                double weight = getWeight(entityId, neighborId);
                if (threshold <= weight) {
                    retainedNeighbors.add(neighborId);
                }
            }
            addDecomposedBlock(entityId, retainedNeighbors, newBlocks);
        } else {
            if (entityId < datasetLimit) {
                for (int neighborId : validEntities) {
                	
                    double weight = getWeight(entityId, neighborId);
                   // if(isValidComparison(entityId, neighborId))
                    //	System.out.println(threshold + "  "+ weight +"  " );
                    if (threshold <= weight) {
                        retainedNeighbors.add(neighborId - datasetLimit);
                    }
                }
                addDecomposedBlock(entityId, retainedNeighbors, newBlocks);
            } else {
                for (int neighborId : validEntities) {
                    double weight = getWeight(entityId, neighborId);
                    if(isMatch(entityId, neighborId))
                    	System.out.println(threshold + "  "+ weight +"  " );
                    if (threshold <= weight) {
                        retainedNeighbors.add(neighborId);
                    }
                }
                addReversedDecomposedBlock(entityId - datasetLimit, retainedNeighbors, newBlocks);
            }
        }
        return false;
    }
}
