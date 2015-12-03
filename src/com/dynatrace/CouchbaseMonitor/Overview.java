package com.dynatrace.CouchbaseMonitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.dynatrace.diagnostics.pdk.Status;
import com.sun.org.apache.xml.internal.security.utils.Base64;


public class Overview {
	
	//private String Type;
	String tempValue = "0";
	String json;
	boolean CouchbaseStreamSuccess = true;
	JSONArray Temp_Array = new JSONArray();
	JSONObject op = new JSONObject();
	JSONObject samples = new JSONObject();
	
	//Couchbase Metrics
    private static long items = 0;
    private static long memoryUsed = 0;
    private static long highWatermark = 0;
    private static long cacheFetched = 0;
    private static long cacheRetrieved = 0;
    private static long activeDocsResRatio = 0;
    private static long replicaDocsResRatio = 0;
    private static long oomPerSecond = 0;
    private static long tmpOOMPerSecond = 0;
    private static long memoryMax = 0;
    private static long diskQueueSize = 0;
    private static long diskQueueFlush = 0;
    private static long diskReads = 0;
    private static long diskWrites = 0;
    private static long dcpQueue = 0;
    private static long activevBucket = 0;
    private static long replicavBucket = 0;
    private static double cpuUtilizationRate = 0;
    private static long currConnections = 0;
    private static long memActualUsed = 0;
    private static long epMemHighWat = 0;
    private static long couchDocsFragmentation = 0;
    private static long cmdGet = 0;
    private static long cmdSet = 0;
    private static long currItems = 0;
    private static long epCacheMissRate = 0;
    private static long getMisses = 0;
    private static long misses = 0;
    private static long ops = 0;
    
    
	
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
        try {
        	Temp_Array = (JSONArray) samples.get("curr_items");
        	items = JSONArrayAnalyzer(Temp_Array);
        	log.info("curr_items : " + items);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Memory Used
        try {
	        Temp_Array = (JSONArray) samples.get("mem_used");
	        memoryUsed = JSONArrayAnalyzer(Temp_Array);
	        log.info("mem_used : " + memoryUsed);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get High Watermark
        try {
	        Temp_Array = (JSONArray) samples.get("ep_mem_high_wat");
	        highWatermark = JSONArrayAnalyzer(Temp_Array);
	        log.info("ep_mem_high_wat : " + highWatermark);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Cache Fetched
        try {
	        Temp_Array = (JSONArray) samples.get("ep_bg_fetched");
	        cacheFetched = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_bg_fetched : " + cacheFetched);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        System.out.println("The return value for the ep_bg_fetched element array: " + cacheFetched);
        
        //Get Cached Retrieved
        try {
	        Temp_Array = (JSONArray) samples.get("get_hits");
	        cacheRetrieved = JSONArrayAnalyzer(Temp_Array);
        	log.info("get_hits : " + cacheRetrieved);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Active Documents Resident Ratio
        try {
	        Temp_Array = (JSONArray) samples.get("vb_active_resident_items_ratio");
	        activeDocsResRatio = JSONArrayAnalyzer(Temp_Array);
	        log.info("vb_active_resident_items_ratio : " + activeDocsResRatio);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Replica Documents Resident Ratio
        try {
	        Temp_Array = (JSONArray) samples.get("vb_replica_resident_items_ratio");
	        replicaDocsResRatio = JSONArrayAnalyzer(Temp_Array);
	        log.info("vb_replica_resident_items_ratio : " + replicaDocsResRatio);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get OOM per Second
        try {
	        Temp_Array = (JSONArray) samples.get("ep_oom_errors");
	        oomPerSecond = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_oom_errors : " + oomPerSecond);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get TMP OOM per Second
        try {
	        Temp_Array = (JSONArray) samples.get("ep_tmp_oom_errors");
	        tmpOOMPerSecond = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_tmp_oom_errors : " + tmpOOMPerSecond);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Maximum Memory
        try {
	        Temp_Array = (JSONArray) samples.get("ep_kv_size");
	        memoryMax = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_kv_size : " + memoryMax);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Disk Queue Size
        try {
	        Temp_Array = (JSONArray) samples.get("ep_queue_size");
	        diskQueueSize = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_queue_size : " + diskQueueSize);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Disk Queue Flush
        try {
	        Temp_Array = (JSONArray) samples.get("ep_flusher_todo");
	        diskQueueFlush = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_flusher_todo : " + diskQueueFlush);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Disk Reads
        // try {
        //Temp_Array = (JSONArray) samples.get("ep_io_num_read");
        //diskReads = JSONArrayAnalyzer(Temp_Array);
        //System.out.println("The return value for the ep_io_num_read element array: " + diskReads);
        
        //Get Disk Writes
        // try {
        //Temp_Array = (JSONArray) samples.get("ep_io_num_write");
        //diskWrites = JSONArrayAnalyzer(Temp_Array);
        //System.out.println("The return value for the ep_io_num_write element array: " + diskWrites);
        
        //Get DCP Queue Size
        // try {
        //Temp_Array = (JSONArray) samples.get("ep_dcp_total_queue");
        //dcpQueue = JSONArrayAnalyzer(Temp_Array);
        //System.out.println("The return value for the ep_dcp_total_queue element array: " + dcpQueue);
        
        //Get Active vBucket Count
        try {
	        Temp_Array = (JSONArray) samples.get("vb_active_num");
	        activevBucket = JSONArrayAnalyzer(Temp_Array);
        	log.info("vb_active_num : " + activevBucket);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get CPU Utilization Rate
        try {
	        Temp_Array = (JSONArray) samples.get("cpu_utilization_rate");
	        cpuUtilizationRate = JSONArrayDoubleAnalyzer(Temp_Array);
        	log.info("cpu_utilization_rate : " + cpuUtilizationRate);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Current Connections
        try {
	        Temp_Array = (JSONArray) samples.get("curr_connections");
	        currConnections = JSONArrayAnalyzer(Temp_Array);
        	log.info("curr_connections : " + currConnections);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Memory Actual Used
        try {
	        Temp_Array = (JSONArray) samples.get("mem_actual_used");
	        memActualUsed = JSONArrayAnalyzer(Temp_Array);
        	log.info("mem_actual_used : " + memActualUsed);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get EP Memory High Wat
        try {
	        Temp_Array = (JSONArray) samples.get("ep_mem_high_wat");
	        epMemHighWat = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_mem_high_wat : " + epMemHighWat);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Couch Docs Fragementation
        try {
	        Temp_Array = (JSONArray) samples.get("couch_docs_fragmentation");
	        couchDocsFragmentation = JSONArrayAnalyzer(Temp_Array);
        	log.info("couch_docs_fragmentation : " + couchDocsFragmentation);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Command Get
        try {
	        Temp_Array = (JSONArray) samples.get("cmd_get");
	        cmdGet = JSONArrayAnalyzer(Temp_Array);
        	log.info("cmd_get : " + cmdGet);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Command Set
        try {
	        Temp_Array = (JSONArray) samples.get("cmd_set");
	        cmdSet = JSONArrayAnalyzer(Temp_Array);
        	log.info("cmd_set : " + cmdSet);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Current Items
        try {
	        Temp_Array = (JSONArray) samples.get("curr_items");
	        currItems = JSONArrayAnalyzer(Temp_Array);
        	log.info("curr_items : " + currItems);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get EP Cache Miss Rate
        try {
	        Temp_Array = (JSONArray) samples.get("ep_cache_miss_rate");
	        epCacheMissRate = JSONArrayAnalyzer(Temp_Array);
        	log.info("ep_cache_miss_rate : " + epCacheMissRate);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Get Misses
        try {
	        Temp_Array = (JSONArray) samples.get("get_misses");
	        getMisses = JSONArrayAnalyzer(Temp_Array);
        	log.info("get_misses : " + getMisses);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Misses
        try {
	        Temp_Array = (JSONArray) samples.get("misses");
	        misses = JSONArrayAnalyzer(Temp_Array);
        	log.info("misses : " + misses);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        
        //Get Ops
        try {
	        Temp_Array = (JSONArray) samples.get("ops");
	        ops = JSONArrayAnalyzer(Temp_Array);
        	log.info("ops : " + ops);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
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
	
	
    //Average JSON array results for long values
	public static long JSONArrayAnalyzer (JSONArray array) {
    	long totalMetric = 0;
    	long averageMetric = 0;
    	long denom = 0;
    	//Analyze array and return an average of results
    	for(int i=0; i<array.size(); i++){
    		long temp = (long) array.get(i);
    		totalMetric = totalMetric + temp;
    		if (i>0)
    		{
    			denom = i+1;
    			averageMetric = totalMetric/denom;
    		}
        }
    	return averageMetric;
    }
	
    //Average JSON array results for double values
	public static double JSONArrayDoubleAnalyzer (JSONArray array) {
    	double totalMetric = 0;
    	double averageMetric = 0;
    	double denom = 0;
    	//Analyze array and return an average of results
    	for(int i=0; i<array.size(); i++){
    		double temp = (double) array.get(i);
    		totalMetric = totalMetric + temp;
    		if (i>0)
    		{
    			denom = i+1;
    			averageMetric = totalMetric/denom;
    		}
        }
    	return averageMetric;
    }
	
	//Retrieve JSON metrics for local use
	public long getItems()
	{
		return items;
	}
	
	public long getMemoryUsed()
	{
		return memoryUsed;
	}
	
	public long getHighWatermark()
	{
		return highWatermark;
	}
	
	public long getCacheFetched()
	{
		return cacheFetched;
	}
	
	public long getCacheRetrieved()
	{
		return cacheRetrieved;
	}
	
	public long getActiveDocsResRatio()
	{
		return activeDocsResRatio;
	}
	
	public long getReplicaDocsResRatio()
	{
		return replicaDocsResRatio;
	}
	
	public long getOOMPerSecond()
	{
		return oomPerSecond;
	}
	
	public long getTmpOOMPerSecond()
	{
		return tmpOOMPerSecond;
	}
	
	public long getMemoryMax ()
	{
		return memoryMax;
	}
	
	public long getDiskQueueSize()
	{
		return diskQueueSize;
	}
	
	public long getDiskQueueFlush()
	{
		return diskQueueFlush;
	}
	
	public long getDiskReads()
	{
		return diskReads;
	}
	
	public long getDiskWrites()
	{
		return diskWrites;
	}
	
	public long getDCPQueue()
	{
		return dcpQueue;
	}
	
	public long getActivevBucket()
	{
		return activevBucket;
	}
	
	public long getReplicavBucket()
	{
		return replicavBucket;
	}
	
	public double getCPUUtilizationRate()
	{
		return cpuUtilizationRate;
	}
	
	public long getCurrConnections()
	{
		return currConnections;
	}
	
	public long getMemActualUsed()
	{
		return memActualUsed;
	}
	
	public long getEPMemHighWat()
	{
		return epMemHighWat;
	}
	
	public long getCouchDocsFragmentation()
	{
		return couchDocsFragmentation;
	}
	
	public long getCmdGet()
	{
		return cmdGet;
	}
	
	public long getCmdSet()
	{
		return cmdSet;
	}
	
	public long getCurrItems()
	{
		return currItems;
	}
	
	public long getEPCacheMissRate()
	{
		return epCacheMissRate;
	}
	
	public long getGetMisses()
	{
		return getMisses;
	}
	
	public long getMisses()
	{
		return misses;
	}
	
	public long getOps()
	{
		return ops;
	}
	
	//Retrieve Couchbase Stream Success
	public boolean getCouchbaseStreamSuccess()
	{
		return CouchbaseStreamSuccess;
	}
	
}