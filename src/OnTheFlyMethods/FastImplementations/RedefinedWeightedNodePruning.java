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
import DataStructures.Comparison;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;
import Utilities.Converter;
import Utilities.ExecuteBlockComparisons;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RedefinedWeightedNodePruning extends MetaBlocking.EnhancedMetaBlocking.FastImplementations.RedefinedWeightedNodePruning {

    protected double totalComparisons;
    Map<Integer,Integer> map = new HashMap<Integer,Integer>();
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
 int apagar=0;
    //@Override
    protected boolean verifyValidEntities(int entityId, int xxx, List<AbstractBlock> newBlocks, ExecuteBlockComparisons ebc, Instances trainingInstances) {
    	int index;
    	retainedNeighbors.clear();
        if (!cleanCleanER) {
//            for (int neighborId : validEntities) {
//                if (isValidComparison(entityId, neighborId,ebc)) {
//                    totalComparisons++;
//                    duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
//                }
//            }
        } else {
            if (entityId < datasetLimit) {
//            	//Iterator<Integer> temp = validEntitiesB.iterator();
            	int size =validEntities.size();
            	Iterator<Integer> it = validEntitiesB.iterator();
               for (int neighborId : validEntities) 
                {
            	   
             	  Integer value = map.get(entityId);
             	  if (value !=null && value==neighborId){
             		 // System.out.println("----");
             		  continue;
             	  }
             		  
             	  value = map.get(neighborId);
             	  if (value !=null && value == entityId){
             		 // System.out.println("----");
             		  continue;
             	  }
             		  
             	  map.put(entityId,neighborId);
            	   
            	   
            	  // if(entityId==1178 && neighborId==2562)
                 //  	System.out.println("ok");
//                	// index=temp.next();
            	   
            	   int blockIndex=it.next();
                	 if (isValidComparison(entityId, neighborId,ebc)) {
                        totalComparisons++;
                        duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
                        if(apagar++%1000==0)
                   		  System.out.println(apagar);

                        
                        Comparison c ;
                        if(entityId<datasetLimit)
                        	c = new Comparison(true, entityId, neighborId-datasetLimit);
                        else
                        	c = new Comparison(true, entityId-datasetLimit, neighborId);
                  	  final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(blockIndex, c);
                  	  if(commonBlockIndices==null)
                  		  continue;
//                  	  if(!retainedEntitiesD1.contains(comparison.getEntityId1()))
//                  		  retainedEntitiesD1.add(comparison.getEntityId1());
//                  	  if(!retainedEntitiesD2.contains(comparison.getEntityId2()))
//                  		  retainedEntitiesD2.add(comparison.getEntityId2());
                  	  ////////////////////////////
                  	  
                  	if(c.getEntityId1()==1 && c.getEntityId2()==12088)
                    	System.out.println();
                  	  double[] instanceValues = new double[7];

                       // int entityId2 = comparison.getEntityId2() + entityIndex.getDatasetLimit();

                        double ibf1 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(c.getEntityId1(), 0));
                        double ibf2 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(c.getEntityId2(), 1));
                      
                        instanceValues[0] = commonBlockIndices.size()*ibf1*ibf2;
                        
                        double raccb = 0;
                        for (Integer index1 : commonBlockIndices) {
                            raccb += 1.0 / comparisonsPerBlock[index1];
                        }
                        if (raccb < 1.0E-6) {
                            raccb = 1.0E-6;
                        }
                        instanceValues[1] = raccb;

                        String temp=Integer.toString(entityId) +"00"+ Integer.toString(neighborId-datasetLimit); 
                        instanceValues[2] = commonBlockIndices.size() / (redundantCPE[c.getEntityId1()] + redundantCPE[c.getEntityId2()] - commonBlockIndices.size());
                        instanceValues[3] = nonRedundantCPE[c.getEntityId1()];
                        instanceValues[4] = nonRedundantCPE[c.getEntityId2()];
                       
                        
                        	instanceValues[5] =	ebc.getSimilarityAttribute(c.getEntityId1(), c.getEntityId2());
                        
                        instanceValues[6] = adp.isSuperfluous(getComparison(entityId, neighborId))?1:0;
                        
                        Instance newInstance = new DenseInstance(1.0, instanceValues);
                        newInstance.setDataset(trainingInstances);
                        trainingInstances.add(newInstance);                      
                    }
               }
            } else {
            	Iterator<Integer> it = validEntitiesB.iterator();
                for (int neighborId : validEntities) 
                {                	
                	
                	 Integer value = map.get(entityId);
                	  if (value !=null && value==neighborId){
                		 // System.out.println("----");
                		  continue;
                	  }
                		  
                	  value = map.get(neighborId);
                	  if (value !=null && value == entityId){
                		 // System.out.println("----");
                		  continue;
                	  }
                		  
                	  map.put(entityId,neighborId);
                	
                	
                	   int blockIndex=it.next();
                    if (isValidComparison(entityId, neighborId,ebc)) {
                        totalComparisons++;
                        duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
                        if(apagar++%1000==0)
                  		  System.out.println(apagar);
                        
                        if(apagar==3)
                        	System.out.println();
                        
                        
                        Comparison c ;
                        if(entityId<datasetLimit)
                        	c = new Comparison(true, entityId, neighborId-datasetLimit);
                        else
                        	c = new Comparison(true, entityId-datasetLimit, neighborId);
                    	  final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(blockIndex, c);
                      	  if(commonBlockIndices==null)
                      		  continue;
//                      	  if(!retainedEntitiesD1.contains(comparison.getEntityId1()))
//                      		  retainedEntitiesD1.add(comparison.getEntityId1());
//                      	  if(!retainedEntitiesD2.contains(comparison.getEntityId2()))
//                      		  retainedEntitiesD2.add(comparison.getEntityId2());
                      	  ////////////////////////////
                      	  double[] instanceValues = new double[7];

                           // int entityId2 = comparison.getEntityId2() + entityIndex.getDatasetLimit();

                            double ibf1 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(entityId, 0));
                            double ibf2 = Math.log(noOfBlocks/entityIndex.getNoOfEntityBlocks(neighborId-datasetLimit, 1));
                          
                            instanceValues[0] = commonBlockIndices.size()*ibf1*ibf2;

                            
                            
                            double raccb = 0;
                            for (Integer index1 : commonBlockIndices) {
                                raccb += 1.0 / comparisonsPerBlock[index1];
                            }
                            if (raccb < 1.0E-6) {
                                raccb = 1.0E-6;
                            }
                            instanceValues[1] = raccb;

                            instanceValues[2] = commonBlockIndices.size() / (redundantCPE[c.getEntityId1()] + redundantCPE[neighborId-datasetLimit] - commonBlockIndices.size());
                            instanceValues[3] = nonRedundantCPE[entityId];
                            instanceValues[4] = nonRedundantCPE[neighborId-datasetLimit];
                            instanceValues[5] =ebc.getSimilarityAttribute(c.getEntityId1(), c.getEntityId2());
                            
                            instanceValues[6] = adp.isSuperfluous(getComparison(entityId, neighborId))?1:0;

                            Instance newInstance = new DenseInstance(1.0, instanceValues);
                            newInstance.setDataset(trainingInstances);
                            trainingInstances.add(newInstance);
                           
                     //return true; 
                    }
               	

                }
            }
        }
		return false;
    }
}

//
//if (!cleanCleanER) {
//    for (int neighborId : validEntities) {
//        if (isValidComparison(entityId, neighborId)) {
//            totalComparisons++;
//            duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
//        }
//    }
//} else {
//    if (entityId < datasetLimit) {
//        for (int neighborId : validEntities) {
//            if (isValidComparison(entityId, neighborId)) {
//                totalComparisons++;
//                duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
//            }
//        }
//    } else {
//        for (int neighborId : validEntities) {
//            if (isValidComparison(entityId, neighborId)) {
//                totalComparisons++;
//                duplicatePropagation.isSuperfluous(getComparison(entityId, neighborId));
//            }
//        }
//    }
//}
