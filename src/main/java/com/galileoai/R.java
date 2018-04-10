package com.galileoai;


/**
 * Created by baymin
 * 2017-07-10 13:39
 */
public class R {

    /**
     * 成功带data
     * @param object
     * @return
     */
    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("上传成功");
        result.setData(object);
        return result;
    }
    /**
     * 成功带data，并带当前的状态值
     * 分页查询 status 表示当前页数
     * @param object
     * @return
     */
    public static Result error(Object object) {
        Result result = new Result();
        result.setCode(-1);
        result.setMsg("失败");
        result.setData(object);
        return result;
    }
}
