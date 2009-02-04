package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

	
public class LivenessResource {
	public enum ResourceNames {
		RES_names_PORT,
		RES_names_TIMER,
	    RES_names_CHANEND,
	    RES_names_SYNC,
	    RES_names_THREAD,
	    RES_names_LOCK,
	    RES_names_CLKBLK,
	    RES_names_COPROC
	}
	
	
	private static final HashSet<String> m_names0 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_freer_1r(0, 0).getNmenonic())); 
	
	private static final HashSet<ResourceNames> m_resources0 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_TIMER, ResourceNames.RES_names_CHANEND, ResourceNames.RES_names_SYNC,
					ResourceNames.RES_names_THREAD, ResourceNames.RES_names_LOCK)); 
	
	private static final HashSet<String> m_names1 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_getst_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_msync_1r(0, 0).getNmenonic(),
				new RevbXtaInstruction_mjoin_1r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources1 = new HashSet<ResourceNames>(
			Arrays.asList( ResourceNames.RES_names_SYNC)); 
	
	private static final HashSet<String> m_names2 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_tinitcp_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_tinitdp_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_tinitsp_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_tinitpc_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_tstart_1r(0, 0).getNmenonic(),
				new RevbXtaInstruction_tsetr_3r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources2 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_THREAD)); 
	
	private static final HashSet<String> m_names3 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_setv_1r(0, 0).getNmenonic(),
				new RevbXtaInstruction_setev_1r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources3 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_TIMER, ResourceNames.RES_names_CHANEND)); 
	
	private static final HashSet<String> m_names4 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_setd_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_getd_l2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_setc_ru6(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources4 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_TIMER, ResourceNames.RES_names_CHANEND,
					ResourceNames.RES_names_CLKBLK)); 
	
	private static final HashSet<String> m_names5 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_setclk_l2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_setrdy_l2r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources5 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_CLKBLK));
	
	private static final HashSet<String> m_names6 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_setpt_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_getts_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_inshr_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_outshr_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_peek_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_settw_l2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_endin_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_setpsc_2r(0, 0).getNmenonic(),
				new RevbXtaInstruction_clrpt_1r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources6 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT));
	
	private static final HashSet<String> m_names7 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_eeu_1r(0, 0).getNmenonic(),
					new RevbXtaInstruction_edu_1r(0, 0).getNmenonic(),
					new RevbXtaInstruction_eet_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_eef_2r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources7 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_TIMER, ResourceNames.RES_names_CHANEND));
	
	private static final HashSet<String> m_names8 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_in_2r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources8 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_TIMER, ResourceNames.RES_names_CHANEND,
					ResourceNames.RES_names_LOCK));
	
	private static final HashSet<String> m_names9 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_out_2r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources9 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_PORT, ResourceNames.RES_names_CHANEND, ResourceNames.RES_names_LOCK));
	
	private static final HashSet<String> m_names10 = new HashSet<String>(
			Arrays.asList(new RevbXtaInstruction_int_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_inct_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_outt_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_outct_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_testct_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_testwct_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_chkct_2r(0, 0).getNmenonic(),
					new RevbXtaInstruction_testlcl_l2r(0, 0).getNmenonic()));
	private static final HashSet<ResourceNames> m_resources10 = new HashSet<ResourceNames>(
			Arrays.asList(ResourceNames.RES_names_CHANEND));
	
	private static final ArrayList<NameResourcePair> m_names
		= new ArrayList<NameResourcePair>(Arrays.asList(
				new NameResourcePair(m_names0, m_resources0), 
				new NameResourcePair(m_names1, m_resources1),
				new NameResourcePair(m_names2, m_resources2), 
				new NameResourcePair(m_names3, m_resources3),
				new NameResourcePair(m_names4, m_resources4), 
				new NameResourcePair(m_names5, m_resources5),
				new NameResourcePair(m_names6, m_resources6), 
				new NameResourcePair(m_names7, m_resources7),
				new NameResourcePair(m_names8, m_resources8), 
				new NameResourcePair(m_names9, m_resources9),
				new NameResourcePair(m_names10, m_resources10)
				));
	
	
	private HashSet<ResourceNames> m_possibleResource;
	
	private ResourceNames m_resourcename;
	
	//this holds the number of the register with the resource in it
	// need to be extended to hold memory locations as well
	//FIXME
	private int m_resourceIdentifier;
	
	public LivenessResource(int regNum){
		m_resourceIdentifier = regNum;
		m_possibleResource = new HashSet<ResourceNames>();
	}
	
	public LivenessResource(int regNum, ResourceNames resourcename){
		m_resourceIdentifier = regNum;
		m_resourcename = resourcename;
		m_possibleResource = new HashSet<ResourceNames>();
	}
	
	public void setResourcenames(ResourceNames resourcenames){
		m_resourcename = resourcenames;
	}
	
	public ResourceNames getResourcename(){
		if(m_resourcename != null){
			return m_resourcename;
		} else {
			return null;
		}
		
	}
	
	public void setResourceIdentifier(int id){
		m_resourceIdentifier= id;
	}
	
	public int getResourceIdentifier(){
		return m_resourceIdentifier;
	}
	
	public void applyInstruction(XtaInstruction inst){
		String nmenonic = inst.getNmenonic();
		for(NameResourcePair names : m_names){
			if(names.getNames().contains(nmenonic)){
				if(m_possibleResource.isEmpty()){
					m_possibleResource = names.getResources();
				} else {
					m_possibleResource.retainAll(names.getResources());
				}
				//at this point the m_possibleResource should not be empty
				assert(!m_possibleResource.isEmpty());
				return;
			}
		}
		
	}
	
	
	
	
}
