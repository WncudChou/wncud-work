package com.wncud.zookeeper.core;

public abstract class ZooKeeperListener {

	// Reference to the zk watcher which also contains configuration and
	// constants

	/**
	 * Called when a new node has been created.
	 * 
	 * @param path
	 *            full path of the new node
	 */
	public abstract void nodeCreated(String path) ;

	/**
	 * Called when a node has been deleted
	 * 
	 * @param path
	 *            full path of the deleted node
	 */
	public abstract void nodeDeleted(String path) ;

	/**
	 * Called when an existing node has changed data.
	 * 
	 * @param path
	 *            full path of the updated node
	 */
	public abstract void nodeDataChanged(String path) ;

	/**
	 * Called when an existing node has a child node added or removed.
	 * 
	 * @param path
	 *            full path of the node whose children have changed
	 */
	public abstract void nodeChildrenChanged(String path) ;
}
