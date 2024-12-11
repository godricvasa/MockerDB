package com.util;

public class CacheUtil
{
	public static LRUCache cache = new LRUCache(100);
	
	public static void setCache(String key, Object value) 
	{
		cache.put(key, value);
	}
	
	public static Object getCache(String key)
	{
		return cache.get(key);
	}
	
	public static void remove(String key)
	{
		cache.remove(key);
	}
}
