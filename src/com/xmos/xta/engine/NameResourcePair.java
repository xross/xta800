package com.xmos.xta.engine;

import java.util.HashSet;

import com.xmos.xta.engine.LivenessResource.ResourceNames;

public class NameResourcePair{
	private HashSet<String> m_names;
	private HashSet<ResourceNames> m_resources;
	
	public NameResourcePair(HashSet<String> names, HashSet<ResourceNames> resources){
		m_names = names;
		m_resources = resources;
	}
	public HashSet<String> getNames(){
		return m_names;
	}
	public HashSet<ResourceNames> getResources(){
		return m_resources;
	}
}
