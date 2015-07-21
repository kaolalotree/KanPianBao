package com.kpb.model;

import java.util.Date;

public class ScreenSche {
    private Integer screenScheId;

    private Integer filmId;

    private Integer cinemaId;

    private Date time;

    private String hall;

    private Double price;

    public Integer getScreenScheId() {
        return screenScheId;
    }

    public void setScreenScheId(Integer screenScheId) {
        this.screenScheId = screenScheId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Integer cinemaId) {
        this.cinemaId = cinemaId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}