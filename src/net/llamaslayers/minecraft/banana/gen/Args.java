package net.llamaslayers.minecraft.banana.gen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Args {
	String[] value();
}
