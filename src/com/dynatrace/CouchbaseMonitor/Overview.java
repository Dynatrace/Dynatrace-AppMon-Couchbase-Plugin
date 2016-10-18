package com.dynatrace.CouchbaseMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.dynatrace.diagnostics.pdk.Status;


public class Overview {
	
	//private String Type;
	String tempValue = "0";
	String json;
	boolean CouchbaseStreamSuccess = true;
	JSONArray Temp_Array = new JSONArray();
	JSONArray Converted_Array = new JSONArray();
	JSONObject op = new JSONObject();
	JSONObject samples = new JSONObject();
	
	//Couchbase Metrics
    private static double items = 0;
    private static double memoryUsed = 0;
    private static double highWatermark = 0;
    private static double cacheFetched = 0;
    private static double cacheRetrieved = 0;
    private static double activeDocsResRatio = 0;
    private static double replicaDocsResRatio = 0;
    private static double oomPerSecond = 0;
    private static double tmpOOMPerSecond = 0;
    private static double memoryMax = 0;
    private static double diskQueueSize = 0;
    private static double diskQueueFlush = 0;
    private static double diskReads = 0;
    private static double diskWrites = 0;
    private static double dcpQueue = 0;
    private static double activevBucket = 0;
    private static double replicavBucket = 0;
    private static double cpuUtilizationRate = 0;
    private static double currConnections = 0;
    private static double memActualUsed = 0;
    private static double epMemHighWat = 0;
    private static double couchDocsFragmentation = 0;
    private static double cmdGet = 0;
    private static double cmdSet = 0;
    private static double currItems = 0;
    private static double epCacheMissRate = 0;
    private static double getMisses = 0;
    private static double misses = 0;
    private static double ops = 0;
    
    
	
	private static final Logger log = Logger.getLogger(Overview.class.getName());
	
