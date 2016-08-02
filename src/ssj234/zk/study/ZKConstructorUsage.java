package ssj234.zk.study;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKConstructorUsage implements Watcher{
	public static CountDownLatch countdown=new CountDownLatch(1);
	@Override
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:"+event);
		if(KeeperState.SyncConnected==event.getState()){
			countdown.countDown();
		}
	}

	public static void main(String[] args) throws Exception {
		ZooKeeper zk=new ZooKeeper("localhost:2181", 5000, new ZKConstructorUsage());
		System.out.println(zk.getState());
		try{
			countdown.await();//×èÈû
		}catch(InterruptedException e){}
		System.out.println("zk session established.");
	}

}
