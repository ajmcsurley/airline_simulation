package com.demo.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.demo.model.Aircraft;
import com.demo.model.Airport;
import com.demo.model.Route;
import com.demo.service.AircraftService;
import com.demo.service.AirportService;
import com.demo.service.RouteService;
import com.demo.service.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class RouteAction extends ActionSupport{
	private AircraftService aircraftService;
	private AirportService airportService;
	private List<Aircraft> planes;
	private List<String> myPlanes;
	private String errorMsg;
	private String planeToSet;
	private List<Route> routes;
	private RouteService routeService;
	private String planeCurrentLocation;
	
	private String hour;
	private String min;
	private String airportToGo;
	//private List<String> timeslots;
	private List<String> hours;
	private List<String> mins;
	private List<Airport> airports;
	private List<Route> routeProfits;
	
	private static String dayPlusText=" (Day+1)";

	/**
	 * Route setup home page, show all the aircrafts of the user 
	 * a select list of planes name to choose from
	 */
	public String getRoute(){
		Map session = ActionContext.getContext().getSession();
	    if(session.get("logined")==null){
	    	setErrorMsg("Please sign in first");
	    	return ERROR;
	    }
	    int userId = (Integer)session.get("userId");

		planes = aircraftService.getUserPlanes(userId);
		if(planes==null || planes.isEmpty()){
			setErrorMsg("You have no aircrafts yet");
	    	return ERROR;
		}
		myPlanes = new ArrayList<String>();
		for(Aircraft a : planes){
			myPlanes.add(a.getCustomizedName());
		}
		return SUCCESS;
	}
	
	public String profitDetails(){
	  Map session = ActionContext.getContext().getSession();
	  if(session.get("logined")==null){
	    	setErrorMsg("Please sign in first");
	    	return ERROR;
	  }
	
	  int userId = (Integer)session.get("userId");
	  if(session.get("superuser")!=null){
	  	userId = -1;
	  }
	  
	  routeProfits = routeService.getProfitDetails(userId);
	  if(userId == -1) return "adminsuccess";
	  return SUCCESS;

	}
	
	
	/**
	 * return the route page of one specific aircraft  after passing in planeToSet
	 */
	public String aircraftRoute(){
		Map session = ActionContext.getContext().getSession();
	    if(session.get("logined")==null){
	    	setErrorMsg("Please sign in first");
	    	return ERROR;
	    }
	    int userId = (Integer)session.get("userId");
	    if(getPlaneToSet()==null || getPlaneToSet().isEmpty()){
	    	setErrorMsg("Please choose an aircraft first");
	    	return ERROR;
	    }
	    if(airportService.getMyAirport(userId).isEmpty()){
	    	setErrorMsg("Please buy a hub first in airport page");
	    	return ERROR;
	    }
		String planeName = planeToSet;
		
		session.put("routePlane", planeName);

		routes = routeService.getAircraftRoutes(userId, planeName);
		Route nextRoute = routes.get(routes.size()-1);
		setPlaneCurrentLocation(nextRoute.getDepartureAirportIdentifier());
		routes.remove(routes.size()-1);
		String[] ss = nextRoute.getDepartureTimeLocal().split(":");
		int currentHour = Integer.valueOf(ss[0]);
		int currentMin = Integer.valueOf(ss[1]);
		populateTimeSlots(currentHour, currentMin);
		session.put("planeCurLocation", planeCurrentLocation);
		return SUCCESS;
	}
	
	/**
	 *  populate variable airports and timeslots used to be shown in selector list
	 */
	private void populateTimeSlots(int currentHour, int currentMin){
		if(airports == null || airports.isEmpty()){
			setAirports(airportService.getAllAirport());
		}
		
		if(hours == null || hours.isEmpty()){
			hours = new ArrayList<String>();
			for(int i=0;i<24;i++){
				int h = (currentHour + i)%24;
				String s ="";
				if(h<10){
					s+="0";
				}
				s+=h;
				if(h<currentHour){
					s+=dayPlusText;
				}
				hours.add(s);
			}
		}
		if(mins == null || mins.isEmpty()){
			mins = new ArrayList<String>();
			for(int i=0;i<60;i+=5){
				String s ="";
				if(i<10){
					s+="0";
				}
				s+=i;
				mins.add(s);
			}
		}
	}
	
	/**
	 * add a new route of a airplane's route list
	 * parameters passed in : depatureTime, airportToGo
	 */
	public String addRoute(){
		Map session = ActionContext.getContext().getSession();
	    if(session.get("logined")==null){
	    	setErrorMsg("Please sign in first");
	    	return ERROR;
	    }
	    int userId = (Integer)session.get("userId");
	    String planeName = (String)session.get("routePlane");
	    String planeLocation = (String)session.get("planeCurLocation");
	    setPlaneToSet(planeName);
		

		int dayPlus = 0;
		if(hour.contains(dayPlusText)){
			hour = hour.split(" ")[0];
			dayPlus = 1;
		}
			
		String depatureTime = hour+":"+min;

		int ret = routeService.addRoute(userId,planeName,planeLocation,depatureTime,airportToGo, dayPlus);

		routes = routeService.getAircraftRoutes(userId, planeName);
		Route nextRoute = routes.get(routes.size()-1);
		session.put("planeCurLocation", nextRoute.getDepartureAirportIdentifier());
		String[] ss = nextRoute.getDepartureTimeLocal().split(":");
		int currentHour = Integer.valueOf(ss[0]);
		int currentMin = Integer.valueOf(ss[1]);
		populateTimeSlots(currentHour, currentMin);

		routes.remove(routes.size()-1);
		if(ret == Utils.TIMEERROR){
			setErrorMsg("Next depart time should be more than 30 mins later than arrival time");
	    	return ERROR;
		}
		if(ret == Utils.AIRPORTERROR){
			setErrorMsg("Invalid departure airport, please select again");
	    	return ERROR;
		}

		return SUCCESS;
	}

	
	public String undoRoute(){

		Map session = ActionContext.getContext().getSession();
	    if(session.get("logined")==null){
	    	setErrorMsg("Please sign in first");
	    	return ERROR;
	    }
	    int userId = (Integer)session.get("userId");
	    String planeName = (String)session.get("routePlane");

	    routeService.undoUserRoute(userId, planeName);
		
		
		routes = routeService.getAircraftRoutes(userId, planeName);
		Route nextRoute = routes.get(routes.size()-1);
		setPlaneCurrentLocation(nextRoute.getDepartureAirportIdentifier());
		routes.remove(routes.size()-1);
		String[] ss = nextRoute.getDepartureTimeLocal().split(":");
		int currentHour = Integer.valueOf(ss[0]);
		int currentMin = Integer.valueOf(ss[1]);
		populateTimeSlots(currentHour, currentMin);
		session.put("planeCurLocation", planeCurrentLocation);
		return SUCCESS;
	
	}

	public AircraftService getAircraftService() {
		return aircraftService;
	}


	public void setAircraftService(AircraftService aircraftService) {
		this.aircraftService = aircraftService;
	}


	public List<Aircraft> getPlanes() {
		return planes;
	}


	public void setPlanes(List<Aircraft> planes) {
		this.planes = planes;
	}


	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public List<String> getMyPlanes() {
		return myPlanes;
	}


	public void setMyPlanes(List<String> myPlanes) {
		this.myPlanes = myPlanes;
	}


	public String getPlaneToSet() {
		return planeToSet;
	}


	public void setPlaneToSet(String planeToSet) {
		this.planeToSet = planeToSet;
	}


	public List<Route> getRoutes() {
		return routes;
	}


	public void setRountes(List<Route> routes) {
		this.routes = routes;
	}


	public RouteService getRouteService() {
		return routeService;
	}


	public void setRouteService(RouteService routeService) {
		this.routeService = routeService;
	}


	public String getPlaneCurrentLocation() {
		return planeCurrentLocation;
	}


	public void setPlaneCurrentLocation(String planeCurrentLocation) {
		this.planeCurrentLocation = planeCurrentLocation;
	}


	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}


