package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
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
public class ZKSetDataAsync implements Watcher{

	public static CountDownLatch countdown=new CountDownLatch(1);
	public static ZooKeeper zk=null;
	private static Stat stat=new Stat();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String path="/zk-book";
		zk=new ZooKeeper("localhost:2181", 5000, new ZKSetDataAsync());
		countdown.await();
		
		zk.getData(path, true, null);
		
		
		zk.setData(path, "456".getBytes(),-1,new IStatCallback(),null);
		
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
class IStatCallback implements AsyncCallback.StatCallback{

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		if(rc==0){
			System.out.println("SUCCESS");
		}
	}
	
}
