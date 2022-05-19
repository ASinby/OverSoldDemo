package com.sinby.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：sinby
 * @Date :2022/03/24 14:06
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultData<T> {

    private boolean success;    //是否成功
    private String msg; //信息
    private int code;   //状态码
    private T data; //返回数据

    public ResultData<T> info(boolean success,String msg){
        ResultData<T> resultData = new ResultData<>();

        resultData.setCode(success?200:400);
        resultData.setMsg(msg);
        resultData.setSuccess(success);
        resultData.setData(null);

        return resultData;
    }

    public ResultData<T> success(T data){
        ResultData<T> resultData = new ResultData<>();

        resultData.setCode(200);
        resultData.setMsg("成功！");
        resultData.setSuccess(true);
        resultData.setData(data);

        return resultData;
    }
}
