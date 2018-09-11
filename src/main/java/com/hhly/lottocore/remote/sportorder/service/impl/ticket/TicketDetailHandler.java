package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jsk3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.k3.Jxk3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.kl10.Cqkl10OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.poker.SdPokerOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.ssc.CqsscOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.D11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.highordervalidator.x115.Sd11x5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.DltOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.F3dOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.Pl3OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.Pl5OrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.QlcOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.QxcOrderDetailValidate;
import com.hhly.lottocore.remote.numorder.service.impl.numvalidator.SsqOrderDetailValidate;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryChildEnum;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.sportsutil.SportsZsUtil;
import com.hhly.skeleton.lotto.base.order.vo.BetContentVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;

/**
 * @author yuanshangbing
 * @version 1.0
 * @desc 出票明细页面 票信息抽象类
 * @date 2017/11/1 10:47
 * @company 益彩网络科技公司
 */
@Component("ticketDetailHandler")
public class TicketDetailHandler{

    /** 双色球订单明细校验 */
    @Resource(name="ssqOrderDetailValidate")
    private SsqOrderDetailValidate ssqOrderDetailValidate;
    /** 大乐透订单明细校验 */
    @Resource(name="dltOrderDetailValidate")
    private DltOrderDetailValidate dltOrderDetailValidate;
    /** 福彩3D订单明细校验 */
    @Resource(name="f3dOrderDetailValidate")
    private F3dOrderDetailValidate f3dOrderDetailValidate;
    /** 排列三订单明细校验 */
    @Resource(name="pl3OrderDetailValidate")
    private Pl3OrderDetailValidate pl3OrderDetailValidate;
    /** 排列五订单明细校验 */
    @Resource(name="pl5OrderDetailValidate")
    private Pl5OrderDetailValidate pl5OrderDetailValidate;
    /** 七乐彩订单明细校验 */
    @Resource(name="qlcOrderDetailValidate")
    private QlcOrderDetailValidate qlcOrderDetailValidate;
    /** 七星彩订单明细校验 */
    @Resource(name="qxcOrderDetailValidate")
    private QxcOrderDetailValidate qxcOrderDetailValidate;

    /** 山东十一选五订单明细校验 */
    @Resource(name="sd11x5OrderDetailValidate")
    private Sd11x5OrderDetailValidate sd11x5OrderDetailValidate;
    /** 广东十一选五订单明细校验 */
    @Resource(name="d11x5OrderDetailValidate")
    private D11x5OrderDetailValidate d11x5OrderDetailValidate;

    /** 江苏快3订单明细校验 */
    @Resource(name="jsk3OrderDetailValidate")
    private Jsk3OrderDetailValidate jsk3OrderDetailValidate;

    /** 江西快3订单明细校验 */
    @Resource(name="jxk3OrderDetailValidate")
    private Jxk3OrderDetailValidate jxk3OrderDetailValidate;

    /** 重庆时时彩订单明细校验 */
    @Resource(name="cqsscOrderDetailValidate")
    private CqsscOrderDetailValidate cqsscOrderDetailValidate;

    /** 快乐扑克订单明细校验 */
    @Resource(name="sdPokerOrderDetailValidate")
    private SdPokerOrderDetailValidate sdPokerOrderDetailValidate;

    /** 重庆快乐十分订单明细校验 */
    @Resource(name="cqkl10OrderDetailValidate")
    private Cqkl10OrderDetailValidate cqkl10OrderDetailValidate;

    //==========================================================================================

    /**
     * 竞彩
     * 根据票内容计算注数（篮球，足球，胜负彩，北京单场，胜负过关）
     * @param content
     * @return
     */
    protected int getSportsManyNote(String content,Integer lotteryCode){
       return SportsZsUtil.getSportsManyNote(content,lotteryCode);
    }

    /**
     * 数字彩/高频彩计算注数
     * @param content
     * @return
     */
    protected int getNumberManyNote(String content,Integer lotteryCode,Integer lotteryChildCode,Integer contentType){
        BetContentVO betContentVO = new BetContentVO();
        int num =0;
       //票内容已经没有胆拖，只有单式，复式
        String [] ticketContents = content.split(SymbolConstants.SEMICOLON);
        if(ticketContents.length>0){//一张票里面有多注
            for(String ticket: ticketContents){
                num = num + countNumNote(lotteryCode,betContentVO,lotteryChildCode,contentType,ticket);
            }
        }else {//单注
            num = countNumNote(lotteryCode,betContentVO,lotteryChildCode,contentType,content);
        }
        return num;
    }

