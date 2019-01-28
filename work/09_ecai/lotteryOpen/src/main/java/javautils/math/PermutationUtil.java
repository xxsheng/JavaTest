package javautils.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 排列算法
 * Created by Nick on 2016/11/27.
 */
public class PermutationUtil {
    public static void main(String args[]) throws Exception {
        // String[] str = {"-", "-", "1","2","3"};
        // List<Object[]> objects = permutation("799".split(""));
        // for (Object[] object : objects) {
        //     System.out.println(Arrays.toString(object));
        // }
        // System.out.println(objects.size());
    }

    /**
     * 求某一组元素的排列列表，如123，可以排成123 132 213 231 321 312，元素一般5个以内就好，不然有性能瓶颈
     */
    public static List<Object[]> permutation(Object[] objs) {
        if (objs.length > 5) {
            System.out.println("PermutationUtil.permutation排列元素不要超过5个，会有性能瓶颈");
        }

        return doPermutation(objs, 0, objs.length - 1);
    }

    private static List<Object[]> doPermutation(Object[] objs , int first,int end) {
        if (objs == null || objs.length <= 0) {
            return new ArrayList<>();
        }

        List<Object[]> result = new ArrayList<>();
        //输出str[first..end]的所有排列方式
        if(first == end) {    //输出一个排列方式
            Object[] permuration = new Object[objs.length];
            int count = 0;
            for(int j=0; j<= end ;j++) {
                permuration[count++] =objs[j];
            }
            result.add(permuration);
        }

        for(int i = first; i <= end ; i++) {
            swap(objs, i, first);
            List<Object[]> subResult = doPermutation(objs, first + 1, end);//固定好当前一位，继续排列后面的
            result.addAll(subResult);
            swap(objs, i, first);
        }

        return result;
    }

    private static void swap(Object[] objs, int i, int first) {
        Object tmp;
        tmp = objs[first];
        objs[first] = objs[i];
        objs[i] = tmp;
    }
}
