package com.future.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@MappedJdbcTypes(JdbcType.VARCHAR) // Database type
@MappedTypes({List.class}) //java data type
public class ListTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i,
                             List<String> parameter, JdbcType jdbcType) throws SQLException {
        String hobbys = dealListToOneStr(parameter);
        ps.setString(i , hobbys);
    }
    /** * Set concatenation string * @param parameter * @return */
    private String dealListToOneStr(List<String> parameter){

        if(parameter == null || parameter.size() <=0)
            return null;
        String res = "";
        for (int i = 0 ;i < parameter.size(); i++) {

            if(i == parameter.size()-1){

                res+=parameter.get(i);
                return res;
            }
            res+=parameter.get(i)+",";
        }
        return null;
    }
    @Override
    public List<String> getResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        if(value == null) {
            value = StringUtils.EMPTY;
        }
        return Arrays.asList(value.split(","));
    }
    @Override
    public List<String> getResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        if(value == null) {
            value = StringUtils.EMPTY;
        }
        return Arrays.asList(value.split(","));
    }
    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException{
        String value = cs.getString(columnIndex);
        if(value == null) {
            value = StringUtils.EMPTY;
        }
        return Arrays.asList(value.split(","));
    }
}