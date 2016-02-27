package dk.kolbeck.elastic.plugin.hamming;

import java.util.Map;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractFloatSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.script.ScriptException;

/**
 * Factory for the script that calculates the Hamming distance between 'hash'
 * and field
 * <p/>
 * This native script demonstrates how to write native custom scores scripts.
 */
public class HammingDistanceScriptFactory implements NativeScriptFactory {

	@Override
	public ExecutableScript newScript(@Nullable Map<String, Object> params) {
		String fieldName = params == null ? null : XContentMapValues.nodeStringValue(params.get("field"), null);
		long hashValue = params == null ? null : XContentMapValues.nodeLongValue(params.get("hash"));

		if (fieldName == null) {
			throw new ScriptException("Missing the field parameter");
		}
		return new HammingDistanceScript(fieldName, hashValue);
	}

	@Override
	public boolean needsScores() {
		return true;
	}

	/**
	 * This script takes a long value from the field specified in the
	 * parameter field. And calculates a score based on Hamming distance to value in field 'hash'
	 */
	private static class HammingDistanceScript extends AbstractFloatSearchScript {
		private final String field;
		private final long hash;

		public HammingDistanceScript(String field, long hash) {
			this.field = field;
			this.hash = hash;
		}

		@Override
		public float runAsFloat() {
			long fieldValue = ((ScriptDocValues.Longs) doc().get(field)).getValue();

			return distance(fieldValue, hash);
		}

		/**
		 * This method returns the distance between two input of the form
		 * '01010101' as a fraction 1. is exact match.
		 * 
		 * @param s1
		 * @param s2
		 * @return
		 */
		private float distance(String s1, String s2) {
			if (s1.length() != s2.length())
				return 0;

			int counter = 0;
			for (int k = 0; k < s1.length(); k++) {
				if (s1.charAt(k) != s2.charAt(k)) {
					counter++;
				}
			}
						
			float result = (float)(s1.length() - counter) / (float)s1.length();
			return (result);
		}
		
		private float distance(long l1, long l2) {
			float result = (float)(Long.SIZE - Long.bitCount(l1 ^ l2)) / (float)Long.SIZE;
			return result;
		}

	}
}
