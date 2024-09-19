package camp.woowak.lab.menu.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UpdateMenuStockDao {
	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public int updateMultipleMenuStocks(Map<String, Long> menuStockMap) {
		String sql = "UPDATE menu SET stock_count = ? WHERE id = ?";

		List<Map.Entry<String, Long>> entries = new ArrayList<>(menuStockMap.entrySet());

		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map.Entry<String, Long> entry = entries.get(i);
				ps.setLong(1, entry.getValue());  // stock
				ps.setString(2, entry.getKey());  // id
			}

			@Override
			public int getBatchSize() {
				return menuStockMap.size();
			}
		}).length;
	}
}