    /**
     * 根据彩种计算数字彩/高频彩注数
     * @return
     */
    private int countNumNote(Integer lotteryCode,BetContentVO betContentVO,Integer lotteryChildCode,Integer contentType,String content){
        OrderDetailVO orderDetail = new OrderDetailVO();
        orderDetail.setContentType(contentType);
        orderDetail.setLotteryChildCode(lotteryChildCode);
        orderDetail.setPlanContent(content);
        LotteryEnum.Lottery lottery = LotteryEnum.Lottery.getLottery(lotteryCode);
        switch (lottery) {
            case SSQ://双色球
                getRedAndBlue(content, betContentVO);
                orderDetail.setLotteryChildCode(LotteryChildEnum.LotteryChild.SSQ_PT.getValue());//双色球票表都是普通投注
                return ssqOrderDetailValidate.getBetNum(orderDetail,betContentVO);
            case DLT://大乐透
                getRedAndBlue(content, betContentVO);
                orderDetail.setLotteryChildCode(LotteryChildEnum.LotteryChild.DLT_PT.getValue());//大乐透票表都是普通投注
                return dltOrderDetailValidate.getBetNum(orderDetail,betContentVO);
            case F3D://福彩3D
                setF3DBetContent(betContentVO,contentType,orderDetail);
                return f3dOrderDetailValidate.getBetNum(orderDetail,betContentVO);
            case PL3://排列3
                setPL3BetContent(betContentVO,orderDetail);
                return pl3OrderDetailValidate.getBetNum(orderDetail,betContentVO);
            case PL5://排列5
                setPL5BetContent(betContentVO,contentType,orderDetail);
                return pl5OrderDetailValidate.getBetNum(orderDetail,betContentVO);
            case SDPOKER://山东快乐扑克3
                return sdPokerOrderDetailValidate.validatePlanContent(orderDetail);
            /*case QLC:
                return qlcChaseValidate;
            case QXC:
                return qxcChaseValidate;
            case SD11X5:
                return sd11x5ChaseValidate;
            case D11X5:
                return d11x5ChaseValidate;
            case JSK3:	   //江苏快3
                return jsk3ChaseValidate;
            case JXK3:	   //江西快3
                return jxk3ChaseValidate;
            case CQSSC:
                return cqsscChaseValidate;
            case SDPOKER:
                return sdPokerChaseValidate;
            case CQKL10:
                return cqkl10ChaseValidate;*/
//		case DKL10:
//			return dkl10ChaseValidate;
            default:
                throw new ResultJsonException(ResultBO.err("40502"));
        }


    }

    /**
     * 双色球、大乐透 获取投注内容里面的红球和篮球
     * @param content
     * @param betContentVO
     */
    private void getRedAndBlue(String content, BetContentVO betContentVO) {
        String [] redblue = content.split(SymbolConstants.DOUBLE_ADD);
        betContentVO.setArea1(redblue[0].split(SymbolConstants.COMMA));//红球
        betContentVO.setArea2(redblue[1].split(SymbolConstants.COMMA));//篮球球
    }

