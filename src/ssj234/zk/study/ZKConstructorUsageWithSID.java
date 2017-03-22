package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * sid = session id 这是为了复用连接，维护之前会话的有效性
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
			countdown.await();// 阻塞
		} catch (InterruptedException e) {
		}
		
		// 连接成功后获取一下session的id和密码
		long sessionId = zooKeeper.getSessionId();
		byte[] sessionPasswd = zooKeeper.getSessionPasswd();

		// 使用假的session复用再次连接，由于错误会受到Expired通知
		zooKeeper = new ZooKeeper("localhost:2181", 5000,
				new ZKConstructorUsageWithSID(), 1l, "test".getBytes());

		//使用了正确的session，会成功连接
		zooKeeper = new ZooKeeper("localhost:2181", 5000,
				new ZKConstructorUsageWithSID(), sessionId, sessionPasswd);

		Thread.sleep(Integer.MAX_VALUE);
	}

}
