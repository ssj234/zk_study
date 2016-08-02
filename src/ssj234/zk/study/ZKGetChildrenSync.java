package ssj234.zk.study;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * EventType.NodeChildrenChanged
 * @author shishengjie
 *
 */
public class ZKGetChildrenSync implements Watcher {
	public static CountDownLatch countdown=new CountDownLatch(1);
	static ZooKeeper zk=null;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String path="/zk-book";
		zk=new ZooKeeper("localhost:2181", 5000, new ZKGetChildrenSync());
		countdown.await();
		
		//若/zk-book是EPHEMERAL的，则创建/zk-book/c1时会发生错误
		zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//NoChildrenForEphemerals for /zk-book1/c1
		zk.create(path+"/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		List<String> childrenList=zk.getChildren(path, true);
		System.out.println(childrenList);
		
		zk.create(path+"/c2", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		Thread.sleep(99999999);
	}

	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){//已经连接
			if(EventType.NodeChildrenChanged==event.getType()){//类型
				try{
					System.out.println("ReGetChild:"+zk.getChildren(event.getPath(),true));
				}catch(Exception e){}
			}else if(EventType.None==event.getType()&&null==event.getPath()){//类型为空且Path为null
				countdown.countDown();
			}
		}
	}

}
