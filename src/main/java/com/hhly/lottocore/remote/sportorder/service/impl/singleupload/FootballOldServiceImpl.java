package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.FootballOldService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.OldFootballConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.ArrayUtil;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadErrorResultBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 老足彩单式上传
 * @author longguoyou
 * @date 2017年7月14日
 * @compay 益彩网络科技有限公司
 */
@Service("footballOldService")
public class FootballOldServiceImpl extends SingleUploadOrderValidateMethod implements FootballOldService {
	
	@Autowired
	private UserUtil userUtil;
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	@Value("${single_upload_dir}")
	private String singleUploadDir;

	@Override
	public ResultBO<?> validate(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception {
		
//		String[] uploadDatas = FileUtil.readFileLine(jczqSingleUploadVO.getFilePath(), EncodingType.UFT8.getShortName());
		//7牛云 
		ResultBO<?> resultBO = FileUtil.readFileFromQiniu(jczqSingleUploadVO, (QiniuUploadVO)map.get("qiniuUploadVO"),  EncodingType.GBK.getShortName());
		if(resultBO.isError()){
			return resultBO;
		}
		List<String> ret = (List<String>)resultBO.getData();
		String[] uploadDatas = ret.toArray(new String[ret.size()]);

		UserInfoBO userInfoBO = userUtil.getUserByToken(jczqSingleUploadVO.getToken());
		if(!ObjectUtil.isBlank(userInfoBO)){
			jczqSingleUploadVO.setUserId(userInfoBO.getId());
		}
		// 内容验证出错List
		List<SingleUploadErrorResultBO> errorList = new ArrayList<SingleUploadErrorResultBO>();
		// 内容验证正确List
		List<SingleUploadSuccessResultBO> successList = new ArrayList<SingleUploadSuccessResultBO>();
		//获取用户替换字符
		resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), OldFootballConstants.getShortName(jczqSingleUploadVO.getLotteryCode()));
		if(resultBO.isError()){
			return resultBO;
		}
		Map<String, String> mapTransfer = (Map<String,String>)resultBO.getData();
		jczqSingleUploadVO.setTransfer(mapTransfer);
		//验证总注数 限制总条数，不分正确/错误 
		if(uploadDatas.length > OldFootballConstants.BET_NUM_LIMIT){
			return ResultBO.err(MessageCodeConstants.BET_NUM_LIMIT_SERVICE);
		}
		//验证投注内容同时包含#和*时
		resultBO = hasNumberSignAndStar(uploadDatas);
		if(resultBO.isError()){return resultBO;}
		Map<String,SingleUploadSuccessResultBO> successResult = new ConcurrentHashMap<String,SingleUploadSuccessResultBO>();
		List<String> errorResult = new ArrayList<String>();
		if(!ObjectUtil.isBlank(uploadDatas)){
			for(int i = 0; i < uploadDatas.length; i++){
				if(!ObjectUtil.isBlank(uploadDatas[i])){
					if(successResult.keySet().contains(uploadDatas[i])){
						SingleUploadSuccessResultBO successResultBO = new SingleUploadSuccessResultBO(i+1, uploadDatas[i], null,
								successResult.get(uploadDatas[i]).getMultiple(), successResult.get(uploadDatas[i]).getCount());
						successResultBO.setBetDetails(successResult.get(uploadDatas[i]).getBetDetails());
						successList.add(successResultBO);
						continue;
					}
					if(errorResult.contains(uploadDatas[i])){
						errorList.add(new SingleUploadErrorResultBO(i+1, uploadDatas[i], resultBO.getErrorCode()));
						continue;
					}
					resultBO = super.verify(jczqSingleUploadVO, uploadDatas[i], i+1);
					if(resultBO.isError()){
						errorList.add(new SingleUploadErrorResultBO(i+1, uploadDatas[i], resultBO.getErrorCode()));
						errorResult.add(uploadDatas[i]);
					}else{
						SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO)resultBO.getData();
						successList.add(successResultBO);
						successResult.put(uploadDatas[i],successResultBO);
					}
				}
			}
		}
		SingleUploadJCBO singleUploadBO = new SingleUploadJCBO();
		singleUploadBO.setLoseTotal(errorList.size());
		singleUploadBO.setErrorList(errorList);
		singleUploadBO.setWinTotal(successList.size());
		singleUploadBO.setSuccessList(successList);
		singleUploadBO.setFileName(jczqSingleUploadVO.getFilePath().replace(singleUploadDir, SymbolConstants.ENPTY_STRING));
		return ResultBO.ok(singleUploadBO);
	}
    
	private ResultBO<?> hasNumberSignAndStar(String[] uploadDatas) {
		if(ArrayUtil.judeIfContains(uploadDatas, SymbolConstants.NUMBER_SIGN, SymbolConstants.STAR)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_CONTAINS_NUMBER_SIGN_STAR_SERVICE);
		}
		return ResultBO.ok();
	}
}
