package one.yate.dal.spring.base.rules;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import one.yate.dal.spring.core.DataSourceKeyRule;

public abstract class ADataSourceKeyRule implements DataSourceKeyRule {

    protected String field;
    protected boolean readonly = false;
    protected String dataSourceKey;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isReadonly() {
        if (TransactionSynchronizationManager.getCurrentTransactionName() != null
                && !readonly) {
            return true;
        }
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

}
