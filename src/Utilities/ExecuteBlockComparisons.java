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

package Utilities;

import DataStructures.AbstractBlock;
import DataStructures.Attribute;
import DataStructures.Comparison;
import DataStructures.EntityProfile;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.tokenisers.TokeniserQGram3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gap2
 */
public class ExecuteBlockComparisons {
 
    private final EntityProfile[] dataset1;
    private final EntityProfile[] dataset2;
    
    public ExecuteBlockComparisons(String[] profilesPath) {
        dataset1 = loadProfiles(profilesPath[0]);
        System.out.println("Entities 1\t:\t" + dataset1.length);
        if (profilesPath.length == 2) {
            dataset2 = loadProfiles(profilesPath[1]);
            System.out.println("Entities 2\t:\t" + dataset2.length);
        } else {
            dataset2 = null;
        }
    }
    
    public ExecuteBlockComparisons(List<EntityProfile>[] profiles) {
        dataset1 = profiles[0].toArray(new EntityProfile[profiles[0].size()]);
        System.out.println("Entities 1\t:\t" + dataset1.length);
        if (profiles.length == 2) {
            dataset2 = profiles[1].toArray(new EntityProfile[profiles[1].size()]);
            System.out.println("Entities 2\t:\t" + dataset2.length);
        } else {
            dataset2 = null;
        }
    }
    
    public long comparisonExecution(List<AbstractBlock> blocks) {
        long startingTime = System.currentTimeMillis();
        for (AbstractBlock block : blocks) {
            ComparisonIterator iterator = block.getComparisonIterator();
            while (iterator.hasNext()) {
                Comparison comparison = iterator.next();
                if (dataset2 != null) {
                    ProfileComparison.getJaccardSimilarity(dataset1[comparison.getEntityId1()].getAttributes(), 
                                                           dataset2[comparison.getEntityId2()].getAttributes());
                } else {
                    ProfileComparison.getJaccardSimilarity(dataset1[comparison.getEntityId1()].getAttributes(), 
                                                           dataset1[comparison.getEntityId2()].getAttributes());
                }
            }
        }
        long endingTime = System.currentTimeMillis();
        return endingTime-startingTime;
    }
    
    private EntityProfile[] loadProfiles(String profilesPath) {
        List<EntityProfile> entityProfiles = (ArrayList<EntityProfile>) SerializationUtilities.loadSerializedObject(profilesPath);
        EntityProfile[] e =entityProfiles.toArray(new EntityProfile[entityProfiles.size()]);
        for (EntityProfile entityProfile : entityProfiles) {
        	//entityProfile.getatt(entityProfile);
        	entityProfile.att_value="";
        	for ( Attribute att : entityProfile.getAttributes() ) {
        		entityProfile.att_value=entityProfile.att_value.concat(att.getValue().toLowerCase().trim().replaceAll("[\\W]|_", " ")+ "  ");
    		}
		}
        return e;
    }
    public Set<Attribute> exportEntityA(int entityIds1) {
		return dataset1[entityIds1].getAttributes();
		
	}
	public Set<Attribute> exportEntityB(int entityIds1) {
		if(dataset2!=null)
			return dataset2[entityIds1].getAttributes();
		return dataset1[entityIds1].getAttributes();
		
	}
	public double  getSImilarity (int entityIds1, int entityIds2){
//		for(Attribute att:dataset1[entityIds1].getAttributes()){
//			//if(att.getName().value(""))
//    			//profile1.add(att);
//			System.out.print(att.getValue()+ " :");
//		}
//		System.out.print( " ");
//		for(Attribute att:dataset2[entityIds2].getAttributes()){
//			//if(att.getName().value(""))
//    			//profile1.add(att);
//			System.out.print(att.getValue()+ " :");
//		}
//		System.out.println();
		if(dataset2==null){
			return ProfileComparison.getJaccardSimilarity(dataset1[entityIds1].getAttributes(), 
	                 dataset1[entityIds2].getAttributes());
		}else
			return ProfileComparison.getJaccardSimilarity(dataset1[entityIds1].getAttributes(), 
	                 dataset2[entityIds2].getAttributes());
		
		 
	}

	public double  getSimilarityAttribute (int entityIds1, int entityIds2){

		TokeniserQGram3 tok =new TokeniserQGram3();
		JaccardSimilarity jc =new JaccardSimilarity();
		//JaroWinkler jw = new JaroWinkler();
		//Levenshtein le= new Levenshtein();
		//System.out.println(dataset1[entityIds1].att_value + " --------" + dataset2[entityIds2].att_value);
		if(dataset2!=null)
			return jc.getSimilarity(dataset1[entityIds1].att_value,dataset2[entityIds2].att_value );
		else
			return jc.getSimilarity(dataset1[entityIds1].att_value,dataset1[entityIds2].att_value);
	}
}