

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Project3 {

	public static Airport find_airport(String analyze, ACC curr) {

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
			if (curr.table[index].get_airport_code().equals(analyze)) {
				return curr.table[index];

			}
			index++;
			if (index >= 1000) {
				index = 0;
			}

		}

		return null;
	}

	public static int simulation(ACC curr) throws UnderflowException {
		int tick = 0;

		if (tick == 0 && curr.flights_current_size_minHeap > 1) {
			Flight first = curr.deleteMin();
			tick = first.admission_time;
			curr.flg_running = first;
			curr.quantum_time = 0;
			curr.acc_running_full = true;
			while (curr.flights_current_size_minHeap > 1 && curr.flights[1].admission_time == tick) {
				curr.readyqueue.add(curr.deleteMin());
			}
			tick++;
		}
		while (true) {
//			if (tick % 10 == 0) {
//				System.out.println("tick: " + tick + " size of  acc compare " + curr.compare_acc.size()
//						+ " flights in input " + curr.flights_current_size_minHeap);
//				System.out.println("acc waiting flights");
//				for (int i = 0; i < curr.acc_waiting.size(); i++) {
//					System.out.println(curr.acc_waiting.get(i).code + " " + curr.acc_waiting.get(i).current_operation
//							+ " " + curr.acc_waiting.get(i).operation_time[curr.acc_waiting.get(i).current_operation]);
//
//				}
//
//				if (curr.flg_running != null) {
//					System.out.println("acc running this flight: " + curr.flg_running.code + " "
//							+ curr.flg_running.current_operation + " "
//							+ curr.flg_running.operation_time[curr.flg_running.current_operation]);
//				}
//				System.out.println("now acc READY QUEUE" + curr.readyqueue.size() + curr.readyqueue.peek());
//				System.out.println("&&&&&&&&");
//				for (Airport a : curr.table) {
//					if (a != null) {
//						ATC atc = a.my_atc;
//						System.out.println("size of atc compare" + atc.compare_atc.size());
//						System.out.println("atc waiting flights");
//						for (int i = 0; i < atc.atc_waiting.size(); i++) {
//							System.out.println(atc.atc_waiting.get(i).code + " "
//									+ atc.atc_waiting.get(i).current_operation + " "
//									+ atc.atc_waiting.get(i).operation_time[atc.atc_waiting.get(i).current_operation]);
//
//						}
//
//						if (atc.flg_running != null) {
//							System.out.println(atc.get_ATC_code()+"atc running this flight: " + atc.flg_running.code + " "
//									+ atc.flg_running.current_operation + " "
//									+ atc.flg_running.operation_time[atc.flg_running.current_operation]);
//						}
//
//						System.out.println("now atc READY QUEUE" + atc.readyqueue.size() + atc.readyqueue.peek());
//
//					}
//				}
//			}

			// running acc waiting status
			if (curr.acc_waiting.size() > 0) {
				ArrayList<Integer> todelete = new ArrayList<Integer>();
				for (int i = 0; i < curr.acc_waiting.size(); i++) {
					Flight f = curr.acc_waiting.get(i);
					int left_time = --f.operation_time[f.current_operation];
					if (left_time == 0) {
						todelete.add(i);
						int oper = ++f.current_operation;
						curr.compare_acc.add(f);
					}
				}
				for (int i = 0; i < todelete.size(); i++) {
					int x = todelete.get(i);
					curr.acc_waiting.remove(x);
					for (int j = i + 1; j < todelete.size(); j++) {
						todelete.set(j, todelete.get(j) - 1);
					}
				}
			}

			// running atc waiting status
			for (Airport a : curr.table) {
				if (a != null) {
					ATC atc = a.my_atc;
					if (!atc.atc_waiting.isEmpty()) {
						ArrayList<Integer> todelete = new ArrayList<Integer>();
						for (int i = 0; i < atc.atc_waiting.size(); i++) {
							Flight f = atc.atc_waiting.get(i);
							if (f.current_operation < 21) {
								int left_time = --f.operation_time[f.current_operation];
								if (left_time == 0) {
									todelete.add(i);
									int oper = ++f.current_operation;
									atc.compare_atc.add(f);
								}
							}
						}
						for (int i = 0; i < todelete.size(); i++) {
							int x = todelete.get(i);
							atc.atc_waiting.remove(x);
							for (int j = i + 1; j < todelete.size(); j++) {
								todelete.set(j, todelete.get(j) - 1);
							}
						}
					}
				}
			}

			// flights that are admitted to the system at this moment
			while (curr.flights_current_size_minHeap > 1 && curr.flights[1].admission_time == tick) {
				curr.compare_acc.add(curr.deleteMin());
			}
			if (curr.flg_running != null)
				if (curr.flg_running.current_operation == 21) {
					System.out.println("#######");
					curr.flg_running = null;
					curr.quantum_time = 0;
					curr.acc_running_full = false;

				}
			if (curr.acc_running_full && curr.flg_running != null) {

				curr.quantum_time++;
				int left_time = --curr.flg_running.operation_time[curr.flg_running.current_operation];
				if (left_time == 0) {
					Flight f = curr.flg_running;
					int op = ++f.current_operation;
					curr.flg_running = null;
					curr.acc_running_full = false;
					curr.quantum_time = 0;
					if (op == 1 || op == 11) {
						curr.acc_waiting.add(f);
					} else if (op == 3) {
//						f.departure_airport >>go for departure airport compare atc
						Airport a = find_airport(f.departure_airport, curr);
						if (a != null) {
							a.my_atc.compare_atc.add(f);
						}
					}

					else if (op == 13) {
//						f.arrival_airport >>go for arrival airport compare atc
						Airport a = find_airport(f.landing_airport, curr);
						if (a != null) {
							a.my_atc.compare_atc.add(f);
						}
					}

				} else if (left_time != 0 && curr.quantum_time >= 30) {
					curr.recompile = curr.flg_running;
					curr.flg_running = null;
					curr.acc_running_full = false;
					curr.quantum_time = 0;
				} else if (left_time != 0 && curr.quantum_time < 30) {
					// nothing
				}

			}

			// atc running status
			for (Airport a : curr.table) {
				if (a != null) {
					ATC atc = a.my_atc;
					atc.add_to_readyQueue();
					if (!atc.atc_running_full) {
						if (!atc.readyqueue.isEmpty()) {
							atc.flg_running = atc.readyqueue.poll();
							atc.atc_running_full = true;
						}
					} else if (atc.flg_running.current_operation < 21) {
						int left_time = --atc.flg_running.operation_time[atc.flg_running.current_operation];
						if (left_time == 0) {
							Flight f = atc.flg_running;
							int op = ++f.current_operation;
							if (op == 10 || op == 20) {
								curr.compare_acc.add(f);
							} else if (op == 4 || op == 6 || op == 8 || op == 14 || op == 16 || op == 18) {
								atc.atc_waiting.add(f);
							}
							atc.flg_running = null;
							atc.atc_running_full = false;
							if (!atc.readyqueue.isEmpty()) {
								atc.flg_running = atc.readyqueue.poll();
								atc.atc_running_full = true;
							}

						} else {
							// nothing
						}

					}
				}
			}

			curr.add_to_readyQueue();
			if (curr.recompile != null) {
				curr.readyqueue.add(curr.recompile);
				curr.recompile = null;
			}
			if (!curr.acc_running_full) {
				if (!curr.readyqueue.isEmpty()) {
					curr.flg_running = curr.readyqueue.poll();
					curr.acc_running_full = true;
					curr.quantum_time = 0;
				}

			}

//			System.out.println("---------------");
			// simulation finishes if there is no event left to simulate
			int stop = 0;
			if (curr.readyqueue.isEmpty() && curr.flights_current_size_minHeap <= 1 && curr.acc_waiting.isEmpty()
					&& curr.acc_running_full == false) {
				stop = 1;
				for (Airport a : curr.table) {
					if (a != null) {
						ATC a_atc = a.my_atc;
						if (a_atc.readyqueue.isEmpty() && a_atc.atc_waiting.isEmpty()
								&& a_atc.atc_running_full == false) {
							continue;
						} else {
							stop = 0;
							break;
						}
					}
				}
			}
			if (stop == 1)
				break;
			///////////////////////////
			tick++;
		}

		return tick;

	}

	public static void main(String[] args) throws UnderflowException {
		// read input
		int a = 0, f = 0;
		// storing ACC in hashmap includes {table of airports{includes one ATC}, min
		// heap for flights, }
//		ACC[] accs = new ACC[a];
		HashMap<String, ACC> hash_map_accs = new HashMap<String, ACC>();

		File file = new File(args[0]);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (sc.hasNextLine()) {
			String[] nums = sc.nextLine().split(" ");
			a = Integer.parseInt(nums[0]);
			f = Integer.parseInt(nums[1]);
		}
		for (int x = 0; x < a; x++) {
			if (sc.hasNextLine()) {
				String[] codes = sc.nextLine().split(" ");
				ACC acc = new ACC(codes[0]);
				for (int h = 1; h < codes.length; h++) {
					Airport airp = new Airport(codes[h]);
					acc.add_airport(airp);
				}
				hash_map_accs.put(acc.get_ACC_code(), acc);
			}
		}

		for (int y = 0; y < f; y++) {
			if (sc.hasNextLine()) {
				String[] info = sc.nextLine().split(" ");
				Flight flg = new Flight();
				flg.admission_time = Integer.parseInt(info[0]);
				flg.code = info[1];
				flg.acc_code = info[2];
				flg.departure_airport = info[3];
				flg.landing_airport = info[4];
				for (int p = 0; p < 21; p++) {
					flg.operation_time[p] = Integer.parseInt(info[5 + p]);
				}
				// make accs as hash table would be better
//				for (ACC acc : accs) {
//					if (acc.get_ACC_code().equals(flg.acc_code)) {
//						acc.temp_flights.add(flg);
//					}
//				}
				hash_map_accs.get(flg.acc_code).temp_flights.add(flg);
			}
		}
		try {
			FileWriter myWriter = new FileWriter(args[1]);

			for (java.util.Map.Entry<String, ACC> set : hash_map_accs.entrySet()) {
				ACC curr = set.getValue();
				curr.buildHeap();
				int time = simulation(curr);
				myWriter.write(curr.get_ACC_code() + " " + time);
				for (Airport air : curr.table) {
					if (air != null) {
						myWriter.write(" " + air.my_atc.get_ATC_code());
					}
				}
				myWriter.write("\n");
//			while (set.getValue().flights_current_size_minHeap > 1) {
//				Flight min = set.getValue().deleteMin();
//				System.out.println("*******" + min.admission_time + " " + min.code + " " + min.acc_code);
//			}
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

}
