package com.kpb.model;

import java.util.Date;

public class Order {
    private Integer orderId;

    private Integer screenScheId;

    private Integer userId;

    private String status;

    private String seat;

    private Date time;

    private String filmName;

    private String cinemaName;

    private Date screenTime;

    private String hall;

    private Double price;

    private String token;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getScreenScheId() {
        return screenScheId;
    }

    public void setScreenScheId(Integer screenScheId) {
        this.screenScheId = screenScheId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public Date getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(Date screenTime) {
        this.screenTime = screenTime;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public Double getPrice(){return price;}

    public void setPrice(Double price){this.price=price;}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}