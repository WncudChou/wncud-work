package com.wncud.zookeeper.core;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZooKeeperWatcher implements Watcher {
	private static final Logger LOG = LoggerFactory
			.getLogger(ZooKeeperWatcher.class);

	// zookeeper connection
	private RecoverableZooKeeper recoverableZooKeeper;
	private String quorumServers;
	private String basePath;
	private int sessionTimeout;

	private int maxRetries;
	private int retryIntervalMillis;

	// listeners to be notified
	private final List<ZooKeeperListener> listeners = new CopyOnWriteArrayList<ZooKeeperListener>();

	public ZooKeeperWatcher(String quorumServers, int sessionTimeout,
			int maxRetries, int retryIntervalMillis)
			throws NoSuchAlgorithmException, IOException, KeeperException,
			InterruptedException {
		super();
		this.quorumServers = quorumServers;
		this.sessionTimeout = sessionTimeout;
		this.maxRetries = maxRetries;
		this.retryIntervalMillis = retryIntervalMillis;
		recoverableZooKeeper = ZKUtil.connect(quorumServers, sessionTimeout,
				retryIntervalMillis, maxRetries, this);
	}


	private void createZNodes(String path) throws KeeperException {
		String[] nodes = path.split("/");
		String parent = "";
		for (int i = 0; i < nodes.length; i++) {
			if (StringUtils.isEmpty(nodes[i])) {
				continue;
			}
			parent = parent + "/" + nodes[i];
			ZKUtil.createAndFailSilent(this, parent);
		}
	}

	public void process(WatchedEvent event) {
		LOG.debug(prefix("Received ZooKeeper Event, " + "type="
				+ event.getType() + ", " + "state=" + event.getState() + ", "
				+ "path=" + event.getPath()));
		switch (event.getType()) {
		// If event type is NONE, this is a connection status change
		case None: {
			try {
				connectionEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		// Otherwise pass along to the listeners

		case NodeCreated: {
			for (ZooKeeperListener listener : listeners) {
				listener.nodeCreated(event.getPath());
			}
			break;
		}

		case NodeDeleted: {
			for (ZooKeeperListener listener : listeners) {
				listener.nodeDeleted(event.getPath());
			}
			break;
		}

		case NodeDataChanged: {
			for (ZooKeeperListener listener : listeners) {
				listener.nodeDataChanged(event.getPath());
			}
			break;
		}

		case NodeChildrenChanged:
			for (ZooKeeperListener listener : listeners) {
				listener.nodeChildrenChanged(event.getPath());
			}
			break;

		default:
			break;
		}
	}

	protected void connectionEvent(WatchedEvent event)
			throws NoSuchAlgorithmException, IOException, KeeperException,
			InterruptedException {
		switch (event.getState()) {
		case SyncConnected:
			// Now, this callback can be invoked before the this.zookeeper is
			// set.
			// Wait a little while.
			long finished = System.currentTimeMillis() + 2000;
			while (System.currentTimeMillis() < finished) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (this.recoverableZooKeeper != null)
					break;
			}
			if (this.recoverableZooKeeper == null) {
				// LOG.error(
				// "ZK is null on connection event -- see stack trace "
				// +
				// "for the stack trace when constructor was called on this zkw",
				// this.constructorCaller);
				// throw new NullPointerException("ZK is null");
				recoverableZooKeeper = new RecoverableZooKeeper(quorumServers,
						sessionTimeout, this, maxRetries, retryIntervalMillis);
			}

			LOG.debug(this.basePath + " connected");
			break;

		// Abort the server if Disconnected or Expired
		case Disconnected:
			LOG.debug(prefix("Received Disconnected from ZooKeeper, ignoring"));
			break;

		case Expired:
			String msg = prefix(this.basePath + " received expired from "
					+ "ZooKeeper, aborting");
			LOG.warn(msg);
			// TODO: One thought is to add call to ZooKeeperListener so say,
			// ZooKeeperNodeTracker can zero out its data values.

			throw new KeeperException.SessionExpiredException();
		default:
			break;
		}
	}

	/**
	 * Adds this instance's identifier as a prefix to the passed
	 * <code>str</code>
	 * 
	 * @param str
	 *            String to amend.
	 * @return A new string with this instance's identifier as prefix: e.g. if
	 *         passed 'hello world', the returned string could be
	 */
	public String prefix(final String str) {
		return this.toString() + " " + str;
	}

	/**
	 * Handles KeeperExceptions in client calls.
	 * <p>
	 * This may be temporary but for now this gives one place to deal with
	 * these.
	 * <p>
	 * TODO: Currently this method rethrows the exception to let the caller
	 * handle
	 * <p>
	 * 
	 * @param ke
	 * @throws org.apache.zookeeper.KeeperException
	 */
	public void keeperException(KeeperException ke) throws KeeperException {
		LOG.error(
				prefix("Received unexpected KeeperException, re-throwing exception"),
				ke);
		throw ke;
	}

	/**
	 * Handles InterruptedExceptions in client calls.
	 * <p>
	 * This may be temporary but for now this gives one place to deal with
	 * these.
	 * <p>
	 * TODO: Currently, this method does nothing. Is this ever expected to
	 * happen? Do we abort or can we let it run? Maybe this should be logged as
	 * WARN? It shouldn't happen?
	 * <p>
	 * 
	 * @param ie
	 */
	public void interruptedException(InterruptedException ie) {
		LOG.debug(prefix("Received InterruptedException, doing nothing here"),
				ie);
		// At least preserver interrupt.
		Thread.currentThread().interrupt();
		// no-op
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public RecoverableZooKeeper getRecoverableZooKeeper() {
		return recoverableZooKeeper;
	}

	public void setRecoverableZooKeeper(
			RecoverableZooKeeper recoverableZooKeeper) {
		this.recoverableZooKeeper = recoverableZooKeeper;
	}

	/**
	 * Register the specified listener to receive ZooKeeper events.
	 * 
	 * @param listener
	 */
	public void registerListener(ZooKeeperListener listener) {
		listeners.add(listener);
	}

	/**
	 * Register the specified listener to receive ZooKeeper events and add it as
	 * the first in the list of current listeners.
	 * 
	 * @param listener
	 */
	public void registerListenerFirst(ZooKeeperListener listener) {
		listeners.add(0, listener);
	}

}
