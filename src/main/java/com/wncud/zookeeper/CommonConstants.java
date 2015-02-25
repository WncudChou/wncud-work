package com.wncud.zookeeper;

/**
 * Created by jianjunz@jumei.com on 2014/8/11.
 */
public class CommonConstants {

	public static final char SPLIT_CHAR = '.';

	public static final String HBASE_BINLOG_EVENT_TABLE_NAME = "binlog_events";

	public static final String DEFAULT_QUALIFIER_TABLE_NAME = "tableName";

	public static final String DEFAULT_QUALIFIER_DB_NAME = "dbName";

	public static final String DEFAULT_QUALIFIER_DATA = "data";

	public static final String DEFAULT_QUALIFIER_NEXT = "nextKey";

	public static final String DEFAULT_HBASE_CF_STR = "f";
	public static final byte[] DEFAULT_HBASE_CF = "f".getBytes();

	public static final String DEFAULT_OPT = "squirrel_opt";

	public static final String DEFAULT_TS = "squirrel_ts";

	public static final byte[] DEFAULT_OPT_QUALIFIER = DEFAULT_OPT.getBytes();

	public static final byte[] DEFAULT_TS_QUALIFIER = DEFAULT_TS.getBytes();

	public static final int DEFAULT_HBASE_REGION_NUM = 20;

	public static final int SQOOP_IMPORT_RETRY_COUNT = 5;

	public static final String HBASE_TABLE_LATEST_SUFFIX = "LATEST";

	public static final String SPLIT_TABLE_NAME = "_";

	public static final String SQOOP_IMPORT_BANDWIDTH = "sqoop.import.bandwidth";
	public static final long DEFAULT_SQOOP_IMPORT_BANDWIDTH = 3145728;// 3M

	public static final String HBASE_REGION_SPLIT_ROW_COUNT = "hbase.region.split.rowcount";

	public static final long DEFAULT_HBASE_REGION_SPLIT_ROW_COUNT = 2000000;

	// kafka
	public static final String KAFKA_ZK_SESSION_TIMEOUT = "zookeeper.session.timeout.ms";
	public static final String DEFAULT_KAFKA_ZK_SESSION_TIMEOUT = "400";

	public static final String KAFKA_ZK_CONNECT = "kafka.zookeeper.connect";
	public static final String DEFAULT_KAFKA_ZK_CONNECT = "10.1.33.41:2181,10.1.33.42:2181,10.1.33.43:2181";
	public static final String KAFKA_BROKER_LIST = "kafka.metadata.broker.list";
	public static final String DEFAULT_KAFKA_BROKER_LIST = "10.1.33.41:9092";

	// filelog
	public static final String FILELOG_ZK_BASE_SLAVE_PATH = "/squirrel/filelog/slaves";
	public static final String FILELOG_ZK_BASE_MASTER_PATH = "/squirrel/filelog/master";
	public static final String FILELOG_ZK_BASE_AGENT_PATH = "/squirrel/filelog/agents";
	public static final String FILELOG_ZK_BASE_TASK_PATH = "/squirrel/filelog/tasks";
	public static final String FILELOG_ZK_BASE = "/squirrel/filelog";
	
	public static final int FILELOG_SERVER_START_PORT = 8001;
	public static final String FILELOG_SERVER_MAX_TASK = "filelog.slave.max.task";
	public static final String FILELOG_ENHANCE_HOST = "filelog_host";
	public static final String FILELOG_ENHANCE_PATH = "filelog_path";
	public static final String FILELOG_STORE_INTERVAL_KEY = "filelog.store.interval";
	public static final long FILELOG_STORE_INTERVAL = 1000 * 10;
	public static final String FILELOG_HBASE_STORE_PREFIX="log";
	public static final String FILELOG_CONFIG_CENTER_HOST = "filelog.logconfig.center.host";
	public static final String DEFAULT_FILELOG_CONFIG_CENTER_HOST = "10.1.33.41";
	

	// zookeeper
	public static final String ZK_QUORUM_SERVERS = "squirrel.zookeeper.quorum";
	public static final String ZK_SESSION_TIMEOUT_KEY = "zookeeper.session.timeout";
	public static final String ZK_RECOVERY_RETRY_KEY = "zk.recovery.retry";
	public static final String ZK_RECOVERY_RETRY_INTERVALMILL_KEY = "zk.recovery.retry.intervalmill";
	public static final int ZK_RECOVERY_RETRY = 3;
	public static final int ZK_RECOVERY_RETRY_INTERVALMILL = 1000;
	public static final int ZK_SESSION_TIMEOUT = 180 * 1000;
	
	//es
	public static final String ES_ID="_id";
	public static final String ES_DEFAULT_TYPE = "default_type";


}