//	public String getDepatureTime() {
//		return depatureTime;
//	}
//
//
//	public void setDepatureTime(String depatureTime) {
//		this.depatureTime = depatureTime;
//	}


	public String getAirportToGo() {
		return airportToGo;
	}


	public void setAirportToGo(String airportToGo) {
		this.airportToGo = airportToGo;
	}


//	public List<String> getTimeslots() {
//		return timeslots;
//	}
//
//
//	public void setTimeslots(List<String> timeslots) {
//		this.timeslots = timeslots;
//	}




	public AirportService getAirportService() {
		return airportService;
	}


	public List<Airport> getAirports() {
		return airports;
	}

	public void setAirports(List<Airport> airports) {
		this.airports = airports;
	}

	public static String getDayPlusText() {
		return dayPlusText;
	}

	public static void setDayPlusText(String dayPlusText) {
		RouteAction.dayPlusText = dayPlusText;
	}

	public void setAirportService(AirportService airportService) {
		this.airportService = airportService;
	}


	public String getHour() {
		return hour;
	}


	public void setHour(String hour) {
		this.hour = hour;
	}


	public String getMin() {
		return min;
	}


	public void setMin(String min) {
		this.min = min;
	}


	public List<String> getHours() {
		return hours;
	}


	public void setHours(List<String> hours) {
		this.hours = hours;
	}


	public List<String> getMins() {
		return mins;
	}


	public void setMins(List<String> mins) {
		this.mins = mins;
	}

	public List<Route> getRouteProfits() {
		return routeProfits;
	}

	public void setRouteProfits(List<Route> routeProfits) {
		this.routeProfits = routeProfits;
	}
	
	
	
}
