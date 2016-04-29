package one.yate.dal.spring.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import one.yate.dal.spring.annotation.DataSource;
import one.yate.dal.spring.core.DataSourceKeyDeterminer;
import one.yate.dal.spring.datasource.DataSourceNode;
import one.yate.dal.spring.datasource.DynamicDataSourceHolder;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceAspect {

    protected static final Logger log = LoggerFactory
            .getLogger(DataSourceAspect.class);

    protected DataSourceKeyDeterminer dataSourceKeyDeterminer;

    public void before(JoinPoint point) {
        if (DynamicDataSourceHolder.getManualSwitch()) {
            log.debug("manual switch is on");

            return;
        }
        log.debug("manual switch is off");

        boolean putDataSource = false;
        try {
            MethodSignature methodSignature = ((MethodSignature) point
                    .getSignature());
            Method method = methodSignature.getMethod();
            Object[] parameterValues = point.getArgs();
            Object target = point.getTarget();
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature())
                    .getMethod().getParameterTypes();

            Method m = target.getClass().getMethod(
                    point.getSignature().getName(), parameterTypes);
            if (m != null && m.isAnnotationPresent(DataSource.class)) {
                DataSource data = m.getAnnotation(DataSource.class);

                log.debug(
                        "find annotation datasource with name in method level:{}",
                        data.name());

                DynamicDataSourceHolder.putDataSource(data.name());
                putDataSource = true;
                return;
            }

            Annotation[][] parameterAnnotations = point.getTarget().getClass()
                    .getMethod(method.getName(), parameterTypes)
                    .getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] pas = parameterAnnotations[i];
                for (int j = 0; j < pas.length; j++) {
                    Annotation annotation = pas[j];
                    if (annotation instanceof DataSource) {
                        DataSource data = (DataSource) annotation;
                        if (data.name() != null
                                && data.name().trim().length() > 0) {

                            log.debug("find annotation datasource with name in parameter level:{}"
                                    + data.name());

                            DynamicDataSourceHolder.putDataSource(data.name());
                            putDataSource = true;
                            return;
                        }
                        if (data.field() == null
                                || data.field().trim().length() == 0) {
                            return;
                        } else {

                            log.debug("find annotation datasource with field:"
                                    + data.field() + ",value:"
                                    + parameterValues[i] + ",readonly:"
                                    + data.readonly());

                            String datasourceKey = dataSourceKeyDeterminer
                                    .determine(data.field(), new Long(
                                            parameterValues[i].toString()),
                                            data.readonly());
                            DynamicDataSourceHolder
                                    .putDataSource(datasourceKey);
                            putDataSource = true;
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("error in datasource aspect", e);

        } finally {
            if (!putDataSource) {

                log.debug("no annotation datasource found, use default:{}"
                        + dataSourceKeyDeterminer.getDefaultDataSourceKey());

                DynamicDataSourceHolder.putDataSource(dataSourceKeyDeterminer
                        .getDefaultDataSourceKey());
            }
            DynamicDataSourceHolder
                    .putOriginalDataSource(DynamicDataSourceHolder
                            .getDataSouce());
        }
    }

    public void after(JoinPoint point) {
        if (DynamicDataSourceHolder.getManualSwitch()) {
            log.debug("manual switch is on");
            return;
        }
        log.debug("manual switch is off");
        DataSourceNode out = DynamicDataSourceHolder.getOriginalDataSource();
        if (out != null && out.getParent() != null) {
            log.debug("reset datasource to:" + out.getParent().getName());
            DynamicDataSourceHolder.putDataSource(out.getParent().getName());
            DynamicDataSourceHolder.setOriginalDataSource(out.getParent());
        }
    }

    public void setDataSourceKeyDeterminer(
            DataSourceKeyDeterminer dataSourceKeyDeterminer) {
        this.dataSourceKeyDeterminer = dataSourceKeyDeterminer;
    }

}
