package cn.steamyao.seckill.lock.zookeeper;


import com.google.common.base.Joiner;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.lock.zookeeper
 * @date 2019/4/17 19:47
 * @description   https://blog.csdn.net/qiangcuo6087/article/details/79067136
 *                https://blog.csdn.net/u013219624/article/details/83153016
 *                https://blog.csdn.net/justloveyou_/article/details/72466105(类加载)
 *                https://blog.csdn.net/naruto_Mr/article/details/81506065
 *                https://blog.csdn.net/zhailuxu/article/details/80658016
 */
public class ZookeeperLock {

        public final static Joiner j = Joiner.on("|").useForNull("");

        //zk客户端
        private ZooKeeper zk;
        //zk是一个目录结构，root为最外层目录
        private String root = "/locks";
        //锁的名称
        private String lockName;
        //当前线程创建的序列node
        private ThreadLocal<String> nodeId = new ThreadLocal<>();
        //用来同步等待zkclient链接到了服务端
        private CountDownLatch connectedSignal = new CountDownLatch(1);
        private final static int sessionTimeout = 3000;
        private final static byte[] data= new byte[0];

        public ZookeeperLock(String config, String lockName) {
            this.lockName = lockName;

            try {
                zk = new ZooKeeper(config, sessionTimeout, new Watcher() {

                    @Override
                    public void process(WatchedEvent event) {
                        // 建立连接
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            connectedSignal.countDown();
                        }
                    }

                });

                connectedSignal.await();
                Stat stat = zk.exists(root, false);
                if (null == stat) {
                    // 创建根节点
                    zk.create(root, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        class LockWatcher implements Watcher {
            private CountDownLatch latch = null;

            public LockWatcher(CountDownLatch latch) {
                this.latch = latch;
            }

            @Override
            public void process(WatchedEvent event) {

                if (event.getType() == Event.EventType.NodeDeleted)
                    latch.countDown();
            }
        }

        public void lock() {
            try {

                // 创建临时子节点
                String myNode = zk.create(root + "/" + lockName , data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);

                System.out.println(j.join(Thread.currentThread().getName() + myNode, "created"));

                // 取出所有子节点
                List<String> subNodes = zk.getChildren(root, false);
                TreeSet<String> sortedNodes = new TreeSet<>();
                for(String node :subNodes) {
                    sortedNodes.add(root +"/" +node);
                }

                String smallNode = sortedNodes.first();
                String preNode = sortedNodes.lower(myNode);

                if (myNode.equals( smallNode)) {
                    // 如果是最小的节点,则表示取得锁
                    System.out.println(j.join(Thread.currentThread().getName(), myNode, "get lock"));
                    this.nodeId.set(myNode);
                    return;
                }

                CountDownLatch latch = new CountDownLatch(1);
                Stat stat = zk.exists(preNode, new LockWatcher(latch));// 同时注册监听。
                // 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
                if (stat != null) {
                    System.out.println(j.join(Thread.currentThread().getName(), myNode,
                            " waiting for " + root + "/" + preNode + " released lock"));

                    latch.await();// 等待，这里应该一直等待其他线程释放锁
                    nodeId.set(myNode);
                    latch = null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        public void unlock() {
            try {
                System.out.println(j.join(Thread.currentThread().getName(), nodeId.get(), "unlock "));
                if (null != nodeId) {
                    zk.delete(nodeId.get(), -1);
                }
                nodeId.remove();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }

    }


