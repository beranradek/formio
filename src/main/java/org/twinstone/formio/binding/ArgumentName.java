package org.twinstone.formio.binding;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Pojmenovani argumentu u builder konstruktoru
 * (nutne u viceparametroveho konstruktoru pro prirazeni fieldu k argumentum, 
 * u jednoparametroveho konstruktoru lze vynechat)
 */
@Target({PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface ArgumentName {
	/** deklarovane jmeno argumentu (musi byt unikatni a musi odpovidat jednomu z fieldu) */
	String value();
}
