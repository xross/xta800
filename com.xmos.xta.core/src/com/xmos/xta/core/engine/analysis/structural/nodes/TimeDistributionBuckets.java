package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.List;

public class TimeDistributionBuckets {
	public static final int BUCKET_COUNT = 80;
	
	private double m_buckets [];
	private double m_lowest;
	private double m_highest;
	private boolean m_isInfinite;
	
	public TimeDistributionBuckets(){
		m_buckets = new double [BUCKET_COUNT];
		m_isInfinite = false;
	}
	public TimeDistributionBuckets(TimeDistributionBuckets b){
		m_buckets = new double [BUCKET_COUNT];
		
		for(int i =0;i< BUCKET_COUNT; i++) {
			m_buckets[i] = b.m_buckets[i];
		}
		m_isInfinite = b.m_isInfinite;
		m_lowest = b.m_lowest;
		m_highest = b.m_highest;
	}
	
	public TimeDistributionBuckets(long time){
		m_buckets = new double [BUCKET_COUNT];
		m_lowest = time;
		m_highest = time;
		m_buckets[timeToBucket(time)] = 1;
		m_isInfinite = false;
	}
	
	private void normalise(){
		double total = 0;

		for(int i=0;i<BUCKET_COUNT;i++){
			total += m_buckets[i];
		}
		for(int i=0;i<BUCKET_COUNT;i++){
			m_buckets[i] = m_buckets[i]/total;
		}
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
			m_buckets = time.m_buckets;
			normalise();
		}
		
	}
	
	
	public void add (TimeDistributionBuckets b0, TimeDistributionBuckets b1){
		if(b0.isInfinite() || b1.isInfinite()) {
			setInfinite();
			m_buckets = new double [BUCKET_COUNT];
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
		normalise();
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
		normalise();
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
	/*
	public void repeat(long count) {
		TimeDistributionBuckets n = new TimeDistributionBuckets(this);
		List<TimeDistributionBuckets> list = new LinkedList<TimeDistributionBuckets>();
		for(int i=0;i<count;i++){
			list.add(n);
		}
		TimeDistributionBuckets p = new TimeDistributionBuckets();
		
		p.add(list);
		
		m_buckets = p.m_buckets;
		m_highest = p.m_highest;
		m_isInfinite = p.m_isInfinite;
		m_lowest = p.m_lowest;
		normalise();
	}
	*/
	public void repeat(long count) {
		TimeDistributionBuckets n = new TimeDistributionBuckets(this);
		TimeDistributionBuckets res = null;
		
		while(count > 0){
			if((count %2 ) != 0){
				TimeDistributionBuckets s = new TimeDistributionBuckets();
				if(res == null){
					s = n;
				} else {
					s.add(res, n);
				}
				
				res = s;
			}
			TimeDistributionBuckets t = new TimeDistributionBuckets();
			t.add(n, n);
			n = t;
			count = count >> 1;
		}
		
		m_buckets = res.m_buckets;
		m_highest = res.m_highest;
		m_isInfinite = res.m_isInfinite;
		m_lowest = res.m_lowest;
		normalise();
	}

	
	public void print(){
		
		System.out.println(m_lowest + " "+ m_highest);
		double current = m_lowest;
		double inc = (m_highest - m_lowest) / BUCKET_COUNT;
		
		for(double i : m_buckets){
			System.out.println(current + "\t" + i);
			current += inc;
		}

	}
	public double getBucket(int i) {
		return m_buckets[i];
	}
	
	public double getMaxBucket() {
		double max = 0.0;
		for(double i : m_buckets){
			max = Math.max(max, i);
		}
		return max;
	}
	
	public long getLowestBucketCycles(){
		return (long)m_lowest;
	}
	public long getHighestBucketCycles(){
		return (long)m_highest;
	}
	
	
}
