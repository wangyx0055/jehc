package jehc.xtmodules.xtcore.util.quartz.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jehc.solrmodules.solrdao.SolrCoreDao;
import jehc.solrmodules.solrdao.SolrUrlDao;
import jehc.solrmodules.solrmodel.SolrCore;
import jehc.solrmodules.solrmodel.SolrUrl;
import jehc.xtmodules.xtcore.solr.utils.SolrUtil;
import jehc.xtmodules.xtcore.util.springutil.SpringUtil;

/**
 * 全量索引
 * @author 邓纯杰
 *
 */
public class Solr_fullimportTask extends Thread{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 业务逻辑处理
	 */
	public void service() {
		new Solr_dataimportTask().start();
	}
	
	public void run(){
		try {
			excute();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void excute(){
		logger.info("----------开始进行增量索引--------------");
		SolrCoreDao solrCoreDao = (SolrCoreDao)SpringUtil.getBean("solrCoreDao");
		SolrUrlDao solrUrlDao = (SolrUrlDao)SpringUtil.getBean("solrUrlDao");
		Map<String, Object> condition = new HashMap<String, Object>();
		List<SolrCore> solr_CoreList = solrCoreDao.getSolrCoreListByCondition(condition);
		for(int i = 0; i < solr_CoreList.size(); i++){
			SolrUrl solr_url = solrUrlDao.getSolrUrlById(solr_CoreList.get(i).getSolr_url_id());
			SolrUtil.fullimport(solr_url.getSolr_url_url(), solr_CoreList.get(i).getSolr_core_name());
		}
	}
}
