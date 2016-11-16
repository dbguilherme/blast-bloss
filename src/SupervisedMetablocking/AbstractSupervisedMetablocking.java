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
import DataStructures.EntityIndex;
import DataStructures.FastEntityIndex;
import DataStructures.IdDuplicates;
import DataStructures.UnilateralBlock;
import Utilities.ComparisonIterator;
import Utilities.Constants;
import Utilities.ExecuteBlockComparisons;
import Utilities.ProfileComparison;
import Utilities.StatisticsUtilities;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author gap2
 */

public abstract class AbstractSupervisedMetablocking implements Constants {
 
    protected final boolean dirtyER;
 
    protected int noOfAttributes;
    protected int noOfClassifiers;
    protected double noOfBlocks;
    protected double validComparisons;
    protected double[] comparisonsPerBlock;
    protected double[] nonRedundantCPE;
    protected double[] redundantCPE;
    
    protected Attribute classAttribute;
    protected ArrayList<Attribute> attributes;
    protected final FastEntityIndex entityIndex;
    protected Instances trainingInstances;
    protected final List<AbstractBlock> blocks;
    protected List<Double>[] overheadTimes;
    protected List<Double>[] resolutionTimes;
    protected List<Double> sampleMatches;
    protected List<Double> sampleNonMatches;
    protected List<Double>[] sampleComparisons;
    protected List<Double>[] sampleDuplicates;
    protected List<String> classLabels;
    protected final Set<IdDuplicates> duplicates;
    protected Set<Comparison> trainingSet;
    protected Set<IdDuplicates> detectedDuplicates;
    protected double[] counters;
    protected double[] counters_entro;
    protected double max_weight = 0.0d;
    protected List<Integer> neighbors= new ArrayList<>();;
    protected Set<Integer> validEntities= new HashSet<>();;
    
    public double totalBlocks;
    public int counter_a = 0;
    //List<Integer> neighbors;
    protected int[] flags;
    public HashSet<Integer> counter_a_set = new HashSet<>();
    public int counter_b = 0;
    public HashSet<Integer> counter_b_set = new HashSet<>();
    public int counter_tot = 0;
    public ExecuteBlockComparisons ebc;

	private double threshold_max;

	private double threshold;

	private double[]  averageWeight ;
    
    
    public AbstractSupervisedMetablocking (int classifiers, List<AbstractBlock> bls, Set<IdDuplicates> duplicatePairs, ExecuteBlockComparisons ebc) {
        blocks = bls;
        dirtyER = blocks.get(0) instanceof BilateralBlock;
        entityIndex = new FastEntityIndex(blocks);
        duplicates = duplicatePairs;
        noOfClassifiers = classifiers;
    	averageWeight = new double[entityIndex.getNoOfEntities()];;
        //BilateralBlock b=new Bi;
        this.ebc=ebc;
        
        getStatistics();
        prepareStatistics();
        getAttributes();
        int noOfEntities=entityIndex.getNoOfEntities();
        counters = new double[noOfEntities];
        counters_entro = new double[noOfEntities];
        flags = new int[noOfEntities];
        totalBlocks=blocks.size();
        for (int i = 0; i < noOfEntities; i++) {
            flags[i] = -1;
        }
        
    }
    
    protected abstract void applyClassifier(Classifier classifier) throws Exception;
    protected abstract List<AbstractBlock> gatherComparisons();
    protected abstract void initializeDataStructures();
    protected abstract void processComparisons(int configurationId, int iteration, BufferedWriter writer1, BufferedWriter writer2, BufferedWriter writer3, BufferedWriter writer4, double th);
    
    
    
