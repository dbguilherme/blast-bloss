package MetaBlocking.EnhancedMetaBlocking.FastImplementations;

import DataStructures.AbstractBlock;
import DataStructures.BilateralBlock;
import DataStructures.Comparison;
import DataStructures.IdDuplicates;
import MetaBlocking.FastImplementations.WeightedNodePruning;
import Utilities.ComparisonIterator;
import Utilities.Converter;
import Utilities.ExecuteBlockComparisons;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;

/**
 * @author stravanni
 */
public class RedefinedWeightedNodePruning extends WeightedNodePruning {

	Map<Integer,List<Integer>> map = new HashMap<Integer,List<Integer>>();
    protected double[] averageWeight;
    int values[][] = new int[3000][65000];
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

        return false;
    }
    int count=0;
    
    protected int noOfAttributes;
    protected int noOfClassifiers;
    protected double noOfBlocks;
    protected double validComparisons;
    protected double[] comparisonsPerBlock;
    protected double[] nonRedundantCPE;
    protected double[] redundantCPE;
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    List<String> classLabels;
    
    private void getStatistics(List<AbstractBlock> blocks) {
        noOfBlocks = blocks.size();
        System.out.println("BLOCK SIZE "+ (blocks.size() + 1));
        validComparisons = 0;
        int noOfEntities = entityIndex.getNoOfEntities();
        
        redundantCPE = new double[noOfEntities];
        nonRedundantCPE = new double[noOfEntities];
        comparisonsPerBlock = new double[(int)(blocks.size() + 1)];
        for (AbstractBlock block : blocks) {
        	block.setNoOfComparisons(((BilateralBlock)block).getIndex1Entities().length* ((BilateralBlock)block).getIndex2Entities().length);
        //	System.out.println("block.getNoOfComparisons()  " +block.getNoOfComparisons() +" block.getBlockIndex()  " + block.getBlockIndex());
            comparisonsPerBlock[block.getBlockIndex()] = block.getNoOfComparisons();
            
            ComparisonIterator iterator = block.getComparisonIterator();
            while (iterator.hasNext()) {
                Comparison comparison = iterator.next();
                    
                int entityId2 = comparison.getEntityId2()+entityIndex.getDatasetLimit();
                redundantCPE[comparison.getEntityId1()]++;
                redundantCPE[entityId2]++;
                    
                if (!entityIndex.isRepeated(block.getBlockIndex(), comparison)) {
                    validComparisons++;
                    nonRedundantCPE[comparison.getEntityId1()]++;
                    nonRedundantCPE[entityId2]++;
                }
            }
        }
    }
    
    private void getAttributes() {
      
        attributes.add(new Attribute("ECBS"));
        attributes.add(new Attribute("RACCB"));
        attributes.add(new Attribute("JaccardSim"));
        attributes.add(new Attribute("NodeDegree1"));
        attributes.add(new Attribute("NodeDegree2"));
        attributes.add(new Attribute("teste weight"));
      // attributes.add(new Attribute("teste weight"));
        classLabels = new ArrayList<String>();
        classLabels.add("0");
        classLabels.add("1");
        
        Attribute classAttribute = new Attribute("class", classLabels);
        attributes.add(classAttribute);
        noOfAttributes = attributes.size();
    }
    
   
    
   // @Override
    protected List<AbstractBlock> pruneEdges(List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc, AbstractDuplicatePropagation adp) {
       List<Integer> retainedEntitiesD1 = new ArrayList<Integer>();
  	   List<Integer> retainedEntitiesD2 = new ArrayList<Integer>();
  	   List<AbstractBlock> blockAfterPrunning = new ArrayList<AbstractBlock>();
  	   // blockAfterPrunning.addAll(newBlocks);
  	   // blockAfterPrunning.clear();
  	   getAttributes();
  	  int matchingInstances=3000;
	  HashSet<Comparison> trainingSet = new HashSet<Comparison>(4*matchingInstances);
      Instances trainingInstances = new Instances("trainingSet", attributes, 2*matchingInstances);
      trainingInstances.setClassIndex(noOfAttributes - 1);
      
  	   getStatistics(newBlocks);
        if (weightingScheme.equals(WeightingScheme.ARCS)) {
            for (int i = 0; i < noOfEntities; i++) {
               // processArcsEntity(i);
               // verifyValidEntities(i,0, newBlocks);
            }
        }
        else {
        	for(AbstractBlock block : newBlocks) {
        		retainedEntitiesD1.clear();
        		retainedEntitiesD2.clear();
                ComparisonIterator iterator = block.getComparisonIterator();
                while (iterator.hasNext()) {
                	
                	  Comparison comparison = iterator.next();
                	  int flag=0;
                	  Integer key = comparison.getEntityId1();
                	  List<Integer> list = map.get(key);
                	  if (list == null)
                	  {
                	      list = new LinkedList<Integer>();
                	      map.put(key,list);
                	  }else{
                		  for (int j = 0; j < list.size(); j++) {
                			  if(list.get(j).equals(comparison.getEntityId2())){
//                				  System.out.println("encontrou----------------------");
  								  flag=1;
                				  break;
                			}
                		  }
                	  }
                	  list.add(comparison.getEntityId2());
                	 
                	  if(flag==1)
                		  continue;
//                	  if(values[comparison.getEntityId1()][comparison.getEntityId2()]==1){
//                		  System.out.println("ok");
//                		  continue;
//                	  }
//                	  values[comparison.getEntityId1()][comparison.getEntityId2()]=1;
                	  
                	  processEntity(comparison.getEntityId1());
                      final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(block.getBlockIndex(), comparison);
                      
                      if(verifyValidEntities(comparison.getEntityId1(), comparison.getEntityId2()+datasetLimit, newBlocks,ebc)){
                    	  if(!retainedEntitiesD1.contains(comparison.getEntityId1()))
                    		  retainedEntitiesD1.add(comparison.getEntityId1());
                    	  if(!retainedEntitiesD2.contains(comparison.getEntityId2()))
                    		  retainedEntitiesD2.add(comparison.getEntityId2());
                    	  ////////////////////////////
                    	  double[] instanceValues = new double[7];

                          int entityId2 = comparison.getEntityId2() + entityIndex.getDatasetLimit();

                          double ibf1 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(comparison.getEntityId1(), 0));
                          double ibf2 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(comparison.getEntityId2(), 1));
                          instanceValues[0] = commonBlockIndices.size()*ibf1*ibf2;

                          
                          
                          double raccb = 0;
                          for (Integer index : commonBlockIndices) {
                              raccb += 1.0 / comparisonsPerBlock[index];
                          }
                          if (raccb < 1.0E-6) {
                              raccb = 1.0E-6;
                          }
                          instanceValues[1] = raccb;

                          instanceValues[2] = commonBlockIndices.size() / (redundantCPE[comparison.getEntityId1()] + redundantCPE[entityId2] - commonBlockIndices.size());
                          instanceValues[3] = nonRedundantCPE[comparison.getEntityId1()];
                          instanceValues[4] = nonRedundantCPE[entityId2];
                          instanceValues[5] =ebc.getSimilarityAttribute(comparison.getEntityId1(), comparison.getEntityId2());
                          
                          instanceValues[6] = adp.isSuperfluous(getComparison(comparison.getEntityId1(), entityId2))?1:0;

                          Instance newInstance = new DenseInstance(1.0, instanceValues);
                          newInstance.setDataset(trainingInstances);
                          trainingInstances.add(newInstance);
                      };
                      count++;
                }
                if(!retainedEntitiesD1.isEmpty() || !retainedEntitiesD2.isEmpty()){
                	int[] blockEntitiesD1 = Converter.convertCollectionToArray(retainedEntitiesD1);
                	int[] blockEntitiesD2 = Converter.convertCollectionToArray(retainedEntitiesD2);
                	blockAfterPrunning.add(new BilateralBlock(blockEntitiesD1, blockEntitiesD2, 0.0));
                	//if(retainedEntitiesD1.size()>2)
                	//	System.out.println("ok");
                }
        	}
        	BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter("/tmp/test.arff"));
				writer.write(trainingInstances.toString());
	        	 writer.flush();
	        	 writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 
//        	
//            for (int i = 0; i < noOfEntities; i++) {
//                processEntity(i);
//                verifyValidEntities(i,0, newBlocks,ebc);
//            }
           
        }
        System.out.println("count----" + count);
        return blockAfterPrunning;   
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
