
package com.hhly.lottocore.config;

import com.hhly.lottocore.remote.recommend.service.IRcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import com.hhly.lottocore.remote.exporter.LottoHessianServiceExporter;
import com.hhly.lottocore.remote.lotto.service.IFootBallAnalysisService;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.lotto.service.IJczqOrderService;
import com.hhly.lottocore.remote.lotto.service.ILotteryIssueService;
import com.hhly.lottocore.remote.lotto.service.ILotteryService;
import com.hhly.lottocore.remote.lotto.service.ISportDataService;
import com.hhly.lottocore.remote.numorder.service.OrderAddService;
import com.hhly.lottocore.remote.ordercopy.service.IOrderCopyService;
import com.hhly.lottocore.remote.ordercopy.service.v1_1.IOrderCopyServiceV11;
import com.hhly.lottocore.remote.ordergroup.service.IOrderGroupService;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.lottocore.remote.sportorder.service.IOrderService;
import com.hhly.lottocore.remote.sportorder.service.ISingleOrderService;
import com.hhly.lottocore.remote.sportorder.service.ITicketDetailService;
import com.hhly.lottocore.remote.sportorder.service.ValidateOrderPayTimeService;
import com.hhly.lottocore.remote.trend.service.IDltTrendService;
import com.hhly.lottocore.remote.trend.service.IF3dTrendService;
import com.hhly.lottocore.remote.trend.service.IHighTrendService;
import com.hhly.lottocore.remote.trend.service.INumTrendService;
import com.hhly.lottocore.remote.trend.service.IPl3TrendService;
import com.hhly.lottocore.remote.trend.service.IPl5TrendService;
import com.hhly.lottocore.remote.trend.service.IQlcTrendService;
import com.hhly.lottocore.remote.trend.service.IQxcTrendService;
import com.hhly.lottocore.remote.trend.service.ISsqTrendService;

