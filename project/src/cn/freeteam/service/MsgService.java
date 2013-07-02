package cn.freeteam.service;

import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseService;
import cn.freeteam.dao.MsgMapper;
import cn.freeteam.model.Msg;
import cn.freeteam.model.MsgExample;
import cn.freeteam.model.MsgExample.Criteria;

public class MsgService extends BaseService{

	private MsgMapper msgMapper;
	
	public MsgService() {
		initMapper("msgMapper");
	}


	/**
	 * 分页查询
	 */
	public List<Msg> find(Msg Msg,String order,int currPage,int pageSize){
		MsgExample example=new MsgExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(Msg, criteria);
		if (order!=null && order.trim().length()>0) {
			example.setOrderByClause(order);
		}
		example.setCurrPage(currPage);
		example.setPageSize(pageSize);
		return msgMapper.selectPageByExample(example);
	}
	/**
	 * 统计
	 * @param info
	 * @return
	 */
	public int count(Msg msg){
		MsgExample example=new MsgExample();
		Criteria criteria=example.createCriteria();
		proSearchParam(msg, criteria);
		return msgMapper.countByExample(example);
	}

	/**
	 * 处理查询条件
	 * @param info
	 * @param criteria
	 */
	public void proSearchParam(Msg msg,Criteria criteria){
		if (msg!=null ) {
		}
	}

	/**
	 * 根据id查询
	 * @param id
	 * @param cache
	 * @return
	 */
	public Msg findById(String id){
		return msgMapper.selectByPrimaryKey(id);
	}
	/**
	 * 更新
	 * @param question
	 */
	public void update(Msg msg){
		msgMapper.updateByPrimaryKeySelective(msg);
		DBCommit();
	}
	/**
	 * 添加
	 * @param question
	 * @return
	 */
	public String add(Msg msg){
		msg.setId(UUID.randomUUID().toString());
		msgMapper.insert(msg);
		DBCommit();
		return msg.getId();
	}
	/**
	 * 删除 
	 * @param id
	 */
	public void del(String id){
		msgMapper.deleteByPrimaryKey(id);
		DBCommit();
	}
	public MsgMapper getMsgMapper() {
		return msgMapper;
	}

	public void setMsgMapper(MsgMapper msgMapper) {
		this.msgMapper = msgMapper;
	}
}
