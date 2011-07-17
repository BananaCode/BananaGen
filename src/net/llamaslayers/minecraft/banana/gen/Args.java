package net.llamaslayers.minecraft.banana.gen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Nightgunner5
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Args {
	/**
	 * @return A list of allowed args
	 */
	String[] value();
}
