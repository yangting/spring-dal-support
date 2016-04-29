package one.yate.dal.spring.base.rules;

import one.yate.dal.spring.core.DataSourceKeyRule;

public class DataSourceKeyModuloRule extends ADataSourceKeyRule implements
        DataSourceKeyRule {

    private Long moduloNumber;
    private Long remainder;

    public DataSourceKeyModuloRule() {
        super();
    }

    public Long getModuloNumber() {
        return moduloNumber;
    }

    public void setModuloNumber(Long moduloNumber) {
        this.moduloNumber = moduloNumber;
    }

    public Long getRemainder() {
        return remainder;
    }

    public void setRemainder(Long remainder) {
        this.remainder = remainder;
    }

    public boolean applyRule(String field, Long value) {
        return applyRule(field, value, false);
    }

    public boolean applyRule(String field, Long value, boolean readonly) {
        if (field == null || value == null) {
            return false;
        }
        if (field.compareTo(getField()) != 0) {
            return false;
        }
        if (!isReadonly() && readonly) {
            return false;
        }
        if (isReadonly() && !readonly) {
            return false;
        }
        if (value.longValue() % moduloNumber.longValue() == remainder
                .longValue()) {
            return true;
        }
        return false;
    }
}
