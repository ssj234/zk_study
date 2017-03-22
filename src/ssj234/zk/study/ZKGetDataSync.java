package ssj234.zk.study;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * EventType.NodeDataChanged
 * @author shishengjie
 *
 */
public class ZKGetDataSync implements Watcher {
	public static CountDownLatch countdown=new CountDownLatch(1);
	static ZooKeeper zk=null;
	private static Stat stat=new Stat();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String path="/zk-book-d";
		zk=new ZooKeeper("localhost:2181", 5000, new ZKGetDataSync());
		countdown.await();
		
		//��/zk-book��EPHEMERAL�ģ��򴴽�/zk-book/c1ʱ�ᷢ������
		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		System.out.println(new String(zk.getData(path, true, stat)));
		System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
		
		zk.setData(path, "123".getBytes(), -1);
		
		Thread.sleep(99999999);
	}

	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected==event.getState()){//�Ѿ�����
			if(EventType.NodeDataChanged==event.getType()){//����
				try{
					System.out.println(new String(zk.getData(event.getPath(), true, stat)));
					System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
					Thread.sleep(20000);
				}catch(Exception e){}
			}else if(EventType.None==event.getType()&&null==event.getPath()){//����Ϊ����PathΪnull
				countdown.countDown();
			}
		}
	}

}
