package com.wncud.zookeeper.core;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.cloudera.htrace.Trace;
import org.cloudera.htrace.TraceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class RecoverableZooKeeper {
	private static final Logger LOG = LoggerFactory
			.getLogger(RecoverableZooKeeper.class);
	// the actual ZooKeeper client instance
	private ZooKeeper zk;
	private final RetryCounterFactory retryCounterFactory;
	// An identifier of this process in the cluster
	private final String identifier;
	private final byte[] id;
	private Watcher watcher;
	private int sessionTimeout;
	private String quorumServers;
	// private String node;

	// the magic number is to be backward compatible
	/**
	 * Size of boolean in bytes
	 */
	public static final int SIZEOF_BOOLEAN = Byte.SIZE / Byte.SIZE;

	/**
	 * Size of byte in bytes
	 */
	public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;
	/**
	 * Size of int in bytes
	 */
	public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;

	private static final byte MAGIC = (byte) 0XFF;
	private static final int MAGIC_SIZE = SIZEOF_BYTE;
	private static final int ID_LENGTH_OFFSET = MAGIC_SIZE;
	private static final int ID_LENGTH_SIZE = SIZEOF_INT;

	public RecoverableZooKeeper(String quorumServers, int sessionTimeout,
			Watcher watcher, int maxRetries, int retryIntervalMillis)
			throws IOException, NoSuchAlgorithmException, KeeperException,
			InterruptedException {
		this.zk = new ZooKeeper(quorumServers, sessionTimeout, watcher);
		this.retryCounterFactory = new RetryCounterFactory(maxRetries,
				retryIntervalMillis);

		// the identifier = processID@hostName
		this.identifier = ManagementFactory.getRuntimeMXBean().getName();
		LOG.info("The identifier of this process is " + identifier);
		this.id = identifier.getBytes();
		this.watcher = watcher;
		this.sessionTimeout = sessionTimeout;
		this.quorumServers = quorumServers;

		// ������ؽڵ�
	}

	public RecoverableZooKeeper(String quorumServers, int sessionTimeout,
			int maxRetries, int retryIntervalMillis, Watcher watcher,
			String node, String identifier) throws IOException {
		this.identifier = identifier;
		LOG.info("The identifier of this process is " + identifier);
		this.id = identifier.getBytes();
		this.watcher = watcher;
		this.sessionTimeout = sessionTimeout;
		this.quorumServers = quorumServers;
		this.zk = new ZooKeeper(quorumServers, sessionTimeout, watcher);
		this.retryCounterFactory = new RetryCounterFactory(maxRetries,
				retryIntervalMillis);

	}

	public void reconnectAfterExpiration() throws IOException,
			InterruptedException, NoSuchAlgorithmException, KeeperException {
		LOG.info("Closing dead ZooKeeper connection, session" + " was: 0x"
				+ Long.toHexString(zk.getSessionId()));
		zk.close();
		this.zk = new ZooKeeper(this.quorumServers, this.sessionTimeout,
				this.watcher);
		// ������ؽڵ�

		LOG.info("Recreated a ZooKeeper, session" + " is: 0x"
				+ Long.toHexString(zk.getSessionId()));
	}

	/**
	 * s is an idempotent operation. Retry before throwing exception
	 * 
	 * @re existturn A Stat instance
	 */
	public Stat exists(String path, Watcher watcher) throws KeeperException,
			InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				return zk.exists(path, watcher);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "exists");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * exists is an idempotent operation. Retry before throwing exception
	 * 
	 * @return A Stat instance
	 */
	public Stat exists(String path, boolean watch) throws KeeperException,
			InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				return zk.exists(path, watch);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "exists");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * getChildren is an idempotent operation. Retry before throwing exception
	 * 
	 * @return List of children znodes
	 */
	public List<String> getChildren(String path, Watcher watcher)
			throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				return zk.getChildren(path, watcher);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "getChildren");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * getChildren is an idempotent operation. Retry before throwing exception
	 * 
	 * @return List of children znodes
	 */
	public List<String> getChildren(String path, boolean watch)
			throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				return zk.getChildren(path, watch);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "getChildren");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * getData is an idempotent operation. Retry before throwing exception
	 * 
	 * @return Data
	 */
	public byte[] getData(String path, Watcher watcher, Stat stat)
			throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				byte[] revData = zk.getData(path, watcher, stat);
				return this.removeMetaData(revData);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "getData");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * getData is an idemnpotent operation. Retry before throwing exception
	 * 
	 * @return Data
	 */
	public byte[] getData(String path, boolean watch, Stat stat)
			throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		while (true) {
			try {
				byte[] revData = zk.getData(path, watch, stat);
				return this.removeMetaData(revData);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "getData");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	/**
	 * setData is NOT an idempotent operation. Retry may cause BadVersion
	 * Exception Adding an identifier field into the data to check whether
	 * badversion is caused by the result of previous correctly setData
	 * 
	 * @return Stat instance
	 */
	public Stat setData(String path, byte[] data, int version)
			throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		byte[] newData = appendMetaData(data);
		while (true) {
			try {
				return zk.setData(path, newData, version);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "setData");
					break;
				case BADVERSION:
					// try to verify whether the previous setData success or not
					try {
						Stat stat = new Stat();
						byte[] revData = zk.getData(path, false, stat);
						if (Bytes.equals(revData, newData)) {
							// the bad version is caused by previous successful
							// setData
							return stat;
						}
					} catch (KeeperException keeperException) {
						// the ZK is not reliable at this moment. just throwing
						// exception
						throw keeperException;
					}

					// throw other exceptions and verified bad version
					// exceptions
				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			retryCounter.useRetry();
		}
	}

	public byte[] removeMetaData(byte[] data) {
		if (data == null || data.length == 0) {
			return data;
		}
		// check the magic data; to be backward compatible
		byte magic = data[0];
		if (magic != MAGIC) {
			return data;
		}

		int idLength = Bytes.toInt(data, ID_LENGTH_OFFSET);
		int dataLength = data.length - MAGIC_SIZE - ID_LENGTH_SIZE - idLength;
		int dataOffset = MAGIC_SIZE + ID_LENGTH_SIZE + idLength;

		byte[] newData = new byte[dataLength];
		System.arraycopy(data, dataOffset, newData, 0, dataLength);

		return newData;

	}

	private byte[] appendMetaData(byte[] data) {
		if (data == null || data.length == 0) {
			return data;
		}

		byte[] newData = new byte[MAGIC_SIZE + ID_LENGTH_SIZE + id.length
				+ data.length];
		int pos = 0;
		pos = Bytes.putByte(newData, pos, MAGIC);
		pos = Bytes.putInt(newData, pos, id.length);
		pos = Bytes.putBytes(newData, pos, id, 0, id.length);
		pos = Bytes.putBytes(newData, pos, data, 0, data.length);

		return newData;
	}

	public long getSessionId() {
		return zk.getSessionId();
	}

	public void close() throws InterruptedException {
		zk.close();
	}

	public States getState() {
		return zk.getState();
	}

	public ZooKeeper getZooKeeper() {
		return zk;
	}

	public byte[] getSessionPasswd() {
		return zk.getSessionPasswd();
	}

	public void sync(String path, AsyncCallback.VoidCallback cb, Object ctx) {
		this.zk.sync(path, null, null);
	}

	private void retryOrThrow(RetryCounter retryCounter, KeeperException e,
			String opName) throws KeeperException {
		LOG.warn("Possibly transient ZooKeeper exception: " + e);
		if (!retryCounter.shouldRetry()) {
			LOG.error("ZooKeeper " + opName + " failed after "
					+ retryCounter.getMaxRetries() + " retries");
			throw e;
		}
	}

	/**
	 * <p>
	 * NONSEQUENTIAL create is idempotent operation. Retry before throwing
	 * exceptions. But this function will not throw the NodeExist exception back
	 * to the application.
	 * </p>
	 * <p>
	 * But SEQUENTIAL is NOT idempotent operation. It is necessary to add
	 * identifier to the path to verify, whether the previous one is successful
	 * or not.
	 * </p>
	 *
	 * @return Path
	 */
	public String create(String path, byte[] data, List<ACL> acl,
			CreateMode createMode) throws KeeperException, InterruptedException {
		TraceScope traceScope = null;
		try {
			traceScope = Trace.startSpan("RecoverableZookeeper.create");
			byte[] newData = appendMetaData(data);
			switch (createMode) {
			case EPHEMERAL:
			case PERSISTENT:
				return createNonSequential(path, newData, acl, createMode);

			case EPHEMERAL_SEQUENTIAL:
			case PERSISTENT_SEQUENTIAL:
				return createSequential(path, newData, acl, createMode);

			default:
				throw new IllegalArgumentException("Unrecognized CreateMode: "
						+ createMode);
			}
		} finally {
			if (traceScope != null)
				traceScope.close();
		}
	}

	private String createNonSequential(String path, byte[] data, List<ACL> acl,
			CreateMode createMode) throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		boolean isRetry = false; // False for first attempt, true for all
									// retries.
		while (true) {
			try {
				return zk.create(path, data, acl, createMode);
			} catch (KeeperException e) {
				switch (e.code()) {
				case NODEEXISTS:
					if (isRetry) {
						// If the connection was lost, there is still a
						// possibility that
						// we have successfully created the node at our previous
						// attempt,
						// so we read the node and compare.
						byte[] currentData = zk.getData(path, false, null);
						if (currentData != null
								&& Bytes.compareTo(currentData, data) == 0) {
							// We successfully created a non-sequential node
							return path;
						}
						LOG.error("Node " + path + " already exists with "
								+ Bytes.toStringBinary(currentData)
								+ ", could not write "
								+ Bytes.toStringBinary(data));
						throw e;
					}
					LOG.info("Node " + path
							+ " already exists and this is not a " + "retry");
					throw e;

				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "create");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
			isRetry = true;
		}
	}

	private String createSequential(String path, byte[] data, List<ACL> acl,
			CreateMode createMode) throws KeeperException, InterruptedException {
		RetryCounter retryCounter = retryCounterFactory.create();
		boolean first = true;
		String newPath = path + this.identifier;
		while (true) {
			try {
				if (!first) {
					// Check if we succeeded on a previous attempt
					String previousResult = findPreviousSequentialNode(newPath);
					if (previousResult != null) {
						return previousResult;
					}
				}
				first = false;
				return zk.create(newPath, data, acl, createMode);
			} catch (KeeperException e) {
				switch (e.code()) {
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case OPERATIONTIMEOUT:
					retryOrThrow(retryCounter, e, "create");
					break;

				default:
					throw e;
				}
			}
			retryCounter.sleepUntilNextRetry();
		}
	}

	private String findPreviousSequentialNode(String path)
			throws KeeperException, InterruptedException {
		int lastSlashIdx = path.lastIndexOf('/');
		assert (lastSlashIdx != -1);
		String parent = path.substring(0, lastSlashIdx);
		String nodePrefix = path.substring(lastSlashIdx + 1);

		List<String> nodes = zk.getChildren(parent, false);
		List<String> matching = filterByPrefix(nodes, nodePrefix);
		for (String node : matching) {
			String nodePath = parent + "/" + node;
			Stat stat = zk.exists(nodePath, false);
			if (stat != null) {
				return nodePath;
			}
		}
		return null;
	}

	/**
	 * Filters the given node list by the given prefixes. This method is
	 * all-inclusive--if any element in the node list starts with any of the
	 * given prefixes, then it is included in the result.
	 *
	 * @param nodes
	 *            the nodes to filter
	 * @param prefixes
	 *            the prefixes to include in the result
	 * @return list of every element that starts with one of the prefixes
	 */
	private static List<String> filterByPrefix(List<String> nodes,
			String... prefixes) {
		List<String> lockChildren = new ArrayList<String>();
		for (String child : nodes) {
			for (String prefix : prefixes) {
				if (child.startsWith(prefix)) {
					lockChildren.add(child);
					break;
				}
			}
		}
		return lockChildren;
	}

	/**
	 * Delete the specified node with the specified version. Sets no watches.
	 * Throws all exceptions.
	 */
	public static boolean deleteNode(ZooKeeperWatcher zkw, String node,
			int version) throws KeeperException {
		try {
			zkw.getRecoverableZooKeeper().delete(node, version);
			return true;
		} catch (KeeperException.BadVersionException bve) {
			return false;
		} catch (InterruptedException ie) {
			zkw.interruptedException(ie);
			return false;
		}
	}

	/**
	 * delete is an idempotent operation. Retry before throwing exception. This
	 * function will not throw NoNodeException if the path does not exist.
	 */
	public void delete(String path, int version) throws InterruptedException,
            KeeperException {
		TraceScope traceScope = null;
		try {
			traceScope = Trace.startSpan("RecoverableZookeeper.delete");
			RetryCounter retryCounter = retryCounterFactory.create();
			boolean isRetry = false; // False for first attempt, true for all
										// retries.
			while (true) {
				try {
					zk.delete(path, version);
					return;
				} catch (KeeperException e) {
					switch (e.code()) {
					case NONODE:
						if (isRetry) {
							LOG.info("Node " + path
									+ " already deleted. Assuming a "
									+ "previous attempt succeeded.");
							return;
						}
						LOG.warn("Node " + path + " already deleted, retry="
								+ isRetry);
						throw e;

					case CONNECTIONLOSS:
					case SESSIONEXPIRED:
					case OPERATIONTIMEOUT:
						retryOrThrow(retryCounter, e, "delete");
						break;

					default:
						throw e;
					}
				}
				retryCounter.sleepUntilNextRetry();
				isRetry = true;
			}
		} finally {
			if (traceScope != null)
				traceScope.close();
		}
	}
}
