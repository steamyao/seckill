package cn.steamyao.seckill.common.pojo;

import java.util.HashMap;
import java.util.Map;


public class Result extends HashMap{

	private static final long serialVersionUID = 1L;


    public Result(int status,String msg) {
        this.put("status",status);
        this.put("msg",msg);
    }

    public static Result error(String msg) {
        return new Result(500,msg);

    }

	public static Result error(int status, String msg) {
	  return new Result(status,msg);

	}
	

	public static Result ok(String msg) {
	   return new Result(200,msg);

	}


}