package com.hwua.erhai.vo;

public class DoCarDeleteResponse extends Response {
    private String carId;

    public DoCarDeleteResponse() {
    }

    public DoCarDeleteResponse(int code, String msg, String carId) {
        super(code, msg);
        this.carId = carId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}
