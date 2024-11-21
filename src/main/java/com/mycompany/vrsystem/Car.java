package com.mycompany.vrsystem;

public class Car {
    private String carId;
    private String carModel;
    private String status;
    private double price;

    public Car(String carId, String carModel, String status, double price) {
        this.carId = carId;
        this.carModel = carModel;
        this.status = status;
        this.price = price;
    }

    public String getCarId() {
        return carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }
}
