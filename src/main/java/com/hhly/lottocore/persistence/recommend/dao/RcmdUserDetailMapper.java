package com.hhly.lottocore.persistence.recommend.dao;


import com.hhly.skeleton.lotto.base.recommend.bo.RcmdPersonStatisInfo;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdUserDetailBO;

import java.util.List;

public interface RcmdUserDetailMapper {


    int insert(RcmdUserDetailBO record);


    List<RcmdPersonStatisInfo> queryPersonStatisInfo(Integer userId);

}