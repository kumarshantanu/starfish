package springer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRowExtractor<T> {

    T extract(ResultSet rs) throws SQLException;

}
