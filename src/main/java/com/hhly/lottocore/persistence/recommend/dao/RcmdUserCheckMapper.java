package com.hhly.lottocore.persistence.recommend.dao;


import com.hhly.lottocore.persistence.recommend.po.RcmdUserCheckPO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdUserCheckBO;

public interface RcmdUserCheckMapper {


    int insert(RcmdUserCheckPO record);

    void update(RcmdUserCheckPO record);

    RcmdUserCheckBO queryUserCheckInfo(Integer userId);

    RcmdUserCheckBO queryUserCheckInfoNoFilterStatus(Integer userId);


}