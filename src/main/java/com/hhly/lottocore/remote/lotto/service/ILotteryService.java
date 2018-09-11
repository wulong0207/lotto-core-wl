package com.hhly.lottocore.remote.lotto.service;


import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.cms.lotterymgr.bo.LotteryTypeBO;
import com.hhly.skeleton.cms.operatemgr.vo.OperateAdVO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotBettingMulBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

import java.util.Date;
import java.util.List;

/**
 * @author Administrator on
 * @version 1.0
 * @desc 彩种service
 * @date 2017/4/28.
 * @company 益彩网络科技有限公司
 */
public interface ILotteryService {
    /**
     * 查询 投注注数，倍数截止时间信息表
     * @param lotteryCode
     * @return
     */
    ResultBO<List<LotBettingMulBO>> findLotteryDettingMul(Integer lotteryCode);
    /**
     * 查询彩种信息
     * @return
     */
    ResultBO<List<LotteryTypeBO>> findAllLotteryType();
    /**
     * 查询高频彩当天最大截止销售时间
     * @param lotteryCode
     * @return
     */
    Date findMaxEndDrawTime(int lotteryCode);
    
    LotteryBO findSingleFront(LotteryVO lotteryVO);
    
    /**
     * 通过开奖彩种类型查询彩种集合
     * @param lotteryVO
     * @return
     * @date 2017年9月23日上午11:27:42
     * @author cheng.chen
     */
    List<LotteryBO> queryLotterySelectList(LotteryVO vo);
}
