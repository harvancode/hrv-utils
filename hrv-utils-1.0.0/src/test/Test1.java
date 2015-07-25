package test;

import org.apache.log4j.Logger;

import com.hrv.component.beans.BeanPropertiesUtils;
import com.hrv.component.beans.BeanPropertiesUtilsFactory;

public class Test1 {
	private static final Logger logger = Logger.getLogger(Test1.class);

	public Test1() {
	}

	public void test1() {
		Class1 c1 = new Class1();
		Class1 c2 = new Class1();

		c1.setString1("ini string1");
		c1.setString2("ini string2");
		c1.setInteger1(1);
		c1.setInteger2(2);

		BeanPropertiesUtilsFactory buf = new BeanPropertiesUtilsFactory();
		BeanPropertiesUtils utils = buf.getInstance(Class1.class);

		utils.copyProperties(c1, c2);

		logger.info(c2.getString1());
		logger.info(c2.getString2());
		logger.info(c2.getInteger1());
		logger.info(c2.getInteger2());

		while (true) {
			try {
				Thread.sleep(1000);
				logger.debug(Thread.currentThread().getId());
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	public static void main(String[] args) {
		Test1 app = new Test1();

		app.test1();
	}
}
