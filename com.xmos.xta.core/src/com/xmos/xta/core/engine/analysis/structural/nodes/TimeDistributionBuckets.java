package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.List;

public class TimeDistributionBuckets {
	private static final int BUCKET_COUNT = 200;
	
	private long m_buckets [];
	private double m_lowest;
	private double m_highest;
	private boolean m_isInfinite;
	
	public TimeDistributionBuckets(){
		m_buckets = new long [BUCKET_COUNT];
		m_isInfinite = false;
	}
	
	public TimeDistributionBuckets(int time){
		m_buckets = new long [BUCKET_COUNT];
		m_lowest = time;
		m_highest = time;
		m_buckets[timeToBucket(time)] = 1;
		m_isInfinite = false;
	}
	
	/**
	 * this is used for adding two buckets together that are in series.
	 * i.e. a sequence
	 */
	public void add (List<TimeDistributionBuckets> list){
		if(list.size() == 2){
			add(list.get(0), list.get(1));
		} else {
			TimeDistributionBuckets time = list.get(0);
			int i = 1;
			while(i < list.size()){
				TimeDistributionBuckets t = new TimeDistributionBuckets();
				t.add(time, list.get(i));
				i++;
				time = t;
			}
			m_buckets = time.m_buckets;
			m_lowest = time.m_lowest;
			m_highest = time.m_highest;
			m_isInfinite = time.m_isInfinite;
		}
	}
	
	
	public void add (TimeDistributionBuckets b0, TimeDistributionBuckets b1){
		if(b0.isInfinite() || b1.isInfinite()) {
			setInfinite();
			return;
		}
		m_lowest = b0.m_lowest + b1.m_lowest;
		m_highest = b0.m_highest + b1.m_highest;
		for(int i =0;i< BUCKET_COUNT; i++) {
			for(int j =0 ;j< BUCKET_COUNT;j++) {
				double t = b0.bucketToTime(i) + b1.bucketToTime(j);
				
				int b = timeToBucket(t);
				
				if(b <0 || b >=BUCKET_COUNT){
					timeToBucket(t);
				}
				
				m_buckets[b] += b0.m_buckets[i] * b1.m_buckets[j];
			}
		}
	}
	
	/**
	 *  this is for adding two buckets together that are in parallel.
	 *  i.e. a conditional
	 */
	public void merge (Collection<TimeDistributionBuckets> tdbs){
		//always check for infinity first
		for(TimeDistributionBuckets t : tdbs){
			if(t.isInfinite()){
				setInfinite();
				return;
			}
		}
		m_lowest = Double.MAX_VALUE;
		m_highest = 0;
		for(TimeDistributionBuckets t : tdbs){
			m_lowest = Math.min(t.m_lowest, m_lowest);
		}
		for(TimeDistributionBuckets t : tdbs){
			m_highest = Math.max(t.m_highest, m_highest);
		}
		for(int i =0;i< BUCKET_COUNT; i++) {
			for(TimeDistributionBuckets t : tdbs){
				m_buckets[timeToBucket(t.bucketToTime(i))] += t.m_buckets[i] ;
			}
		}
	}
	
	private double bucketToTime(long i){
		double spread = m_highest - m_lowest;
		double interval = (spread / (double)(BUCKET_COUNT-1));
		return  m_lowest  +(interval*i);
	}
	
	
	private int timeToBucket(double time){
		double spread = (m_highest - m_lowest);
		double dist = (time - m_lowest);
		if(dist == 0){
			return 0;
		}
		double q = (dist/spread)* (double)(BUCKET_COUNT-1);
		return (int)q ;
	}

	public void setInfinite() {
		m_buckets = null;
		m_isInfinite = true;
	}
	public boolean isInfinite(){
		return m_isInfinite;
	}

	public void repeat(long count) {
		m_lowest = m_lowest * count;
		m_highest = m_highest * count;
	}

	public void print(){
		for(long i : m_buckets){
			System.out.println(i);
		}

	}
	
	
}
