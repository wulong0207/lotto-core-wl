
package com.hhly.lottocore.controller.lottery.sport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

/**
 * @desc    
 * @author  cheng chen
 * @date    2018年4月20日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RestController
@RequestMapping("/sport/fb")
public class FbController {

    private static final Logger logger = Logger.getLogger(FbController.class);
    
    @Autowired
    IJcDataService jcDataService;
    
    @RequestMapping("findRecommedMatch")
    public Object findYbfRecommedMatch(@RequestBody JcParamVO vo){
    	return ResultBO.ok(jcDataService.findSportMatchFBSPInfo(Integer.valueOf(vo.getLotteryCode()), vo.getIssueCode(), null));
    }
}
