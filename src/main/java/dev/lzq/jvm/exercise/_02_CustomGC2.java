package dev.lzq.jvm.exercise;

/**
 * link：https://mp.weixin.qq.com/s?__biz=MzU5ODUwNzY1Nw==&mid=2247483866&idx=1&sn=8b16cd987ca3cc484de7274bdffac897&chksm=fe42683cc935e12a17d70b4f7be232b0f7c62a0ce86ec2eee31921dd4a17667079c1470d36e4&mpshare=1&scene=23&srcid=0824aMs8ONR8sgBtqIjgXxuU#rd
 *
 * 题目：让JVM先3次YoungGC再1次CMS GC的代码。
 *
 * JVM ARGS：-verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx20m -Xms20m -Xmn10m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:PretenureSizeThreshold=2M -XX:CMSInitiatingOccupancyFraction=60 -XX:+UseCMSInitiatingOccupancyOnly
 *
 * 参数解析：
 *  -XX:PretenureSizeThreshold 设置当分配的对象超过设定值时不在Eden区分配，直接在Old区分配，但是这个参数只能CMS前提下才生效，ParallelGC不生效。
 */
public class _02_CustomGC2 {

    private static final int _1M = 1*1024*1024;
    private static final int _2M = 2*1024*1024;

    public static void main(String[] args) {
        // 在这里想怎么触发GC就怎么调用ygc()和cmsGc()两个方法
        ygc(3);
        cmsGc(1);
        ygc(2);
        cmsGc(2);
    }

    /**
     * @param n 预期发生n次young gc
     */
    private static void ygc(int n){
        for (int i=0; i<n; i++){
            // 由于Eden区设置为8M, 所以分配8个1M就会导致一次YoungGC
            for(int j=0; j<8; j++){
                byte[] tmp = new byte[_1M];
            }
        }
    }

    /**
     * @param n 预期发生n次CMS gc
     */
    private static void cmsGc(int n){
        for (int i=0; i<n; i++){
            for(int j=0; j<3; j++) {
                // 由于设置了-XX:PretenureSizeThreshold=2M, 所以分配的2M对象不会在Eden区分配而是直接在Old区分配
                byte[] tmp = new byte[_2M];
            }
            try {
                // sleep10秒是为了让CMS GC线程能够有足够的时间检测到Old区达到了触发CMS GC的条件并完成CMS GC, CMS GC线程默认2s扫描一次，可以通过参数CMSWaitDuration配置，例如-XX:CMSWaitDuration=3000
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
