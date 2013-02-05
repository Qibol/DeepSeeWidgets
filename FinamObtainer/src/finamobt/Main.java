package finamobt;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.intersys.objects.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar from = Calendar.getInstance();
		/*from.set(2012, 2, 1);
		Calendar to = Calendar.getInstance();
		to.set(2012, 9, 15);*/
		HashMap<String, String> tickers = DataObtainer.getAvailableTickers();
		String filename = "/home/dev/finam_import/finam_import_"
			+ from.get(Calendar.YEAR) + "_" 
			+ (from.get(Calendar.MONTH) + 1) + "_"
			+ from.get(Calendar.DAY_OF_MONTH) ;
		DataObtainer dobt = new DataObtainer(from, from, "tfile", filename);
		Iterator<Map.Entry<String,String>> it = tickers.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, String> ticker = (Map.Entry<String, String>) it.next();
			dobt.get(ticker);
		}
		Database db;
		try {
			db = CacheDatabase.getDatabase("jdbc:Cache://localhost:1972/FINANCE", "dev", "<here should be your password>");
			Finam.Data.ParseFile(db, filename);
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