    protected double getWeight(int entityId, int neighborId) {
    	
    	//for (int i = 0; i < entityIndex.getNoOfEntities(); i++) {
            //processEntity(entityId);            
       // }
    	long[] vE = new long[2];
                long[] v_E = new long[2];
                //@fof
                long abE = (long)counters[neighborId];
                long aE  = entityIndex.getNoOfEntityBlocks(entityId, 0) - abE;
                long bE  = entityIndex.getNoOfEntityBlocks(neighborId, 0) - abE;
                //@fon
                counter_tot++;
//                if (abE < 1) {
//                    System.out.println("ab < 1");
//                }
//                if (aE < 0) {
//                    counter_a++;
//                }
//                if (bE < 0) {
//                    counter_b++;
//                }
                vE[0] = (long) counters[neighborId];
                vE[1] = entityIndex.getNoOfEntityBlocks(entityId, 0) - vE[0];
                v_E[0] = entityIndex.getNoOfEntityBlocks(neighborId, 0) - vE[0];
                v_E[1] = (int) (totalBlocks - (vE[0] + vE[1] + v_E[0]));

                if (vE[0] < 1) {
                    vE[0] = 1;
                }
                if (v_E[0] < 1) {
                    v_E[0] = 1;
                }
                if (vE[1] < 1) {
                    vE[1] = 1;
                }
                if (v_E[1] < 1) {
                    v_E[1] = 1;
                }
                long[][] cME = {vE, v_E};
                //ChisqTest inferenceChiE = new ChisqTest(cME);

                ChiSquareTest chi_squared_test_E = new ChiSquareTest();
                //GTest g_test = new GTest();
                //chi_squared_test.chiSquareTest(cME);
                if (max_weight == 0.0d) {
                    //System.out.println("max weight chi = 0");
                    //return (inferenceChiE.testStatistic * counters_entro[neighborId]);
                    //double a1 = 2 * vE[0] + vE[1] + v_E[0];
                    double a2 = vE[0] + 2 * v_E[0] + v_E[1];
                    double a3 = vE[0] + vE[1] + v_E[0];
                    double a4 = vE[1] + v_E[0] + 2 * v_E[1];
                    //System.out.println("---" + counters_entro[neighborId]);
                   Double x=counters_entro[neighborId];
//                   if(entityId==114 && neighborId==4592+entityIndex.getDatasetLimit()){
//                	   System.out.println(" vE[0] " + vE[0] +  " vE[1] " +  vE[0] + " v_E[0] "+ v_E[0] + " v_E[1] "+ v_E[1]);
//                   }
                    return chi_squared_test_E.chiSquare(cME) * counters_entro[neighborId];
                    
                    //return chi_squared_test_E.chiSquare(cME) * counters_entro[neighborId] * (a1 + a2 + a3 + a4);

                    //double[] expected = new double[]{(double) vE[0], (double) vE[1]};
                    //return g_test.g(expected, v_E) * counters_entro[neighborId];
                } else {
                    //return (inferenceChiE.testStatistic * entityIndex.get_common_entropy(comparison, true)) / max_weight;
                    System.out.println("Not implemented");
                }
              //  int id =((BilateralBlock) blocks.get(0)).getIndex1Entities();
           
        return -1;
    }
    public void setThreshold(int entityId ){
    	 threshold_max = 0;
         double min = Double.MAX_VALUE;
         double w = 0;
         threshold = 0;
    	
    	for (int neighborId : validEntities) {
            w = getWeight(entityId, neighborId);
            
            threshold_max = Math.max(threshold_max, w);
            min = Math.min(min, w);
        }
            threshold = threshold_max / 2;
    }
    
    
    
    
    
    public void applyProcessing(int iteration, Classifier[] classifiers, ExecuteBlockComparisons ebc, int tamanho, BufferedWriter writer1, BufferedWriter writer2, BufferedWriter writer3, BufferedWriter writer4, int i2, String string) throws Exception {
//    	 for (int i = 0; i < entityIndex.getNoOfEntities(); i++) {
//             processEntity(i);
//             setThreshold(i);
//             averageWeight[i] = threshold;
//         }
    	
    	 
    	 initializeDataStructures();
    	 classifiers[0].buildClassifier(loadSet("/tmp/final_treina.arff"));
    	 
    	 
    	 Instances data=loadSet("/tmp/test.arff");
    	 
    	 
    	 int count=0,erro=0,falso_positivo=0;
    	 for (int i = 0; i < data.size(); i++) {
    		 int instanceLabel = (int) classifiers[0].classifyInstance(data.get(i));  
             if (instanceLabel == 1) {
                 
            	 if(data.get(i).classValue()==1){
            		 count++;
            	 }
             }else
             {
            	 if(data.get(i).classValue()==1){
            		 erro++;
            		 for (int j = 0; j < 6; j++) {
						System.out.print(data.get(i).value(j) +"  ");
					}
            		 System.out.println("");
            		 //System.out.println(data.get(i).v);
            	 }else
            		 falso_positivo++;
            		 
             }
		}
    	 System.out.println("count " + count +" erro "+ erro +"  "+ falso_positivo);
    	 
    	 
    	 
//        getTrainingSet(iteration);
//        for (int i = 0; i < classifiers.length; i++) {
//            System.out.println("\n\nClassifier id\t:\t" + i);
//            initializeDataStructures();
//            
//            long startingTime = System.currentTimeMillis();
//            classifiers[i].buildClassifier(trainingInstances);
//            applyClassifier(classifiers[i]);
//            List<AbstractBlock> newBlocks = gatherComparisons();
//            double overheadTime = System.currentTimeMillis()-startingTime;
//            System.out.println("CL"+i+" Overhead time\t:\t" + overheadTime);
//            overheadTimes[i].add(overheadTime);
//            
//            //commented out for faster experiments
//            //use when measuring resolution time
//            long comparisonsTime = 0;//ebc.comparisonExecution(newBlocks);
//            System.out.println("CL"+i+" Classification time\t:\t" + (comparisonsTime+overheadTime));
//            resolutionTimes[i].add(new Double(comparisonsTime+overheadTime));
//            
//            double th = 0;
//			processComparisons(i, iteration, writer1, writer2,writer3, writer4,th);
//        }
    }
    
