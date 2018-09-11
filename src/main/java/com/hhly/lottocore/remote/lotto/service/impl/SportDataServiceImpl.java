package com.hhly.lottocore.remote.lotto.service.impl;

import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.lottocore.persistence.jc.dao.JcDataDaoMapper;
import com.hhly.lottocore.persistence.jc.dao.MatchDataDaoMapper;
import com.hhly.lottocore.remote.lotto.service.ISportDataService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.page.ISimplePage;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.UserInfoBOUtil;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsBO;
import com.hhly.skeleton.lotto.base.database.bo.OddsFbEuropeOddsMeanBO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsMeanVO;
import com.hhly.skeleton.lotto.base.database.vo.OddsFbEuropeOddsVO;
import com.hhly.skeleton.lotto.base.sport.bo.*;
import com.hhly.skeleton.lotto.base.sport.vo.GjParamVO;
import com.hhly.skeleton.lotto.base.sport.vo.JcParamVO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author lgs on
 * @version 1.0
 * @desc
 * @date 2017/12/13.
 * @company 益彩网络科技有限公司
 */
@Service("iSportDataService")
public class SportDataServiceImpl implements ISportDataService {

    private static final Logger logger = Logger.getLogger(SportDataServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IPageService pageService;

    @Autowired
    private JcDataDaoMapper jcDataDaoMapper;

    @Autowired
    private MatchDataDaoMapper matchDataDaoMapper;

    @Value("${before_file_url}")
    protected String beforeFileUrl;

    private static final DecimalFormat df = new DecimalFormat("##.00");

    /**
     * 获取可以销售的竞彩足球对阵
     *
     * @param vo
     * @return
     */
    @Override
    public List<JczqDaoBO> findJczqData(JcParamVO vo) {
        return jcDataDaoMapper.findJczqData(vo);
    }

    /**
     * 获取销售截止的竞彩足球对阵
     *
     * @param vo
     * @return
     */
    @Override
    public List<JczqDaoBO> findSaleEndJczqData(JcParamVO vo) {
        return jcDataDaoMapper.findJczqSaleEndData(vo);
    }


    /**
     * 获取销售中对阵赛事
     *
     * @param vo
     * @return
     */
    @Override
    public List<MatchDataBO> findMatchData(JcParamVO vo) {
        return matchDataDaoMapper.findMatchData(vo);
    }

    /**
     * 查询受注赛程统计数量
     *
     * @param vo
     * @return
     */
    @Override
    public List<MatchDataBO> findMatchTotal(JcParamVO vo) {
        return jcDataDaoMapper.findMatchTotal(vo);
    }


    /**
     * 查询销售截止场次数
     *
     * @param vo
     * @return
     */
    @Override
    public Integer findJcSaleEndDataTotal(JcParamVO vo) {
        return jcDataDaoMapper.findJcSaleEndDataTotal(vo);
    }

    /**
     * 查询竞彩足球对阵胜平负赔率历史变化
     *
     * @param sportAgainstInfoId
     * @return
     */
    @Override
    public List<SportDataFbWDFBO> findJczqWdfSpData(Long sportAgainstInfoId) {
        return jcDataDaoMapper.findJczqWdfSpData(sportAgainstInfoId);
    }

    /**
     * 获取竞彩篮球受注赛程
     *
     * @param vo
     * @return
     */
    @Override
    public List<JclqDaoBO> findJclqData(JcParamVO vo) {
        return jcDataDaoMapper.findJclqData(vo);
    }

    /**
     * 获取竞彩篮球销售截止赛事
     *
     * @param vo
     * @return
     */
    @Override
    public List<JclqDaoBO> findJclqSaleEndData(JcParamVO vo) {
        return jcDataDaoMapper.findJclqSaleEndData(vo);
    }

    /**
     * 查询竞彩篮球胜平负赔率历史变化
     *
     * @param sportAgainstInfoId
     * @return
     */
    @Override
    public List<SportDataBbWFBO> findJclqWfHistorySpData(Long sportAgainstInfoId) {
        return jcDataDaoMapper.findJclqWfSpData(sportAgainstInfoId);
    }


    /**
     * 查询大小分赔率历史变化
     *
     * @param sportAgainstInfoId
     */
    @Override
    public List<SportDataBbSSSBO> findJclqSssHistorySpData(Long sportAgainstInfoId) {
        return jcDataDaoMapper.findJclqSssSpData(sportAgainstInfoId);
    }

    /**
     * 查询老足彩赛事
     *
     * @param vo
     * @return
     */
    @Override
    public List<JcOldDataBO> findJcOldData(JcParamVO vo) {
        return jcDataDaoMapper.findJcOldData(vo);
    }

    /**
     * 查询老足彩对阵赔率
     *
     * @param vo
     * @return
     */
    @Override
    public List<JcOldDataSpBO> findJcOldDataSp(JcParamVO vo) {
        return jcDataDaoMapper.findJcOldDataSp(vo);
    }

    /**
     * 获取北单销售对阵
     *
     * @param vo
     * @return
     */
    @Override
    public List<BjDaoBO> findBjData(JcParamVO vo) {
        return jcDataDaoMapper.findBjData(vo);
    }

    /**
     * 获取北单销售对阵
     *
     * @param vo
     * @return
     */
    @Override
    public List<BjDaoBO> findBjDataBO(JcParamVO vo) {
        return jcDataDaoMapper.findBjData(vo);
    }


    /**
     * 获取北单销售截止对阵
     *
     * @param vo
     * @return
     */
    @Override
    public List<BjDaoBO> findSaleEndTimeBjData(JcParamVO vo) {
        return jcDataDaoMapper.findSaleEndTimeBjData(vo);
    }

    /**
     * 获取北单赛事信息
     *
     * @param vo
     * @return
     */
    @Override
    public List<MatchDataBO> findBjMatch(JcParamVO vo) {
        return jcDataDaoMapper.findBjMatch(vo);
    }

    /**
     * 根据id查询对阵sp值
     *
     * @param id
     * @return
     */
    @Override
    public JczqDaoBO findFootBallSpById(Long id) {
        return jcDataDaoMapper.findFootBallSpById(id);
    }

    /**
     * 获取平均欧赔
     *
     * @param vo
     * @return
     */
    @Override
    public List<OddsFbEuropeOddsMeanBO> findAvgOddsBySourceId(OddsFbEuropeOddsMeanVO vo) {
        return jcDataDaoMapper.findAvgOddsBySourceId(vo);
    }

    /**
     * 获取投注页面每个平均欧赔
     *
     * @param vo
     * @return
     */
    @Override
    public List<OddsFbEuropeOddsBO> findAvgOdds(OddsFbEuropeOddsVO vo) {
        return jcDataDaoMapper.findAvgOdds(vo);
    }


    /**
     * 根据id获取欧赔
     *
     * @param vo
     * @return
     */
    @Override
    public List<OddsFbEuropeOddsBO> findOddsByEuropeId(OddsFbEuropeOddsVO vo) {
        return jcDataDaoMapper.findOddsByEuropeId(vo);
    }


    @Override
    public ResultBO<?> findGjData(GjParamVO queryVO) {
        PagingBO<GjDataBO> pageData = pageService.getPageData(queryVO, new ISimplePage<GjDataBO>() {

            @Override
            public int getTotal() {
                return jcDataDaoMapper.findGjDataCount(queryVO);
            }

            @Override
            public List<GjDataBO> getData() {
                return jcDataDaoMapper.findGjData(queryVO);
            }
        });
        List<GjDataBO> list = pageData.getData();
        for(GjDataBO bean : list){
            bean.setHomeLogo(UserInfoBOUtil.getHeadUrl(bean.getHomeLogo(), beforeFileUrl));
            bean.setVisitiLogo(UserInfoBOUtil.getHeadUrl(bean.getVisitiLogo(), beforeFileUrl));
            bean.setMatchStatus(getMatchStatus(bean.getMatchStatus()));
            bean.setSp(df.format(Float.parseFloat(bean.getSp())));
        }
        logger.info("查询冠军或冠亚军展示数据列表信息：count= " + pageData.getTotal() + " 条");
        logger.info("查询冠军或冠亚军展示数据列表信息：detailList=" + list.size() + " 条");
        return ResultBO.ok(list);
    }

    /**
     * 处理返回赛事状态
     * @param status
     * @return
     */
    private Integer getMatchStatus(Integer status){
        if(ObjectUtil.isBlank(status)){
            return null;
        }
        if(status == 9 || status == 10 || status == 18){
            return status;
        }
        if(status == 15 || status == 16 || status == 17){
            return 16;
        }
        return null;
    }

    @Override
    public List<Map<Integer, String>> findSelectTeam() {
        return jcDataDaoMapper.getSelectTeam();
    }
}
