package com.hhly.lottocore.persistence.sport.dao;

import com.hhly.lottocore.persistence.sport.po.SportStatusWFPO;

public interface SportStatusWFDaoMapper {

    int insert(SportStatusWFPO record);


    int updateByPrimaryKey(SportStatusWFPO record);
}