package com.hwua.erhai.vo;

public class DoCarUpdateUsableResponse extends Response {
    private String carId;
    private String usable;

    public DoCarUpdateUsableResponse() {
    }

    public DoCarUpdateUsableResponse(int code, String msg, String carId, String usable) {
        super(code, msg);
        this.carId = carId;
        this.usable = usable;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUsable() {
        return usable;
    }

    public void setUsable(String usable) {
        this.usable = usable;
    }
}
