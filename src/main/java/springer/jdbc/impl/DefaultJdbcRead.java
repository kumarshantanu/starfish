package springer.jdbc.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import springer.jdbc.JdbcException;
import springer.jdbc.IJdbcRead;
import springer.jdbc.IResultSetExtractor;
import springer.jdbc.IRowExtractor;
import springer.jdbc.helper.JdbcUtil;
import springer.util.Util;

public class DefaultJdbcRead implements IJdbcRead {

    public List<Map<String, Object>> queryForList(Connection conn, final String sql, Object[] params) {
        return queryForList(conn, sql, params, -1, true);
    }

    public List<Map<String, Object>> queryForList(Connection conn, final String sql, Object[] params,
            final long limit, final boolean throwLimitExceedException) {
        return queryCustom(conn, sql, params, new IResultSetExtractor<List<Map<String, Object>>>() {
            public List<Map<String, Object>> extract(ResultSet rs) {
                try {
                    return extractMaps(rs, limit, throwLimitExceedException);
                } catch (SQLException e) {
                    throw new JdbcException(String.format("Unable to get result for SQL: [%s]", sql), e);
                }
            }
        });
    }

    public static List<Map<String, Object>> extractMaps(ResultSet rs, long limit, boolean throwLimitExceedException)
            throws SQLException {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(1);
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int colCount = rsmd.getColumnCount();
        final String[] colNames = new String[colCount];
        for (int i = 0; i < colCount; i++) {
            colNames[i] = rsmd.getColumnName(i + 1);
        }
        if (limit >= 0) {
            for (long rowCount = 1; rs.next(); rowCount++) {
                if (rowCount > limit) {
                    if (throwLimitExceedException) {
                        throw new IllegalStateException("Expected max " + limit + " rows but found more");
                    } else {
                        break;
                    }
                }
                final Object[] vals = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    vals[i] = JdbcUtil.getValue(rs, i + 1);
                }
                result.add(Util.zipmap(colNames, vals));
            }
        } else {
            while (rs.next()) {
                final Object[] vals = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    vals[i] = JdbcUtil.getValue(rs, i + 1);
                }
                result.add(Util.zipmap(colNames, vals));
            }
        }
        return result;
    }

    public <T> List<T> queryForList(Connection conn, final String sql, Object[] params, final IRowExtractor<T> extractor) {
        return queryForList(conn, sql, params, extractor, -1, true);
    }

    public <T> List<T> queryForList(Connection conn, final String sql, Object[] params, final IRowExtractor<T> extractor,
            final long limit, final boolean throwLimitExceedException) {
        return queryCustom(conn, sql, params, new IResultSetExtractor<List<T>>() {
            public List<T> extract(ResultSet rs) {
                final List<T> result = new ArrayList<T>(1);
                try {
                    if (limit >= 0) {
                        for (long rowCount = 1; rs.next(); rowCount++) {
                            if (rowCount > limit) {
                                if (throwLimitExceedException) {
                                    throw new IllegalStateException("Expected max " + limit + " rows but found more");
                                } else {
                                    break;
                                }
                            }
                            result.add(extractor.extract(rs));
                        }
                    } else {
                        while (rs.next()) {
                            result.add(extractor.extract(rs));
                        }
                    }
                } catch (SQLException e) {
                    throw new JdbcException(String.format("Unable to execute SQL statement: [%s]", sql), e);
                }
                return result;
            }
        });
    }

    public <K, V> Map<K, V> queryForMap(Connection conn, String sql, Object[] params, IRowExtractor<K> keyExtractor,
            IRowExtractor<V> valueExtractor) {
        return queryForMap(conn, sql, params, keyExtractor, valueExtractor, -1, true);
    }

    public <K, V> Map<K, V> queryForMap(Connection conn, String sql, Object[] params, IRowExtractor<K> keyExtractor,
            IRowExtractor<V> valueExtractor, long limit, boolean throwLimitExceedException) {
        Util.echo("Query SQL: [%s], args: %s\n", sql, Arrays.toString(params));
        final PreparedStatement pstmt = JdbcUtil.prepareStatementWithParams(conn, sql, params);
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            Map<K, V> result = new LinkedHashMap<K, V>();
            if (limit >= 0) {
                for (long rowCount = 1; rs.next(); rowCount++) {
                    if (rowCount > limit) {
                        if (throwLimitExceedException) {
                            throw new IllegalStateException("Expected max " + limit + " rows but found more");
                        } else {
                            break;
                        }
                    }
                    result.put(keyExtractor.extract(rs), valueExtractor.extract(rs));
                }
            } else {
                while (rs.next()) {
                    result.put(keyExtractor.extract(rs), valueExtractor.extract(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new JdbcException(String.format("Unable to execute SQL statement: [%s]", sql), e);
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    public <T> T queryCustom(Connection conn, String sql, Object[] params, IResultSetExtractor<T> extractor) {
        Util.echo("Query SQL: [%s], args: %s\n", sql, Arrays.toString(params));
        final PreparedStatement pstmt = JdbcUtil.prepareStatementWithParams(conn, sql, params);
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            return extractor.extract(rs);
        } catch (SQLException e) {
            throw new JdbcException(String.format("Unable to execute SQL statement: [%s]", sql), e);
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

}
