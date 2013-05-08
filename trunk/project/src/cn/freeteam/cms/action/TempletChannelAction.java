package cn.freeteam.cms.action;

import java.io.File;
import java.util.List;
import java.util.UUID;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Channel;
import cn.freeteam.cms.model.Templet;
import cn.freeteam.cms.model.TempletChannel;
import cn.freeteam.cms.service.TempletChannelService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.util.FileUtil;
import cn.freeteam.util.OperLogUtil;
import cn.freeteam.util.ResponseUtil;

/**
 * 
 * <p>Title: TempletChannelAction.java</p>
 * 
 * <p>Description:模板栏目相关操作 </p>
 * 
 * <p>Date: Feb 4, 2013</p>
 * 
 * <p>Time: 7:52:23 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class TempletChannelAction extends BaseAction{

	private TempletChannelService templetChannelService;
	private TempletService templetService;
	private Templet templet;
	private TempletChannel templetChannel; 
	private File img;
	private String imgFileName;
	private String oldImg;
	private String root;
	private String onclick;
	
	public File getImg() {
		return img;
	}


	public void setImg(File img) {
		this.img = img;
	}


	public String getImgFileName() {
		return imgFileName;
	}


	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}


	public String getOldImg() {
		return oldImg;
	}


	public void setOldImg(String oldImg) {
		this.oldImg = oldImg;
	}


	public TempletChannelAction() {
		init("templetChannelService","templetService");
	}


	/**
	 * 编辑页面
	 * @return
	 */
	public String edit(){
		if (templet!=null && templet.getId()!=null && templet.getId().trim().length()>0) {
			templet=templetService.findById(templet.getId());
		}
		if (templetChannel!=null && templetChannel.getId()!=null && templetChannel.getId().trim().length()>0) {
			templetChannel=templetChannelService.findById(templetChannel.getId());
			templet=templetService.findById(templetChannel.getTempletid());
		}
		return "edit";
	}

	/**
	 * 编辑处理
	 * @return
	 */
	public String editDo(){
		try {
			templet=templetService.findById(templet.getId());
			if (templetChannel.getId()!=null && templetChannel.getId().trim().length()>0) {
				//更新
				TempletChannel oldtempletChannel=templetChannelService.findById(templetChannel.getId());
				//如果原来有和现在的pagemark不同则判断新的pagemark是否存在
				if (templetChannel.getPagemark()!=null && templetChannel.getPagemark().trim().length()>0 && 
						oldtempletChannel.getPagemark()!=null && 
						!oldtempletChannel.getPagemark().equals(templetChannel.getPagemark())) {
					if (templetChannelService.hasPagemark(templetChannel.getTempletid(), templetChannel.getPagemark())) {
						write("<script>alert('此页面标识已存在!');history.back();</script>", "GBK");
						return null;
					}
				}
				//如果原来有和现在的logo不同则删除原来的logo文件 
				if (!oldImg.equals(oldtempletChannel.getImg()) && oldtempletChannel.getImg()!=null && oldtempletChannel.getImg().trim().length()>0) {
					FileUtil.del(getHttpRequest().getRealPath("/")+"/templet/"+templet.getId()+"/upload/"+oldtempletChannel.getImg().trim().replaceAll("/", "\\\\"));
				}else {
					templetChannel.setImg(oldImg);
				}
				if (img!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/")+"/templet/"+templet.getId();
					String ext=FileUtil.getExt(imgFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
					String id=UUID.randomUUID().toString();
					File targetFile=new File(root+"\\upload\\"+id+ext);
					File folder=new File(root+"\\upload\\");
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(img, targetFile);
					//生成访问地址
					templetChannel.setImg("/upload/"+id+ext);
				}
				templetChannelService.update(templetChannel);
				OperLogUtil.log(getLoginName(), "更新栏目 "+templetChannel.getName(), getHttpRequest());
			}else {
				//添加
				//判断页面标识是否存在
				if (templetChannel.getPagemark()!=null && templetChannel.getPagemark().trim().length()>0 && 
						templetChannelService.hasPagemark(templetChannel.getTempletid(), templetChannel.getPagemark())) {
					write("<script>alert('此页面标识已存在!');history.back();</script>", "GBK");
					return null;
				}
				if (img!=null) {
					//生成目标文件
					String root=getHttpRequest().getRealPath("/")+"/templet/"+templet.getId();
					String ext=FileUtil.getExt(imgFileName).toLowerCase();
					if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".gif".equals(ext) && !".png".equals(ext)) {
						write("<script>alert('logo只能上传jpg,jpeg,gif,png格式的图片!');history.back();</script>", "GBK");
						return null;
					}
					String id=UUID.randomUUID().toString();
					File targetFile=new File(root+"\\upload\\"+id+ext);
					File folder=new File(root+"\\upload\\");
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					//复制到目标文件
					FileUtil.copy(img, targetFile);
					//生成访问地址
					templetChannel.setImg("/upload/"+id+ext);
				}
				templetChannelService.insert(templetChannel);
				OperLogUtil.log(getLoginName(), "添加栏目 "+templetChannel.getName(), getHttpRequest());
			}
			write("<script>alert('操作成功!');location.href='templetChannel_edit.do?templetChannel.id="+templetChannel.getId()+"';</script>", "GBK");
			return null;
		} catch (Exception e) {
			DBProException(e);
			write("<script>alert('"+e.toString()+"');history.back();</script>", "GBK");
		}
		
		return null;
	}

	/**
	 * 树结构
	 * @return
	 */
	public String son(){		
		List<TempletChannel> list=null; 
		list=templetChannelService.findByPar("", root);
		//生成树
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("[");
		if (list!=null && list.size()>0) {
			for (int i = 0; i <list.size() ; i++) {
				if (templetChannel!=null && templetChannel.getId()!=null && templetChannel.getId().trim().length()>0 && templetChannel.getId().equals(list.get(i).getId())) {
					continue;
				}
				if (!"[".equals(stringBuilder.toString())) {
					stringBuilder.append(",");
				}
				stringBuilder.append("{ \"text\": \"");
				stringBuilder.append("<a  onclick=");
				if (onclick!=null && onclick.trim().length()>0) {
					stringBuilder.append(onclick);
				}else {
					stringBuilder.append("showOne");
				}
				stringBuilder.append("('");
				stringBuilder.append(list.get(i).getId());
				stringBuilder.append("','");
				stringBuilder.append(list.get(i).getName().replaceAll(" ", ""));
				stringBuilder.append("')><b>");
				stringBuilder.append(list.get(i).getName());
				stringBuilder.append("\", \"hasChildren\": ");
				if (templetChannelService.hasChildren(list.get(i).getId())) {
					stringBuilder.append("true");
				}else {
					stringBuilder.append("false");
				}
				stringBuilder.append(",\"id\":\"");
				stringBuilder.append(list.get(i).getId());
				stringBuilder.append("\" }");
			}
		}
		stringBuilder.append("]");
		ResponseUtil.writeUTF(getHttpResponse(), stringBuilder.toString());
		return null;
	}
	public TempletChannelService getTempletChannelService() {
		return templetChannelService;
	}

	public void setTempletChannelService(TempletChannelService templetChannelService) {
		this.templetChannelService = templetChannelService;
	}


	public Templet getTemplet() {
		return templet;
	}


	public void setTemplet(Templet templet) {
		this.templet = templet;
	}


	public TempletService getTempletService() {
		return templetService;
	}


	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}


	public TempletChannel getTempletChannel() {
		return templetChannel;
	}


	public void setTempletChannel(TempletChannel templetChannel) {
		this.templetChannel = templetChannel;
	}


	public String getRoot() {
		return root;
	}


	public void setRoot(String root) {
		this.root = root;
	}


	public String getOnclick() {
		return onclick;
	}


	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
}
