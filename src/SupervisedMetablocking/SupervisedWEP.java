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

package SupervisedMetablocking;

import DataStructures.AbstractBlock;
import DataStructures.BilateralBlock;
import DataStructures.Comparison;
import DataStructures.DecomposedBlock;
import DataStructures.IdDuplicates;
import Utilities.ComparisonIterator;
import Utilities.Converter;
import Utilities.ExecuteBlockComparisons;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 *
 * @author gap2
 */

public class SupervisedWEP extends AbstractSupervisedMetablocking {
    
    private List<Integer> retainedEntities1;
    private List<Integer> retainedEntities2;
    Set<IdDuplicates> duplicatePairs;
    
    public SupervisedWEP (int noOfClassifiers, List<AbstractBlock> bls, Set<IdDuplicates> duplicatePairs, ExecuteBlockComparisons ebc) {
        super (noOfClassifiers, bls, duplicatePairs, ebc);
        this.duplicatePairs=duplicatePairs;
    }

    @Override
    protected void applyClassifier(Classifier classifier , List<ArrayList<Instance>> testSet) throws Exception {
//        for (AbstractBlock block : blocks) {
//            ComparisonIterator iterator = block.getComparisonIterator();
//            while (iterator.hasNext()) {
//                Comparison comparison = iterator.next();
//                final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(block.getBlockIndex(), comparison);
//                if (commonBlockIndices == null) {
//                    continue;
//                }
//
//                if (trainingSet.contains(comparison)) {
//                  //  continue;
//                }
//
//                Instance currentInstance = getFeatures(NON_DUPLICATE, commonBlockIndices, comparison, noOfBlocks);
//                int instanceLabel = (int) classifier.classifyInstance(currentInstance);  
//                if (instanceLabel == DUPLICATE) {
//                    retainedEntities1.add(comparison.getEntityId1());
//                    retainedEntities2.add(comparison.getEntityId2());
//                }
//            }
//        }
    	
    	int tp=0,fp=0,fn=0,vn=0;
    	for (int i = 0; i < testSet.size(); i++)		
		{
    		ArrayList<Instance> list = testSet.get(i);
    		count=0;
    		//Iterator<Instance> listit = list.iterator();
    		int controle=0;
    		while(controle<list.size()){
    			//System.out.println("teste " + controle);
    			Instance currentInstance=list.get(controle);
    			 int instanceLabel = (int) classifier.classifyInstance(currentInstance);  
                 if (instanceLabel == DUPLICATE) {
                	 if(currentInstance.classValue()==1.0)
                		 tp++;
                	 else
                		 fp++;
                    // retainedEntities1.add(comparison.getEntityId1());
                    // retainedEntities2.add(comparison.getEntityId2());
                 }else{
                	 if(currentInstance.classValue()==0.0)
                		 vn++;
                	 else
                		 fn++;
                 }
                 controle++;
    		}
    		
		}
    	System.out.println("controle "+ tp +"  "+ fp +" ---test---" +vn +" " +fn +" ---  "+  testSet.size() );
		System.out.println(" pq " + (double)tp/duplicatePairs.size() + " "+ (double)tp/(tp+fp));
    	
//    	{
//            ComparisonIterator iterator = block.getComparisonIterator();
//            while (iterator.hasNext()) {
//                Comparison comparison = iterator.next();
//                final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(block.getBlockIndex(), comparison);
//                if (commonBlockIndices == null) {
//                    continue;
//                }
//
//                if (trainingSet.contains(comparison)) {
//                  //  continue;
//                }
//
//                Instance currentInstance = getFeatures(NON_DUPLICATE, commonBlockIndices, comparison, noOfBlocks);
//                int instanceLabel = (int) classifier.classifyInstance(currentInstance);  
//                if (instanceLabel == DUPLICATE) {
//                    retainedEntities1.add(comparison.getEntityId1());
//                    retainedEntities2.add(comparison.getEntityId2());
//                }
//            }
//        }
    }

    @Override
    protected List<AbstractBlock> gatherComparisons() {
        int[] entityIds1 = Converter.convertCollectionToArray(retainedEntities1);
        int[] entityIds2 = Converter.convertCollectionToArray(retainedEntities2);
        
        boolean cleanCleanER = blocks.get(0) instanceof BilateralBlock;
        final List<AbstractBlock> newBlocks = new ArrayList<AbstractBlock>();
        newBlocks.add(new DecomposedBlock(cleanCleanER, entityIds1, entityIds2));
        return newBlocks;
    }

