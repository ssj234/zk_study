package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * KeeperState.SyncConnected
 * @author shishengjie
 *
 */
public class ZKCreateZNode implements Watcher{
	public static CountDownLatch countdown=new CountDownLatch(1);
	@Override
	public void process(WatchedEvent event) {
//		System.out.println("Receive watched event:"+event);
		if(KeeperState.SyncConnected==event.getState()){
			countdown.countDown();
		}
	}

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper=new ZooKeeper("localhost:2181", 5000, new ZKCreateZNode());
		try{
			countdown.await();//×èÈû
		}catch(InterruptedException e){}
		
		String path1=zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("success create znode: "+path1);
		
		
		String path2=zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("success create znode: "+path2);
		
		Thread.sleep(Integer.MAX_VALUE);
	}

}
