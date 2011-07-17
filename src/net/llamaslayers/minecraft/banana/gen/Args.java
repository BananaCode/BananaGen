package net.llamaslayers.minecraft.banana.gen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A list of allowed args for a generator and its populators
 * 
 * @author Nightgunner5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Args {
	/**
	 * @return A list of allowed args
	 */
	String[] value();
}
