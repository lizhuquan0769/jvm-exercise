package dev.lzq.jvm.exercise;

import java.util.ArrayList;
import java.util.List;

/**
 * linke：https://mp.weixin.qq.com/s?__biz=MzU5ODUwNzY1Nw==&mid=2247483866&idx=1&sn=8b16cd987ca3cc484de7274bdffac897&chksm=fe42683cc935e12a17d70b4f7be232b0f7c62a0ce86ec2eee31921dd4a17667079c1470d36e4&mpshare=1&scene=23&srcid=0824aMs8ONR8sgBtqIjgXxuU#rd
 *
 * 题目：让JVM先3次YoungGC再1次CMS GC的代码。
 *
 * JVM ARGS：-verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xmx40m -Xms40m -Xmn10m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=60
 *
 * 参数解析：
 *  -verbose:gc  输出虚拟机中GC的详细情况。
 *  -Xmn10m  设置年轻代为10m（年轻代=Eden区 + S1 + S2，三者默认比例为8:1:1）。
 *  -XX:+UseParNewGC 指定年轻代使用并行收集器，可以和CMS垃圾收集器一起配合使用。是UseParallelGC的升级版本，在serial基础上实现的多线程收集器，有更好的性能和优点。
 *  -XX:+UseConcMarkSweepGC  指定老年代使用CMS垃圾收集器，以牺牲吞吐量为代价来获取最短回收停顿的垃圾回收器（减少了回收的停顿时间，降低了堆空间的利用率），对于要求服务器响应速度的应用上非常合适。
 *  -XX:CMSInitiatingOccupancyFraction   配置CMS触发老年代进行垃圾回收的阀值
 *  -XX:+UseCMSInitiatingOccupancyOnly   配置CMS只根据阀值来进行垃圾回收，就是说关闭动态检测机制（CMS会根据历史记录，预测老年代还需要多久填满及进行一次回收所需要的时间。在老年代空间用完之前，CMS可以根据自己的预测自动执行垃圾回收。）
 */
public class _01_CustomGC {
    public static void main(String[] args) throws Exception {
        // list集合全局引入byte数组, 是为了每次YGC后，byte[]不被回收，直接进入Old区
        List<byte[]> holdList = new ArrayList<>();


        // 由于main方法允许肯定会有1~2M内存，所以为了触发第一次YGC，这里只需要分配7M即可
        for (int i=0; i<7; i++){
            holdList.add(new byte[1*1024*1024]);
        }

        // 为了触发第2,3次YGC，每次也只需要分配7M
        for (int i=0; i<7; i++){
            holdList.add(new byte[1*1024*1024]);
        }
        for (int i=0; i<7; i++){
            holdList.add(new byte[1*1024*1024]);
        }

        // sleep一下子为了让CMS GC线程能够有足够的时间检测到Old区达到了触发CMS GC的条件，
        // CMS GC线程默认2s扫描一次，可以通过参数CMSWaitDuration配置，例如-XX:CMSWaitDuration=3000
        Thread.sleep(1000);
    }
}
