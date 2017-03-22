package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * sid = session id ����Ϊ�˸������ӣ�ά��֮ǰ�Ự����Ч��
 * @author shishengjie
 *
 */
public class ZKConstructorUsageWithSID implements Watcher {
	public static CountDownLatch countdown = new CountDownLatch(1);

	@Override
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:" + event);
		if (KeeperState.SyncConnected == event.getState()) {
			countdown.countDown();
		}
	}

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000,
				new ZKConstructorUsageWithSID());
		try {
			countdown.await();// ����
		} catch (InterruptedException e) {
		}
		
		// ���ӳɹ����ȡһ��session��id������
		long sessionId = zooKeeper.getSessionId();
		byte[] sessionPasswd = zooKeeper.getSessionPasswd();

		// ʹ�üٵ�session�����ٴ����ӣ����ڴ�����ܵ�Expired֪ͨ
		zooKeeper = new ZooKeeper("localhost:2181", 5000,
				new ZKConstructorUsageWithSID(), 1l, "test".getBytes());

		//ʹ������ȷ��session����ɹ�����
		zooKeeper = new ZooKeeper("localhost:2181", 5000,
				new ZKConstructorUsageWithSID(), sessionId, sessionPasswd);

		Thread.sleep(Integer.MAX_VALUE);
	}

}
