package com.loveplusplus.zhengzhou.bean;

public class Bus {
	private int id;
	private String name;
	private String startTime;
	private String endTime;
	private String price;
	private String card;
	private String company;
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Override
	public String toString() {
		return "Bus [id=" + id + ", name=" + name + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", price=" + price + ", card="
				+ card + ", company=" + company + "]";
	}
	
}
