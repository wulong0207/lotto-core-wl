package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.BJDCSelectedMatchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadErrorResultBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 北京单场【选择场次】单式上传
 * @author longguoyou
 * @date 2017年8月9日
 * @compay 益彩网络科技有限公司
 */
@Service("bjdcSelectedMatchService")
public class BJDCSelectedMatchServiceImpl extends SingleUploadOrderValidateMethod implements BJDCSelectedMatchService {

	private static Logger logger = LoggerFactory.getLogger(BJDCSelectedMatchServiceImpl.class);
	@Autowired
	private UserUtil userUtil;
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	@Value("${single_upload_dir}")
	private String singleUploadDir;
	
	@Override
	public ResultBO<?> validate(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception {
//		String[] uploadDatas = FileUtil.readFileLine(jczqSingleUploadVO.getFilePath(), EncodingType.UFT8.getShortName());
		long begin = System.currentTimeMillis();
		//7牛云 读取文件内容
		ResultBO<?> resultBO = FileUtil.readFileFromQiniu(jczqSingleUploadVO, (QiniuUploadVO)map.get("qiniuUploadVO"),  EncodingType.GBK.getShortName());
		if(resultBO.isError()){
			return resultBO;
		}
		logger.info("读取七牛云文件耗时：" + (System.currentTimeMillis()-begin) /1000 + "秒");
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
		//获取赛事编号
		List<String> listMatchCode = super.getListMatchFromParam(jczqSingleUploadVO.getSelectedMatchs());
		//对阵信息对象集
		List<BjDaoBO> listBjDaoBO = new ArrayList<BjDaoBO>();
		BjDaoBO bjDaoBO = null;
		long begin2 = System.currentTimeMillis();
		for(int i = 0; i < listMatchCode.size(); i++){
			bjDaoBO = jcDataService.findBjSingleDataByBjNum(listMatchCode.get(i), String.valueOf(jczqSingleUploadVO.getLotteryCode()));
			if(!ObjectUtil.isBlank(bjDaoBO)){
				listBjDaoBO.add(bjDaoBO);
			}
		}
	    logger.info("完成比赛场次查询缓存验证耗时：" + (System.currentTimeMillis()-begin2) /1000 + "秒");
		if(BJDCConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode()).equals("Q")){
			resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), "BQ");
		}else{
			resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), BJDCConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode()));
		}
		if(resultBO.isError()){
			return resultBO;
		}
		Map<String, String> mapTransfer = (Map<String,String>)resultBO.getData();
		jczqSingleUploadVO.setTransfer(mapTransfer);
		long begin3 = System.currentTimeMillis();
		resultBO = super.verifyBjNum(listBjDaoBO);
		if(resultBO.isError()){
			return resultBO;
		}
		logger.info("验证赛事截止销售时间：" + (System.currentTimeMillis()-begin3) /1000 + "秒");
		long begin4 = System.currentTimeMillis();
		if(!ObjectUtil.isBlank(uploadDatas)){
			for(int i = 0; i < uploadDatas.length; i++){
				if(!ObjectUtil.isBlank(uploadDatas[i])){
					resultBO = super.verify(jczqSingleUploadVO, uploadDatas[i], i+1);
					if(resultBO.isError()){
						errorList.add(new SingleUploadErrorResultBO(i+1, uploadDatas[i], resultBO.getErrorCode()));
					}else{
						SingleUploadSuccessResultBO successResultBO = (SingleUploadSuccessResultBO)resultBO.getData();
						successList.add(successResultBO);
					}
				}
			}
		}
		logger.info("全部验证完毕耗时：" + (begin-begin4) /1000 + "秒");
		long begin5 = System.currentTimeMillis();
		SingleUploadJCBO singleUploadBO = new SingleUploadJCBO();
		singleUploadBO.setLoseTotal(errorList.size());
		singleUploadBO.setErrorList(errorList);
		singleUploadBO.setWinTotal(successList.size());
		singleUploadBO.setSuccessList(successList);
		singleUploadBO.setFileName(jczqSingleUploadVO.getFilePath().replace(singleUploadDir, SymbolConstants.ENPTY_STRING));
		setValueOfSaleEndTimeAndIssueCodeBj(singleUploadBO, listBjDaoBO);
		logger.info("最后排序耗时：" + (System.currentTimeMillis()-begin5) /1000 + "秒");
		return ResultBO.ok(singleUploadBO);
	}

}
