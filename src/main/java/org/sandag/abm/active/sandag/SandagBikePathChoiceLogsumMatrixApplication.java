package org.sandag.abm.active.sandag;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.sandag.abm.active.AbstractPathChoiceLogsumMatrixApplication;
import org.sandag.abm.active.Network;
import org.sandag.abm.active.NodePair;
import org.sandag.abm.active.PathAlternativeList;
import org.sandag.abm.active.PathAlternativeListGenerationConfiguration;
import org.sandag.abm.application.SandagModelStructure;
import org.sandag.abm.ctramp.Person;
import org.sandag.abm.ctramp.Tour;

public class SandagBikePathChoiceLogsumMatrixApplication extends AbstractPathChoiceLogsumMatrixApplication<SandagBikeNode,SandagBikeEdge,SandagBikeTraversal>
{

    private static final String[] MARKET_SEGMENT_NAMES = {"MaleMandatoryOutbound", "MaleMandatoryInbound", "MaleOther", "FemaleMandatoryOutbound", "FemaleMandatoryInbound", "FemaleOther"};
    private static final int[] MARKET_SEGMENT_FEMALE_VALUES = {0,0,0,1,1,1};
    private static final int[] MARKET_SEGMENT_TOUR_PURPOSE_INDICES = {1,1,4,1,1,4};
    private static final boolean[] MARKET_SEGMENT_INBOUND_TRIP_VALUES = {false,true,false,false,true,false};
    
    private ThreadLocal<SandagBikePathChoiceModel> model;
    private Person[] persons;
    private Tour[] tours;
    
    public SandagBikePathChoiceLogsumMatrixApplication(PathAlternativeListGenerationConfiguration<SandagBikeNode,SandagBikeEdge,SandagBikeTraversal> configuration, 
    		                                           final HashMap<String,String> propertyMap)
    {
        super(configuration);
        model = new ThreadLocal<SandagBikePathChoiceModel>() {
        	@Override
        	protected SandagBikePathChoiceModel initialValue() {
        		return new SandagBikePathChoiceModel(propertyMap);
        	}
        };
        persons = new Person[MARKET_SEGMENT_NAMES.length];
        tours = new Tour[MARKET_SEGMENT_NAMES.length];
        
        //for dummy person
        SandagModelStructure modelStructure = new SandagModelStructure();
        for (int i=0; i<MARKET_SEGMENT_NAMES.length; i++) {
            persons[i] = new Person(null,1,modelStructure);
            persons[i].setPersGender(MARKET_SEGMENT_FEMALE_VALUES[i]);
            tours[i] = new Tour(persons[i],1,MARKET_SEGMENT_TOUR_PURPOSE_INDICES[i]);
        }
    }

    @Override
    protected double[] calculateMarketSegmentLogsums(PathAlternativeList<SandagBikeNode, SandagBikeEdge> alternativeList)
    {
        SandagBikePathAlternatives alts = new SandagBikePathAlternatives(alternativeList);
        double[] logsums = new double[MARKET_SEGMENT_NAMES.length]; 
        for (int i=0; i<MARKET_SEGMENT_NAMES.length; i++) {
            logsums[i] = model.get().getPathLogsums(persons[i], alts, MARKET_SEGMENT_INBOUND_TRIP_VALUES[i], tours[i]);
        }
        return logsums;    
    }
    

    
    public static void main(String ... args) {
        String RESOURCE_BUNDLE_NAME = "sandag_abm_active_test";
        HashMap<String,String> propertyMap = new HashMap<String,String>();
        SandagBikeNetworkFactory factory;
        Network<SandagBikeNode, SandagBikeEdge, SandagBikeTraversal> network;
        PathAlternativeListGenerationConfiguration<SandagBikeNode, SandagBikeEdge, SandagBikeTraversal> configuration;
        SandagBikePathChoiceLogsumMatrixApplication application;
        
        ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
        propertyMap = new HashMap<>();
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            propertyMap.put(key, rb.getString(key));
        }
        factory = new SandagBikeNetworkFactory(propertyMap);
        network = factory.createNetwork();
        
        configuration = new SandagBikeTazPathAlternativeListGenerationConfiguration(propertyMap, network);
        application = new SandagBikePathChoiceLogsumMatrixApplication(configuration,propertyMap);
        
        Map<NodePair<SandagBikeNode>,double[]> logsums = application.calculateMarketSegmentLogsums();
        for (NodePair<SandagBikeNode> od : logsums.keySet()) 
        	System.out.println(od + " : " + Arrays.toString(logsums.get(od)));
    }
    
}
