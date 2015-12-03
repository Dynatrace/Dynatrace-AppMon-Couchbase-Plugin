package com.dynatrace.CouchbaseMonitor;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Monitor;
import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.MonitorMeasure;
import com.dynatrace.diagnostics.pdk.Status;

public class CouchbaseMonitor implements Monitor {

	//Metric Groups
	private static final String METRIC_GROUP_COUCHBASE_STATS = "Couchbase Stats";

	//Individual Metrics
	private static final String MSR_COUCH_ACTIVEDOCSRATIO = "Active Docs Resident Ratio";
	private static final String MSR_COUCH_ACTIVEVBUCKETCNT = "Active vBucket Count";
	private static final String MSR_COUCH_CACHEFETCHED = "Cache Fetched";
	private static final String MSR_COUCH_CACHERETREIVED = "Cache Retrieved";
	private static final String MSR_COUCH_DCPQUEUEITEMS = "DCP Queue Items";
	private static final String MSR_COUCH_DISKREADS = "Disk Reads per Second";
	private static final String MSR_COUCH_DISKWRITEQUEUEFLUSH = "Disk Write Queue Flush";
	private static final String MSR_COUCH_DISKWRITEQUEUESIZE = "Disk Write Queue Size";
	private static final String MSR_COUCH_DISKWRTIES = "Disk Writes per Second";
	private static final String MSR_COUCH_HIGHWATERMARK = "High Watermark";
	private static final String MSR_COUCH_ITEMS = "Items";
	private static final String MSR_COUCH_MEMMAX = "Memory Max";
	private static final String MSR_COUCH_MEMUSED = "Memory Used";
	private static final String MSR_COUCH_OOMSECOND = "OOM per Second";
	private static final String MSR_COUCH_REPLICADOCSRATIO = "Replica Docs Resident Ratio";
	private static final String MSR_COUCH_REPLICAVBUCKETCNT = "REplica vBucket Count";
	private static final String MSR_COUCH_TMPOOMSECOND = "TMP OOM per Second";
	private static final String MSR_COUCH_CPUUTILRATE = "CPU Utilization Rate";
	private static final String MSR_COUCH_CURRCONNECTIONS = "Current Connections";
	private static final String MSR_COUCH_MEMACTUALUSED = "Memory Actual Used";
	private static final String MSR_COUCH_EPMEMHIGHWAT = "EP Memory High Wat";
	private static final String MSR_COUCH_DOCSFRAGMENTATION = "Docs Fragmentation";
	private static final String MSR_COUCH_CMDGET = "Command Get";
	private static final String MSR_COUCH_CMDSET = "Command Set";
	private static final String MSR_COUCH_CURRITEMS = "Current Items";
	private static final String MSR_COUCH_EPCACHEMISSRATE = "EP Cache Miss Rate";
	private static final String MSR_COUCH_GETMISSES = "Get Misses";
	private static final String MSR_COUCH_MISSES = "Misses";
	private static final String MSR_COUCH_OPS = "Ops";
	

	private static final Logger log = Logger.getLogger(CouchbaseMonitor.class
			.getName());

	@Override
	public Status setup(MonitorEnvironment env) throws Exception {
		// TODO
		return new Status(Status.StatusCode.Success);
	}

	@Override
	public Status execute(MonitorEnvironment env) throws Exception {

		try {

			log.info("Starting execute...");

			Boolean couchbaseStreamSuccess = true;
			String Server = env.getHost().getAddress();

			
			log.info("Server : " + Server);

			String SSL = env.getConfigString("SSL");
			
			log.info("SSL : " + SSL);
			
			String URL = env.getConfigString("URL");
			
			log.info("URL : " + URL);

			String Port = env.getConfigString("Port");
			
			log.info("Port : " + Port);

			String User = env.getConfigString("Username");

			log.info("User : " + User);
			
			String Bucket = env.getConfigString("Bucket");

			log.info("Bucket Name : " + Bucket);
			
			String Zoom = env.getConfigString("zoom");

			log.info("Zoom : " + Zoom);

			String Pass = env.getConfigPassword("Password");

			Overview temp = new Overview(Server, SSL, URL, Port, Bucket, Zoom, User, Pass);
			couchbaseStreamSuccess = temp.getCouchbaseStreamSuccess();
			if (couchbaseStreamSuccess){
				Collection<MonitorMeasure> measures;
				//Retrieve and set Couchbase Stats
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_ACTIVEDOCSRATIO)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getActiveDocsResRatio());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_ACTIVEVBUCKETCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getActivevBucket());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CACHEFETCHED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCacheFetched());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CACHERETREIVED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCacheRetrieved());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DCPQUEUEITEMS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getDCPQueue());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DISKREADS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getDiskReads());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DISKWRITEQUEUEFLUSH)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getDiskQueueFlush());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DISKWRITEQUEUESIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getDiskQueueSize());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DISKWRTIES)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getDiskWrites());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_HIGHWATERMARK)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getHighWatermark());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_ITEMS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getItems());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_MEMMAX)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getMemoryMax());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_MEMUSED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getMemoryUsed());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_OOMSECOND)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getOOMPerSecond());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_REPLICADOCSRATIO)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getReplicaDocsResRatio());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_REPLICAVBUCKETCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getReplicavBucket());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_TMPOOMSECOND)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getTmpOOMPerSecond());
					}
				}

				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CPUUTILRATE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCPUUtilizationRate());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CURRCONNECTIONS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrConnections());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_MEMACTUALUSED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getMemActualUsed());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_EPMEMHIGHWAT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getEPMemHighWat());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_DOCSFRAGMENTATION)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCouchDocsFragmentation());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CMDGET)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCmdGet());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CMDSET)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCmdSet());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_CURRITEMS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrItems());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_EPCACHEMISSRATE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getEPCacheMissRate());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_GETMISSES)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getGetMisses());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_MISSES)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getMisses());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COUCHBASE_STATS,
						MSR_COUCH_OPS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getOps());
					}
				}
				

			return new Status(Status.StatusCode.Success);
			}
			else {
				Status error = new Status(Status.StatusCode.ErrorInfrastructure);
	    		error.setMessage("The URL did not return the expected Couchbase Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
	    		return error;
			}
		} catch (Exception exp) {
			log.log(Level.SEVERE, exp.getMessage(), exp);
			throw exp;
		}
	}

	@Override
	public void teardown(MonitorEnvironment env) throws Exception {
		// TODO
	}
}
