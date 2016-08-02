package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
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
public class ZKCreateZNodeAsync implements Watcher{
	public static CountDownLatch countdown=new CountDownLatch(1);
	@Override
	public void process(WatchedEvent event) {
//		System.out.println("Receive watched event:"+event);
		if(KeeperState.SyncConnected==event.getState()){
			countdown.countDown();
		}
	}

	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper=new ZooKeeper("localhost:2181", 5000, new ZKCreateZNodeAsync());
		try{
			countdown.await();//阻塞
		}catch(InterruptedException e){}
		
		zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(),"i am context");
		
		
		zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(),"i am context");
		
		Thread.sleep(Integer.MAX_VALUE);
	}

}

class IStringCallback implements AsyncCallback.StringCallback{

	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		//rc result code
		//path 传入的path
		//ctx 传入的
		//name 实际创建的节点名
		System.out.println("Create path result:["+rc+","+path+","+ctx+",real path name:"+name);
	}
	
}