/**
 * @desc    
 * @author  cheng chen
 * @date    2018年8月3日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Configuration
public class HessionServerConfig {
	
	@Autowired
	ILotteryIssueService lotteryIssueService;
	
	@Autowired
	INumTrendService numTrendService;
	
	@Autowired
	IDltTrendService dltTrendService;
	
	@Autowired
	ISsqTrendService ssqTrendService;
	
	@Autowired
	IF3dTrendService f3dTrendService;
	
	@Autowired
	IPl3TrendService pl3TrendService;
	
	@Autowired
	IPl5TrendService pl5TrendService;
	
	@Autowired
	IQlcTrendService qlcTrendService;
	
	@Autowired
	IQxcTrendService qxcTrendService;
	
	@Autowired
	IHighTrendService highTrendService;
	
	@Autowired
	IHighTrendService c11x5TrendService;

	@Autowired
	IHighTrendService sscTrendService;
	
	@Autowired
	IHighTrendService k3TrendService;
	
	@Autowired
	IHighTrendService kl10TrendService;	
	
	@Autowired
	IHighTrendService pokeTrendService;	
	
	@Autowired
	IHighTrendService kl12TrendService;
	
	@Autowired
	IHighTrendService xyscTrendService;	
	
	@Autowired
	IHighTrendService sslTrendService;
	
	@Autowired
	IHighTrendService qyhTrendService;
	
	@Autowired
	IHighTrendService yydjTrendService;
	
	@Autowired
	IHighTrendService bbwpTrendService;	
	
	@Autowired
	IHighTrendService kzcTrendService;
	
	@Autowired
	IOrderService orderService;
	
	@Autowired
	IOrderSearchService orderSearchService;
	
	@Autowired
	IJczqOrderService jczqOrderService;
	
	@Autowired
	IJcDataService jcDataService;
	
	@Autowired
	ISportDataService iSportDataService;
	
	@Autowired
	ILotteryService lotteryService;
	
	@Autowired
	ISingleOrderService singleOrderService;
	
	@Autowired
	ValidateOrderPayTimeService validateOrderPayTimeService;
	
	@Autowired
	IOrderCopyService orderCopyService;
	
	@Autowired
	IOrderCopyServiceV11 orderCopyServiceV11;
	
	@Autowired
	IOrderGroupService orderGroupService;

	@Autowired
	IRcmdService rcmdService;
	
	@Autowired
	ITicketDetailService ticketDetailService;
	
	@Autowired
	OrderAddService orderAddService;
	
	@Autowired
	IFootBallAnalysisService iFootBallAnalysisService;
	
    @Bean(name = "/remote/lotteryIssueService")
    public HessianServiceExporter lotteryIssueService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(lotteryIssueService);
        exporter.setServiceInterface(ILotteryIssueService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/numTrendService")
    public HessianServiceExporter numTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(numTrendService);
        exporter.setServiceInterface(INumTrendService.class);
        return exporter;
    }    

    @Bean(name = "/remote/dltTrendService")
    public HessianServiceExporter dltTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(dltTrendService);
        exporter.setServiceInterface(IDltTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/ssqTrendService")
    public HessianServiceExporter ssqTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(ssqTrendService);
        exporter.setServiceInterface(ISsqTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/f3dTrendService")
    public HessianServiceExporter f3dTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(f3dTrendService);
        exporter.setServiceInterface(IF3dTrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/pl3TrendService")
    public HessianServiceExporter pl3TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(pl3TrendService);
        exporter.setServiceInterface(IPl3TrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/pl5TrendService")
    public HessianServiceExporter pl5TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(pl5TrendService);
        exporter.setServiceInterface(IPl5TrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/qlcTrendService")
    public HessianServiceExporter qlcTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(qlcTrendService);
        exporter.setServiceInterface(IQlcTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/qxcTrendService")
    public HessianServiceExporter qxcTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(qxcTrendService);
        exporter.setServiceInterface(IQxcTrendService.class);
        return exporter;
    }

    @Bean(name = "/remote/highTrendService")
    public HessianServiceExporter highTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(highTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/c11x5TrendService")
    public HessianServiceExporter c11x5TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(c11x5TrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/sscTrendService")
    public HessianServiceExporter sscTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(sscTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/k3TrendService")
    public HessianServiceExporter k3TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(k3TrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/kl10TrendService")
    public HessianServiceExporter kl10TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(kl10TrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }

    @Bean(name = "/remote/pokeTrendService")
    public HessianServiceExporter pokeTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(pokeTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/kl12TrendService")
    public HessianServiceExporter kl12TrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(kl12TrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/xyscTrendService")
    public HessianServiceExporter xyscTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(xyscTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/sslTrendService")
    public HessianServiceExporter sslTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(sslTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/qyhTrendService")
    public HessianServiceExporter qyhTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(qyhTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/yydjTrendService")
    public HessianServiceExporter yydjTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(yydjTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    } 
    
    @Bean(name = "/remote/bbwpTrendService")
    public HessianServiceExporter bbwpTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(bbwpTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/kzcTrendService")
    public HessianServiceExporter kzcTrendService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(kzcTrendService);
        exporter.setServiceInterface(IHighTrendService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/orderService")
    public HessianServiceExporter orderService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderService);
        exporter.setServiceInterface(IOrderService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/orderSearchService")
    public HessianServiceExporter orderSearchService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderSearchService);
        exporter.setServiceInterface(IOrderSearchService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/jczqOrderService")
    public HessianServiceExporter jczqOrderService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(jczqOrderService);
        exporter.setServiceInterface(IJczqOrderService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/jcDataService")
    public HessianServiceExporter jcDataService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(jcDataService);
        exporter.setServiceInterface(IJcDataService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/iSportDataService")
    public HessianServiceExporter iSportDataService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(iSportDataService);
        exporter.setServiceInterface(ISportDataService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/lotteryService")
    public HessianServiceExporter lotteryService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(lotteryService);
        exporter.setServiceInterface(ILotteryService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/singleOrderService")
    public HessianServiceExporter singleOrderService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(singleOrderService);
        exporter.setServiceInterface(ISingleOrderService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/validateOrderPayTimeService")
    public HessianServiceExporter validateOrderPayTimeService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(validateOrderPayTimeService);
        exporter.setServiceInterface(ValidateOrderPayTimeService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/orderCopyService")
    public HessianServiceExporter orderCopyService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderCopyService);
        exporter.setServiceInterface(IOrderCopyService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/orderCopyServiceV11")
    public HessianServiceExporter orderCopyServiceV11() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderCopyServiceV11);
        exporter.setServiceInterface(IOrderCopyServiceV11.class);
        return exporter;
    }
    
    @Bean(name = "/remote/orderGroupService")
    public HessianServiceExporter orderGroupService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderGroupService);
        exporter.setServiceInterface(IOrderGroupService.class);
        return exporter;
    }

    @Bean(name = "/remote/rcmdService")
    public HessianServiceExporter rcmdService() {
        LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(rcmdService);
        exporter.setServiceInterface(IRcmdService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/ticketDetailService")
    public HessianServiceExporter ticketDetailService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(ticketDetailService);
        exporter.setServiceInterface(ITicketDetailService.class);
        return exporter;
    }  
    
    @Bean(name = "/remote/orderAddService")
    public HessianServiceExporter orderAddService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(orderAddService);
        exporter.setServiceInterface(OrderAddService.class);
        return exporter;
    }
    
    @Bean(name = "/remote/iFootBallAnalysisService")
    public HessianServiceExporter iFootBallAnalysisService() {
    	LottoHessianServiceExporter exporter = new LottoHessianServiceExporter();
        exporter.setService(iFootBallAnalysisService);
        exporter.setServiceInterface(IFootBallAnalysisService.class);
        return exporter;
    }    
}
