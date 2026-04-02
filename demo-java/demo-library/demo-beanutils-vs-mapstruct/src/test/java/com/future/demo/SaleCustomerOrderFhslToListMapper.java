package com.future.demo;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SaleCustomerOrderFhslToListMapper {

    SaleCustomerOrderFhslToListMapper INSTANCE = Mappers.getMapper(SaleCustomerOrderFhslToListMapper.class);

    void copy(SaleCustomerOrderFhslResponseVo source, @MappingTarget SaleCustomerOrderInfoForListVo target);
}
