package zoo;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

public class Animal implements Watcher {
    public String animalName;
    public String hostPort;
    public Integer partySize;
    public String root;
    public Object mutex;


    public ZooKeeper zoo;

    public Animal(String animalName, String hostPort, int partySize,String root) {
        this.animalName = animalName;
        this.hostPort = hostPort;
        this.partySize = partySize;
        this.root=root;

        this.mutex = new Object();

        try {
            this.zoo=new ZooKeeper("localhost:"+hostPort, 2000, this);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting animal runner");
        //Создаём объект
        Animal animal = new Animal(args[0], args[1],Integer.parseInt(args[2]),"/zoo");

        try {
            animal.enter();
            Thread.sleep(1000);
            animal.close();
        } catch (Exception e) {
            System.out.println("Animal was not permitted to the zoo. " + e);
        }
    }


    public void enter() throws KeeperException, InterruptedException {
        zoo.create(root+"/"+animalName, animalName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.PERSISTENT);
        synchronized (mutex){
            while (true) {
                List<String> party = null;
                try {
                    party = zoo.getChildren(root, this);
                } catch (KeeperException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (party.size() < partySize) {
                    System.out.println(party);
                    System.out.println("Waiting for the others.");
                    try {
                        mutex.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Noticed someone.");
                } else {
                    System.out.println("Всё на месте");
                    break;
                }
            }
        }
    }

    @Override
    public void process(WatchedEvent we) {
            synchronized (mutex){
                System.out.println("Event from keeper: "+we);
                mutex.notifyAll();
            }
    }

    public void close() throws InterruptedException {
        try {
            zoo.delete(root+"/"+animalName,-1);
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        zoo.close();
    }

}
