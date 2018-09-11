package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.base.util.UserUtil;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.FootballSelectedMacthService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.EncodingType;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.FileUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadErrorResultBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import com.hhly.skeleton.user.bo.UserInfoBO;

/**
 * 竞彩足球【选择场次】单式上传
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
@Service("selectedMatchService")
public class FootballSelectedMatchServiceImpl extends SingleUploadOrderValidateMethod implements FootballSelectedMacthService {
	
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
		//String[] uploadDatas= FileUtil.readFileFromQiniu(jczqSingleUploadVO, (QiniuUploadVO)map.get("qiniuUploadVO"),EncodingType.GBK.getShortName());
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
		//获取赛事编号
		List<String> listMatchCode = super.getListMatchFromParam(jczqSingleUploadVO.getSelectedMatchs());
		//对阵信息对象集
		List<JczqDaoBO> listJczqDaoBO = new ArrayList<JczqDaoBO>();
		JczqDaoBO jczqDaoBO = null;
		for(int i = 0; i < listMatchCode.size(); i++){
			jczqDaoBO = jcDataService.findSingleUpMatchDataByOfficialCode(listMatchCode.get(i));
			if(!ObjectUtil.isBlank(jczqDaoBO)){
				listJczqDaoBO.add(jczqDaoBO);
			}
		}
		resultBO = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), JCZQConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode()));
		if(resultBO.isError()){
			return resultBO;
		}
		Map<String, String> mapTransfer = (Map<String,String>)resultBO.getData();
		jczqSingleUploadVO.setTransfer(mapTransfer);
		resultBO = super.verifyOfficalMatchCodes(jczqSingleUploadVO);
		if(resultBO.isError()){
			return resultBO;
		}
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
		SingleUploadJCBO singleUploadBO = new SingleUploadJCBO();
		singleUploadBO.setLoseTotal(errorList.size());
		singleUploadBO.setErrorList(errorList);
		singleUploadBO.setWinTotal(successList.size());
		singleUploadBO.setSuccessList(successList);
		singleUploadBO.setFileName(jczqSingleUploadVO.getFilePath().replace(singleUploadDir, SymbolConstants.ENPTY_STRING));
		setValueOfSaleEndTimeAndIssueCode(singleUploadBO, listJczqDaoBO);
		return ResultBO.ok(singleUploadBO);
	}
}
