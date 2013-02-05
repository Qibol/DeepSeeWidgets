package finamobt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//Carry on. If using old date to, it will write trash string at the end

public class DataObtainer {

	private String encoding = "UTF-8";
	private String filename;
	private FileWriter outStream;
	private BufferedWriter outBuffer;
	private Calendar from;
	private Calendar to;
	private int period;
	private String ext;
	private int dateFormat; 
	private int timeFormat;
	private int candleTime; 
	private int delimiter;
	private int encloser; 
	private int entryFormat;
	private boolean addHeader; 
	private boolean fillEmpty;

	public DataObtainer(Calendar fromIn, Calendar toIn, String filenameIn, String outFileIn) {
		from = fromIn;
		to = toIn;
		filename = filenameIn;		
		period = 8;
		ext = ".txt";
		dateFormat = 4; 
		timeFormat = 3;
		candleTime = 0; 
		delimiter = 1;
		encloser = 1; 
		entryFormat = 1;
		addHeader = false; 
		fillEmpty = true;
		try {
			outStream = new FileWriter(outFileIn, false);
			outBuffer = new BufferedWriter(outStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataObtainer(Calendar fromIn, Calendar toIn, int periodIn, String filenameIn, String outFileIn, String extIn,
			int dateFormatIn, int timeFormatIn, int candleTimeIn, int delimiterIn,
			int encloserIn, int entryFormatIn, boolean addHeaderIn, boolean fillEmptyIn) {
		from = fromIn;
		to = toIn;
		period = periodIn;
		filename = filenameIn;
		ext =  extIn;
		dateFormat = dateFormatIn;
		timeFormat = timeFormatIn;
		candleTime = candleTimeIn;
		delimiter = delimiterIn;
		encloser = encloserIn;
		entryFormat = entryFormatIn;
		addHeader = addHeaderIn;
		fillEmpty = fillEmptyIn;
	}

	public boolean get(Map.Entry<String, String> ticker) {
		URL url;
		try {
			//Open connection
			// Parameters are unknown, just write them :)
			url = new URL("http://195.128.78.52/export9.out?market=1&mstime=on&mstimever=1&" + getQuery(ticker));

			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			/* if you need POST request, uncomment this
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(getQuery());
			wr.flush();
			 */

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				outBuffer.write(line);
				outBuffer.write("\n");
				outBuffer.flush();
			}
			//wr.close();
			rd.close();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public void end() {
		try {
			outBuffer.flush();
			outBuffer.close();
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getQuery(Map.Entry<String, String> ticker) {
		String data = "";
		try {
			// day from
			data += URLEncoder.encode("df", encoding) + "=" + URLEncoder.encode(String.valueOf(from.get(Calendar.DAY_OF_MONTH)), encoding);
			// month from 
			data += "&" + URLEncoder.encode("mf", encoding) + "=" + URLEncoder.encode(String.valueOf(from.get(Calendar.MONTH)), encoding);
			// year from
			data += "&" + URLEncoder.encode("yf", encoding) + "=" + URLEncoder.encode(String.valueOf(from.get(Calendar.YEAR)), encoding);
			//day to
			data += "&" + URLEncoder.encode("dt", encoding) + "=" + URLEncoder.encode(String.valueOf(to.get(Calendar.DAY_OF_MONTH)), encoding);
			// month to 
			data += "&" + URLEncoder.encode("mt", encoding) + "=" + URLEncoder.encode(String.valueOf(to.get(Calendar.MONTH)), encoding);
			// year to
			data += "&" + URLEncoder.encode("yt", encoding) + "=" + URLEncoder.encode(String.valueOf(to.get(Calendar.YEAR)), encoding);
			/* period:
			"1" - тики
			"2" - 1 мин.
			"3" - 5 мин.
			"4" - 10 мин.
			"5" - 15 мин.
			"6" - 30 мин.
			"7" - 1 час
			"11" - 1 час (с 10:30)
			"8" - 1 день
			"9" - 1 неделя
			"10" - 1 месяц 
			 */
			data += "&" + URLEncoder.encode("p", encoding) + "=" + URLEncoder.encode(String.valueOf(period), encoding);
			// output filename
			data += "&" + URLEncoder.encode("f", encoding) + "=" + URLEncoder.encode(filename, encoding);
			// file extension: ".txt" or ".csv"
			data += "&" + URLEncoder.encode("e", encoding) + "=" + URLEncoder.encode(ext, encoding);
			// ticker
			data += "&" + URLEncoder.encode("em", encoding) + "=" + URLEncoder.encode(ticker.getKey(), encoding);
			data += "&" + URLEncoder.encode("code", encoding) + "=" + URLEncoder.encode(ticker.getValue(), encoding);
			data += "&" + URLEncoder.encode("cn", encoding) + "=" + URLEncoder.encode(ticker.getValue(), encoding);
			/* date format: 
			 * 	"1" - yyyymmdd
				"2" - yymmdd
				"3" - ddmmyy
				"4" - dd/mm/yy
				"5" - mm/dd/yy
			 */
			data += "&" + URLEncoder.encode("dtf", encoding) + "=" + URLEncoder.encode(String.valueOf(dateFormat), encoding);
			/*
			 * time format: 
			 * 	"1" - hhmmss
				"2" - hhmm
				"3" - hh:mm:ss
				"4" - hh:mm
			 */
			data += "&" + URLEncoder.encode("tmf", encoding) + "=" + URLEncoder.encode(String.valueOf(timeFormat), encoding);
			// candle time type: 0 - start, 1 - end
			data += "&" + URLEncoder.encode("MSOR", encoding) + "=" + URLEncoder.encode(String.valueOf(candleTime), encoding);
			/* delimiter: 
				"1" - ","
				"2" - "."
				"3" - ";"
				"4" - "\t"
				"5" - " "
			 */
			data += "&" + URLEncoder.encode("sep", encoding) + "=" + URLEncoder.encode(String.valueOf(delimiter), encoding);
			/* encloser:
			 	"1" - nothing
				"2" - "."
				"3" - ","
				"4" - " "
				"5" - "'"
			 */
			data += "&" + URLEncoder.encode("sep2", encoding) + "=" + URLEncoder.encode(String.valueOf(encloser), encoding);
			/* file entry format:
			 	"1" - TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL
			 	"2" - TICKER, PER, DATE, TIME, OPEN, HIGH, LOW, CLOSE
			 	"3" - TICKER, PER, DATE, TIME, CLOSE, VOL
			 	"4" - TICKER, PER, DATE, TIME, CLOSE
			 	"5" - DATE, TIME, OPEN, HIGH, LOW, CLOSE, VOL
			 */
			data += "&" + URLEncoder.encode("datf", encoding) + "=" + URLEncoder.encode(String.valueOf(entryFormat), encoding);
			// add header
			data += "&" + URLEncoder.encode("at", encoding) + "=" + URLEncoder.encode(addHeader ? "1" : "0", encoding);
			// fill empty periods
			data += "&" + URLEncoder.encode("fsp", encoding) + "=" + URLEncoder.encode(fillEmpty ? "1" : "0", encoding);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.err.print("Unsupported encoding in query constructor");
		}
		return data;
	}

	public static HashMap<String, String> getAvailableTickers() {
		//don't ask me how I did it :)
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("2", "SIBN");
		ret.put("3", "SBER");
		ret.put("4", "SNGS");
		ret.put("6", "MSNG");
		ret.put("7", "RTKM");
		ret.put("8", "LKOH");
		ret.put("9", "IRGZ");
		ret.put("11", "SARE");
		ret.put("13", "SNGSP");
		ret.put("15", "RTKMP");
		ret.put("23", "SBERP");
		ret.put("24", "SAREP");
		ret.put("29", "AFLT");
		ret.put("30", "MFGS");
		ret.put("31", "LSNG");
		ret.put("39", "AVAZ");
		ret.put("40", "AVAZP");
		ret.put("51", "MFGSP");
		ret.put("70", "SAGOP");
		ret.put("445", "SAGO");
		ret.put("509", "GUMM");
		ret.put("510", "KROT");
		ret.put("511", "KROTP");
		ret.put("522", "KUBE");
		ret.put("542", "LSNGP");
		ret.put("556", "ZMZN");
		ret.put("585", "NZGZ");
		ret.put("603", "ZMZNP");
		ret.put("795", "GMKN");
		ret.put("825", "TATN");
		ret.put("826", "TATNP");
		ret.put("1012", "TRNFP");
		ret.put("12983", "MGTSP");
		ret.put("12984", "MGTS");
		ret.put("13855", "APTK");
		ret.put("15518", "KRSG");
		ret.put("15522", "UTAR");
		ret.put("15523", "MTSS");
		ret.put("15544", "KMAZ");
		ret.put("15545", "WBDF");
		ret.put("15547", "IRKT");
		ret.put("15722", "JNOS");
		ret.put("15723", "JNOSP");
		ret.put("15724", "TZUM");
		ret.put("15736", "YRSL");
		ret.put("15843", "OMZZ");
		ret.put("15844", "OMZZP");
		ret.put("15914", "MMBM");
		ret.put("15965", "VSMO");
		ret.put("16049", "SCON");
		ret.put("16080", "SVAV");
		ret.put("16136", "CHMF");
		ret.put("16140", "DLVB");
		ret.put("16141", "DLVBP");
		ret.put("16173", "FLKO");
		ret.put("16265", "TASB");
		ret.put("16266", "TASBP");
		ret.put("16276", "LPSB");
		ret.put("16284", "KTSB");
		ret.put("16285", "KTSBP");
		ret.put("16329", "KLSB");
		ret.put("16330", "MISB");
		ret.put("16331", "MISBP");
		ret.put("16342", "YRSB");
		ret.put("16343", "YRSBP");
		ret.put("16352", "VDSB");
		ret.put("16359", "MRSB");
		ret.put("16369", "AKHA");
		ret.put("16440", "OGKE");
		ret.put("16452", "ASSB");
		ret.put("16455", "RZSB");
		ret.put("16456", "VGSB");
		ret.put("16457", "VGSBP");
		ret.put("16517", "UDSB");
		ret.put("16518", "UDSBP");
		ret.put("16546", "VRSB");
		ret.put("16547", "VRSBP");
		ret.put("16610", "GRAZ");
		ret.put("16615", "NNSB");
		ret.put("16616", "NNSBP");
		ret.put("16694", "KISB");
		ret.put("16695", "KISBP");
		ret.put("16712", "CLSB");
		ret.put("16713", "CLSBP");
		ret.put("16782", "MAGN");
		ret.put("16783", "RTSB");
		ret.put("16784", "RTSBP");
		ret.put("16797", "TORS");
		ret.put("16798", "TORSP");
		ret.put("16804", "MSSV");
		ret.put("16805", "UAZA");
		ret.put("16825", "DASB");
		ret.put("16842", "GAZP");
		ret.put("16866", "ROSB");
		ret.put("16908", "PMSB");
		ret.put("16909", "PMSBP");
		ret.put("16917", "MSRS");
		ret.put("16921", "TGMK");
		ret.put("16933", "MSSB");
		ret.put("17046", "NLMK");
		ret.put("17067", "VZRZP");
		ret.put("17068", "VZRZ");
		ret.put("17086", "MGNT");
		ret.put("17123", "PLZL");
		ret.put("17137", "ISKJ");
		ret.put("17204", "OGKC");
		ret.put("17257", "VLHZ");
		ret.put("17273", "ROSN");
		ret.put("17282", "TGKE");
		ret.put("17359", "KZMS");
		ret.put("17370", "NVTK");
		ret.put("17375", "PKBA");
		ret.put("17474", "VOSB");
		ret.put("17502", "TGKI");
		ret.put("17564", "AKRN");
		ret.put("17597", "TGKB");
		ret.put("17618", "TGKF");
		ret.put("17698", "HALS");
		ret.put("17713", "RASP");
		ret.put("17850", "PRIM");
		ret.put("17919", "DGBZ");
		ret.put("17920", "DGBZP");
		ret.put("17921", "SVSB");
		ret.put("17922", "SVSBP");
		ret.put("17942", "VRPH");
		ret.put("18176", "TGKN");
		ret.put("18189", "TGKBP");
		ret.put("18310", "TGKD");
		ret.put("18371", "TTLK");
		ret.put("18382", "TGKA");
		ret.put("18391", "TGKDP");
		ret.put("18425", "TGKJ");
		ret.put("18441", "TRMK");
		ret.put("18564", "DIXY");
		ret.put("18584", "EONR");
		ret.put("18654", "PIKK");
		ret.put("18684", "OGKB");
		ret.put("19012", "TAVR");
		ret.put("19043", "VTBR");
		ret.put("19095", "WTCM");
		ret.put("19096", "WTCMP");
		ret.put("19623", "URKA");
		ret.put("19629", "NMTP");
		ret.put("19632", "VTGK");
		ret.put("19651", "SYNG");
		ret.put("19676", "ARMD");
		ret.put("19715", "AFKS");
		ret.put("19717", "PHST");
		ret.put("19724", "DVEC");
		ret.put("19736", "LSRG");
		ret.put("19737", "MVID");
		ret.put("19738", "OGKA");
		ret.put("19814", "TGKK");
		ret.put("19897", "TCBN");
		ret.put("19915", "ARSA");
		ret.put("19916", "KBSB");
		ret.put("19960", "CHZN");
		ret.put("19968", "ETGK");
		ret.put("20030", "KCHE");
		ret.put("20066", "BSPB");
		ret.put("20087", "STSB");
		ret.put("20088", "STSBP");
		ret.put("20100", "NKNC");
		ret.put("20101", "NKNCP");
		ret.put("20107", "MRKP");
		ret.put("20125", "GCHE");
		ret.put("20204", "KZBE");
		ret.put("20235", "MRKC");
		ret.put("20266", "HYDR");
		ret.put("20286", "MRKV");
		ret.put("20309", "MRKZ");
		ret.put("20321", "RKKE");
		ret.put("20346", "MRKS");
		ret.put("20402", "MRKU");
		ret.put("20412", "MRKK");
		ret.put("20498", "KCHEP");
		ret.put("20509", "FEES");
		ret.put("20516", "IRAO");
		ret.put("20637", "ROST");
		ret.put("20681", "MRKY");
		ret.put("20702", "AMEZ");
		ret.put("20703", "AZKM");
		ret.put("20706", "BEGY");
		ret.put("20708", "FESH");
		ret.put("20709", "KHEL");
		ret.put("20710", "KOGK");
		ret.put("20711", "OPIN");
		ret.put("20712", "RUSP");
		ret.put("20715", "TAMZ");
		ret.put("20716", "TUZA");
		ret.put("20717", "UKUZ");
		ret.put("20718", "UUAZ");
		ret.put("20719", "VSMZ");
		ret.put("20737", "ODVA");
		ret.put("20890", "KOSB");
		ret.put("20892", "MGNZ");
		ret.put("20893", "NVNG");
		ret.put("20894", "PBSB");
		ret.put("20895", "PMOT");
		ret.put("20897", "RSAM");
		ret.put("20898", "SNTZ");
		ret.put("20899", "SVTZ");
		ret.put("20912", "KRSB");
		ret.put("20913", "KRSBP");
		ret.put("20947", "MERF");
		ret.put("20958", "VRAO");
		ret.put("20959", "VRAOP");
		ret.put("20971", "MRKH");
		ret.put("20972", "MRKHP");
		ret.put("20999", "CHEP");
		ret.put("21000", "CHKZ");
		ret.put("21001", "CHMK");
		ret.put("21002", "CNTL");
		ret.put("21004", "LNZL");
		ret.put("21005", "MNPZ");
		ret.put("21006", "OSMP");
		ret.put("21007", "SUMZ");
		ret.put("21018", "MTLR");
		ret.put("21078", "BLNG");
		ret.put("21105", "YKST");
		ret.put("21116", "MOTZ");
		ret.put("21166", "SELL");
		ret.put("21167", "NEKK");
		ret.put("22094", "LNZLP");
		ret.put("22401", "SZPR");
		ret.put("22454", "ERMK");
		ret.put("22525", "KMEZ");
		ret.put("22555", "OSFD");
		ret.put("22602", "BACT");
		ret.put("22603", "FSRV");
		ret.put("22652", "IRSG");
		ret.put("22736", "OPTI");
		ret.put("22788", "SKRN");
		ret.put("22797", "BSPBP");
		ret.put("22806", "PRIN");
		ret.put("22843", "UNAC");
		ret.put("22891", "OMSH");
		ret.put("35220", "SNOS");
		ret.put("35238", "RSEA");
		ret.put("35242", "BISV");
		ret.put("35243", "BISVP");
		ret.put("35247", "PRTK");
		ret.put("35248", "REBR");
		ret.put("35285", "KBTK");
		ret.put("35332", "ZOYA");
		ret.put("35334", "DIKO");
		ret.put("35363", "DIOD");
		ret.put("66644", "RNAV");
		ret.put("66692", "KUSTP");
		ret.put("66693", "RODNP");
		ret.put("66694", "SXPNP");
		ret.put("66848", "ERAV");
		ret.put("66893", "RUGR");
		ret.put("74344", "TATB");
		ret.put("74446", "RVST");
		ret.put("74461", "DNKOP");
		ret.put("74540", "SEMZ");
		ret.put("74549", "MSTT");
		ret.put("74561", "TRCN");
		ret.put("74562", "MAGE");
		ret.put("74563", "MAGEP");
		ret.put("74584", "LIFE");
		ret.put("74628", "TNBP");
		ret.put("74629", "TNBPP");
		ret.put("74718", "RUALR");
		ret.put("74726", "AGRE");
		ret.put("74728", "TSKA");
		ret.put("74744", "DZRD");
		ret.put("74745", "DZRDP");
		ret.put("74746", "TUCH");
		ret.put("74779", "RBCM");
		ret.put("75094", "KSGR");
		ret.put("75124", "URFD");
		ret.put("80307", "USYN");
		ret.put("80313", "KDSK");
		ret.put("80316", "YASH");
		ret.put("80390", "MNFD");
		ret.put("80593", "TAER");
		ret.put("80621", "MFBA");
		ret.put("80728", "NMOS");
		ret.put("80745", "MTLRP");
		ret.put("80818", "PRMB");
		ret.put("80915", "ZIRE");
		ret.put("81040", "UTII");
		ret.put("81114", "PHOR");
		ret.put("81241", "PLSM");
		ret.put("81287", "NFAZ");
		ret.put("81297", "TPLF");
		ret.put("81360", "SELG");
		ret.put("81398", "GAZC");
		ret.put("81399", "GAZS");
		ret.put("81405", "SITR");
		ret.put("81565", "BEGYP");
		ret.put("81575", "CNTLP");
		ret.put("81695", "LINK");
		ret.put("81757", "BANE");
		ret.put("81758", "BANEP");
		ret.put("81759", "ELSI");
		ret.put("81760", "EMTI");
		ret.put("81766", "YKEN");
		ret.put("81769", "YKENP");
		ret.put("81786", "RUSI");
		ret.put("81820", "ALRS");
		ret.put("81829", "MGVM");
		ret.put("81856", "KZOS");
		ret.put("81857", "KZOSP");
		ret.put("81858", "NPOF");
		ret.put("81882", "ALNU");
		ret.put("81885", "IGST");
		ret.put("81886", "IGST03");
		ret.put("81887", "IGSTP");
		ret.put("81888", "KOSBP");
		ret.put("81889", "KRGE");
		ret.put("81890", "KRGEP");
		ret.put("81891", "KRKN");
		ret.put("81892", "KRKNP");
		ret.put("81896", "PAZA");
		ret.put("81897", "PBSBP");
		ret.put("81898", "PDMS");
		ret.put("81899", "TGKO");
		ret.put("81901", "BRZL");
		ret.put("81903", "KMTZ");
		ret.put("81904", "KOMM");
		ret.put("81905", "KRKO");
		ret.put("81906", "KRKOP");
		ret.put("81907", "MASZ");
		ret.put("81908", "PETR");
		ret.put("81909", "PETR02");
		ret.put("81910", "PETR03");
		ret.put("81911", "PETR04");
		ret.put("81912", "PETR05");
		ret.put("81913", "PETR06");
		ret.put("81914", "TANL");
		ret.put("81915", "TANLP");
		ret.put("81916", "TRAV");
		ret.put("81917", "YAKG");
		ret.put("81918", "ZILL");
		ret.put("81919", "ZIOP");
		ret.put("81929", "NSVZ");
		ret.put("81930", "UFMO");
		ret.put("81933", "CHGZ");
		ret.put("81934", "ELTZ");
		ret.put("81935", "ERCO");
		ret.put("81936", "ERCO02");
		ret.put("81937", "ERCO03");
		ret.put("81938", "ERCO04");
		ret.put("81939", "HIMC");
		ret.put("81940", "HIMCP");
		ret.put("81941", "KAZT");
		ret.put("81942", "KAZTP");
		ret.put("81943", "KUNF");
		ret.put("81944", "MORI");
		ret.put("81945", "MUGS");
		ret.put("81946", "MUGSP");
		ret.put("81947", "NKSH");
		ret.put("81948", "NVNGP");
		ret.put("81953", "USBN");
		ret.put("81954", "VJGZ");
		ret.put("81955", "VJGZP");
		ret.put("81992", "NAUK");
		ret.put("81996", "CITB");
		ret.put("81997", "GAZA");
		ret.put("81998", "GAZAP");
		ret.put("82000", "TGVK");
		ret.put("82001", "ZVEZ");
		ret.put("82014", "VTLD");
		ret.put("82115", "GAZT");
		ret.put("82164", "FORTP");
		ret.put("82165", "TRUDP");
		ret.put("82312", "PRMI");
		ret.put("82424", "PLNT");
		ret.put("82425", "PTRM");
		ret.put("82460", "ABRD");
		ret.put("82476", "EVCO");
		ret.put("82493", "UNKL");
		ret.put("82610", "SELGP");
		ret.put("82611", "URKZ");
		ret.put("82616", "ALBK");
		ret.put("82798", "IGIP");
		ret.put("82832", "RBCM-001D");
		ret.put("82843", "AVAN");
		ret.put("82844", "PAVT");
		ret.put("82846", "SELG-003D");
		ret.put("82886", "VTRS");
		ret.put("82890", "MOBB");
		ret.put("83121", "PRFN");
		ret.put("83122", "SKYC");
		ret.put("83165", "KUZB");
		return ret;
	}
}
