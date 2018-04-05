package Impl;

import java.util.concurrent.ConcurrentHashMap;

import dictquery.QueryService;

public class RemoteDictQueryServiceImpl implements QueryService {
	private static final ConcurrentHashMap<String, String> dict = new ConcurrentHashMap<String, String>();

	static {
		dict.put("sky", "tiankong");
		dict.put("computer", "diannao");
	}

	@Override
	public String queryWord(String word) {
		System.out.println("RemoteDictQueryServiceImpl.queryWord called");
		String result = dict.get(word);

		if (null == result) {
			result = "N/A";
		}
		return result;
	}

}
