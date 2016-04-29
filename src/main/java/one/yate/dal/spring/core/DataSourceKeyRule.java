package one.yate.dal.spring.core;

public interface DataSourceKeyRule {

    public String getDataSourceKey();

    public boolean applyRule(String field, Long value);

    public boolean applyRule(String field, Long value, boolean readonly);

    public boolean isReadonly();
}
