package com.loveplusplus.zhengzhou.bean;

public class Station {
	private int id;
	private String name;
	
	public Station(){
		
	}
	
	public Station(int id){
		this.id=id;
	}
	
	public Station(String name){
		this.name=name;
	}
	public Station(int id,String name){
		this.id=id;
		this.name=name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Station [id=" + id + ", name=" + name + "]\n";
	}
	
	
}
