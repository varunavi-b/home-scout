package com.homescout.service;

import com.homescout.model.Buyer;
import com.homescout.model.Enquiry;
import com.homescout.model.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnquiryService {
    private final List<Enquiry> enquiries = new ArrayList<>();

    public Enquiry send(Buyer buyer, Property property, String message) {
        Enquiry e = new Enquiry(buyer, property, message);
        enquiries.add(e);
        return e;
    }

    public List<Enquiry> forAgent(int agentId) {
        return enquiries.stream()
                .filter(e -> e.getProperty().getAgentId() == agentId)
                .collect(Collectors.toList());
    }

    public List<Enquiry> forBuyer(Buyer buyer) {
        return enquiries.stream()
                .filter(e -> e.getBuyer().equals(buyer))
                .collect(Collectors.toList());
    }

    public List<Enquiry> all() { return new ArrayList<>(enquiries); }
}
