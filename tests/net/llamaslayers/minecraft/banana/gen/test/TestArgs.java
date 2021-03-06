package net.llamaslayers.minecraft.banana.gen.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.llamaslayers.minecraft.banana.gen.Args;
import net.llamaslayers.minecraft.banana.gen.GenPlugin;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration; 
import org.junit.Test;

/**
 * @author Nightgunner5
 */
public class TestArgs {
	/**
	 * Make sure all args used are declared and documented
	 */
	@SuppressWarnings("static-method")
	@Test
	public void test() throws IOException {
		String failarg = null;
		String failgen = null;
		FileConfiguration args =  YamlConfiguration.loadConfiguration(new File("src/args.yml"));
		for (String generator : GenPlugin.generators.keySet()) {
			Set<String> knownArgs = new HashSet<String>();

			Args declaredArgs = GenPlugin.generators.get(generator).getClass().getAnnotation(Args.class);
			if (declaredArgs != null) {
				for (String arg : declaredArgs.value()) {
					if (args.getString(generator + "." + arg + ".description", "TODO").equals("TODO")) {
						System.err.println(generator + "." + arg
								+ ".description = TODO");
						failarg = arg;
						failgen = generator;
					}
					knownArgs.add(arg);
				}
			}

			try {
				String failed = checkSource(new File("src", GenPlugin.generators.get(generator).getClass().getName().replace('.', '/')
						+ ".java"), generator, knownArgs, args);
				if (failed != null) {
					failgen = generator;
					failarg = failed;
				}
				for (BlockPopulator populator : GenPlugin.generators.get(generator).getDefaultPopulators(null)) {
					failed = checkSource(new File("src", populator.getClass().getName().replace('.', '/')
							+ ".java"), generator, knownArgs, args);
					if (failed != null) {
						failgen = generator;
						failarg = failed;
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		args.save(new File("src/args.yml"));

		if (failarg != null && failgen != null) {
			fail("Arg " + failarg + " for generator " + failgen
					+ " is not documented.");
		}
	}

	private static String checkSource(File source, String generator,
		Set<String> knownArgs, FileConfiguration args) throws IOException {
		byte[] buffer = new byte[(int) source.length()];
		FileInputStream in = new FileInputStream(source);
		in.read(buffer);
		in.close();

		String failed = null;

		String contents = new String(buffer);
		Pattern p = Pattern.compile("getArg(String|Int|Double)?\\s*\\(\\s*\\w*\\s*,\\s*\"([^\"]+)\"\\s*(?:,\\s*([^,]+?)\\s*(?:,\\s*([^,]+?)\\s*,\\s*([^,]+?)\\s*)?)?\\)");
		Matcher m = p.matcher(contents);
		while (m.find()) {
			String type = "flag";
			if (m.group(1) == null) {
			} else if (m.group(1).equals("String")) {
				type = "string";
			} else if (m.group(1).equals("Int")) {
				type = "integer";
			} else if (m.group(1).equals("Double")) {
				type = "double";
			}

			String arg = m.group(2);
			String def = m.group(3);
			String min = m.group(4);
			String max = m.group(5);

			if (!knownArgs.contains(arg)) {
				System.err.println(generator + "." + arg + " is undeclared");
				failed = arg;
			}

			args.getString(generator + "." + arg + ".type", type);
			if (args.getString(generator + "." + arg + ".description", "TODO").equals("TODO")) {
				System.err.println(generator + "." + arg
						+ ".description = TODO");
				failed = arg;
			}
			if (!type.equals("flag")) {
				args.getString(generator + "." + arg + ".default", def);
				if (!type.equals("string") && min != null
						&& max != null) {
					args.getString(generator + "." + arg + ".min", min);
					args.getString(generator + "." + arg + ".max", max);
				}
			}
		}

		return failed;
	}
}
