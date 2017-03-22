package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * 删除delete操作作用于子节点
 * 
 * @author shishengjie
 * 
 */
public class ZKAuthGet implements Watcher {
	public static String PATH = "/zk-book-auth_test1";
	public static ZooKeeper zk = null;
	public static CountDownLatch countdown = new CountDownLatch(1);
	boolean flag = false;

	public static void main(String[] args) throws Exception {

		ZooKeeper zk2 = new ZooKeeper("localhost:2181", 5000, new ZKAuthGet());
		countdown.await();
		// 会抛出异常， NoAuth for /zk-book-auth_test1
		zk2.getData(PATH, false, null);

	}

	@Override
	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			countdown.countDown();
		}
	}

}
