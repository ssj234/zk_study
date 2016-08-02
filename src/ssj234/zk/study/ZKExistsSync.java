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
 * ������ɾ�������ݸ��� ����֪ͨ�ͻ���
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

		zk.exists(path, true); // �ж�path�Ƿ���ڣ�ע��һ��watcher

		zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);//�����ڵ㣬EventType.NodeCreated
		zk.setData(path, "123".getBytes(), -1);//�������ݣ�EventType.NodeDataChanged

		//�����ӽڵ��Ŀ����Ϊ�˲��� �����ڵ���ӽڵ�仯������֪ͨ�ͻ���
		zk.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		zk.delete(path + "/c1", -1);
		
		zk.delete(path, -1);//ɾ���ڵ㣬EventType.NodeDeleted

		Thread.sleep(99999999l);
	}

	@Override
	public void process(WatchedEvent event) {
		
		//�����zk.exists��Ҫ�������watcher
		try {
			if (KeeperState.SyncConnected == event.getState()) {
				if (EventType.None == event.getType()
						&& null == event.getPath()) {
					countdown.countDown();
				} else if (EventType.NodeDataChanged == event.getType()) {

					System.out.println("datachanged:"
							+ new String(zk.getData("/zk-book2", true, stat)));
					zk.exists(event.getPath(), true);// �ж�path�Ƿ���ڣ�ע��һ��watcher

				} else if (EventType.NodeCreated == event.getType()) {
					System.out.println("Node(" + event.getPath() + ")Created");
					zk.exists(event.getPath(), true);// �ж�path�Ƿ���ڣ�ע��һ��watcher
				} else if (EventType.NodeDeleted == event.getType()) {
					System.out.println("Node(" + event.getPath() + ")Deleted");
					zk.exists(event.getPath(), true);// �ж�path�Ƿ���ڣ�ע��һ��watcher
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