    @Override
    protected void initializeDataStructures() {
        detectedDuplicates = new HashSet<IdDuplicates>();
        retainedEntities1 = new ArrayList<Integer>();
        retainedEntities2 = new ArrayList<Integer>();
    }

//    @Override
//    protected void processComparisons(int classifierId) {
//        System.out.println("\n\nProcessing comparisons...");
//
//        int[] entityIds1 = Converter.convertCollectionToArray(retainedEntities1);
//        int[] entityIds2 = Converter.convertCollectionToArray(retainedEntities2);
//        for (int i = 0; i < entityIds1.length; i++) {
//            Comparison comparison = new Comparison(dirtyER, entityIds1[i], entityIds2[i]);
//            if (areMatching(comparison)) {
//                final IdDuplicates matchingPair = new IdDuplicates(entityIds1[i], entityIds2[i]);
//                detectedDuplicates.add(matchingPair);  
//            }
//        }
//                
//        System.out.println("Executed comparisons\t:\t" + entityIds1.length);
//        System.out.println("Detected duplicates\t:\t" + detectedDuplicates.size());
//        sampleComparisons[classifierId].add((double)entityIds1.length);
//        sampleDuplicates[classifierId].add((double)detectedDuplicates.size());
//    }

    int armazena=0;
    protected void processComparisons(int classifierId, int iteration, BufferedWriter writer1, BufferedWriter writer2, BufferedWriter writer3,BufferedWriter writer4, double th) {
    	        System.out.println("\n\nProcessing comparisons...");
       // create_conection("tese_scholar_clean");
        int[] entityIds1 = Converter.convertCollectionToArray(retainedEntities1);
        int[] entityIds2 = Converter.convertCollectionToArray(retainedEntities2);
        int teste=0;
        for (int i = 0; i < entityIds1.length; i++) {
        	//System.out.println(entityIds1[i] +" ---" + entityIds2[i]);
        	teste++;
            Comparison comparison = new Comparison(dirtyER, entityIds1[i], entityIds2[i]);
            if (areMatching(comparison)) {
                final IdDuplicates matchingPair = new IdDuplicates(entityIds1[i], entityIds2[i]);
                detectedDuplicates.add(matchingPair);  
                //System.out.println("match ->>>>" +entityIds1[i] +" ---" + entityIds2[i]);
            }
        }
               
        System.out.println("Executed comparisons blocking\t:\t" 	);
        System.out.println("Executed comparisons\t:\t" + entityIds1.length);
        System.out.println("Detected duplicates\t:\t" + detectedDuplicates.size());
        sampleComparisons[classifierId].add((double)entityIds1.length);
        sampleDuplicates[classifierId].add((double)detectedDuplicates.size());
        try {
        	if(classifierId==0){
        		Double d =((double)detectedDuplicates.size())/(duplicates.size())*100.0;
				writer1.write("ExecutedComparisons " + (entityIds1.length) + " DetectedDuplicates " + detectedDuplicates.size() + " PC " + d + " sampleMatches "+ sampleMatches.get(0) +  " "+  sampleNonMatches.get(0) + "  th " + th +" \n");
        		//armazena++;
        	}else
        	if(classifierId==1){
        		Double d =(sampleDuplicates[classifierId].get(armazena))/(duplicates.size())*100.0;
        		writer2.write("ExecutedComparisons " + (entityIds1.length) + " DetectedDuplicates " + detectedDuplicates.size() + " PC " + d + " sampleMatches "+ sampleMatches.get(0) +  " samplesNMatch "+  sampleNonMatches.get(0) + " time " + overheadTimes[classifierId].get(iteration) +" \n");
        		//armazena++;
        	}else
        	if(classifierId==2){
        		Double d =(sampleDuplicates[classifierId].get(armazena))/(duplicates.size())*100.0;
        		writer3.write("ExecutedComparisons " + (entityIds1.length) + " DetectedDuplicates " + detectedDuplicates.size() + " PC " + d + " sampleMatches "+ sampleMatches.get(0) +  " samplesNMatch "+  sampleNonMatches.get(0) + " time " + overheadTimes[classifierId].get(iteration) +" \n");
        		armazena++;
        	}
//        	else
//            	if(classifierId==3){
//            		Double d =(sampleDuplicates[classifierId].get(armazena))/(duplicates.size())*100.0;
//            		writer4.write("ExecutedComparisons " + (entityIds1.length) + " DetectedDuplicates " + detectedDuplicates.size() + " PC " + d + " sampleMatches "+ sampleMatches.get(iteration) +  " samplesNMatch "+  sampleNonMatches.get(iteration) + " time " + overheadTimes[classifierId].get(iteration) +" \n");
//            		armazena++;
//            	}
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    



}