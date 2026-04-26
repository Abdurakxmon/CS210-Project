package com.cs210.project.models;

import com.cs210.project.constants.Enums.BillItemType;
import java.math.BigDecimal;

public class BillItem {
    private int id;
    private int billId;
    private BillItemType itemType;
    private BigDecimal amount;
    private String serviceName;

    public BillItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public BillItemType getItemType() {
        return itemType;
    }

    public void setItemType(BillItemType itemType) {
        this.itemType = itemType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
