package com.hhly.lottocore.persistence.recommend.dao;


import com.hhly.skeleton.lotto.base.recommend.bo.RcmdUserTypeBO;

public interface RcmdUserTypeMapper {


    int insert(RcmdUserTypeBO record);

    RcmdUserTypeBO queryRcmdUserTypeByUserId(Integer userId);

}