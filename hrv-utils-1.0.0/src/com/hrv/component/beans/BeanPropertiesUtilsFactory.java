package com.hrv.component.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.janino.SimpleCompiler;

import com.hrv.component.utils.sourcecode.VoidReturnType;
import com.hrv.component.utils.sourcecode.builder.ClassBuilder;
import com.hrv.component.utils.sourcecode.builder.CommonMethodBuilder;
import com.hrv.component.utils.sourcecode.wrapper.ClassWrapper;
import com.hrv.component.utils.sourcecode.wrapper.CommonMethodWrapper;

public class BeanPropertiesUtilsFactory {
	private static final Logger logger = Logger.getLogger(BeanPropertiesUtilsFactory.class);
	private static final String packageLocation = BeanPropertiesUtilsFactory.class.getPackage().getName();
	private static final String suffixName = "PropertiesUtils";
	private static Map<Class<?>, BeanPropertiesUtils> mapperCache = new Hashtable<Class<?>, BeanPropertiesUtils>();

	private StringBuilder getCopyPropertiesSourceCode(Class<?> clazz) throws Exception {
		String name = null;
		Method setterMethod = null;

		String clzName = clazz.getSimpleName() + suffixName;
		Class<?> clazzReqursive = clazz;
		String source = "source", desc = "desc";

		ClassWrapper cb = new ClassBuilder();
		cb.setPackage(packageLocation);
		cb.addImport(clazz.getName());
		cb.addModifier(com.hrv.component.utils.sourcecode.Modifier.PUBLIC);
		cb.setName(clzName);
		cb.addInterface(BeanPropertiesUtils.class.getName());

		CommonMethodWrapper mb = new CommonMethodBuilder(cb);
		mb.addModifier(com.hrv.component.utils.sourcecode.Modifier.PUBLIC);
		mb.setReturnType(new VoidReturnType());
		mb.setName("copyProperties");
		mb.addParameter(Object.class.getName(), source);
		mb.addParameter(Object.class.getName(), desc);

		while (clazzReqursive != null && clazzReqursive != Object.class) {
			for (Method getterMethod : clazzReqursive.getDeclaredMethods()) {
				int modifiers = getterMethod.getModifiers();

				if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
					// filter get
					name = getterMethod.getName();

					if (name.startsWith("get")) {
						name = name.replace("get", "");

						try {
							setterMethod = clazzReqursive.getMethod("set" + name, getterMethod.getReturnType());

							mb.getContent().append("\t\t((").append(clazz.getSimpleName()).append(")").append(desc).append(").").append(setterMethod.getName())
									.append("(((").append(clazz.getSimpleName()).append(")").append(source).append(").").append(getterMethod.getName())
									.append("());\n");
						} catch (NoSuchMethodException nsme) {
							logger.error("=================================");
							logger.error("Warning...");
							logger.error("No Such Method : " + "set" + name);
							logger.error("=================================");
						}
					}
				}
			}

			clazzReqursive = clazzReqursive.getSuperclass();
		}

		logger.debug(cb.create());

		return new StringBuilder(cb.create());
	}

	private void prepareCopyProperties(Class<?> clazz1) {
		BeanPropertiesUtils theobj;

		if ((theobj = mapperCache.get(clazz1)) == null) {
			try {
				StringBuilder sb = getCopyPropertiesSourceCode(clazz1);

				SimpleCompiler compiler = new SimpleCompiler();
				compiler.cook(sb.toString());

				Class<?> clazz = Class.forName(packageLocation + "." + clazz1.getSimpleName() + suffixName, true, compiler.getClassLoader());
				theobj = (BeanPropertiesUtils) clazz.newInstance();

				logger.info("Initialized... " + clazz.getName());

				mapperCache.put(clazz1, theobj);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public BeanPropertiesUtils getInstance(Class<?> clazz1) {
		BeanPropertiesUtils theobj;

		if ((theobj = mapperCache.get(clazz1)) == null) {
			prepareCopyProperties(clazz1);

			theobj = mapperCache.get(clazz1);
		}
		return theobj;
	}
}