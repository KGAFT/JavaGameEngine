package com.KGAFT.VulkanGameEngine.Utils;

public class ReportFromVulkan {
    private int messageSeverity;
    private int messageType;
    private long callBackData;
    private long userData;

    private long date;

    public ReportFromVulkan(int messageSeverity, int messageType, long callBackData, long userData, long date) {
        this.messageSeverity = messageSeverity;
        this.messageType = messageType;
        this.callBackData = callBackData;
        this.userData = userData;
        this.date = date;
    }

    public ReportFromVulkan() {
    }

    public int getMessageSeverity() {
        return messageSeverity;
    }

    public void setMessageSeverity(int messageSeverity) {
        this.messageSeverity = messageSeverity;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getCallBackData() {
        return callBackData;
    }

    public void setCallBackData(long callBackData) {
        this.callBackData = callBackData;
    }

    public long getUserData() {
        return userData;
    }

    public void setUserData(long userData) {
        this.userData = userData;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
