package com.swater.meimeng.activity.newtabMain.downquence;

public class Celltest {
	public static void main(String[] args) {
		CellMgrThreadPool pool_mgr = new CellMgrThreadPool();

		new Thread(pool_mgr).start();
		String[] urls = new String[] { "www.baid", "ww..wwwwww", "ww.ffff" };
		for (String url : urls) {
			CellTaskMgr cell = CellTaskMgr.getInstance();
			cell.addDownTask(new CellDownTask(url,null));
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
	void DoAction_Down(){
		CellMgrThreadPool pool_mgr = new CellMgrThreadPool();

		new Thread(pool_mgr).start();
		String[] urls = new String[] { "www.baid", "ww..wwwwww", "ww.ffff" };
		for (String url : urls) {
			CellTaskMgr cell = CellTaskMgr.getInstance();
			cell.addDownTask(new CellDownTask(url,null));
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
	}
}
