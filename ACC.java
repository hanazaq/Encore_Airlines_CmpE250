

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

class Sortbycode implements Comparator<Flight> {

	// Method
	// Sorting in ascending order of code
	public int compare(Flight a, Flight b) {

		return a.code.compareTo(b.code);
	}
}

public class ACC {
	ArrayList<Flight> temp_flights = new ArrayList<Flight>();
	int flights_current_size_minHeap; // min heap current size
	Flight[] flights; // min heap

	boolean acc_running_full = false;
	Flight recompile;
	Flight flg_running;
	// should not exceed 0
	int quantum_time = 0;
	ArrayList<Flight> compare_acc = new ArrayList<Flight>(); // compare before entering ready queue
	Queue<Flight> readyqueue = new LinkedList<Flight>();
	ArrayList<Flight> acc_waiting = new ArrayList<Flight>();
	Airport[] table = new Airport[1000];

	private String code;
	// 4 unique capital letter
	// responsible for set of airports know each others in THIS ACC
	// each airport >> 1 atc
	// flight occurs only at THIS ACC

	ACC(String code) {
		this.code = code;
	}

	// process the first of this, time slice 30
	// when adding to queue:
	// 1> new has priority over the sent back
	// 2> two new> flight with the lower flight code, by string comparison, has
	// higher priority.

	public void add_to_readyQueue() {
		Collections.sort(compare_acc, new Sortbycode());
		while (!compare_acc.isEmpty()) {
			this.readyqueue.add(compare_acc.remove(0));
		}
	}

	// creare your own hash table size =1000 using linear probing
	// store airport code
	// initial slot= last three digits of {sum(Ascii(char at i)* (31**i))} // i is
	// the index in airport code String
	public void add_airport(Airport my_airport) {
		String analyze = my_airport.get_airport_code();
		int initial_slot = 0;
		for (int i = 0; i < analyze.length(); i++) {
			char character = analyze.charAt(i);
			int ascii = (int) character;
			initial_slot += (ascii * (Math.pow(31, i)));
		}
		// get last 3 digits
		int index = initial_slot % 1000;
		// linear probing
		for (int i = 0; i < 1000; i++) {
			if (this.table[index] == null) {
				this.table[index] = my_airport;
				break;
			}
			index++;
			if (index >= 1000) {
				index = 0;
			}

		}
		String atc_code = analyze;
		if (index >= 100) {
			atc_code += "" + index;
		} else if (index >= 10) {
			atc_code += "0" + index;
		} else {
			atc_code += "00" + index;
		}
		my_airport.my_atc = new ATC(atc_code);
//		System.out.println(atc_code);
	}

	public String get_ACC_code() {
		return code;
	}

	public void set_ACC_code(String code) {
		this.code = code;
	}

	private void percolateDown(int hole) {

		int child;
		Flight temp = this.flights[hole];
		int l = this.flights_current_size_minHeap;
		for (; hole * 2 <= l; hole = child) {
			child = hole * 2;
			if (child != l && child + 1 < l
					&& this.flights[child + 1].admission_time < this.flights[child].admission_time) {
				child++;
			} else if (child != l && child + 1 < l
					&& this.flights[child + 1].admission_time == this.flights[child].admission_time
					&& this.flights[child + 1].code.compareTo(this.flights[child].code) < 0) {
				child++;
			}

			if (child != l && this.flights[child].admission_time < temp.admission_time) {
				this.flights[hole] = this.flights[child];
			} else if (child != l && this.flights[child].admission_time == temp.admission_time
					&& this.flights[child].code.compareTo(temp.code) < 0) {
				this.flights[hole] = this.flights[child];
			} else {
				break;
			}
		}
		this.flights[hole] = temp;
		

	}

	public void buildHeap() {
		this.flights_current_size_minHeap = this.temp_flights.size() + 1;
		this.flights = new Flight[this.flights_current_size_minHeap];
		// index 0 empty
		for (int i = 1; i < this.flights_current_size_minHeap; i++) {
			this.flights[i] = this.temp_flights.get(i - 1);
		}

		for (int i = this.flights_current_size_minHeap / 2; i > 0; i--) {
			this.percolateDown(i);
		}
	}

	public Flight deleteMin() throws UnderflowException {
		if (this.flights[1] == null) {
			throw new UnderflowException();
		}
		Flight minItem = this.flights[1];
		this.flights[1] = this.flights[--this.flights_current_size_minHeap];
		this.flights[this.flights_current_size_minHeap]=null;
		this.percolateDown(1);
		return minItem;
	}
}
