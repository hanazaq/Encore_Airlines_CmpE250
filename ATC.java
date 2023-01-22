
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

class Sortbycodey implements Comparator<Flight> {

	// Method
	// Sorting in ascending order of code
	public int compare(Flight a, Flight b) {

		return a.code.compareTo(b.code);
	}
}

public class ATC {
	// f 7 characters, the first 4 ACC, the following 3 characters
	// are numbers from 0 to 999, filled with zeros from the front. //related to the
	// hashtable in ACC
	private String code;

	ATC(String code) {
		this.code = code;
	}

	boolean atc_running_full = false;
	Flight flg_running;
	ArrayList<Flight> atc_waiting = new ArrayList<Flight>();
	ArrayList<Flight> compare_atc = new ArrayList<Flight>(); //collecting here before entering the ready queue
	Queue<Flight> readyqueue = new LinkedList<Flight>();

	public String get_ATC_code() {
		return code;
	}

	public void set_ATC_code(String code) {
		this.code = code;
	}

	public void add_to_readyQueue() {
		Collections.sort(compare_atc, new Sortbycodey());
		while (!compare_atc.isEmpty()) {
			this.readyqueue.add(compare_atc.remove(0));
		}
	}
}
