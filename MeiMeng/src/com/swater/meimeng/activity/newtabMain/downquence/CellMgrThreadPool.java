package com.swater.meimeng.activity.newtabMain.downquence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CellMgrThreadPool implements Runnable {
	
	CellTaskMgr mgr;
	ExecutorService pool;
	int pool_size=5;
	int sleep_cell=1000;
	boolean isStop=false;
	public CellMgrThreadPool(){
		
		mgr=CellTaskMgr.getInstance();
		pool=Executors.newFixedThreadPool(pool_size);
	}
	public void setStop(boolean isstop){
		this.isStop=isstop;
	}

	@Override
	public void run() {
		while (!isStop) {
			CellDownTask task=mgr.getCellTask();
			if (null!=task) {
				pool.execute(task);
			}else{
				
				
				try {
					Thread.sleep(200);
					
				} catch (Exception e) {
					e.printStackTrace();
				
				}
			}
			
		}
		if (isStop) {
			pool.shutdown();
		}
		
		
	}

}
