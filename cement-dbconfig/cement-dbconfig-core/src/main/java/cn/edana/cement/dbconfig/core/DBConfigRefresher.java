package cn.edana.cement.dbconfig.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DBConfigRefresher implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private ConfigService configService;

    private AtomicBoolean ready = new AtomicBoolean(false);

    public DBConfigRefresher(DBConfigManager manager) {
        this.configService = manager.getConfigService();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (this.ready.compareAndSet(false, true)) {
            this.registerDBConfigListener();
        }

    }

    private void registerDBConfigListener() {
        ConfigListener listener = () -> {
            DBConfigRefresher.this.applicationContext.publishEvent(
                    new RefreshEvent(this, null, "Refresh DB config"));
            log.info("DB Config refresh");
        };
        this.configService.addListener(listener);
    }

    @EventListener
    public void handleRefreshEvent(RefreshEvent event) {
        log.info("refresh event");
    }
}
