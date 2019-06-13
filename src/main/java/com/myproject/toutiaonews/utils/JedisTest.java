package com.myproject.toutiaonews.utils;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

/**
 * @Author slzhao
 * @create: 2019-06-10 10:35
 **/
public class JedisTest {


    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }


    public static void maintest(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();  // 先删除所有数据

        //get, set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");  // 3秒过期时间
        //print(1, jedis.get("hello2"));

        // 数值操作
        jedis.set("pv", "100");
        jedis.incr("pv");  // 加1
        jedis.decrBy("pv", 5);  // 减5
        print(2, jedis.get("pv"));
        print(3, jedis.keys("*"));

        // 列表操作：最近来访，粉丝列表，消息队列
        String listName = "list";
        jedis.del(listName); // 先清空原列表，如果有的话
        for (int i = 0; i < 10; i++) {
            // lpush，头插；rpush，尾插
            jedis.rpush(listName, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 9)); // start->end，包括end
        print(5, jedis.llen(listName));
        print(6, jedis.lpop(listName)); // 弹出首元素（左边第一个）
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        print(9, jedis.lindex(listName, 3));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xxx"));
        print(11, jedis.lrange(listName, 0, 12));

        // hash：可变字段
        String userKey = "user_zsl";
        jedis.hset(userKey, "name", "tom");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "1688888888");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(15, jedis.hexists(userKey, "phone"));
        print(16, jedis.hexists(userKey, "age"));
        print(17, jedis.hkeys(userKey));  // entrySet.getKey()
        print(18, jedis.hvals(userKey));  // entrySet.getValue()
        jedis.hsetnx(userKey, "school", "neu"); // 新增，school不存在
        jedis.hsetnx(userKey, "name", "zsl");  // 无效，name已经存在
        print(19, jedis.hgetAll(userKey));

        // 集合
        String likeKey1 = "newsLike1";
        String likeKey2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            // sadd往集合中添加元素，已经存在的添加操作会忽略
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * 2));
        }
        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
        print(22, jedis.sunion(likeKey1, likeKey1)); // 并集
        print(23, jedis.sdiff(likeKey1, likeKey2));  // 差集
        print(24, jedis.sinter(likeKey1, likeKey2)); // 交集
        print(25, jedis.sismember(likeKey1, "12"));  // 检查元素是否存在集合中:1存在;0不存在
        print(26, jedis.sismember(likeKey2, "12"));
        print(27, jedis.srem(likeKey1, "5")); // 删除，返回被删除的成员个数
        print(27, jedis.smembers(likeKey1));
        // 从1移动到2
        jedis.smove(likeKey1, likeKey2, "4");
        print(28, jedis.smembers(likeKey1));
        print(28, jedis.smembers(likeKey2));
        print(29, jedis.scard(likeKey1)); // 返回集合中元素个数

        // 排序集合，有限队列，排行榜
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        print(30, jedis.zcard(rankKey));
        print(31, jedis.zcount(rankKey, 61, 100));
        // 改错卷了
        print(32, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");  // 给Lucy加2分
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc"); // 会先新增一个Luc元素
        print(34, jedis.zscore(rankKey, "Luc"));
        print(35, jedis.zcount(rankKey, 0, 100));
        print(36, jedis.zrange(rankKey, 0, -1));  // 从低到高排序后，-1表示最后一个
        print(36, jedis.zrange(rankKey, 1, 3));
        print(36, jedis.zrevrange(rankKey, 0, -1)); // 从高到低排序
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(38, jedis.zrank(rankKey, "Ben")); // 排名
        print(39, jedis.zrevrank(rankKey, "Ben"));

        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");
        // 专门对score一直的元素操作的zset命令
        print(40, jedis.zlexcount(setKey, "-", "+"));
        print(41, jedis.zlexcount(setKey, "(b", "[d"));
        print(42, jedis.zlexcount(setKey, "[b", "[d"));
        jedis.zrem(setKey, "b");
        print(43, jedis.zrange(setKey, 0, -1));
        jedis.zremrangeByLex(setKey, "(c", "+");  // 删除比c大的元素
        print(44, jedis.zrange(setKey, 0, -1));

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {  // 默认8条线程
            Jedis j = pool.getResource();
            j.get("a");
            j.close();
        }
    }
}
