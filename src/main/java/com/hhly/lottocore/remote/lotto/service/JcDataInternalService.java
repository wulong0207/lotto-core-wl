package com.hhly.lottocore.remote.lotto.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.sport.bo.*;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @desc 竞彩内部接口(对阵,SP等相关数据)
 * @author zhanglei
 * @date 2017年6月22日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface JcDataInternalService {
    /**
     * 查询竞彩足球受注赛程数据
     *
     * @param vo
     * @return
     */
    ResultBO<JczqMainDataBO> findJczqData(JcParamVO vo);

    /**
     * 查询竞彩足球历史SP值
     *
     * @return
     */
    ResultBO<List<String[]>> findJczqWdfSpData(Long sportAgainstInfoId, Short type);

    /**
     * 查询竞彩足球销售截止的赛程数量。
     *
     * @return
     */
    Integer findJczqSaleEndDataTotal();

    /**
     * 查询竞彩足球销售截止的赛程。
     *
     * @return
     */
    ResultBO<JczqMainDataBO> findJczqSaleEndData();

    /**
     * 根据系统编号获取竞彩足球订单对阵信息
     *
     * @param systemCode
     * @return
     */
    JczqOrderBO findJczqOrderBOBySystemCode(String systemCode);

    /**
     * 查询竞彩篮球受注赛程数据以及SP值
     *
     * @param vo
     * @return
     */
    ResultBO<JclqMainDataBO> findJclqData(JcParamVO vo);


    /**
     * 根据系统编号获取竞彩篮球订单对阵信息
     *
     * @param systemCode
     * @return
     */
    JclqOrderBO findJclqOrderBOBySystemCode(String systemCode);


    /**
     * 根据老足彩彩期查询老足投注赛程数据
     *
     * @param issueCode
     * @param lotteryCode
     * @return
     */
    ResultBO<List<JcOldDataBO>> findJcOldData(String issueCode, String lotteryCode);


    /**
     * 根据彩期获取竞彩足球订单对阵信息
     *
     * @param issueCode
     * @return
     */
    ResultBO<List<JczqOrderBO>> findJczqOrderDataByIssueCode(String issueCode);


    /**
     * 根据周几001 获取对应
     *
     * @param officialCode
     * @return 单式上传对阵json字符串
     */
    JczqDaoBO findSingleUpMatchDataByOfficialCode(String officialCode);
}
