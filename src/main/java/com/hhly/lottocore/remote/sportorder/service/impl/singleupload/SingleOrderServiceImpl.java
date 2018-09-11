package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.persistence.singleupload.dao.SingleUploadLogDaoMapper;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.sportorder.service.ISingleOrderService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.bo.UploadFileBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.SingleUploadEnum.UploadType;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.mq.SingleUploadLogMsgModel;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.PropertyUtil;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadLogBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadLogVO;

/**
 * 单式上传验证服务接口实现
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
@Service("singleOrderService")
public class SingleOrderServiceImpl implements ISingleOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(SingleOrderServiceImpl.class);
	
	@Autowired
	private RedisUtil redisUtil;
	
	//竞足
	@Autowired
	private FootballSingleUploadValidate footballSingleUploadValidate;
	
	//老足
	@Autowired
	private FootballOldSingleUploadValidate footballOldSingleUploadValidate;
	
	//北京单场
	@Autowired
	private BJDCSingleUploadValidate BJDCSingleUploadValidate;
	
	@Autowired
	private SingleUploadLogDaoMapper singleUploadLogDaoMapper;

	@Resource(name="singleUploadLogMessageProvider")
	private MessageProvider singleUploadLogMessageProvider;
	
	private static final String UNIX_PATH = "/_upload_file/single_upload";
	
	private static final String DEFAULT_PATH = "C:\\upload";
	 
	@Value("${single_upload_dir}")
	private String singleUploadDir;//根目录
	
	/** 7牛accessKey  **/
	@Value("${accessKey}")
	private String accessKey;
	/** 7牛secretKey **/
	@Value("${secretKey}")
	private String secretKey;
	/** bucketName **/
	@Value("${bucketName}")
	private String bucketName;
	/** 允许批量上传文件数量  **/
	@Value("${uploadLimit}")
	private Integer uploadLimit;
	/** 允许上传文件类型 **/
	@Value("${fileType}")
	private String fileType;
	/** 文件访问路径  **/
	@Value("${uploadURL}")
	private String uploadURL;
	/**域名和文件名中间的路径*/
	@Value("${savePath}")
	private String savePath;
	/**允许批量上传文件大小*/
	@Value("${limitSize}")
	private String limitSize;
	

	/**
	 * 验证统一入口
	 */
	@Override
	public ResultBO<?> validateOrder(String originalFilename, SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception {
//		UploadFileBO uploadFileBO = singleUpload(originalFilename, jczqSingleUploadVO , (List<String>)map.get(Constants.TXT_CONTENT));
//		jczqSingleUploadVO.setFilePath(uploadFileBO.getUrl());
//		if(ObjectUtil.isBlank(map)){
//			map = new HashMap<String, Object>();
//			map.put(Constants.FIRST_TIME_UPLOAD_FILE_NAME, uploadFileBO.getName());
//		}
//		String fileName = (String)map.get(Constants.FIRST_TIME_UPLOAD_FILE_NAME);
//		saveFileName(fileName, jczqSingleUploadVO.getUserId());
		
		QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, savePath, Long.parseLong(limitSize));
		qiniuUploadVO.setUploadURL(uploadURL);
		
		map.put("qiniuUploadVO", qiniuUploadVO);
		
		ResultBO<?> result = this.handleLotteryContent(jczqSingleUploadVO, map);
		if(!result.isError()){
			log(jczqSingleUploadVO, result);
		}
		return result;
	}
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年6月16日
	 * @param basePath 文件上传根目录
	 * @param singleUploadJCVO
	 * @param originalFileName 上传文件原始文件名
	 * 
	 * @return 按规则需存盘最终的文件
	 */
	private File[] getFilePath(SingleUploadJCVO singleUploadJCVO, String basePath, String originalFileName){
        long curTime = System.currentTimeMillis();
		String strTime = String.valueOf(curTime);
	    String userFile = null;//按规律定义的文件名
	    userFile = singleUploadJCVO.getUserId() + SymbolConstants.UNDERLINE + Constants.NUM_1 + SymbolConstants.UNDERLINE + singleUploadJCVO.getLotteryCode() + SymbolConstants.UNDERLINE + strTime.substring(strTime.length()-6, strTime.length());
	    singleUploadJCVO.setSecondFileName(userFile);

	    String path = FileUtil.getFilePath(basePath, FileUtil.getExtensionName(originalFileName), UploadType.getUploadType(singleUploadJCVO.getUploadType()).getShortName(), singleUploadJCVO.getLotteryCode());
	    
	    File[] file = new File[2];
	    file[0]= new File(path);
	    file[1]= new File(path + singleUploadJCVO.getSecondFileName() + SymbolConstants.DOT + FileUtil.getExtensionName(originalFileName));
		return file;
	}
	
	/**
	 * 单式上传文件
	 * @author longguoyou
	 * @date 2017年6月16日
	 * @param originalFilename
	 * @param singleUploadJCVO
	 * @param list
	 * @return
	 */
	private UploadFileBO singleUpload(String originalFilename, SingleUploadJCVO singleUploadJCVO, List<String> list) {
		logger.debug("单式上传文件开始.....");
		String basePath = singleUploadDir + UNIX_PATH;//保存根目录
		String os = System.getProperty("os.name");  
	    if(os.toLowerCase().startsWith("win")){  
	    	basePath = DEFAULT_PATH;
	    }
        File[] targetFile = getFilePath(singleUploadJCVO, basePath, originalFilename); 
        if(!targetFile[0].exists()){  
            targetFile[0].mkdirs();  
        }  
        //写文件 
        try {  
//           file.transferTo(targetFile[1]);
        	 FileUtil.saveFileFromString(targetFile[1].getPath(), list);
        } catch (Exception e) {  
            logger.error("单式上传文件", e);
        } 
        UploadFileBO vo = new UploadFileBO();
        vo.setName(singleUploadJCVO.getSecondFileName());
        vo.setUrl(targetFile[1].getPath());
        logger.debug("单式上传文件结束.....");
        return vo;  
	}
	
    
	/**
	 * 按彩种分支
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @param map
	 * @return
	 */
	private ResultBO<?> handleLotteryContent(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception{
		Lottery lottery = Lottery.getLottery(Integer.valueOf(jczqSingleUploadVO.getLotteryCode()));
		switch (lottery) {
		case FB:
			return footballSingleUploadValidate.handle(jczqSingleUploadVO, map);
		case ZC_NINE:
		case ZC6:
		case SFC:
		case JQ4:
			return footballOldSingleUploadValidate.handle(jczqSingleUploadVO, map);
		case BB:
			return null;
		case BJDC:
		case SFGG:
			return BJDCSingleUploadValidate.handle(jczqSingleUploadVO, map);
		}
		return null;
	}
	
	/**
	 * 保存第二次上传生成文件名到redis缓存
	 * @author longguoyou
	 * @date 2017年6月23日
	 * @param fileName
	 * @return
	 */ 
	private void saveFileName(String fileName, Integer userId)throws Exception{
		if(!ObjectUtil.isBlank(fileName)){
			redisUtil.addString(CacheConstants.getSingleUploadCacheKey(userId), fileName, CacheConstants.TWO_HOURS);
		}
	}
	
	/**
	 * 获取redis缓存文件名
	 * @author longguoyou
	 * @date 2017年6月23日
	 * @param userId
	 * @return
	 */
	public String getRedisFileName(Integer userId){
		return redisUtil.getString(CacheConstants.getSingleUploadCacheKey(userId));
	}

	@Override
	public List<SingleUploadLogBO> findSingleUploadLogInfo(SingleUploadLogVO singleUploadLogVO) {
		return singleUploadLogDaoMapper.findSingleUploadLogInfo(singleUploadLogVO);
	}

	@Override
	public void log(SingleUploadJCVO singleUploadJCVO, ResultBO<?> result ) {
		SingleUploadLogMsgModel model = new SingleUploadLogMsgModel();
        model.setUserId(singleUploadJCVO.getUserId());
		model.setLotteryCode(singleUploadJCVO.getLotteryCode());
		model.setFileUrl(singleUploadJCVO.getFilePath().replace(singleUploadDir, SymbolConstants.ENPTY_STRING));
		model.setFileFormat(getFileFormat(singleUploadJCVO.getFilePath()));
		model.setSessionType(singleUploadJCVO.getUploadType());
		model.setOperationType(singleUploadJCVO.getFlag()?(short)1:(short)2);
		model.setShift(singleUploadJCVO.getShiftContent());
		model.setShiftType(ObjectUtil.isBlank(singleUploadJCVO.getShiftContent())?(short)0:(short)1);
		model.setUploadResult(ObjectUtil.isBlank(singleUploadJCVO.getFilePath())?(short)0:(short)1);
		SingleUploadJCBO singleUploadJCBO = (SingleUploadJCBO)result.getData();
		model.setRemark(PropertyUtil.getConfigValue(MessageCodeConstants.SINGLE_UPLOAD_LOG_MSG, ObjectUtil.isBlank(singleUploadJCBO.getSuccessList())?0:singleUploadJCBO.getSuccessList().size(), ObjectUtil.isBlank(singleUploadJCBO.getErrorList())?0:singleUploadJCBO.getErrorList().size()));
		singleUploadLogMessageProvider.sendMessage(Constants.QUEUE_NAME_FOR_SINGLE_UPLOAD_LOG, model);
        logger.debug("singleUploadLogMesssage Send Message: " + model);	
	}
	/**
	 * 获取文件类型
	 * @author longguoyou
	 * @date 2017年7月3日
	 * @param filePath
	 * @return
	 */
	private Short getFileFormat(String filePath){
		String format = filePath.substring(filePath.lastIndexOf(SymbolConstants.DOT)+ Constants.NUM_1);
		switch(format){
		case "txt":
			return (short)1;
		case "zip":
			return (short)2;
		case "rar":
			return (short)3;
		default:
			return null;	
		}
	}
}
