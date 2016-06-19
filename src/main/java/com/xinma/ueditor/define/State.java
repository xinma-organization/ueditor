package com.xinma.ueditor.define;

/**
 * 处理状态接口
 * 
 * @author hancong03@baidu.com
 *
 */
public interface State {

	public boolean isSuccess();

	public void putInfo(String name, String val);

	public void putInfo(String name, long val);

	public Object getInfo(String name);

	public String toJSONString();

}
