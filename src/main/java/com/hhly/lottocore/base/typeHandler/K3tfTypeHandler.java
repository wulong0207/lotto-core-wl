package com.hhly.lottocore.base.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * @desc    二同复遗漏为豹子遗漏和二同单遗漏中的较小者
 * @author  Tony Wang
 * @date    2017年8月2日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class K3tfTypeHandler extends BaseTypeHandler<Integer> {

	  @Override
	  public void setNonNullParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
		  throw new UnsupportedOperationException("K3tfTypeHandler不支持setNonNullParameter()");
	  }

	  @Override
	  public Integer getNullableResult(ResultSet rs, String columnName) throws SQLException {
		// 二同复遗漏为豹子遗漏和二同单遗漏中的较小者
		if("tf11".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t111") < rs.getInt("t11") ? rs.getInt("t111") : rs.getInt("t11");
			} catch (SQLException e) {
				return null;
			}
		}
		else if("tf22".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t222") < rs.getInt("t22") ? rs.getInt("t222") : rs.getInt("t22");
			} catch (SQLException e) {
				return null;
			}
		}
		else if("tf33".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t333") < rs.getInt("t33") ? rs.getInt("t333") : rs.getInt("t33");
			} catch (SQLException e) {
				return null;
			}
		}
		else if("tf44".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t444") < rs.getInt("t44") ? rs.getInt("t444") : rs.getInt("t44");
			} catch (SQLException e) {
				return null;
			}
		}
		else if("tf55".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t555") < rs.getInt("t55") ? rs.getInt("t555") : rs.getInt("t55");
			} catch (SQLException e) {
				return null;
			}
		}
		else if("tf66".equalsIgnoreCase(columnName)) {
			try {
				return rs.getInt("t666") < rs.getInt("t66") ? rs.getInt("t666") : rs.getInt("t66");
			} catch (SQLException e) {
				return null;
			}
		}
		else {
			throw new UnsupportedOperationException(String.format("K3tfTypeHandler不支持处理列名%s为的数据", columnName));
		}
	  }

	  @Override
	  public Integer getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		  throw new UnsupportedOperationException("K3tfTypeHandler不支持getNullableResult(ResultSet rs, int columnIndex)");
	  }

	  @Override
	  public Integer getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		  throw new UnsupportedOperationException("K3tfTypeHandler不支持getNullableResult(CallableStatement cs, int columnIndex)");
	  }
	}