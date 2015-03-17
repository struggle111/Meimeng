package com.swater.meimeng.activity.newtabMain.downquence;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.swater.meimeng.mutils.NSlog.NSLoger;

import android.content.SyncAdapterType;

public class CellTaskMgr {
	public static final String TAG = "----CellTaskMgr--";

	LinkedList<CellDownTask> ls_task;
	
	public LinkedList<CellDownTask> getLs_task() {
		return ls_task;
	}

	public Set<String> getTask_ids() {
		return task_ids;
	}

	public void setTask_ids(Set<String> task_ids) {
		this.task_ids = task_ids;
	}

	public void setLs_task(LinkedList<CellDownTask> ls_task) {
		this.ls_task = ls_task;
	}
	Set<String> task_ids;
	static CellTaskMgr mgr;

	public CellTaskMgr() {

		ls_task = new LinkedList<CellDownTask>();
		task_ids = new HashSet<String>();
	}

	public static synchronized CellTaskMgr getInstance() {

		if (mgr == null) {
			mgr = new CellTaskMgr();

		}
		return mgr;
	}

	public void addDownTask(CellDownTask TASK) {
		synchronized (TASK) {

			if (!isexist(TASK.getIdName())) {
				ls_task.addLast(TASK);
			}

		}
	}

	public boolean isexist(String idName) {
		synchronized (idName) {
			if (task_ids.contains(idName)) {
				return true;
			} else {
				task_ids.add(idName);
				NSLoger.Log("-- 新增加下载任务---" + idName);
			}

		}
		return false;
	}
	public CellDownTask getCellTask(){
		
		synchronized (ls_task) {
			if (ls_task.size()>0) {
				CellDownTask cell=ls_task.removeFirst();
				if (cell.isFinish==true) {
					NSLoger.Log("-- 移出已完成下载任务---" + cell.getIdName());
					cell=ls_task.removeFirst();
				}
				NSLoger.Log("-- 取出下载任务---" + cell.getIdName());
				return cell;
			}
		}
		return null;
	}
}
