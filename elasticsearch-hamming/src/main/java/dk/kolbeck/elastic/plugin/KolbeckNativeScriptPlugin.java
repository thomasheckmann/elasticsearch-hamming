package dk.kolbeck.elastic.plugin;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptModule;

import dk.kolbeck.elastic.plugin.hamming.HammingDistanceScriptFactory;
/**
 * http://stackoverflow.com/questions/32785803/similar-image-search-by-phash-distance-in-elasticsearch
 * 
 * https://github.com/imotov/elasticsearch-native-script-example/tree/2.1
 * 
 * 
 * @author dkthahko
 *
 */
public class KolbeckNativeScriptPlugin extends Plugin {

	@Override
	public String description() {
		return "Native script (c) by kolbeck.dk";
	}

	@Override
	public String name() {
		return "kolbeck-native-script";
	}

	public void onModule(ScriptModule module) {
		// Register each script that we defined in this plugin
		module.registerScript("hamming", HammingDistanceScriptFactory.class);
	}
}
