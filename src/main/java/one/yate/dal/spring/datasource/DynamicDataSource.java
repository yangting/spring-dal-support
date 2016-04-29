package one.yate.dal.spring.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    protected List<String> dataSourceValus = new ArrayList<String>();

    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSouce();
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        if (targetDataSources != null) {
            for (Entry<Object, Object> entry : targetDataSources.entrySet()) {
                if (entry.getValue() != null
                        && entry.getValue() instanceof String) {
                    dataSourceValus.add((String) entry.getValue());
                }
            }
        }
    }

    public List<String> getDataSourceValues() {
        return dataSourceValus;
    }
}
