/**
 * @author @stravanni
 */
package Experiments;

import BlockBuilding.AbstractBlockingMethod;
import BlockBuilding.MemoryBased.TokenBlocking;
import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;
import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.BilateralDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.UnilateralDuplicatePropagation;
import DataStructures.AbstractBlock;
import DataStructures.Attribute;
import DataStructures.EntityProfile;
import Utilities.ExecuteBlockComparisons;
import Utilities.RepresentationModel;
import Utilities.SerializationUtilities;
import Experiments.Exp_Util;
import MetaBlocking.ThresholdWeightingScheme;
import MetaBlocking.WeightingScheme;
import OnTheFlyMethods.FastImplementations.BlastWeightedNodePruning;
import OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ctc.wstx.io.EBCDICCodec;

/**
 * @author stravanni
 */

public class Test_metablocking {

    private static boolean CLEAN = true;
    private static String BASEPATH_CER = "/Users/gio/Desktop/umich/data/data_blockingFramework/";
    private static String BASEPATH_DER = "/Users/gio/Desktop/umich/data/data_blockingFramework/";

    public static void main(String[] args) throws IOException {

        int dataset = 0;
        boolean save = false;
        String blocking_type = "T";
        WeightingScheme ws = WeightingScheme.CHI_ENTRO;
        //WeightingScheme ws = WeightingScheme.FISHER_ENTRO; // For dirty dataset use this test-statistic because of the low number of co-occurrence in the blocks (Fisher exact test vs. Chi-squared ~ approximated)
        ThresholdWeightingScheme th_schme = ThresholdWeightingScheme.AM3;

        String mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/sintetica";
        String profilesPathA =  mainDirectory+"/300Kprofiles"	;	
        String groundTruthPath =  mainDirectory+"/300KIdDuplicates";	
        List<EntityProfile>[] profiles = new List[2];
		//profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
        //UnilateralDuplicatePropagation adp = new UnilateralDuplicatePropagation(groundTruthPath);
		
		mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/movies";
		profilesPathA= mainDirectory+"/token/dataset1_imdb";
		String profilesPathB= mainDirectory+"/token/dataset2_dbpedia";
		groundTruthPath =  mainDirectory+ "/ground/groundtruth";
		
//		mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/base_clean_serializada";
//		profilesPathA= mainDirectory+"/dblp";
//		profilesPathB= mainDirectory+"/scholar";
//		groundTruthPath =  mainDirectory+ "/groundtruth"; 
		
		profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
		profiles[1] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathB);
 
		BilateralDuplicatePropagation adp =new BilateralDuplicatePropagation(groundTruthPath);
		

        //List<EntityProfile>[] profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
        AbstractBlockingMethod blocking;

        Instant start = Instant.now();

        if (profiles.length > 1) {
            if (blocking_type == "M") {
                blocking = new TokenBlocking(new List[]{profiles[0], profiles[1]});
                
            } else {
                //blocking = new TokenBlocking(new List[]{profiles[0]});
                blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
                //blocking = new AttributeClusteringBlocking_original(RepresentationModel.TOKEN_UNIGRAMS, Exp_Util.getEntitiesPath(BASEPATH, dataset, CLEAN));
            }
        } else {
            System.out.println("\nok\n");
            //blocking = new TokenBlocking(new List[]{profiles[0]});
            blocking = new BlockBuilding.MemoryBased.AttributeClusteringBlockingEntropy(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
            //blocking = new AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, new List[]{profiles[0]});

        }
        List<AbstractBlock> blocks = blocking.buildBlocks();
//		EntityProfile temp = profiles[0].get(0);
//		
//		Set<Attribute> att = temp.getAttributes();
//		
//		Iterator<Attribute> tt = att.iterator();
//		
//		//Attribute;
//		Attribute aaa = null;
//		while(tt.hasNext()){
//			aaa= tt.next();
//			System.out.print(aaa.getValue() +" " );
//		}
        System.out.println("n. of blocks: " + blocks.size());
        double SMOOTHING_FACTOR = 1.005; // CLEAN
        //double SMOOTHING_FACTOR = 1.0; // CLEAN Dbpedia
        //double SMOOTHING_FACTOR = 1.015; // DIRTY
        double FILTERING_RATIO = 0.8;

        // TODO NOTICE DOWN:
        //FOR CENSUS
//        double SMOOTHING_FACTOR = 1.05; // DIRTY
//        double FILTERING_RATIO = 1; //


        Instant start_purging = Instant.now();
        System.out.println("blocking time: " + Duration.between(start, start_purging));

        ComparisonsBasedBlockPurging cbbp = new ComparisonsBasedBlockPurging(SMOOTHING_FACTOR);
        cbbp.applyProcessing(blocks,adp);

        System.out.println("\n01: " + blocks.get(0).getEntropy() + "\n\n");

        BlockFiltering bf = new BlockFiltering(FILTERING_RATIO);
        bf.applyProcessing(blocks,adp);

        System.out.println("\n02: " + blocks.get(0).getEntropy() + "\n\n");


        System.out.println("n. of blocks: " + blocks.size());

      //  adp = Exp_Util.getGroundTruth(mainDirectory, groundTruthPath, CLEAN);


        Instant start_blast = Instant.now();

        System.out.println("block purging_filtering time: " + Duration.between(start_purging, start_blast));
        for(int i=0; i< blocks.size();i++)
        {
        	//System.out.println("\nmain: " + blocks.get(i).getEntropy() + "");       	
        	
        }
        BlastWeightedNodePruning b_wnp = new BlastWeightedNodePruning(adp, ws, th_schme, blocks.size());
        
      //  OnTheFlyMethods.
        
       // RedefinedWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning(adp, ws, th_schme, blocks.size());
        //OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.RedefinedWeightedNodePruning(adp, ws, th_schme, blocks.size());
       // OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning b_wnp = new OnTheFlyMethods.FastImplementations.ReciprocalWeightedNodePruning(adp, ws, th_schme, blocks.size());
     //   BlastWeightedNodePruning b_wnp = new BlastWeightedNodePruning(adp, ws, th_schme, blocks.size());
        
        ExecuteBlockComparisons ebc =new ExecuteBlockComparisons(profiles);
        b_wnp.applyProcessing(blocks,adp,ebc);
        double[] values = b_wnp.getPerformance();

        System.out.println("pc: " + values[0]);
        System.out.println("pq: " + values[1]);
        System.out.println("f1: " + (2 * values[0] * values[1]) / (values[0] + values[1]));

        Instant end_blast = Instant.now();

        System.out.println("blast time: " + Duration.between(start_blast, end_blast));
        System.out.println("Total time: " + Duration.between(start, end_blast));
    }
}