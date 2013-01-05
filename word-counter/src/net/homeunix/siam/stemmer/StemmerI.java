package net.homeunix.siam.stemmer;

import java.util.List;

public interface StemmerI {
	public List<String> stem(String token);
}
