package cn.net.younian.youbackup.util;

/**
 * Created by Administrator on 2017/3/10.
 */

enum RestoreType {
    OVERWRITE(0), SKIP(1), BOTH(2);

    long value;

    RestoreType(long type) {
        this.value = type;
    }

    long getValue() {
        return value;
    }
}
