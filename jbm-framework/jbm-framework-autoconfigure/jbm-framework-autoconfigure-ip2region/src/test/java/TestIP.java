import jbm.framework.boot.autoconfigure.ip2region.IpRegionTemplate;
import org.junit.Test;

/**
 * @program: JBM6
 * @author: wesley.zhang
 * @create: 2020-02-18 05:20
 **/
public class TestIP {

    @Test
    public void testIP() {
        IpRegionTemplate ipRegionTemplate = new IpRegionTemplate();
        ipRegionTemplate.init();
        ipRegionTemplate.getRegion("180.162.26.193");
    }
}