    private Instances loadSet(String file) {
    	BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			Instances data = new Instances(reader);
	    	reader.close();
	    	data.setClassIndex(data.numAttributes() - 1);
	    	return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
		
	}

	protected boolean areMatching(Comparison comparison) {
        if (dirtyER) {
            final IdDuplicates duplicatePair1 = new IdDuplicates(comparison.getEntityId1(), comparison.getEntityId2());
            final IdDuplicates duplicatePair2 = new IdDuplicates(comparison.getEntityId2(), comparison.getEntityId1());
            return duplicates.contains(duplicatePair1) || duplicates.contains(duplicatePair2);
        }
       // if(comparison.getEntityId1()< comparison.getEntityId2()){
        	final IdDuplicates duplicatePair1 = new IdDuplicates(comparison.getEntityId1(), comparison.getEntityId2());
        	return duplicates.contains(duplicatePair1);
       // }else
        //	return false;
    }
    
    private void getAttributes() {
        attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute("ECBS"));
        attributes.add(new Attribute("RACCB"));
        attributes.add(new Attribute("JaccardSim"));
        attributes.add(new Attribute("NodeDegree1"));
        attributes.add(new Attribute("NodeDegree2"));
       // attributes.add(new Attribute("teste weight"));
      // attributes.add(new Attribute("teste weight"));
        classLabels = new ArrayList<String>();
        classLabels.add(NON_MATCH);
        classLabels.add(MATCH);
        
