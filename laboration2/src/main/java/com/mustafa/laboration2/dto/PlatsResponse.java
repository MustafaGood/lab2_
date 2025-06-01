package com.mustafa.laboration2.dto;

import com.mustafa.laboration2.entity.Plats;

public class PlatsResponse {
    private Long id;
    private String namn;
    private String beskrivning;
    private String anvandarId;
    private String status;
    private Double latitude;
    private Double longitude;

    public PlatsResponse(Plats plats) {
        this.id = plats.getId();
        this.namn = plats.getNamn();
        this.beskrivning = plats.getBeskrivning();
        this.anvandarId = plats.getAnvandarId();
        this.status = plats.getStatus().name();
        if (plats.getKoordinater() != null) {
            this.latitude = plats.getKoordinater().getY();
            this.longitude = plats.getKoordinater().getX();
        }
    }

    public Long getId() { return id; }
    public String getNamn() { return namn; }
    public String getBeskrivning() { return beskrivning; }
    public String getAnvandarId() { return anvandarId; }
    public String getStatus() { return status; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
