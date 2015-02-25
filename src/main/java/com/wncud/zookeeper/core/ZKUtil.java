package com.wncud.zookeeper.core;

import com.wncud.zookeeper.CommonConstants;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.proto.SetDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class ZKUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ZKUtil.class);

	public static RecoverableZooKeeper connect(String ensemble,
			ZooKeeperWatcher watcher) throws IOException,
			NoSuchAlgorithmException, KeeperException, InterruptedException {

		if (ensemble == null) {
			throw new IOException("Unable to determine ZooKeeper ensemble");
		}

		int timeout = CommonConstants.ZK_SESSION_TIMEOUT;
		LOG.debug(watcher.getBasePath()
				+ " opening connection to ZooKeeper with ensemble (" + ensemble
				+ ")");
		int retry = CommonConstants.ZK_RECOVERY_RETRY;
		int retryIntervalMillis = CommonConstants.ZK_RECOVERY_RETRY_INTERVALMILL;
		return new RecoverableZooKeeper(ensemble, timeout, watcher, retry,
				retryIntervalMillis);
	}

	public static RecoverableZooKeeper connect(String ensemble, int timeout,
			int retryIntervalMillis, int retry, ZooKeeperWatcher watcher)
			throws IOException, NoSuchAlgorithmException, KeeperException,
			InterruptedException {
		if (ensemble == null) {
			throw new IOException("Unable to determine ZooKeeper ensemble");
		}
		LOG.debug(watcher.getBasePath()
				+ " opening connection to ZooKeeper with ensemble (" + ensemble
				+ ")");
		return new RecoverableZooKeeper(ensemble, timeout, watcher, retry,
				retryIntervalMillis);

	}

	//
	// Existence checks and watches
	//

	/**
	 * Watch the specified znode for delete/create/change events. The watcher is
	 * set whether or not the node exists. If the node already exists, the
	 * method returns true. If the node does not exist, the method returns
	 * false.
	 * 
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node to watch
	 * @return true if znode exists, false if does not exist or error
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static boolean watchAndCheckExists(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		try {

			Stat s = zkw.getRecoverableZooKeeper().exists(znode, zkw);
			boolean exists = s != null ? true : false;
			if (exists) {
				LOG.debug(zkw.prefix("Set watcher on existing znode " + znode));
			} else {
				LOG.debug(zkw
						.prefix(znode + " does not exist. Watcher is set."));
			}
			return exists;
		} catch (KeeperException e) {
			LOG.warn(zkw.prefix("Unable to set watcher on znode " + znode), e);
			zkw.keeperException(e);
			return false;
		} catch (InterruptedException e) {
			LOG.warn(zkw.prefix("Unable to set watcher on znode " + znode), e);
			zkw.interruptedException(e);
			return false;
		}
	}

	/**
	 * Get znode data. Does not set a watcher.
	 *
	 * @return ZNode data
	 */
	public static byte[] getData(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		try {
			byte[] data = zkw.getRecoverableZooKeeper().getData(znode, null,
					null);
			// logRetrievedMsg(zkw, znode, data, false);
			return data;
		} catch (KeeperException.NoNodeException e) {
			LOG.debug(zkw.prefix("Unable to get data of znode " + znode + " "
					+ "because node does not exist (not an error)"));
			return null;
		} catch (KeeperException e) {
			LOG.warn(zkw.prefix("Unable to get data of znode " + znode), e);
			zkw.keeperException(e);
			return null;
		} catch (InterruptedException e) {
			LOG.warn(zkw.prefix("Unable to get data of znode " + znode), e);
			zkw.interruptedException(e);
			return null;
		}
	}

	/**
	 * Get the data at the specified znode and set a watch.
	 *
	 * Returns the data and sets a watch if the node exists. Returns null and no
	 * watch is set if the node does not exist or there is an exception.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @return data of the specified znode, or null
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static byte[] getDataAndWatch(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		return getDataInternal(zkw, znode, null, true);
	}

	private static byte[] getDataInternal(ZooKeeperWatcher zkw, String znode,
			Stat stat, boolean watcherSet) throws KeeperException {
		try {
			byte[] data = zkw.getRecoverableZooKeeper().getData(znode, zkw,
					stat);
			return data;
		} catch (KeeperException.NoNodeException e) {
			LOG.debug(zkw.prefix("Unable to get data of znode " + znode + " "
					+ "because node does not exist (not an error)"));
			throw e;
		} catch (KeeperException e) {
			LOG.warn(zkw.prefix("Unable to get data of znode " + znode), e);
			zkw.keeperException(e);
			return null;
		} catch (InterruptedException e) {
			LOG.warn(zkw.prefix("Unable to get data of znode " + znode), e);
			zkw.interruptedException(e);
			return null;
		}
	}

	/**
	 * List all the children of the specified znode, setting a watch for
	 * children changes and also setting a watch on every individual child in
	 * order to get the NodeCreated and NodeDeleted events.
	 *
	 * @param zkw
	 *            zookeeper reference
	 * @param znode
	 *            node to get children of and watch
	 * @return list of znode names, null if the node doesn't exist
	 * @throws org.apache.zookeeper.KeeperException
	 */
	public static List<String> listChildrenAndWatchThem(ZooKeeperWatcher zkw,
			String znode) throws KeeperException {
		List<String> children = listChildrenAndWatchForNewChildren(zkw, znode);
		if (children == null) {
			return null;
		}
		for (String child : children) {
			watchAndCheckExists(zkw, joinZNode(znode, child));
		}
		return children;
	}

	/**
	 * Join the prefix znode name with the suffix znode name to generate a
	 * proper full znode name.
	 *
	 * Assumes prefix does not end with slash and suffix does not begin with it.
	 *
	 * @param prefix
	 *            beginning of znode name
	 * @param suffix
	 *            ending of znode name
	 * @return result of properly joining prefix with suffix
	 */
	public static String joinZNode(String prefix, String suffix) {
		return prefix + ZNODE_PATH_SEPARATOR + suffix;
	}

	/**
	 * Lists the children znodes of the specified znode. Also sets a watch on
	 * the specified znode which will capture a NodeDeleted event on the
	 * specified znode as well as NodeChildrenChanged if any children of the
	 * specified znode are created or deleted.
	 *
	 * Returns null if the specified node does not exist. Otherwise returns a
	 * list of children of the specified node. If the node exists but it has no
	 * children, an empty list will be returned.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node to list and watch children of
	 * @return list of children of the specified node, an empty list if the node
	 *         exists but has no children, and null if the node does not exist
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static List<String> listChildrenAndWatchForNewChildren(
			ZooKeeperWatcher zkw, String znode) throws KeeperException {
		try {
			List<String> children = zkw.getRecoverableZooKeeper().getChildren(
					znode, zkw);
			return children;
		} catch (KeeperException.NoNodeException ke) {
			LOG.debug(zkw.prefix("Unable to list children of znode " + znode
					+ " " + "because node does not exist (not an error)"));
			return null;
		} catch (KeeperException e) {
			LOG.warn(
					zkw.prefix("Unable to list children of znode " + znode
							+ " "), e);
			zkw.keeperException(e);
			return null;
		} catch (InterruptedException e) {
			LOG.warn(
					zkw.prefix("Unable to list children of znode " + znode
							+ " "), e);
			zkw.interruptedException(e);
			return null;
		}
	}

	/**
	 *
	 * Set the specified znode to be an ephemeral node carrying the specified
	 * data.
	 *
	 * If the node is created successfully, a watcher is also set on the node.
	 *
	 * If the node is not created successfully because it already exists, this
	 * method will also set a watcher on the node.
	 *
	 * If there is another problem, a KeeperException will be thrown.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @param data
	 *            data of node
	 * @return true if node created, false if not, watch set in both cases
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static boolean createEphemeralNodeAndWatch(ZooKeeperWatcher zkw,
			String znode, byte[] data) throws KeeperException {
		boolean ret = true;
		try {
			zkw.getRecoverableZooKeeper().create(znode, data,
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (KeeperException.NodeExistsException nee) {
			ret = false;
		} catch (InterruptedException e) {
			LOG.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
		if (!watchAndCheckExists(zkw, znode)) {
			// It did exist but now it doesn't, try again
			return createEphemeralNodeAndWatch(zkw, znode, data);
		}
		return ret;
	}

	/**
	 * Creates the specified node, if the node does not exist. Does not set a
	 * watch and fails silently if the node already exists.
	 *
	 * The node created is persistent and open access.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static void createAndFailSilent(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		createAndFailSilent(zkw, znode, new byte[0]);
	}

	/**
	 * Creates the specified node containing specified data, iff the node does
	 * not exist. Does not set a watch and fails silently if the node already
	 * exists.
	 *
	 * The node created is persistent and open access.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @param data
	 *            a byte array data to store in the znode
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static void createAndFailSilent(ZooKeeperWatcher zkw, String znode,
			byte[] data) throws KeeperException {
		createAndFailSilent(zkw,
				(ZKUtilOp.CreateAndFailSilent) ZKUtilOp.createAndFailSilent(
						znode, data));
	}

	private static void createAndFailSilent(ZooKeeperWatcher zkw,
			ZKUtilOp.CreateAndFailSilent cafs) throws KeeperException {
		CreateRequest create = (CreateRequest) toZooKeeperOp(zkw, cafs)
				.toRequestRecord();
		String znode = create.getPath();
		try {
			RecoverableZooKeeper zk = zkw.getRecoverableZooKeeper();
			if (zk.exists(znode, false) == null) {
				zk.create(znode, create.getData(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.fromFlag(create.getFlags()));
			}
		} catch (KeeperException.NodeExistsException nee) {
		} catch (KeeperException.NoAuthException nee) {
			try {
				if (null == zkw.getRecoverableZooKeeper().exists(znode, false)) {
					// If we failed to create the file and it does not already
					// exist.
					throw (nee);
				}
			} catch (InterruptedException ie) {
				zkw.interruptedException(ie);
			}

		} catch (InterruptedException ie) {
			zkw.interruptedException(ie);
		}
	}

	/**
	 * Convert from ZKUtilOp to ZKOp
	 */
	private static Op toZooKeeperOp(ZooKeeperWatcher zkw, ZKUtilOp op)
			throws UnsupportedOperationException {
		if (op == null)
			return null;

		if (op instanceof ZKUtilOp.CreateAndFailSilent) {
			ZKUtilOp.CreateAndFailSilent cafs = (ZKUtilOp.CreateAndFailSilent) op;
			return Op.create(cafs.getPath(), cafs.getData(),
					Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
		} else if (op instanceof ZKUtilOp.DeleteNodeFailSilent) {
			ZKUtilOp.DeleteNodeFailSilent dnfs = (ZKUtilOp.DeleteNodeFailSilent) op;
			return Op.delete(dnfs.getPath(), -1);
		} else if (op instanceof ZKUtilOp.SetData) {
			ZKUtilOp.SetData sd = (ZKUtilOp.SetData) op;
			return Op.setData(sd.getPath(), sd.getData(), -1);
		} else {
			throw new UnsupportedOperationException(
					"Unexpected ZKUtilOp type: " + op.getClass().getName());
		}
	}

	public abstract static class ZKUtilOp {
		private String path;

		private ZKUtilOp(String path) {
			this.path = path;
		}

		/**
		 * @return a createAndFailSilent ZKUtilOp
		 */
		public static ZKUtilOp createAndFailSilent(String path, byte[] data) {
			return new CreateAndFailSilent(path, data);
		}

		/**
		 * @return a deleteNodeFailSilent ZKUtilOP
		 */
		public static ZKUtilOp deleteNodeFailSilent(String path) {
			return new DeleteNodeFailSilent(path);
		}

		/**
		 * @return a setData ZKUtilOp
		 */
		public static ZKUtilOp setData(String path, byte[] data) {
			return new SetData(path, data);
		}

		/**
		 * @return path to znode where the ZKOp will occur
		 */
		public String getPath() {
			return path;
		}

		/**
		 * ZKUtilOp representing createAndFailSilent in ZooKeeper (attempt to
		 * create node, ignore error if already exists)
		 */
		public static class CreateAndFailSilent extends ZKUtilOp {
			private byte[] data;

			private CreateAndFailSilent(String path, byte[] data) {
				super(path);
				this.data = data;
			}

			public byte[] getData() {
				return data;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (!(o instanceof CreateAndFailSilent))
					return false;

				CreateAndFailSilent op = (CreateAndFailSilent) o;
				return getPath().equals(op.getPath())
						&& Arrays.equals(data, op.data);
			}
		}

		/**
		 * ZKUtilOp representing deleteNodeFailSilent in ZooKeeper (attempt to
		 * delete node, ignore error if node doesn't exist)
		 */
		public static class DeleteNodeFailSilent extends ZKUtilOp {
			private DeleteNodeFailSilent(String path) {
				super(path);
			}

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (!(o instanceof DeleteNodeFailSilent))
					return false;

				return super.equals(o);
			}
		}

		/**
		 * @return ZKUtilOp representing setData in ZooKeeper
		 */
		public static class SetData extends ZKUtilOp {
			private byte[] data;

			private SetData(String path, byte[] data) {
				super(path);
				this.data = data;
			}

			public byte[] getData() {
				return data;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (!(o instanceof SetData))
					return false;

				SetData op = (SetData) o;
				return getPath().equals(op.getPath())
						&& Arrays.equals(data, op.data);
			}
		}
	}

	//
	// Data setting
	//

	/**
	 * Sets the data of the existing znode to be the specified data. Ensures
	 * that the current data has the specified expected version.
	 *
	 * <p>
	 * If the node does not exist, a
	 * {@link org.apache.zookeeper.KeeperException.NoNodeException} will be
	 * thrown.
	 *
	 * <p>
	 * If their is a version mismatch, method returns null.
	 *
	 * <p>
	 * No watches are set but setting data will trigger other watchers of this
	 * node.
	 *
	 * <p>
	 * If there is another problem, a KeeperException will be thrown.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @param data
	 *            data to set for node
	 * @param expectedVersion
	 *            version expected when setting data
	 * @return true if data set, false if version mismatch
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static boolean setData(ZooKeeperWatcher zkw, String znode,
			byte[] data, int expectedVersion) throws KeeperException,
			NoNodeException {
		try {
			return zkw.getRecoverableZooKeeper().setData(znode, data,
					expectedVersion) != null;
		} catch (InterruptedException e) {
			zkw.interruptedException(e);
			return false;
		}
	}

	/**
	 * Sets the data of the existing znode to be the specified data. The node
	 * must exist but no checks are done on the existing data or version.
	 *
	 * <p>
	 * If the node does not exist, a
	 * {@link org.apache.zookeeper.KeeperException.NoNodeException} will be
	 * thrown.
	 *
	 * <p>
	 * No watches are set but setting data will trigger other watchers of this
	 * node.
	 *
	 * <p>
	 * If there is another problem, a KeeperException will be thrown.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node
	 * @param data
	 *            data to set for node
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static void setData(ZooKeeperWatcher zkw, String znode, byte[] data)
			throws KeeperException, NoNodeException {
		setData(zkw, (ZKUtilOp.SetData) ZKUtilOp.setData(znode, data));
	}

	private static void setData(ZooKeeperWatcher zkw, ZKUtilOp.SetData setData)
			throws KeeperException, NoNodeException {
		SetDataRequest sd = (SetDataRequest) toZooKeeperOp(zkw, setData)
				.toRequestRecord();
		setData(zkw, sd.getPath(), sd.getData(), sd.getVersion());
	}

	/**
	 * Delete the specified node. Sets no watches. Throws all exceptions.
	 */
	public static void deleteNode(ZooKeeperWatcher zkw, String node)
			throws KeeperException {
		deleteNode(zkw, node, -1);
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
	 * Check if the specified node exists. Sets no watches.
	 *
	 * @param zkw
	 *            zk reference
	 * @param znode
	 *            path of node to watch
	 * @return version of the node if it exists, -1 if does not exist
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static int checkExists(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		try {
			Stat s = zkw.getRecoverableZooKeeper().exists(znode, null);
			return s != null ? s.getVersion() : -1;
		} catch (KeeperException e) {
			LOG.warn(zkw.prefix("Unable to set watcher on znode (" + znode
					+ ")"), e);
			zkw.keeperException(e);
			return -1;
		} catch (InterruptedException e) {
			LOG.warn(zkw.prefix("Unable to set watcher on znode (" + znode
					+ ")"), e);
			zkw.interruptedException(e);
			return -1;
		}
	}

	public static boolean exist(ZooKeeperWatcher zkw, String znode)
			throws KeeperException {
		if (checkExists(zkw, znode) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * Lists the children of the specified znode without setting any watches.
	 *
	 * Used to list the currently online regionservers and their addresses.
	 *
	 * Sets no watches at all, this method is best effort.
	 *
	 * Returns an empty list if the node has no children. Returns null if the
	 * parent node itself does not exist.
	 *
	 * @param zkw
	 *            zookeeper reference
	 * @param znode
	 *            node to get children of as addresses
	 * @return list of data of children of specified znode, empty if no
	 *         children, null if parent does not exist
	 * @throws org.apache.zookeeper.KeeperException
	 *             if unexpected zookeeper exception
	 */
	public static List<String> listChildrenNoWatch(ZooKeeperWatcher zkw,
			String znode) throws KeeperException {
		List<String> children = null;
		try {
			// List the children without watching
			children = zkw.getRecoverableZooKeeper().getChildren(znode, null);
		} catch (NoNodeException nne) {
			return null;
		} catch (InterruptedException ie) {
			zkw.interruptedException(ie);
		}
		return children;
	}

	private static final char ZNODE_PATH_SEPARATOR = '/';

	public static void main(String[] args) throws UnknownHostException {
		String appName = System.getProperty("user.dir");
		appName = appName.substring(appName.lastIndexOf(File.separator) + 1);
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}

}
