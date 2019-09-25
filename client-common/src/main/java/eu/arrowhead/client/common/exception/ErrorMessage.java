/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.common.exception;

public class ErrorMessage {

  private String errorMessage;
  private int errorCode;
  private ExceptionType exceptionType;
  private String origin;
  private String documentation = "https://github.com/hegeduscs/arrowhead/tree/4.0/documentation";

  public ErrorMessage() {
  }

  public ErrorMessage(String errorMessage, int errorCode, ExceptionType exceptionType, String origin) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
    this.exceptionType = exceptionType;
    this.origin = origin;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public ExceptionType getExceptionType() {
    return exceptionType;
  }

  public void setExceptionType(ExceptionType exceptionType) {
    this.exceptionType = exceptionType;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

}