    /**
     * 设置福彩3D入参内容
     * @param betContentVO
     */
    private void setF3DBetContent(BetContentVO betContentVO,Integer contentType,OrderDetailVO orderDetail){
        String content = orderDetail.getPlanContent();
        // 子玩法类型
        LotteryChildEnum.LotteryChild lotChild = LotteryChildEnum.LotteryChild.valueOf(orderDetail.getLotteryChildCode());
        // 内容类型
        OrderEnum.BetContentType type = OrderEnum.BetContentType.getContentType(contentType);

        switch (lotChild) {
            case D_DIRECT:
                // 直选(包含单式、复式、和值)
                switch (type) {
                    case SINGLE://单式2|5|8
                        return;
                    case MULTIPLE://复式1,2|4,5|7,8
                        String doubleContent[] = content.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
                        String area3[] =doubleContent[0].split(SymbolConstants.COMMA);
                        String area2[] =doubleContent[1].split(SymbolConstants.COMMA);
                        String area1[] =doubleContent[2].split(SymbolConstants.COMMA);
                        betContentVO.setArea1(area1);
                        betContentVO.setArea2(area2);
                        betContentVO.setArea3(area3);
                        return;
                    case SUM://和值 每张票内容都是单独的数字，每个数字对应了对应的注数
                        // 注数 = 各和值对应注数之和
                        betContentVO.setArea1(new String[]{content});
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            case D_G3:
                // 组三(包含单式、复式、和值)
                switch (type) {
                    case SINGLE://单式  票内容有可能是 1,1,7;1,1,8;7,7,8 或者1,1,3
                        return;
                    case MULTIPLE://票内容 0,1,2,3,4,5,6,7,8,9
                        // 20170701 add 复式两种格式（0,1,2,3或0,1,2,3|0,1,2,3）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
                        //票内容 都是 0,1,2,3
                        betContentVO.setArea1(content.split(SymbolConstants.COMMA));
                        return;
                    case SUM:
                        betContentVO.setArea1(new String[]{content});
                        // 注数 = 各和值对应注数之和
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            case D_G6:
                // 组六(包含单式、复式、胆拖、和值)
                switch (type) {
                    case SINGLE://单式 胆拖的会拆成多注单式
                        return;
                    case MULTIPLE://复式
                        String area1[] = content.split(SymbolConstants.COMMA);
                        betContentVO.setArea1(area1);
                        return;
                    case SUM://和值
                        betContentVO.setArea1(new String[]{content});
                        // 注数 = 各和值对应注数之和
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            default:
                throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
        }


    }

    /**
     * 获取排五计算注数入参内容
     * @return
     */
    private void setPL5BetContent(BetContentVO betContentVO,Integer contentType,OrderDetailVO orderDetail){
        String content = orderDetail.getPlanContent();
        LotteryChildEnum.LotteryChild lotChild = LotteryChildEnum.LotteryChild.valueOf(orderDetail.getLotteryChildCode());
        // 内容类型
        OrderEnum.BetContentType type = OrderEnum.BetContentType.getContentType(contentType);
        switch (lotChild) {
            case PL5_DIRECT:
                // 直选(包含单式、复式)
                switch (type) {
                    case SINGLE:
                        return;
                    case MULTIPLE:
                        String pl5 [] = content.split(SymbolConstants.VERTICAL_BAR);
                        betContentVO.setArea5(pl5[0].split(SymbolConstants.COMMA));//个位
                        betContentVO.setArea4(pl5[1].split(SymbolConstants.COMMA));
                        betContentVO.setArea3(pl5[2].split(SymbolConstants.COMMA));
                        betContentVO.setArea2(pl5[3].split(SymbolConstants.COMMA));
                        betContentVO.setArea1(pl5[2].split(SymbolConstants.COMMA));//万位
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            default:
                throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
        }
    }

    /**
     * 设置排三入参注数内容
     * @return
     */
    private void setPL3BetContent(BetContentVO betContentVO,OrderDetailVO orderDetail){
        String content = orderDetail.getPlanContent();
        // 子玩法类型
        LotteryChildEnum.LotteryChild lotChild = LotteryChildEnum.LotteryChild.valueOf(orderDetail.getLotteryChildCode());
        // 内容类型
        OrderEnum.BetContentType type = OrderEnum.BetContentType.getContentType(orderDetail.getContentType());
        switch (lotChild) {
            case PL3_DIRECT:
                // 直选(包含单式、复式、和值)
                switch (type) {
                    case SINGLE:
                        return;
                    case MULTIPLE:
                        String pl5 [] = content.split(SymbolConstants.VERTICAL_BAR);
                        betContentVO.setArea3(pl5[0].split(SymbolConstants.COMMA));//个位
                        betContentVO.setArea2(pl5[1].split(SymbolConstants.COMMA));
                        betContentVO.setArea1(pl5[2].split(SymbolConstants.COMMA));//百位
                        return;
                    case SUM:
                        betContentVO.setArea1(new String[]{content});
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            case PL3_G3:
                // 组三(包含单式、复式、和值)
                switch (type) {
                    case SINGLE:
                        // 20170701 add 单式两种格式（1,1,0 或 1|0）(无论哪种都是1注)
                        return;
                    case MULTIPLE:
                        // 20170701 add 复式两种格式（0,1,2,3或0,1,2,3|0,1,2,3）(第一种投注内容只有一个区域；第二种投注内容有两个区域)
                        //票内容都是 0,1,2,3
                        String area1[] = content.split(SymbolConstants.COMMA);
                        betContentVO.setArea1(area1);
                        return;
                    case SUM:
                        // 注数 = 各和值对应注数之和
                        betContentVO.setArea1(new String[]{content});
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            case PL3_G6:
                // 组六(包含单式、复式、胆拖、和值)
                //票内容不会有胆拖，会拆成单注
                switch (type) {
                    case SINGLE:
                        return;
                    case MULTIPLE:
                        //1,2,3,4
                        String area1[] = content.split(SymbolConstants.COMMA);
                        betContentVO.setArea1(area1);
                        return;
                    case SUM:
                        // 注数 = 各和值对应注数之和
                        betContentVO.setArea1(new String[]{content});
                        return;
                    default:
                        throw new ResultJsonException(ResultBO.err("40403"));
                }
            default:
                throw new ResultJsonException(ResultBO.err("40234", lotChild.getDesc()));
        }
    }


}
