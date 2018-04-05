package impl;

import java.util.concurrent.ConcurrentHashMap;

import dictquery.QueryService;

public class LocalDictQueryServiceImpl implements QueryService {
	private static final ConcurrentHashMap<String, String> dict = new ConcurrentHashMap<String, String>();

	static {
		dict.put("test", "ceshi");
		dict.put("china", "zhongguo");
	}

	@Override
	public String queryWord(String word) {
		System.out.println("LocalDictQueryServiceImpl.queryWord called");
		String result = dict.get(word);

		if (null == result) {
			result = "N/A";
		}
		return result;
	}

}
