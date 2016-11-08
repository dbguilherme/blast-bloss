package Experiments;

import BlockBuilding.AbstractTokenBlocking;
import BlockBuilding.DiskBased.AttributeClusteringBlocking;
import BlockBuilding.MemoryBased.SortedNeighborhoodBlocking;
import BlockBuilding.MemoryBased.TokenBlocking;
import BlockProcessing.AbstractEfficiencyMethod;
import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;
import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.BilateralDuplicatePropagation;
import BlockProcessing.ComparisonRefinement.ComparisonPropagation;
import BlockProcessing.ComparisonRefinement.UnilateralDuplicatePropagation;
import DataStructures.AbstractBlock;
import DataStructures.Comparison;
import DataStructures.EntityProfile;
import MetaBlocking.FastImplementations.CardinalityEdgePruning;
import MetaBlocking.WeightingScheme;
import Utilities.ComparisonIterator;
import Utilities.RepresentationModel;
import Utilities.SerializationUtilities;
import Utilities.BlockStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author giovanni
 */
public class tmp_TempExperiments {

    private static int DATASET = 4;
    private static boolean CLEAN = true;
    private static String BASEPATH = "/Users/gio/Desktop/umich/data/data_blockingFramework/";

    public static void main(String[] args) {

        Instant start = Instant.now();

        List<EntityProfile>[] profiles=new List[1];
        AbstractDuplicatePropagation adp;
//        if (args.length > 0) {
//            BASEPATH = args[0] + "/";
//            //profiles = Utilities.getEntities(BASEPATH + "profiles/", DATASET, CLEAN);
//            profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
           
//        } else {
//            profiles = Utilities.getEntities(DATASET, CLEAN);
//            adp = Utilities.getGroundTruth(DATASET, CLEAN);
//        }
       // String mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/acm";
		//String profilesPathA = mainDirectory+"/dataset";
		//String groundTruthPath = mainDirectory+ "/groundtruth";
        
        String mainDirectory = System.getProperty("user.home")+"/Dropbox/blocagem/bases/sintetica";
        String profilesPathA =  mainDirectory+"/10Kprofiles"	;	
        String groundTruthPath =  mainDirectory+"/10KIdDuplicates";	
		
		//profilesPathB= mainDirectory+"/dataset2_gp";

 

		profiles[0] = (List<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPathA);
		TokenBlocking imtb = new TokenBlocking(profiles);
        List<AbstractBlock> blocks = imtb.buildBlocks();
        //final AbstractDuplicatePropagation duplicatePropagation = new UnilateralDuplicatePropagation(groundTruthPath[i]);
		 adp = new UnilateralDuplicatePropagation(groundTruthPath);
				 
        //AbstractTokenBlocking tb;
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, new List[]{profiles[0]});
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, profiles);
        //BlockBuilding.MemoryBased.AttributeClusteringBlocking ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, profiles);
        //TokenBlocking ac = new TokenBlocking(profiles);


             
//         AbstractEfficiencyMethod blockPurging = new ComparisonsBasedBlockPurging();
//         blockPurging.applyProcessing(blocks);
//         
//         ComparisonPropagation cp = new ComparisonPropagation();
//         cp.applyProcessing(blocks);
//		 
//         BlockStatistics bStats = new BlockStatistics(blocks, adp);
//         
//         bStats.applyProcessing();
         
		 AbstractTokenBlocking ac = null;
//        if (profiles.length > 1) {
//            //ac = new TokenBlocking(profiles);
//            ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_SHINGLING, profiles, 120, 3, true);
//            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, profiles);
//        } else {
            ac = new TokenBlocking(new List[]{profiles[0]});
            //ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, new List[]{profiles[0]});
           ac = new BlockBuilding.MemoryBased.AttributeClusteringBlocking(RepresentationModel.TOKEN_UNIGRAMS, profiles, 300, 5, false);
         //   ac = new BlockBuilding.MemoryBased.TokenBlocking(profiles);
      //  }

       // List<AbstractBlock> blocks = ac.buildBlocks();

        //AbstractDuplicatePropagation adp = Utilities.getGroundTruth(BASEPATH, DATASET, CLEAN);
        //AbstractDuplicatePropagation adp = Utilities.getGroundTruth(DATASET, CLEAN);
        BlockStatistics bStats1 = new BlockStatistics(blocks, adp);
        double[] values = bStats1.applyProcessing();

        Instant end = Instant.now();

        System.out.println("Total time: " + Duration.between(start, end).toString());
    }

}