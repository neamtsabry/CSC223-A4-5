/**
 * This is an class that holds station objects
 * for ease of mapping to id
 * 
 * It also contains a method to parse station objects back into
 * strings to help write to the station data when desired
 *
 */
public class Station {
		String name;
		int bikes;
		int pedelecs;
		int availableDocks;
		int maintainenceRequest;
		int capacity;
		boolean kioskBoolean;
		int kioskNumber;
		String address;
		
		public Station(
		String nameValue,
		Integer bikesValue,
		Integer pedelecsValue,
		Integer availableDocksValue,
		Integer maintainenceRequestValue,
		Integer capacityValue,
		Integer kiosk,
		String address1){
			this.name = nameValue;
			this.bikes = bikesValue;
			this.pedelecs = pedelecsValue;
			this.availableDocks = availableDocksValue;
			this.maintainenceRequest = maintainenceRequestValue;
			this.capacity = capacityValue;
			this.kioskNumber = kiosk;
			if (kiosk > 0){
				this.kioskBoolean = true;
			} else{
				this.kioskBoolean = false;
			}
			this.address = address1;
		}
		
		public String getStationString(){
			String fileInput = this.name + "," + this.bikes + ","
					+ this.pedelecs + "," + this.availableDocks +
					"," + this.maintainenceRequest + ","
					+ this.capacity + "," + this.kioskNumber + "," + this.address;
			return fileInput;
		}
	}