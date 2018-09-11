package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.remote.sportorder.service.BJDCIncludeMatchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadErrorResultBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBetDetail;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 北京单场【包含场次】单式上传
 * @author longguoyou
 * @date 2017年8月9日
 * @compay 益彩网络科技有限公司
 */
@Service("bjdcIncludeMatchService")
public class BJDCIncludeMatchServiceImpl extends SingleUploadOrderValidateMethod implements BJDCIncludeMatchService {

	private static Logger logger = LoggerFactory.getLogger(IncludeMatchServiceImpl.class);
	
	@Autowired
	private UserUtil userUtil;
	
	@Value("${single_upload_dir}")
	private String singleUploadDir;

	@Override
	public ResultBO<?> validate(SingleUploadJCVO singleUploadVO, Map<String, Object> map) throws Exception {
		UserInfoBO userInfo = userUtil.getUserByToken(singleUploadVO.getToken());
		if(!ObjectUtil.isBlank(userInfo)){
			singleUploadVO.setUserId(userInfo.getId());
		}
		// 1. 读取文件
//		String[] uploadDatas = FileUtil.readFileLine(singleUploadVO.getFilePath(), EncodingType.UFT8.getShortName());
		
		//7牛云 读取文件内容
		ResultBO<?> resultBO = FileUtil.readFileFromQiniu(singleUploadVO, (QiniuUploadVO)map.get("qiniuUploadVO"),  EncodingType.GBK.getShortName());
		if(resultBO.isError()){
			return resultBO;
		}
		List<String> ret = (List<String>)resultBO.getData();
		String[] uploadDatas = ret.toArray(new String[ret.size()]);

		singleUploadVO.setPlayCode(BJDCConstants.getShortName(singleUploadVO.getLotteryChildCode()));
		// 2. 是否转换格式，1：是（按标准格式替换转换内容） 2：无
		Map<String, String> shiftMap = new HashMap<String, String>();
		if (!ObjectUtil.isBlank(singleUploadVO.getShiftContent())) {
			ResultBO<?> result = null;
			if(singleUploadVO.getPlayCode().equals("Q")){
				result = getStandarTransfer(singleUploadVO.getShiftContent(),"BQ");
			}else{
				result = getStandarTransfer(singleUploadVO.getShiftContent(),singleUploadVO.getPlayCode());
			}
			if(result.isError()){
				return result;
			}
			shiftMap = (Map<String, String>)result.getData(); 
		}
		// 3. 循环验证
		// 内容验证出错List
		List<SingleUploadErrorResultBO> errorList = new ArrayList<SingleUploadErrorResultBO>();
		// 内容验证正确List
		List<SingleUploadSuccessResultBO> successList = new ArrayList<SingleUploadSuccessResultBO>();
		
		int length = getLength(singleUploadVO.getLotteryCode(),singleUploadVO.getLotteryChildCode());
		for (int i = 0; i < uploadDatas.length; i++) {
			if(ObjectUtil.isBlank(uploadDatas[i])){
				continue;
			}
			ResultBO<?> verifyResult = verifySingleUploadContent(uploadDatas[i], singleUploadVO.getPlayCode(),
					length, shiftMap, singleUploadVO.getLotteryCode());
			if (verifyResult.isError()) {
				errorList.add(new SingleUploadErrorResultBO(i + 1, uploadDatas[i],verifyResult.getErrorCode()));
			} else {
				SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO)verifyResult.getData();
				if(!ObjectUtil.isBlank(successResultBO)){
					successList.add(successResultBO);
				}
			}
		}

		// 4. 获取该方案最后截止销售时间
		List<JczqDaoBO> listJczqDaoBO = new ArrayList<JczqDaoBO>();
		if (!ObjectUtil.isBlank(successList)) {
			for (int i = 0; i < successList.size(); i++) {
				for (SingleUploadJCBetDetail s : successList.get(i).getBetDetails()) {
					if(!ObjectUtil.isBlank(s.getIssueCode())&&!ObjectUtil.isBlank(s.getSaleEndTime())){
						JczqDaoBO jczqDaoBO = new JczqDaoBO();
						jczqDaoBO.setIssueCode(s.getIssueCode());
						jczqDaoBO.setSaleEndDate(s.getSaleEndTime());
						listJczqDaoBO.add(jczqDaoBO);
					}
				}
			}
		}
		// 5. 新增上传记录日志

		// 6. 封装参数返回
		SingleUploadJCBO singleUploadBO = new SingleUploadJCBO();
		singleUploadBO.setErrorList(errorList);
		singleUploadBO.setSuccessList(successList);
		singleUploadBO.setLoseTotal(errorList.size());
		singleUploadBO.setWinTotal(successList.size());
		singleUploadBO.setFileName(singleUploadVO.getFilePath().replace(singleUploadDir, SymbolConstants.ENPTY_STRING));
		setValueOfSaleEndTimeAndIssueCode(singleUploadBO,listJczqDaoBO);
		return ResultBO.ok(singleUploadBO);
	}
}
