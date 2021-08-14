
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class ProjectThree {
	List<City> allCities;
	Map<String, City> allAttractions;
	
	public ProjectThree(){
		allCities= new ArrayList<City>();
		allAttractions= new HashMap< String, City>();
		
		
	}
	
	void readAttractions(String filename) throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(filename));
		while (scanner.hasNext()){
			String data = scanner.nextLine();
			String[] dataPoints= data.split(",");
			City city=findOrCreateCity(dataPoints[1]);
			allAttractions.put(dataPoints[0], city);	
			city.setAttraction(dataPoints[0]);
		}
	}
	
	void readRoads(String filename)throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(filename));
		while (scanner.hasNext()){
			
			String data = scanner.nextLine();
			String[] dataPoints= data.split(",");
			
			Road road= new Road();
			City start=findOrCreateCity(dataPoints[0]);
			City end=findOrCreateCity(dataPoints[1]);
			road.setStart(start);
			road.setEnd(end);
			road.setMiles(Integer.parseInt(dataPoints[2]));
			road.setMinutes(Integer.parseInt(dataPoints[3]));
			start.routes.add(road);
			
			Road reverseRoad= new Road();
			reverseRoad.setStart(end);
			reverseRoad.setEnd(start);
			reverseRoad.setMiles(Integer.parseInt(dataPoints[2]));
			reverseRoad.setMinutes(Integer.parseInt(dataPoints[3]));
			end.routes.add(reverseRoad);
			
		}
			
	}
	
	City findOrCreateCity(String cityName){
		for(int i=0; i<allCities.size(); i++) {
			if (allCities.get(i).getName().equals(cityName)) {
				return allCities.get(i);
			}
		}
		
		City city=new City(cityName);
		allCities.add(city);
		return city;
		
	}
	City min_dis(List<City> cities ,Map<City , Double >distance ) {
		double min_dis = Double.MAX_VALUE;
		City min_city = cities.get(0);
		for(City c : cities) {
			if(min_dis > distance.get(c)) {
				min_dis = distance.get(c);
				min_city = c;
			}
		}
		return min_city;
	}
	double shortestDis(City S , City D) {
		List<City> cities= new ArrayList<City>();
		Map<City , Double >distance= new HashMap<City , Double>();
		for(City c : this.allCities ) {
			cities.add(c);
			distance.put(c, Double.MAX_VALUE);
			
		}
		distance.put(S, 0.0);
		while(cities.size()>0) {
			City curr = min_dis(cities,distance);
			cities.remove(curr);
			for(Road R : curr.routes) {
				if(distance.get(R.end)> R.miles + distance.get(curr)) {
					distance.put(R.end, R.miles + distance.get(curr));
					R.end.prev = curr;
				}
			}
			if(curr==D) break;
			}
		return distance.get(D);
			
	}
	
	ArrayList<ArrayList<String>> permute(List<String> num) {
	ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
	result.add(new ArrayList<String>());
 
	for (int i = 0; i < num.size(); i++) {
		ArrayList<ArrayList<String>> current = new ArrayList<ArrayList<String>>();
 
		for (ArrayList<String> l : result) {
			
			for (int j = 0; j < l.size()+1; j++) {
			
				l.add(j, num.get(i));
 
				ArrayList<String> temp = new ArrayList<String>(l);
				current.add(temp);
 
				l.remove(j);
			}
		}
 
		result = new ArrayList<ArrayList<String>>(current);
	}
 
	return result;
}
	
	List<String> findRoute(City start, City end, List<String> attractions){
		ArrayList<ArrayList<String>>  all_route = permute(attractions);
		int i = 0;
		int best_ind = -1;
		double best_dis = Double.MAX_VALUE;
		for(ArrayList<String> R : all_route) {
			double dis = 0;
			City prev = start;
			for(String att : R) {
				dis+=shortestDis(prev , this.allAttractions.get(att));
				prev =  this.allAttractions.get(att);
			}
			dis+=shortestDis(prev , end);
			if(dis<best_dis) {
				best_dis = dis;
				best_ind = i ;
			}
			i++;
		}
		
		
		return all_route.get(best_ind);
	}
	List<Route> dijikstraPath(City S , City E){
		List<Route> path= new LinkedList<Route>();
		City Prev = E.prev;
		while(E!=null || S !=null) {
			Route curr = new Route(Prev.name , E.name);
			path.add(curr);
			E = Prev;
			Prev = E.prev;
		    if(Prev==S) {
		    	 curr = new Route(Prev.name , E.name);
				path.add(curr);
		    	break;
		    }
			
		}
		
		Collections.reverse(path);
		return path;
	}
	
	List<Route> route(String starting_city, String ending_city, List<String> attractions){
		List<Route> finalRoute= new LinkedList<Route>();
		City start = findByName(starting_city);
		City end=findByName(ending_city);
		List<String> att_route = findRoute(start, end, attractions);
		double dis = 0;
		City prev = start;
		for(String att : att_route) {
			dis+=shortestDis(prev,this.allAttractions.get(att) );
			finalRoute.addAll(dijikstraPath(prev,this.allAttractions.get(att)));
			
			prev =  this.allAttractions.get(att);
		}
		dis+=shortestDis(prev , end);
		finalRoute.addAll(dijikstraPath( prev , end));
		return finalRoute;
	}
	City findByName(String name) {
		for(int i=0; i<allCities.size(); i++) {
			if(allCities.get(i).getName().equalsIgnoreCase(name)) {
				return allCities.get(i);
			}
		}
		return null;
	}
	
	
	public static void main(String args[]) throws FileNotFoundException {
		ProjectThree projectThree= new ProjectThree();
		projectThree.readRoads("roads.csv");
		projectThree.readAttractions("attractions.csv");		
		List<String> attractions= new ArrayList<String>();
		Scanner s= new Scanner(System.in);
		System.out.print("Name of starting city (or EXIT to quit): ");
		String start=s.nextLine();
		System.out.print("Name of ending city: ");
		String end=s.nextLine();
		while(true) {
			System.out.print("List an attraction along the way (or ENOUGH to stoplisting): ");
			String attraction  = s.nextLine();
			if(attraction.equalsIgnoreCase("ENOUGH")) {
				break;
			}
			if(projectThree.allAttractions.get(attraction)==null) {
				System.out.println("Attraction"+ attraction+ " unknown.");
			}
			else {
				attractions.add(attraction);
			}
		}
		
		List<Route> path = projectThree.route(start, end, attractions);
		System.out.println("Here is the best route for your trip:");
		for(Route R : path) {
			System.out.println(R);
		}
		
		
		
	}

}

 class Route{
	 String start;
	 String end; 
	 
	 public Route(String S , String E) {
		start = S;
		end = E;
	 }
	 
	
	
	 
	 
	 public String toString() {
		 
		 return "* "+ this.start+" -> "+ end;
		 
	 }
	 
 }



class Road{
	 City start;
	 City end;
	 int miles;
	 int minutes;
	 
	public City getStart() {
		return start;
	}
	public void setStart(City start) {
		this.start = start;
	}
	public City getEnd() {
		return end;
	}
	public void setEnd(City end) {
		this.end = end;
	}
	public int getMiles() {
		return miles;
	}
	public void setMiles(int miles) {
		this.miles = miles;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public String toString() {
		String s=start.getName()+ " to "+ end.getName()+" "+ getMiles()+ " miles ";
		return s;
		
	}
	 
	 
 }
 
 class City{
	String name;
	List<Road> routes;
	String attraction;
	City prev;
	
	public List<Road> getRoads() {
		return routes;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.prev = null;
	}
	
	City(String cityName){
		setName(cityName);
		routes= new ArrayList<Road>();
	}
	
	public String getAttraction() {
		return attraction;
	}
	public void setAttraction(String attraction) {
		this.attraction = attraction;
	}
	public String toString(){
		
		String s= getName()+ ":";
		if (attraction!=null) {
			//s+="\n"+ getAttraction();
		}
		for(int i=0; i<routes.size(); i++) {
			s+="\n"+routes.get(i);
		}
		return s;
	}
	
 }