        classAttribute = new Attribute("class", classLabels);
        attributes.add(classAttribute);
        noOfAttributes = attributes.size();
    }
    
    private void getStatistics() {
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
    
    protected void setNormalizedNeighborEntities(int blockIndex, int entityId) {
    	int datasetLimit=entityIndex.getDatasetLimit();
		neighbors.clear();
		
//		  for (AbstractBlock block : blocks) {
//              // sort entity ids in block
//              List<Integer> entityIds = new ArrayList<>();
//              for (int id : ((BilateralBlock) block).getIndex1Entities()) {
//                  entityIds.add(id);
//              }
//              for (int id : ((BilateralBlock) block).getIndex2Entities()) {
//                  entityIds.add(id+datasetLimit);
//              }
//              Collections.sort(entityIds);
//              arrayOfBlocks[counter++] = entityIds.toArray(new Integer[entityIds.size()]);
//          }
		
        if (true) {
            //int datasetLimit;
            
			if (entityId < datasetLimit) {
                for (int originalId : ((BilateralBlock)blocks.get(blockIndex)).getIndex2Entities()) {
                    neighbors.add(originalId + datasetLimit);
                    //neighbors_entro.add();
                }
            } else {
                for (int originalId : ((BilateralBlock)blocks.get(blockIndex)).getIndex1Entities()) {
                    neighbors.add(originalId);
                }
            }
        }
        //;/if(neighbors.size()>5)
        //	System.out.println(neighbors.size() +"\n\n ");
//        } else {
//            if (!nodeCentric) {
//                for (int neighborId : uBlocks[blockIndex].getEntities()) {
//                    if (neighborId < entityId) {
//                        neighbors.add(neighborId);
//                    }
//                }
//            } else {
//                for (int neighborId : uBlocks[blockIndex].getEntities()) {
//                    if (neighborId != entityId) {
//                        neighbors.add(neighborId);
//                    }
//                }
//            }
//        }
    }
    
    protected void processEntity(int entityId) {
		validEntities.clear();
        final int[] associatedBlocks = entityIndex.getEntityBlocks(entityId, 0);
        if (associatedBlocks.length == 0) {
            return;
        }

     //   if(entityId==114)
        //	System.out.println("ok");
        for (int blockIndex : associatedBlocks) {
            setNormalizedNeighborEntities(blockIndex, entityId);
            
            for (int neighborId : neighbors) {
                if (flags[neighborId] != entityId) {
                    counters[neighborId] = 0;
                    counters_entro[neighborId] = 0;
                    flags[neighborId] = entityId;
                }
               // if(neighborId==4592+entityIndex.getDatasetLimit())
                //	System.out.println("ok");
                counters[neighborId]++;
                counters_entro[neighborId] += entityIndex.getEntropyBlock(blockIndex);
              // if(counters[neighborId]>5)
              //  System.out.println(counters[neighborId]+  " counters_entro: " + counters_entro[neighborId] + " " + neighborId);
                validEntities.add(neighborId);
            }
        }
    }
    
    double countT=0;
    int count=0;
    
    protected Instance getFeatures(int match, List<Integer> commonBlockIndices, Comparison comparison, double noOfBlocks2) {
        double[] instanceValues = new double[noOfAttributes];

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
      // if(match==1)
      //  instanceValues[5]=getWeight(comparison.getEntityId1(),comparison.getEntityId2()+entityIndex.getDatasetLimit());
       // instanceValues[6]= Math.sqrt(Math.pow(averageWeight[comparison.getEntityId1()], 2) + Math.pow(averageWeight[entityId2], 2)) / 4;
       // System.out.println(instanceValues[6]);
//        if(comparison.getEntityId1()==114 && comparison.getEntityId2()==4592){
//        	System.out.print(comparison.getEntityId1() + " "+ comparison.getEntityId2() +" " +" --------- "+        ebc.getSimilarityAttribute(comparison.getEntityId1(), comparison.getEntityId2()));
//        	for (int i = 0; i < instanceValues.length-1; i++) {
//        		System.out.print(instanceValues[i]+"  ");
//        	}
//        }
       
       // instanceValues[5] =ebc.getSimilarityAttribute(comparison.getEntityId1(), comparison.getEntityId2());
    	
        //System.out.println(instanceValues[5]);
//        if(areMatching(comparison)){
//        	countT+=getWeight(comparison.getEntityId1(),comparison.getEntityId2());
//            count++;
//            System.out.println("Mat---" + countT/count++ +"   count " + count);
//        }
        	
       // else
        	//if(count%100==0)
        	//System.out.println("                non "+ getWeight(comparison.getEntityId1(),comparison.getEntityId2()));
        
      // System.out.print(match +" \n"); 
        instanceValues[5] = match;
        Instance newInstance = new DenseInstance(1.0, instanceValues);
        newInstance.setDataset(trainingInstances);
        return newInstance;
    }
    
    protected void getTrainingSet(int iteration) {
        int trueMetadata = 0;
        Random random = new Random(iteration);
        int matchingInstances = (int) (SAMPLE_SIZE*duplicates.size()+1);
        double nonMatchRatio = matchingInstances / (validComparisons - duplicates.size());

        trainingSet = new HashSet<Comparison>(4*matchingInstances);
        trainingInstances = new Instances("trainingSet", attributes, 2*matchingInstances);
        trainingInstances.setClassIndex(noOfAttributes - 1);

        
      //  counters = new double[entityIndex.getNoOfEntities()];
       // counters_entro = new double[entityIndex.getNoOfEntities()];
        int matchming=0;
        System.out.println("matchingInstances "+ matchingInstances);
        for (AbstractBlock block : blocks) {
            ComparisonIterator iterator = block.getComparisonIterator();
            while (iterator.hasNext()) {
                Comparison comparison = iterator.next();
                final List<Integer> commonBlockIndices = entityIndex.getCommonBlockIndices(block.getBlockIndex(), comparison);
                if (commonBlockIndices == null) {
                    continue;
                }

                int match = NON_DUPLICATE; // false
                if (areMatching(comparison)) {
                	matchming++;
                    if (random.nextDouble() < SAMPLE_SIZE) {
                        trueMetadata++;
                        match = DUPLICATE; // true
                    } else {
                        continue;
                    }
                } else if (nonMatchRatio <= random.nextDouble()) {
                    continue;
                }

                trainingSet.add(comparison);
                System.out.println("match " + match +" ");
                Instance newInstance = getFeatures(match, commonBlockIndices, comparison, nonMatchRatio);
                for (int i = 0; i < 5; i++) {
					System.out.print(newInstance.valueSparse(i)+" ");
				}
                System.out.println();
                trainingInstances.add(newInstance);
            }
        }

        sampleMatches.add((double) trueMetadata);
        sampleNonMatches.add((double) (trainingSet.size() - trueMetadata));
        System.out.println("match " + trueMetadata +" "+ (trainingSet.size() - trueMetadata) +" total dup "+ matchming);
    }
    
    private void prepareStatistics() {
        sampleMatches = new ArrayList<Double>();
        sampleNonMatches = new ArrayList<Double>();
        overheadTimes = new ArrayList[noOfClassifiers];
        resolutionTimes = new ArrayList[noOfClassifiers];
        sampleComparisons = new ArrayList[noOfClassifiers];
        sampleDuplicates = new ArrayList[noOfClassifiers];
        for (int i = 0; i < noOfClassifiers; i++) {
            overheadTimes[i] = new ArrayList<Double>();
            resolutionTimes[i] = new ArrayList<Double>();
            sampleComparisons[i] = new ArrayList<Double>();
            sampleDuplicates[i] = new ArrayList<Double>();
        }
    }
    
    public void printStatistics() {
        System.out.println("\n\n\n\n\n+++++++++++++++++++++++Printing overall statistics+++++++++++++++++++++++");
        
        double avSMatches = StatisticsUtilities.getMeanValue(sampleMatches);
        double avSNonMatches = StatisticsUtilities.getMeanValue(sampleNonMatches);
        System.out.println("Sample matches\t:\t" + avSMatches + "+-" + StatisticsUtilities.getStandardDeviation(avSMatches, sampleMatches));
        System.out.println("Sample non-matches\t:\t" + avSNonMatches + "+-" + StatisticsUtilities.getStandardDeviation(avSNonMatches, sampleNonMatches));
         
        for (int i = 0; i < overheadTimes.length; i++) {
            System.out.println("\n\n\n\n\nClassifier id\t:\t" + (i+1));
            double avOTime = StatisticsUtilities.getMeanValue(overheadTimes[i]);
            double avRTime = StatisticsUtilities.getMeanValue(resolutionTimes[i]);
            double avSEComparisons = StatisticsUtilities.getMeanValue(sampleComparisons[i]);
            double avSDuplicates = StatisticsUtilities.getMeanValue(sampleDuplicates[i]);

            final List<Double> pcs = new ArrayList<Double>();
            for (int j = 0; j < sampleMatches.size(); j++) {
                pcs.add(sampleDuplicates[i].get(j)/(duplicates.size())*100.0);
            }
            double avSPC = StatisticsUtilities.getMeanValue(pcs);

            System.out.println("Overhead time\t:\t" + avOTime + "+-" + StatisticsUtilities.getStandardDeviation(avOTime, overheadTimes[i]));
            System.out.println("Resolution time\t:\t" + avRTime + "+-" + StatisticsUtilities.getStandardDeviation(avRTime, resolutionTimes[i]));
            System.out.println("Sample duplicates\t:\t" + avSDuplicates + "+-" + StatisticsUtilities.getStandardDeviation(avSDuplicates, sampleDuplicates[i]));
            System.out.println("Sample PC\t:\t" + avSPC);
            System.out.println("Sample comparisons\t:\t" + avSEComparisons + "+-" + StatisticsUtilities.getStandardDeviation(avSEComparisons, sampleComparisons[i]));
        }
    }

	protected void savePairs(int i, ExecuteBlockComparisons ebc) {
		// TODO Auto-generated method stub
		
	}
}