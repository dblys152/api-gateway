package com.ys.authentication.adapter.out.persistence;

import com.ys.authentication.domain.AuthenticationInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthenticationInfoRepository {
    void insert(AuthenticationInfo authenticationInfo);
}
