package util;

import java.util.Comparator;

import bean.SortBean;

/**
 * Created by Administrator on 2017/7/27.
 */

public class PinyinComparator implements Comparator<SortBean> {
    @Override
    public int compare(SortBean o1, SortBean o2) {

        if (o1.getFirstLetter().equals("@")
                || o2.getFirstLetter().equals("#")) {
            return -1;
        } else if (o1.getFirstLetter().equals("#")
                || o2.getFirstLetter().equals("@")) {
            return 1;
        } else {
            return o1.getFirstLetter().compareTo(o2.getFirstLetter());
        }
    }
}
