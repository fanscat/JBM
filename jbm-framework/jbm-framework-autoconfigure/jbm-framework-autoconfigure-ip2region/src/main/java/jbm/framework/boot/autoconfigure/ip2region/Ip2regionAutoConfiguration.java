package jbm.framework.boot.autoconfigure.ip2region;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @program: JBM6
 * @author: wesley.zhang
 * @create: 2020-02-18 00:58
 **/
@ConditionalOnClass(org.lionsoul.ip2region.Util.class)
public class Ip2regionAutoConfiguration {

    /**
     * @return
     */
    @Bean
    public IpRegionTemplate ipRegionTemplate() {
        IpRegionTemplate ipRegionTemplate = new IpRegionTemplate();
        return ipRegionTemplate;
    }

}
