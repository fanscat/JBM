package service.test.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.jbm.util.TimeUtil;

@Service
public class WalkListener implements ApplicationListener<WalkEvent> {

	@Override
	public void onApplicationEvent(WalkEvent event) {
		WalkEvent weak = (WalkEvent) event;
		System.out.println("现在是北京时间：" + TimeUtil.format(weak.times));
	}
}