	public Overview(String server, String ssl, String url, String port, String bucket, String zoom, final String Username, final String Password) throws IOException, ParseException{
		
		//log.info("Making call for JSON");
		
		Authenticator.setDefault (new Authenticator() {
    	    protected PasswordAuthentication getPasswordAuthentication() {
    	        return new PasswordAuthentication (Username, Password.toCharArray());
    	    }
    	});
		//JSONParser parser = new JSONParser();

		String couchbaseURL = ssl + "://" + server + ":" + port + url;
		log.info("URL for Couchbase JSON: " + couchbaseURL);
		
		//Connect to Couchbase host and read JSON
        try {
        	//String userCredentials = Username + ":" + Password;
        	log.info("Trying to read Couchbase Stream...");
        	json = readUrl(couchbaseURL);
        	log.info("Couchbase Stream Read");
        	 //Convert to JSON Object and read contents
        	JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
        	log.info("Retrieving Couchbase JSON");
            //Go to Object Tree
            op = (JSONObject) jsonObject.get("op");
            samples = (JSONObject) op.get(bucket);
        }
		catch (Exception exp) {
			log.severe("The URL did not return the expected Couchbase JSON.  Please double check that the URL is available and that the Monitor configuration is correct.");
			log.log(Level.SEVERE, exp.getMessage(), exp);
			Status error = new Status(Status.StatusCode.ErrorInfrastructure);
    		error.setMessage("The URL did not return the expected Couchbase Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
            log.info("Couchbase Stream Closed");
    		CouchbaseStreamSuccess = false;
    		//return error;
    		return;
		}
        /*finally {
        	//Close Stream
        	in.close();
            log.info("Couchbase Stream Closed");
        }*/
      
        //Couchbase Metrics
        //Get Items
        items = metricRetriever("curr_items");
        memoryUsed = metricRetriever("mem_used");
        highWatermark = metricRetriever("ep_mem_high_wat");
        cacheFetched = metricRetriever("ep_bg_fetched");
        cacheRetrieved = metricRetriever("get_hits");
        activeDocsResRatio = metricRetriever("vb_active_resident_items_ratio");
        replicaDocsResRatio = metricRetriever("vb_replica_resident_items_ratio");
        oomPerSecond = metricRetriever("ep_oom_errors");
        tmpOOMPerSecond = metricRetriever("ep_tmp_oom_errors");
        memoryMax = metricRetriever("ep_kv_size");
        diskQueueSize = metricRetriever("ep_queue_size");
        diskQueueFlush = metricRetriever("ep_flusher_todo");
        activevBucket = metricRetriever("vb_active_num");
        cpuUtilizationRate = metricRetriever("cpu_utilization_rate");
        currConnections = metricRetriever("curr_connections");
        memActualUsed = metricRetriever("mem_actual_used");
        epMemHighWat = metricRetriever("ep_mem_high_wat");
        couchDocsFragmentation = metricRetriever("couch_docs_fragmentation");
        cmdGet = metricRetriever("cmd_get");
        cmdSet = metricRetriever("cmd_set");
        currItems = metricRetriever("curr_items");
        epCacheMissRate = metricRetriever("ep_cache_miss_rate");
        getMisses = metricRetriever("get_misses");
        misses = metricRetriever("misses");
        ops = metricRetriever("ops");
	}
	
	//Read JSON URL
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    }
	    catch (Exception exp) {
			log.severe("The URL did not return the expected Couchbase Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
			log.log(Level.SEVERE, exp.getMessage(), exp);
			Status error = new Status(Status.StatusCode.ErrorInfrastructure);
    		error.setMessage("The URL did not return the expected Couchbase Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
    		//Close Stream
        	reader.close();
            log.info("Couchbase Stream Closed");
    		//return error;
    		return "error";
		}
	    finally {
	        if (reader != null)
	            reader.close();
	        log.info("Couchbase Stream Closed");
	    }
	}
	
	public double metricRetriever (String metricName) {
        double metricValue = 0;
        //Long longMetricValue;
		try {
        	log.fine("Begin arrray evaluation (" + metricName + ")");
        	Temp_Array = (JSONArray) samples.get(metricName);
        	log.fine("Attempting to get component type (" + metricName + ")");
        	Object Temp_Array_Type = Temp_Array.get(0);
        	if (Temp_Array_Type instanceof Long) {
        		log.fine("metricRetriever: Value is a long (" + metricName + ")");
        		metricValue = JSONArrayAnalyzer(Temp_Array);
        	}
        	else {
        		log.fine("Value already a double (" + metricName + ")");
        		//metricValue = JSONArrayDoubleAnalyzer(Temp_Array);
        		metricValue = JSONArrayAnalyzer(Temp_Array);
        	}
        	log.info(metricName + " : " + metricValue);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
		return metricValue;
	}
	
	
    //Average JSON array results for double AND long values
	public static double JSONArrayAnalyzer (JSONArray array) {
    	double totalMetric = 0;
    	double averageMetric = 0;
    	double denom = 0;
    	Long longTemp;
    	double doubleTemp = 0;
    	//Analyze array and return an average of results
    	for(int i=0; i<array.size(); i++){
    		Object Temp_Array_Type = array.get(i);
        	//Check to see if object is a long
    		if (Temp_Array_Type instanceof Long) {
        		longTemp = (long) array.get(i);
        		log.finer("JSONArrayAnalyzer: Retrieved long value = " + longTemp);
        		doubleTemp = longTemp.doubleValue();
        		log.finer("JSONArrayAnalyzer: Newly converted double value = " + doubleTemp);
        	}
        	//Otherwise bring in as a double
    		else {
        		doubleTemp = (double) array.get(i);
        		log.finer("JSONArrayAnalyzer: Retrieved double value = " + doubleTemp);
        	}
        	totalMetric = totalMetric + doubleTemp;
    		log.finer("JSONArrayAnalyzer: totalMetric = " + totalMetric);
    		if (i>0)
    		{
    			denom = i+1;
    			averageMetric = totalMetric/denom;
    		}
        }
    	log.fine("JSONArrayAnalyzer: averageMetric = " + averageMetric);
    	return averageMetric;
    }
	
	//Retrieve JSON metrics for local use
	public double getItems()
	{
		return items;
	}
	
	public double getMemoryUsed()
	{
		return memoryUsed;
	}
	
	public double getHighWatermark()
	{
		return highWatermark;
	}
	
	public double getCacheFetched()
	{
		return cacheFetched;
	}
	
	public double getCacheRetrieved()
	{
		return cacheRetrieved;
	}
	
	public double getActiveDocsResRatio()
	{
		return activeDocsResRatio;
	}
	
	public double getReplicaDocsResRatio()
	{
		return replicaDocsResRatio;
	}
	
	public double getOOMPerSecond()
	{
		return oomPerSecond;
	}
	
	public double getTmpOOMPerSecond()
	{
		return tmpOOMPerSecond;
	}
	
	public double getMemoryMax ()
	{
		return memoryMax;
	}
	
	public double getDiskQueueSize()
	{
		return diskQueueSize;
	}
	
	public double getDiskQueueFlush()
	{
		return diskQueueFlush;
	}
	
	public double getDiskReads()
	{
		return diskReads;
	}
	
	public double getDiskWrites()
	{
		return diskWrites;
	}
	
	public double getDCPQueue()
	{
		return dcpQueue;
	}
	
	public double getActivevBucket()
	{
		return activevBucket;
	}
	
	public double getReplicavBucket()
	{
		return replicavBucket;
	}
	
	public double getCPUUtilizationRate()
	{
		return cpuUtilizationRate;
	}
	
	public double getCurrConnections()
	{
		return currConnections;
	}
	
	public double getMemActualUsed()
	{
		return memActualUsed;
	}
	
	public double getEPMemHighWat()
	{
		return epMemHighWat;
	}
	
	public double getCouchDocsFragmentation()
	{
		return couchDocsFragmentation;
	}
	
	public double getCmdGet()
	{
		return cmdGet;
	}
	
	public double getCmdSet()
	{
		return cmdSet;
	}
	
	public double getCurrItems()
	{
		return currItems;
	}
	
	public double getEPCacheMissRate()
	{
		return epCacheMissRate;
	}
	
	public double getGetMisses()
	{
		return getMisses;
	}
	
	public double getMisses()
	{
		return misses;
	}
	
	public double getOps()
	{
		return ops;
	}
	
	//Retrieve Couchbase Stream Success
	public boolean getCouchbaseStreamSuccess()
	{
		return CouchbaseStreamSuccess;
	}
	
}