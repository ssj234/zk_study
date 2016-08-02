package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 创建、删除、数据更新 都会通知客户端
 * 
 * @author shishengjie
 * 
 */
public class ZKExistsSync implements Watcher {

	public static CountDownLatch countdown = new CountDownLatch(1);
	public static ZooKeeper zk = null;
	private static Stat stat = new Stat();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String path = "/zk-book2";
		zk = new ZooKeeper("localhost:2181", 5000, new ZKExistsSync());
		countdown.await();

		zk.exists(path, true); // 判断path是否存在，注册一个watcher

		zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);//创建节点，EventType.NodeCreated
		zk.setData(path, "123".getBytes(), -1);//设置数据，EventType.NodeDataChanged

		//创建子节点的目的是为了测试 监听节点的子节点变化，不会通知客户端
		zk.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		zk.delete(path + "/c1", -1);
		
		zk.delete(path, -1);//删除节点，EventType.NodeDeleted

		Thread.sleep(99999999l);
	}

	@Override
	public void process(WatchedEvent event) {
		
		//里面的zk.exists主要用来添加watcher
		try {
			if (KeeperState.SyncConnected == event.getState()) {
				if (EventType.None == event.getType()
						&& null == event.getPath()) {
					countdown.countDown();
				} else if (EventType.NodeDataChanged == event.getType()) {

					System.out.println("datachanged:"
							+ new String(zk.getData("/zk-book2", true, stat)));
					zk.exists(event.getPath(), true);// 判断path是否存在，注册一个watcher

				} else if (EventType.NodeCreated == event.getType()) {
					System.out.println("Node(" + event.getPath() + ")Created");
					zk.exists(event.getPath(), true);// 判断path是否存在，注册一个watcher
				} else if (EventType.NodeDeleted == event.getType()) {
					System.out.println("Node(" + event.getPath() + ")Deleted");
					zk.exists(event.getPath(), true);// 判断path是否存在，注册一个watcher
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
