package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZKAuthSet implements Watcher{
	public static String PATH="/zk-book-auth_test";
	public static ZooKeeper zk=null;
	public static CountDownLatch countdown=new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
		
		zk=new ZooKeeper("localhost:2181", 5000, new ZKSetDataSync());
		countdown.await();
		zk.addAuthInfo("digest", "foo:true".getBytes());
		
		zk.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		Thread.sleep(99999l);
	}

	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){
			countdown.countDown();
		}
	}

}
