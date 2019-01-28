package javautils.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Nick on 2017/3/11.
 */
public final class RandomUtil {
    private RandomUtil(){}

    /**
     * 从min - max 里随机出n个不重复的数字
     * @param min 最小数值
     * @param max 最大数值
     * @param n 生成多少个
     */
    public static List<Integer> randomUniqueNumbers(int min, int max, int n) {
        List<Integer> list = new ArrayList<>();
        for (int i=min; i<=max; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);

        List<Integer> result = new ArrayList<>();
        for (int i=0; i<n; i++) {
            Integer value = list.get(i);
            result.add(value);
        }

        return result;
    }

    public static void main(String[] args) {
        int count = 10000;

        List<Integer> result = randomUniqueNumbers(0, 99999, count);

        HashSet<Integer> resultChecker = new HashSet<>(result);

        System.out.println("是否是正确的结果"+ (resultChecker.size() == count));
    }
}
