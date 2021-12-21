package Ph;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Forks implements Watcher {
    static final String hostPort = "2181";
    public ZooKeeper zoo;

    public Forks() throws InterruptedException, KeeperException {
        String path = "/ph/forks";

        try {
            this.zoo = new ZooKeeper("localhost:" + hostPort, 2000, this);
        } catch (IOException e) {
            System.out.println(e);
        }
        if(zoo.exists("/ph", false)==null){
            this.enter("/ph");
        }
        if(zoo.exists(path, false)==null){
            this.enter(path);
        }
        for (int i = 1; i < 6; i++) {
            Stat stat_right = zoo.exists(path +"/"+ i, false);
            if (stat_right == null) {
                this.enter(path +"/"+ i);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, KeeperException {
        System.out.println("Start");
        Forks frk = new Forks();
        System.out.println("THE END");
    }

    public void enter(String path_for_create) throws KeeperException, InterruptedException {
        String s = "fork";
        zoo.create(path_for_create, s.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Override
    public void process(WatchedEvent we) {
    }

    public void close(String path_for_delete) throws InterruptedException {
        try {
            zoo.delete(path_for_delete, -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
