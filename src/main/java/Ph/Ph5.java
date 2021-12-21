package Ph;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Ph5 implements Watcher {
    static final String path = "/ph/forks";
    static final String hostPort = "2181";
    public Integer id;
    public Integer fork_left;
    public Integer fork_right;
    public ZooKeeper zoo;

    public Ph5(Integer id_m) {
        this.id = id_m;
        this.fork_left = id_m;
        if (id != 5) {
            this.fork_right = id_m + 1;
        } else {
            this.fork_right = 1;
        }
    }

    public void m() {
        try {
            this.zoo = new ZooKeeper("localhost:" + hostPort, 2000, this);
        } catch (IOException e) {
            System.out.println(e);
        }
        String path_for_delete_left = path + "/" + fork_left;
        String path_for_delete_right = path + "/" + fork_right;
        Integer dm = 0;
        Integer pr;

        while (true) {
            if (dm == 0) {
                System.out.println("Я " + id + " и я думаю.... ");
            }
            try {
                if (zoo.exists(path_for_delete_left, false) != null) {
                    this.close(path_for_delete_left);
                    System.out.println("Взял левую ");
                    pr = 0;

                    if (zoo.exists(path_for_delete_right, false) != null) {
                        this.close(path_for_delete_right);
                        System.out.println("КУШАЮ) ");
                        Thread.sleep(2000);
                        this.enter(path_for_delete_right);
                        this.enter(path_for_delete_left);
                        pr = 1;
                        dm = 0;
                    }
                    if (pr != 1) {
                        this.enter(path_for_delete_left);
                        System.out.println("Не дождался, вернул левую( ");
                        dm = 1;
                    }
                } else {
                    dm = 2;
                }
                Thread.sleep(1000);

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Ph5 ph = new Ph5(5);
        ph.m();
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