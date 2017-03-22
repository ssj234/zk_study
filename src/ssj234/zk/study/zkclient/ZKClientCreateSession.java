package ssj234.zk.study.zkclient;

import org.I0Itec.zkclient.ZkClient;

public class ZKClientCreateSession {

	public static void main(String[] args) {
		ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
//		zkClient.create(path, data, mode)
//		zkClient.createEphemeral(path)
//		zkClient.createPersistentSequential(path, data)
//		zkClient.delete(arg0)
//		zkClient.writeData(path, object)
//		zkClient.readData(path)
		System.out.println("Zookeeper session established.");
	}
}
