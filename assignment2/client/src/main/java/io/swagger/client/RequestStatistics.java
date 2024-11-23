package io.swagger.client;

public class RequestStatistics {
  public final long startTime;
  public final String requestType;
  public final long latency;
  public final int responseCode;

  public RequestStatistics(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }
}