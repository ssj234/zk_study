package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * @author shishengjie
 *
 */
public class ZKSetDataSync implements Watcher{

	public static CountDownLatch countdown=new CountDownLatch(1);
	public static ZooKeeper zk=null;
	private static Stat stat=new Stat();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String path="/zk-book";
		zk=new ZooKeeper("localhost:2181", 5000, new ZKSetDataSync());
		countdown.await();
		
		zk.getData(path, true, null);
		
		Stat stat=zk.setData(path, "456".getBytes(), -1);
		System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
		
		Stat stat2=zk.setData(path, "456".getBytes(), stat.getVersion());
		System.out.println(stat2.getCzxid()+","+stat2.getMzxid()+","+stat2.getVersion());
		
		try{
			zk.setData(path, "456".getBytes(), stat.getVersion());
		}catch(KeeperException e){
			System.out.println("Error:"+e.code()+","+e.getMessage());
		}
		
		Thread.sleep(99999999l);
	}

	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){
			if(EventType.None==event.getType()&&
					null==event.getPath()){
				countdown.countDown();
			}else if(EventType.NodeDataChanged==event.getType()){
				try {
					System.out.println("datachanged:"+new String(zk.getData("/zk-book", true, stat)));
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		
		}
	}

}
